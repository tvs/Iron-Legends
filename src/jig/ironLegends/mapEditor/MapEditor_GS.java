package jig.ironLegends.mapEditor;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Iterator;
import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.util.Vector2D;
import jig.ironLegends.IronLegends;
import jig.ironLegends.MapLoader;
import jig.ironLegends.core.ATSprite;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.TextWriter;
import jig.ironLegends.core.ui.Button;
import jig.ironLegends.core.ui.ButtonToolbar;
import jig.ironLegends.core.ui.MouseState;
import jig.ironLegends.core.ui.TextEditBox;

public class MapEditor_GS extends GameScreen 
{

	ButtonToolbar<Button> m_cmdButtons;
	
	Button m_saveBt;
	Button m_loadBt;
	TextEditBox m_mapName;
	TextEditBox m_rotation;
	int m_topSpriteIdx = 0;
	
	double m_rotationIncDeg;
	double m_curRotationDeg;
	
	//TODO: add visible world borders
	ButtonToolbar<TileButton> m_tileToolbar = new ButtonToolbar<TileButton>(0, 0);
	
	Point m_curMouse = new Point(0,0);
	int m_buttonStartX = 16*IronLegends.SCREEN_WIDTH/20;
	TileButton m_selButton = null;
	ATSprite m_curSprite = null;
	
	MapLayer m_mapLayer = null;
	MouseState m_mouseState = new MouseState();
	//protected static final int 
	protected Point m_centerPt;

	MapCalc m_mapCalc;
	Fonts m_fonts;
	
	MapEditor m_mapEditor;
	int m_mapWidth;
	int m_mapHeight;
	public Rectangle VISIBLE_BOUNDS = new Rectangle();
	
	public MapEditor_GS(MapLayer mapLayer, MapCalc mapCalc, Fonts fonts, MapEditor mapEditor) 
	{
		super(MapEditor.MAPEDIT_SCREEN);

		
		m_mapLayer = mapLayer;
		m_mapCalc = mapCalc;
		m_fonts = fonts;
		m_mapEditor = mapEditor;
		
		m_centerPt = new Point(m_mapWidth/2, m_mapHeight/2);
		setWorldDim(IronLegends.WORLD_WIDTH, IronLegends.WORLD_HEIGHT);
		
		m_mapEditor.centerOnPoint(m_centerPt.x, m_centerPt.y);
		m_mapCalc.centerOnPoint(m_centerPt.x, m_centerPt.y);
		
		m_mouseState.onEnter(-1, new Point(0,0));
		// create viewable layers in order to be rendered?
		
		// sprite tool bar
		createSpriteToolbar();
		
		// create cmd buttons
		createCmdButtons();
		
		// adjust locations to align to the right of the screen
		setLocations();
		
	}
	private void setLocations() 
	{
		int buttonStartX = MapEditor.SCREEN_WIDTH - m_cmdButtons.getMaxWidth() - m_tileToolbar.getMaxWidth() - 2;
		
		int cmdStartX = buttonStartX + m_tileToolbar.getMaxWidth() + 1;
		
		m_tileToolbar.setPosition(buttonStartX, (int)m_tileToolbar.getPosition().getY());
		
		m_cmdButtons.setPosition(cmdStartX, (int)m_cmdButtons.getPosition().getY());
		m_buttonStartX = buttonStartX;
	}
	private void createCmdButtons() 
	{
		int btX = (int)m_tileToolbar.getPosition().getX() + m_tileToolbar.getMaxWidth()+2;
		int btY = 10;

		m_cmdButtons = new ButtonToolbar<Button>(btX, btY);
	
		int cmdId = -1;
		m_saveBt = new Button(cmdId, btX, 10, IronLegends.SCREEN_SPRITE_SHEET + "#csshader");
		m_saveBt.initText(-1,-1, m_fonts.instructionalFont);
		m_saveBt.setText("SAVE");
		m_cmdButtons.append(m_saveBt);
				
		m_mapName = new TextEditBox(m_fonts.instructionalFont, cmdId, btX, btY, IronLegends.SCREEN_SPRITE_SHEET + "#csshader");
		m_cmdButtons.append(m_mapName);
	
		m_loadBt = new Button(cmdId, btX, btY, IronLegends.SCREEN_SPRITE_SHEET + "#csshader");
		m_loadBt.initText(-1,-1, m_fonts.instructionalFont);
		m_loadBt.setText("LOAD");
		m_cmdButtons.append(m_loadBt);

		m_rotation = new TextEditBox(m_fonts.instructionalFont, cmdId, btX, btY, IronLegends.SCREEN_SPRITE_SHEET + "#csshader");
		m_rotation.setText("45");
		m_rotationIncDeg = 45;
		//m_rotation.setText(Double.toString(m_rotationIncDeg));
		m_cmdButtons.append(m_rotation);
	}

