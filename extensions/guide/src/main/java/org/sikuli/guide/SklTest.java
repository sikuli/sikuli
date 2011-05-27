package org.sikuli.guide;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoManager;

import org.junit.Test;
import org.sikuli.guide.EditorWindow.LocationSelector;
import org.sikuli.guide.EditorWindow.RelatedAnchorVisulization;
import org.sikuli.guide.Overview.OverviewListener;
import org.sikuli.guide.SikuliGuideComponent.Layout;
import org.sikuli.guide.SklSideRelationship.Side;
import org.sikuli.guide.SklStepPreview.AddComponentAction;
import org.sikuli.script.Debug;
import org.sikuli.script.Region;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;


public class SklTest {

   
   SklStepModel createStepModel(){
      SklObjectModel c = new DefaultSklObjectModel();
      c.setX(0);
      c.setY(0);
      c.setWidth(100);
      c.setHeight(100);
      
      SklAnchorModel ak = new SklAnchorModel();
      ak.setX(400);
      ak.setY(10);
      ak.setWidth(200);
      ak.setHeight(100);
      
      SklTextModel t = new DefaultSklTextModel();
      t.setWidth(50);
      t.setHeight(50);
      t.setText("Text");
      t.setHasShadow(true);
      
      SklTextModel bt = new DefaultSklTextModel();
      bt.setText("Bottom Text");
      bt.setHasShadow(true);

      SklTextModel rt = new DefaultSklTextModel();
      rt.setText("Right Text");
      rt.setHasShadow(true);

      SklImageModel img = new SklImageModel("play.png");

      SklStepModel step = new SklStepModel();
      step.addModel(c); 
      step.addModel(bt);
      step.addModel(rt);
      step.addModel(ak);
      step.addModel(img);
      
      step.addRelationship(new SklOffsetRelationship(c, t, 200,0));
      step.addRelationship(new SklSideRelationship(c, bt, Side.BOTTOM));
      step.addRelationship(new SklSideRelationship(c, rt, Side.RIGHT));
      step.addRelationship(new SklSideRelationship(ak, img, Side.LEFT));

      
      step.addAnimation(SklAnimationFactory.createMoveToAnimation(c, new Point(200,200)));
      step.addAnimation(SklAnimationFactory.createResizeToAnimation(c, new Dimension(200,200)));
      step.addAnimation(SklAnimationFactory.createMoveToAnimation(t, new Point(200,200)));

      return step;
   }
   
   @Test
   public void testObjectModel() throws Exception{
      
      SklObjectModel c = new DefaultSklObjectModel();
      c.setX(0);
      c.setY(0);
      c.setWidth(100);
      c.setHeight(100);
      
      SklAnchorModel ak = new SklAnchorModel();
      ak.setX(400);
      ak.setY(10);
      ak.setWidth(200);
      ak.setHeight(100);
      
      SklTextModel t = new DefaultSklTextModel();
      t.setWidth(50);
      t.setHeight(50);
      t.setText("Text");
      t.setHasShadow(true);
      
      SklTextModel bt = new DefaultSklTextModel();
      bt.setText("Bottom Text");
      bt.setHasShadow(true);

      SklTextModel rt = new DefaultSklTextModel();
      rt.setText("Right Text");
      rt.setHasShadow(true);

      SklImageModel img = new SklImageModel("play.png");

      SklStepModel step = new SklStepModel();
      step.addModel(c); 
      step.addModel(bt);
      step.addModel(rt);
      step.addModel(ak);
      step.addModel(img);
      
      step.addRelationship(new SklOffsetRelationship(c, t, 200,0));
      step.addRelationship(new SklSideRelationship(c, bt, Side.BOTTOM));
      step.addRelationship(new SklSideRelationship(c, rt, Side.RIGHT));
      step.addRelationship(new SklSideRelationship(ak, img, Side.LEFT));

      
      step.addAnimation(SklAnimationFactory.createMoveToAnimation(c, new Point(200,200)));
      step.addAnimation(SklAnimationFactory.createResizeToAnimation(c, new Dimension(200,200)));
      step.addAnimation(SklAnimationFactory.createMoveToAnimation(t, new Point(200,200)));

      SklStepSimpleViewer viewer = new SklStepSimpleViewer(step);
      helperShowInFrame(viewer);
   }
   
   
   SklStepModel createStep1(){
      SklAnchorModel anchor = new SklAnchorModel();
      anchor.setX(90);
      anchor.setY(145);
      anchor.setWidth(50);
      anchor.setHeight(50);
      
      //anchor.setPattern(new SklPatternModel());

      SklTextModel text1 = new DefaultSklTextModel("Step 1");
      text1.setHasShadow(true);

      SklStepModel step = new SklStepModel();
      step.addModel(anchor);      
      step.addModel(text1);
      
      step.addRelationship(new SklSideRelationship(anchor, text1, Side.BOTTOM));

      
      SklImageModel referenceImage = new SklImageModel("reference.png");
      step.setReferenceImage(referenceImage);

      return step;
   }
   
