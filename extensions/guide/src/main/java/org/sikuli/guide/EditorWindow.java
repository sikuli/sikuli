package org.sikuli.guide;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;

import org.sikuli.guide.util.ComponentMover;
import org.sikuli.script.Debug;
import org.sikuli.script.FindFailed;
import org.sikuli.script.Finder;
import org.sikuli.script.OpenCV;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.natives.FindResult;
import org.sikuli.script.natives.FindResults;
import org.sikuli.script.natives.Mat;
import org.sikuli.script.natives.Vision;

interface RectangleSelectionListener {      
   void rectangleSelectionCompleted(Rectangle rectangle);
   void rectangleSelectionStarted();
   void rectangleSelectionUpdated(Rectangle rectangle);
}

public class EditorWindow extends JFrame 
implements MouseListener, KeyListener, 
Transition, GlobalMouseMotionListener, MouseMotionListener {


   public final static double SCALE = 1.0f;

   BufferedImage image;
   RectangleSelector rectangleSelector;
   PointSelector pointSelector;
   LocationSelector locationSelector;
   BackgroundImage backgroundImage;
   Toolbar toolbar;
   JPanel palletePanel;   
   private OverviewWindow overview;  

   interface PointSelectionListener {
      void pointSelectionCompleted(PointSelectionEvent e);
      void pointSelectionStarted(PointSelectionEvent e);
      void pointSelectionUpdated(PointSelectionEvent e);   
   }

   public class PointSelectionEvent {
      public PointSelector selector;
      public ArrayList<Point> selectedPoints;
   }

   class Toolbar extends JWindow {


      class ToolbarButton extends JButton implements ActionListener{

         ToolbarButton(String text, String command){            
            super(text);
            setActionCommand(command);
            setFocusable(false);
            addActionListener(this);
         }

         @Override
         public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand() == "Anchor"){
               doAddAnchor();
            }else if (e.getActionCommand() == "Select"){
               doSelect();
            }else if (e.getActionCommand() == "Text"){
               doAddText();
            }else if (e.getActionCommand() == "Connect"){
               doLinkComponentToAnchor();
            }else if (e.getActionCommand() == "Callout"){
               doAddCallout();
            }else if (e.getActionCommand() == "Flag"){
               doAddFlag();               
            }else if (e.getActionCommand() == "Circle"){
               doAddCircle();
            }else if (e.getActionCommand() == "Bracket"){
               doAddBracket();
            }else if (e.getActionCommand() == "Run"){
               doRun();
            }else if (e.getActionCommand() == "RunAll"){
               doRunAll();
            }

         }

      }

      public Toolbar(Frame owner){
         super(owner);

         setBackground(Color.red);
         //setOpaque(true);

         ComponentMover cm = new ComponentMover();
         cm.registerComponent(this);

         JPanel p = new JPanel();
         p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
         p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));

         //         p.add(new ToolbarButton("Select","Select"));                  

         p.add(new ToolbarButton("Anchor","Anchor"));
         p.add(new ToolbarButton("Text","Text"));
         //         p.add(new ToolbarButton("Callout","Callout"));
         //         p.add(new ToolbarButton("Flag","Flag"));
         //         p.add(new ToolbarButton("Circle","Circle"));
         //         p.add(new ToolbarButton("Bracket","Bracket"));

         p.add(new ToolbarButton("Play","Run"));
         p.add(new ToolbarButton("Play All","RunAll"));

         p.setSize(p.getPreferredSize());

         //p.setBackground(new Color(255,245,238,100));
         Color bg = new Color(119,136,153);
         p.setBackground(bg);
         p.setBorder(BorderFactory.createLineBorder(Color.black));

         add(p);

         setSize(p.getSize());      
         //setMovable(true);
      }      
   }

   class PointSelector extends JComponent implements
   MouseMotionListener, MouseListener{

      ArrayList<Point> selectedPoints = new ArrayList<Point>();
      ArrayList<PointSelectionListener> listeners = new  ArrayList<PointSelectionListener>();

      int numPointsToSelect = 1; 
      public PointSelector(Rectangle bounds){
         setBounds(bounds);
         addMouseListener(this);
         addMouseMotionListener(this);
         setVisible(false);
      }

      public void start(int numPointsToSelect){
         selectedPoints.clear();
         this.numPointsToSelect = numPointsToSelect;   
         setVisible(true);         
      }
      public void stop() {
         setVisible(false);  
      }


      @Override
      public void mouseDragged(MouseEvent arg0) {
      }

      @Override
      public void mouseMoved(MouseEvent arg0) {
      }

      @Override
      public void mouseClicked(MouseEvent arg0) {
      }

      @Override
      public void mouseEntered(MouseEvent arg0) {
      }

      @Override
      public void mouseExited(MouseEvent arg0) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
         Debug.info("selector pressed at: " + e.getX() + "," + e.getY());     
         Point selectedPoint = e.getPoint();         
         if (selectedPoints.size() < numPointsToSelect){

            selectedPoints.add(selectedPoint);

            for (PointSelectionListener listener : listeners){
               PointSelectionEvent pe = new PointSelectionEvent();
               pe.selectedPoints = selectedPoints;
               pe.selector = this;
               listener.pointSelectionUpdated(pe);
            }


            if (selectedPoints.size() == numPointsToSelect){

               for (PointSelectionListener listener : listeners){
                  PointSelectionEvent pe = new PointSelectionEvent();
                  pe.selectedPoints = selectedPoints;
                  pe.selector = this;
                  listener.pointSelectionCompleted(pe);
               }
            }

         }
      }

      @Override
      public void mouseReleased(MouseEvent arg0) {
      }

      public void addListener(PointSelectionListener listener){
         listeners.add(listener);
      }

   }




   class Button extends JButton implements ActionListener{

      public Button(String label){
         super(label);
         addActionListener(this);
         setOpaque(false);
      }

      @Override
      public void actionPerformed(ActionEvent arg0) {
         Debug.info("A button is pressed");

      }

   }

   class BackgroundImage extends SikuliGuideComponent {

      BufferedImage image;
      //BufferedImage darkenImage;
      double scale = (double) SCALE;
      public BackgroundImage(BufferedImage image){
         this.image = image;
         setBounds(new Rectangle(0,0,(int)(image.getWidth()*scale),(int)(image.getHeight()*scale)));
      }

      @Override
      public void paintComponent(Graphics g){
         super.paintComponent(g);         
         Graphics2D g2d = (Graphics2D) g;
         if (image != null){            
            Rectangle r = getParent().getBounds();
            //Debug.info("Image bounds: " + r);
            //g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.6f));
            g2d.drawImage(image, 0, 0, r.width, r.height, null);
         }    
      }

      public BufferedImage crop(Rectangle r) {
         // TODO Make this boundary safe
         return image.getSubimage(r.x,r.y,r.width,r.height);
      }
   }


   ArrayList<Target> clickables = new ArrayList<Target>();
   class Target extends SikuliGuideComponent {

      Color normalColor = new Color(1.0f,1.0f,0,0.1f);
      Color mouseOverColor = new Color(1.0f,0,0,0.1f);

      String name;
      Region region;
      public Target(Region region){
         this.region = region;
         this.setBounds(region.getRect());
         this.setLocation(region.x,region.y);
         this.setMovable(true);
      }

      public void setName(String name){
         this.name = name;
      }

      boolean mouseOver;
      public void setMouseOver(boolean mouseOver){
         if (this.mouseOver != mouseOver){
            repaint();
         }            
         this.mouseOver = mouseOver;

      }

      public void paintComponent(Graphics g){
         super.paintComponent(g);

         Graphics2D g2d = (Graphics2D) g;

         if (mouseOver){
            g2d.setColor(mouseOverColor);
         }else{
            g2d.setColor(normalColor);
         }

         g2d.fillRect(0,0,getWidth()-1,getHeight()-1);
         g2d.setColor(Color.white);
         g2d.drawRect(0,0,getWidth()-1,getHeight()-1);
         g2d.setColor(Color.black);
         g2d.drawRect(1,1,getWidth()-3,getHeight()-3);

      }
   }

   Point clickLocation;
   public Point getClickLocation() {
      return clickLocation;
   }

   SikuliGuide guide;
   GlobalMouseMotionTracker mouseTracker;
   Target lastClickedClickable;
   Button saveButton;


   JLayeredPane controlLayer;

   public EditorWindow(){
      //super(f);

      // this allows us to layout the components ourselves
      setLayout(null);

      // this window should cover the same area as the guide
      //setBounds(new Screen().getRect());//guide.getBounds());
      //Screen s = new Screen();
      //setBounds(0,0,(int)(s.w*SCALE),(int)(s.h*SCALE));
      //setBackground(Color.red);

      //setAlwaysOnTop(true);


      //Env.getOSUtil().setWindowOpacity(this, 0.8f);

      //Color bg = new Color(1.0f,1.0f,1.0f,1.0f);
      //Color bg = new Color(1.0f,0,0,0.1f);
      //Color bg = new Color(1.0f,0,0,0.1f);
      Color bg = new Color(119,136,153);
      //Color bg = Color.red;

      getContentPane().setBackground(bg);
      setBackground(bg);


      // This makes the JWindow transparent

      // None of these seemed to work
      //Env.getOSUtil().setWindowOpacity(this, 0.5f);
      //Env.getOSUtil().setWindowOpaque(this, false);
      // Env.getOSUtil().bringWindowToFront(this, true);


      // TODO: figure out how to make this JWindow non-draggable
      // 1) associate this window with a JFrame and make the JFrame not movable
      //      setFocusableWindowState(false);
      //      setFocusable(false);
      //      setEnabled(false);      
      //      getContentPane().setFocusable(false);
      //      getRootPane().setFocusable(false);

      // capture the click event
      //      addMouseListener(this);
      //      addMouseMotionListener(this);
      addKeyListener(this);

      rectangleSelector = new RectangleSelector();
      getContentPane().addMouseListener(rectangleSelector);
      getContentPane().addMouseMotionListener(rectangleSelector);

      locationSelector = new LocationSelector();
      getContentPane().addMouseListener(locationSelector);

      //      getGlassPane().addMouseListener(locationSelector);
      //      getGlassPane().setVisible(true);
      //rectangleSelector.addListener(this);


      getContentPane().addKeyListener(this);

      addWindowListener(new WindowAdapter(){
         public void windowClosed(WindowEvent e){
            // stop the global mouse tracker's timer thread
            mouseTracker.stop();
         }
      });

      setFocusable(true);
      getContentPane().setFocusable(true);


      palletePanel = new JPanel();
      palletePanel.setLayout(null);
      palletePanel.setBackground(null);
      palletePanel.setOpaque(false);      
      getLayeredPane().add(palletePanel, JLayeredPane.PALETTE_LAYER);

      toolbar = new Toolbar(this);
      toolbar.setLocation(10,30);
      toolbar.setVisible(true);
      toolbar.pack();
      toolbar.toFront();

      controlBox = new ControlBox();
      controlBox.setEditor(this);
      addToPaletteLayer(controlBox);      

      textPropertyEditor = new TextPropertyEditor();
      textPropertyEditor.setVisible(false);
      addToPaletteLayer(textPropertyEditor);

      overview = new OverviewWindow(this);
      overview.setLocation(10,70);
      overview.setEditor(this);
      overview.setVisible(true);
      overview.toFront();

      linkedAnchorVisualization = new RelatedAnchorVisulization();
      addToPaletteLayer(linkedAnchorVisualization);

   }

   public void addClickableRegion(Region region, String name){
      Target c = new  Target(region);
      c.setName(name);
      clickables.add(c);
      add(c);
   }

   @Override
   // notifies the owner of this click target that the target has
   // been clicked
   public void mouseClicked(MouseEvent e) {
      //Debug.log("clicked on " + e.getX() + "," + e.getY());
      Point p = e.getPoint();

      requestFocus();

      lastClickedClickable  = null;


      // find clicked clickable
      for (Target c : clickables){         
         if (c.getBounds().contains(p)){
            lastClickedClickable = c;
         }
      }         

      if (lastClickedClickable != null){

         synchronized(guide){
            guide.notify();
         }
      }
   }

   @Override
   public void mouseEntered(MouseEvent arg0) {
   }

   @Override
   public void mouseExited(MouseEvent arg0) {
   }

   @Override
   public void mousePressed(MouseEvent arg0) {
      Debug.log("pressed");
      //      setLocation(100,100);
      //      repaint();
   }

   @Override
   public void mouseReleased(MouseEvent arg0) {
      Debug.log("released");
      //      setLocation(0,0);
      //      repaint();
   }


   @Override
   public String waitForTransition() {

      //toFront();
      setVisible(true);
      //setAlwaysOnTop(true);

      //mouseTracker.start();      

      synchronized(this){
         try {
            this.wait();
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
      setVisible(false);

      if (lastClickedClickable != null)
         return lastClickedClickable.name;
      else
         return "Next";
   }

   Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
   Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
   Cursor hourglassCursor = new Cursor(Cursor.WAIT_CURSOR);
   Cursor currentCursor = null;

   @Override
   public void globalMouseMoved(int x, int y) {
      //Debug.log("moved to " + x + "," + y);

      Point p = new Point(x,y);
      for (Target c : clickables){

         c.setMouseOver(c.getBounds().contains(p));


         // TODO: figure out why setCursor is not working
         //         if (c.getBounds().contains(p)){
         //            Debug.log("inside");
         //            cursor = handCursor;
         //            this.getContentPane().setCursor(hourglassCursor);
         //            setCursor(hourglassCursor);
         //            getOwner().setCursor(ho1u2rglassCursor);
         //            c.setCursor(hourglassCursor);
         //         }


      }



   }

   //   @Override
   //   public void toFront(){
   //      if(Env.getOS() == OS.MAC){
   //         // this call is necessary to allow clicks to go through the window (ignoreMouse == true)
   //         Env.getOSUtil().bringWindowToFront(this, false);
   //      }     
   //      super.toFront();
   //
   //   }

   public void clear() {
      clickables.clear();
      getContentPane().removeAll();      
   }

   @Override
   public void globalMouseIdled(int x, int y) {
   }


   Point mouseLoc;
   @Override
   public void mouseDragged(MouseEvent e) {
      Debug.info("dragged to: " + e.getX() + "," + e.getY());
      mouseLoc = e.getPoint();
      repaint();
   }

   @Override
   public void mouseMoved(MouseEvent e) {
      Debug.info("moved to: " + e.getX() + "," + e.getY());     
      mouseLoc = e.getPoint();
      repaint();

   }


   JPanel backgroundImagePanel;

   Rectangle selectedRectangle = null;
   ArrayList<Point> selectedPoints = null;
   @Override
   public void paint(Graphics g){

      //((Graphics2D) g).scale(0.5f,0.5f);      
      super.paint(g);

      Graphics2D g2d = (Graphics2D) g;
      if (selectedRectangle != null){
         Rectangle r = selectedRectangle;
         g2d.setColor(Color.red);
         g2d.drawRect(r.x,r.y,r.width,r.height);
      }

      //      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));
      //      g2d.setColor(Color.black);
      //      g2d.fillRect(0,0,500,500);
      //      if (selectedPoints != null){
      //         
      //         for (Point p : selectedPoints){
      //            g2d.drawOval(p.x-5,p.y-5,10,10);
      //         }
      //      }

   }


   //   public void setImage(BufferedImage image) {
   //      this.image = image;
   //      backgroundImage = new BackgroundImage(image);
   //      addToDefaultLayer(backgroundImage,-1);
   //   }


   public void doSelect(){
      locationSelector.stop();

      Object action = new SelectAction();
      componentSelector.start(action);
      locationSelector.start(action);
   }

   public void doAddAnchor(){
      //rectangleSelector.start(new AddAnchorAction());
      //componentSelector.stop();
      //locationSelector.stop();
      locationSelector.start(new AddAnchorActionByClick());
   }

   public void doAddText(){
      // TODO consider reverse, such as: 
      // action = new AddTextAction(locationSelector)
      // action.start()
      componentSelector.stop(); 
      rectangleSelector.stop();
      locationSelector.start(new AddTextAction());
   }

   void doAddCallout(){
      componentSelector.stop();
      locationSelector.start(new AddCalloutAction());
   }

   void doAddFlag(){
      componentSelector.stop();
      locationSelector.start(new AddFlagAction());
   }

   void doAddCircle(){
      locationSelector.stop();
      componentSelector.start(new AddCircleToTargetAction());
   }

   void doAddBracket(){
      locationSelector.stop();
      componentSelector.start(new AddBracketToTargetAction());
   }

   public void doLinkComponentToAnchor(){
      locationSelector.stop();
      componentSelector.start(new LinkComponentToAnchorAction());
   }

   void createStepToPlay(Step step){
      step.clear();

      unselectComponent();

      Component[] components = step.getView().getComponents();

      Debug.info("" + components.length + " components");

      for (Component comp: components){
         if (comp instanceof SikuliGuideAnchor){

            Debug.info("Adding an anchor to the current step");

            SikuliGuideAnchor anchor = (SikuliGuideAnchor) comp;
            Rectangle bounds = anchor.getBounds();

            BufferedImage screenImage = step.getScreenImage();
            BufferedImage croppedImage 
            = screenImage.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);


            Part part = new Part(new Pattern(croppedImage));
            // TODO: fold this into target
            part.setTargetOrigin(new Point(anchor.getLocation()));

            for (SikuliGuideComponent annotation : anchor.getFollowers()){   

               if (annotation.isVisible() && !(annotation instanceof Connector)){
                  Debug.info("Adding " + annotation);
                  part.addComponent((SikuliGuideComponent) annotation.clone());
               }
            }

            step.addPart(part);  
         }

      }
   }

   public void doRunAll(){
      setPlayAll(true);
      for (Step step : steps){
         createStepToPlay(step);
      }

      setVisible(false);
      synchronized(this){
         this.notify();
      }
   }

   public void doRun(){
      setPlayAll(false);
      Step step = getCurrentStep();
      createStepToPlay(step);
      //      step.clear();
      //      
      //      unselectComponent();
      //      
      //      Component[] components = this.getCurrentComponents();
      //
      //      Debug.info("" + components.length + " components");
      //
      //      for (Component comp: components){
      //         if (comp instanceof SikuliGuideAnchor){
      //
      //            Debug.info("Adding an anchor to the current step");
      //
      //            SikuliGuideAnchor anchor = (SikuliGuideAnchor) comp;
      //            Rectangle bounds = anchor.getBounds();
      //
      //            BufferedImage screenImage = step.getScreenImage();
      //            BufferedImage croppedImage 
      //            = screenImage.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
      //
      //
      //            Part part = new Part(new Pattern(croppedImage));
      //            // TODO: fold this into target
      //            part.setTargetOrigin(new Point(anchor.getLocation()));
      //
      //            for (SikuliGuideComponent annotation : anchor.getFollowers()){   
      //
      //               if (annotation.isVisible() && !(annotation instanceof Connector)){
      //                  Debug.info("Adding " + annotation);
      //                  part.addComponent((SikuliGuideComponent) annotation.clone());
      //               }
      //            }
      //
      //            step.addPart(part);  
      //         }
      //
      //      }

      setVisible(false);
      synchronized(this){
         this.notify();
      }
   }


   float zoomLevel = 1.0f;
   float getZoomLevel(){
      return zoomLevel;
   }

   Dimension screenImageSize = null;
   Dimension getScreenImageSize(){
      return screenImageSize;
   }

   public void setScreenImageSize(Dimension size){
      screenImageSize = (Dimension) size.clone();
      setSize(size);
      setLocationRelativeTo(null);
   }


   void setZoomLevel(float zoomLevel){
      this.zoomLevel = zoomLevel;

      int containerWidth = getContentPane().getWidth();
      int containerHeight = getContentPane().getHeight();

      int w = getScreenImageSize().width;
      int h = getScreenImageSize().height;

      StepView view = getCurrentStep().getView();
      int w1 = (int) (w * zoomLevel);
      int h1 = (int) (h * zoomLevel);
      view.setSize(w1,h1);
      view.setLocation(containerWidth/2 - w1/2, containerHeight/2 - h1/2);
      palletePanel.setBounds(view.getBounds());


      for (Component comp : view.getComponents()){
         SikuliGuideComponent sklComp = (SikuliGuideComponent) comp;
         if (sklComp instanceof SikuliGuideAnchor || sklComp instanceof BackgroundImage)
            sklComp.setZoomLevel(zoomLevel);
      }

      //      for (Component comp : palletePanel.getComponents()){
      //         SikuliGuideComponent sklComp = (SikuliGuideComponent) comp;
      //         sklComp.setZoomLevel(zoomLevel);
      //      }

      Debug.log("Zoom done");
      repaint();
   }

   @Override
   public void keyPressed(KeyEvent k) {
      //Debug.log("pressed " + k.getKeyCode());
      if (k.isMetaDown()){



         if (k.getKeyCode() == KeyEvent.VK_UP){
            Debug.info("Zoom in");
            setZoomLevel(getZoomLevel() + 0.1f);
         } else if (k.getKeyCode() == KeyEvent.VK_DOWN) {
            setZoomLevel(getZoomLevel() - 0.1f);
         }



      }else if (k.getKeyCode() == KeyEvent.VK_1){       
         doAddAnchor();
      }else if (k.getKeyCode() == KeyEvent.VK_2){       
         doAddText();
      }else if (k.getKeyCode() == KeyEvent.VK_3){
         doLinkComponentToAnchor();
      }else if (k.getKeyCode() == KeyEvent.VK_4){
         //componentSelector.start(new EditTextPropertyAction());
      }else if (k.getKeyCode() == KeyEvent.VK_ESCAPE){         
         doRun();
      }else if (k.getKeyCode() == KeyEvent.VK_LEFT || k.getKeyCode() == KeyEvent.VK_UP){
         previousStep();                  
      }else if (k.getKeyCode() == KeyEvent.VK_RIGHT || k.getKeyCode() == KeyEvent.VK_DOWN){
         nextStep();
      }else if (k.getKeyCode() == KeyEvent.VK_BACK_SPACE){
         Debug.log("User pressed DELETE");
         deleteSelectedComponent();
      }
   }



   void deleteComponent(SikuliGuideComponent comp){
      //Step step = getCurrentStep();

      unselectComponent();

      StepView stepPanel = getCurrentStep().getView();

      comp.removeFrom(stepPanel);

      currentStepContentChanged();

      //repaint();

   }

   void deleteSelectedComponent(){
      if (selectedComponent != null){

         deleteComponent(selectedComponent);
         //componentSelector.selectedComponent.setVisible(false);
      }

   }

   private Step step;




   private void crop(Rectangle bounds) {
      // TODO Auto-generated method stub

   }

   @Override
   public void keyReleased(KeyEvent arg0) {
   }


   @Override
   public void keyTyped(KeyEvent k) {
   }


   class AddAnchorAction implements RectangleSelectionListener{ 

      @Override
      public void rectangleSelectionCompleted(Rectangle rectangle) {

         //removeMouseListener(rectangleSelector);
         //removeMouseMotionListener(rectangleSelector);

         perform(rectangle);
      }

      @Override
      public void rectangleSelectionStarted() {
      }

      @Override
      public void rectangleSelectionUpdated(Rectangle rectangle) {
         selectedRectangle = rectangle;    
         repaint();
      }

      public SikuliGuideComponent perform(Rectangle rectangle){
         // ignore too small rectangles
         if (rectangle.width < 2 && rectangle.height < 2){
            return null;
         }

         SikuliGuideAnchor anchor = new SikuliGuideAnchor(new Region(rectangle));
         anchor.addMouseListener(componentSelector);
         anchor.setEditable(true);
         anchor.setMovable(true);

         selectedRectangle = null;

         addToCurrentStep(anchor,0);
         currentStepContentChanged();

         return anchor;
      }

   }



   public void setStep(Step step) {
      this.step = step;
   }

   public Step getStep() {
      return step;
   }

   public Step getStep(int index){
      return steps.get(index);
   }



   TextPropertyEditor textPropertyEditor;



   class EditTextPropertyAction{



      public void componentSelected(SikuliGuideComponent comp) {
         perform(comp);
      }     

      public void perform(SikuliGuideComponent comp){
         Debug.info("User double-clicked on this");

         // TODO allow other types to be edited
         if (comp instanceof SikuliGuideText){

            Debug.info("User double-clicked on Text");


            textPropertyEditor.setTargetComponent(comp);
            textPropertyEditor.setVisible(true);
            textPropertyEditor.requestFocus();

            Debug.info("" +textPropertyEditor);

            controlBox.setTargetComponent(comp);
            controlBox.setVisible(true);
            //controlBox.setAutoResizeEnabled(true);
         }
      }
   }



   ControlBox controlBox;

   SikuliGuideComponent selectedComponent;

   class SelectAction {

      public void componentSelected(SikuliGuideComponent comp) {         
         controlBox.setTargetComponent(comp);
         controlBox.setVisible(true);

         selectedComponent = comp;

         // highlight the anchor of the selected component
         if (comp.getLeader() instanceof SikuliGuideAnchor){


            SikuliGuideAnchor anchor = (SikuliGuideAnchor) comp.getLeader();
            linkedAnchorVisualization.setVisible(true);
            linkedAnchorVisualization.setAnchor(anchor);

         }

         repaint();  
      }

      public void locationSelected(Point location) {

         unselectComponent();
      }

   }

   class AddBracketToTargetAction {

      public void componentSelected(SikuliGuideComponent comp) {

         Region r = new Region(comp.getBounds());
         SikuliGuideBracket bracket = new SikuliGuideBracket();


         if(r.w > r.h)
            bracket.setLocationRelativeToComponent(comp, SikuliGuideComponent.TOP);
         else
            bracket.setLocationRelativeToComponent(comp, SikuliGuideComponent.LEFT);

         comp.addFollower(bracket);

         add(bracket,0);
         repaint();         
      }
   }

   class AddCircleToTargetAction {

      public void componentSelected(SikuliGuideComponent comp) {

         SikuliGuideCircle circle = new SikuliGuideCircle();
         circle.setLocationRelativeToComponent(comp, SikuliGuideComponent.OVER);

         comp.addFollower(circle);

         add(circle,0);
         repaint();
      }
   }

   class LinkComponentToAnchorAction {

      SikuliGuideAnchor anchor;
      SikuliGuideComponent sourceComponent;

      SikuliGuideRectangle selectionVisulization;

      final int SELECTING_FIRST = 0;
      final int SELECTING_ANCHOR = 1;
      final int SELECTING_SOURCECOMPONENT = 2;

      int task =  SELECTING_FIRST;

      public void perform(SikuliGuideComponent sourceComponent,
            SikuliGuideComponent anchor){

         sourceComponent.followComponent(anchor);
         anchor.addFollower(sourceComponent);         

         Debug.info("Linking " + sourceComponent  + " to " + anchor);


         // TODO improve this visualization code
         // remove selection visuals
         if (selectionVisulization != null)
            removeFromPaletteLayer(selectionVisulization);

         //         Component[] comps = getLayeredPane().getComponentsInLayer(JLayeredPane.PALETTE_LAYER);
         //         for (Component c : comps){
         //            SikuliGuideComponent comp = (SikuliGuideComponent) c; 
         //
         //            // remove this visual from the component it follows
         //            SikuliGuideComponent leader = comp.getLeader();
         //            leader.removeFollower(comp);
         //
         //            // remove this visual from the layer pane
         //            getLayeredPane().remove(comp);
         //         }     



         addConnectorVisulization(sourceComponent,anchor);

      }

      public void selectionCompleted(){
         perform(sourceComponent, anchor);
      }

      public void addSelectionVisulization(SikuliGuideComponent comp){

         SikuliGuideRectangle rect = new SikuliGuideRectangle(null);
         rect.setMargin(5,5,5,5);
         rect.setLocationRelativeToComponent(comp, SikuliGuideComponent.OVER);         

         addToPaletteLayer(rect);

         selectionVisulization = rect;
      }

      public void addConnectorVisulization(SikuliGuideComponent from, SikuliGuideComponent to){

         Connector connector = new Connector(from, to);

         //         from.addConnector(connector);
         //         to.addConnector(connector);
         connector.setForeground(Color.white);
         connector.setStyle(SikuliGuideArrow.ELBOW_Y);


         // TODO: add to pallete
         //addToDefaultLayer(connector,-2);
         repaint();
      }

      public void componentSelected(SikuliGuideComponent comp) {

         if (task == SELECTING_FIRST){
            Debug.info("Selecting a component");

            if (comp instanceof SikuliGuideAnchor){

               anchor = (SikuliGuideAnchor) comp;
               task = SELECTING_SOURCECOMPONENT;

            }else{

               sourceComponent = comp;
               task = SELECTING_ANCHOR;
            }

            addSelectionVisulization(comp);


         }else if (task == SELECTING_ANCHOR && comp instanceof SikuliGuideAnchor){
            Debug.info("Selected the target anchor");


            anchor = (SikuliGuideAnchor) comp;                     
            selectionCompleted();

         }else if (task == SELECTING_SOURCECOMPONENT 
               && !(comp instanceof SikuliGuideAnchor)){
            Debug.info("Selected the source component");

            sourceComponent = comp;
            selectionCompleted();                   
         }
      }            
   }


   // convenient function for testing
   public SikuliGuideComponent performAddTextAction(Point location){      
      return (new AddTextAction().perform(location));
   }
   public SikuliGuideComponent performAddAnchorAction(Rectangle rectangle){      
      return (new AddAnchorAction().perform(rectangle));
   }
   public void performLinkComponentToAnchor(SikuliGuideComponent sourceComponent, SikuliGuideComponent anchor){
      new LinkComponentToAnchorAction().perform(sourceComponent, anchor);
   }


   void currentStepContentChanged(){      
      Step step = getCurrentStep();
      if (step != null){
         step.refreshThumbnailImage();

         if (overview != null)
            overview.stepContentChanged(step);

         repaint();
      }
   }

   public Step getCurrentStep() {
      return currentStep;//steps.get(currentStepIndex);
   }

   private Component[] getCurrentComponents(){
      return getCurrentStep().getView().getComponents();
   }

   class AddTextAction{
      public void locationSelected(Point location) {         
         perform(location);
      }

      public SikuliGuideComponent perform(Point location){
         Debug.info("Adding a text element at " + location);

         SikuliGuideText text = new SikuliGuideText("Text");
         SikuliGuideShadow shadow = new SikuliGuideShadow(text);

         text.setLocationRelativeToPoint(location, SikuliGuideComponent.CENTER);         
         text.setMovable(true);
         text.addMouseListener(componentSelector);
         text.addMouseMotionListener(componentSelector);

         addToCurrentStep(shadow,0);
         addToCurrentStep(text,0);

         linkComponentToClosestAnchor(text);
         (new SelectAction()).componentSelected(text);

         // after adding a text element, we want to switch back to the selection mode
         doSelect();

         (new SelectAction()).componentSelected(text);

         // we want to notify those interested in the changes in the content of the current step
         currentStepContentChanged();
         return text;
      }
   }

   class AddAnchorActionByClick{
      public void locationSelected(Point location) {         
         perform(location);
      }

      public SikuliGuideComponent perform(Point location){

         // rectangle centered on the clicked location
         Rectangle r = new Rectangle(50,50);
         r.setLocation(location);
         r.x -= r.width/2;
         r.y -= r.height/2;

         SikuliGuideAnchor anchor = new SikuliGuideAnchor(new Region(r));
         anchor.addMouseListener(componentSelector);
         anchor.setEditable(true);
         anchor.setMovable(true);

         addToCurrentStep(anchor,0);

         // after adding a text element, we want to switch back to the selection mode
         doSelect();

         (new SelectAction()).componentSelected(anchor);


         currentStepContentChanged();         
         return anchor;
      }
   }

   class AddFlagAction{
      public void locationSelected(Point location) {         
         perform(location);
      }

      public void perform(Point location){
         Debug.info("adding flag at:" + location);
         SikuliGuideFlag text = new SikuliGuideFlag("Flag");
         SikuliGuideShadow shadow = new SikuliGuideShadow(text);

         text.setLocationRelativeToPoint(location, SikuliGuideComponent.CENTER);         
         text.setMovable(true);
         text.addMouseListener(componentSelector);

         addToCurrentStep(shadow,0);
         addToCurrentStep(text,0);
         currentStepContentChanged();
      }
   }

   class AddCalloutAction{
      public void locationSelected(Point location) {         
         perform(location);
      }

      public void perform(Point location){
         Debug.info("adding callout at:" + location);
         SikuliGuideCallout text = new SikuliGuideCallout("Callout");
         SikuliGuideShadow shadow = new SikuliGuideShadow(text);

         text.setLocationRelativeToPoint(location, SikuliGuideComponent.CENTER);         
         text.setMovable(true);
         text.addMouseListener(componentSelector);

         addToCurrentStep(shadow,0);
         addToCurrentStep(text,0);
         currentStepContentChanged();
      }
   }


   class RectangleSelector implements 
   MouseMotionListener, MouseListener{

      Point p = null, q = null;
      boolean selecting = false;
      boolean running = false;
      Object action;

      public Rectangle getSelectedRectangle(){
         if (p == null)
            return null;

         Rectangle r = new Rectangle(p);
         r.add(q);
         return r;
      }

      public RectangleSelector(){
      }

      @Override
      public void mouseDragged(MouseEvent e) {
         if (running){
            //Debug.info("dragged to: " + e.getX() + "," + e.getY());     

            if (selecting){
               q = e.getPoint();
               notifySelectionUpdated();
            }
         }
      }

      @Override
      public void mouseMoved(MouseEvent e) {
         if (running){
            Debug.info("moved to: " + e.getX() + "," + e.getY());     

            if (selecting)
               q = e.getPoint();
         }
      }

      @Override
      public void mouseClicked(MouseEvent arg0) {
      }

      @Override
      public void mouseEntered(MouseEvent arg0) {
      }

      @Override
      public void mouseExited(MouseEvent arg0) {
      }

      @Override
      public void mousePressed(MouseEvent e) {
         if (running){
            Debug.info("pressed at: " + e.getX() + "," + e.getY());     

            selecting = true;
            p = e.getPoint();
            q = e.getPoint();         

            notifySelectionStarted();
         }
      }

      @Override
      public void mouseReleased(MouseEvent e) {
         if (running){
            selecting = false;
            q = e.getPoint();

            notifySelectionCompleted();
         }
      }

      ArrayList<RectangleSelectionListener> listeners = new  ArrayList<RectangleSelectionListener>();

      void notifySelectionCompleted(){         
         for (RectangleSelectionListener listener : listeners){
            listener.rectangleSelectionCompleted(getSelectedRectangle());
         }         
      }

      void notifySelectionStarted(){         
         for (RectangleSelectionListener listener : listeners){
            listener.rectangleSelectionStarted();
         }         
      }

      void notifySelectionUpdated(){         
         for (RectangleSelectionListener listener : listeners){
            listener.rectangleSelectionUpdated(getSelectedRectangle());
         }         
      }

      public void start(Object action) {
         listeners.add((RectangleSelectionListener) action);
         this.action = action;
         running = true;         
      }

      public void stop(){
         listeners.remove(action);
         running = false;
      }

   }

   class LocationSelector extends MouseAdapter{
      Object action;
      boolean running = false;
      public void start(Object action){
         this.action = action;
         this.running = true;
         Debug.info("LocationSelector started");
      }

      public void stop(){
         this.running = false;
         action = null;
      }

      public void mouseClicked(MouseEvent e) {
         if (!running)
            return;         

         Debug.info("[LocationSelector] User selected a point: " + e.getPoint());

         Point selectedLocation = e.getPoint();

         StepView view = getCurrentStep().getView();
         Point o = view.getLocation();
         selectedLocation.x -= o.x;
         selectedLocation.y -= o.y;

         //float zoomLevel = StepView.getZoomLevel();
         //         p.x = (int) (p.x / zoomLevel);
         //         p.y = (int) (p.y / zoomLevel);         
         //         Component clickedComponent = getCurrentStep().getStepView().getComponentAt(p);
         //         Debug.info("[LocationSelector] Component at this point is: " + clickedComponent);

         //         if (!(clickedComponent instanceof BackgroundImage)) {
         //            if (action instanceof SelectAction){            
         //               Debug.info("[LocationSelector] User selected: " + clickedComponent);
         //               SikuliGuideComponent sklComponent = (SikuliGuideComponent) clickedComponent;
         //               ((SelectAction) action).componentSelected(sklComponent);
         //            }            
         //         } 

         if (action instanceof AddTextAction){            
            ((AddTextAction) action).locationSelected(selectedLocation);
         }

         else if (action instanceof AddCalloutAction){               
            ((AddCalloutAction) action).locationSelected(selectedLocation);
         }

         else if (action instanceof AddFlagAction){               
            ((AddFlagAction) action).locationSelected(selectedLocation);
         }

         else if (action instanceof SelectAction){               
            ((SelectAction) action).locationSelected(selectedLocation);
         }         

         else if (action instanceof AddAnchorActionByClick){               
            ((AddAnchorActionByClick) action).locationSelected(selectedLocation);
         }


      }

   }


   SikuliGuideAnchor findClosestAnchor(SikuliGuideComponent component){
      double minDistance = Double.MAX_VALUE;


      SikuliGuideAnchor closestAnchor = null;
      for (Component c : getCurrentComponents()){

         if (c instanceof SikuliGuideAnchor){

            SikuliGuideAnchor anchor = (SikuliGuideAnchor) c;

            double distance = anchor.getCenter().distance(component.getCenter());

            if (distance < minDistance){
               closestAnchor = anchor;
               minDistance = distance;
            }

         }

      }
      return closestAnchor;         
   }

   void linkComponentToClosestAnchor(SikuliGuideComponent component){      
      SikuliGuideComponent closestAnchor = findClosestAnchor(component);

      if (closestAnchor != null){
         component.followComponent(closestAnchor);
         closestAnchor.addFollower(component);
      }
   }



   RelatedAnchorVisulization linkedAnchorVisualization;
   class RelatedAnchorVisulization extends SikuliGuideRectangle{

      public RelatedAnchorVisulization() {
         super(null);         
         setMargin(5,5,5,5);
         setForeground(Color.green);
         addToPaletteLayer(this);
      }

      public void setAnchor(SikuliGuideComponent comp){
         setLocationRelativeToComponent(comp, SikuliGuideComponent.OVER);
      }

   }

   class ComponentSelector extends MouseAdapter{

      ArrayList<SikuliGuideComponent> selectedComponents =
         new ArrayList<SikuliGuideComponent>();


      boolean running = false;
      Object action;
      public void start(Object action){
         this.action = action;
         this.running = true;
      }

      public void stop(){
         this.running = false;
         action = null;
      }



      SikuliGuideComponent draggedComponent = null;
      SikuliGuideAnchor closestAnchor = null;

      SikuliGuideComponent selectedComponent = null;
      public void mouseDragged(MouseEvent e){
         Debug.log("[ComponentSelector] Component is dragged to: " +e.getPoint());


         draggedComponent = (SikuliGuideComponent) e.getSource();

         Debug.log("[ComponentSelector] Component is at: " + draggedComponent.getLocation());

         closestAnchor = findClosestAnchor(draggedComponent);     
         if (closestAnchor != null)
            Debug.log("[ComponentSelector] Closest anchor is at: " + closestAnchor.getLocation());


         linkedAnchorVisualization.setVisible(true);
         linkedAnchorVisualization.setAnchor(closestAnchor);

      }

      //SikuliGuideRectangle selectionVisulization;
      //      public void setSelectionVisulization(SikuliGuideComponent comp){
      ////         if (selectionVisulization == null){
      ////            selectionVisulization = new SikuliGuideRectangle(null);
      ////            selectionVisulization.setMargin(5,5,5,5);
      ////            selectionVisulization.setForeground(Color.green);
      ////            addToPaletteLayer(selectionVisulization);
      ////         }
      ////
      //         
      //         //selectionVisulization.setLocationRelativeToComponent(comp, SikuliGuideComponent.OVER);         
      //      }


      public void mouseReleased(MouseEvent e){
         // TODO update only when user really dragged moved the component
         SikuliGuideComponent comp = (SikuliGuideComponent) e.getSource();
         currentStepContentChanged();


         if (draggedComponent != null){
            linkComponentToClosestAnchor(draggedComponent);   
         }

         draggedComponent = null;

      }

      public void mouseClicked(MouseEvent e) {



         SikuliGuideComponent comp = (SikuliGuideComponent) e.getSource();

         selectedComponent = comp;

         if (action instanceof LinkComponentToAnchorAction){            
            ((LinkComponentToAnchorAction) action).componentSelected(comp);
         }

         else if (action instanceof AddCircleToTargetAction){            
            ((AddCircleToTargetAction) action).componentSelected(comp);
         }

         else if (action instanceof AddBracketToTargetAction){            
            ((AddBracketToTargetAction) action).componentSelected(comp);
         }

         else if (action instanceof SelectAction){            
            ((SelectAction) action).componentSelected(comp);
         }




         else if (action instanceof EditTextPropertyAction){

            ((EditTextPropertyAction) action).componentSelected(comp);

         }

         if  (e.getClickCount() == 2){

            new EditTextPropertyAction().perform(comp);
         }
      }  


      public void clear() {
         selectedComponents.clear();
      }


      public  ArrayList<SikuliGuideComponent> getSelectedComponents(){
         return selectedComponents;
      }
   }

   ComponentSelector componentSelector = new ComponentSelector();
   class PointSelectionAdapter implements PointSelectionListener {

      @Override
      public void pointSelectionCompleted(PointSelectionEvent e){

         for (Point p : e.selectedPoints){
            Debug.info("selected point :" + p);
         }

         e.selector.stop();

         Point p = selectedPoints.get(0);
         SikuliGuideText text = new SikuliGuideText("Some text");
         SikuliGuideShadow shadow = new SikuliGuideShadow(text);

         text.setLocationRelativeToPoint(p, SikuliGuideComponent.CENTER);         
         text.setMovable(true);
         text.addMouseListener(componentSelector);

         add(shadow,0);
         add(text,0);
         repaint();
      }

      @Override
      public void pointSelectionStarted(PointSelectionEvent e) {
      }

      @Override
      public void pointSelectionUpdated(PointSelectionEvent e) {
         selectedPoints = e.selectedPoints;

         repaint();
      }
   }

   float opacity = 1.0f;
   public float getOpacity(){
      return opacity;
   }

   public void setOpacity(float alpha){
      opacity = alpha;
      // Env.getOSUtil().setWindowOpacity(this, alpha);
   }


   int currentLayer = 10;
   // all components corresponding to a step are stored in the same layer


   public void addToPaletteLayer(JComponent component){
      palletePanel.add(component);
   }

   public void removeFromPaletteLayer(JComponent component){
      getLayeredPane().remove(component);
   }


   //   public Component[] getComponentsFromDefaultLayer(){
   //      int layerIndex = getLayereIndexFromStepIndex(currentStepIndex);
   //      return getLayeredPane().getComponentsInLayer(layerIndex);      
   //   }

   //   public void addToDefaultLayer(JComponent component, int index){
   //      int layerIndex = getLayereIndexFromStepIndex(currentStepIndex);
   //      getLayeredPane().add(component, layerIndex, index);
   //   }

   public void addToCurrentStep(JComponent component, int index){
      getCurrentStep().getView().add(component,index);
   }

   //   public void addToDefaultLayerBelow(JComponent component, int index){
   //      int layerIndex = getLayereIndexFromStepIndex(currentStepIndex);
   //      getLayeredPane().add(component, layerIndex-1, index);
   //   }   

   //   int getLayereIndexFromStepIndex(int stepIndex){
   //      return 2*stepIndex + 10;
   //   }
   //
   //   void setLayerVisiblity(int layerIndex, boolean visibility){
   //      for (Component c : getLayeredPane().getComponentsInLayer(layerIndex)){
   //         c.setVisible(visibility);
   //      }
   //   }


   void unselectComponent(){
      selectedComponent = null;
      controlBox.setVisible(false);
      linkedAnchorVisualization.setVisible(false);
      textPropertyEditor.setVisible(false);

      textPropertyEditor.removeLeader();
      controlBox.removeLeader();
      linkedAnchorVisualization.removeLeader();

   }

   void setPalleteScale(){
      //      selectedComponent = null;
      //      
      //      controlBox.setVisible(false);
      //      controlBox.x = controlBox.x/2; 
      //      controlBox.x = controlBox.x/2; 
      //      
      //      
      //      linkedAnchorVisualization.setVisible(false);
      //      textPropertyEditor.setVisible(false);
   }


   public void selectStep(Step selectedStep){

      if (currentStep != null)
         currentStep.getView().setVisible(false);

      currentStep = selectedStep;

      currentStep.getView().setVisible(true);
      //      
      //      int layerIndex = getLayereIndexFromStepIndex(currentStepIndex);
      //      setLayerVisiblity(layerIndex, false);
      //
      //      currentStepIndex = stepIndex;
      //
      //      layerIndex = getLayereIndexFromStepIndex(currentStepIndex);
      //      setLayerVisiblity(layerIndex, true);

      if (overview != null)
         overview.selectStep(selectedStep);

      unselectComponent();

      setZoomLevel(getZoomLevel());
   }


   public void selectStep(int stepIndex){
      if (stepIndex >= 0 && stepIndex < steps.size()){
         selectStep(steps.get(stepIndex));
      }
   }

   public void nextStep(){
      int currentStepIndex = steps.indexOf(getCurrentStep());
      if (currentStepIndex < steps.size() - 1){
         selectStep(currentStepIndex+1);
      }
   }   

   public void previousStep(){
      int currentStepIndex = steps.indexOf(getCurrentStep());      
      if (currentStepIndex > 0){
         selectStep(currentStepIndex-1);
      }
   }

   class LinearStepper {
      float beginVal;
      float endVal;
      int step;
      int steps;

      public LinearStepper(float beginVal, float endVal, int steps){
         this.step = 0;
         this.steps = steps;
         this.beginVal = beginVal;
         this.endVal = endVal;
      }

      public float next(){
         float ret = beginVal + step * (endVal - beginVal) / steps;
         step += 1;
         return ret;
      }

      public boolean hasNext(){
         return step <= steps;
      }
   }

   // TODO better implementation of this
   class OpacityAnimator implements ActionListener{
      LinearStepper stepper;
      Timer timer;
      int i;
      boolean moreOpaque;
      OpacityAnimator(boolean moreOpaque){
         this.moreOpaque = moreOpaque;

         if (moreOpaque){
            setOpacity(0.0f);
            stepper = new LinearStepper(0.0f, 1.0f, 10);
         }else{
            setOpacity(1.0f);
            stepper = new LinearStepper(1.0f, 0.0f, 10);                       
         }
         Timer timer = new Timer(50, this);
         timer.start();
         i = 0;
      }

      @Override
      public void actionPerformed(ActionEvent e){
         if (stepper.hasNext()){

            float f = stepper.next();
            //Debug.info("f:" + f);
            setOpacity(f);
            repaint();
         }else{
            ((Timer)e.getSource()).stop();

            if (!moreOpaque){
               setVisible(false);
            }

         }
      }
   }

   private boolean playAll = false;   

   @Override
   public void setVisible(boolean visible){

      final LinearAnimator anim = null;


      if (!isVisible() && visible){         
         new OpacityAnimator(true);
         super.setVisible(visible);
      }else if (isVisible() && !visible){

         if (opacity > 0)
            new OpacityAnimator(false);
         else
            super.setVisible(false);
      }       

   }

   //   public void importStep(BufferedImage screenImage){
   //      selectStep(0);          
   //
   //      BackgroundImage bgImage = new BackgroundImage(screenImage);
   //      addToDefaultLayer(bgImage,-1);
   //      Step step = new Step();
   //      step.setScreenImage(screenImage);
   //      steps.add(step);
   //
   //      stepCount = 1;
   //   }



   public BufferedImage createThumbnailForStep(int stepIndex){      
      StepView stepPanel = stepViews.get(stepIndex);      
      //      return stepPanel.createThumbnail(0.2f);
      return stepPanel.createForegroundThumbnail(200,150);
   }


   public void updateStepThumbnail(Step step, StepView panel){

      BufferedImage tb = panel.createForegroundThumbnail(200,150);
      step.setThumbnailImage(tb);      
   }


   ArrayList<Rectangle> getBlobRegions(BufferedImage image){
      Mat m = OpenCV.convertBufferedImageToMat(image);
      FindResults results = Vision.findBlobs(m);
      Debug.info("find " + results.size() + " blobs.");

      ArrayList<Rectangle> valid = new ArrayList<Rectangle>();
      for (int i=0; i < results.size()-1; ++i){
         FindResult result = results.get(i);       
         Rectangle r = new Rectangle(result.getX(),result.getY(),result.getW(),result.getH());

         // filtering
         if (r.width > 10 && r.height > 10 && r.width < 50 && r.height < 50)
            valid.add(r);     
      }
      return valid;
   }

   int stepCount = 0;
   ArrayList<Step> steps = new ArrayList<Step>(); 
   ArrayList<StepView> stepViews = new ArrayList<StepView>();

   private boolean segmentationEnabled = false;


   Step currentStep = null;
   public Step importStep(RecordedClickEvent event){


      // the index of the imported step
      int stepIndex = steps.size();

      Debug.info("importing " + stepIndex);


      Step step = new Step();      
      StepView view = new StepView(step);


      stepViews.add(view);
      steps.add(step);


      step.setIndex(stepIndex);
      step.setView(view);


      BufferedImage screenImage = event.getScreenImage();
      step.setScreenImage(screenImage);

      setScreenImageSize(new Dimension(screenImage.getWidth(),screenImage.getHeight()));

      BackgroundImage bgImage = new BackgroundImage(screenImage);      
      view.setSize(screenImage.getWidth(), screenImage.getHeight());      
      view.add(bgImage);

      selectStep(step);

      getLayeredPane().add(view, JLayeredPane.DEFAULT_LAYER);
      //      selectStep(stepIndex);          


      Point p = event.getClickLocation();
      Rectangle r = new Rectangle(100,100);
      r.x = p.x - r.width/2;
      r.y = p.y - r.height/2;


      Rectangle small = new Rectangle(20,20);
      small.x = p.x - small.width/2;
      small.y = p.y - small.height/2;


      //

      if (segmentationEnabled){
         BufferedImage neighoborImage = bgImage.crop(r);


         ArrayList<Rectangle> blobs = getBlobRegions(neighoborImage);

         Rectangle mergedBlob = new Rectangle(small);
         for (Rectangle blob : blobs){
            blob.x += r.x;
            blob.y += r.y;

            mergedBlob.add(blob);

         }

         mergedBlob.grow(10,10);
         performAddAnchorAction(mergedBlob);
      }else{

         performAddAnchorAction(r);

         SikuliGuideText txt = (SikuliGuideText) performAddTextAction(new Point(r.x+50,r.y-20));
         txt.setText("Click");     
      }



      step.refreshThumbnailImage();   

      if (overview != null){
         overview.addStep(step);
         // for some reason, this setVisible needs to be set in order
         // for the overview window to be drawn correctly...
         overview.setVisible(true);
      }

      return step;
   }

   public void importSteps(ArrayList<RecordedClickEvent> events) {

      //      int stepIndex = 0;

      for (RecordedClickEvent event : events){


         importStep(event);

         //      overview.addStep(step);

      }

      selectStep(steps.get(0));

      //      overview = new OverviewWindow();

      // populate overview
      //      for (Step step : steps){
      //         overview.addStep(step);
      //      }

      //      overview.setLocation(10,50);
      //      //overview.setMovable(true);
      //      overview.setEditor(this);

      // this causes the element to position correctly
      //      overview.setVisible(true);
      //      //overview.pack();
      //      overview.toFront();
      //      
      // selectStep(0);
      doSelect();
   }

   public ArrayList<Step> getSteps() {
      return steps;
   }

   public void setOverview(OverviewWindow overview) {
      this.overview = overview;
   }

   public OverviewWindow getOverview() {
      return overview;
   }

   public void setSegmentationEnabled(boolean segmentationEnabled) {
      // TODO fix this hack to load library
      try {
         Finder f = new Finder("");
      } catch (IOException e) {
      }

      this.segmentationEnabled = segmentationEnabled;
   }

   public boolean isSegmentationEnabled() {
      return segmentationEnabled;
   }

   public void setPlayAll(boolean playAll) {
      this.playAll = playAll;
   }

   public boolean isPlayAll() {
      return playAll;
   }

   static public int main() throws FindFailed{
      SikuliGuide g = new SikuliGuide();
      
      SikuliGuideButton btn = new SikuliGuideButton("Start Recording (Select a Region)");
      btn.setLocationRelativeToRegion(new Screen(), SikuliGuideComponent.INSIDE);      

      g.addComponent(btn);
      g.showNow();
      
      
      ScreenRegionSelectionWindow w = 
         new ScreenRegionSelectionWindow(new JFrame());

      w.startModal();

      Region r = w.getSelectedRegion();
      Debug.info("Selected region: " + r);


      Region regionToRecord = r;
      ClickRecorder c = new ClickRecorder(regionToRecord);


      EditorWindow ew = new EditorWindow();    
      ew.setVisible(false);
      ew.setScreenImageSize(new Dimension(r.w,r.h));
      ew.setBounds(0,0,r.w+100,r.h+100);

      //ew.setSegmentationEnabled(true);
      //ew.importSteps(c.getClickEvents());      
      ew.setLocation(284,50);


      btn = new SikuliGuideButton("Stop Recording");
      btn.setLocationRelativeToRegion(regionToRecord, SikuliGuideComponent.TOP);      


      while (true){
         g.addComponent(btn);
         g.addComponent(c);         
         String ret = g.showNow();

         if (ret != null && ret.compareTo("Stop Recording") == 0){
            break;
         }else{
            RecordedClickEvent rce = c.getLastClickEvent();            
            ew.importStep(rce);                        
         }

      }

      g.setVisible(true);
      ew.selectStep(0);
      ew.setVisible(true);      

      while (true){

         ew.setVisible(true);
         ew.setLocation(284,50);

         synchronized(ew){
            try {
               ew.wait();
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }

         ew.setVisible(false);

         try {
            Thread.sleep(1000);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }

         Screen s = new Screen();


         if (ew.isPlayAll()){            

            g.playSteps(ew.getSteps());


         }else{
            btn = new SikuliGuideButton("Back to Editor");
            btn.setLocation(s.getTopRight().left(200).below(50));

            Step step = ew.getCurrentStep();         
            g.addComponent(btn);
            step.setTransition(g.getTransition());
            g.playStep(step);
         }


      }


   }

}
