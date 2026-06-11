package fuzzydl;

import java.io.*;
import java.util.*;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;

/**
 * Individual.
 * @author Fernando Bobillo
 */
public class Individual implements Serializable
{
	private static final long serialVersionUID = -4778924027329161352L;


	/**
	 * Default prefix for new individual names.
	 */
	public static final String DEFAULT_NAME = "i";


	/**
	 * List of concepts such that a concept assertion has been processed.
	 */
	HashSet<Concept> listOfConcepts;


	/**
	 * Concrete role restrictions.
	 */
	Hashtable<String, ArrayList<Assertion>> concreteRoleRestrictions;


	/**
	 * Fillers to show
	 */
	Hashtable<String, HashSet<String>> fillersToShow;


	/**
	 * Name of the individual
	 */
	protected String name;


	/**
	 * Indicates if the individual is indirectly blocked or not.
	 */
	protected Set<String> nominalList;


	/**
	 * List of roles for which to apply the not self rule 
	 */
	protected HashSet<String> notSelfRoles;
	

	/**
	 * Array of representative individuals
	 */
	protected ArrayList<RepresentativeIndividual> representatives;


	/**
	 * Role relations.
	 */
	Hashtable<String, ArrayList<Relation>> roleRelations;


	/**
	 * Role restrictions.
	 */
	Hashtable<String, ArrayList<Restriction>> roleRestrictions;


	public Individual(String name)
	{
		concreteRoleRestrictions = new Hashtable<String, ArrayList<Assertion>>();
		fillersToShow = new Hashtable<String, HashSet<String>>();
 		listOfConcepts = new HashSet<Concept> ();
		this.name = name;
		nominalList = new HashSet<String>(); 
		notSelfRoles = new HashSet<String> ();
		representatives = new ArrayList<RepresentativeIndividual>();
		roleRestrictions = new Hashtable<String, ArrayList<Restriction>>();
		roleRelations = new Hashtable<String,ArrayList<Relation>>();		
	}


	/**
	 * Gets a copy of an individual.
	 * @return A copy of the individual.
	 */
	@Override
	public Individual clone()
	{
		Individual ind = new Individual(name);
		cloneAttributes(ind);
		return ind;
	}


	void cloneAttributes(Individual ind)
	{	
		ind.concreteRoleRestrictions = new Hashtable<String, ArrayList<Assertion>>();
		for (String s : concreteRoleRestrictions.keySet())
		{
			ArrayList<Assertion> array = new ArrayList<Assertion>(concreteRoleRestrictions.get(s));
			ind.concreteRoleRestrictions.put(s, array);
		}

 		ind.fillersToShow = new Hashtable<String, HashSet<String>>(fillersToShow);
 		ind.listOfConcepts = new HashSet<Concept> (listOfConcepts);
		ind.nominalList = new HashSet<String>(nominalList);
 		ind.notSelfRoles = new HashSet<String> (notSelfRoles);		
		ind.representatives = new ArrayList<RepresentativeIndividual>(representatives);

		ind.roleRelations = new Hashtable<String, ArrayList<Relation>>();
		for (String s : roleRelations.keySet())
		{
			ArrayList<Relation> array = new ArrayList<Relation>(roleRelations.get(s));
			ind.roleRelations.put(s, array);
		}

		ind.roleRestrictions = new Hashtable<String, ArrayList<Restriction>>();
		for (String s : roleRestrictions.keySet())
		{
			ArrayList<Restriction> array = new ArrayList<Restriction>(roleRestrictions.get(s));
			ind.roleRestrictions.put(s, array);
		}
	}


	/**
	 * Gets the name of the individual.
	 * @return Name of the individual.
	 */
	@Override
	public String toString()
	{
		return name;
	}


	/**
	 * Sets the name of the individual.
	 * @param name Name of the individual.
	 */
	void setName(String name)
	{
		this.name = name;
	}


