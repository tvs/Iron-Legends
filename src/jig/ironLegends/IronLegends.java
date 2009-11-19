package jig.ironLegends;

import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.ViewableLayer;
import jig.engine.hli.ScrollingScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.physics.vpe.VanillaPhysicsEngine;
import jig.engine.util.Vector2D;
import jig.ironLegends.collision.Handler_CPB_CPBLayer;
import jig.ironLegends.collision.Sink_CPB_CPB_Default;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.GameScreens;
import jig.ironLegends.core.HighScore;
import jig.ironLegends.core.HighScorePersistance;
import jig.ironLegends.core.InstallInfo;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.ResourceIO;
import jig.ironLegends.core.SoundFx;
import jig.ironLegends.core.StaticBodyLayer;
import jig.ironLegends.core.GameScreens.ScreenTransition;
import jig.ironLegends.mapEditor.MapCalc;
import jig.ironLegends.screens.CustomizePlayerGS;
import jig.ironLegends.screens.CustomizePlayerTextLayer;
import jig.ironLegends.screens.GameInfoTextLayer;
import jig.ironLegends.screens.GameOverTextLayer;
import jig.ironLegends.screens.GamePlayTextLayer;
import jig.ironLegends.screens.GamePlay_GS;
import jig.ironLegends.screens.HelpScreen;
import jig.ironLegends.screens.HelpTextLayer;
import jig.ironLegends.screens.SplashScreen;
import jig.ironLegends.screens.TestUI_GS;
import jig.misc.sat.PolygonFactory;

public class IronLegends extends ScrollingScreenGame
{
	public static final int TILE_WIDTH = 32;
	public static final int TILE_HEIGHT = 32;	
	public static final int WORLD_WIDTH = 1200;
	public static final int WORLD_HEIGHT = 800;
	public static final int SCREEN_WIDTH = 800;
	public static final int SCREEN_HEIGHT = 600;
	public static final String GAME_ROOT = "jig/ironLegends/";
	public static final String RESOURCE_ROOT = "jig/ironLegends/resources/";
	
	public static final String SPRITE_SHEET = RESOURCE_ROOT + "hr-spritesheet.png";
	public static final String SPRITE_SHEET2 = RESOURCE_ROOT + "ironLegends-spritesheet.png";
	
	public static final String MY_RESOURCES = "hr-resources.xml";
	public static final String IRON_LEGENDS_RESOURCES = "ironLegends-resources.xml";

	static final int START_LIVES = 2;
	
	public static final int SPLASH_SCREEN = 0;
	public static final int HELP_SCREEN = 1;
	public static final int GAMEOVER_SCREEN = 2;
	public static final int GAMEPLAY_SCREEN = 3;
	public static final int GAMEWON_SCREEN = 4;
	//static final int LEVELCOMPLETE_SCREEN = 5;
	public static final int CUSTOMIZEPLAYER_SCREEN = 6;
	public static final int TESTUI_SCREEN = -1;
	
	Fonts m_fonts = new Fonts();
	
	Mitko m_mitko;
	public BodyLayer<Body> m_mitkoLayer;
	public BodyLayer<Body> m_batLayer;
	
	// which layer do the rocks go in?
	BodyLayer<Body> m_tankObstacleLayer;	// trees
	BodyLayer<Body> m_tankBulletObstacleLayer; // walls, buildings
	
	BodyLayer<Body> m_bgLayer;
	BodyLayer<Body> m_powerUpLayer;
	
	public jig.engine.physics.vpe.VanillaPhysicsEngine m_physicsEngine;
	
	// TODO: move into level class
	LevelProgress m_levelProgress;
	GameProgress m_gameProgress;
	
	boolean m_bGameOver = false;

	GameScreens m_screens = new GameScreens();
	
	//protected jig.engine.audio.jsound.AudioClip m_audioBallBrick2 = null;
	//protected jig.engine.audio.jsound.AudioClip m_audioBallBrick3 = null;
	protected HighScore m_highScore = new HighScore();
	protected String m_sError;

