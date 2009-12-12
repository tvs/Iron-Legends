package jig.ironLegends.core;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import jig.engine.FontResource;
import jig.engine.ResourceFactory;

public class Fonts 
{
	public Fonts()
	{
		scoreboardFont = null;
		instructionalFont = null;
		smInstructionalFont = null;
		inYourFaceFont = null;
		titleFont = null;
		gameInfoFont = null;
		textFont = null;
	}
	public void create(ResourceFactory resourceFactory)
	{
		scoreboardFont = resourceFactory.getFontResource(new Font("Sans Serif",
				Font.PLAIN, 12), Color.black, null);
		instructionalFont = resourceFactory.getFontResource(new Font(
				"Sans Serif", Font.BOLD, 36), Color.white, null);
		smInstructionalFont = resourceFactory.getFontResource(new Font(
				"Sans Serif", Font.BOLD, 18), Color.white, null);
		
		inYourFaceFont = resourceFactory.getFontResource(new Font("Sans Serif",
				Font.BOLD, 48), Color.red, null);
		titleFont = resourceFactory.getFontResource(new Font("Sans Serif",
				Font.PLAIN, 96), Color.black, null);
		
		gameInfoFont = resourceFactory.getFontResource(new Font("Sans Serif", Font.BOLD, 16)
				, Color.black, null);
		
		
		try {
			URL url = this.getClass().getResource("/fonts/FFFATLAN.TTF");			
			InputStream is = (url == null ? new BufferedInputStream(new FileInputStream("fonts/FFFATLAN.TTF")) : url.openStream());
			Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 8f);
			textFont = resourceFactory.getFontResource(font, Color.WHITE, null);
		} catch (Exception e) {
			System.err.println("Error: Loading stock font FFFATLAN.TTF");
			textFont = resourceFactory.getFontResource(new Font("Sans Serif", Font.PLAIN, 12), Color.WHITE, null);
		}
	}
	
	public FontResource gameInfoFont;
	public FontResource scoreboardFont;
	public FontResource instructionalFont;
	public FontResource smInstructionalFont;
	public FontResource inYourFaceFont;
	public FontResource titleFont;
	public FontResource textFont;
}
