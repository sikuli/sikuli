package org.sikuli.guide;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoManager;

import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.OS;
import org.sikuli.script.Screen;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;


public class SklEditor extends JFrame {
   
   private SklStepModel _currentStepModel;
   private SklStepEditView _currentStepEditView;
   
   private SklDocumentListView _documentListView;   
   private SklDocument _document;
   
   public SklEditor(){
      
      
      // TODO: get this to work
//      view.getActionMap().put("Undo", UndoManagerHelper.getUndoAction(manager));
//      view.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
//      view.setFocusable(true);
      
      _document = new SklDocument();
      _documentListView = new SklDocumentListView(_document);

      _currentStepModel = new SklStepModel(); 
      _currentStepEditView = new SklStepEditView(null);
      
      setDocument(_document);
      
//      scrollPane.setPreferredSize(new Dimension(120, 80));
//      add(scrollPane,BorderLayout.CENTER);

      JPanel centeringWrapper = new JPanel();
      centeringWrapper.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
      //centeringWrapper.setBorder(BorderFactory.createLoweredBevelBorder());
      centeringWrapper.setOpaque(true);
      centeringWrapper.setBackground(Color.white);
      centeringWrapper.setLayout(new GridBagLayout());
      GridBagConstraints c = new GridBagConstraints();
      c.anchor = GridBagConstraints.CENTER;
      centeringWrapper.add(_currentStepEditView, c);
      
      JScrollPane scrollPane = new JScrollPane(centeringWrapper);
      
      JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            _documentListView, scrollPane);
      splitPane.setDividerLocation(250);
  
      UndoManager manager = new UndoManager();
      
//      JToolBar toolbar = new JToolBar();
//      toolbar.add(UndoManagerHelper.getUndoAction(manager));
//      toolbar.add(UndoManagerHelper.getRedoAction(manager));
//      toolbar.add(new NewAction());
//      toolbar.addSeparator();
//      toolbar.add(new LoadAction());
//      toolbar.add(new SaveAction());
//      toolbar.add(new SaveAsAction());
//      toolbar.addSeparator();
//      toolbar.add(new CaptureAction());
//      toolbar.addSeparator();
//      toolbar.add(new PlayAction());
//      toolbar.add(new PlayAllAction());

  //      view.addUndoableEditListener(manager);

      Container content = getContentPane();
//      content.add(toolbar, BorderLayout.NORTH);
      content.add(splitPane,BorderLayout.CENTER);      
      
      
      SklEditorMenuBar menuBar = new SklEditorMenuBar();
      setJMenuBar(menuBar);
      
