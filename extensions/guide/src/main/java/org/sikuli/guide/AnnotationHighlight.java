/**
 * 
 */
package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

class AnnotationHighlight extends Annotation {

	Rectangle rectangle;
	//protected ScreenImage screen;

	public AnnotationHighlight(Rectangle rectangle){
		super();
		//this.screen = screen;
		this.rectangle = rectangle;
	}
	
	boolean border = true;
	Color border_color = Color.black;
		
	public void setBorder(boolean enable_border){
		border = enable_border;
	}

	public void setBorderColor(Color color){
		border_color = color;
	}
	
	public void paintAnnotation(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;

		if (rectangle == null)
			return;
		
		//BufferedImage image = screen.getImage();
		//BufferedImage subimage = ImageUtil.getSubImageBounded(image, rectangle);
		//g2d.drawImage(subimage, rectangle.x, rectangle.y, null);

		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 0.0f));			
      g2d.setColor(Color.black);
		g2d.fillRect(rectangle.x,rectangle.y,rectangle.width,rectangle.height);

//		Color oldcolor = g2d.getColor();
//      g2d.setColor(Color.black);		
//      g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
//		g2d.drawRect(rectangle.x,rectangle.y,rectangle.width,rectangle.height);
//
//      g2d.setColor(oldcolor);    
		
	}
}