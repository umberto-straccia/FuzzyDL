package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;
import java.util.ArrayList;


/**
 * Defuzzification query.
 * @author Fernando Bobillo
 */
public abstract class DefuzzifyQuery extends Query
{

	// Individual
	protected Individual a;

	// Fuzzy concept
	protected Concept conc;

	// Name of the concrete feature
	protected String fName;

	// Objective expressiom
	protected Expression objExpr;


	public DefuzzifyQuery(Concept c, Individual ind, String featureName)
	{
		conc = c;
		a = ind;
		fName = featureName;
		MILPHelper.PRINT_VARIABLES = false;
		MILPHelper.PRINT_LABELS = false;
	}


	@Override
	public void preprocess(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		kb.setDynamicBlocking();
		Solution s = (new MaxSatisfiableQuery(conc, a)).solve(kb);

		if ((s != null) && s.isConsistentKB())
		{
			a = kb.individuals.get(a.toString());
			kb.setDynamicBlocking();
			kb.addAssertion(a, conc, Degree.getDegree(s.getSolution()));
			kb.solveAssertions();

			CreatedIndividual b;
			if(a.roleRelations.containsKey(fName))
			{
				ArrayList<Relation> relSet = a.roleRelations.get(fName);
				b = (CreatedIndividual) relSet.get(0).getObjectIndividual();
				Variable q = kb.milp.getVariable(b);
				objExpr = getObjExpression(q);
			}
		}
	}


	@Override
	public Solution solve(KnowledgeBase kb) throws FuzzyOntologyException
	{
		try
		{
			kb.solveABox();
			KnowledgeBase cloned = kb.clone();
			preprocess(cloned);

			if (objExpr != null)
			{
				MILPHelper.PRINT_LABELS = true;
				MILPHelper.PRINT_VARIABLES = true;

				Solution sol = cloned.optimize(objExpr);
				if (sol.getSolution() < 0)
					return new Solution(-sol.getSolution());
				else
					return sol;
			}
			else
			{
				Util.println("\nWarning: Problem in defuzzification. Answer is 0.");
				return null;
			}
		} catch (InconsistentOntologyException ex)
		{
			return new Solution (false);
		}
	} 


	/**
	 * Gets the objective expression.
	 * @param q Variable taking part in the query.
	 * @return Objective expression.
	 */
	public abstract Expression getObjExpression(Variable q);

}