	/*
	 * @brief creates a button toolbar with sprites where the buttons are for placing "items" on the map 
	 */
	private void createSpriteToolbar() 
	{
		int sx = m_buttonStartX;
		int sy = 10;
		
		int tileButtonId = 0;
		m_tileToolbar.setPosition(sx, sy);
		m_tileToolbar.append(new TileButton("wall", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET + "#wall"));
		tileButtonId++;
		
		m_tileToolbar.append(new TileButton("rock1", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET + "#rock1"));			
		tileButtonId++;
		m_tileToolbar.append(new TileButton("rock2", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET + "#rock2"));			
		tileButtonId++;
		m_tileToolbar.append(new TileButton("tree", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET + "#tree"));			
		tileButtonId++;
		// NOTE: just testing that crates looked correct!
		// Should add controls for all of the objects � Not sure if the maploader is prepared for 
		m_tileToolbar.append(new TileButton("crate", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET + "#crate"));
		tileButtonId++;
		//m_tileToolbar.append(new TileButton("tree", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET + "#red-base"));			
		//tileButtonId++;
		
		// TODO: add more buttons here crates? bases? launch points? (launch points might not be visible in the game
		// but could be used as spawning grounds for each team e.g. lp1-team1
		
		// TODO: add delete icon
		m_tileToolbar.append(new TileButton("del", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET + "#mine"));
		TileButton b = m_tileToolbar.getButton(tileButtonId);
		b.setDelete(true);
		tileButtonId++;
	}

	void setActiveButton(TileButton button)
	{
		m_selButton = button;
		if (m_selButton != null)
		{
			m_curSprite = new ATSprite(button.getSpriteName());
		}
		else
			m_curSprite = null;
	}
	
	public int processCommands(KeyCommands keyCmds, Mouse mouse, final long deltaMs)
	{
		m_curMouse = mouse.getLocation();
		
		boolean recalcMap = false;
		
		boolean bMouseOnMap = false;
		boolean bMouseOnTileButtons = false;
		
		if (m_curMouse.x < m_buttonStartX)
			bMouseOnMap = true;
		else if (m_curMouse.x < m_tileToolbar.getPosition().getX() + m_tileToolbar.getMaxWidth())
			bMouseOnTileButtons = true;
		// TODO: add a contains method
		
		if (bMouseOnMap)
		{
			if (keyCmds.wasPressed("left"))
			{
				m_centerPt.x -= IronLegends.TILE_WIDTH;
				recalcMap = true;
			}
			if (keyCmds.wasPressed("right"))
			{
				m_centerPt.x += IronLegends.TILE_WIDTH;
				recalcMap = true;
			}
			if (keyCmds.wasPressed("down"))
			{
				m_centerPt.y += IronLegends.TILE_HEIGHT;
				recalcMap = true;
			}
			if (keyCmds.wasPressed("up"))
			{
				m_centerPt.y -= IronLegends.TILE_HEIGHT;
				recalcMap = true;
			}
			if (recalcMap)
			{
				//setMapCenter()
				//public static final 
				if (m_centerPt.x > (m_mapWidth - MapEditor.SCREEN_WIDTH/2 + (MapEditor.SCREEN_WIDTH - m_buttonStartX)))
					m_centerPt.x = m_mapWidth - MapEditor.SCREEN_WIDTH/2 + (MapEditor.SCREEN_WIDTH - m_buttonStartX);
				if (m_centerPt.x < MapEditor.SCREEN_WIDTH/2)
					m_centerPt.x = MapEditor.SCREEN_WIDTH/2;
				
				if (m_centerPt.y > (m_mapHeight - MapEditor.SCREEN_HEIGHT/2))
					m_centerPt.y = m_mapHeight - MapEditor.SCREEN_HEIGHT/2;
				if (m_centerPt.y < MapEditor.SCREEN_HEIGHT/2)
					m_centerPt.y = MapEditor.SCREEN_HEIGHT/2;
				
				m_mapEditor.centerOnPoint(m_centerPt.x, m_centerPt.y);
				m_mapCalc.centerOnPoint(m_centerPt.x, m_centerPt.y);			
			}
		}
		
		if (bMouseOnTileButtons)
		{
			if (keyCmds.wasPressed("down"))
			{
				m_tileToolbar.scrollDown(1);
			}
			else if (keyCmds.wasPressed("up"))
			{
				m_tileToolbar.scrollUp(1);
			}
			
		}
		m_mouseState.update(mouse, deltaMs, -1);
		
		m_mapName.processInput(keyCmds);
		m_rotation.processInput(keyCmds);
		{
			Iterator<Button> iter = m_cmdButtons.iterator();
			while (iter.hasNext())
			{
				Button b = iter.next();
				b.update(mouse, deltaMs);
			}
		}

		if (m_rotation.getText().length() > 0)
		{
			try{
				m_rotationIncDeg = Double.parseDouble(m_rotation.getText());
			}catch(java.lang.NumberFormatException ex)
			{
				m_rotationIncDeg = 0.0;
				m_rotation.setText("");
			}
		}
		else
			m_rotationIncDeg = 0.0;
		
		if (m_saveBt.wasLeftClicked())
		{
			String mapName = m_mapName.getText();
			MapEditorSave save = new MapEditorSave(m_mapWidth, m_mapHeight, this);
			MapLoader.saveLayer(save, "maps/" + mapName + ".txt", m_mapEditor.m_rr);
		}
		if (m_loadBt.wasLeftClicked())
		{
			m_mapLayer.clear();
			String mapName = m_mapName.getText();
			MapEditorLoadSink sink = new MapEditorLoadSink(this);
			MapLoader.loadLayer(sink, "maps/" + mapName + ".txt", m_mapEditor.m_rr);
		}
	
		// update each button
		Iterator<TileButton> btIter = m_tileToolbar.iterator();
		while (btIter.hasNext())
		{
			TileButton b = btIter.next();
			b.update(mouse, deltaMs);
			if (b.wasLeftClicked())
			{
				setActiveButton(b);
			}
		}
		
		if (m_curMouse.x < m_buttonStartX && m_selButton != null)
		{
			if (keyCmds.wasPressed("rotateCW"))
			{
				m_curRotationDeg += m_rotationIncDeg;
			}
			else if (keyCmds.wasPressed("rotateCCW"))
			{
				m_curRotationDeg -= m_rotationIncDeg;
			}
			
			if (m_mouseState.wasLeftClicked())
			{
				// determine where on grid was clicked
				Point pt = m_mouseState.LeftDownPt();
				// convert to world coordinates
				//System.out.println("Mouse Click Point: " + pt);
				Vector2D worldPt = m_mapCalc.screenToWorld(pt);
				if (m_selButton.isDelete())
				{
					// search for item to delete
					// since dealing with rotation, either switch so can use contains method (shape)
					// or just require that the center be within "delta" ..
					// start with center within delta, switch if need to.
					int idx = m_mapLayer.selectClosest(worldPt, m_tileToolbar.getMaxWidth()/2);
					if (idx >= 0)
						m_mapLayer.remove(idx);
				}
				else if (worldPt.getX() < m_mapWidth && worldPt.getY() < m_mapHeight)
				{
					// add new map item based on m_selButton					
					m_mapLayer.add(new SpriteMapItem(worldPt, Math.toRadians(m_curRotationDeg), m_selButton.getCode(), m_selButton.getSpriteName()));					
				}
			}
		}
		
		return super.processCommands(keyCmds, mouse, deltaMs);
	}
	
