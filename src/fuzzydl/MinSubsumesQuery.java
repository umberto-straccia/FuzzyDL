package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;

/**
 * Minimize subsumption query.
 * @author Fernando Bobillo
 */ 
public class MinSubsumesQuery extends SubsumptionQuery
{

	public MinSubsumesQuery(Concept c1, Concept c2, int type) throws FuzzyOntologyException
	{
		super(c1, c2, type);
	}


	public void preprocess(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		if (kb.isClassified() == false)
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
	
			Variable q = kb.milp.getNewVariable(Variable.UP_VARIABLE);
			kb.old01Variables += 1;
			objExpr = new Expression(new Term(1,q));
	
			kb.addAssertion(ind, Concept.complement(conc), Degree.getDegree(new Expression(1, new Term(-1, q)))); // a: not c or d >= 1-q
			kb.solveAssertions();
		}
	}


	@Override
	public Solution solve(KnowledgeBase kb) throws FuzzyOntologyException
	{
		try
		{
			Solution sol = null;
			setInitialTime();

			if (kb.isClassified() && c1.isAtomic() && c2.isAtomic())
			{
				ClassificationNode n1 = kb.getClassificationNode(c1.toString());
				ClassificationNode n2 = kb.getClassificationNode(c2.toString());
				if (n1 != null && n1.isThing())
					sol = new Solution(1); 
				else if (n2 != null && n2.isThing())
					sol = new Solution(1); 
				else
					return sol = new Solution(kb.getSubFlags(n1, n2));
			}
			else
			{
				KnowledgeBase cloned;
				if ( (ConfigReader.OPTIMIZATIONS == 0) || kb.hasNominalsInTBox() )
				{
					cloned = kb.clone();
					cloned.solveABox();
				}
				else
					cloned = kb.cloneWithoutABox();

				preprocess(cloned);
				sol = cloned.optimize(objExpr);
			}

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
		return c1 + " subsumes " + c2 + " ? >= ";
	}

}
