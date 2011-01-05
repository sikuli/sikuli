/**
 * 
 */
package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Stroke;

class AnnotationText extends Annotation{
	

	String text;
	int x,y;
	public AnnotationText(String text, int x, int y){
		this.text = text;
		this.x = x;
		this.y = y;
		color = Color.red;
	}
	
	public void setColor(Color color){
		this.color = color;
	}
	
	public void paintAnnotation(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(color);
		Stroke pen = new BasicStroke(3.0F);
		g2d.setStroke(pen);
		g2d.drawString(text, x, y);
	}
}