package org.sikuli.guide;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEditSupport;

import org.sikuli.script.Debug;
import org.sikuli.script.Pattern;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;



interface StepDataEventListener extends EventListener {   
   void contentsChanged(Object source);
}

@Root
public class SklStepModel implements Selectable, Cloneable{
   
   @ElementList
   ArrayList<SklModel> sklModelList = new ArrayList<SklModel>();
   
   @ElementList
   ArrayList<SklRelationship> sklRelationshipList = new ArrayList<SklRelationship>();

   @Element
   SklAnimationManager sklAnimationManager = new SklAnimationManager();

   @Element(required = false)
   private SklImageModel referenceImage;

   @Override
   public Object clone() throws CloneNotSupportedException{
      SklStepModel clonedStepModel = (SklStepModel) super.clone();
      
      // mapping original model to cloned model
      Map<SklModel, SklModel> map = new HashMap<SklModel, SklModel>();
      
      // Cloning models
      clonedStepModel.sklModelList = new ArrayList<SklModel>(sklModelList.size());
      for(SklModel model: sklModelList){ 

         // TODO: this seems work but looks ugly. Must be a better way
         SklModel clonedModel =  (SklModel) ((DefaultSklObjectModel) model).clone();
         
         clonedStepModel.sklModelList.add(clonedModel);         
         map.put(model, clonedModel);
      }

      // Cloning relationships
      clonedStepModel.sklRelationshipList = new ArrayList<SklRelationship>(sklRelationshipList.size());
      for(SklRelationship relationship : sklRelationshipList){  
       
         SklRelationship clonedRelationship = (SklRelationship) relationship.clone();         
         clonedRelationship.setParent(map.get(relationship.getParent()));
         clonedRelationship.setDependent(map.get(relationship.getDependent()));
         
         clonedStepModel.sklRelationshipList.add(clonedRelationship);
      }
      

      return clonedStepModel;
   }
   
   EventListenerList listenerList = new EventListenerList();
   public void addDataEventListener(StepDataEventListener l) {
      listenerList.add(StepDataEventListener.class, l);
   }

   public void removeDataEventListener(StepDataEventListener l) {
      listenerList.remove(StepDataEventListener.class, l);
   }
   
   protected void fireDataContentsChanged() {
      Object[] listeners = listenerList.getListenerList();
      for (int i = listeners.length-2; i>=0; i-=2) {
          if (listeners[i]==StepDataEventListener.class) {
              ((StepDataEventListener)listeners[i+1]).contentsChanged(this);
          }
      }
  }
   
   
   public ArrayList<SklModel> getModels(){
      return sklModelList;
   } 
   
   PropertyChangeListener modelPropertyChangeListener = new PropertyChangeListener(){
      @Override
      public void propertyChange(PropertyChangeEvent evt) {
         fireDataContentsChanged();
      }      
   };
   
   public void addModel(SklModel sklModel) {
      sklModelList.add(sklModel);      
      sklModel.addPropertyChangeListener(modelPropertyChangeListener);      
      fireDataContentsChanged();   
   }

   public void removeModel(SklModel sklModel){
      sklModel.removePropertyChangeListener(modelPropertyChangeListener);
      sklModelList.remove(sklModel);
   }

   public void addRelationship(SklRelationship relationship) {
      sklRelationshipList.add(relationship);      
   }

   public void removeRelationship(SklRelationship relationship) {
      sklRelationshipList.remove(relationship);      
   }


   public void addAnimation(SklAnimation anim){
      sklAnimationManager.add(anim);
   }

   public void startTracking(SikuliGuide g){
      
      
      SklVisibilityCheckerGroup group = new SklVisibilityCheckerGroup();
      g.addTransition(group);
      
      for (SklModel model : getModels()){
         if (model instanceof SklAnchorModel){
            
            SklAnchorModel anchor = (SklAnchorModel) model;

            // set up the pattern image
            BufferedImage image = referenceImage.getImage();
            BufferedImage patternImage = image.getSubimage(anchor.getX(), anchor.getY(), anchor.getWidth(), anchor.getHeight());            
            Pattern pattern = new Pattern(patternImage);

            SklTracker tracker = new SklTracker(pattern);
            tracker.start();

            
            if (anchor.getCommand() == SklAnchorModel.ASSERT_COMMAND){
               SklVisibilityChecker c = new SklVisibilityChecker(group, anchor);
               anchor.setOpacity(0.5f);
               tracker._listener = c;
            }else if (anchor.getCommand() == SklAnchorModel.CLICK_COMMAND){
               SklClicker c = new SklClicker(anchor);
               tracker._listener = c;
               g.addTransition(c);

            }

         }
      }
   }

   public void startAnimation(){
      sklAnimationManager.start();
   }

   public void setReferenceImageModel(SklImageModel referenceImage) {
      this.referenceImage = referenceImage;
   }
   
   public SklImageModel getReferenceImageModel() {
      return referenceImage;
   }
   
   private boolean _selected = false;

   @Override
   public boolean isSelected() {
      return _selected;
   }

   @Override
   public void setSelected(boolean selected) {
      _selected = selected;
   }
}

class SklStepSimpleViewer extends JPanel {

   SklStepModel stepModel;   
   SklStepSimpleViewer(SklStepModel stepModel){

      setLayout(null);
      setOpaque(true);

      setModel(stepModel);    
   }

   public void setModel(SklStepModel stepModel){
      this.stepModel = stepModel;

      removeAll();

      if (stepModel.getReferenceImageModel() != null){
         SklView imageView = SklViewFactory.createView(stepModel.getReferenceImageModel());
         add(imageView,0);
      }

      for (SklModel model : stepModel.getModels()){
         SklView view = SklViewFactory.createView(model);
         add(view,0);
      }      
   }
}

//interface EditorSelectionEventListener extends EventListener{
//   void valueChanged(EditorSelectionEvent e);
//}

//class EditorSelectionEvent {      
//   Object source;
//   Object selected;
//   
//   EditorSelectionEvent(Object source, Object selected){
//      this.source = selected;
//      this.selected = selected;
//   }
//   
//   Object getSelected(){
//      return selected;
//   }
//}
//
//


