package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;

import java.io.*;
import java.util.*;

/**
 * Fuzzy concept.
 * @author Fernando Bobillo
 */
public class Concept implements Serializable
{
	private static final long serialVersionUID = 4034150648415164602L;


	/**
	 * Default prefix for new individual names.
	 */
	public static final String DEFAULT_NAME = "concept@";


	// ------------------
	// Types of concepts
	// ------------------

	/**
	 * Conjunction.
	 */
	public final static int AND = 0;


	/**
	 * Goedel conjunction.
	 */
	public final static int G_AND = 1;


	/**
	 * Lukasiewicz conjunction.
	 */
	public final static int L_AND = 2;


	/**
	 * Disjunction.
	 */
	public final static int OR = 3;


	/**
	 * Goedel disjunction.
	 */
	public final static int G_OR = 4;


	/**
	 * Lukasiewicz disjunction.
	 */
	public final static int L_OR = 5;


	/**
	 * Existential restriction.
	 */
	public final static int SOME = 6;


	/**
	 * Universal restriction.
	 */
	public final static int ALL = 7;


	/**
	 * Upper fuzzy rough concept.
	 */
	public final static int UPPER_APPROX = 8;


	/**
	 * Lower fuzzy rough concept.
	 */
	public final static int LOWER_APPROX = 9;


	/**
	 * Negated fuzzy number.
	 */
	public final static int FUZZY_NUMBER_COMPLEMENT = 10;


	/**
	 * Tight upper fuzzy rough concept.
	 */
	public final static int TIGHT_UPPER_APPROX = 11;


	/**
	 * Tight lower fuzzy rough concept.
	 */
	public final static int TIGHT_LOWER_APPROX = 12;


	/**
	 * Loose upper fuzzy rough concept.
	 */
	public final static int LOOSE_UPPER_APPROX = 13;


	/**
	 * Loose lower fuzzy rough concept.
	 */
	public final static int LOOSE_LOWER_APPROX = 14;


	/**
	 * Goedel implication.
	 */
	public final static int G_IMPLIES = 15;


	/**
	 * Negated Goedel implication.
	 */
	public final static int NOT_G_IMPLIES = 16;


	/**
	 * Atomic concept.
	 */
	public final static int ATOMIC = 17;


	/**
	 * Complement.
	 */
	public final static int COMPLEMENT = 18;

	
	/**
	 * Top concept.
	 */
	public final static int TOP = 19;


	/**
	 * Bottom concept.
	 */
	public final static int BOTTOM = 20;


	/**
	 * At most datatype restriction.
	 */
	public final static int AT_MOST_VALUE = 21;


	/**
	 * At least datatype restriction.
	 */
	public final static int AT_LEAST_VALUE = 22;


	/**
	 * Exact datatype restriction.
	 */
	public final static int EXACT_VALUE = 23;


	/**
	 * Negated at most datatype restriction.
	 */
	public final static int NOT_AT_MOST_VALUE = 24;


	/**
	 * Negated at least datatype restriction.
	 */
	public final static int NOT_AT_LEAST_VALUE = 25;


	/**
	 * Negated exact datatype restriction.
	 */
	public final static int NOT_EXACT_VALUE = 26;


	/**
	 * Weighted concept.
	 */
	public final static int WEIGHTED = 27;


	/**
	 * Negated weighted concept.
	 */
	public final static int NOT_WEIGHTED = 28;


	/**
	 * Weighted sum.
	 */
	public final static int W_SUM = 29;


	/**
	 * Negated weighted sum.
	 */
	public final static int NOT_W_SUM = 30;


	/**
	 * Positive threshold.
	 */
	public final static int POS_THRESHOLD = 31;


	/**
	 * Negated positive threshold.
	 */
	public final static int NOT_POS_THRESHOLD = 32;


	/**
	 * Negative threshold.
	 */
	public final static int NEG_THRESHOLD = 33;


	/**
	 * Negated negative threshold.
	 */
	public final static int NOT_NEG_THRESHOLD = 34;


	/**
	 * Extended positive threshold.
	 */
	public final static int EXT_POS_THRESHOLD = 35;


	/**
	 * Extended negated positive threshold.
	 */
	public final static int NOT_EXT_POS_THRESHOLD = 36;


	/**
	 * Extended negative threshold.
	 */
	public final static int EXT_NEG_THRESHOLD = 37;


	/**
	 * Extended negated negative threshold.
	 */
	public final static int NOT_EXT_NEG_THRESHOLD = 38;


	/**
	 * Concrete concept.
	 */
	public final static int CONCRETE = 39;


	/**
	 * Negated concrete concept.
	 */
	public final static int CONCRETE_COMPLEMENT = 40;


	/**
	 * Modified concept.
	 */
	public final static int MODIFIED = 41;


	/**
	 * Negated modified concept.
	 */
	public final static int MODIFIED_COMPLEMENT = 42;


	/**
	 * Self reflexivity concept.
	 */
	public final static int SELF = 43;


	/**
	 * Fuzzy number.
	 */
	public final static int FUZZY_NUMBER = 44;


	/**
	 * OWA concept.
	 */
	public final static int OWA = 45;


	/**
	 * Quantifier-guided OWA concept.
	 */
	public final static int QUANTIFIED_OWA = 46;


	/**
	 * Negated OWA concept.
	 */
	public final static int NOT_OWA = 47;


	/**
	 * Negated quantifier-guided OWA concept.
	 */
	public final static int NOT_QUANTIFIED_OWA = 48;


	/**
	 * Choquet integral.
	 */
	public final static int CHOQUET_INTEGRAL = 49;


	/**
	 * Sugeno integral.
	 */
	public final static int SUGENO_INTEGRAL = 50;	


	/**
	 * Quasi-Sugeno integral.
	 */
	public final static int QUASI_SUGENO_INTEGRAL = 51;


	/**
	 * Negated Choquet integral.
	 */
	public final static int NOT_CHOQUET_INTEGRAL = 52;


	/**
	 * Negated Sugeno integral.
	 */
	public final static int NOT_SUGENO_INTEGRAL = 53;	


	/**
	 * Negated quasi-Sugeno integral.
	 */
	public final static int NOT_QUASI_SUGENO_INTEGRAL = 54;


	/**
	 * Weighted maximum.
	 */
	public final static int W_MAX = 55;


	/**
	 * Negated weighted maximum.
	 */
	public final static int NOT_W_MAX = 56;


	/**
	 * Weighted minimum.
	 */
	public final static int W_MIN = 57;


	/**
	 * Negated weighted minimum.
	 */
	public final static int NOT_W_MIN = 58;


	/**
	 * Weighted sum.
	 */
	public final static int W_SUM_ZERO = 59;


	/**
	 * Negated weighted sum.
	 */
	public final static int NOT_W_SUM_ZERO = 60;


	/**
	 * Negated self reflexivity concept.
	 */
	public final static int NOT_SELF = 61;


	/**
	 * Has value restriction concept.
	 */
	public final static int HAS_VALUE= 62;


	/**
	 * Negated has value restriction concept.
	 */
	public final static int NOT_HAS_VALUE= 63;


	/**
	 * Zadeh'set inclusion implication, only used for min-subs queries.
	 */
	public final static int Z_IMPLIES = 64;


	/**
	 * Negated Zadeh'set inclusion implication.
	 */
	public final static int NOT_Z_IMPLIES = 65;


