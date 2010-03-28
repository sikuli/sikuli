package edu.mit.csail.uid;

import java.io.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.image.*;
import java.awt.event.*;
import java.net.URL;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.imageio.*;

import org.python.util.PythonInterpreter; 
import org.python.core.*; 

public class SikuliPane extends JTextPane implements KeyListener, 
                                                     CaretListener{
   private String _editingFilename;
   private String _srcBundlePath = null;
   private boolean _dirty = false;
   private Class _historyBtnClass;
   private CurrentLineHighlighter _highlighter;

   private String _tabString = "   ";

   public SikuliPane(){
      setEditorKitForContentType("text/python", new SikuliEditorKit());
      setContentType("text/python");
      addKeyListener(this);
      _highlighter = new CurrentLineHighlighter(this);
      addCaretListener(_highlighter);
      addCaretListener(this);
      setFont(new Font("Osaka-Mono", Font.PLAIN, 18));
      setMargin( new Insets( 3, 3, 3, 3 ) );
      setTabs(3);
      setBackground(Color.WHITE);
   }

   public void setTabs(int spaceForTab)
   {
      String t = "";
      for(int i=0;i<spaceForTab;i++) t += " ";
      _tabString = t;
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

   public String getSrcBundle(){
      if( _srcBundlePath == null ){
         File tmp = Utils.createTempDir();
         _srcBundlePath = Utils.slashify(tmp.getAbsolutePath(),true);
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
      if( _dirty ){
         Object[] options = {"Yes", "No", "Cancel"};
         int ans = JOptionPane.showOptionDialog(this,
               getCurrentShortFilename() + " has been modified. Save changes?",
               "Do you want to close this tab?",
               JOptionPane.YES_NO_CANCEL_OPTION,
               JOptionPane.WARNING_MESSAGE,
               null,
               options, options[0]);
         if( ans == JOptionPane.CANCEL_OPTION || 
             ans == JOptionPane.CLOSED_OPTION )
            return false;
         else if( ans == JOptionPane.YES_OPTION )
            saveFile();
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

   private void convertSrcToHtml(String bundle){
      PythonInterpreter py = new PythonInterpreter();
      Debug.log(1, "Convert Sikuli source code " + bundle + " to HTML");
      py.set("local_convert", true);
      py.set("sikuli_src", bundle);
      py.exec(pyConverter);
   }

   private void writeFile(String filename) throws IOException{
      this.write( new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(filename), "UTF8")));
   }

   private void writeSrcFile(boolean writeHTML) throws IOException{
      this.write( new BufferedWriter(new OutputStreamWriter(
                  new FileOutputStream(_editingFilename), "UTF8")));
      if(writeHTML)
         convertSrcToHtml(getSrcBundle());
      _dirty = false;
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
      _srcBundlePath = bundlePath;
      _editingFilename = getSourceFilename(bundlePath);
      Debug.log(1, "save to bundle: " + _srcBundlePath);
      writeSrcFile(true);
   }
   
   private String getSourceFilename(String filename){
      if( filename.endsWith(".sikuli") || 
          filename.endsWith(".sikuli" + "/") ){
         File f = new File(filename);
         String dest = f.getName();
         dest = dest.replace(".sikuli", ".py");
         return _srcBundlePath + dest;
      }
      return filename;
   }
   
   public void loadFile(String filename) throws IOException{
      if( filename.endsWith("/") )
         filename = filename.substring(0, filename.length()-1);
      _srcBundlePath = filename + "/";
      _editingFilename = getSourceFilename(filename);
      this.read( new BufferedReader(new InputStreamReader(
                  new FileInputStream(_editingFilename), "UTF8")), null);
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
      _dirty = true;
   }


   int _caret_last_x = -1;
   boolean _can_update_caret_last_x = true;

   public void caretUpdate(CaretEvent evt){
      if(_can_update_caret_last_x)
         _caret_last_x = -1;
      else
         _can_update_caret_last_x = true;
   }

   // see: getMagicCaretPosition, getNextVisualPositionFrom
   // FIXME: dirty hack for fixing cursor movement
   public void keyPressed(java.awt.event.KeyEvent ke) {
      boolean up = false;
      int pos;
      if(ke.getModifiers()!=0)
         return;
      switch(ke.getKeyCode()){
         case KeyEvent.VK_LEFT:
         case KeyEvent.VK_RIGHT:
            _caret_last_x = -1;
            break;
         case KeyEvent.VK_UP:
            up = true;
         case KeyEvent.VK_DOWN:
            int line = getLineAtCaret();
            int tarLine = up? line-1 : line+1;
            try{
               if(tarLine<=0){
                  jumpTo(1,1);
                  return;
               }
               if(tarLine>getNumLines()){
                  setCaretPosition(getDocument().getLength()-1);
                  return;
               }

               Rectangle curRect = modelToView(getCaretPosition());
               Rectangle tarEndRect;
               if(tarLine < getNumLines())
                  tarEndRect = modelToView(getLineStartOffset(tarLine)-1);
               else
                  tarEndRect = modelToView(getDocument().getLength()-1);
               Debug.log(7, "curRect: " + curRect + ", tarEnd: " + tarEndRect);
               if(_caret_last_x == -1)
                  _caret_last_x  = curRect.x;
               if( _caret_last_x > tarEndRect.x ){
                  pos = viewToModel(new Point(tarEndRect.x, tarEndRect.y));
                  _can_update_caret_last_x = false;
               }
               else{
                  pos = viewToModel(new Point(_caret_last_x, tarEndRect.y));
                  if(up && getLineAtCaret(pos)==getLineAtCaret(pos+1) 
                        /*&& _caret_last_x == curRect.x*/)
                     pos++;
               }
               setCaretPosition(pos);
            }
            catch(BadLocationException e){
               e.printStackTrace();
            }
            ke.consume();
            break;
      }
   
   }

   public void keyReleased(java.awt.event.KeyEvent ke) {
      /*
      final int S_MOD = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      if( ke.getModifiers()==S_MOD ) {
         if (ke.getKeyCode()==KeyEvent.VK_X){
            this.cut();
         } else if (ke.getKeyCode()==KeyEvent.VK_C) {
            this.copy();
         } else if (ke.getKeyCode()==KeyEvent.VK_V) {
            this.paste();
         }
      }
      */
   }

   private void expandTab() throws BadLocationException{
      int pos = getCaretPosition();
      Document doc = getDocument();
      doc.remove(pos-1, 1);
      doc.insertString(pos-1, _tabString, null);
   }
   
   public void keyTyped(java.awt.event.KeyEvent ke) { 
      _dirty = true;
      try{
         if(ke.getKeyChar() == '\t') expandTab();
         checkCompletion(ke);
      }
      catch(BadLocationException e){
         e.printStackTrace();
      }
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
         System.err.println("negative indentation not supported yet!!");
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
   private int getLineStartOffset(int line) throws BadLocationException { 
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

   static Pattern patPngStr = Pattern.compile("(\".+?\\.png\")");
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


   public File getFileInBundle(String filename){
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
   
   boolean replaceWithImage(int startOff, int endOff) 
                                          throws BadLocationException{
      Document doc = getDocument();
      String imgStr = doc.getText(startOff, endOff - startOff);
      String filename = imgStr.substring(1,endOff-startOff-1);;
      boolean useParameters = false;
      boolean exact = false;
      int numMatches = -1;
      float similarity = -1f;

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
               similarity = Float.valueOf(strArg);
            }
            if( str.startsWith("firstN") ){
               String strArg = str.substring(str.lastIndexOf("(")+1);
               numMatches = Integer.valueOf(strArg);
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
         if(useParameters)
            icon.setParameters(exact, similarity, numMatches);
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

}

class CaptureButton extends JButton implements ActionListener, Cloneable{
   protected Element _line;
   protected SikuliPane _codePane;
   protected boolean _isCapturing;

   /*
   public String toString(){
      return " \"CAPTURE-BUTTON\" ";
   }
   */

   public CaptureButton(){
      super();
      URL imageURL = SikuliIDE.class.getResource("/icons/capture.png");
      setIcon(new ImageIcon(imageURL));
      setToolTipText("Take a screenshot");
      setBorderPainted(false);
      setMaximumSize(new Dimension(26,26));
      addActionListener(this);
      _line = null;
   }

   public CaptureButton(SikuliPane codePane, Element elmLine){
      this();
      _line = elmLine;
      _codePane = codePane;
      setBorderPainted(true);
      setCursor(new Cursor (Cursor.HAND_CURSOR));
   }

   public boolean hasNext(){  return false;  }
   public CaptureButton getNextDiffButton(){ return null; }
   public void setParentPane(SikuliPane parent){
      _codePane = parent;
   }

   public void setDiffMode(boolean flag){}
   
   public void setSrcElement(Element elmLine){
      _line = elmLine;
   }

   public Element getSrcElement(){  return _line;  }

   protected void insertAtCursor(JTextPane pane, String imgFilename){
      ImageButton icon = new ImageButton(pane, imgFilename);
      pane.insertComponent(icon);
   }

   public void captureCompleted(String imgFullPath){
      _isCapturing = false;
      if(imgFullPath == null) return;
      Debug.log("captureCompleted: " + imgFullPath);
      Element src = getSrcElement();
      if( src == null ){
         if(_codePane == null)
            insertAtCursor(SikuliIDE.getInstance().getCurrentCodePane(), imgFullPath);
         else
            insertAtCursor(_codePane, imgFullPath);
         return;
      }

      int start = src.getStartOffset();
      int end = src.getEndOffset();
      try{
         StyledDocument doc = (StyledDocument)src.getDocument();
         String text = doc.getText(start, end-start);
         Debug.log(text);
         for(int i=start;i<end;i++){
            Element elm = doc.getCharacterElement(i);
            if(elm.getName().equals(StyleConstants.ComponentElementName)){
               AttributeSet attr=elm.getAttributes();
               Component com=StyleConstants.getComponent(attr);
               if( com instanceof CaptureButton ){
                  Debug.log("button is at " + i);
                  int oldCaretPos = _codePane.getCaretPosition();
                  _codePane.select(i, i+1);
                  ImageButton icon = new ImageButton(_codePane, imgFullPath);
                  _codePane.insertComponent(icon);
                  _codePane.setCaretPosition(oldCaretPos);
                  break;
               }
            }
         }
      }
      catch(BadLocationException ble){
         ble.printStackTrace();
      }
   
   }


   public void capture(final int delay){
      if(_isCapturing)
         return;
      _isCapturing = true;
      Thread t = new Thread("capture"){
         public void run(){
            SikuliIDE ide = SikuliIDE.getInstance();
            if(delay!=0) ide.setVisible(false);
            try{
               Thread.sleep(delay);
            }
            catch(Exception e){}
            new ScreenOverlay(ide.getCurrentCodePane(), CaptureButton.this);
            if(delay!=0) ide.setVisible(true);
         }
      };
      t.start();
   }

   public void actionPerformed(ActionEvent e) {
      Debug.log("capture!");
      UserPreferences pref = UserPreferences.getInstance();
      int delay = (int)(pref.getCaptureDelay() * 1000.0) +1;
      capture(delay);
   }
}

class ScreenOverlay extends JWindow{
   static Rectangle fullscreenRect = new Rectangle(
         Toolkit.getDefaultToolkit().getScreenSize() );
   static Color _overlayColor = new Color(0F,0F,0F,0.6F);
   static GraphicsDevice _gdev;

   SikuliPane _parentPane;
   CaptureButton _captureBtn = null;
   ButtonSubregion _subregionBtn = null;

   BufferedImage _screen = null;
   BufferedImage _darker_screen = null;
   Rectangle rectSelection;
   BasicStroke bs;
   int srcx, srcy, destx, desty;

   BasicStroke _StrokeCross = new BasicStroke (1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1, new float [] { 2f }, 0);

   private void captureScreen() throws AWTException{
      Robot _robot = new Robot();
      _screen = _robot.createScreenCapture(fullscreenRect);

      float scaleFactor = .6f;
      RescaleOp op = new RescaleOp(scaleFactor, 0, null);
      _darker_screen = op.filter(_screen, null);
   }

   private void drawSelection(Graphics2D g2d){
      if (srcx != destx || srcy != desty)
      {
         int x1 = (srcx < destx) ? srcx : destx;
         int y1 = (srcy < desty) ? srcy : desty;
         int x2 = (srcx > destx) ? srcx : destx;
         int y2 = (srcy > desty) ? srcy : desty;

         rectSelection.x = x1;
         rectSelection.y = y1;
         rectSelection.width = (x2-x1)+1;
         rectSelection.height = (y2-y1)+1;

         g2d.setColor(Color.white);
         g2d.setStroke(bs);
         g2d.draw(rectSelection);

         int cx = (x1+x2)/2;
         int cy = (y1+y2)/2;
         g2d.setStroke(_StrokeCross);
         g2d.drawLine(cx, y1, cx, y2);
         g2d.drawLine(x1, cy, x2, cy);
      }
   }

   public void paint(Graphics g)
   {
      if( _screen != null ){
         Graphics2D g2d = (Graphics2D)g;

         g2d.drawImage(_darker_screen,0,0,this);
         drawSelection(g2d);
         setVisible(true);
      }
      else
         setVisible(false);
   }

   void init(){
      rectSelection = new Rectangle ();
      bs = new BasicStroke (3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
            0, new float [] { 12, 12 }, 0);

      addMouseListener(new MouseAdapter(){
         public void mousePressed(java.awt.event.MouseEvent e){
            if (_screen == null) return;
            destx = srcx = e.getX();
            desty = srcy = e.getY();
            repaint();
         }

         public void mouseReleased(java.awt.event.MouseEvent e){
            if (_screen == null) return;
            if( e.getButton() == java.awt.event.MouseEvent.BUTTON3 ){
               close();
               if(_captureBtn!=null)
                  _captureBtn.captureCompleted(null);
               //FIXME: add subregion completed
               return;
            }
            if( _captureBtn!=null){
               int w = rectSelection.width, h = rectSelection.height;
               if( rectSelection.width <= 0 || rectSelection.height <= 0){
                  _captureBtn.captureCompleted(null);
                  return;
               }
               BufferedImage cropImg = cropSelection();
               String filename = 
                     Utils.saveImage(cropImg, _parentPane.getSrcBundle());
               if( filename != null){
                  close();
                  String fullpath = _parentPane.getFileInBundle(filename).getAbsolutePath();
                  _captureBtn.captureCompleted(Utils.slashify(fullpath,false));
               }
            }
            else{
               int x = rectSelection.x, y = rectSelection.y;
               int w = rectSelection.width, h = rectSelection.height;
               close();
               _subregionBtn.complete(x, y, w, h);
            }
         }
      });

      //doesn't work.. (no focus?)
      addKeyListener(new KeyAdapter(){
         public void keyReleased(java.awt.event.KeyEvent e){
            System.out.println("release: " + e.getKeyCode());
            if( e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE ){
               ScreenOverlay.this.close();
            }
         }
      });

      addMouseMotionListener( new MouseMotionAdapter(){
         public void mouseDragged(java.awt.event.MouseEvent e) {
            if (_screen == null) return;
            destx = e.getX();
            desty = e.getY();
            repaint(); 
         }
      });
   }

   private void close(){
      _gdev.setFullScreenWindow(null);
      this.setVisible(false);
      this.dispose();
   }



   private BufferedImage cropSelection(){
      int w = rectSelection.width, h = rectSelection.height;
      BufferedImage crop  = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      Graphics2D crop_g2d = crop.createGraphics();
      try {
         crop_g2d.drawImage(
            _screen.getSubimage(rectSelection.x, rectSelection.y, w, h),
            null, 0, 0
         );
      }
      catch (RasterFormatException e) {
         e.printStackTrace();
      }
      crop_g2d.dispose();
      return crop;
   }

   public ScreenOverlay(SikuliPane parent, JButton btn){
      _parentPane = parent;
      // FIXME: replace this with a common parent of capture button & subregion button
      if( btn instanceof CaptureButton )
         _captureBtn = (CaptureButton)btn;
      else
         _subregionBtn = (ButtonSubregion)btn;
      init();
      try{
         captureScreen();
      }
      catch(AWTException e){
         e.printStackTrace();
      }
      _gdev = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      if( _gdev.isFullScreenSupported() ){
         _gdev.setFullScreenWindow(this);
      }
      else{
         Debug.log("Fullscreen mode is not supported.");
      }
      setLocation(0,0);
   }

}


class RegionButton extends JButton {
   SikuliPane _pane;
   int _x, _y, _w, _h;


   public RegionButton(SikuliPane pane, int x, int y, int w, int h){
      _pane = pane;
      _x = x;
      _y = y;
      _w = w;
      _h = h;
      try{
         String imgFilename = Utils.saveTmpImage(getRegionImage(x,y,w,h));
         setIcon(new ImageIcon(imgFilename));
      }
      catch(Exception e){
         e.printStackTrace();
      }
      setBorderPainted(true);
      setToolTipText( this.toString() );
   }

   public String toString(){
      return String.format("Region(%d,%d,%d,%d)", _x, _y, _w, _h);
   }

   static Rectangle fullscreenRect = new Rectangle(
         Toolkit.getDefaultToolkit().getScreenSize() );
   private BufferedImage getRegionImage(int x, int y, int w, int h) throws AWTException{
      Robot _robot = new Robot();
      BufferedImage _screen = _robot.createScreenCapture(fullscreenRect);
      int scr_w = _screen.getWidth(), scr_h = _screen.getHeight();
      int max_h = 80;
      float scale = (float)max_h/scr_h;
      scr_w *= scale;
      scr_h *= scale;
      BufferedImage screen = new BufferedImage(scr_w, scr_h, BufferedImage.TYPE_INT_RGB);
      Graphics2D screen_g2d = screen.createGraphics();
      try {
         screen_g2d.drawImage(_screen, 0, 0,  scr_w, scr_h, null);
         int sx = (int)(x*scale), sy = (int)(y*scale),
             sw = (int)(w*scale), sh = (int)(h*scale);
         screen_g2d.setColor(new Color(255,0,0, 150));
         screen_g2d.fillRect(sx, sy, sw, sh);
      }
      catch (RasterFormatException e) {
         e.printStackTrace();
      }
      screen_g2d.dispose();
      return screen;
   }
}

class ImageButton extends JButton implements ActionListener /*, MouseListener*/ {
   static final int DEFAULT_NUM_MATCHES = 10;
   static final float DEFAULT_SIMILARITY = 0.7f;


   private String _imgFilename, _thumbFname;
   private JTextPane _pane;
   private float _similarity;
   private boolean _exact;
   private int _numMatches;
   private boolean _showText;

   private PatternWindow pwin = null;


   /*
   public void mousePressed(java.awt.event.MouseEvent e) {}
   public void mouseReleased(java.awt.event.MouseEvent e) {}
   public void mouseClicked(java.awt.event.MouseEvent e) {}

   public void mouseEntered(java.awt.event.MouseEvent e) {
   }

   public void mouseExited(java.awt.event.MouseEvent e) {
   }
   */

   public void setParameters(boolean exact, float similarity, int numMatches){
      Debug.log("setParameters: " + exact + "," + similarity + "," + numMatches);
      _exact = exact;
      if(similarity>=0) _similarity = similarity;
      if(numMatches>=0) _numMatches = numMatches;
      setToolTipText( this.toString() );
   }

   private String createThumbnail(String imgFname){
      final int max_h = 40;
      Image img = new ImageIcon(imgFname).getImage();
      int w = img.getWidth(null), h = img.getHeight(null);
      if(max_h >= h)  return imgFname;
      float scale = (float)max_h/h;
      w *= scale;
      h *= scale;
      BufferedImage thumb = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
      Graphics2D g2d = thumb.createGraphics();
      g2d.drawImage(img, 0, 0,  w, h, null);
      g2d.dispose();
      return Utils.saveTmpImage(thumb);
   }
   
   public ImageButton(JTextPane pane, String imgFilename){
      _pane = pane;
      _imgFilename = imgFilename;
      _showText = true;
      _exact = false;
      _similarity = DEFAULT_SIMILARITY;
      _numMatches = DEFAULT_NUM_MATCHES;

      _thumbFname = createThumbnail(imgFilename);
      setIcon(new ImageIcon(_thumbFname));
      setBorderPainted(true);
      setCursor(new Cursor (Cursor.HAND_CURSOR));
      addActionListener(this);
      //addMouseListener(this);
      setToolTipText( this.toString() );
   }

   private boolean useThumbnail(){
      return !_imgFilename.equals(_thumbFname);
   }
   
   public void paint(Graphics g){
      super.paint(g);
      Graphics2D g2d = (Graphics2D)g;
      drawText(g2d);
      if( useThumbnail() ){
         g2d.setColor( new Color(0, 128, 128, 128) );
         g2d.drawRoundRect(3, 3, getWidth()-7, getHeight()-7, 5, 5);
      }
   }

   private static Font textFont = new Font("arial", Font.BOLD, 12);
   private void drawText(Graphics2D g2d){
      String str = "";
      _showText = false;
      if( !_exact && _similarity != DEFAULT_SIMILARITY){
         _showText = true;
         str += _similarity + " ";
      }
      if(_numMatches != DEFAULT_NUM_MATCHES){
         _showText = true;
         str += "(" + _numMatches + ")";
      }
      if( !_showText )
         return;

      final int w = g2d.getFontMetrics().stringWidth(str);
      final int fontH = g2d.getFontMetrics().getMaxAscent();
      final int x = getWidth() - w - 3, y = 0;
      final int borderW = 2;
      g2d.setFont( textFont );
      g2d.setColor( new Color(0, 128, 0, 128) );
      g2d.fillRoundRect(x-borderW, y, w+borderW*2, fontH+borderW*2, 3, 3);
      g2d.setColor( Color.white );
      g2d.drawString(str, x, y+fontH+1);
   }

   public void actionPerformed(ActionEvent e) {
      Debug.log("click on image");
      pwin = new PatternWindow(this, _exact, _similarity, _numMatches);
   }

   public String getImageFilename(){
      return _imgFilename;
   }

   public String toString(){
      String img = new File(_imgFilename).getName();
      if( _exact || _similarity != DEFAULT_SIMILARITY 
                 || _numMatches != DEFAULT_NUM_MATCHES ){
                 
         String ret = "Pattern(\"" + img + "\")";
         if(_exact)
            ret += ".exact()";
         else
            ret += String.format(Locale.ENGLISH, ".similar(%.2f)", _similarity);
         if(_numMatches != DEFAULT_NUM_MATCHES)
            ret += String.format(".firstN(%d)", _numMatches);
         return ret;
      }
      else
         return "\"" + img + "\"";
   }
}
