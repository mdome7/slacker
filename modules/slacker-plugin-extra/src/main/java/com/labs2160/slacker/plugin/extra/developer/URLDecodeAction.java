package com.labs2160.slacker.plugin.extra.developer;

import com.labs2160.slacker.api.NoArgumentsFoundException;
import com.labs2160.slacker.api.SimpleAbstractAction;
import com.labs2160.slacker.api.SlackerContext;
import com.labs2160.slacker.api.SlackerException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by michaeldometita on 9/26/16.
 */
public class URLDecodeAction extends SimpleAbstractAction {

    private final static String DEFAULT_CHARSET = "UTF-8";

    @Override
    public boolean execute(SlackerContext ctx) throws SlackerException {
        String [] args = ctx.getRequestArgs();
        if (args.length == 0) {
            throw new NoArgumentsFoundException("Arguments required");
        } else {
            StringBuilder sb = new StringBuilder(args[0]);
            for (int i = 1; i < args.length; i++) {
                sb.append(" ");
                sb.append(args[i]);
            }

            try {
                ctx.setResponseMessage(URLDecoder.decode(sb.toString(), DEFAULT_CHARSET));
            } catch (UnsupportedEncodingException e) {
                // shouldn't happen unless charset is customizable and set to an invalid value
                throw new SlackerException("Error url encoding string using charset=" + DEFAULT_CHARSET + ", string=" + sb.toString());
            }
        }
        return true;
    }
}
