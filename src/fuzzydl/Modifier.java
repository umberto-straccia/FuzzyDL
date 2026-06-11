package fuzzydl;

import java.io.Serializable;

/**
 * Fuzzy modifier.
 * @author Fernando Bobillo
 */
public abstract class Modifier implements Serializable
{
	private static final long serialVersionUID = 7174732161598113320L;
	
	/**
	 * Name of the modifier
	 */
	protected String name;


	public Modifier(String name)
	{
		this.name = name;
	}


	/**
	 * Gets the name of the modifier.
	 * @param name Name of the modifier.
	 */
	public void setName(String name)
	{
		this.name = name;
	}


	/**
	 * Modifies a fuzzy concept
	 * @param c A fuzzy concept
	 * @return Fuzzy concept resulting from the application of the modifier to c.
	 */
	public abstract Concept modify(Concept c);


	/**
	 * Gets the image in [0,1] of a real number to the modifier.
	 * @param x A real number in the range of values of the modifierfunction.
	 * @return Image in [0,1] of x to the explicit modifier function.
	 */
	public abstract double getMembershipDegree(double x);


	/**
	 * Solves an assertion of the form (individual, concept, lower degree) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param con A concept.
	 * @param degree Lower bound for the degree.
	 * @param kb A fuzzy KB.
	 */
	public abstract void solveAssertion(Individual ind, Concept con, Degree degree, KnowledgeBase kb);


	/**
	 * Solves an assertion of the form (individual, negated concept, lower degree) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param con A concept.
	 * @param degree Lower bound for the degree.
	 * @param kb A fuzzy KB.
	 */
	public void solveComplementAssertion(Individual ind, Concept con, Degree degree, KnowledgeBase kb)
	{
		Assertion ass = new Assertion(ind, con, degree);
		kb.ruleComplementedComplexAssertion(ass);
	}


	/**
	 * Gets the name of the modifier.
	 * @return Name of the modifier.
	 */
	@Override
	public String toString()
	{
		return name;
	}


	/**
	 * Gets the definition of the modifier.
	 * @return Definition of the modifier.
	 */
	public abstract String getName();

}
