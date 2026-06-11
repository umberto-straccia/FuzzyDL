package fuzzydl;

import fuzzydl.milp.*;

/**
 * Smallest of maxima defuzzification query.
 * @author Fernando Bobillo
 */
public class SomDefuzzifyQuery extends DefuzzifyQuery
{

	public SomDefuzzifyQuery(Concept c, Individual ind, String fName)
	{
		super(c, ind, fName);
	}


	@Override
	public String toString()
	{
		return "Smallest of the maxima defuzzification of feature " + fName + " for instance " + a.toString() + " = ";
	}


	@Override
	public Expression getObjExpression(Variable q)
	{
		return new Expression(new Term(1, q));
	}
	
}