   SklStepModel createStep2(){
      SklAnchorModel anchor = new SklAnchorModel();
      anchor.setX(27);
      anchor.setY(125);
      anchor.setWidth(25);
      anchor.setHeight(25);
      
      //anchor.setPattern(new SklPatternModel("play.png"));

      SklTextModel text1 = new DefaultSklTextModel("Step 2");
      text1.setHasShadow(true);

      SklStepModel step = new SklStepModel();
      step.addModel(anchor);      
      step.addModel(text1);
      
      step.addRelationship(new SklSideRelationship(anchor, text1, Side.RIGHT));
      
      SklImageModel referenceImage = new SklImageModel("reference.png");
      step.setReferenceImage(referenceImage);

      return step;
   }
   
   SklStepModel createStep3(){
      SklAnchorModel anchor = new SklAnchorModel();
      anchor.setX(150);
      anchor.setY(30);
      anchor.setWidth(50);
      anchor.setHeight(50);
      
      //anchor.setPattern(new SklPatternModel("play.png"));

      SklTextModel text1 = new DefaultSklTextModel("Step 3");
      text1.setHasShadow(true);

      SklStepModel step = new SklStepModel();
      step.addModel(anchor);      
      step.addModel(text1);
      
      step.addRelationship(new SklSideRelationship(anchor, text1, Side.LEFT));
      
      SklImageModel referenceImage = new SklImageModel("reference.png");
      step.setReferenceImage(referenceImage);

      return step;
   }
   @Test
   public void testEditorLoad() throws IOException{
      SklEditor e = new SklEditor();

      
      (e.new LoadAction()).actionPerformed(null);
      //(e.new NewAction()).actionPerformed(null);
      //(e.new CaptureAction()).actionPerformed(null);
      
      
      
      synchronized(e){
         try {
            e.wait();
         } catch (InterruptedException ex) {
            ex.printStackTrace();
         }
      }
   }
   
   @Test
   public void testEditor() throws IOException{
      
      SklEditor e = new SklEditor();
      
      e.addStep(createStep1());
      e.addStep(createStep2());
      e.addStep(createStep3());
      
      
      //(e.new PlayAction()).actionPerformed(null); 
      
      RecordedClickEvent rce;

      rce = new RecordedClickEvent();
      rce.setScreenImage(ImageIO.read(new File("screen1.png")));
      rce.setClickLocation(new Point(200,140));
      
      e.importStep(rce);      
      
      e.selectStep(0);
      
      //e.removeStep(2);

      (e.new SaveAction()).actionPerformed(null); 

      
      synchronized(e){
         try {
            e.wait();
         } catch (InterruptedException ex) {
            ex.printStackTrace();
         }
      }
   }
   
   @Test
   public void testStepList(){
      
         final SklStepListModel list = new SklStepListModel();
         list.addElement(createStep1());
         list.addElement(createStep2());
         list.addElement(createStep3());
         
         //final SklStepSimpleViewer stepView = new SklStepSimpleViewer((SklStepModel) list.getElementAt(0));
         final SklStepPreview stepView = new SklStepPreview((SklStepModel) list.getElementAt(0));
         
         SklStepListSelectionModel listSelection = new SklStepListSelectionModel();
         listSelection.setSelectionInterval(0,0);
         listSelection.addListSelectionListener(new ListSelectionListener(){

            @Override
            public void valueChanged(ListSelectionEvent e) {
                              
               Debug.info("" + e);
               
               if (e.getValueIsAdjusting()  == false){

                  int index = ((SklStepListSelectionModel) e.getSource()).getLeadSelectionIndex();
                  Debug.info("index:" + index);

                  //((SklStepListSelectionModel) e.getSource()).isSelectedIndex(e.getLastIndex());
                  
                  SklStepModel stepModel = (SklStepModel) list.getElementAt(index);

                  Debug.info(" selected step:" + stepModel);
                  
                  stepView.setModel(stepModel);
                  stepView.repaint();
                  
               }
               
            }
            
         });
         
         
         
         SklStepListView view1 = new SklStepListView(list,listSelection);
         //SklStepListView view2 = new SklStepListView(list,listSelection);
         //list.ad
         
         
         //view.addSelectionListener(new StepListViewSelectionEventListener(){
            
         //});
         helperShowInSplitFrame(view1,stepView);
         
   }
   