	public void render(RenderingContext rc)
	{
		// map item selection items
		m_tileToolbar.render(rc);		
		m_cmdButtons.render(rc);
		//m_mapName.render(rc);
		
		// render status info
		{
			TextWriter text = new TextWriter(rc);
			text.setY(m_cmdButtons.getHeight()+m_cmdButtons.getPosition().y);
			text.setLineStart(m_cmdButtons.getPosition().x);
			
			text.setFont(m_fonts.instructionalFont);
			Vector2D wc = m_mapCalc.screenToWorld(m_mapCalc.getCenter());
			text.println("Center in WC:");
			text.println((int)wc.getX() + "," + (int)wc.getY());
			
			wc = m_mapCalc.screenToWorld(new Point(m_curMouse.x, m_curMouse.y));
			text.println("Mouse in WC:");
			text.println((int)wc.getX() + "," + (int)wc.getY());			
		}
		
		// render mouse
		if (m_curSprite != null &&
				m_curMouse.x < m_buttonStartX)
		{
			
			m_curSprite.setCenterPosition(new Vector2D(m_curMouse.x, m_curMouse.y));
			m_curSprite.setRotation(Math.toRadians(m_curRotationDeg));
			m_curSprite.render(rc);
		}				
	}

	public void setWorldDim(int width, int height) 
	{
		m_mapWidth	= width;
		m_mapHeight = height;
		m_centerPt.x = width/2;
		m_centerPt.y = height/2;
		
		m_mapEditor.setWorldBounds(0,0, width, height);
		m_mapCalc.setWorldBounds(0,0, width, height);
		/*
		 // todo turn into an update for visible bounds so can call it when needed just clamp to rectangle
		if (m_centerPt.x > (m_mapWidth - IronLegends.SCREEN_WIDTH/2 + (IronLegends.SCREEN_WIDTH - m_buttonStartX)))
			m_centerPt.x = m_mapWidth - IronLegends.SCREEN_WIDTH/2 + (IronLegends.SCREEN_WIDTH - m_buttonStartX);
		if (m_centerPt.x < IronLegends.SCREEN_WIDTH/2)
			m_centerPt.x = IronLegends.SCREEN_WIDTH/2;
		
		if (m_centerPt.y > (m_mapHeight - IronLegends.SCREEN_HEIGHT/2))
			m_centerPt.y = m_mapHeight - IronLegends.SCREEN_HEIGHT/2;
		if (m_centerPt.y < IronLegends.SCREEN_HEIGHT/2)
			m_centerPt.y = IronLegends.SCREEN_HEIGHT/2;
		
		VISIBLE_BOUNDS = new Rectangle(
				IronLegends.SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2,
				WORLD_WIDTH - 2 * (SCREEN_WIDTH / 2), WORLD_HEIGHT - 2 * (SCREEN_HEIGHT / 2));

		*/
		
		m_mapEditor.centerOnPoint(m_centerPt.x, m_centerPt.y);
		m_mapCalc.centerOnPoint(m_centerPt.x, m_centerPt.y);
	}

}
