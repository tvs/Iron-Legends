package jig.ironLegends.mapEditor;

import jig.ironLegends.core.ui.Button;

public class TileButton extends Button 
{
	protected String m_sCode;
	protected boolean m_bIsDelete;
	protected String m_sRsc;
	
	
	public TileButton(String sCode, int id, int sx, int sy, String rsc)
	{
		super(id, sx, sy, rsc);
		m_sCode = sCode;
		m_sRsc = rsc;
	}
	
	public String getSpriteName()
	{
		return m_sRsc;
	}
	
	public void setDelete(boolean isDelete)
	{
		m_bIsDelete = isDelete;
	}
	public boolean isDelete()
	{
		return m_bIsDelete;
	}
	
	public String getCode() { return m_sCode;}
}
