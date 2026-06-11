package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;

/**
 * Lowest upper bound of a role assertion (ind1, ind2, role)
 * @author Fernando Bobillo
 */
public class MaxRelatedQuery extends RelatedQuery
{

	public MaxRelatedQuery(Individual a, Individual b, String roleName)
	{
		ind1 = a;
		ind2 = b;
		role = roleName;
	}


	@Override
	public void preprocess(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		// glb(ind1 : b-some R ind2)
		Concept conc = Concept.hasValue(role, ind2);
		Variable q = kb.milp.getVariable(ind1, conc);
		kb.addAssertion(ind1, conc, Degree.getDegree(q));
		kb.old01Variables ++;
		objExpr = new Expression(new Term(-1, q));
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
		return "Is " + ind1.toString() + " related to " +  ind2.toString() + " through " + role + " ? <= ";
	}

}
