package fuzzydl;

import fuzzydl.exception.FuzzyOntologyException;
import fuzzydl.milp.*;
import fuzzydl.util.Util;

/**
 * Fuzzy concept satisfiability query.
 * @author Fernando Bobillo
 */
public abstract class SatisfiableQuery extends Query {

	/**
	 * Fuzzy concept.
	 */
	protected Concept conc;


	/**
	 * Objective expression.
	 */
	protected Expression objExpr;


	/**
	 * Optional individual used during the satisfiability test.
	 */
	protected Individual ind;


	/**
	 * Constructor for a general satisfiability query.
	 * 
	 * @param c A fuzzy concept for which the satisfiability is to be tested.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public SatisfiableQuery(Concept c) throws FuzzyOntologyException
	{
		this(c, null);
	}


	/**
	 * Constructor for a satisfiability query involving a specific individual.
	 * 
	 * @param c A fuzzy concept for which the satisfiability is to be tested.
	 * @param a An individual used in the satisfiability test.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public SatisfiableQuery(Concept c, Individual a) throws FuzzyOntologyException
	{
		if (c.isConcrete())
			Util.error("Error: " + c + " cannot be a concrete concept.");
		conc = c;
		ind = a;
	}
}
