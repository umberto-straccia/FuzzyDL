package fuzzydl;

import fuzzydl.exception.FuzzyOntologyException;

/**
 * Modified fuzzy concept.
 * @author Fernando Bobillo
 */
public abstract class ModifiedConcept extends Concept
{
	private static final long serialVersionUID = 6641839517162454853L;
	
	/**
	 * Fuzzy modifier.
	 */
	protected Modifier mod;


	public ModifiedConcept(Concept c, Modifier mod)
	{
		super(Concept.MODIFIED);
		c1 = c;
		this.mod = mod;
	}


	/**
	 * Gets the complement of the concept.
	 * @return Complement of the concept.
	 */
	public abstract Concept complement();


	/**
	 * Solves an assertion of the form (individual, concept, lower degree) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param degree Lower bound for the degree.
	 * @param kb A fuzzy KB.
	 */
	public void solveAssertion(Individual ind, Degree degree, KnowledgeBase kb)
	{
		mod.solveAssertion(ind, c1, degree, kb);
	}	


	/**
	 * Solves an assertion of the form (individual, negated concept, lower degree) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param degree Lower bound for the degree.
	 * @param kb A fuzzy KB.
	 */
	public void solveComplementAssertion(Individual ind, Degree degree, KnowledgeBase kb)
	{
		mod.solveComplementAssertion(ind, this, degree, kb);
	}


	@Override
	public String toString()
	{
		String name = "(" + mod + " " + c1 + ")"; 
		if (type == MODIFIED)
			return name;
		else // getType(= == MODIFIED_COMPLEMENT)
			return "(not (" + name + "))";
	}


	@Override
	public abstract Concept replace(Concept a, Concept c) throws FuzzyOntologyException;

}