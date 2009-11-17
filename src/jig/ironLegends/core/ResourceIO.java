package jig.ironLegends.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

// loads from jar or from disk
public class ResourceIO
{
	
	private BufferedReader m_br;
	private BufferedWriter m_bw;

	
	java.net.URLConnection m_urlConnection;
	FileInputStream m_fis;
	
	FileOutputStream m_fos;
	
	String m_sInstallDir;

	public ResourceIO(String installDir)
	{
		m_br = null;
		m_urlConnection = null;
		m_fis = null;
		m_sInstallDir = installDir;

		m_bw = null;
		m_fos = null;
}
	
	public boolean openWrite(String file)
	{
		if (m_bw != null || m_fos != null || m_urlConnection != null)
			return false;
		return getBufferedWriter(file);
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
		if (m_bw != null)
		{
			try{m_bw.close();} catch (IOException ex){bSuccess = false;}
			m_bw = null;
		}
		if (m_fos != null)
		{
			try { m_fos.close();} catch (IOException ex){bSuccess = false;}
			m_fos = null;
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
	
	public BufferedWriter getBufferedWriter()
	{
		return m_bw;
	}
	protected boolean getBufferedWriter(String file)
	{
		// try from disk
		if (m_bw == null)
		{
			String sFile = m_sInstallDir + "/" + file;
			if (sFile.startsWith("file:/"))
				sFile = sFile.substring(6);
			else if (sFile.startsWith("jar:file:/"))
				sFile = sFile.substring("jar:file:/".length());
			try
			{
				m_fos = new FileOutputStream(sFile);
				m_bw = new BufferedWriter(new OutputStreamWriter(m_fos));
			} catch (FileNotFoundException ex)
			{
				System.out.println(ex);					
			}
		}
		if (m_bw == null)
		{
			String sFile = file;
			if (sFile.startsWith("file:/"))
				sFile = sFile.substring(6);
			else if (sFile.startsWith("jar:file:/"))
				sFile = sFile.substring("jar:file:/".length());
			try
			{
				m_fos = new FileOutputStream(sFile);
				m_bw = new BufferedWriter(new OutputStreamWriter(m_fos));
			} catch (FileNotFoundException ex)
			{
				System.out.println(ex);					
			}
		}
		return (m_br != null?true:false);		
	}
	protected boolean getBufferedWriter(URL url)
	{
		try{			
			m_urlConnection = url.openConnection();
			m_bw = new BufferedWriter(new OutputStreamWriter(m_urlConnection.getOutputStream()));
		}
		catch (IOException ex)
		{
			System.out.println("Reading " + url + " Received IOException: " + ex);
		}
		return (m_bw == null?false:true);
	}
}