      setSize(1000,600);
      setLocationRelativeTo(null);
      setVisible(true);      
      setTitle(getTitleString("untitled"));
      
      
      
      
      
      
      setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
      addWindowListener(new WindowAdapter(){
         @Override
         public void windowClosing(WindowEvent arg0) {
            Debug.info("[Editor] editor window is closing");
            _storyRunnerFrame.dispose();
            dispose();
         }
      });
      
   }
   

   
   String _I(String str){
      if (str.equals("menuInsert")){
         return "Insert";
      }else if (str.equals("menuInsertStep")){
         return "New Step";
      }else if (str.equals("menuInsertAnchor")){
         return "New Target";
      }else if (str.equals("menuInsertText")){
         return "New Text";
      }else if (str.equals("menuFile")){
         return "File";
      }else if (str.equals("menuFileNew")){
         return "New";
      }else if (str.equals("menuFileOpen")){
         return "Open";
      }else if (str.equals("menuFileSave")){
         return "Save";
      }else if (str.equals("menuFileSaveAs")){
         return "Save As";
      }else if (str.equals("menuFileQuit")){
         return "Quit";
      }else if (str.equals("menuRunAll")){
         return "Run All";
      }else if (str.equals("menuRunCurrent")){
         return "Run Current";
      }else if (str.equals("menuRunFromCurrent")){
         return "Run From Current";
      }else if (str.equals("menuRun")){
         return "Run";
      }else if (str.equals("menuEdit")){
         return "Edit";
      }else if (str.equals("menuEditCloneScreen")){
         return "Clone Screen";
      }
         
      return str;

   }
   
   class SklEditorMenuBar extends JMenuBar{
      private JMenu _insertMenu = new JMenu(_I("menuInsert"));
      private JMenu _fileMenu = new JMenu(_I("menuFile"));
      private JMenu _editMenu = new JMenu(_I("menuEdit"));
      private JMenu _runMenu = new JMenu(_I("menuRun"));

      SklEditorMenuBar(){
         try {
            initFileMenu();
            initInsertMenu();
            initRunMenu();
            initEditMenu();
         } catch (NoSuchMethodException e) {
            e.printStackTrace();
         }         
         add(_fileMenu);
         add(_editMenu);
         add(_insertMenu);
         add(_runMenu);
      }
   
      private JMenuItem createMenuItem(String name, KeyStroke shortcut, ActionListener listener){
         JMenuItem item = new JMenuItem(name);
         return createMenuItem(item, shortcut, listener);
      }
      
      private JMenuItem createMenuItem(JMenuItem item, KeyStroke shortcut, ActionListener listener){
         if(shortcut != null) 
            item.setAccelerator(shortcut);
         item.addActionListener(listener);
         return item;
      }
      
      private void initRunMenu() throws NoSuchMethodException {
         int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
         _runMenu.setMnemonic(java.awt.event.KeyEvent.VK_R);
         _runMenu.add( createMenuItem(_I("menuRunAll"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, scMask),
               new RunAction(RunAction.RUN_ALL)));
         _runMenu.add( createMenuItem(_I("menuRunCurrent"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, InputEvent.SHIFT_MASK | scMask),
               new RunAction(RunAction.RUN_CURRENT)));
         _runMenu.add( createMenuItem(_I("menuRunFromCurrent"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, scMask),
               new RunAction(RunAction.RUN_FROM_CURRENT)));
      }
      
      private void initEditMenu() throws NoSuchMethodException {
         int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
         _editMenu.setMnemonic(java.awt.event.KeyEvent.VK_E);
         _editMenu.add( createMenuItem(_I("menuEditCloneScreen"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, scMask),
               new EditAction(EditAction.CLONE_SCREEN)));      

      }
      
      private void initInsertMenu() throws NoSuchMethodException{
         int scMask = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
         _insertMenu.setMnemonic(java.awt.event.KeyEvent.VK_I);
         _insertMenu.add( createMenuItem(_I("menuInsertStep"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, scMask),
               new InsertAction(InsertAction.INSERT_STEP)));     
         _insertMenu.add( createMenuItem(_I("menuInsertAnchor"), 
               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, scMask),
               new InsertAction(InsertAction.INSERT_ANCHOR)));     
//         _insertMenu.add( createMenuItem(_I("menuInsertText"), 
//               KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, scMask),
//               new InsertAction(InsertAction.INSERT_TEXT)));
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
//         _fileMenu.add( createMenuItem(_I("menuFileExport"),
//                  KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 
//                     InputEvent.SHIFT_MASK | scMask),
//                  new FileAction(FileAction.EXPORT)));
         _fileMenu.addSeparator();
//         if(!Utils.isMacOSX()){
//            _fileMenu.add( createMenuItem(_I("menuFilePreferences"),
//                     KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, scMask),
//                     new FileAction(FileAction.PREFERENCES)));
//         }
//         _fileMenu.add( createMenuItem(_I("menuFileCloseTab"), 
//                  KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, scMask),
//                  new FileAction(FileAction.CLOSE_TAB)));
         if(!(Env.getOS() == OS.MAC)){
            _fileMenu.addSeparator();
            _fileMenu.add( createMenuItem(_I("menuFileQuit"), 
                     null, new FileAction(FileAction.QUIT)));
         }
      }
   
     
   
   }
   
   void initScreenRecorder(){
      if (_screenRecorder == null){
         Rectangle window = new Rectangle();
         window.x = getLocation().x + 300;
         window.y = getLocation().y + 100;
         window.width = 640;
         window.height = 480;
         
         _screenRecorder = new ScreenRecorder();         
         _screenRecorder.setWindow(window);
         _screenRecorder.addListener(new ScreenRecorderListener(){

            @Override
            public void imageRecorded(Object source, BufferedImage recordedImage) {
               getDocument().addStep(_currentStepModel, recordedImage);
            }

            @Override
            public void windowHidden(Object source) {
               setMinimized(false);
               setAlwaysOnTop(false);
            }
            
         });
      }
   }
   
   
   StoryRunnerListener _storyRunnerListener = new StoryRunnerListener(){
      
      void switchBackToEditor(){
         _storyRunnerFrame.setAlwaysOnTop(false);

         Debug.info("[SklEditor] switched back");
         SklEditor.this.setVisible(true);
         SklEditor.this.repaint();
         // call a repaint later, on XP, this is necessary for the editor
         // to be re-painted properly. otherwise, the area occluded by
         // the player will not be redrawn immediately
         SwingUtilities.invokeLater(new Runnable(){

            @Override
            public void run() {
               SklEditor.this.repaint();      
            }
            
         });
      }

      @Override
      public void storyCompleted() {               
         Debug.info("[SklEditor] story completed");
         switchBackToEditor();
      }

      @Override
      public void storyStopped() {
         Debug.info("[SklEditor] story stopped");
         switchBackToEditor();
      }

      @Override
      public void storyFailed(int index) {
         switchBackToEditor();

         //if (doc == getDocument())
         //getDocument().selectStep(index);
      }
    
      @Override
      public void storyStarted() {
         Debug.info("[SklEditor] story started");
         SklEditor.this.setVisible(false);
      }
      
   };
   
   class RunAction extends MenuAction{
      static final String RUN_ALL = "runAll";
      static final String RUN_CURRENT = "runCurrent";
      static final String RUN_FROM_CURRENT = "runFromCurrent";
      
      public RunAction(){
         super();
      }

      public RunAction(String item) throws NoSuchMethodException{
         super(item);
      }

      private void runHelper(final SklDocument doc){
         runHelper(doc, 0);
      }
      
      private void runHelper(final SklDocument doc, int startIndex){
         SklEditor.this.setVisible(false);

         _storyRunnerFrame.setTitle("Sikuli Story Runner");
         _storyRunnerFrame.setSize(231,600);
         _storyRunnerFrame.setVisible(true);
         _storyRunnerFrame.setAlwaysOnTop(true);
         
         // try to position to the upper right corner of the screen
         Screen s = new Screen();
         int x = s.w - _storyRunnerFrame.getWidth() - 5;
         int y = 25;
         _storyRunnerFrame.setLocation(x,y);

         _storyRunner.setStory(doc);
         // TODO: fix this hack, somehow we need to purposely resize the frame
         // to make the size of the cells in the list updated
         _storyRunnerFrame.setSize(230,600);
         _storyRunner.runStory(startIndex);
         _storyRunner.addListener(_storyRunnerListener);
      }
      
      public void runCurrent(ActionEvent ae){
         
         SklDocument tempDoc = new SklDocument();
         tempDoc.addStep(getSelectedStep());

         runHelper(tempDoc);

      }

      public void runAll(ActionEvent ae){         

         runHelper(getDocument());

      }
      
      public void runFromCurrent(ActionEvent ae){
       
         runHelper(getDocument(), getDocument().getSelectedStepIndex());
         
      }
      
   }
   
   ScreenRecorder _screenRecorder;
   class InsertAction extends MenuAction {
      static final String INSERT_STEP = "insertStep";
      static final String INSERT_ANCHOR = "insertAnchor";      
      static final String INSERT_TEXT = "insertText";      
      
      public InsertAction(){
         super();
      }

      public InsertAction(String item) throws NoSuchMethodException{
         super(item);
      }

      public void insertText(ActionEvent ae){         
         _currentStepEditView.doInsert(SklTextModel.class);
      }
      
      public void insertAnchor(ActionEvent ae){         
         _currentStepEditView.doInsert(SklAnchorModel.class);
         
         //_currentStepModel.addRelationship(new SklSideRelationship());
      }
      
      public void insertStep(ActionEvent ae){
         setMinimized(true);
         setAlwaysOnTop(true);
         
         initScreenRecorder();
         _screenRecorder.setVisible(true);
      }
      
      
      public void insertStepOld(ActionEvent ae){
         
         Thread t = new Thread(){
            
            @Override 
            public void run(){
               //setVisible(false);
               setMinimized(true);
               setAlwaysOnTop(true);

               
               JFrame f = new JFrame("JFrame");
               f.setSize(0,0);
               f.setLocation(0,0);
               f.setUndecorated(true);
               f.setVisible(true);
               
               ScreenRecorderWindow w = 
                  new ScreenRecorderWindow(f);
               
               Rectangle p = SklEditor.this.getBounds();
               w.setBounds(new Rectangle(p.x + p.width + 10, p.y + 31, 640, 480));
               
               w.editor = SklEditor.this;
               
               w.startModal(1);
               //w.setVisible(false);
               
               setMinimized(false);
               setAlwaysOnTop(false);
               setVisible(true);
               
               f.setVisible(false);
               f.dispose();

            }
         };

         t.start();
         
      }

   }
   
   class EditAction extends MenuAction {
      
      static final String CLONE_SCREEN = "cloneScreen";
      public EditAction(String item) throws NoSuchMethodException{
         super(item);
      }
      
      public void cloneScreen(ActionEvent ae){
         SklStepModel step = getSelectedStep();
         getDocument().addStep(step, step.getReferenceImageModel().getImage());
      }

      
   }
   
   class FileAction extends MenuAction {
      
      static final String NEW = "doNew";
      static final String OPEN = "doLoad";
      static final String SAVE = "doSave";
      static final String SAVE_AS = "doSaveAs";
//      static final String EXPORT = "doExport";
//      static final String CLOSE_TAB = "doCloseTab";
//      static final String PREFERENCES = "doPreferences";
      static final String QUIT = "doQuit";
      
      public FileAction(String item) throws NoSuchMethodException{
         super(item);
      }
      
      public void doNew(ActionEvent ae){
         
      }
      
      public void doLoad(ActionEvent ae){
         File file = new FileChooser(SklEditor.this).load();
         if (file == null) 
            return;         

         SklDocument doc = SklDocument.load(file);
         
         
         

         if (getDocument().isEmpty()){
            
            setDocument(doc);
            setTitle(getTitleString(file.getAbsolutePath()));
            
         }else{

            SklEditor e = new SklEditor();
            e.setVisible(true);
            e.setTitle(getTitleString(file.getAbsolutePath()));
            
            Point o = getLocation();
            e.setLocation(o.x + 50, o.y + 50);
            
            e.setDocument(doc);
         }

         
      }
      
      String getBundlePathFromUser(){
         File file = new FileChooser(SklEditor.this).save();
         if (file == null) 
            return null;         
         
         String bundlePath = file.getAbsolutePath();            
         if( !bundlePath.endsWith(".sikuli"))
            bundlePath += ".sikuli";

         return bundlePath;
      }

      public void doSaveAs(ActionEvent ae){
         String bundlePath = getBundlePathFromUser();
         if (bundlePath == null)
            return;
         
         File destBundle = new File(bundlePath);
         getDocument().saveAs(destBundle);  
         setTitle(getTitleString(destBundle.getAbsolutePath()));
      }
      
      public void doSave(ActionEvent ae){
         
         if (getDocument().isUntitled()){
            doSaveAs(ae);
         }else{         
            getDocument().save();
         }
      }
      
      public void doQuit(ActionEvent ae){
         System.exit(0);
      }
      
      
   }
   
   
   private String getTitleString(String filename){
      return "Sikuli Story Builder - " + filename; 
   }
   
   
   public class NewAction extends CaptureAction {
      
      public NewAction(){
         super();
         putValue(NAME, "New");
      }
      
      @Override
      public void actionPerformed(ActionEvent evt) {

         SklEditor e = new SklEditor();
         e.setVisible(true);

         Point o = getLocation();
         e.setLocation(o.x + 50, o.y + 50);
         
      }
      
   }
   
   public class CaptureAction extends AbstractAction {
      
      public CaptureAction(){
         super("Capture");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         Thread t = new Thread(){
            
            @Override 
            public void run(){
               //setVisible(false);
               setMinimized(true);
               setAlwaysOnTop(true);

               
               JFrame f = new JFrame("JFrame");
               f.setSize(0,0);
               f.setLocation(0,0);
               f.setUndecorated(true);
               f.setVisible(true);
               
               ScreenRecorderWindow w = 
                  new ScreenRecorderWindow(f);
               
               Rectangle p = SklEditor.this.getBounds();
               w.setBounds(new Rectangle(p.x + p.width + 10, p.y + 31, 640, 480));
               
               w.editor = SklEditor.this;
               
               w.startModal();
               
               
               
//               for (RecordedClickEvent e : w.clickEvents){
//                  importStep(e);
//               }
               
//               if (w.clickEvents.size() > 0)
//                  _document.selectStep();
               
               //setVisible(true);
               setMinimized(false);
               setAlwaysOnTop(false);
               setVisible(true);
               
               f.setVisible(false);               
               f.dispose();

            }
         };

         t.start();
      }
   }
   

   
   public class PlayAction extends AbstractAction {
      
      public PlayAction(){
         super("Play");
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         
         
         Thread t = new Thread(){

            public void run(){
               setVisible(false);
               SikuliGuide g = new SikuliGuide();                   
               g.playStep(_currentStepModel);    
               
               try {
                  Thread.sleep(1000);
               } catch (InterruptedException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
               }
               
               setVisible(true);
            }
         };
         
         t.start();
         
         
      }
    }
   
   
   SklStoryRunner _storyRunner = new SklStoryRunner();      
   class StoryRunnerFrame  extends JFrame {
      
      StoryRunnerFrame(SklStoryRunner storyRunner){
         //super(parent);
//         setSize(250,600);
//         setLocationRelativeTo(null);
//         setVisible(true);
//         setAlwaysOnTop(true);
//         
         //final JFrame storyRunnerFrame = new JFrame();
         setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
         
         Container content = getContentPane();
         content.add(storyRunner, BorderLayout.CENTER);
         
         
         
      }      
   }
   
   StoryRunnerFrame _storyRunnerFrame = new StoryRunnerFrame(_storyRunner);
   
   public class PlayAllAction extends AbstractAction {
      
      public PlayAllAction(){
         super("Play All");
      }

      @Override
      public void actionPerformed(ActionEvent e) {
//
//         SklEditor.this.setVisible(false);
//
//         _storyRunnerFrame.setTitle("Sikuli Story Runner");
//         _storyRunnerFrame.setSize(231,600);
//         _storyRunnerFrame.setVisible(true);
//         _storyRunnerFrame.setAlwaysOnTop(true);
//         
//         // try to position to the upper right corner of the screen
//         Screen s = new Screen();
//         int x = s.w - _storyRunnerFrame.getWidth() - 5;
//         int y = 25;
//         _storyRunnerFrame.setLocation(x,y);
//
//         _storyRunner.setDocument(getDocument());
//         // TODO: fix this hack, somehow we need to purposely resize the frame
//         // to make the size of the cells in the list updated
//         _storyRunnerFrame.setSize(230,600);
//         _storyRunner.run();
//         _storyRunner.addListener(new StoryRunnerListener(){
//
//            @Override
//            public void storyCompleted() {
//               SklEditor.this.setVisible(true);
//               SklEditor.this.repaint();
//               _storyRunnerFrame.setAlwaysOnTop(false);
//            }
//
//            @Override
//            public void storyStopped() {
//               SklEditor.this.setVisible(true);
//               SklEditor.this.repaint();
//               _storyRunnerFrame.setAlwaysOnTop(false);
//            }
//
//            @Override
//            public void storyFailed(int index) {
//               SklEditor.this.setVisible(true);
//               SklEditor.this.repaint();
//
//               getDocument().selectStep(index);
//               _storyRunnerFrame.setAlwaysOnTop(false);
//            }
//
//
//            @Override
//            public void storyStarted() {
//               SklEditor.this.setVisible(false);
//            }
//            
//         });

      }
    }
   
   
   public void cloneCurrentStep(){
         
      SklStepModel step = getSelectedStep();      
      try {
         SklStepModel clone = (SklStepModel) step.clone();
         getDocument().addStep(step, clone);
      } catch (CloneNotSupportedException e) {
      }

   }
   
   public void cloneCurrentReferenceImage(){      
      SklStepModel step = getSelectedStep();
      getDocument().addStep(step, step.getReferenceImageModel().getImage());
   }
   
   
   public void importStep(RecordedClickEvent event){

      SklStepModel importedStepModel = new SklStepModel();
      
      
      Point clickLocation = event.getClickLocation();
      if (clickLocation != null){
         Rectangle defaultAnchorBounds = new Rectangle(50,50);
         // center the default anchor at the click location
         defaultAnchorBounds.x = clickLocation.x - defaultAnchorBounds.width/2;
         defaultAnchorBounds.y = clickLocation.y - defaultAnchorBounds.height/2;
         
         SklAnchorModel anchor = new SklAnchorModel(defaultAnchorBounds);      
         importedStepModel.addModel(anchor);
      }
      
      SklImageModel imageModel = new SklImageModel();
      imageModel.setImage(event.getScreenImage());       
      importedStepModel.setReferenceImageModel(imageModel);
      
//         // TODO chooses the best location automatically (that does not go outside of display bounds) 
//         SikuliGuideText txt = (SikuliGuideText) 
//            performAddTextAction(new Point(defaultAnchorBounds.x+50,defaultAnchorBounds.y-20));
//         txt.setText("Click");     
//         currentStepContentChanged();
//      }
//
      int index = _document._steps.indexOf(_currentStepModel);
      _document.addStep(index+1,importedStepModel);
      _document.selectStep(index+1);
   }
   
   public void setMinimized(boolean minimized){
      if (minimized){         
         setSize(250,600);         
      } else{        
         setSize(1000,600);
      }
   }
   
   //
   // http://stackoverflow.com/questions/309023/howto-bring-a-java-window-to-the-front
   //
   @Override
   public void setVisible(final boolean visible) {
     // make sure that frame is marked as not disposed if it is asked to be visible
     if (visible) {
         //setDisposed(false);
     }
     // let's handle visibility...
     if (!visible || !isVisible()) { // have to check this condition simply because super.setVisible(true) invokes toFront if frame was already visible
         super.setVisible(visible);
     }
     // ...and bring frame to the front.. in a strange and weird way
     if (visible) {
         int state = super.getExtendedState();
         state &= ~JFrame.ICONIFIED;
         super.setExtendedState(state);
         super.setAlwaysOnTop(true);
         super.toFront();
         super.requestFocus();
         super.setAlwaysOnTop(false);
     }
   }

   @Override
   public void toFront() {
     super.setVisible(true);
     int state = super.getExtendedState();
     state &= ~JFrame.ICONIFIED;
     super.setExtendedState(state);
     super.setAlwaysOnTop(true);
     super.toFront();
     super.requestFocus();
     super.setAlwaysOnTop(false);
   }
   
   public static void main(String[] args){
      SklEditor ew = new SklEditor();    
      ew.setVisible(true);
   }

   public void setDocument(SklDocument document) {
      _document = document;
      _documentListView.setDocument(document);
      
      // this allows the editing pane to show the selected step in the list
      _document.getSelectionModel().addListSelectionListener(new ListSelectionListener(){

         @Override
         public void valueChanged(ListSelectionEvent e) {
                           
            if (e.getValueIsAdjusting()  == false){
               int index = ((ListSelectionModel) e.getSource()).getLeadSelectionIndex();
               _currentStepModel = (SklStepModel) getDocument().getStep(index);
               _currentStepEditView.setModel(_currentStepModel);
               _currentStepEditView.validate();
               _currentStepEditView.repaint();
               validate();
            }
         }
         
      });
      
      
      if (_document.getSteps().size()>0)
         _document.selectStep(0);   

      repaint();
      validate();            
   }


   public SklDocument getDocument() {
      return _document;
   }
   
   public SklStepModel getSelectedStep(){      
      ListSelectionModel selection = _document.getSelectionModel();
      int index = selection.getLeadSelectionIndex();
      return _document.getStep(index);
   }

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