	/**
	 * Adds b relation to the individual.
	 * @param roleName Role of the relation.
	 * @param b Object of the relation.
	 * @param degree Lower bound for the degree.
	 * @param kb Reference fuzzy KB.
	 * @return The created relation.
	 */
	Relation addRelation(String roleName, Individual b, Degree degree, KnowledgeBase kb) throws InconsistentOntologyException
	{		
		ArrayList<Relation> rels = roleRelations.get(roleName);
		if (rels == null)
		   rels = new ArrayList<Relation>();

		// We check if the relation already exists when both degrees are double
		boolean addNewRel = true;
		Relation rel = new Relation(roleName, this, b, degree);

		if (degree.isNumeric())
		{
			double newDegree = ((DegreeNumeric) degree).getNumericalValue();

			// Check relation does not exist
			for (int i=0; i<rels.size(); i++)
			{
				Relation oldRel = (Relation) rels.get(i);
				String oldRole = oldRel.getRoleName();
				Individual oldInd = oldRel.getObjectIndividual();

				// If there exists b similar relation, stop the loop. Do not add b new relation
				if (b.toString().equals(oldInd.toString()) && oldRole.equals(roleName) && oldRel.getDegree().isNumeric())
				{
					addNewRel = false;
					double oldDegree = ((DegreeNumeric) oldRel.getDegree()).getNumericalValue();

					// If the existing relation has a smaller degree, replace it
					if (newDegree > oldDegree)
					{
						addNewRel = false;
						rels.set(i, rel);
						roleRelations.put(roleName, rels);
					}
					Util.println("Relation " + name + ", " + b.toString() + " through role " + roleName + " has already been processed hence ignored");
					break;
				}
			}
		}

		// If not, add new relation to the list
		if (addNewRel == true)
		{
			Util.println("Adding (" + this + ", " + b + ") : " + roleName);
			kb.numRelations++;

			rels.add(rel);
			roleRelations.put(roleName, rels);

			// Add MILP restriction
			Variable assVar = kb.milp.getVariable(rel);

			// If the degree is not x_{(a,b):R}
			if (degree.toString().compareTo(assVar.toString()) != 0)
				kb.milp.addNewConstraint(new Expression(new Term(1, assVar)), Inequation.GE, degree);

			// x_{(a,b):R} = x_{a : \exists R.{b} }
/*			Concept hasValue = Concept.hasValue(roleName, b.toString());
			Variable hasValueVar = kb.milp.getVariable(this, hasValue);
			kb.milp.addNewConstraint(new Expression(new Term(1, assVar), new Term(-1, hasValueVar)), Inequation.EQ);
*/

			// x_{b : {b} } >= x_{(a,b):R}
			Variable bIsB = kb.milp.getNominalVariable(b.toString());
			kb.milp.addNewConstraint(new Expression(new Term(1, bIsB), new Term(-1, assVar)), Inequation.GE);

			// Show abstract fillers
			if (kb.milp.showVars.showAbstractRoleFillers(roleName, this.toString()))
				kb.milp.showVars.addIndividualToShow(b.toString());

			if (kb.isLoaded() )
			{
				// Apply domain restrictions
				for (String r : kb.domainRestrictions.keySet() )
					kb.ruleDomainLazyUnfolding(r, rel);

				// Apply range restrictions
				for (String r : kb.rangeRestrictions.keySet() )
					kb.ruleRangeLazyUnfolding(r, rel);
				
				// Add inverse restriction
				if(kb.invRoles.containsKey(roleName))
				{
					Variable var1 = kb.milp.getVariable(this, b, roleName);
					for (String invRole : kb.invRoles.get(roleName))
					{
						Variable var2 = kb.milp.getVariable(b, this, invRole);
						kb.milp.addNewConstraint(new Expression(new Term(1, var1), new Term(-1, var2)), Inequation.EQ);					   	
					}
				}
			}

			// Apply restrictions with same role name as the created relation
			ArrayList<Restriction> restrics = roleRestrictions.get(roleName);
			if(restrics != null)
				for(Restriction r : restrics)
					solveRelationRestriction(rel, r, kb);

			// Apply not-self rule
			if (b.equals(this))
				if (notSelfRoles.contains(roleName))
					solveNotSelfRule(roleName, kb);
		}

		return rel;
	}


	/**
	 * Apply not self rule.
	 * @param roleName Role name.
	 * @param kb Reference fuzzy KB.
	 */
	protected void solveNotSelfRule(String roleName, KnowledgeBase kb)
	{
		// (v,v):R
		Variable var1 =  kb.milp.getVariable(this, this, roleName);
	
		// v:\neg \some R.Self
		Concept c = Concept.complement(Concept.self(roleName));
		Variable var2 =  kb.milp.getVariable(this, c);

		kb.milp.addNewConstraint(new Expression(1, new Term(-1, var1), new Term(-1, var2)), Inequation.EQ);
	}	


	/**
	 * Adds a universal restriction to the individual.
	 * @param roleName Role of the restriction.
	 * @param c Concept of the restriction.
	 * @param degree Lower bound for the degree.
	 * @param kb Reference fuzzy KB.
	 */
	void addRestriction(String roleName, Concept c, Degree degree, KnowledgeBase kb) throws InconsistentOntologyException
	{
		Restriction restric = new Restriction(roleName, c, degree);
		commonPartAddRestriction(roleName, restric, kb);
	}


