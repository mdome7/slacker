package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.SlackerContext;
import com.labs2160.slacker.api.SlackerException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by michaeldometita on 9/26/16.
 */
public class MathActionTest {
    private static final String [] FAKE_PATH = {"math"};

    private final MathAction action = new MathAction();

    @Test
    public void testLargeNumbersLong() throws SlackerException {
        SlackerContext ctx = new SlackerContext(FAKE_PATH, new String[]{"100000000", "*", "7"});
        action.execute(ctx);
        Assert.assertEquals("700000000", ctx.getResponse().getMessage());
    }

    @Test
    public void testDoubleRoundDown() throws SlackerException {
        SlackerContext ctx = new SlackerContext(FAKE_PATH, new String[]{"10", "/", "3"});
        action.execute(ctx);
        Assert.assertEquals("3.3333", ctx.getResponse().getMessage());
    }
    @Test
    public void testDoubleRoundUp() throws SlackerException {
        SlackerContext ctx = new SlackerContext(FAKE_PATH, new String[]{"5", "/", "3"});
        action.execute(ctx);
        Assert.assertEquals("1.6667", ctx.getResponse().getMessage());
    }

    @Test
    public void testDoubleTwoDigitMantissa() throws SlackerException {
        SlackerContext ctx = new SlackerContext(FAKE_PATH, new String[]{"1", "/", "4"});
        action.execute(ctx);
        Assert.assertEquals("0.25", ctx.getResponse().getMessage());
    }
}
