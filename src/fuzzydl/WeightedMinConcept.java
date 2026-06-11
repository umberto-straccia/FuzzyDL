package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;

import java.util.*;

/**
 * Weighted sum concept.
 * @author Fernando Bobillo
 */
public class WeightedMinConcept extends Concept
{
	private static final long serialVersionUID = 4093851010000366714L;
	
	ArrayList<Double> weights;
	ArrayList<Concept> concepts;


	public WeightedMinConcept(ArrayList<Double> weights, ArrayList<Concept> concepts) throws FuzzyOntologyException
	{
		super(Concept.W_MIN);

		if (weights.size() != concepts.size())
			Util.error("Error: The number of weights and the number of concepts should be the same");

		boolean one = false;
		for (double d : weights)
			if (d == 1)
				one = true;
		if(one == false)
			Util.error("Error: Some of the weights of the weighted min concept must be 1.0.");

		this.concepts = concepts;
		this.weights = weights;
		setName(toString());
	}


	@Override
	public String toString()
	{
		String s =  "(w-min ";
		int n = concepts.size();
		for(int i=0; i<n; i++)
		{
			s += "(" + concepts.get(i);
			s += " " + weights.get(i) + ") ";
		}
		s += ")";
		if(type == Concept.NOT_W_MIN)
			return "(not " + s + ")";
		else // if(type == Concept.W_MIN)
			return s;
	}


	/**
	 * Gets the complement of the concept.
	 * @return Complement of the concept.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public Concept complement() throws FuzzyOntologyException
	{
		Concept aux = new WeightedMinConcept(weights, concepts);
		if(type == Concept.W_MIN)
			aux.setType(NOT_W_MIN);
		return aux;
	}


	/**
	 * Solves an assertion of the form (individual, concept) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param kb A fuzzy KB.
	 */
	public void solveAssertion(Individual ind, KnowledgeBase kb)
	{
		Variable xAinWS = kb.milp.getVariable(ind, this);
		int n = concepts.size();

		// max_i = \max \{ 1 - w_{i}, x_i \}
		Vector<Variable> max = new Vector<Variable>();
		for(int i=0; i<n; i++)
		{
			Concept ci = concepts.get(i);
			Variable xi = kb.milp.getVariable(ind, ci);
			kb.addAssertion(ind, ci, Degree.getDegree(xi));

			Variable maxVar = kb.milp.getNewVariable(Variable.UP_VARIABLE);
			ZadehSolver.orEquation(maxVar, xi, 1 - weights.get(i), kb.milp);
			max.add(maxVar);
		}

		// min of the max_i = x:
		ZadehSolver.andEquation(max, xAinWS, kb.milp);
	}


	/**
	 * Solves an assertion of the form (individual, not concept) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param kb A fuzzy KB.
	 */
	public void solveComplementedAssertion(Individual ind, KnowledgeBase kb)
	{
		Variable xAinWS = kb.milp.getVariable(ind, this);
		int n = concepts.size();

		// negmax_i = \min \{ w_{i}, 1 - x_i \}
		Vector<Variable> negmax = new Vector<Variable>();
		for(int i=0; i<n; i++)
		{
			Concept ci = concepts.get(i);
			Concept notCi = Concept.complement(ci);
			Variable xi = kb.milp.getVariable(ind, ci);
			Variable xNoti = kb.milp.getVariable(ind, notCi);
			kb.addAssertion(ind, notCi, Degree.getDegree(xNoti));

			Variable maxVar = kb.milp.getNewVariable(Variable.UP_VARIABLE);
			ZadehSolver.andNegatedEquation(maxVar, xi, weights.get(i), kb.milp);
			negmax.add(maxVar);
		}

		// max of the negmax_i = x:
		ZadehSolver.orEquation(negmax, xAinWS, kb.milp);

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

		Concept aux = new WeightedMinConcept(weights, replacedConcepts);
		if(type == Concept.W_MIN)
			aux.setType(NOT_W_MIN);
		return aux;
	}
}
