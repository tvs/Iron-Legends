package jig.ironLegends.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class HighScorePersistance 
{
	protected String m_sInstallDir;
	protected String m_sError;
	protected String m_sHigh;
	
	public HighScorePersistance(String installDir)
	{
		m_sInstallDir = installDir;
	}
	
	public boolean save(HighScore highScore) 
	{
		boolean bSuccess = true;
		FileOutputStream fos = null;
		String sHighScoreFile = m_sInstallDir + "HighScore.txt";

		{
			File f = new File(sHighScoreFile);
			if (!f.exists()) 
			{
				try {
					bSuccess = f.createNewFile();
					if (bSuccess == false) {
						m_sError = "createFile was false";
					}
				} catch (IOException ex) {
					m_sError = "createFile" + ex.toString();
				}
			}
			
			if (bSuccess) 
			{
				f.setWritable(true);
			}
		}
		
		if (bSuccess) 
		{
			try {
				fos = new FileOutputStream(sHighScoreFile);
				/*
				 * PrintStream ps; ps = new PrintStream(fos);
				 * ps.print(highScore); ps.close();
				 */
			} catch (FileNotFoundException ex) {

				System.out
						.println("Writing HighScore Received FileNotFoundException: "
								+ ex);
				bSuccess = false;
				m_sError = "Writing : " + ex.toString();
			}
		}
		if (bSuccess) {
			try {
				BufferedWriter ps = new BufferedWriter(new OutputStreamWriter(
						fos));
				ps.write(Integer.toString(highScore.getHighScore()));
				ps.write("\n");
				ps.write(highScore.getPlayer());
				ps.write("\n");
				ps.close();
			} catch (IOException ex) {
				System.out.println("Writing HighScore Received IOException: "
						+ ex);
				bSuccess = false;
				m_sError = "Writing : " + ex.toString();
			}
		}
		return bSuccess;
	}
	public void load(HighScore highScore) 
	{
		FileInputStream fis = null;
		try 
		{
			fis = new FileInputStream(m_sInstallDir + "HighScore.txt");
			BufferedReader dis = new BufferedReader(new InputStreamReader(fis));
			String sHighScore = dis.readLine();
			//m_sHigh = sHighScore;
			String sPlayer = dis.readLine();
			
			if (sHighScore != null && sHighScore.length() > 0)
			{
				highScore.setHighScore(Integer.parseInt(sHighScore));
				highScore.setPlayer(sPlayer);
				return;
			}
		} catch (FileNotFoundException ex) {
		} catch (IOException ex) {
		}

		// create file since didn't load it above
		save(highScore);
	}
}
