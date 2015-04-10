package com.labs2160.slacker.plugin.slackchat;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
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
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.xhtmlim.packet.XHTMLExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.labs2160.slacker.api.InvalidRequestException;
import com.labs2160.slacker.api.NoArgumentsFoundException;
import com.labs2160.slacker.api.Request;
import com.labs2160.slacker.api.RequestCollector;
import com.labs2160.slacker.api.RequestHandler;
import com.labs2160.slacker.api.Response;
import com.labs2160.slacker.api.ScheduledJob;
import com.labs2160.slacker.api.SlackerContext;
import com.labs2160.slacker.api.SlackerException;

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
public class SlackChatCollector implements RequestCollector, ChatManagerListener, ChatMessageListener {

    /** period betwen empty messages sent to HipChat server to keep connection alive */
    private final static int KEEP_ALIVE_PERIOD_SEC = 90;

    private final static Logger logger = LoggerFactory.getLogger(SlackChatCollector.class);

    private XMPPTCPConnection conn;

    private RequestHandler handler;

    /** chat used for keepalive messages */
    private Chat keepAliveChat;

    /** Jabber ID */
    private final String username;

    /** keyword used for this to react to message in a multi-user chat room - must be the first word of the message */
    private final String mucKeyword;

    /** multi-user chat room nickname */
    private final String mucNickname;

    /** multi-user chat (MUC) domain (e.g. conf.hipchat.com) */
    private final String mucDomain;

    private Map<String,MultiUserChat> rooms;

    public SlackChatCollector(String host, String username, String password, String mucNickname, String mucDomain, String mucKeyword) {
        XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                .setHost(host).setPort(5222)
                .setServiceName(host)
                .setUsernameAndPassword(username, password)
                .setResource("bot")
                .setConnectTimeout(10000)
                .setSendPresence(false)
                .build();
        this.username = username;
        this.mucNickname = mucNickname;
        this.mucDomain = mucDomain;
        this.mucKeyword = mucKeyword;
        this.rooms = new HashMap<>();

        logger.debug("username={}, mucNickname={}, mucDomain={}, mucKeyword={}", username, mucNickname, mucDomain, mucKeyword);
        conn = new XMPPTCPConnection(config);
    }

    /**
     * Add a room to join at startup.  Do not add the Conference (MUC) domain.
     * e.g. 1234_my_room (not 1234_my_room@muc.domain)
     * @param roomId
     */
    public void addRoom(String roomId) {
        logger.debug("Room added: {}", roomId);
        this.rooms.put(roomId, null);
    }