@Root
class SklDocument {
   
   @ElementList
   ArrayList<SklStepModel> _steps = new ArrayList<SklStepModel>();
     
   private ListSelectionModel _selection = new DefaultListSelectionModel();

   SklDocument(){
   }
   
   
   public int getSelectedStepIndex(){      
      ListSelectionModel selection = getSelectionModel();
      return selection.getLeadSelectionIndex();
   }

   
   public boolean isEmpty() {
      return _steps.isEmpty();
   }

   ListModelAdapter _listModelAdapter = new ListModelAdapter();
   ListModel getListModel(){
      return _listModelAdapter;
   }
   
   class ListModelAdapter implements ListModel {
   
      ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();
      @Override
      public void addListDataListener(ListDataListener l) {
         listeners.add(l);
      }

      @Override
      public Object getElementAt(int index) {
         return _steps.get(index);
      }

      @Override
      public int getSize() {      
         return _steps.size();
      }

      @Override
      public void removeListDataListener(ListDataListener l) {
         listeners.remove(l);      
      }
      
      void notifyListenersContentChanged() {
         // no attempt at optimziation
         ListDataEvent le = new ListDataEvent(this,
             ListDataEvent.CONTENTS_CHANGED, 0, getSize());
         for (int i = 0; i < listeners.size(); i++) {
           ((ListDataListener) listeners.get(i)).contentsChanged(le);
         }
       }

