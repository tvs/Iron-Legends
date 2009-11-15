package jig.ironLegends;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import jig.engine.ResourceFactory;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.ConvexPolygon;
//Level
import jig.engine.util.Vector2D;
import jig.ironLegends.core.ResourceReader;
import jig.ironLegends.core.Tile;
import jig.misc.sat.PolygonFactory;

public class MapLoader 
{
	
	// return a randomized range either "left" border, "center", or "right" border
	static public int getRandBorderRange(Random random, int start, int max, int range)
	{
		float rand = random.nextFloat();
		int t = (int) (rand*3000);
		if (t < 1000)
		{
			return start;
		}
		else if (t < 2000)
		{
			return start + max - range;
		}
		else
		{
			return start + (max-range-1)/2;
		}		
	}
	static public int getRandRange(Random random, int start, int range)
	{
		float rand = random.nextFloat();
		int part = (int)(rand * 20);
		return start+ part;
	}
	
	static public boolean populate(int level, MapGrid grid, Mitko mitko
			, PolygonFactory polygonFactory
			, BodyLayer<Body> hedgeLayer
			, BodyLayer<Body> tankObstacleLayer
			, BodyLayer<Body> bgLayer
			, BodyLayer<Body> antLayer
			, BodyLayer<Body> batLayer
			, BodyLayer<Body> powerupLayer
			, BodyLayer<Body> batCaveLayer
			, BodyLayer<Body> creatures)
	{
		// NOTE: could use Level and Tile but since don't have time to play with them
		// will just a simple version of my own that I can control
		// TODO: for each hedge cell, add a "hedge" tile.
		int iCell = 0;
		int tileHeight 	= IronLegends.TILE_HEIGHT;
		int tileWidth	= IronLegends.TILE_WIDTH;
		
		int y = 0;
		int x = 0;
		Random random = new Random();
		random.setSeed(level);

		// tank collisions: trees, rocks
		// tank+bullet collision: building, walls
		
		for (int j = 0; j < grid.getRows(); ++j)
		{			
			for (int i = 0; i < grid.getCols(); ++i)
			{
				GridCell cell = grid.getCell(iCell);
				
				String cellInfo = cell.getInfo();
				boolean bEmpty = true;
				
				if (cellInfo.startsWith("wall"))
				{			
					// wall v,h always placed in same portion of tile?
					//ConvexPolygon shape = polygonFactory.createRectangle(new Vector2D(x,y), 48, 132);
					Obstacle ob = new Obstacle(x,y, cellInfo, polygonFactory);
					tankObstacleLayer.add(ob);					
				}
				else if (cellInfo.startsWith("b"))
				{
					// building
				}
				else if (cellInfo.startsWith("r"))
				{
					// rock1,rock2 -- if one sprite was the "rectangle" of the shape, we could load the sprite and get the dimensions
					// desired... for now hard code					
				}
				else if (cellInfo.startsWith("tr"))
				{
					// tree
				}
				else if (cellInfo.equals("m"))
				{
					mitko.setPosition(new Vector2D(x,y));
					mitko.setResetPosition(mitko.getPosition());
				}
				else if (cellInfo.equals("h"))
				{
					Tile hedge = new Tile(x, y, "wall");
					hedgeLayer.add(hedge);
					bEmpty = false;
				}
				else if (cellInfo.startsWith("c"))
				{
					batCaveLayer.add(new BatCave(x,y));
				}
				else if (cellInfo.startsWith("p"))
				{
					int posX = getRandRange(random, x, 3);
					int posY = getRandRange(random, y, 1);
					PowerUp p = new PowerUp(x, y, cellInfo);
					powerupLayer.add(p);
				}
				else if (cellInfo.startsWith("t"))
				{
					boolean bHorizontal = false;
					if (cellInfo.equals("th"))
						bHorizontal = true;
					
					double dx = getRandRange(random, x, tileWidth - Bat.WIDTH);
					double dy = getRandRange(random, y, tileHeight- Bat.HEIGHT);
					
					Vector2D pos = IronLegends.bodyPosToPolyPos(Bat.WIDTH, Bat.HEIGHT, new Vector2D(dx, dy));
					
					Bat creature = new Bat(bHorizontal, polygonFactory.createRectangle(pos, Bat.WIDTH, Bat.HEIGHT), mitko, batCaveLayer, creatures);
					batLayer.add(creature);
					creature = null;
				}
				
				if (bEmpty)
					bgLayer.add(new Tile(x,y, "empty"));

				iCell++;
				x += tileWidth;
			}
			
			x = 0;
			y += tileHeight;
		}
		
		random = null;		
		return true;
	}
	

	static public boolean saveGrid(String sName, String sFile, MapGrid grid, final ResourceReader rw)
	{
		boolean bSuccess = true;
		
		rw.openWrite(sFile);
		
		BufferedWriter ps = rw.getBufferedWriter();
		
		try {
			int rows = grid.getRows();
			int cols = grid.getCols();
			/*
			 name of map
			 cols rows
			 map data
			 */
			ps.write(sName + "\n");
			ps.write(Integer.toString(cols) + " " + Integer.toString(rows) + "\n");
			// from top left of map
			int iCell = 0;
			for (int row = 0; row < rows; ++row)
			{
				for (int col = 0; col < cols; ++col)
				{
					GridCell cell = grid.getCell(iCell);
					ps.write(cell.getInfo() + " ");
					iCell++;
				}
				ps.write("\n");
			}
			
			ps.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		rw.close();
		
		
		return bSuccess;
	}
	
	static public boolean loadGrid(String sFile, MapGrid grid, final ResourceReader rr)
	{
		// open file
		// read line
		// add cells
		//ResourceFactory resourceFactory = ResourceFactory.getFactory();
		URL url = ResourceFactory.findResource(IronLegends.GAME_ROOT + sFile);
		if (url != null)
		{
			//System.out.println("Located level " + level + " at\n\t" + url.toString());
			rr.open(url);
		}
		else
		{
			rr.open(sFile);
		}

		BufferedReader br = null;

		br = rr.getBufferedReader();
		if (br == null)
		{			
			rr.close();
			return false;
		}
			
		boolean bSuccess = true;

		while (bSuccess)
		{
			try
			{
				String line = br.readLine();
				String mapName = line;
				grid.setName(mapName);
				
				line = br.readLine();
				String tokens[] = line.split(" ");
				
				if (tokens.length != 2)
				{
					break;
				}
				// extract cols,rows
				int cols = 0;
				int rows = 0;
				cols = Integer.parseInt(tokens[0]);
				rows = Integer.parseInt(tokens[1]);
				tokens = null;
				
				grid.setDim(rows, cols);
				for (int j = 0; j < rows; ++j)
				{
					line = br.readLine();
					if (line == null)
					{
						bSuccess = false;
						break;
					}
					
					tokens = line.split("\\s+");
					if (tokens.length != cols)
					{
						bSuccess = false;
						break;
					}
					for (int i = 0; i < cols; ++i)
					{
						// process each cell info
						grid.setCell(i,j, tokens[i]);
					}
				}
				
				break;
				
			} catch (FileNotFoundException ex) 
			{
				bSuccess = false;
				System.out.println(ex);
			} catch (IOException ex) 
			{
				bSuccess = false;
				System.out.println(ex);
			}
		}

		rr.close();
		
		return bSuccess;
	}
}
