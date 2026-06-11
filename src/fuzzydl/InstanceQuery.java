package fuzzydl;

import fuzzydl.exception.FuzzyOntologyException;
import fuzzydl.milp.*;
import fuzzydl.util.*;

/**
 * Instance checking query.
 * 
 * @author Fernando Bobillo
 */
public abstract class InstanceQuery extends Query
{

	protected Concept conc;
	protected Individual ind;
	protected Expression objExpr;


	public InstanceQuery(Concept concept, Individual individual) throws FuzzyOntologyException
	{
		if (concept.isConcrete())
			Util.error("Error: " + concept + " cannot be a concrete concept.");
		conc = concept;
		ind = individual;
	}

}
