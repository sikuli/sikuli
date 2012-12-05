/*
 * Copyright 2010-2011, Sikuli.org
 * Released under the MIT License.
 *
 */
package org.sikuli.ide;

import org.sikuli.ide.extmanager.ExtensionManagerFrame;
import org.sikuli.ide.sikuli_test.*;

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
import javax.swing.undo.*;
import javax.swing.event.UndoableEditListener;
import javax.swing.event.UndoableEditEvent;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXSearchField;
import org.jdesktop.swingx.JXTaskPaneContainer;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXPanel;


import org.sikuli.script.Debug;
import org.sikuli.script.CapturePrompt;
import org.sikuli.script.ScriptRunner;
import org.sikuli.script.ScreenImage;
import org.sikuli.script.Observer;
import org.sikuli.script.Subject;
import org.sikuli.script.internal.hotkey.HotkeyManager;
import org.sikuli.script.HotkeyListener;
import org.sikuli.script.HotkeyEvent;

import org.apache.commons.cli.CommandLine;

import com.explodingpixels.macwidgets.MacUtils;


public class SikuliIDE extends JFrame {
   final static boolean ENABLE_RECORDING = false;
   final static boolean ENABLE_UNIFIED_TOOLBAR = true;

   final static Color COLOR_SEARCH_FAILED = Color.red;
   final static Color COLOR_SEARCH_NORMAL = Color.black;

   private static NativeLayer _native;

   private ConsolePane _console;
   private CloseableTabbedPane _mainPane;
   private JXCollapsiblePane  _sidePane;
   private JSplitPane _mainSplitPane;

   private JTabbedPane _auxPane;
   private JPanel _unitPane;
   private StatusBar _status;
   private JXCollapsiblePane _cmdList;
   private JXSearchField _searchField;
   private FindAction _findHelper;

   private CaptureButton _btnCapture;
   private ButtonRun _btnRun, _btnRunViz;

   private JMenuBar _menuBar = new JMenuBar();
   private JMenu _fileMenu = new JMenu(_I("menuFile"));
   private JMenu _editMenu = new JMenu(_I("menuEdit"));
   private JMenu _runMenu = new JMenu(_I("menuRun"));
   private JMenu _viewMenu = new JMenu(_I("menuView"));   
   private JMenu _toolMenu = new JMenu(_I("menuTool"));
   private JMenu _helpMenu = new JMenu(_I("menuHelp"));
   private JCheckBoxMenuItem _chkShowUnitTest;
   private UnitTestRunner _testRunner;

   private UndoAction _undoAction = new UndoAction();
   private RedoAction _redoAction = new RedoAction();

   private static CommandLine _cmdLine;
   private static boolean _useStderr = false;

   private static SikuliIDE _instance = null;

   private static Icon PY_SRC_ICON = getIconResource("/icons/py-src-16x16.png");

   private boolean _inited = false;

   static String _I(String key, Object... args){ 
      return I18N._I(key, args);
   }

   public static void errorMsg(String msg){
      if(_useStderr)
         System.err.println(msg);
      else
         JOptionPane.showMessageDialog(null, msg);
   }

