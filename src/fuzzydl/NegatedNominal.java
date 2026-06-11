package fuzzydl;

import fuzzydl.exception.*;


/**
 * Negated nominal concept. Only used in range restrictions for the moment.
 * @author Fernando Bobillo
 */
public class NegatedNominal extends Concept
{
	private static final long serialVersionUID = 1310038880804083625L;
	
	String indName;


	public NegatedNominal(String indName)
	{
		super(ATOMIC);
		this.indName = indName;
		name = "(not { " + indName + " } )";
	}


	/**
	 * Gets the complement of the concept.
	 * @return Complement of the concept.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public Concept complement() throws FuzzyOntologyException
	{
		throw new FuzzyOntologyException("Negated nominals cannot be complemented");
	}

}
