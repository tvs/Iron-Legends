package jig.ironLegends.screens;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.IronLegends;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.ui.Button;
import jig.ironLegends.core.ui.ButtonToolbar;
import jig.ironLegends.core.ui.RolloverButton;
import jig.ironLegends.core.ui.ServerButton;
import jig.ironLegends.mapEditor.TileButton;
import jig.ironLegends.oxide.client.ILClientThread;
import jig.ironLegends.oxide.packets.ILServerAdvertisementPacket;


// TODO Scrolling server selector!
/**
 * A menu screen for handling the server selection and joining
 *  
 * @author Travis Hall
 */
public class ServerSelectScreen extends GameScreen {	
	protected Sprite bg;
	protected Sprite banner;
	protected Sprite shader;
	
	protected Sprite server_text;
	protected Sprite map_text;
	protected Sprite players_text;
	protected Sprite separator;
	
	protected RolloverButton up_button;
	protected RolloverButton down_button;
	protected RolloverButton create_button;
	protected RolloverButton connect_button;
	protected RolloverButton bbutton;
	
	protected Fonts fonts;
	protected ILClientThread client;
	
	protected int offset;
	protected int buttonID = 0;
	
	protected ButtonToolbar<ServerButton> serverSelect;
	protected Map<InetSocketAddress, ServerButton> servers;
	
	protected ServerButton activeButton;
		
	public ServerSelectScreen(int name, Fonts fonts, ILClientThread client) {
		super(name);
		
		this.fonts = fonts;
		this.client = client;
		
		bg = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#background");
		bg.setPosition(new Vector2D(0, 0));
		
		banner = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#banner");
		banner.setPosition(new Vector2D(34, 0));
		
		shader = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#big-shader");
		shader.setPosition(new Vector2D(96, 52));
		
		server_text = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#server-text");
		server_text.setPosition(new Vector2D(201, 28));
		
		map_text = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#map-text");
		map_text.setPosition(new Vector2D(456, 28));
		
		players_text = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#players-text");
		players_text.setPosition(new Vector2D(593, 28));
		
		separator = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#separator");
		separator.setPosition(new Vector2D(96, 52));
		
		create_button = new RolloverButton(-1, 593, 491, IronLegends.SCREEN_SPRITE_SHEET + "#create-button");
		connect_button = new RolloverButton(-2, 614, 535, IronLegends.SCREEN_SPRITE_SHEET + "#connect-button");
		bbutton = new RolloverButton(-3, 0, 535, IronLegends.SCREEN_SPRITE_SHEET + "#back-button");
		
		up_button = new RolloverButton(-4, 667, 392, IronLegends.SCREEN_SPRITE_SHEET + "#up-arrow");
		down_button = new RolloverButton(-5, 685, 392, IronLegends.SCREEN_SPRITE_SHEET + "#down-arrow");
		
		this.serverSelect = new ButtonToolbar<ServerButton>(96, 52);
		this.servers = new HashMap<InetSocketAddress, ServerButton>();
	}
	
	@Override
	public void render(RenderingContext rc) {
		bg.render(rc);
		banner.render(rc);
		shader.render(rc);
		separator.render(rc);
		server_text.render(rc);
		map_text.render(rc);
		players_text.render(rc);
		
		create_button.render(rc);
		connect_button.render(rc);
		bbutton.render(rc);
		
		this.serverSelect.render(rc);
		
		up_button.render(rc);
		down_button.render(rc);
	}
	
	@Override
	public int processCommands(KeyCommands keyCmds, Mouse mouse, final long deltaMs)
	{
		if (this.client.servers != null) {
			synchronized(this.client.servers) {
				Set<InetSocketAddress> addr = this.client.servers.keySet();
				
				Iterator<InetSocketAddress> itr = addr.iterator();
				
				while(itr.hasNext()) {
					InetSocketAddress sa = itr.next();
					ILServerAdvertisementPacket p = this.client.servers.get(sa);
					
					if(!this.servers.containsKey(sa)) {
						ServerButton b = new ServerButton(this.buttonID++, 0, 0, IronLegends.SCREEN_SPRITE_SHEET + "#csshader", sa);
						b.setFont(this.fonts.textFont);
						b.setText(p.serverName);
						
						this.servers.put(sa, b);
						this.serverSelect.append(b);
					} else {
						Button b = this.servers.get(sa);
						b.setText(p.serverName);
					}
				}
			}
		}
		
		Iterator<ServerButton> btIter = this.serverSelect.iterator();
		while (btIter.hasNext())
		{
			ServerButton b = btIter.next();
			b.update(mouse, deltaMs);
			if (b.wasLeftClicked())
			{
				setActiveButton(b);
			}
		}
		
		bbutton.update(mouse, deltaMs);
		if (bbutton.wasLeftClicked()) {
			this.client.setActive(false);
			this.client.setLookingForServers(false);
			return IronLegends.SPLASH_SCREEN;
		}
		
		create_button.update(mouse, deltaMs);
		if (create_button.wasLeftClicked())
			System.out.println("Woah woah woah, not yet implemented. Settle down!");
		
		connect_button.update(mouse, deltaMs);
		if (connect_button.wasLeftClicked()) {
			if (this.activeButton != null) {
				try {
					this.client.connectTo(this.activeButton.socketAddress.getAddress(),
										  this.activeButton.socketAddress.getPort());
					return IronLegends.GAMEPLAY_SCREEN;
				} catch (IOException e) {
					System.err.println("Unable to connect");
				}
			}
		}
		
		up_button.update(mouse, deltaMs);
		if(up_button.wasLeftClicked()) {
			this.serverSelect.scrollUp(1);
		}
		down_button.update(mouse, deltaMs);
		if(down_button.wasLeftClicked()) {
			this.serverSelect.scrollDown(1);
		}
		
		return name();		
	}
	
	private void setActiveButton(ServerButton b) {
		this.activeButton = b;
	}
}
