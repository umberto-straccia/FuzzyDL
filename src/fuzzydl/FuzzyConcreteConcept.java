package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;

/**
 * Fuzzy concrete concept defined with an explicit membership function.
 * @author Fernando Bobillo
 */
public abstract class FuzzyConcreteConcept extends Concept
{

	private static final long serialVersionUID = -2493279357925543693L;

	double k1, k2;

	
	/**
	 * Constructor.
	 * @param name Name of the concept.
	 */
	public FuzzyConcreteConcept(String name)
	{
		super (name, Concept.CONCRETE);
		this.name = name;
	}


	/**
	 * Sets the value of the parameter k1.
	 * @param k1 New value of the parameter.
	 */
	public void setK1(double k1)
	{
		this.k1 = k1;
	}


	/**
	 * Sets the value of the parameter k2.
	 * @param k2 New value of the parameter.
	 */
	public void setK2(double k2)
	{
		this.k2 = k2;
	}

	
	@Override
	public String computeName()
	{
		return name;
	}


	@Override
	public String toString()
	{
		if (type == Concept.CONCRETE)
			return name;
		else // Concept.CONCRETE_COMPLEMENT
			return "(not " + name + " )";

//		return getName();
/*	if (type == Concept.CONCRETE)
			return getName();
		else // Concept.CONCRETE_COMPLEMENT
			return "(not " + getName() + " )";
*/
	}


	/**
	 * Gets the complement of the concept.
	 * @return The complement of the concept.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public abstract Concept complement() throws FuzzyOntologyException;


	/**
	 * Solves an assertion of the form (individual, concept, degree) with respect to a fuzzy KB.
	 * @param ind Subject of the assertion.
	 * @param lowerLimit Lower bound of the assertion.
	 * @param kb Fuzzy KB.
	 */
	public abstract void solveAssertion(CreatedIndividual ind, Degree lowerLimit, KnowledgeBase kb);


	/**
	 * Adds an equation of the form x = d(xFree).
	 * @param xFree Free variable.
	 * @param x [0,1] variable.
	 * @param kb Fuzzy KB.
	 */
	public abstract void addEquation(Variable xFree, Variable x, KnowledgeBase kb);


	/**
	 * Solves an assertion of the form (individual, complement of the concept, degree) with respect to a fuzzy KB.
	 * @param ind Subject of the assertion.
	 * @param lowerLimit Lower bound of the assertion.
	 * @param kb Fuzzy KB.
	 */
	public void solveComplementAssertion(CreatedIndividual ind, Degree lowerLimit, KnowledgeBase kb)
	{
		Assertion ass = new Assertion(ind, this, lowerLimit);
		kb.ruleComplementedComplexAssertion(ass);
	}


	/**
	 * Gets the image in [0,1] of a real number to the explicit membership function.
	 * @param x A real number in the range of values of the explicit membership function.
	 * @return Image in [0,1] of x to the explicit membership function.
	 */
	public abstract double getMembershipDegree(double x);


	/**
	 * Gets the definition of the CFC as a string.
	 * @return Definition of the CFC.
	 */
	public abstract String getName();

	
	/**
	 * Given a name of the form "(not name)", returns "name".
	 */
	protected String getNameWithoutNot()
	{
		return name.substring(5, name.length()-2);
	}

}
