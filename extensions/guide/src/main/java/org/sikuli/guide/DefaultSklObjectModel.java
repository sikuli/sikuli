package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.swing.JComponent;

import org.sikuli.guide.SklView.ShadowRenderer;
import org.sikuli.guide.util.ComponentMover;
import org.sikuli.script.Debug;
import org.simpleframework.xml.*;
import org.simpleframework.xml.convert.Converter;
import org.simpleframework.xml.stream.InputNode;
import org.simpleframework.xml.stream.OutputNode;

interface Selectable {
   public void setSelected(boolean selected);
   public boolean isSelected();
}

interface SklModel extends Cloneable{   
      
   public void setX(int x);
   public int getX();
   public void setY(int y);
   public int getY();   
   public void setWidth(int width);
   public int getWidth();
   public void setHeight(int width);
   public int getHeight();
   Color getBackground();
   Color getForeground();
   
   public void setOpacity(float opacity);
   public float getOpacity();
   
   public boolean isHasShadow();
   public void setHasShadow(boolean hasShadow);
   
   public void setSelected(boolean selected);
   public boolean isSelected();
   
   
   public void addPropertyChangeListener(PropertyChangeListener listener);
   public void removePropertyChangeListener(PropertyChangeListener listener);
   
   static public final String PROPERTY_HEIGHT = "height";
   static public final String PROPERTY_WIDTH = "width";
   static public final String PROPERTY_X = "x";
   static public final String PROPERTY_Y = "y";
   
   static public final String PROPERTY_SELECTED = "selected";
   static public final String PROPERTY_OPACITY = "opacity";
   
}

interface SklTextModel extends SklModel {
   
   public void setText(String text);
   public String getText();
   
   
   public final int padding = 2;    
   static public final String PROPERTY_TEXT = "text";
}

class SklViewFactory {
   
   public static SklView createView(SklModel model){
      if (model instanceof SklTextModel)
         return new SklTextView(model);
      else if (model instanceof SklAnchorModel)
         return new SklAnchorView(model);
      else if (model instanceof SklImageModel)
         return new SklImageView(model);
      else if (model instanceof SklControlBox)
         return new SklControlBoxView(model);
      else         
         return new SklView(model);
   }      
}

@Root
public class DefaultSklObjectModel implements SklModel {
   
   public DefaultSklObjectModel(){      
   }
       
   @Override
   public Object clone() throws CloneNotSupportedException{
      DefaultSklObjectModel o = (DefaultSklObjectModel) super.clone();
      o.x = x;
      o.y = y;
      o.width = width;
      o.height = height;
      o.backgroundColor = backgroundColor;
      o.foregroundColor = foregroundColor;
      o.hasShadow = hasShadow;
      o.opacity = opacity;
      return o;
   }
   
   protected final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
   
   public void addPropertyChangeListener( PropertyChangeListener listener ){
      this.pcs.addPropertyChangeListener( listener );
   }

   public void removePropertyChangeListener( PropertyChangeListener listener ){
      this.pcs.removePropertyChangeListener( listener );
   }
   
   
//   public SklObjectView createView(){
//      if (this instanceof SklRectangleModel){
//         return new SklRectangleView((SklRectangleModel) this);
//      }else if (this instanceof DefaultSklTextModel){
//         return new SklTextView((DefaultSklTextModel) this);
//      }else if (this instanceof SklFlagModel){
//         return new SklFlagView((SklFlagModel) this);
//      }
//      return new SklObjectView(this);
//   }

   @Attribute
   private int x = 0;
   @Attribute 
   private int y = 0;
   @Attribute
   private int width = 0;
   @Attribute
   private int height = 0;

//   @Element
//   @Convert (ColorConverter.class)
//   private Color foreground = Color.black;   
//
//   @Element
//   @Convert (ColorConverter.class)
//   private Color background = Color.white;
   
   public int padding=5;

   @Attribute
   private boolean hasShadow;
   
   @Attribute 
   private float opacity = 1f;
   
   @Attribute
   String backgroundColor = "FFFFFFFF";
   
   @Attribute
   String foregroundColor = "FF000000";
   
   boolean selected = false;
   
   //private SklObjectView view = null;
   //private SklStepModel step = null;
   
   public void setLocation(Point location) {
      setLocation(x,y);
   }
   public void setLocation(int x, int y) {
      setX(x);
      setY(y);
   }

   public Point getLocation() {
      return new Point(x,y);
   }
   public void setSize(Dimension size) {
      setSize(size.width,size.height);
   }
   
   public void setSize(int width, int height) {
      setWidth(width);
      setHeight(height);
   }

   public Dimension getSize() {
      return new Dimension(width,height);
   }
   
   
   String color2String(Color color){
      return Integer.toHexString(color.getRGB());
   }
   
   Color string2Color(String colorString){
      int rgb = Long.decode("0x"+colorString).intValue();
      return new Color(rgb);
   }
   
   public void setBackground(Color color) {
      this.backgroundColor = color2String(color);
   }
   
