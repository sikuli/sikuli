package org.sikuli.guide;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
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
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.JWindow;
import javax.swing.Timer;

import org.sikuli.script.Debug;
import org.sikuli.script.Env;
import org.sikuli.script.OS;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.Screen;

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
   LocationSelector locationSelector;
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

   
   class Toolbar extends SikuliGuideComponent {
      
      
      class ToolbarButton extends JButton implements ActionListener{
         
         ToolbarButton(String text, String command){            
            super(text);
            setActionCommand(command);
            setFocusable(false);
            addActionListener(this);
         }

         @Override
         public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand() == "Target"){
               doSelectTarget();
            }else if (e.getActionCommand() == "Text"){
               doAddText();
            }else if (e.getActionCommand() == "Connect"){
               doConnectTextToTarget();
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
            }
            
         }
         
      }
            
      public Toolbar(){
         setBackground(Color.red);
         setOpaque(true);
         
         JPanel p = new JPanel();
         p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
         p.setLayout(new BoxLayout(p,BoxLayout.X_AXIS));
         
         p.add(new ToolbarButton("Target","Target"));
         p.add(new ToolbarButton("Text","Text"));
         p.add(new ToolbarButton("Callout","Callout"));
         p.add(new ToolbarButton("Flag","Flag"));
         p.add(new ToolbarButton("Circle","Circle"));
         p.add(new ToolbarButton("Bracket","Bracket"));

         p.add(new ToolbarButton("Connect","Connect"));
         p.add(new ToolbarButton("Run","Run"));

         p.setSize(p.getPreferredSize());
         p.setBackground(new Color(0,0,0,0.2f));

         add(p);
         
         setSize(p.getSize());      
         setMovable(true);
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
      //BufferedImage darkenImage;
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
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,0.6f));
            //g2d.drawImage(image, r.x, r.y, r.width, r.height, null);
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


      Env.getOSUtil().setWindowOpacity(this, 0.8f);

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



      //add(new JTextField(50));

      getContentPane().setBackground(bg);
      setBackground(bg);
      // p.setBackground(bg);


      //getContentPane().setOpaque(false);
      // This makes the JWindow transparent

      // None of these seemed to work
      //Env.getOSUtil().setWindowOpacity(this, 0.5f);
      //Env.getOSUtil().setWindowOpaque(this, false);
      //Env.getOSUtil().bringWindowToFront(this, true);


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


//      pointSelector = new PointSelector(getBounds());
//      pointSelector.addListener(new PointSelectionAdapter());
//      add(pointSelector);
      
      locationSelector = new LocationSelector();
      addMouseListener(locationSelector);

      getContentPane().addKeyListener(this);

      addWindowListener(new WindowAdapter(){
         public void windowClosed(WindowEvent e){
            // stop the global mouse tracker's timer thread
            mouseTracker.stop();
         }
      });

      setFocusable(true);
      getContentPane().setFocusable(true);

