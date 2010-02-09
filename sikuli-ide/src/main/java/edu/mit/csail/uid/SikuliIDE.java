package edu.mit.csail.uid;

import edu.mit.csail.uid.sikuli_test.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import org.python.util.PythonInterpreter; 
import org.python.core.*; 


public class SikuliIDE extends JFrame {
   boolean ENABLE_RECORDING = false;

   private NativeLayer _native;

   private ConsolePane _console;
   private CloseableTabbedPane _mainPane, _sidePane;
   private JSplitPane _codeAndUnitPane;
   private JTabbedPane _auxPane;
   private JPanel _unitPane;
   private StatusBar _status;

   private CaptureButton _btnCapture;
   private ButtonRun _btnRun, _btnRunViz;

   private JMenuBar _menuBar = new JMenuBar();
   private JMenu _fileMenu = new JMenu("File");
   private JMenu _viewMenu = new JMenu("View");
   private JCheckBoxMenuItem _chkShowUnitTest;
   private UnitTestRunner _testRunner;

   private static SikuliIDE _instance = null;

   private static Icon PY_SRC_ICON = getIconResource("/icons/py-src-16x16.png");

   private String _preloadFilename = null;
   private boolean _inited = false;

   public static Icon getIconResource(String name) {
      URL url= SikuliIDE.class.getResource(name);
      if (url == null) {
         System.err.println("Warning: could not load \""+name+"\" icon");
         return null;
      }
      return new ImageIcon(url);
   }


   public void onStopRunning(){
      Debug.log(2, "StopRunning");
      _btnRun.stopRunning();
      _btnRunViz.stopRunning();
   }

   public void onQuickCapture(){
      onQuickCapture(null);
   }

   public void onQuickCapture(String arg){
      Debug.log(2, "QuickCapture");
      _btnCapture.capture(0);
   }

   public static SikuliIDE getInstance(String args[]){
      if( _instance == null )
         _instance = new SikuliIDE(args);
      return _instance;
   }

   public static SikuliIDE getInstance(){
      if( _instance == null )
         _instance = new SikuliIDE(null);
      return _instance;
   }

   private JMenuItem createMenuItem(JMenuItem item, KeyStroke shortcut, ActionListener listener){
      if(shortcut != null) 
         item.setAccelerator(shortcut);
      item.addActionListener(listener);
      return item;
   }

   private JMenuItem createMenuItem(String name, KeyStroke shortcut, ActionListener listener){
      JMenuItem item = new JMenuItem(name);
      return createMenuItem(item, shortcut, listener);
   }
   
