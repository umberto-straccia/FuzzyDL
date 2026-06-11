package fuzzydl;

import fuzzydl.milp.*;

/**
 * Largest of maxima defuzzification query.
 * @author Fernando Bobillo
 */
public class LomDefuzzifyQuery extends DefuzzifyQuery
{

	public LomDefuzzifyQuery(Concept c, Individual ind, String featureName)
	{
		super(c, ind, featureName);
	}


	@Override
	public String toString()
	{
		return "Largest of the maxima defuzzification of feature " + fName + " for instance " + a.toString() + " = ";
	}


	@Override
	public Expression getObjExpression(Variable q)
	{
		return new Expression(new Term(-1, q));
	}
	
}