	/**
	 * Sigma-count concept
	 */
	public final static int SIGMA_CONCEPT = 66;


	/**
	 * Negated sigma-count concept.
	 */
	public final static int NOT_SIGMA_CONCEPT = 67;


	/**
	 * Number of concept types.
	 */
	final static int NUM_TYPES = 68;


	// -------------
	// Attributes
	// -------------

	/**
	 * In some complex concepts, it is a simpler concept.
	 */
	protected Concept c1;


	/**
	 * Used atomic concepts.
	 */
	protected HashSet<Concept> atomicConcepts;


	/**
	 * In some complex (n-ary) concepts, it is a vector of simpler concepts.
	 */
	protected ArrayList<Concept> concepts;


	// Name of the concept
	protected String name;


	// Number of new concepts
	private static int numNewConcepts = 1;


	/**
	 * In some complex concepts, it is the name of a role.
	 */
	String role;


	// Used to create new concepts
	private final static String specialString = "@";


	/**
	 * Type of the concept
	 */
	protected int type;


	// Value in datatype restrictions
	Object value;


	// In some complex concepts, it is a weight
	double weight = 1;


	// In some complex concepts, it is a weighted variable
	private Variable weightVariable;


	// -------------
	// Constructors
	// -------------
		

	/**
	 * Atomic concept constructor.
	 * @param name Name of the atomic concept
	 */
	public Concept(String name)
	{
		this(ATOMIC);
		this.name = name;
	}


	Concept(int type)
	{
		this.type = type;
	}


	Concept(String name, int type)
	{
		this(type);
		this.name = name;
	}


	Concept(int type, double w, Concept c)
	{
		this(type);
		c1 = c;
		weight = w;
	}


	Concept(int type, Variable w, Concept c)
	{
		this(type);
		c1 = c;
		weightVariable = w;
	}


	Concept(int type, String role, Object value)
	{
		this(type);
		this.role = role;
		this.value = value;
	}


	/**
	 * Gets a top concept.
	 */
	public final static Concept CONCEPT_TOP = new Concept("*top*", TOP);


	/**
	 * Gets a bottom concept.
	 */
	public final static Concept CONCEPT_BOTTOM = new Concept("*bottom*", BOTTOM);


	/**
	 * Gets a new atomic concept.
	 * @return A new atomic concept.
	 */
	public static Concept newAtomicConcept()
	{
		return new Concept("NewConcept" + specialString + numNewConcepts++);
	}


	// ------------------------------
	// Methods to create new concepts
	// ------------------------------


	/**
	 * Gets a new binary conjunction concept.
	 * @param c1 A concept.
	 * @param c2 Another concept.
	 * @return A new binary conjunction concept.
	 */
	public static Concept and(Concept c1, Concept c2)
	{
		ArrayList<Concept> v = new ArrayList<Concept> ();
		v.add(c1);
		v.add(c2);
		return and(v);
	}


	/**
	 * Gets a new n-ary conjunction concept.
	 * @param v List of concepts.
	 * @return A new n-ary conjunction concept.
	 */
	public static Concept and(ArrayList<Concept> v)
	{
		return nAry(AND, v);
	}


	/**
	 * Gets a new binary conjunction concept under Goedel semantics.
	 * @param c1 A concept.
	 * @param c2 Another concept.
	 * @return A new binary conjunction concept under Goedel semantics.
	 */
	public static Concept gAnd(Concept c1, Concept c2)
	{	
		ArrayList<Concept> v = new ArrayList<Concept> ();
		v.add(c1);
		v.add(c2);
		return gAnd(v);
	}


	/**
	 * Gets a new n-ary conjunction concept under Goedel semantics.
	 * @param v List of concepts.
	 * @return A new n-ary conjunction concept under Goedel semantics.
	 */
	public static Concept gAnd(ArrayList<Concept> v)
	{
		Concept aux = nAry(G_AND, v);
		return aux;
	}



	/**
	 * Gets a new binary conjunction concept under Lukasiewicz semantics.
	 * @param c1 A concept.
	 * @param c2 Another concept.
	 * @return A new binary conjunction concept under Lukasiewicz semantics.
	 */
	public static Concept lAnd(Concept c1, Concept c2)
	{
		ArrayList<Concept> v = new ArrayList<Concept> ();
		v.add(c1);
		v.add(c2);

		if (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL)
			return and(v);;
		return lAnd(v);
	}


	/**
	 * Gets a new n-ary conjunction concept under Lukasiewicz semantics.
	 * @param v List of concepts.
	 * @return A new n-ary conjunction concept under Lukasiewicz semantics.
	 */
	public static Concept lAnd(ArrayList<Concept> v)
	{
		if (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL)
			return nAry(AND, v);	
		return nAry(L_AND, v);
	}


	/**
	 * Gets a new binary disjunction concept.
	 * @param c1 A concept.
	 * @param c2 Another concept.
	 * @return A new binary disjunction concept.
	 */
	public static Concept or(Concept c1, Concept c2)
	{
		ArrayList<Concept> v = new ArrayList<Concept> ();
		v.add(c1);
		v.add(c2);
		return or(v);
	}


	/**
	 * Gets a new n-ary disjunction concept.
	 * @param v List of concepts.
	 * @return A new n-ary disjunction concept.
	 */
	public static Concept or(ArrayList<Concept> v)
	{
		return nAry(OR, v);
	}


	/**
	 * Gets a new binary disjunction concept under Goedel semantics.
	 * @param c1 A concept.
	 * @param c2 Another concept.
	 * @return A new binary disjunction concept under Goedel semantics.
	 */
	public static Concept gOr(Concept c1, Concept c2)
	{
		ArrayList<Concept> v = new ArrayList<Concept> ();
		v.add(c1);
		v.add(c2);
		if (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL)
			return or(v);
		return gOr(v);
	}


	/**
	 * Gets a new n-ary disjunction concept under Goedel semantics.
	 * @param v List of concepts.
	 * @return A new n-ary disjunction concept under Goedel semantics.
	 */
	public static Concept gOr(ArrayList<Concept> v)
	{
		if (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL)
			return nAry(OR, v);
		return nAry(G_OR, v);
	}


	/**
	 * Gets a new binary disjunction concept under Lukasiewicz semantics.
	 * @param c1 A concept.
	 * @param c2 Another concept.
	 * @return A new binary disjunction concept under Lukasiewicz semantics.
	 */
	public static Concept lOr(Concept c1, Concept c2)
	{
		ArrayList<Concept> v = new ArrayList<Concept> ();
		v.add(c1);
		v.add(c2);
		if (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL)
			return or(v);
		return lOr(v);
	}


	/**
	 * Gets a new n-ary disjunction concept under Lukasiewicz semantics.
	 * @param v List of concepts.
	 * @return A new n-ary disjunction concept under Lukasiewicz semantics.
	 */
	public static Concept lOr(ArrayList<Concept> v)
	{
		if (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL)
			return nAry(OR, v);
		return nAry(L_OR, v);
	}


