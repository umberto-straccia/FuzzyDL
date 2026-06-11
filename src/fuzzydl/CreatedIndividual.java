package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.util.*;
import java.util.*;


/**
 * New individual (created during the reasoning).
 * @author Fernando Bobillo
 */
public class CreatedIndividual extends Individual
{
	private static final long serialVersionUID = 3485424201995872626L;


	/**
	 * Blocked.
	 */
	public final static int BLOCKED = 0;


	/**
	 * Not blocked.
	 */
	public final static int NOT_BLOCKED = 1;


	/**
	 * Unchecked blocking.
	 */
	public final static int UNCHECKED = 2;


	/**
	 * Name of the blocking ancestors.
	 */
	String blockingAncestor;
	String blockingAncestorY;
	String blockingAncestorYprime;
	

	/**
	 * List of concept labels.
	 */
	HashSet<Integer> conceptList;


	/**
	 * Depth of the individual in the completion forest.
	 */
	int depth;


	/**
	 * Indicates if the individual is directly blocked or not.
	 */
	int directlyBlocked;


	/**
	 * Indicates if the individual is indirectly blocked or not.
	 */
	int indirectlyBlocked;


	/**
	 * Parent of the individual.
	 */
	Individual parent;


	/**
	 * Name of the role for which the individual is a filler.
	 */
	private String roleName;


	/**
	 * Indicates if the individual is concrete or not (abstract).
	 */
	private boolean isConcrete = false;
	

	/**
	 * Constructor.
	 * @param name Name of the individual.
	 */
	public CreatedIndividual(String name)
	{
		this(name, null, null);
		Util.println("Created new individual " + name + ", ID = " + getIntegerID());
	}


	/**
	 * Constructor.
	 * @param name Name of the individual.
	 * @param parent Parent of the individual.
	 * @param roleName Name of the role for which the individual is a filler.
	 */
	public CreatedIndividual(String name, Individual parent, String roleName)
	{
		super(name);
		conceptList = new HashSet<Integer>();
		directlyBlocked = UNCHECKED;
		indirectlyBlocked = UNCHECKED;
		notSelfRoles = new HashSet<String>();
		this.parent = parent;

		if ( (parent != null) && parent.isBlockable() )
			depth = ((CreatedIndividual) parent).depth + 1;
		else
			depth = 2;

		//System.out.println(name + " depth " + depth);

		this.roleName = roleName;
		if (parent != null)
			Util.println("Created new individual " + name + ", ID = " + getIntegerID());
	}


	/**
	 * Constructor.
	 * @param name Name of the individual.
	 * @param parent Parent of the individual.
	 * @param roleName Name of the role for which the individual is a filler.
		 * @param kb the knowledge base.
	 */
	public CreatedIndividual(String name, Individual parent, String roleName, KnowledgeBase kb)
	{
		this(name, parent, roleName);

		// update list of R-successors
		//int type = kb.blockingType;
		//if (type == KnowledgeBase.ANYWHERE_DOUBLE_BLOCKING)
		if (roleName != null)
		{
			Util.println("update list of role-successors ");
			ArrayList<String> rsuccs = kb.rSuccessors.get(roleName);					
			if (rsuccs == null)
				rsuccs = new ArrayList<String>();
			rsuccs.add(name);
			Util.println("r-succ list : " + roleName + " : " + rsuccs);
			kb.rSuccessors.put(roleName,rsuccs);
		}
	}


	@Override
	public CreatedIndividual clone()
	{
		CreatedIndividual ind = new CreatedIndividual(toString(), null, roleName);
		cloneSpecialAttributes(ind);
		return ind;
	}


	void cloneSpecialAttributes(CreatedIndividual ind)
	{
		cloneAttributes(ind);

		ind.blockingAncestor = blockingAncestor;
		ind.blockingAncestorY = blockingAncestorY;
		ind.blockingAncestorYprime = blockingAncestorYprime;

		ind.conceptList = new HashSet<Integer>(conceptList);
		ind.depth = depth;
		ind.directlyBlocked = directlyBlocked;
		ind.indirectlyBlocked = indirectlyBlocked;
		ind.isConcrete = isConcrete;

		if (parent != null)
			ind.parent = parent.clone();

		ind.roleName = roleName;
	}


	/**
	 * Gets the depth of the individual.
	 * @return Depth of the individual
	 */
	public int getDepth()
	{
		return depth;
	}


	/**
	 * Gets the integer ID from the individual's name
	 * @return integer ID from the individual's name
	 */
	public int getIntegerID()
	{
		int prefix =  Individual.DEFAULT_NAME.length();
		return Integer.decode(name.substring(prefix));
	}


	/**
	 * Gets the parent of the individual.
	 * @return Parent of the individual
	 */
	public Individual getParent()
	{
		return parent;
	}


	/**
	 * Gets the name of the parent of the individual.
	 * @return Name of the parent of the individual
	 */
	public String getParentName()
	{
		return parent.toString();
	}


	/**
	 * Gets the toString of the role for which the individual is a filler.
	 * @return Name of the role for which the individual is a filler
	 */
	public String getRoleName()
	{
		return roleName;
	}


