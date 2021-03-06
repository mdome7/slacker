package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.*;
import com.labs2160.slacker.api.annotation.ActionDescription;
import com.labs2160.slacker.api.response.SlackerOutput;
import com.labs2160.slacker.api.response.TextOutput;
import jersey.repackaged.com.google.common.collect.Lists;

import java.security.SecureRandom;
import java.util.*;

@ActionDescription(
        name = "Random Picker",
        description = "Randomly picks N values from a list of supplied values",
        argsSpec = "<N: number of values to pick> <value 1> <value 2> ... <value N>",
        argsExample = "2 Peter Lois Stewey Meg Chris"
)
public class RandomPickerAction extends SimpleAbstractAction {

    /** randomizer */
    private static final SecureRandom RANDOM = new SecureRandom(Long.toString(System.currentTimeMillis()).getBytes());

    @Override
    public SlackerOutput execute(SlackerContext ctx) throws SlackerException {
        String [] args = ctx.getRequestArgs();
        try {
            if (args.length < 3) {
                throw new InvalidRequestException("At least 3 arguments required - number of items to pick and at least 2 item choices");
            }
            int toPick = Integer.parseInt(args[0]);
            if (toPick <= 0) {
                throw new InvalidRequestException("Number of items to pick must be greater than 0 but was " + toPick);
            } else {
                String [] choices = Arrays.copyOfRange(args, 1, args.length);
                toPick = Math.min(toPick, choices.length);
                int upperBound = choices.length;

                String [] picked = new String[toPick];
                do {
                    int p = RANDOM.nextInt(upperBound--);
                    picked[--toPick] = choices[p];
                    if (p < upperBound) {
                        choices[p] = choices[upperBound];
                    }
                } while (toPick > 0);
                return new TextOutput(join(picked));
            }
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
