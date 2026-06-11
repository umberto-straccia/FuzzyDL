package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;

/**
 * Lowest upper bound of a concept assertion.
 * @author Fernando Bobillo
 */
public class MaxInstanceQuery extends InstanceQuery
{

	public MaxInstanceQuery(Concept concept, Individual individual) throws FuzzyOntologyException
	{
		super(concept, individual);
	}


	@Override
	public void preprocess(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		Variable q = kb.milp.getVariable(ind, conc);
		kb.old01Variables += 1;
		this.objExpr = new Expression(new Term(-1, q));

		if (conc.toString().contains("(all ") ||  conc.toString().contains("(not (b-some ") )
			kb.setDynamicBlocking();

		// a: c >= q
		kb.addAssertion(ind, conc, Degree.getDegree(q));
		kb.solveAssertions();
	}


	@Override
	public Solution solve(KnowledgeBase kb) throws FuzzyOntologyException
	{
		try
		{
			setInitialTime();
			kb.solveABox();
			KnowledgeBase cloned = kb.clone();
			preprocess(cloned);
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
		return "Is " + ind + " instance of " + conc + " ? <= ";
	}

}