	/**
	 * Gets if the individual is indirectly blocked with respect to a fuzzy KB.
	 * @param kb A fuzzy KB.
	 * @return true if the individual is indirectly blocked; false otherwise.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public boolean isIndirectlyBlocked(KnowledgeBase kb)  throws InconsistentOntologyException
	{
		Util.println(" --> Testing indirect blocking " + this + "	at  depth  " + this.depth);

		int type = kb.blockingType;
		boolean dynamic = kb.blockingDynamic;

		// Indirect blocking applies only if we have dynamic blocking
		switch (type)
		{
			case KnowledgeBase.NO_BLOCKING:
				return false;

			case KnowledgeBase.SUBSET_BLOCKING:
			case KnowledgeBase.SET_BLOCKING:
				if (!dynamic)
					return false;
				else
					return isIndirectlySimpleBlocked(kb);
	
			case KnowledgeBase.ANYWHERE_SUBSET_BLOCKING:
			case KnowledgeBase.ANYWHERE_SET_BLOCKING:
				if (!dynamic)
					return false;
				else
					return isIndirectlyAnyWhereSimpleBlocked(kb);

			case KnowledgeBase.DOUBLE_BLOCKING:
				return isIndirectlyPairWiseBlocked(kb);
	
			case KnowledgeBase.ANYWHERE_DOUBLE_BLOCKING:
			default:
				return isIndirectlyAnyWherePairWiseBlocked(kb);
		}
	}


	/**
	 * Gets if the individual is indirectly blocked with respect to a fuzzy KB.
	 * Case SUBSET or SET blocking
	 * @param kb A fuzzy KB.
	 * @return true if the individual is indirectly blocked; false otherwise.
	 */
	private boolean isIndirectlySimpleBlocked(KnowledgeBase kb)  throws InconsistentOntologyException
	{
		// Don't test if not deep enough in completion forest
		if (depth < 4)
		{
			Util.println(" depth < 4, node is not indirectly blocked");
			indirectlyBlocked = NOT_BLOCKED;
			return false;
		}
				
		// Check if already blocked
		if  (indirectlyBlocked == BLOCKED)
		{
			Util.println(" Already checked if indirectly blocked, node IS  blocked");
			return true;
		}

		if (indirectlyBlocked == NOT_BLOCKED)
		{
			Util.println(" Already checked if indirectly blocked, node is not blocked");
			return false;
		}

		// Proceed, assuming indirectlyBlocked == UNCHECKED holds

		indirectlyBlocked = NOT_BLOCKED;
		Individual anc = this.getParent();
		while ((anc != null) && anc.isBlockable())
		{
			CreatedIndividual ancestor = (CreatedIndividual) anc;
			Util.println("	  Indirect blocking: check if directly blocked " + ancestor.name + " at depth  " +  ancestor.depth);

			if (ancestor.isDirectlyBlocked(kb))
			{
				indirectlyBlocked = BLOCKED;
				blockingAncestor = ancestor.toString();

				Util.println(name + " IS INDIRECTLY blocked by " + ancestor);
				break;
			}

			anc = ancestor.getParent();
		}

		return (indirectlyBlocked == BLOCKED);
	}


		/**
	 * Gets if the individual is indirectly anywhere blocked with respect to a fuzzy KB.
	 * Case SUBSET or SET blocking
	 * @param kb A fuzzy KB.
	 * @return true if the individual is indirectly blocked; false otherwise.
	 */
	private boolean isIndirectlyAnyWhereSimpleBlocked(KnowledgeBase kb)  throws InconsistentOntologyException
	{
		// Don't test if not deep enough in completion forest
		if (depth < 3)
		{
			Util.println(" depth < 3, node is not indirectly anywhere blocked");
			indirectlyBlocked = NOT_BLOCKED;
			return false;
		}

		// Check if already blocked
		if  (indirectlyBlocked == BLOCKED)
		{
			Util.println(" Already checked if indirectly blocked, node IS  blocked");
			return true;
		}

		if (indirectlyBlocked == NOT_BLOCKED)
		{
			Util.println(" Already checked if indirectly blocked, node is not blocked");
			return false;
		}

		// Proceed, assuming indirectlyBlocked == UNCHECKED holds

		indirectlyBlocked = NOT_BLOCKED;
		Individual anc = this.getParent();
		while ((anc != null) && anc.isBlockable())
		{
			CreatedIndividual ancestor = (CreatedIndividual) anc;
			Util.println("	  Indirect blocking: check if directly blocked " + ancestor.name + " at depth  " +  ancestor.depth);

			if (ancestor.isDirectlyBlocked(kb))
			{
				indirectlyBlocked = BLOCKED;
				blockingAncestor = ancestor.toString();

				Util.println(name + " IS INDIRECTLY anywhere simple blocked by " + ancestor);
				break;
			}

			anc = ancestor.getParent();
		}

		return (indirectlyBlocked == BLOCKED);
	}



	/**
	 * Gets if the individual is indirectly blocked with respect to a fuzzy KB.
	 * @param kb A fuzzy KB.
	 * @return true if the individual is indirectly blocked; false otherwise.
	 */
	private boolean isIndirectlyPairWiseBlocked(KnowledgeBase kb)  throws InconsistentOntologyException
	{
		// Util.println(" --> Testing indirect blocking " + this + "	at  depth  " + this.depth);

		// Don't test if not deep enough in completion forest
		if (depth < 5)
		{
			Util.println(" depth < 5, node is not indirectly blocked");
			indirectlyBlocked = NOT_BLOCKED;
			return false;
		}

		// Check if already blocked
		if  (indirectlyBlocked == BLOCKED)
		{   Util.println(" Already checked if indirectly blocked, node IS  blocked");
			return true;
		}

		if (indirectlyBlocked == NOT_BLOCKED)
		{
			Util.println(" Already checked if indirectly blocked, node is not blocked");
			return false;
		}

		// Proceed, assuming indirectlyBlocked == UNCHECKED holds				

		indirectlyBlocked = NOT_BLOCKED;
		Individual anc = this.getParent();

		while ((anc != null) && anc.isBlockable())
		{
			CreatedIndividual ancestor = (CreatedIndividual) anc;
			Util.println("	  Indirect blocking: check if directly blocked " + ancestor.name + " at depth  " +  ancestor.depth);

			if (ancestor.isDirectlyBlocked(kb))
			{
				indirectlyBlocked = BLOCKED;
				blockingAncestor = ancestor.toString();

				Util.println(name + " IS INDIRECTLY blocked by " + ancestor);
				break;
			}

			anc = ancestor.getParent();						
		}

		return (indirectlyBlocked == BLOCKED);
	}



