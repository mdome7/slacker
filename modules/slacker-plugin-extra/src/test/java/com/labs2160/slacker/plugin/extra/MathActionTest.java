package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.SlackerContext;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.response.SlackerOutput;
import com.labs2160.slacker.api.response.TextOutput;
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
        Assert.assertEquals("700000000", executeAction("100000000 * 7"));
    }

    @Test
    public void testDoubleRoundDown() throws SlackerException {
        Assert.assertEquals("3.3333", executeAction("10 / 3"));
    }
    @Test
    public void testDoubleRoundUp() throws SlackerException {
        Assert.assertEquals("1.6667", executeAction("5 / 3"));
    }

    @Test
    public void testDoubleTwoDigitMantissa() throws SlackerException {
        Assert.assertEquals("0.25", executeAction("1 / 4"));
    }

    private String executeAction(String input) throws SlackerException {
        SlackerContext ctx = new SlackerContext(FAKE_PATH, input.split(" "));
        SlackerOutput output = action.execute(ctx);
        Assert.assertTrue(output instanceof TextOutput);
        return ((TextOutput) output).getMessage();
    }
}
