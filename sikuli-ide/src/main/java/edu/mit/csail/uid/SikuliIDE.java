package edu.mit.csail.uid;

import edu.mit.csail.uid.sikuli_test.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.util.*;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class SikuliIDE extends JFrame {
   boolean ENABLE_RECORDING = false;

   private NativeLayer _native;

   private ConsolePane _console;
   private CloseableTabbedPane _mainPane, _sidePane;
   private JSplitPane _codeAndUnitPane;
   private JTabbedPane _auxPane;
   private JPanel _unitPane;
   private StatusBar _status;
   private JToolBar _cmdToolBar;

   private CaptureButton _btnCapture;
   private ButtonRun _btnRun, _btnRunViz;

   private JMenuBar _menuBar = new JMenuBar();
   private JMenu _fileMenu = new JMenu(_I("menuFile"));
   private JMenu _runMenu = new JMenu(_I("menuRun"));
   private JMenu _viewMenu = new JMenu(_I("menuView"));
   private JMenu _helpMenu = new JMenu(_I("menuHelp"));
   private JCheckBoxMenuItem _chkShowUnitTest;
   private UnitTestRunner _testRunner;

   private static SikuliIDE _instance = null;

   private static Icon PY_SRC_ICON = getIconResource("/icons/py-src-16x16.png");

   private boolean _inited = false;

   static String _I(String key, Object... args){ 
      return I18N._I(key, args);
   }

   public static ImageIcon getIconResource(String name) {
      URL url= SikuliIDE.class.getResource(name);
      if (url == null) {
         System.err.println("Warning: could not load \""+name+"\" icon");
         return null;
      }
      return new ImageIcon(url);
   }


   public void onStopRunning(){
      Debug.log(2, "StopRunning");
      this.setVisible(true);
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

   //FIXME: singleton lock
   public static synchronized SikuliIDE getInstance(String args[]){
      Debug.log(5, "create SikuliIDE " + args);
      if( _instance == null ){
         _instance = new SikuliIDE(args);
      }
      return _instance;
   }

   public static synchronized SikuliIDE getInstance(){
      Debug.log(5, "create SikuliIDE()");
      return getInstance(null);
   }

   private JMenuItem createMenuItem(JMenuItem item, KeyStroke shortcut, ActionListener listener){
      if(shortcut != null) 
         item.setAccelerator(shortcut);
      item.addActionListener(listener);
      return item;
   }

   boolean checkDirtyPanes(){
      for(int i=0;i<_mainPane.getComponentCount();i++){
         JScrollPane scrPane = (JScrollPane)_mainPane.getComponentAt(i);
         SikuliPane codePane = (SikuliPane)scrPane.getViewport().getView();
         if(codePane.isDirty()){
            getRootPane().putClientProperty("Window.documentModified", true);
            return true;
         }
      }
      getRootPane().putClientProperty("Window.documentModified", false);
      return false;
   }

   private JMenuItem createMenuItem(String name, KeyStroke shortcut, ActionListener listener){
      JMenuItem item = new JMenuItem(name);
      return createMenuItem(item, shortcut, listener);
   }
   
   private void initRunMenu() throws NoSuchMethodException{
      int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      _runMenu.setMnemonic(java.awt.event.KeyEvent.VK_R);
      _runMenu.add( createMenuItem(_I("menuRunRun"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, scMask),
               new RunAction(RunAction.RUN)));
      _runMenu.add( createMenuItem(_I("menuRunRunAndShowActions"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, 
                  InputEvent.ALT_MASK | scMask),
               new RunAction(RunAction.RUN_SHOW_ACTIONS)));

      UserPreferences pref = UserPreferences.getInstance();
      JMenuItem stopItem = createMenuItem(_I("menuRunStop"), 
               KeyStroke.getKeyStroke(
                  pref.getStopHotkey(), pref.getStopHotkeyModifiers()),
               new RunAction(RunAction.RUN_SHOW_ACTIONS));
      stopItem.setEnabled(false);
      _runMenu.add(stopItem);
   }

   private void initFileMenu() throws NoSuchMethodException{
      int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      _fileMenu.setMnemonic(java.awt.event.KeyEvent.VK_F);
      _fileMenu.add( createMenuItem(_I("menuFileNew"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, scMask),
               new FileAction(FileAction.NEW)));
      _fileMenu.add( createMenuItem(_I("menuFileOpen"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, scMask),
               new FileAction(FileAction.OPEN)));
      _fileMenu.add( createMenuItem(_I("menuFileSave"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, scMask),
               new FileAction(FileAction.SAVE)));
      _fileMenu.add( createMenuItem(_I("menuFileSaveAs"),
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, 
                  InputEvent.SHIFT_MASK | scMask),
               new FileAction(FileAction.SAVE_AS)));
      _fileMenu.add( createMenuItem(_I("menuFileExport"),
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 
                  InputEvent.SHIFT_MASK | scMask),
               new FileAction(FileAction.EXPORT)));
      _fileMenu.addSeparator();
      if(!Utils.isMacOSX()){
         _fileMenu.add( createMenuItem(_I("menuFilePreferences"),
                  KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, scMask),
                  new FileAction(FileAction.PREFERENCES)));
      }
      _fileMenu.add( createMenuItem(_I("menuFileCloseTab"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, scMask),
               new FileAction(FileAction.CLOSE_TAB)));
      if(!Utils.isMacOSX()){
         _fileMenu.addSeparator();
         _fileMenu.add( createMenuItem(_I("menuFileQuit"), 
                  null, new FileAction(FileAction.QUIT)));
      }
   }

   private void initHelpMenu() throws NoSuchMethodException{
      int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      _helpMenu.setMnemonic(java.awt.event.KeyEvent.VK_H);
      _helpMenu.add( createMenuItem(_I("menuHelpCheckUpdate"), 
               null, new HelpAction(HelpAction.CHECK_UPDATE)));
   }

   private void initViewMenu() throws NoSuchMethodException{
      int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      _viewMenu.setMnemonic(java.awt.event.KeyEvent.VK_V);
      _chkShowUnitTest = new JCheckBoxMenuItem(_I("menuViewUnitTest"));
      _viewMenu.add( createMenuItem(_chkShowUnitTest, 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, scMask),
               new ViewAction(ViewAction.UNIT_TEST)));

      JMenuItem chkShowCmdList = 
         new JCheckBoxMenuItem(_I("menuViewCommandList"), true);
      _viewMenu.add( createMenuItem(chkShowCmdList, 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, scMask),
               new ViewAction(ViewAction.CMD_LIST)));
   }

   private void initMenuBars(JFrame frame){
      try{
         initFileMenu();
         initRunMenu();
         initViewMenu();
         initHelpMenu();
      }
      catch(NoSuchMethodException e){
         e.printStackTrace();
      }

      _menuBar.add(_fileMenu);
      _menuBar.add(_runMenu);
      _menuBar.add(_viewMenu);
      _menuBar.add(_helpMenu);
      frame.setJMenuBar(_menuBar);
   }

   private String[][] CommandsOnToolbar = {
      {"find"}, {"PATTERN"},
      {_I("cmdFind")},
      {"findAll"}, {"PATTERN"},
      {_I("cmdFindAll")},
      {"wait"}, {"PATTERN", "[timeout]"},
      {_I("cmdWait")},
      {"waitVanish"}, {"PATTERN", "[timeout]"},
      {_I("cmdWaitVanish")},
      {"exists"}, {"PATTERN", "[timeout]"},
      {_I("cmdExists")},
      {"----"},{},{},
      {"click"}, {"PATTERN","[modifiers]"},
      {_I("cmdClick")},
      {"doubleClick"}, {"PATTERN","[modifiers]"},
      {_I("cmdDoubleClick")},
      {"rightClick"}, {"PATTERN","[modifiers]"},
      {_I("cmdRightClick")},
      {"hover"}, {"PATTERN"},
      {_I("cmdHover")},
      {"dragDrop"}, {"PATTERN", "PATTERN", "[modifiers]"},
      {_I("cmdDragDrop")},
/*
      {"drag"}, {"PATTERN"},
      {"dropAt"}, {"PATTERN", "[delay]"},
*/
      {"type"}, {"_text", "[modifiers]"},
      {_I("cmdType")},
      {"type"}, {"PATTERN", "_text", "[modifiers]"},
      {_I("cmdType2")},
      {"paste"}, {"_text", "[modifiers]"},
      {_I("cmdPaste")},
      {"paste"}, {"PATTERN", "_text", "[modifiers]"},
      {_I("cmdPaste2")},
      {"----"},{},{},
      {"onAppear"}, {"PATTERN", "_handler"},
      {_I("cmdOnAppear")},
      {"onVanish"}, {"PATTERN", "_handler"},
      {_I("cmdOnVanish")},
      {"onChange"}, {"_handler"},
      {_I("cmdOnChange")},
      {"observe"}, {"[time]","[background]"},
      {_I("cmdObserve")},
   };

   private JToolBar initCmdToolbar(){
      JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
      UserPreferences pref = UserPreferences.getInstance();
      JCheckBox chkAutoCapture = 
         new JCheckBox(_I("cmdListAutoCapture"), 
                       pref.getAutoCaptureForCmdButtons());
      chkAutoCapture.addChangeListener(new ChangeListener(){
         public void stateChanged(javax.swing.event.ChangeEvent e){
            boolean flag = ((JCheckBox)e.getSource()).isSelected();
            UserPreferences pref = UserPreferences.getInstance();
            pref.setAutoCaptureForCmdButtons(flag);
         }
      });
      toolbar.add(new JLabel(_I("cmdListCommandList")));
      toolbar.add(chkAutoCapture);
      for(int i=0;i<CommandsOnToolbar.length;i++){
         String cmd = CommandsOnToolbar[i++][0];
         String[] params = CommandsOnToolbar[i++];
         String[] desc = CommandsOnToolbar[i];
         if( cmd.equals("----") )
            toolbar.addSeparator();
         else
            toolbar.add(new ButtonGenCommand(cmd, desc[0], params));
      }
      return toolbar;
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
               Debug.log(3, "close tab: " + _mainPane.getComponentCount());
               boolean ret = codePane.close();
               Debug.log(3, "close tab after: " + _mainPane.getComponentCount());
               checkDirtyPanes();
               return ret;
            }
            catch(Exception e){
               Debug.info("Can't close this tab: " + e.getStackTrace());
               return false;
            }
         }

      });

      _mainPane.addChangeListener(new ChangeListener(){
         public void stateChanged(javax.swing.event.ChangeEvent e){
            JTabbedPane tab = (JTabbedPane)e.getSource();
            int i = tab.getSelectedIndex();
            if(i>=0)
               SikuliIDE.this.setTitle(tab.getTitleAt(i));

         }
      });
            
   }

   private void initAuxPane(){
      _auxPane = new JTabbedPane();
      _console = new ConsolePane();
      _auxPane.addTab(_I("paneMessage"), _console);
   }

   private void initUnitPane(){
      _testRunner = new UnitTestRunner();
      _unitPane = _testRunner.getPanel();
      _chkShowUnitTest.setState(false);
      (new ViewAction()).toggleUnitTest();
      addAuxTab(_I("paneTestTrace"), _testRunner.getTracePane());
   }

   private void initSidePane(){
      _sidePane = new CloseableTabbedPane();
      _sidePane.addChangeListener(new ChangeListener(){
         public void stateChanged(javax.swing.event.ChangeEvent e){
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

   // Constructor 
   protected SikuliIDE(String[] args) {
      super("Sikuli IDE");

      ScriptRunner srunner = ScriptRunner.getInstance(args);

      initNativeLayer();


      initMenuBars(this);
      final Container c = getContentPane();
      c.setLayout(new BorderLayout());
      initTabPane();
      initAuxPane();
      initSidePane();
      initUnitPane();

      _codeAndUnitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT, true, _mainPane, _sidePane);
      JSplitPane mainAndConsolePane = new JSplitPane(
            JSplitPane.VERTICAL_SPLIT, true, _codeAndUnitPane, _auxPane);
      _cmdToolBar = initCmdToolbar();

      c.add(initToolbar(), BorderLayout.NORTH);
      c.add(_cmdToolBar, BorderLayout.WEST);
      c.add(mainAndConsolePane, BorderLayout.CENTER);
      c.add(initStatusbar(), BorderLayout.SOUTH);
      c.doLayout();

      setSize(DEFAULT_WINDOW_W, DEFAULT_WINDOW_H);
      adjustCodePaneWidth();
      mainAndConsolePane.setDividerLocation(500);

      initShortcutKeys();
      //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      initHotkeys();
      initWindowListener();

      if(args!=null && args.length>=1)
         loadFile(args[0]);
      else
         (new FileAction()).doNew();

      _inited = true;
      setVisible(true);
      checkUpdate();
   }

   private void checkUpdate(){
      UserPreferences pref = UserPreferences.getInstance();
      long last_check = pref.getCheckUpdateTime();
      long now = (new Date()).getTime();
      if(now - last_check > 1000*86400){
         Debug.log(3, "check update");
         (new HelpAction()).checkUpdate(true);
      }
      pref.setCheckUpdateTime();
   }

   private void initWindowListener(){
      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent e) {
            SikuliIDE.this.quit();
         }
      });
   }

   public boolean isInited(){ return _inited; }


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

   private void initNativeLayer(){
      String os = "unknown";
      if(Utils.isWindows()) os = "Windows";
      else if(Utils.isMacOSX()) os = "Mac";
      else if(Utils.isLinux()) os = "Linux";
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


   static boolean _runningSkl = false;
   public static void runSkl(String filename, String[] args) throws IOException{
      _runningSkl = true;
      String name = (new File(filename)).getName();
      name = name.substring(0, name.lastIndexOf('.'));
      File tmpDir = Utils.createTempDir();
      File sikuliDir = new File(tmpDir + File.separator + name + ".sikuli");
      sikuliDir.mkdir();
      Utils.unzip(filename, sikuliDir.getAbsolutePath());
      ScriptRunner srunner = ScriptRunner.getInstance(args);
      try{
         srunner.runPython(Utils.slashify(sikuliDir.getAbsolutePath(),true));
      }
      catch(Exception e){
         JOptionPane.showMessageDialog(null, 
               _I("msgRunningSklError", filename, e));
      }
      System.exit(0);
   }

   public static void main(String[] args) {
      if(args!=null && args.length>=1){
         try{
            if(args[0].endsWith(".skl")){
               runSkl(args[0], args);
               return;
            }
         }
         catch(IOException e){
            System.err.println("Can't open file: " + args[0] + "\n" + e);
         }
      }
      try{
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e){
         e.printStackTrace();
      }

      if(Utils.isMacOSX()){
         NativeLayerForMac.initApp();
         try{ Thread.sleep(1000); } catch(InterruptedException ie){}
      }
      if(!_runningSkl){
         SikuliIDE.getInstance(args);
      }
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
      this.setTitle(fname);
   }

   public void setTitle(String title){
      super.setTitle("Sikuli " + IDESettings.SikuliVersion + " - " + title);
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
               Debug.log(2, "MenuAction." + action);
               actMethod.invoke(this, new Object[0]);
            }
            catch(Exception ex){
               ex.printStackTrace();
            }
         }
      }
   }
   
   class RunAction extends MenuAction {
      static final String RUN = "run";
      static final String RUN_SHOW_ACTIONS = "runShowActions";

      public RunAction(){
         super();
      }

      public RunAction(String item) throws NoSuchMethodException{
         super(item);
      }

      public void run(){
         _btnRun.runCurrentScript();
      }

      public void runShowActions(){
         _btnRunViz.runCurrentScript();
      }

   }

   class ViewAction extends MenuAction {
      static final String UNIT_TEST = "toggleUnitTest";
      static final String CMD_LIST = "toggleCmdList";

      public ViewAction(){
         super();
      }

      public ViewAction(String item) throws NoSuchMethodException{
         super(item);
      }

      public void toggleCmdList(){
         _cmdToolBar.setVisible(!_cmdToolBar.isVisible());
      }

      public void toggleUnitTest(){
         if( _chkShowUnitTest.getState() ){
            _sidePane.addTab(_I("tabUnitTest"), _unitPane);
            adjustCodePaneWidth();
         }
         else
            _sidePane.remove(_unitPane);
      }
   }

   public void quit(){
      (new FileAction()).doQuit();
   }

   class HelpAction extends MenuAction {
      static final String CHECK_UPDATE = "doCheckUpdate";
      public HelpAction(){
         super();
      }
      public HelpAction(String item) throws NoSuchMethodException{
         super(item);
      }
      
      public boolean checkUpdate(boolean isAutoCheck){
         AutoUpdater au = new AutoUpdater();
         UserPreferences pref = UserPreferences.getInstance();
         if( au.checkUpdate() ){
            String ver = au.getVersion();
            String details = au.getDetails();
            if(isAutoCheck && pref.getLastSeenUpdate().equals(ver))
               return false;
            UpdateFrame f = new UpdateFrame(
                  _I("dlgUpdateAvailable", ver), details );
            UserPreferences.getInstance().setLastSeenUpdate(ver);
            return true;
         }
         return false;
      }

      public void doCheckUpdate(){
         if(!checkUpdate(false)){
            JOptionPane.showMessageDialog(null, _I("msgNoUpdate"));
         }
      }
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
         scrPane.setRowHeaderView(new LineNumberView(codePane));
         _mainPane.addTab(_I("tabUntitled"), scrPane);
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
         codePane.requestFocus();
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
         setToolTipText(_I("menuRunRunAndShowActions"));
      }

      protected void runPython(File f) throws IOException{
         ScriptRunner srunner = ScriptRunner.getInstance(null);
         String path = SikuliIDE.getInstance().getCurrentBundlePath();
         srunner.addTempHeader("initSikuli()");
         srunner.addTempHeader("setShowActions(True)");
         srunner.runPython(path, f);
      }
   }

   class ButtonRun extends JButton implements ActionListener {
      private Thread _runningThread = null;

      public ButtonRun(){
         super();

         URL imageURL = SikuliIDE.class.getResource("/icons/run.png");
         setIcon(new ImageIcon(imageURL));
         setMaximumSize(new Dimension(26,26));
         setBorderPainted(false);
         initTooltip();
         addActionListener(this);
      }

      protected void runPython(File f) throws IOException{
         ScriptRunner srunner = ScriptRunner.getInstance(null);
         String path= SikuliIDE.getInstance().getCurrentBundlePath();
         srunner.addTempHeader("initSikuli()");
         srunner.runPython(path, f);
      }

      private void initTooltip(){
         UserPreferences pref = UserPreferences.getInstance();
         String strHotkey = Utils.convertKeyToText(
               pref.getStopHotkey(), pref.getStopHotkeyModifiers() );
         String stopHint = _I("btnRunStopHint", strHotkey);
         setToolTipText(_I("btnRun", stopHint));
      }

      private int findErrorSource(Throwable thr, String filename) {
         String err = thr.toString();
         if(err.startsWith("Traceback")){
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                  ", line (\\d+),");
            java.util.regex.Matcher m = p.matcher(err);
            if(m.find()){
               Debug.log(4, "error line: " + m.group(1));
               return Integer.parseInt(m.group(1));
            }
         }
         else if(err.startsWith("SyntaxError")){
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(
                  ", (\\d+), (\\d+),");
            java.util.regex.Matcher m = p.matcher(err);
            if(m.find()){
               Debug.log(4, "SyntaxError error line: " + m.group(1));
               return Integer.parseInt(m.group(1));
            }
         }
         return _findErrorSource(thr, filename);
      }

      private int _findErrorSource(Throwable thr, String filename) {
         StackTraceElement[] s;
         Throwable t = thr;
         while (t != null) {
            s = t.getStackTrace();
            Debug.log(4, "stack trace:");
            for (int i = s.length-1; i >= 0 ; i--){
               StackTraceElement si = s[i];
               Debug.log(4, si.getLineNumber() + " " + si.getFileName());
               if( si.getLineNumber()>=0 && filename.equals( si.getFileName() ) ){
                  return si.getLineNumber();
               }
            }
            t = t.getCause();
            Debug.log(3, "cause: " + t);
         }
         return -1;
      }



      public void stopRunning(){
         if(_runningThread != null){
            _runningThread.interrupt();
            _runningThread.stop();
         }
      }

      public void actionPerformed(ActionEvent ae) {
         runCurrentScript();
      }

      public void addErrorMark(int line){
         JScrollPane scrPane = (JScrollPane)_mainPane.getSelectedComponent();
         LineNumberView lnview = (LineNumberView)(scrPane.getRowHeader().getView());
         lnview.addErrorMark(line);
         SikuliPane codePane = SikuliIDE.this.getCurrentCodePane();
         codePane.setErrorHighlight(line);
      }

      public void resetErrorMark(){
         JScrollPane scrPane = (JScrollPane)_mainPane.getSelectedComponent();
         LineNumberView lnview = (LineNumberView)(scrPane.getRowHeader().getView());
         lnview.resetErrorMark();
         SikuliPane codePane = SikuliIDE.this.getCurrentCodePane();
         codePane.setErrorHighlight(-1);
      }

      public void runCurrentScript() {
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
                     resetErrorMark();
                     //codePane.setErrorHighlight(-1);
                     runPython(tmpFile);
                  }
                  catch(Exception e){
                     java.util.regex.Pattern p = 
                        java.util.regex.Pattern.compile("SystemExit:( [0-9]+)");
                     Matcher matcher = p.matcher(e.toString());
                     if(matcher.find()){
                        Debug.info(_I("msgExit", matcher.group(1)));
                     }
                     else{
                        Debug.info(_I("msgStopped"));
                        int srcLine = findErrorSource(e, 
                                          tmpFile.getAbsolutePath());
                        if(srcLine != -1){
                           Debug.info( _I("msgErrorLine", srcLine) );
                           addErrorMark(srcLine);
                        }
                        Debug.info( _I("msgErrorMsg", e.toString()) );
                     }
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
      setToolTipText(SikuliIDE._I("btnInsertImageHint"));
      addActionListener(this);
   }

   public void actionPerformed(ActionEvent ae) {
      SikuliPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
      File file = new FileChooser(SikuliIDE.getInstance()).loadImage();
      if(file == null)
         return;
      String path = Utils.slashify(file.getAbsolutePath(), false);
      System.out.println("load: " + path);
      ImageButton icon = new ImageButton(codePane, 
                                         codePane.getFileInBundle(path).getAbsolutePath());
      codePane.insertComponent(icon);
   }
}

