package jig.ironLegends.mapEditor;

import jig.engine.RenderingContext;
import jig.engine.util.Vector2D;

/** @class MapItem
    @brief represents an item on the map
 */
public abstract class MapItem 
{
	MapItem()
	{
	}
	
	abstract Vector2D getCenterPosition();
	abstract void render(RenderingContext rc);
}
