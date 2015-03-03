package com.labs2160.slacker.plugin.hipchat;

import java.io.IOException;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.RequestCollector;
import com.labs2160.slacker.api.RequestHandler;

/**
 * TLS is required for all connections.
 * The nickname used when joining rooms must match the name on the HipChat account.
 * Valid auth types are jabber:iq:auth and SASL PLAIN.
 * Communication with users from other HipChat groups is not permitted.
 * Connections are expected to be long-lived, so any clients connecting repeatedly may be rate limited.
 * Connections are dropped after 150s of inactivity. We suggest sending a single space (" ") as keepalive data every 60 seconds.
 * Room history is automatically sent when joining a room unless your JID resource is "bot".
 * @author mike
 *
 */
public class HipChatCollector implements RequestCollector, ChatManagerListener {
	
	private final static Logger logger = LoggerFactory.getLogger(HipChatCollector.class);
	
	private XMPPTCPConnection conn;
	
	private RequestHandler handler;

	/** Jabber ID */
	private String jid;
	
	public HipChatCollector() {
		XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
				.setHost("chat.hipchat.com").setPort(5222)
				.setServiceName("chat.hipchat.com")
				.setUsernameAndPassword("8618_364462", "Ban4na!!")
				.build();
		conn = new XMPPTCPConnection(config);
	}

	@Override
	public void start(RequestHandler handler) {
		try {
			this.handler = handler;
			logger.debug("Connecting to server {}", conn.getHost());
			conn.connect();
			conn.login();
			ChatManager.getInstanceFor(conn).addChatListener(this);
			logger.info("HipChat state: connected={}, authenticated={}", conn.isAuthenticated(), conn.isAuthenticated());
		} catch (SmackException | IOException | XMPPException e) {
			throw new IllegalStateException("Unable to start the collector; " + e.getMessage(), e);
		}
	}

	@Override
	public void shutdown() {
		conn.disconnect();
	}

	@Override
	public boolean isActive() {
		return conn.isConnected();
	}

	public XMPPConnection getConnection() {
		return conn;
	}

	/**
	 * Handler function for  ChatManagerListener.
	 */
	@Override
	public void chatCreated(Chat chat, boolean createdLocally) {
		// TODO Auto-generated method stub
		
	}
}
