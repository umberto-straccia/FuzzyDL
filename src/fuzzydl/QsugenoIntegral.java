package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.Util;

import java.util.*;

/**
 * Quasi Sugeno integral concept.
 * @author Fernando Bobillo
 */
public class QsugenoIntegral extends SugenoIntegral
{
	private static final long serialVersionUID = -5127387641918324538L;


	public QsugenoIntegral(ArrayList<Double> weights, ArrayList<Concept> concepts) throws FuzzyOntologyException
	{
		super(QUASI_SUGENO_INTEGRAL);
		initSugenoIntegral(weights, concepts);
	}


	@Override
	public String toString()
	{
		String name = "(qsugeno (";
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

		if(type == QUASI_SUGENO_INTEGRAL)
			return name;
		else
			return "(not " + name + " )";
	}


	/**
	 * Gets the complement of the concept.
	 * @return Complement of the concept.
	 */
	public Concept complement() throws FuzzyOntologyException
	{
		Concept aux = new QsugenoIntegral(weights, concepts);
		if(type == QUASI_SUGENO_INTEGRAL)
			aux.setType(NOT_QUASI_SUGENO_INTEGRAL);
		return aux;
	}


	@Override
	protected void andEquation(Variable x1, Variable x2, Variable x3, KnowledgeBase kb)
	{
		LukasiewiczSolver.andEquation(x1, x2, x3, kb.milp);
	}


	@Override
	public Concept replace(Concept a, Concept c) throws FuzzyOntologyException
	{
		ArrayList<Concept> replacedConcepts = new ArrayList<Concept> ();
		for (Concept ci : concepts)
			replacedConcepts.add(ci.replace(a, c));
		 
		Concept aux = new QsugenoIntegral(weights, replacedConcepts);
		if(type == QUASI_SUGENO_INTEGRAL)
			aux.setType(NOT_QUASI_SUGENO_INTEGRAL);
		return aux;
	}
}
