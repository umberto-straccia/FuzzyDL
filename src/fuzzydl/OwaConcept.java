package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;
import java.util.*;

/**
 * OWA concept.
 * @author Fernando Bobillo
 */
public class OwaConcept extends Concept
{
	private static final long serialVersionUID = -4447202427816504586L;
	
	ArrayList<Double> weights;
	ArrayList<Concept> concepts;


	public OwaConcept(ArrayList<Double> weights, ArrayList<Concept> concepts) throws FuzzyOntologyException
	{
		super(OWA);
		this.concepts = concepts;
		if (weights != null)
		{
			if (weights.size() != concepts.size())
				Util.error("Error: The number of weights and the number of concepts should be the same");
			this.weights = weights;
			setName(toString());
		}
		else
			this.weights = new ArrayList<Double> ();
	}


	@Override
	public String toString()
	{
		String name = "(owa (";
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

		if(type == OWA)
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
		Concept aux = new OwaConcept(weights, concepts);
		if(type == OWA)
			aux.setType(NOT_OWA);
		return aux;
	}


	/**
	 * Solves an assertion of the form (individual, concept) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param kb A fuzzy KB.
	 */
	public void solveAssertion(Individual ind, KnowledgeBase kb)
	{
		if (ConfigReader.OPTIMIZATIONS == 0)
		{
			// New n variables x_i
			int n = concepts.size();
			Variable x[] = new Variable[n];
			for(int i=0; i<n; i++)
			{
				Concept ci = concepts.get(i);
				x[i] = kb.milp.getVariable(ind, ci);
				kb.addAssertion(ind, ci, Degree.getDegree(x[i]) );
			}
	
			// y1 > y2 > ... > yn
			Variable y[] = kb.milp.getOrderedPermutation(x);
	
			// \sum_{i} wi * yi = x_{ind:OWA}
			Expression exp = new Expression();
			for(int i=0; i<n; i++)
				exp.addTerm(new Term(weights.get(i), y[i]));
			Degree degree = Degree.getDegree(kb.milp.getVariable(ind, this));
			kb.milp.addNewConstraint(exp, Inequation.EQ, degree);
		}
		else
		{
			int n = concepts.size();
			double wn = weights.get(n-1);
			double w1 = weights.get(0);
			double a = (1.0/n) - (wn - w1)/2;
			Expression exp = new Expression();
			
			// (1/n - (w_n - w_1)/2) \sum^n_{i=1} x_i
			Variable[] x = new Variable[n];
			for(int i=0; i<n; i++)
			{
				Concept ci = concepts.get(i);
				x[i] = kb.milp.getVariable(ind, ci);
				kb.addAssertion(ind, ci, Degree.getDegree(x[i]) );
				exp.addTerm(new Term(a, x[i]));
			}

			// (w_n - w_1) / (n-1) \sum_{i,j} \min\{ x_i, x_j \}
			double b = (wn - w1)/(n-1);
			for(int i=0; i<n-1; i++)
				for(int j=i+1; j<n; j++)
				{
					Variable min = kb.milp.getNewVariable(Variable.UP_VARIABLE);
					ZadehSolver.andEquation(min, x[i], x[j], kb.milp);
					exp.addTerm(new Term(b, min));
				}
			Degree degree = Degree.getDegree(kb.milp.getVariable(ind, this));
			kb.milp.addNewConstraint(exp, Inequation.EQ, degree);
		}
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
		Variable x[] = new Variable[n];
		Term[] terms = new Term[n];
		for(int i=0; i<n; i++)
		{
			Concept ci = concepts.get(i);
			Concept notCi = Concept.complement(ci);
			Variable xi = kb.milp.getVariable(ind, ci);
			Variable xNoti = kb.milp.getVariable(ind, notCi);
			terms[i] = new Term(- weights.get(i), xi);
			x[i] = kb.milp.getVariable(ind, ci);
			kb.addAssertion(ind, notCi, Degree.getDegree(xNoti));
		}

		// y1 > y2 > ... > yn
		Variable y[] = kb.milp.getOrderedPermutation(x);

		// 1 - \sum_{i} wi * yi = xAinNotWS
		Expression exp = new Expression(1, new Term(-1, xAinNotWS) );
		for(int i=0; i<n; i++)
			exp.addTerm(new Term(-weights.get(i), y[i]));
		kb.milp.addNewConstraint(exp, Inequation.EQ);

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
		 
		Concept aux = new OwaConcept(weights, replacedConcepts);
		if(type == OWA)
			aux.setType(NOT_OWA);
		return aux;
	}


}