   @Test
   public void testXMLSerialization() throws Exception{
      
      SklAnchorModel anchor = new SklAnchorModel();
      anchor.setX(100);
      anchor.setY(100);
      anchor.setWidth(50);
      anchor.setHeight(50);
      
      anchor.setPattern(new SklPatternModel("play.png"));

      SklTextModel text1 = new DefaultSklTextModel("Some text");
      text1.setX(100);
      text1.setY(500);
      text1.setHasShadow(true);

      SklTextModel text2 = new DefaultSklTextModel("Dependent text");
      text2.setHasShadow(true);


      SklTextModel text3 = new DefaultSklTextModel("Another Dependent text");
      text3.setHasShadow(true);

//      SklFlagModel flag1 = new SklFlagModel("A Flag");
//      flag1.setHasShadow(true);
//      flag1.setDirection(SklFlagModel.DIRECTION_WEST);
//
//      SklCalloutModel callout1 = new SklCalloutModel("A Callout");
//      callout1.setHasShadow(true);
//      callout1.setDirection(SklFlagModel.DIRECTION_NORTH);

      SklImageModel image1 = new SklImageModel("tiger.png");
      image1.setHasShadow(true);
      
      
      SklStepModel step = new SklStepModel();
      step.addModel(anchor);      
      step.addModel(text1);
      step.addModel(text2);
      step.addModel(text3);
//      step.addModel(flag1);
//      step.addModel(callout1);
      step.addModel(image1);

      step.addRelationship(new SklSideRelationship(anchor, text1, Side.BOTTOM));
      step.addRelationship(new SklSideRelationship(text1, text2, Side.RIGHT));
      step.addRelationship(new SklOffsetRelationship(text2, text3, 200,0));
      step.addRelationship(new SklSideRelationship(text2, image1, Side.BOTTOM));

      //      step.addRelationship(new SklSideRelationship(text3, flag1, Side.RIGHT));
//      step.addRelationship(new SklSideRelationship(text3, callout1, Side.BOTTOM));
//      step.addRelationship(new SklSideRelationship(callout1, image1, Side.RIGHT));
      
      SklImageModel referenceImage = new SklImageModel("reference.png");
      step.setReferenceImage(referenceImage);
      
      Strategy strategy = new CycleStrategy("id","ref");
      Serializer serializer = new Persister(strategy);
     
      File fout = new File("/Users/tomyeh/Desktop/output_raw.xml");
      serializer.write(step, fout);

      File fin = new File("/Users/tomyeh/Desktop/output_raw.xml");
      SklStepModel dsStep = serializer.read(SklStepModel.class, fin);


      //SklStepSimpleViewer viewer = new SklStepSimpleViewer(step);

      SklStepSimpleViewer viewer = new SklStepSimpleViewer(dsStep);
      helperShowInFrame(viewer);

//      anchor.setOpacity(0);
//      step.updateRelationships(anchor);
      //addAnimationsHelper(step, anchor);
      

      
//
//      g.playStep(step);
//
//      fout = new File("/Users/tomyeh/Desktop/output_anchored.xml");
//      serializer.write(step, fout);
//      
//      
//      File fin = new File("/Users/tomyeh/Desktop/output_anchored.xml");
//
//      SklStepModel dsStep = serializer.read(SklStepModel.class, fin);
//
//         
//      //g.playStep(step);
//       g.playStep(dsStep);
//       // g.showNow(5);
   }
   
   
   @Test
   public void testXMLSerializationOld() throws Exception{
      
      
      SikuliGuide g = new SikuliGuide();
      
      DefaultSklObjectModel comp = new DefaultSklObjectModel();
      comp.setLocation(new Point(100,100));
      comp.setSize(new Dimension(500,300));      
      comp.setForeground(Color.red);
      comp.setBackground(Color.yellow);
      
      SklAnchorModel anchor = new SklAnchorModel(new Rectangle(250,250,200,300));
      anchor.setPattern(new SklPatternModel("play.png"));

      DefaultSklTextModel text1 = new DefaultSklTextModel("Some text");
      text1.setLocation(50,500);
      text1.setHasShadow(true);

      DefaultSklTextModel text2 = new DefaultSklTextModel("Dependent text");
      text2.setHasShadow(true);

      DefaultSklTextModel text3 = new DefaultSklTextModel("Another Dependent text");
      text3.setHasShadow(true);

      SklFlagModel flag1 = new SklFlagModel("A Flag");
      flag1.setHasShadow(true);
      flag1.setDirection(SklFlagModel.DIRECTION_WEST);

      SklCalloutModel callout1 = new SklCalloutModel("A Callout");
      callout1.setHasShadow(true);
      callout1.setDirection(SklFlagModel.DIRECTION_NORTH);

      SklImageModel image1 = new SklImageModel("tiger.png");
      image1.setHasShadow(true);
      
      
      SklStepModel step = new SklStepModel();
      step.addComponent(anchor);      
      step.addComponent(text1);
      step.addComponent(text2);
      step.addComponent(text3);
      step.addComponent(flag1);
      step.addComponent(callout1);
      step.addComponent(image1);

      step.addRelationship(new SklSideRelationship(anchor, text1, Side.BOTTOM));
      step.addRelationship(new SklSideRelationship(text1, text2, Side.RIGHT));
      step.addRelationship(new SklOffsetRelationship(text2, text3, 200,0));
      step.addRelationship(new SklSideRelationship(text3, flag1, Side.RIGHT));
      step.addRelationship(new SklSideRelationship(text3, callout1, Side.BOTTOM));
      step.addRelationship(new SklSideRelationship(callout1, image1, Side.RIGHT));
      
      SklImageModel referenceImage = new SklImageModel("reference.png");
      step.setReferenceImage(referenceImage);
      
      anchor.setOpacity(0);
      step.updateRelationships(anchor);
      //addAnimationsHelper(step, anchor);
      

      
      Strategy strategy = new CycleStrategy("id","ref");
      Serializer serializer = new Persister(strategy);

      
      File fout = new File("/Users/tomyeh/Desktop/output_raw.xml");
      serializer.write(step, fout);

      g.playStep(step);

      fout = new File("/Users/tomyeh/Desktop/output_anchored.xml");
      serializer.write(step, fout);
      
      
      File fin = new File("/Users/tomyeh/Desktop/output_anchored.xml");

      SklStepModel dsStep = serializer.read(SklStepModel.class, fin);

         
      //g.playStep(step);
       g.playStep(dsStep);
       // g.showNow(5);
   }
   
