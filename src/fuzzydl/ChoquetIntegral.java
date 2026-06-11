package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;

import java.io.*;
import java.util.*;

/**
 * Choquet integral concept.
 * @author Fernando Bobillo
 */
public class ChoquetIntegral extends Concept implements Serializable
{

	private static final long serialVersionUID = -3629039059519229845L;

	protected ArrayList<Double> weights;
	protected ArrayList<Concept> concepts;


	public ChoquetIntegral(ArrayList<Double> weights, ArrayList<Concept> concepts) throws FuzzyOntologyException
	{
		super(CHOQUET_INTEGRAL);
		this.concepts = concepts;
		if (weights != null)
		{
			this.weights = weights;
			int n = weights.size();
			if (concepts.size() != n)
				Util.error("Error: The number of weights and the number of concepts should be the same");
			for (int i = 1; i < n; i++) 
		    {
		        Double prev = this.weights.get(i - 1);
		        Double curr = this.weights.get(i);
		        if (prev > curr) {
		        	Util.println("Choquet integral requires ordererd weights: ordering");
		        	Collections.sort(weights);
		        	break;
		        }
		    }
			double max = this.weights.get(n - 1);
			if (max != 1.0)
			{
				Util.println("In Choquet integral, the greatest weight should be 1: normalizing weights");
				for(int i = 0; i < n; i++)
					this.weights.set(i, this.weights.get(i) / max);			
			}
			setName(toString());
		}
		else
			this.weights = new ArrayList<Double> ();
	}


	@Override
	public String toString()
	{
		String name = "(choquet (";
		if (weights != null)
		{
			name += weights.get(0);
			for(int i=1; i<weights.size(); i++)
				name += " " + weights.get(i).toString();
		}
		name += ") (";
		name += concepts.get(0);
		for(int i=1; i<concepts.size(); i++)
			name += " " + concepts.get(i).toString();
		name += ") )";

		if(type == CHOQUET_INTEGRAL)
			return name;
		else
			return "(not " + name + " )";
	}


	/**
	 * Gets the complement of the concept.
	 * @return Complement of the concept.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public Concept complement() throws FuzzyOntologyException
	{
		Concept aux = new ChoquetIntegral(weights, concepts);
		if(type == CHOQUET_INTEGRAL)
			aux.setType(NOT_CHOQUET_INTEGRAL);
		return aux;
	}


	/**
	 * Solves an assertion of the form (individual, concept) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param kb A fuzzy KB.
	 */
	public void solveAssertion(Individual ind, KnowledgeBase kb)
	{
		// New n variables
		int n = concepts.size();
		Variable x[] = new Variable[n];
		for(int i=0; i<n; i++)
		{
			Concept ci = concepts.get(i);
			x[i] = kb.milp.getVariable(ind, ci);
			kb.addAssertion(ind, ci, Degree.getDegree(x[i]) );
		}
		
		// y1 > y2 > ... > yn
		Variable z[][] = new Variable[n][n];
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
				z[i][j] = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);		
		Variable y[] = kb.milp.getOrderedPermutation(x, z);
		
		// y1 w1 + \sum^{n}_{i=2} yi (wi - wi-1) = x_{ind:CI}
		Expression exp = new Expression();
		exp.addTerm(new Term(weights.get(0), y[0]));
		for(int i=1; i<n; i++)
			exp.addTerm(new Term(weights.get(i) - weights.get(i-1), y[i]));
		Degree degree = Degree.getDegree(kb.milp.getVariable(ind, this));
		kb.milp.addNewConstraint(exp, Inequation.EQ, degree);
	}


	/**
	 * Solves an assertion of the form (individual, not concept) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param kb A fuzzy KB.
	 */
	public void solveComplementedAssertion(Individual ind, KnowledgeBase kb)
	{
		// New n variables
		int n = concepts.size();
		Variable x[] = new Variable[n];
		for(int i=0; i<n; i++)
		{
			Concept ci = concepts.get(i);
			Concept notCi = Concept.complement(ci);
			x[i] = kb.milp.getVariable(ind, notCi);
			kb.addAssertion(ind, notCi, Degree.getDegree(x[i]) );
		}
		
		// y1 > y2 > ... > yn
		Variable z[][] = new Variable[n][n];
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
				z[i][j] = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);		
		Variable y[] = kb.milp.getOrderedPermutation(x, z);

		// 1 - y1 w1 - \sum^{n}_{i=2} yi (wi - wi-1) = x_{ind:not CI}
		Expression exp = new Expression(1);
		exp.addTerm(new Term(- weights.get(0), y[0]));
		for(int i=1; i<n; i++)
			exp.addTerm(new Term(weights.get(i-1) - weights.get(i), y[i]));
		Degree degree = Degree.getDegree(kb.milp.getVariable(ind, this));
		kb.milp.addNewConstraint(exp, Inequation.EQ, degree);

		kb.ruleComplemented(ind, this);
	}


	@Override
	public HashSet<Concept> computeAtomicConcepts()
	{
		HashSet<Concept> conceptList = new HashSet<Concept>();
		for (Concept c : concepts)
			conceptList.addAll(c.computeAtomicConcepts());
		return conceptList;
	}


	@Override
	public HashSet<String> getRoles()
	{
		HashSet<String> roleList = new HashSet<String>();
		for (Concept c : concepts)
			roleList.addAll(c.getRoles());
		return roleList;
	}


	@Override
	public Concept replace(Concept a, Concept c) throws FuzzyOntologyException
	{
		ArrayList<Concept> replacedConcepts = new ArrayList<Concept> ();
		for (Concept ci : concepts)
			replacedConcepts.add(ci.replace(a, c));
		 
		Concept aux = new ChoquetIntegral(weights, replacedConcepts);
		if(type == CHOQUET_INTEGRAL)
			aux.setType(NOT_CHOQUET_INTEGRAL);
		return aux;
	}

}