	protected String m_sInstallDir = "\\Temp";
	protected HighScorePersistance m_highScorePersist;
	protected PolygonFactory m_polygonFactory;
	ResourceIO m_rr;
	protected SoundFx m_sfx;
	PlayerInfo m_playerInfo;
	
	private MapCalc m_mapCalc;

	void setWorldDim(int width, int height)
	{
		setWorldBounds(0,0, width, height);
		m_mapCalc.setWorldBounds(0,0, width, height);
	}
	
	public IronLegends() 
	{
		super(SCREEN_WIDTH, SCREEN_HEIGHT, false);
		gameframe.setTitle("Iron Legends");

		m_mapCalc = new MapCalc(WORLD_WIDTH, WORLD_HEIGHT);
		setWorldDim(WORLD_WIDTH, WORLD_HEIGHT);
		
		m_sInstallDir 	= InstallInfo.getInstallDir("/" + GAME_ROOT + "IronLegends.class", "IronLegends.jar");
		m_levelProgress = new LevelProgress();
		m_gameProgress 	= new GameProgress(m_levelProgress);
		m_rr 			= new ResourceIO(m_sInstallDir);
		m_sfx 			= new SoundFx();
		m_playerInfo    = new PlayerInfo("Mitko");
		
		loadResources();
		
		// create persons polygon factory 
		m_polygonFactory = null;

		//*
		// some places cast ConvexPolygon to PersonsConvexPolygon because some accessor methods are needed!!
		// if ConvexPolygon had those missing methods, we would be able to
		for (Iterator<PolygonFactory> f = ServiceRegistry.lookupProviders(PolygonFactory.class); f.hasNext();) 
		{
			m_polygonFactory = f.next();
			if (m_polygonFactory.getClass().getName().equals("PersonsFactory"))
				break;
		}
		if (m_polygonFactory == null)
			return;
		//*/
		
		m_highScorePersist = new HighScorePersistance(m_sInstallDir);
		m_highScorePersist.load(m_highScore);		
				
		// GAME OBJECTS
		m_physicsEngine = new VanillaPhysicsEngine();

		Vector2D startPos = new Vector2D(40,WORLD_HEIGHT-22);
		m_mitko = new Mitko(m_polygonFactory.createRectangle(startPos, Mitko.WIDTH,Mitko.HEIGHT), m_mapCalc);
		
		// SCREENS
		m_screens.addScreen(new SplashScreen(SPLASH_SCREEN, m_fonts));
		m_screens.addScreen(new GamePlay_GS(GAMEPLAY_SCREEN, this));
		m_screens.addScreen(new HelpScreen(HELP_SCREEN, m_fonts));
		m_screens.addScreen(new GameScreen(GAMEWON_SCREEN));
		//m_screens.addScreen(new MPGameScreen(LEVELCOMPLETE_SCREEN));
		m_screens.addScreen(new CustomizePlayerGS(CUSTOMIZEPLAYER_SCREEN, m_playerInfo));
		m_screens.addScreen(new TestUI_GS(TESTUI_SCREEN, m_fonts));
		
		GameScreen gameplayScreen = m_screens.getScreen(GAMEPLAY_SCREEN);
		
		// HELP Screen
		{
			GameScreen helpScreen = m_screens.getScreen(HELP_SCREEN);

			BodyLayer<VanillaAARectangle> bgTileLayer = new StaticBodyLayer.NoUpdate<VanillaAARectangle>();
			
			BgTileGenerator tileGenerator = new BgTileGenerator();
			tileGenerator.Tile(bgTileLayer, IronLegends.SPRITE_SHEET + "#testTile2"
					, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, TILE_WIDTH,TILE_HEIGHT);
			
			helpScreen.addViewableLayer(bgTileLayer);
			CustomizePlayerGS customizePlayerScreen = (CustomizePlayerGS)m_screens.getScreen(CUSTOMIZEPLAYER_SCREEN);
			
			customizePlayerScreen.addViewableLayer(bgTileLayer);
			customizePlayerScreen.setTextLayer(new CustomizePlayerTextLayer(m_fonts, m_gameProgress));
			customizePlayerScreen.addViewableLayer(customizePlayerScreen.m_textLayer);
			
			helpScreen.addViewableLayer(new HelpTextLayer(m_fonts));
		}

		 // SPLASH Screen
//		{
//			VanillaAARectangle splashBg = new VanillaAARectangle(RESOURCE_ROOT + "hr-splash.png") 
//			{
//				@Override
//				public void update(long deltaMs) 
//				{
//					// TODO Auto-generated method stub
//				}
//			};
//				splashBg.setPosition(new Vector2D(0, 0));
//				
//				// add splash bg to both splash screen and gameplay screen
//				GameScreen splashScreen = m_screens.getScreen(SPLASH_SCREEN);
//				GameScreen gameWonScreen = m_screens.getScreen(GAMEWON_SCREEN);
//			{
//				BodyLayer<Body> splashBgLayer = new StaticBodyLayer.NoUpdate<Body>();
//				splashBgLayer.add(splashBg);
//				
//				splashScreen.addViewableLayer(splashBgLayer);
//			}
//			{
//				BodyLayer<Body> bgLayer = new AbstractBodyLayer.NoUpdate<Body>();
//				bgLayer.add(splashBg);
//				gameWonScreen.addViewableLayer(bgLayer);                
//				gameWonScreen.addViewableLayer(new GameWonTextLayer(m_fonts, m_gameProgress));
//			}
//			{
//				/*
//				BodyLayer<Body> gameBgLayer = new StaticBodyLayer.NoUpdate<Body>();
//				gameBgLayer.add(splashBg);
//				
//				gameplayScreen.addViewableLayer(gameBgLayer);
//				 */
//			}
//		}

		/*
		{
			PaintableCanvas c1  = new PaintableCanvas((int)Mitko.WIDTH, (int)Mitko.HEIGHT, 1, new Color(128,128,128));
			
			c1.setWorkingFrame(0);
			c1.fillRectangle(0,0, (int)Mitko.WIDTH, (int)Mitko.HEIGHT, new Color(0,255,0));
			//c1.fillRectangle(0,0, 1, (int)Mitko.HEIGHT, new Color(128,128,128));
			//c1.fillRectangle(0,0, 1, (int)Mitko.HEIGHT, new Color(128,128,128));
			c1.loadFrames("mitkoPoly");
			
			m_mPos = new VanillaAARectangle("mitkoPoly") {
				
				@Override
				public void update(long deltaMs) {
					// TODO Auto-generated method stub
					
				}
			};
		}
		*/

		// could be moved below to "creating level" section
		m_tankObstacleLayer = new AbstractBodyLayer.NoUpdate<Body>();
		m_tankBulletObstacleLayer = new AbstractBodyLayer.NoUpdate<Body>();
		
		m_bgLayer = new AbstractBodyLayer.NoUpdate<Body>();
		m_powerUpLayer = new AbstractBodyLayer.IterativeUpdate<Body>();
		m_batLayer = new AbstractBodyLayer.NoUpdate<Body>();
		m_mitkoLayer = new AbstractBodyLayer.NoUpdate<Body>();
		m_mitkoLayer.add(m_mitko);

		gameplayScreen.addViewableLayer(m_bgLayer);
		gameplayScreen.addViewableLayer(m_tankObstacleLayer);
		gameplayScreen.addViewableLayer(m_tankBulletObstacleLayer);
		gameplayScreen.addViewableLayer(m_powerUpLayer);
		gameplayScreen.addViewableLayer(m_batLayer);
		gameplayScreen.addViewableLayer(m_mitkoLayer);
		
		gameplayScreen.addViewableLayer(new GameInfoTextLayer(m_fonts, m_gameProgress, m_mitko, m_highScore));
		
		// gameover screen has all the layers of gameplay except the text layer is different
		{
			Iterator<ViewableLayer> iter = gameplayScreen.getViewableLayers();
			GameScreen gameOverScreen = new GameScreen(GAMEOVER_SCREEN);
			while (iter.hasNext())
			{
				gameOverScreen.addViewableLayer(iter.next());				
			}
			gameOverScreen.addViewableLayer(new GameOverTextLayer(m_fonts, m_gameProgress));
			m_screens.addScreen(gameOverScreen);
		}
		
		gameplayScreen.addViewableLayer(new GamePlayTextLayer(m_fonts, m_gameProgress, m_playerInfo));

		// start with splash screen
		m_screens.addTransition(SPLASH_SCREEN, GAMEPLAY_SCREEN, "enter");
		m_screens.addTransition(GAMEOVER_SCREEN, SPLASH_SCREEN, "enter");
		m_screens.addTransition(SPLASH_SCREEN, HELP_SCREEN, "F1");
		m_screens.addTransition(SPLASH_SCREEN, HELP_SCREEN, "h" );
		m_screens.addTransition(HELP_SCREEN, SPLASH_SCREEN, "enter");
		m_screens.addTransition(HELP_SCREEN, SPLASH_SCREEN, "esc");
		m_screens.addTransition(GAMEWON_SCREEN, SPLASH_SCREEN, "enter");
		m_screens.addTransition(SPLASH_SCREEN, CUSTOMIZEPLAYER_SCREEN, "c");
		m_screens.addTransition(CUSTOMIZEPLAYER_SCREEN, SPLASH_SCREEN, "enter");
		m_screens.addTransition(CUSTOMIZEPLAYER_SCREEN, SPLASH_SCREEN, "esc");
		m_screens.addTransition(SPLASH_SCREEN, TESTUI_SCREEN, "t");
		m_screens.setActiveScreen(SPLASH_SCREEN);
		
		// Configure Commands
		/*
		keyCmds.addCommand("left", KeyEvent.VK_J);
		keyCmds.addCommand("right", KeyEvent.VK_L);
		keyCmds.addCommand("up", KeyEvent.VK_I);
		keyCmds.addCommand("down", KeyEvent.VK_K);
		*/
		m_keyCmds.addCommand("left", KeyEvent.VK_LEFT);
		m_keyCmds.addCommand("right", KeyEvent.VK_RIGHT);
		m_keyCmds.addCommand("up", KeyEvent.VK_UP);
		m_keyCmds.addCommand("down", KeyEvent.VK_DOWN);
		
		m_keyCmds.addCommand("space", KeyEvent.VK_SPACE);
		m_keyCmds.addCommand("pause", KeyEvent.VK_P);
		m_keyCmds.addCommand("enter", KeyEvent.VK_ENTER);
		
		m_keyCmds.addCommand("h", KeyEvent.VK_H);
		m_keyCmds.addCommand("F1", KeyEvent.VK_F1);
		m_keyCmds.addCommand("esc", KeyEvent.VK_ESCAPE);
		m_keyCmds.addCommand("backspace", KeyEvent.VK_BACK_SPACE);
		// cheat codes
		m_keyCmds.addCommand("splat", KeyEvent.VK_8);
		m_keyCmds.addCommand("faint", KeyEvent.VK_F);
		m_keyCmds.addCommand("weedsCollected", KeyEvent.VK_W);
			
    	m_keyCmds.addCommand("smoke", KeyEvent.VK_S);
    	
    	m_keyCmds.addAlphabet();
		
		populateGameLayers();
	}

	
	protected boolean loadResources()
	{
		boolean bSuccess = true;
		ResourceFactory resourceFactory = ResourceFactory.getFactory();

		resourceFactory.loadResources(RESOURCE_ROOT, IRON_LEGENDS_RESOURCES);
		resourceFactory.loadResources(RESOURCE_ROOT, MY_RESOURCES);
		// FONTS
		m_fonts.create(resourceFactory);

		// AUDIO
		// works within IDE using MP3, but when package as executable jar, must
		// be a wav file
		String audioRoot = RESOURCE_ROOT;
		//m_audioBallBrick2 = resourceFactory.getAudioClip(audioRoot + "hr-ballBrick2.wav");
		//m_audioBallBrick3 = resourceFactory.getAudioClip(audioRoot + "hr-ballBrick3.wav");
		
		m_sfx.addSfx("faint1", resourceFactory.getAudioClip(audioRoot + "hr-sfx-AyeYay.wav"));
		m_sfx.addSfx("faint2", resourceFactory.getAudioClip(audioRoot + "hr-sfx-CreatureMeal.wav"));
		
		m_sfx.addSfx("weedPulled1", resourceFactory.getAudioClip(audioRoot + "hr-sfx-PickingWeed.wav"));
		
		m_sfx.addSfx("collectPowerup1", resourceFactory.getAudioClip(audioRoot + "hr-sfx-CollectPowerUp.wav"));
		
		m_sfx.addSfx("powerup1", resourceFactory.getAudioClip(audioRoot + "hr-sfx-Courage.wav"));
		
		m_sfx.addSfx("trapCreature1", resourceFactory.getAudioClip(audioRoot + "hr-sfx-Gotcha.wav"));
		m_sfx.addSfx("trapCreature2", resourceFactory.getAudioClip(audioRoot + "hr-sfx-CreatureSmash.wav"));
		
		m_sfx.addSfx("smoke1", resourceFactory.getAudioClip(audioRoot + "hr-sfx-YumYum.wav"));
		
		return bSuccess;
	}
	