	/**
	 * Gets a new n-ary concept.
	 * @param type Type of the concept (conjunctions or disjuntions).
	 * @param v List of concepts.
	 * @return A new n-ary concept.
	 */
	public static Concept nAry(int type, ArrayList<Concept> v)
	{
		if (v.size() == 1)
			return v.get(0);

		boolean changes;
		do
		{
			changes = false;
			Util.order(v);

			if (isType(type, AND))
				for (Concept ci : v)
					if (ci.getType() == BOTTOM)
						return CONCEPT_BOTTOM;

			if (isType(type, OR))
				for (Concept ci : v)
					if (ci.getType() == TOP)
						return CONCEPT_TOP;

			// Look for both C and not C
			if ( (type == AND) || (type == L_AND) || (type == OR) || (type == L_OR) ||
				  ((isType(type, AND)) && (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL)) ||
					((isType(type, OR)) && (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL))
				)									 
			{
				for (int i=0; i<v.size() - 1; i++)
				{
					Concept ci = v.get(i);
					String notci = Concept.complement(ci).toString();
					for (int j=i+1; j<v.size(); j++)
					{
						String cj = v.get(j).toString();
						if (cj.equals(notci))
						{
							if (isType(type, OR))
								return CONCEPT_TOP;
							else //	isType(type, AND))
								return CONCEPT_BOTTOM;
						}
					}
				}
			}

			int i=0;
			//while ((i < v.size())
			while ((i < v.size()) && v.size() > 1) // UMBERTO
			{
				Concept ci = v.get(i);
				int typeCi = ci.getType(); 

				// (OP A (OP B C) ... ) => (OP A B C ... )
				if (typeCi == type)
				{
					for (Concept cj : ci.concepts)
						v.add(cj);
					removeElement(v, i);
					changes = true;
				}

				else if (typeCi == TOP)
				{
					removeElement(v, i);
					changes = true;
				}

				else if (typeCi == BOTTOM)
				{
					removeElement(v, i);
					changes = true;
				}

				else
					if ( ( ((type == OR) || (type == G_OR)) && ( (typeCi== AND) || (typeCi == G_AND) ) ) || 
					 	 ( ((type == AND) || (type == G_AND)) && ( (typeCi== OR) || (typeCi == G_OR) ) ) || 
						 ( (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL) && 
						   ((isType(type, OR) && (isType(typeCi, AND)))
							|| (isType(type, AND) && (isType(typeCi, OR))) )
					     )
					   )
					{											
						for (int j=i+1; j<v.size(); j++)
						{
							Concept cj = v.get(j);
							if (containsSubconcept(ci.concepts, cj))
							{																	 
								// C OR (C AND D) -> C
								// C AND (C OR D) -> C
								removeElement(v, i);
								changes = true;
								break;
							}
							if ( ( (type == AND) && (typeCi == OR) ) ||
								 ( (type == OR) && (typeCi == AND) ) ||
								 ( (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL) &&
									((isType(type, OR) && (isType(typeCi, AND)))
									|| (isType(type, AND) && (isType(typeCi, OR))))
								 )
							   )
							{
								int index = containsNegatedSubconcept(ci.concepts, cj);
								if (index != -1)
								{
									// C OR (NOT C AND D) -> C OR D
									// C AND (NOT C OR D) -> C AND D
									ci.concepts.remove(index);
									changes = true;
									if (ci.concepts.size() == 1)
										ci = ci.concepts.get(0);
									else
										ci.name = ci.computeName();
									v.set(i, ci);
									break;
								}
							}
						}
						i++;
					}
					else
						i++;
				}
			
			// (AND/OR C C) = C
			if ( (type == OR) || (type == G_OR) || (type == AND) || (type == G_AND) ||
				  ((KnowledgeBase.semantics == FuzzyLogic.CLASSICAL) &&
					((isType(type, OR) || (isType(type, AND))))
				  )
				)
			{
				i=1;
				while (i < v.size())
				{
					if (v.get(i).toString().equals(v.get(i-1).toString()))
					{
						removeElement(v, i);
						changes = true;
					}
					else
						i++;
				}
			}

			// (all R C) AND (all R C) = (all R (AND C D))
			// (some R C) AND (some R *top*) = (some R C)
			if ( (type == Concept.G_AND) ||  (type == Concept.AND)
				  ||
				  ((KnowledgeBase.semantics == FuzzyLogic.CLASSICAL) && (isType(type, AND)))
				)
			{
				i = 0;
				while (i < v.size())
				{
					Concept ci = v.get(i);
					if (ci.getType() == ALL)
						for (int j=i+1; j < v.size(); j++)
						{
							Concept cj = v.get(j);
							if ( (cj.getType() == ALL) && (ci.getRole().equals(cj.getRole())))
							{
								changes = true;
								v.remove(ci);
								v.remove(cj);
								if ( (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL) && (isType(type, AND)))
									v.add(Concept.all(ci.getRole(), and(ci.c1, cj.c1)));
								else
									v.add(Concept.all(ci.getRole(), gAnd(ci.c1, cj.c1)));
							}
						}
					//(some R C) AND (some R *top*) = (some R C)
					else if (ci.getType() == SOME)
						for (int j=i+1; j < v.size(); j++)
						{
							Concept cj = v.get(j);
							if (cj.getType() == SOME && (ci.getRole().equals(cj.getRole())))
							{
								if (ci.c1.getType() == TOP)
								{
									changes = true;
									v.remove(ci);
								}
								if (cj.c1.getType() == TOP)
								{
									changes = true;
									v.remove(cj);
								}
							}
						}

					i++;
				}
			}

			// (some R C) OR (some R D) = (some R (OR C D))
			if ( (type == Concept.G_OR) ||  (type == Concept.OR)
				 ||
				  ((KnowledgeBase.semantics == FuzzyLogic.CLASSICAL) && (isType(type, OR)))
				)
			{
				i = 0;
				while (i < v.size())
				{
					Concept ci = v.get(i);
					if (ci.getType() == SOME)
						for (int j=i+1; j < v.size(); j++)
						{
							Concept cj = v.get(j);
							if ((cj.getType() == SOME) && (ci.getRole().equals(cj.getRole())))
							{
								changes = true;
								v.remove(ci);
								v.remove(cj);

								if ( (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL) && (isType(type, OR)))
									v.add(Concept.some(ci.getRole(), or(ci.c1, cj.c1)));
								else
									v.add(Concept.some(ci.getRole(), gOr(ci.c1, cj.c1)));							
							}
						}
					i++;
				}
			}
		} while (changes == true);

		Util.order(v);

		if (v.size() == 1)
			return v.get(0);
		else
		{
			Concept aux = new Concept(type);
			aux.concepts = new ArrayList<Concept>(v);
			return aux;
		}
	}


	/**
	 * Gets a new implication concept under Lukasiewicz semantics.
	 * @param c1 Implicator concept.
	 * @param c2 Implicated concept.
	 * @return A new implication concept under Lukasiewicz semantics.
	 */
	public static Concept lImplies(Concept c1, Concept c2)
	{
		if (c1.getType() == Concept.TOP)
			return c2;
		if ((c2.getType() == Concept.TOP) || (c1.getType() == Concept.BOTTOM) )
			return Concept.CONCEPT_TOP;
		if (c2.getType() == Concept.BOTTOM) 
			return Concept.complement(c1);

		if (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL)
			return or(Concept.complement(c1), c2);

		return lOr(Concept.complement(c1), c2);
	}


