package jig.ironLegends.core;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.Map.Entry;

import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.audio.jsound.AudioClip;
import jig.engine.physics.Body;
import jig.engine.util.Vector2D;
import jig.ironLegends.Animator;

public class SpecialFx {

	class Animation
	{
		String m_sprite = null;
		Animator m_animator = null;
		private long m_durationMs;
		Vector2D m_dummyMoveVec = new Vector2D(0,1);
		
		public Animation(String sprite, Animator animator, long durationMs)
		{
			m_sprite = sprite;
			m_animator = animator;
			m_durationMs = durationMs;
		}
		String getSprite()
		{
			return m_sprite;
		}
		public void update(long deltaMs) {
			m_animator.update(deltaMs, m_dummyMoveVec);
		}
		public int getFrame()
		{
			return m_animator.getFrame();
		}
		public long durationMs()
		{
			return m_durationMs;
		}
		
	}
	
	class SpecialEffect extends Body
	{
		Animation m_animation = null;
		String m_sound = null;
		private double m_accMs;
				
		public SpecialEffect(Animation animation, String soundName)
		{
			super(animation.getSprite());
			m_sound = soundName;
			m_animation = animation;
			setActivation(false);
			m_accMs = 0;
		}

		public void play(Vector2D centerPos, SoundFx soundFx) {
			m_accMs = 0;
			
			setCenterPosition(centerPos);
			setActivation(true);
			soundFx.play(m_sound);
		}

		@Override
		public void update(long deltaMs) {
			// TODO Auto-generated method stub
			if (isActive())
			{
				m_animation.update(deltaMs);
				setFrame(m_animation.getFrame());
				
				m_accMs += deltaMs;
				if (m_accMs > m_animation.durationMs())
					setActivation(false);
			}
		}
		
	}
	class SpecialEffectPrototype
	{
		final String m_soundName;
		final String m_spriteName;
		final long m_durationMs;
		final int m_frames;
		
		public SpecialEffectPrototype(
				  final String soundName
				, final String spriteName
				, final long durationMs
				, final int frames)
		{
			m_soundName = soundName;
			m_spriteName = spriteName;
			m_durationMs = durationMs;
			m_frames = frames;
		}

		public String soundName() {
			return m_soundName;
		}

		public int frames() {
			return m_frames;
		}

		public long durationMs() {
			return m_durationMs;
		}

		public int startFrame() {
			return 0;
		}

		public String spriteName() {
			return m_spriteName;
		}
	}
	class SpecialEffectLayer
	{
		protected SpecialEffectPrototype m_factory;
		protected Vector<SpecialEffect> m_vSameType;
		
		public SpecialEffectLayer(SpecialEffectPrototype factory)
		{
			m_factory = factory;
			m_vSameType = new Vector<SpecialEffect>();
		}

		public Iterator<SpecialEffect> entries() {
			return m_vSameType.iterator();
		}

		public SpecialEffect addNew() {
			Animation animation = null;
			
			Animator animator = new Animator(m_factory.frames(), m_factory.durationMs()/m_factory.frames(), m_factory.startFrame());
			
			animation = new Animation(m_factory.spriteName(), animator, m_factory.durationMs());
			SpecialEffect sfx = new SpecialEffect(animation, m_factory.soundName());
			m_vSameType.add(sfx);
			return sfx;
		}

		public void render(RenderingContext rc) {
			Iterator<SpecialEffect> iter = m_vSameType.iterator();
			while (iter.hasNext())
			{
				SpecialEffect sfx = iter.next();
				if (sfx.isActive())
					sfx.render(rc);
			}
		}

		public void update(long deltaMs) {
			Iterator<SpecialEffect> iter = m_vSameType.iterator();
			while (iter.hasNext())
			{
				SpecialEffect sfx = iter.next();
				sfx.update(deltaMs);
			}
		}

	}
	
	public SpecialFx(SoundFx soundFx)
	{
		m_specialEffects = new TreeMap<String, SpecialEffectLayer>();
		m_soundFx = soundFx;
	}
	
	// creates inactive special effect available for later use
	public void add(String name, String soundName, String spriteName, long durationMs, int frames)
	{
		// create
		m_specialEffects.put(name, 
				new SpecialEffectLayer(
						new SpecialEffectPrototype(soundName, spriteName, durationMs, frames)));
	}
	
	public void play(String name, Vector2D centerPos)
	{
		SpecialEffectLayer v = m_specialEffects.get(name);
		if (v == null)
			return;
		
		SpecialEffect sfx = null;
		
		// find inactive
		Iterator<SpecialEffect> iter = v.entries();
		while (iter.hasNext())
		{
			sfx = iter.next(); 
			if (!sfx.isActive())
				break;
		}
		// have to add new special effect based on name
		if (sfx == null)
			sfx = v.addNew();
		
		sfx.play(centerPos, m_soundFx);
	}
	
	SoundFx m_soundFx;
	
	protected TreeMap<String, SpecialEffectLayer> m_specialEffects;

	public void render(RenderingContext rc)
	{
		Iterator<Entry<String, SpecialEffectLayer>> iter = m_specialEffects.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<String, SpecialEffectLayer> en = iter.next();
			en.getValue().render(rc);
		}

	}
	
	public void update(long deltaMs) {
		// TODO Auto-generated method stub
		
		Iterator<Entry<String, SpecialEffectLayer>> iter = m_specialEffects.entrySet().iterator();
		while (iter.hasNext())
		{
			Entry<String, SpecialEffectLayer> en = iter.next();
			en.getValue().update(deltaMs);
		}
		
	}
	 
}