      void notifyListenersInternalAdded(int index0, int index1) {
         // no attempt at optimziation
         ListDataEvent le = new ListDataEvent(this,
             ListDataEvent.INTERVAL_ADDED, index0, index1);
         for (int i = 0; i < listeners.size(); i++) {
           ((ListDataListener) listeners.get(i)).intervalAdded(le);
         }
       }

   
   }
   
   ArrayList<SklStepModel> getSteps(){
      return _steps;
   }
   

    // REMAINDER ARE OVERRIDES JUST TO CALL NOTIFYLISTENERS

//    public boolean add(Object o) {
//      boolean b = super.add(o);
//      if (b)
//        notifyListeners();
//      return b;
//    }
//
   
   
   
   
   public void addStep(SklStepModel previousStep, BufferedImage referenceImage){
      
      SklStepModel newStepModel = new SklStepModel();
      SklImageModel imageModel = new SklImageModel();
      imageModel.setImage(referenceImage);
      newStepModel.setReferenceImageModel(imageModel);

      addStep(previousStep, newStepModel);
   }
   
   public void addStep(SklStepModel previousStep, SklStepModel newStepModel){
      int index = _steps.indexOf(previousStep);
      addStep(index+1, newStepModel);
      selectStep(index+1);
   }
   
    public void addStep(int index, SklStepModel stepModel) {
      _steps.add(index, stepModel);
      _listModelAdapter.notifyListenersContentChanged();
      _listModelAdapter.notifyListenersInternalAdded(index, index);
    }
    
