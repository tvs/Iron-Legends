package jig.ironLegends.screens;

import java.util.List;

import jig.engine.ViewableLayer;
import jig.ironLegends.IronLegends;
import jig.ironLegends.collision.Handler_CPB_CPBLayer;
import jig.ironLegends.collision.Sink_CPB_CPB_Default;
import jig.ironLegends.core.GameScreen;

public class GamePlay_GS extends GameScreen 
{

	IronLegends m_ironLegends;
	
	public GamePlay_GS(int name, IronLegends ironLegends) {
		super(name);
		m_ironLegends = ironLegends;
		
		addViewableLayer(m_ironLegends.m_tankObstacleLayer);
		addViewableLayer(m_ironLegends.m_tankBulletObstacleLayer);
		addViewableLayer(m_ironLegends.m_powerUpLayer);
		addViewableLayer(m_ironLegends.m_batLayer);
		addViewableLayer(m_ironLegends.m_mitkoLayer);
	}
	
	@Override
	public void activate(int prevScreen)
	{
		m_ironLegends.newGame();		
	}
	
	@Override
	public void populateLayers(List<ViewableLayer> gameObjectLayers) {
		super.populateLayers(gameObjectLayers);

		m_ironLegends.m_physicsEngine.manageViewableSet(m_ironLegends.m_mitkoLayer);
		m_ironLegends.m_physicsEngine.manageViewableSet(m_ironLegends.m_batLayer);
		
		// don't hit the obstacles
		m_ironLegends.m_physicsEngine.registerCollisionHandler(
				new Handler_CPB_CPBLayer(m_ironLegends.m_mitko, m_ironLegends.m_tankObstacleLayer
						, new Sink_CPB_CPB_Default()));
		m_ironLegends.m_physicsEngine.registerCollisionHandler(
				new Handler_CPB_CPBLayer(m_ironLegends.m_mitko, m_ironLegends.m_tankBulletObstacleLayer
						, new Sink_CPB_CPB_Default()));
	}
}