    @Override
    public void start(RequestHandler handler) {
        this.handler = handler;
        connect(false);
        try {
            conn.login();

            this.keepAliveChat = ChatManager.getInstanceFor(conn).createChat(username); // loopback chat

            ChatManager.getInstanceFor(conn).addChatListener(this);

            joinRooms();
            logger.info("HipChat: connected={}, authenticated={}", conn.isAuthenticated(), conn.isAuthenticated());
        } catch (XMPPException | SmackException | IOException e) {
            throw new IllegalStateException("Cannot initialize HipChat - " + e.getMessage(), e);
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
        final Message responseMsg = process(msg);
        sendMessage(chat, responseMsg);
    }

    private boolean sendMessage(Chat chat, String message) {
        final Message msg = new Message();
        msg.setBody(message);
        return sendMessage(chat, msg, 2);
    }

    private boolean sendMessage(Chat chat, Message msg) {
        return sendMessage(chat, msg, 2);
    }

    private boolean sendMessage(Chat chat, Message msg, int attempts) {
        try {
            chat.sendMessage(msg);
            return true;
        } catch (NotConnectedException e) {
            logger.warn("Cannot send message - {} ({} attempts left)", e.getMessage(), attempts);
            if (attempts > 0 && connect(true)) {
                return sendMessage(chat, msg, --attempts); // retry
            } else {
                logger.error("Failed to send response to {}, body={}", chat.getParticipant(), msg.getBody());
            }
            return false;
        }
    }

    private boolean connect(boolean quietly) {
        if (! conn.isConnected()) {
            try {
                logger.debug("Connecting to server");
                conn.connect();
            } catch (SmackException | IOException | XMPPException e) {
                if (quietly) {
                    logger.warn("Could not connect to server; " + e.getMessage(), e);
                    return false; // connect failed, don't throw Exception
                } else {
                    throw new IllegalStateException("Cannot connect to server; " + e.getMessage(), e);
                }
            }
        }
        return true;
    }

    @Override
    public ScheduledJob [] getScheduledJobs() {
        return null;
    }

    public void joinRooms() throws SmackException {
        for (String roomId : rooms.keySet()) {
            try {
                joinRoom(roomId);
            } catch (NoResponseException | XMPPException | NotConnectedException e) {
                logger.warn("Could not join room \"{}\" - {}", roomId, e.getMessage());
            }
        }
    }

    private void joinRoom(final String roomId) throws XMPPException, SmackException {
        logger.info("Joining room: {}", roomId);
        MultiUserChatManager mucm = MultiUserChatManager.getInstanceFor(conn);
        final MultiUserChat chat = mucm.getMultiUserChat(roomId + "@" + mucDomain);
        chat.addMessageListener(new MessageListener() {
            @Override
            public void processMessage(Message message) {
                // only process messages not set by me and starts with the mucKeyword
                logger.debug("Room({}) {}", roomId, message.getBody());
                if (message.getBody() != null &&
                        message.getFrom().indexOf(mucNickname) < 0  &&
                        message.getBody().startsWith(mucKeyword)) {
                    logger.debug("Message from {} - {}", message.getFrom(), message.getBody());
                    Message responseMsg = process(message);
                    try {
                        chat.sendMessage(responseMsg);
                    } catch (NotConnectedException | XMPPException e) {
                        logger.warn("Cannot send response to room: {} - {}", roomId, e.getMessage());
                    }
                }
            }
        });

        rooms.put(roomId, chat);
        chat.join("Chef the Robot");
    }

    public Message process(Message msg) {
        Message responseMsg = new Message();
        try {
            String body = msg.getBody();
            if (body == null || body.trim().length() == 0) {
                logger.trace("Empty message from {}", msg.getFrom());
            } else {
                logger.debug("Message from {}: {}", msg.getFrom(), msg.getBody());
                String [] requestTokens = body.split(" ");

                if (mucKeyword.equals(requestTokens[0])) {
                    requestTokens = Arrays.copyOfRange(requestTokens, 1, requestTokens.length);
                }

                SlackerContext ctx = handler.handle(new Request("hipchat", requestTokens));
                responseMsg = createResponseMessage(ctx.getResponse());
            }
        } catch (NoArgumentsFoundException e) {
            logger.warn("Missing arguments {}, request={} ({})", msg.getFrom(), msg.getBody(), e.getMessage());
            responseMsg.setBody(Emoticon.RUKM + " You need to supply arguments");
        } catch (InvalidRequestException e) {
            logger.warn("Invalid request from {}, request={} ({})", msg.getFrom(), msg.getBody(), e.getMessage());
            responseMsg.setBody(Emoticon.SHRUG + " I could understand your gibberish");
        } catch (SlackerException e) {
            logger.error("Error while trying to handle HipChat message from {}", msg.getFrom(), e);
            responseMsg.setBody(Emoticon.DOH + " I'm not able to help you out right now.");
        }
        return responseMsg;
    }

    private Message createResponseMessage(Response response) {
        Message responseMsg = new Message();
        responseMsg.setBody(response.getMessage());

        XHTMLExtension xhtmlExtension = new XHTMLExtension();
        String html = OutputUtil.cleanResponse(response);
        logger.debug(html);
        xhtmlExtension.addBody(html);
        responseMsg.addExtension(xhtmlExtension);
        return responseMsg;
    }
}
