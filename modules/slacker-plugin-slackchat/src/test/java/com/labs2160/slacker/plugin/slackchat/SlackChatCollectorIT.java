package com.labs2160.slacker.plugin.slackchat;

import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.labs2160.slacker.api.RequestHandler;
import com.labs2160.slacker.plugin.slackchat.SlackChatCollector;

@RunWith(MockitoJUnitRunner.class)
public class SlackChatCollectorIT {

	@Mock
	private RequestHandler handler;

	private SlackChatCollector collector;

	@Before
	public void before() {
		collector = new SlackChatCollector(
		        getSystemProperty("xmpp.host"),
		        getSystemProperty("xmpp.user"),
                getSystemProperty("xmpp.password"),
                getSystemProperty("xmpp.muc.nickname"),
                getSystemProperty("xmpp.muc.domain"),
                getSystemProperty("xmpp.muc.keyword")
		        );
	}

	@Test
	public void test() throws NotConnectedException {
		collector.start(handler);
		Chat chat = ChatManager.getInstanceFor(collector.getConnection()).createChat("8618_364462");
		chat.sendMessage("hello");
	}

	private String getSystemProperty(String key) {
		String value = System.getProperty(key);
		if (value == null || value.trim().isEmpty()) {
			throw new RuntimeException("System property \"" + key + "\" must be specified");
		}
		return value;
	}
}