    public boolean addStep(SklStepModel stepModel) {
       boolean b = _steps.add(stepModel);
       if (b){
          _listModelAdapter.notifyListenersContentChanged();        
          _listModelAdapter.notifyListenersInternalAdded(_steps.size()-1, _steps.size()-1);
       }
       return b;
     }
    
    public SklStepModel removeStep(int i) {
       SklStepModel o = _steps.remove(i);
       if (o != null){
          // TODO: notify removal event
          _listModelAdapter.notifyListenersContentChanged();          
          
          // automatically select the step next to the one removed, or the one before if the removed
          // step is the last step
          int j = Math.min(i, _steps.size() - 1);
          selectStep(j);
       }
       return o;
    }

    
    public void selectStep(int index){
       _selection.setSelectionInterval(index,index);
    }

    
    public SklStepModel getStep(int index) {
       return _steps.get(index);
     }


   public void setSelection(ListSelectionModel selection) {
      _selection = selection;
   }

   public ListSelectionModel getSelectionModel() {
      return _selection;
   }
   
   String _bundlePath = null;
   //String bundlePath;
   
   boolean isUntitled(){
      return _bundlePath == null;
   }
   
   public void clear(){
      _steps.clear();
   }
   
   private void saveReferenceImagesToBundle(File bundle){
      for (SklStepModel stepModel : getSteps()){          
         
            SklImageModel imageModel = stepModel.getReferenceImageModel();
            if (imageModel.getImageUrl() == null){
               String filename = SaveLoadHelper.getTimestamp() + ".png";
               imageModel.setImageUrl(filename);
               Debug.info("saving as " + filename);
            }
                        
            String targetImagePath =   bundle.getAbsolutePath() + File.separator + imageModel.getImageUrl();
            if (!new File(targetImagePath).exists()){                       
               SaveLoadHelper.saveImage(imageModel.getImage(), imageModel.getImageUrl(), bundle.getAbsolutePath());
            }
         
      }      
   }
   
   
   
   
   
   

   
   

   
   class ScriptGenerator {
      
