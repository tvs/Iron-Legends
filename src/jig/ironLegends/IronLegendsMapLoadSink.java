package jig.ironLegends;

import jig.ironLegends.MapLoader.IMapLoadSink;
import jig.ironLegends.mapEditor.MapItemPersist;

public class IronLegendsMapLoadSink implements IMapLoadSink {

	IronLegends m_ironLegends;

	int m_redspawnSeq;
	int m_bluespawnSeq;
	
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
		m_redspawnSeq = 0;
		m_bluespawnSeq = 0;
		m_ironLegends.m_spawnInfo.clear();
	}

	@Override
	public void onLine(String line) {
		MapItemPersist mapItem = new MapItemPersist(line);

		if (mapItem.name().equals("redspawn"))
		{
			SpawnInfo s = new SpawnInfo(mapItem.name(), m_redspawnSeq);
			m_redspawnSeq++;
			
			s.setCenterPosition(mapItem.centerPosition());
			s.setRotationDeg(mapItem.rotDeg());
			m_ironLegends.m_spawnInfo.add(s);
		}
		else if (mapItem.name().equals("bluespawn"))
		{
			SpawnInfo s = new SpawnInfo(mapItem.name(), m_bluespawnSeq);
			m_bluespawnSeq++;
			
			s.setCenterPosition(mapItem.centerPosition());
			s.setRotationDeg(mapItem.rotDeg());
			m_ironLegends.m_spawnInfo.add(s);
		}
		else if (mapItem.name().equals("wall") 	|| 
			mapItem.name().equals("building")||
			mapItem.name().equals("crate") ) {
			Obstacle ob = new Obstacle(line, m_ironLegends.m_polygonFactory);
			m_ironLegends.m_tankBulletObstacleLayer.add(ob);
		} else if (mapItem.name().equals("tree")  ||
				   mapItem.name().equals("rock1") ||
				   mapItem.name().equals("rock2")   ){
			Obstacle ob = new Obstacle(line, m_ironLegends.m_polygonFactory);
			m_ironLegends.m_tankObstacleLayer.add(ob);
		} else {
			Obstacle ob = new Obstacle(line, m_ironLegends.m_polygonFactory);
			m_ironLegends.m_tankBulletObstacleLayer.add(ob);
		}
		
		
	}

}
