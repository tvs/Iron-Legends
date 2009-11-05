package jig.ironLegends;

import jig.engine.util.Vector2D;
import jig.ironLegends.MPline2D;

public class MPmath2D {

	static final double TWOpi = Math.PI*2.0;

	private MPmath2D(){}
	// ------------------------------------------------------------------------
	//	Bearing between two points in radians (0 ,2Pi, is positive y axis)
	static double trueBrg(Vector2D point, Vector2D origin)
	{
		double dBearing = Math.atan2((double)(point.getX() - origin.getX()), (double)(point.getY() - origin.getY()));
	
		if (dBearing <= 0)
			dBearing  += TWOpi;
	
		return dBearing;
	}
	// ------------------------------------------------------------------------
	// Distance between two points
	static double getDistance(Vector2D point1, Vector2D point2)
	{
		
		double deltaX = point1.getX() - point2.getX();
		double deltaY = point1.getY() - point2.getY();

		return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}

	static double deltaAngle(final double angleStart, final double angleEnd)
	{
		double dDeltaAngle = angleEnd - angleStart;

		if (dDeltaAngle < 0)
			dDeltaAngle += TWOpi;

		return dDeltaAngle;
	}
	
	public static Vector2D reflectAcross(final MPline2D line, final Vector2D pt)
	{
		double magnitude			= getDistance(line.initial, pt);
		double dBearingOfLine		= trueBrg(line.terminal, line.initial);
		double dBearingToXY			= trueBrg(pt, line.initial);
		double dBearingToReflectPt	= dBearingOfLine - deltaAngle(dBearingOfLine, dBearingToXY);

		if (dBearingToReflectPt<0)
			dBearingToReflectPt += TWOpi;

		//reflect point
		double x = magnitude*Math.sin(dBearingToReflectPt);
		double y = magnitude*Math.cos(dBearingToReflectPt);

		x += line.initial.getX();
		y += line.initial.getY();

		return new Vector2D(x,y);
	}

	static public class SlopeInterceptResult
	{
		public SlopeInterceptResult(){}
		public double m;
		public double b;
	}
	// ------------------------------------------------------------------------
	//	Finds intersecting point between two lines in 2 space
	//	returns false if: parallel lines
	// ------------------------------------------------------------------------

	static Vector2D Intersection(MPline2D line1, MPline2D line2)
	{
		boolean bIntercept = true;
	
		boolean bLine1Vert = false;
		boolean bLine2Vert = false;
		boolean bLine1Horz = false;
		boolean bLine2Horz = false;
		double x = 0;
		double y = 0;
	
		if (line1.isVertical())
			bLine1Vert = true;
	
		if (line1.isHorizontal())
			bLine1Horz = true;
	
		if (line2.isVertical())
			bLine2Vert = true;
	
		if (line2.isHorizontal())
			bLine2Horz = true;
	
		if (bLine1Vert || bLine1Horz || bLine2Vert || bLine2Horz){
			if (bLine1Vert && bLine2Vert ||
					bLine1Horz && bLine2Horz || 
					bLine1Vert && bLine1Horz ||
					bLine2Vert && bLine2Horz	 ){
				bIntercept = false;
			}else if (bLine1Vert && bLine2Horz){
				x = line1.initial.getX();
				y = line2.initial.getY();
			}else if (bLine2Vert && bLine1Horz){
				x = line2.initial.getX();
				y = line1.initial.getY();
			}else{
				bIntercept = false;
	
				SlopeInterceptResult mb = new SlopeInterceptResult();
				if (bLine1Vert){
					x = line1.initial.getX();
					bIntercept = SlopeIntercept(line2, mb);
	
					if (bIntercept)
						y = mb.m*x + mb.b;
				}else if (bLine2Vert){
					x = line2.initial.getX();
					bIntercept = SlopeIntercept(line1, mb);
	
					if (bIntercept)
						y = mb.m*x + mb.b;
				}else if (bLine1Horz){
					y = line1.initial.getY();
					bIntercept = SlopeIntercept(line2,mb);
	
					if (bIntercept)
						x = (y - mb.b)/mb.m;
				}else if (bLine2Horz){
					y = line2.initial.getY();
					bIntercept = SlopeIntercept(line1, mb);
	
					if (bIntercept)
						x = (y - mb.b)/mb.m;
				}
			}
		}else{
			SlopeInterceptResult mb1 = new SlopeInterceptResult();
			SlopeInterceptResult mb2 = new SlopeInterceptResult();
			if (SlopeIntercept(line1, mb1) && SlopeIntercept(line2, mb2))
			{
				if (mb1.m == mb2.m){
					bIntercept = false;
				}else{
					x = (mb2.b-mb1.b)/(mb1.m-mb2.m);
					y = (mb1.m*x) + mb1.b;
				}
			}else{
				bIntercept = false;
			}
	
		}
		if (bIntercept)
			return new Vector2D(x,y);
		
		return null;
	}

