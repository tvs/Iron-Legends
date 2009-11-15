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
	//protected MapEditorGrid m_grid; // just basic text info for grid
	protected MapLayer m_mapLayer;

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
		
		loadResources();
				
		m_mapLayer = new MapLayer();
		//m_mapLayer.add(new SpriteMapItem(new Vector2D(400,400), 0, "wall", IronLegends.SPRITE_SHEET2 + "#wall"));
		//m_mapLayer.add(new SpriteMapItem(new Vector2D(300,400), Math.toRadians(45), "wall", IronLegends.SPRITE_SHEET2 + "#wall"));
				
		m_screens.addScreen(new MapEditor_GS(m_mapLayer, m_mapCalc, m_fonts, this));

		// move map
		m_keyCmds.addCommand("left", KeyEvent.VK_LEFT);
		m_keyCmds.addCommand("right", KeyEvent.VK_RIGHT);
		m_keyCmds.addCommand("up", KeyEvent.VK_UP);
		m_keyCmds.addCommand("down", KeyEvent.VK_DOWN);
		
		// rotate map item
		m_keyCmds.addCommand("rotateCCW", KeyEvent.VK_Q);
		m_keyCmds.addCommand("rotateCW", KeyEvent.VK_E);	
		
		m_keyCmds.addAlphabet();
		m_keyCmds.addNumbers();
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
		resourceFactory.loadResources(IronLegends.RESOURCE_ROOT, IronLegends.IRON_LEGENDS_RESOURCES);

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

			m_mapLayer.render(rc);
			
			rc.setTransform(tr);
		}
		
		m_screens.getActiveScreen().render(rc);
	}
	
	@Override
	public void update(final long deltaMs)
	{

		m_keyCmds.update(keyboard);

		m_screens.getActiveScreen().processCommands(m_keyCmds, mouse, deltaMs);
		//centerOnPoint(m_centerPt.x, m_centerPt.y);

		//centerOnPoint(0,0);
	}
}
