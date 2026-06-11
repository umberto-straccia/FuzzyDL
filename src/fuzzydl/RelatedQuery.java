package fuzzydl;

import fuzzydl.milp.*;

/**
 * Entailment of a role assertion query.
 * @author Fernando Bobillo
 */
public abstract class RelatedQuery extends Query {

	/**
	 * Abstract role.
	 */
	protected String role;


	/**
	 * Subject of the relation.
	 */
	protected Individual ind1;


	/**
	 * Object of the relation.
	 */
	protected Individual ind2;


	/**
	 * Objective expression.
	 */
	protected Expression objExpr;

}
