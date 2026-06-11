package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;
import java.util.ArrayList;

/**
 * Middle of maxima defuzzification query.
 * @author Fernando Bobillo
 */
public class MomDefuzzifyQuery extends DefuzzifyQuery
{

	public MomDefuzzifyQuery(Concept c, Individual ind, String featureName)
	{
		super(c, ind, featureName);
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
			kb.solveABox();
			KnowledgeBase cloned = kb.clone();
			cloned.setDynamicBlocking();
			Solution s = (new MaxSatisfiableQuery(conc, a)).solve(cloned);

			if (s.isConsistentKB() == false)
				return s;
			else
			{
				double d = s.getSolution();

				// LOM
				cloned = kb.clone();
				Individual ind = (Individual) cloned.individuals.get(a.toString());
				cloned.setDynamicBlocking();
				cloned.addAssertion(a, conc, Degree.getDegree(d));
				cloned.solveAssertions();
				if(! ind.roleRelations.containsKey(fName))
				{
					Util.println("\nWarning: Problem in defuzzification. Answer is 0.");
					return null;
				}

				ArrayList<Relation> relSet = ind.roleRelations.get(fName);
				CreatedIndividual b = (CreatedIndividual) relSet.get(0).getObjectIndividual();
				Variable q = cloned.milp.getVariable(b);
				if (q == null)
				{
					Util.println("\nWarning: Problem in defuzzification. Answer is 0.");
					return null;
				}					
						
				objExpr = new Expression(new Term(-1, q));

				Solution sol1 = cloned.optimize(objExpr); 
				if (sol1.getSolution() < 0)
					sol1 = new Solution(sol1.getSolution());

				// SOM
				objExpr = new Expression(new Term(1, q));
				Solution sol2 = cloned.optimize(objExpr); 
				if (sol2.getSolution() < 0) sol2 = new Solution(sol2.getSolution());

				// MOM
				if (sol1.isConsistentKB() && sol2.isConsistentKB())
				{
					double value = (sol1.getSolution() + sol2.getSolution()) / 2;
					kb.milp.printInstanceOfLabels(fName, a.toString(), value);
					return new Solution(value);
				}
				else
					// Returns an inconsistent KB solution
					return sol1;
			}
		} catch (InconsistentOntologyException ex)
		{
			return new Solution (false);
		}
	}


	@Override
	public String toString()
	{
		return "Middle of the maxima defuzzification of feature " + fName + " for instance " + a.toString() + " = ";
	}


	@Override
	public Expression getObjExpression(Variable q)
	{
		// Put anything here, we do not use this method
		return new Expression(new Term(-1, q));
	}
	
}