	/**
	 * Adds a negated datatype restriction to the individual.
	 * @param fName Feature of the restriction.
	 * @param ass Assertion with the negated datatype restriction.
	 */
	void addConcreteRestriction(String fName, Assertion ass) throws InconsistentOntologyException
	{
		ArrayList<Assertion> restrics = concreteRoleRestrictions.get(fName);
		if (restrics == null)
		   restrics = new ArrayList<Assertion>();
		restrics.add(ass);
		concreteRoleRestrictions.put(fName, restrics);
	}


	/**
	 * Adds a hasValue restriction to the individual.
	 * @param roleName Role of the restriction.
	 * @param indName Individual of the hasValue restriction.
	 * @param degree Lower bound for the degree.
	 * @param kb Reference fuzzy KB.
	 */
	void addRestriction(String roleName, String indName, Degree degree, KnowledgeBase kb) throws InconsistentOntologyException
	{
		Restriction restric = new HasValueRestriction(roleName, indName, degree);
		commonPartAddRestriction(roleName, restric, kb);
/*
		// Make sure that { indName } is the representative of the role
		if (kb.funcRoles.contains(roleName) && (roleRelations.get(roleName) == null) )
		{
			ArrayList<Relation> rels = new ArrayList<Relation> ();
			Relation relation = new Relation(roleName, this, kb.getIndividual(indName), degree);
			rels.add(relation);
			roleRelations.put(roleName, rels);
		}
*/
	}


	private void commonPartAddRestriction(String roleName, Restriction restric, KnowledgeBase kb) throws InconsistentOntologyException
	{
		ArrayList<Restriction> restrics = roleRestrictions.get(roleName);
		if (restrics == null)
		   restrics = new ArrayList<Restriction>();
		restrics.add(restric);
		roleRestrictions.put(roleName, restrics);
		
		// Apply new restriction to all the existing relations via roleName
		ArrayList<Relation> rels = roleRelations.get(roleName);
		if(rels != null)
		{
			for(Relation r : rels)
			{
				Util.println("Adding universal restriction " + restric + " to relation " + r);
				solveRelationRestriction(r, restric, kb);
			}
		}
	}


	void addNotSelfRestriction(String role,  KnowledgeBase kb)
	{
		if (notSelfRoles.contains(role) == false)
		{
			// Add new self restriction to the list
			notSelfRoles.add(role);

			// Apply new restriction to all the existing relations via roleName
			ArrayList<Relation> rels =  roleRelations.get(role);
			if (rels != null)
				for (Relation r :rels)
					if (r.getObjectIndividual().equals(this))
					{
						solveNotSelfRule(role, kb);
						return;
					}
		}
	}

	
	/**
	 * Apply b universal restriction to b relation of the individual.
	 * @param rel A relation.
	 * @param restric A restriction.
	 * @param kb Reference fuzzy KB.
	 */
	protected void solveRelationRestriction(Relation rel, Restriction restric, KnowledgeBase kb) throws InconsistentOntologyException
	{
		switch (kb.getLogic())
		{
			case LUKASIEWICZ:
				LukasiewiczSolver.solveAll(rel, restric, kb);
				break;

			case ZADEH:
				ZadehSolver.solveAll(rel, restric, kb);
				break;

			default: // case CLASSICAL:
				ClassicalSolver.solveAll(rel, restric, kb);
		}

		// Dynamic blocking
		if (kb.blockingDynamic)
			rel.getObjectIndividual().unblock(kb);
	}


	/** 
	 * Gets b individual p with b representative of b set of individuals.
	 * Given b fuzzy number F, b representative individual is the set of
	 * individuals that are greater or equal (or less or equal) than F.
	 * The representative individual is related to p via b concrete feature f.
	 * 
	 * @param type Type of the representative individual (GREATER_EQUAL, LESS_EQUAL).
	 * @param fName Name of the feature for which the individual is b filler.
	 * @param f Fuzzy number.
	 * @param kb Reference fuzzy KB.
	 * @return A new individual with b representative individual.
	 */
	CreatedIndividual getRepresentative(int type, String fName, TriangularFuzzyNumber f, KnowledgeBase kb) throws InconsistentOntologyException
	{
		CreatedIndividual i = getRepresentativeIfExists(type, fName, f, kb);
		if (i != null)
			return i;
		i = kb.getNewConcreteIndividual(null, null);
		RepresentativeIndividual ind = new RepresentativeIndividual(type, fName, f, i);
		representatives.add(ind);
		return i;
	}