   @Test
   public void testPlayFromXML() throws Exception{
      Strategy strategy = new CycleStrategy("id","ref");
      Serializer serializer = new Persister(strategy);

//      File fin = new File("/Users/tomyeh/Desktop/output_raw.xml");
//      File fin = new File("/Users/tomyeh/Desktop/output_anchored.xml");
      File fin = new File("/Users/tomyeh/Desktop/output_anchored_to_reference.xml");

      SklStepModel step = serializer.read(SklStepModel.class, fin);
       
      SikuliGuide g = new SikuliGuide();
      //g.playStep(dsStep);
      
      //g.testStep(step);
      
      
      g.addComponent(new SklStepPreview(step));
      
      SikuliGuideButton btn = new SikuliGuideButton("Exit");
      btn.setActualLocation(50,50);
      g.addToFront(btn);
      g.showNow();

      

      
//      File fout = new File("/Users/tomyeh/Desktop/output_anchored_to_reference.xml");
//      serializer.write(step, fout);
      
   }
   
   void helperShowInSplitFrame(JComponent view1, JComponent view2){
      
      
      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      
      // TODO: get this to work
//      view.getActionMap().put("Undo", UndoManagerHelper.getUndoAction(manager));
//      view.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
//      view.setFocusable(true);
      
      JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            view1, view2);
      splitPane.setDividerLocation(250);
      
      Container content = f.getContentPane();
//      content.add(toolbar, BorderLayout.NORTH);
      content.add(splitPane,BorderLayout.CENTER);      

//      content.add(view1, BorderLayout.CENTER);
//      content.add(view2, BorderLayout.EAST);
      
      f.setSize(1000,600);
      f.setLocationRelativeTo(null);
      f.setVisible(true);

      //f.pack();
      
