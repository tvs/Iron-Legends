package jig.ironLegends.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;


public class HighScorePersistance 
{
	protected String m_sInstallDir;
	protected String m_sError;
	protected String m_sHigh;
	
	protected ResourceIO m_rw;
	protected ResourceIO m_rr;
	
	public HighScorePersistance(String installDir)
	{
		m_sInstallDir = installDir;

		m_rr = new ResourceIO(installDir);
		m_rw = new ResourceIO(installDir);
	}
	
	public boolean save(HighScore highScore) 
	{
		boolean bSuccess = true;
		
		m_rw.openWrite("HighScore.txt");

		BufferedWriter ps = m_rw.getBufferedWriter();
		
		try {
			ps.write(Integer.toString(highScore.getHighScore()));
			ps.write("\n");
			ps.write(highScore.getPlayer());
			ps.write("\n");
			ps.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		m_rw.close();
		
		return bSuccess;
	}
	public void load(HighScore highScore) 
	{
		m_rr.open("HighScore.txt");
		
		BufferedReader fis = null;
		fis = m_rr.getBufferedReader();
		if (fis != null)
		{
			try 
			{
				String sHighScore = fis.readLine();
				String sPlayer = fis.readLine();
				
				if (sHighScore != null && sHighScore.length() > 0)
				{
					highScore.setHighScore(Integer.parseInt(sHighScore));
					highScore.setPlayer(sPlayer);
					fis.close();				
					return;
				}			
			} catch (FileNotFoundException ex) {
			} catch (IOException ex) {
			}
		}
		
		// create file since didn't load it above
		save(highScore);
	}
}