	/** 
	 * Return b individual p with b representative of b set of individuals if it
	 * exists. Given b fuzzy number F, b representative individual is the set of
	 * individuals that are greater or equal (or less or equal) than F.
	 * The representative individual is related to p via b concrete feature f.
	 * 
	 * @param type Type of the representative individual (GREATER_EQUAL, LESS_EQUAL).
	 * @param fName Name of the feature for which the individual is b filler.
	 * @param f Fuzzy number.
	 * @param kb Reference fuzzy KB.
	 * @return A new individual with b representative individual.
	 */
	CreatedIndividual getRepresentativeIfExists(int type, String fName, TriangularFuzzyNumber f, KnowledgeBase kb)
	{		
		// Retrieve representative individual, if it exists
		for(RepresentativeIndividual ind : representatives) {
			if ((ind.getType() == type) &&
			(ind.getFeatureName().compareTo(fName) == 0) &&
			ind.getFuzzyNumber().equals(f))
				return ind.getIndividual();
		}
		// Otherwise, null
		return null;
	}


	/**
	 * Unblock the individual.
	 * @param kb Reference fuzzy KB.
	 */
	protected void unblockPairWise(KnowledgeBase kb)
	{
		Util.println("	Test of Pair-wise Unblock children of " +  this.name);

		// "this" is a blocking Y node: unblock blocked nodes
		if(kb.directlyBlockedChildren.containsKey(name))
		{
			Util.println("	  -> " + this.name + " is a blocking Y node");

			// remove Y from the Yprime list
			CreatedIndividual Yprime = (CreatedIndividual) ((CreatedIndividual) this).parent;
			ArrayList<String> y_indivs = kb.yprimeIndivs.get(Yprime.toString());
			y_indivs.remove(name);

			if (! y_indivs.isEmpty())
				kb.yprimeIndivs.put(Yprime.toString(), y_indivs);
			else
			   kb.yprimeIndivs.remove(Yprime.toString());

			// update Xprime list
			for (String Xname : kb.directlyBlockedChildren.get(name))
			{
				Util.println("	  -> processing X node " + Xname);
				// remove Xname from the  Xprime list
				CreatedIndividual X = (CreatedIndividual) kb.individuals.get(Xname);
				CreatedIndividual Xprime = (CreatedIndividual) X.getParent();

				ArrayList<String> x_indivs = kb.xprimeIndivs.get(Xprime.toString());
				x_indivs.remove(Xname);
				if (! x_indivs.isEmpty())
					kb.xprimeIndivs.put(Xprime.toString(), x_indivs);
				else
					kb.xprimeIndivs.remove(Xprime.toString());

				// at last, unblock
				kb.unblockIndividual(Xname);
			}

			// now, Y (= this) cannot be a blocking node anymore
			kb.directlyBlockedChildren.remove(name);
		}

		// if "this" is a Yprime node: unblock blocking Y nodes
		if (kb.yprimeIndivs.containsKey(name))
		{
			Util.println("	  -> " + this.name + " is a  Yprime node");
			for (String Yname : kb.yprimeIndivs.get(name))
			{
				Util.println("	  -> processing Y node " + Yname);
				
				for (String Xname : kb.directlyBlockedChildren.get(Yname))
				{
					Util.println("	  -> processing X node " + Xname);
					// remove X from the  Xprime list
					CreatedIndividual X = (CreatedIndividual) kb.individuals.get(Xname);
					CreatedIndividual Xprime = (CreatedIndividual) X.getParent();
					if (Xprime != null)
					{
						Util.println("**************** " + Xprime);
						ArrayList<String> x_indivs = kb.xprimeIndivs.get(Xprime.toString());
						x_indivs.remove(Xname);
						if (! x_indivs.isEmpty())
							kb.xprimeIndivs.put(Xprime.toString(), x_indivs);
						else
							kb.xprimeIndivs.remove(Xprime.toString());						
					}

					// unblock X
					kb.unblockIndividual(Xname);
				}
				// now, Yname cannot be a blocking node anymore
				kb.directlyBlockedChildren.remove(Yname);

			}

			// now, remove Yprime from the Yprime list
			kb.yprimeIndivs.remove(name);
		}

		// if "this" is a Xprime node: unblock blocked X nodes
		if (kb.xprimeIndivs.containsKey(name))
		{
			Util.println("	  -> " + this.name + " is a  Xprime node");

			ArrayList<String> x_indivs = kb.xprimeIndivs.get(name);
			for (String Xname : x_indivs)
			{
				Util.println("	  -> processing X node " + Xname);
				// remove X from the  directlyBlockedChildren list
				CreatedIndividual X = (CreatedIndividual) kb.individuals.get(Xname);
				//String Yname = X.blockingAncestor; // ?UMBERTO? blockingAncestorY
				String Yname = X.blockingAncestorY;
				if (Yname != null)
				{
					CreatedIndividual Y = (CreatedIndividual) kb.individuals.get(Yname);
					ArrayList<String> blockedByY = kb.directlyBlockedChildren.get(Yname);
					blockedByY.remove(Xname);
					if (! blockedByY.isEmpty())
						kb.directlyBlockedChildren.put(Yname, blockedByY);
					else
					{
						kb.directlyBlockedChildren.remove(Yname);
						// update Yprime list
						CreatedIndividual Yprime = (CreatedIndividual) Y.getParent();
						//ArrayList<String> y_indivs = kb.xprimeIndivs.get(Yprime.toString()); // ?UMBERTO? yprimeIndivs?
						ArrayList<String> y_indivs = kb.yprimeIndivs.get(Yprime.toString()); 
						y_indivs.remove(Yname);
						if (! y_indivs.isEmpty())
							//kb.yprimeIndivs.put(Yprime.toString(), x_indivs); // ?UMBERTO? y_indivs ?
							kb.yprimeIndivs.put(Yprime.toString(), y_indivs); 
						else
							kb.yprimeIndivs.remove(Yprime.toString());
					}					
				}
				// unblock X
				kb.unblockIndividual(Xname);
			}

			// now, remove Xprime from the Xprime list
			kb.xprimeIndivs.remove(name);
		}				
	}

	
	protected void unblock(KnowledgeBase kb)
	{
		int type = kb.blockingType;
		boolean dynamic = kb.blockingDynamic;

		if (! (this instanceof CreatedIndividual))
		   return; // unblock only children of created individuals

		if (! dynamic)
		   return; // no unblock if not dynamic blocking

		// Util.println(" Unblock children of " +  this.name);

		switch (type)
		{
			case KnowledgeBase.NO_BLOCKING:
				break;

			case KnowledgeBase.SUBSET_BLOCKING:				
			case KnowledgeBase.SET_BLOCKING:
			case KnowledgeBase.ANYWHERE_SUBSET_BLOCKING:
			case KnowledgeBase.ANYWHERE_SET_BLOCKING:
				unblockSimple(kb); 
				break;

			case KnowledgeBase.DOUBLE_BLOCKING:				
			case KnowledgeBase.ANYWHERE_DOUBLE_BLOCKING:
			default:
				unblockPairWise(kb);
		}
	}