	protected boolean loadMap(String sMapFile)
	{
		boolean bSuccess = true;
		
		m_tankObstacleLayer.clear();
		m_tankBulletObstacleLayer.clear();
		IronLegendsMapLoadSink sink = new IronLegendsMapLoadSink(this);
		MapLoader.loadLayer(sink, sMapFile, m_rr);
				
		return bSuccess;
	}
	
	protected boolean loadLevel(int level)
	{
		m_bgLayer.clear();
		m_batLayer.clear();
		m_powerUpLayer.clear();
		
		m_levelProgress.reset();
		
		// hard code map for now
		//loadMap("m1.txt");
		//loadMap("maps/borders.txt");
		loadMap("maps/mapitems.txt");
		/*
		String sMap = "levels/level" + level + ".txt";
		if (!MapLoader.loadGrid(sMap, m_grid, m_rr))
		{
			// TODO: won the game?... or error. for now "won" the game
			int totalScore = m_gameProgress.gameOver(); 
			if (totalScore > m_highScore.getHighScore())
			{
				m_highScore.setHighScore(totalScore);
				m_highScore.setPlayer(m_playerInfo.getName());
				m_highScorePersist.save(m_highScore);
			}	
			m_screens.setActiveScreen(GAMEWON_SCREEN);
			populateGameLayers();
			return true;
		}		
		 
		*/
		// for now require just set to 1 so people can still play until we get game over logic/criteria
		m_levelProgress.setWeedsRequired(1);
		populateGameLayers();
		m_levelProgress.setIntro(2999);
		m_bFirstLevelUpdate = false; 
		return true;
	}
	
