package fuzzydl;

import fuzzydl.exception.*;
import java.util.*;

/**
 * Sigma-count concept.
 * @author Fernando Bobillo
 */
public class SigmaConcept extends Concept
{
	private static final long serialVersionUID = -158123666621761722L;
	
	FuzzyConcreteConcept d;
	Individual ind;
	Collection<Individual> inds;


	public SigmaConcept(Concept c, String r, Collection<Individual> inds, FuzzyConcreteConcept d) throws FuzzyOntologyException
	{
		super (Concept.SIGMA_CONCEPT);
		this.c1 = c;
		this.d = d;
		this.inds = inds;
		this.role = r;
	}


	public Collection<Individual> getIndividuals() 
	{
		return inds;
	}


	public FuzzyConcreteConcept getFuzzyConcept() 
	{
		return d;
	}


	@Override
	public String toString()
	{
		String name = "(sigma-count " + role + " " + c1.toString() + " { ";
		for(Individual ind : inds)
			name += ind + " ";
		name += " } " + d.toString() + ")";

		if(type == SIGMA_CONCEPT)
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
		Concept aux = new SigmaConcept(c1, role, inds, d);
		if(type == SIGMA_CONCEPT)
			aux.setType(NOT_SIGMA_CONCEPT);
		return aux;
	}

}
