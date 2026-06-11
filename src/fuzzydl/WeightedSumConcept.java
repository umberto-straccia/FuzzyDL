package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;

import java.util.*;

/**
 * Weighted sum concept.
 * @author Fernando Bobillo
 */
public class WeightedSumConcept extends Concept
{
	private static final long serialVersionUID = 562181287666112171L;

	ArrayList<Double> weights;
	ArrayList<Concept> concepts;


	public WeightedSumConcept(ArrayList<Double> weights, ArrayList<Concept> concepts) throws FuzzyOntologyException
	{
		super(Concept.W_SUM);

		if (weights.size() != concepts.size())
			Util.error("Error: The number of weights and the number of concepts should be the same");

		double sum = 0;
		for (double d : weights)
			sum += d;
		if(sum > 1)
			Util.error("Error: The sum of the weights of the weighted sum concept cannot be greater than 1.0.");

		this.concepts = concepts;
		this.weights = weights;
		setName(toString());
	}


	@Override
	public String toString()
	{
		String s =  "(w-sum ";
		int n = concepts.size();
		for(int i=0; i<n; i++)
		{
			s += "(" + concepts.get(i);
			s += " " + weights.get(i) + ") ";
		}
		s += ")";
		if(type == Concept.NOT_W_SUM)
			return "(not " + s + ")";
		else // if(type == Concept.W_SUM)
			return s;
	}


	/**
	 * Gets the complement of the concept.
	 * @return Complement of the concept.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public Concept complement() throws FuzzyOntologyException
	{
		Concept aux = new WeightedSumConcept(weights, concepts);
		if(type == Concept.W_SUM)
			aux.setType(NOT_W_SUM);
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
		Term[] terms = new Term[n];
		for(int i=0; i<n; i++)
		{
			Concept ci = concepts.get(i);
			Variable xi = kb.milp.getVariable(ind, ci);
			terms[i] = new Term(weights.get(i), xi);
			kb.addAssertion(ind, ci, Degree.getDegree(xi));
		}
		kb.milp.addNewConstraint(new Expression(terms), Inequation.EQ, Degree.getDegree(xAinWS) );
	}


	/**
	 * Solves an assertion of the form (individual, not concept) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param kb A fuzzy KB.
	 */
	public void solveComplementedAssertion(Individual ind, KnowledgeBase kb)
	{
		Variable xAinNotWS = kb.milp.getVariable(ind, this);
		int n = concepts.size();
		Term[] terms = new Term[n];
		for(int i=0; i<n; i++)
		{
			Concept ci = concepts.get(i);
			Concept notCi = Concept.complement(ci);
			Variable xi = kb.milp.getVariable(ind, ci);
			Variable xNoti = kb.milp.getVariable(ind, notCi);
			terms[i] = new Term(- weights.get(i), xi);
			kb.addAssertion(ind, notCi, Degree.getDegree(xNoti));
		}
		kb.milp.addNewConstraint(new Expression(1, terms), Inequation.EQ, Degree.getDegree(xAinNotWS) );

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
		 
		Concept aux = new WeightedSumConcept(weights, replacedConcepts);
		if(type == Concept.W_SUM)
			aux.setType(NOT_W_SUM);
		return aux;
	}
}


