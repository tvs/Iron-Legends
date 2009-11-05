package jig.ironLegends.screens;

import jig.ironLegends.GameProgress;
import jig.ironLegends.IronLegends;
import jig.ironLegends.PlayerInfo;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.ScreenTextLayer;
import jig.ironLegends.core.TextLayer;
import jig.ironLegends.core.TextWriter;
import jig.engine.RenderingContext;

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
			text.setFont(m_fonts.titleFont);
			text.setY(IronLegends.WORLD_HEIGHT/8);
			text.setLineStart(-1);
			
			text.println("Good Job");
			text.println(m_playerInfo.getName() + "!");
			
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

			text.setFont(m_fonts.instructionalFont);
			text.println("");
			int curLevel = m_gameProgress.getCurLevel();
			switch(curLevel)
			{
				// TODO: load intro text from level
				case 1:
					text.println("Collect the weeds");
					text.println("Stay away from the ants");
					text.println("Type 's' to use your stash of courage");
				break;
				case 2:
					text.print("Watch out for the spiders!");
				break;
				case 3:
					text.println("No amount of courage will help");
					text.println("trap the hungry BATS");
				break;
				case 4:
					text.println("Hope you saved some stash!");
				break;
			}
		}
	}
	protected GameProgress m_gameProgress;
}
