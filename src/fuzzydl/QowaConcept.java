package fuzzydl;

import fuzzydl.exception.*;
import java.util.*;

/**
 * Quantified-guided OWA concept.
 * @author Fernando Bobillo
 */
public class QowaConcept extends OwaConcept
{
	private static final long serialVersionUID = -5400581138113598997L;
	
	FuzzyConcreteConcept quantifier;


	public QowaConcept(FuzzyConcreteConcept quantifier, ArrayList<Concept> concepts) throws FuzzyOntologyException
	{
		super(null, concepts);
		this.quantifier = quantifier;
		computeWeights(concepts.size());
		this.concepts = concepts;
		setName(toString());
	}


	@Override
	public String toString()
	{
		String name = "(q-owa " + quantifier.getName();
		for(int i=0; i<concepts.size(); i++)
			name += " " + concepts.get(i).toString();
		name += ")";

		if(type == QUANTIFIED_OWA)
			return name;
		else
			return "(not " + name + " )";
	}


	/**
	 * Gets the complement of the concept.
	 * @return Complement of the concept.
	 */
	@Override
	public Concept complement() throws FuzzyOntologyException
	{
		Concept aux = new OwaConcept(weights, concepts);
		if(type == QUANTIFIED_OWA)
			aux.setType(NOT_QUANTIFIED_OWA);
		return aux;
	}


	private void computeWeights(int n)
	{
		double previous = 0;
		if (n > 0)
		{
			for (int i=1; i<=n; i++)
			{
				double w = ((double) i) / n;
				weights.add(i-1, quantifier.getMembershipDegree(w - previous));
				previous = w;
			}
		}
	}

	@Override
	public Concept replace(Concept a, Concept c) throws FuzzyOntologyException
	{
		ArrayList<Concept> replacedConcepts = new ArrayList<Concept> ();
		for (Concept ci : concepts)
			replacedConcepts.add(ci.replace(a, c));
		 
		Concept aux = new OwaConcept(weights, replacedConcepts);
		if(type == QUANTIFIED_OWA)
			aux.setType(NOT_QUANTIFIED_OWA);
		return aux;
	}

}
