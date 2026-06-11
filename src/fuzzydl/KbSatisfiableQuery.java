package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;

/**
 * Knowledge base satisfiability degree
 * @author Fernando Bobillo
 */
public class KbSatisfiableQuery extends Query
{


	public KbSatisfiableQuery() throws FuzzyOntologyException
	{

	}


	@Override
	public void preprocess(KnowledgeBase kb) throws FuzzyOntologyException
	{

	}


	@Override
	public Solution solve(KnowledgeBase kb) throws FuzzyOntologyException
	{
		try
		{
			boolean result = isConsistentKB(kb);
			if (result == true)
				return new Solution (1.0);
			else
				return new Solution (false);
		} catch (InconsistentOntologyException ex)
		{
			return new Solution (false);
		}
	} 


	public boolean isConsistentKB(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		kb.solveABox();
		KnowledgeBase cloned = kb.clone();
		if (cloned.individuals.size() == 0)
		{
			cloned.getNewIndividual();
			cloned.solveAssertions();
		}
		Solution sol = cloned.optimize(null);
		return (sol != null) && sol.isConsistentKB();
	}


	@Override
	public String toString()
	{
		return "Is KnowledgeBase satisfiable? = ";
	}
	
}
