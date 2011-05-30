package org.sikuli.guide;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.undo.UndoManager;

import org.sikuli.script.Debug;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;


public class SklEditor extends JFrame {
   
   SklStepModel stepModel;
   SklStepPreview stepView;
   
   SklStepListView stepListView;
   
   SklStepListModel stepListModel;
   SklStepListSelectionModel stepListSelection;
   
   
   public SklEditor(){
      
      //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      // TODO: get this to work
//      view.getActionMap().put("Undo", UndoManagerHelper.getUndoAction(manager));
//      view.getInputMap().put(KeyStroke.getKeyStroke("control Z"), "Undo");
//      view.setFocusable(true);
      
      stepModel = new SklStepModel(); 
      stepView = new SklStepPreview(null);
      
      stepListModel = new SklStepListModel();      
      stepListSelection = new SklStepListSelectionModel();
      // this allows the editing pane to show the selected step in the list
      stepListSelection.addListSelectionListener(new ListSelectionListener(){

         @Override
         public void valueChanged(ListSelectionEvent e) {
                           
            if (e.getValueIsAdjusting()  == false){

               int index = ((SklStepListSelectionModel) e.getSource()).getLeadSelectionIndex();
               stepModel = (SklStepModel) stepListModel.getElementAt(index);
               stepView.setModel(stepModel);
               stepView.repaint();
               
            }
            
         }
         
      });
      
            
      stepListView = new SklStepListView(stepListModel, stepListSelection);

      JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            stepListView, stepView);
      splitPane.setDividerLocation(250);
  
      UndoManager manager = new UndoManager();
      
      JToolBar toolbar = new JToolBar();
//      toolbar.add(UndoManagerHelper.getUndoAction(manager));
//      toolbar.add(UndoManagerHelper.getRedoAction(manager));
      toolbar.add(new NewAction());
      toolbar.addSeparator();
      toolbar.add(new LoadAction());
      toolbar.add(new SaveAction());
      toolbar.add(new SaveAsAction());
      toolbar.addSeparator();
      toolbar.add(new CaptureAction());
      toolbar.addSeparator();
      toolbar.add(new PlayAction());
      toolbar.add(new PlayAllAction());

  //      view.addUndoableEditListener(manager);

      
      Container content = getContentPane();
      content.add(toolbar, BorderLayout.NORTH);
      content.add(splitPane,BorderLayout.CENTER);      
      
      setSize(1000,600);
      setLocationRelativeTo(null);
      setVisible(true);      
      setTitle("untitled");
      
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
               
               if (w.clickEvents.size() > 0)
                  selectStep(0);
               
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
   
   public class LoadAction extends AbstractAction {
      public LoadAction(){
         super("Load");
      }

      @Override
      public void actionPerformed(ActionEvent event) {
         
         if (bundlePath != null) {
            
            SklEditor e = new SklEditor();
            e.setVisible(true);

            Point o = getLocation();
            e.setLocation(o.x + 50, o.y + 50);
            
            (e.new LoadAction()).actionPerformed(null);
            return;

         }
            

         File file = new FileChooser(SklEditor.this).load();
         if (file == null) 
            return;
         
         bundlePath = file.getAbsolutePath();
         if( !file.getAbsolutePath().endsWith(".sikuli") )
            bundlePath += ".sikuli";
         
         String filename = bundlePath + File.separator + "wysiwyg.xml";
         Debug.log(1, "load from: " + filename);
         
         Strategy strategy = new CycleStrategy("id","ref");
         Serializer serializer = new Persister(strategy);
        
         SklDocument doc; 

         File fin = new File(filename);
         try {
            
               doc = serializer.read(SklDocument.class, fin);
         
               for (SklStepModel stepModel : doc.steps){
                  stepModel.getReferenceImage().bundlePath = bundlePath;
                  addStep(stepModel);
               }       
               
         } catch (Exception e1) {
            e1.printStackTrace();
         }
         
         selectStep(0);
         
         setTitle(bundlePath);
         
         

      }
   }
      
   public class SaveAsAction extends AbstractAction {
      public SaveAsAction(){
         super("Save As ...");
      }

      @Override
      public void actionPerformed(ActionEvent event) {
         
         File file = new FileChooser(SklEditor.this).save();
         if (file == null) 
            return;
         
         Debug.info("file: " + file);

         String bundlePath = file.getAbsolutePath();
         if( !file.getAbsolutePath().endsWith(".sikuli") )
            bundlePath += ".sikuli";
         
         File f = new File(bundlePath);
         if( !f.exists() )
            f.mkdir();
         
         // TODO: enable checking of overwriting
//         if(Utils.exists(bundlePath)){
//            int res = JOptionPane.showConfirmDialog(
//                  null, I18N._I("msgFileExists", bundlePath), 
//                  I18N._I("dlgFileExists"), JOptionPane.YES_NO_OPTION);
//            if(res != JOptionPane.YES_OPTION)
//               return null;
         //}
         //saveAsBundle(bundlePath);
         
         for (Enumeration<?> e = stepListModel.elements() ; e.hasMoreElements(); ){
            SklStepModel stepModel = (SklStepModel) e.nextElement();
            
            String filename = getTimestamp() + ".png";
            SklImageModel imageModel = stepModel.getReferenceImage();
            
            imageModel.setImageUrl(filename);
            
            BufferedImage image = imageModel.getImage();
            saveImage(image, filename, bundlePath);
         }
         
        
         String filename = bundlePath + File.separator + "wysiwyg.xml";
         Debug.log(1, "save to: " + filename);
         
         Strategy strategy = new CycleStrategy("id","ref");
         Serializer serializer = new Persister(strategy);
        
         SklDocument doc = new SklDocument();
         for (Enumeration<?> e = stepListModel.elements() ; e.hasMoreElements(); ){
            SklStepModel stepModel = (SklStepModel) e.nextElement();                        
            doc.steps.add(stepModel);
         }       
         
         File fout = new File(filename);
         try {
               serializer.write(doc, fout);
         } catch (Exception e1) {
            e1.printStackTrace();
         }
         
         setTitle(bundlePath);
      }
   }