      String generateScript(SklDocument doc, File bundle){
         String s = "";
         
         for (SklStepModel stepModel : doc.getSteps()  ){
            //Enumeration<?> e = stepListModel.elements() ; e.hasMoreElements() ; ){
//         }
//            SklStepModel stepModel = (SklStepModel) e.nextElement();
//            
            String stepScript = generateStep(stepModel,bundle);
            
            s += stepScript;
            s += "wait(3) \n";
            //s += "click(\"" + targetImageFile.getName() + "\") \n";            
         }
         
         // TODO: crop images
         
         return s;
      }
      
      
      private String generateStep(SklStepModel stepModel, File bundlePathFile){
         
         String s = "";
         
         // compile, resolve command for each anchor based on associated text
         stepModel.compile();
         
         // for each anchor, but we assume there's only one anchor for automation mode
         for (SklModel model : stepModel.getModels()){
            
            if (model instanceof SklAnchorModel ){
               
               
               String referenceImageFilename = stepModel.getReferenceImageModel().getImageUrl();
               // TODO: fix this. sometimes different targets may refer to the same reference image
               String targetImageFilename = "target" + referenceImageFilename;
               
               BufferedImage croppedTargetImage;
               BufferedImage referenceImage = stepModel.getReferenceImageModel().getImage();
               
               // TODO: check boundary               
               croppedTargetImage = referenceImage.getSubimage(model.getX(), model.getY(), model.getWidth(), model.getHeight());
               
               File croppedTargetImageFile = new File(bundlePathFile, targetImageFilename);               
               
               try {
                  ImageIO.write(croppedTargetImage, "png", croppedTargetImageFile);

               } catch (IOException e) {
                  e.printStackTrace();
               }
               
               if (((SklAnchorModel) model).getCommand() == SklAnchorModel.CLICK_COMMAND){
                  s += "click(\"" + croppedTargetImageFile.getName() + "\") \n";
               }else if (((SklAnchorModel) model).getCommand() == SklAnchorModel.ASSERT_COMMAND){
                  s += "find(\"" + croppedTargetImageFile.getName() + "\") \n";
               }else if (((SklAnchorModel) model).getCommand() == SklAnchorModel.TYPE_COMMAND){
                  s += "type(\"" + croppedTargetImageFile.getName() + "\",\"" + ((SklAnchorModel) model).getArgument() + "\") \n";                  
               }
                              
            }
            
         }
         
         return s;
         
      }
      
   }
   
