package fuzzydl;

import fuzzydl.exception.FuzzyOntologyException;
import fuzzydl.milp.*;
import fuzzydl.util.*;


/**
 * Subsumption query.
 * @author Fernando Bobillo
 */
public abstract class SubsumptionQuery extends Query {

	/**
	 * Subsumed concept
	 */
	protected Concept c1;

	/**
	 * Subsumer concept
	 */
	protected Concept c2;

	/**
	 * Fuzzy implication used.
	 */
	protected int type;

	/**
	 * Objective epxression
	 */protected Expression objExpr;

	/**
	 * Lukasiewicz implication
	 */
	public final static int LUKASIEWICZ = 0;

	/**
	 * Goedel implication
	 */
	public final static int GOEDEL = 1;

	/**
	 * Zadeh implication
	 */
	public final static int ZADEH = 2;

	/**
	 * Kleene-Dienes implication
	 */
	public final static int KLEENE_DIENES = 3;


	public SubsumptionQuery(Concept c1, Concept c2, int type) throws FuzzyOntologyException
	{
		if (c1.isConcrete())
			Util.error("Error: " + c1 + " cannot be a concrete concept.");
		if (c2.isConcrete())
			Util.error("Error: " + c1 + " cannot be a concrete concept.");
		this.c1 = c1;
		this.c2 = c2;
		this.type = type;
	}
}