   public Color getBackground() {
      return string2Color(backgroundColor);
   }
   
   public void setForeground(Color color) {
      this.foregroundColor = color2String(color);
   }
   
   public Color getForeground() {
      return string2Color(foregroundColor);
   }
   
   
   @Override
   public void setHasShadow(boolean hasShadow) {
      this.hasShadow = hasShadow;
   }
   
   @Override
   public boolean isHasShadow() {
      return hasShadow;
   }
   
   @Override
   public void setOpacity(float opacity) {
      this.pcs.firePropertyChange(PROPERTY_HEIGHT, this.opacity, this.opacity = opacity);
   }
   
   @Override
   public float getOpacity() {
      return opacity;
   }

   public Rectangle getBounds() {
      return new Rectangle(getLocation(), getSize());
   }

   public void setBounds(Rectangle bounds) {
      setLocation(bounds.getLocation());
      setSize(bounds.getSize());
   }

   @Override
   public int getHeight() {
      return height;
   }

   @Override
   public int getWidth() {
      return width;
   }

   @Override
   public int getX() {
      return x;
   }

   @Override
   public int getY() {
      return y;
   }

   
   @Override
   public void setHeight(int height) {
      int old = this.height;
      this.height = height;
      this.pcs.firePropertyChange(PROPERTY_HEIGHT, old, height);
   }

   @Override
   public void setWidth(int width) {
      int old = this.width;
      this.width = width;
      this.pcs.firePropertyChange(PROPERTY_WIDTH, old, width);
   }

   @Override
   public void setX(int x) {
      int old = this.x;
      this.x = x;
      this.pcs.firePropertyChange(PROPERTY_X, old, x);      
   }

   @Override
   public void setY(int y) {
      this.pcs.firePropertyChange(PROPERTY_Y, this.y, this.y = y);      
   }
   
   @Override
   public void setSelected(boolean selected){
      this.pcs.firePropertyChange(PROPERTY_SELECTED, this.selected, this.selected = selected);      
   }
   
   @Override
   public boolean isSelected(){
      return selected;
   }
}

class ColorConverter implements Converter<Color>{

   @Override
   public Color read(InputNode node) throws Exception {
      
      String nm = node.getAttribute("color").getValue();      
      int rgb = Long.decode("0x"+nm).intValue();
      return new Color(rgb);
   }

   @Override
   public void write(OutputNode node, Color color) throws Exception {
      node.setAttribute("color",Integer.toHexString(color.getRGB()));
   }
   
}

class SklView extends JComponent implements PropertyChangeListener {
      
//   protected DefaultSklComponentModel model;
   protected SklModel _model;
//   public SklObjectView(DefaultSklObjectModel model){
////      this.model = model;
////      this.model.setView(this);   
//      init();
//      update();
//   }
   
   public SklView(SklModel model){
      _model = model;
      _model.addPropertyChangeListener(this);      
      init();
      update();
   }
   
   // Initialize the view
   protected void init(){      
   }
   
   Point origin = new Point(0,0);
   
   // Update the view based on the current attributes of the associated model
   protected void update(){
      //setForeground(model.getForeground());
      
      
      //origin = getModel().getStep().getView().getOrigin();
     // Point modelLocation = model.getLocation();      
      //modelLocation.translate(origin.x,origin.y);
      
      setActualLocation(_model.getX(), _model.getY());
      setActualSize(_model.getWidth(), _model.getHeight());
      if (_model.isHasShadow() && shadowRenderer == null){
         shadowRenderer = new ShadowRenderer(this, 10);
      }
   }
   
   public void updateModelLocation(Point newLocation){
      _model.setX(newLocation.x - origin.x);
      _model.setY(newLocation.y - origin.y);
   }
      
   Rectangle actualBounds = new Rectangle();
   public void setActualSize(int width, int height){
      setActualSize(new Dimension(width, height));
   }
   
   public void setActualSize(Dimension actualSize){
      
      actualBounds.setSize(actualSize);
      
      Dimension paintSize = (Dimension) actualSize.clone();

      if (_model.isHasShadow()){
         paintSize.width += (2*shadowSize);
         paintSize.height += (2*shadowSize);
      }
      super.setSize(paintSize);
   }  
   
   public void setActualLocation(int x, int y){
      setActualLocation(new Point(x,y));
   }
   
   public void setActualLocation(Point p){
      
      int paintX = p.x;
      int paintY = p.y;
      
      actualBounds.setLocation(p);
      
      if (_model.isHasShadow()){
         paintX -= (shadowSize-shadowOffset);
         paintY -= (shadowSize-shadowOffset);
      }
      
      super.setLocation(paintX, paintY);
   }
   
   public int getActualWidth(){
      return getActualBounds().width;
   }
   
   public int getActualHeight(){
      return getActualBounds().height;
   }

   public Rectangle getActualBounds() {
      return actualBounds;
   }

   public Dimension getActualSize() {
      return actualBounds.getSize();
   }
   
