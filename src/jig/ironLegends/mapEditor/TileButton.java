package jig.ironLegends.mapEditor;

import jig.ironLegends.core.ui.Button;

public class TileButton extends Button 
{
	protected String m_sCode;
	public TileButton(String sCode, int id, int sx, int sy, String rsc)
	{
		super(id, sx, sy, rsc);
		m_sCode = sCode;
	}
	
	public String getCode() { return m_sCode;}
}
