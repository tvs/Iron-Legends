package jig.ironLegends;

import jig.engine.util.Vector2D;

public class Math2D 
{

	public static final double TWOpi = Math.PI*2.0;

	private Math2D(){}
	// ------------------------------------------------------------------------
	//	Bearing between two points in radians (0 ,2Pi, is positive y axis aka +y -> "north")
	static double trueBrg(Vector2D point, Vector2D origin)
	{
		// + y axis is (0 aka north)
		double dBearing = Math.atan2((double)(point.getX() - origin.getX()), (double)(point.getY() - origin.getY()));
		//double dBearing = Math.atan2((double)(point.getY() - origin.getY()), (double)(point.getX() - origin.getX()));
	
		if (dBearing <= 0)
			dBearing  += TWOpi;
	
		return dBearing;
	}
	// ------------------------------------------------------------------------
	// Distance between two points
	static double getDistance(Vector2D point1, Vector2D point2)
	{
		//return Math.sqrt(point1.distance2(point2));
		
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
}