	/**
	 * Gets a new implication concept under Kleene-Dienes semantics.
	 * @param c1 Implicator concept.
	 * @param c2 Implicated concept.
	 * @return A new implication concept under Kleene-Dienes semantics.
	 */
	public static Concept kdImplies(Concept c1, Concept c2)
	{
		if (c1.getType() == Concept.TOP)
			return c2;
		if ((c2.getType() == Concept.TOP) || (c1.getType() == Concept.BOTTOM) )
			return Concept.CONCEPT_TOP;

		 if (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL)
			  return or(Concept.complement(c1), c2);

		return gOr(Concept.complement(c1), c2);
	}


	/**
	 * Gets a new implication concept under Goedel semantics.
	 * @param c1 Implicator concept.
	 * @param c2 Implicated concept.
	 * @return A new implication concept under Goedel semantics.
	 */
	public static Concept gImplies(Concept c1, Concept c2)
	{
		int typeC1 = c1.getType();
		if (typeC1 == Concept.TOP)
			return c2;
		if ((c2.getType() == Concept.TOP) || (typeC1 == Concept.BOTTOM) )
			return Concept.CONCEPT_TOP;

		if (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL)
			return or(Concept.complement(c1), c2);

		ArrayList<Concept> v = new ArrayList<Concept> ();
		if (typeC1 == G_OR)
		{
			for (Concept ci : c1.concepts)
				v.add(gImplies(ci, c2));
			return gAnd(v);
		}
		else
		{
			Concept c = new Concept(Concept.G_IMPLIES);
			v.add(c1);
			v.add(c2);
			c.concepts = v;
			return c;
		}
	}


	/**
	 * Gets a new implication concept under Zadeh semantics.
	 * @param c1 Implicator concept.
	 * @param c2 Implicated concept.
	 * @return A new implication concept under Zadeh semantics.
	 */
	public static Concept zImplies(Concept c1, Concept c2)
	{
		if (KnowledgeBase.semantics == FuzzyLogic.CLASSICAL)
			return or(Concept.complement(c1), c2);

		Concept c = new Concept(Concept.Z_IMPLIES);
		ArrayList<Concept> v = new ArrayList<Concept> ();
		v.add(c1);
		v.add(c2);
		c.concepts = v;
		return c;
	}


	/**
	 * Gets a new existential restriction concept.
	 * @param role Abstract role.
	 * @param c Concept. 
	 * @return A new existential restriction concept.
	 */
	public static Concept some(String role, Concept c)
	{
		if ((ConfigReader.OPTIMIZATIONS != 0) && (c.getType() == BOTTOM))
			return Concept.CONCEPT_BOTTOM;
	
		Concept aux = new Concept(SOME);
		aux.c1 = c;
		aux.role = role;
		aux.name = aux.computeName();
		return aux;
	}


	/**
	 * Gets a new has value restriction concept.
	 * @param role Abstract role.
	 * @param i Individual. 
	 * @return A new as value restriction concept.
	 */
	public static Concept hasValue(String role, Individual i)
	{
		return hasValue(role, i.toString());
	}


	/**
	 * Gets a new has value restriction concept.
	 * @param role Abstract role.
	 * @param i Individual name. 
	 * @return A new has value restriction concept.
	 */
	public static Concept hasValue(String role, String i)
	{
		Concept aux = new Concept(HAS_VALUE);
		aux.role = role;
		aux.value = i;
		aux.name = aux.computeName();
		return aux;
	}


	/**
	 * Gets a new negated has value restriction concept.
	 * @param role Abstract role.
	 * @param i Individual name. 
	 * @return A new negated has value restriction concept.
	 */
	public static Concept notHasValue(String role, String i)
	{
		Concept aux = hasValue(role, i);
		aux.setType(Concept.NOT_HAS_VALUE);
		aux.name = aux.computeName();
		return aux;
	}

	/**
	 * Gets a new universal restriction concept.
	 * @param role Abstract role.
	 * @param c Concept. 
	 * @return A new universal restriction concept.
	 */
	public static Concept all(String role, Concept c)
	{
		if ((ConfigReader.OPTIMIZATIONS != 0) && (c.getType() == TOP))
			return Concept.CONCEPT_TOP;

		Concept aux = new Concept(ALL);
		aux.c1 = c;
		aux.role = role;
		aux.name = aux.computeName();
		return aux;
	}


	/**
	 * Gets a new local reflexivity concept.
	 * @param role Abstract role.
	 * @return A new local reflexivity concept.
	 */
	public static Concept self(String role)
	{
		Concept aux = new Concept(SELF);
		aux.role = role;
		aux.name = aux.computeName();
		return aux;
	}


	/**
	 * Gets a new upper fuzzy rough concept.
	 * @param role Abstract role.
	 * @param c Concept. 
	 * @return A new pper fuzzy rough concept.
	 */
	public static Concept upperApprox(String role, Concept c)
	{
		return Concept.some(role, c);
	}


	/**
	 * Gets a new lower fuzzy rough concept.
	 * @param role Abstract role.
	 * @param c Concept. 
	 * @return A new lower fuzzy rough concept.
	 */
	public static Concept lowerApprox(String role, Concept c)
	{
		return Concept.all(role, c);
	}


	/**
	 * Gets a new tight upper fuzzy rough concept.
	 * @param role Abstract role.
	 * @param c Concept. 
	 * @return A new tight upper fuzzy rough concept.
	 */
	public static Concept tightUpperApprox(String role, Concept c)
	{
		return Concept.all(role, Concept.some(role, c));
	}


	/**
	 * Gets a new tight lower fuzzy rough concept.
	 * @param role Abstract role.
	 * @param c Concept. 
	 * @return A new tight lower fuzzy rough concept.
	 */
	public static Concept tightLowerApprox(String role, Concept c)
	{
		return Concept.all(role, Concept.all(role, c));
	}


	/**
	 * Gets a new loose upper fuzzy rough concept.
	 * @param role Abstract role.
	 * @param c Concept. 
	 * @return A new loose upper fuzzy rough concept.
	 */
	public static Concept looseUpperApprox(String role, Concept c)
	{
		return Concept.some(role, Concept.some(role, c));
	}


	/**
	 * Gets a new loose lower fuzzy rough concept.
	 * @param role Abstract role.
	 * @param c Concept. 
	 * @return A new loose lower fuzzy rough concept.
	 */
	public static Concept looseLowerApprox(String role, Concept c)
	{
		return Concept.some(role, Concept.all(role, c));
	}


	/**
	 * Gets a new at-most datatype restriction concept.
	 * @param role Concrete feature.
	 * @param o Value of the concrete filler. 
	 * @return A new at-most datatype restriction concept.
	 */
	public static Concept atMostValue(String role, Object o)
	{
		return new Concept(AT_MOST_VALUE, role, o);
	}


	/**
	 * Gets a new at-least datatype restriction concept.
	 * @param role Concrete feature.
	 * @param o Value of the concrete filler. 
	 * @return A new at-least datatype restriction concept.
	 */
	public static Concept atLeastValue(String role, Object o)
	{
		return new Concept(AT_LEAST_VALUE, role, o);
	}


	/**
	 * Gets a new exact datatype restriction concept.
	 * @param role Concrete feature.
	 * @param o Value of the concrete filler. 
	 * @return A new exact datatype restriction concept.
	 */
	public static Concept exactValue(String role, Object o)
	{
		return new Concept(EXACT_VALUE, role, o);
	}


	/**
	 * Gets a new weighted concept.
	 * @param w Weight.
	 * @param c Concept.
	 * @return A new weighted concept.
	 */
	public static Concept weightedConcept(double w, Concept c)
	{
		return new Concept(WEIGHTED, w, c);
	}


