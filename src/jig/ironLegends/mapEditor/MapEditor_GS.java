package jig.ironLegends.mapEditor;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.Vector;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.util.Vector2D;
import jig.ironLegends.IronLegends;
import jig.ironLegends.MapLoader;
import jig.ironLegends.core.ATSprite;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.ui.Button;
import jig.ironLegends.core.ui.MouseState;
import jig.ironLegends.core.ui.TextEditBox;
import jig.ironLegends.mapEditor.MapCalc;

public class MapEditor_GS extends GameScreen 
{

	Vector<Button> m_cmdButtons;
	
	Button m_saveBt;
	Button m_loadBt;
	TextEditBox m_mapName;
	TextEditBox m_rotation;
	
	double m_rotationIncDeg;
	double m_curRotationDeg;
	
	Vector<TileButton> m_tileButtons;
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
	int m_maxSpriteWidth;
	
	int m_mapWidth;
	int m_mapHeight;
	
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
		int sx = m_buttonStartX;
		int sy = 10;
		m_maxSpriteWidth = 0;
		m_tileButtons = new Vector<TileButton>();
		
		int tileButtonId = 0;
		m_tileButtons.add(new TileButton("wall", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET2 + "#wall"));
		sy += m_tileButtons.get(tileButtonId).getHeight();
		if (m_maxSpriteWidth < m_tileButtons.get(tileButtonId).getWidth())
			m_maxSpriteWidth = m_tileButtons.get(tileButtonId).getWidth();	
		tileButtonId++;
		
		setActiveButton(m_tileButtons.get(0));
		
		m_tileButtons.add(new TileButton("rock1", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET2 + "#rock1"));			
		sy += m_tileButtons.get(tileButtonId).getHeight();
		if (m_maxSpriteWidth < m_tileButtons.get(tileButtonId).getWidth())
			m_maxSpriteWidth = m_tileButtons.get(tileButtonId).getWidth();	
		tileButtonId++;
		
		m_tileButtons.add(new TileButton("rock2", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET2 + "#rock2"));			
		sy += m_tileButtons.get(tileButtonId).getHeight();
		if (m_maxSpriteWidth < m_tileButtons.get(tileButtonId).getWidth())
			m_maxSpriteWidth = m_tileButtons.get(tileButtonId).getWidth();	
		tileButtonId++;
		
		m_tileButtons.add(new TileButton("del", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET + "#testEditBox"));
		sy += m_tileButtons.get(tileButtonId).getHeight();
		if (m_maxSpriteWidth < m_tileButtons.get(tileButtonId).getWidth())
			m_maxSpriteWidth = m_tileButtons.get(tileButtonId).getWidth();
		m_tileButtons.get(tileButtonId).setDelete(true);
		tileButtonId++;
		
		// create cmd buttons
		m_cmdButtons = new Vector<Button>();
		
		int btX = sx + m_maxSpriteWidth+2;
		int btY = 10;		

		m_saveBt = new Button(-1, btX, 10, IronLegends.SPRITE_SHEET + "#testEditBox");
		m_saveBt.initText(-1,-1, m_fonts.instructionalFont);
		m_saveBt.setText("SAVE");
		m_cmdButtons.add(m_saveBt);		
		btY += m_saveBt.getHeight();
		
		m_mapName = new TextEditBox(m_fonts.instructionalFont, -2, btX, btY, IronLegends.SPRITE_SHEET + "#testEditBox");
		btY += m_mapName.getHeight();
		m_cmdButtons.add(m_mapName);		
		
		m_loadBt = new Button(-3, btX, btY, IronLegends.SPRITE_SHEET + "#testEditBox");
		m_loadBt.initText(-1,-1, m_fonts.instructionalFont);
		m_loadBt.setText("LOAD");
		m_cmdButtons.add(m_loadBt);		
		btY += m_loadBt.getHeight();
		
		m_rotation = new TextEditBox(m_fonts.instructionalFont, -4, btX, btY, IronLegends.SPRITE_SHEET + "#testEditBox");
		btY += m_rotation.getHeight();
		//m_rotation.setText(Double.toString(m_rotationIncDeg));
		m_cmdButtons.add(m_rotation);			
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
		if (m_curMouse.x < m_buttonStartX)
			bMouseOnMap = true;
		
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
				if (m_centerPt.x > (m_mapWidth - IronLegends.SCREEN_WIDTH/2 + (IronLegends.SCREEN_WIDTH - m_buttonStartX)))
					m_centerPt.x = m_mapWidth - IronLegends.SCREEN_WIDTH/2 + (IronLegends.SCREEN_WIDTH - m_buttonStartX);
				if (m_centerPt.x < IronLegends.SCREEN_WIDTH/2)
					m_centerPt.x = IronLegends.SCREEN_WIDTH/2;
				
				if (m_centerPt.y > (m_mapHeight - IronLegends.SCREEN_HEIGHT/2))
					m_centerPt.y = m_mapHeight - IronLegends.SCREEN_HEIGHT/2;
				if (m_centerPt.y < IronLegends.SCREEN_HEIGHT/2)
					m_centerPt.y = IronLegends.SCREEN_HEIGHT/2;
				
				m_mapEditor.centerOnPoint(m_centerPt.x, m_centerPt.y);
				m_mapCalc.centerOnPoint(m_centerPt.x, m_centerPt.y);			
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
			m_rotationIncDeg = Double.parseDouble(m_rotation.getText());
		else
			m_rotationIncDeg = 0.0;
		
		if (m_saveBt.wasLeftClicked())
		{
			String mapName = m_mapName.getText();
			MapEditorSave save = new MapEditorSave(m_mapWidth, m_mapHeight, this);
			MapLoader.saveLayer(save, mapName + ".txt", m_mapEditor.m_rr);
		}
		if (m_loadBt.wasLeftClicked())
		{
			m_mapLayer.clear();
			String mapName = m_mapName.getText();
			MapEditorLoadSink sink = new MapEditorLoadSink(this);
			MapLoader.loadLayer(sink, mapName + ".txt", m_mapEditor.m_rr);
		}
	
		// update each button
		Iterator<TileButton> btIter = m_tileButtons.iterator();
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
					int idx = m_mapLayer.selectClosest(worldPt, m_maxSpriteWidth/2);
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
		Iterator<TileButton> btIter = m_tileButtons.iterator();
		while (btIter.hasNext())
		{
			Button b = btIter.next();
			b.render(rc);
		}
		
		// render cmd buttons
		{
			Iterator<Button> iter = m_cmdButtons.iterator();
			while (iter.hasNext())
			{
				Button b = iter.next();
				b.render(rc);
			}
			
			m_mapName.render(rc);
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
		
		m_mapEditor.centerOnPoint(m_centerPt.x, m_centerPt.y);
	}

}
