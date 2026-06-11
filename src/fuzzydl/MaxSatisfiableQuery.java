package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;

/**
 * Maximal satisfiability degree of a fuzzy concept.
 * @author Fernando Bobillo
 */
public class MaxSatisfiableQuery extends SatisfiableQuery
{

	/**
	 * Constructor for a general satisfiability query.
	 * 
	 * @param c A fuzzy concept for which the satisfiability is to be tested.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public MaxSatisfiableQuery(Concept c) throws FuzzyOntologyException
	{
		super(c);
	}


	/**
	 * Constructor for a satisfiability query involving a specific individual.
	 * 
	 * @param c A fuzzy concept for which the satisfiability is to be tested.
	 * @param a An individual used in the satisfiability test.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public MaxSatisfiableQuery(Concept c, Individual a) throws FuzzyOntologyException
	{
		super(c, a);
	}


	@Override
	public void preprocess(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		if (conc.toString().contains("(all ") ||  conc.toString().contains("(not (b-some ") )
			kb.setDynamicBlocking();

		Variable q = kb.milp.getVariable(ind, conc);
		kb.old01Variables += 1;
		objExpr = new Expression(new Term(-1, q));
		kb.addAssertion(ind, conc, Degree.getDegree(q));	
		kb.solveAssertions();
	}


	@Override
	public Solution solve(KnowledgeBase kb) throws FuzzyOntologyException
	{
		try
		{
			setInitialTime();

			KnowledgeBase cloned;
			boolean useABox = (ind != null) || (ConfigReader.OPTIMIZATIONS == 0);

			if (useABox)
				cloned = kb.clone();
			else
				cloned = kb.cloneWithoutABox();

			if (ind == null)
				ind = cloned.getNewIndividual();

			if (useABox)
				cloned.solveABox();

			preprocess(cloned);

			Solution sol = cloned.optimize(objExpr);
			if (sol.getSolution() < 0)
				sol = new Solution(-sol.getSolution());

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
	if (ind != null)
		return "Is Concept " + conc + " satisfiable? [Individual " + ind + "] <= ";
	else
		return "Is Concept " + conc + " satisfiable? <= ";
	}
	
}