	/**
	 * Gets a new positive threshold concept.
	 * @param w Weight.
	 * @param c Concept.
	 * @return A new positive threshold concept.
	 */
	public static Concept posThreshold(double w, Concept c)
	{
		return new Concept(POS_THRESHOLD, w, c);
	}


	/**
	 * Gets a new negative threshold concept.
	 * @param w Weight.
	 * @param c Concept.
	 * @return A new negative threshold concept.
	 */
	public static Concept negThreshold(double w, Concept c)
	{
		return new Concept(NEG_THRESHOLD, w, c);
	}


	/**
	 * Gets a new extended positive threshold concept.
	 * @param w Variable.
	 * @param c Concept.
	 * @return A new extended positive threshold concept.
	 */
	public static Concept extendedPosThreshold(Variable w, Concept c)
	{
		return new Concept(EXT_POS_THRESHOLD, w, c);
	}


	/**
	 * Gets a new extended negative threshold concept.
	 * @param w Variable.
	 * @param c Concept.
	 * @return A new extended negative threshold concept.
	 */
	public static Concept extendedNegThreshold(Variable w, Concept c)
	{
		return new Concept(EXT_NEG_THRESHOLD, w, c);
	}


	// -----------
	// Complement
	// -----------

	/**
	 * Gets the complement of the concept.
	 * @param c A concept to be complemented.
	 * @return Complement of the concept.
	 */
	public static Concept complement(Concept c)
	{
		try
		{
			switch(c.getType())
			{
				case ATOMIC:
					Concept aux = new Concept("(not " + c.toString() + ")", COMPLEMENT);
					aux.c1 = c;
					return aux;

				case COMPLEMENT:
					return new Concept(c.toString().substring(5, c.toString().length() - 1));

				case SOME:
					return Concept.all(c.role , Concept.complement(c.c1));

				case UPPER_APPROX:
					return Concept.lowerApprox(c.role , Concept.complement(c.c1));

				case LOOSE_UPPER_APPROX:
					return Concept.tightLowerApprox(c.role , Concept.complement(c.c1));

				case TIGHT_UPPER_APPROX:
					return Concept.looseLowerApprox(c.role , Concept.complement(c.c1));

				case ALL:
					return Concept.some(c.role, Concept.complement(c.c1));

				case LOWER_APPROX:
					return Concept.upperApprox(c.role , Concept.complement(c.c1));

				case LOOSE_LOWER_APPROX:
					return Concept.tightUpperApprox(c.role , Concept.complement(c.c1));

				case TIGHT_LOWER_APPROX:
					return Concept.looseUpperApprox(c.role , Concept.complement(c.c1));

				case AND:
					ArrayList<Concept> concepts = new ArrayList<Concept> ();
					for (Concept ci : c.concepts)
						concepts.add(Concept.complement(ci));
					return Concept.or(concepts);

				case G_AND:
					concepts = new ArrayList<Concept> ();
					for (Concept ci : c.concepts)
						concepts.add(Concept.complement(ci));
					return Concept.gOr(concepts);

				case L_AND:
					concepts = new ArrayList<Concept> ();
					for (Concept ci : c.concepts)
						concepts.add(Concept.complement(ci));
					return Concept.lOr(concepts);

				case OR:
					concepts = new ArrayList<Concept> ();
					for (Concept ci : c.concepts)
						concepts.add(Concept.complement(ci));
					return Concept.and(concepts);

				case G_OR:
					concepts = new ArrayList<Concept> ();
					for (Concept ci : c.concepts)
						concepts.add(Concept.complement(ci));
					return Concept.gAnd(concepts);

				case L_OR:
					concepts = new ArrayList<Concept> ();
					for (Concept ci : c.concepts)
						concepts.add(Concept.complement(ci));
					return Concept.lAnd(concepts);

				case TOP:
					return CONCEPT_BOTTOM;

				case BOTTOM:
					return CONCEPT_TOP;

				case CONCRETE:
				case CONCRETE_COMPLEMENT:
					return ((FuzzyConcreteConcept) c).complement();

				case MODIFIED:
				case MODIFIED_COMPLEMENT:
					return ((ModifiedConcept) c).complement();

				case AT_MOST_VALUE:
					aux = Concept.atMostValue(c.role, c.value);
					aux.setType(NOT_AT_MOST_VALUE);
					return aux;

				case NOT_AT_MOST_VALUE:
					return Concept.atMostValue(c.role, c.value);

				case AT_LEAST_VALUE:
					aux = Concept.atLeastValue(c.role, c.value);
					aux.setType(NOT_AT_LEAST_VALUE);
					return aux;

				case NOT_AT_LEAST_VALUE:
					return  Concept.atLeastValue(c.role, c.value);

				case EXACT_VALUE:
					aux = Concept.exactValue(c.role, c.value);
					aux.setType(NOT_EXACT_VALUE);
					return aux;

				case NOT_EXACT_VALUE:
					return Concept.exactValue(c.role, c.value);

				case WEIGHTED:
					aux = Concept.weightedConcept(c.getWeight(), c.c1);
					aux.setType(NOT_WEIGHTED);
					return aux;

				case NOT_WEIGHTED:
					return Concept.weightedConcept(c.getWeight(), c.c1);

				case POS_THRESHOLD:
					aux = Concept.posThreshold(c.getWeight(), c.c1);
					aux.setType(NOT_POS_THRESHOLD);
					return aux;

				case NOT_POS_THRESHOLD:
					return Concept.posThreshold(c.getWeight(), c.c1);

				case NEG_THRESHOLD:
					aux = Concept.negThreshold(c.getWeight(), c.c1);
					aux.setType(NOT_NEG_THRESHOLD);
					return aux;

				case NOT_NEG_THRESHOLD:
					return Concept.negThreshold(c.getWeight(), c.c1);

				case EXT_POS_THRESHOLD:
					aux = Concept.extendedPosThreshold(c.getWeightVar(), c.c1);
					aux.setType(NOT_EXT_POS_THRESHOLD);
					return aux;

				case NOT_EXT_POS_THRESHOLD:
					return Concept.extendedPosThreshold(c.getWeightVar(), c.c1);

				case EXT_NEG_THRESHOLD:
					aux = Concept.extendedNegThreshold(c.getWeightVar(), c.c1);
					aux.setType(NOT_EXT_NEG_THRESHOLD);
					return aux;

				case NOT_EXT_NEG_THRESHOLD:
					return Concept.extendedNegThreshold(c.getWeightVar(), c.c1);

				case G_IMPLIES:
					aux = Concept.gImplies(c.concepts.get(0), c.concepts.get(1));
					aux.setType(NOT_G_IMPLIES);
					return aux;

				case NOT_G_IMPLIES:
					return Concept.gImplies(c.concepts.get(0), c.concepts.get(1));

				case Z_IMPLIES:
					aux = Concept.zImplies(c.concepts.get(0), c.concepts.get(1));
					aux.setType(NOT_Z_IMPLIES);
					return aux;

				case NOT_Z_IMPLIES:
					return Concept.zImplies(c.concepts.get(0), c.concepts.get(1));

				case FUZZY_NUMBER:
				case FUZZY_NUMBER_COMPLEMENT:
					return ((FuzzyConcreteConcept) c).complement();

				case W_SUM:
				case NOT_W_SUM:
					return ((WeightedSumConcept) c).complement();

				case W_SUM_ZERO:
				case NOT_W_SUM_ZERO:
					return ((WeightedSumZeroConcept) c).complement();

				case OWA:
				case NOT_OWA:
					return ((OwaConcept) c).complement();

				case QUANTIFIED_OWA:
				case NOT_QUANTIFIED_OWA:
					return ((QowaConcept) c).complement();

				case CHOQUET_INTEGRAL:
				case NOT_CHOQUET_INTEGRAL:
					return ((ChoquetIntegral) c).complement();

				case SUGENO_INTEGRAL:
				case NOT_SUGENO_INTEGRAL:
					return ((SugenoIntegral) c).complement();

				case QUASI_SUGENO_INTEGRAL:
				case NOT_QUASI_SUGENO_INTEGRAL:
					return ((QsugenoIntegral) c).complement();

				case W_MAX:
				case NOT_W_MAX:
					return ((WeightedMaxConcept) c).complement();

				case W_MIN:
				case NOT_W_MIN:
					return ((WeightedMinConcept) c).complement();

				case SELF:
					aux = self(c.getRole());
					aux.setType(NOT_SELF);
					aux.name = aux.computeName();
					return aux;

				case NOT_SELF:
					return self(c.getRole());

				case HAS_VALUE:
					aux = hasValue(c.role, (String) c.value);
					aux.setType(NOT_HAS_VALUE);
					aux.name = aux.computeName();
					return aux;

				case NOT_HAS_VALUE:
					return hasValue(c.role, (String) c.value);

				case SIGMA_CONCEPT:
				case NOT_SIGMA_CONCEPT:
					return ((SigmaConcept) c).complement();
					
				default:
					Util.error("Error complementing concept " + c);
					return null;
			}
		}
		catch (FuzzyOntologyException e) { }

		return null;
	}