	/**
	 * Gets if the individual is indirectly anywhere pairwise blocked with respect to a fuzzy KB.
	 * @param kb A fuzzy KB.
	 * @return true if the individual is indirectly blocked; false otherwise.
	 */
	private boolean isIndirectlyAnyWherePairWiseBlocked(KnowledgeBase kb)  throws InconsistentOntologyException
	{
		// Util.println(" --> Testing indirect blocking " + this + "	at  depth  " + this.depth);

		// Don't test if not deep enough in completion forest
		if (depth < 4)
		{
			Util.println(" depth < 4, node is not indirectly anywhere pairwise blocked");
			indirectlyBlocked = NOT_BLOCKED;
			return false;
		}

		// Check if already blocked
		if  (indirectlyBlocked == BLOCKED)
		{   Util.println(" Already checked if indirectly anywhere pairwise blocked, node IS  blocked");
			return true;
		}

		if (indirectlyBlocked == NOT_BLOCKED)
		{
			Util.println(" Already checked if indirectly anywhere pairwise blocked, node is not blocked");
			return false;
		}

		// Proceed, assuming indirectlyBlocked == UNCHECKED holds

		indirectlyBlocked = NOT_BLOCKED;
		Individual anc = this.getParent();

		while ((anc != null) && anc.isBlockable())
		{
			CreatedIndividual ancestor = (CreatedIndividual) anc;
			Util.println("	  Indirect anywhere pairwise blocking: check if directly blocked " + ancestor.name + " at depth  " +  ancestor.depth);

			if (ancestor.isDirectlyBlocked(kb))
			{
				indirectlyBlocked = BLOCKED;
				blockingAncestor = ancestor.toString();

				Util.println(name + " IS INDIRECTLY anywhere pairwise blocked by " + ancestor);
				break;
			}

			anc = ancestor.getParent();
		}

		return (indirectlyBlocked == BLOCKED);
	}



	/**
	 * Gets if the individual is directly blocked with respect to a fuzzy KB.
	 * @param kb A fuzzy KB.
	 * @return true if the individual is directly blocked; false otherwise.
	 * It is assumed that the individual and all ancestors are not blocked
	 */
	boolean isDirectlyBlocked(KnowledgeBase kb) throws InconsistentOntologyException
	{
		int type = kb.blockingType;
		switch (type)
		{
			case KnowledgeBase.NO_BLOCKING:
				return false;

			case KnowledgeBase.SUBSET_BLOCKING:
			case KnowledgeBase.SET_BLOCKING:
				return isDirectlySimpleBlocked(kb);

			case KnowledgeBase.ANYWHERE_SUBSET_BLOCKING:
			case KnowledgeBase.ANYWHERE_SET_BLOCKING:
				return isDirectlyAnyWhereSimpleBlocked(kb);

			case KnowledgeBase.DOUBLE_BLOCKING:
				return isDirectlyPairWiseBlocked(kb);
							
			case KnowledgeBase.ANYWHERE_DOUBLE_BLOCKING:
			default:
				return isDirectlyAnyWherePairWiseBlocked(kb);
		}
	}


	/**
	 * Gets if the individual is directly blocked with respect to a fuzzy KB.
	 * Case SUBSET or SET blocking
	 * It is assumed that the individual and all ancestors are not blocked
	 * @param kb A fuzzy KB.
	 * @return true if the individual is directly blocked; false otherwise.
	 */
	private boolean isDirectlySimpleBlocked(KnowledgeBase kb)
	{
		Util.println("	Directly Simple blocking status " + this.directlyBlocked);

		// Don't test if not deep enough in completion forest
		if (depth < 3)
		{
			Util.println(" depth < 3, node is not blocked");
			directlyBlocked = NOT_BLOCKED;
			return false;
		}
				
		// If already blocked don't test again
		if (directlyBlocked == BLOCKED)
		{
			Util.println(" Already directly blocked by " + this.blockingAncestor);
			return true;
		}

		if (directlyBlocked == NOT_BLOCKED)
		{
			Util.println(" Already checked if directly blocked, node is not blocked");
			return false;
		}

		// Proceed, assuming directlyBlocked == UNCHECKED holds

		// Direct blocking
		directlyBlocked = NOT_BLOCKED;
		Util.println(" Testing direct blocking : " + this);

		// Loops until the node is blocked or we reach the first root ancestor.
		Individual anc = this.getParent();

		while((anc != null) && anc.isBlockable())
		{
			CreatedIndividual ancestor = (CreatedIndividual) anc;
			Util.println("	  compare with created individual " + ancestor.name + " of depth  " +  ancestor.depth);

			// Test if the concept labels matches
			if(matchConceptLabels(ancestor, kb))
			{
				directlyBlocked = BLOCKED;
				blockingAncestor = anc.toString();

				ArrayList<String> blockedChildren;
				if(kb.directlyBlockedChildren.containsKey(blockingAncestor))
					blockedChildren = kb.directlyBlockedChildren.get(blockingAncestor);
				else
					blockedChildren = new ArrayList<String>();

				if(! blockedChildren.contains(name))
					blockedChildren.add(name);

				kb.directlyBlockedChildren.put(blockingAncestor, blockedChildren);
				
				Util.println(name + " IS DIRECTLY blocked by " + anc);

				// Mark all descendants as indirectly blocked
				markIndirectlyBlocked();
				break;
			}

			Util.println(name + " IS NOT directly blocked by " + anc);
			anc = ancestor.getParent();
		}

		return (directlyBlocked == BLOCKED);
	}


