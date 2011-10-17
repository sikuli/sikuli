/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.awt.event.*;
import java.util.Map;
import java.util.HashMap;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
//import javax.swing.undo.*;

import javax.imageio.*;

import org.python.util.PythonInterpreter; 
import org.python.core.*; 

import org.sikuli.ide.indentation.PythonIndentation;

import org.sikuli.script.ImageLocator;
import org.sikuli.script.ScriptRunner;
import org.sikuli.script.Debug;
import org.sikuli.script.Location;


public class SikuliPane extends JTextPane implements KeyListener, 
                                                     CaretListener{
   private File _editingFile;
   private String _srcBundlePath = null;
   private boolean _dirty = false;
   private Class _historyBtnClass;
   private CurrentLineHighlighter _highlighter;
   private ImageLocator _imgLocator;

   private String _tabString = "   ";

   private Pattern _lastSearchPattern = null;
   private String _lastSearchString = null;
   private Matcher _lastSearchMatcher;

   private UndoManager _undo = new UndoManager();

   // TODO: move to SikuliDocument
   private PythonIndentation _indentationLogic;

   public SikuliPane(){
      UserPreferences pref = UserPreferences.getInstance();
      setEditorKitForContentType("text/python", new SikuliEditorKit());
      setContentType("text/python");
      initKeyMap();
      addKeyListener(this);
      setTransferHandler(new MyTransferHandler());
      _highlighter = new CurrentLineHighlighter(this);
      addCaretListener(_highlighter);
      addCaretListener(this);
      setFont(new Font(pref.getFontName(), Font.PLAIN, pref.getFontSize()));
      setMargin( new Insets( 3, 3, 3, 3 ) );
      //setTabSize(4);
      setBackground(Color.WHITE);
      if(!Utils.isMacOSX())
         setSelectionColor(new Color(170, 200, 255));
      updateDocumentListeners();

      _indentationLogic = new PythonIndentation();
      _indentationLogic.setTabWidth(pref.getTabWidth());
      pref.addPreferenceChangeListener(new PreferenceChangeListener(){

         @Override
         public void preferenceChange(PreferenceChangeEvent event){
            // TODO: define constants for preference keys in UserPreferences
            if( event.getKey().equals("TAB_WIDTH") ){
               _indentationLogic.setTabWidth(Integer.parseInt(event.getNewValue()));
            }
         }});
   }

   private void updateDocumentListeners(){
      getDocument().addDocumentListener(new DirtyHandler());
      getDocument().addUndoableEditListener(_undo);
   }

   public UndoManager getUndoManager(){
      return _undo;
   }

   public PythonIndentation getIndentationLogic(){
      return _indentationLogic;
   }

   private void initKeyMap(){
      InputMap map = this.getInputMap();
      int shift = InputEvent.SHIFT_MASK;
      map.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, shift),          
              SikuliEditorKit.sklDeindentAction);
   }


   public void setTabSize(int charactersPerTab)
   {
      FontMetrics fm = this.getFontMetrics( this.getFont() );
      int charWidth = fm.charWidth( 'w' );
      int tabWidth = charWidth * charactersPerTab;

      TabStop[] tabs = new TabStop[10];

      for (int j = 0; j < tabs.length; j++) {
         int tab = j + 1;
         tabs[j] = new TabStop( tab * tabWidth );
      }

      TabSet tabSet = new TabSet(tabs);
      SimpleAttributeSet attributes = new SimpleAttributeSet();
      StyleConstants.setFontSize(attributes, 18);
      StyleConstants.setFontFamily(attributes, "Osaka-Mono");
      StyleConstants.setTabSet(attributes, tabSet);
      int length = getDocument().getLength();
      getStyledDocument().setParagraphAttributes(0, length, attributes, true);
   }
 

   public void setTabs(int spaceForTab)
   {
      String t = "";
      for(int i=0;i<spaceForTab;i++) t += " ";
      _tabString = t;
   }

   public boolean isDirty(){  return _dirty; }
   public void setDirty(boolean flag){ 
      if(_dirty == flag)
         return;
      _dirty = flag; 
      if(flag)
         getRootPane().putClientProperty("Window.documentModified", true);
      else
         SikuliIDE.getInstance().checkDirtyPanes();
   }

   public int getLineAtCaret(int caretPosition)
   {
      Element root = getDocument().getDefaultRootElement();
      return root.getElementIndex( caretPosition ) + 1;
   }

   public int getLineAtCaret()
   {
      int caretPosition = getCaretPosition();
      Element root = getDocument().getDefaultRootElement();
      return root.getElementIndex( caretPosition ) + 1;
   }
 
   public int getColumnAtCaret() {
      int offset = getCaretPosition();
      int column;
      try {
         column = offset - Utilities.getRowStart(this, offset);
      } catch (BadLocationException e) {
         column = -1;
      }
      return column+1;
   }

   void setSrcBundle(String newBundlePath){
      _srcBundlePath = newBundlePath;
      _imgLocator = new ImageLocator(_srcBundlePath);
   } 

   public String getSrcBundle(){
      if( _srcBundlePath == null ){
         File tmp = Utils.createTempDir();
         setSrcBundle(Utils.slashify(tmp.getAbsolutePath(),true));
      }
      return _srcBundlePath;
   }
   
   public void setErrorHighlight(int lineNo){
      _highlighter.setErrorLine(lineNo);
      try{
         if(lineNo>0)
            jumpTo(lineNo);   
      }
      catch(Exception e){
         e.printStackTrace();
      }
      repaint();
   }
   
   public void setHistoryCaptureButton(CaptureButton btn){
      _historyBtnClass = btn.getClass();
   }

   public boolean close() throws IOException{
      if( isDirty() ){
         Object[] options = {I18N._I("yes"), I18N._I("no"), I18N._I("cancel")};
         int ans = JOptionPane.showOptionDialog(this,
               I18N._I("msgAskSaveChanges", getCurrentShortFilename()),
               I18N._I("dlgAskCloseTab"),
               JOptionPane.YES_NO_CANCEL_OPTION,
               JOptionPane.WARNING_MESSAGE,
               null,
               options, options[0]);
         if( ans == JOptionPane.CANCEL_OPTION || 
             ans == JOptionPane.CLOSED_OPTION )
            return false;
         else if( ans == JOptionPane.YES_OPTION )
            saveFile();
         setDirty(false);
      }
      return true;
   }

   public String getCurrentShortFilename(){
      if(_srcBundlePath != null){
         File f = new File(_srcBundlePath);
         return f.getName();
      }
      return "Untitled";
   }

   public File getCurrentFile(){
      if(_editingFile == null && isDirty()){
         try{
            saveAsFile();
            return _editingFile;
         }
         catch(IOException e){
            e.printStackTrace();
         }
      }
      return _editingFile;
   }

   static InputStream SikuliToHtmlConverter = SikuliIDE.class.getResourceAsStream("/scripts/sikuli2html.py");
   static String pyConverter = Utils.convertStreamToString(SikuliToHtmlConverter);

   static InputStream SikuliBundleCleaner= SikuliIDE.class.getResourceAsStream("/scripts/clean-dot-sikuli.py");
   static String pyBundleCleaner = Utils.convertStreamToString(SikuliBundleCleaner);

   private void convertSrcToHtml(String bundle){
      PythonInterpreter py = 
         ScriptRunner.getInstance(null).getPythonInterpreter();
      Debug.log(2, "Convert Sikuli source code " + bundle + " to HTML");
      py.set("local_convert", true);
      py.set("sikuli_src", bundle);
      py.exec(pyConverter);
   }

   private void writeFile(String filename) throws IOException{
      this.write( new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(filename), "UTF8")));
   }

   private void cleanBundle(String bundle){
      PythonInterpreter py = 
         ScriptRunner.getInstance(null).getPythonInterpreter();
      Debug.log(2, "Clear source bundle " + bundle);
      py.set("bundle_path", bundle);
      py.exec(pyBundleCleaner);
   
   }

   private void writeSrcFile(boolean writeHTML) throws IOException{
      this.write( new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(_editingFile), "UTF8")));
      if(writeHTML)
         convertSrcToHtml(getSrcBundle());
      cleanBundle(getSrcBundle());
      setDirty(false);
   }

   public String saveFile() throws IOException{
      if(_editingFile==null)
         return saveAsFile();
      else{
         writeSrcFile(true);
         return getCurrentShortFilename();
      }
   }

   public String saveAsFile() throws IOException{
      File file = new FileChooser(SikuliIDE.getInstance()).save();
      if(file == null)  return null;

      String bundlePath = file.getAbsolutePath();
      if( !file.getAbsolutePath().endsWith(".sikuli") )
         bundlePath += ".sikuli";
      if(Utils.exists(bundlePath)){
         int res = JOptionPane.showConfirmDialog(
               null, I18N._I("msgFileExists", bundlePath), 
               I18N._I("dlgFileExists"), JOptionPane.YES_NO_OPTION);
         if(res != JOptionPane.YES_OPTION)
            return null;
      }
      saveAsBundle(bundlePath);

      return getCurrentShortFilename();
   }


   public String exportAsZip() throws IOException, FileNotFoundException{
      File file = new FileChooser(SikuliIDE.getInstance()).export();
      if(file == null)  return null;

      String zipPath = file.getAbsolutePath();
      String srcName = file.getName();
      if( !file.getAbsolutePath().endsWith(".skl") ){
         zipPath += ".skl";
      }
      else{
         srcName = srcName.substring(0, srcName.lastIndexOf('.'));
      }
      writeFile(getSrcBundle() + srcName + ".py");
      Utils.zip(getSrcBundle(), zipPath);
      Debug.log(1, "export to executable file: " + zipPath);
      return zipPath;
   }

   private void saveAsBundle(String bundlePath) throws IOException{
      bundlePath = Utils.slashify(bundlePath, true);
      if(_srcBundlePath != null)
         Utils.xcopy( _srcBundlePath, bundlePath );
      else
         Utils.mkdir(bundlePath);
      setSrcBundle(bundlePath);
      _editingFile = createSourceFile(bundlePath, ".py");
      Debug.log(1, "save to bundle: " + getSrcBundle());
      writeSrcFile(true);
      //TODO: update all bundle references in ImageButtons
      //BUG: if save and rename images, the images will be gone..
   }

   private File createSourceFile(String bundlePath, String ext){
      if( bundlePath.endsWith(".sikuli") || 
          bundlePath.endsWith(".sikuli/") ){
         File dir = new File(bundlePath);
         String name = dir.getName();
         name = name.substring(0, name.lastIndexOf("."));
         return new File(bundlePath, name+ext);
      }
      return new File(bundlePath);
   }
   
   private File findSourceFile(String sikuli_dir){
      if( sikuli_dir.endsWith(".sikuli") || 
          sikuli_dir.endsWith(".sikuli" + "/") ){
         File dir = new File(sikuli_dir);
         File[] pys = dir.listFiles(new GeneralFileFilter("py", "Python Source"));
         if(pys.length > 1){
            String sikuli_name = dir.getName();
            sikuli_name = sikuli_name.substring(0, sikuli_name.lastIndexOf('.'));
            for(File f : pys){
               String py_name = f.getName();
               py_name = py_name.substring(0, py_name.lastIndexOf('.'));
               if( py_name.equals(sikuli_name) )
                  return f;
            }
         }
         if(pys.length >= 1)
            return pys[0];
      }
      return new File(sikuli_dir);
   }
   
   public void loadFile(String filename) throws IOException{
      if( filename.endsWith("/") )
         filename = filename.substring(0, filename.length()-1);
      setSrcBundle(filename+"/");
      _editingFile = findSourceFile(filename);
      this.read( new BufferedReader(new InputStreamReader(
                  new FileInputStream(_editingFile), "UTF8")), null);
      updateDocumentListeners();
      setDirty(false);
   }

   public String loadFile() throws IOException{
      File file = new FileChooser(SikuliIDE.getInstance()).load();
      if(file == null)  return null;

      String fname = Utils.slashify(file.getAbsolutePath(),false);
      loadFile(fname);
      return fname;
   }


   int _caret_last_x = -1;
   boolean _can_update_caret_last_x = true;

   public void caretUpdate(CaretEvent evt){
      if(_can_update_caret_last_x)
         _caret_last_x = -1;
      else
         _can_update_caret_last_x = true;
   }

   public void keyPressed(java.awt.event.KeyEvent ke) {
   }

   public void keyReleased(java.awt.event.KeyEvent ke) {
   }

   private void expandTab() throws BadLocationException{
      int pos = getCaretPosition();
      Document doc = getDocument();
      doc.remove(pos-1, 1);
      doc.insertString(pos-1, _tabString, null);
   }
   
   public void keyTyped(java.awt.event.KeyEvent ke) { 
      /*
      try{
         //if(ke.getKeyChar() == '\t') expandTab();
         checkCompletion(ke);
      }
      catch(BadLocationException e){
         e.printStackTrace();
      }
      */
   }

   @Override public void read(Reader in, Object desc) throws IOException{
      super.read(in, desc);
      Document doc = getDocument();
      Element root = doc.getDefaultRootElement();
      parse(root);
      setCaretPosition(0);
   }

   public void jumpTo(int lineNo, int column) throws BadLocationException{
      Debug.log(6, "jumpTo: " + lineNo+","+column);
      int off = getLineStartOffset(lineNo-1)+column-1;
      int lineCount= getDocument().getDefaultRootElement().getElementCount(); 
      if( lineNo < lineCount ){
         int nextLine = getLineStartOffset(lineNo);
         if( off >= nextLine )
            off = nextLine-1;
      }
      if(off < 0) off = 0;
      setCaretPosition( off );
   }

   public void jumpTo(int lineNo) throws BadLocationException{
      Debug.log(6,"jumpTo: " + lineNo);
      setCaretPosition( getLineStartOffset(lineNo-1) );
   }

   public void jumpTo(String funcName) throws BadLocationException{
      Debug.log(6, "jumpTo: " + funcName);
      Element root = getDocument().getDefaultRootElement(); 
      int pos = getFunctionStartOffset(funcName, root);
      if(pos>=0)
         setCaretPosition(pos);
      else
         throw new BadLocationException("Can't find function " + funcName,-1);
   }


   private int getFunctionStartOffset(String func, Element node) throws BadLocationException{
      Document doc = getDocument();
      int count = node.getElementCount();
      Pattern patDef = Pattern.compile("def\\s+"+func+"\\s*\\(");
      for(int i=0;i<count;i++){
         Element elm = node.getElement(i);
         if( elm.isLeaf() ){
            int start = elm.getStartOffset(), end = elm.getEndOffset();
            String line = doc.getText(start, end-start);
            Matcher matcher = patDef.matcher(line);
            if(matcher.find())
               return start;
         }
         else{
            int p = getFunctionStartOffset(func, elm);
            if(p>=0) return p;
         }
      }
      return -1;
   }

   public int getNumLines(){
      Document doc = getDocument();
      Element root = doc.getDefaultRootElement();
      int lineIdx = root.getElementIndex(doc.getLength()-1);
      return lineIdx+1;
   }
   
   public void indent(int startLine, int endLine, int level){
      Document doc = getDocument();
      String strIndent = "";
      if(level>0){
         for(int i=0;i<level;i++)
            strIndent += "  ";
      }
      else{
         Debug.error("negative indentation not supported yet!!");
      }
      for(int i=startLine;i<endLine;i++){
         try{
            int off = getLineStartOffset(i);
            if(level>0)
               doc.insertString(off, strIndent, null);
         }
         catch(Exception e){
            e.printStackTrace();
         }
      } 
   }

   // line starting from 0
   int getLineStartOffset(int line) throws BadLocationException { 
      Element map = getDocument().getDefaultRootElement(); 
      if (line < 0) { 
         throw new BadLocationException("Negative line", -1); 
      } else if (line >= map.getElementCount()) { 
         throw new BadLocationException("No such line", getDocument().getLength()+1); 
      } else { 
         Element lineElem = map.getElement(line); 
         return lineElem.getStartOffset(); 
      }  
   }   

   int parseRange(int start, int end){
      try{
         end = parseLine(start, end, patCaptureBtn);
         end = parseLine(start, end, patPatternStr);
         end = parseLine(start, end, patSubregionStr);
         end = parseLine(start, end, patPngStr);
      }
      catch(BadLocationException e){
         e.printStackTrace();
      }
      return end;
   }

   void parse(Element node){
      int count = node.getElementCount();
      for(int i=0;i<count;i++){
         Element elm = node.getElement(i);
         Debug.log(8, elm.toString() );
         if( elm.isLeaf() ){
            int start = elm.getStartOffset(), end = elm.getEndOffset();
            parseRange(start, end);
         }
         else
            parse(elm);
      }
   }

   static Pattern patPngStr = Pattern.compile("(\"[^\"]+?\\.(?i)png\")");
   static Pattern patCaptureBtn = Pattern.compile("(\"__SIKULI-CAPTURE-BUTTON__\")");
   static Pattern patPatternStr = Pattern.compile(
            "\\b(Pattern\\s*\\(\".*?\"\\)(\\.\\w+\\([^)]*\\))*)");
   static Pattern patSubregionStr = Pattern.compile(
            "\\b(Region\\s*\\([\\d\\s,]+\\))");

   int parseLine(int startOff, int endOff, Pattern ptn) throws BadLocationException{
      //System.out.println(startOff + " " + endOff);
      if(endOff <= startOff)
         return endOff;
      Document doc = getDocument();
      while(true){
         String line = doc.getText(startOff, endOff-startOff);
         Matcher m = ptn.matcher(line);
         //System.out.println("["+line+"]");
         if( m.find() ){
            int len = m.end() - m.start();
            if(replaceWithImage(startOff+m.start(), startOff+m.end(), ptn)){
               startOff += m.start()+1;
               endOff -= len-1;
            }
            else
               startOff += m.end()+1;
         }
         else
            break;
      }
      return endOff;
   }


   public File copyFileToBundle(String filename){
      File f = new File(filename);
      String bundlePath = getSrcBundle();
      if(f.exists()){
         try{
            File newFile = Utils.smartCopy(filename, bundlePath);
            return newFile;
         }
         catch(IOException e){
            e.printStackTrace();
            return f;
         }
      }
      return null;
   }

   public File getFileInBundle(String filename){
      if(_imgLocator == null)
         return null;
      try{
         String fullpath = _imgLocator.locate(filename);
         return new File(fullpath);
      }
      catch(IOException e){
         return null;
      }
   }
   
   boolean replaceWithImage(int startOff, int endOff, Pattern ptn) 
                                          throws BadLocationException{
      Document doc = getDocument();
      String imgStr = doc.getText(startOff, endOff - startOff);
      Component comp = null;

      if( ptn == patPatternStr || ptn == patPngStr ){
         comp = ImageButton.createFromString(this, imgStr);
      }
      else if( ptn == patSubregionStr ){
         comp = RegionButton.createFromString(this, imgStr);
      }
      else if( ptn == patCaptureBtn ){
         Element root = doc.getDefaultRootElement();
         int lineIdx = root.getElementIndex(endOff);
         Element line = root.getElement(lineIdx);
         comp = new CaptureButton(this, line);
      }

      if(comp != null){
         this.select(startOff, endOff);
         this.insertComponent(comp);
         return true;
      }

      return false;
   }
   
   void checkCompletion(java.awt.event.KeyEvent ke) throws BadLocationException{
      Document doc = getDocument();
      Element root = doc.getDefaultRootElement();
      int pos = getCaretPosition();
      int lineIdx = root.getElementIndex(pos);
      Element line = root.getElement(lineIdx);
      int start = line.getStartOffset(), len = line.getEndOffset() - start;
      String strLine = doc.getText(start, len-1);
      Debug.log(9,"["+strLine+"]");
      if( strLine.endsWith("find") && ke.getKeyChar()=='(' ){
         ke.consume();
         doc.insertString( pos, "(", null);
         CaptureButton btnCapture = new CaptureButton(this, line);
         insertComponent(btnCapture);
         doc.insertString( pos+2, ")", null);
      }

   }

   void insertString(String str){
      int sel_start = getSelectionStart();
      int sel_end = getSelectionEnd();
      if(sel_end != sel_start){
         try{
            getDocument().remove(sel_start, sel_end-sel_start);
         }
         catch(BadLocationException e){
            e.printStackTrace();
         }
      }
      int pos = getCaretPosition();
      insertString(pos, str);
      int new_pos = getCaretPosition();
      int end = parseRange(pos, new_pos);
      setCaretPosition(end);
   }

   void insertString(int pos, String str){
      Document doc = getDocument();
      try{
         doc.insertString( pos, str, null );
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }

   void appendString(String str){
      Document doc = getDocument();
      try{
         int start = doc.getLength();
         doc.insertString( doc.getLength(), str, null );
         int end = doc.getLength();
         //end = parseLine(start, end, patHistoryBtnStr);
      }
      catch(Exception e){
         e.printStackTrace();
      }
   }

   // search forward
   /*
   public int search(Pattern pattern){
      return search(pattern, true);
   }

   public int search(Pattern pattern, boolean forward){
      if(!pattern.equals(_lastSearchPattern)){
         _lastSearchPattern = pattern;
         Document doc = getDocument();
         int pos = getCaretPosition();
         Debug.log("caret: "  + pos);
         try{
            String body = doc.getText(pos, doc.getLength()-pos);
            _lastSearchMatcher = pattern.matcher(body);
         }
         catch(BadLocationException e){
            e.printStackTrace();
         }
      }
      return continueSearch(forward);
   }
   */

   /*
   public int search(String str){
      return search(str, true);
   }
   */

   public int search(String str, int pos, boolean forward){
      int ret = -1;
      Document doc = getDocument();
      Debug.log(9, "search caret: "  + pos + ", " + doc.getLength());
      try{
         String body;
         int begin;
         if(forward){
            int len = doc.getLength()-pos;
            body = doc.getText(pos, len>0?len:0);
            begin = pos;
         }
         else{
            body = doc.getText(0, pos);
            begin = 0;
         }
         Pattern pattern = Pattern.compile(str);
         Matcher matcher = pattern.matcher(body);
         ret = continueSearch(matcher, begin, forward);
         if(ret < 0){
            if(forward && pos != 0) // search from beginning
               return search(str, 0, forward);
            if(!forward && pos != doc.getLength()) // search from end
               return search(str, doc.getLength(), forward);
         }
      }
      catch(BadLocationException e){
         Debug.log(7, "search caret: "  + pos + ", " + doc.getLength() + 
               e.getStackTrace());
      }
      return ret;
   }

   protected int continueSearch(Matcher matcher, int pos, boolean forward){
      boolean hasNext = false;
      int start=0, end=0;
      if(!forward){
         while(matcher.find()){
            hasNext = true;
            start = matcher.start();
            end = matcher.end();
         }
      }
      else{
         hasNext = matcher.find();
         if(!hasNext)
            return -1;
         start = matcher.start();
         end = matcher.end();
      }
      if(hasNext){
         Document doc = getDocument();
         getCaret().setDot(pos+end);
         getCaret().moveDot(pos+start);
         getCaret().setSelectionVisible(true);
         return pos+start;
      }
      return -1;
   }




   private class DirtyHandler implements DocumentListener {
      public void changedUpdate(DocumentEvent ev) {
         Debug.log(9, "change update");
         //setDirty(true);
      }
      public void insertUpdate(DocumentEvent ev) {
         Debug.log(9, "insert update");
         setDirty(true);
      }
      public void removeUpdate(DocumentEvent ev) {
         Debug.log(9, "remove update");
         setDirty(true);
      }
   }



}

