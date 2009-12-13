package jig.ironLegends.screens;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import jig.engine.Mouse;
import jig.engine.RenderingContext;
import jig.engine.Sprite;
import jig.engine.util.Vector2D;
import jig.ironLegends.IronLegends;
import jig.ironLegends.core.Fonts;
import jig.ironLegends.core.GameScreen;
import jig.ironLegends.core.KeyCommands;
import jig.ironLegends.core.TextWriter;
import jig.ironLegends.core.ui.RolloverButton;
import jig.ironLegends.core.ui.TextEditBox;
import jig.ironLegends.oxide.client.ClientInfo;
import jig.ironLegends.oxide.packets.ILPacketFactory;

/**
 * @author Travis Hall
 */
public class LobbyScreen extends GameScreen {
	private static int VERT_OFFSET = 10;
	
	protected IronLegends game;
	protected Fonts fonts;
	
	protected Sprite bg;
	protected Sprite banner;
	protected Sprite pShader;
	protected Sprite pText;
	protected Sprite mShader;
	protected Sprite mText;
	protected Sprite oShader;
	protected Sprite oText;
	protected RolloverButton bbutton;
	protected RolloverButton mLButton;
	protected RolloverButton mRButton;
	protected RolloverButton tLButton;
	protected RolloverButton tRButton;
	
	protected TextEditBox serverNameBox;
	
	private int mapSelected = 0;
	
	private ClientInfo playerClient;

	
	/**
	 * @param name
	 */
	public LobbyScreen(int name, Fonts fonts, IronLegends game) {
		super(name);
		
		this.fonts = fonts;
		this.game = game;
		
		this.serverNameBox = new TextEditBox(this.fonts.textFont, -1, 574, 268, IronLegends.SCREEN_SPRITE_SHEET + "#csshader");
		
		bg = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#background");
		bg.setPosition(new Vector2D(0, 0));
		
		banner = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#banner");
		banner.setPosition(new Vector2D(34, 0));
		
		pShader = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#player-shader");
		pShader.setPosition(new Vector2D(96, 52));
		
		pText = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#players-text");
		pText.setPosition(new Vector2D(201, 28));
		
		mShader = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#options-shader");
		mShader.setPosition(new Vector2D(480, 52));
		
		mText = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#map-text");
		mText.setPosition(new Vector2D(565, 28));
		
		oShader = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#options-shader");
		oShader.setPosition(new Vector2D(480, 245));
		
		oText = new Sprite(IronLegends.SCREEN_SPRITE_SHEET + "#server-text");
		oText.setPosition(new Vector2D(549, 221));
		
		bbutton = new RolloverButton(-3, 0, 535,
				IronLegends.SCREEN_SPRITE_SHEET + "#back-button");
		
		mLButton = new RolloverButton(-4, 490, 118, IronLegends.SCREEN_SPRITE_SHEET + "#left-arrow");
		mRButton = new RolloverButton(-5, 690, 118, IronLegends.SCREEN_SPRITE_SHEET + "#right-arrow");
		
		tLButton = new RolloverButton(-6, 490, 118, IronLegends.SCREEN_SPRITE_SHEET + "#left-arrow");
		tRButton = new RolloverButton(-7, 690, 118, IronLegends.SCREEN_SPRITE_SHEET + "#right-arrow");
	}
	
