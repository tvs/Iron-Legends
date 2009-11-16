package jig.ironLegends.mapEditor;

import java.util.Iterator;

import jig.ironLegends.MapLoader.IMapSave;


public class MapEditorSave implements IMapSave 
{
	MapLayer m_mapLayer;
	MapEditor_GS m_mapEditorGS;
	int m_mapWidth;
	int m_mapHeight;
	Iterator<MapItem> m_iter; 
	
	// TODO: rows cols is size of map
	MapEditorSave(int width, int height, MapEditor_GS mapEditorGS)
	{
		m_mapEditorGS	= mapEditorGS;
		m_mapLayer 		= m_mapEditorGS.m_mapLayer;
		m_mapWidth 		= width;
		m_mapHeight 	= height;
		
		m_iter = m_mapLayer.iterator();
	}
	
	@Override
	public int cols() 
	{
		return m_mapWidth;
	}

	@Override
	public String mapName() 
	{
		return m_mapEditorGS.m_mapName.getText();
	}

	@Override
	public String nextLine() 
	{
		
		if (m_iter.hasNext())
		{
			MapItem m = m_iter.next();
			return m.encoding();
		}
		
		return null;
	}

	@Override
	public int rows() 
	{
		return m_mapHeight;
	}
}
