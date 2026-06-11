package fuzzydl;

import fuzzydl.milp.*;

import java.io.*;
import java.util.*;


/**
 * Sigma-count pending tasks.
 * @author Fernando Bobillo
 */
public class SigmaCount implements Serializable
{
	private static final long serialVersionUID = 609993414470563625L;

	// Concept
	private Concept concept;

	// Role
	private String role;

	// Variable
	private Variable var;
	
	// Subject individual
	private Individual ind;

	// Object individuals
	private Collection<Individual> inds;


	// Constructor
	public SigmaCount(Variable var, Individual ind, Collection<Individual> inds, String role, Concept concept)
	{
		this.concept = concept;
		this.ind = ind;
		this.inds = inds;
		this.role = role;
		this.var = var;
	}

	
	public Variable getVariable()
	{
		return var;
	}


	public Individual getIndividual()
	{
		return ind;
	}


	public Collection<Individual> getIndividuals()
	{
		return inds;
	}


	public String getRole()
	{
		return role;
	}


	public Concept getConcept()
	{
		return concept;
	}

}