class MyTransferHandler extends TransferHandler{

   static Map<String, String> _copiedImgs;

   static {
      _copiedImgs = new HashMap<String,String>();
   }

   public MyTransferHandler(){
   }

   public void exportToClipboard(JComponent comp, Clipboard clip, int action) {
      super.exportToClipboard(comp, clip, action);
   }

   protected void exportDone(JComponent source,
                          Transferable data,
                          int action){
      if(action == TransferHandler.MOVE){
         JTextPane aTextPane = (JTextPane)source;
         int sel_start = aTextPane.getSelectionStart();
         int sel_end = aTextPane.getSelectionEnd();
         Document doc = aTextPane.getDocument();
         try{
            doc.remove(sel_start, sel_end - sel_start);
         }
         catch(BadLocationException e){
            e.printStackTrace();
         
         }
      }
   
   }

   public int getSourceActions(JComponent c) {
      return COPY_OR_MOVE;
   }

   protected Transferable createTransferable(JComponent c){
      JTextPane aTextPane = (JTextPane)c;

      SikuliEditorKit kit = ((SikuliEditorKit)aTextPane.getEditorKit());
      Document doc = aTextPane.getDocument();
      int sel_start = aTextPane.getSelectionStart();
      int sel_end = aTextPane.getSelectionEnd();

      StringWriter writer = new StringWriter();
      try{
         _copiedImgs.clear();
         kit.write(writer, doc, sel_start, sel_end - sel_start, _copiedImgs);
         return new StringSelection(writer.toString());
      }
      catch(Exception e){
         e.printStackTrace();
      }
      return null;
   }

