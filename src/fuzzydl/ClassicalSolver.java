package fuzzydl;

import fuzzydl.exception.*;

/**
 * Solver for classical logic semantics.
 * @author Fernando Bobillo
 */
public class ClassicalSolver 
{

	
	/**
	 * Solves a conjunction fuzzy assertion with respect to a reference fuzzy KB.
	 * @param ass A conjunction fuzzy assertion.
	 * @param kb A reference fuzzy KB.
	 */
	public static void solveAnd(Assertion ass, KnowledgeBase kb)
	{
		ZadehSolver.solveAnd(ass, kb);
	}


	/**
	 * Solves a disjunction fuzzy assertion with respect to a reference fuzzy KB.
	 * @param ass A disjunction fuzzy assertion.
	 * @param kb A reference fuzzy KB.
	 */
	public static void solveOr(Assertion ass, KnowledgeBase kb)
	{
		LukasiewiczSolver.solveOr(ass, kb);
	}


	/**
	 * Solves a existential restriction fuzzy assertion with respect to a reference fuzzy KB.
	 * @param ass A existential restriction fuzzy assertion.
	 * @param kb A reference fuzzy KB.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public static void solveSome(Assertion ass, KnowledgeBase kb) throws InconsistentOntologyException
	{
		ZadehSolver.solveSome(ass, kb);
	}


	/**
	 * Solves a universal restriction fuzzy assertion with respect to a reference fuzzy KB.
	 * @param rel A relation.
	 * @param restric A universal restriction.
	 * @param kb A reference fuzzy KB.
	 */
	public static void solveAll(Relation rel, Restriction restric, KnowledgeBase kb)
	{
		ZadehSolver.solveAll(rel, restric, kb);
	}

}