	protected boolean advanceLevel() 
	{
		m_mitko.reset();
		return loadLevel(m_gameProgress.advanceLevel());

	}

	/**
	 * based on which state game is in, 
	 * this should be called after resources have been created
	 * 	including audio/graphic resources as well as level objects
	 */
	protected void populateGameLayers() 
	{
		gameObjectLayers.clear();
		m_physicsEngine.clear();

		Iterator<ViewableLayer> layerIterator = null;
		
		layerIterator = m_screens.getScreen(m_screens.activeScreen()).getViewableLayers();			
		
		if (layerIterator != null)
		{
			while (layerIterator.hasNext()) {
				gameObjectLayers.add(layerIterator.next());
			}
		}

		// configure physics engine to handle updates only if 
		// in state gameplay and not game over
		int curScreen = m_screens.activeScreen();
		switch (curScreen)
		{
			case SPLASH_SCREEN:
			case GAMEOVER_SCREEN:
			case HELP_SCREEN:
			case GAMEWON_SCREEN:
			case CUSTOMIZEPLAYER_SCREEN:
			break;
			//case GAMEPLAY_SCREEN:
			default:
				m_physicsEngine.manageViewableSet(m_mitkoLayer);
				m_physicsEngine.manageViewableSet(m_batLayer);
				
				/*
				 collision resolution in following order
				 mitko - hedge
				 creature - hedge
				 mitko - creatures
				 mitko - weeds/powerups
				 */
				
				// don't hit the obstacles
				m_physicsEngine.registerCollisionHandler(
						new Handler_CPB_CPBLayer(m_mitko, m_tankObstacleLayer
								, new Sink_CPB_CPB_Default()));
				m_physicsEngine.registerCollisionHandler(
						new Handler_CPB_CPBLayer(m_mitko, m_tankBulletObstacleLayer
								, new Sink_CPB_CPB_Default()));
			break;
		}
	}

