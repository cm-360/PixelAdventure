package lib.expr;

import java.util.HashMap;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class ExpUtil {
	
	private static final HashMap<String, Double> CACHE = new HashMap<String, Double>();

	public static double calculate(String expression, HashMap<String, Double> variables) {
		String cacheKey = String.format("%s\t%s", expression, variables.toString());
		if (CACHE.containsKey(cacheKey)) {
			return CACHE.get(cacheKey);
		} else {
			Expression exp = new ExpressionBuilder(expression).variables(variables.keySet()).build();
			for (String var : variables.keySet())
				exp.setVariable(var, variables.get(var));
			double result = exp.evaluate();
			CACHE.put(cacheKey, result);
			return result;
		}
	}
	
	public static int getCacheSize() {
		return CACHE.size();
	}

}
