package jig.ironLegends.screens;

import jig.ironLegends.IronLegends;
import jig.ironLegends.core.GameScreen;

public class GamePlay_GS extends GameScreen 
{

	IronLegends m_ironLegends;
	
	public GamePlay_GS(int name, IronLegends ironLegends) {
		super(name);
		m_ironLegends = ironLegends;
	}
	
	public void activate(int prevScreen)
	{
		//m_ironLegends.populateGameLayers();
		// TODO probably move all the items below to GamePlay_GS if possible instead of accessing it from ironlegends
		//m_ironLegends.m_physicsEngine.manageViewableSet(m_ironLegends.m_mitkoLayer);
		//m_ironLegends.m_physicsEngine.manageViewableSet(m_ironLegends.m_batLayer);
	}
	

}