	protected void newGame() 
	{
		m_bGameOver = false;

		m_gameProgress.reset();
		m_mitko.newGame();
		loadLevel(m_gameProgress.getCurLevel());
	}
	
	protected KeyCommands m_keyCmds = new KeyCommands();
	protected boolean m_paused = false;
	

	protected void processCommands(final long deltaMs)
	{
		m_keyCmds.update(keyboard);
		
		// TODO update each button on cur screen
		// TODO definitely create a Screen class for each screen because
		// then each screen class can hold all the logic for processing input
		GameScreen curScreen = m_screens.getActiveScreen();
		if (curScreen != null)
		{
			int iNewScreen = curScreen.processCommands(m_keyCmds, mouse, deltaMs);
			int iCurScreen = curScreen.name();
			if (iCurScreen != iNewScreen)
			{
				// NOTE if had a SplashScreen GameScreen, the activate could populateGameLayers?
				m_screens.setActiveScreen(iNewScreen);
				GameScreen newScreen = m_screens.getActiveScreen();
				curScreen.deactivate();
				newScreen.activate(iCurScreen);
				populateGameLayers();
				// todo: this needs to be moved into activate
				if (iNewScreen == GAMEPLAY_SCREEN && iCurScreen == SPLASH_SCREEN)
				{
					newGame();
				}
			}
		}		
		
		if (m_levelProgress.isExitActivated())
		{
			if (m_keyCmds.wasPressed("enter"))
			{
				m_levelProgress.setExitComplete(true);
			}
		}
		
		//curScreen.processInput(m_keyCmds);
		
		if (m_screens.activeScreen() == GAMEPLAY_SCREEN)
		{
			if (m_keyCmds.wasPressed("die"))
			{
			}
		}

		ScreenTransition t = m_screens.transition(m_keyCmds);
		if (t != null)
		{
			GameScreen newScreen = m_screens.getActiveScreen();
			// TODO: create handlers for transition so game can be modified appropriately
			// e.g. no need to test what the transition is to execute newGame
			if (t.m_to == GAMEPLAY_SCREEN && t.m_from == SPLASH_SCREEN)
			{
				newGame();
			}
			else if (t.m_to == SPLASH_SCREEN && t.m_from == GAMEOVER_SCREEN)
			{
				m_mitko.reset();
				populateGameLayers();
			}
			else
			{
				populateGameLayers();
			}
			curScreen.deactivate();
			newScreen.activate(t.m_from);
		}
		
		if (m_keyCmds.wasPressed("pause")){
			if (m_paused) {
			} else {
			}
			m_paused = !m_paused;
			System.out.println("");
		}

	}
	
