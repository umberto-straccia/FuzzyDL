package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;


/**
 * Greatest lower bound of a role assertion (ind1, ind2, role)
 * @author Fernando Bobillo
 */
public class MinRelatedQuery extends RelatedQuery
{

	public MinRelatedQuery(Individual a, Individual b, String roleName)
	{
		ind1 = a;
		ind2 = b;
		role = roleName;
	}


	@Override
	public void preprocess(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		Concept conc = Concept.hasValue(role, ind2);
		Variable q = kb.milp.getVariable(ind1, conc);
		kb.addAssertion(ind1, conc, Degree.getDegree(q));
		kb.old01Variables += 1;
		objExpr = new Expression(new Term(1,q));

		if (conc.toString().contains("(some ") ||  conc.toString().contains("(b-some ") )
			kb.setDynamicBlocking();

		// a: not c >= 1-q
		kb.addAssertion(ind1, Concept.complement(conc), Degree.getDegree(new Expression(1, new Term(-1, q))));
		kb.solveAssertions();
	}


	@Override
	public Solution solve(KnowledgeBase kb) throws FuzzyOntologyException
	{
		try
		{
			setInitialTime();
			kb.oldBinaryVariables += 1;
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
		return "Is " + ind1.toString() + " related to " +  ind2.toString() + " through " + role + " ? >= ";
	}

}
