package com.labs2160.slacker.plugin.slackchat;

import org.jivesoftware.smack.SmackException;
import org.junit.Before;
import org.junit.Test;

public class SlackChatCollectorTest {

    private SlackChatCollector collector;

    @Before
    public void before() {
        collector = new SlackChatCollector("xmpp.host", "xmpp.user", "xmpp.password",
                "xmpp.muc.nickname", "xmpp.muc.domain", "xmpp.muc.keyword");
    }

    @Test
    public void testJoinRooms() throws SmackException {
        collector.addRoom("test");
        collector.addRoom("hello");
        collector.joinRooms();
    }
}