	protected void move(final long deltaMs) 
	{		
		//processCommands(deltaMs);
		
		boolean left 	= m_keyCmds.isPressed("left");
		boolean right	= m_keyCmds.isPressed("right");
		boolean up	 	= m_keyCmds.isPressed("up");
		boolean down 	= m_keyCmds.isPressed("down");
		boolean smoke	= m_keyCmds.wasPressed("smoke");
		if (smoke && m_mitko.getStoredPowerUps() > 0)
		{
			m_mitko.smoke();
			m_sfx.play("smoke1");
		}
		
		m_mitko.move(left, right, up, down, deltaMs);
	}

	boolean m_bFirstLevelUpdate = false;
	
	static public Vector2D bodyPosToPolyPos(int w, int h, Vector2D pos)
	{
		double r = Math.sqrt(w*w + h*h)/2;
		double deltaX = r - w/2;
		double deltaY = r - h/2;
		
		return new Vector2D(pos.getX()-deltaX, pos.getY()-deltaY);
	}

	@Override
	public void update(final long deltaMs) 
	{
		processCommands(deltaMs);
		
		if (m_levelProgress.isExitActivated())
		{
			super.update(deltaMs);
			processCommands(deltaMs);
			if (m_levelProgress.isExitComplete())
				advanceLevel();
			//m_levelProgress.update(deltaMs);
			return;
		}
		if (!m_levelProgress.isIntro()  )
		{
			super.update(deltaMs);
		}
		else if (!m_bFirstLevelUpdate)
		{
			super.update(deltaMs);
			m_bFirstLevelUpdate = true;
		}
		

		int curScreen = m_screens.activeScreen();
		
		switch (curScreen)
		{
			case SPLASH_SCREEN:
			break;
			case GAMEOVER_SCREEN:
				m_physicsEngine.applyLawsOfPhysics(deltaMs);
			break;
			case GAMEPLAY_SCREEN:
				if (m_levelProgress.isIntro())
				{
					m_levelProgress.update(deltaMs);
				}
				else if (m_levelProgress.isExitActivated())
				{
					m_levelProgress.update(deltaMs);
				}
				else
				{
					m_physicsEngine.applyLawsOfPhysics(deltaMs);
					//m_physicsEngine.applyLawsOfPhysics(5);
				}

				if (m_mitko.isFainting() 
						&& m_mitko.doneFainting() 
						&& m_screens.activeScreen() != GAMEOVER_SCREEN)
				{
					if (m_gameProgress.getLivesRemaining() == 0)
					{
						m_screens.setActiveScreen(GAMEOVER_SCREEN);
						
						int totalScore = m_gameProgress.gameOver(); 
						if (totalScore > m_highScore.getHighScore())
						{
							m_highScore.setHighScore(totalScore);
							m_highScore.setPlayer(m_playerInfo.getName());
							m_highScorePersist.save(m_highScore);
						}	
					}
					else
					{
						m_gameProgress.mitkoFainted();
						m_mitko.reset();
					}
					
					populateGameLayers();			
				}

				if (m_levelProgress.isLevelComplete()) 
				{
					if (!m_levelProgress.isExitActivated())
					{
						m_levelProgress.setExit(true);
					}
				}

			break;
		}

		move(deltaMs);
		// don't center if would cause scrolling past "edge" of screen
		{
			//Vector2D pos = m_mitko.getCenterPosition();
			// translate to screen, if pos + world
			updateMapCenter(m_mitko.getCenterPosition());
			
		}
	}

