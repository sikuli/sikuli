/**
 * 
 */
package org.sikuli.guide;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.sikuli.guide.SikuliGuideComponent.Layout;
import org.sikuli.script.Debug;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class SklRelationship implements PropertyChangeListener, Cloneable {
   
   private SklModel parent;
   @Element
   private SklModel dependent;
   
   public SklRelationship(){      
   }
   
   public SklRelationship(SklModel parent, SklModel dependent){
      this.setParent(parent);
      this.setDependent(dependent);      
   }
   
   @Override
   public Object clone() throws CloneNotSupportedException{
      return super.clone();
   }
   
   protected void updateDependent(){
      // TODO: fix opacity relationship
      if (dependent != null && parent != null)
         dependent.setOpacity(getParent().getOpacity());
   }

   @Element
   public void setParent(SklModel parent) {
      if (this.parent != null){
         this.parent.removePropertyChangeListener(this);
      }

      if (parent != null)      
         parent.addPropertyChangeListener(this);
      
      this.parent = parent;
   }

   @Element
   public SklModel getParent() {
      return parent;
   }

   @Override
   public void propertyChange(PropertyChangeEvent e) {
         updateDependent();
   }
   
   public void setDependent(SklModel dependent){
      if (this.dependent != null){
         this.dependent.removePropertyChangeListener(this);
      }

      if (dependent != null)      
         dependent.addPropertyChangeListener(this);
      
      this.dependent = dependent;      
   }

   public SklModel getDependent() {
      return dependent;
   }      
}

class SklSideRelationship extends SklRelationship{

   
   public enum Side{
      TOP,
      BOTTOM,
      LEFT,
      RIGHT,
      FOLLOWERS,
      INSIDE,
      OVER,
      ORIGIN,
      CENTER
   };
   
   @Element
   Side layoutConstant;
   
   SklSideRelationship(){      
   }
   
   SklSideRelationship(SklModel model, SklModel dependent, Side layoutConstant){
      super(model, dependent);
      this.layoutConstant = layoutConstant;
      updateDependent();
   }
   
   @Override
   protected void updateDependent(){
      super.updateDependent();
            
      Rectangle r = new Rectangle(getParent().getX(), getParent().getY(), getParent().getWidth(), getParent().getHeight());
      Dimension d = new Dimension(getDependent().getWidth(), getDependent().getHeight());;
            
      int x=0;
      int y=0;
      if (layoutConstant == Side.TOP){
         x = r.x + r.width/2 - d.width/2;
         y = r.y - d.height;
      } else if (layoutConstant == Side.BOTTOM){
         x = r.x + r.width/2 - d.width/2;
         y = r.y + r.height;
      } else if (layoutConstant == Side.LEFT){
         x = r.x - d.width;
         y = r.y + r.height/2 - d.height/2;   
      } else if (layoutConstant == Side.RIGHT){
         x = r.x + r.width;
         y = r.y + r.height/2 - d.height/2;                  
      } else if (layoutConstant == Side.INSIDE){
         x = r.x + r.width/2 - d.width/2;
         y = r.y + r.height/2 - d.height/2;                  
      } else if (layoutConstant == Side.OVER){
         x = r.x;
         y = r.y;         
         getDependent().setWidth(r.width);
         getDependent().setHeight(r.height);
      } 
      
      getDependent().setX(x);
      getDependent().setY(y);           
   }      
}

@Root
class SklOffsetRelationship extends SklRelationship{

   @Attribute
   private int offsetx;
   @Attribute
   private int offsety;
   
   public SklOffsetRelationship(){      
   }
   
   public SklOffsetRelationship(SklModel parent, SklModel dependent, int offsetx, int offsety){      
      super(parent, dependent);
      this.offsetx = offsetx;
      this.offsety = offsety;
      
      updateDependent();
   }
   
   @Override
   protected void updateDependent(){
      super.updateDependent();      
      getDependent().setX(getParent().getX() + offsetx);
      getDependent().setY(getParent().getY() + offsety);
   }

   public void setOffsetx(int offsetx) {
      this.offsetx = offsetx;
   }

   public int getOffsetx() {
      return offsetx;
   }

   public void setOffsety(int offsety) {
      this.offsety = offsety;
   }

   public int getOffsety() {
      return offsety;
   }

 
}

