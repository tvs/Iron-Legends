package jig.ironLegends;

import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class BgTileGenerator//<V extends Body>
{
	public BgTileGenerator()
	{
	}
	
	//public void Tile(V layer, int ulx, int uly)
	public void Tile(BodyLayer<VanillaAARectangle> layer, String sResource
			, int ulx, int uly, int width, int height, int tileWidth, int tileHeight)
	{
		
		for (int y = uly; y < uly + height;  y += tileHeight)
		{
			for (int x = ulx; x < ulx + width; x += tileWidth)
			{
				VanillaAARectangle rect = new VanillaAARectangle(sResource) {
					
					@Override
					public void update(long deltaMs) {
						// TODO Auto-generated method stub
						
					}
				};
				rect.setPosition(new Vector2D(x, y));
				layer.add(rect);
			}
		}		
		//V i = new V();
		
		
		
	}
}
