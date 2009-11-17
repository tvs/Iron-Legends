package jig.ironLegends;

import jig.ironLegends.MapLoader.IMapLoadSink;
import jig.ironLegends.mapEditor.MapItemPersist;

public class IronLegendsMapLoadSink implements IMapLoadSink 
{

	IronLegends m_ironLegends;
	
	public IronLegendsMapLoadSink(IronLegends ironLegends)
	{
		m_ironLegends = ironLegends;
	}
	
	@Override
	public void mapDim(int width, int height) 
	{
		m_ironLegends.setWorldDim(width, height);	
	}

	@Override
	public void mapName(String sMapName) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLine(String line) 
	{
		// TODO Auto-generated method stub
		MapItemPersist mapItem = new MapItemPersist(line);
	}

}
