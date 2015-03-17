package com.labs2160.slacker.plugin.misc;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import jersey.repackaged.com.google.common.collect.Lists;

import com.labs2160.slacker.api.Action;
import com.labs2160.slacker.api.InvalidRequestException;
import com.labs2160.slacker.api.SlackerException;
import com.labs2160.slacker.api.WorkflowContext;

public class RandomPickerAction implements Action {
	
	/** randomizer */
	private static final SecureRandom RANDOM = new SecureRandom(Long.toString(System.currentTimeMillis()).getBytes());

	@Override
	public boolean execute(WorkflowContext ctx) throws SlackerException {
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
