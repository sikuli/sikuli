package org.sikuli.guide;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEditSupport;

import org.sikuli.script.Debug;

class SklStepEditView extends JPanel implements KeyListener {

   UndoableEditSupport undoableEditSupport = new UndoableEditSupport(this);

   public void addUndoableEditListener(
         UndoableEditListener undoableEditListener) {
      undoableEditSupport.addUndoableEditListener(undoableEditListener);
   }

   public void removeUndoableEditListener(
         UndoableEditListener undoableEditListener) {
      undoableEditSupport.removeUndoableEditListener(undoableEditListener);
   }
   
//   EventListenerList listenerList = new EventListenerList();
//   public void addSelectionListener(EditorSelectionEventListener l) {
//      listenerList.add(EditorSelectionEventListener.class, l);
//   }
//
//   public void removeSelectionListener(EditorSelectionEventListener l) {
//      listenerList.remove(EditorSelectionEventListener.class, l);
//   }
//   
//   protected void fireSelectionValueChanged(Object selected) {
//      // Guaranteed to return a non-null array
//      Object[] listeners = listenerList.getListenerList();
//      // Process the listeners last to first, notifying
//      // those that are interested in this event
//      for (int i = listeners.length-2; i>=0; i-=2) {
//         EditorSelectionEvent editorEvent = null;
//          if (listeners[i]==EditorSelectionEventListener.class) {
//              // Lazily create the event:
//              if (editorEvent == null)
//                 editorEvent = new EditorSelectionEvent(this, selected);
//              ((EditorSelectionEventListener)listeners[i+1]).valueChanged(editorEvent);
//          }
//      }
//  }
//  
   

   class AddComponentAtLocationAction extends AddComponentAction {

      AddComponentAtLocationAction(SklModel model, Point location){
         super(model);
         
         // calculate the upper-left corner of the component that will
         // enable the component to be centered on the given location         
         Point o = location;
         o.x -= model.getWidth()/2;
         o.y -= model.getHeight()/2;

         model.setX(o.x);
         model.setY(o.y);
      }
   }
   
   // Variables that manage selection
   // TODO: replace with ListSelectionModel
   SklModel selectedModel;
   SklControlBox box = new SklControlBox(null);
   SklControlBoxView boxView = null;
   SklRelationship relationship = new SklRelationship(null,box); ;
   class SelectAction extends AbstractAction {

      SklModel model;
      SklRectangleModel rect;      

      public SelectAction(SklModel model){       
         this.model = model;         
      }

      @Override
      public void actionPerformed(ActionEvent e) {
         
         if (selectedModel != null)
            selectedModel.setSelected(false);         
         
         selectedModel = model;
         selectedModel.setSelected(true);
         
         //fireSelectionValueChanged(selectedModel);

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

      SklModel parent;
      SklModel dependent;

      public LinkAction(SklModel parent, SklModel dependent){
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

      SklModel model;
      SklView view;
      public AddComponentAction(SklModel model){
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
      }

      public void undo() throws CannotUndoException {
         super.undo();
         textModel.setText(oldText);
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
      }

      public void undo() throws CannotUndoException {
         super.undo();
         model.setBounds(oldBounds);
      }
   }


   class UndoableMove extends AbstractUndoableEdit {
      SklModel model;
      Point oldLocation;
      Point newLocation;

      public UndoableMove(SklModel model, Point oldLocation, Point newLocation) {
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
   SklStepEditView(SklStepModel step){

      setLayout(null);
      setOpaque(true);
      setBackground(Color.black);

      

      cm = new ComponentDragMover();
      cm.addMoveListener(new DraggedMoveListener(){

         @Override
         public void componentMoved(Component source, Point origin,
               Point destination) {

            Debug.info("Dragged from " + origin + " to " + destination);

            SklView view = (SklView) source;
            undoableEditSupport.postEdit(new UndoableMove(view.getModel(), origin, destination));
//

            Point newLocation = view.getActualLocation();
            view.getModel().setX(newLocation.x);
            view.getModel().setY(newLocation.y);
            
         }

      });
      
      setModel(step);
      
      
      addKeyListener(this);
      setFocusable(true);
      
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
      for (SklModel model : step.getModels()){
                  
         SklView view = SklViewFactory.createView(model);         
         view.addMouseListener(new SklComponentSelectAdapter());
         cm.registerComponent(view);
         add(view,0);

      }

      if (step.getReferenceImageModel() != null){      
         SklView view = SklViewFactory.createView(step.getReferenceImageModel());
         add(view);
         setPreferredSize(view.getSize());
         //setSize(view.getSize());
         
//         view.addKeyListener(this);
//         view.setFocusable(true);

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
         SklStepEditView.this.requestFocus();
         
         SklView view = (SklView) e.getSource();
         Debug.info("[SklStepEditView] component clicked: " + view);

         (new SelectAction(view.getModel())).actionPerformed(null);
      }
   }   

   
   
   
   class SklStepEditorMouseAdapter extends MouseAdapter{


      @Override
      public void mousePressed(MouseEvent e) {
         
         //Debug.info("[SklStepEditView] component clicked: " + view);
         SklStepEditView.this.requestFocus();

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

   public void doInsertAnchor() {
      final Cursor oldCursor = getCursor();
      Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
      setCursor(cursor);
      
      
      addMouseListener(new MouseAdapter(){

         @Override
         public void mousePressed(MouseEvent e) {

            if (e.getClickCount() == 1){

               Point p = e.getPoint();

               SklAnchorModel anchor = new SklAnchorModel();
               anchor.setSize(50,50);
               (new AddComponentAtLocationAction(anchor, p)).actionPerformed(null);
               (new SelectAction(anchor)).actionPerformed(null);
               
               
               SklStepEditView.this.removeMouseListener(this);
               setCursor(oldCursor);
            } 
         }
      });
      
   }

   @Override
   public void keyPressed(KeyEvent k) {
      Debug.log("[EditView] pressed: " + k.getKeyCode());
      
      if (k.getKeyCode() == KeyEvent.VK_BACK_SPACE){
         Debug.log("[EditView] pressed DELETE");
         
         
         
      } else if (k.getKeyCode() == KeyEvent.VK_ESCAPE){
         Debug.log("[EditView] pressed ESCAPE");
         
         (new DeselectAction()).actionPerformed(null);
      }

      
   }

   @Override
   public void keyReleased(KeyEvent k) {
   }

   @Override
   public void keyTyped(KeyEvent k) {
   }

}