	/**
	 * Gets if the individual is directly anywhere simple blocked with respect to a fuzzy KB.
	 * Case SUBSET or SET blocking
	 * It is assumed that the individual and all ancestors are not blocked
	 * @param kb A fuzzy KB.
	 * @return true if the individual is directly blocked; false otherwise.
	 */
	private boolean isDirectlyAnyWhereSimpleBlocked(KnowledgeBase kb) throws InconsistentOntologyException
	{
		Util.println("	Directly Anywhere Simple blocking status " + this.directlyBlocked);

		// Don't test if not deep enough in completion forest
		
		int nodeID = this.getIntegerID();
		if (nodeID <= 1)
		{
			Util.println(" nodeID : " + nodeID + " <= 1 : node is not blocked");
			directlyBlocked = NOT_BLOCKED;
			return false;
		}
		
		if (depth < 2)
		{
			Util.println(" depth < 2, node is not blocked");
			directlyBlocked = NOT_BLOCKED;
			return false;
		}

		// If already blocked don't test again
		if (directlyBlocked == BLOCKED)
		{
			Util.println(" Already directly blocked by " + this.blockingAncestor);
			return true;
		}

		if (directlyBlocked == NOT_BLOCKED)
		{
			Util.println(" Already checked if directly blocked, node is not blocked");
			return false;
		}

		// Proceed, assuming directlyBlocked == UNCHECKED holds

		// Direct blocking
		directlyBlocked = NOT_BLOCKED;
		Util.println(" Testing direct anywhere blocking : " + this);

		// Find anywhere blocking node
		TreeSet<CreatedIndividual> candidateInd = matchingIndividual(kb);
		Util.println("	  Anywhere blocking: Found individuals: " + candidateInd.toString());

		// Check if we found one
		if(! candidateInd.isEmpty())
		{
			//pick the the first blocking node
			Individual anc = candidateInd.first();
		
			directlyBlocked = BLOCKED;
			blockingAncestor = anc.toString();

			ArrayList<String> blockedChildren;
			if(kb.directlyBlockedChildren.containsKey(blockingAncestor))
				blockedChildren = kb.directlyBlockedChildren.get(blockingAncestor);
			else
				blockedChildren = new ArrayList<String>();

			if(! blockedChildren.contains(name))
				blockedChildren.add(name);

			kb.directlyBlockedChildren.put(blockingAncestor, blockedChildren);

			Util.println(name + " IS DIRECTLY ANYWHERE blocked by " + anc);

			// Mark all descendants as indirectly blocked
			markIndirectlyBlocked();
		} else {
			Util.println(name + " IS NOT directly ANYWHERE blocked");
			// anc = ancestor.getParent();
		}

		return (directlyBlocked == BLOCKED);
	}


	/**
	 * Gets if the individual is directly anywhere simple blocked with respect to a fuzzy KB.
	 * Case SUBSET or SET blocking
	 * It is assumed that the individual and all ancestors are not blocked
	 * @param kb A fuzzy KB.
	 * @return true if the individual is directly blocked; false otherwise.
	 */
/*	private boolean OLDisDirectlyAnyWhereSimpleBlocked(KnowledgeBase kb) throws InconsistentOntologyException
	{
		Util.println("	Directly Anywhere Simple blocking status " + this.directlyBlocked);

		// Don't test if not deep enough in completion forest
		
		int nodeID = this.getIntegerID();
		if (nodeID <= 1)
		{
			Util.println(" nodeID : " + nodeID + " <= 1 : node is not blocked");
			directlyBlocked = NOT_BLOCKED;
			return false;
		}
		
		if (depth < 2)
		{
			Util.println(" depth < 2, node is not blocked");
			directlyBlocked = NOT_BLOCKED;
			return false;
		}

		// If already blocked don't test again
		if (directlyBlocked == BLOCKED)
		{
			Util.println(" Already directly blocked by " + this.blockingAncestor);
			return true;
		}

		if (directlyBlocked == NOT_BLOCKED)
		{
			Util.println(" Already checked if directly blocked, node is not blocked");
			return false;
		}

		// Proceed, assuming directlyBlocked == UNCHECKED holds

		// Direct blocking
		directlyBlocked = NOT_BLOCKED;
		Util.println(" Testing direct anywhere blocking : " + this);

		// Use new method test
		//Set<CreatedIndividual> candidateInd=  matchingIndividual(kb);
		//Util.println("	  Anywhere blocking: Found individuals: " + candidateInd.toString());

		// Loops until the node is blocked
		int i = 1;

		while( i < nodeID)
		{
			String ancName = Individual.DEFAULT_NAME + i;
			Individual anc = kb.individuals.get(ancName);

			CreatedIndividual ancestor = (CreatedIndividual) anc;
			Util.println("	  compare with created individual " + ancestor.name + " of depth  " +  ancestor.depth);

			// Test if the concept labels matches
			if(matchConceptLabels(ancestor, kb))
			{
				directlyBlocked = BLOCKED;
				blockingAncestor = anc.toString();

				ArrayList<String> blockedChildren;
				if(kb.directlyBlockedChildren.containsKey(blockingAncestor))
					blockedChildren = kb.directlyBlockedChildren.get(blockingAncestor);
				else
					blockedChildren = new ArrayList<String>();

				if(! blockedChildren.contains(name))
					blockedChildren.add(name);

				kb.directlyBlockedChildren.put(blockingAncestor, blockedChildren);

				Util.println(name + " IS DIRECTLY ANYWHERE blocked by " + anc);

				// Mark all descendants as indirectly blocked
				markIndirectlyBlocked();
				break;
			}

			Util.println(name + " IS NOT directly ANYWHERE blocked by " + anc);
			//anc = ancestor.getParent();
			i++;
		}

		return (directlyBlocked == BLOCKED);
	}
*/	


