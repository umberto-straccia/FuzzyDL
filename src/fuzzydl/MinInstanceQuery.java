package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;

/** 
 * Greatest lower bound of a concept assertion.
 * @author Fernando Bobillo
 */ 
public class MinInstanceQuery extends InstanceQuery
{

	public MinInstanceQuery(Concept concept, Individual individual) throws FuzzyOntologyException
	{
		super(concept, individual);
	}


	@Override
	public void preprocess(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		Variable q = kb.milp.getNewVariable(Variable.UP_VARIABLE);
		kb.old01Variables += 1;
		objExpr = new Expression(new Term(1,q));

		if (conc.toString().contains("(some ") ||  conc.toString().contains("(b-some ") )
			kb.setDynamicBlocking();

		// a: not c >= 1-q
		kb.addAssertion(ind, Concept.complement(conc), Degree.getDegree(new Expression(1, new Term(-1, q))));
		kb.solveAssertions();
/*	
		Variable v = kb.milp.getVariable(ind, conc);
		kb.milp.addNewConstraint(new Expression(new Term(1,v), new Term(-1,q)), Inequation.LE);
*/	}


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
		return "Is " + ind + " instance of " + conc + " ? >= ";
	}

}
