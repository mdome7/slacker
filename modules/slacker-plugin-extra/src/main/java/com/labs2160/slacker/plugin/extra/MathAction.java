package com.labs2160.slacker.plugin.extra;

import com.labs2160.slacker.api.*;
import com.labs2160.slacker.api.annotation.ActionDescription;
import com.labs2160.slacker.api.annotation.ConfigParam;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

import java.util.Map;
import java.util.Properties;

@ActionDescription(
        name = "Calculator",
        description = "Evaluates mathematical expressions and returns results",
        argsSpec = "<mathematical expression>",
        argsExample = "(1 + 5) / 2",
        configParams = {
            @ConfigParam(key = "digitsAfterDecimal", description = "the maximum digits after decimal point", defaultValue = "4")
        }
)
public class MathAction implements Action {

    private Evaluator evaluator;

    public static final short DEFAULT_DIGITS_AFTER_DECIMAL = 4;

    /** digits after decimal */
    private short digitsAfterDecimal;

    public MathAction() {
        evaluator = new Evaluator();
        digitsAfterDecimal = DEFAULT_DIGITS_AFTER_DECIMAL;
    }

    @Override
    public void setComponents(Map<String, Resource> resources, Properties config) throws InitializationException {
        digitsAfterDecimal = Short.parseShort(config.getProperty("digitsAfterDecimal", "" + DEFAULT_DIGITS_AFTER_DECIMAL));
    }

    @Override
    public boolean execute(SlackerContext ctx) throws SlackerException {
        if (ctx.getRequestArgs() == null || ctx.getRequestArgs().length == 0) {
            throw new NoArgumentsFoundException("Arguments required");
        }
        final String expr = join(ctx.getRequestArgs());

        try {
            double result = evaluator.getNumberResult(expr);

            if (result == Math.floor(result) || digitsAfterDecimal == 0) {
                ctx.setResponseMessage(Long.toString(Math.round(result)));
            } else {
                double m = Math.pow(10, digitsAfterDecimal);
                result = Math.round(result * m)/m;
                ctx.setResponseMessage(Double.toString(result));
            }
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
