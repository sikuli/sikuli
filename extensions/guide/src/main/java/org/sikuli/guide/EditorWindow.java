package org.sikuli.guide;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
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
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JWindow;

import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.OS;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;
import org.sikuli.script.TransparentWindow;

interface RectangleSelectionListener {      
   void rectangleSelectionCompleted(Rectangle rectangle);
   void rectangleSelectionStarted();
   void rectangleSelectionUpdated(Rectangle rectangle);
}

public class EditorWindow extends JWindow 
implements MouseListener, KeyListener, 
RectangleSelectionListener,
Transition, GlobalMouseMotionListener, MouseMotionListener {

   
   public final static double SCALE = 1.0;

   BufferedImage image;
   RectangleSelector rectangleSelector;
   PointSelector pointSelector;
   BackgroundImage backgroundImage;
   
   interface PointSelectionListener {
      void pointSelectionCompleted(PointSelectionEvent e);
      void pointSelectionStarted(PointSelectionEvent e);
      void pointSelectionUpdated(PointSelectionEvent e);   
   }
   
   public class PointSelectionEvent {
      public PointSelector selector;
      public ArrayList<Point> selectedPoints;
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
   
   
   class RectangleSelector implements 
   MouseMotionListener, MouseListener{

      Point p = null, q = null;
      boolean selecting = false;

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
         Debug.info("dragged to: " + e.getX() + "," + e.getY());     

         if (selecting){
            q = e.getPoint();
            notifySelectionUpdated();
         }

      }

      @Override
      public void mouseMoved(MouseEvent e) {
         Debug.info("moved to: " + e.getX() + "," + e.getY());     

         if (selecting)
            q = e.getPoint();
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
         Debug.info("pressed at: " + e.getX() + "," + e.getY());     

         selecting = true;
         p = e.getPoint();
         q = e.getPoint();         

         notifySelectionStarted();
      }

      @Override
      public void mouseReleased(MouseEvent e) {
         selecting = false;
         q = e.getPoint();

         notifySelectionCompleted();
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

      public void addListener(RectangleSelectionListener listener){
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

   class BackgroundImage extends JComponent {

      BufferedImage image;
      double scale = (double) SCALE;
      public BackgroundImage(BufferedImage image){
         this.image = image;
         setBounds(0,0,(int)(image.getWidth()*scale),(int)(image.getHeight()*scale));
      }

      @Override
      public void paintComponent(Graphics g){
         super.paintComponent(g);         
         Graphics2D g2d = (Graphics2D) g;
         if (image != null){            
            Rectangle r = getBounds();
            g2d.drawImage(image, r.x, r.y, r.width, r.height, null);
         }    
      }

      public BufferedImage crop(Rectangle r) {
         // TODO Make this bounadry safe
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
   
   public EditorWindow(JFrame f){
      super(f);

      // this allows us to layout the components ourselves
      setLayout(null);

      // this window should cover the same area as the guide
      //setBounds(new Screen().getRect());//guide.getBounds());
      Screen s = new Screen();
      setBounds(0,0,(int)(s.w*SCALE),(int)(s.h*SCALE));
      
      setAlwaysOnTop(true);


      //      mouseTracker = GlobalMouseLocationTracker.getInstance();
      //      mouseTracker.addListener(this);

      //Color bg = new Color(1.0f,1.0f,1.0f,1.0f);
      //Color bg = new Color(1.0f,0,0,0.1f);
      //Color bg = new Color(1.0f,0,0,0.1f);
      Color bg = Color.black;

      JTextPane p = new JTextPane();
      p.setContentType("text/html");
      p.setText("<font size=20>This is some text</font>");
      p.setBounds(getBounds());
      //p.setOpaque(true);
      p.requestFocus();
      //add(p);

      //Env.getOSUtil().setWindowOpacity(this, 0.5f);


      //add(new JTextField(50));

      getContentPane().setBackground(bg);
      setBackground(bg);
      // p.setBackground(bg);


      //getContentPane().setOpaque(false);
      // This makes the JWindow transparent
      //Env.getOSUtil().setWindowOpaque(this, false);

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
      rectangleSelector.addListener(this);


      pointSelector = new PointSelector(getBounds());
      pointSelector.addListener(new PointSelectionAdapter());
      add(pointSelector);
      
    //  pointSelector.addListener(this);

      getContentPane().addKeyListener(this);

      addWindowListener(new WindowAdapter(){
         public void windowClosed(WindowEvent e){
            // stop the global mouse tracker's timer thread
            mouseTracker.stop();
         }
      });

      setFocusable(true);
      getContentPane().setFocusable(true);
      //      JTextField label = new JTextField("Test test getting keyboard");
      //      add(label);


      saveButton = new Button("Save");
      saveButton.setSize(saveButton.getPreferredSize());
      add(saveButton);

      
      controlLayer = new JLayeredPane();
      controlLayer.setBounds(getBounds());
      //controlLayer.setLayout(null);
      controlLayer.add(new JLabel("This is the control layer"));
      add(controlLayer,0);
      
      
      //controlLayer.set
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

      toFront();
      setVisible(true);
      setAlwaysOnTop(true);

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
         //            getOwner().setCursor(hourglassCursor);
         //            c.setCursor(hourglassCursor);
         //         }


      }



   }

   @Override
   public void toFront(){
      if(Env.getOS() == OS.MAC){
         // this call is necessary to allow clicks to go through the window (ignoreMouse == true)
         Env.getOSUtil().bringWindowToFront(this, false);
      }     
      super.toFront();

   }

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
      super.paint(g);

      Graphics2D g2d = (Graphics2D) g;
      if (selectedRectangle != null){
         Rectangle r = selectedRectangle;

         g2d.setColor(Color.red);
         g2d.drawRect(r.x,r.y,r.width,r.height);
      }
      
//      if (selectedPoints != null){
//         
//         for (Point p : selectedPoints){
//            g2d.drawOval(p.x-5,p.y-5,10,10);
//         }
//      }

   }


   public void setImage(BufferedImage image) {
      this.image = image;
      backgroundImage = new BackgroundImage(image);
      add(backgroundImage);
   }

   

   @Override
   public void keyPressed(KeyEvent k) {
      Debug.log("pressed " + k.getKeyCode());

      if (k.getKeyCode() == KeyEvent.VK_1){       

//         pointSelector.start(2);
//         pointSelector.addListener(new RectangleSelection());
         
         addMouseListener(rectangleSelector);
         addMouseMotionListener(rectangleSelector);

      }else if (k.getKeyCode() == KeyEvent.VK_2){       
         
         pointSelector.start(1);

      }else if (k.getKeyCode() == KeyEvent.VK_3){
         
         componentSelector.start(new LinkAnnotationToTargetAction());
      
      }else if (k.getKeyCode() == KeyEvent.VK_4){
         
         componentSelector.start(new EditTextPropertyAction());
         
      }else if (k.getKeyCode() == KeyEvent.VK_ESCAPE){
         
         for (Component comp: getContentPane().getComponents()){
            
            if (comp instanceof Target){
               
               Target target = (Target) comp;
               Rectangle bounds = target.getBounds();
               
               // TODO: removing this scaling when back to full-screen mode
               bounds.x *= SCALE;
               bounds.y *= SCALE;
               bounds.width *= SCALE;
               bounds.height *= SCALE;
               BufferedImage croppedImage = backgroundImage.crop(bounds);
               
               Part part = new Part(new Pattern(croppedImage));
               // TODO: fold this into target
               part.setTargetOrigin(new Point(target.getLocation()));
               
               for (SikuliGuideComponent annotation : target.getFollowers()){   
                  
                  if (JLayeredPane.getLayer(annotation) != JLayeredPane.PALETTE_LAYER){                  
                     part.addComponent((SikuliGuideComponent) annotation.clone());
                     
                  }
               }
               
               if (step == null)
                  step = new Step();
               step.addPart(part);  
               
               
            }
            
         }
         
         
         
         synchronized(this){
            this.notify();
         }
      }

   }

   private Step step;
   
   
   

   private void crop(Rectangle bounds) {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void keyReleased(KeyEvent arg0) {
      // TODO Auto-generated method stub

   }


   @Override
   public void keyTyped(KeyEvent k) {
      Debug.log("typed " + k.getKeyCode());


   }

   
  
   @Override
   public void rectangleSelectionCompleted(Rectangle rectangle) {
      //this.add(button,0);
//      saveButton.setLocation(rectangle.x, rectangle.y + rectangle.height + 2);
//      repaint();
      removeMouseListener(rectangleSelector);
      removeMouseMotionListener(rectangleSelector);
      
      Target c = new Target(new Region(rectangle));
      c.addMouseListener(componentSelector);
      add(c,0);
      
      selectedRectangle = null;
      repaint();
   }

   @Override
   public void rectangleSelectionStarted() {
   }

   @Override
   public void rectangleSelectionUpdated(Rectangle rectangle) {
      selectedRectangle = rectangle;    
      repaint();
   }
   
//   class RectangleSelection implements PointSelectionListener {
//      @Override
//      public void pointSelectionCompleted(PointSelectionEvent e){
//
//         for (Point p : e.selectedPoints){
//            Debug.info("selected point :" + p);
//         }
//
//         e.selector.stop();
//      }
//
//      @Override
//      public void pointSelectionStarted(PointSelectionEvent e) {
//      }
//
//      @Override
//      public void pointSelectionUpdated(PointSelectionEvent e) {
//         
//         Point p = e.selectedPoints.get(0);
//         Point q = e.selectedPoints.get(1);
//         
//         Rectangle r = new Rectangle(p);
//         r.add(q);
//         
//         selectedRectangle = r;    
//         repaint();
//      }
//   }
   
   public void setStep(Step step) {
      this.step = step;
   }

   public Step getStep() {
      return step;
   }
   
   
   class EditTextPropertyAction{
      public void componentSelected(SikuliGuideComponent comp) {
         
         // TODO allow other types to be edited
         if (comp instanceof SikuliGuideText){
            TextPropertyEditor ed = new TextPropertyEditor(comp);
            ed.setLocationRelativeTo(comp, SikuliGuideComponent.TOP);        
            // TODO automatically transfer focus to text field

            ed.requestFocus();
            // TODO only one editor for one target 
            // TODO remove editor from layeredpane when done
            getLayeredPane().add(ed, JLayeredPane.PALETTE_LAYER);
         }
         
      }      
   }
   
   class LinkAnnotationToTargetAction {

      //  try to link target and annotation
      
      Target target;
      SikuliGuideComponent annotation;

      final int SELECTING_FIRST = 0;
      final int SELECTING_TARGET = 1;
      final int SELECTING_ANNOTATION = 2;
      
      int task =  SELECTING_FIRST;
      
      public void selectionCompleted(){
         
         target.addFollower(annotation);         
         
            Debug.info("Linking " + annotation  + " to " + target);
            
            // remove selection visuals
            Component[] comps = getLayeredPane().getComponentsInLayer(JLayeredPane.PALETTE_LAYER);
            for (Component c : comps){
               SikuliGuideComponent comp = (SikuliGuideComponent) c; 
               
               // remove this visual from the component it follows
               SikuliGuideComponent leader = comp.getLeader();
               leader.removeFollower(comp);
               
               // remove this visual from the layer pane
               getLayeredPane().remove(comp);
            }     
            
            addLinkageVisualization(annotation,target);
      }
      
      public void addSelectionRectangle(SikuliGuideComponent comp){
         Rectangle bounds = comp.getBounds();
         bounds.grow(5,5);
         SikuliGuideRectangle rect = new SikuliGuideRectangle(new Region(bounds));
         comp.addFollower(rect);
         getLayeredPane().add(rect,JLayeredPane.PALETTE_LAYER);
         rect.repaint();

      }
      
      public void addLinkageVisualization(SikuliGuideComponent from, SikuliGuideComponent to){
         
         Connector connector = new Connector(from, to);
//         
       
         from.addConnector(connector);
         to.addConnector(connector);
         connector.setForeground(Color.gray);
         
         add(connector,0);
         //connector.repaint();
         repaint();
      }
      
      public void componentSelected(SikuliGuideComponent comp) {
         
            if (task == SELECTING_FIRST){
               Debug.info("Selecting any component");
               
               if (comp instanceof Target){
               
                  target = (Target) comp;
                  task = SELECTING_ANNOTATION;
                
               }else{
                  
                  annotation = comp;
                  task = SELECTING_TARGET;
               }
               
               addSelectionRectangle(comp);

               
            }else if (task == SELECTING_TARGET && comp instanceof Target){
                  Debug.info("Selected a target component");

               
                  target = (Target) comp;                     
                  addSelectionRectangle(comp);
                  selectionCompleted();
                  
            }else if (task == SELECTING_ANNOTATION && !(comp instanceof Target)){
                  Debug.info("Selected an annotation component");

                  annotation = comp;
                  addSelectionRectangle(comp);
                  selectionCompleted();                   
               }
            }            
      }

   class ComponentSelector extends MouseAdapter{
        
      ArrayList<SikuliGuideComponent> selectedComponents =
         new ArrayList<SikuliGuideComponent>();
      
      
      
      Object action;
      public void start(Object action){
         this.action = action;
      }
      
      
      public void mouseClicked(MouseEvent e) {
      
         SikuliGuideComponent comp = (SikuliGuideComponent) e.getSource();
         
         if (action instanceof LinkAnnotationToTargetAction){            
            ((LinkAnnotationToTargetAction) action).componentSelected(comp);
         }
         
         if (action instanceof EditTextPropertyAction){
            
            ((EditTextPropertyAction) action).componentSelected(comp);
            
         }
//         SikuliGuideComponent comp = (SikuliGuideComponent) e.getSource();
//         
//         selectedComponents.add(comp);         
//         
//         Rectangle bounds = comp.getBounds();
//         bounds.grow(5,5);
//         SikuliGuideRectangle rect = new SikuliGuideRectangle(new Region(bounds));
//         comp.addFollower(rect);
//         add(rect,0);
//         rect.repaint();
//      
//         Debug.info("[ComponentSelector] a component is selected ( " + selectedComponents.size() + " )");
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
}