	/**
	 * Unblock the individual.
	 * Case subset/set blocking
	 * @param kb Reference fuzzy KB.
	 */
	protected void unblockSimple(KnowledgeBase kb)
	{
		Util.println(" Simple Unblock children of " +  this.name);
		
		if(kb.directlyBlockedChildren.containsKey(name))
			kb.unblockChildren(name);
	}


	protected boolean equals(Individual ind)
	{
		return name.equals(ind.name);
	}


	protected boolean isBlockable()
	{
		return false;
	}


	protected void setLabel(String indName) throws InconsistentOntologyException
	{
		throw new InconsistentOntologyException("Individuals cannot have names " + name + " and " + indName);
	}

	
	void prune()
	{
		ArrayList<Individual> toPrune = new ArrayList<Individual> ();
		for (String role : roleRelations.keySet())
		{
			ArrayList<Relation> rels = roleRelations.get(role);
			if(rels == null)
				continue;

			for (Relation r : rels)
			{
				Individual object = r.getObjectIndividual();
				if (object.isBlockable())
					toPrune.add(object);
			}
			// We remove all relations
			rels = null;
		}
		// We remove all relations
		roleRelations = new Hashtable<String,ArrayList<Relation>>();

		// Prune blockable successors
		for (Individual i : toPrune)
			i.prune();
	}


	void addConcept(Concept c)
	{
		listOfConcepts.add(c);
	}


	Set<Concept> getConcepts()
	{
		return listOfConcepts;
	}
	

	void addToNominalList(String indName)
	{
		nominalList.add(indName);
	}


	Set<String> getNominalList()
	{
		return nominalList;
	}

}
