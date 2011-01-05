package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

public class Annotation {

	protected Color color;

	public Annotation() {
	}

	public void paintAnnotation(Graphics g) {
	}

	protected void paintRectangle(Graphics2D g2d, Rectangle r, float width,
			Color color) {
		Stroke old_pen = g2d.getStroke();
		Stroke Pen = new BasicStroke(width);
		g2d.setStroke (Pen);
		g2d.setColor(color);
		g2d.drawRect(r.x,r.y,r.width,r.height);
		g2d.setStroke(old_pen);
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

}