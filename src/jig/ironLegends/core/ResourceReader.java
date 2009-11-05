package jig.ironLegends.core;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

// loads from jar or from disk?
public class ResourceReader
{
	
	private BufferedReader m_br;

	java.net.URLConnection m_urlConnection;
	FileInputStream m_fis;
	String m_sInstallDir;

	public ResourceReader(String installDir)
	{
		m_br = null;
		m_urlConnection = null;
		m_fis = null;
		m_sInstallDir = installDir;
	}
	
	public boolean open(URL url)
	{
		if (m_br != null || m_fis != null || m_urlConnection != null)
			return false;
		
		return getBufferedReader(url);
	}
	public boolean open(String file)
	{
		if (m_br != null || m_fis != null || m_urlConnection != null)
			return false;
		
		return getBufferedReader(file);
	}	
	
	public void close()
	{
		boolean bSuccess = true;
		
		if (m_br != null)
		{
			try{m_br.close();} catch (IOException ex){bSuccess = false;}
			m_br = null;
		}
		if (m_urlConnection != null)
		{				
			m_urlConnection = null;
		}
		if (m_fis != null)
		{
			try { m_fis.close();} catch (IOException ex){bSuccess = false;}
			m_fis = null;
		}
	}
	
	public BufferedReader getBufferedReader()
	{
		return m_br;
	}
	protected boolean getBufferedReader(String file)
	{
		// try from disk
		if (m_br == null)
		{
			String sFile = m_sInstallDir + "/" + file;
			if (sFile.startsWith("file:/"))
				sFile = sFile.substring(6);
			else if (sFile.startsWith("jar:file:/"))
				sFile = sFile.substring("jar:file:/".length());
			try
			{
				m_fis = new FileInputStream(sFile);
				m_br = new BufferedReader(new InputStreamReader(m_fis));
			} catch (FileNotFoundException ex)
			{
				System.out.println(ex);					
			}
		}
		if (m_br == null)
		{
			String sFile = file;
			if (sFile.startsWith("file:/"))
				sFile = sFile.substring(6);
			else if (sFile.startsWith("jar:file:/"))
				sFile = sFile.substring("jar:file:/".length());
			try
			{
				m_fis = new FileInputStream(sFile);
				m_br = new BufferedReader(new InputStreamReader(m_fis));
			} catch (FileNotFoundException ex)
			{
				System.out.println(ex);					
			}
		}
		return (m_br != null?true:false);		
	}
	
	protected boolean getBufferedReader(URL url)
	{			
		// try from url (e.g. from JAR)
		{
			try{			
				m_urlConnection = url.openConnection();
				m_br = new BufferedReader(new InputStreamReader(m_urlConnection.getInputStream()));
			}
			catch (IOException ex)
			{
				System.out.println("Reading " + url + " Received IOException: " + ex);
			}
		}
		
		return (m_br == null?false:true);
	}
}