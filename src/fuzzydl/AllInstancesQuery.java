package fuzzydl;


import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;
import java.util.*;

/** 
 * Min instance query for every individual of a knowledge base.
 * @author Fernando Bobillo
 */ 
public class AllInstancesQuery extends Query
{

	protected Concept conc;
	private String name;
	private List<Individual> individuals;
	private List<Double> degrees;


	public AllInstancesQuery(Concept concept) throws FuzzyOntologyException
	{
		if (concept.isConcrete())
			Util.error("Error: " + concept + " cannot be a concrete concept.");
		conc = concept;
		degrees = new ArrayList<Double> ();
		individuals = new ArrayList<Individual> ();
		name = "Instances of " + conc + "?";
	}


	@Override
	public void preprocess(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		
	}


	@Override
	public Solution solve(KnowledgeBase kb) throws FuzzyOntologyException
	{
		name = "";
		Solution sol = null;
		individuals = new ArrayList<Individual> (kb.individuals.values());
		
		try {
			kb.solveABox();
		} catch (InconsistentOntologyException e) {
			return new Solution(false);
		}

		for (Individual i : individuals)
			if (! (i instanceof CreatedIndividual))
			{
				Query q = new MinInstanceQuery(conc, i);
				sol = q.solve(kb);
/*				Calendar calendar = Calendar.getInstance();
				System.out.println((++count) + "\t" + i + "\t" + sol.toString() + "\t" + 
						calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
*/				if (sol.isConsistentKB())
				{
					degrees.add(sol.getSolution());
					name += q.toString() + sol.getSolution() + "\n";
				}
				else
				{
					name = "Instances of " + conc + "?  Inconsistent KB";
					break;
				}
			}
		return sol;
	}


	/**
	 * Specific algorithm to solve the instance retrieval.
	 * @param kb A fuzzy KB.
	 * @return An optimal solution to the query.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public Solution solveNew(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		name = "";
		List<Variable> newVariables = new ArrayList<Variable> ();
		Map<String,String> varNames = new HashMap<String,String> ();
		individuals = new ArrayList<Individual> (kb.individuals.values());
		KnowledgeBase cloned = kb.clone();

		// Lines 91-95 not evaluated
		try {
			cloned.solveABox();
		} catch (InconsistentOntologyException e) {
			return new Solution(false);
		}

		for (Individual i : individuals)
			if (! (i instanceof CreatedIndividual))
			{
				Variable q = cloned.milp.getNewVariable(Variable.UP_VARIABLE);
				cloned.old01Variables += 1;
				String s = "Is " + i + " instance of " + conc + " ? >= ";
				varNames.put(q.toString(), s);
				cloned.milp.showVars.addVariable(q, s);
				newVariables.add(q);
				// a: not c >= 1-q
				cloned.addAssertion(i, Concept.complement(conc), Degree.getDegree(new Expression(1, new Term(-1, q))));
			}
		
		cloned.solveAssertions();
		Expression objExpr = new Expression();
		for (Variable var : newVariables)
			objExpr.addTerm(new Term(1, var));
		MILPHelper.PRINT_LABELS = false;
		MILPHelper.PRINT_VARIABLES = false;
    	MILPHelper.PARTITION = true;
		Solution sol = cloned.optimize(objExpr);
		MILPHelper.PARTITION = false;
    	MILPHelper.PRINT_LABELS = true;
		MILPHelper.PRINT_VARIABLES = true;

		if (sol.isConsistentKB())
		{
			Hashtable<String, Double> ht = sol.getShowedVariables();
			Hashtable<String, Double> individualsAndDegrees = new Hashtable<String,Double> ();
			for (String s : ht.keySet() )
			{
				String varName = varNames.get(s);
				double value = ht.get(s);
				name += varName + value + "\n";
				individualsAndDegrees.put(varName, value);
			}
			
			for(int i=0; i<individuals.size(); i++)
			{
				String varName = "Is " + individuals.get(i).toString()+ " instance of " + conc + " ? >= ";
				double value = individualsAndDegrees.get(varName);
				degrees.add(value);
			}
		}
		else
			name = "Instances of " + conc + "?  Inconsistent KB";
		return sol;
	}


	@Override
	public String toString()
	{
		return name;
	}


	/**
	 * Gets the named individuals of the knowledge base.
	 * 
	 * @return Lists of the individuals in the knowledge base.
	 */
	public List<Individual> getIndividuals()
	{
		return individuals;
	}


	/**
	 * Gets the degree of membership to the query concept for every individual in the knowledge base.
	 * 
	 * @return Lists of degrees of membership to the query concept for every individual in the knowledge base.
	 */
	public List<Double> getDegrees()
	{
		return degrees;
	}


}
