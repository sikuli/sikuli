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
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEditSupport;

import org.sikuli.guide.SklSideRelationship.Side;
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
   SklModel _selectedModel;
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
         
         _selectedModel = model;
         
         // Set the control box         
         box.setTarget(model);
         boxView.setVisible(true);
      }
   }

   class DeselectAction extends AbstractAction {
      @Override
      public void actionPerformed(ActionEvent e) {         
         _selectedModel = null;         
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
         _stepModel.addRelationship(relationship);

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
            _stepModel.addRelationship(linkRelationship);
         }

         public void undo() throws CannotUndoException {
            super.undo();
            _stepModel.removeRelationship(linkRelationship);
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

         addModelView(model);
                  
         _stepModel.addModel(model);
         
         if (model instanceof SklAnchorModel){

            SklTextModel text = new DefaultSklTextModel("When I click");
            text.setHasShadow(true);
            
            addModelView(text);
            
            _stepModel.addModel(text);
            _stepModel.addRelationship(new SklSideRelationship(model, text, Side.TOP));
         }
         
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
            _stepModel.addModel(model);
            // TODO: added back to the same z order
            add(view,0);
            repaint();        
         }

         public void undo() throws CannotUndoException {
            super.undo();
            cm.deregisterComponent(view);
            _stepModel.removeModel(model);
            remove(view);
            repaint();
         }
      }
   }
   
   
   void addModelView(SklModel model){
      SklView view = SklViewFactory.createView(model);
      addInteractivity(view);      
      add(view,0);      
   }
   
   void addInteractivity(SklView view){
      
      // this allows view to be moved by users
      cm.registerComponent(view);
      
      // this allows component to be selected by click
      view.addMouseListener(new SklComponentSelectAdapter());

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



   SklStepModel _stepModel;
   SklStepEditView(SklStepModel step){

      setLayout(null);
      setOpaque(true);
      setBackground(Color.black);

      

      cm = new ComponentDragMover();
      cm.addMoveListener(new DraggedMoveListener(){

         @Override
         public void componentMoved(Component source, Point origin,
               Point destination) {

            SklView view = (SklView) source;
            undoableEditSupport.postEdit(new UndoableMove(view.getModel(), origin, destination));

            Point newLocation = view.getActualLocation();
            view.getModel().setX(newLocation.x);
            view.getModel().setY(newLocation.y);
            
         }

      });
      
      setModel(step);
      
      
      addKeyListener(this);
      setFocusable(true);
      
   }
   
   


   ComponentDragMover cm;
   void setModel(SklStepModel step) {
      
      _stepModel = step;      
      
      removeAll();
      
      if (_stepModel == null)
         return;
//
      for (SklModel model : _stepModel.getModels()){
                  
         SklView view = SklViewFactory.createView(model);         
         view.addMouseListener(new SklComponentSelectAdapter());
         cm.registerComponent(view);
         add(view,0);

      }

      if (step.getReferenceImageModel() != null){      
         SklView view = SklViewFactory.createView(step.getReferenceImageModel());
         add(view);
         setPreferredSize(view.getSize());
      }

      boxView = (SklControlBoxView) SklViewFactory.createView(box);
      boxView.setVisible(false);
      add(boxView,0);
      
      this.addMouseListener(new SklStepEditorMouseAdapter());
   }
   
   SklTextPropertyEditor te;
   
   class SklComponentSelectAdapter extends MouseAdapter{

      @Override
      public void mousePressed(MouseEvent e) {            
         SklStepEditView.this.requestFocus();
         
         SklView view = (SklView) e.getSource();
         //Debug.info("[SklStepEditView] component clicked: " + view);

         (new SelectAction(view.getModel())).actionPerformed(null);
         
         if (e.getClickCount() == 2){
            //Debug.info("[SklStepEditView] component double-clicked: " + view);

            (new SelectAction(view.getModel())).actionPerformed(null);
            
            if (te == null){
               te = new SklTextPropertyEditor();
            }
            

            if (view.getModel() instanceof SklTextModel){
               remove(te);
               add(te);

               te.setTextModel((SklTextModel) view.getModel());               
               te.setLocation(view.getLocation().x, view.getLocation().y - te.getHeight());
               te.setVisible(true);
               te.requestFocus();
            }
            

            
            

         }
      }
   }   

   
   
   
   class SklStepEditorMouseAdapter extends MouseAdapter{


      @Override
      public void mousePressed(MouseEvent e) {
         
         //Debug.info("[SklStepEditView] component clicked: " + view);
         SklStepEditView.this.requestFocus();

         Point p = e.getPoint();
//         if (e.getClickCount() == 2){
//
//            DefaultSklTextModel textModel = new DefaultSklTextModel("Text");
//            textModel.setHasShadow(true);           
//
//            (new AddComponentAtLocationAction(textModel, p)).actionPerformed(null);
//            (new SelectAction(textModel)).actionPerformed(null);
//
//         } else {

            (new DeselectAction()).actionPerformed(null);
        // }

      }
   }
   
   
   interface UserAction{
      public void start();
      public void stop();
   }
   
   class UserAddElementToLocationAction implements UserAction {
      
      Class _class;
      UserAddElementToLocationAction(Class cs){
         _class = cs;
      }
      
      Cursor oldCursor;
      
      @Override
      public void start(){
         oldCursor = getCursor();
         Cursor cursor = new Cursor(Cursor.CROSSHAIR_CURSOR);
         setCursor(cursor);         
         addMouseListener(_locationSelector);
      }
      
      @Override
      public void stop(){
         removeMouseListener(_locationSelector);
         setCursor(oldCursor);
      }
      
      MouseListener _locationSelector = new  MouseAdapter(){
         @Override
         public void mousePressed(MouseEvent e) {

            if (e.getClickCount() == 1){
               locationSelected(e.getPoint());
            } 
         }
      };
      
      void locationSelected(Point p){
         SklModel newModel = null;
         if (_class == SklAnchorModel.class){
            SklAnchorModel anchorModel = new SklAnchorModel();
            anchorModel.setSize(50,50);
            newModel = anchorModel;
         }else{
            SklTextModel textModel = new DefaultSklTextModel();
            textModel.setText("When I click");
            textModel.setHasShadow(true);
            newModel = textModel;
         }
         
         (new AddComponentAtLocationAction(newModel, p)).actionPerformed(null);
         (new SelectAction(newModel)).actionPerformed(null);
         stop();
      }
      
   }

   UserAction _currentUserAction;
   
   public void doInsert(Class class_) {
      requestFocus();
      if (_currentUserAction != null){
         _currentUserAction.stop();
      }

       _currentUserAction = new UserAddElementToLocationAction(class_);
      _currentUserAction.start();

   }
   
   void doDeleteSelectedModel(){      
      _stepModel.removeModel(_selectedModel);
      setModel(_stepModel);
      repaint();
   }
   
   
   @Override
   public void keyPressed(KeyEvent k) {
      //Debug.log("[EditView] pressed: " + k.getKeyCode());
      
      if (k.getKeyCode() == KeyEvent.VK_BACK_SPACE){
         Debug.log("[EditView] pressed DELETE");
         
         doDeleteSelectedModel();
         
         
      } else if (k.getKeyCode() == KeyEvent.VK_ESCAPE){
         Debug.log("[EditView] pressed ESCAPE");
         
         (new DeselectAction()).actionPerformed(null);
         
         
         if (_currentUserAction != null){
            _currentUserAction.stop();
         }
      }

      
   }

   @Override
   public void keyReleased(KeyEvent k) {
   }

   @Override
   public void keyTyped(KeyEvent k) {
   }

}