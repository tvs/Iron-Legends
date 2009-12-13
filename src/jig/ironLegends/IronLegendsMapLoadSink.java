package jig.ironLegends;

import jig.ironLegends.MapLoader.IMapLoadSink;
import jig.ironLegends.mapEditor.MapItemPersist;

public class IronLegendsMapLoadSink implements IMapLoadSink {

	IronLegends m_ironLegends;

	int m_redspawnSeq;
	int m_bluespawnSeq;
	int m_mapItemSeq; // rolling counter for all map items
	
	// returns the current map item sequence (if done loading, this will be the max map item count)
	public int mapItemSeq()
	{
		return m_mapItemSeq;
	}
	
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
		m_ironLegends.m_redBase = null;
		m_ironLegends.m_blueBase = null;
		
		m_mapItemSeq = 0;
	}

	@Override
	public void onLine(String line) {
		MapItemPersist mapItem = new MapItemPersist(line);

		Obstacle ob = null;
		
		if (mapItem.name().equals("redspawn"))
		{
			SpawnInfo s = new SpawnInfo(mapItem.name(), m_redspawnSeq, m_mapItemSeq);
			m_mapItemSeq++;
			m_redspawnSeq++;
			
			s.setCenterPosition(mapItem.centerPosition());
			s.setRotationDeg(mapItem.rotDeg());
			m_ironLegends.m_spawnInfo.add(s);
		}
		else if (mapItem.name().equals("bluespawn"))
		{
			SpawnInfo s = new SpawnInfo(mapItem.name(), m_bluespawnSeq, m_mapItemSeq);
			m_mapItemSeq++;
			m_bluespawnSeq++;
			
			s.setCenterPosition(mapItem.centerPosition());
			s.setRotationDeg(mapItem.rotDeg());
			m_ironLegends.m_spawnInfo.add(s);
		}
		else if (mapItem.name().equals("redbase"))
		{
			ob = new Obstacle(line, m_ironLegends.m_polygonFactory, m_mapItemSeq);
			if (!m_ironLegends.isMultiPlayerMode()) {
				ob.setActivation(false);
			}
			m_mapItemSeq++;
			m_ironLegends.m_tankBulletObstacleLayer.add(ob);
			m_ironLegends.m_redBase = ob;
		}
		else if (mapItem.name().equals("bluebase"))
		{
			ob = new Obstacle(line, m_ironLegends.m_polygonFactory, m_mapItemSeq);
			if (!m_ironLegends.isMultiPlayerMode()) {
				ob.setActivation(false);
			}
			m_mapItemSeq++;
			m_ironLegends.m_tankBulletObstacleLayer.add(ob);
			m_ironLegends.m_blueBase = ob;
		}
		else if (mapItem.name().equals("wall") 	|| 
			mapItem.name().equals("building")||
			mapItem.name().equals("crate") ) {
			ob = new Obstacle(line, m_ironLegends.m_polygonFactory, m_mapItemSeq);
			m_mapItemSeq++;
			m_ironLegends.m_tankBulletObstacleLayer.add(ob);
		} else if (mapItem.name().equals("tree")  ||
				   mapItem.name().equals("rock1") ||
				   mapItem.name().equals("rock2")   ){
			ob = new Obstacle(line, m_ironLegends.m_polygonFactory, m_mapItemSeq);
			m_mapItemSeq++;
			m_ironLegends.m_tankObstacleLayer.add(ob);
		} else {
			ob = new Obstacle(line, m_ironLegends.m_polygonFactory, m_mapItemSeq);
			m_mapItemSeq++;
			m_ironLegends.m_tankBulletObstacleLayer.add(ob);
		}
		if (ob != null)
			m_ironLegends.m_obstacles.put(ob.getMapItemSeq(), ob);
		
		
	}

}