   public static ImageIcon getIconResource(String name) {
      URL url= SikuliIDE.class.getResource(name);
      if (url == null) {
         Debug.error("Warning: could not load \""+name+"\" icon");
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
      for(int i=0;i<_mainPane.getTabCount();i++){
         try{
            JScrollPane scrPane = (JScrollPane)_mainPane.getComponentAt(i);
            SikuliPane codePane = (SikuliPane)scrPane.getViewport().getView();
            if(codePane.isDirty()){
               getRootPane().putClientProperty("Window.documentModified", true);
               return true;
            }
         }
         catch(Exception e){
            Debug.log("checkDirtyPanes: " + e.getMessage());
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

   private void initEditMenu() throws NoSuchMethodException{
      int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      _editMenu.setMnemonic(java.awt.event.KeyEvent.VK_E);
      JMenuItem undoItem = _editMenu.add( _undoAction );
      undoItem.setAccelerator(
         KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, scMask));
      JMenuItem redoItem = _editMenu.add( _redoAction );
      redoItem.setAccelerator(
         KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, scMask|InputEvent.SHIFT_MASK));
      _editMenu.addSeparator();

      _editMenu.add( createMenuItem(_I("menuEditCut"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, scMask),
               new EditAction(EditAction.CUT)));
      _editMenu.add( createMenuItem(_I("menuEditCopy"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, scMask),
               new EditAction(EditAction.COPY)));
      _editMenu.add( createMenuItem(_I("menuEditPaste"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, scMask),
               new EditAction(EditAction.PASTE)));
      _editMenu.add( createMenuItem(_I("menuEditSelectAll"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, scMask),
               new EditAction(EditAction.SELECT_ALL)));
      _editMenu.addSeparator();

      JMenu findMenu = new JMenu(_I("menuFind"));
      _findHelper = new FindAction();
      findMenu.setMnemonic(KeyEvent.VK_F);
      findMenu.add( createMenuItem(_I("menuFindFind"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, scMask),
               new FindAction(FindAction.FIND)));
      findMenu.add( createMenuItem(_I("menuFindFindNext"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, scMask),
               new FindAction(FindAction.FIND_NEXT)));
      findMenu.add( createMenuItem(_I("menuFindFindPrev"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, scMask | InputEvent.SHIFT_MASK),
               new FindAction(FindAction.FIND_PREV)));
      _editMenu.add(findMenu);
      _editMenu.addSeparator();
      _editMenu.add( createMenuItem(_I("menuEditIndent"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, 0),
               new EditAction(EditAction.INDENT)));
      _editMenu.add( createMenuItem(_I("menuEditUnIndent"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_TAB, InputEvent.SHIFT_MASK),
               new EditAction(EditAction.UNINDENT)));
   }

   private void initHelpMenu() throws NoSuchMethodException{
      int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      _helpMenu.setMnemonic(java.awt.event.KeyEvent.VK_H);
      _helpMenu.add( createMenuItem(_I("menuHelpCheckUpdate"), 
               null, new HelpAction(HelpAction.CHECK_UPDATE)));
      _helpMenu.addSeparator();
      _helpMenu.add( createMenuItem(_I("menuHelpGuide"), 
               null, new HelpAction(HelpAction.OPEN_GUIDE)));
      _helpMenu.add( createMenuItem(_I("menuHelpDocumentations"), 
               null, new HelpAction(HelpAction.OPEN_DOC)));
      _helpMenu.add( createMenuItem(_I("menuHelpAsk"), 
               null, new HelpAction(HelpAction.OPEN_ASK)));
      _helpMenu.add( createMenuItem(_I("menuHelpBugReport"), 
               null, new HelpAction(HelpAction.OPEN_BUG_REPORT)));
      _helpMenu.add( createMenuItem(_I("menuHelpTranslation"), 
               null, new HelpAction(HelpAction.OPEN_TRANSLATION)));
      _helpMenu.addSeparator();
      _helpMenu.add( createMenuItem(_I("menuHelpHomepage"), 
               null, new HelpAction(HelpAction.OPEN_HOMEPAGE)));
   }
   
   private void initToolMenu() throws NoSuchMethodException{
      int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
      _toolMenu.setMnemonic(java.awt.event.KeyEvent.VK_T);
      _toolMenu.add( createMenuItem(_I("menuToolExtensions"), 
               null,
               new ToolAction(ToolAction.EXTENSIONS)));

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
         initEditMenu();
         initRunMenu();
         initViewMenu();
         initToolMenu();
         initHelpMenu();
      }
      catch(NoSuchMethodException e){
         e.printStackTrace();
      }

      _menuBar.add(_fileMenu);
      _menuBar.add(_editMenu);
      _menuBar.add(_runMenu);
      _menuBar.add(_viewMenu);
      _menuBar.add(_toolMenu);      
      _menuBar.add(_helpMenu);
      frame.setJMenuBar(_menuBar);
   }

   private String[] CommandCategories = {
      _I("cmdListFind"),
      _I("cmdListMouse"),
      _I("cmdListKeyboard"),
      _I("cmdListObserver")
   };
   
   private String[][] CommandsOnToolbar = {
      {"exists"}, {"PATTERN", "[timeout]"},
      {_I("cmdExists")},
      {"find"}, {"PATTERN"},
      {_I("cmdFind")},
      {"findAll"}, {"PATTERN"},
      {_I("cmdFindAll")},
      {"wait"}, {"PATTERN", "[timeout]"},
      {_I("cmdWait")},
      {"waitVanish"}, {"PATTERN", "[timeout]"},
      {_I("cmdWaitVanish")},
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
      {"----"},{},{},
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

   private JComponent createCommandPane(){
      JXTaskPaneContainer con = new JXTaskPaneContainer();

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
      JXTaskPane setPane = new JXTaskPane();
      setPane.setTitle(_I("cmdListSettings"));
      setPane.add(chkAutoCapture);
      int cat = 0;
      JXTaskPane taskPane = new JXTaskPane();
      taskPane.setTitle(CommandCategories[cat++]);
      con.add(taskPane);
      for(int i=0;i<CommandsOnToolbar.length;i++){
         String cmd = CommandsOnToolbar[i++][0];
         String[] params = CommandsOnToolbar[i++];
         String[] desc = CommandsOnToolbar[i];
         if( cmd.equals("----") ){
            taskPane = new JXTaskPane();
            taskPane.setTitle(CommandCategories[cat++]);
            con.add(taskPane);
         }
         else
            taskPane.add(new ButtonGenCommand(cmd, desc[0], params));
      }
      con.add(setPane);
      Dimension conDim = con.getSize();
      con.setPreferredSize(new Dimension(250,850));
      _cmdList = new JXCollapsiblePane(JXCollapsiblePane.Direction.LEFT);
      _cmdList.setMinimumSize(new Dimension(0,0));
      _cmdList.add(new JScrollPane(con));
      _cmdList.setCollapsed(false);
      return _cmdList;
   }

   private JToolBar initCmdToolbar(){
      JToolBar toolbar = new JToolBar(JToolBar.VERTICAL);
      toolbar.add(createCommandPane());
      return toolbar;
   }


   private JToolBar initToolbar(){
      if(ENABLE_UNIFIED_TOOLBAR)
         MacUtils.makeWindowLeopardStyle(this.getRootPane());

      JToolBar toolbar = new JToolBar();
      _btnRun = new ButtonRun();
      JButton btnInsertImage = new ButtonInsertImage();
      _btnCapture = new CaptureButton();
      JButton btnSubregion = new ButtonSubregion();
      _btnRunViz = new ButtonRunViz();
      toolbar.add(_btnCapture);
      toolbar.add(btnInsertImage);
      toolbar.add(btnSubregion);
      toolbar.add(Box.createHorizontalGlue());
      /*
      if( ENABLE_RECORDING ){
         JToggleButton btnRecord = new ButtonRecord();
         toolbar.add(btnRecord);
      }
      */
      toolbar.add(_btnRun);
      toolbar.add(_btnRunViz);
      toolbar.add(Box.createHorizontalGlue());
      toolbar.add( createSearchField() );
      toolbar.add(Box.createRigidArea(new Dimension(7,0))); 
      toolbar.setFloatable(false);
      //toolbar.setMargin(new Insets(0, 0, 0, 5));
      return toolbar;
   }

   private JComponent createSearchField(){
      /*
      if(Utils.isMacOSX()){
         _searchField = new JTextField();
         _searchField.putClientProperty("JTextField.variant", "search");
      }
      else{
         _searchField = new JXSearchField();
      }
      */
      _searchField = new JXSearchField("Find");
      _searchField.setUseNativeSearchFieldIfPossible(true);
      //_searchField.setLayoutStyle(JXSearchField.LayoutStyle.MAC);
      _searchField.setMinimumSize(new Dimension(220,30));
      _searchField.setPreferredSize(new Dimension(220,30));
      _searchField.setMaximumSize(new Dimension(380,30));
      _searchField.setMargin(new Insets(0, 3, 0, 3));

      _searchField.setCancelAction(new ActionListener(){
         public void actionPerformed(ActionEvent evt) {    
            getCurrentCodePane().requestFocus();
            _findHelper.setFailed(false);
         }
      });
      _searchField.setFindAction(new ActionListener(){
         public void actionPerformed(ActionEvent evt) {    
            //FIXME: On Linux the found selection disappears somehow
            if(!Utils.isLinux())  //HACK
               _searchField.selectAll();
            boolean ret = _findHelper.findNext(_searchField.getText());
            _findHelper.setFailed(!ret);
         }
      });
      _searchField.addKeyListener(new KeyAdapter(){
         public void keyReleased(java.awt.event.KeyEvent ke) {
            boolean ret;
            if(ke.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER){
               //FIXME: On Linux the found selection disappears somehow
               if(!Utils.isLinux())  //HACK
                  _searchField.selectAll();
               ret = _findHelper.findNext(_searchField.getText());
            }
            else
               ret = _findHelper.findStr(_searchField.getText());
            _findHelper.setFailed(!ret);
         }
      });
      return _searchField;
   }


   private void initTabPane(){
      _mainPane = new CloseableTabbedPane();
      _mainPane.setUI(new AquaCloseableTabbedPaneUI());
      _mainPane.addCloseableTabbedPaneListener(
                new CloseableTabbedPaneListener(){
         public boolean closeTab(int i){
            try{
               JScrollPane scrPane = (JScrollPane)_mainPane.getComponentAt(i);
               SikuliPane codePane = (SikuliPane)scrPane.getViewport().getView();
               Debug.log(8, "close tab " + i + " n:" + _mainPane.getComponentCount());
               boolean ret = codePane.close();
               Debug.log(8, "after close tab n:" + _mainPane.getComponentCount());
               checkDirtyPanes();
               return ret;
            }
            catch(Exception e){
               Debug.info("Can't close this tab: " + e.getStackTrace());
               e.printStackTrace();
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
            updateUndoRedoStates();
         }
      });
            
   }

   private void initAuxPane(){
      _auxPane = new JTabbedPane();
      _console = new ConsolePane();
      _auxPane.addTab(_I("paneMessage"), _console);
      if(Utils.isWindows() || Utils.isLinux())
         _auxPane.setBorder(BorderFactory.createEmptyBorder(5,8,5,8)); 
      _auxPane.setMinimumSize(new Dimension(0, 100));
   }

   private void initUnitPane(){
      _testRunner = new UnitTestRunner();
      _unitPane = _testRunner.getPanel();
      _chkShowUnitTest.setState(false);
      addAuxTab(_I("paneTestTrace"), _testRunner.getTracePane());
   }

   private void initSidePane(){
      initUnitPane();
      _sidePane = new JXCollapsiblePane(JXCollapsiblePane.Direction.RIGHT);
      _sidePane.setMinimumSize(new Dimension(0,0));
      CloseableTabbedPane tabPane = new CloseableTabbedPane();
      _sidePane.getContentPane().add(tabPane);
      tabPane.setMinimumSize(new Dimension(0,0));
      tabPane.addTab(_I("tabUnitTest"), _unitPane);
      tabPane.addCloseableTabbedPaneListener(new CloseableTabbedPaneListener(){
         public boolean closeTab(int tabIndexToClose){
            _sidePane.setCollapsed(true);
            _chkShowUnitTest.setState(false);
            return false;
         }
      });
      _sidePane.setCollapsed(true);
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
         private boolean isKeyNextTab(java.awt.event.KeyEvent ke){
            if(ke.getKeyCode() == java.awt.event.KeyEvent.VK_TAB && 
               ke.getModifiers() == InputEvent.CTRL_MASK)
                  return true;
            if(ke.getKeyCode() == java.awt.event.KeyEvent.VK_CLOSE_BRACKET && 
               ke.getModifiers()==(InputEvent.META_MASK|InputEvent.SHIFT_MASK) )
                  return true;
            return false;
         }

         private boolean isKeyPrevTab(java.awt.event.KeyEvent ke){
            if(ke.getKeyCode() == java.awt.event.KeyEvent.VK_TAB && 
               ke.getModifiers()==(InputEvent.CTRL_MASK|InputEvent.SHIFT_MASK) )
                  return true;
            if(ke.getKeyCode() == java.awt.event.KeyEvent.VK_OPEN_BRACKET && 
               ke.getModifiers()==(InputEvent.META_MASK|InputEvent.SHIFT_MASK) )
                  return true;
            return false;
         }

         public void eventDispatched( AWTEvent e ){
            java.awt.event.KeyEvent ke = (java.awt.event.KeyEvent)e;
            //Debug.log(ke.toString());
            if( ke.getID() == java.awt.event.KeyEvent.KEY_PRESSED ){
               if( isKeyNextTab(ke) ) 
                  nextTab();
               else if( isKeyPrevTab(ke) )
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
   
   // Constructor 
   protected SikuliIDE(String[] args) {
      super("Sikuli IDE");

      ScriptRunner.getInstance(getPyArgs());

      _native.initIDE(this);


      initMenuBars(this);
      final Container c = getContentPane();
      c.setLayout(new BorderLayout());
      initTabPane();
      initAuxPane();
      initSidePane();


      JPanel codeAndUnitPane = new JPanel(new BorderLayout(10,10));
      codeAndUnitPane.setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
      codeAndUnitPane.add(_mainPane, BorderLayout.CENTER);
      codeAndUnitPane.add(_sidePane, BorderLayout.EAST);
      _mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            codeAndUnitPane, _auxPane);
      _mainSplitPane.setResizeWeight(1.0);
      _mainSplitPane.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

      JPanel editPane = new JPanel(new BorderLayout(0,0));
      editPane.add(createCommandPane(), BorderLayout.WEST);
      editPane.add(_mainSplitPane, BorderLayout.CENTER);


      c.add(initToolbar(), BorderLayout.NORTH);
      c.add(editPane, BorderLayout.CENTER);
      c.add(initStatusbar(), BorderLayout.SOUTH);
      c.doLayout();

      setSize(UserPreferences.getInstance().getIdeSize());
      setLocation(UserPreferences.getInstance().getIdeLocation());

      initShortcutKeys();
      //setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      initHotkeys();
      initWindowListener();
      initTooltip();

      if( _cmdLine.getArgs().length > 0 ){
         for(String f : _cmdLine.getArgs())
            loadFile(Utils.slashify(f, false));
      }
      else
         (new FileAction()).doNew(null);

      _inited = true;
      restoreSession();
      setVisible(true);
      autoCheckUpdate();
      getCurrentCodePane().requestFocus();
   }

   private void initTooltip(){
      ToolTipManager tm = ToolTipManager.sharedInstance();
      tm.setDismissDelay(30000);
   }

   private void autoCheckUpdate(){
      UserPreferences pref = UserPreferences.getInstance();
      if( !pref.getCheckUpdate() )
         return;
      long last_check = pref.getCheckUpdateTime();
      long now = (new Date()).getTime();
      if(now - last_check > 1000*86400){
         Debug.log(3, "check update");
         (new HelpAction()).checkUpdate(true);
      }
      pref.setCheckUpdateTime();
   }

   private void saveSession(){
      int nTab = _mainPane.getTabCount();
      StringBuffer sbuf = new StringBuffer();
      for(int i=0;i<nTab;i++){
         try{
            JScrollPane scrPane = (JScrollPane)_mainPane.getComponentAt(i);
            SikuliPane codePane = (SikuliPane)scrPane.getViewport().getView();
            File f = codePane.getCurrentFile();
            if( f != null){
               String bundlePath= codePane.getSrcBundle();
               Debug.log(5, "save session: " + bundlePath);
               if(i!=0)
                  sbuf.append(";");
               sbuf.append(bundlePath);
            }
         }
         catch(Exception e){
            e.printStackTrace();
         }
      }
      UserPreferences.getInstance().setIdeSession(sbuf.toString());
   }

   private void restoreSession(){
      String session_str = UserPreferences.getInstance().getIdeSession();
      if(session_str == null)
         return;
      String[] filenames = session_str.split(";");
      for(int i=0;i<filenames.length;i++){
         Debug.log(5, "restore session: " + filenames[i]);
         File f = new File(filenames[i]);
         if(f.exists())
            loadFile(filenames[i]);
      }
   }

   private void initWindowListener(){
      setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
      addWindowListener(new WindowAdapter(){
         public void windowClosing(WindowEvent e) {
            SikuliIDE.this.quit();
         }
      });
      addComponentListener(new ComponentAdapter() {
         public void componentResized(ComponentEvent e) {
            UserPreferences.getInstance().setIdeSize(SikuliIDE.this.getSize());
         }
         public void componentMoved(ComponentEvent e) {
            UserPreferences.getInstance().setIdeLocation(SikuliIDE.this.getLocation());
         }
      });
   }

   public boolean isInited(){ return _inited; }


   public void removeCaptureHotkey(int key, int mod){
      HotkeyManager.getInstance()._removeHotkey(key, mod);
   }

   public void installCaptureHotkey(int key, int mod){
      HotkeyManager.getInstance()._addHotkey(key, mod, new HotkeyListener(){
         public void hotkeyPressed(HotkeyEvent e){
            onQuickCapture();
         }
      });
   }

   private void initHotkeys(){
      UserPreferences pref = UserPreferences.getInstance();
      int key = pref.getCaptureHotkey();
      int mod = pref.getCaptureHotkeyModifiers();
      installCaptureHotkey(key, mod);
      key = pref.getStopHotkey();
      mod = pref.getStopHotkeyModifiers();
      HotkeyManager.getInstance()._addHotkey(key, mod, new HotkeyListener(){
         public void hotkeyPressed(HotkeyEvent e){
            onStopRunning();
         }
      });
   }

   static private void initNativeLayer(){
      String os = "unknown";
      if(Utils.isWindows()) os = "Windows";
      else if(Utils.isMacOSX()) os = "Mac";
      else if(Utils.isLinux()) os = "Linux";
      String className = "org.sikuli.ide.NativeLayerFor" + os;

      try{
         Class c = Class.forName(className);
         Constructor constr = c.getConstructor();
         _native = (NativeLayer)constr.newInstance();
      }
      catch( Exception e){
         e.printStackTrace();
      }
   }

   public void loadFile(String file){
      (new FileAction()).doNew(null);
      try{
         getCurrentCodePane().loadFile(file);
         setCurrentFilename(file);
      }
      catch(IOException e){
         Debug.error("Can't load file " + file);
         Debug.error(e.getMessage());
      }
   }


   static boolean _runningSkl = false;
   public static int runSikuli(String filename, String[] args) throws IOException{
      int exitCode = -1;
      File file = new File(filename);
      if(!file.exists())
         throw new IOException(filename + ": No such file");

      ScriptRunner srunner = new ScriptRunner(args);
      try{
         srunner.runPython(Utils.slashify(filename,true));
      }
      catch(Exception e){
         java.util.regex.Pattern p = 
            java.util.regex.Pattern.compile("SystemExit: ([0-9]+)");
         Matcher matcher = p.matcher(e.toString());
         if(matcher.find()){
            exitCode = Integer.parseInt(matcher.group(1));
            Debug.info(_I("msgExit", matcher.group(1) ));
         }
         else{
            errorMsg( _I("msgRunningSklError", filename, e) );
         }
      }
      srunner.close();
      return exitCode;
   }

   public static int runSkl(String filename, String[] args) throws IOException{
      _runningSkl = true;
      File file = new File(filename);
      if(!file.exists())
         throw new IOException(filename + ": No such file");
      String name = file.getName();
      name = name.substring(0, name.lastIndexOf('.'));
      File tmpDir = Utils.createTempDir();
      File sikuliDir = new File(tmpDir + File.separator + name + ".sikuli");
      sikuliDir.mkdir();
      Utils.unzip(filename, sikuliDir.getAbsolutePath());
      return runSikuli(sikuliDir.getAbsolutePath(), args);
   }

   static String[] getPyArgs(){
      String[] pargs = _cmdLine.getArgs();
      if( _cmdLine.hasOption("args") ) 
         pargs = _cmdLine.getOptionValues("args");
      return pargs; 
   }

   //FIXME: supports args
   private static void runUnitTest(String filename, String[] args){
      TextUnitTestRunner tester = new TextUnitTestRunner(args);
      File file = new File(filename);
      filename = file.getAbsolutePath();
      if(filename.endsWith(".sikuli")){
         try{
            boolean result = tester.testSikuli(filename);
            if(!result)
               System.exit(1);
            System.exit(0);
         }
         catch(Exception e){
            Debug.error(e.getMessage());
            System.exit(2);
         }
      }
      System.exit(-1);
   }

   public static void main(String[] args) {
      initNativeLayer();
      CommandArgs cmdArgs = new CommandArgs();
      _cmdLine = cmdArgs.getCommandLine(args);

      if( _cmdLine.hasOption("h") ){
         cmdArgs.printHelp();
         return;
      }

      if( _cmdLine.hasOption("s") ){
         _useStderr = true;
      }
      
      if( _cmdLine.hasOption("test") ){
         runUnitTest(_cmdLine.getOptionValue("test"), getPyArgs());
      }
         
      if(args!=null && args.length>=1){
         try{
            String[] pargs = getPyArgs();
            int exitCode = 0;
            if( _cmdLine.hasOption("run") ){
               String filename = _cmdLine.getOptionValue("run");
               File file = new File(filename);
               filename = file.getAbsolutePath();
               if(filename.endsWith(".skl"))
                  exitCode = runSkl(filename, pargs);
               else if(filename.endsWith(".sikuli")){
                  exitCode = runSikuli(filename, pargs);
               }
               System.exit(exitCode);
            }
            if( _cmdLine.getArgs().length>0 ){
               String file = _cmdLine.getArgs()[0];
               if(file.endsWith(".skl")){
                  exitCode = runSkl(file, pargs);
                  System.exit(exitCode);
               }
            }
         }
         catch(IOException e){
            errorMsg(e.getMessage());
            System.exit(-2);
         }
      }
      try{
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e){
         e.printStackTrace();
      }

      if(Utils.isMacOSX()){
         _native.initApp();
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
      String fname = pane.getCurrentFile().getAbsolutePath();
      return fname;
   }

   public boolean closeCurrentTab(){
      SikuliPane pane = getCurrentCodePane();
      (new FileAction()).doCloseTab(null);
      if( pane == getCurrentCodePane() )
         return false;
      return true;
   }

   public void showPreferencesWindow(){
      PreferencesWin pwin = new PreferencesWin();
      pwin.setVisible(true);
   }
   
   public void showExtensionsFrame(){
      ExtensionManagerFrame extmg = ExtensionManagerFrame.getInstance();
      extmg.setVisible(true);
   }

   class MenuAction implements ActionListener {
      protected Method actMethod = null;
      protected String action;
      
      public MenuAction(){
      }

      public MenuAction(String item) throws NoSuchMethodException{
         Class[] params = new Class[0];
         Class[] paramsWithEvent = new Class[1];
         try{
            paramsWithEvent[0] = Class.forName("java.awt.event.ActionEvent");
            actMethod = this.getClass().getMethod(item, paramsWithEvent);
            action = item;
         }
         catch(ClassNotFoundException cnfe){
            Debug.error("Can't find menu action: " + cnfe);
         }
      }
      
      public void actionPerformed(ActionEvent e) {
         if(actMethod != null){
            try{
               Debug.log(3, "MenuAction." + action);
               Object[] params = new Object[1];
               params[0] = e;
               actMethod.invoke(this, params);
            }
            catch(Exception ex){
               ex.printStackTrace();
            }
         }
      }
   }
   
   class ToolAction extends MenuAction {
	   static final String EXTENSIONS = "extensions";
	   
	   public ToolAction(){
		   super();
	   }
	   
	   public ToolAction(String item) throws NoSuchMethodException{
		   super(item);
	   }

	   public void extensions(ActionEvent ae){
              showExtensionsFrame();
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

      public void run(ActionEvent ae){
         _btnRun.runCurrentScript();
      }

      public void runShowActions(ActionEvent ae){
         _btnRunViz.runCurrentScript();
      }

   }

// Uncomment this for Mac OS X 10.5 (Java5)
/*  
    static void openURL(String url){
      try{
	 String cmd[] = {"open", url};
	 Process p = Runtime.getRuntime().exec(cmd);
	 p.waitFor();
	 return;
      }
      catch(Exception e){
	 Debug.info("Can't open " + url);
      }
   }
*/

   static void openURL(String url){
	   try{
		   URL u = new URL(url);
		   java.awt.Desktop.getDesktop().browse(u.toURI());
	   }
	   catch(Exception ex){
		   ex.printStackTrace();
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

      public void toggleCmdList(ActionEvent ae){
         _cmdList.setCollapsed(!_cmdList.isCollapsed());
      }

      public void toggleUnitTest(ActionEvent ae){
         if(_chkShowUnitTest.getState())
            _sidePane.setCollapsed(false);
         else
            _sidePane.setCollapsed(true);
      }
   }

   public void quit(){
      saveSession();
      (new FileAction()).doQuit(null);
   }

   class HelpAction extends MenuAction {
      static final String CHECK_UPDATE = "doCheckUpdate";
      static final String OPEN_DOC = "openDoc";
      static final String OPEN_GUIDE = "openGuide";
      static final String OPEN_ASK = "openAsk";
      static final String OPEN_BUG_REPORT = "openBugReport";
      static final String OPEN_TRANSLATION = "openTranslation";
      static final String OPEN_HOMEPAGE = "openHomepage";

      public HelpAction(){
         super();
      }
      public HelpAction(String item) throws NoSuchMethodException{
         super(item);
      }



      
      public void openDoc(ActionEvent ae){
         openURL("http://doc.sikuli.org/");
      }

      public void openGuide(ActionEvent ae){
         openURL("http://doc.sikuli.org/index.html#complete-guide-to-sikuli-script");
      }

      public void openAsk(ActionEvent ae){
         openURL("https://answers.launchpad.net/sikuli");
      }

      public void openBugReport(ActionEvent ae){
         openURL("https://bugs.launchpad.net/sikuli/+filebug");
      }

      public void openTranslation(ActionEvent ae){
         openURL("https://translations.launchpad.net/sikuli/sikuli-x/+translations");
      }

      public void openHomepage(ActionEvent ae){
         openURL("http://sikuli.org");
      }

      public boolean checkUpdate(boolean isAutoCheck){
         AutoUpdater au = new AutoUpdater();
         UserPreferences pref = UserPreferences.getInstance();
         Debug.log("Check update");
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

      public void doCheckUpdate(ActionEvent ae){
         if(!checkUpdate(false)){
            JOptionPane.showMessageDialog(null, 
                  _I("msgNoUpdate"), "Sikuli " + IDESettings.SikuliVersion,
                  JOptionPane.INFORMATION_MESSAGE);
         }
      }
   }

   class FindAction extends MenuAction {
      static final String FIND = "doFind";
      static final String FIND_NEXT = "doFindNext";
      static final String FIND_PREV = "doFindPrev";

      public FindAction(){
         super();
      }

      public FindAction(String item) throws NoSuchMethodException{
         super(item);
      }

      public void doFind(ActionEvent ae){
         _searchField.selectAll();
         _searchField.requestFocus();
      }

      public void doFindNext(ActionEvent ae){
         findNext(_searchField.getText());
      }

      public void doFindPrev(ActionEvent ae){
         findPrev(_searchField.getText());
      }

      private boolean _find(String str, int begin, boolean forward){
         SikuliPane codePane = getCurrentCodePane();
         int pos = codePane.search(str, begin, forward);
         Debug.log(7, "find \"" + str + "\" at " + begin + ", found: " + pos);
         if(pos < 0)
            return false;
         return true;
      }

      public boolean findStr(String str){
         if(getCurrentCodePane() != null)
            return _find(str, getCurrentCodePane().getCaretPosition(), true);
         return false;
      }

      public boolean findPrev(String str){
         if(getCurrentCodePane() != null)
            return _find(str, getCurrentCodePane().getCaretPosition(), false);
         return false;
      }

      public boolean findNext(String str){
         if(getCurrentCodePane() != null)
            return _find(str, 
                         getCurrentCodePane().getCaretPosition()+str.length(),
                         true);
         return false;
      }

      public void setFailed(boolean failed){
         Debug.log(7, "search failed: " + failed);
         _searchField.setBackground(Color.white);
         if(failed){
            _searchField.setForeground(COLOR_SEARCH_FAILED);
         }
         else
            _searchField.setForeground(COLOR_SEARCH_NORMAL);
      }

   }

   class EditAction extends MenuAction {
      static final String CUT = "doCut";
      static final String COPY = "doCopy";
      static final String PASTE = "doPaste";
      static final String SELECT_ALL = "doSelectAll";
      static final String INDENT = "doIndent";
      static final String UNINDENT = "doUnindent";

      public EditAction(){
         super();
      }

      public EditAction(String item) throws NoSuchMethodException{
         super(item);
      }

      private void performEditorAction(String action, ActionEvent ae){
         SikuliIDE ide = SikuliIDE.getInstance();
         SikuliPane pane = ide.getCurrentCodePane();
         pane.getActionMap().get(action).actionPerformed(ae);
      }

      public void doCut(ActionEvent ae){
         performEditorAction(DefaultEditorKit.cutAction, ae);
      }

      public void doCopy(ActionEvent ae){
         performEditorAction(DefaultEditorKit.copyAction, ae);
      }

      public void doPaste(ActionEvent ae){
         performEditorAction(DefaultEditorKit.pasteAction, ae);
      }

      public void doSelectAll(ActionEvent ae){
         performEditorAction(DefaultEditorKit.selectAllAction, ae);
      }

      public void doIndent(ActionEvent ae){
         SikuliIDE ide = SikuliIDE.getInstance();
         SikuliPane pane = ide.getCurrentCodePane();
         (new SikuliEditorKit.InsertTabAction()).actionPerformed(pane);
      }

      public void doUnindent(ActionEvent ae){
         SikuliIDE ide = SikuliIDE.getInstance();
         SikuliPane pane = ide.getCurrentCodePane();
         (new SikuliEditorKit.DeindentAction()).actionPerformed(pane);
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
      
      public void doQuit(ActionEvent ae){
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

      public void doPreferences(ActionEvent ae){
         SikuliIDE.getInstance().showPreferencesWindow();
      }

      public void doNew(ActionEvent ae){
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
      
      public void doLoad(ActionEvent ae){
         try{
            doNew(ae);
            SikuliPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
            String fname = codePane.loadFile();
            if(fname!=null)
               SikuliIDE.getInstance().setCurrentFilename(fname);
            else
               doCloseTab(ae);
         }
         catch(IOException eio){
            eio.printStackTrace();
         }
      }
      
      public void doSave(ActionEvent ae){
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

      public void doSaveAs(ActionEvent ae){
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

      public void doExport(ActionEvent ae){
         try{
            SikuliPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
            String fname = codePane.exportAsZip();
         }
         catch(Exception ex){
            ex.printStackTrace();
         }
      }


      public void doCloseTab(ActionEvent ae){
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
            Debug.error("Error in starting up SikuliGenerator...");
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
         URL imageURL = SikuliIDE.class.getResource("/icons/run_big_yl.png");
         setIcon(new ImageIcon(imageURL));
         setToolTipText(_I("menuRunRunAndShowActions"));
         setText(_I("btnRunSlowMotionLabel"));
      }

      protected void runPython(File f) throws Exception{
         ScriptRunner srunner = new ScriptRunner(getPyArgs());
         try{
            String path = SikuliIDE.getInstance().getCurrentBundlePath();
            srunner.addTempHeader("initSikuli()");
            srunner.addTempHeader("setShowActions(True)");
            srunner.runPython(path, f);
            srunner.close();
         }
         catch(Exception e){
            srunner.close();
            throw e;
         }
      }
   }

   class ButtonRun extends ToolbarButton implements ActionListener {
      private Thread _runningThread = null;

      public ButtonRun(){
         super();

         URL imageURL = SikuliIDE.class.getResource("/icons/run_big_green.png");
         setIcon(new ImageIcon(imageURL));
         initTooltip();
         addActionListener(this);
         setText(_I("btnRunLabel"));
         //setMaximumSize(new Dimension(45,45));
      }

      protected void runPython(File f) throws Exception{
         ScriptRunner srunner = new ScriptRunner(getPyArgs());
         try{
            String path = SikuliIDE.getInstance().getCurrentBundlePath();
            srunner.addTempHeader("initSikuli()");
            srunner.runPython(path, f);
            srunner.close();
         }
         catch(Exception e){
            srunner.close();
            throw e;
         }
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
         //codePane.setErrorHighlight(line);
      }

      public void resetErrorMark(){
         JScrollPane scrPane = (JScrollPane)_mainPane.getSelectedComponent();
         LineNumberView lnview = (LineNumberView)(scrPane.getRowHeader().getView());
         lnview.resetErrorMark();
         SikuliPane codePane = SikuliIDE.this.getCurrentCodePane();
         //codePane.setErrorHighlight(-1);
      }

      public void runCurrentScript() {
         _runningThread = new Thread(){
            public void run(){
               SikuliPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
               File tmpFile;
               try{
                  tmpFile = File.createTempFile("sikuli-tmp",".py");
                  tmpFile.deleteOnExit();
                  try{
                     BufferedWriter bw = new BufferedWriter(
                                           new OutputStreamWriter( 
                                             new FileOutputStream(tmpFile), 
                                              "UTF8"));
                     codePane.write(bw);
                     SikuliIDE.getInstance().setVisible(false);
                     _console.clear();
                     resetErrorMark();
                     runPython(tmpFile);
                  }
                  catch(Exception e){
                     java.util.regex.Pattern p = 
                        java.util.regex.Pattern.compile("SystemExit: ([0-9]+)");
                     Matcher matcher = p.matcher(e.toString());
                     if(matcher.find()){
                        Debug.info(_I("msgExit", matcher.group(1)));
                     }
                     else{
                        Debug.error(_I("msgStopped"));
                        int srcLine = findErrorSource(e, 
                                          tmpFile.getAbsolutePath());
                        if(srcLine != -1){
                           Debug.error( _I("msgErrorLine", srcLine) );
                           addErrorMark(srcLine);
                           try{ codePane.jumpTo(srcLine); }
                           catch(BadLocationException be){}
                           codePane.requestFocus();
                        }
                        Debug.error( _I("msgErrorMsg", e.toString()) );
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

   class UndoAction extends AbstractAction{ 
      public UndoAction(){ 
         super(_I("menuEditUndo")); 
         setEnabled(false); 
      } 
      public void updateUndoState(){ 
         if (getCurrentCodePane() != null && 
             getCurrentCodePane().getUndoManager().canUndo()){
            setEnabled(true); 
         } 
         else { 
            setEnabled(false); 
         } 
      } 
      public void actionPerformed(ActionEvent e){ 
         UndoManager undo = getCurrentCodePane().getUndoManager();
         try{ 
            undo.undo(); 
         } 
         catch (CannotUndoException ex){} 
         updateUndoState(); 
         _redoAction.updateRedoState(); 
      } 
   }

   class RedoAction extends AbstractAction{ 
      public RedoAction() { 
         super(_I("menuEditRedo")); 
         setEnabled(false); 
      } 
      public void actionPerformed(ActionEvent e){ 
         UndoManager undo = getCurrentCodePane().getUndoManager();
         try{ 
            undo.redo(); 
         }
         catch(CannotRedoException ex){} 
         updateRedoState(); 
         _undoAction.updateUndoState(); 
      } 
      protected void updateRedoState() { 
         if (getCurrentCodePane() != null && 
             getCurrentCodePane().getUndoManager().canRedo()){
            setEnabled(true); 
         } 
         else { 
            setEnabled(false); 
         } 
      }
   }

   public void updateUndoRedoStates(){
      _undoAction.updateUndoState();
      _redoAction.updateRedoState();
   }

}

class ButtonInsertImage extends ToolbarButton implements ActionListener{
   public ButtonInsertImage(){
      super();
      URL imageURL = SikuliIDE.class.getResource("/icons/insert-image-icon.png");
      setIcon(new ImageIcon(imageURL));
      setText(SikuliIDE._I("btnInsertImageLabel"));
      //setMaximumSize(new Dimension(26,26));
      setToolTipText(SikuliIDE._I("btnInsertImageHint"));
      addActionListener(this);
   }

   public void actionPerformed(ActionEvent ae) {
      SikuliPane codePane = SikuliIDE.getInstance().getCurrentCodePane();
      File file = new FileChooser(SikuliIDE.getInstance()).loadImage();
      if(file == null)
         return;
      String path = Utils.slashify(file.getAbsolutePath(), false);
      Debug.info("load image: " + path);
      ImageButton icon = new ImageButton(codePane, 
                                         codePane.copyFileToBundle(path).getAbsolutePath());
      codePane.insertComponent(icon);
   }
}

class ButtonSubregion extends ToolbarButton implements ActionListener, Observer{
   public ButtonSubregion(){
      super();
      URL imageURL = SikuliIDE.class.getResource("/icons/region-icon.png");
      setIcon(new ImageIcon(imageURL));
      setText(SikuliIDE._I("btnRegionLabel"));
      //setMaximumSize(new Dimension(26,26));
      setToolTipText( SikuliIDE._I("btnRegionHint") );
      addActionListener(this);
   }

   public void update(Subject s){
      if(s instanceof CapturePrompt){
         CapturePrompt cp = (CapturePrompt)s;
         ScreenImage r = cp.getSelection();
         if(r!=null){
            cp.close();
            Rectangle roi = r.getROI();
            complete((int)roi.getX(), (int)roi.getY(),
                     (int)roi.getWidth(), (int)roi.getHeight());
         }
         SikuliIDE.getInstance().setVisible(true);
      }
   }

   public void actionPerformed(ActionEvent ae) {
      SikuliIDE ide = SikuliIDE.getInstance();
      SikuliPane codePane = ide.getCurrentCodePane();
      ide.setVisible(false);
      CapturePrompt prompt = new CapturePrompt(null, this);
      prompt.prompt(SikuliIDE._I("msgCapturePrompt"), 500);
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
