package jig.ironLegends.core;

import java.awt.Color;
import java.awt.Font;

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
	}
	
	public FontResource gameInfoFont;
	public FontResource scoreboardFont;
	public FontResource instructionalFont;
	public FontResource smInstructionalFont;
	public FontResource inYourFaceFont;
	public FontResource titleFont;
}
