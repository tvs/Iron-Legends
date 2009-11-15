package jig.ironLegends.mapEditor;

import java.util.SortedMap;
import java.util.TreeMap;

import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.GridCell;
import jig.ironLegends.MapGrid;

public class MapEditorGrid extends MapGrid
{

	MapEditorGrid(int tileWidth, int tileHeight)
	{
		super(tileWidth, tileHeight);
		m_tiles = new TreeMap<String, Sprite>();
	}
	
	void render(RenderingContext rc)
	{
		// render each tile in world
		int y = 0;
		
		for (int iRow = 0; iRow < m_rows; ++iRow)
		{
			int x = 0;
			for (int iCol = 0; iCol < m_cols; ++iCol)
			{
				GridCell cell = getCell(iCol, iRow);
				Sprite s = m_tiles.get("e");
				if (cell != null)
				{
					Sprite s2 = m_tiles.get(cell.getInfo());
					if (s2 != null)
						s = s2;
				}
				
				Vector2D origPos = s.getPosition();
				
				s.setPosition(new Vector2D(x,y));
				s.render(rc);
				s.setPosition(origPos);
				
				x += m_tileWidth;
			}
			y += m_tileHeight;
		}
		
	}
	void addSprite(String code, Sprite sprite)
	{
		m_tiles.put(code, sprite);
	}
	
	SortedMap<String, Sprite> m_tiles;
}
