package jig.ironLegends.mapEditor;

import jig.engine.util.Vector2D;
import jig.ironLegends.IronLegends;
import jig.ironLegends.MapLoader.IMapLoadSink;

public class MapEditorLoadSink implements IMapLoadSink 
{
	MapEditor_GS m_mapEditorGS;
	MapLayer m_mapLayer;
	public MapEditorLoadSink(MapEditor_GS mapEditorGS)
	{
		m_mapEditorGS = mapEditorGS;
		m_mapLayer = m_mapEditorGS.m_mapLayer;
	}

	@Override
	public void mapDim(int cols, int rows) 
	{
		
	}

	@Override
	public void mapName(String sMapName) 
	{
		m_mapEditorGS.m_mapName.setText("sMapName");	
	}

	@Override
	public void onLine(String line) 
	{
		// item:x:y:rotDeg:spriteName
		String tokens[] = line.split(":");


		if (tokens.length != 4)
			return;
		
		double x;
		double y;
		double rotDeg;
		String sName = tokens[0];
		x = Double.parseDouble(tokens[1]);
		y = Double.parseDouble(tokens[2]);
		rotDeg = Double.parseDouble(tokens[3]);
		String sSpriteName = tokens[4];
		
		m_mapLayer.add(new SpriteMapItem(new Vector2D(x,y), Math.toRadians(rotDeg), sName, sSpriteName));
	}

}
