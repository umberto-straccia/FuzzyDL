package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;

/**
 * Minimize subsumption query.
 * @author Fernando Bobillo
 */ 
public class MaxSubsumesQuery extends SubsumptionQuery
{

	public MaxSubsumesQuery(Concept c1, Concept c2, int type) throws FuzzyOntologyException
	{
		super(c1, c2, type);
	}


	@Override
	public void preprocess(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		Individual ind = kb.getNewIndividual();
		Concept conc;

		switch (type)
		{
			case LUKASIEWICZ:
				conc = Concept.lOr(Concept.complement(c2), c1);
				break;

			case GOEDEL:
				conc = Concept.gImplies(c2, c1);
				break;

			case ZADEH:
				conc = Concept.zImplies(c2, c1);
				break;

			default:
				conc = Concept.gOr(Concept.complement(c2), c1);
		}

		Variable q = kb.milp.getVariable(ind, conc);
		kb.old01Variables += 1;
		objExpr = new Expression(new Term(-1,q));

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
			if ( (ConfigReader.OPTIMIZATIONS == 0) || kb.hasNominalsInTBox() )
			{
				cloned = kb.clone();
				cloned.solveABox();
			}
			else
				cloned = kb.cloneWithoutABox();

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
		return c1 + " subsumes " + c2 + " ? <= ";
	}

}
