package jig.ironLegends.core;

/**
 * Stores the state of a key to detect down to up, and up to down events
 * and exposing this in user friendly methods (isPressed, wasPressed, wasReleased, wasToggled)
 */
public class KeyState 
{
	public KeyState(int key)
	{
		m_key = key;
		m_bIsPressed	= false;
		m_bLastPressed	= m_bIsPressed;
	}

	public void updateState(jig.engine.Keyboard keyboard)
	{
		m_bLastPressed	= m_bIsPressed;
		m_bIsPressed 	= keyboard.isPressed(m_key);
	}
	
	// went either up or down
	public boolean wasToggled()
	{
		return (m_bIsPressed != m_bLastPressed);
	}
	
	// triggers on up to down only
	public boolean wasPressed()
	{
		if (m_bIsPressed && m_bLastPressed == false)
			return true;
		return false;
	}
	
	// triggers on down to up only
	public boolean wasReleased()
	{
		if (m_bIsPressed == false && m_bLastPressed)
			return true;
		return false;
	}
	public boolean isPressed()
	{
		return m_bIsPressed;
	}
	
	protected int m_key;
	
	protected boolean m_bIsPressed;
	protected boolean m_bLastPressed;
	
}
