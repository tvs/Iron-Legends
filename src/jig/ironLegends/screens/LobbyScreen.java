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
import jig.ironLegends.oxide.packets.ILStartGamePacket;

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
	
	protected RolloverButton sButton;
	
	protected TextEditBox serverNameBox;
	
	private int mapSelected = 0;
	
	//private ClientInfo playerClient;

	
	/**
	 * @param name
	 */
	public LobbyScreen(int name, Fonts fonts, IronLegends game) {
		super(name);
		
		this.fonts = fonts;
		this.game = game;
		this.game.playerClient = null;
		
		this.serverNameBox = new TextEditBox(this.fonts.textFont, -1, 574, 271, IronLegends.SCREEN_SPRITE_SHEET + "#server-entry-box");
		this.serverNameBox.setText("Server");
		
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
		
		sButton = new RolloverButton(-8, 614, 535,
				IronLegends.SCREEN_SPRITE_SHEET + "#start-button");
		
		mLButton = new RolloverButton(-4, 490, 118, IronLegends.SCREEN_SPRITE_SHEET + "#left-arrow");
		mRButton = new RolloverButton(-5, 690, 118, IronLegends.SCREEN_SPRITE_SHEET + "#right-arrow");
		
		tLButton = new RolloverButton(-6, 90, 118, IronLegends.SCREEN_SPRITE_SHEET + "#left-arrow");
		tRButton = new RolloverButton(-7, 90, 118, IronLegends.SCREEN_SPRITE_SHEET + "#right-arrow");
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
			
			sButton.render(rc);
		}
		
		TextWriter text = new TextWriter(rc);
		
		text.setY(268);
		text.setLineStart(498);
		
		text.setFont(fonts.textFont);
		text.print("Server Name  ");
		
		if (!this.game.createdServer) {
			if (this.game.client.lobbyState != null) {
				synchronized(this.game.client.lobbyState) {
					text.setLineStart(574);
					text.println(this.game.client.lobbyState.serverName);
				}
			}
		} else {
			this.serverNameBox.render(rc);

			/*
			int iCurMap = 0;
			if (this.game.m_availableMaps
			Iterator<String> mapIter = this.game.m_availableMaps.iterator();
			while (mapIter.hasNext()
			*/
			
		}
		
		// Getting lobby status
		if (this.game.client.lobbyState != null) {
			synchronized(this.game.client.lobbyState) {
				if (this.game.client.lobbyState.clients != null) {
					Iterator<ClientInfo> itr = this.game.client.lobbyState.clients.iterator();
					
					int ypos = 80;
					while(itr.hasNext()) {
						ClientInfo c = itr.next();
						text.print(c.name, 110, ypos);
						
						// hostname
						if (this.game.client.myAddress.getHostAddress().equals(c.clientIP))
						{
							if (this.game.playerClient == null)
							{
								this.game.playerClient = c;
							}
							tLButton.setPosition(new Vector2D(305, ypos));
							tLButton.render(rc);
							tRButton.setPosition(new Vector2D(350, ypos));
							tRButton.render(rc);							
						}
						/*
						if (c.name.compareTo(this.game.m_playerInfo.getName()) == 0) {
							if (this.playerClient == null)
							{
								this.playerClient = c;
							}
							tLButton.setPosition(new Vector2D(305, ypos));
							tLButton.render(rc);
							tRButton.setPosition(new Vector2D(350, ypos));
							tRButton.render(rc);
						}
						*/
						
						text.print("Team  ", 270, ypos);
						String txt = "NIL";
						if (c.team == ClientInfo.RED_TEAM) {
							txt = "RED";
						} else if (c.team == ClientInfo.BLU_TEAM) {
							txt = "BLU";
						}
						text.print(txt, 315, ypos);
						
						ypos += VERT_OFFSET;
					}
				}
			}
		}
		
		// Server/Client specific printing
		if (this.game.createdServer) {
			int ind = this.game.getMapName().lastIndexOf('/');
			int fin = this.game.getMapName().length();
			text.print(this.game.getMapName().substring(ind+1, fin - 4), 580, 120);
		} else {
			int ind = this.game.client.lobbyState.map.lastIndexOf('/');
			int fin = this.game.client.lobbyState.map.length();
			text.print(this.game.client.lobbyState.map.substring(ind+1, fin-4), 580, 120);
		}
		
		
	}
	
	@Override
	public int processCommands(KeyCommands keyCmds, Mouse mouse, final long deltaMs)
	{
		//this.game.client.update(deltaMs);
		
		//if (this.game.createdServer) {
		//	this.game.server.update(deltaMs);
		//}
		
		bbutton.update(mouse, deltaMs);
		if (bbutton.wasLeftClicked()) {
			if (this.game.createdServer) this.game.createdServer = false;
			this.game.client.disconnect();
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
			
			this.sButton.update(mouse, deltaMs);
			if (this.sButton.wasLeftClicked()) {
				ILStartGamePacket msg = ILPacketFactory.newStartGamePacket(this.game.server.packetID()
						, this.game.server.hostAddress.getHostAddress() + "\0"
						, this.game.server.hostAddress.getHostAddress() + "\0"
						, this.game.server.getMapName() + "\0");
				//TODO: during ironlegends update, keep track of how many players are sending client updates
				// if all clients are sending client updates, send start game with go = true
				// client update could be modified to acknowledge it received go and then
				// when server receives client update with go set from all clients
				// then the server can stop sending go.
				msg.m_bGo = false;
				msg.m_bSinglePlayer = false;
				this.game.server.send(msg);
			}
			
		}
		
		try {
			if (this.game.client.tickExpired()) {
				// Send our identification
				if (this.game.playerClient == null) {
					this.game.client.sendLobby(ILPacketFactory.newLobbyEventPacket(this.game.client.packetID(), 
						this.game.client.hostAddress.getHostAddress() + "\0",
						this.game.client.myAddress.getHostAddress() + "\0",
 						this.game.m_playerInfo.getName() + "\0",
						ClientInfo.RED_TEAM));
				} else {
					// Send our current state
					this.game.client.sendLobby(ILPacketFactory.newLobbyEventPacket(this.game.client.packetID(), 
							this.game.client.hostAddress.getHostAddress() + "\0",
							this.game.client.myAddress.getHostAddress() + "\0",
							this.game.playerClient.name + "\0", this.game.playerClient.team));
				}
			}
		} catch (IOException e) {
			Logger.getLogger("global").warning(e.toString());
		}
		
		// If the client has picked up a "start game" message -- we dump it into the m_client queue?
		// TODO: get rid of the m_client queue
		/* mjpp - just let ironllegends mainloop act upon this packet
		
		if (this.game.client.receivedStartGame) {
			this.game.m_client.send(this.game.client.startGamePacket);
		}
		*/
		
		return name();		
	}
	
	private void getNextMap() {
		this.mapSelected = (this.mapSelected+1) % (this.game.m_availableMaps.size());
		this.game.setMapName(this.game.m_availableMaps.get(this.mapSelected));
	}
	
	private void getPreviousMap() {
		if (this.mapSelected == 0) 
			this.mapSelected = this.game.m_availableMaps.size() - 1;
		else 
			this.mapSelected = (this.mapSelected-1) % (this.game.m_availableMaps.size());
		this.game.setMapName(this.game.m_availableMaps.get(this.mapSelected));
	}
	
	private void nextTeam() {
		if (this.game == null)
			return;
		if (this.game.playerClient == null)
			return;
		
		if (this.game.playerClient.team == ClientInfo.RED_TEAM) {
			this.game.playerClient.team = ClientInfo.BLU_TEAM;
		} else {
			this.game.playerClient.team = ClientInfo.RED_TEAM;
		}
	}

}
