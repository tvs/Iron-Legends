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
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaPhysicsEngine;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.GameScreens;
import jig.ironLegends.core.HighScore;
import jig.ironLegends.core.HighScorePersistance;
import jig.ironLegends.core.InstallInfo;
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

	public static final String GAME_ROOT = "jig/ironLegends/";
	public static final String RESOURCE_ROOT = "jig/ironLegends/resources/";
	public static final String SPRITE_SHEET = RESOURCE_ROOT + "spritesheet.png";

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
	// protected Tank m_tank;
	protected ViewableLayer m_bgLayer;
	protected BodyLayer<Body> m_tankLayer;

	protected Navigator m_navigator;
	protected String m_sError;
	protected String m_sInstallDir;

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
				"resources.xml");
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

		m_keyCmds.addCommand("a", KeyEvent.VK_A);
		m_keyCmds.addCommand("b", KeyEvent.VK_B);
		m_keyCmds.addCommand("c", KeyEvent.VK_C);
		m_keyCmds.addCommand("d", KeyEvent.VK_D);
		m_keyCmds.addCommand("e", KeyEvent.VK_E);
		m_keyCmds.addCommand("f", KeyEvent.VK_F);
		m_keyCmds.addCommand("g", KeyEvent.VK_G);
		m_keyCmds.addCommand("h", KeyEvent.VK_H);
		m_keyCmds.addCommand("i", KeyEvent.VK_I);
		m_keyCmds.addCommand("j", KeyEvent.VK_J);
		m_keyCmds.addCommand("k", KeyEvent.VK_K);
		m_keyCmds.addCommand("l", KeyEvent.VK_L);
		m_keyCmds.addCommand("m", KeyEvent.VK_M);
		m_keyCmds.addCommand("n", KeyEvent.VK_N);
		m_keyCmds.addCommand("o", KeyEvent.VK_O);
		m_keyCmds.addCommand("p", KeyEvent.VK_P);
		m_keyCmds.addCommand("q", KeyEvent.VK_Q);
		m_keyCmds.addCommand("r", KeyEvent.VK_R);
		m_keyCmds.addCommand("s", KeyEvent.VK_S);
		m_keyCmds.addCommand("t", KeyEvent.VK_T);
		m_keyCmds.addCommand("u", KeyEvent.VK_U);
		m_keyCmds.addCommand("v", KeyEvent.VK_V);
		m_keyCmds.addCommand("w", KeyEvent.VK_W);
		m_keyCmds.addCommand("x", KeyEvent.VK_X);
		m_keyCmds.addCommand("y", KeyEvent.VK_Y);
		m_keyCmds.addCommand("z", KeyEvent.VK_Z);
	}

	private void setupScreens() {
		// background layer
		ImageResource bkg = ResourceFactory.getFactory().getFrames(
				SPRITE_SHEET + "#background").get(0);
		m_bgLayer = new ImageBackgroundLayer(bkg, WORLD_WIDTH, WORLD_HEIGHT,
				ImageBackgroundLayer.TILE_IMAGE);

		// Splash screen		
		GameScreen splashScreen = new GameScreen(SPLASH_SCREEN);
		ImageResource splashimg = ResourceFactory.getFactory().getFrames(RESOURCE_ROOT + "splash.png").get(0); 
		ViewableLayer splashBgLayer = new ImageBackgroundLayer(splashimg, SCREEN_WIDTH, SCREEN_HEIGHT, ImageBackgroundLayer.TILE_IMAGE);
		splashScreen.addViewableLayer(splashBgLayer);				
		m_screens.addScreen(splashScreen);

		// GamePlay Screen
		GameScreen gameplayScreen = new GameScreen(GAMEPLAY_SCREEN);
		gameplayScreen.addViewableLayer(m_bgLayer);
		m_screens.addScreen(gameplayScreen);

		// Screen Transitions
		m_screens.addTransition(SPLASH_SCREEN, GAMEPLAY_SCREEN, "enter");

		// Start with Splash screen
		m_screens.setActiveScreen(SPLASH_SCREEN);
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
	}

	private void processCommands(long deltaMs) {
		m_keyCmds.update(keyboard);
		GameScreen curScreen = m_screens.getActiveScreen();

		// Screen Transitions
		ScreenTransition t = m_screens.transition(m_keyCmds);
		if (t != null) {
			GameScreen newScreen = m_screens.getActiveScreen();
			populateGameLayers();
			curScreen.deactivate();
			newScreen.activate(t.m_from);
		}
	}

	@Override
	public void update(long deltaMs) {
		super.update(deltaMs);
		processCommands(deltaMs);
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