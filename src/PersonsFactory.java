


import jig.engine.physics.vpe.ConvexPolygon;
import jig.engine.physics.vpe.PersonsConvexPolygon;
import jig.engine.util.Vector2D;
import jig.misc.sat.PolygonFactory;

/**
 * Factory methods for the (stub) ConvexPolygon class.
 * 
 * @author Scott Wallace (modified to create PersonsConvexPolygon by Michael JP Persons : )
 *
 */
public class PersonsFactory implements PolygonFactory {
	
	public ConvexPolygon createRectangle(Vector2D origin, double w, double h) {
		return new PersonsConvexPolygon(origin, w, h);
	}
	
	public ConvexPolygon createNGon(Vector2D origin, double radius, int n) {
		return new PersonsConvexPolygon(origin, radius, n);
	}

}