   public Point getActualLocation() {
      return actualBounds.getLocation();
   }

   
   ShadowRenderer shadowRenderer;
   int shadowSize = 10;
   int shadowOffset = 2;
   
   class ShadowRenderer {

      SklView source;
      public ShadowRenderer(SklView source, int shadowSize){
         this.source = source;
         sourceActualSize = source.getActualSize();
         this.shadowSize = shadowSize;
      }

      float shadowOpacity = 0.8f;
      int shadowSize = 10;
      Color shadowColor = Color.black;
      BufferedImage createShadowMask(BufferedImage image){ 
         BufferedImage mask = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB); 

         Graphics2D g2d = mask.createGraphics(); 
         g2d.drawImage(image, 0, 0, null); 
         // Ar = As*Ad - Cr = Cs*Ad -> extract 'Ad' 
         g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, shadowOpacity)); 
         g2d.setColor(shadowColor); 
         g2d.fillRect(0, 0, image.getWidth(), image.getHeight()); 
         g2d.dispose(); 
         return mask; 
      } 

      ConvolveOp getBlurOp(int size) {
         float[] data = new float[size * size];
         float value = 1 / (float) (size * size);
         for (int i = 0; i < data.length; i++) {
            data[i] = value;
         }
         return new ConvolveOp(new Kernel(size, size, data));
      }

      BufferedImage shadowImage = null;
      Dimension sourceActualSize = null;
      public BufferedImage createShadowImage(){    

         BufferedImage image = new BufferedImage(source.getActualWidth() + shadowSize * 2,
               source.getActualHeight() + shadowSize * 2, BufferedImage.TYPE_INT_ARGB);
         Graphics2D g2 = image.createGraphics();
         g2.translate(shadowSize,shadowSize);
         source.paintPlain(g2);

         shadowImage = new BufferedImage(image.getWidth(),image.getHeight(),BufferedImage.TYPE_INT_ARGB);
         getBlurOp(shadowSize).filter(createShadowMask(image), shadowImage);
         g2.dispose();
         
         //Debug.info("[Shadow] shadowImage created: " + shadowImage);

         return shadowImage;
      }

      public void paintComponent(Graphics g){      
         Graphics2D g2d = (Graphics2D)g;

         // create shadow image if the size of the source component has changed since last rendering attempt
         if (shadowImage == null || source.getActualHeight() != sourceActualSize.height || 
               source.getActualWidth() != sourceActualSize.width){
            createShadowImage();
            sourceActualSize = source.getActualSize();
         }
         //Debug.info("[Shadow] painting shadow" + shadowImage);
         g2d.drawImage(shadowImage, 0, 0, null, null);
      }
   }
   
   public void paintPlain(Graphics g){
      super.paint(g);
   }

   public void paint(Graphics g){
      
      // render the component in an offscreen buffer with shadow
      BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D g2 = image.createGraphics();
      
      if (shadowRenderer != null){
         shadowRenderer.paintComponent(g2);
         g2.translate((shadowSize-shadowOffset),(shadowSize-shadowOffset));
      }
      
      super.paint(g2);
      
      Graphics2D g2d = (Graphics2D) g;      
      ((Graphics2D) g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,_model.getOpacity()));
      g2d.drawImage(image,0,0,null,null);


      if (_model.isSelected()){
          Rectangle r = getBounds();
          g2d.setColor(Color.green);
          g2d.drawRect(0,0,r.width-1,r.height-1);
      }
      
      // Debug draw
      
//      Rectangle r = getBounds();
//      g2d.setColor(Color.red);
//      g2d.drawRect(0,0,r.width-1,r.height-1);
      
   }

   public SklModel getModel() {
      return _model;
   }

   @Override
   public void propertyChange(PropertyChangeEvent evt) {
      
//      if (getParent() != null){
//         Rectangle r = new Rectangle(getBounds());
//         getParent().repaint(r.x,r.y,r.width,r.height);
////         getTopLevelAncestor().repaint();
//      }

      
      if (evt.getPropertyName().equals(SklModel.PROPERTY_X)){         
         setActualLocation(_model.getX(), getActualLocation().y);
      } else if (evt.getPropertyName().equals(SklModel.PROPERTY_Y)){      
         setActualLocation(getActualLocation().x, _model.getY());
      } else if (evt.getPropertyName().equals(SklModel.PROPERTY_WIDTH)){
         setActualSize(_model.getWidth(), getActualSize().height);
      } else if (evt.getPropertyName().equals(SklModel.PROPERTY_HEIGHT)){
         setActualSize(getActualSize().width, _model.getHeight());
      } else if (evt.getPropertyName().equals(SklModel.PROPERTY_SELECTED)){

      } else if (evt.getPropertyName().equals(SklModel.PROPERTY_OPACITY)){

      } else {  
         return;
      }

      if (getParent() != null){
         Rectangle r = new Rectangle(getBounds());
         //getParent().repaint(r.x,r.y,r.width,r.height);
         //getParent().repaint();
         // TODO: fix this
         if (getTopLevelAncestor() != null)
            getTopLevelAncestor().repaint();
      }

   }
}