	// -------------------------------------------------------------------
	//	Computes slope and y intercept for a non vertical, non horizontal line
	// -------------------------------------------------------------------
	
	static boolean SlopeIntercept(MPline2D line, SlopeInterceptResult result)
	{
		boolean bComputed = false;
	
		if (line.terminal.getX() != line.initial.getX() && 
				line.terminal.getY() != line.initial.getY()		){
	
			result.m = (line.terminal.getY() - line.initial.getY())/(line.terminal.getX() - line.initial.getX());
			result.b = line.initial.getY() - (result.m * line.initial.getX());
	
			bComputed = true;
		}
	
		return bComputed;
	}

//		Find The intersecting point
//		The bounds are indicated by lines 1 and 2
	public static Vector2D boundedIntersection(
			  final MPline2D line1
			, final MPline2D line2
			)
	{
		double xMax, xMin, yMax, yMin;
		boolean bSuccess = true;

		Vector2D intersectPt = null;
		
		intersectPt = Intersection(line1, line2);
		
		if (bSuccess)
		{
			//BOUND FOR LINE 2
			//Get Bounds for x
			if(line2.initial.getX() > line2.terminal.getX()){
				xMax = line2.initial.getX(); xMin = line2.terminal.getX();
			}else{
				xMax = line2.terminal.getX(); xMin = line2.initial.getX();
			}
			
			//Get Bounds for y
			if(line2.initial.getY() > line2.terminal.getY()){
				yMax = line2.initial.getY(); yMin = line2.terminal.getY();
			}else{
				yMax = line2.terminal.getY(); yMin = line2.initial.getY();
			}

			//See if the Intersection lies within the two points
			if( intersectPt.getX() <= xMax && intersectPt.getX() >= xMin && 
				intersectPt.getY() <= yMax && intersectPt.getY() >= yMin)
				bSuccess = true;
			else 
				bSuccess = false;
		}
		
		//BOUND For Line 1
		if(bSuccess){
			//Get Bounds for x
			if(line1.initial.getX() > line1.terminal.getX()){
				xMax = line1.initial.getX(); xMin = line1.terminal.getX();
			}else{
				xMax = line1.terminal.getX(); xMin = line1.initial.getX();
			}
			
			//Get Bounds for y
			if(line1.initial.getY() > line1.terminal.getY()){
				yMax = line1.initial.getY(); yMin = line1.terminal.getY();
			}else{
				yMax = line1.terminal.getY(); yMin = line1.initial.getY();
			}

			//See if the Intersection lies within the two points
			if(intersectPt.getX() <= xMax && intersectPt.getX() >= xMin &&
					intersectPt.getY() <= yMax && intersectPt.getY() >= yMin)
				bSuccess = true;
			else 
				bSuccess = false;
		}
		
		if (bSuccess)
			return intersectPt;
		return null;
	}
}
