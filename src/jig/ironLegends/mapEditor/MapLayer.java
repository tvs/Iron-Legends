package jig.ironLegends.mapEditor;

import java.util.Iterator;
import java.util.Vector;

import jig.engine.RenderingContext;
import jig.engine.util.Vector2D;

/** @class MapLayer
    @brief represents a "layer" of map items (e.g. walls, building etc)
    
         
 */
public class MapLayer 
{
	public MapLayer()
	{
		m_mapItems = new Vector<MapItem>();
	}

	public void add(MapItem item)
	{
		m_mapItems.add(item);	
	}
	public Iterator<MapItem> iterator()
	{
		return m_mapItems.iterator();
	}
	public void render(RenderingContext rc)
	{
		Iterator<MapItem> iter = m_mapItems.iterator();
		
		while (iter.hasNext())
		{
			MapItem m = iter.next();
			m.render(rc);
		}
	}
	
	public int selectClosest(Vector2D pos, double maxDist)
	{
		int closestIdx = -1;
		double dClosest = -1;
		
		Iterator<MapItem> iter = m_mapItems.iterator();

		maxDist *= maxDist;
		int idx = 0;
		while (iter.hasNext())
		{			
			MapItem m = iter.next();
			double dist = pos.distance2(m.getCenterPosition());
			if (dist < maxDist && (dist < dClosest || dClosest < 0))
			{
				closestIdx = idx;
				dClosest = dist;
			}			
			idx++;
		}
		
		return closestIdx;
	}
	
	
	private Vector<MapItem> m_mapItems;


	public void remove(int idx) 
	{
		if (idx >= 0 && idx < m_mapItems.size())
		m_mapItems.remove(idx);
		
	}

	public void clear() 
	{
		m_mapItems.clear();		
	} 
}
