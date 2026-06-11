package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;

/**
 * Classification query.
 * @author Fernando Bobillo
 */
public class ClassificationQuery extends Query
{

	public ClassificationQuery( )
	{

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
			kb.classify();
			return new Solution (1.0);
		} 
		catch (Exception ex)
		{
			ex.printStackTrace();
			return new Solution (false);
		}
	}


	@Override
	public String toString()
	{
		return "Classify ? <= ";
	}

}
