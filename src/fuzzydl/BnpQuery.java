package fuzzydl;

import fuzzydl.milp.*;


/**
 * Best Non-Fuzzy Performance (BNP) of a fuzzy number.
 * @author Fernando Bobillo
 */
public class BnpQuery extends Query
{

	/**
	 * Fuzzy number.
	 */
	TriangularFuzzyNumber c;


	public BnpQuery(TriangularFuzzyNumber c)
	{
		this.c = c;
	}


	@Override
	public void preprocess(KnowledgeBase kb)
	{

	}


	@Override
	public Solution solve(KnowledgeBase kb)
	{
		return new Solution(c.getBestNonFuzzyPerformance());
	}


	@Override
	public String toString()
	{
		return "Best non-fuzzy performance of " + c.getName() + " = ";
	}

}