   private void initFileMenu() throws NoSuchMethodException{
      int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      _fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);
      _fileMenu.add( createMenuItem("New", 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, scMask),
               new FileAction(FileAction.NEW)));
      _fileMenu.add( createMenuItem("Open...", 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, scMask),
               new FileAction(FileAction.OPEN)));
      _fileMenu.add( createMenuItem("Save", 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, scMask),
               new FileAction(FileAction.SAVE)));
      _fileMenu.add( createMenuItem("Save as...", 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, 
                  InputEvent.SHIFT_MASK | scMask),
               new FileAction(FileAction.SAVE_AS)));
      _fileMenu.add( createMenuItem("Export executable...", 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 
                  InputEvent.SHIFT_MASK | scMask),
               new FileAction(FileAction.EXPORT)));
      _fileMenu.addSeparator();
      if(!isMacOSX()){
         _fileMenu.add( createMenuItem("Preferences",
                  KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, scMask),
                  new FileAction(FileAction.PREFERENCES)));
      }
      _fileMenu.add( createMenuItem("Close Tab", 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, scMask),
               new FileAction(FileAction.CLOSE_TAB)));
      if(!isMacOSX()){
         _fileMenu.addSeparator();
         _fileMenu.add( createMenuItem("Quit", null, 
                  new FileAction(FileAction.QUIT)));
      }
   }

   private void initViewMenu() throws NoSuchMethodException{
      int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      _viewMenu.setMnemonic(java.awt.event.KeyEvent.VK_V);
      _chkShowUnitTest = new JCheckBoxMenuItem("Unit Test");
      _viewMenu.add( createMenuItem(_chkShowUnitTest, 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, scMask),
               new ViewAction(ViewAction.UNIT_TEST)));
   }

   private void initMenuBars(JFrame frame){
      try{
         initFileMenu();
         initViewMenu();
      }
      catch(NoSuchMethodException e){
         e.printStackTrace();
      }

      _menuBar.add(_fileMenu);
      _menuBar.add(_viewMenu);
      frame.setJMenuBar(_menuBar);
   }

   private JToolBar initToolbar(){
      JToolBar toolbar = new JToolBar();
      _btnRun = new ButtonRun();
      JButton btnInsertImage = new ButtonInsertImage();
      _btnCapture = new CaptureButton();
      JButton btnSubregion = new ButtonSubregion();
      _btnRunViz = new ButtonRunViz();
      toolbar.add(_btnCapture);
      toolbar.add(btnInsertImage);
      toolbar.add(btnSubregion);
      toolbar.addSeparator();
      if( ENABLE_RECORDING ){
         JToggleButton btnRecord = new ButtonRecord();
         toolbar.add(btnRecord);
      }
      toolbar.add(_btnRun);
      toolbar.add(_btnRunViz);
      toolbar.setFloatable(false);
      return toolbar;
   }


   private void initTabPane(){
      _mainPane = new CloseableTabbedPane();
      _mainPane.addCloseableTabbedPaneListener(
                new CloseableTabbedPaneListener(){
         public boolean closeTab(int i){
            try{
               JScrollPane scrPane = (JScrollPane)_mainPane.getComponentAt(i);
               SikuliPane codePane = (SikuliPane)scrPane.getViewport().getView();
               return codePane.close();
            }
            catch(Exception e){
               Debug.info("Can't close this tab: " + e.getStackTrace());
               return false;
            }
         }

      });

      _mainPane.addChangeListener(new ChangeListener(){
         public void stateChanged(ChangeEvent e){
            JTabbedPane tab = (JTabbedPane)e.getSource();
            int i = tab.getSelectedIndex();
            if(i>=0)
               SikuliIDE.this.setTitle("Sikuli - " + tab.getTitleAt(i));

         }
      });
            
   }

   private void initAuxPane(){
      _auxPane = new JTabbedPane();
      _console = new ConsolePane();
      _auxPane.addTab("Message", _console);
   }

   private void initUnitPane(){
      _testRunner = new UnitTestRunner();
      _unitPane = _testRunner.getPanel();
      _chkShowUnitTest.setState(false);
      (new ViewAction()).toggleUnitTest();
      addAuxTab("Test Trace", _testRunner.getTracePane());
   }

   private void initSidePane(){
      _sidePane = new CloseableTabbedPane();
      _sidePane.addChangeListener(new ChangeListener(){
         public void stateChanged(ChangeEvent e){
            JTabbedPane pane = (JTabbedPane)e.getSource();
            int sel = pane.getSelectedIndex();
            if( sel == -1 ) { // all tabs closed
               _codeAndUnitPane.setDividerLocation(1.0D);
               _chkShowUnitTest.setState(false);
            }
         }
      });
   }

   private StatusBar initStatusbar(){
      _status = new StatusBar();
      return _status;
   }


   public void addAuxTab(String tabName, JComponent com){
      _auxPane.addTab(tabName, com);
   }
   
   private void initShortcutKeys(){
      final int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      Toolkit.getDefaultToolkit().addAWTEventListener( new AWTEventListener() {
         public void eventDispatched( AWTEvent e ){
            java.awt.event.KeyEvent ke = (java.awt.event.KeyEvent)e;
            //Debug.log(ke.toString());
            if( ke.getID() == java.awt.event.KeyEvent.KEY_PRESSED ){
               if( ke.getKeyCode() == java.awt.event.KeyEvent.VK_RIGHT && 
                   ke.getModifiers() == scMask)
                  nextTab();
               else
               if( ke.getKeyCode() == java.awt.event.KeyEvent.VK_LEFT && 
                   ke.getModifiers() == scMask)
                  prevTab();
            }
         } }, AWTEvent.KEY_EVENT_MASK );

   }

   private void nextTab(){
      int i = _mainPane.getSelectedIndex();
      int next = (i+1) % _mainPane.getTabCount();
      _mainPane.setSelectedIndex(next);
   }

   private void prevTab(){
      int i = _mainPane.getSelectedIndex();
      int prev = (i-1+_mainPane.getTabCount()) % _mainPane.getTabCount();
      _mainPane.setSelectedIndex(prev);
   }
   
   static final int DEFAULT_WINDOW_W = 1024;
   static final int DEFAULT_WINDOW_H = 700;

   // for uist demo
   /*
   static final int DEFAULT_WINDOW_W = 500; 
   static final int DEFAULT_WINDOW_H = 700;
   */


   protected SikuliIDE(String[] args) {
      super("Sikuli IDE");

      initNativeLayer();

      if(args!=null && args.length>=1){
         try{
            if(args[0].endsWith("skl"))
               runSkl(args[0]);
         }
         catch(IOException e){
            System.err.println("Can't open file: " + args[0] + "\n" + e);
         }
      }

      initMenuBars(this);
      final Container c = getContentPane();
      c.setLayout(new BorderLayout());
      initTabPane();
      initAuxPane();
      initSidePane();
      initUnitPane();

      if(args!=null && args.length>=1)
         loadFile(args[0]);
      else if(_preloadFilename != null)
         loadFile(_preloadFilename);
      else
         (new FileAction()).doNew();

      //scrPane.setRowHeaderView(new LineNumberView(_codeEditor));


      _codeAndUnitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, true, _mainPane, _sidePane);
      JSplitPane mainAndConsolePane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT, true, _codeAndUnitPane, _auxPane);

      c.add(initToolbar(), BorderLayout.NORTH);
      c.add(mainAndConsolePane, BorderLayout.CENTER);
      c.add(initStatusbar(), BorderLayout.SOUTH);
      c.doLayout();

      setSize(DEFAULT_WINDOW_W, DEFAULT_WINDOW_H);
      adjustCodePaneWidth();
      mainAndConsolePane.setDividerLocation(500);

      initShortcutKeys();
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

      initHotkeys();

      _inited = true;
      setVisible(true);
   }

   public boolean isInited(){ return _inited; }

   public void preloadFile(String filename){
      if(_inited)
         loadFile(filename);
      else
         _preloadFilename = filename;
   }

   public void runSkl(String filename) throws IOException{
      String name = (new File(filename)).getName();
      name = name.substring(0, name.lastIndexOf('.'));
      File tmpDir = Utils.createTempDir();
      File sikuliDir = new File(tmpDir + File.separator + name + ".sikuli");
      sikuliDir.mkdir();
      Utils.unzip(filename, sikuliDir.getAbsolutePath());
      ScriptRunner runner = new ScriptRunner();
      runner.runPython(Utils.slashify(sikuliDir.getAbsolutePath(),true));
      System.exit(0);
   }

   public void installCaptureHotkey(int key, int mod){
      _native.installHotkey(key, mod, this, 
                          "onQuickCapture", "(Ljava/lang/String;)V");
   }

   private void initHotkeys(){
      UserPreferences pref = UserPreferences.getInstance();
      int key = pref.getCaptureHotkey();
      int mod = pref.getCaptureHotkeyModifiers();
      installCaptureHotkey(key, mod);
      key = pref.getStopHotkey();
      mod = pref.getStopHotkeyModifiers();
      _native.installHotkey(key, mod, this, 
                          "onStopRunning", "()V");
   }

   private boolean isLinux(){
      String os = System.getProperty("os.name").toLowerCase();
      if( os.startsWith("linux" ) )
         return true;
      return false;
   }

   private boolean isWindows(){
      String os = System.getProperty("os.name").toLowerCase();
      if( os.startsWith("windows" ) )
         return true;
      return false;
   }
   
   private boolean isMacOSX(){
      String os = System.getProperty("os.name").toLowerCase();
      if( os.startsWith("mac os x" ) )
         return true;
      return false;
   }

   private void initNativeLayer(){
      String os = "unknown";
      if(isWindows()) os = "Windows";
      else if(isMacOSX()) os = "Mac";
      else if(isLinux()) os = "Linux";
      String className = "edu.mit.csail.uid.NativeLayerFor" + os;

      try{
         Class c = Class.forName(className);
         Constructor constr = c.getConstructor();
         _native = (NativeLayer)constr.newInstance();
         _native.initIDE(this);
      }
      catch( Exception e){
         e.printStackTrace();
      }
   }

   public void loadFile(String file){
      (new FileAction()).doNew();
      try{
         getCurrentCodePane().loadFile(file);
         setCurrentFilename(file);
      }
      catch(IOException e){
         e.printStackTrace();
      }
   }

   private void adjustCodePaneWidth(){
      int pos = getWidth() - _sidePane.getMinimumSize().width-15;
      if(_codeAndUnitPane != null && pos >= 0)
         _codeAndUnitPane.setDividerLocation(pos);
   }

   public static void main(String[] args) {
      try{
         System.setProperty("apple.laf.useScreenMenuBar", "true");
         System.setProperty("com.apple.mrj.application.apple.menu.about.name", "SikuliIDE");
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e){
         e.printStackTrace();
      }
      SikuliIDE.getInstance(args);
   }

   public void jumpTo(String funcName) throws BadLocationException{
      SikuliPane pane = getCurrentCodePane();
      pane.jumpTo(funcName);
      pane.grabFocus();
   }

   public void jumpTo(int lineNo) throws BadLocationException{
      SikuliPane pane = getCurrentCodePane();
      pane.jumpTo(lineNo);
      pane.grabFocus();
   }
   
   public SikuliPane getCurrentCodePane(){
      if(_mainPane.getSelectedIndex() == -1)
         return null;
      JScrollPane scrPane = (JScrollPane)_mainPane.getSelectedComponent();
      SikuliPane ret = (SikuliPane)scrPane.getViewport().getView();
      return ret;
   }

   public void setCurrentFilename(String fname){
      if( fname.endsWith("/") )
         fname = fname.substring(0, fname.length()-1);
      int i = _mainPane.getSelectedIndex();
      fname = fname.substring(fname.lastIndexOf("/")+1);
      _mainPane.setTitleAt(i, fname);
      this.setTitle("Sikuli - " + fname);
   }

   public String getCurrentBundlePath(){
      SikuliPane pane = getCurrentCodePane();
      return pane.getSrcBundle();
   }
   
   public String getCurrentFilename(){
      SikuliPane pane = getCurrentCodePane();
      String fname = pane.getCurrentFilename();
      return fname;
   }

   public boolean closeCurrentTab(){
      SikuliPane pane = getCurrentCodePane();
      (new FileAction()).doCloseTab();
      if( pane == getCurrentCodePane() )
         return false;
      return true;
   }

   public void showPreferencesWindow(){
      PreferencesWin pwin = new PreferencesWin();
      pwin.setVisible(true);
   }

   class MenuAction implements ActionListener {
      protected Method actMethod = null;
      protected String action;
      
      public MenuAction(){
      }

      public MenuAction(String item) throws NoSuchMethodException{
         Class[] params = new Class[0];
         actMethod = this.getClass().getMethod(item, params);
         action = item;
      }
      
      public void actionPerformed(ActionEvent e) {
         if(actMethod != null){
            try{
               Debug.log("FileAction." + action);
               actMethod.invoke(this, new Object[0]);
            }
            catch(Exception ex){
               ex.printStackTrace();
            }
         }
      }
   }
   
   class ViewAction extends MenuAction {
      static final String UNIT_TEST = "toggleUnitTest";

      public ViewAction(){
         super();
      }

      public ViewAction(String item) throws NoSuchMethodException{
         super(item);
      }

      public void toggleUnitTest(){
         if( _chkShowUnitTest.getState() ){
            _sidePane.addTab("Unit Test", _unitPane);
            adjustCodePaneWidth();
         }
         else
            _sidePane.remove(_unitPane);
      }
   }

   public void quit(){
      (new FileAction()).doQuit();
   }

   class FileAction extends MenuAction {
      static final String NEW = "doNew";
      static final String OPEN = "doLoad";
      static final String SAVE = "doSave";
      static final String SAVE_AS = "doSaveAs";
      static final String EXPORT = "doExport";
      static final String CLOSE_TAB = "doCloseTab";
      static final String PREFERENCES = "doPreferences";
      static final String QUIT = "doQuit";

      public FileAction(){
         super();
      }

      public FileAction(String item) throws NoSuchMethodException{
         super(item);
      }
      
      public void doQuit(){
         SikuliIDE ide = SikuliIDE.getInstance();
         while(true){
            SikuliPane codePane = ide.getCurrentCodePane();
            if(codePane == null)
               break;
            if(!ide.closeCurrentTab())
               return;
         }
         System.exit(0);
      }

      public void doPreferences(){
         SikuliIDE.getInstance().showPreferencesWindow();
      }

      public void doNew(){
         SikuliPane codePane = new SikuliPane();
         JScrollPane scrPane = new JScrollPane(codePane);
         _mainPane.addTab("Untitled", scrPane);
         _mainPane.setSelectedIndex(_mainPane.getTabCount()-1);
         codePane.addCaretListener(new CaretListener(){
            public void caretUpdate(CaretEvent evt){
               SikuliPane comp = (SikuliPane)evt.getSource();
               int line = comp.getLineAtCaret();
               int col = comp.getColumnAtCaret();
               if(_status != null)
                  _status.setCaretPosition(line, col);
            }
         
         });
      }
      
      public void doLoad(){
         try{
            doNew();
            SikuliPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
            String fname = codePane.loadFile();
            if(fname!=null)
               SikuliIDE.getInstance().setCurrentFilename(fname);
            else
               doCloseTab();
         }
         catch(IOException eio){
            eio.printStackTrace();
         }
      }
      
      public void doSave(){
         try{
            SikuliPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
            String fname = codePane.saveFile();
            if(fname!=null)
               SikuliIDE.getInstance().setCurrentFilename(fname);
         }
         catch(IOException eio){
            eio.printStackTrace();
         }
      }

      public void doSaveAs(){
         try{
            SikuliPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
            String fname = codePane.saveAsFile();
            if(fname!=null)
               SikuliIDE.getInstance().setCurrentFilename(fname);
         }
         catch(IOException eio){
            eio.printStackTrace();
         }
      }

      public void doExport(){
         try{
            SikuliPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
            String fname = codePane.exportAsZip();
         }
         catch(Exception ex){
            ex.printStackTrace();
         }
      }


      public void doCloseTab(){
         SikuliPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
         try{
            if(codePane.close())
               _mainPane.remove(_mainPane.getSelectedIndex());
         }
         catch(IOException e){
            Debug.info("Can't close this tab: " + e.getStackTrace());
         }
      }
   }

   class ButtonRecord extends JToggleButton implements ActionListener {
      public ButtonRecord(){
         super();
         URL imageURL = SikuliIDE.class.getResource("/icons/record.png");
         setIcon(new ImageIcon(imageURL));
         setMaximumSize(new Dimension(26,26));
         setBorderPainted(false);
         setToolTipText("Record");
         addActionListener(this);
      }

      private void initSikuliGenerator(){
      }

      public void startSikuliGenerator(){
         try{
            String args[] = {"/tmp/sikuli-video.mov", "/tmp/sikuli-event.log"};

            //FIXME: test if this works..
            Class c = Class.forName("SikuliGenerator");
            Class[] t_params = {
               String[].class, SikuliPane.class
            };
            Constructor constr = c.getConstructor(t_params);
            constr.newInstance(new Object[]{
               args, getCurrentCodePane()
            });

            /*
            SikuliGenerator sg = new SikuliGenerator(args, 
                                                     getCurrentCodePane());
                                                     */
         }
         catch(Exception e){
            System.err.println("Error in starting up SikuliGenerator...");
            e.printStackTrace();
         }
      }

      public void actionPerformed(ActionEvent ae) {
         if( getModel().isSelected() ){
            Debug.info("start recording");
            Thread recordThread = new Thread(){
               public void run() {
                  Utils.runRecorder();
                  Debug.info("recording completed");
                  getModel().setSelected(false);
                  startSikuliGenerator();
               }
            };
            recordThread.start();
         }
         else{
            Debug.info("stop recording...");
            Utils.stopRecorder();
         }
      }
   }

   class ButtonRunViz extends ButtonRun {
      public ButtonRunViz(){
         super();
         URL imageURL = SikuliIDE.class.getResource("/icons/runviz.png");
         setIcon(new ImageIcon(imageURL));
         setToolTipText("Run and show each action");
         addHeader("setShowActions(True)");
      }
   }

   class ButtonRun extends JButton implements ActionListener {
      private java.util.List<String> _headers;
      private Thread _runningThread = null;

      protected void addHeader(String line){
         _headers.add(line);
      }

      public ButtonRun(){
         super();

         String[] h = new String[]{
            "from python.edu.mit.csail.uid.Sikuli import *",
            "setThrowException(False)",
      	    "setShowActions(False)"
         };
         _headers = new LinkedList<String>(Arrays.asList(h));
         URL imageURL = SikuliIDE.class.getResource("/icons/run.png");
         setIcon(new ImageIcon(imageURL));
         setMaximumSize(new Dimension(26,26));
         setBorderPainted(false);
         initTooltip();
         addActionListener(this);
      }

      private void initTooltip(){
         UserPreferences pref = UserPreferences.getInstance();
         String strHotkey = Utils.convertKeyToText(
               pref.getStopHotkey(), pref.getStopHotkeyModifiers() );
         String stopHint = "Press " + strHotkey + " to stop";
         setToolTipText("Run (" + stopHint + ")");
      }

      private int findErrorSource(Throwable thr, String filename) {
         StackTraceElement[] s;
         Throwable t = thr;
         while (t != null) {
            s = t.getStackTrace();
            for (int i = 0; i < s.length; i++) {
               StackTraceElement si = s[i];
               if( filename.equals( si.getFileName() ) ){
                  return si.getLineNumber();
               }
            }
            t = t.getCause();
         }
         return -1;
      }


      private void runPython(File f) throws IOException{
         PythonInterpreter py = new PythonInterpreter();
         Iterator<String> it = _headers.iterator();
         while(it.hasNext())
            py.exec(it.next());
         String path= SikuliIDE.getInstance().getCurrentBundlePath();
         py.exec("setBundlePath('" + path + "')");
         py.execfile( f.getAbsolutePath() );
      }

      public void stopRunning(){
         if(_runningThread != null){
            _runningThread.interrupt();
            _runningThread.stop();
         }
      }

      public void actionPerformed(ActionEvent ae) {
         _runningThread = new Thread(){
            public void run(){
               SikuliPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
               File tmpFile;
               try{
                  tmpFile = File.createTempFile("sikuli-tmp",".py");
                  try{
                     BufferedWriter bw = new BufferedWriter(
                                           new OutputStreamWriter( 
                                             new FileOutputStream(tmpFile), 
                                              "UTF8"));
                     codePane.write(bw);
                     SikuliIDE.getInstance().setVisible(false);
                     _console.clear();
                     codePane.setErrorHighlight(-1);
                     runPython(tmpFile);
                  }
                  catch(Exception e){
                     int srcLine = findErrorSource(e, tmpFile.getAbsolutePath());
                     if(srcLine != -1){
                        Debug.info("[Error] source lineNo: " + srcLine);
                        codePane.setErrorHighlight(srcLine);
                     }
                     Debug.info("[Error] " + e.toString());
                  } 
                  finally{
                     SikuliIDE.getInstance().setVisible(true);
                     _runningThread = null;
                  }
               }
               catch(IOException e){ e.printStackTrace(); }

            }
         };
         _runningThread.start();
      }
   }
}

