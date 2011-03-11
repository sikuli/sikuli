/**
 * 
 */
package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import org.sikuli.script.Region;

public class Spotlight extends Annotation {

	Rectangle rectangle;
//	Region region;
	//protected ScreenImage screen;
	
	public final static int RECTANGLE = 0;
   public final static int CIRCLE = 1;
	

	public Spotlight(Rectangle rectangle){
		super();
		this.rectangle = rectangle;
	}
	
	boolean border = true;
	Color border_color = Color.black;
	int style = RECTANGLE;
	
	public void setBorder(boolean enable_border){
		border = enable_border;
	}

	public void setBorderColor(Color color){
		border_color = color;
	}
	
	public void setStyle(int style){
	   this.style = style;
	}
	
	public void setRegion(Region region){
	   this.rectangle = region.getRect();
	}
	
	public void paintAnnotation(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if (rectangle == null)
			return;
	
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));			
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		      RenderingHints.VALUE_ANTIALIAS_ON);       

		if (style == RECTANGLE){
		   g2d.fillRect(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
		}else if (style == CIRCLE){
	      Ellipse2D.Double ellipse =
	         new Ellipse2D.Double(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
	      g2d.fill(ellipse);		   

	      // adding visual ringing effect
	      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));        
	      int ds[] =  {15,10,5};
	      
	      for (int d: ds){
	         ellipse =
	            new Ellipse2D.Double(rectangle.x-d,rectangle.y-d,rectangle.width+2*d,rectangle.height+2*d);
	         g2d.setColor(Color.white);
	         g2d.fill(ellipse);
	      }
		}
		
	
	}
}