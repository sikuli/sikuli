package org.sikuli.guide;

import java.awt.Color;
import java.awt.Component;
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
public class SklStepModel extends DefaultSklObjectModel {
   
   @ElementList
   ArrayList<SklObjectModel> sklModelList = new ArrayList<SklObjectModel>();
   
   @ElementList
   ArrayList<SklRelationship> sklRelationshipList = new ArrayList<SklRelationship>();

   @Element
   SklAnimationManager sklAnimationManager = new SklAnimationManager();

   @Element(required = false)
   private SklImageModel referenceImage;

   @Commit
   public void build(){
      // link every component to this step
      // this may not be necessary
      for (SklObjectModel model : sklModelList){
         //model.setStep(this);
      }
   }

   
   @Override
   public Object clone() throws CloneNotSupportedException{
      SklStepModel clonedStepModel = (SklStepModel) super.clone();
      
      // mapping original model to cloned model
      Map<SklObjectModel, SklObjectModel> map = new HashMap<SklObjectModel, SklObjectModel>();
      
      // Cloning models
      clonedStepModel.sklModelList = new ArrayList<SklObjectModel>(sklModelList.size());
      for(SklObjectModel model: sklModelList){ 

         // TODO: this seems work but looks ugly. Must be a better way
         SklObjectModel clonedModel =  (SklObjectModel) ((DefaultSklObjectModel) model).clone();
         
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
   
   
   public ArrayList<SklObjectModel> getModels(){
      return sklModelList;
   } 

   public void addComponent(DefaultSklObjectModel sklComponent) {
//      sklComponent.setStep(this);
//      sklComponentList.add(sklComponent);      
   }
   
   PropertyChangeListener modelPropertyChangeListener = new PropertyChangeListener(){

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
         fireDataContentsChanged();
      }
      
   };
   
   public void addModel(SklObjectModel sklModel) {
      sklModelList.add(sklModel);      
      sklModel.addPropertyChangeListener(modelPropertyChangeListener);      
      fireDataContentsChanged();   
   }

   public void removeModel(SklObjectModel sklModel){
      sklModel.removePropertyChangeListener(modelPropertyChangeListener);
      sklModelList.remove(sklModel);
   }

   public void removeComponent(DefaultSklObjectModel sklComponent){
//      sklComponent.setStep(null);
//      sklComponentList.remove(sklComponent);
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

   public void updateRelationships(SklObjectModel model){
      for (SklRelationship relationship : sklRelationshipList){
         if (model == relationship.getParent()){
            relationship.updateDependent();
            updateRelationships(relationship.getDependent());
         }else if (model == relationship.getDependent()){
            relationship.updateDependent();
         }
      }
   }

   public void updateDependentViews(SklObjectModel model){
      for (SklRelationship relationship : sklRelationshipList){
         if (model == relationship.getParent()){
            // TODO: FIXTHIS
            //relationship.dependent.updateView();
         }
      }
   }

   public void startTracking(SikuliGuide g){
      for (SklObjectModel model : getModels()){
         if (model instanceof SklAnchorModel){
            
            SklAnchorModel anchor = (SklAnchorModel) model;
            
            // set up the pattern image
            BufferedImage image = referenceImage.getImage();
            BufferedImage patternImage = image.getSubimage(anchor.getX(), anchor.getY(), anchor.getWidth(), anchor.getHeight());            
            Pattern pattern = new Pattern(patternImage);
            
            
            // 
//            SklTracker tracker = new SklTracker(pattern);
//            tracker.setAnchor(anchor);
//            tracker.start();
            
             SklWaitClicker c = new SklWaitClicker(pattern);
             c.setAnchor(anchor);
             c.start();

             g.addTransition(c);
            
         }
      }
   }

   public void startAnimation(){
      sklAnimationManager.start();
   }

   public void setReferenceImage(SklImageModel referenceImage) {
      this.referenceImage = referenceImage;
   }
   
   public SklImageModel getReferenceImage() {
      return referenceImage;
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

      SklImageView imageView = (SklImageView) SklViewFactory.createView(stepModel.getReferenceImage());
      add(imageView,0);

      for (SklObjectModel model : stepModel.getModels()){
         SklObjectView view = SklViewFactory.createView(model);
         add(view,0);
      }      
      //  stepModel.startAnimation();   
   }
}

interface EditorSelectionEventListener extends EventListener{
   void valueChanged(EditorSelectionEvent e);
}

class EditorSelectionEvent {      
   Object source;
   Object selected;
   
   EditorSelectionEvent(Object source, Object selected){
      this.source = selected;
      this.selected = selected;
   }
   
   Object getSelected(){
      return selected;
   }
}



class SklStepPreview extends JPanel {

   UndoableEditSupport undoableEditSupport = new UndoableEditSupport(this);

   public void addUndoableEditListener(
         UndoableEditListener undoableEditListener) {
      undoableEditSupport.addUndoableEditListener(undoableEditListener);
   }

   public void removeUndoableEditListener(
         UndoableEditListener undoableEditListener) {
      undoableEditSupport.removeUndoableEditListener(undoableEditListener);
   }
   
   EventListenerList listenerList = new EventListenerList();
   public void addSelectionListener(EditorSelectionEventListener l) {
      listenerList.add(EditorSelectionEventListener.class, l);
   }

   public void removeSelectionListener(EditorSelectionEventListener l) {
      listenerList.remove(EditorSelectionEventListener.class, l);
   }
   
   protected void fireSelectionValueChanged(Object selected) {
      // Guaranteed to return a non-null array
      Object[] listeners = listenerList.getListenerList();
      // Process the listeners last to first, notifying
      // those that are interested in this event
      for (int i = listeners.length-2; i>=0; i-=2) {
         EditorSelectionEvent editorEvent = null;
          if (listeners[i]==EditorSelectionEventListener.class) {
              // Lazily create the event:
              if (editorEvent == null)
                 editorEvent = new EditorSelectionEvent(this, selected);
              ((EditorSelectionEventListener)listeners[i+1]).valueChanged(editorEvent);
          }
      }
  }
  
   

   class AddComponentAtLocationAction extends AddComponentAction {

      AddComponentAtLocationAction(SklObjectModel model, Point location){
         super(model);
         
         //model.createView();  

         // calculate the upper-left corner of the component that will
         // enable the component to be centered on the given location
         
         // TODO: FIX THIS
         Point o = location;
//         o.x -= model.getSize().width/2;
//         o.y -= model.getSize().height/2;

         model.setX(o.x);
         model.setY(o.y);

      }
   }
   
   

   
   

   // Variables that manage selection
   // TODO: replace with ListSelectionModel
   SklObjectModel selectedModel;
   SklControlBox box = new SklControlBox(null);
   SklControlBoxView boxView = null;
   SklRelationship relationship = new SklRelationship(null,box); ;
   class SelectAction extends AbstractAction {

      SklObjectModel model;
      SklRectangleModel rect;      

      public SelectAction(SklObjectModel model){       
         this.model = model;         
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         
         if (selectedModel != null)
            selectedModel.setSelected(false);         
         
         selectedModel = model;
         selectedModel.setSelected(true);
         
         fireSelectionValueChanged(selectedModel);

         // Set the control box         
         box.setTarget(model);
         boxView.setVisible(true);
      }
   }

   class DeselectAction extends AbstractAction {
      @Override
      public void actionPerformed(ActionEvent e) {         
         if (selectedModel != null)
            selectedModel.setSelected(false);
         selectedModel = null;         
         boxView.setVisible(false);
      }
   }

   class LinkAction extends AbstractAction {

      SklObjectModel parent;
      SklObjectModel dependent;

      public LinkAction(SklObjectModel parent, SklObjectModel dependent){
         this.parent = parent;
         this.dependent = dependent;
      }

      @Override
      public void actionPerformed(ActionEvent e) {

         int offsetx = dependent.getX() - parent.getX();
         int offsety = dependent.getY() - parent.getY();

         SklRelationship relationship = new SklOffsetRelationship(parent,dependent,offsetx,offsety);
         stepModel.addRelationship(relationship);

         undoableEditSupport.postEdit(new UndoableLink(relationship));      
      }


      private class UndoableLink extends AbstractUndoableEdit {
         SklRelationship linkRelationship;

         public UndoableLink(SklRelationship linkRelationship) {
            this.linkRelationship = linkRelationship;
         }

         public String getPresentationName() {
            return "link ";
         }

         public void redo() throws CannotRedoException {
            super.redo();
            stepModel.addRelationship(linkRelationship);
         }

         public void undo() throws CannotUndoException {
            super.undo();
            stepModel.removeRelationship(linkRelationship);
         }
      }
   }

   class AddComponentAction extends AbstractAction {

      SklObjectModel model;
      SklObjectView view;
      public AddComponentAction(SklObjectModel model){
         this.model = model;
      }

      @Override
      public void actionPerformed(ActionEvent e) {

         view = SklViewFactory.createView(model);
         
         cm.registerComponent(view);

         view.addMouseListener(new SklComponentSelectAdapter());

         stepModel.addModel(model);
         add(view,0);

         undoableEditSupport.postEdit(new UndoableAdd());      
      }

      private class UndoableAdd extends AbstractUndoableEdit {

         public UndoableAdd() {
         }

         public String getPresentationName() {
            return "step ";
         }

         public void redo() throws CannotRedoException {
            super.redo();
            cm.registerComponent(view);
            stepModel.addModel(model);
            // TODO: added back to the same z order
            add(view,0);
            repaint();        
         }

         public void undo() throws CannotUndoException {
            super.undo();
            cm.deregisterComponent(view);
            stepModel.removeModel(model);
            remove(view);
            repaint();
         }
      }
   }

//   public void testAddText(){
//
//      DefaultSklTextModel textModel = new DefaultSklTextModel("Text");
//      textModel.setHasShadow(true);      
//      (new AddComponentAction(textModel)).actionPerformed(null);
//
//      textModel.setText("New Text");
//      textModel.updateView();
//
//      undoableEditSupport.postEdit(new UndoableTextEdit(textModel, "Text", "New Text"));      
//
//      textModel.setLocation(100,100);
//      textModel.updateView();
//
//      undoableEditSupport.postEdit(new UndoableMove(textModel, new Point(0,0), new Point(100,100)));     
//
//      SklRectangleModel rectModel = new SklRectangleModel(new Rectangle(10,10,100,100));
//      rectModel.setHasShadow(true);
//
//      (new AddComponentAction(rectModel)).actionPerformed(null);
//
//
//      Rectangle oldBounds = rectModel.getBounds();
//      Rectangle newBounds = new Rectangle(50,50,400,400);
//
//      rectModel.setBounds(newBounds);
//      rectModel.updateView();
//
//      undoableEditSupport.postEdit(new UndoableBoundsChange(rectModel, oldBounds, newBounds));
//   }

   class UndoableTextEdit extends AbstractUndoableEdit {
      DefaultSklTextModel textModel;
      String oldText;
      String newText;

      public UndoableTextEdit(DefaultSklTextModel textModel, String oldText, String newText) {
         this.textModel = textModel;
         this.oldText = oldText;
         this.newText = newText;
      }

      public String getPresentationName() {
         return "Edit text";
      }

      public void redo() throws CannotRedoException {
         super.redo();
         textModel.setText(newText);
         textModel.updateView();
      }

      public void undo() throws CannotUndoException {
         super.undo();
         textModel.setText(oldText);
         textModel.updateView();
      }
   }

   class UndoableBoundsChange extends AbstractUndoableEdit {
      DefaultSklObjectModel model;
      Rectangle oldBounds;
      Rectangle newBounds;

      public UndoableBoundsChange(DefaultSklObjectModel model, Rectangle oldBounds, Rectangle newBounds) {
         this.model = model;
         this.oldBounds = oldBounds;
         this.newBounds = newBounds;
      }

      public String getPresentationName() {
         return "Move";
      }

      public void redo() throws CannotRedoException {
         super.redo();
         model.setBounds(newBounds);
         model.updateView();
      }

      public void undo() throws CannotUndoException {
         super.undo();
         model.setBounds(oldBounds);
         model.updateView();
      }
   }


   class UndoableMove extends AbstractUndoableEdit {
      SklObjectModel model;
      Point oldLocation;
      Point newLocation;

      public UndoableMove(SklObjectModel model, Point oldLocation, Point newLocation) {
         this.model = model;
         this.oldLocation = oldLocation;
         this.newLocation = newLocation;
      }

      public String getPresentationName() {
         return "Move";
      }

      public void redo() throws CannotRedoException {
         super.redo();
         model.setX(newLocation.x);
         model.setY(newLocation.y);
      }

      public void undo() throws CannotUndoException {
         super.undo();
         model.setX(oldLocation.x);
         model.setY(oldLocation.y);
      }
   }



   SklStepModel stepModel;
   SklStepPreview(SklStepModel step){

      setLayout(null);
      setOpaque(true);
      setBackground(Color.black);

      

      cm = new ComponentDragMover();
      cm.addMoveListener(new DraggedMoveListener(){

         @Override
         public void componentMoved(Component source, Point origin,
               Point destination) {

            Debug.info("Dragged from " + origin + " to " + destination);

            SklObjectView view = (SklObjectView) source;
            undoableEditSupport.postEdit(new UndoableMove(view.getModel(), origin, destination));
//

            Point newLocation = view.getActualLocation();
            view.getModel().setX(newLocation.x);
            view.getModel().setY(newLocation.y);
            
//            view.updateModelLocation(view.getActualLocation());
//            view.getModel().updateView();

         }

      });
      
      setModel(step);   
   }
   
   

   void setModel(SklStepModel step) {
      setStep(step);
   }

   ComponentDragMover cm;
   public void setStep(SklStepModel step){
      this.stepModel = step;      
      removeAll();
      
      if (step == null)
         return;
//
      for (SklObjectModel model : step.getModels()){
                  
         SklObjectView view = SklViewFactory.createView(model);         
         view.addMouseListener(new SklComponentSelectAdapter());
         cm.registerComponent(view);
         add(view,0);

      }

      if (step.getReferenceImage() != null){      
         SklObjectView view = SklViewFactory.createView(step.getReferenceImage());
         add(view);
      }

      boxView = (SklControlBoxView) SklViewFactory.createView(box);
      boxView.setVisible(false);
      add(boxView,0);

      
      //
      this.addMouseListener(new SklStepEditorMouseAdapter());
//
//      Rectangle r = new Rectangle(0,0,0,0);      
//      for (DefaultSklObjectModel sklComp : step.sklComponentList){
//         r.add(sklComp.getBounds());
//      }
//      //r.grow(10,10);
//      //setBounds(0,0,1000,500);
//      setBounds(100,100,1000,500);
//      setSize(step.getReferenceImage().getSize());
//
//      //      step.startTracking();
//      //      step.startAnimation();

   }
   
   
   class SklComponentSelectAdapter extends MouseAdapter{

      @Override
      public void mousePressed(MouseEvent e) {            
         SklObjectView view = (SklObjectView) e.getSource();

         (new SelectAction(view.getModel())).actionPerformed(null);
      }
   }   

   class SklStepEditorMouseAdapter extends MouseAdapter{


      @Override
      public void mousePressed(MouseEvent e) {

         Point p = e.getPoint();
         if (e.getClickCount() == 2){

            DefaultSklTextModel textModel = new DefaultSklTextModel("Text");
            textModel.setHasShadow(true);           

            (new AddComponentAtLocationAction(textModel, p)).actionPerformed(null);
            (new SelectAction(textModel)).actionPerformed(null);

         } else {

            (new DeselectAction()).actionPerformed(null);
         }

      }
   }

}