class ButtonInsertImage extends JButton implements ActionListener{
   public ButtonInsertImage(){
      super();
      URL imageURL = SikuliIDE.class.getResource("/icons/insert-image.png");
      setIcon(new ImageIcon(imageURL));
      setMaximumSize(new Dimension(26,26));
      setBorderPainted(false);
      setToolTipText("Insert an image");
      addActionListener(this);
   }

   public void actionPerformed(ActionEvent ae) {
      SikuliPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
      final JFileChooser fcLoad = new JFileChooser();
      fcLoad.setCurrentDirectory(new File(System.getProperty("user.dir")));
      fcLoad.setAcceptAllFileFilterUsed(false);
      fcLoad.setFileFilter(new ImageFileFilter());
      fcLoad.setSelectedFile(null);
      if(fcLoad.showDialog(codePane, null) != JFileChooser.APPROVE_OPTION)
         return;
      File file = fcLoad.getSelectedFile();
      String path = Utils.slashify(file.getAbsolutePath(), false);
      System.out.println("load: " + path);
      ImageButton icon = new ImageButton(codePane, 
                                         codePane.getFileInBundle(path).getAbsolutePath());
      codePane.insertComponent(icon);
   }
}

class ButtonSubregion extends JButton implements ActionListener{
   public ButtonSubregion(){
      super();
      URL imageURL = SikuliIDE.class.getResource("/icons/subregion.png");
      setIcon(new ImageIcon(imageURL));
      setMaximumSize(new Dimension(26,26));
      setBorderPainted(false);
      setToolTipText("Find within a subregion");
      addActionListener(this);
   }

   public void actionPerformed(ActionEvent ae) {
      SikuliIDE ide = SikuliIDE.getInstance();
      SikuliPane codePane = ide.getCurrentCodePane();
      ide.setVisible(false);
      new ScreenOverlay(codePane, this);
      ide.setVisible(true);
   }

   public void complete(int x, int y, int w, int h){
      Debug.log(7,"subregion: %d %d %d %d", x, y, w, h);
      SikuliIDE ide = SikuliIDE.getInstance();
      SikuliPane codePane = ide.getCurrentCodePane();
      ide.setVisible(false);
      JButton icon = new RegionButton(codePane, x, y, w, h);
      ide.setVisible(true);
      codePane.insertComponent(icon);
   }
}
