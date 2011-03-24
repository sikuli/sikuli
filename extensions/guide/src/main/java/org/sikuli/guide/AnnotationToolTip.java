/**
 * 
 */
package org.sikuli.guide;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;

import javax.swing.JComponent;

public class AnnotationToolTip{// extends Annotation{

	// style parameters
	final int spacing = 10;	// spacing between the box and the target
	final float hp = 0.2f;
	final int thickness = 20;

	String _text;
	Point _target;
	
	public AnnotationToolTip(String text, Point target){
		super();
		_text = text;
		_target = target;
	}
	
	void paintContainer(Graphics2D g2d, Rectangle rect){

		Stroke pen = new BasicStroke(1.0F);
		g2d.setStroke(pen);
		
		g2d.setColor(new Color(255,250,205));
		g2d.fillRect(rect.x,rect.y,rect.width,rect.height);

		g2d.setColor(new Color(238,221,130));
		g2d.drawRect(rect.x,rect.y,rect.width,rect.height);
	}
	
	public void paintAnnotation(Graphics g){
		Graphics2D g2d = (Graphics2D) g;
		
		if (_text == null || _target == null)
			return;
		
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);

		
		//Font f = new Font("sansserif", Font.BOLD, 32);
		//Font f = new Font("sansserif", Font.BOLD, 12);
		Font f = new Font("sansserif", Font.PLAIN, 12);
		g2d.setFont(f);
		
		FontMetrics fm = g2d.getFontMetrics(f);
		int width = fm.stringWidth(_text);
		int height = fm.getHeight();
		
		// calculate the position of the tool tip
		//int y = _target.y + spacing + height;	// below the target
		//int x = _target.x;// - Math.round(0.2f * width);
		
		Rectangle rect = new Rectangle(_target.x,_target.y,width,height);
		
		paintContainer(g2d, rect);
		
		g2d.setColor(Color.black);
		g2d.drawString(_text, _target.x, _target.y + height - 2);
	}
}