class ButtonSubregion extends JButton implements ActionListener, Observer{
   public ButtonSubregion(){
      super();
      URL imageURL = SikuliIDE.class.getResource("/icons/subregion.png");
      setIcon(new ImageIcon(imageURL));
      setMaximumSize(new Dimension(26,26));
      setBorderPainted(false);
      setToolTipText( SikuliIDE._I("btnRegionHint") );
      addActionListener(this);
   }

   public void update(Subject s){
      if(s instanceof CapturePrompt){
         CapturePrompt cp = (CapturePrompt)s;
         ScreenImage r = cp.getSelection();
         if(r==null)
            return;
         cp.close();
         Rectangle roi = r.getROI();
         complete((int)roi.getX(), (int)roi.getY(),
                  (int)roi.getWidth(), (int)roi.getHeight());
      }
   }

   public void actionPerformed(ActionEvent ae) {
      SikuliIDE ide = SikuliIDE.getInstance();
      SikuliPane codePane = ide.getCurrentCodePane();
      ide.setVisible(false);
      CapturePrompt prompt = new CapturePrompt(null, this);
      prompt.prompt(500);
      ide.setVisible(true);
   }

   public void complete(int x, int y, int w, int h){
      Debug.log(7,"Region: %d %d %d %d", x, y, w, h);
      SikuliIDE ide = SikuliIDE.getInstance();
      SikuliPane codePane = ide.getCurrentCodePane();
      ide.setVisible(false);
      JButton icon = new RegionButton(codePane, x, y, w, h);
      codePane.insertComponent(icon);
      ide.setVisible(true);
      codePane.requestFocus();
   }
}
