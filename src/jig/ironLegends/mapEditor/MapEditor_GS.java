package jig.ironLegends.mapEditor;

import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.Iterator;
import java.util.Vector;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.util.Vector2D;
import jig.ironLegends.GridCell;
import jig.ironLegends.IronLegends;
import jig.ironLegends.MapLoader;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.ui.Button;
import jig.ironLegends.core.ui.MouseState;
import jig.ironLegends.core.ui.TextEditBox;
import jig.ironLegends.mapEditor.MapCalc;
import jig.ironLegends.core.ui.IUIEvent;

public class MapEditor_GS extends GameScreen 
{

	Button m_saveBt;
	Button m_loadBt;
	TextEditBox m_mapName;
	
	Vector<TileButton> m_tileButtons;
	Point m_curMouse = new Point(0,0);
	int m_buttonStartX = 4*IronLegends.SCREEN_WIDTH/5;
	TileButton m_selButton = null;
	MapEditorGrid m_mapGrid = null;
	MouseState m_mouseState = new MouseState();
	//protected static final int 

	MapCalc m_mapCalc;
	Fonts m_fonts;
	
	MapEditor m_mapEditor;
	
	public MapEditor_GS(MapEditorGrid mapGrid, MapCalc mapCalc, Fonts fonts, MapEditor mapEditor) 
	{
		super(MapEditor.MAPEDIT_SCREEN);

		m_mapGrid = mapGrid;
		m_mapCalc = mapCalc;
		m_fonts = fonts;
		m_mapEditor = mapEditor;
		
		m_mouseState.onEnter(-1, new Point(0,0));
		// create viewable layers in order to be rendered?
		
		// create all buttons
		int sx = m_buttonStartX;
		int btX = sx + IronLegends.TILE_WIDTH;
		int btY = 10;
		

		m_saveBt = new Button(-1, btX, 10, IronLegends.SPRITE_SHEET + "#testEditBox");
		m_saveBt.initText(-1,-1, m_fonts.instructionalFont);
		m_saveBt.setText("SAVE");
		btY += IronLegends.TILE_HEIGHT;
		
		m_mapName = new TextEditBox(m_fonts.instructionalFont, -2, btX, btY, IronLegends.SPRITE_SHEET + "#testEditBox");
		btY += IronLegends.TILE_HEIGHT;
		
		m_loadBt = new Button(-3, btX, btY, IronLegends.SPRITE_SHEET + "#testEditBox");
		m_loadBt.initText(-1,-1, m_fonts.instructionalFont);
		m_loadBt.setText("LOAD");
		btY += IronLegends.TILE_HEIGHT;
		
		int sy = 10;
		m_tileButtons = new Vector<TileButton>();
		
		int tileButtonId = 0;
		m_tileButtons.add(new TileButton("w", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET + "#wall"));
		tileButtonId++;
		sy += IronLegends.TILE_HEIGHT;
		m_selButton = m_tileButtons.get(0);
		
		m_tileButtons.add(new TileButton("b", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET + "#testTile2"));			
		tileButtonId++;
		sy += IronLegends.TILE_HEIGHT;
		
		m_tileButtons.add(new TileButton("e", tileButtonId, sx, sy, IronLegends.SPRITE_SHEET + "#testEditBox"));			
		tileButtonId++;
		sy += IronLegends.TILE_HEIGHT;
		
		// master list of sprites to use for looking up code when rendering		
		Iterator<TileButton> btIter = m_tileButtons.iterator();
		while (btIter.hasNext())
		{
			TileButton b = btIter.next();
			m_mapGrid.addSprite(b.getCode(), b);
		}
	}
	
	public int processCommands(KeyCommands keyCmds, Mouse mouse, final long deltaMs)
	{
		m_mouseState.update(mouse, deltaMs, -1);
		m_curMouse = mouse.getLocation();
		m_saveBt.update(mouse, deltaMs);
		m_loadBt.update(mouse, deltaMs);
		m_mapName.processInput(keyCmds);
		m_mapName.update(mouse, deltaMs);
		
		if (m_saveBt.wasLeftClicked())
		{
			String mapName = m_mapName.getText();
			MapLoader.saveGrid(mapName, mapName+".txt", m_mapGrid, m_mapEditor.m_rr);
		}
		if (m_loadBt.wasLeftClicked())
		{
			String mapName = m_mapName.getText();
			//MapEditorGrid newGrid = new MapEditorGrid(IronLegends.TILE_WIDTH, IronLegends.TILE_HEIGHT);
			MapLoader.loadGrid(mapName + ".txt", m_mapGrid,  m_mapEditor.m_rr);
			//m_mapGrid = newGrid;
		}
	
		// update each button
		Iterator<TileButton> btIter = m_tileButtons.iterator();
		while (btIter.hasNext())
		{
			TileButton b = btIter.next();
			b.update(mouse, deltaMs);
			if (b.wasLeftClicked())
			{
				m_selButton = b;
			}
		}
		
		if (m_curMouse.x < m_buttonStartX && m_selButton != null)
		{
			if (m_mouseState.wasLeftClicked())
			{
				// update gridCell with new info
				
				// determine where on grid was clicked
				Point pt = m_mouseState.LeftDownPt();
				// convert to world coordinates
				//System.out.println("Mouse Click Point: " + pt);
				Vector2D worldPt = m_mapCalc.screenToWorld(pt);
				//System.out.println("WorldCoordinate: " + worldPt);
				GridCell cell = m_mapGrid.getCell(worldPt);
				if (cell != null)
				{
					cell.setInfo(m_selButton.getCode());
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
		
		// render buttons
		m_saveBt.render(rc);
		m_mapName.render(rc);
		m_loadBt.render(rc);
		
		// render mouse
		if (m_selButton != null && 
			m_curMouse.x < m_buttonStartX)
		{
			// render "selected" tile sprite at mouse location?
			AffineTransform at = rc.getTransform();
			double tx = m_selButton.getPosition().getX();
			tx -= m_curMouse.x;
			
			double ty = m_selButton.getPosition().getY();
			ty -= m_curMouse.y;
			
			rc.setTransform(AffineTransform.getTranslateInstance(-tx, -ty));
			m_selButton.render(rc);
			rc.setTransform(at);
		}
	}

}
