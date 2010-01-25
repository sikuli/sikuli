package edu.mit.csail.uid.sikuli_test;

import edu.mit.csail.uid.SikuliIDE;
import java.util.Vector;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestFailure;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.runner.BaseTestRunner;
import junit.runner.FailureDetailView;
import junit.runner.SimpleTestCollector;
import junit.runner.TestCollector;
import junit.runner.TestRunListener;
import junit.runner.StandardTestSuiteLoader;
import junit.runner.TestSuiteLoader;

import edu.mit.csail.uid.Debug;
import java.io.PrintStream;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.python.util.PythonInterpreter; 
import org.python.core.*; 

public class UnitTestRunner extends BaseTestRunner implements TestRunContext{
   private static final int GAP= 4;
   private JFrame minFrame;
   private JPanel mainPane, minPane;
   private JToolBar toolbar;
   private JButton fRun;
   private ProgressBar fProgressIndicator;
   private CounterPanel fCounterPanel;
   private JTabbedPane fTestViewTab;
   private FailureDetailView fFailureView;
   private JScrollPane tracePane;
   private Vector fTestRunViews= new Vector(); // view associated with tab in tabbed pane

   private DefaultListModel fFailures;

   private Thread fRunner;
   private TestResult fTestResult;

   //private HashMap<String, Integer> _lineNoOfTest;


   public JComponent getTracePane(){  return tracePane; }

   public JPanel getPanel(){
      return mainPane;
   }

   public JFrame getMinFrame(){
      return minFrame;
   }

   private JToolBar initToolbar(){
      JToolBar toolbar = new JToolBar();
      toolbar.setFloatable(false);
      fRun = createRunButton();
      toolbar.add(fRun);
      return toolbar;
   }

   protected JButton createRunButton() {
      JButton run= new JButton("Run");
      run.setEnabled(true);
      run.addActionListener(
         new ActionListener() {
            public void actionPerformed(ActionEvent e) {
               runSuite();
            }
         }
      );
      return run;
   }
   
   protected CounterPanel createCounterPanel() {
      return new CounterPanel();
   }

   protected JTabbedPane createTestRunViews() {
      JTabbedPane pane= new JTabbedPane();

      FailureRunView lv= new FailureRunView(this);
      fTestRunViews.addElement(lv);
      lv.addTab(pane);

      TestHierarchyRunView tv= new TestHierarchyRunView(this);
      fTestRunViews.addElement(tv);
      tv.addTab(pane);

      pane.addChangeListener(
            new ChangeListener() {
               public void stateChanged(ChangeEvent e) {
                  testViewChanged();
               }
            }
            );
      return pane;
   }

   public void testViewChanged() {
      TestRunView view= (TestRunView)fTestRunViews.elementAt(fTestViewTab.getSelectedIndex());
      view.activate();
   }