//      saveButton = new Button("Save");
//      saveButton.setSize(saveButton.getPreferredSize());
//      add(saveButton);


      controlLayer = new JLayeredPane();
      controlLayer.setBounds(getBounds());
      add(controlLayer,0);

      Toolbar toolbar = new Toolbar();
      toolbar.setLocation(200,50);
      add(toolbar);

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
         //            getOwner().setCursor(ho1u2rglassCursor);
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


   public void setImage(BufferedImage image) {
      this.image = image;
      backgroundImage = new BackgroundImage(image);
      add(backgroundImage);
   }


   
   void doSelectTarget(){
      addMouseListener(rectangleSelector);
      addMouseMotionListener(rectangleSelector);
      
      locationSelector.stop();
   }
   
   void doAddText(){
      
      // TODO consider reverse, such as: 
      // action = new AddTextAction(locationSelector)
      // action.start()
      componentSelector.stop();      
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
   
   void doConnectTextToTarget(){
      locationSelector.stop();
      componentSelector.start(new ConnectAnnotationAndTargetAction());
   }

   void doRun(){
      
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
   
   @Override
   public void keyPressed(KeyEvent k) {
      Debug.log("pressed " + k.getKeyCode());

      if (k.getKeyCode() == KeyEvent.VK_1){       
         doSelectTarget();
      }else if (k.getKeyCode() == KeyEvent.VK_2){       
         doAddText();
      }else if (k.getKeyCode() == KeyEvent.VK_3){
         doConnectTextToTarget();
      }else if (k.getKeyCode() == KeyEvent.VK_4){

         componentSelector.start(new EditTextPropertyAction());

      }else if (k.getKeyCode() == KeyEvent.VK_ESCAPE){         
         doRun();
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

      removeMouseListener(rectangleSelector);
      removeMouseMotionListener(rectangleSelector);

      // ignore too small rectangles
      if (rectangle.width < 2 && rectangle.height < 2){
         return;
      }
      
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
         perform(comp);
      }     
      
      public void perform(SikuliGuideComponent comp){
        
         // TODO allow other types to be edited
         if (comp instanceof SikuliGuideText){
            
            SikuliGuideText text = (SikuliGuideText) comp;
            text.setEditable(true);
            //TextPropertyEditor ed = new TextPropertyEditor(comp);
            //ed.setLocationRelativeTo(comp, SikuliGuideComponent.TOP);
            // TODO automatically transfer focus to text field

            // TODO only one editor for one target 
            // TODO remove editor from layeredpane when done
            //getLayeredPane().add(ed, JLayeredPane.PALETTE_LAYER);
            //ed.requestFocus();

         }
      }
   }

   
   class AddBracketToTargetAction {
      
      public void componentSelected(SikuliGuideComponent comp) {
         
         Region r = new Region(comp.getBounds());
         SikuliGuideBracket bracket = new SikuliGuideBracket();
         
         
         if(r.w > r.h)
            bracket.setLocationRelativeToRegion(r, SikuliGuideComponent.TOP);
         else
            bracket.setLocationRelativeToRegion(r, SikuliGuideComponent.LEFT);

         comp.addFollower(bracket);
         
         add(bracket,0);
         repaint();         
      }
   }
   
   class AddCircleToTargetAction {
      
      public void componentSelected(SikuliGuideComponent comp) {
         
         Region r = new Region(comp.getBounds());
         SikuliGuideCircle circle = new SikuliGuideCircle(r);
         comp.addFollower(circle);
         
         
         add(circle,0);
         repaint();         
      }
   }
   
   class ConnectAnnotationAndTargetAction {

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

         addConnectorVisulization(annotation,target);
      }

      public void addSelectionRectangle(SikuliGuideComponent comp){
         Rectangle bounds = comp.getBounds();
         bounds.grow(5,5);
         SikuliGuideRectangle rect = new SikuliGuideRectangle(new Region(bounds));
         comp.addFollower(rect);
         getLayeredPane().add(rect,JLayeredPane.PALETTE_LAYER);
         rect.repaint();

      }

      public void addConnectorVisulization(SikuliGuideComponent from, SikuliGuideComponent to){

         Connector connector = new Connector(from, to);
         //         

         from.addConnector(connector);
         to.addConnector(connector);
         connector.setForeground(Color.green);

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

   class AddTextAction{
      public void locationSelected(Point location) {         
         perform(location);
      }
      
      public void perform(Point location){
         SikuliGuideText text = new SikuliGuideText("Text");
         SikuliGuideShadow shadow = new SikuliGuideShadow(text);

         text.setLocationRelativeToPoint(location, SikuliGuideComponent.CENTER);         
         text.setMovable(true);
         text.addMouseListener(componentSelector);

         add(shadow,0);
         add(text,0);
         repaint();
      }
   }
   
   class AddFlagAction{
      public void locationSelected(Point location) {         
         perform(location);
      }
      
      public void perform(Point location){
         Debug.info("adding callout at:" + location);
         SikuliGuideFlag text = new SikuliGuideFlag("Flag");
         SikuliGuideShadow shadow = new SikuliGuideShadow(text);

         text.setLocationRelativeToPoint(location, SikuliGuideComponent.CENTER);         
         text.setMovable(true);
         text.addMouseListener(componentSelector);

         add(shadow,0);
         add(text,0);
         repaint();
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

         add(shadow,0);
         add(text,0);
         repaint();
      }
   }
   
   class LocationSelector extends MouseAdapter{
      Object action;
      boolean running = false;
      public void start(Object action){
         this.action = action;
         this.running = true;
      }
      
      public void stop(){
         this.running = false;
         action = null;
      }

      public void mouseClicked(MouseEvent e) {
         if (!running)
            return;
         
         Debug.info("point selected: " + e.getPoint());
         if (action instanceof AddTextAction){            
            ((AddTextAction) action).locationSelected(e.getPoint());
         }
         
         if (action instanceof AddCalloutAction){               
            ((AddCalloutAction) action).locationSelected(e.getPoint());
         }
         
         if (action instanceof AddFlagAction){               
            ((AddFlagAction) action).locationSelected(e.getPoint());
         }

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

      public void mouseClicked(MouseEvent e) {
         
         
         
         SikuliGuideComponent comp = (SikuliGuideComponent) e.getSource();

         if (action instanceof ConnectAnnotationAndTargetAction){            
            ((ConnectAnnotationAndTargetAction) action).componentSelected(comp);
         }

         if (action instanceof AddCircleToTargetAction){            
            ((AddCircleToTargetAction) action).componentSelected(comp);
         }
         
         if (action instanceof AddBracketToTargetAction){            
            ((AddBracketToTargetAction) action).componentSelected(comp);
         }

         
         if  (e.getClickCount() == 2){
            
            new EditTextPropertyAction().perform(comp);
         }
         
         if (action instanceof EditTextPropertyAction){

            ((EditTextPropertyAction) action).componentSelected(comp);

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
      Env.getOSUtil().setWindowOpacity(this, alpha);
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
}