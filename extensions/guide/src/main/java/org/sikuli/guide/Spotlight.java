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

public class Spotlight extends Annotation {

	Rectangle rectangle;
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
	
	public void paintAnnotation(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if (rectangle == null)
			return;
		
		//BufferedImage image = screen.getImage();
		//BufferedImage subimage = ImageUtil.getSubImageBounded(image, rectangle);
		//g2d.drawImage(subimage, rectangle.x, rectangle.y, null);

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));			
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
		      RenderingHints.VALUE_ANTIALIAS_ON);       
		//      g2d.setColor(Color.black);
//		g2d.fillRect(rectangle.x,rectangle.y,rectangle.width,rectangle.height);

		if (style == RECTANGLE){
		   g2d.fillRect(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
		}else if (style == CIRCLE){
	      Ellipse2D.Double ellipse =
	         new Ellipse2D.Double(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
	      g2d.fill(ellipse);		   
		}
		
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));        		
		
		
//		Color oldcolor = g2d.getColor();
//      g2d.setColor(Color.black);		
//      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
//		g2d.drawRect(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
//
//      g2d.setColor(oldcolor);    
		
	}
}