   public boolean canImport(JComponent comp, DataFlavor[] transferFlavors){
      for(int i=0; i<transferFlavors.length; i++){
         //System.out.println(transferFlavors[i]);
         if(transferFlavors[i].equals(DataFlavor.stringFlavor))
            return true;
      }
      return false;
   }

   public boolean importData(JComponent comp, Transferable t){
      DataFlavor htmlFlavor = DataFlavor.stringFlavor;
      if(canImport(comp, t.getTransferDataFlavors())){
         try{
            String transferString = (String)t.getTransferData(htmlFlavor);
            SikuliPane targetTextPane = (SikuliPane)comp;
            for(Map.Entry<String,String> entry : _copiedImgs.entrySet()){
               String imgName = entry.getKey();
               String imgPath = entry.getValue();
               File destFile = targetTextPane.copyFileToBundle(imgPath);
               String newName = destFile.getName();
               if(!newName.equals(imgName)){
                  String ptnImgName = "\"" + imgName + "\"";
                  newName = "\"" + newName + "\"";
                  transferString = transferString.replaceAll(ptnImgName, newName);
                  Debug.info(ptnImgName + " exists. Rename it to " + newName);
               }
            }
            targetTextPane.insertString(transferString);
         }catch (Exception e){
            Debug.error("Can't transfer: " + t.toString());
         }
         return true;
      }
      return false;
   }
}



