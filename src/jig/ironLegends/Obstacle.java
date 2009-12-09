package jig.ironLegends;

import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.core.MultiSpriteBody;
import jig.ironLegends.mapEditor.MapItemPersist;
import jig.misc.sat.PolygonFactory;

public class Obstacle extends MultiSpriteBody {
	private static final int CRATE_MAX_HEALTH = 20;
	private static final int BASE_MAX_HEALTH = 2000;
	private static int WALL_MAX_HEALTH = 40;
	private String m_name;
	private Destructible m_destructible;
	private HealthBar m_healthBar = null;

	public String name() {
		return m_name;
	}

	Obstacle(MapItemPersist item, PolygonFactory pf) {
		super(null);

		m_name = item.name();

		Vector2D pos = item.centerPosition();

		int h = addSprite(item.resource());
		Sprite s = getSprite(h);
		super.setShape(pf.createRectangle(pos, s.getWidth(), s.getHeight()));
		super.setCenterPosition(pos);
		setRotation(Math.toRadians(item.rotDeg()));
		super.setCenterPosition(pos);

		// set destructible data
		setItemSpecificData(pf);
	}

	Obstacle(String cellInfo, PolygonFactory pf) {
		this(new MapItemPersist(cellInfo), pf);
	}

	@Override
	public void update(long deltaMs) {
		if (!isActive()) {
			return;
		}

		if (m_name.equals("wall")) {
			if (m_destructible.getHealth() <= WALL_MAX_HEALTH / 2) {
				getSprite(0).setFrame(1);
			}
		} else if (m_name.equals("redbase") || m_name.equals("bluebase")) {
			if (m_destructible.getHealth() <= BASE_MAX_HEALTH / 2) {
				getSprite(0).setFrame(1);
			}
		}
	}

	// allow caller to set damage? or create wrapper methods?
	public Destructible getDestructible() {
		return m_destructible;
	}

	@Override
	public void render(RenderingContext rc) {
		if (!isActive())
			return;

		super.render(rc);
		if (m_healthBar != null && m_destructible != null) {
			m_healthBar.render(rc, m_destructible.getHealth());
		}
		// else if (m_healthBar != null)
		{
			// m_healthBar.render(rc, 10);
		}
	}

	// TODO: override render and set frame based on health?
	// TODO: on collision, get destructible and assign damage
	private void setItemSpecificData(PolygonFactory pf) {
		// TODO: lookup from table?

		if (m_name.equals("wall")) {
			m_destructible = new Destructible(WALL_MAX_HEALTH);
		} else if (m_name.equals("rock1")) {
			// m_destructible = new Destructible(WALL_MAX_HEALTH);
			// create different shape?
			/*
			 TEST for creating a irregular polygon
			Vector2D vertices[] = new Vector2D[12];
			vertices[0] = new Vector2D(37,0);
			vertices[1] = new Vector2D(66,2);
			vertices[2] = new Vector2D(80,5);
			vertices[3] = new Vector2D(93,13);
			vertices[4] = new Vector2D(103,27);
			vertices[5] = new Vector2D(102,42);
			vertices[6] = new Vector2D(87,51);
			vertices[7] = new Vector2D(69,57);
			vertices[8] = new Vector2D(51,56);
			vertices[9] = new Vector2D(24,54);
			vertices[10] = new Vector2D(2,44);
			vertices[11] = new Vector2D(0,33);
			
			Sprite s = getSprite(0);
			double w = s.getWidth();
			double h = s.getHeight();
			
			Class factory = null;
			Class params[] = new Class[] {Vector2D.class, Vector2D[].class};
			
			try {
				factory = Class.forName("PersonsFactory");
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Method factoryCreate = null;
			try {
				factoryCreate = factory.getDeclaredMethod("createIrregularConvexNGon", params);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			PersonsConvexPolygon p = null;
			try {
				p = (PersonsConvexPolygon) factoryCreate.invoke(factory.newInstance()
						, new Object[]{
							new Vector2D(w/2, h/2)
							//new Vector2D(51, 29)
							,vertices
							}
				);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Vector2D centerPos = getCenterPosition();
			setShape(p);
			setCenterPosition(centerPos);
			m_healthBar = new HealthBar(getPosition(), 20, 20, true);
			*/
		} else if (m_name.equals("rock2")) {
			// m_destructible = new Destructible(WALL_MAX_HEALTH);
			Sprite s = getSprite(0);
			double w = s.getWidth();
			double h = s.getHeight();
			double sm = w < h ? w : h;
			sm = .75 * sm;
			// double r = Math.sqrt(w*w + h*h)/2;
			double r = Math.sqrt(sm * sm + sm * sm) / 2;
			Vector2D centerPos = getCenterPosition();
			setShape(pf.createNGon(getPosition(), r, 12));
			setCenterPosition(centerPos);
		} else if (m_name.equals("tree")) {
			// m_destructible = new Destructible(WALL_MAX_HEALTH);
		} else if (m_name.equals("crate")) {
			m_destructible = new Destructible(CRATE_MAX_HEALTH);
		} else if (m_name.equals("bluebase")) {
			m_destructible = new Destructible(BASE_MAX_HEALTH);
			m_healthBar = new HealthBar(getPosition(), m_destructible
					.getMaxHealth(), 20, true);
		} else if (m_name.equals("redbase")) {
			m_destructible = new Destructible(BASE_MAX_HEALTH);
			m_healthBar = new HealthBar(getPosition(), m_destructible
					.getMaxHealth(), 20, true);
		}
	}

	public boolean isTree() {
		return m_name.equals("tree");
	}
}
