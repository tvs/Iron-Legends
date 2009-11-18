package jig.ironLegends;

public class Destructible 
{
	private int m_health;
	private int m_maxHealth;
	String m_sName;
	
	public Destructible(int maxHealth)
	{
		this(null, maxHealth, maxHealth);
	}
	
	public Destructible(int health, int maxHealth)
	{
		this(null, health, maxHealth);
	}
	public Destructible(String sName, int health, int maxHealth)
	{
		m_sName = sName;
		m_health = health;
		m_maxHealth = maxHealth;
	}

	/*
	 * @brief adjusts health by damage amount
	 * @return true-> destroyed, false otherwise
	 */
	public boolean causeDamage(int damage)
	{
		m_health -= damage;
		if (m_health < 0)
			m_health = 0;
		if (m_health == 0)
			return true;
		return false;
	}
	public boolean restore(int health)
	{
		m_health += health;
		if (m_health > m_maxHealth)
		{
			m_health = m_maxHealth;
			return true;
		}
		return false;
	}
	public void reset()
	{
		m_health = m_maxHealth;		
	}
	
	
}
