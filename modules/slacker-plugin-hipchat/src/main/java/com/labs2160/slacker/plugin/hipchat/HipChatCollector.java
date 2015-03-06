package com.labs2160.slacker.plugin.hipchat;

import java.io.IOException;
import java.util.Arrays;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.InvalidRequestException;
import com.labs2160.slacker.api.Request;
import com.labs2160.slacker.api.RequestCollector;
import com.labs2160.slacker.api.RequestHandler;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.WorkflowContext;

/**
 * TLS is required for all connections.
 * The nickname used when joining rooms must match the name on the HipChat account.
 * Valid auth types are jabber:iq:auth and SASL PLAIN.
 * Communication with users from other HipChat groups is not permitted.
 * Connections are expected to be long-lived, so any clients connecting repeatedly may be rate limited.
 * Connections are dropped after 150s of inactivity. We suggest sending a single space (" ") as keepalive data every 60 seconds.
 * Room history is automatically sent when joining a room unless your JID resource is "bot".
 *
 * TODO: Rename to XMPPCollector?
 */
public class HipChatCollector implements RequestCollector, ChatManagerListener, ChatMessageListener {
	
	private final static Logger logger = LoggerFactory.getLogger(HipChatCollector.class);
	
	private XMPPTCPConnection conn;
	
	private RequestHandler handler;

	/** Jabber ID */
	private String jid;
	
	public HipChatCollector(String host, String username, String password) {
		XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
				.setHost(host).setPort(5222)
				.setServiceName(host)
				.setUsernameAndPassword(username, password)
				.setResource("bot")
				.setConnectTimeout(10000)
				.setSendPresence(true)
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
		logger.debug("HipChat disconnecting");
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
	 * Handler function for ChatManagerListener.
	 */
	@Override
	public void chatCreated(Chat chat, boolean createdLocally) {
		logger.debug("Incoming chat createdLocally={}", createdLocally);
		if (!createdLocally) {
			chat.addMessageListener(this);
		}
	}

	/**
	 * Invoke the request handler and respond with the result.
	 * Handler function for ChatMessageListener.
	 */
	@Override
	public void processMessage(Chat chat, Message msg) {
		logger.debug("Message from {} : {}", chat.getParticipant(), msg.getBody());
		try {
			String body = msg.getBody();
			if (body == null || body.trim().length() == 0) {
				logger.warn("Empty message body from {}, participant={}", msg.getFrom(), chat.getParticipant());
				sendMessage(chat, " ");
			} else {
				logger.debug("Message from {}, body={}", msg.getFrom(), msg.getBody());
				String [] parsedBody = body.split(" ");
				WorkflowContext ctx = handler.handle(new Request("hipchat", parsedBody));
				chat.sendMessage(ctx.getResponseMessage());
			}
		} catch (InvalidRequestException e) {
			logger.warn("Could not process request from {}, request={}", chat.getParticipant(), msg.getBody());
			sendMessage(chat, "Sorry buddy.  I'm not able to help you out right now.");
		} catch (SlackerException | NotConnectedException e) {
			logger.error("Error while trying to handle HipChat message from {}", chat.getParticipant(), e);
			sendMessage(chat, "Sorry buddy.  I'm not able to help you out right now.");
		}
	}
	
	private void sendMessage(Chat chat, String message) {
		try {
			chat.sendMessage(message);
		} catch (NotConnectedException e1) {
			logger.error("Cannot send error reply back to chat; {}", e1.getMessage());
		}
	}
}
