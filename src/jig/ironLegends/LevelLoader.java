package jig.ironLegends;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Random;

import jig.engine.ResourceFactory;
import jig.engine.physics.Body;
import jig.engine.physics.BodyLayer;
//Level
import jig.engine.util.Vector2D;
import jig.ironLegends.core.ResourceReader;
import jig.ironLegends.core.Tile;
import jig.misc.sat.PolygonFactory;

public class LevelLoader 
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
	
	static public boolean populate(int level, LevelGrid grid, Mitko mitko
			, PolygonFactory polygonFactory
			, BodyLayer<Body> hedgeLayer
			, BodyLayer<Body> bgLayer
			, BodyLayer<Body> weedLayer
			, BodyLayer<Body> antLayer
			, BodyLayer<Body> spiderLayer
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

		for (int j = 0; j < grid.getRows(); ++j)
		{			
			for (int i = 0; i < grid.getCols(); ++i)
			{
				GridCell cell = grid.getCell(iCell);
				
				String cellInfo = cell.getInfo();
				boolean bHedge = false;
				if (cellInfo.equals("m"))
				{
					mitko.setPosition(new Vector2D(x,y));
					mitko.setResetPosition(mitko.getPosition());
				}
				else if (cellInfo.equals("h") || cellInfo.equals("H"))
				{
					Tile hedge = new Tile(x, y, "hedge");
					hedgeLayer.add(hedge);
					bHedge = true;
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
				else if (cellInfo.startsWith("w"))
				{					
					// x,y is top left of 32x32
					// weed is 10x10, allow variable positioning
					// up to 20x, 20y
					int posX = getRandRange(random, x, tileWidth - Weed.WIDTH );
					int posY = getRandRange(random, y, tileHeight - Weed.HEIGHT);

					Weed wd = new Weed(posX, posY, cellInfo);
					weedLayer.add(wd);					
				}
				else if (cellInfo.startsWith("a"))
				{
					boolean bHorizontal = false;
					if (cellInfo.equals("ah"))
						bHorizontal = true;
					
					// position with 1-2 pixels of edge (i.e. 0,0 - 2,2 or tileWidth-Ant.WIDTH-					
					double dx = getRandBorderRange(random, x, tileWidth, Ant.WIDTH);
					double dy = getRandBorderRange(random, y, tileHeight, Ant.HEIGHT);
					
					Vector2D pos = IronLegends.bodyPosToPolyPos(Ant.WIDTH, Ant.HEIGHT, new Vector2D(dx, dy));
					
					Ant ant = new Ant(bHorizontal, polygonFactory.createRectangle(pos, Ant.WIDTH, Ant.HEIGHT), mitko);
					//ant.setPosition(newPos);
					antLayer.add(ant);
					creatures.add(ant);
					ant = null;
				}
				else if (cellInfo.startsWith("s"))
				{
					boolean bHorizontal = false;
					if (cellInfo.equals("sh"))
						bHorizontal = true;
					
					double dx = getRandRange(random, x, tileWidth - Spider.WIDTH);
					double dy = getRandRange(random, y, tileHeight- Spider.HEIGHT);
					
					Vector2D pos = IronLegends.bodyPosToPolyPos(Spider.WIDTH, Spider.HEIGHT, new Vector2D(dx, dy));
					
					Spider creature = new Spider(bHorizontal, polygonFactory.createRectangle(pos, Spider.WIDTH, Spider.HEIGHT), mitko);
					spiderLayer.add(creature);
					creatures.add(creature);
					creature = null;
				}
				else if (cellInfo.startsWith("b"))
				{
					boolean bHorizontal = false;
					if (cellInfo.equals("bh"))
						bHorizontal = true;
					
					double dx = getRandRange(random, x, tileWidth - Bat.WIDTH);
					double dy = getRandRange(random, y, tileHeight- Bat.HEIGHT);
					
					Vector2D pos = IronLegends.bodyPosToPolyPos(Bat.WIDTH, Bat.HEIGHT, new Vector2D(dx, dy));
					
					Bat creature = new Bat(bHorizontal, polygonFactory.createRectangle(pos, Bat.WIDTH, Bat.HEIGHT), mitko, batCaveLayer, creatures);
					batLayer.add(creature);
					creature = null;
				}
				
				if (!bHedge)
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
	

	
	static public boolean loadGrid(int level, LevelGrid grid, final ResourceReader rr)
	{
		// open file
		// read line
		// add cells
		//ResourceFactory resourceFactory = ResourceFactory.getFactory();
		URL url = ResourceFactory.findResource(IronLegends.RESOURCE_ROOT + "levels/level" + level + ".txt");
		if (url != null)
		{
			System.out.println("Located level " + level + " at\n\t" + url.toString());
			rr.open(url);
		}
		else
		{
			rr.open("levels/level" + level + ".txt");
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
