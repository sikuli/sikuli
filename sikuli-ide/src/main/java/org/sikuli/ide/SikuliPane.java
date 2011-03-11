package org.sikuli.ide;

import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.awt.event.*;
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
   private String _editingFilename;
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
      setEditorKitForContentType("text/python", new SikuliEditorKit());
      setContentType("text/python");
      initKeyMap();
      addKeyListener(this);
      //setTransferHandler(new JTextPaneTransferHandler());
      _highlighter = new CurrentLineHighlighter(this);
      addCaretListener(_highlighter);
      addCaretListener(this);
      if(Utils.isMacOSX())
         setFont(new Font("Courier", Font.PLAIN, 18));
      else 
         setFont(new Font("monospaced", Font.PLAIN, 18));
      setMargin( new Insets( 3, 3, 3, 3 ) );
      //setTabSize(4);
      setBackground(Color.WHITE);
      if(!Utils.isMacOSX())
         setSelectionColor(new Color(170, 200, 255));
      updateDocumentListeners();

      _indentationLogic = new PythonIndentation();
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

   public String getCurrentFilename(){
      if(_editingFilename==null){
         try{
            saveAsFile();
            return _editingFilename;
         }
         catch(IOException e){
            e.printStackTrace();
         }
      }
      return _editingFilename;
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
                  new FileOutputStream(_editingFilename), "UTF8")));
      if(writeHTML)
         convertSrcToHtml(getSrcBundle());
      cleanBundle(getSrcBundle());
      setDirty(false);
   }

   public String saveFile() throws IOException{
      if(_editingFilename==null)
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
      _editingFilename = getSourceFilename(bundlePath);
      Debug.log(1, "save to bundle: " + getSrcBundle());
      writeSrcFile(true);
      //TODO: update all bundle references in ImageButtons
      //BUG: if save and rename images, the images will be gone..
   }
   
   private String getSourceFilename(String filename){
      if( filename.endsWith(".sikuli") || 
          filename.endsWith(".sikuli" + "/") ){
         File f = new File(filename);
         String dest = f.getName();
         dest = dest.replace(".sikuli", ".py");
         return getSrcBundle() + dest;
      }
      return filename;
   }
   
   public void loadFile(String filename) throws IOException{
      if( filename.endsWith("/") )
         filename = filename.substring(0, filename.length()-1);
      setSrcBundle(filename+"/");
      _editingFilename = getSourceFilename(filename);
      this.read( new BufferedReader(new InputStreamReader(
                  new FileInputStream(_editingFilename), "UTF8")), null);
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

   @Override public void paste() {
      int start = getCaretPosition();
      super.paste();
      int end = getCaretPosition();
      Debug.log(9,"paste: %d %d", start, end);
      try{
         end = parseLine(start, end, patPatternStr);
         parseLine(start, end, patPngStr);
      }
      catch(BadLocationException e){
         e.printStackTrace();
      }
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

   void parse(Element node){
      int count = node.getElementCount();
      for(int i=0;i<count;i++){
         Element elm = node.getElement(i);
         Debug.log(8, elm.toString() );
         if( elm.isLeaf() ){
            int start = elm.getStartOffset(), end = elm.getEndOffset();
            try{
               end = parseLine(start, end, patPatternStr);
               end = parseLine(start, end, patSubregionStr);
               parseLine(start, end, patPngStr);
            }
            catch(BadLocationException e){
               e.printStackTrace();
            }
         }
         else
            parse(elm);
      }
   }

   static Pattern patPngStr = Pattern.compile("(\"[^\"]+?\\.(?i)png\")");
   static Pattern patHistoryBtnStr = Pattern.compile("(\"\\[SIKULI-(CAPTURE|DIFF)\\]\")");
   static Pattern patPatternStr = Pattern.compile(
            "\\b(Pattern\\s*\\(\".*?\"\\)(\\.\\w+\\([^)]*\\))*)");
   static Pattern patSubregionStr = Pattern.compile(
            "\\b(Region\\s*\\([\\d\\s,]+\\))");

   int parseLine(int startOff, int endOff, Pattern ptn) throws BadLocationException{
      //System.out.println(startOff + " " + endOff);
      Document doc = getDocument();
      while(true){
         String line = doc.getText(startOff, endOff-startOff);
         Matcher m = ptn.matcher(line);
         //System.out.println("["+line+"]");
         if( m.find() ){
            int len = m.end() - m.start();
            if(replaceWithImage(startOff+m.start(), startOff+m.end())){
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
            Utils.xcopy(filename, bundlePath);
            filename = f.getName();
         }
         catch(IOException e){
            e.printStackTrace();
            return f;
         }
      }
      filename = bundlePath + "/" + filename;
      f = new File(filename);
      if(f.exists()) return f;
      return null;
   }

   public File getFileInBundle(String filename){
      try{
         String fullpath = _imgLocator.locate(filename);
         return new File(fullpath);
      }
      catch(IOException e){
         return null;
      }
   }
   
   boolean replaceWithImage(int startOff, int endOff) 
                                          throws BadLocationException{
      Document doc = getDocument();
      String imgStr = doc.getText(startOff, endOff - startOff);
      String filename = imgStr.substring(1,endOff-startOff-1);;
      boolean useParameters = false;
      boolean exact = false;
      int numMatches = -1;
      float similarity = -1f;
      Location offset = null;

      //Debug.log("imgStr: " + imgStr);
      //Debug.log("filename " + filename);
      if( imgStr.startsWith("Pattern") ){
         useParameters = true;
         String[] tokens = imgStr.split("\\)\\s*\\.?");
         for(String str : tokens){
            //System.out.println("token: " + str);
            if( str.startsWith("exact") )  exact = true;
            if( str.startsWith("Pattern") )
               filename = str.substring(
                     str.indexOf("\"")+1,str.lastIndexOf("\""));
            if( str.startsWith("similar") ){
               String strArg = str.substring(str.lastIndexOf("(")+1);
               try{
                  similarity = Float.valueOf(strArg);
               }
               catch(NumberFormatException e){
                  return false;
               }
            }
            if( str.startsWith("firstN") ){
               String strArg = str.substring(str.lastIndexOf("(")+1);
               numMatches = Integer.valueOf(strArg);
            }
            if( str.startsWith("targetOffset") ){
               String strArg = str.substring(str.lastIndexOf("(")+1);
               String[] args = strArg.split(",");
               try{
                  offset = new Location(0,0);
                  offset.x = Integer.valueOf(args[0]);
                  offset.y = Integer.valueOf(args[1]);
               }
               catch(NumberFormatException e){
                  return false;
               }
            }
         }
      }
      else if( imgStr.startsWith("Region") ){
         String[] tokens = imgStr.split("[(),]");
         try{
            int x = Integer.valueOf(tokens[1]), y = Integer.valueOf(tokens[2]), 
                w = Integer.valueOf(tokens[3]), h = Integer.valueOf(tokens[4]);
            this.select(startOff, endOff);
            RegionButton icon = new RegionButton(this, x, y, w, h);
            this.insertComponent(icon);
            return true;
         }
         catch(NumberFormatException e){
            return false;
         }
         catch(Exception e){
            e.printStackTrace();
            return false;
         }
      }
      else if( patHistoryBtnStr.matcher(imgStr).matches() ){
         Element root = doc.getDefaultRootElement();
         int lineIdx = root.getElementIndex(startOff);
         Element line = root.getElement(lineIdx);
         try{
            CaptureButton btnCapture = (CaptureButton)_historyBtnClass.newInstance();
            if( imgStr.indexOf("DIFF") >= 0 )
               btnCapture.setDiffMode(true);
            btnCapture.setSrcElement(line);
            btnCapture.setParentPane(this);
            //System.out.println("new btn: " + btnCapture.getSrcElement());
            this.select(startOff, endOff);
            if( btnCapture.hasNext() ){
               int pos = startOff;
               doc.insertString(pos++, "[", null);
               this.insertComponent(btnCapture);
               pos++;
               while(btnCapture.hasNext()){
                  CaptureButton btn = btnCapture.getNextDiffButton();
                  doc.insertString(pos++, ",", null);
                  this.insertComponent(btn);
                  pos++;
               }
               doc.insertString(pos, "]", null);
            }
            else
               this.insertComponent(btnCapture);
         }
         catch(Exception e){
            e.printStackTrace();
         }
         return true;
      }

      File f = getFileInBundle(filename);
      Debug.log(7,"replaceWithImage: " + filename);
      if( f != null && f.exists() ){
         this.select(startOff, endOff);
         ImageButton icon = new ImageButton(this, f.getAbsolutePath());
         if(useParameters){
            icon.setParameters(exact, similarity, numMatches);
            if(offset != null)
               icon.setTargetOffset(offset);
         }
         this.insertComponent(icon);
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
      insertString(getCaretPosition(), str);
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
         end = parseLine(start, end, patHistoryBtnStr);
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