   private void initComponents(){
      toolbar = initToolbar();
      /*
      if (inMac()) 
         fProgressIndicator= new MacProgressBar(fStatusLine); 
      else                                                            
      */
      fProgressIndicator= new ProgressBar();
      fCounterPanel= createCounterPanel();
      fFailures= new DefaultListModel();
      fTestViewTab= createTestRunViews();
      fFailureView= createFailureDetailView();
      tracePane= new JScrollPane(fFailureView.getComponent(), ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
      initMinFrame();
   }

   private void initMinFrame(){
      minFrame = new JFrame("Sikuli Test");
      minFrame.setAlwaysOnTop(true);
      minFrame.setSize(255, 85);
      minFrame.getRootPane().putClientProperty("Window.alpha", new Float(0.7f));
      /*
      Container con = minFrame.getContentPane();
      con.add(minPane);
      minFrame.doLayout();
      minFrame.setVisible(true);
      */
   }
   
   private static final String FAILUREDETAILVIEW_KEY= "FailureViewClass";
   protected FailureDetailView createFailureDetailView() {
      String className= BaseTestRunner.getPreference(FAILUREDETAILVIEW_KEY);
      if (className != null) {
         Class viewClass= null;
         try {
            viewClass= Class.forName(className);
            return (FailureDetailView)viewClass.newInstance();
         } catch(Exception e) {
            JOptionPane.showMessageDialog(mainPane, "Could not create Failure DetailView - using default view");
         }
      }
      return new DefaultFailureDetailView();
   }


   public UnitTestRunner(){
      mainPane = new JPanel(new GridBagLayout());
      minPane = new JPanel(new GridBagLayout());

      initComponents();

      addGrid(mainPane, toolbar, 		0, 0, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);

      addGrid(mainPane, new JSeparator(), 	0, 1, 2, GridBagConstraints.HORIZONTAL, 1.0, GridBagConstraints.WEST);


      addGrid(mainPane, fCounterPanel,	 0, 2, 2, GridBagConstraints.NONE, 			0.0, GridBagConstraints.WEST);
      addGrid(mainPane, fProgressIndicator, 	0, 3, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);

      addGrid(mainPane, new JSeparator(), 	0, 5, 2, GridBagConstraints.HORIZONTAL, 1.0, GridBagConstraints.WEST);
      addGrid(mainPane, new JLabel("Results:"),	0, 6, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);

      addGrid(mainPane, fTestViewTab, 	 0, 7, 2, GridBagConstraints.BOTH, 			1.0, GridBagConstraints.WEST);



      //_lineNoOfTest = new HashMap<String, Integer>();
   }

   void addMinComponentsToPane(JPanel pane){
      addGrid(pane, toolbar, 		0, 0, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);

      addGrid(pane, fCounterPanel,	 0, 2, 2, GridBagConstraints.NONE, 			0.0, GridBagConstraints.WEST);
      addGrid(pane, fProgressIndicator, 	0, 3, 2, GridBagConstraints.HORIZONTAL, 	1.0, GridBagConstraints.WEST);
   }

   private void addGrid(JPanel p, Component co, int x, int y, int w, 
                        int fill, double wx, int anchor) {
      GridBagConstraints c= new GridBagConstraints();
      c.gridx= x; c.gridy= y;
      c.gridwidth= w;
      c.anchor= anchor;
      c.weightx= wx;
      c.fill= fill;
      if(fill == GridBagConstraints.BOTH || fill == GridBagConstraints.VERTICAL)
         c.weighty= 1.0;
      c.insets= new Insets(y == 0 ? 10 : 0,  GAP, GAP, GAP); 
      p.add(co, c);
   }


   public ListModel getFailures() {
      return fFailures;
   }

   public void handleTestSelected(Test test){
      moveCursorToTest(test);
      showFailureDetail(test);
   }

   private void moveCursorToTest(Test test){
      if( test instanceof TestCase ){
         String func = ((TestCase)test).getName();
         try{
            SikuliIDE.getInstance().jumpTo(func);
         }
         catch(BadLocationException e){
            e.printStackTrace();
         }
      }
   }

   private void showFailureDetail(Test test) {
      if (test != null) {
         ListModel failures= getFailures();
         for (int i= 0; i < failures.getSize(); i++) {
            TestFailure failure= (TestFailure)failures.getElementAt(i);
            if (failure.failedTest() == test) {
               fFailureView.showFailure(failure);
               return;
            }
         }
      }
      fFailureView.clear();
   }

   synchronized public void runSuite() {
      SikuliIDE ide = SikuliIDE.getInstance();
      if (fRunner != null) {
         fTestResult.stop();
         showIDE(true);
      } else {
         try{
            showIDE(false);
            reset();
            String filename = ide.getCurrentFilename();
            String path = ide.getCurrentBundlePath();
            Test suite = genTestSuite(filename, path);
            doRun(suite);
         }
         catch(IOException e){
            e.printStackTrace();
            showIDE(true);
         }
      }
   }


   private void showInfo(String message) {
      //fStatusLine.showInfo(message);
      System.out.println(message);
   }

   private void postInfo(final String message) {
      SwingUtilities.invokeLater(
            new Runnable() {
               public void run() {
                  showInfo(message);
               }
            }
      );
   }

   protected void reset() {
      fCounterPanel.reset();
      fProgressIndicator.reset();
      fFailureView.clear();
      fFailures.clear();
   }

   private void setButtonLabel(final JButton button, final String label) {
      SwingUtilities.invokeLater(
            new Runnable() {
               public void run() {
                  button.setText(label);
               }
            }
     );
   }

   protected void runFinished(final Test testSuite) {
      SwingUtilities.invokeLater(
            new Runnable() {
               public void run() {
                  for (Enumeration e= fTestRunViews.elements(); e.hasMoreElements(); ) {
                     TestRunView v= (TestRunView) e.nextElement();
                     v.runFinished(testSuite, fTestResult);
                  }
               }
            }
     );
   }





   public static final int SUCCESS_EXIT= 0;
   public static final int FAILURE_EXIT= 1;
   public static final int EXCEPTION_EXIT= 2;

   public TestSuiteLoader getLoader() {
      return new StandardTestSuiteLoader();
   }

   private void revealFailure(Test test) {
      for (Enumeration e= fTestRunViews.elements(); e.hasMoreElements(); ) {
         TestRunView v= (TestRunView) e.nextElement();
         v.revealFailure(test);
      }
   }

   private void appendFailure(Test test, Throwable t) {
      fFailures.addElement(new TestFailure(test, t));
      if (fFailures.size() == 1)
         revealFailure(test);
   }

   public void testFailed(final int status, final Test test, final Throwable t) {
      SwingUtilities.invokeLater(
            new Runnable() {
               public void run() {
                  switch (status) {
                     case TestRunListener.STATUS_ERROR:
                        fCounterPanel.setErrorValue(fTestResult.errorCount());
                        appendFailure(test, t);
                        break;
                     case TestRunListener.STATUS_FAILURE:
                        fCounterPanel.setFailureValue(fTestResult.failureCount());
                        appendFailure(test, t);
                        break;
                  }
               }
            }
            );
   }

   public void testStarted(String testName) {
      Debug.log(8,"test started: " + testName);
   }

   public void testEnded(String testName) {
      Debug.log(8,"test ended: " + testName);
      synchUI();
      SwingUtilities.invokeLater(
         new Runnable() {
            public void run() {
               if (fTestResult != null) {
                  fCounterPanel.setRunValue(fTestResult.runCount());
                  fProgressIndicator.step(fTestResult.runCount(), fTestResult.wasSuccessful());
               }
            }
         }
     );
   }

   private void synchUI() {
      try {
         SwingUtilities.invokeAndWait(
            new Runnable() {
               public void run() {}
            }
         );
      }
      catch (Exception e) {
      }
   }

   protected TestResult createTestResult() {
      return new TestResult();
   }

   private void doRun(final Test testSuite) {
      setButtonLabel(fRun, "Stop");
      fRunner= new Thread("TestRunner-Thread") {
         public void run() {
            UnitTestRunner.this.start(testSuite);
            postInfo("Running...");

            long startTime= System.currentTimeMillis();
            testSuite.run(fTestResult);

            if (fTestResult.shouldStop()) {
               postInfo("Stopped");
            } else {
               long endTime= System.currentTimeMillis();
               long runTime= endTime-startTime;
               postInfo("Finished: " + elapsedTimeAsString(runTime) + " seconds");
            }
            runFinished(testSuite);
            setButtonLabel(fRun, "Run");
            showIDE(true);
            fRunner= null;
            System.gc();
         }
      };
      // make sure that the test result is created before we start the
      // test runner thread so that listeners can register for it.
      fTestResult= createTestResult();
      fTestResult.addListener(UnitTestRunner.this);
      aboutToStart(testSuite);

      fRunner.start();
   }

   private void showIDE(boolean show){
      SikuliIDE.getInstance().setVisible(show);
      if(show){
         addMinComponentsToPane(mainPane);
      }
      else{
         addMinComponentsToPane(minPane);
         Container con = minFrame.getContentPane();
         con.add(minPane);
         minFrame.doLayout();
      }
      minFrame.setVisible(!show);
   }

   protected void aboutToStart(final Test testSuite) {
      for (Enumeration e= fTestRunViews.elements(); e.hasMoreElements(); ) {
         TestRunView v= (TestRunView) e.nextElement();
         v.aboutToStart(testSuite, fTestResult);
      }
   }


   private void start(final Test test) {
      SwingUtilities.invokeLater(
            new Runnable() {
               public void run() {
                  int total= test.countTestCases();
                  fProgressIndicator.start(total);
                  fCounterPanel.setTotal(total);
               }
            }
      );
   }


   private String genTestClassName(String filename){
      String fname = new File(filename).getName();
      int dot = fname.indexOf(".");
      return fname.substring(0, dot);
   }


   private Test genTestSuite(String filename, String bundlePath) throws IOException{
      String className = genTestClassName(filename);
      TestSuite ret = new TestSuite(className);
      PythonInterpreter interp = new PythonInterpreter();
      String testCode = 
         "import junit\n"+
         "from junit.framework.Assert import *\n"+
         "from python.edu.mit.csail.uid.Sikuli import *\n"+
         "from python.edu.mit.csail.uid.SikuliTest import *\n"+
         "class "+className+" (junit.framework.TestCase):\n"+
         "  def __init__(self, name):\n"+
         "    junit.framework.TestCase.__init__(self,name)\n"+
         "    self.theTestFunction = getattr(self,name)\n"+
         "    setBundlePath('"+bundlePath+"')\n"+
         "  def runTest(self):\n"+
         "    self.theTestFunction()\n";

      BufferedReader in = new BufferedReader(new FileReader(filename));
      String line;
      //int lineNo = 0;
      //Pattern patDef = Pattern.compile("def\\s+(\\w+)\\s*\\(");
      while( (line = in.readLine()) != null ){
        // lineNo++;
         testCode += "  " + line + "\n";
         /*
         Matcher matcher = patDef.matcher(line);
         if(matcher.find()){
            String func = matcher.group(1);
            Debug.log("Parsed " + lineNo + ": " + func);
            _lineNoOfTest.put( func, lineNo );
         }
         */
      }
      interp.exec(testCode);
      PyList tests = (PyList)interp.eval(
            "["+className+"(f) for f in dir("+className+") if f.startswith(\"test\")]");
      while( tests.size() > 0 ){
         PyObject t = tests.pop();
         Test t2 = (Test)(t).__tojava__(TestCase.class);
         ret.addTest( t2 );
      }

      return ret;
   }



   protected void runFailed(String message) {
      System.err.println(message);
      fRunner= null;
   }


}