	// --------
	// Methods
	// --------

	String computeName()
	{
		try
		{
			switch(type)
			{
				case ATOMIC:
					return name;

				case COMPLEMENT:
					return "(not " + name + ")";

				case SOME:
					return "(some " + role + " " + c1 + ")";

				case ALL:
					return "(all " + role + " " + c1 + ")";

				case AND:
					String s = "(and ";
					for (Concept ci : concepts)
						s += ci.toString() + " ";
					return s + ")";

				case G_AND:
					s = "(g-and ";
					for (Concept ci : concepts)
						s += ci.toString() + " ";
					return s + ")";

				case L_AND:
					s = "(l-and ";
					for (Concept ci : concepts)
						s += ci.toString() + " ";
					return s + ")";

				case OR:
					s = "(or ";
					for (Concept ci : concepts)
						s += ci.toString() + " ";
					return s + ")";

				case G_OR:
					s = "(g-or ";
					for (Concept ci : concepts)
						s += ci.toString() + " ";
					return s + ")";

				case L_OR:
					s = "(l-or ";
					for (Concept ci : concepts)
						s += ci.toString() + " ";
					return s + ")";

				case TOP:
					return "*top*";

				case BOTTOM:
					return "*bottom*";

				case AT_MOST_VALUE:
					if (value instanceof Variable)
						return "(<= " + role + " " + ((Variable) value) + ")";
					else if (value instanceof String)
						return "(<= " + role + " \"" + value + "\")";
					else
						return "(<= " + role + " " + value + ")";

				case NOT_AT_MOST_VALUE:
					if (value instanceof Variable)
						return "(not (<= " + role + " " + ((Variable) value) + ") )"; // umberto
					else if (value instanceof String)
						return "(not (<= " + role + " \"" + value + "\") )";
					else
						return "(not (<= " + role + " " + value + ") )";

				case AT_LEAST_VALUE:
					if (value instanceof Variable)
						return "(>= " + role +  " " + ((Variable) value) + ")"; //umberto
					else if (value instanceof String)
						return "(>= " + role + " \"" + value + "\")";
					else
						return "(>= " + role + " " + value + ")";

				case NOT_AT_LEAST_VALUE:
					if (value instanceof Variable)
						return "(not (>= " + role + " " + ((Variable) value) + ") )"; // umberto
					else if (value instanceof String)
						return "(not (>= " + role + " \"" + value + "\") )";
					else
						return "(not (>= " + role + " " + value + ") )";

				case EXACT_VALUE:
					if (value instanceof Variable)
						return "(= " + role  + " " + ((Variable) value) + ") "; // umberto
					else if (value instanceof String)
						return "(= " + role + " \"" + value + "\")";
					else
						return "(= " + role + " " + value + ")";

				case NOT_EXACT_VALUE:
					if (value instanceof Variable)
						return "(not (= " + role  + " " + ((Variable) value)+ ") )"; // umberto
					else if (value instanceof String)
						return "(not (= " + role + " \"" + value + "\") )";
					else
						return "(not (= " + role + " " + value + ") )";

				case WEIGHTED:
					return "(" + weight + " " + c1 + ")";

				case NOT_WEIGHTED:
					return "(not ( " + weight + " " + c1+ "))";

				case POS_THRESHOLD:
					return "(" + " [ >= " + weight + "] " + c1 + ")" ;

				case NOT_POS_THRESHOLD:
					return "(not (" + " [ >= " + weight + "] " + c1 + ") )" ;

				case NEG_THRESHOLD:
					return "(" + " [ <= " + weight + "] " + c1+ ")" ;
				
				case NOT_NEG_THRESHOLD:
					return "(not (" + " [ <= " + weight + "] " + c1 + ") )" ;

				case EXT_POS_THRESHOLD:
					return "(" + " [ >= " + weightVariable + "] " + c1 + ")" ;

				case NOT_EXT_POS_THRESHOLD:
					return "(not (" + " [ >= " + weightVariable + "] " + c1 + ") )" ;

				case EXT_NEG_THRESHOLD:
					return "(" + " [ <= " + weightVariable + "] " + c1 + ")" ;

				case NOT_EXT_NEG_THRESHOLD:
					return "(not (" + " [ <= " + weightVariable + "] " + c1 + ") )" ;

				case G_IMPLIES:
					return "(g-implies " + concepts.get(0) + " " + concepts.get(1) + ")";

				case NOT_G_IMPLIES:
					return "(not (g-implies " + concepts.get(0) + " " + concepts.get(1) + ") )";

				case Z_IMPLIES:
					return "(z-implies " + concepts.get(0) + " " + concepts.get(1) + ")";

				case NOT_Z_IMPLIES:
					return "(not (z-implies " + concepts.get(0) + " " + concepts.get(1) + ") )";

				case SELF:
					return "(self " + role + ")";

				case NOT_SELF:
					return "(not (self " + role + ") )";

				case UPPER_APPROX:
					return"(ua " + role + " " + c1 + ")";

				case LOWER_APPROX:
					return"(la " + role + " " + c1 + ")";
					
				case LOOSE_UPPER_APPROX :
					return"(lua " + role + " " + c1 + ")";	

				case LOOSE_LOWER_APPROX :
					return"(lla " + role + " " + c1 + ")";	

				case TIGHT_UPPER_APPROX :
					return"(tua " + role + " " + c1 + ")";	

				case TIGHT_LOWER_APPROX :
					return"(tla " + role + " " + c1 + ")";

				case HAS_VALUE:
					return "(b-some " + role + " " + value + ")";

				case NOT_HAS_VALUE:
					return "(not (b-some " + role + " " + value + ") )";

				/*
					case CONCRETE:
					case CONCRETE_COMPLEMENT:
					case MODIFIED:
					case MODIFIED_COMPLEMENT:
				*/
				default:
					Util.error("Error computing the name of the concept " + this);
					return null;
			}
		}
		catch (FuzzyOntologyException e) { }

		return null;
	}


