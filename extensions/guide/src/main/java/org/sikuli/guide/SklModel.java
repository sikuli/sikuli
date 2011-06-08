package org.sikuli.guide;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

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
   
   
   public void addPropertyChangeListener(PropertyChangeListener listener);
   public void removePropertyChangeListener(PropertyChangeListener listener);
   
   static public final String PROPERTY_HEIGHT = "height";
   static public final String PROPERTY_WIDTH = "width";
   static public final String PROPERTY_X = "x";
   static public final String PROPERTY_Y = "y";
   
   static public final String PROPERTY_OPACITY = "opacity";
   
}

@Root
class DefaultSklObjectModel implements SklModel {
   
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
   
}
