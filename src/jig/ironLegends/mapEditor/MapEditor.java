package jig.ironLegends.mapEditor;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.Sprite;
import jig.engine.hli.ScrollingScreenGame;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;
import jig.ironLegends.GridCell;
import jig.ironLegends.IronLegends;
import jig.ironLegends.MapGrid;
import jig.ironLegends.MapLoader;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.GameScreens;
import jig.ironLegends.core.InstallInfo;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.KeyState;
import jig.ironLegends.core.ResourceReader;
import jig.ironLegends.core.ui.MouseState;

public class MapEditor extends ScrollingScreenGame 
{
	protected String m_sInstallDir;
	protected ResourceReader m_rr;
	protected Fonts m_fonts = new Fonts();
	protected GameScreens m_screens = new GameScreens();
	protected MapEditorGrid m_grid; // just basic text info for grid
	protected Point m_centerPt;

	public static final int MAPEDIT_SCREEN = 0;

	KeyCommands m_keyCmds = new KeyCommands();
	/*
	class MapGrid extends LevelGrid
	{
	*/
	MapCalc m_mapCalc = new MapCalc(IronLegends.SCREEN_WIDTH, IronLegends.SCREEN_HEIGHT);

	public MapEditor() 
	{
		super(IronLegends.SCREEN_WIDTH, IronLegends.SCREEN_HEIGHT, false);

		gameframe.setTitle("Iron Legends");

		setWorldBounds(0,0, IronLegends.WORLD_WIDTH, IronLegends.WORLD_HEIGHT);
		m_mapCalc.setWorldBounds(0,0, IronLegends.WORLD_WIDTH, IronLegends.WORLD_HEIGHT);
		
		m_sInstallDir 	= InstallInfo.getInstallDir("/" + IronLegends.GAME_ROOT + "IronLegends.class", "IronLegends.jar");
		m_rr 			= new ResourceReader(m_sInstallDir);
		m_centerPt = new Point(IronLegends.WORLD_WIDTH/2, IronLegends.WORLD_HEIGHT/2);
		
		loadResources();
		
		int m_rows = IronLegends.WORLD_WIDTH/IronLegends.TILE_WIDTH;
		int m_cols = IronLegends.WORLD_HEIGHT/IronLegends.TILE_HEIGHT;
		
		m_grid = new MapEditorGrid(IronLegends.TILE_WIDTH, IronLegends.TILE_HEIGHT);
		m_grid.setDim(m_rows,m_cols);
		
		for (int j = 0; j < m_rows; ++j)
		{
			for (int i = 0; i < m_cols; ++i)
			{
				m_grid.setCell(i,j, "e");
			}
		}
		
		m_screens.addScreen(new MapEditor_GS(m_grid, m_mapCalc, m_fonts, this));

		// load text info TODO: 
		//LevelLoader.loadGrid(1, m_grid, m_rr);
		m_keyCmds.addCommand("left", KeyEvent.VK_LEFT);
		m_keyCmds.addCommand("right", KeyEvent.VK_RIGHT);
		m_keyCmds.addCommand("up", KeyEvent.VK_UP);
		m_keyCmds.addCommand("down", KeyEvent.VK_DOWN);
		m_keyCmds.addAlphabet();
	}

	public static void main(String[] args) 
	{
		MapEditor game = new MapEditor();
		game.run();
	}

	protected void loadResources()
	{
		
		ResourceFactory resourceFactory = ResourceFactory.getFactory();

		resourceFactory.loadResources(IronLegends.RESOURCE_ROOT, IronLegends.MY_RESOURCES);

		// FONTS
		
		m_fonts.create(resourceFactory);
	}
	
	@Override
	public void render(final RenderingContext rc) 
	{
		super.render(rc);

		{
			AffineTransform tr = rc.getTransform();
			//rc.setTransform(worldToScreenTransform);
			AffineTransform at = m_mapCalc.getWorldToScreenTransform();
			rc.setTransform(at);

			m_grid.render(rc);
			rc.setTransform(tr);
		}
		
		m_screens.getActiveScreen().render(rc);
	}
	
	@Override
	public void update(final long deltaMs)
	{

		m_keyCmds.update(keyboard);
		boolean recalcMap = false;
		if (m_keyCmds.wasPressed("left"))
		{
			m_centerPt.x -= IronLegends.TILE_WIDTH;
			recalcMap = true;
		}
		if (m_keyCmds.wasPressed("right"))
		{
			m_centerPt.x += IronLegends.TILE_WIDTH;
			recalcMap = true;
		}
		if (m_keyCmds.wasPressed("down"))
		{
			m_centerPt.y += IronLegends.TILE_HEIGHT;
			recalcMap = true;
		}
		if (m_keyCmds.wasPressed("up"))
		{
			m_centerPt.y -= IronLegends.TILE_HEIGHT;
			recalcMap = true;
		}
		if (recalcMap)
		{
			centerOnPoint(m_centerPt.x, m_centerPt.y);
			m_mapCalc.centerOnPoint(m_centerPt.x, m_centerPt.y);			
		}
		m_screens.getActiveScreen().processCommands(m_keyCmds, mouse, deltaMs);
		//centerOnPoint(0,0);
	}
}