	/**
	 * Marks the subtree of a node as indirectly blocked	 
	 * @param none.
	 */
	private void markIndirectlyBlocked()
	{
		Util.println("	------ Mark subtree of " + name + " indirectly blocked" );

		Queue<CreatedIndividual> queue = new LinkedList<CreatedIndividual>();
		queue.add(this);
		while (! queue.isEmpty())
		{
			CreatedIndividual ind = queue.remove();

			// If there are no descendants, skip
			if (ind.roleRelations.isEmpty())
				break;

			for (String role : ind.roleRelations.keySet())
			{
				ArrayList<Relation> rels = new ArrayList<Relation>(ind.roleRelations.get(role));
				for (Relation rel : rels)
				{
					Util.println("	  " + rel.getSubjectIndividual() + " has role " + rel.getRoleName() + " with filler " +  rel.getObjectIndividual());
					Individual son = rel.getObjectIndividual();

					if (son != ind.parent)  // son is not the parent via inverse role
					{
						if (son.isBlockable() )
						{
							CreatedIndividual sonCasted = (CreatedIndividual) son;
							Util.println("		  filler is not " + name + "'s parent, so mark "  + son +  " as INDIRECTLY BLOCKED");
							sonCasted.indirectlyBlocked = BLOCKED;
							if (! rel.getSubjectIndividual().equals(rel.getObjectIndividual()))
								queue.add(sonCasted);							
						}
					}
					else
						Util.println("		filler is parent, so skip ");
				}
			}
		}
		Util.println("	------ END Mark INDIRECTLY BLOCKED subtree of " + name + " ----------------" );
	}


	/**
	 * Marks the subtree of a node as indirectly unblocked
	 * @param kb KnowledgeBase
	 */
	public void markIndirectlySimpleUnChecked(KnowledgeBase kb)
	{
		Util.println("	------ MARK UNCHECKED subtree of : " + name);

		Queue<CreatedIndividual> queue = new LinkedList<CreatedIndividual>();
		queue.add(this);
		while (! queue.isEmpty())
		{
			CreatedIndividual ind = (CreatedIndividual) queue.remove();

			// If there are no descendants, skip
			if (ind.roleRelations.isEmpty())
				break;

			for (String role : ind.roleRelations.keySet())
			{
				ArrayList<Relation> rels = new ArrayList<Relation>(ind.roleRelations.get(role));
				for (Relation rel : rels)
				{
					Util.println("	 " + rel.getSubjectIndividual() + " has role " + rel.getRoleName() + " with filler " +  rel.getObjectIndividual());
					Individual son = rel.getObjectIndividual();
					if (! (son == ind.parent)) // not parent via inverse role
					{
						if (son.isBlockable() )
						{
							CreatedIndividual sonCasted = (CreatedIndividual) son;
							Util.println("		  filler is not " + name + "'s parent, so mark "  + son + " as UNCHECKED");
							sonCasted.unblockIndirectlyBlocked(kb);						
							if (! rel.getSubjectIndividual().equals(rel.getObjectIndividual()))
								queue.add(sonCasted);
						}
					}
					else
						Util.println("	  filler is parent, so skip ");
				}
			}
		}
		Util.println("	------ MARK END UNCHECKED subtree of " + name + " ----------------" );
	}


