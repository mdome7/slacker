package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.*;
import com.labs2160.slacker.api.annotation.ActionDescription;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.util.Map;
import java.util.Properties;

@ActionDescription(
        name = "Calculator",
        description = "Evaluates mathematical expressions and returns results",
        argsSpec = "<mathematical expression>",
        argsExample = "(1 + 5) / 2"
)
public class MathAction implements Action {

    private Evaluator evaluator;

    public MathAction() {
        evaluator = new Evaluator();
    }

    @Override
    public void setComponents(Map<String, Resource> resources, Properties config) {
        // do nothing
    }

    @Override
    public boolean execute(SlackerContext ctx) throws SlackerException {
        if (ctx.getRequestArgs() == null || ctx.getRequestArgs().length == 0) {
            throw new NoArgumentsFoundException("Arguments required");
        }
        final String expr = join(ctx.getRequestArgs());

        try {
            String result = evaluator.evaluate(expr);
            if (result.endsWith(".0")) {
                result = result.substring(0, result.length() - 2);
            }
            ctx.setResponseMessage(result);
        } catch (EvaluationException e) {
            throw new SlackerException("Could not evaluate expression: " + expr, e);
        }
        return true;
    }

    private String join(String [] tokens) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < tokens.length; i++) {
            sb.append(tokens[i]);
            if (i < tokens.length - 1) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

}
