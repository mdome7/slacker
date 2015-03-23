package com.labs2160.slacker.plugin.hipchat;

import org.jivesoftware.smack.SmackException;
import org.junit.Before;
import org.junit.Test;

public class HipChatCollectorTest {

    private HipChatCollector hipchat;

    @Before
    public void before() {
        hipchat = new HipChatCollector("xmpp.host", "xmpp.user", "xmpp.password",
                "xmpp.muc.nickname", "xmpp.muc.domain", "xmpp.muc.keyword");
    }

    @Test
    public void testJoinRooms() throws SmackException {
        hipchat.addRoom("test");
        hipchat.addRoom("hello");
        hipchat.joinRooms();
    }
}
