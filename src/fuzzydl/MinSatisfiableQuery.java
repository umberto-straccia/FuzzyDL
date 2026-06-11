package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;

/**
 * Minimal satisfiability degree of a fuzzy concept.
 * @author Fernando Bobillo
 */
public class MinSatisfiableQuery extends SatisfiableQuery
{

	/**
	 * Constructor for a general satisfiability query.
	 * 
	 * @param c A fuzzy concept for which the satisfiability is to be tested.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public MinSatisfiableQuery(Concept c) throws FuzzyOntologyException
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
	public MinSatisfiableQuery(Concept c, Individual a) throws FuzzyOntologyException
	{
		super(c, a);
	}


	@Override
	public void preprocess(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		if (conc.toString().contains("(some ") ||  conc.toString().contains("(b-some ") )
			kb.setDynamicBlocking();

		Variable q = kb.milp.getNewVariable(Variable.UP_VARIABLE);
		kb.old01Variables += 1;
		objExpr = new Expression(new Term(1, q));
		kb.addAssertion(ind, Concept.complement(conc), Degree.getDegree(new Expression(1, new Term(-1, q))));
		kb.solveAssertions();
	}


	@Override
	public Solution solve(KnowledgeBase kb) throws FuzzyOntologyException
	{
		try
		{
			setInitialTime();
			kb.oldBinaryVariables += 1;

			boolean useABox = (ind != null) || (ConfigReader.OPTIMIZATIONS == 0);
			KnowledgeBase cloned;
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
		return "Is Concept " + conc + " satisfiable? [Individual " + ind + "] >= ";
	else
		return "Is Concept " + conc + " satisfiable? >= ";
	}
	
}