	/**
	 * Test if the individual is pair-wise directly blocked with respect to a fuzzy KB.
	 * @param kb A fuzzy KB.
	 * @return true if the individual is double blocked; false otherwise.
	 */
	private boolean isDirectlyPairWiseBlocked(KnowledgeBase kb)
	{
		Util.println("	Directly pairwise blocking status " + this.directlyBlocked);

		// Don't test if not deep enough in completion forest
		if (depth < 4)
		{
			Util.println(" depth < 4, node is not directly blocked");
			directlyBlocked = NOT_BLOCKED;
			return false;
		}

		// If already blocked don't test again
		if (directlyBlocked == BLOCKED)
		{
			Util.println(" Already directly blocked by " + this.blockingAncestor);
			return true;
		}

		if (directlyBlocked == NOT_BLOCKED)
		{
			Util.println(" Already checked if directly blocked, node is not blocked");
			return false;

		}

		// Proceed, assuming directlyBlocked == UNCHECKED holds

		// Direct blocking
		directlyBlocked = NOT_BLOCKED;
		Util.println(" Testing direct pair-wise blocking : " + this);			   

		CreatedIndividual nodeXprime = (CreatedIndividual) this.getParent();
		CreatedIndividual nodeY = (CreatedIndividual) this.getParent();											 

		// Test for direct blocking
		while (((CreatedIndividual) nodeY).getParent().isBlockable())
		{
			CreatedIndividual nodeYprime = (CreatedIndividual) nodeY.getParent();
			//Util.println("	  nodeX.roleName " + this.roleName);
			Util.println("		" + nodeXprime.name + " : " + this.roleName + " : " + this.name);
			Util.println("		" + nodeYprime.name + " : " + nodeY.roleName + " : " + nodeY.name);

				
			if ((this.roleName == nodeY.roleName) && matchConceptLabels(nodeY, kb) && nodeXprime.matchConceptLabels(nodeYprime, kb) )
			{
				// We got a pair-wise direct blocking
				directlyBlocked = BLOCKED;
				blockingAncestor = nodeY.toString();

				ArrayList<String> blockedChildren;
				if(kb.directlyBlockedChildren.containsKey(blockingAncestor))
					blockedChildren = kb.directlyBlockedChildren.get(blockingAncestor);
				else
					blockedChildren = new ArrayList<String>();

				// Add nodeX to blocked nodes of blocking node nodeY
				if(! blockedChildren.contains(name))
					blockedChildren.add(name);

				kb.directlyBlockedChildren.put(blockingAncestor, blockedChildren);

				// Create a link from y' to y
				ArrayList<String> y_indivs;
		
				String yprime = nodeYprime.toString();
			
				if(kb.yprimeIndivs.containsKey(yprime))
					y_indivs = kb.yprimeIndivs.get(yprime);
				else
					y_indivs = new ArrayList<String>();

				if(! y_indivs.contains(this.blockingAncestor))
					y_indivs.add(this.blockingAncestor);

				// Given yprime, update the list of y nodes
				kb.yprimeIndivs.put(yprime, y_indivs);

				// Create a link from x' to x
				ArrayList<String> x_indivs;

				String xprime = nodeXprime.toString();

				if(kb.xprimeIndivs.containsKey(xprime))
					x_indivs = kb.xprimeIndivs.get(xprime);
				else
					x_indivs = new ArrayList<String>();

				if(! x_indivs.contains(name))
					x_indivs.add(name);

				// Given xprime, update the list of x nodes
				kb.xprimeIndivs.put(xprime, x_indivs);
		
				Util.println("BLOCKING : x =" + name + " is directly blocked with y = " + nodeY + ", x' = " + nodeXprime + ", y' = " + nodeYprime);

				// Mark all descendants as indirectly blocked
				markIndirectlyBlocked();
				break;
			}

			nodeY = (CreatedIndividual) nodeY.getParent();		
		}

		return directlyBlocked == BLOCKED;
	}