   String bundlePath; 

   public class SaveAction extends AbstractAction {
      public SaveAction(){
         super("Save");
      }

      
      @Override
      public void actionPerformed(ActionEvent event) {
         
         if (bundlePath == null){
            File file = new FileChooser(SklEditor.this).save();
            if(file == null)  
               return;

            //File file = new File("/Users/tomyeh/Desktop/test");
            Debug.info("file: " + file);


            bundlePath = file.getAbsolutePath();
            if( !file.getAbsolutePath().endsWith(".sikuli") )
               bundlePath += ".sikuli";

            File f = new File(bundlePath);
            if( !f.exists() )
               f.mkdir();
         
         }
         
         
         // TODO: enable checking of overwriting
//         if(Utils.exists(bundlePath)){
//            int res = JOptionPane.showConfirmDialog(
//                  null, I18N._I("msgFileExists", bundlePath), 
//                  I18N._I("dlgFileExists"), JOptionPane.YES_NO_OPTION);
//            if(res != JOptionPane.YES_OPTION)
//               return null;
         //}
         //saveAsBundle(bundlePath);
         
         for (Enumeration<?> e = stepListModel.elements() ; e.hasMoreElements(); ){
            SklStepModel stepModel = (SklStepModel) e.nextElement();
            
            String filename = getTimestamp() + ".png";
            SklImageModel imageModel = stepModel.getReferenceImage();
            
            imageModel.setImageUrl(filename);
            
            BufferedImage image = imageModel.getImage();
            saveImage(image, filename, bundlePath);
         }
         
        
         String filename = bundlePath + File.separator + "wysiwyg.xml";
         Debug.log(1, "save to: " + filename);
         
         Strategy strategy = new CycleStrategy("id","ref");
         Serializer serializer = new Persister(strategy);
        
         SklDocument doc = new SklDocument();
         for (Enumeration<?> e = stepListModel.elements() ; e.hasMoreElements(); ){
            SklStepModel stepModel = (SklStepModel) e.nextElement();                        
            doc.steps.add(stepModel);
         }       
         
         File fout = new File(filename);
         try {
            
            //serializer.write(stepModel, fout);
         
               serializer.write(doc, fout);
       
         
         } catch (Exception e1) {
            e1.printStackTrace();
         }
         
         setTitle(bundlePath);
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
               g.playStep(stepModel);    
               
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
   
   public class PlayAllAction extends AbstractAction {
      
      public PlayAllAction(){
         super("Play All");
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         
         
         Thread t = new Thread(){

            public void run(){
               setVisible(false);
               SikuliGuide g = new SikuliGuide();               
               g.playStepList(stepListModel);
               setVisible(true);
            }
         };
         
         t.start();
         
         
      }
    }
   
   
   public void importStep(RecordedClickEvent event){

      SklStepModel importedStepModel = new SklStepModel();
      Point clickLocation = event.getClickLocation();
      Rectangle defaultAnchorBounds = new Rectangle(50,50);
      // center the default anchor at the click location
      defaultAnchorBounds.x = clickLocation.x - defaultAnchorBounds.width/2;
      defaultAnchorBounds.y = clickLocation.y - defaultAnchorBounds.height/2;
      
      SklImageModel imageModel = new SklImageModel();
      imageModel.setImage(event.getScreenImage()); 
      
      SklAnchorModel anchor = new SklAnchorModel(defaultAnchorBounds);      
      importedStepModel.addModel(anchor);
      
      importedStepModel.setReferenceImage(imageModel);
      
//         // TODO chooses the best location automatically (that does not go outside of display bounds) 
//         SikuliGuideText txt = (SikuliGuideText) 
//            performAddTextAction(new Point(defaultAnchorBounds.x+50,defaultAnchorBounds.y-20));
//         txt.setText("Click");     
//         currentStepContentChanged();
//      }
//
//      if (overview != null){
//         overview.addStep(step);
//      }
//      validate();
//     
//      return step;
      
      addStep(importedStepModel);
   }
   

   public void selectStep(int index){
      stepListSelection.setSelectionInterval(index,index);
   }

   public void removeStep(int index){
      stepListModel.removeElementAt(index);
      stepListSelection.setSelectionInterval(index-1,index-1);
   }
   
   public void addStep(SklStepModel step) {
      stepListModel.addElement(step);
      int n = stepListModel.size();
      // select the step just added
      stepListSelection.setSelectionInterval(n-1,n-1);
   }
   
   
   
   protected static String getAltFilename(String filename){
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
   
   
   public static String getTimestamp(){
      return (new Date()).getTime() + "";
   }
   
   public static String saveImage(BufferedImage img, String filename, String bundlePath){
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

}


@Root
class SklDocument {
   
   @ElementList
   ArrayList<SklStepModel> steps = new ArrayList<SklStepModel>();
  
   SklDocument(){
   }

}
