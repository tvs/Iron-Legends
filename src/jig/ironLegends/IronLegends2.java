package jig.ironLegends;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

import jig.engine.ImageResource;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.ViewableLayer;
import jig.engine.hli.ImageBackgroundLayer;
import jig.engine.hli.ScrollingScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaPhysicsEngine;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.GameScreens;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.SoundFx;
import jig.ironLegends.core.GameScreens.ScreenTransition;
import jig.misc.sat.PolygonFactory;

public class IronLegends2 extends ScrollingScreenGame {
	public static final int WORLD_WIDTH = 2000;
	public static final int WORLD_HEIGHT = 2000;
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	public static final Rectangle WORLD_BOUNDS = new Rectangle(0, 0,
			WORLD_WIDTH, WORLD_HEIGHT);
	public static final Rectangle VISIBLE_BOUNDS = new Rectangle(SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2,
			WORLD_WIDTH - 2 * (SCREEN_WIDTH / 2), WORLD_HEIGHT - 2 * (SCREEN_HEIGHT / 2));

	public static final String GAME_ROOT = "jig/ironLegends/";
	public static final String RESOURCE_ROOT = "jig/ironLegends/resources/";
	public static final String SPRITE_SHEET = RESOURCE_ROOT + "ironLegends-spritesheet.png";

	public static final int SPLASH_SCREEN = 0;
	public static final int HELP_SCREEN = 1;
	public static final int GAMEOVER_SCREEN = 2;
	public static final int GAMEPLAY_SCREEN = 3;
	public static final int GAMEWON_SCREEN = 4;
	public static final int LEVELCOMPLETE_SCREEN = 5;
	public static final int CUSTOMIZEPLAYER_SCREEN = 6;

	protected VanillaPhysicsEngine m_physicsEngine;
	protected PolygonFactory m_polygonFactory;
	protected GameScreens m_screens = new GameScreens();
	protected KeyCommands m_keyCmds = new KeyCommands();
	protected Fonts m_fonts = new Fonts();
	protected SoundFx m_sfx;

	protected PlayerInfo m_playerInfo;
	protected Tank m_tank;
	protected ViewableLayer m_bgLayer;
	protected BodyLayer<Body> m_tankLayer;
	protected BodyLayer<Body> m_opponentLayer;
	protected BodyLayer<Bullet> m_bulletLayer;
	
	protected Navigator m_navigator;
	protected String m_sError;
	protected String m_sInstallDir;
	
	MapGrid m_grid;

	public IronLegends2() {
		super(SCREEN_WIDTH, SCREEN_HEIGHT, false);
		setWorldBounds(WORLD_BOUNDS);
		gameframe.setTitle("Iron Legends");

		// Load resources
		loadResources();

		// create persons polygon factory
		m_polygonFactory = null;

		// Load polygon factory
		for (Iterator<PolygonFactory> f = ServiceRegistry
				.lookupProviders(PolygonFactory.class); f.hasNext();) {
			m_polygonFactory = f.next();
			if (m_polygonFactory.getClass().getName().equals("PersonsFactory")) {
				break;
			}
		}
		if (m_polygonFactory == null) {
			return;
		}

		// Physics Engine
		m_physicsEngine = new VanillaPhysicsEngine();

		// Commands
		configureCommands();

		// Screens
		setupScreens();

		// Load Layers
		populateGameLayers();
	}

	private void loadResources() {
		ResourceFactory.getFactory().loadResources(RESOURCE_ROOT,
				"ironLegends-resources.xml");
	}

	/**
	 * Configure Commands
	 */
	private void configureCommands() {
		m_keyCmds.addCommand("left", KeyEvent.VK_LEFT);
		m_keyCmds.addCommand("right", KeyEvent.VK_RIGHT);
		m_keyCmds.addCommand("up", KeyEvent.VK_UP);
		m_keyCmds.addCommand("down", KeyEvent.VK_DOWN);

		m_keyCmds.addCommand("space", KeyEvent.VK_SPACE);
		m_keyCmds.addCommand("pause", KeyEvent.VK_P);
		m_keyCmds.addCommand("enter", KeyEvent.VK_ENTER);
		m_keyCmds.addCommand("esc", KeyEvent.VK_ESCAPE);
		m_keyCmds.addCommand("backspace", KeyEvent.VK_BACK_SPACE);

		m_keyCmds.addCommand("F1", KeyEvent.VK_F1);
		m_keyCmds.addCommand("F2", KeyEvent.VK_F2);
		m_keyCmds.addCommand("F3", KeyEvent.VK_F3);
		m_keyCmds.addCommand("F4", KeyEvent.VK_F4);
		m_keyCmds.addCommand("F5", KeyEvent.VK_F5);
		m_keyCmds.addCommand("F6", KeyEvent.VK_F6);
		m_keyCmds.addCommand("F7", KeyEvent.VK_F7);
		m_keyCmds.addCommand("F8", KeyEvent.VK_F8);
		m_keyCmds.addCommand("F9", KeyEvent.VK_F9);
		m_keyCmds.addCommand("F10", KeyEvent.VK_F10);
		m_keyCmds.addCommand("F11", KeyEvent.VK_F11);
		m_keyCmds.addCommand("F12", KeyEvent.VK_F12);
		
		m_keyCmds.addAlphabet();
	}