	/**
	 * Gets a printable name of the concept.
	 * @return Name of the concept.
	 */
	@Override
	public String toString()
	{
		if (name == null)
			name = computeName();
		return name;
	}


	/**
	 * Sets the name of the concept.
	 * @param newName Name of the concept.
	 */
	public void setName(String newName)
	{
		name = newName;
	}


	/**
	 * Gets the type of the concept.
	 * @return Type of the concept.
	 */
	public int getType()
	{
		return type;
	}


	/**
	 * Gets the role attribute.
	 * @return Role.
	 */
	public String getRole()
	{
		return role;
	}


	/**
	 * Gets the value attribute.
	 * @return Value.
	 */
	public Object getValue()
	{
		return value;
	}


	/**
	 * Sets the value attribute.
	 * @param newValue Value.
	 */
	public void setValue(Object newValue)
	{
		value = newValue;
	}


	/**
	 * Sets the type of the concept.
	 * @param type Type of the concept.
	 */
	public void setType(int type)
	{
		this.type = type;
		name = toString();
	}


	/**
	 * Gets the weight attribute.
	 * @return Weight.
	 */
	public double getWeight()
	{
		return weight;	
	}


	/**
	 * Gets the weight variable;
	 * @return Weight variable.
	 */
	public Variable getWeightVar()
	{
		return weightVariable;	
	}


	/**
	 * Checks if the concept is concrete (types CONCRETE, CONCRETE_COMPLEMENT, FUZZY_NUMBER or FUZZY_NUMBER_COMPLEMENT)
	 * @return true if the concepts is concrete; false otherwise.
	 */
	public boolean isConcrete()
	{
		if ((type == CONCRETE) || type == (CONCRETE_COMPLEMENT) || (type == FUZZY_NUMBER) || (type == FUZZY_NUMBER_COMPLEMENT) )
			return true;
		else if ((type == MODIFIED) || (type == MODIFIED_COMPLEMENT) )
			return c1.isConcrete();
		else
			return false;
	}


	/**
	 * Checks if the concept is atomic.
	 * @return true if the concepts is atomic; false otherwise.
	 */
	public boolean isAtomic()
	{
		return type == ATOMIC;
	}


	/**
	 * Checks if type1 is of type type2
	 * @return true if type1 is of type type2; false otherwise.
	 */
	private static boolean isType(int type1, int type2)
	{
		switch (type2)
		{
			 case OR:
				  return ((type1 == OR) || (type1 == G_OR) || (type1 == L_OR));
			 case AND:
				  return ((type1 == AND) || (type1 == G_AND) || (type1 == L_AND));
		}
		return false;
	}


	/**
	 * Checks if the concept is complemented atomic.
	 * @return true if the concepts is atomic; false otherwise.
	 */
	public boolean isComplementedAtomic()
	{
		return type == COMPLEMENT;
	}


	/**
	 * Gets a set of the atomic concepts that compose the current concept.
	 * @return A HashSet of instances of the class Concept.
	 */
	HashSet<Concept> computeAtomicConcepts()
	{
		HashSet<Concept> set = new HashSet<Concept>();
		switch (type)
		{		
			case ATOMIC:
				set.add(this);
				return set;

			case COMPLEMENT:
			case ALL:
			case SOME:
			case LOOSE_LOWER_APPROX:
			case TIGHT_LOWER_APPROX:
			case LOWER_APPROX:
			case LOOSE_UPPER_APPROX:
			case TIGHT_UPPER_APPROX:
			case UPPER_APPROX:
			case WEIGHTED:
			case NOT_WEIGHTED:
			case NEG_THRESHOLD:
			case NOT_NEG_THRESHOLD:
			case POS_THRESHOLD:
			case NOT_POS_THRESHOLD:
			case EXT_POS_THRESHOLD:
			case NOT_EXT_POS_THRESHOLD:
			case EXT_NEG_THRESHOLD:
			case NOT_EXT_NEG_THRESHOLD:
			case MODIFIED:
			case MODIFIED_COMPLEMENT:

				return c1.computeAtomicConcepts();

			case AND:
			case G_AND:
			case L_AND:
			case OR:
			case G_OR:
			case L_OR:

				for (Concept con : concepts)
					set.addAll(con.computeAtomicConcepts());
				return set;

			case G_IMPLIES:
			case NOT_G_IMPLIES:

				set = concepts.get(0).computeAtomicConcepts();
				set.addAll(concepts.get(1).computeAtomicConcepts());
				return set;

			// Aggregation operators override the method

			// Empty set
			default:
				return set;
		}
	}



	/**
	 * Gets a set of the roles that compose the current concept.
	 * @return A HashSet of Strings representing the roles.
	 */
	HashSet<String> getRoles()
	{
		HashSet<String> c = new HashSet<String>();
		switch (type)
		{		
			case ATOMIC:
				return c;

			case COMPLEMENT:
			case WEIGHTED:
			case NOT_WEIGHTED:
			case NEG_THRESHOLD:
			case NOT_NEG_THRESHOLD:
			case POS_THRESHOLD:
			case NOT_POS_THRESHOLD:
			case EXT_POS_THRESHOLD:
			case NOT_EXT_POS_THRESHOLD:
			case EXT_NEG_THRESHOLD:
			case NOT_EXT_NEG_THRESHOLD:
			case MODIFIED:
			case MODIFIED_COMPLEMENT:

				return c1.getRoles();

			case SELF:
			case NOT_SELF:

				c.add(getRole());
				return c;

			case ALL:
			case SOME:
			case LOOSE_LOWER_APPROX:
			case TIGHT_LOWER_APPROX:
			case LOWER_APPROX:
			case LOOSE_UPPER_APPROX:
			case TIGHT_UPPER_APPROX:
			case UPPER_APPROX:

				c.add(getRole());
				c.addAll(c1.getRoles());
				return c;

			case AND:
			case G_AND:
			case L_AND:
			case OR:
			case G_OR:
			case L_OR:

				for (Concept con : concepts)
					c.addAll(con.getRoles());
				return c;
				
			case G_IMPLIES:
			case NOT_G_IMPLIES:
				c = concepts.get(0).getRoles();
				c.addAll(concepts.get(1).getRoles());
				return c;

			// Aggregation operators override the method

			// Empty set
			default:
				return c;
		}
	}


	// Remove the i-th concept of the vector of simpler concepts
	void delConcept(int i)
	{
		int size = concepts.size();
		if (i < size)
		{
			if (size == 2)
			{
				type = Concept.ATOMIC;
				c1 = concepts.get(1-i);
				concepts.remove(1);
				concepts.remove(0);
				name = c1.toString();
			}
			else
			{
				concepts.remove(i);
				name = computeName();
			}
		}
	}


	public static int containsNegatedSubconcept(ArrayList<Concept> v, Concept cj)
	{
		for(int i=0; i<v.size(); i++)
		{
			Concept ci = v.get(i);
			if (ci.toString().equals(Concept.complement(cj).toString()))
				return i;
		}
		return -1;
	}


