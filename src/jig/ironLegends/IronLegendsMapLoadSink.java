package jig.ironLegends;

import jig.ironLegends.MapLoader.IMapLoadSink;
import jig.ironLegends.mapEditor.MapItemPersist;

public class IronLegendsMapLoadSink implements IMapLoadSink {

	IronLegends m_ironLegends;

	public IronLegendsMapLoadSink(IronLegends ironLegends) {
		m_ironLegends = ironLegends;
	}

	@Override
	public void mapDim(int width, int height) {
		m_ironLegends.setWorldDim(width, height);
	}

	@Override
	public void mapName(String sMapName) {
		m_ironLegends.setMapName(sMapName);
	}

	@Override
	public void onLine(String line) {
		MapItemPersist mapItem = new MapItemPersist(line);
		Obstacle ob = new Obstacle(line, m_ironLegends.m_polygonFactory);

		if (mapItem.name().equals("wall") || mapItem.name().equals("building")) {
			m_ironLegends.m_tankBulletObstacleLayer.add(ob);
		} else {
			// "tree"
			m_ironLegends.m_tankObstacleLayer.add(ob);
		}
	}

}