   //boolean _isNew = true;
   
   void save(){
      saveAs(new File(_bundlePath));
   }
   
   void saveAs(File destBundle){
      if( !destBundle.exists() )
         destBundle.mkdir();
      
      saveReferenceImagesToBundle(destBundle);
      
      Strategy strategy = new CycleStrategy("id","ref");
      Serializer serializer = new Persister(strategy);
     
      
      File fout = SaveLoadHelper.getXMLFileFromBundle(destBundle);         
      try {
            serializer.write(this, fout);
      } catch (Exception e1) {
         e1.printStackTrace();
      }
            
     
      String scriptString = (new ScriptGenerator()).generateScript(this, destBundle);
      Debug.info(""  + scriptString);
      
      File pyfile =  SaveLoadHelper.getPYFileFromBundle(destBundle);           
      FileWriter writer;
      try {
         writer = new FileWriter(pyfile);
         PrintWriter out = new PrintWriter(writer);            
         out.print(scriptString);
         out.close();
      } catch (IOException e) {
      }
      
   }
   
   static SklDocument load(File bundlefile) {
      
      Strategy strategy = new CycleStrategy("id","ref");
      Serializer serializer = new Persister(strategy);
      
     
      File fin = SaveLoadHelper.getXMLFileFromBundle(bundlefile);         
      SklDocument doc = null;
      
      try {
         doc = serializer.read(SklDocument.class, fin);
         
         for (SklStepModel stepModel : doc.getSteps()){
            stepModel.getReferenceImageModel().bundlePath = bundlefile.getAbsolutePath();
         }
         
         doc._bundlePath = bundlefile.getAbsolutePath();

         return doc;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }      
   }

//
//    public boolean addAll(Collection o) {
//      boolean b = super.add(o);
//      if (b)
//        notifyListeners();
//      return b;
//    }
//
//    public void clear() {
//      super.clear();
//      notifyListeners();
//    }
//
//    public Object remove(int i) {
//      Object o = super.remove(i);
//      notifyListeners();
//      return o;
//    }
//
//    public boolean remove(Object o) {
//      boolean b = super.remove(o);
//      if (b)
//        notifyListeners();
//      return b;
//    }
//
//    public Object set(int index, Object element) {
//      Object o = super.set(index, element);
//      notifyListeners();
//      return o;
//    }
}