	/**
	 * Test if the individual is anywhere pair-wise directly blocked with respect to a fuzzy KB.
	 * @param kb A fuzzy KB.
	 * @return true if the individual is double blocked; false otherwise.
	 */
	private boolean isDirectlyAnyWherePairWiseBlocked(KnowledgeBase kb) throws InconsistentOntologyException
	{
		Util.println("	Directly anywhere pairwise blocking status " + this.directlyBlocked);

		// Don't test if not deep enough in completion forest
		if (depth < 3)
		{
			Util.println(" depth < 3, node is not directly  anywhere pairwise blocked");
			directlyBlocked = NOT_BLOCKED;
			return false;
		}

		// If already blocked don't test again
		if (directlyBlocked == BLOCKED)
		{
			Util.println(" Already directly  anywhere pairwise blocked by " + this.blockingAncestor);
			return true;
		}

		if (directlyBlocked == NOT_BLOCKED)
		{
			Util.println(" Already checked if directly anywhere pairwise blocked, node is not blocked");
			return false;

		}

		// Proceed, assuming directlyBlocked == UNCHECKED holds

		// Direct blocking
		directlyBlocked = NOT_BLOCKED;
		Util.println(" Testing direct  anywhere pairwise blocking : " + this);

		//CreatedIndividual nodeXprime = (CreatedIndividual) this.getParent();
		CreatedIndividual nodeXprime = (CreatedIndividual) this.getParent();
		String xprime = nodeXprime.toString();
		CreatedIndividual nodeX = this;
		String roleName = this.roleName;
		Util.println("		Edge nodeXPrime:Role:nodeX = " + xprime + " : " + nodeX.roleName + " : " + nodeX.name);
		ArrayList<String> rsuccs = kb.rSuccessors.get(roleName);
		int indexNodeX = rsuccs.indexOf(nodeX.name);
		Util.println("		successors list " + rsuccs);
		Util.println("			   position " + indexNodeX);

		// Test for direct blocking
		//while (((CreatedIndividual) nodeY).getParent().isBlockable())
		int i =0;
		while (i < indexNodeX)
		{
			String ynode = rsuccs.get(i);
			CreatedIndividual nodeY = (CreatedIndividual) kb.getIndividual(ynode);
			Util.println("		nodeY " + ynode + " depth = " + nodeY.depth);
	
			// skip if nodeY not deep enough in tree
			if (nodeY.depth < 3)
			{
				Util.println(" depth < 3, node cannot be nodeY");
				i++;						
			} else
			{
				CreatedIndividual nodeYprime = (CreatedIndividual) nodeY.getParent();
				//Util.println("	  nodeX.roleName " + this.roleName);
				Util.println("		" + nodeXprime.name + " : " + this.roleName + " : " + this.name);
				Util.println("		" + nodeYprime.name + " : " + nodeY.roleName + " : " + nodeY.name);
	
	
				//if ((this.roleName == nodeY.roleName) && matchConceptLabels(nodeY, kb) && nodeXprime.matchConceptLabels(nodeYprime, kb) )
				if (matchConceptLabels(nodeY, kb) && nodeXprime.matchConceptLabels(nodeYprime, kb) )
				{
					// We got a pair-wise direct blocking
					directlyBlocked = BLOCKED;
					blockingAncestor = nodeY.toString();
	
					ArrayList<String> blockedChildren;
					if(kb.directlyBlockedChildren.containsKey(blockingAncestor))
						blockedChildren = kb.directlyBlockedChildren.get(blockingAncestor);
					else
						blockedChildren = new ArrayList<String>();
	
					// Add nodeX to blocked nodes of blocking node nodeY
					if(! blockedChildren.contains(name))
						blockedChildren.add(name);
	
					kb.directlyBlockedChildren.put(blockingAncestor, blockedChildren);
	
					// Create a link from y' to y
					ArrayList<String> y_indivs;
	
					String yprime = nodeYprime.toString();
	
					if(kb.yprimeIndivs.containsKey(yprime))
						y_indivs = kb.yprimeIndivs.get(yprime);
					else
						y_indivs = new ArrayList<String>();
	
					if(! y_indivs.contains(this.blockingAncestor))
						y_indivs.add(this.blockingAncestor);
	
					// Given yprime, update the list of y nodes
					kb.yprimeIndivs.put(yprime, y_indivs);
	
					// Create a link from x' to x
					ArrayList<String> x_indivs;
	
					//String xprime = nodeXprime.toString();
	
					if(kb.xprimeIndivs.containsKey(xprime))
						x_indivs = kb.xprimeIndivs.get(xprime);
					else
						x_indivs = new ArrayList<String>();
	
					if(! x_indivs.contains(name))
						x_indivs.add(name);
	
					// Given xprime, update the list of x nodes
					kb.xprimeIndivs.put(xprime, x_indivs);
	
					Util.println("BLOCKING : x =" + name + " is directly blocked with y = " + nodeY + ", x' = " + nodeXprime + ", y' = " + nodeYprime);
					blockingAncestor = nodeXprime.toString();
					blockingAncestorY = nodeY.toString();
					blockingAncestorYprime = nodeYprime.toString();

					// Mark all descendants as indirectly blocked
					markIndirectlyBlocked();
					break;
				}

				i++;
				// nodeY = (CreatedIndividual) nodeY.getParent();
			}
		}

		return directlyBlocked == BLOCKED;
	}


	/**
	 * Gets if the individual is blocked with respect to a fuzzy KB.
	 * @param kb A fuzzy KB.
	 * @return true if the individual is blocked; false otherwise.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public boolean isBlocked(KnowledgeBase kb)  throws InconsistentOntologyException
	{
		return ( isIndirectlyBlocked(kb) ||  isDirectlyBlocked(kb));
	}


	/**
	 * Checks if there is a matching individual to this one
	 */
	//private Set<CreatedIndividual> matchingIndividual(KnowledgeBase kb) throws InconsistentOntologyException
	private TreeSet<CreatedIndividual> matchingIndividual(KnowledgeBase kb) throws InconsistentOntologyException
	{
		Util.println("--->>  ------------------- ");
		Util.println("--->>  Find matching individual for : " + name + " ID : " + this.getIntegerID() + " size : "+ conceptList.size());
		Util.println("       concept list : " + conceptList);

		boolean firstConcept = true;
		TreeSet<CreatedIndividual> candidateSet = new TreeSet<CreatedIndividual>(new IndividualComparator());

		int type = kb.blockingType;

		for (int l1 : conceptList)
		{
			Util.println("     Process   concept  " + l1 + ": " + kb.getConceptFromNumber(l1));
			Util.println("     Individuals List  " + kb.conceptIndividualList.get(l1));

			TreeSet<CreatedIndividual> currentInd = kb.conceptIndividualList.get(l1);
			
			if (!firstConcept)
			{
				currentInd = individualSetIntersectionOf(candidateSet,currentInd);
				// If empty, exit immediately
				if (currentInd.isEmpty())
					return currentInd;
			} 
			else // firstConcept
			{			
				// Drop no good individuals:
				// 
				// 1. created later
				// 2. node label size is not ok
				// 3. is not blocked
				//
				// Do it only for the fist concept
				
				//candidateSet = new HashSet<CreatedIndividual>();
				candidateSet = new TreeSet<CreatedIndividual>(new IndividualComparator());
				for (CreatedIndividual ind : currentInd)
				{
					if (ind.getIntegerID() >= this.getIntegerID()) break;
					
					Util.println("------->> individual  " + ind.name + " ID : " + ind.getIntegerID() + " size : "+ ind.conceptList.size());
					Util.println("------->> concept list  " + ind.conceptList);
					
					// Node should be created earlier and node is not blocked
					boolean isBlocked = (ind.directlyBlocked == BLOCKED) || (ind.indirectlyBlocked == BLOCKED);
					
					Util.println("------->> Blocked?   " + isBlocked);
					
					if ( (ind.getIntegerID() < this.getIntegerID()) && !isBlocked )						
					{							
						switch (type)
						{
							case KnowledgeBase.ANYWHERE_SUBSET_BLOCKING: // subset blocking
								if (ind.conceptList.size() >= conceptList.size())
									candidateSet.add(ind);
						
							case KnowledgeBase.ANYWHERE_SET_BLOCKING: // set blocking
								if (ind.conceptList.size() == conceptList.size())
									candidateSet.add(ind);						
						}
					}
				}			

				Util.println("------->> Candidate set  " + candidateSet.toString());	

				// For concept c, there is no candidate, so return immediately false
				if (candidateSet.isEmpty())
					return candidateSet;

				firstConcept = false;
			}
		}
		// Util.println("	  Found individuals: " + candidateSet.toString());
		Util.println("--->>  ------------------- ");
		return candidateSet;
	}


