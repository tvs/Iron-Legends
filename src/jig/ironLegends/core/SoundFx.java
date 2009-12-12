package jig.ironLegends.core;

import java.util.SortedMap;
import java.util.TreeMap;

import jig.engine.ResourceFactory;
import jig.engine.audio.jsound.AudioClip;
import jig.ironLegends.IronLegends;

public class SoundFx 
{
	public SoundFx()
	{
		m_sfx = new TreeMap<String, AudioClip>();
	}
	
	public void addSfx(String name, String file) {
		AudioClip clip = ResourceFactory.getFactory().getAudioClip(IronLegends.RESOURCE_AUDIO + file);
		addSfx(name, clip);
	}
	
	public void addSfx(String name, AudioClip clip)
	{
		m_sfx.put(name, clip);		
	}
	
	public void play(String name)
	{
		AudioClip sfx = m_sfx.get(name);
		if (sfx != null)
			sfx.play(.5);
	}
	
	protected SortedMap<String,AudioClip> m_sfx;
}
