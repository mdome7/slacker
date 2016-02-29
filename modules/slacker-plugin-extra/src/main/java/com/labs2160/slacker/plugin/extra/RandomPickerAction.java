package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.*;
import com.labs2160.slacker.api.annotation.ActionDescription;
import jersey.repackaged.com.google.common.collect.Lists;

import java.security.SecureRandom;
import java.util.*;

@ActionDescription(
        name = "Random Picker",
        description = "Randomly picks N values from a list of supplied values",
        argsSpec = "<N: number of values to pick> <value 1> <value 2> ... <value N>",
        argsExample = "2 Peter Lois Stewey Meg Chris"
)
public class RandomPickerAction implements Action {

    /** randomizer */
    private static final SecureRandom RANDOM = new SecureRandom(Long.toString(System.currentTimeMillis()).getBytes());

    @Override
    public void setConfiguration(Map<String, Resource> resources, Properties config) {
        // do nothing
    }

    @Override
    public boolean execute(SlackerContext ctx) throws SlackerException {
        String [] args = ctx.getRequestArgs();
        try {
            if (args.length < 3) {
                throw new InvalidRequestException("At least 3 arguments required - number of items to pick and at least 2 item choices");
            }
            int num = Integer.parseInt(args[0]);
            if (num <= 0) {
                throw new InvalidRequestException("Number of items to pick must be greater than 0 but was " + num);
            } else {
                List<String> choices = new LinkedList<>(Lists.newArrayList(Arrays.copyOfRange(args, 1, args.length)));
                num = Math.min(num, choices.size());
                String [] picked = new String[num];
                do {
                    picked[--num] = choices.remove(RANDOM.nextInt(choices.size()));
                } while (num > 0);
                ctx.setResponseMessage(join(picked));
            }
            return true;
        } catch (NumberFormatException e) {
            throw new InvalidRequestException("First argument has to be a number but was \"" + args[0] + "\"");
        }
    }

    public String join(String [] picked) {
        StringBuilder sb = new StringBuilder(picked[0]);
        for (int i = 1; i < picked.length; i++) {
            sb.append(" ");
            sb.append(picked[i]);
        }
        return sb.toString();
    }
}
