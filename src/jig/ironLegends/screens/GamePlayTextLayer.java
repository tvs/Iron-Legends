package jig.ironLegends.screens;

import jig.ironLegends.GameProgress;
import jig.ironLegends.IronLegends;
import jig.ironLegends.PlayerInfo;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.ScreenTextLayer;
import jig.ironLegends.core.TextWriter;

public class GamePlayTextLayer extends ScreenTextLayer
{
	protected PlayerInfo m_playerInfo;
	public GamePlayTextLayer(Fonts fonts, GameProgress gameProgress, PlayerInfo playerInfo)
	{
		super(fonts);
		m_gameProgress = gameProgress;
		m_playerInfo = playerInfo;
	}
	
	@Override
	public void render(TextWriter text)
	{
		if (m_gameProgress.getLevelProgress().isExitActivated())
		{
			// blink faster as get ready to finish
			text.setFont(m_fonts.instructionalFont);
			text.setY(IronLegends.WORLD_HEIGHT/8);
			text.setLineStart(-1);
			
			// single player
			boolean bSinglePlayer = true;
			
			if (bSinglePlayer)
			{
				if (m_gameProgress.getLevelProgress().didWin()) 
				{
					text.println("Congratulations");
					text.setFont(m_fonts.titleFont);
					text.println(m_playerInfo.getName().toUpperCase() + "!");
					
					text.setFont(m_fonts.instructionalFont);
					text.println("YOU WON!!");
				}
				else
				{
					text.println("The enemy is too strong");
					text.setFont(m_fonts.inYourFaceFont);
					text.print("You LOST ");
					text.println(m_playerInfo.getName() + "!");
					text.setFont(m_fonts.instructionalFont);
				}
			}
			else
			{
				// if your base destroyed...
				// TODO text for whether your team won or lost
			}
			text.println("");
			text.setFont(m_fonts.instructionalFont);
			text.println("Press enter to continue");
		}
		else if (m_gameProgress.getLevelProgress().isIntro())
		{
			text.setFont(m_fonts.titleFont);
			text.setY(IronLegends.WORLD_HEIGHT/8);
			text.setLineStart(-1);
			
			int i = (int)(m_gameProgress.getLevelProgress().introRemaining()/1000);
			
			Integer ii = i+1;
			
			text.println(ii.toString());
			
			// TODO: load intro text from map?
			
			
		}
	}
	protected GameProgress m_gameProgress;
}
