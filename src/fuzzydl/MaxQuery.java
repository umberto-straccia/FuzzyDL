package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;

/**
 * Maximize expression query.
 * @author Fernando Bobillo
 */
public class MaxQuery extends Query
{

	/**
	 * Expression to be maximized.
	 */
	private Expression objExpr;


	public MaxQuery(Expression expr)
	{
		objExpr = Expression.negateExpression(expr);
	}


	@Override
	public void preprocess(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{

	}	


	@Override
	public Solution solve(KnowledgeBase kb) throws FuzzyOntologyException
	{
		try
		{
			setInitialTime();
			kb.solveABox();
			KnowledgeBase cloned = kb.clone();
			Solution sol = cloned.optimize(objExpr);
			setTotalTime();
			return sol;
		} catch (InconsistentOntologyException ex)
		{
			return new Solution (false);
		}
	}


	@Override
	public String toString()
	{
		return objExpr + " <= ";
	}

}
