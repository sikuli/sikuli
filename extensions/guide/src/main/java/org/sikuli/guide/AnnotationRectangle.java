/**
 * 
 */
package org.sikuli.guide;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

class AnnotationRectangle extends Annotation {
	
	Rectangle rectangle;
	float width;
	public void setWidth(float width) {
		this.width = width;
	}


	public AnnotationRectangle(Rectangle rectangle){
		super();
		this.rectangle = rectangle;
		this.color = Color.green;
		this.width = 3.0F;
	}
	

	public void paintAnnotation(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		paintRectangle(g2d, rectangle, width, color);
	}
}