	public static boolean containsSubconcept(ArrayList<Concept> v, Concept cj)
	{
		for(Concept ci : v)
			if (ci.toString().equals(cj.toString()))
				return true;
		return false;
	}


	public static void removeElement(ArrayList<Concept> v, int i)
	{
		if (v.size() > 1)
			v.remove(i);
	}


	// -----------
	// Replacement
	// -----------

	/**
	 * Gets a new concept by replacing an atomic concept a with a complex concept c.
	 * @param a Atomic concept.
	 * @param c Complex concept.
	 * @return Complement of the concept.
	 * @throws FuzzyOntologyException Parsing error.
	 */
	public Concept replace(Concept a, Concept c) throws FuzzyOntologyException
	{
		try
		{
			switch(c.getType())
			{
				case ATOMIC:
					if (equals(a))
						return c;
					else
						return this;

				case COMPLEMENT:
					if (c1.equals(a))
						return Concept.complement(c);
					else
						return this;

				case SOME:
					return Concept.some(role , c1.replace(a, c));

				case UPPER_APPROX:
					return Concept.upperApprox(role , c1.replace(a, c));

				case LOOSE_UPPER_APPROX:
					return Concept.looseUpperApprox(role , c1.replace(a, c));

				case TIGHT_UPPER_APPROX:
					return Concept.tightUpperApprox(role , c1.replace(a, c));

				case ALL:
					return Concept.all(role , c1.replace(a, c));

				case LOWER_APPROX:
					return Concept.lowerApprox(role , c1.replace(a, c));

				case LOOSE_LOWER_APPROX:
					return Concept.looseLowerApprox(role , c1.replace(a, c));

				case TIGHT_LOWER_APPROX:
					return Concept.tightLowerApprox(role , c1.replace(a, c));

				case AND:
					ArrayList<Concept> replacedConcepts = new ArrayList<Concept> ();
					for (Concept ci : concepts)
						replacedConcepts.add(ci.replace(a, c));
					return Concept.and(concepts);

				case G_AND:
					replacedConcepts = new ArrayList<Concept> ();
					for (Concept ci : concepts)
						replacedConcepts.add(ci.replace(a, c));
					return Concept.gAnd(concepts);

				case L_AND:
					replacedConcepts = new ArrayList<Concept> ();
					for (Concept ci : concepts)
						replacedConcepts.add(ci.replace(a, c));
					return Concept.lAnd(concepts);

				case OR:
					replacedConcepts = new ArrayList<Concept> ();
					for (Concept ci : concepts)
						replacedConcepts.add(ci.replace(a, c));
					return Concept.or(concepts);

				case G_OR:
					replacedConcepts = new ArrayList<Concept> ();
					for (Concept ci : concepts)
						replacedConcepts.add(ci.replace(a, c));
					return Concept.gOr(concepts);

				case L_OR:
					replacedConcepts = new ArrayList<Concept> ();
					for (Concept ci : concepts)
						replacedConcepts.add(ci.replace(a, c));
					return Concept.lOr(concepts);

				case MODIFIED:
				case MODIFIED_COMPLEMENT:
					return ((ModifiedConcept) this).replace(a, c);

				case WEIGHTED:
					return Concept.weightedConcept(getWeight(), c1.replace(a, c));

				case NOT_WEIGHTED:
					return Concept.complement(weightedConcept(getWeight(), c1.replace(a, c)));

				case POS_THRESHOLD:
					return Concept.posThreshold(getWeight(), c1.replace(a, c));

				case NOT_POS_THRESHOLD:
					return Concept.complement(Concept.posThreshold(getWeight(), c1.replace(a, c)));

				case NEG_THRESHOLD:
					return Concept.negThreshold(getWeight(), c1.replace(a, c));

				case NOT_NEG_THRESHOLD:
					return Concept.complement(Concept.negThreshold(getWeight(), c1.replace(a, c)));

				case EXT_POS_THRESHOLD:
					return Concept.extendedPosThreshold(getWeightVar(), c1.replace(a, c));

				case NOT_EXT_POS_THRESHOLD:
					return Concept.complement(Concept.extendedPosThreshold(getWeightVar(), c1.replace(a, c)));

				case EXT_NEG_THRESHOLD:
					return Concept.extendedNegThreshold(getWeightVar(), c1.replace(a, c));

				case NOT_EXT_NEG_THRESHOLD:
					return Concept.complement(Concept.extendedNegThreshold(getWeightVar(), c1.replace(a, c)));

				case G_IMPLIES:
					return Concept.gImplies(concepts.get(0).replace(a, c), concepts.get(1).replace(a, c));

				case NOT_G_IMPLIES:
					return Concept.complement(Concept.gImplies(concepts.get(0).replace(a, c), concepts.get(1).replace(a, c)));

				case W_SUM:
				case NOT_W_SUM:
					return ((WeightedSumConcept) c).replace(a, c);

				case W_SUM_ZERO:
				case NOT_W_SUM_ZERO:
					return ((WeightedSumZeroConcept) c).replace(a, c);

				case OWA:
				case NOT_OWA:
					return ((OwaConcept) c).replace(a, c);

				case QUANTIFIED_OWA:
				case NOT_QUANTIFIED_OWA:
					return ((QowaConcept) c).replace(a, c);

				case CHOQUET_INTEGRAL:
				case NOT_CHOQUET_INTEGRAL:
					return ((ChoquetIntegral) c).replace(a, c);

				case SUGENO_INTEGRAL:
				case NOT_SUGENO_INTEGRAL:
					return ((SugenoIntegral) c).replace(a, c);

				case QUASI_SUGENO_INTEGRAL:
				case NOT_QUASI_SUGENO_INTEGRAL:
					return ((QsugenoIntegral) c).replace(a, c);

				case W_MAX:
				case NOT_W_MAX:
					return ((WeightedMaxConcept) c).replace(a, c);

				case W_MIN:
				case NOT_W_MIN:
					return ((WeightedMinConcept) c).replace(a, c);

				case TOP:
				case BOTTOM:
				case CONCRETE:
				case CONCRETE_COMPLEMENT:
				case AT_MOST_VALUE:
				case NOT_AT_MOST_VALUE:
				case AT_LEAST_VALUE:
				case NOT_AT_LEAST_VALUE:
				case EXACT_VALUE:
				case NOT_EXACT_VALUE:
				case FUZZY_NUMBER:
				case FUZZY_NUMBER_COMPLEMENT:
				case SELF:
				case NOT_SELF:
					return this;

				default:
					Util.error("Error replacing in concept " + this);
					return null;
			}
		}
		catch (FuzzyOntologyException e) { }

		return null;
	}


	public HashSet<Concept> getAtomicConcepts()
	{
		if (atomicConcepts == null)
			atomicConcepts = computeAtomicConcepts();
		return atomicConcepts;
	}


	public HashSet<String> getAtomicConceptNames()
	{
		if (atomicConcepts == null)
			atomicConcepts = computeAtomicConcepts();
		HashSet<String> set = new HashSet<String>();
		for (Concept c : atomicConcepts)
			set.add(c.name);
		return set;
	}


	public boolean equals(Concept c)
	{
		return name.equals(c.toString());
	}
	
	
	public boolean hasNominals()
	{
		return toString().contains("(b-some ");
	}

}
