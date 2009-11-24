package jig.ironLegends.core;

import java.awt.Color;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

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
				"Sans Serif", Font.BOLD, 36), Color.black, null);
		smInstructionalFont = resourceFactory.getFontResource(new Font(
				"Sans Serif", Font.BOLD, 18), Color.black, null);
		
		inYourFaceFont = resourceFactory.getFontResource(new Font("Sans Serif",
				Font.BOLD, 48), Color.red, null);
		titleFont = resourceFactory.getFontResource(new Font("Sans Serif",
				Font.PLAIN, 96), Color.black, null);
		
		gameInfoFont = resourceFactory.getFontResource(new Font("Sans Serif", Font.BOLD, 16)
				, Color.black, null);
		
		
		try {
			InputStream is = new BufferedInputStream(new FileInputStream("fonts/FFFATLAN.TTF"));
			Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(Font.PLAIN, 8f);
			textFont = resourceFactory.getFontResource(font, Color.WHITE, null);
		} catch (Exception e) {
			System.err.println("Font resource error on FFFATLAN.TTF\nLoading stock font");
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