class SaveLoadHelper{
   
   static String getAltFilename(String filename){
      int pDot = filename.lastIndexOf('.');
      int pDash = filename.lastIndexOf('-');
      int ver = 1;
      String postfix = filename.substring(pDot);
      String name;
      if(pDash >= 0){
         name = filename.substring(0, pDash);
         ver = Integer.parseInt(filename.substring(pDash+1, pDot));
         ver++;
      }
      else
         name = filename.substring(0, pDot);
      return name + "-" + ver + postfix;
   }
   
   static String saveImage(BufferedImage img, String filename, String bundlePath){
      final int MAX_ALT_NUM = 100;
      String fullpath = bundlePath;
      File path = new File(fullpath);
      if( !path.exists() ) path.mkdir();
      if(!filename.endsWith(".png"))
         filename += ".png";
      File f = new File(path, filename);
      int count = 0;
      while( f.exists() && count < MAX_ALT_NUM){
         Debug.log( f.getName() + " exists");
         f = new File(path, getAltFilename(f.getName()));
         count++;
      }
      if(count >= MAX_ALT_NUM)
         f = new File(path, getTimestamp() + ".png");
      fullpath = f.getAbsolutePath();
      fullpath = fullpath.replaceAll("\\\\","/");
      try{
         ImageIO.write(img, "png", new File(fullpath));
      }
      catch(IOException e){
         e.printStackTrace();
         return null;
      }
      return fullpath;
   }
   
   static File getXMLFileFromBundle(File bundlefile){
      String bundlePath = bundlefile.getAbsolutePath();
      if( !bundlefile.getAbsolutePath().endsWith(".sikuli") )
         bundlePath += ".sikuli";
      String filename = bundlePath + File.separator + "wysiwyg.xml";
      return new File(filename);
   }

   static String getTimestamp(){
      return (new Date()).getTime() + "";
   }
   
   // /to/path/foo.sikuli -> /to/path/foo.sikuli/foo.py
   static File getPYFileFromBundle(File bundlefile){
      String bundlePath = bundlefile.getAbsolutePath();
      if( bundlePath.endsWith(".sikuli") || 
            bundlePath.endsWith(".sikuli" + "/") ){
         File f = new File(bundlePath);
         String dest = f.getName();
         dest = dest.replace(".sikuli", ".py");
         return new File(bundlePath + File.separator + dest);
      }else{
         return null;
      }
   }
}