      synchronized(f){
         try {
            f.wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

   }
   
   void helperShowInFrame(JComponent view){
      
      
      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      
      // TODO: get this to work
//      view.getActionMap().put("Undo", UndoManagerHelper.getUndoAction(manager));
//      view.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
//      view.setFocusable(true);
      
      Container content = f.getContentPane();
//      content.add(toolbar, BorderLayout.NORTH);
      content.add(view, BorderLayout.CENTER);
      
      f.setSize(1000,600);
      f.setLocationRelativeTo(null);
      f.setVisible(true);

      //f.pack();
      
      synchronized(f){
         try {
            f.wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

   }
   
   JFrame helperSimpleFrame(SklStepPreview view){
      
      
      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      
      UndoManager manager = new UndoManager();
      view.addUndoableEditListener(manager);

      JToolBar toolbar = new JToolBar();
      toolbar.add(UndoManagerHelper.getUndoAction(manager));
      toolbar.add(UndoManagerHelper.getRedoAction(manager));
      

      // TODO: get this to work
//      view.getActionMap().put("Undo", UndoManagerHelper.getUndoAction(manager));
//      view.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
//      view.setFocusable(true);
      
      Container content = f.getContentPane();
      content.add(toolbar, BorderLayout.NORTH);
      content.add(new JButton("Dummy"), BorderLayout.WEST);
      content.add(view, BorderLayout.CENTER);
      

      return f;
   }
   
   @Test
   public void testStepEditView(){
      
      SklStepModel step = new SklStepModel();
      step.setReferenceImage(new SklImageModel("reference.png"));
             
      SklStepPreview view = new SklStepPreview(step);
      
      JFrame f = helperSimpleFrame(view);
      
      SklTextModel textModel = new DefaultSklTextModel("Text");
      textModel.setHasShadow(true);      
      (view.new AddComponentAtLocationAction(textModel, new Point(100,100))).actionPerformed(null);
      

      SklAnchorModel anchorModel = new SklAnchorModel();
      anchorModel.setBounds(new Rectangle(150,150,100,100));
      (view.new AddComponentAction(anchorModel)).actionPerformed(null);


      SklAnchorModel anchorModel2 = new SklAnchorModel();
      anchorModel2.setBounds(new Rectangle(350,150,50,50));
      (view.new AddComponentAction(anchorModel2)).actionPerformed(null);

      
      (view.new LinkAction(anchorModel, textModel)).actionPerformed(null);

      (view.new SelectAction(anchorModel)).actionPerformed(null);
      (view.new SelectAction(anchorModel2)).actionPerformed(null);

      //(view.new AddComponentAction(box)).actionPerformed(null);
      
      
      f.setSize(1000,600);
      f.setVisible(true);

      //f.pack();
      
      synchronized(f){
         try {
            f.wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      
   }
   
   @Test
   public void testPreview() throws Exception{
      Strategy strategy = new CycleStrategy("id","ref");
      Serializer serializer = new Persister(strategy);
      File fin = new File("/Users/tomyeh/Desktop/output_anchored_to_reference.xml");
      SklStepModel step = serializer.read(SklStepModel.class, fin);
       
      
      SklStepPreview view = new SklStepPreview(step);
//      g.addComponent(view);
      

      
      
      //view.testAddText();
      
      
//      JFrame f = new JFrame();
//      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//      
//      
//      UndoManager manager = new UndoManager();
//      view.addUndoableEditListener(manager);
//
//      JToolBar toolbar = new JToolBar();
//      toolbar.add(UndoManagerHelper.getUndoAction(manager));
//      toolbar.add(UndoManagerHelper.getRedoAction(manager));
//
//      Container content = f.getContentPane();
//      content.add(toolbar, BorderLayout.NORTH);
//      content.add(view, BorderLayout.CENTER);
//
//      
//      view.testAddText();
//
//      f.setSize(1000,600);
//      f.setVisible(true);
//
//      //f.pack();
//      
//      synchronized(f){
//         try {
//            f.wait();
//         } catch (InterruptedException e) {
//            e.printStackTrace();
//         }
//      }
//      
   }
//   
//   
//   @Test
//   public void testSklText() {
//
//      SikuliGuide g = new SikuliGuide();
//
//      Region r = new Region(250,250,200,300);
//
//      
//      SklComponent comp = new SklComponent();
//      comp.setLocation(new Point(100,100));
//      comp.setSize(new Dimension(500,300));      
//      comp.setForeground(Color.red);
//      comp.setBackground(Color.yellow);
//      
//      SklRectangleModel rect = new SklRectangleModel(new Rectangle(100,100,300,500));
//      rect.setForeground(Color.green);
//      SklRectangleView rectView = new SklRectangleView(rect);
//      
////      comp.setLocation(new Point(100,100));
////      comp.setSize(new Dimension(500,300));      
////      comp.setForeground(Color.red);
////      comp.setBackground(Color.yellow);
//      
//
//      
//      SklTextModel text = new SklTextModel();
//      text.setText("This is text");
//      text.setBackground(Color.yellow);
//      text.setHasShadow(true);
//
//      SklTextModel dependentText1 = new SklTextModel();
//      dependentText1.setText("This is a dependent.");
//      dependentText1.setBackground(Color.red);
//      dependentText1.setHasShadow(true);
//      
//      SklTextModel dependentText2 = new SklTextModel();
//      dependentText2.setText("This is another dependent.");
//      dependentText2.setBackground(Color.green);
//      dependentText2.setHasShadow(true);
//
//      SklTextModel dependentText3 = new SklTextModel();
//      dependentText3.setText("This is yet another dependent.");
//      dependentText3.setBackground(Color.cyan);
//      dependentText3.setHasShadow(true);
//
//      
//      text.addDependentRelationship(dependentText1, new SklOffsetRelationship(50,50));
//      dependentText1.addDependentRelationship(dependentText2, new SklSideRelationship(Layout.BOTTOM));
//      dependentText2.addDependentRelationship(dependentText3, new SklSideRelationship(Layout.RIGHT));
//
//      
//      //text.updateDependents();
//      
//      SklComponentView compView = new SklComponentView(comp);      
//      SklTextView textView = new SklTextView(text);
//      SklTextView textView1 = new SklTextView(dependentText1);
//      SklTextView textView2 = new SklTextView(dependentText2);
//      SklTextView textView3 = new SklTextView(dependentText3);
//      
//      SklAnimationManager animMgr = new SklAnimationManager();
//      
//      animMgr.add(SklAnimationFacotry.createMoveAnimation(text, new Point(800,80), new Point(500,80)));
//      animMgr.add(SklAnimationFacotry.createMoveAnimation(text, new Point(500,80), new Point(500,200)));
//      animMgr.start();
//
//      //animMgr.add(animSeq);
//      
//      //textView.testAnimation();
//
//      g.addComponent(rectView);
//      g.addComponent(compView);
//      g.addComponent(textView);
//      g.addComponent(textView1);
//      g.addComponent(textView2);
//      g.addComponent(textView3);
//
//      g.showNow(5);
//      
////      text.setText("Text is updated");
////      text.setLocation(new Point(500,50));
////
////      textView.invalidate();
//      
////      g.addComponent(textView);
////      g.showNow(2);
//      
//   }
   
   void addAnimationsHelper(SklStepModel step, DefaultSklObjectModel o){
      step.addAnimation(SklAnimationFactory.createMoveAnimation(o, new Point(50,50), new Point(500,250)));
      step.addAnimation(SklAnimationFactory.createOpacityAnimation(o, 1f, 0f));
      step.addAnimation(SklAnimationFactory.createOpacityAnimation(o, 0f, 0.5f));
//      step.addAnimation(SklAnimationFacotry.createResizeAnimation(o, new Dimension(200,300), new Dimension(100,100)));
//      step.addAnimation(SklAnimationFacotry.createResizeAnimation(o, new Dimension(100,100), new Dimension(300,300)));
   }
//   
   void animateComponent(DefaultSklObjectModel o){
      SklAnimationManager animMgr = new SklAnimationManager();
      
      animMgr.add(SklAnimationFactory.createMoveAnimation(o, new Point(50,50), new Point(500,250)));
      animMgr.add(SklAnimationFactory.createOpacityAnimation(o, 1f, 0f));
      animMgr.add(SklAnimationFactory.createOpacityAnimation(o, 0f, 0.5f));
      //animMgr.add(SklAnimationFacotry.createOpacityAnimation(o, 0.5f, 1f));
      animMgr.add(SklAnimationFactory.createResizeAnimation(o, new Dimension(200,300), new Dimension(100,100)));
      animMgr.add(SklAnimationFactory.createResizeAnimation(o, new Dimension(100,100), new Dimension(300,300)));
      animMgr.start();
   }
//
//   @Test
//   public void testAnchor() {
//
//      SikuliGuide g = new SikuliGuide();
//      
//
//      SklAnchorModel o = new SklAnchorModel(new Rectangle(250,250,200,300));
//      g.addToFront(o.createView());
//      
//      SklRectangleModel r = new SklRectangleModel();
//      r.setForeground(Color.red);
//      g.addToFront(r.createView());
//      
//      o.addDependentRelationship(r, new SklSideRelationship(Layout.OVER));      
//
//      animateComponent(o);
//      
//      g.showNow(5);
//
//   }
   
   @Test
   public void testCallout() {
      SikuliGuide g = new SikuliGuide();
      
      //RelationshipManager relationshipMgr = new RelationshipManager();
      
      Rectangle r = new Rectangle(250,250,200,300);

      SklRectangleModel o = new SklRectangleModel(r);
      o.setForeground(Color.red);
      g.addToFront(o.createView());
      
      SklCalloutModel t = new SklCalloutModel("This is Top");
      t.setHasShadow(true);
      t.setDirection(SklCalloutModel.DIRECTION_SOUTH);
      g.addToFront(t.createView());      
      //RelationshipManager.getInstance().addRelationship(new SklSideRelationship(o, t, Side.TOP));
      
      t = new SklCalloutModel("This is Bottom");
      t.setHasShadow(true);
      t.setDirection(SklCalloutModel.DIRECTION_NORTH);
      g.addToFront(t.createView());
      //RelationshipManager.getInstance().addRelationship(new SklSideRelationship(o, t, Side.BOTTOM));
      
      
      t = new SklCalloutModel("This is Left. 8pt font.");
      t.setHasShadow(true);      
      t.setDirection(SklCalloutModel.DIRECTION_EAST);
      t.setFontSize(8);
      //RelationshipManager.getInstance().addRelationship(new SklSideRelationship(o, t, Side.LEFT));
      g.addToFront(t.createView());
//      
//      t = new SklCalloutModel("This is Right. There is lots of text in this. This should change line.");
//      t.setHasShadow(true);      
//      t.setDirection(SklCalloutModel.DIRECTION_WEST);
//      t.setMaximumWidth(200);
//      o.addDependentRelationship(t, new SklSideRelationship(Layout.RIGHT));      
//      g.addToFront(t.createView());

      
//        o.updateDependents();
      
      animateComponent(o);

      
      g.showNow();
      
   }
   
   
//   @Test
//   public void testFlag() {
//
//      SikuliGuide g = new SikuliGuide();
//
//      Rectangle r = new Rectangle(50,250,200,300);
//
//      SklRectangleModel o = new SklRectangleModel(r);
//      o.setForeground(Color.red);
//      g.addToFront(o.createView());
//      
//      SklFlagModel t = new SklFlagModel("This is Top");
//      t.setHasShadow(true);
//      t.setDirection(SklFlagModel.DIRECTION_SOUTH);
//      o.addDependentRelationship(t, new SklSideRelationship(Layout.TOP));      
//      g.addToFront(t.createView());
//      
//      t = new SklFlagModel("This is Bottom");
//      t.setHasShadow(true);
//      t.setDirection(SklFlagModel.DIRECTION_NORTH);
//      o.addDependentRelationship(t, new SklSideRelationship(Layout.BOTTOM));      
//      g.addToFront(t.createView());
////      
//      t = new SklFlagModel("This is Left");
//      t.setHasShadow(true);      
//      t.setDirection(SklFlagModel.DIRECTION_EAST);
//      o.addDependentRelationship(t, new SklSideRelationship(Layout.LEFT));      
//      g.addToFront(t.createView());
//   
//      t = new SklFlagModel("This is Right");
//      t.setHasShadow(true);      
//      t.setDirection(SklFlagModel.DIRECTION_WEST);
//      o.addDependentRelationship(t, new SklSideRelationship(Layout.RIGHT));      
//      g.addToFront(t.createView());
//
//      t = new SklFlagModel("This is inside");
//      t.setHasShadow(true);      
//      o.addDependentRelationship(t, new SklSideRelationship(Layout.INSIDE));      
//      g.addToFront(t.createView());
//      
//      o.updateDependents();
//
//      animateComponent(o);
//
//      
//      g.setDialog("test flags");
//      g.showNow(5);
//
//   }
//
//   @Test
//   public void testText() {
//
//      SikuliGuide g = new SikuliGuide();
//
//      Rectangle r = new Rectangle(50,250,200,300);
//
//      SklRectangleModel o = new SklRectangleModel(r);
//      o.setForeground(Color.red);
//      g.addToFront(o.createView());
//      
//      SklTextModel t = new SklTextModel("This is Top");
//      t.setHasShadow(true);
//      o.addDependentRelationship(t, new SklSideRelationship(Layout.TOP));      
//      g.addToFront(t.createView());
//      
//      t = new SklTextModel("This is Bottom");
//      t.setHasShadow(true);
//      o.addDependentRelationship(t, new SklSideRelationship(Layout.BOTTOM));      
//      g.addToFront(t.createView());
//      
//      t = new SklTextModel("This is Left");
//      t.setHasShadow(true);      
//      o.addDependentRelationship(t, new SklSideRelationship(Layout.LEFT));      
//      g.addToFront(t.createView());
//   
//      t = new SklTextModel("This is Right");
//      t.setHasShadow(true);      
//      o.addDependentRelationship(t, new SklSideRelationship(Layout.RIGHT));      
//      g.addToFront(t.createView());
//
//      t = new SklTextModel("This is inside");
//      t.setHasShadow(true);      
//      o.addDependentRelationship(t, new SklSideRelationship(Layout.INSIDE));      
//      g.addToFront(t.createView());
//
//      animateComponent(o);
//
//
////
////
////
////      SikuliGuideText bottom = new SikuliGuideText("This is Bottom");
////      bottom.setLocationRelativeToRegion(r, Layout.BOTTOM);
////      g.addToFront(bottom);
////
////      SikuliGuideText left = new SikuliGuideText("This is Left");
////      left.setLocationRelativeToRegion(r, Layout.LEFT);
////      g.addToFront(left);
////
////      SikuliGuideText right = new SikuliGuideText("This is Right");
////      right.setLocationRelativeToRegion(r, Layout.RIGHT);
////      g.addToFront(right);
////      
////      
////      SikuliGuideText inside = new SikuliGuideText("This is Inside");
////      inside.setLocationRelativeToRegion(r, Layout.INSIDE);
////      g.addToFront(inside);
////
////
////      SikuliGuideText tl = new SikuliGuideText("Right Alignment");
////      tl.setLocationRelativeToRegion(r, Layout.LEFT);
////      tl.setVerticalAlignmentWithRegion(r, 0f);
////      g.addToFront(tl);
////
////      SikuliGuideText bl = new SikuliGuideText("Bottom Alignment");
////      bl.setShadowDefault();
////      bl.setLocationRelativeToRegion(r, Layout.RIGHT);
////      bl.setVerticalAlignmentWithRegion(r, 1f);
////      g.addToFront(bl);
////
////      SikuliGuideText ht = new SikuliGuideText("Left Alignment");
////      ht.setShadowDefault(); 
////      ht.setLocationRelativeToRegion(r, Layout.TOP);
////      ht.setHorizontalAlignmentWithRegion(r, 0f);
////      ht.setLocation(ht.getX(),ht.getY()-100);
////      g.addToFront(ht);
////
////      SikuliGuideText rb = new SikuliGuideText("Right Alignment");
////      rb.setShadowDefault();
////      rb.setLocationRelativeToRegion(r, Layout.BOTTOM);
////      rb.setHorizontalAlignmentWithRegion(r, 1f);
////      rb.setLocation(rb.getX(),rb.getY()+100);      
////      g.addToFront(rb);
////
////      SikuliGuideText cb = new SikuliGuideText("Center Alignment");
////      cb.setLocationRelativeToRegion(r, Layout.BOTTOM);
////      cb.setHorizontalAlignmentWithRegion(r, 0.5f);
////      cb.setLocation(cb.getX(),cb.getY()+50);
////      g.addToFront(cb);
////
////
////      SikuliGuideText qq = new SikuliGuideText("Long text with maximum width set to 300");
////      qq.setLocationRelativeToRegion(r, Layout.RIGHT);
////      qq.setVerticalAlignmentWithRegion(r, 0f);      
////      qq.setMaximumWidth(300);
////      g.addToFront(qq);
////
////
////
////      r = new Region(800,400,100,100);
////      o = new SikuliGuideRectangle(r);
////      o.setForeground(Color.green);
////      g.addToFront(o);
////
////
////      SikuliGuideText t = new SikuliGuideText("This is a sentence in small font and it is long and supposed to be auto wrapped.");      
////      t.setFontSize(10);
////      t.setLocationRelativeToComponent(o, Layout.TOP);
////      //t.setHorizontalAlignmentWithRegion(r, 0.5f);      
////      g.addToFront(t);
////      
////      o.resizeTo(new Dimension(200,200));
//
//      g.setDialog("test text");
//      g.showNow(5);
//
//   }
}


