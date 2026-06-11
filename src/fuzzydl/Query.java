package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.Solution;
import java.util.*;

/**
 * Query.
 * @author Fernando Bobillo
 */
public abstract class Query
{

	protected double initialTime;
	protected double totalTime;
	
	
	/**
	 * Checks time just before the reasoning.
	 */
	public void setInitialTime()
	{
		initialTime = (new Date()).getTime();
	}


	/**
	 * Checks time just after the reasoning.
	 */
	public void setTotalTime()
	{
		double endTime = (new Date()).getTime();
		totalTime = ((endTime - initialTime) / 1000.);
	}


	/**
	 * Gets the total reasoning time in seconds.
	 * @return Total reasoning time in seconds.
	 */
	public double getTotalTime()
	{
		return totalTime;
	}


	/**
	 * Performs some preprocessing steps of the query over a fuzzy KB.
	 * @param kb A fuzzy KB.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public abstract void preprocess(KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException;


	/**
	 * Solves the query over a fuzzy KB.
	 * @param kb A fuzzy KB.
	 * @return An optimal solution to the query.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public abstract Solution solve(KnowledgeBase kb) throws FuzzyOntologyException;


	/**
	 * Gets the name of the query.
	 * @return Name of the query.
	 */
	@Override
	public abstract String toString();

}