	@Override
	public void render(RenderingContext rc) {
		bg.render(rc);
		banner.render(rc);
		bbutton.render(rc);
		pShader.render(rc);
		pText.render(rc);
		
		mShader.render(rc);
		mText.render(rc);
		
		oShader.render(rc);
		oText.render(rc);
		
		
		if (this.game.createdServer) {
			mLButton.render(rc);
			mRButton.render(rc);
		}
		
		TextWriter text = new TextWriter(rc);
		
		text.setY(268);
		text.setLineStart(498);
		
		text.setFont(fonts.textFont);
		text.print("Server Name: ");
		
		if (!this.game.createdServer) {
			if (this.game.client.lobbyState != null) {
				synchronized(this.game.client.lobbyState) {
					text.setLineStart(574);
					text.println(this.game.client.lobbyState.serverName);
				}
			}
		} else {
			this.serverNameBox.render(rc);
		}
		
		
		if (this.game.client.lobbyState != null) {
			synchronized(this.game.client.lobbyState) {
				if (this.game.client.lobbyState.clients != null) {
					Iterator<ClientInfo> itr = this.game.client.lobbyState.clients.iterator();
					
					int ypos = 80;
					while(itr.hasNext()) {
						ClientInfo c = itr.next();
						
//						text.setY(ypos);
//						text.setLineStart(110);
//						text.print(c.name);
						text.print(c.name, 110, ypos);
						// Using the other method of manually setting the spacing breaks everything
						// who the hell knows why
						// Setline doesn't seem to work here -- spaced it out manually
//						text.setLineStart(300);
//						text.print("              Team:   ");
						
						if (c.name.compareTo(this.game.m_playerInfo.getName()) == 0) {
							this.playerClient = c;
							tLButton.setPosition(new Vector2D(315, ypos+5));
							tLButton.render(rc);
							tRButton.setPosition(new Vector2D(350, ypos+5));
							tRButton.render(rc);
						}
						
						text.print("Team: ", 270, ypos);
//						text.setLineStart(315);
						String txt = "NIL";
						if (c.team == ClientInfo.RED_TEAM) {
							txt = "RED";
						} else if (c.team == ClientInfo.BLU_TEAM) {
							txt = "BLU";
						}
//						text.println(txt);
						text.print(txt, 315, ypos);
						
						ypos += VERT_OFFSET;
					}
				}
				
//				text.setY(120);
//				text.setLineStart(580);
//				text.println(this.game.client.lobbyState.map);
				text.print(this.game.client.lobbyState.map, 580, 120);
				
			}
		}
		
	}
	
	@Override
	public int processCommands(KeyCommands keyCmds, Mouse mouse, final long deltaMs)
	{
		this.game.client.update(deltaMs);
		
		bbutton.update(mouse, deltaMs);
		if (bbutton.wasLeftClicked()) {
			if (this.game.createdServer) this.game.createdServer = false;
			return IronLegends.SERVER_SCREEN;
		}
		
		tLButton.update(mouse, deltaMs);
		tRButton.update(mouse, deltaMs);
		
		if (tLButton.wasLeftClicked() || tRButton.wasLeftClicked()) {
			this.nextTeam();
		}
		
		if (this.game.createdServer){ 
			this.serverNameBox.update(mouse, deltaMs);
			if (this.serverNameBox.isActive())
			{
				serverNameBox.processInput(keyCmds);
				this.game.server.setServerName(serverNameBox.getText().toUpperCase());
			}
			
			this.mRButton.update(mouse, deltaMs);
			if (this.mRButton.wasLeftClicked()) {
				this.getNextMap();
			}
			
			this.mLButton.update(mouse, deltaMs);
			if (this.mLButton.wasLeftClicked()) {
				this.getPreviousMap();
			}
			
			this.game.server.setMapName(this.game.getMapName());
		}
		
		try {
			// Send our identification
			if (this.playerClient == null) {
				this.game.client.send(ILPacketFactory.newLobbyEventPacket(this.game.client.packetID(), 
					this.game.m_playerInfo.getName(),
					ClientInfo.RED_TEAM));
			} else {
				// Send our current state
				this.game.client.send(ILPacketFactory.newLobbyEventPacket(this.game.client.packetID(), 
						this.playerClient.name, this.playerClient.team));
			}
		} catch (IOException e) {
			Logger.getLogger("global").warning(e.toString());
		}
		
		return name();		
	}
	
	private void getNextMap() {
		this.mapSelected = (this.mapSelected+1)%(this.game.m_availableMaps.size());
		this.game.setMapName(this.game.m_availableMaps.get(this.mapSelected));
	}
	
	private void getPreviousMap() {
		this.mapSelected = (this.mapSelected-1)%(this.game.m_availableMaps.size());
		this.game.setMapName(this.game.m_availableMaps.get(this.mapSelected));
	}
	
	private void nextTeam() {
		if (this.playerClient.team == ClientInfo.RED_TEAM) {
			this.playerClient.team = ClientInfo.BLU_TEAM;
		} else {
			this.playerClient.team = ClientInfo.RED_TEAM;
		}
	}

}