	private void setupScreens() {
		// background layer
		ImageResource bkg = ResourceFactory.getFactory().getFrames(
				SPRITE_SHEET + "#background").get(0);
		m_bgLayer = new ImageBackgroundLayer(bkg, WORLD_WIDTH, WORLD_HEIGHT,
				ImageBackgroundLayer.TILE_IMAGE);

		// Splash screen
		ImageResource splashimg = ResourceFactory.getFactory().getFrames(
				RESOURCE_ROOT + "splash.png").get(0);
		ViewableLayer splashBgLayer = new ImageBackgroundLayer(splashimg,
				SCREEN_WIDTH, SCREEN_HEIGHT, ImageBackgroundLayer.TILE_IMAGE);

		GameScreen splashScreen = new GameScreen(SPLASH_SCREEN);
		splashScreen.addViewableLayer(splashBgLayer);
		m_screens.addScreen(splashScreen);

		// GamePlay Screen
		m_tank = new Tank(m_polygonFactory, new Vector2D(100, 100), "Player");
		m_tankLayer = new AbstractBodyLayer.NoUpdate<Body>();
		m_tankLayer.add(m_tank);

		m_opponentLayer = new AbstractBodyLayer.NoUpdate<Body>();
		while (m_opponentLayer.size() < 10) {
			Vector2D pos = Vector2D.getRandomXY(VISIBLE_BOUNDS.getMinX(), VISIBLE_BOUNDS.getMaxX(), VISIBLE_BOUNDS.getMinY(), VISIBLE_BOUNDS.getMaxY());
			Tank t = new Tank(m_polygonFactory, pos, "Enemy");
			m_opponentLayer.add(t);
		}
		
		m_bulletLayer = new AbstractBodyLayer.IterativeUpdate<Bullet>();
		
		GameScreen gameplayScreen = new GameScreen(GAMEPLAY_SCREEN);
		gameplayScreen.addViewableLayer(m_bgLayer);
		gameplayScreen.addViewableLayer(m_tankLayer);
		gameplayScreen.addViewableLayer(m_opponentLayer);
		gameplayScreen.addViewableLayer(m_bulletLayer);
		m_screens.addScreen(gameplayScreen);

		// Screen Transitions
		m_screens.addTransition(SPLASH_SCREEN, GAMEPLAY_SCREEN, "enter");

		// Start with Splash screen
		m_screens.setActiveScreen(GAMEPLAY_SCREEN);
	}

	private void populateGameLayers() {
		gameObjectLayers.clear();
		m_physicsEngine.clear();

		Iterator<ViewableLayer> layerIterator = m_screens.getScreen(
				m_screens.activeScreen()).getViewableLayers();
		if (layerIterator != null) {
			while (layerIterator.hasNext()) {
				gameObjectLayers.add(layerIterator.next());
			}
		}
		
		int activeScreen = m_screens.activeScreen();
		if (activeScreen == GAMEPLAY_SCREEN) {
			m_physicsEngine.manageViewableSet(m_tankLayer);
			m_physicsEngine.manageViewableSet(m_opponentLayer);
			m_physicsEngine.manageViewableSet(m_bulletLayer);
			
			// Register Collision Handlers
		}
	}

	private void processCommands(long deltaMs) {
		m_keyCmds.update(keyboard);
		int activeScreen = m_screens.activeScreen();
		GameScreen curScreen = m_screens.getScreen(activeScreen);

		// Screen Transitions
		ScreenTransition t = m_screens.transition(m_keyCmds);
		if (t != null) {
			GameScreen newScreen = m_screens.getActiveScreen();
			curScreen.deactivate();
			newScreen.activate(t.m_from);
			populateGameLayers();
		}
		
		if (activeScreen == GAMEPLAY_SCREEN) {
			m_tank.controlMovement(m_keyCmds, mouse, getCenter());
			if (mouse.isLeftButtonPressed()) {
				m_tank.fire(getBullet());
			}
		}
	}

	private Bullet getBullet() {
		Bullet bullet = null;
		for (Bullet b : m_bulletLayer) {
			if (!b.isActive()) {
				bullet = b;
				break;
			}
		}
		
		if (bullet == null) {
			bullet = new Bullet();
			m_bulletLayer.add(bullet);
		}
		
		return bullet;
	}

	@Override
	public void update(long deltaMs) {
		processCommands(deltaMs);

		super.update(deltaMs);
		int activeScreen = m_screens.activeScreen();
		if (activeScreen == GAMEPLAY_SCREEN) {
			m_physicsEngine.applyLawsOfPhysics(deltaMs);
		}
		
		// center screen on tank
		Vector2D center = m_tank.getCenterPosition();
		// TODO: on right & bottom object moves beyond the bounds 
		centerOnPoint(center.clamp(VISIBLE_BOUNDS));
	}

	@Override
	public void render(RenderingContext rc) {
		super.render(rc);
		GameScreen curScreen = m_screens.getActiveScreen();
		if (curScreen != null) {
			curScreen.render(rc);
		}
	}

	public static void main(String[] args) {
		IronLegends2 game = new IronLegends2();
		game.run();
	}
}