	/**
	 * Gets the intersection of two concept labels.
	 * @param set1 Set of concept labels of an individual.
	 * @param set2 Set of concept labels of another individual.
	 * @return Intersection of two concept labels.
	 */
	static TreeSet<CreatedIndividual> individualSetIntersectionOf(TreeSet<CreatedIndividual> set1, TreeSet<CreatedIndividual> set2)
	{
		TreeSet<CreatedIndividual> a;
		TreeSet<CreatedIndividual> b;
		TreeSet<CreatedIndividual> res = new TreeSet<CreatedIndividual>(new IndividualComparator());

		if ((set1.size() <= set2.size()))
		{
			a = set1;
			b = set2;			
		} 
		else
		{
			a = set2;
			b = set1;
		}

		for (CreatedIndividual ind : a)
			if (b.contains(ind))
				res.add(ind);
		
		return res;
	}

	
	/**
	 * Checks if two individuals match concept labels
	 */
	private boolean matchConceptLabels(CreatedIndividual b, KnowledgeBase kb)
	{
		Util.println("--->>  Concept label comparison : " + name + " with "+ b.name);
		Util.println("------->> individual  " + name + " size : "+ conceptList.size());
		for(int l1 : conceptList)
			Util.println("concept  " + l1 + ": " + kb.getConceptFromNumber(l1) );
			Util.println("------->> individual  " + b.name + " size : "+ b.conceptList.size());
		for(int l2 : b.conceptList)
			Util.println("concept  " + l2+ ": " + kb.getConceptFromNumber(l2) );

		int type = kb.blockingType;

		// indirect blocking applies only if we have dynamic blocking
		switch (type)
		{
			case KnowledgeBase.NO_BLOCKING: // no blocking. matching does not apply
				return false;

			case KnowledgeBase.SUBSET_BLOCKING: // subset blocking
			case KnowledgeBase.ANYWHERE_SUBSET_BLOCKING: // subset blocking
				return matchSubSetConceptLabels(b, kb); // match subset

			case KnowledgeBase.SET_BLOCKING: // set blocking
			case KnowledgeBase.ANYWHERE_SET_BLOCKING: // set blocking
			case KnowledgeBase.DOUBLE_BLOCKING: // pair-wise  blocking
			case KnowledgeBase.ANYWHERE_DOUBLE_BLOCKING: // anywhere pair-wise blocking	
			default:
				return matchSetConceptLabels(b, kb); // match set
		}
	}


	/**
	 * Check that every concept in the labels of this is also in b
	 */
	private boolean matchSubSetConceptLabels(CreatedIndividual b, KnowledgeBase kb)
	{
		if (b == null) 
			return false;

		return b.conceptList.containsAll(conceptList);
	}


	/**
	 * Check that two concept labels are equal
	 */
	private boolean matchSetConceptLabels(CreatedIndividual b, KnowledgeBase kb)
	{
		if (b == null)
			return false;
	
		return b.conceptList.equals(conceptList);
	}


	/**
	 * Unblocks an indirectly blocked individual.
	 */
	void unblockDirectlyBlocked(KnowledgeBase kb)
	{
		Util.println("Directly blocked individual : " + this + " : now unchecked");
		directlyBlocked = UNCHECKED;
		blockingAncestor = null;
		ArrayList<Assertion> a = kb.blockedExistAssertions.get(this.toString());
		if (a != null)
		{
			kb.existAssertions.addAll(a);
			kb.blockedExistAssertions.remove(this.toString());
		}
	}


	/**
	 * Unblocks an indirectly blocked individual.
	 */
	void unblockIndirectlyBlocked(KnowledgeBase kb)
	{
		Util.println("Indirectly blocked individual : " + this + " : now unchecked");
		indirectlyBlocked = UNCHECKED;
		blockingAncestor = null;
		ArrayList<Assertion> a = kb.blockedExistAssertions.get(this.toString());
		if (a != null)
		{
			kb.existAssertions.addAll(a);
			kb.blockedExistAssertions.remove(this.toString());
		}
		a = kb.blockedAssertions.get(this.toString());
		if (a != null)
		{
			kb.addAssertions(a);
			kb.blockedAssertions.remove(this.toString());
		}				
	}


	/**
	 * Sets that the individual is concrete.
	 */
	public void setConcreteIndividual()
	{
		isConcrete = true;
	}


	/**
	 * Checks if the individual is concrete.
	 * @return true if the individual is concrete; false otherwise.
	 */
	public boolean isConcrete()
	{
		return isConcrete;
	}


	@Override
	protected boolean isBlockable()
	{
		return nominalList.isEmpty();
	}


}