	private void updateMapCenter(Vector2D centerPosition) 
	{
		centerOnPoint(centerPosition);
		m_mapCalc.centerOnPoint(centerPosition);
	}

	private String m_mapName;
		
	@Override
	public void render(final RenderingContext rc) 
	{
		if (rc != null)
		{
			super.render(rc);
			GameScreen curScreen = m_screens.getActiveScreen();
			if (curScreen != null)
				curScreen.render(rc);
		}
		
		//Area gameInfoArea = new Area(new Rectangle2D.Float(600, 0, 200, 600));
		//Color clear = new Color(128,128,128);
		//m_side.drawArea(gameInfoArea, clear);
		// get polygon
		/*
		Vector2D pos = m_mitko.getPosition();
		ConvexPolygon p = m_mitko.getShape();
		int x = 0;
		int y = 0;
		boolean bDone = false;
		for (x = (int)pos.getX(); !bDone && x < m_mitko.getWidth()+pos.getX(); x++) {
			for (y = (int)pos.getY(); !bDone && y < m_mitko.getHeight()+pos.getY(); y++) {
				if (p.contains(x, y)) 
				{
					bDone = true;
					break;
					
				}
			}
			if (bDone)
				break;
		}
		
		//m_mPos.setPosition(new Vector2D(pos.getX()+x, pos.getY()+y));
		m_mPos.setPosition(new Vector2D(x, y));
		m_mPos.render(rc);
		*/
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		IronLegends game = new IronLegends();
		game.run();
	}

	public void setMapName(String mapName) 
	{
		m_mapName = mapName;
	}
}
