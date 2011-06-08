package org.sikuli.guide;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.sikuli.script.Debug;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.CycleStrategy;
import org.simpleframework.xml.strategy.Strategy;

@Root
public class SklDocument {
   
   static public final String PROPERTY_BUNDLEPATH = "bundlepath";
   static public final String PROPERTY_MODIFIED = "modified";
   
   protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
   
   public void addPropertyChangeListener( PropertyChangeListener listener ){
      this.pcs.addPropertyChangeListener( listener );
   }

   public void removePropertyChangeListener( PropertyChangeListener listener ){
      this.pcs.removePropertyChangeListener( listener );
   }

   private File _bundlePath = null;
   void setBundlePath(File bundlePath){
      this.pcs.firePropertyChange(PROPERTY_BUNDLEPATH, _bundlePath, _bundlePath = bundlePath);
   }
   
   File getBundlePath(){
      return _bundlePath;
   }   
   
   @ElementList
   ArrayList<SklStepModel> _steps = new ArrayList<SklStepModel>();

   
   private class StepListener implements StepDataEventListener{
      @Override
      public void contentsChanged(Object source) {
         //Debug.info("[Document] model content changed");
         setModified(true);
      }
   }
   
   StepListener _stepListener = new StepListener();
      
   
   private void addListenersToStep(SklStepModel step){
      step.addDataEventListener(_stepListener);
   }
   
   
   @Commit
   void build(){
      for (SklStepModel step : _steps){
         addListenersToStep(step);
      }
      
   }
   
   private boolean _modified = false;
   
   
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
   
   private class ListModelAdapter implements ListModel {
   
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

      void notifyListenersIntervalAdded(int index0, int index1) {
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
   
   public int size(){
      return _steps.size();
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
      _listModelAdapter.notifyListenersIntervalAdded(index, index);
     
      addListenersToStep(stepModel);
      setModified(true);
    }
    
    public void addStep(SklStepModel stepModel) {
       addStep(_steps.size(), stepModel);
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
       // call this to ensure the selected item is visible
       _listModelAdapter.notifyListenersContentChanged();
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
   
   //String _bundlePath = null;
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
         String storyScript = "";
         
         for (SklStepModel stepModel : doc.getSteps()  ){
            String stepScript = generateStep(stepModel,bundle);
            
            storyScript += stepScript;
            storyScript += "wait(3) \n";
         }
         return storyScript;
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
   
   void save(){
      if (_bundlePath != null)
         saveAs(_bundlePath);
   }
   
   void saveAs(File destBundle){
      if( !destBundle.exists() )
         destBundle.mkdir();
      
      setBundlePath(destBundle);
      
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
      
      
      // save success
      setModified(false);
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
         
         doc.setBundlePath(bundlefile);

         return doc;
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }      
   }

   public void setModified(boolean modified) {
      this.pcs.firePropertyChange(PROPERTY_MODIFIED, _modified, _modified = modified);
   }

   public boolean isModified() {
      return _modified;
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
