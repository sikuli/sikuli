/**
 * 
 */
package org.sikuli.guide;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.lang.reflect.Array;

public class SikuliGuideArrow extends SikuliGuideComponent{

   
   
	public Point getFrom() {
		return from;
	}

	public Point getTo() {
		return to;
	}

	Point from;
	Point to;
	public SikuliGuideArrow(SikuliGuide g, Point from, Point to){
	   super(g);
		this.from = from;
		this.to = to;
		
		Rectangle r = new Rectangle(from);
		r.add(to);
		r.grow(10,10);		
		setBounds(r);
		setForeground(Color.red);
	
	}
	
	


	public void paintAnnotation1(Graphics gc)
	{
		Graphics2D g = (Graphics2D) gc;

		// as we're filling rather than stroking, control point is at the apex,

		//float arrowRatio = 0.5f;
		//float arrowLength = 80.0f;

		if (to.distance(from) < 2)
			return;

		float arrowRatio = (to.x - from.x)/(to.y-from.y);
		float arrowLength = (float) to.distance(from);



		BasicStroke stroke = ( BasicStroke ) g.getStroke();

		//float endX = 350.0f;
		float endX = to.x;

		float veeX = endX - stroke.getLineWidth() * 0.5f / arrowRatio;

		// vee
		Path2D.Float path = new Path2D.Float();

		float waisting = 0.5f;

		float waistX = endX - arrowLength * 0.5f;
		float waistY = arrowRatio * arrowLength * 0.5f * waisting;
		float arrowWidth = arrowRatio * arrowLength;

		path.moveTo ( veeX - arrowLength, -arrowWidth );
		path.quadTo ( waistX, -waistY, endX, 0.0f );
		path.quadTo ( waistX, waistY, veeX - arrowLength, arrowWidth );

		// end of arrow is pinched in
		path.lineTo ( veeX - arrowLength * 0.75f, 0.0f );
		path.lineTo ( veeX - arrowLength, -arrowWidth );

		g.setColor ( Color.BLUE );
		g.fill ( path );

		// move stem back a bit
		g.setColor ( Color.RED );
		g.draw( new Line2D.Float ( 50.0f, 0.0f, veeX - arrowLength * 0.5f, 0.0f ) );

		g.drawLine(from.x,from.y,to.x,to.y);
	}

	private void drawPolylineArrow(Graphics g, int[] xPoints, int[] yPoints, int headLength, int headwidth){

		double theta1;

		//calculate the length of the line - convert from Object to Integer to int value

		Object tempX1 = ((Array.get(xPoints, ((xPoints.length)-2))) );

		Object tempX2 = ((Array.get(xPoints, ((xPoints.length)-1))) );

		Integer fooX1 = (Integer)tempX1;

		int x1 = fooX1.intValue();

		Integer fooX2 = (Integer)tempX2;

		int x2 = fooX2.intValue();

		Object tempY1 = ((Array.get(yPoints, ((yPoints.length)-2))) );

		Object tempY2 = ((Array.get(yPoints, ((yPoints.length)-1))) );

		Integer fooY1 = (Integer)tempY1;

		int y1 = fooY1.intValue();

		Integer fooY2 = (Integer)tempY2;

		int y2 = fooY2.intValue();

		int deltaX = (x2-x1);

		int deltaY = (y2-y1);

		double theta = Math.atan((double)(deltaY)/(double)(deltaX));

		if (deltaX < 0.0){

			theta1 = theta+Math.PI; //If theta is negative make it positive

		}

		else{

			theta1 = theta; //else leave it alone

		}

		int lengthdeltaX =- (int)(Math.cos(theta1)*headLength);

		int lengthdeltaY =- (int)(Math.sin(theta1)*headLength);

		int widthdeltaX = (int)(Math.sin(theta1)*headwidth);

		int widthdeltaY = (int)(Math.cos(theta1)*headwidth);

		g.drawPolyline(xPoints, yPoints, xPoints.length);

		g.drawLine(x2,y2,x2+lengthdeltaX+widthdeltaX,y2+lengthdeltaY-widthdeltaY);

		g.drawLine(x2,y2,x2+lengthdeltaX-widthdeltaX,y2+lengthdeltaY+widthdeltaY);

	}//end drawPolylineArrow

	public void paint(Graphics g) {
	   super.paint(g);
	   
		Graphics2D g2d = (Graphics2D) g;

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);       

		Rectangle r = getBounds();
		
		Stroke pen = new BasicStroke(3.0F);
		g2d.setStroke(pen);
		g2d.translate(-r.x,-r.y);
      //g2d.translate(10,10);
      
		drawPolylineArrow(g, new int[]{from.x,to.x}, new int[]{from.y,to.y}, 6, 6);
	}
}