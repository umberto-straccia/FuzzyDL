package fuzzydl;

import java.io.*;
import java.util.*;

import fuzzydl.exception.*;
import fuzzydl.graph.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;


/**
 * Fuzzy knowledge base.
 * @author Fernando Bobillo
 */
public class KnowledgeBase implements Serializable
{
	private static final long serialVersionUID = 2117202683246301403L;


	/**
	 * Version of the reasoner.
	 */
	final static double VERSION = 2.63;


	/**
	 * Datatypes ranges.
	 */
	static final double MAXVAL = 1000 * (double) Integer.MAX_VALUE;
	static final double MAXVAL2 = MAXVAL + MAXVAL;


	/**
	 * Blocking types.
	 */
	final static int NO_BLOCKING = 0;
	final static int SUBSET_BLOCKING = 1;
	final static int SET_BLOCKING = 2;
	final static int DOUBLE_BLOCKING = 3;
	final static int ANYWHERE_SUBSET_BLOCKING = 4;
	final static int ANYWHERE_SET_BLOCKING = 5;
	final static int ANYWHERE_DOUBLE_BLOCKING = 6;


	/**
	 * Rules.
	 */
	final static int RULE_ATOMIC = 0;
	final static int RULE_COMPLEMENT = 1;
	final static int RULE_G_AND = 2;
	final static int RULE_L_AND = 3;
	final static int RULE_G_OR = 4;
	final static int RULE_L_OR = 5;
	final static int RULE_G_SOME = 6;
	final static int RULE_L_SOME = 7;
	final static int RULE_G_ALL = 8;
	final static int RULE_L_ALL = 9;
	final static int RULE_TOP = 10;
	final static int RULE_BOTTOM = 11;
	final static int RULE_G_IMPLIES = 12;
	final static int RULE_NOT_G_IMPLIES = 13;
	final static int RULE_CONCRETE = 14;
	final static int RULE_NOT_CONCRETE = 15;
	final static int RULE_MODIFIED = 16;
	final static int RULE_NOT_MODIFIED= 17;
	final static int RULE_DATATYPE = 18;
	final static int RULE_NOT_DATATYPE = 19;
	final static int RULE_FUZZY_NUMBER = 20;
	final static int RULE_NOT_FUZZY_NUMBER = 21;
	final static int RULE_WEIGHTED = 22;
	final static int RULE_NOT_WEIGHTED = 23;
	final static int RULE_THRESHOLD = 24;
	final static int RULE_NOT_THRESHOLD = 25;
	final static int RULE_OWA = 26;
	final static int RULE_NOT_OWA = 27;
	final static int RULE_W_SUM = 28;
	final static int RULE_NOT_W_SUM = 29;
	final static int RULE_CHOQUET_INTEGRAL =30;
	final static int RULE_NOT_CHOQUET_INTEGRAL = 31;	
	final static int RULE_SUGENO_INTEGRAL = 32;
	final static int RULE_NOT_SUGENO_INTEGRAL = 33;	
	final static int RULE_QUASI_SUGENO_INTEGRAL = 34;
	final static int RULE_NOT_QUASI_SUGENO_INTEGRAL = 35;
	final static int RULE_SELF = 36;
	final static int RULE_NOT_SELF = 37;
	final static int RULE_W_MIN = 38;
	final static int RULE_NOT_W_MIN = 39;
	final static int RULE_W_MAX = 40;
	final static int RULE_NOT_W_MAX = 41;
	final static int RULE_W_SUM_ZERO = 42;
	final static int RULE_NOT_W_SUM_ZERO = 43;
	final static int RULE_HAS_VALUE = 44;
	final static int RULE_NOT_HAS_VALUE = 45;
	final static int RULE_Z_IMPLIES = 46;
	final static int RULE_NOT_Z_IMPLIES = 47;
	final static int RULE_SIGMA_COUNT = 48;
	final static int RULE_NOT_SIGMA_COUNT = 49;
	final static int NUMBER_OF_RULES = 50;


	/**
	 * Names of the rules
	 */
	private final static String[] RULE_NAMES = 
	{
		"ATOMIC", "COMPLEMENT", "G_AND", "L_AND", "G_OR", "L_OR", 
		"G_SOME", "L_SOME", "G_ALL", "L_ALL", "TOP", "BOTTOM",
		"G_IMPLIES", "NOT_G_IMPLIES", "CONCRETE", "NOT_CONCRETE", 
		"MODIFIED", "NOT_MODIFIED", "DATATYPE", "NOT_DATATYPE", 
		"FUZZY_NUMBER", "NOT_FUZZY_NUMBER", "WEIGHTED", "NOT_WEIGHTED", 
		"THRESHOLD", "NOT_THRESHOLD", "OWA", "NOT_OWA", 
		"W_SUM", "NOT_W_SUM", "CHOQUET_INTEGRAL", "NOT_CHOQUET_INTEGRAL",
		"SUGENO_INTEGRAL", "NOT_SUGENO_INTEGRAL",
		"QUASI_SUGENO_INTEGRAL", "NOT_QUASI_SUGENO_INTEGRAL",
		"SELF", "RULE_NOT_SELF",
		"W_MIN", "NOT_W_MIN", "W_MAX", "NOT_W_MAX" , "W_SUM_ZERO", "NOT_W_SUM_ZERO",
		"HAS_VALUE", "NOT_HAS_VALUE", "Z_IMPLIES", "NOT_Z_IMPLIES", "SIGMA_COUNT", "NOT_SIGMA_COUNT"
	};


	/**
	 * ABox completely expanded.
	 */
	private boolean ABOX_EXPANDED;


	/**
	 * Abstract roles.
	 */
	public HashSet<String> abstractRoles;


	/**
	 * Acyclic TBox.
	 */
	private boolean acyclicTbox;


	/**
	 * Appplications of the transitive funcRole rule.
	 */
	private ArrayList<String> appliedTransRoleRules;


	/**
	 * Fuzzy assertions.
	 */
	ArrayList<Assertion> assertions;


	/**
	 * Fuzzy concepts.
	 */
	public Hashtable<String,Concept> atomicConcepts;


	/**
	 * Definitions A = C.
	 */
	Hashtable<String, HashSet<Concept>> axiomsAequivC;


	/**
	 * Primitive concept definitions A isA C.
	 */
	Hashtable<String, HashSet<PrimitiveConceptDefinition>> axiomsAisaB;


	/**
	 * Primitive concept definitions A isA C.
	 */
	Hashtable<String, HashSet<PrimitiveConceptDefinition>> axiomsAisaC;


	/**
	 * Equivalent concepts C = D.
	 */
	ArrayList<ConceptEquivalence> axiomsCequivD;


	/**
	 * Primitive concept definitions A isA C.
	 */
	Hashtable<String, HashSet<GeneralConceptInclusion>> axiomsCisaA;


	/**
	 * GCIs C isA D.
	 */
	Hashtable<String, HashSet<GeneralConceptInclusion>> axiomsCisaD;
	

	// Part of the TBox with axioms A isA B and A isa C for further absorption processing
	private Hashtable<String, HashSet<PrimitiveConceptDefinition>> axiomsToDoAisaB;
	private Hashtable<String, HashSet<PrimitiveConceptDefinition>> axiomsToDoAisaC;
	private Hashtable<String, HashSet<GeneralConceptInclusion>> axiomsToDoCisaA;
	private Hashtable<String, HashSet<GeneralConceptInclusion>> axiomsToDoCisaD;
	private Hashtable<String, HashSet<PrimitiveConceptDefinition>> axiomsToDoTmpAisaC;
	private Hashtable<String, HashSet<GeneralConceptInclusion>> axiomsToDoTmpCisaA;
	private Hashtable<String, HashSet<GeneralConceptInclusion>> axiomsToDoTmpCisaD;


	/**
	 * Blocked assertions.
	 */
	Hashtable<String, ArrayList<Assertion>> blockedAssertions;


	/**
	 * Blocked existential assertions.
	 */
	Hashtable<String, ArrayList<Assertion>> blockedExistAssertions;


	/**
	 * Dynamism of blocking.
	 */
	boolean blockingDynamic;


	/**
	 * Type of the blocking that must be checked for this KB.
	 */
	int blockingType;


	/**
	 * Classified ontology.
	 */
	private boolean CLASSIFIED;


	/**
	 * Set of created individuals that have a concept in the concept list conceptList
	 */
	Hashtable<Integer, TreeSet<CreatedIndividual>> conceptIndividualList;


	/**
	 * Fuzzy concepts.
	 */
	public Hashtable<String,FuzzyConcreteConcept> concreteConcepts;


	/**
	 * Concrete features.
	 */
	public Hashtable<String, ConcreteFeature> concreteFeatures;


	/**
	 * Concrete fuzzy concepts.
	 */
	public boolean concreteFuzzyConcepts;


	/**
	 * Concrete roles.
	 */
	public HashSet<String> concreteRoles;


	/**
	 * Direcyly blocked childrens.
	 */
	Hashtable<String,ArrayList<String>> directlyBlockedChildren;


	/**
	 * Disjoint variables.
	 */
	private Hashtable<String, HashSet<String>> disjointVariables;


	/**
	 * Domain restrictions.
	 */
	Hashtable<String, HashSet<Concept>> domainRestrictions;


	/**
	 * Exists assertions.
	 */
	public ArrayList<Assertion> existAssertions;


	/**
	 * Functional roles.
	 */
	public HashSet<String> funcRoles;


	/**
	 * Fuzzy numbers.
	 */
	public Hashtable<String,TriangularFuzzyNumber> fuzzyNumbers;


	/**
	 * Individuals.
	 */
	Hashtable<String, Individual> individuals;


	/**
	 * Inverse roles.
	 */
	Hashtable<String, Set<String>> invRoles;


	/**
	 * Inverse functional roles.
	 */
	private HashSet<String> invFuncRoles;


	/**
	 * KB completely loaded from file.
	 */
	private boolean KB_LOADED;


	/**
	 * true: unsatisfiable KB; false: satisfiable KB or unknown.
	 */
	private boolean KB_UNSAT;


	/**
	 * DL language.
	 */
	private String language;


	/**
	 * For every nominal in a node, a list of the nodes where it appears
	 */
	private Hashtable<String, Set<String> > labelsWithNodes;
	

	/**
	 * already lazy unfoldable.
	 */
	private boolean lazyUnfoldable;

	
	/**
	 * Maximal depth of the completion forest.
	 */
	private int maxDepth;


	/**
	 * MILP problem manager.
	 */
	public MILPHelper milp;


	/**
	 * Fuzzy modifiers.
	 */
	public Hashtable<String,Modifier> modifiers;


	/**
	 * Classified atomic concepts.
	 */
	List<ClassificationNode> nodesClassification = new ArrayList<ClassificationNode> ();


	/**
	 * Number of assertions.
	 */
	private int numAssertions;


	/**
	 * Assigns a number to a concept name
	 */
	private Hashtable<String, Integer> numberOfConcepts;


	/**
	 * Assigns a number to a role name
	 */
	private Hashtable<String, Integer> numberOfRoles;


	/**
	 * Number of new concepts.
	 */
	private int numDefinedConcepts;


	/**
	 * Number of new individuals.
	 */
	private int numDefinedInds;


	/**
	 * Number of relations.
	 */
	int numRelations;


	/**
	 * Number of variables that the old calculus would create.
	 */
	int old01Variables;
	int oldBinaryVariables;


	/**
	 * Strings appearing in concrete concepts and their associated real numbers.
	 */
	private Hashtable<String, Integer> order;


	/**
	 * Positive datatype restrictions.
	 */
	private ArrayList<Assertion> positiveConcreteValueAssertions;


	/**
	 * Processed assertion.
	 */
	private HashSet<Integer> processedAssertions;


	/**
	 * Range restrictions.
	 */
	Hashtable<String, HashSet<Concept>> rangeRestrictions;


	/**
	 * Reflexive roles.
	 */
	HashSet<String> reflexiveRoles;


	/**
	 * All parents for a role constructed from all role inclusions in kb.
	 */
	private Hashtable<String, Hashtable<String,Double>> rolesWithAllParents;


	/**
	 * Transitive childrens of a role.
	 */
	Hashtable<String, ArrayList<String>> rolesWithTransChildren;


	/**
	 * Direct parents of a role directly defined.
	 */
	Hashtable<String, Hashtable<String,Double>> rolesWithParents;


	/**
	 * R-successors.
	 */
	Hashtable<String, ArrayList<String>> rSuccessors;


	/**
	 * Rule acyclic TBox.
	 */
	private boolean ruleAcyclicTbox;


	/**
	 * Number of application of the rules.
	 */
	int[] rulesApplied;


	/**
	 * Fuzzy logic.
	 */
	public static FuzzyLogic semantics;
	public FuzzyLogic localSemantics;


	/**
	 * Show the logic language to the user or not.
	 */
	public boolean showLanguage;


	/**
	 * Similarity relations.
	 */
	public HashSet<String> similarityRels;


	/**
	 * Subsumption degrees in classified ontologies.
	 */
	HashMap<String, HashMap<String, Double>> subFlags;


	/**
	 * Symmetric roles.
	 */
	HashSet<String> symmetricRoles;


	// Part of the TBox with axioms A = C to which we can apply lazy unfolding
	private Hashtable<String, Concept> tDef;


	// Part of the TBox with disjoitn axioms which we can apply lazy unfolding
	Hashtable<String, HashSet<String>> tDis;


	// Used by string datatypes
	private ArrayList<Concept> tempStringConceptList;


	// Used by string datatypes
	private ArrayList<String> tempStringList;


	// Used by string datatypes
	private Hashtable<String, ArrayList<Relation>> tempRelationsList;


	// Part of the TBox to which we cannot apply lazy unfolding
	private ArrayList<GeneralConceptInclusion> tG;


	/**
	 * Transitive roles.
	 */
	HashSet<String> transRoles;

	
	// Part of the TBox with axioms A isA C to which we can apply lazy unfolding
	private Hashtable<String, HashSet<PrimitiveConceptDefinition>> tInc;


	// GCIs of the form A = B with both A and B being atomic.
	private Hashtable<String, HashSet<String>> tSyn;


	/**
	 * x' individuals for indirect blocking.
	 */
	Hashtable<String,ArrayList<String>> xprimeIndivs;


	/**
	 * y' individuals for indirect blocking.
	 */
	Hashtable<String,ArrayList<String>> yprimeIndivs;


	/**
	 * Constructor
	 */
	public KnowledgeBase()
	{
		ABOX_EXPANDED = false;
		abstractRoles = new HashSet<String>();
		appliedTransRoleRules = new ArrayList<String>();
		assertions = new ArrayList<Assertion>();
		atomicConcepts = new Hashtable<String, Concept>();
		axiomsAequivC = new Hashtable<String, HashSet<Concept>>();
		axiomsAisaB = new Hashtable<String, HashSet<PrimitiveConceptDefinition>> ();
		axiomsAisaC = new Hashtable<String, HashSet<PrimitiveConceptDefinition>> ();
		axiomsCequivD = new ArrayList<ConceptEquivalence> ();
		axiomsCisaA = new Hashtable<String, HashSet<GeneralConceptInclusion>>();
		axiomsCisaD = new Hashtable<String, HashSet<GeneralConceptInclusion>>();
		blockedAssertions = new Hashtable<String, ArrayList<Assertion>>();
		blockedExistAssertions = new Hashtable<String, ArrayList<Assertion>>();
		blockingType = DOUBLE_BLOCKING;
		CLASSIFIED = false;
		conceptIndividualList =  new Hashtable<Integer, TreeSet<CreatedIndividual>>();
		concreteConcepts = new Hashtable<String, FuzzyConcreteConcept>();
		concreteFeatures = new Hashtable<String, ConcreteFeature>();
		concreteFuzzyConcepts = false;
		concreteRoles = new HashSet<String>();
		directlyBlockedChildren = new Hashtable<String, ArrayList<String>>();
		tDis = new Hashtable<String, HashSet<String>>();
		disjointVariables = new Hashtable<String, HashSet<String>>();
		domainRestrictions = new Hashtable<String, HashSet<Concept>>();
		existAssertions = new ArrayList<Assertion>();
		funcRoles = new HashSet<String>();
		fuzzyNumbers = new Hashtable<String, TriangularFuzzyNumber>();
		individuals = new Hashtable<String, Individual>();
		invRoles = new Hashtable<String, Set<String>>();
		invFuncRoles = new HashSet<String>();
		lazyUnfoldable = false;
		KB_LOADED = false;
		KB_UNSAT = false;
		labelsWithNodes = new Hashtable<String, Set<String>>();
		maxDepth = 1;
		milp = new MILPHelper();
		modifiers = new Hashtable<String, Modifier>();
		numAssertions = 0;
		numberOfConcepts = new Hashtable<String, Integer>();
		numberOfRoles = new Hashtable<String, Integer>();
		numDefinedConcepts = 0;
		numDefinedInds = 0;
		numRelations = 0;
		old01Variables = 0;
		oldBinaryVariables = 0;
		order = new Hashtable<String, Integer>();
		positiveConcreteValueAssertions = new ArrayList<Assertion>();
		processedAssertions = new HashSet<Integer>();
		rangeRestrictions = new Hashtable<String, HashSet<Concept>>();
		reflexiveRoles = new HashSet<String>();
		rolesWithAllParents = new Hashtable<String, Hashtable<String,Double>>();
		rolesWithParents = new Hashtable<String, Hashtable<String, Double>>();
		rolesWithTransChildren = new Hashtable<String, ArrayList<String>>();
		rSuccessors = new Hashtable<String, ArrayList<String>>();
		rulesApplied = new int[NUMBER_OF_RULES];
		showLanguage = false;
		similarityRels = new HashSet<String>();
		subFlags = new HashMap<String, HashMap<String, Double>> (); 		
		symmetricRoles = new HashSet<String>();
		tDef = new Hashtable<String, Concept>();
		tempRelationsList = new Hashtable<String, ArrayList<Relation>>();
		tempStringConceptList = new ArrayList<Concept>();
		tempStringList = new ArrayList<String>();
		tG = new ArrayList<GeneralConceptInclusion>();
		tInc = new Hashtable<String, HashSet<PrimitiveConceptDefinition>> ();
		transRoles = new HashSet<String>();
		tSyn = new Hashtable<String, HashSet<String>>();
		xprimeIndivs = new Hashtable<String, ArrayList<String>>();
		yprimeIndivs = new Hashtable<String, ArrayList<String>>();
	}


	/**
	 * Gets a copy of a knowledge base.
	 * @return A copy of the knowledge base.
	 */
	@Override
	public KnowledgeBase clone()
	{
		KnowledgeBase kb = cloneWithoutABox();

		// Clone assertions
		kb.assertions = new ArrayList<Assertion>(assertions);

        // Clone individuals
        kb.individuals = new Hashtable<String, Individual>();
        for(String i : individuals.keySet())
        {
            Individual clonedIndividual = individuals.get(i).clone();
            kb.individuals.put(i, clonedIndividual);
        }

		// Cloner nominal nodes
		kb.labelsWithNodes = new Hashtable<String, Set<String>>(labelsWithNodes);
		
		// Clone milp
		kb.milp = milp.clone();

		// Clone blocking
		kb.blockedAssertions = new Hashtable<String, ArrayList<Assertion>>(blockedAssertions);
		kb.blockedExistAssertions = new Hashtable<String, ArrayList<Assertion>>(blockedExistAssertions);
		kb.directlyBlockedChildren = new Hashtable<String, ArrayList<String>>(directlyBlockedChildren);
		kb.numDefinedConcepts = numDefinedConcepts;
		kb.numDefinedInds = numDefinedInds;
		kb.rSuccessors = new Hashtable<String, ArrayList<String>>(rSuccessors);		
		kb.xprimeIndivs = new Hashtable<String, ArrayList<String>>(xprimeIndivs);
		kb.yprimeIndivs = new Hashtable<String, ArrayList<String>>(yprimeIndivs);

		// Clone statistics
		kb.maxDepth = maxDepth;
		kb.numAssertions = numAssertions;
		kb.numRelations = numRelations;
		kb.old01Variables = old01Variables;
		kb.oldBinaryVariables = oldBinaryVariables;
		kb.rulesApplied = rulesApplied.clone();

		return kb;
	}


	/**
	 * Gets a copy of a knowledge base except the ABox.
	 * @return A copy of the knowledge base.
	 */
	public KnowledgeBase cloneWithoutABox()
	{
		KnowledgeBase kb = new KnowledgeBase();

		kb.ABOX_EXPANDED = ABOX_EXPANDED;
		kb.abstractRoles = new HashSet<String>(abstractRoles);
		kb.acyclicTbox = acyclicTbox;
		kb.appliedTransRoleRules = new ArrayList<String>(appliedTransRoleRules);
		kb.atomicConcepts = new Hashtable<String, Concept>(atomicConcepts);
		kb.axiomsAequivC = new Hashtable<String, HashSet<Concept>>(axiomsAequivC);
		for(String name : axiomsAequivC.keySet())
			kb.axiomsAequivC.put(name, new HashSet<Concept>(axiomsAequivC.get(name)) );
		kb.axiomsAisaB = new Hashtable<String, HashSet<PrimitiveConceptDefinition>>(axiomsAisaB);
		kb.axiomsAisaC = new Hashtable<String, HashSet<PrimitiveConceptDefinition>>(axiomsAisaC);
		kb.axiomsCequivD = new ArrayList<ConceptEquivalence> (axiomsCequivD);
		kb.axiomsCisaA = new Hashtable<String, HashSet<GeneralConceptInclusion>>(axiomsCisaA);
		kb.axiomsCisaD = new Hashtable<String, HashSet<GeneralConceptInclusion>>(axiomsCisaD);
		kb.blockingDynamic = blockingDynamic;
		kb.blockingType = blockingType;
		kb.CLASSIFIED = CLASSIFIED;
		kb.conceptIndividualList =  new Hashtable<Integer, TreeSet<CreatedIndividual>>(conceptIndividualList);
		kb.concreteConcepts = new Hashtable<String, FuzzyConcreteConcept>(concreteConcepts);
		kb.concreteFeatures = new Hashtable<String, ConcreteFeature>(concreteFeatures);
		kb.concreteFuzzyConcepts = concreteFuzzyConcepts;
		kb.concreteRoles = new HashSet<String>(concreteRoles);
		kb.disjointVariables = new Hashtable<String, HashSet<String>>(disjointVariables);
		kb.domainRestrictions = new Hashtable<String, HashSet<Concept>>(domainRestrictions);
		kb.existAssertions = new ArrayList<Assertion>(existAssertions);
		kb.funcRoles = new HashSet<String>(funcRoles);
		kb.fuzzyNumbers = new Hashtable<String, TriangularFuzzyNumber>(fuzzyNumbers);
		kb.invFuncRoles = new HashSet<String>(invFuncRoles);
		kb.invRoles = new Hashtable<String, Set<String>>(invRoles);
		kb.KB_LOADED = KB_LOADED;
		kb.KB_UNSAT = KB_UNSAT;
		kb.language = language;
		kb.lazyUnfoldable = lazyUnfoldable;
		kb.milp.showVars = milp.showVars.clone();
		kb.modifiers = new Hashtable<String, Modifier>(modifiers);
		kb.numberOfConcepts = new Hashtable<String, Integer>(numberOfConcepts);
		kb.numberOfRoles = new Hashtable<String, Integer>(numberOfRoles);
		kb.order = new Hashtable<String, Integer>(order);
		kb.positiveConcreteValueAssertions = new ArrayList<Assertion>(positiveConcreteValueAssertions);
		kb.processedAssertions = new HashSet<Integer>(processedAssertions);
		kb.rangeRestrictions = new Hashtable<String, HashSet<Concept>>(rangeRestrictions);
		kb.reflexiveRoles = new HashSet<String>(reflexiveRoles);
		kb.rolesWithAllParents = new Hashtable<String, Hashtable<String, Double>>(rolesWithAllParents);
		kb.rolesWithParents = new Hashtable<String, Hashtable<String, Double>>(rolesWithParents);
		kb.rolesWithTransChildren = new Hashtable<String, ArrayList<String>>(rolesWithTransChildren);
		kb.ruleAcyclicTbox = ruleAcyclicTbox;
		kb.showLanguage = showLanguage;
		kb.similarityRels = new HashSet<String>(similarityRels);
		kb.subFlags = new HashMap<String, HashMap<String, Double>> (subFlags); 
		kb.symmetricRoles = new HashSet<String>(symmetricRoles);
		kb.tDef = new Hashtable<String, Concept>(tDef);
		kb.tDis = new Hashtable<String, HashSet<String>>(tDis);
		kb.tempRelationsList = new Hashtable<String, ArrayList<Relation>>(tempRelationsList);
		kb.tG = new ArrayList<GeneralConceptInclusion>(tG);
		kb.tInc = new Hashtable<String, HashSet<PrimitiveConceptDefinition>>(tInc);
		kb.transRoles = new HashSet<String>(transRoles);
		kb.tSyn = new Hashtable<String, HashSet<String>>(tSyn);

		return kb;
	}





	/**
	 * Saves a fuzzy KB into a text file.
	 * @param fileName Name of the output fie.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public void saveToFile(String fileName) throws FuzzyOntologyException
	{
		File file = null;
		try
		{
			PrintStream p;
			if (fileName != null) 
			{
				file = new File(fileName);
				FileOutputStream fos = new FileOutputStream(file);
				p = new PrintStream(fos);
			}
			else
			{
				p = System.out;
			}

			// Fuzzy logic
			p.println("(define-fuzzy-logic " + semantics + " )");

			// Fuzzy concrete concepts
			for (Concept c : concreteConcepts.values())
			{
				if (c instanceof CrispConcreteConcept)
				{
					CrispConcreteConcept cfc = (CrispConcreteConcept) c;
					p.println("(define-fuzzy-concept " + c.name + " " + cfc.getName() + " )");
				}
				else if (c instanceof LeftConcreteConcept)
				{
					LeftConcreteConcept cfc = (LeftConcreteConcept) c;
					p.println("(define-fuzzy-concept " + c.name + " " + cfc.getName() + " )");
				}
				else if (c instanceof RightConcreteConcept)
				{
					RightConcreteConcept cfc = (RightConcreteConcept) c;
					p.println("(define-fuzzy-concept " + c.name + " " + cfc.getName() + " )");
				}
				else if (c instanceof TriangularConcreteConcept)
				{
					TriangularConcreteConcept cfc = (TriangularConcreteConcept) c;
					p.println("(define-fuzzy-concept " + c.name + " " + cfc.getName() + " )");
				}
				else if (c instanceof TrapezoidalConcreteConcept)
				{
					TrapezoidalConcreteConcept cfc = (TrapezoidalConcreteConcept) c;
					p.println("(define-fuzzy-concept " + c.name + " " + cfc.getName() + " )");
				}
			}

			// Modifiers
			for (Modifier mod : modifiers.values())
			{
				p.println("(define-modifier " + mod + " " + mod.getName() + " )");			
			}

			// Features
			for (ConcreteFeature f : concreteFeatures.values())
			{
				String name = f.getName();
				p.println("(functional " + name + ")");

				int type = f.getType();
				switch (type)
				{
					 case ConcreteFeature.STRING:
						p.println("(range " + name + " *string*)");
					 	break;
					 	
					 case ConcreteFeature.INTEGER:

					 	Integer k1 = (Integer) f.getK1();
					 	Integer k2 = (Integer) f.getK2();
					 	p.println("(range " + name + " *integer* " + k1 + " " + k2 + ")" );
					 
					 	break;
					 	
					 // case ConcreteFeature.REAL:
					 default:

					 	Double kd1 = 1.0 * (Double) f.getK1();
					 	Double kd2 = 1.0 * (Double) f.getK2();
					 	p.println("(range " + name + " *real* " + kd1 + " " + kd2 + ")" );
				}
			}

			// ABox
			for(Assertion ass : assertions)
			{
				String deg = degreeIfNotOne(ass.getLowerLimit());
				if (! deg.contains(":"))
					p.println( "(instance " + ass.getIndividual().toString() + " " + ass.getConcept().toString() + " " + deg + ")" );
			}

			for (Individual ind : individuals.values() )
			{
				for (ArrayList<Relation> a : ind.roleRelations.values())
					for (Relation rel : a)
					{
						String deg = degreeIfNotOne(rel.getDegree());
						if (! deg.contains(":"))
							p.println( "(related " + ind + " " + rel.getObjectIndividual() + " " + rel.getRoleName() + " " + deg + ")" );
					}
			}

			// TBox
			if (KB_LOADED)
				saveAbsorbedTBoxToFile(p);
			else
				saveTBoxToFile(p);

			// RBox
			for(String r : reflexiveRoles)
				p.println( "(reflexive " + r + ")" );

			for(String r : symmetricRoles)
				p.println( "(symmetric " + r + ")" );

			for(String r : transRoles)
				p.println( "(transitive " + r + ")" );

			for (String r : invRoles.keySet())
			{
				Set<String> inv = invRoles.get(r);
				if (inv != null)
					for(String s : inv)
						p.println( "(inverse " + r + " " + s + ")" );
			}

			for (String r : rolesWithParents.keySet())
			{
				Hashtable<String, Double> par = rolesWithParents.get(r);
				if (par != null)
					for(String s : par.keySet())
						p.println( "(implies-role " + r + " " + s + " " + degreeIfNotOne(par.get(s).doubleValue()) + ")" );
			}

			for(String r : funcRoles)
			{
				if (concreteFeatures.keySet().contains(r) == false)
					p.println( "(functional " + r + ")" );
			}

		}
		catch (Exception e) {
			Util.error("Error writing to the file " + fileName);
		}
		
	}


	private void saveAbsorbedTBoxToFile(PrintStream p)
	{	
	 	for(String atomicConcept : tInc.keySet())
			for (PrimitiveConceptDefinition pcd : tInc.get(atomicConcept))
			{
				Concept c = pcd.getDefinition();
				p.println( "(define-primitive-concept " + atomicConcept + " " + c + " " + degreeIfNotOne(pcd.getDegree()) + ")" );
			}

		for (String atomicConcept : tDef.keySet())
			p.println( "(equivalent-concepts " + atomicConcept + " " + tDef.get(atomicConcept) + ")" );

		for (String atomicConcept : tSyn.keySet())
			for (String c : tSyn.get(atomicConcept))
				p.println( "(equivalent-concepts " + atomicConcept + " " + c + ")" );

		for (GeneralConceptInclusion gci : tG)
			switch (gci.getType())
			{
				case GeneralConceptInclusion.LUKASIEWICZ:
					p.println( "(l-implies " + gci.getSubsumed() + " " + gci.getSubsumer() + degreeIfNotOne(gci.getDegree()) + ")" );
					break;

				case GeneralConceptInclusion.GOEDEL:
					p.println( "(g-implies " + gci.getSubsumed() + " " + gci.getSubsumer() + degreeIfNotOne(gci.getDegree()) + ")" );
					break;

				case GeneralConceptInclusion.KLEENE_DIENES:
					p.println( "(kd-implies " + gci.getSubsumed() + " " + gci.getSubsumer() + degreeIfNotOne(gci.getDegree()) + ")" );
					break;

				case GeneralConceptInclusion.ZADEH:
					p.println( "(implies " + gci.getSubsumed() + " " + gci.getSubsumer() + degreeIfNotOne(gci.getDegree()) + ")" );
					break;
			}

		saveTBoxCommonPartToFile(p);

	}


	private void saveTBoxToFile(PrintStream p)
	{
		for(String atomicConcept : axiomsAequivC.keySet())
			for (Concept c : axiomsAequivC.get(atomicConcept))
				p.println( "(define-concept " + atomicConcept + " " + c + ")" );

		for(String atomicConcept : axiomsAisaB.keySet())
			for (PrimitiveConceptDefinition pcd : axiomsAisaB.get(atomicConcept))
			{
				Concept c = pcd.getDefinition();
				p.println( "(define-primitive-concept " + atomicConcept + " " + c + " " + degreeIfNotOne(pcd.getDegree()) + ")" );
			}

		for(String atomicConcept : axiomsAisaC.keySet())
			for (PrimitiveConceptDefinition pcd : axiomsAisaC.get(atomicConcept))
			{
				Concept c = pcd.getDefinition();
				p.println( "(define-primitive-concept " + atomicConcept + " " + c + " " + degreeIfNotOne(pcd.getDegree()) + ")" );
			}

		for(HashSet<GeneralConceptInclusion> gcis: axiomsCisaD.values() )
			for (GeneralConceptInclusion gci : gcis)
			{
				switch (gci.getType())
				{
					case GeneralConceptInclusion.LUKASIEWICZ:
						p.println( "(l-implies " + gci.getSubsumed() + " " + gci.getSubsumer() + degreeIfNotOne(gci.getDegree()) + ")" );
						break;

					case GeneralConceptInclusion.GOEDEL:
						p.println( "(g-implies " + gci.getSubsumed() + " " + gci.getSubsumer() + degreeIfNotOne(gci.getDegree()) + ")" );
						break;

					case GeneralConceptInclusion.KLEENE_DIENES:
						p.println( "(kd-implies " + gci.getSubsumed() + " " + gci.getSubsumer() + degreeIfNotOne(gci.getDegree()) + ")" );
						break;

					case GeneralConceptInclusion.ZADEH:
						p.println( "(implies " + gci.getSubsumed() + " " + gci.getSubsumer() + degreeIfNotOne(gci.getDegree()) + ")" );
						break;
				}
		}

		for(HashSet<GeneralConceptInclusion> gcis: axiomsCisaA.values() )
			for (GeneralConceptInclusion gci : gcis)
			{
				switch (gci.getType())
				{
					case GeneralConceptInclusion.LUKASIEWICZ:
						p.println( "(l-implies " + gci.getSubsumed() + " " + gci.getSubsumer() + degreeIfNotOne(gci.getDegree()) + ")" );
						break;

					case GeneralConceptInclusion.GOEDEL:
						p.println( "(g-implies " + gci.getSubsumed() + " " + gci.getSubsumer() + degreeIfNotOne(gci.getDegree()) + ")" );
						break;

					case GeneralConceptInclusion.KLEENE_DIENES:
						p.println( "(kd-implies " + gci.getSubsumed() + " " + gci.getSubsumer() + degreeIfNotOne(gci.getDegree()) + ")" );
						break;

					case GeneralConceptInclusion.ZADEH:
						p.println( "(implies " + gci.getSubsumed() + " " + gci.getSubsumer() + degreeIfNotOne(gci.getDegree()) + ")" );
						break;
				}
		}

		for(ConceptEquivalence ce : axiomsCequivD)
			p.println( "(equivalent-concepts " + ce.getC1() + " " + ce.getC2() + ")" );

		saveTBoxCommonPartToFile(p);
	}


	private void saveTBoxCommonPartToFile(PrintStream p)
	{
		for(String a : tDis.keySet()) 
		{
			for (String disjC : tDis.get(a))
				if (a.compareTo(disjC) < 0)
					p.println("(disjoint " + a + " " + disjC + " )");
		}
	
		for(String role : domainRestrictions.keySet())
			for (Concept c : domainRestrictions.get(role))
				p.println( "(domain " + role + " " + c + ")" );
	
		for(String role : rangeRestrictions.keySet())
			for (Concept c : rangeRestrictions.get(role))
				p.println( "(range " + role + " " + c + ")" );
	}

	/**
	 * Adds a individual to the KB.
	 * @param indName Name of the individual.
	 * @param ind Individual to be added.
	 */
	private void addIndividual(String indName, Individual ind) throws InconsistentOntologyException
	{
		individuals.put(indName, ind);
		if (isLoaded())
		{
			solveGCI(ind);
			solveReflexiveRoles(ind);
		}
	}


	/**
	 * Adds a created individual to the KB.
	 * @param indName Name of the individual.
	 * @param ind Individual to be added.
	 */
	private void addCreatedIndividual(String indName, CreatedIndividual ind) throws InconsistentOntologyException
	{
		individuals.put(indName, ind);	
		if (isLoaded() && (ind.isConcrete() == false))
		{
			solveGCI(ind);
			solveReflexiveRoles(ind);
		}
	}


	/**
	 * Gets an individual with the indicated name (creating it if necessary).
	 * @param indName Name of the individual.
	 * @return Individual with the given name.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public Individual getIndividual(String indName) throws InconsistentOntologyException
	{
		if(checkIndividualExists(indName))
			return individuals.get(indName);
		else
		{
			Individual a = new Individual(indName);
			addIndividual(indName, a);
			return a;
		}
	}


	/**
	 * Checks if there exists an individual with the given name.
	 * @param indName Name of the individual.
	 * @return true if there exists an individual with the given name; false otherwise.
	 */
	boolean checkIndividualExists(String indName)
	{
		boolean exists = true;
		if( individuals.isEmpty() ||  (! individuals.containsKey(indName)) )
			exists = false;
		return exists;
	}


	/**
	 * Adds a fuzzy concept to the array of concepts in the fuzzy KB.
	 * @param conceptName Name of the concept.
	 * @param conc Fuzzy concept.
	 */
	public void addConcept(String conceptName, FuzzyConcreteConcept conc)
	{
        if (abstractRoles.contains(conceptName) || concreteRoles.contains(conceptName) )
            Util.println("Warning: " + conceptName + " is the name of both a concept and a role.");

		concreteConcepts.put(conceptName, conc);
	}


	/**
	 * Gets a concept with indicated name.
	 * @param name Name of the concept.
	 * @return A concept with the given name.
	 */
	public Concept getConcept(String name)
	{
		Concept c = atomicConcepts.get(name);
		if (c != null)
			return c;
		else
		{
			c = concreteConcepts.get(name);
			if (c != null)
				return c;
			else
			{
                if (abstractRoles.contains(name) || concreteRoles.contains(name) )
                    Util.println("Warning: " + name + " is the name of both a concept and a role.");
				c = new Concept(name);
				atomicConcepts.put(name, c);		
				return c;
			}
		}
	}


	/**
	 * Adds a fuzzy number to the fuzzy KB.
	 * @param fName Name of the fuzzy number.
	 * @param f Fuzzy number.
	 */
	public void addFuzzyNumber(String fName, TriangularFuzzyNumber f)
	{
		addConcept(fName, f);
		fuzzyNumbers.put(fName, f);
	}


	/**
	 * Checks if there exists a fuzzy number with the indicated name.
	 * @param concName Name of the fuzzy number.
	 * @return true if there exists a fuzzy number with the given name; false otherwise.
	 */
	public boolean checkFuzzyNumberConceptExists(String concName)
	{
		if (!concreteConcepts.containsKey(concName))
			return false;
		Concept c = concreteConcepts.get(concName);
		return (c.getType() == Concept.FUZZY_NUMBER);
	}


	/**
	 * Adds a fuzzy modifier to the fuzzy KB.
	 * @param modName Name of the fuzzy modifier.
	 * @param mod Modifier.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public void addModifier(String modName, Modifier mod) throws FuzzyOntologyException
	{
		if(modifiers.containsKey(modName))
			Util.error("Error : " + modName + " modifier is already defined");
		else
		  modifiers.put(modName,mod);
	}


	/**
	 * Adds a list of fuzzy assertions.
	 * @param listOfAssertions A list of fuzzy assertions.
	 */
	void addAssertions(ArrayList<Assertion> listOfAssertions)
	{
		assertions.addAll(listOfAssertions);
	}


	/**
	 * Adds a fuzzy assertion.
	 * @param newAss A fuzzy assertion. 
	 */
	private void addAssertion(Assertion newAss)
	{
//		System.out.println(newAss);
		
		Degree deg = newAss.getLowerLimit();
		if (deg.isNumeric() && deg.isNumberZero())
			return;

		if (isAssertionProcessed(newAss) )
		{
			Util.println("\n	 Assertion (without the degree): " + newAss + " already processed ");
			// Add xNewAss >= lowerBound
			milp.addNewConstraint(newAss);
		}
		else
		{
			Util.println("\n	 Adding assertion: " + newAss);
			numAssertions++;
			assertions.add(newAss);
			Concept c = newAss.getConcept();
			Individual ind = newAss.getIndividual();				
			
			if ( (c.getType() != Concept.TOP) && (ind.isBlockable()) )
			{
				int aux = getNumberFromConcept(c.toString());
				((CreatedIndividual) ind).conceptList.add(aux);

				((CreatedIndividual) ind).directlyBlocked = CreatedIndividual.UNCHECKED;
				Util.println("	 Mark node.directlyBlocked = " + ind.name + " as unchecked ");

				addIndividualToConcept(aux, ind);	
			}
		}
	}


	/**
	 * Add the individual a to the individual list of the concept.
	 * @param conceptID Concept numerical ID.
	 * @param a Individual
	 */
	public void addIndividualToConcept(int conceptID, Individual a)
	{
		// Add only if created individual
		if ( (a instanceof CreatedIndividual) )
		{
			TreeSet<CreatedIndividual> individualList = conceptIndividualList.get(conceptID);
			if ( individualList == null)
				individualList = new TreeSet<CreatedIndividual>(new IndividualComparator());

			individualList.add((CreatedIndividual) a);
			conceptIndividualList.put(conceptID, individualList);
			Util.println("------------->  List of Ind for C >> ID: " + conceptID + " descr : " + getConceptFromNumber(conceptID) +  " : " + conceptIndividualList.get(conceptID));	
		}
	}


	/**
	 * Adds a fuzzy relation of the form (indA, indB, funcRole, degree)
	 * @param indA A subbject individual.
	 * @param role An abstract role.
	 * @param indB An object individual.
	 * @param degree Lower bound for the degree.
	 * @return Added relation.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public Relation addRelation(Individual indA, String role, Individual indB, Degree degree) throws InconsistentOntologyException
	{
		abstractRoles.add(role);
		Relation rel = indA.addRelation(role, indB, degree, this);
		if (isLoaded() && funcRoles.contains(role))
			mergeFillers(indA, role);
		return rel;
	}


	/**
	 * Adds a fuzzy synonyme definition.
	 * @param conceptName1 Name of an atomic fuzzy concept.
	 * @param conceptName2 Name of another atomic fuzzy concept.
	 */
	private void defineSynonym(String conceptName1, String conceptName2)
	{
		//Util.println("def syn : " + conceptName1 + " = " + conceptName2 );
		HashSet<String> def = tSyn.get(conceptName1);
		if (def == null)
			def = new HashSet<String>();
		def.add(conceptName2);
		tSyn.put(conceptName1, def);
		getConcept(conceptName1);
//		tSynCount++;
	}


	/**
	 * Adds a fuzzy synonyme definition.
	 * @param conceptName1 Name of an atomic fuzzy concept.
	 * @param conceptName2 Name of another atomic fuzzy concept.
	 */
	private void defineSynonyms(String conceptName1, String conceptName2)
	{
		defineSynonym(conceptName1, conceptName2);
		defineSynonym(conceptName2, conceptName1);
//		tSynCount--;
	}


	/**
	 * Adds a fuzzy concept definition.
	 * @param conceptName Name of an atomic fuzzy concept (defined).
	 * @param conc A fuzzy concept (definition).
	 */
	public void defineConcept(String conceptName, Concept conc) 
	{
		// Declares the atomic concept
		getConcept(conceptName);

		if (ConfigReader.OPTIMIZATIONS != 0)
		{
			if (conceptName.equals(conc.toString()) )
				return;
			else
			{
				if (conc.getType() == Concept.ATOMIC)
				{
					defineSynonyms(conceptName, conc.toString());
					return;
				}
			}
		}

		// Add to axiomsCequivD	
		try {
			addAxiomToAequivC(conceptName, conc);
		} catch (FuzzyOntologyException e) { }
	}


	private Hashtable<String,Integer> getAtC()
	{
		Hashtable<String,Integer> atC = new Hashtable<String,Integer> ();
		int size = 0;
		for (String e : atomicConcepts.keySet())
			atC.put(e, size++);
		return atC;
	}


	/**
	 * We return true if we know that htere are cycles because of tSyn.
	 * False does not mean that there are no cycles!
	 */
	private boolean addTDefLinks(Digraph g, Hashtable<String,Integer> atC, boolean useTdr)
	{
		for (String a : tDef.keySet())
		{
			int v1 = atC.get(a);
			Concept c = tDef.get(a);
			for (Concept b : c.getAtomicConcepts())
			{
				String bName = b.toString();
				Set<String> set = tSyn.get(a) ;
				if ( (set != null) && set.contains(bName))
					return true;

				int v2 = atC.get(bName);
				g.addEdge(v1, v2);
			}
			
			// Consider domain and range axioms
			if (useTdr)
				if (addTdrLinks(g, atC, c.getRoles(), v1))
					return true;
		}
		return false;
	}


	/**
	 * We return true if we know that there are cycles because of tSyn.
	 * False does not mean that there are no cycles!
	 */
	private boolean addTIncLinks(Digraph g, Hashtable<String,Integer> atC, boolean useTdr)
	{
		for (String a : tInc.keySet())
		{
			int v1 = atC.get(a);
			for (PrimitiveConceptDefinition pcd : tInc.get(a))
			{
				Concept c = pcd.getDefinition();
				for (Concept b : c.getAtomicConcepts())
				{
					String bName = b.toString();
					Set<String> set = tSyn.get(a) ;
					if ( (set != null) && set.contains(bName))
						return true;

					int v2 = atC.get(bName);
					g.addEdge(v1, v2);
				}

				// Consider domain and range axioms
				if (useTdr)
					if (addTdrLinks(g, atC, c.getRoles(), v1))
						return true;
			}
		}
		return false;
	}


	/**
	 * We return true if we know that there are cycles because of tSyn.
	 * False does not mean that there are no cycles!
	 */
	private boolean addTdrLinks(Digraph g, Hashtable<String,Integer> atC, HashSet<String> usedRoles, int v)
	{
		HashSet<String> rolesToBeChecked = new HashSet<String> (usedRoles); 
		for (String usedRole : usedRoles)
		{
			rolesToBeChecked.add(usedRole);
			Hashtable<String, Double> parents = rolesWithAllParents.get(usedRole);
			if (parents != null)
				rolesToBeChecked.addAll(parents.keySet());
		}

		for (String s : rolesToBeChecked)
		{
			Set<Concept> restrictions = new HashSet<Concept> ();
			Set<Concept> aux = domainRestrictions.get(s);
			if (aux != null)
				restrictions.addAll(aux);
			aux = rangeRestrictions.get(s);
			if (aux != null)
				restrictions.addAll(aux);

			for (Concept d : restrictions)
				for (Concept usedConcept : d.getAtomicConcepts())
				{
					Set<String> set = tSyn.get(d.toString()) ;
					if ( (set != null) && set.contains(usedConcept.toString()))
						return true;

					// Add link to graph
					int w = atC.get(usedConcept.toString());
					g.addEdge(v, w);
				}					
		}
		
		return false;
	}


	// Check if tInc \cup tDef is acyclic
	private boolean isTBoxAcyclic()
	{
		// Application mapping every atomic concept into an integer number
		Hashtable<String,Integer> atC = getAtC();
		Digraph g = getDigraph(atC);

		// Add links to the graph because of tInc and tDef
		if (addTIncLinks(g, atC, true))
			return false;

		if (addTDefLinks(g, atC, true))
			return false;

		// Check whether the graph has a cycle
		DirectedCycleFinder d = new DirectedCycleFinder(g);
		return ! d.hasCycle();
	}


	/**
	 * Adds an atomic fuzzy concept definition.
	 * @param conceptName Name of an atomic fuzzy concept (defined).
	 * @param conc A fuzzy concept (definition).
	 * @param implication A fuzzy implication.
	 * @param n Degree of truth.
	 */
	public void defineAtomicConcept(String conceptName, Concept conc, int implication, double n)
	{
		// Declares the atomic concept
		getConcept(conceptName);

		if ((n==1) && !(implication==GeneralConceptInclusion.KLEENE_DIENES))
			implication = GeneralConceptInclusion.LUKASIEWICZ;

		// Redundant elimination
		if (!isRedundantAisaC(conceptName, conc, implication, n) )
		{
			HashSet<PrimitiveConceptDefinition> hs;
			if (conc.isAtomic())
				hs = axiomsAisaB.get(conceptName);
			else
				hs = axiomsAisaC.get(conceptName);
			if (hs == null)
				hs = new HashSet<PrimitiveConceptDefinition>();

			PrimitiveConceptDefinition def = new PrimitiveConceptDefinition(conceptName, conc, implication, n);

			//if (hs.contains(def))
			//	return;

			hs.add(def);
			
			if (conc.isAtomic())
				axiomsAisaB.put(conceptName, hs);
			else
				axiomsAisaC.put(conceptName, hs);
		}
	}


	private void gciTranformDefineAtomicConcept(String conceptName, Concept conc, int implication, double n)
	{
		// Declares the atomic concept
		getConcept(conceptName);

		// Redundant elimination
		if (!isRedundantAisaC(conceptName, conc, implication, n) )
		{
			HashSet<PrimitiveConceptDefinition> hs;
			if (conc.isAtomic())
				hs = axiomsAisaB.get(conceptName);
			else
				hs = axiomsToDoTmpAisaC.get(conceptName);
			if (hs == null)
				hs = new HashSet<PrimitiveConceptDefinition>();

			PrimitiveConceptDefinition def = new PrimitiveConceptDefinition(conceptName, conc, implication, n);

			//if (hs.contains(def))
			//	return;

			hs.add(def);

			if (conc.isAtomic())
				axiomsAisaB.put(conceptName, hs);
			else
				axiomsToDoTmpAisaC.put(conceptName, hs);
		}
	}



	/**
	 * Checks if A => C redundant
	 * @param conceptName Name of an atomic fuzzy concept (defined).
	 * @param conc A fuzzy concept (definition).
	 * @param implication A fuzzy implication.
	 * @param n Degree of truth.
	 */
	private boolean isRedundantAisaC(String conceptName, Concept conc, int implication, double n)
	{
		if (conc.getType() == Concept.TOP)
//		{
//			redundantAisCCount++;
			return true;
//		}

		if ((conc.toString().equals(conceptName) && (implication != GeneralConceptInclusion.KLEENE_DIENES)))
//		{
//			redundantAisCCount++;
			return true;
//		}

		if ( (conc.getType() == Concept.OR) || (conc.getType() == Concept.G_OR) || (conc.getType() == Concept.L_OR))
		{
			for (Concept ci : conc.concepts)
			{
				if (ci.toString().equals(conceptName))
//				{
//					redundantAisCCount++;	
					return true;
//				}
			}
		}

		return false;
	}


	/**
	 * Checks if C => A redundant
	 * @param conceptName Name of an atomic fuzzy concept (defined).
	 * @param conc A fuzzy concept (definition).
	 * @param implication A fuzzy implication.
	 * @param n Degree of truth.
	 */
/*	private boolean redundantCisaA(String conceptName, Concept conc, int implication, double n)
	{
		if (conc.getType() == Concept.BOTTOM)
			return true;

		if ((conc.toString().equals(conceptName) && (implication != GeneralConceptInclusion.KLEENE_DIENES)))
			return true;

		if ( (conc.getType() == Concept.AND) || (conc.getType() == Concept.G_AND) || (conc.getType() == Concept.L_AND))
		{
			for (Concept ci : conc.concepts)
			{
				if (ci.toString().equals(conceptName))
				return true;
			}
		}
		return false;
	}
*/

	
	void setUnsatisfiableKB()
	{
		KB_UNSAT = true;
		milp.addContradiction();
	}


	/**
	 * Checks if C => D redundant
	 * @param C Subsumed concept.
	 * @param D Subsumer concept.
	 * @param implication A fuzzy implication.
	 * @param n Degree of truth.
	 */
	private boolean isRedundantGCI(Concept C, Concept D, int implication, double n) throws InconsistentOntologyException
	{
		if (D.getType() == Concept.TOP)
//		{
//			redundantGCICount++;
			return true;
//		}

		if (C.getType() == Concept.BOTTOM)
//		{
//			redundantGCICount++;
			return true;
//		}

		if ( (C.getType() == Concept.TOP) && (D.getType() == Concept.BOTTOM) ) 
		{
			setUnsatisfiableKB();
			throw new InconsistentOntologyException("Unsatisfiable fuzzy KB");
		}
		
		if ((C.toString().equals(D.toString()) && (implication != GeneralConceptInclusion.KLEENE_DIENES)))
//		{
//			redundantGCICount++;
			return true;
//		}
		
		if ( (implication != GeneralConceptInclusion.KLEENE_DIENES) &&
			 ((D.getType() == Concept.OR) || (D.getType() == Concept.G_OR) || (D.getType() == Concept.L_OR))
		   )
		{
			for (Concept ci : D.concepts)
			{
				if (ci.toString().equals(C.toString()))
//				{
//					redundantGCICount++;
					return true;
//				}
			}
		}

		if (  (implication != GeneralConceptInclusion.KLEENE_DIENES) &&
		  ((C.getType() == Concept.AND) || (C.getType() == Concept.G_AND) || (C.getType() == Concept.L_AND))
		   )
		{
			for (Concept ci : C.concepts)
			{
				if (ci.toString().equals(D.toString()))
//				{
//					redundantGCICount++;
					return true;
//				}
			}
		}

		return false;
	}


	/**
	 * Absorbs synonyms in axiomsAisaB.
	 * @return true if there are changes; false otherwise.
	 */
	private boolean synonymAbsorptionAisaB(PrimitiveConceptDefinition pcd1)
	{
		//Util.println("------ Try synonym absorption : " + pcd1);
		String a = pcd1.getDefinedConcept();
		Concept conc = pcd1.getDefinition();
		int implication = pcd1.getType();
		double n = pcd1.getDegree();

		if ( conc.isAtomic() && 
			! conc.toString().equals(a) &&	(
			(semantics == FuzzyLogic.CLASSICAL) || 
			( (n == 1) && (implication != GeneralConceptInclusion.KLEENE_DIENES) ) ) 
		)
		{
			String b = conc.toString();										
			// Look for  (b => a >= 1), remove it and create a synonym definition
			// We need to search in both sets below			
			HashSet<PrimitiveConceptDefinition> hs2 = axiomsAisaB.get(b);
			HashSet<PrimitiveConceptDefinition> hs3 = tInc.get(b);										
	
			if (hs2 != null)
				for (PrimitiveConceptDefinition pcd2 : hs2)
				{
					if (pcd2.getDefinition().toString().equals(a) && (
							(semantics == FuzzyLogic.CLASSICAL) || 
							( (pcd2.getDegree() == 1) && (pcd2.getType() != GeneralConceptInclusion.KLEENE_DIENES) ) ) 
					)
					{
						// Synonym definition
						defineSynonyms(a, b);
	
						// Remove A isa B
						removeAisaB(a, pcd1);
						removeAisaB(b, pcd2);

						Util.println("------ Synonym absorption from   axiomsAisaB : " + a + " = " + b);
						//absorptionCountSyn++;
						return true;
					}
				}
	
			if (hs3 != null)
				for (PrimitiveConceptDefinition pcd3 : hs3)
				{
					if (pcd3.getDefinition().toString().equals(a) && (
							(semantics == FuzzyLogic.CLASSICAL) ||
							( (pcd3.getDegree() == 1) && (pcd3.getType() != GeneralConceptInclusion.KLEENE_DIENES) ) )
					)
					{
						// Synonym definition
						defineSynonyms(a, b);

						// Remove A isa C
						removeAisaB(a, pcd1);
						hs3.remove(pcd3);
						if (hs3.isEmpty())
							tInc.remove(b);
	
						Util.println("------ Synonym absorption from tInc : " + a + " = " + b);
						//absorptionCountSyn++;
						return true;
					}
				}
		}

		return false;
	}


	/**
	 * Absorbs synonyms in axiomsToDoAisaB. note that A => B is in tInc.
	 * @return true if there are changes; false otherwise.
	 */
	private boolean synonymAbsorptionToDoAisaB(PrimitiveConceptDefinition pcd1)
	{
		//Util.println("------ Try synonym absorption from ToDo list : " + pcd1);
		String a = pcd1.getDefinedConcept();
		Concept conc = pcd1.getDefinition();
		int implication = pcd1.getType();
		double n = pcd1.getDegree();

		if ( conc.isAtomic() &&
			! conc.toString().equals(a) &&	(
			(semantics == FuzzyLogic.CLASSICAL) ||
			( (n == 1) && (implication != GeneralConceptInclusion.KLEENE_DIENES) ) )
		)
		{
			String b = conc.toString();
			// Look for  (b => a >= 1), remove it and create a synonym definition
			// We need to search in both sets below
			HashSet<PrimitiveConceptDefinition> hs2 = axiomsAisaB.get(b);
			HashSet<PrimitiveConceptDefinition> hs3 = tInc.get(b);
			HashSet<PrimitiveConceptDefinition> hs4 = axiomsToDoAisaB.get(b);

			if (hs2 != null)
				for (PrimitiveConceptDefinition pcd2 : hs2)
				{
					if (pcd2.getDefinition().toString().equals(a) && (
							(semantics == FuzzyLogic.CLASSICAL) ||
							( (pcd2.getDegree() == 1) && (pcd2.getType() != GeneralConceptInclusion.KLEENE_DIENES) ) )
					)
					{
						// Synonym definition
						defineSynonyms(a, b);

						// Remove A isa B from tInc
						//removetIncAisaB(a, pcd1);
						removeAisaX(a, pcd1, tInc);
						removeAisaB(b, pcd2);

						Util.println("------ Synonym absorption from   axiomsAisaB : " + a + " = " + b);
						//absorptionCountSyn++;
						return true;
					}
				}

			if (hs3 != null)
				for (PrimitiveConceptDefinition pcd3 : hs3)
				{
					if (pcd3.getDefinition().toString().equals(a) && (
							(semantics == FuzzyLogic.CLASSICAL) ||
							( (pcd3.getDegree() == 1) && (pcd3.getType() != GeneralConceptInclusion.KLEENE_DIENES) ) )
					)
					{
						// Synonym definition
						defineSynonyms(a, b);

						// Remove A isa B
						removeAisaX(a, pcd1, tInc);
						removeAisaX(b, pcd3, tInc);

						Util.println("------ Synonym absorption from tInc : " + a + " = " + b);
						//absorptionCountSyn++;
						return true;
					}
				}

			if (hs4 != null)
				for (PrimitiveConceptDefinition pcd4 : hs4)
				{
					if (pcd4.getDefinition().toString().equals(a) && (
							(semantics == FuzzyLogic.CLASSICAL) ||
							( (pcd4.getDegree() == 1) && (pcd4.getType() != GeneralConceptInclusion.KLEENE_DIENES) ) )
					)
					{
						// Synonym definition
						defineSynonyms(a, b);

						// Remove A isa B
						removeAisaX(a, pcd1, axiomsToDoAisaB);
						removeAisaX(b, pcd4, axiomsToDoAisaB);

						Util.println("------ Synonym absorption from tInc : " + a + " = " + b);
						//absorptionCountSyn++;
						return true;
					}
				}
		}
		return false;
	}


	/**
	 * Adds some disjoint concept axioms.
	 * @param disjointConcepts A vector of concept names.
	 */
	public void addAtomicConceptsDisjoint(ArrayList<String> disjointConcepts)
	{
		Util.println("disjoint axioms:" + disjointConcepts);
		for (int i=0; i<disjointConcepts.size(); i++)
		{
			String c1 = disjointConcepts.get(i);
			getConcept(c1);
			for (int j=i+1; j<disjointConcepts.size(); j++)
				addMutuallyDisjoint(c1, disjointConcepts.get(j));
		}
	}


	/**
	 * Adds some disjoint concept axioms.
	 * @param disjointConcepts A vector of concepts.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public void addConceptsDisjoint(ArrayList<Concept> disjointConcepts) throws InconsistentOntologyException
	{
		Util.println("disjoint axioms:" + disjointConcepts);
		for (int i=0; i<disjointConcepts.size(); i++)
		{
			Concept c1 = disjointConcepts.get(i);
			for (int j=i+1; j<disjointConcepts.size(); j++)
				addConceptsDisjoint(c1, disjointConcepts.get(j));
		}
	}
	

	private void addMutuallyDisjoint(String c1, String c2)
	{
		addConceptsDisjoint(c1, c2);
		addConceptsDisjoint(c2, c1);
	}


	private void addConceptsDisjoint(String c1, String c2)
	{
		if (c1.equals(c2))
			return;

		HashSet<String> set = tDis.get(c1);
		if (set == null)
			set = new HashSet<String> ();
		set.add(c2);

		tDis.put(c1, set);
	}
	
	
	public void addConceptsDisjoint(Concept c, Concept d) throws InconsistentOntologyException
	{
		if (c.toString().equals(d.toString()))
			return;

		if (c.isAtomic() && d.isAtomic())
			addMutuallyDisjoint(c.toString(), d.toString());
		else
		{
			// New concepts
			Concept a = getNewAtomicConcept();
			Concept b = getNewAtomicConcept();
	
			// C ==> A
			zImplies(c, a);
	
			// D ==> B
			zImplies(d, b);		
			
			// disjoint(A,B)
			addMutuallyDisjoint(a.toString(), b.toString());
		}
	}


	private Concept getNewAtomicConcept()
	{
		numDefinedConcepts++;
		String conceptName = Concept.DEFAULT_NAME + numDefinedConcepts;
		return new Concept(conceptName);
	}
	

	/**
	 * Adds some equivalent funcRole axioms.
	 * @param equivRoles An array list of equivalent fuzzy funcRole names.
	 */
	public void addEquivalentRoles(ArrayList<String> equivRoles)
	{
		if (equivRoles.size() >= 2)
		{
			String r1 = equivRoles.get(0);
			for (int i=1; i<equivRoles.size(); i++)
			{
				String r2 = equivRoles.get(i);
				roleImplies(r1, r2);
				roleImplies(r2, r1);
				r1 = r2;
			}
		}
	}


	/**
	 * Adds some equivalent concept axioms.
	 * @param equivConcepts An array list of vector of equivalent fuzzy concepts.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public void addEquivalentConcepts(ArrayList<Concept> equivConcepts) throws InconsistentOntologyException
	{	
		if (equivConcepts.size() >= 2)
		{
			Concept c1 = equivConcepts.get(0);
			for (int i=1; i<equivConcepts.size(); i++)
			{
				Concept c2 = equivConcepts.get(i);
				if (c1.getType() == Concept.ATOMIC)
					defineConcept(c1.toString(), c2);
				else if (c2.getType() == Concept.ATOMIC)
					defineConcept(c2.toString(), c1);
				else
					defineEquivalentConcepts(c1, c2);
			}
		}
	}


	/**
	 * Adds a concept equivalence axiom.
	 * @param c1 A concept.
	 * @param c2 Another concept.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public void defineEquivalentConcepts(Concept c1, Concept c2) throws InconsistentOntologyException
	{
		lImplies(c1, c2, Degree.ONE);
		lImplies(c2, c1, Degree.ONE);
	}


	/**
	 * Adds a disjoint union concept axiom.
	 * @param disjointUnionConcepts A vector of concepts names.
	 */
	public void addDisjointUnionConcept(ArrayList<String> disjointUnionConcepts)
	{
		if (disjointUnionConcepts.size() >= 2)
		{
			String name1 = disjointUnionConcepts.get(0);

			if (disjointUnionConcepts.size() == 2)
			{
				String name2 = disjointUnionConcepts.get(1);
				Concept c2 = getConcept(name2);
				defineConcept(name1, c2);
			}
			else
			{
				// Define C1 is a union of C2 ...
				Concept bigOr = null;
				for (int i=1; i<disjointUnionConcepts.size(); i++)
				{
					String name = disjointUnionConcepts.get(i);
					Concept c = getConcept(name);
					if (bigOr == null)
						bigOr = c;
					else
						bigOr = Concept.or(bigOr, c);
				}

				defineConcept(name1, bigOr);

				// Make C2...Cn disjoint
				disjointUnionConcepts.remove(0);
				addAtomicConceptsDisjoint(disjointUnionConcepts);
			}
		}
	}


	/**
	 * Adds a functional funcRole axiom.
	 * @param role A role.
	 */
	public void roleIsFunctional(String role)
	{
		funcRoles.add(role);
	}


	/**
	 * Adds an inverse functional funcRole axiom.
	 * @param role A role.
	 */
	public void roleIsInverseFunctional(String role)
	{
		invFuncRoles.add(role);
		Set<String> iv = invRoles.get(role);
		if (iv != null)
		{
			for (String inverse : iv)
				funcRoles.add(inverse);				
		}
		else
		{
			String inverse = role + "@inverse";
			addInverseRoles(role, inverse);
			abstractRoles.add(inverse);
			funcRoles.add(inverse);
		}
	}


	/**
	 * Adds a transitive funcRole axiom.
	 * @param role A role.
	 */
	public void roleIsTransitive(String role)
	{
		if (! transRoles.contains(role))
		{
			abstractRoles.add(role);
			transRoles.add(role);
		}
	}


	/**
	 * Adds a reflexive funcRole axiom.
	 * @param role A role.
	 */
	public void roleIsReflexive(String role)
	{
		if (! reflexiveRoles.contains(role))
		{
			abstractRoles.add(role);
			reflexiveRoles.add(role);
		}
	}


	/**
	 * Adds a symmetric funcRole axiom.
	 * @param role A role.
	 */
	public void roleIsSymmetric(String role)
	{
		abstractRoles.add(role);
		symmetricRoles.add(role);
		String invName = role + "@inverse";
		addInverseRoles(role, invName);
		roleImplies(role, invName);
		roleImplies(invName, role);
	}


	/**
	 * Adds a fuzzy similarity relation.
	 * @param role A role.
	 */
	public void addSimilarityRelation(String role)
	{
		if (! similarityRels.contains(role))
		{
			roleIsReflexive(role);
			roleIsSymmetric(role);
			similarityRels.add(role);			
		}
	}


	/**
	 * Adds a fuzzy equivalence relation.
	 * @param role A role.
	 */
	public void addEquivalenceRelation(String role)
	{
		addSimilarityRelation(role);
		roleIsTransitive(role);
	}


	/**
	 * Gets the set of inverse roles of some inverse of a given role.
	 * @param role A role.
	 * @return The set of all inverse roles of some inverse of role.
	 */
	private Set<String> getInversesOfInverseRole(String role)
	{
		Set<String> inv = invRoles.get(role);
		if ((inv == null) || (inv.size() == 0))
			return null;
		else
		{
			// There is only one iteration of the loop
			for (String r : inv)
			{
				Set<String> inv2 = invRoles.get(r);
				if ((inv2 == null) || (inv2.size() == 0))
					return null;
				else
					return inv2;
			}
		}
		return null;
	}


	/**
	 * Adds an inverse funcRole axiom.
	 * @param role A role.
	 * @param invRole An inverse funcRole of funcRole.
	 */
	public void addInverseRoles(String role, String invRole)
	{
		abstractRoles.add(role);
		abstractRoles.add(invRole);

		// Equivalent roles to "funcRole" are inverse of "invRole"
		Set <String> a = getInversesOfInverseRole(role);
		if (a != null)
		   for (String r : a)
		   {
			   if (role.compareTo(r) != 0)
				   addSimpleInverseRoles(invRole, r);
		   }

		// Equivalent roles to "invRole" are inverse of "funcRole"
		Set <String> b = getInversesOfInverseRole(invRole);
		if (b != null)
		   for (String r : b)
		   {
			   if (invRole.compareTo(r) != 0)
				   addSimpleInverseRoles(role, r);
		   }

		// Inverse roles of "invRole" and inverse roles of "funcRole" are inverse
		a = invRoles.get(role);
		b = invRoles.get(invRole);
		if ((a != null) && (b != null))
		{
			for (String r1 : a)
				for (String r2 : b)
					if ((invRole.compareTo(r1) != 0) && (role.compareTo(r2) != 0))
					addSimpleInverseRoles(r1, r2);
		}

		// "funcRole" and "invRole" are inverse
		addSimpleInverseRoles(role, invRole);
	}


	/**
	 * States that two roles are inverse without recursion.
	 * @param funcRole A funcRole.
	 * @param invRole An inverse funcRole of funcRole.
	 */
	private void addSimpleInverseRoles(String role, String invRole)
	{
		Set<String> inv = invRoles.get(role);
		if (inv == null)
			inv = new HashSet<String>();
 		inv.add(invRole);
		invRoles.put(role, inv);

		inv = invRoles.get(invRole);
		if (inv == null)
			inv = new HashSet<String>();
		inv.add(role);
		invRoles.put(invRole, inv);
		
		if (invFuncRoles.contains(role))
				this.funcRoles.add(invRole);
		if (invFuncRoles.contains(invRole))
			this.funcRoles.add(role);
	}


	/**
	 * Adds a RIA (subsumed, subsumer, 1).
	 * @param subsumed Subsumed funcRole.
	 * @param subsumer Subsumed funcRole.
	 */
	public void roleImplies(String subsumed, String subsumer)
	{
		roleSubsumes(subsumer, subsumed, 1);
	}


	/**
	 * Adds a RIA (subsumed, subsumer, degree).
	 * @param subsumed Subsumed funcRole.
	 * @param subsumer Subsumed funcRole.
	 * @param n Lower bound for the degree.
	 */
	public void roleImplies(String subsumed, String subsumer, Double n)
	{
		roleSubsumes(subsumer, subsumed, n);
	}


	/**
	 * Adds a funcRole range axiom.
	 * @param role A role.
	 * @param conc of the funcRole.
	 */
	public void roleRange(String role, Concept conc)
	{
		if (conc == Concept.CONCEPT_TOP)
			return;

		HashSet<Concept> cs = rangeRestrictions.get(role);
		if (cs == null)
			cs = new HashSet<Concept>();

		cs.add(conc);
		rangeRestrictions.put(role, cs);
//		rrCount++;
	}


	/**
	 * Adds a domain funcRole axiom.
	 * @param role A role.
	 * @param conc Domain of the funcRole.
	 */
	public void roleDomain(String role, Concept conc)
	{
		if (conc == Concept.CONCEPT_TOP)
			return;

		HashSet<Concept> cs = domainRestrictions.get(role);
		if (cs == null)
			cs = new HashSet<Concept>();

		cs.add(conc);
		domainRestrictions.put(role, cs);
//		drCount++;
	}


	/**
	 * Solves the inverse funcRole axioms.
	 */
	private void solveInverseRoles() throws InconsistentOntologyException
	{
		formInvRoleIncAxioms();
		formInvTransRoles();
		formInvRoleRelations();
	}


	// Computes relations for the inverse roles
	private void formInvRoleRelations() throws InconsistentOntologyException
	{
		Hashtable<String, ArrayList<Relation>> tempRoleRelations = new Hashtable<String, ArrayList<Relation>>();

		for (Individual indA : individuals.values())
		{
			for (String role: indA.roleRelations.keySet())
			{
				if(invRoles.containsKey(role))
				{
					ArrayList<Relation> rels = indA.roleRelations.get(role);
					for(Relation rel : rels)
					{
						Individual indB = rel.getObjectIndividual();
						String indBname = indB.toString();
						ArrayList<Relation> tempRels = tempRoleRelations.get(indBname);
						if (tempRels == null)
							tempRels = new ArrayList<Relation>();

						// For every inverse invRole
						for(String invRole: invRoles.get(role))
						{
							Variable var1 = milp.getVariable(indA, indB, role);
							Variable var2 = milp.getVariable(indB, indA, invRole);
							milp.addNewConstraint(new Expression(new Term(1, var1), new Term(-1, var2)), Inequation.EQ);					   	
							Relation tempRel = new Relation(invRole, indB, indA, Degree.getDegree(var2));
							tempRels.add(tempRel);
						}
						tempRoleRelations.put(indBname, tempRels);
					}
				}
			}
		}

		for (String indName : tempRoleRelations.keySet())
		{
			ArrayList<Relation> rels = tempRoleRelations.get(indName);
			if (rels != null)
				for(Relation r : rels)
					r.getSubjectIndividual().addRelation(r.getRoleName(), r.getObjectIndividual(), r.getDegree(), this);
		}
	}

/*
	// Computes relations for the inverse roles and RIAs
	// (C => D, n) implies (inv(C) => inv(D), n)
	private void formInvRoleIncAxiomsOLD()
	{
		//Util.println(" formInvRoleIncAxioms");
		Hashtable<String, Hashtable<String,Double>> copy = new Hashtable<String, Hashtable<String,Double>> (rolesWithParents);

		for (String roleC : copy.keySet())
		{
			//Util.println(" here1   : " + roleC);
			if(invRoles.containsKey(roleC))
			{
				Hashtable<String,Double> parents = rolesWithParents.get(roleC);

				//Util.println(" here2 : " + parents);
				for (String roleP : parents.keySet())
				{
					//Util.println(" here3 : " + roleP);
				   if(invRoles.containsKey(roleP))
				   {
						Double n = parents.get(roleP);
						if (n == null)
							n = 1.0;

					   	// For every inverse funcRole of C
						for (String invRoleC : invRoles.get(roleC))
						{
							//Util.println(" here4 : " + invRoleC);

							// For every inverse funcRole of D
							for (String invRoleP : invRoles.get(roleP))
							{
								//Util.println(invRoleP + " subsumes " + invRoleC);
						   		roleSubsumes(invRoleP, invRoleC, n);
					   		}
				   		}
				   }
				}
			}
		}
	}
*/


	// Computes relations for the inverse roles and RIAs
	// (R => P, n) implies (inv(R) => inv(P), n)
	private void formInvRoleIncAxioms()
	{
		//Util.println(" formInvRoleIncAxioms");
		Hashtable<String, Hashtable<String,Double>> toDo = new Hashtable<String, Hashtable<String,Double>> (rolesWithParents);
		boolean noMoreRoleInclusions = toDo.isEmpty();

		while (! noMoreRoleInclusions)
		{
			Hashtable<String, Hashtable<String,Double>> rolesWithParentsTmp = new Hashtable<String, Hashtable<String,Double>>();
			noMoreRoleInclusions = true;

			for (String roleR : toDo.keySet())
			{
				//Util.println(" here1   : ");
				//Util.println(roleR + " ==> " + rolesWithParents.get(roleR));
				//Util.println(roleR + " inverses : " + invRoles.get(roleR));

				if(invRoles.containsKey(roleR))
				{
					Hashtable<String,Double> parents = rolesWithParents.get(roleR);

					//Util.println(" here2 : ");

					for (String roleP : parents.keySet())
					{
					   //Util.println(" here3 : " + roleP);
									   //Util.println(roleP + " ==> " + rolesWithParents.get(roleP));
									   //Util.println(roleP + " inverses : " + invRoles.get(roleP));

					   if(invRoles.containsKey(roleP))
					   {
							Double n = parents.get(roleP);
							if (n == null)
								n = 1.0;

						   	// For every inverse funcRole of C
							for (String invRoleR : invRoles.get(roleR))
							{
								//Util.println(" here4 : " + invRoleR);

								// For every inverse funcRole of D
								for (String invRoleP : invRoles.get(roleP))
								{
							   		noMoreRoleInclusions = noMoreRoleInclusions && ! roleSubsumesBool(invRoleP, invRoleR, n, rolesWithParentsTmp);
																	//noMoreRoleInclusions = false;
																	//Util.println(invRoleR  + " ==> " + rolesWithParentsTmp.get(invRoleR));
						   		}
					   		}
					   }
					}
				}
			}

			toDo.clear();
			toDo = new Hashtable<String, Hashtable<String,Double>>(rolesWithParentsTmp);
			//rolesWithParents.addAll(rolesWithParentsTmp);

			if (! noMoreRoleInclusions)
			{
				noMoreRoleInclusions = true;
				for (String R : rolesWithParentsTmp.keySet())
				{
					Hashtable<String,Double> parentsTmp = rolesWithParentsTmp.get(R);
					for (String P : parentsTmp.keySet())
					{
						Double n = parentsTmp.get(P);
						if (n == null) n = 1.0;
						boolean addRole = roleSubsumesBool(P, R, n);
						noMoreRoleInclusions = noMoreRoleInclusions && (!addRole);
						//Util.println(" here5; addRole/noMoreRoleInclusions  : " + addRole + " / " + noMoreRoleInclusions);
					}
				}
			}
		}
	}


	// Computes relations for the inverse roles and transitive roles
	private void formInvTransRoles()
	{
		//Util.println("------- formInvTransRoles ---- " );
	 	HashSet<String> toDo = new HashSet<String>(transRoles);
		boolean noMoreRoles = toDo.isEmpty();

		while (! noMoreRoles)
		{
			HashSet<String>  transRolesTmp = new HashSet<String>();					
			noMoreRoles = true;
			for ( String transRole : toDo)
			{
				// For every inverse funcRole
				if (invRoles.get(transRole) != null)
				{
					for (String invRole : invRoles.get(transRole))
					{
						if(invRoles.containsKey(transRole) && !(transRoles.contains(invRole)))
						{
							transRolesTmp.add(invRole);
							noMoreRoles = false;
						}
					}
				}						 	 
			}
			toDo.clear();
			toDo.addAll(transRolesTmp);
			transRoles.addAll(transRolesTmp);
		}
		//Util.println("transRoles :  "  + transRoles);
	}


	/**
	 * Solves the fuzzy funcRole inclusion axioms.
	 */
	void solveRoleInclusionAxioms() throws InconsistentOntologyException
	{
		createRolesWithAllParents();
		createRolesWithTransChildren();

		for (Individual ind : individuals.values())
		{
			for (String role : ind.roleRelations.keySet())
				if(rolesWithAllParents.containsKey(role))
				   tempRelationsList.put(role, ind.roleRelations.get(role));

			for (String roleC : tempRelationsList.keySet())
			{
				Hashtable<String,Double> parents = rolesWithAllParents.get(roleC);

				for (String roleP : parents.keySet())
				{
					Double n = parents.get(roleP);
					addRelationWithRoleParent(ind, roleC, roleP, n);
				}
			} 
		}
	}


	void addRelationWithRoleParent(Individual ind, String roleC, String roleP, double n) throws InconsistentOntologyException
	{
		// Lukasiewicz semantics
		if (semantics == FuzzyLogic.LUKASIEWICZ)
		{
			List<Relation> relations = ind.roleRelations.get(roleC);
			if (relations != null)
				for (Relation r : relations)
					addRelationWithRoleParentInLukasiewicz( r, roleP, n);
		}
		// Zadeh semantics: assumes degree 1
		else
		{
			List<Relation> relations = ind.roleRelations.get(roleC);
			if (relations != null)
				for (Relation r : relations)
					addRelation(ind, roleP, r.getObjectIndividual(), r.getDegree());
		}
	}


	void addRelationWithRoleParentInLukasiewicz(Relation r, String roleP, double n) throws InconsistentOntologyException
	{
		Degree deg = r.getDegree();
		if (deg.isNumeric())
		{
			Double aux = ((DegreeNumeric) deg).getNumericalValue();
			Double lukTnorm = n - 1.0 + aux;
			if (lukTnorm < 0)
				lukTnorm = 0.0;							
			addRelation(r.getSubjectIndividual(), roleP, r.getObjectIndividual(), Degree.getDegree(lukTnorm));
		}
		else
		{
			old01Variables += 2;
			oldBinaryVariables++;
			
			Variable x = milp.getNewVariable(Variable.UP_VARIABLE);
			milp.addNewConstraint(new Expression(new Term(1,x)), Inequation.EQ, deg);
	
			// Add x lAnd n
			Variable newL = milp.getNewVariable(Variable.UP_VARIABLE);
			Variable yn = milp.getNewVariable(Variable.BINARY_VARIABLE);
	
			milp.addNewConstraint(new Expression(1, new Term(-1,yn)), Inequation.GE, Degree.getDegree(newL));
			milp.addNewConstraint(new Expression(-1+n, new Term(1,x), new Term(1,yn)), Inequation.EQ, Degree.getDegree(newL));
			milp.addNewConstraint(new Expression(-1, new Term(1,x), new Term(1,yn)), Inequation.LE);
			milp.addNewConstraint(new Expression(-1+n, new Term(1,yn)), Inequation.LE);
	
			addRelation(r.getSubjectIndividual(), roleP, r.getObjectIndividual(), Degree.getDegree(newL));
		}
	}

	/**
	 * Solves the fuzzy funcRole inclusion axioms for a given relation
	 * @param ind Individual subject of the relation.
	 * @param r Fuzzy relation.
	 */
	void solveRoleInclusionAxioms(Individual ind, Relation r) throws InconsistentOntologyException
	{
		String roleC = r.getRoleName();
		Hashtable<String,Double> parents = rolesWithAllParents.get(roleC);

		if (parents != null)
		{
			for (String roleP : parents.keySet())
			{
				Double n = parents.get(roleP);
				Util.println("Adding new relations, since " + roleP + " is an ancestor of " + r.getRoleName() + " with degree " + n);

				// Lukasiewicz semantics
				if (semantics == FuzzyLogic.LUKASIEWICZ)
				{
					Degree deg = r.getDegree();
					if (deg.isNumeric())
					{
						double lukTnorm = ((DegreeNumeric) deg).getNumericalValue() + n - 1.0;
						if (lukTnorm < 0)
							lukTnorm = 0;

						ind.addRelation(roleP, r.getObjectIndividual(), new DegreeNumeric(lukTnorm), this);
						if (funcRoles.contains(roleP))
							mergeFillers(ind, roleP);
					}
					else
					{
						old01Variables += 2;
						oldBinaryVariables++;

						Variable x = milp.getNewVariable(Variable.UP_VARIABLE);
						milp.addNewConstraint(new Expression(new Term(1,x)), Inequation.EQ, deg);

						// Add x lAnd n
						Variable newL = milp.getNewVariable(Variable.UP_VARIABLE);
						Variable yn = milp.getNewVariable(Variable.BINARY_VARIABLE);

						milp.addNewConstraint(new Expression(1, new Term(-1,yn)), Inequation.GE, Degree.getDegree(newL));
						milp.addNewConstraint(new Expression(-1+n, new Term(1,x), new Term(1,yn)), Inequation.EQ, Degree.getDegree(newL));
						milp.addNewConstraint(new Expression(-1, new Term(1,x), new Term(1,yn)), Inequation.LE);
						milp.addNewConstraint(new Expression(-1+n, new Term(1,yn)), Inequation.LE);

						ind.addRelation(roleP, r.getObjectIndividual(), new DegreeVariable(newL), this);
						if (funcRoles.contains(roleP))
							mergeFillers(ind, roleP);
					}
				}
				// Zadeh semantics: assumes degree 1
				else
				{
					ind.addRelation(roleP, r.getObjectIndividual(), r.getDegree(), this);
					if (funcRoles.contains(roleP))
						mergeFillers(ind, roleP);
				}
			}
		}
	}


	// Solves a GCI for a given individual
	private void solveGCI(Individual ind, GeneralConceptInclusion gci)
	{
		if ((gci.getSubsumed().getType() == Concept.MODIFIED) && (gci.getSubsumed().c1.getType() == Concept.CONCRETE))
			return;

		if ((gci.getSubsumer().getType() == Concept.MODIFIED) && (gci.getSubsumer().c1.getType() == Concept.CONCRETE))
			return;

		switch(gci.getType())
		{
			case GeneralConceptInclusion.LUKASIEWICZ:
				solveLukasiewiczGCI(ind, gci);
				break;

			case GeneralConceptInclusion.GOEDEL:
				solveGoedelGCI(ind, gci);
				break;

			case GeneralConceptInclusion.KLEENE_DIENES:
				solveKleeneDienesGCI(ind, gci);
				break;

			case GeneralConceptInclusion.ZADEH:
				solveZadehGCI(ind, gci);
				break;
		}
	}


	private void solveLukasiewiczGCI(Individual ind, GeneralConceptInclusion gci)
	{
		Concept C = gci.getSubsumed();
		Concept D = gci.getSubsumer();
		Degree L = gci.getDegree();
		Util.println("\n---------------- Applying GCI -------------------------------------");
		Util.println("-->: " + D + " l-subsumes " + C + " >= " + L);

		if (C.getType() == Concept.TOP)
		{
			if (D.getType() == Concept.BOTTOM)
				// Inconsistency
				milp.addNewConstraint(new Expression(1), Inequation.EQ, Degree.getDegree(0.0));
			else
			{
				Assertion newAss = new Assertion(ind, D, L);  // a:D >= n
				addAssertion(newAss);
			}
		}
		else
		{
			Concept notC = Concept.complement(C);
			if (D.getType() == Concept.BOTTOM)
			{
				Assertion newAss = new Assertion(ind, notC, L);   // a: \not C >= n
				addAssertion(newAss);
			}
			else
			{
				Variable xIndIsNotC = milp.getVariable(ind, notC);
				Variable xIndIsD = milp.getVariable(ind, D);
				addAssertion(ind, notC, new DegreeVariable(xIndIsNotC) );
				addAssertion(ind, D, new DegreeVariable(xIndIsD) );
				if (L.isNumeric() && ((DegreeNumeric) L).getNumericalValue() == 1)
				{
					old01Variables++;
					milp.addNewConstraint(new Expression(1, new Term(-1,xIndIsNotC), new Term(-1,xIndIsD)), Inequation.LE); // xIndIsC <= xIndIsD	
				}
				else
				{
					old01Variables += 2;
					milp.addNewConstraint(new Expression(new Term(1,xIndIsNotC), new Term(1,xIndIsD)), Inequation.GE, L); // 1 - x1 + x2 >= L
				}
			}
		}
		Util.println("-------------- GCI completed ---------------------------------------");
	}


	private void solveGoedelGCI(Individual ind, GeneralConceptInclusion gci)
	{
		Concept C = gci.getSubsumed();
		Concept D = gci.getSubsumer();
		Util.println("\n----------------- Applying GCI ----------------------------------");
		Util.println("-->: " + D.toString() + " g-subsumes " + C.toString() + " >= " + gci.getDegree());

		Degree L = gci.getDegree();

		if (C.getType() == Concept.TOP)
		{
			if (D.getType() == Concept.BOTTOM)
				// Inconsistency
				milp.addNewConstraint(new Expression(1), Inequation.EQ, Degree.getDegree(0.0));
			else
			{
				Assertion newAss = new Assertion(ind, D, L);  // a:D >= n
				addAssertion(newAss);
			}
		}
		else
		{
			Concept notC = Concept.complement(C);
			if (D.getType() == Concept.BOTTOM)
			{
				Assertion newAss = new Assertion(ind, notC, L);   // a: \not C >= n
				addAssertion(newAss);
			}
			else
			{
				Variable xIndIsNotC = milp.getVariable(ind, notC);
				Variable xIndIsD = milp.getVariable(ind, D);
				addAssertion(ind, notC, new DegreeVariable(xIndIsNotC) );
				addAssertion(ind, D, new DegreeVariable(xIndIsD) );
				if (L.isNumeric() && ((DegreeNumeric) L).getNumericalValue() == 1)
				{
					old01Variables++;
					milp.addNewConstraint(new Expression(1, new Term(-1,xIndIsNotC), new Term(-1,xIndIsD)), Inequation.LE); // xIndIsC <= xIndIsD
				}
				else
				{
					Concept cImplD = Concept.gImplies(C, D);
					addAssertion(new Assertion(ind, cImplD, L)); // a : C g-implies D >= L
				}
			}
		}
		Util.println("-------------- GCI completed ---------------------------------------");
	}


	private void solveKleeneDienesGCI(Individual ind, GeneralConceptInclusion gci)
	{
		Concept C = gci.getSubsumed();
		Concept D = gci.getSubsumer();
		Concept cImplD = Concept.kdImplies(C, D);
		Util.println("\n----------- Applying GCI ------------------------------------------");
		Util.println("-->: " + D.toString() + " kd-subsumes " + C.toString() + " >= " + gci.getDegree());

		if (C.getType() == Concept.TOP)
			addAssertion(new Assertion(ind, D, gci.getDegree()));
		else
			addAssertion(new Assertion(ind, cImplD, gci.getDegree()));
		Util.println("-------------- GCI completed ---------------------------------------");
	}


	private void solveZadehGCI(Individual ind, GeneralConceptInclusion gci)
	{
		Concept C = gci.getSubsumed();
		Concept D = gci.getSubsumer();
		Util.println("\n----------- Applying GCI ------------------------------------------");
		Util.println("-->: " + D.toString() + " z-subsumes " + C.toString() );

		if (C.getType() == Concept.TOP)
			addAssertion(new Assertion(ind, D, Degree.getDegree(1.0)));
		else
		{
			old01Variables++;
			Concept notC = Concept.complement(C);			
			Variable xIndIsNotC = milp.getVariable(ind, notC);
			Variable xIndIsD = milp.getVariable(ind, D);
			addAssertion(ind, notC, new DegreeVariable(xIndIsNotC) );
			addAssertion(ind, D, new DegreeVariable(xIndIsD) );	
			milp.addNewConstraint(new Expression(1, new Term(-1,xIndIsNotC), new Term(-1,xIndIsD)), Inequation.LE); // xIndIsC <= xIndIsD
		}
		Util.println("-------------- GCI completed ---------------------------------------");
	}


	// Applies all GCIs to one individual
	private void solveGCI(Individual ind)
	{
		for(GeneralConceptInclusion gci : tG)
			solveGCI(ind, gci);
	}


	// Solves a reflexive funcRole axiom
	private void solveReflexiveRole(String role) throws InconsistentOntologyException
	{
		for (Individual ind : individuals.values())
			addRelation(ind, role, ind, Degree.getDegree(1.0));
	}


	// Applies the rule for reflexivity to an individual
	private void solveReflexiveRoles (Individual ind) throws InconsistentOntologyException
	{
		for ( String role : reflexiveRoles)
			addRelation(ind, role, ind, Degree.getDegree(1.0));
	}

	
	// Use right version of the individual (needed when we clone the KB or merge individuals)
	private void getCorrectVersionOfIndividual(Assertion ass) throws InconsistentOntologyException
	{
		Individual ind = ass.getIndividual();
		Individual ind2 = individuals.get(ind.toString());
		if (ind != ind2)
		{
			if (ind2 == null)
				ind2 = this.getIndividual(ind.toString());
			if (! (ind.isBlockable()))
				ass.setIndividual(ind2);
		}
	}


	// Use right version of the individual (needed when we clone the KB or merge individuals)
	private void getCorrectVersionOfIndividual(Relation rel) throws InconsistentOntologyException
	{
		Individual ind = rel.getObjectIndividual();
		Individual ind2 = individuals.get(ind.toString());
		if (ind != ind2)
		{
			if (ind2 == null)
				ind2 = this.getIndividual(ind.toString());
			if (! (ind.isBlockable()))
				rel.setObjectIndividual(ind2);
		}
	}
	
	/**
	 * Solves the datatypes restrictions.
	 */
	private void solveConcreteValueAssertions() throws FuzzyOntologyException, InconsistentOntologyException
	{
		// Positive restrictions
		for (Assertion ass : positiveConcreteValueAssertions)
		{
			Util.println("\n --------- Processing Positive Datatype Assertion ----------------- ");
			Util.println("-> " + ass);
			if (
				(ass.getIndividual().isBlockable()) &&
				( (CreatedIndividual) ass.getIndividual()).isBlocked(this)
			)
				return;
			
			if(numDefinedInds == ConfigReader.MAX_INDIVIDUALS)
				Util.error("Error: Maximal number of individuals created: " + numDefinedInds);
			else
			{
				getCorrectVersionOfIndividual(ass);

				// Check type of the assertion
				rulesApplied[RULE_DATATYPE]++;
				if(ass.getType() == Concept.AT_MOST_VALUE)
					DatatypeReasoner.applyAtMostValueRule(ass, this);
				else if(ass.getType() == Concept.AT_LEAST_VALUE)
					DatatypeReasoner.applyAtLeastValueRule(ass, this);
				else if(ass.getType() == Concept.EXACT_VALUE)
					DatatypeReasoner.applyExactValueRule(ass, this);
			
				Util.println("----");
			}
		}

		positiveConcreteValueAssertions.clear();

		// Negative restrictions
		for (Individual a : individuals.values())
		{
			for (String fName : a.concreteRoleRestrictions.keySet())
			{
				ArrayList<Relation> ar = a.roleRelations.get(fName);
				if (ar != null)
				{
					CreatedIndividual b = (CreatedIndividual) ar.get(0).getObjectIndividual();
					ArrayList<Assertion> restrics = a.concreteRoleRestrictions.get(fName);
					if (restrics != null)
					{
						for (Assertion ass : restrics)
						{
							Util.println("\n --------- Processing Negative Datatype Assertion ----------------- ");
							Util.println("-> " + ass);
	
							getCorrectVersionOfIndividual(ass);
	
							// Check type of the assertion
							rulesApplied[RULE_NOT_DATATYPE]++;
							if(ass.getType() == Concept.NOT_AT_MOST_VALUE)
								//DatatypeReasoner.applyAtMostValueRule(ass, this);
								ruleComplementedAtMostDatatypeRestriction(b, ass);
							else if(ass.getType() == Concept.NOT_AT_LEAST_VALUE)
								//DatatypeReasoner.applyAtLeastValueRule(ass, this);
								ruleComplementedAtLeastDatatypeRestriction(b, ass);				
							else if(ass.getType() == Concept.NOT_EXACT_VALUE)
								//DatatypeReasoner.applyExactValueRule(ass, this);
								ruleComplementedExactDatatypeRestriction(b, ass);
							
							Util.println("----");
						}
					}
				}
			}
		}
	}


	/**
	 * Solves the reflexive role axioms.
	 */
	private void solveReflexiveRoles() throws InconsistentOntologyException
	{
		for(String role : reflexiveRoles)
			solveReflexiveRole(role);
	}


	/**
	 * Solves the functional role axioms.
	 */
	private void solveFunctionalRoles() throws InconsistentOntologyException
	{
		for (String role : funcRoles)
			for (String name : individuals.keySet())
			{
				Individual ind = individuals.get(name);

				// We skip the individual if has already been merged
				if (individuals.get(name).toString().compareTo(name) != 0)
					continue;

				mergeFillers(ind, role);
			}
	}


	/**
	 * If individual ind has two or more fillers via the functional role
	 * funcRole, they are merged into just one filler concept.
	 * 
	 * @param ind Subject individual.
	 * @param funcRole A functional role.
	 */
	private void mergeFillers(Individual ind, String funcRole) throws InconsistentOntologyException
	{
		ArrayList<Relation> rels = ind.roleRelations.get(funcRole);
		if(rels != null)
		{
			String aName = rels.get(0).getObjectIndividual().toString();
			Individual a = individuals.get(aName);
			for(int i=1; i<rels.size(); i++)
			{
				String bName  = rels.get(i).getObjectIndividual().toString();
				Individual b = individuals.get(bName);

				// If a and b have different names
				if(aName.equals(bName) == false)
				{
					merge(a, b);
					individuals.put(bName, a);
				}
			}
		}
	}


	/**
	 * Merges two individuals.
	 * @param a An individual. As an effect, it will contain a merged individual.
	 * @param b Another individual.
	 */
	private void merge(Individual a, Individual b) throws InconsistentOntologyException
	{
		String aName = a.toString();
		String bName = b.toString();

		if ((a instanceof CreatedIndividual) && ! (b instanceof CreatedIndividual) )
		{
			// Swap b and a, so the created individual is merged into the root individual
			Individual aux = b;
			b = a;
			a = aux;
			aName = a.toString();
			bName = b.toString();
		}

		Util.println("Merging individual " + bName + " into " + aName);
		// To do: nominal variables needed only if language contains "B"

		// Unique Name Assumption
		if (! (a instanceof CreatedIndividual) && ! (b instanceof CreatedIndividual) )
		{	
			// xAisA + xBisB <= 1
			Variable xAisA = milp.getNominalVariable(aName);
			Variable xBisB = milp.getNominalVariable(bName, bName);
			milp.addNewConstraint(new Expression(-1, new Term(1, xAisA), new Term(1,xBisB) ), Inequation.LE);

			// Add { b } to a
			addLabelsWithNodes(bName, aName);
		}


		// --------------------------------------------------------------
		// 1. Move edges leading to b so that they lead to a
		// --------------------------------------------------------------

		for (Individual i : individuals.values())
			for (ArrayList<Relation> array : i.roleRelations.values())
				for (Relation r : array)
					if (r.getObjectIndividual().equals(b) )
						r.setObjectIndividual(a);

		// ---------------------------------------------------------------------------------------------
		// 2. Move edges leading from b to a nominal node so that they lead from a
		// ---------------------------------------------------------------------------------------------

		Set<String> toRemove = new HashSet<String> ();
		for (String role : b.roleRelations.keySet())
		{
			ArrayList<Relation> aRels = a.roleRelations.get(role);
			if(aRels == null)
				aRels = new ArrayList<Relation>();

			ArrayList<Relation> bRels = b.roleRelations.get(role);
			ArrayList<Relation> newRels =  new ArrayList<Relation>();
			for (Relation r : bRels)
			{
				if (r.getObjectIndividual().isBlockable() == false)
				{
					r.setSubjectIndividual(a);
					aRels.add(r);
				}
				else
					newRels.add(r);
			}
			a.roleRelations.put(role, aRels);
			if (newRels.isEmpty())
				toRemove.add(role);
			else
				b.roleRelations.put(role,newRels);
		}
		for (String role : toRemove)
			b.roleRelations.remove(role);


		// ------------------------------------------------------
		// 3. Concept assertions using b, now use a
		// ------------------------------------------------------
		for (Assertion ass : assertions)
			if (ass.getIndividual().toString().equals(bName))
				ass.setIndividual(a);

		for (Assertion ass : existAssertions)
			if (ass.getIndividual().toString().equals(bName))
				ass.setIndividual(a);

		// -----------------------------------------
		// 4. Variables using b, now use a
		// -----------------------------------------
		
		boolean param = (b instanceof CreatedIndividual);	
		milp.changeVariableNames(bName, aName, param);


		// -----------------------------------------
		// 5. Prune
		// -----------------------------------------
		b.prune();				
	}


	/**
	 * Adds a Goedel General Concept Inclusion.
	 * @param conc1 Subsumed concept.
	 * @param conc2 Subsumer concept.
	 * @param degree Lower bound for the degree.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public void gImplies(Concept conc1, Concept conc2, Degree degree) throws InconsistentOntologyException
	{
		addSubsumption(conc2, conc1, degree, GeneralConceptInclusion.GOEDEL);
	}


	/**
	 * Adds a Lukasiewicz General Concept Inclusion.
	 * @param conc1 Subsumed concept.
	 * @param conc2 Subsumer concept.
	 * @param degree Lower bound for the degree.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public void lImplies(Concept conc1, Concept conc2, Degree degree) throws InconsistentOntologyException
	{
		addSubsumption(conc2, conc1, degree, GeneralConceptInclusion.LUKASIEWICZ);
	}


	/**
	 * Adds a Kleene-Dienes General Concept Inclusion.
	 * @param conc1 Subsumed concept.
	 * @param conc2 Subsumer concept.
	 * @param degree Lower bound for the degree.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public void kdImplies(Concept conc1, Concept conc2, Degree degree) throws InconsistentOntologyException
	{
		addSubsumption(conc2, conc1, degree, GeneralConceptInclusion.KLEENE_DIENES);
	}


	/**
	 * Adds a Zadeh General Concept Inclusion.
	 * @param conc1 Subsumed concept.
	 * @param conc2 Subsumer concept.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public void zImplies(Concept conc1, Concept conc2) throws InconsistentOntologyException
	{
		addSubsumption(conc2, conc1, Degree.getDegree(1.0), GeneralConceptInclusion.ZADEH);
	}


	/**
	 * Adds a General Concept Inclusion (conc2, conc1, degree, type) even if the left side is atomic.
	 * @param conc2 Subsumer concept.
	 * @param conc1 Subsumed concept.
	 * @param degree Lower bound for the degree.
	 * @param type Type of the GCI (semantics according to the implication).
	 */
	private void addSubsumption(Concept conc2, Concept conc1, Degree degree, int type) throws InconsistentOntologyException
	{
		//Util.println("addSubsumption : " + conc1 + " ==>_" + type + "  " +  conc2 + " >= " + degree);
		double n = ((DegreeNumeric) degree).getNumericalValue();

		if ((n==1) && !(type==GeneralConceptInclusion.KLEENE_DIENES))
			type = GeneralConceptInclusion.LUKASIEWICZ;

		if (isRedundantGCI(conc1, conc2, type, n))
			return;
		
		if (conc1.getType() == Concept.ATOMIC)
			defineAtomicConcept(conc1.toString(), conc2, type, n);
		else
			addGCI(conc2, conc1, degree, type);
	}


/*
	private boolean conceptAbsorptionsAisaB()
	{
		for(HashSet<PrimitiveConceptDefinition> pcds : axiomsAisaB.values())
			for (PrimitiveConceptDefinition tau : pcds)
				if (conceptAbsorption(tau, true))
					return true;
		return false;
	}


	private boolean conceptAbsorptionsAisaC()
	{
		for(HashSet<PrimitiveConceptDefinition> pcds : axiomsAisaC.values())
			for (PrimitiveConceptDefinition tau : pcds)
				if (conceptAbsorption(tau, false))
					return true;
		return false;
	}


	private boolean conceptAbsorptionsCisaA()
	{		
		for(HashSet<GeneralConceptInclusion> gcis : axiomsCisaA.values())
		{			
			for (GeneralConceptInclusion tau : gcis)
				if (conceptAbsorption(tau, true))
					return true;
		}
		return false;
	}


	private boolean conceptAbsorptionsCisaD()
	{
		for(HashSet<GeneralConceptInclusion> gcis : axiomsCisaD.values())
			for (GeneralConceptInclusion tau : gcis)
				if (conceptAbsorption(tau, false))
					return true;
		return false;
	}
*/


	/**
	 * @param pcd A primitive concept definition
	 * @param atomic true for CisaA; false for CisaD
	 * @return true if there are changes; false otherwise
	 */
	private boolean conceptAbsorption(PrimitiveConceptDefinition pcd, boolean atomic)
	{
		//Util.println("----- Try primitive Concept absorption : " + atomic);
		String a = pcd.getDefinedConcept();

		// CA0, FA0
		if (! tDef.containsKey(a))
		{
			addAxiomToInc(a, pcd);
			removeAisaX(a, pcd, atomic);
			Util.println("absorbed axiomsAisaC CA0, FA0 :  " + pcd);
			//absorptionCountCA0FA0 ++;
			return true;
		}

		return false;		
	}


	/**
	 * @param tau A general concept inclusion
	 * @param atomic true for CisaA; false for CisaD
	 * @return true if there are changes; false otherwise
	 */
	private boolean conceptAbsorption(GeneralConceptInclusion tau, boolean atomic)
	{
		// Computes is the degree of the axiom is one
		Degree degree = tau.getDegree();
		double n = ((DegreeNumeric) degree).getNumericalValue();
		boolean degreeIsOne = (n == 1);

		Concept conc1 = tau.getSubsumed();
		String key = conc1.toString();
		Concept conc2 = tau.getSubsumer();
		int implicationType = tau.getType();
		int typeC1 = conc1.getType();
		int typeC2 = conc2.getType();

		//Util.println("----- GeneralConceptInclusion absorption : " + tau);
		//Util.println("----- implication type : " + implicationType);

		//Util.println("GCI: " + tau);
		//Util.println("type C1, C2: " + typeC1 + " : " +  typeC2);
		//Util.println("type implication: " + implicationType);

		// CA1, FA1
		if (conc2.isComplementedAtomic() && degreeIsOne)
		{
			if (! tDef.containsKey(conc2.c1.toString()))
			{
			    PrimitiveConceptDefinition cp = new PrimitiveConceptDefinition(conc2.c1.toString(), Concept.complement(conc1), implicationType, 1.0);
			    //addAxiomToInc(conc2.c1.toString(), new PrimitiveConceptDefinition(conc2.c1.toString(), Concept.complement(conc1), implicationType, 1.0));
			    addAxiomToInc(conc2.c1.toString(), cp);
			    addAxiomToDoAisaX(conc2.c1.toString(), cp);
				removeCisaX(key, tau, atomic);
				Util.println("absorbed axiomsCisaD CA1, FA1 :  "  + conc2.c1.toString()  + " ==> " + Concept.complement(conc1));
				//absorptionCountCA1FA1++;
				return true;
			}
		}

		// CA2, FA2.1
		if ((typeC2 == Concept.OR) || 
			((typeC2 == Concept.L_OR) && (implicationType == GeneralConceptInclusion.LUKASIEWICZ)) ||
			((typeC2 == Concept.G_OR) && (implicationType == GeneralConceptInclusion.KLEENE_DIENES)) ||
			((typeC2 == Concept.L_OR) && (implicationType == GeneralConceptInclusion.ZADEH))
		   )
		{
			//Util.println("CA2, FA2 ");
			ArrayList<Concept> vc = new ArrayList<Concept> (conc2.concepts);
			for (int j=0; j<conc2.concepts.size(); j++)
			{
				Concept ci = conc2.concepts.get(j);
				if (ci.isComplementedAtomic() )
				{
					Concept newC1;
					newC1 = Concept.complement(ci);
/*
					//Util.println("newC1: " + newC1);
					vc.set(j, Concept.complement(conc1) );
					Concept newC2;
					if (typeC2 == Concept.L_OR)
						newC2 = Concept.lOr(vc);
					else if (typeC2 == Concept.G_OR)
						newC2 = Concept.gOr(vc);
					else
						newC2 = Concept.or(vc);
*/
					// Transform "C => C1 or C2 ... Cn" into "A => C1 or C2 ... or Cn or (not C)"
					if (! tDef.containsKey(newC1.toString()))
					{
						//Util.println("newC1: " + newC1);
						vc.set(j, Concept.complement(conc1) );
						Concept newC2;
						if (typeC2 == Concept.L_OR)
							newC2 = Concept.lOr(vc);
						else if (typeC2 == Concept.G_OR)
							newC2 = Concept.gOr(vc);
						else
							newC2 = Concept.or(vc);

						PrimitiveConceptDefinition cp = new PrimitiveConceptDefinition(newC1.toString(), newC2, implicationType, n);
						//addAxiomToInc(newC1.toString(), new PrimitiveConceptDefinition(newC1.toString(), newC2, implicationType, n));
						addAxiomToInc(newC1.toString(), cp);
						addAxiomToDoAisaX(newC1.toString(), cp);
						removeCisaX(key, tau, atomic);
						Util.println("absorbed axiomsCisaD CA2, FA2.1  :  " + newC1.toString() + " ==> " + newC2);
						//absorptionCountCA2FA21++;
						return true;
					}
				}
			}
		}

		// CA3, FA3
		if ((typeC1 == Concept.AND) ||
			((typeC1 == Concept.L_AND) && (implicationType == GeneralConceptInclusion.LUKASIEWICZ)) ||
			((typeC1 == Concept.G_AND) && (implicationType == GeneralConceptInclusion.GOEDEL) ) ||
			((typeC1 == Concept.G_AND) && (implicationType == GeneralConceptInclusion.ZADEH) ) ||
			((typeC1 == Concept.G_AND) && (implicationType == GeneralConceptInclusion.LUKASIEWICZ) && (n == 1) )
		   )
		{
			ArrayList<Concept> vc = new ArrayList<Concept> (conc1.concepts);

			Util.println("----test CA3, FA3-------");
			Util.println("vc  :  " + vc);
			Util.println("conc1  :  " + conc1);
			Util.println("conc1 size :  " + conc1.concepts.size());

			for (int j=0; j<conc1.concepts.size(); j++)
			{
				Concept ci = conc1.concepts.get(j);
				if (ci.isAtomic())
				{
/*
					Util.println("before  :  " + vc);
					vc.remove(j);
					Util.println("absorb CA3,FA3,i    " + j);
					Util.println("removed vc  :  " + vc);
					Util.println("conc1  :  " + conc1);
					Concept newC1;
					if (typeC1 == Concept.L_AND)
						newC1 = Concept.lImplies(Concept.lAnd(vc), conc2);
					else if (typeC1 == Concept.G_AND)
						newC1 = Concept.gImplies(Concept.gAnd(vc), conc2);
					else
						newC1 = Concept.lImplies(Concept.and(vc), conc2);
					Util.println("here 1  :  ");
 */

					// Transform "C1 and C2 ... Cn => C" into "A => (C1 and C2 ... and Cn) implies C"
					if (! tDef.containsKey(ci.toString()))
					{
						vc.remove(j);

						Concept newC1;
						if (typeC1 == Concept.L_AND)
							newC1 = Concept.lImplies(Concept.lAnd(vc), conc2);
						else if (typeC1 == Concept.G_AND)
							newC1 = Concept.gImplies(Concept.gAnd(vc), conc2);
						else
							newC1 = Concept.lImplies(Concept.and(vc), conc2);

						if ((typeC1 == Concept.G_AND) && (implicationType != GeneralConceptInclusion.GOEDEL))
							implicationType = GeneralConceptInclusion.LUKASIEWICZ;

						//Util.println("here 2  :  ");
						PrimitiveConceptDefinition cp = new PrimitiveConceptDefinition(ci.toString(), newC1, implicationType, n);
						//addAxiomToInc(ci.toString(), new PrimitiveConceptDefinition(ci.toString(), newC1, implicationType, n));
						addAxiomToInc(ci.toString(), cp);
						addAxiomToDoAisaX(ci.toString(), cp);
						//defineAtomicConcept(ci.toString(), newC1, implicationType, n);												
						removeCisaX(key, tau, atomic);
						Util.println("absorbed axiomsCisaD CA3, FA3 :  " + ci.toString() + " ==> " + newC1);
						//absorptionCountCA3FA3++;
						return true;
					}
					//Util.println("here 3  :  ");
				}
			}
		}

		// FA2.2
		if ( (typeC2 == Concept.G_IMPLIES) && (implicationType == GeneralConceptInclusion.GOEDEL) && conc2.concepts.get(0).isAtomic() )
		{
			if (! tDef.containsKey(conc2.concepts.get(0).toString()))
			{
				PrimitiveConceptDefinition cp = new PrimitiveConceptDefinition(conc2.concepts.get(0).toString(), Concept.gImplies(conc1, conc2.concepts.get(1)), implicationType, n);
				//addAxiomToInc(conc2.concepts.get(0).toString(), new PrimitiveConceptDefinition(conc2.concepts.get(0).toString(), Concept.gImplies(conc1, conc2.concepts.get(1)), implicationType, n));
				addAxiomToInc(conc2.concepts.get(0).toString(), cp);
				addAxiomToDoAisaX(conc2.concepts.get(0).toString(), cp);

				//defineAtomicConcept(conc2.concepts.get(0).toString(), Concept.gImplies(conc1, conc2.concepts.get(1)), implicationType, n);
				removeCisaX(key, tau, atomic);
				Util.println("absorbed axiomsCisaD FA2.2 :  " + conc2.concepts.get(0).toString() + " ==> " + Concept.gImplies(conc1, conc2.concepts.get(1)));
				//absorptionCountFA22++;
				return true;
			}
		}
/*
		// FA4
		if ((typeC1 == Concept.OR) ||
			((typeC1 == Concept.L_OR) && (implicationType == GeneralConceptInclusion.LUKASIEWICZ))
		   )
		{
			ArrayList<Concept> vc = new ArrayList<Concept> (conc1.concepts);
			for (int j=0; j<conc1.concepts.size(); j++)
			{
				Concept ci = conc1.concepts.get(j);
				if (ci.isAtomic())
				{
/*
					// Compute (not C1) and (not C2) ... and (not Cn) and C"
					vc.remove(j);
					for (int i=0; i<vc.size(); i++)
						vc.set(i, Concept.complement(vc.get(i)));
					vc.add(conc2);
					Concept newC1 = Concept.lAnd(vc);
*/
/*
					// Transform "C1 or C2 ... Cn => C" into "A => (not C1) and (not C2) ... and (not Cn) and C"
					if (! tDef.containsKey(ci.toString()))
					{
						// Compute (not C1) and (not C2) ... and (not Cn) and C"
						vc.remove(j);
						for (int i=0; i<vc.size(); i++)
						vc.set(i, Concept.complement(vc.get(i)));
						vc.add(conc2);
						Concept newC1 = Concept.lAnd(vc);

						PrimitiveConceptDefinition cp = new PrimitiveConceptDefinition(ci.toString(), newC1, implicationType, n);
						addAxiomToInc(ci.toString(), cp);
						addAxiomToDoAisaX(ci.toString(), cp);
						removeCisaX(key, tau, atomic);
						Util.println("absorbed axiomsCisaD FA4 :  " + ci.toString() + " ==> " + newC1);
						//absorptionCountFA4++;
						return true;
					}
				}
			}
		}
*/
		return false;
	}


	/**
	 * @return true if there are changes; false otherwise
	 */
/*	private boolean conceptAbsorption()
	{
		Util.println("-----Concept absorption ");
 				
		// CA0, FA0
		// axiomsAisaC
		for (String a : axiomsAisaC.keySet())
		{
			HashSet<PrimitiveConceptDefinition> pcds = axiomsAisaC.get(a);
			for (PrimitiveConceptDefinition pcd : pcds)
			{
				if (! tDef.containsKey(a))
				{
					addAxiomToInc(a, pcd);
					pcds.remove(pcd);
					if (pcds.isEmpty())
						axiomsAisaC.remove(a);
					else
						axiomsAisaC.put(a, pcds);

					Util.println("absorbed axiomsAisaC CA0, FA0 :  " + pcd);
					return true;
				}
			}
		}

		for (HashSet<GeneralConceptInclusion> gcis : axiomsCisaD.values())
			for (GeneralConceptInclusion tau : gcis)
			{
				// Computes is the degree of the axiom is one
				Degree degree = tau.getDegree();
				double n = ((DegreeNumeric) degree).getNumericalValue();
				boolean degreeIsOne = (n == 1);
	
				Concept conc1 = tau.getSubsumed();
				String key = conc1.toString();
				Concept conc2 = tau.getSubsumer();
				int implicationType = tau.getType();
				int typeC1 = conc1.getType();
				int typeC2 = conc2.getType();
	
				//Util.println("GCI: " + tau);
				//Util.println("type C1, C2: " + typeC1 + " : " +  typeC2);
				//Util.println("type implication: " + implicationType);
	
				// CA1, FA1
				if (conc2.isComplementedAtomic() && degreeIsOne)
				{
					if (! tDef.containsKey(conc2.c1.toString()))
					{
						defineAtomicConcept(conc2.c1.toString(), Concept.complement(conc1), implicationType, 1.0);
						removeCisaD(key, tau);
						Util.println("absorbed axiomsCisaD CA1, FA1 :  "  + conc2.c1.toString()  + " ==> " + Concept.complement(conc1));
						return true;
					}
				}
	
				// CA2, FA2.1
				if ((typeC2 == Concept.OR) || 
					((typeC2 == Concept.L_OR) && (implicationType == GeneralConceptInclusion.LUKASIEWICZ)) ||
					((typeC2 == Concept.G_OR) && (implicationType == GeneralConceptInclusion.KLEENE_DIENES)) ||
					((typeC2 == Concept.L_OR) && (implicationType == GeneralConceptInclusion.ZADEH))
				   )
				{
					//Util.println("CA2, FA2 ");
					ArrayList<Concept> vc = new ArrayList<Concept> (conc2.concepts);
					for (int j=0; j<conc2.concepts.size(); j++)
					{
						Concept ci = conc2.concepts.get(j);
						if (ci.isComplementedAtomic() )
						{
							Concept newC1;
							newC1 = Concept.complement(ci);
							//Util.println("newC1: " + newC1);
							vc.set(j, Concept.complement(conc1) );
							Concept newC2;
							if (typeC2 == Concept.L_OR)
								newC2 = Concept.lOr(vc);
							else if (typeC2 == Concept.G_OR)
								newC2 = Concept.gOr(vc);
							else
								newC2 = Concept.or(vc);
	
							// Transform "C => C1 or C2 ... Cn" into "A => C1 or C2 ... or Cn or (not C)"
							//defineAtomicConcept(ci.toString(), newC2, implicationType, n);
							if (! tDef.containsKey(newC1.toString()))
							{
								defineAtomicConcept(newC1.toString(), newC2, implicationType, n);
								removeCisaD(key, tau);
								Util.println("absorbed axiomsCisaD CA2, FA2.1  :  " + newC1.toString() + " ==> " + newC2);
								return true;
							}
						}
					}
				}
	
				// CA3, FA3
				if ( (typeC1 == Concept.AND) ||
					((typeC1 == Concept.L_AND) && (implicationType == GeneralConceptInclusion.LUKASIEWICZ)) ||
					((typeC1 == Concept.G_AND) && (implicationType == GeneralConceptInclusion.GOEDEL) ) )
				{
					ArrayList<Concept> vc = new ArrayList<Concept> (conc1.concepts);
					for (int j=0; j<conc1.concepts.size(); j++)
					{
						Concept ci = conc1.concepts.get(j);
						if (ci.isAtomic())
						{
							vc.remove(j);
							Concept newC1;
							if (typeC1 == Concept.L_AND)
								newC1 = Concept.lImplies(Concept.lAnd(vc), conc2);
							else if (typeC1 == Concept.G_AND)
								newC1 = Concept.gImplies(Concept.gAnd(vc), conc2);
							else
								newC1 = Concept.lImplies(Concept.and(vc), conc2);
	
							// Transform "C1 and C2 ... Cn => C" into "A => (C1 and C2 ... and Cn) implies C"
							if (! tDef.containsKey(ci.toString()))
							{
								defineAtomicConcept(ci.toString(), newC1, implicationType, n);
								removeCisaD(key, tau);
								Util.println("absorbed axiomsCisaD CA3, FA3 :  " + ci.toString() + " ==> " + newC1);
								return true;
							}
						}
					}
				}
	
				// FA2.2
				if (  (typeC2 == Concept.G_IMPLIES) && (implicationType == GeneralConceptInclusion.GOEDEL) && conc2.concepts.get(0).isAtomic() )
				{
					if (! tDef.containsKey(conc2.concepts.get(0).toString()))
					{
						defineAtomicConcept(conc2.concepts.get(0).toString(), Concept.gImplies(conc1, conc2.concepts.get(1)), implicationType, n);
						removeCisaD(key, tau);
						Util.println("absorbed axiomsCisaD FA2.2 :  " + conc2.concepts.get(0).toString() + " ==> " + Concept.gImplies(conc1, conc2.concepts.get(1)));
						return true;
					}
				}
			}

		return false;
	}


	private boolean roleAbsorptionsAisaC()
	{
		for(HashSet<PrimitiveConceptDefinition> pcds : axiomsAisaC.values())
			for (PrimitiveConceptDefinition tau : pcds)
				if (roleAbsorption(tau))
					return true;
		return false;
	}


	private boolean roleAbsorptionsCisaA()
	{
		for(HashSet<GeneralConceptInclusion> pcds : axiomsCisaA.values())
			for (GeneralConceptInclusion tau : pcds)
				if (roleAbsorption(tau, true))
					return true;
		return false;
	}


	private boolean roleAbsorptionsCisaD()
	{
		for(HashSet<GeneralConceptInclusion> gcis : axiomsCisaD.values())
			for (GeneralConceptInclusion tau : gcis)
				if (roleAbsorption(tau, false))
					return true;
		return false;
	}
*/


	/**
	 * @param tau A primitive concept definition
	 * @return true if there are changes; false otherwise
	 */
	private boolean roleAbsorption(PrimitiveConceptDefinition tau)
	{
		//Util.println("------Role absorption ---------- ");
		Concept conc1 = getConcept(tau.getDefinedConcept());
		String key = conc1.toString();
		Concept conc2 = tau.getDefinition();
		int implicationType = tau.getType();
		int typeC2 = conc2.getType();

		if (typeC2 == Concept.ALL)
		{
			String role = conc2.getRole();	

			// RE2
			if ( isCrispRole(role) && (
				(implicationType != GeneralConceptInclusion.KLEENE_DIENES) ||
				(semantics == FuzzyLogic.CLASSICAL)
			) )
			{
				roleDomain(role, Concept.gImplies(conc1, conc2) );
				removeAisaC(key, tau);
				Util.println("absorbed :  domain " + role + " ,  " + Concept.gImplies(conc1, conc2));
				//absorptionCountRE2++;
				return true;
			}

			// RE3
			if (
				(semantics == FuzzyLogic.CLASSICAL)  ||
				(semantics == FuzzyLogic.LUKASIEWICZ) && (implicationType == GeneralConceptInclusion.LUKASIEWICZ) ||
				(semantics == FuzzyLogic.ZADEH) && (implicationType == GeneralConceptInclusion.ZADEH)						
			)
			{
				roleDomain(role, Concept.gImplies(Concept.some(role, Concept.complement(conc2.c1)), Concept.complement(conc1)));
				removeAisaC(key, tau);
				Util.println("absorbed :  domain " + role + " ,  " + Concept.gImplies(Concept.some(role, Concept.complement(conc2.c1)), Concept.complement(conc1)));
				//absorptionCountRE3++;
				return true;
			}
		}

		return false;
	}


	/**
	 * @param tau A general concept inclusion
	 * @return true if there are changes; false otherwise
	 */
/*	private boolean disjointnessAbsorption(GeneralConceptInclusion tau) throws InconsistentOntologyException
	{
		Concept conc1 = tau.getSubsumed();
		int type = conc1.getType();
		String key = conc1.toString();
		Concept conc2 = tau.getSubsumer();

		// (and a1 a2) => bottom

		//	for (Concept ci : concepts)
		//		if (ci.getType() != ATOMIC)
		//			return false;
			
		
		if ( 
				(conc2 == Concept.CONCEPT_BOTTOM) &&
				( (type == Concept.G_AND) || (
						(type == Concept.AND) && (semantics != FuzzyLogic.LUKASIEWICZ)
				) ) &&
				(conc1.concepts.size() == 2) && 
//				(implicationType == ...) ||
// mirar grados	
		)
		{
				removeCisaD(key, tau);
				// ai are disjoint
				addConceptsDisjoint(conc2.concepts);
				return true;
		}

		return false;
	}
*/


	/**
	 * @param tau A general concept inclusion
	 * @param atomic true for CisaA; false for CisaD
	 * @return true if there are changes; false otherwise
	 */
	private boolean roleAbsorption(GeneralConceptInclusion tau, boolean atomic)
	{
		//Util.println("------Role absorption ---------- ");
		// Computes is the degree of the axiom is one
		Degree degree = tau.getDegree();
		double n = ((DegreeNumeric) degree).getNumericalValue();
		boolean degreeIsOne = (n == 1);

		Concept conc1 = tau.getSubsumed();
		String key = conc1.toString();
		Concept conc2 = tau.getSubsumer();
		int implicationType = tau.getType();
		int typeC1 = conc1.getType();
		int typeC2 = conc2.getType();

		// RB1
		if ((conc1.getType() == Concept.SOME) && (conc1.c1 == Concept.CONCEPT_TOP) && degreeIsOne)
		{
			roleDomain(conc1.getRole(), conc2);
			removeCisaX(key, tau, atomic);
			Util.println("absorbed :  domain " + conc1.getRole() + " ,  " + conc2);
			//absorptionCountRB1++;
			return true;
		}

		// RB2
		if ( (conc1 == Concept.CONCEPT_TOP) && ( (conc2.getType() == Concept.ALL) ||  (conc2.getType() == Concept.NOT_HAS_VALUE) )&& degreeIsOne)
		{
			Concept range;
			if (conc2.getType() == Concept.ALL)
				range = conc2.c1;
			else
				range = new NegatedNominal((String) conc2.getValue());
			roleRange(conc2.getRole(), range);
			removeCisaX(key, tau, atomic);
			Util.println("absorbed :  range " + conc2.getRole() + " ,  " + range );
			//absorptionCountRB2++;
			return true;
		}

		// RE1
		if (((typeC1 == Concept.SOME) || (typeC1 == Concept.HAS_VALUE)) && degreeIsOne)
		{
			roleDomain(conc1.getRole(), Concept.gImplies(conc1, conc2));
			removeCisaX(key, tau, atomic);
			Util.println("absorbed :  domain " + conc1.getRole() + " ,  " + Concept.gImplies(conc1, conc2));
			//absorptionCountRE1++;
			return true;
		}

		if ((typeC2 == Concept.ALL) || (typeC2 == Concept.NOT_HAS_VALUE))
		{
			String role = conc2.getRole();	

			// RE2
			if ( isCrispRole(role) && (
				(implicationType != GeneralConceptInclusion.KLEENE_DIENES) ||
				(semantics == FuzzyLogic.CLASSICAL)
			) )
			{
				roleDomain(role, Concept.gImplies(conc1, conc2) );
				removeCisaX(key, tau, atomic);
				Util.println("absorbed :  domain " + role + " ,  " + Concept.gImplies(conc1, conc2));
				//absorptionCountRE2++;
				return true;
			}

			// RE3
			if (
				(semantics == FuzzyLogic.CLASSICAL)  ||
				(semantics == FuzzyLogic.LUKASIEWICZ) && (implicationType == GeneralConceptInclusion.LUKASIEWICZ) ||
				(semantics == FuzzyLogic.ZADEH) && (implicationType == GeneralConceptInclusion.ZADEH)
			)
			{
				Concept gImpConcept;
				if (typeC2 == Concept.ALL)
					gImpConcept = Concept.gImplies(Concept.some(role, Concept.complement(conc2.c1)), Concept.complement(conc1));
				else // if (typeC2 == Concept.NOT_HAS_VALUE)
					gImpConcept = Concept.gImplies(Concept.hasValue(role, (String) conc2.getValue()), Concept.complement(conc1));
				
				roleDomain(role, gImpConcept);
				removeCisaX(key, tau, atomic);
				Util.println("absorbed :  domain " + role + " ,  " + gImpConcept);
				//absorptionCountRE3++;
				return true;
			}
		}

		//RE4
		// test as for CA3, FA3
		Util.println("test RE4 conditions: type1 = " + typeC1 + " : type inclusion = " +  implicationType);			
		if ((typeC1 == Concept.AND) ||
			((typeC1 == Concept.L_AND) && (implicationType == GeneralConceptInclusion.LUKASIEWICZ)) ||
			((typeC1 == Concept.G_AND) && (implicationType == GeneralConceptInclusion.GOEDEL) ) ||
			((typeC1 == Concept.G_AND) && (implicationType == GeneralConceptInclusion.ZADEH) ) ||
			((typeC1 == Concept.G_AND) && (implicationType == GeneralConceptInclusion.LUKASIEWICZ) && degreeIsOne )
		   )		
		{
			ArrayList<Concept> vc = new ArrayList<Concept> (conc1.concepts);
			for (int j=0; j<conc1.concepts.size(); j++)
			{
				Concept ci = conc1.concepts.get(j);
				// Now test if there is a 'some' concept, so apply RE4
				if ((ci.getType() == Concept.SOME) || (ci.getType() == Concept.HAS_VALUE)) 
				{
					vc.remove(j);
					Concept newC1;
					// Build the new implication
					if (typeC1 == Concept.L_AND)
						newC1 = Concept.lImplies(Concept.lAnd(vc), conc2);
					else if (typeC1 == Concept.G_AND)
						newC1 = Concept.gImplies(Concept.gAnd(vc), conc2);
					else
						newC1 = Concept.lImplies(Concept.and(vc), conc2);					
					
					// Build the domain axiom according to RE4
					roleDomain(ci.getRole(), Concept.gImplies(ci, newC1));
					removeCisaX(key, tau, atomic);
					Util.println("absorbed RE4 :  domain " + ci.getRole() + " ,  " + Concept.gImplies(ci, newC1));
					//absorptionCountRE4++;
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * @return true if there are changes; false otherwise
	 */
/*	private boolean roleAbsorption()
	{
		Util.println("------Role absorption ---------- ");
		for (HashSet<GeneralConceptInclusion> gcis : axiomsCisaD.values() )
			for (GeneralConceptInclusion tau : gcis)
			{
				// Computes is the degree of the axiom is one
				Degree degree = tau.getDegree();
				double n = ((DegreeNumeric) degree).getNumericalValue();
				boolean degreeIsOne = (n == 1);
	
				Concept conc1 = tau.getSubsumed();
				String key = conc1.toString();
				Concept conc2 = tau.getSubsumer();
				int implicationType = tau.getType();
				int typeC1 = conc1.getType();
				int typeC2 = conc2.getType();
	
				// RB1
				if ((conc1.getType() == Concept.SOME) && (conc1.c1 == Concept.CONCEPT_TOP) && degreeIsOne)
				{
					roleDomain(conc1.getRole(), conc2);
					removeCisaD(key, tau);
					Util.println("absorbed :  domain " + conc1.getRole() + " ,  " + conc2);
					return true;
				}
	
				// RB2
				if ( (conc1 == Concept.CONCEPT_TOP) && (conc2.getType() == Concept.ALL) && degreeIsOne)
				{
					roleRange(conc2.getRole(), conc2.c1);
					removeCisaD(key, tau);
					Util.println("absorbed :  range " + conc2.getRole() + " ,  " + conc2.c1);
					return true;
				}
	
				// RE1
				if ((typeC1 == Concept.SOME) && degreeIsOne)
				{
					roleDomain(conc1.getRole(), Concept.gImplies(conc1, conc2));
					removeCisaD(key, tau);
					Util.println("absorbed :  domain " + conc1.getRole() + " ,  " + Concept.gImplies(conc1, conc2));
					return true;
				}
	
				if (typeC2 == Concept.ALL)
				{
					String role = conc2.getRole();	
	
					// RE2
					if ( isCrispRole(role) && (
						(implicationType != GeneralConceptInclusion.KLEENE_DIENES) ||
						(semantics == FuzzyLogic.CLASSICAL)
					) )
					{
						roleDomain(role, Concept.gImplies(conc1, conc2) );
						removeCisaD(key, tau);
						Util.println("absorbed :  domain " + role + " ,  " + Concept.gImplies(conc1, conc2));
						return true;
					}
	
					// RE3
					if (
						(semantics == FuzzyLogic.CLASSICAL) 
						||
						(semantics == FuzzyLogic.LUKASIEWICZ) && (implicationType == GeneralConceptInclusion.LUKASIEWICZ)
						||
						(semantics == FuzzyLogic.ZADEH) && (implicationType == GeneralConceptInclusion.ZADEH)						
					)
					{
						roleDomain(role, Concept.gImplies(Concept.some(role, Concept.complement(conc2.c1)), Concept.complement(conc1)));
						removeCisaD(key, tau);
						Util.println("absorbed :  domain " + role + " ,  " + Concept.gImplies(Concept.some(role, Concept.complement(conc2.c1)), Concept.complement(conc1)));
						return true;
					}
				}
			}

		return false;
	}
*/


	/**
	 * @return true if there are changes; false otherwise
	 */
/*	private boolean gciTransformation()
	{
		Util.println("------GCI transformation---------- ");

		for (int i=0; i<axiomsCisaD.size(); i++)
		{
			GeneralConceptInclusion tau = axiomsCisaD.get(i);

			// Computes is the degree of the axiom is one
			Degree degree = tau.getDegree();
			double n = ((DegreeNumeric) degree).getNumericalValue();

			Concept conc1 = tau.getSubsumed();
			Concept conc2 = tau.getSubsumer();
			int implicationType = tau.getType();
			int typeC1 = conc1.getType();
			int typeC2 = conc2.getType();						

			//Util.println("typeC1, typeC2 :" + typeC1 + " : " + typeC2);

			// CT1, FT1
			if ( (typeC2 == Concept.AND) || (typeC2 == Concept.G_AND) )
			{
				for (Concept ci : conc2.concepts)
				{
					// conc1 => ci  implicationType n
					axiomsCisaD.add(new GeneralConceptInclusion(ci, conc1, degree, implicationType));
					Util.println("absorbed CT1, FT1:  " + conc1 + " ==>  " + ci);		
				}
				axiomsCisaD.remove(i);								
				return true;
			}

			// CT2, FT2
			if ( (typeC1 == Concept.OR) || (typeC1 == Concept.G_OR) )
			{							
				for (Concept ci : conc1.concepts)
				{
					// ci => conc2  implicationType n

					if (ci.isAtomic())
					{
						defineAtomicConcept(ci.toString(), conc2, implicationType, n);
						Util.println("absorbed :  " + ci.toString() + " ==>  " + conc2);
					}
					else
					{
						axiomsCisaD.add(new GeneralConceptInclusion(conc2, ci, degree, implicationType));
						Util.println("absorbed :  " + ci + " ==>  " + conc2);
					}

					axiomsCisaD.add(new GeneralConceptInclusion(conc2, ci, degree, implicationType));
					Util.println("absorbed CT2, FT2:  " + ci + " ==>  " + conc2);

				}
				axiomsCisaD.remove(i);
				return true;
			}
		}

		return false;
	}
*/


	/**
	 * @return true if there are changes; false otherwise
	 */
/*	private boolean gciTransformationNew()
	{
		Util.println("------GCI transformation ---------- ");
		return (gciTransformationFromCisAD() || gciTransformationFromAisAC());
	}
*/


	/**
	 * @param pcd A general concept inclusion
	 * @param atomic true for CisaA; false for CisaD
	 * @return true if there are changes; false otherwise
	 */
	private boolean gciTransformation(GeneralConceptInclusion tau, boolean atomic) throws InconsistentOntologyException
	{
		//Util.println("gciTransformation : = " + tau + " atomic= " + atomic);

		// Computes is the degree of the axiom is one
		Degree degree = tau.getDegree();
		double n = ((DegreeNumeric) degree).getNumericalValue();

		Concept conc1 = tau.getSubsumed();
		Concept conc2 = tau.getSubsumer();
		int implicationType = tau.getType();
		int typeC1 = conc1.getType();
		int typeC2 = conc2.getType();

		//Util.println("typeC1, typeC2 :" + typeC1 + " : " + typeC2);

		// CT1, FT1
		if ( (typeC2 == Concept.AND) || (typeC2 == Concept.G_AND) )
		{
			for (Concept ci : conc2.concepts)
			{
				//conc1 => ci  implicationType n
				//addAxiomToCisaX(ci, conc1, degree, implicationType, ci.isAtomic());
				gciTransformationAddAxiomToCisaX(ci, conc1, degree, implicationType);
				Util.println("absorbed CT1, FT1:  " + conc1 + " ==>  " + ci);				
			}
			//removeCisaX(key, tau, atomic);
			//absorptionCountCT1FT1++;
			return true;
		}

		// CT2, FT2
		if ( (typeC1 == Concept.OR) || (typeC1 == Concept.G_OR) )
		{
			for (Concept ci : conc1.concepts)
			{
				// ci => conc2  implicationType n
				if (ci.isAtomic())
				{
					gciTranformDefineAtomicConcept(ci.toString(), conc2, implicationType, n);
					Util.println("absorbed CT2, FT2 :  " + ci.toString() + " ==>  " + conc2);
				}
				else
				{
					//addAxiomToCisaX(conc2, ci, degree, implicationType, conc2.isAtomic());
					gciTransformationAddAxiomToCisaX(conc2, ci, degree, implicationType);
					Util.println("absorbed CT2, FT2:  " + ci + " ==>  " + conc2);
				}
			}
			//removeCisaX(key, tau, atomic);
			//absorptionCountCT2FT2++;
			return true;
		}
		return false;
	}


	/**
	 * @return true if there are changes; false otherwise
	 */
/*	private boolean gciTransformationFromCisAD()
	{
		Util.println("------GCI transformation from CisAD ---------- ");
		for (HashSet<GeneralConceptInclusion> gcis : axiomsCisaD.values())
			for (GeneralConceptInclusion tau : gcis)
			{
				// Computes is the degree of the axiom is one
				Degree degree = tau.getDegree();
				double n = ((DegreeNumeric) degree).getNumericalValue();
	
				Concept conc1 = tau.getSubsumed();
				String key = conc1.toString();
				Concept conc2 = tau.getSubsumer();
				int implicationType = tau.getType();
				int typeC1 = conc1.getType();
				int typeC2 = conc2.getType();
	
				//Util.println("typeC1, typeC2 :" + typeC1 + " : " + typeC2);
	
				// CT1, FT1
				if ( (typeC2 == Concept.AND) || (typeC2 == Concept.G_AND) )
				{
					for (Concept ci : conc2.concepts)
					{
						// conc1 => ci  implicationType n
						addAxiomToCisaD(ci, conc1, degree, implicationType);
						Util.println("absorbed CT1, FT1:  " + conc1 + " ==>  " + ci);
					}
					removeCisaD(key, tau);
					return true;
				}
	
				// CT2, FT2
				if ( (typeC1 == Concept.OR) || (typeC1 == Concept.G_OR) )
				{
					for (Concept ci : conc1.concepts)
					{
						// ci => conc2  implicationType n
						if (ci.isAtomic())
						{
							defineAtomicConcept(ci.toString(), conc2, implicationType, n);
							Util.println("absorbed :  " + ci.toString() + " ==>  " + conc2);
						}
						else
						{
							addAxiomToCisaD(conc2, ci, degree, implicationType);
							Util.println("absorbed CT2, FT2:  " + ci + " ==>  " + conc2);
						}

					}
					removeCisaD(key, tau);
					return true;
				}
			}

		return false;
	}
*/


	/**
	 * @param pcd A primitive concept definition
	 * @return true if there are changes; false otherwise
	 */
	private boolean gciTransformation(PrimitiveConceptDefinition pcd)
	{
		String a = pcd.getDefinedConcept();
		Concept conc2 = pcd.getDefinition();
		int implicationType = pcd.getType();
		double n = pcd.getDegree();				 								
		int typeC2 = conc2.getType();

		//Util.println("typeC1, typeC2 :" + typeC1 + " : " + typeC2);

		// CT1, FT1
		if ( (typeC2 == Concept.AND) || (typeC2 == Concept.G_AND) )
		{
			for (Concept ci : conc2.concepts)
			{
				// a => ci  implicationType n
				gciTranformDefineAtomicConcept(a, ci, implicationType, n);
				Util.println("absorbed CT1, FT1:  " + a + " ==>  " + ci);
			}													
			//removeAisaC(a, pcd);
			//absorptionCountCT1FT1++;
			return true;
		}
		return false;
	}


	/**
	 * @return true if there are changes; false otherwise
	 */
/*	private boolean gciTransformationFromAisAC()
	{
		Util.println("------GCI transformation from AisAC---------- ");		
		for (String a : axiomsAisaC.keySet())
		{
			HashSet<PrimitiveConceptDefinition> hs = axiomsAisaC.get(a);

			for (PrimitiveConceptDefinition pcd : hs)
			{
				Concept conc2 = pcd.getDefinition();
				int implicationType = pcd.getType();
				double n = pcd.getDegree();				 								
				int typeC2 = conc2.getType();

				//Util.println("typeC1, typeC2 :" + typeC1 + " : " + typeC2);

				// CT1, FT1
				if ( (typeC2 == Concept.AND) || (typeC2 == Concept.G_AND) )
				{
					for (Concept ci : conc2.concepts)
					{
						// a => ci  implicationType n
						hs.remove(pcd);
						defineAtomicConcept(a, ci, implicationType, n);
						Util.println("absorbed CT1, FT1:  " + a + " ==>  " + ci);
					}													
					return true;
				}
			}
		}
		return false;
	}
*/


	private boolean nominalAbsorption(Concept conc1, Concept conc2, Degree degree) throws InconsistentOntologyException
	{
		// (R hasValue o) => D  >= \alpha  is replaced by o : (all R^- D) >= \alpha
		if (conc2.type == Concept.HAS_VALUE)
		{
			String r = conc2.getRole();
			Individual o = getIndividual((String) (conc2.getValue()));
			String invR;
			
			Set<String> iv = invRoles.get(r);
			if (iv != null)
				invR = iv.iterator().next();
			else
			{
				invR = r + "@inverse";
				addInverseRoles(r, invR);
				abstractRoles.add(invR);
			}
			Concept cAll = Concept.all(invR, conc1);
			addAssertion(o, cAll, degree);			
			return true;
		}
		return false;
	}


	/**
	 * Adds a General Concept Inclusion (conc2, conc1, degree, type).
	 * @param conc1 Subsumer concept.
	 * @param conc2 Subsumed concept.
	 * @param degree Lower bound for the degree.
	 * @param type Type of the GCI (semantics according to the implication).
	 */
	private void addGCI(Concept conc1, Concept conc2, Degree degree, int type) throws InconsistentOntologyException
	{
		//Util.println("---- process GCI ---- ");
		//Util.println("TEST   ADD GCI:  " + conc2 + " ==> " + conc1);
		double newDegree = ((DegreeNumeric) degree).getNumericalValue();

		if ( (newDegree == 1) && !(type == GeneralConceptInclusion.KLEENE_DIENES) )
			type = GeneralConceptInclusion.LUKASIEWICZ;

		if (isRedundantGCI(conc2, conc1, type, newDegree))
			return;

		// CT3
		if (nominalAbsorption(conc1, conc2, degree))
			return;

		// Check GCI does not exist
		HashSet<GeneralConceptInclusion> gcis;
		boolean isC1Atomic = conc1.isAtomic();

		//Util.println("is atomic RHS : " + isC1Atomic);
		//Util.println("type RHS : " + conc1.getType());

		if (isC1Atomic)
			gcis = axiomsCisaA.get(conc2.toString());
		else
			gcis = axiomsCisaD.get(conc2.toString());

		if (gcis != null)
			for (GeneralConceptInclusion gci : gcis)
			{
				Concept oldC1 = gci.getSubsumer();
				Concept oldC2 = gci.getSubsumed();
				double oldDegree = ((DegreeNumeric) gci.getDegree()).getNumericalValue();

				// If there exists a similar GCI, replace the GCI or not add it
				if (conc1.equals(oldC1) && conc2.equals(oldC2) && (gci.getType() == type) )
				{
					// If the existing GCI has a smaller degree, replace it
					if (newDegree > oldDegree)
					{
						removeCisaX(oldC2.toString(), gci, isC1Atomic);
						addAxiomToCisaX(conc1, conc2, degree, type, isC1Atomic);
						//addAxiomToCisaD(conc1, conc2, degree, type);
						Util.println("Axiom " + conc1.toString() + " subsumes " + conc2.toString() + " has the degree updated.");
					}
					else
						Util.println("Axiom " + conc1.toString() + " subsumes " + conc2.toString() + " is been already processed hence ignored.");						
					return;
				}
			}

		//Util.println("Ok,  ADD GCI  ");
		addAxiomToCisaX(conc1, conc2, degree, type, isC1Atomic);
	}


	/**
	 * Adds a GCI (conc2, conc1, degree, type) to addAxiomsCisaD.
	 * @param conc1 Subsumer concept.
	 * @param conc2 Subsumed concept.
	 * @param degree Lower bound for the degree.
	 * @param type Type of the GCI (semantics according to the implication).
	 */
	private void addAxiomToCisaA(Concept conc1, Concept conc2, Degree degree, int type) throws InconsistentOntologyException
	{
		double n = ((DegreeNumeric) degree).getNumericalValue();
		if (isRedundantGCI(conc2, conc1, type, n))
			return;
		
		// CT3
		if (nominalAbsorption(conc1, conc2, degree))
			return;

		GeneralConceptInclusion newGci = new GeneralConceptInclusion(conc1, conc2, degree, type);
		String key = newGci.getSubsumed().toString();
		HashSet<GeneralConceptInclusion> set = axiomsCisaA.get(key);
		if (set == null)
			set = new HashSet<GeneralConceptInclusion>();
		else
		{
			for (GeneralConceptInclusion gci : set)
				if (conc1.equals(gci.getSubsumer()) && conc2.equals(gci.getSubsumed()) && (gci.getType() == type) )
				{
					double oldDegree = ((DegreeNumeric) gci.getDegree()).getNumericalValue();
					if (n > oldDegree)
						gci.setDegree(degree);
					return;
				}
		}

		set.add(newGci);
		axiomsCisaA.put(key, set);
//		axiomsCisaACount++;
	}


	private void gciTransformationAddAxiomToCisaX(Concept conc1, Concept conc2, Degree degree, int type) throws InconsistentOntologyException
	{
		double n = ((DegreeNumeric) degree).getNumericalValue();
		if (isRedundantGCI(conc2, conc1, type, n))
			return;

		GeneralConceptInclusion newGci = new GeneralConceptInclusion(conc1, conc2, degree, type);
		String key = newGci.getSubsumed().toString();

		HashSet<GeneralConceptInclusion> set;
		if (conc1.isAtomic())
			set = axiomsToDoTmpCisaA.get(key);
		else
			set = axiomsToDoTmpCisaD.get(key);

		if (set == null)
			set = new HashSet<GeneralConceptInclusion>();
		set.add(newGci);

		if (conc1.isAtomic())
			axiomsToDoTmpCisaA.put(key, set);
		else
			axiomsToDoTmpCisaD.put(key, set);
		//axiomsCisaACount++;
	}


	/**
	 * Adds a GCI (conc2, conc1, degree, type) to addAxiomsCisaD.
	 * @param conc1 Subsumer concept.
	 * @param conc2 Subsumed concept.
	 * @param degree Lower bound for the degree.
	 * @param type Type of the GCI (semantics according to the implication).
	 * @param atomic true for CisaA; false for CisaD
	 */
	private void addAxiomToCisaX(Concept conc1, Concept conc2, Degree degree, int type, boolean atomic) throws InconsistentOntologyException
	{
		if (atomic)
			addAxiomToCisaA(conc1, conc2, degree, type);
		else
			addAxiomToCisaD(conc1, conc2, degree, type);
	}


	/**
	 * Adds a GCI (conc2, conc1, degree, type) to addAxiomsCisaD.
	 * @param conc1 Subsumer concept.
	 * @param conc2 Subsumed concept.
	 * @param degree Lower bound for the degree.
	 * @param type Type of the GCI (semantics according to the implication).
	 */
	private void addAxiomToCisaD(Concept conc1, Concept conc2, Degree degree, int type) throws InconsistentOntologyException
	{
		double n = ((DegreeNumeric) degree).getNumericalValue();
		if (isRedundantGCI(conc2, conc1, type, n))
			return;

		// CT3
		if (nominalAbsorption(conc1, conc2, degree))
			return;

		GeneralConceptInclusion newGci = new GeneralConceptInclusion(conc1, conc2, degree, type);
		String key = newGci.getSubsumed().toString();
		HashSet<GeneralConceptInclusion> set = axiomsCisaD.get(key);
		if (set == null)
			set = new HashSet<GeneralConceptInclusion>();
		set.add(newGci);
		axiomsCisaD.put(key, set);
//		axiomsCisaDCount++;
	}


	/**
	 * Adds a General Concept Inclusion (conc1, conc2, degree)
	 * @param conc1 Subsumed concept.
	 * @param conc2 Subsumer concept.
	 * @param degree Lower bound for the degree.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public void implies(Concept conc1, Concept conc2, Degree degree) throws InconsistentOntologyException
	{
		if (semantics == FuzzyLogic.LUKASIEWICZ)
			addSubsumption(conc2, conc1, degree, GeneralConceptInclusion.LUKASIEWICZ);
		else // "z","c"
			addSubsumption(conc2, conc1, Degree.getDegree(1.0), GeneralConceptInclusion.LUKASIEWICZ);
	}


	/**
	 * Computes the inclusion degree between two roles
	 * @param subsumed Subsumed funcRole.
	 * @param subsumer Subsumer funcRole.
	 * @return Inclusion degree of subsumed in subsumer.
	 */
	double getInclusionDegree(String subsumed, String subsumer)
	{
		Hashtable<String, Double> parents = rolesWithAllParents.get(subsumed);
		if (parents != null)
		{
			Double d = parents.get(subsumer);
			if (d != null)
				return d;
		}

		return 0;
	}


	// Computes transitive closure of the RIAs
	private void createRolesWithAllParents()
	{
		for (String roleC : rolesWithParents.keySet())
		{
			Hashtable<String,Double> parents = rolesWithParents.get(roleC);
			Hashtable<String,Double> allParents = new Hashtable<String,Double>();
			for(String roleD : parents.keySet())
			{
				Double n = parents.get(roleD);
				if(! roleC.equals(roleD) )
				{
					if (!allParents.contains(roleD))
					{
						allParents.put(roleD, n);
						if(rolesWithParents.containsKey(roleD))
							addParentRecursively(roleC, allParents, roleD, n);
					}
					else
					{
						Double oldN = allParents.get(roleD);
						if (n > oldN)
						{
							allParents.put(roleD, n);
							if(rolesWithParents.containsKey(roleD))
								addParentRecursively(roleC, allParents, roleD, n);
						}
					}
				}
			}

			rolesWithAllParents.put(roleC, allParents);

			// If func(R2) and R1 subsumes R2 with degree 1, then func(R1)
			if (! funcRoles.contains(roleC))
			{
				for ( String r2 : allParents.keySet())
				{
					Double n = (Double) parents.get(r2);
					if (n == null)
						n = 0.0;
					if (funcRoles.contains(r2) && (1 == n))
						funcRoles.add(roleC);
				}
			}
		}
	}


	// Used in the computation of the transitive closure of the RIAs
	private void addParentRecursively(String roleC, Hashtable<String,Double> allParents, String currentRole, Double n1)
	{				
		Hashtable<String, Double> parents = rolesWithParents.get(currentRole);
		for (String parent : parents.keySet())
		{
			Double n2 = (Double) parents.get(parent);

			if(roleC.compareTo(parent) != 0)
			{
				if (!allParents.containsKey(parent))
				{
					allParents.put(parent, n1 + n2 - 1.0);
					if(rolesWithParents.containsKey(parent))
						addParentRecursively(roleC, allParents, parent, n1 + n2 - 1.0);
				}
				else
				{
					Double oldN = (Double) allParents.get(parent);
					if (n1 + n2 - 1 > oldN)
					{
						allParents.put(parent, n1 + n2 - 1);
						if(rolesWithParents.containsKey(parent))
							addParentRecursively(roleC, allParents, parent, n1 + n2 - 1.0);
					}
				}
			}
		}
	}


	// Used in the computation of the transitive closure of the RIAs
	private void createRolesWithTransChildren()
	{
		for (String roleC : rolesWithAllParents.keySet())
		{
			if(transRoles.contains(roleC))
			{
				Hashtable<String, Double> parents = rolesWithAllParents.get(roleC);

				for (String roleP : parents.keySet())
				{
					ArrayList<String> transChildren = new ArrayList<String>();
					if(rolesWithTransChildren.containsKey(roleP))
						transChildren = rolesWithTransChildren.get(roleP);
					transChildren.add(roleC);
					rolesWithTransChildren.put(roleP, transChildren);
				}
			}
		}
	}


	/**
	 * Adds a RIA (subsumer, subsumed, degree).
	 * @param subsumer Subsumer funcRole.
	 * @param subsumed Subsumed funcRole.
	 * @param n Lower bound for the degree.
	 */
	private void roleSubsumes(String subsumer, String subsumed, double n)
	{	 
		if (subsumer.equals(subsumed))
			return;

		Hashtable<String, Double> parents;

		if(rolesWithParents.containsKey(subsumed))
			parents = rolesWithParents.get(subsumed);
		else
			parents = new Hashtable<String, Double>();

		if(!parents.containsKey(subsumer))
		{
			parents.put(subsumer, n);
			rolesWithParents.put(subsumed, parents);
		}
		else
		{
			Double old = (Double) parents.get(subsumer);
			if (n > old)
				parents.put(subsumer, n);
						else return;
		}

		Util.println("Add : " + subsumed  + " ==> " + subsumer + " , " + n);
		//Util.println(subsumed  + " ==> " + rolesWithParents.get(subsumed));
	}


	/**
	 * Adds a RIA (subsumer, subsumed, degree).
	 * @param subsumer Subsumer funcRole.
	 * @param subsumed Subsumed funcRole.
	 * @param n Lower bound for the degree.
	 */
	private boolean roleSubsumesBool(String subsumer, String subsumed, double n)
	{
		//Util.println("add superole role : " + subsumer + " :: " + subsumed  + " ==> " + rolesWithParents.get(subsumed));

		if (subsumer.equals(subsumed))
			return false;

		Hashtable<String, Double> parents;

		if(rolesWithParents.containsKey(subsumed))
			parents = rolesWithParents.get(subsumed);
		else
			parents = new Hashtable<String, Double>();

		if(!parents.containsKey(subsumer))
		{
			parents.put(subsumer, n);
			rolesWithParents.put(subsumed, parents);
			//Util.println("Add role 1: ");
		}
		else
		{
			//Util.println("Add role 2: ");
			Double old = (Double) parents.get(subsumer);
			if (n > old)
				parents.put(subsumer, n);
			else
				return false;
		}

		Util.println("Add : " + subsumed  + " ==> " + subsumer + " , " + n);
		//Util.println(subsumed  + " ==> " + rolesWithParents.get(subsumed));

		return true;
	}


				
	/**
	 * Adds a RIA (subsumer, subsumed, degree) to list
	 * @param subsumer Subsumer funcRole.
	 * @param subsumed Subsumed funcRole.
	 * @param n Lower bound for the degree.
	 * @param list rolesWithParents list.
	 */
/*	private void roleSubsumes(String subsumer, String subsumed, double n, Hashtable<String, Hashtable<String,Double>> list)
	{
		if (subsumer.equals(subsumed))
			return;
				
		Hashtable<String, Double> parents;
		if(list.containsKey(subsumed))
			parents = list.get(subsumed);
		else
			parents = new Hashtable<String, Double>();

		if(!parents.containsKey(subsumer))
		{
			parents.put(subsumer, n);
			list.put(subsumed, parents);
		}
		else
		{
			Double old = (Double) parents.get(subsumer);
			if (n > old)
				parents.put(subsumer, n);
		}

		Util.println("Add tmp : " + subsumed  + " ==> " + subsumer + " , " + n);
		//Util.println(subsumed  + " ==> " + list.get(subsumed));
	}
*/


	/**
	 * Adds a RIA (subsumer, subsumed, degree) to list
	 * @param subsumer Subsumer funcRole.
	 * @param subsumed Subsumed funcRole.
	 * @param n Lower bound for the degree.
		 * @param list rolesWithParents list.
	 */
	private boolean roleSubsumesBool(String subsumer, String subsumed, double n, Hashtable<String, Hashtable<String,Double>> list)
	{
		if (subsumer.equals(subsumed))
			return false;

		Hashtable<String, Double> parents;
		if(list.containsKey(subsumed))
			parents = list.get(subsumed);
		else
			parents = new Hashtable<String, Double>();

		if(!parents.containsKey(subsumer))
		{
			parents.put(subsumer, n);
			list.put(subsumed, parents);
		}
		else
		{
			Double old = (Double) parents.get(subsumer);
			if (n > old)
				parents.put(subsumer, n);
			else
				return false;
		}

		Util.println("Add tmp : " + subsumed  + " ==> " + subsumer + " , " + n);
		//Util.println(subsumed  + " ==> " + list.get(subsumed));
	
		return true;
	}



	/**
	 * Unblocks the children of the individual with the given name.
	 * @param ancestor Name of the ancestor individual.
	 */
	void unblockChildren(String ancestor)
	{
		// Directly blocked children
		ArrayList<String> dbChildren = directlyBlockedChildren.get(ancestor);
		if (dbChildren == null)
			return;

		directlyBlockedChildren.remove(ancestor);

		for (String name : dbChildren)
		{
			CreatedIndividual dbChild = (CreatedIndividual) individuals.get(name);

			// Mark the nodes that are directly blocked as uncheck and put back the "some" assertions
			dbChild.unblockDirectlyBlocked(this);
	
			// Mark the nodes that are indirectly blocked as uncheck and put back the "some"  assertions
			dbChild.markIndirectlySimpleUnChecked(this);
		}
	}


	/**
	 * Unblocks the individual and descendants of the individual with the given name.
	 * @param ancestor Name of the ancestor individual.
	 */
	void unblockIndividual(String nodeName)
	{
		CreatedIndividual node = (CreatedIndividual) individuals.get(nodeName);

		// Mark the nodes that are directly blocked as uncheck and put back the "some" assertions
		node.unblockDirectlyBlocked(this);

		// Mark the nodes that are indirectly blocked as uncheck and put back the "some"  assertions
		node.markIndirectlySimpleUnChecked(this);
	}


	/**
	 * Checks if transitivity has been applied to a universal restriction.
	 * @param rel A relation.
	 * @param restric A restriction.
	 * @return true if the transitivity rule has been applied; false otherwise.
	 */
	boolean checkTransRoleApplied(Relation rel, Restriction restric)
	{
		boolean alreadyApplied = false;
		String rule = rel.toString() + " " + restric.getNameWithoutDegree();
		if(appliedTransRoleRules.contains(rule))
			alreadyApplied = true;
		else
			appliedTransRoleRules.add(rule);
		Util.println("checking rule applied " + rule + " is " + alreadyApplied);
		return alreadyApplied;
	}


	/**
	 * Adds a datatype restriction of the form (restrictionType, fName, o).
	 * @param restrictionType Type of the datatype restriction.
	 * @param o Value of the datatype restriction.
	 * @param fName Concrete feature.
	 * @return A datatype restriction.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public Concept addDatatypeRestriction(int restrictionType, Object o, String fName) throws FuzzyOntologyException
	{
		// Check that feature exists
		ConcreteFeature t = (ConcreteFeature) concreteFeatures.get(fName);
		if (t == null)
			Util.error("Error: Concrete feature " + fName + " is not defined");

		// In functions of the form (number), we replace with a double number for efficiency
		if (o instanceof FeatureFunction)
		{
			FeatureFunction fun = (FeatureFunction) o;
			int fType = fun.getType();
			if (fType == FeatureFunction.ATOMIC)
			{
				String name = fun.toString();
				TriangularFuzzyNumber tfn =  fuzzyNumbers.get(name);
				if (tfn != null)
					o = tfn;
				// Begin Umberto
				else {
					Boolean bv = milp.hasVariable(name);
					if (bv)
						o = milp.getVariable(name);
				// End Umberto					
				}
			}
			else if (fType== FeatureFunction.NUMBER)
				o = fun.getNumber();
		}

		// Check type is coherent with o
		int type = t.getType();

		if (! (o instanceof Variable))
		{
			switch (type)
			{
				case ConcreteFeature.STRING:
					if (! (o instanceof String))
						return Concept.CONCEPT_BOTTOM;
//						Util.error("Error: Found " + o + " instead of a string value.");
					tempStringList.add((String) o);
					break;

				case ConcreteFeature.INTEGER:
					// begin Umberto
					if (! (o instanceof Double) && ! (o instanceof FeatureFunction) && ! (o instanceof TriangularFuzzyNumber))						
						return Concept.CONCEPT_BOTTOM;
					break;
					// end Umberto

				case ConcreteFeature.REAL:								
					if (! (o instanceof Double) && ! (o instanceof TriangularFuzzyNumber) && ! (o instanceof FeatureFunction))
						return Concept.CONCEPT_BOTTOM;
					break;

				case ConcreteFeature.BOOLEAN:
					if (o instanceof String)
						Util.error("Error: Found \"" + o + "\" instead of a boolean value.");
					if (! o.toString().equals("true") && ! o.toString().equals("false"))
						Util.error("Error: Found " + o + " instead of a boolean value.");
					if (restrictionType != Concept.EXACT_VALUE)
						Util.error("Error: Only = restrictions are allowed for boolean values.");
					if (o.toString().equals("true"))
						o = Boolean.TRUE;
					else
						o = Boolean.FALSE;
			}
		}
		
		// Create concept
		Concept c;
		if (restrictionType == Concept.AT_MOST_VALUE)
			c = Concept.atMostValue(fName, o);
		else if (restrictionType == Concept.AT_LEAST_VALUE)
			c = Concept.atLeastValue(fName, o);
		else // if (restrictionType == Concept.EXACT_VALUE)
			c = Concept.exactValue(fName, o);

		// Mark concept containing a string restriction (for a later replacing)
		if (type == ConcreteFeature.STRING)
			tempStringConceptList.add(c);

		return c;
	}


	/**
	 * Gets the language of the fuzzy KB, from ALC to SHIF(D).
	 * @return Language of the fuzzy KB.
	 */
	public String getLanguage()
	{
		return language;
	}


	/**
	 * Computes the language of the fuzzy KB, from ALC to SHIF(D).
	 */
	private void computeLanguage()
	{
		if (!transRoles.isEmpty())
			language = "S";
		else
			language = "ALC";

		if (!rolesWithParents.isEmpty() || !symmetricRoles.isEmpty())
			language += "H";

		if (hasNominalsInTBox() || hasNominalsInABox() )
			language += "B";
		
		if (!invFuncRoles.isEmpty() || !invRoles.isEmpty() || !symmetricRoles.isEmpty())
			language += "I";

		if (!invFuncRoles.isEmpty() || !funcRoles.isEmpty())
			language += "F";

		if (concreteFuzzyConcepts)
			language += "(D)";

		Util.println("  expressivity = " + language);

		milp.setNominalVariables(language.contains("B") || hasFunctionalAbstractRoles());
	}
	
	private boolean hasFunctionalAbstractRoles()
	{
		for (String f : funcRoles)
			if (abstractRoles.contains(f))
				return true;
		return false;
	}


	/**
	 * Checks if the ABox contains the b-some constructor.
	 * @return true if the ABox contains the b-some constructor; false otherwise.
	 */
	private boolean hasNominalsInABox()
	{
		for (Assertion ass : assertions)
			if (ass.getConcept().hasNominals())
				return true;

		return false;
	}


	/**
	 * Checks if the TBox contains the b-some constructor.
	 * @return true if the TBox contains the b-some constructor; false otherwise.
	 */
	boolean hasNominalsInTBox()
	{
		for (HashSet<Concept> equivs : axiomsAequivC.values())
			for (Concept c : equivs)
				if (c.hasNominals())
					return true;

		for (HashSet<PrimitiveConceptDefinition> pcds : axiomsAisaC.values())
			for (PrimitiveConceptDefinition pcd : pcds)
				if (pcd.getDefinition().hasNominals())
					return true;

		for (ConceptEquivalence equiv : axiomsCequivD)
			if (equiv.getC1().hasNominals() || equiv.getC2().hasNominals())
				return true;

		for (HashSet<GeneralConceptInclusion> gcis : axiomsCisaA.values())
			for (GeneralConceptInclusion gci : gcis)
				if (gci.getSubsumed().hasNominals() )
					return true;

		for (HashSet<GeneralConceptInclusion> gcis : axiomsCisaD.values())
			for (GeneralConceptInclusion gci : gcis)
				if (gci.getSubsumed().hasNominals() || gci.getSubsumer().hasNominals())
					return true;

		for (GeneralConceptInclusion gci : tG)
			if (gci.getSubsumed().hasNominals() || gci.getSubsumer().hasNominals())
				return true;

		for (Concept c : tDef.values())
			if (c.hasNominals())
				return true;

		for (HashSet<PrimitiveConceptDefinition> pcds : tInc.values())
			for (PrimitiveConceptDefinition pcd : pcds)
				if (pcd.getDefinition().hasNominals())
					return true;
		
		return false;
	}


	/**
	 * Computes the type of the blocking in {NO_BLOCKING, SUBSET_BLOCKING, SET_BLOCKING, (ANYWHERE) DOUBLE_BLOCKING}.
	 * If the type is in {SUBSET_BLOCKING, SET_BLOCKING, (ANYWHERE) DOUBLE_BLOCKING}, it also computes whether it is dynamic or not.
	 */
	private void computeBlockingType()
	{
		Util.println("\n - Blocking Type--- ");
		if (ConfigReader.OPTIMIZATIONS == 0)
		{
			blockingType = DOUBLE_BLOCKING;
			blockingDynamic = true;
			Util.println("No optimization: DOUBLE_BLOCKING + dynamicblocking");
			return;
		}

		if (invRoles.isEmpty() || funcRoles.isEmpty())
		{
			if ( tG.isEmpty() && isTBoxAcyclic() )
			{
				blockingType = NO_BLOCKING;
				Util.println("NO_BLOCKING");
			}
			else
			{
				blockingDynamic = ! invRoles.isEmpty() || ! domainRestrictions.isEmpty();
				Util.println("Dynamic Blocking = " + blockingDynamic);
				if (transRoles.isEmpty() && funcRoles.isEmpty())
				{
					// if ((ConfigReader.ANYWHERE_SIMPLE_BLOCKING) && (!blockingDynamic))
					if (ConfigReader.ANYWHERE_SIMPLE_BLOCKING)	
					{
/*						//blockingType = ANYWHERE_SUBSET_BLOCKING;
						//Util.println("ANYWHERE_SUBSET_BLOCKING");
						blockingType = ANYWHERE_SET_BLOCKING;
						Util.println("ANYWHERE_SET_BLOCKING");
*/
						// Begin Umberto
						if (!blockingDynamic)
						{
							blockingType = ANYWHERE_SUBSET_BLOCKING;
							Util.println("ANYWHERE_SUBSET_BLOCKING");
						} else	
						{
							blockingType = ANYWHERE_SET_BLOCKING;
						    Util.println("ANYWHERE_SET_BLOCKING");
						}
						// End Umberto
					}
					else
					{
						blockingType = SUBSET_BLOCKING;
						Util.println("SUBSET_BLOCKING");
					}		
				}
				else
				{
					// if ((ConfigReader.ANYWHERE_SIMPLE_BLOCKING) && (!blockingDynamic))
					if (ConfigReader.ANYWHERE_SIMPLE_BLOCKING)
					{
						blockingType = ANYWHERE_SET_BLOCKING;
						Util.println("ANYWHERE_SET_BLOCKING");
					}
					else
					{
						blockingType = SET_BLOCKING;
						Util.println("SET_BLOCKING");
					}
				}
			}
		}
		else
		{
			if (!ConfigReader.ANYWHERE_DOUBLE_BLOCKING)
			{
				blockingType = DOUBLE_BLOCKING;
				blockingDynamic = true;
				Util.println("DOUBLE_BLOCKING + dynamicblocking");
			}
			else
			{
				blockingType = ANYWHERE_DOUBLE_BLOCKING;
				blockingDynamic = true;
				Util.println("ANYWHERE PAIRWISE BLOCKING + dynamicblocking");
			}
		}
	}


	/**
	 * Transforms string datatype restrictions into integer datatype restrictions.
	 */
	private void convertStringsIntoIntegers()
	{
		if (tempStringList != null)
		{
			// Sort strings
			Collections.sort(tempStringList);
	
			// Get set of strings in assertions	
			int numStrings = 0;	
	
			Iterator<String> it = tempStringList.iterator();
			if (it.hasNext())
			{
				String previous = it.next();
				order.put(previous, new Integer(++numStrings));
	
				while (it.hasNext())
				{
					String current = it.next();
					if (current.equals(previous) == false)
						order.put(current, new Integer(++numStrings));
					previous = current;
				}
			}
	
			// If there are strings
			if (numStrings > 0)
			{			
				// Change the type of the concrete features from String to Integer
				for (ConcreteFeature t : concreteFeatures.values())
				{
					if (t.getType() == ConcreteFeature.STRING)
					{
						t.setType(ConcreteFeature.INTEGER);
						t.setRange(new Integer(0), new Integer(numStrings + 1));
					}
		 		}
	
				// Replace string s_i with order(s_i)
				for (Concept con : tempStringConceptList)
				{
					String oldValue = (String) con.getValue();
					int aux = ((Integer) order.get(oldValue)).intValue();
					con.setValue(new Double(aux));
					milp.addStringValue(oldValue, aux-1);
				}			 
			}
	
			tempStringConceptList = null;
			tempStringList = null;
		}
	}


	/**
	 * Restricts the range of a variable to [k1, k2].
	 * 
	 * @param xB A variable.
	 * @param k1 Lower bound for the range.
	 * @param k2 Upper bound for the range.
	 */
	void restrictRange(Variable xB, double k1, double k2)
	{
		milp.addNewConstraint(new Expression(-k1, new Term(1,xB)), Inequation.GE);
		milp.addNewConstraint(new Expression(-k2, new Term(1,xB)), Inequation.LE);
	}


	/**
	 * Restricts the range of a variable to [k1, k2] if xF not zero
	 * 
	 * @param xB A variable.
	 * @param xF A variable.
	 * @param k1 Lower bound for the range.
	 * @param k2 Upper bound for the range.
	 */
	void restrictRange(Variable xB, Variable xF, double k1, double k2)
	{
		milp.addNewConstraint(new Expression(MAXVAL, new Term(1, xB), new Term(-k1, xF), new Term(-MAXVAL, xF)), Inequation.GE); // xB \geq k1
		milp.addNewConstraint(new Expression(-MAXVAL, new Term(1, xB), new Term(-k2, xF), new Term(MAXVAL, xF)), Inequation.LE); // xB \leq k2
	}


	CreatedIndividual getNewIndividual() throws InconsistentOntologyException
	{
		return getNewIndividual(null, null);
	}

	
	private CreatedIndividual getNewIndividualCommonCode(Individual parent, String fName)
	{
		numDefinedInds++;
		String indName = Individual.DEFAULT_NAME + numDefinedInds;
		CreatedIndividual b = new CreatedIndividual(indName, parent, fName, this);
		if (b.depth > maxDepth)
			maxDepth = b.depth;
		return b;
	}


	CreatedIndividual getNewIndividual(Individual parent, String fName) throws InconsistentOntologyException
	{
		CreatedIndividual b = getNewIndividualCommonCode(parent, fName);
		addIndividual(b.toString(), b);
		return b;
	}


	CreatedIndividual getNewConcreteIndividual(Individual parent, String fName) throws InconsistentOntologyException
	{
		CreatedIndividual b = getNewIndividualCommonCode(parent, fName);
		b.setConcreteIndividual();
		addCreatedIndividual(b.toString(), b);
		return b;
	}


	// Solves one existential assertion
	private void solveOneExistAssertion() throws FuzzyOntologyException, InconsistentOntologyException
	{
		while (existAssertions.isEmpty() == false)
		{
			Assertion ass = existAssertions.get(0);
			Util.println("\n --------- Processing Existential Assertion ----------------- ");
			Util.println("-> " + ass);

			if (isAssertionProcessed(ass))			
			{
				Util.println("\n	 Assertion (without the degree): " + ass + " already processed ");
				existAssertions.remove(0);
				continue;
			}

			if(ass.getIndividual().isBlockable())
			{
				CreatedIndividual subject = (CreatedIndividual) ass.getIndividual();
				Util.println("---> Testing if created individual " + subject.toString() + " is blocked");
				if(subject.isBlocked(this))
				{
					String name = ass.getIndividual().toString();
					ArrayList<Assertion> indExistAssertions = blockedExistAssertions.get(name);
					if(indExistAssertions == null)
						indExistAssertions = new ArrayList<Assertion>();

					indExistAssertions.add(ass);
					blockedExistAssertions.put(name, indExistAssertions);
					existAssertions.remove(0);
					continue;
				}
			}
			if(numDefinedInds == ConfigReader.MAX_INDIVIDUALS)
				Util.error("Error: Maximal number of individuals created: " + numDefinedInds);
			else
			{
				Util.println("\n NO blocking ");
				ruleSome(ass);
			}
			markProcessAssertion(ass);
			existAssertions.remove(0);
			return;
		}
	}


	/**
	 * Prepares the fuzzy knowledge base to reason with it.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public void solveKB() throws FuzzyOntologyException, InconsistentOntologyException
	{
		if (semantics == null)
			setLogic(FuzzyLogic.LUKASIEWICZ);

		if (ConfigReader.SHOW_VERSION)
			System.out.println("Version: " + getVersion());

		computeLanguage();

		convertStringsIntoIntegers();
		solveInverseRoles();
		solveRoleInclusionAxioms();
		solveReflexiveRoles();
		solveFunctionalRoles();

		preprocessTbox();
		printTBox();
		computeBlockingType();
			
		KB_LOADED = true;
	}


	/**
	 * Solves all the domain and range restrictions.
	 */
	private void solveDomainAndRangeAxioms() throws InconsistentOntologyException
	{
		for (Individual ind : individuals.values())
		{
			for(ArrayList<Relation> rels : ind.roleRelations.values())
				for (Relation rel : rels)
				{
					for (String domainRole : domainRestrictions.keySet() )
						ruleDomainLazyUnfolding(domainRole, rel);
					for (String rangeRole : rangeRestrictions.keySet() )
						ruleRangeLazyUnfolding(rangeRole, rel);
				}
		}
	}


	void ruleDomainLazyUnfolding(String domainRole, Relation rel) throws InconsistentOntologyException
	{
		String role = rel.getRoleName();
		double n = getInclusionDegree(role, domainRole);
		if (domainRole.equals(role))
			n = 1;
		if (n > 0)
		{
			Individual a = rel.getSubjectIndividual();

			// If the individual is indirectly blocked, exit
			if ( (a.isBlockable()) &&
				 ( (CreatedIndividual) a).isIndirectlyBlocked(this) 
			)
				return;

			for (Concept c : domainRestrictions.get(domainRole))
			{
				Variable aIsC = milp.getVariable(a, c);
				Variable xRel = milp.getVariable(rel);
				addAssertion(a, c, Degree.getDegree(aIsC));

				if (semantics == FuzzyLogic.LUKASIEWICZ)
					LukasiewiczSolver.andGeqEquation(aIsC, xRel, n, milp);
				else
					ZadehSolver.andGeqEquation(aIsC, xRel, n, milp);
			}
		}
	}


	void ruleRangeLazyUnfolding(String rangeRole, Relation rel) throws InconsistentOntologyException
	{
		String role = rel.getRoleName();
		double n = getInclusionDegree(role, rangeRole);
		if (rangeRole.equals(role))
			n = 1;
		if (n > 0)
		{
			Individual b = rel.getObjectIndividual();

			// If the individual is indirectly blocked, exit
			if ( b.isBlockable() &&
				 ( (CreatedIndividual) b).isIndirectlyBlocked(this) 
			)
				return;

			for (Concept c : rangeRestrictions.get(rangeRole))
			{
				Variable bIsC;
				if (c instanceof NegatedNominal)
				{
					NegatedNominal nn = (NegatedNominal) c;
					bIsC = milp.getNegatedNominalVariable(b.toString(), nn.indName);
				}
				else
				{
					bIsC = milp.getVariable(b, c);
					addAssertion(b, c, Degree.getDegree(bIsC));
				}
				Variable xRel = milp.getVariable(rel);

				if (semantics == FuzzyLogic.LUKASIEWICZ)
					LukasiewiczSolver.andGeqEquation(bIsC, xRel, n, milp);
				else
					ZadehSolver.andGeqEquation(bIsC, xRel, n, milp);
			}
		}
	}


	/**
	 * Solves all the fuzzy assertions.
	 */
	void solveABox() throws FuzzyOntologyException, InconsistentOntologyException
	{
		if (ABOX_EXPANDED == false)
		{
			solveAssertions();
			ABOX_EXPANDED = true;
		}
	}


	/**
	 * Solves all the fuzzy assertions.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public void solveAssertions() throws FuzzyOntologyException, InconsistentOntologyException
	{	
		if (KB_UNSAT)
			throw new InconsistentOntologyException("Unsatisfiable fuzzy KB");

		// We will exit only after solving all assertions
		boolean exit = false;

		do
		{
			for (int i=0; i<assertions.size(); i++)
			{
				Assertion ass = assertions.get(i);

				Util.println("\n--------- Processing assertion ------------------------- ");
				Util.println("--> " + ass );

				Degree deg = ass.getLowerLimit();
				if (deg.isNumeric() && deg.isNumberZero())
				{
					markProcessAssertion(ass);
					Util.println("--------- Assertion completed ------------------ ");
					continue;
				}

				// Use right version of the individual (needed when we clone the KB or merge individuals)
				getCorrectVersionOfIndividual(ass);

				if (ass.getIndividual().isBlockable())
				{
					Util.println("  Direct Blocking status " + ((CreatedIndividual) ass.getIndividual()).directlyBlocked);
					Util.println("  Indirect Blocking status " + ((CreatedIndividual) ass.getIndividual()).indirectlyBlocked);
				};

				// If the individual is indirectly blocked we skip the assertion
				if ( (ass.getIndividual().isBlockable()) &&
					 ( (CreatedIndividual) ass.getIndividual()).isIndirectlyBlocked(this) )
				{
					String name = ass.getIndividual().toString();
					Util.println(" Skipping assertion (it has an indirectly blocked individual)");
					ArrayList<Assertion> indAssertions = blockedAssertions.get(name);
					if(indAssertions == null)
						indAssertions = new ArrayList<Assertion>();

					indAssertions.add(ass);
					blockedAssertions.put((ass.getIndividual()).toString(), indAssertions);

					continue;
				}

				// Add xAss >= lowerBound
				milp.addNewConstraint(ass);
				
				if (isAssertionProcessed(ass))
				{
					Util.println("Assertion (without the degree): " + ass + " already processed");
					continue;
				}

				Individual ind = ass.getIndividual();
				Concept ci = ass.getConcept();
				addNegatedEquations(ind, ci);
				
				// Apply reasoning rule according to the type of the assertion
				switch (ass.getType())
				{
					case Concept.ATOMIC:
						ruleAtomic(ass);
						break;

					// Atomic but complemented
					case Concept.COMPLEMENT:
						ruleComplementedAtomic(ass);
						break;

					case Concept.AND:
						ruleAnd(ass);
						break;

					case Concept.OR:
						ruleOr(ass);
						break;

					case Concept.SOME:
					case Concept.HAS_VALUE:
						existAssertions.add(ass);
						continue;

					case Concept.ALL:
						ruleAll(ass);
						break;

					case Concept.NOT_HAS_VALUE:
						ruleComplementedHasValue(ass);
						break;

					case Concept.CONCRETE:
						ruleConcrete(ass);
						break;

					case Concept.CONCRETE_COMPLEMENT:
						ruleComplementedConcrete(ass);
						break;

					case Concept.FUZZY_NUMBER:
						ruleFuzzyNumber(ass);
						break;

					case Concept.FUZZY_NUMBER_COMPLEMENT:
						ruleComplementedFuzzyNumber(ass);
						break;

					case Concept.MODIFIED:
						ruleModified(ass);
						break;

					case Concept.MODIFIED_COMPLEMENT:
						ruleComplementedModified(ass);
						break;

					case Concept.BOTTOM:
						ruleBottom(ass);
						break;

					case Concept.TOP:
						ruleTop(ass);
						break;

					case Concept.AT_MOST_VALUE:
					case Concept.AT_LEAST_VALUE:
					case Concept.EXACT_VALUE:
						positiveConcreteValueAssertions.add(ass);
						break;

					case Concept.NOT_AT_MOST_VALUE:
					case Concept.NOT_AT_LEAST_VALUE:
					case Concept.NOT_EXACT_VALUE:
						addNegatedDatatypeRestriction(ass);
						break;

					case Concept.SELF:
						ruleSelf(ass);
						break;

					case Concept.NOT_SELF:
						ruleComplementedSelf(ass);
						break;

					case Concept.UPPER_APPROX:
						ruleUpperApproximation(ass);
						break;

					case Concept.LOWER_APPROX:
						ruleLowerApproximation(ass);
						break;

					case Concept.TIGHT_UPPER_APPROX:
						ruleTightUpperApproximation(ass);
						break;

					case Concept.TIGHT_LOWER_APPROX:
						ruleTightLowerApproximation(ass);
						break;

					case Concept.LOOSE_UPPER_APPROX:
						ruleLooseUpperApproximation(ass);
						break;

					case Concept.LOOSE_LOWER_APPROX:
						ruleLooseLowerApproximation(ass);
						break;

					case Concept.G_AND:
						ruleGoedelAnd(ass);
						break;

					case Concept.G_OR:
						ruleGoedelOr(ass);
						break;
					
					case Concept.L_AND:
						ruleLukasiewiczAnd(ass);
						break;

					case Concept.L_OR:
						ruleLukasiewiczOr(ass);
						break;

					case Concept.G_IMPLIES:
						ruleGoedelImplication(ass);
						break;

					case Concept.NOT_G_IMPLIES:
						ruleComplementedGoedelImplication(ass);
						break;

					case Concept.Z_IMPLIES:
						ruleZadehImplication(ass);
						break;

					case Concept.NOT_Z_IMPLIES:
						this.ruleComplementedZadehImplication(ass);
						break;

					case Concept.W_SUM:
						ruleWeightedSum(ass);
						break;

					case Concept.NOT_W_SUM:
						ruleComplementedWeightedSum(ass);
						break;

					case Concept.W_SUM_ZERO:
						ruleWeightedSumZero(ass);
						break;

					case Concept.NOT_W_SUM_ZERO:
						ruleComplementedWeightedSumZero(ass);
						break;

					case Concept.WEIGHTED:
						ruleWeightedConcept(ass);
						break;

					case Concept.NOT_WEIGHTED:
						ruleComplementedWeighted(ass);
						break;

					case Concept.POS_THRESHOLD:
						rulePositiveThreshold(ass);
						break;

					case Concept.NOT_POS_THRESHOLD:
						ruleComplementedPositiveThreshold(ass);
						break;

					case Concept.NEG_THRESHOLD:
						ruleNegativeThreshold(ass);
						break;

					case Concept.NOT_NEG_THRESHOLD:
						ruleComplementedNegativeThreshold(ass);
						break;

					case Concept.EXT_POS_THRESHOLD:
						ruleExtendedPositiveThreshold(ass);
						break;

					case Concept.NOT_EXT_POS_THRESHOLD:
						ruleComplementedExtendedPositiveThreshold(ass);
						break;

					case Concept.EXT_NEG_THRESHOLD:
						ruleExtendedNegativeThreshold(ass);
						break;

					case Concept.NOT_EXT_NEG_THRESHOLD:
						ruleComplementedExtendedNegativeThreshold(ass);
						break;

					case Concept.OWA:
						ruleOwa(ass);
						break;

					case Concept.NOT_OWA:
						ruleComplementedOwa(ass);
						break;

					case Concept.QUANTIFIED_OWA:
						ruleQuantifiedOwa(ass);
						break;

					case Concept.NOT_QUANTIFIED_OWA:
						ruleComplementedQuantifiedOwa(ass);
						break;

					case Concept.CHOQUET_INTEGRAL:
						ruleChoquet(ass);
						break;

					case Concept.NOT_CHOQUET_INTEGRAL:
						ruleComplementedChoquet(ass);
						break;

					case Concept.SUGENO_INTEGRAL:
						ruleSugeno(ass);
						break;

					case Concept.NOT_SUGENO_INTEGRAL:
						ruleComplementedSugeno(ass);
						break;

					case Concept.QUASI_SUGENO_INTEGRAL:
						ruleQuasiSugeno(ass);
						break;

					case Concept.NOT_QUASI_SUGENO_INTEGRAL:
						ruleComplementedQuasiSugeno(ass);
						break;

					case Concept.W_MIN:
						ruleWeightedMin(ass);
						break;

					case Concept.NOT_W_MIN:
						ruleComplementedWeightedMin(ass);
						break;

					case Concept.W_MAX:
						ruleWeightedMax(ass);
						break;

					case Concept.NOT_W_MAX:
						ruleComplementedWeightedMax(ass);
						break;

					case Concept.SIGMA_CONCEPT:
						ruleSigmaCount(ass);
						break;

					case Concept.NOT_SIGMA_CONCEPT:
						ruleComplementedSigmaCount(ass);
						break;

					default:
						Util.println("Warning: Assertion with type " + ass.getType());
				}

				// For each node in labelsWithNodes, apply AssNom rule
				Set<String> nodes = labelsWithNodes.get(ind.toString());
				if (nodes != null)
					for (String node : nodes)
						ruleAssNom(ind, ci, node);

				//Util.println("Adding " + ass.getNameWithoutDegree() + " to processedAssertions");
				markProcessAssertion(ass);
				ind.addConcept(ci);
				Util.println("--------- Assertion completed ------------------ ");
			}

			assertions.clear();

			// Solve one some rule
			if (assertions.isEmpty())
				solveOneExistAssertion();

			// Check if there are  more assertions
			if ( assertions.isEmpty() && existAssertions.isEmpty() )
				exit = true;

		} while (exit == false);

		// Concrete assertions
		solveConcreteValueAssertions();
	}


	private void addNegatedDatatypeRestriction(Assertion ass) throws InconsistentOntologyException
	{
		Individual a = ass.getIndividual();
		String fName = ass.getConcept().getRole();
		a.addConcreteRestriction(fName, ass);
	}


	private void ruleN2()
	{
		for (Individual o : individuals.values())
		{
			String oName = o.toString();
			Set<Variable> vars = new HashSet<Variable> (); 
			
			if ( ( (o instanceof CreatedIndividual) == false) && (o.getNominalList().isEmpty() == false) )
			{
				// Add xOisO to the list only O if is not a created individual
				Variable xOisO = milp.getNominalVariable(oName, oName);
				vars.add(xOisO);
			}

			for (String bName : o.getNominalList() )
			{
				Variable xOisB = milp.getNominalVariable(oName, bName);
				vars.add(xOisB);
			}

			// The sum must be small or equal than 1
			if (vars.size() >= 2)
			{
				Expression sumVars = new Expression(vars);
				milp.addNewConstraint(Expression.addConstant(sumVars, -1.0), Inequation.LE);
				Util.println("ruleN2: " + sumVars + " <= 1");
			}
		}
	}


	private void ruleN3()
	{
		for (String oName : labelsWithNodes.keySet())
		{
			Set<String> nodes = labelsWithNodes.get(oName);
			if (( nodes != null) && (nodes.size() > 1) )
			{
				Vector<Variable> v = new Vector<Variable> ();
				for (String node : nodes)
					v.add(milp.getNominalVariable(node, oName));

				// x_{v1:{o}} + ... + x_{vn:{o}} = 1 
				Expression exp = new Expression(v);
				exp.setConstant(-1);
				milp.addNewConstraint(exp, Inequation.EQ);
				Util.println("ruleN3: " + exp);
			}
		}
	}





	/**
	 * Applies the rule AssNom to a node v and an assertion <a : C>
	 * @param a Individual of an assertion.
	 * @param c Concept of an assertion.
	 * @param v Node that is an a-node.
	 * @throws InconsistentOntologyException
	 */
	private void ruleAssNom(Individual a, Concept c, String v) throws InconsistentOntologyException
	{
		String aName = a.toString();
		Individual i = getIndividual(v);
		Variable aIsC = milp.getVariable(a, c);
		Variable vIsA = milp.getNominalVariable(v, aName);
		Variable vIsC = milp.getVariable(i, c);

		// Add the assertion "v" is c
		addAssertion(i, c, Degree.getDegree(vIsC));

		// vIs{a}  =>  v:C  >=  a:C  
		Util.println("Adding equation " + vIsA + " => " + vIsC + " >= " + aIsC);
		ZadehSolver.zImpliesLeqEquation(aIsC, vIsA, vIsC, milp);
	}


	private boolean existsPrimiteConceptDefinition(HashSet<PrimitiveConceptDefinition> pcds, PrimitiveConceptDefinition pcd)
	{
		Concept c =  pcd.getDefinition();
		for (PrimitiveConceptDefinition p : pcds)
			if (p.getDefinition().toString().equals(c.toString()))
			{
				double oldDegree = p.getDegree();
				double newDegree = pcd.getDegree();
				if (newDegree > oldDegree)
					pcd.setDegree(newDegree);
				return true;
			}
		return false;
	}


	private void addAxiomToInc(String a, PrimitiveConceptDefinition pcd)
	{
		Concept c =  pcd.getDefinition();		
		int type = pcd.getType();		
		double n = pcd.getDegree();
		
		if (isRedundantAisaC(a, c, type, n))
			return;

		HashSet<PrimitiveConceptDefinition> pcds = tInc.get(a);
		if (pcds == null)
			pcds = new HashSet<PrimitiveConceptDefinition> ();
		else
			if (existsPrimiteConceptDefinition(pcds, pcd))
				return;

		pcds.add(pcd);
		tInc.put(a, pcds);
	}


	private void addAxiomToDoAisaX(String a, PrimitiveConceptDefinition pcd)
	{
		Concept c =  pcd.getDefinition();
		int type = pcd.getType();
		double n = pcd.getDegree();

		if (isRedundantAisaC(a, c, type, n))
			return;

		//Util.println("addAxiomToDoAisaX :  "  + pcd);

		HashSet<PrimitiveConceptDefinition> pcds;
		if (c.isAtomic())
			pcds = axiomsToDoAisaB.get(a);
		else
			pcds = axiomsToDoAisaC.get(a);

		if (pcds == null)
			pcds = new HashSet<PrimitiveConceptDefinition> ();
		else
			if (existsPrimiteConceptDefinition(pcds, pcd))
				return;
		
		pcds.add(pcd);

		if (c.isAtomic())
			axiomsToDoAisaB.put(a, pcds);
		else
			axiomsToDoAisaC.put(a, pcds);
	}


	private void addAxiomToAisaC(String a, PrimitiveConceptDefinition pcd, Hashtable<String, HashSet<PrimitiveConceptDefinition>> list)
	{
		Concept c =  pcd.getDefinition();
		int type = pcd.getType();
		double n = pcd.getDegree();

		if (isRedundantAisaC(a, c, type, n))
			return;

		HashSet<PrimitiveConceptDefinition> pcds = list.get(a);
		if (pcds == null)
			pcds = new HashSet<PrimitiveConceptDefinition> ();
		else
			if (existsPrimiteConceptDefinition(pcds, pcd))
				return;

		pcds.add(pcd);
		list.put(a, pcds);
	}


	private void addAxiomToAequivC(String a, Concept conc) throws FuzzyOntologyException
	{
		if (conc.isConcrete())
			Util.error(conc + " is concrete and cannot appear in a TBox axiom");
		
		HashSet<Concept> hs = axiomsAequivC.get(a);
		if (hs == null)
			hs = new HashSet<Concept> ();
		else
		{
			// Return if the concept already exists
			for (Concept c : hs)
				if (c.toString().equals(conc.toString()))
					return ;
		}
		if (!hs.contains(conc))
		{
			hs.add(conc);
			axiomsAequivC.put(a, hs);
		}
	}


	private void addAxiomsToTg() throws InconsistentOntologyException
	{
		for (String cname : axiomsAequivC.keySet())
		{
			Concept a = new Concept(cname);
			for (Concept b : axiomsAequivC.get(cname))
			defineEquivalentConcepts(a, b);
		}

		for (ConceptEquivalence ce : axiomsCequivD)
		{
			Concept a = ce.getC1();
			Concept b = ce.getC2();
			defineEquivalentConcepts(a, b);
		}

		// Axioms must not be cleared if we want to be able to save the KB correctly
		axiomsAequivC.clear();
		axiomsCequivD.clear();
	}

	
	// Computes if there is some disjoint(a, b) in tDis with b being a head of an axiom in Tdef
	private boolean disjointWithDefinedConcept (String a)
	{		
		for (String b : tDis.get(a))
			if (tDef.containsKey(b))
				return false;
		return true;
	}


	/**
	 * @param gc A GCI
	 * @return true if there are changes; false otherwise.
	 */
	private boolean definitionAbsorption(GeneralConceptInclusion gci)
	{
		String a = gci.getSubsumer().toString();
		String aux = gci.getSubsumed().toString();
		int implication = gci.getType();
		Degree d = gci.getDegree();
		double n = ((DegreeNumeric) d).getNumericalValue();

		//Util.println("try gci def: " + aux + " =>_" + implication + " " + a + " "+ n  );

		if ( (semantics != FuzzyLogic.CLASSICAL) && ( (n != 1) || (implication == GeneralConceptInclusion.ZADEH) ) )
			return false;

		if (!(axiomsAisaC.get(a) == null))
		for(PrimitiveConceptDefinition pcd : axiomsAisaC.get(a))
		{
			Concept conc = pcd.getDefinition();
			String c = conc.toString();

			if (
				gci.getSubsumed().toString().equals(c) &&
				gci.getSubsumer().toString().equals(a) &&
				( (semantics == FuzzyLogic.CLASSICAL) || (d.isNumeric() && (((DegreeNumeric) d).getNumericalValue() == 1.0) && (gci.getType() != GeneralConceptInclusion.KLEENE_DIENES)) ) &&
				! tDef.containsKey(a) &&
				! tInc.containsKey(a) &&
				// there is no disjoint(a, b) in tDis with b being a head of an axiom in Tdef
				! disjointWithDefinedConcept(a)
			)
			{
				// Add A = C
				tDef.put(a, conc);
	
				// Remove A => C
				removeAisaX(a, pcd, false);
	
				// Remove C => A
				removeCisaA(aux, gci);
	
				Util.println("definition absorbed :  " + a + " = " + conc);
				//absorptionCountDef +=1;
				return true;
			}
		}

		if (!(tInc.get(a) == null))
			for(PrimitiveConceptDefinition pcd : tInc.get(a))
			{
				Concept conc = pcd.getDefinition();
				String c = conc.toString();

				if (
					gci.getSubsumed().toString().equals(c) &&
					gci.getSubsumer().toString().equals(a) &&
					( 
						(semantics == FuzzyLogic.CLASSICAL) || 
						(d.isNumeric() && (((DegreeNumeric) d).getNumericalValue() == 1.0) && (gci.getType() != GeneralConceptInclusion.KLEENE_DIENES)) ) &&
						! tDef.containsKey(a) &&
						! (tInc.get(a).size() > 1 )
						// T_def needs not to be acyclic
						// && isTBoxAcyclic(a, conc)
						)
				{
					// Add A = C
					tDef.put(a, conc);

					// Remove A => C
					removeAisaX(a, pcd, tInc);

					// Remove C => A
					removeCisaA(aux, gci);

					Util.println("definition absorbed :  " + a + " = " + conc);
					//absorptionCountDef +=1;
					return true;
				}
			}

		return false;
	}


	/**
	 * @param pcd A primitive concept definition
	 * @return true if there are changes; false otherwise.
	 */
	private boolean definitionAbsorptionToDo(PrimitiveConceptDefinition pcd)
	{
		//Util.println("prim def: " + pcd);
		String a = pcd.getDefinedConcept();
		int implication = pcd.getType();
		double n = pcd.getDegree();

		if ( (semantics != FuzzyLogic.CLASSICAL) && ( (n != 1) || (implication == GeneralConceptInclusion.ZADEH) ) )
			return false;

		//Util.println("Ok: check prim def: " + pcd);
		//System.out.println("Ok: check prim def: " + pcd);
		Concept conc = pcd.getDefinition();
		String c = conc.toString();
		/*
			for(String aux : axiomsCisaA.keySet())
			{
				Set<GeneralConceptInclusion> gcis = axiomsCisaA.get(aux);
				for(GeneralConceptInclusion gci : gcis)
				{
		 */

		if (!(axiomsCisaA.get(c) == null))
			for(GeneralConceptInclusion gci : axiomsCisaA.get(c))
			{
				// Util.println("compare with : " + gci);
				Degree d = gci.getDegree();
				if (
					gci.getSubsumed().toString().equals(c) &&
					gci.getSubsumer().toString().equals(a) &&
					( (semantics == FuzzyLogic.CLASSICAL) || (d.isNumeric() && (((DegreeNumeric) d).getNumericalValue() == 1.0) && (gci.getType() != GeneralConceptInclusion.KLEENE_DIENES)) ) &&
					! tDef.containsKey(a) &&
					! (tInc.get(a).size()  > 1) 
	                // T_def needs not to be acyclic
	                // && isTBoxAcyclic(a, conc)
				)
				{						
					// Add A = C
					tDef.put(a, conc);

					// Remove A => C
					removeAisaX(a, pcd, axiomsToDoAisaC);
					removeAisaX(a, pcd, tInc);

					// Remove C => A
					removeCisaA(c, gci);

					Util.println("definition absorbed :  " + a + " = " + conc);
					return true;
				}
			}

		return false;
	}


	private void removeAisaB(String key, PrimitiveConceptDefinition pcd)
	{
		HashSet<PrimitiveConceptDefinition> pcds = axiomsAisaB.get(key);
		pcds.remove(pcd);
		if (pcds.size() == 0)
			axiomsAisaB.remove(key);
	}


	private void removeAisaX(String key, PrimitiveConceptDefinition pcd, Hashtable<String, HashSet<PrimitiveConceptDefinition>> list)
	{
		HashSet<PrimitiveConceptDefinition> pcds = list.get(key);
		pcds.remove(pcd);
		if (pcds.size() == 0)
			list.remove(key);
	}


	private void removeAisaC(String key, PrimitiveConceptDefinition pcd)
	{
		HashSet<PrimitiveConceptDefinition> pcds = axiomsAisaC.get(key);
		pcds.remove(pcd);
		if (pcds.size() == 0)
			axiomsAisaC.remove(key);
	}


	private void removeAisaX(String key, PrimitiveConceptDefinition pcd, boolean atomic)
	{
		if (atomic)
			removeAisaB(key, pcd);
		else
			removeAisaC(key, pcd);
	}


	private void removeCisaA(String key, GeneralConceptInclusion gci)
	{
		HashSet<GeneralConceptInclusion> gcis = axiomsCisaA.get(key);
		gcis.remove(gci);
		if (gcis.size() == 0)
			axiomsCisaA.remove(key);
	}


	private void removeCisaD(String key, GeneralConceptInclusion gci)
	{
		HashSet<GeneralConceptInclusion> gcis = axiomsCisaD.get(key);
		gcis.remove(gci);
		if (gcis.size() == 0)
			axiomsCisaD.remove(key);
	}


	private void removeCisaX(String key, GeneralConceptInclusion gci, boolean atomic)
	{
		if (atomic)
			removeCisaA(key, gci);
		else
			removeCisaD(key, gci);
	}


	private void gciTransformationsAisaC()
	{
		Util.println("-----gciTransformationsAisaC---");
		for(HashSet<PrimitiveConceptDefinition> gcis : axiomsToDoAisaC.values())
			for (PrimitiveConceptDefinition tau : gcis)
				if (!gciTransformation(tau))
					addAxiomToAisaC(tau.getDefinedConcept(), tau, axiomsAisaC);
	}


	private void gciTransformationsCisaA() throws InconsistentOntologyException
	{
		Util.println("-----gciTransformationsCisaA---");
		for(HashSet<GeneralConceptInclusion> gcis : axiomsToDoCisaA.values())
			for (GeneralConceptInclusion tau : gcis)
				if (!gciTransformation(tau, true))
					addAxiomToCisaA(tau.getSubsumer(), tau.getSubsumed(), tau.getDegree(), tau.getType());
	}


	private void gciTransformationsCisaD() throws InconsistentOntologyException
	{
		Util.println("-----gciTransformationsCisaD---");
		for(HashSet<GeneralConceptInclusion> gcis : axiomsToDoCisaD.values())
			for (GeneralConceptInclusion tau : gcis)
				if (!gciTransformation(tau, false))
					addAxiomToCisaD(tau.getSubsumer(), tau.getSubsumed(), tau.getDegree(), tau.getType());;
	}

	
	private void partitionLoopAisaB()
	{
		Hashtable<String, HashSet<PrimitiveConceptDefinition>> copy = new Hashtable<String, HashSet<PrimitiveConceptDefinition>>(axiomsAisaB);

		//for(HashSet<PrimitiveConceptDefinition> pcds : copy.values())
		for(String a : copy.keySet())
		{
			HashSet<PrimitiveConceptDefinition> pcdstmp = copy.get(a);
			//HashSet<PrimitiveConceptDefinition> pcds = (HashSet<PrimitiveConceptDefinition>) pcdstmp.clone();
			HashSet<PrimitiveConceptDefinition> pcds = new HashSet<PrimitiveConceptDefinition>(pcdstmp);

			//Util.println("pcds : " + pcds);

			for (PrimitiveConceptDefinition tau : pcds)
			{
				if (synonymAbsorptionAisaB(tau))
					continue;
				if (conceptAbsorption(tau, true))
					continue;
			}
		}
	}


	private void partitionLoopToDoAisaB()
	{
		Hashtable<String, HashSet<PrimitiveConceptDefinition>> copy = new Hashtable<String, HashSet<PrimitiveConceptDefinition>>(axiomsToDoAisaB);

		//for(HashSet<PrimitiveConceptDefinition> pcds : copy.values())
		for(String a : copy.keySet())
		{
			HashSet<PrimitiveConceptDefinition> pcdstmp = copy.get(a);
		   // HashSet<PrimitiveConceptDefinition> pcds = (HashSet<PrimitiveConceptDefinition>) pcdstmp.clone();
			HashSet<PrimitiveConceptDefinition> pcds = new HashSet<PrimitiveConceptDefinition>(pcdstmp);

			//Util.println("pcds : " + pcds);

			for (PrimitiveConceptDefinition tau : pcds)
			{
				if (synonymAbsorptionToDoAisaB(tau))
					continue;
				//if (conceptAbsorption(tau, true))
				//	continue;
			}
		}
	
		axiomsToDoAisaB.clear();
	}


	private void partitionLoopAisaC()
	{
		Hashtable<String, HashSet<PrimitiveConceptDefinition>> copy = new Hashtable<String, HashSet<PrimitiveConceptDefinition>>(axiomsAisaC);

		//for(HashSet<PrimitiveConceptDefinition> pcds : copy.values())
		for(String a : copy.keySet())
		{
			HashSet<PrimitiveConceptDefinition> pcdstmp = copy.get(a);
			//HashSet<PrimitiveConceptDefinition> pcds = (HashSet<PrimitiveConceptDefinition>) pcdstmp.clone();
			HashSet<PrimitiveConceptDefinition> pcds = new HashSet<PrimitiveConceptDefinition>(pcdstmp);

			//Util.println("pcds : " + pcds);

			for (PrimitiveConceptDefinition tau : pcds)
			{
				if (conceptAbsorption(tau, false))
					continue;
				if (roleAbsorption(tau))
					continue;
			}
		}
	}


	private void partitionLoopToDoAisaC()
	{
		Hashtable<String, HashSet<PrimitiveConceptDefinition>> copy = new Hashtable<String, HashSet<PrimitiveConceptDefinition>>(axiomsToDoAisaC);

		//for(HashSet<PrimitiveConceptDefinition> pcds : copy.values())
		for(String a : copy.keySet())
		{
			HashSet<PrimitiveConceptDefinition> pcdstmp = copy.get(a);
			//HashSet<PrimitiveConceptDefinition> pcds = (HashSet<PrimitiveConceptDefinition>) pcdstmp.clone();
			HashSet<PrimitiveConceptDefinition> pcds = new HashSet<PrimitiveConceptDefinition>(pcdstmp);
		
			//Util.println("pcds : " + pcds);
		
			for (PrimitiveConceptDefinition tau : pcds)
			{
				if (definitionAbsorptionToDo(tau))
					continue;
				//if (roleAbsorption(tau))
				//	conttitioninue;
			}
		}

		axiomsToDoAisaC.clear();
	}


	private void partitionLoopCisaA()
	{
		Hashtable<String, HashSet<GeneralConceptInclusion>> copy = new Hashtable<String, HashSet<GeneralConceptInclusion>>(axiomsCisaA);
		for(String a : copy.keySet())
		{
			HashSet<GeneralConceptInclusion> pcdstmp = copy.get(a);
			//HashSet<GeneralConceptInclusion> pcds = (HashSet<GeneralConceptInclusion>) pcdstmp.clone();
			HashSet<GeneralConceptInclusion> pcds = new HashSet<GeneralConceptInclusion>(pcdstmp);

			//Util.println("pcds : " + pcds);	
			for (GeneralConceptInclusion tau : pcds)
			{
				if (conceptAbsorption(tau, true))
					continue;
				
				if (definitionAbsorption(tau))
					continue;
				
				if (roleAbsorption(tau, true))
					continue;
			}
		}
	}


	private void partitionLoopCisaD() throws InconsistentOntologyException
	{
		Hashtable<String, HashSet<GeneralConceptInclusion>> copy = new Hashtable<String, HashSet<GeneralConceptInclusion>>(axiomsCisaD);
		for(String a : copy.keySet())
		{
			HashSet<GeneralConceptInclusion> pcdstmp = copy.get(a);
			//HashSet<GeneralConceptInclusion> pcds = (HashSet<GeneralConceptInclusion>) pcdstmp.clone();
			HashSet<GeneralConceptInclusion> pcds = new HashSet<GeneralConceptInclusion>(pcdstmp);

			//Util.println("pcds : " + pcds);

			for (GeneralConceptInclusion tau : pcds)
			{
				if (conceptAbsorption(tau, false))
					continue;
				if (roleAbsorption(tau, false))
					continue;
			}
		}
	}


	/**
	 * Computes if the fuzzy KB has an acyclic TBox.
	 * If not, add primitive and concept definitions as GCIs.
	 */
	private void preprocessTbox() throws InconsistentOntologyException
	{	
		// expressivity 
		// classes 
		// AisC 
		// A=C 
		// A=B 
		// dom 
		// range 
		// GCIs 
		// disj 
		// LU

		// 1. No optimizations: add every TBox axiom to tG
		// Boolean noAbs = true;
		Boolean noAbs = false;
		if ((ConfigReader.OPTIMIZATIONS == 0) || (noAbs))
		{
			Util.println("No Absorption ...  ");
			representTBoxWithGCIs();
			//printTBox();
			return;
		}

		// Phase 0
		// Check if TBOX already lazy unfoldable
		if (isLazyUnfoldable())
		{
			Util.println("Already lazy unfoldable  ");

			lazyUnfoldable = true;

			// copy axiomsAequivC into tDef                 
			for (String a: axiomsAequivC.keySet())
			{
				HashSet<Concept> hs = axiomsAequivC.get(a);
				for (Concept c : hs)
					tDef.put(a, c);
			}

			// copy axiomsAisaC and axiomsAisaB into tInc                   
			for (String a: axiomsAisaC.keySet())
			{
				HashSet<PrimitiveConceptDefinition> hs = axiomsAisaC.get(a);
				for (PrimitiveConceptDefinition pcd : hs)
					addAxiomToInc(a,  pcd);
			}
			for (String a: axiomsAisaB.keySet())
			{
				HashSet<PrimitiveConceptDefinition> hs = axiomsAisaB.get(a);
				for (PrimitiveConceptDefinition pcd : hs)
					addAxiomToInc(a,  pcd);
			}

			// Solve TBox
			solveDomainAndRangeAxioms();
			return;
		}

		// 2. Phase A
		// Add axioms to tDef, step 8b (Phase A)
		addAxiomsToTg();

		// 3. Process GCI transformations until no GCI transformation can be applied
		axiomsToDoAisaB = new Hashtable<String, HashSet<PrimitiveConceptDefinition>>();
		axiomsToDoAisaC = new Hashtable<String, HashSet<PrimitiveConceptDefinition>>(axiomsAisaC);
		axiomsToDoCisaA = new Hashtable<String, HashSet<GeneralConceptInclusion>>(axiomsCisaA);
		axiomsToDoCisaD = new Hashtable<String, HashSet<GeneralConceptInclusion>>(axiomsCisaD);

		axiomsAisaC.clear();
		axiomsCisaA.clear();
		axiomsCisaD.clear();

		axiomsToDoTmpAisaC = new Hashtable<String, HashSet<PrimitiveConceptDefinition>>();
		axiomsToDoTmpCisaA = new Hashtable<String, HashSet<GeneralConceptInclusion>>();
		axiomsToDoTmpCisaD = new Hashtable<String, HashSet<GeneralConceptInclusion>>();

		boolean exit = (axiomsToDoAisaC.isEmpty() && axiomsToDoCisaA.isEmpty() && axiomsToDoCisaD.isEmpty());
		while (!exit)
		{
			// Select axiom tau in axiomsAisaC that has not yet been processed 
			gciTransformationsAisaC();

			// Select axiom tau in axiomsCisaA that has not yet been processed 
			gciTransformationsCisaA();

			// Select axiom tau in axiomsCisaD that has not yet been processed 
			gciTransformationsCisaD();

			axiomsToDoAisaC = new Hashtable<String, HashSet<PrimitiveConceptDefinition>>(axiomsToDoTmpAisaC);
			axiomsToDoCisaA = new Hashtable<String, HashSet<GeneralConceptInclusion>>(axiomsToDoTmpCisaA);
			axiomsToDoCisaD = new Hashtable<String, HashSet<GeneralConceptInclusion>>(axiomsToDoTmpCisaD);

			axiomsToDoTmpAisaC.clear();
			axiomsToDoTmpCisaA.clear();
			axiomsToDoTmpCisaD.clear();

			exit = (axiomsToDoAisaC.isEmpty() && axiomsToDoCisaA.isEmpty() && axiomsToDoCisaD.isEmpty());
		}

		// 4. Process the other absorptions
		// None of them can generate new axioms in the lists axiomsAisaC, axiomsCisaA, axiomsCisaD
		// Hence, GCI transformation cannot be applied anymore.
		partitionLoopAisaB();
		partitionLoopAisaC();
		partitionLoopCisaA();
		partitionLoopCisaD();

		//another round
		partitionLoopToDoAisaB();
		partitionLoopToDoAisaC();
						   
		// 5. Exit condition
		exitCondition();

		// Solve TBox
		for(Individual ind : individuals.values())
			for(GeneralConceptInclusion gci : tG)
				solveGCI(ind, gci);

		solveDomainAndRangeAxioms();
	}


	/**
	 * Checks if the fuzzy KB is already lazy unfoldable
	 */
	private boolean isLazyUnfoldable()
	{
		if (! axiomsCisaA.isEmpty())
			return false;

		if (! axiomsCisaD.isEmpty())
			return false;

		if (! axiomsCequivD.isEmpty())
			return false;

		if (! axiomsAequivC.isEmpty())
		{
			for (String a: axiomsAequivC.keySet())
				if (axiomsAisaB.containsKey(a) || axiomsAisaC.containsKey(a) || (axiomsAequivC.get(a).size() > 1))
					return false;
		}
		
		// disj(a, b) with both a and b in tDef
		for(String a : tDis.keySet())
		{
			for(String b : tDis.get(a))
				if (axiomsAequivC.containsKey(a) && axiomsAequivC.containsKey(b))
					return false;			
		}
		
		return true;
	}


	/**
	 * Add every GCI to tG using the form *top* isA (C -> D) 
	 */
	private void exitCondition()
	{
		Util.println("------Exit condition---------- ");

		// Convert all GCIs in axiomsAisaB
		for (HashSet<PrimitiveConceptDefinition> hs : axiomsAisaB.values())
			for (PrimitiveConceptDefinition pcd : hs)
				exitConditionAisaX(pcd);

		// Convert all GCIs in axiomsAisaC
		for (HashSet<PrimitiveConceptDefinition> hs : axiomsAisaC.values())
			for (PrimitiveConceptDefinition pcd : hs)
				exitConditionAisaX(pcd);

		// Convert all GCIs in axiomsCisaD
		for (HashSet<GeneralConceptInclusion> gcis : axiomsCisaA.values())
			for (GeneralConceptInclusion gci : gcis)
				exitConditionCisaX(gci);

		// Convert all GCIs in axiomsCisaA
		for (HashSet<GeneralConceptInclusion> gcis : axiomsCisaD.values())
			for (GeneralConceptInclusion gci : gcis)
				exitConditionCisaX(gci);
	}


	private void exitConditionCisaX(GeneralConceptInclusion gci)
	{
		Concept c1 = gci.getSubsumed();
		Concept c2 = gci.getSubsumer();
		
		if (c1.type == Concept.TOP)
			tG.add(gci);
		else
		{
			switch (gci.getType())
			{
				case GeneralConceptInclusion.GOEDEL:
					tG.add(new GeneralConceptInclusion(Concept.gImplies(c1, c2), Concept.CONCEPT_TOP, gci.getDegree(), GeneralConceptInclusion.GOEDEL));
					break;

				case GeneralConceptInclusion.KLEENE_DIENES:
					tG.add(new GeneralConceptInclusion(Concept.kdImplies(c1, c2), Concept.CONCEPT_TOP, gci.getDegree(), GeneralConceptInclusion.KLEENE_DIENES));
					break;

				case GeneralConceptInclusion.LUKASIEWICZ:
					tG.add(new GeneralConceptInclusion(Concept.lImplies(c1, c2), Concept.CONCEPT_TOP, gci.getDegree(), GeneralConceptInclusion.LUKASIEWICZ));
					break;

				default: // GeneralConceptInclusion.ZADEH:
					tG.add(new GeneralConceptInclusion(Concept.lImplies(c1, c2), Concept.CONCEPT_TOP, Degree.ONE, GeneralConceptInclusion.ZADEH));
			}
		}
	}

	
	private void exitConditionAisaX(PrimitiveConceptDefinition pcd)
	{
		Concept c1 = getConcept(pcd.getDefinedConcept());
		Concept c2 = pcd.getDefinition();
		
		int implicationType = pcd.getType();
		double n = pcd.getDegree();

		GeneralConceptInclusion gci = new GeneralConceptInclusion(c2, c1, new DegreeNumeric(n), implicationType);

		if (c1.type == Concept.TOP)
			tG.add(gci);
		else
		{
			switch (gci.getType())
			{
				case GeneralConceptInclusion.GOEDEL:
					tG.add(new GeneralConceptInclusion(Concept.gImplies(c1, c2), Concept.CONCEPT_TOP, gci.getDegree(), GeneralConceptInclusion.GOEDEL));
					break;

				case GeneralConceptInclusion.KLEENE_DIENES:
					tG.add(new GeneralConceptInclusion(Concept.kdImplies(c1, c2), Concept.CONCEPT_TOP, gci.getDegree(), GeneralConceptInclusion.KLEENE_DIENES));
					break;

				case GeneralConceptInclusion.LUKASIEWICZ:
					tG.add(new GeneralConceptInclusion(Concept.lImplies(c1, c2), Concept.CONCEPT_TOP, gci.getDegree(), GeneralConceptInclusion.LUKASIEWICZ));
					break;

				default: // GeneralConceptInclusion.ZADEH:
					tG.add(new GeneralConceptInclusion(Concept.lImplies(c1, c2), Concept.CONCEPT_TOP, Degree.ONE, GeneralConceptInclusion.ZADEH));
			}
		}
	}


	/**
	 * Checks if the fuzzy KB is loaded;
	 * @return true if the fuzzy KB is loaded; false otherwise.
	 */
	boolean isLoaded()
	{
		return KB_LOADED;
	}


	/**
	 * Adds a fuzzy assertion of the form (a : C &gt;= n )
	 * @param a An individual
	 * @param c A fuzzy concept
	 * @param n A degree of truth.
	 */
	public void addAssertion(Individual a, Concept c, Degree n)
	{
		addAssertion(new Assertion(a, c, n) );
	}


	/**
	 * Adds a fuzzy assertion of the form (a : forall R.C &gt;= n )
	 * @param a An individual
	 * @param restric A restriction of the form (forall R.C &gt;= n)
	 */
	public void addAssertion(Individual a, Restriction restric)
	{
		if (restric instanceof HasValueRestriction)
		{
			HasValueRestriction hvr = (HasValueRestriction) restric;
			Concept forAll = Concept.notHasValue(restric.getRoleName(), hvr.getIndividual());
			addAssertion(a, forAll, restric.getDegree());
		}
		else
			addAssertion(a, Concept.all(restric.getRoleName(), restric.getConcept()), restric.getDegree());
	}


	/**
	 * Checks the disjointness between abstract and concrete roles.
	 * @param roleName A role name.
	 * @param conc A concept appearing in a restrictions involving the role.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public void checkRole(String roleName, Concept conc) throws FuzzyOntologyException
	{
		if ( (atomicConcepts.get(roleName) !=  null) || (concreteConcepts.get(roleName) !=  null) )
			Util.println("Warning: " + roleName + " is the name of both a concept and a role.");

		if (conc.isConcrete())
		{
			// roleName is concrete
			if (abstractRoles.contains(roleName))
				Util.error("Error: Role " + roleName + " cannot be concrete and abstract.");

			concreteRoles.add(roleName);
		}
		else
		{
			// roleName is abstract
			if (concreteRoles.contains(roleName))
				Util.error("Error: Role " + roleName + " cannot be concrete and abstract.");

			abstractRoles.add(roleName);
		}
	}


	// Return a String representation of the degree if it is different to 1.0
	String degreeIfNotOne(Degree deg)
	{
		if (deg.isNumeric())
			return degreeIfNotOne(((DegreeNumeric) deg).getNumericalValue());
		else
			return deg.toString();
	}


	// Return a String representation of the number if it is different to 1.0
	String degreeIfNotOne(double d)
	{
		if (d == 1.0)
			return "";
		else
			return "" + d;
	}


	private void defineConcreteFeature(String role) throws FuzzyOntologyException
	{
		if (concreteFeatures.contains(role) == false)
		{
			if (abstractRoles.contains(role))
			 Util.error("Error: Role " + role + " cannot be concrete and abstract.");

			concreteRoles.add(role);
			funcRoles.add(role);
			concreteFuzzyConcepts = true;

			milp.addStringFeature(role);
		}
	}

	/**
	 * Define a concrete feature with range boolean.
	 * @param funcRole Name of the concrete feature.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public void defineBooleanConcreteFeature(String funcRole) throws FuzzyOntologyException
	{
		defineConcreteFeature(funcRole);
		concreteFeatures.put(funcRole, new ConcreteFeature(funcRole, true));
	}


	/**
	 * Define a concrete feature with range string.
	 * @param funcRole Name of the concrete feature.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public void defineStringConcreteFeature(String funcRole) throws FuzzyOntologyException
	{
		defineConcreteFeature(funcRole);
		concreteFeatures.put(funcRole, new ConcreteFeature(funcRole));
	}


	/**
	 * Define a concrete feature with range integers in [d1, d2].
	 * @param funcRole Name of the concrete feature.
	 * @param d1 Lower bound of the range.
	 * @param d2 Upper bound of the range.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public void defineIntegerConcreteFeature(String funcRole, Integer d1, Integer d2) throws FuzzyOntologyException
	{
		defineConcreteFeature(funcRole);
		concreteFeatures.put(funcRole, new ConcreteFeature(funcRole, d1, d2) );
	}


	/**
	 * Define a concrete feature with range real numbers in [d1, d2].
	 * @param funcRole Name of the concrete feature.
	 * @param d1 Lower bound of the range.
	 * @param d2 Upper bound of the range.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public void defineRealConcreteFeature(String funcRole, Double d1, Double d2) throws FuzzyOntologyException
	{
		defineConcreteFeature(funcRole);
		concreteFeatures.put(funcRole, new ConcreteFeature(funcRole, d1, d2) );
	}


	/**
	 * Sets the fuzzy logic of the fuzzy knowledge base.
	 * @param logic Fuzzy logic of the fuzzy knowledge base.
	 */
	public void setLogic(FuzzyLogic logic)
	{
		semantics = logic;
		Util.println("Fuzzy logic: " + logic);
	}


	/**
	 * Gets the fuzzy logic of the fuzzy knowledge base.
	 * @return Fuzzy logic of the fuzzy knowledge base.
	 */
	public FuzzyLogic getLogic()
	{
		return semantics;
	}


	private void ruleAtomic(Assertion ass)
	{
		rulesApplied[RULE_ATOMIC]++;
		old01Variables++;
		ruleLazyUnfolding(ass);
	}


	private void ruleComplementedLazyUnfolding(Assertion ass)
	{
		Individual ind = ass.getIndividual();
		Concept notA = ass.getConcept();
		Variable xAnotA = milp.getVariable(ass);
		Concept a = Concept.complement(notA);
		String aName = a.toString();

		// 1. A = B
		Set<String> syns = tSyn.get(aName);
		if (syns != null)
		{
			for (String syn : syns)
			{
				Concept notC = Concept.complement(atomicConcepts.get(syn));
				Variable xNotC = milp.getVariable(ind, notC);
				addAssertion(ind, notC, Degree.getDegree(xNotC));
				milp.addNewConstraint(new Expression(new Term(1,xNotC), new Term(-1,xAnotA)), Inequation.EQ);
				old01Variables++;
			}
		}

		// 2. A = C
		Concept c = tDef.get(aName);
		if (c != null)
		{
			Concept notC = Concept.complement(c);
			Variable xAnotC = milp.getVariable(ind, notC);
			addAssertion(ind, notC, Degree.getDegree(xAnotC));
			milp.addNewConstraint(new Expression(new Term(1, xAnotA), new Term(-1, xAnotC)), Inequation.EQ);
		}
	}


	private void ruleLazyUnfolding(Assertion ass)
	{
		Concept a = ass.getConcept();
		String aName = a.toString();
		Individual ind = ass.getIndividual();
		Variable varA = milp.getVariable(ind, a);

		// 1. A isA C
		Variable indA = milp.getVariable(ind, a);
		Set<PrimitiveConceptDefinition> pcds = tInc.get(aName);
		if (pcds != null)
		{
			for (PrimitiveConceptDefinition pcd : pcds)
			{
				if (pcd.getType() == GeneralConceptInclusion.KLEENE_DIENES)
				{
					Concept kd = Concept.kdImplies(a, pcd.getDefinition());
					addAssertion(ind, kd, Degree.getDegree(pcd.getDegree()));
				}
				else
				{
					// Rule: (A subclassof C >= n) and (a : A) imply (a : C) and x_{a:C} \geq x_{a:C} \otimes n)
					old01Variables++;
					oldBinaryVariables++;
	
					Concept c = pcd.getDefinition();	
					Variable indC = milp.getVariable(ind, c);
					addAssertion(ind, c, new DegreeVariable(indC));
					double n = pcd.getDegree();
					if (n == 1)
						milp.addNewConstraint(new Expression(new Term(1,indC), new Term(-1,indA)), Inequation.GE);					
					else
					{
						switch(pcd.getType())
						{
							case GeneralConceptInclusion.LUKASIEWICZ:
								
								LukasiewiczSolver.andGeqEquation(indC, indA, n, milp);
								break;
			
							case GeneralConceptInclusion.GOEDEL:
								ZadehSolver.andGeqEquation(indC, indA, n, milp);
								break;
			
							case GeneralConceptInclusion.ZADEH:
								milp.addNewConstraint(new Expression(new Term(1,indC), new Term(-1,indA)), Inequation.GE);
								break;
						}
					}
				}
			}
		}

		// 2. A = B (syn)
		Set<String> syns = tSyn.get(aName);
		if (syns != null)
		{
			Util.println("Lazy unfolding for synonyms:" + aName);
						
			for (String syn : syns)
			{
				Util.println("synonym with:" + syn);
				Concept c = atomicConcepts.get(syn);
				Variable indC = milp.getVariable(ind, c);
				addAssertion(ind, c, Degree.getDegree(indC));
				milp.addNewConstraint(new Expression(new Term(1,indC), new Term(-1,indA)), Inequation.EQ);
				old01Variables++;
			}
		}

		// 3. A  = C
		Concept c = tDef.get(aName);
		if (c != null)
		{		
			Variable varC = milp.getVariable(ind, c);
			addAssertion(ind, c, Degree.getDegree(varC));
			milp.addNewConstraint(new Expression(new Term(1, varC), new Term(-1, varA)), Inequation.EQ);
		}

		// 4. Disjoint axioms				
		HashSet<String> disjConcs = tDis.get(aName);
		if (disjConcs != null)
		{
			Util.println("Lazy unfolding Disjoint axioms:" + aName);
			HashSet<String> hs2 = disjointVariables.get(aName);
			if (hs2 == null)
				hs2 = new HashSet<String>();

			for (String name : disjConcs)
			{
				Util.println("disjoint with:" + name);

				// Add v : name
				oldBinaryVariables++;
				Variable varDisj = milp.getVariable(ind, name);
				addAssertion(ind, new Concept(name), Degree.getDegree(varDisj));

				// State that the variables are disjoint
				if (! hs2.contains(varDisj.toString()))
				{
					ZadehSolver.andEquation(varA, varDisj, milp);
					hs2.add(varDisj.toString());					
				}
			}
			disjointVariables.put(aName, hs2);
		}	
	}


	private void ruleComplementedAtomic(Assertion ass)
	{
		rulesApplied[RULE_COMPLEMENT]++;
		Individual ind = ass.getIndividual();
		Concept notA = ass.getConcept();
		Variable xAnotA = milp.getVariable(ass);

		Concept a = Concept.complement(notA);
		Variable xAisA = milp.getVariable(ind, a);

		// x_{a:\not A} = 1 - x_{a: A}
		milp.addNewConstraint(new Expression(1, new Term(-1, xAisA), new Term(-1, xAnotA)), Inequation.EQ);

		this.ruleComplementedLazyUnfolding(ass);
	}


	private void ruleAnd(Assertion ass)
	{
		switch(semantics)
		{
			case LUKASIEWICZ:
				rulesApplied[RULE_L_AND]++;
				LukasiewiczSolver.solveAnd(ass, this);
				break;
				
			case ZADEH:
				rulesApplied[RULE_G_AND]++;
				ZadehSolver.solveAnd(ass, this);
				break;

			default: // case CLASSICAL:
				rulesApplied[RULE_G_AND]++;
				ClassicalSolver.solveAnd(ass, this);
		}
	}


	private void ruleOr(Assertion ass)
	{
		switch(semantics)
		{
			case LUKASIEWICZ:
				rulesApplied[RULE_L_OR]++;
				LukasiewiczSolver.solveOr(ass, this);
				break;
				
			case ZADEH:
				rulesApplied[RULE_G_OR]++;
				ZadehSolver.solveOr(ass, this);
				break;

			default: // case CLASSICAL:
				rulesApplied[RULE_L_OR]++;
				ClassicalSolver.solveOr(ass, this);
		}
	}


	private void ruleHasValue(Assertion ass) throws InconsistentOntologyException
	{
		Individual a = ass.getIndividual();
		Concept c = ass.getConcept();
		Degree d = ass.getLowerLimit();
		String r = c.getRole();
		String oName = (String) (c.getValue());
		Individual o = getIndividual(oName);

		rulesApplied[KnowledgeBase.RULE_HAS_VALUE]++;

		if(funcRoles.contains(r) && a.roleRelations.containsKey(r))
		{
			ArrayList<Relation> relSet = a.roleRelations.get(r);
			Relation rel = relSet.get(0);		
			getCorrectVersionOfIndividual(rel);
			Individual b = rel.getObjectIndividual();

			String bName = b.toString();
			Variable xBisO = milp.getNominalVariable(bName, oName);

			// If b is a created individual, merge b into o
			if (b.isBlockable())
				merge(o, b);
			// Otherwise, merge o into b if they are different
			else if (! b.toString().equals(o.toString()))
				merge(b, o);

			Relation rel2 = new Relation(r, a, o, d);
			Variable xRel = milp.getVariable(rel2);
			Variable xAss = milp.getVariable(a, c);

			// xImpl = x_{a : some R.{o}} => x_{a,b:R}
			Variable xImpl = milp.getNewVariable(Variable.UP_VARIABLE);
			ZadehSolver.zImpliesEquation(xImpl, xAss, xRel, milp);
			
			// x_{b:{o}} =>  x_{a : some R.{o}} => x_{a,b:R} = 1
			ZadehSolver.zImpliesEquation(1, xBisO, xImpl, milp);
			
			// xAss <= x_{b:C} \otimes x_{(a:b):R}
			ZadehSolver.andLeqEquation(xAss, xBisO, xRel, milp);
		}
		else
		{	
			addRelation(a, r, o, d);
		}
	}


	private void addLabelsWithNodes(String node, String indName) throws InconsistentOntologyException
	{
		Set<String> set = labelsWithNodes.get(node);
		if (set == null)
			set = new HashSet<String> ();

		// We only apply ruleAssNom the first time that the label is added to the node
		if (set.contains(indName) == false)
		{
			set.add(indName);
			labelsWithNodes.put(node, set);
			Individual i = getIndividual(node);
			for (Concept c : i.getConcepts())
				ruleAssNom(i, c, indName);
		}
	}


	private void ruleSome(Assertion ass) throws InconsistentOntologyException
	{
		// HasValue restriction
		if (ass.getType() == Concept.HAS_VALUE)
			ruleHasValue(ass); 

		// Other existential restriction
		else
		{
			switch(semantics)
			{
				case LUKASIEWICZ:
					LukasiewiczSolver.solveSome(ass, this);
					break;
	
				case ZADEH:
					ZadehSolver.solveSome(ass, this);
					break;
	
				default: // case CLASSICAL:
					ClassicalSolver.solveSome(ass, this);
			}
		}
	}


	private void ruleAll(Assertion ass) throws InconsistentOntologyException
	{
		// Concept simplification
		if (ass.getConcept().c1.getType() == Concept.TOP)
			addAssertion(ass.getIndividual(), Concept.CONCEPT_TOP, ass.getLowerLimit());
		else
			ass.getIndividual().addRestriction(ass.getConcept().getRole(), ass.getConcept().c1, ass.getLowerLimit(), this );
	}


	private void ruleComplementedHasValue(Assertion ass) throws InconsistentOntologyException
	{
		Individual a = ass.getIndividual();
		String r = ass.getConcept().getRole();
		String b = (String) ass.getConcept().getValue();
		a.addRestriction(r, b, ass.getLowerLimit(), this);
	}


	private void computeVariablesOldCalculus(FuzzyConcreteConcept fcc )
	{
		if (fcc instanceof CrispConcreteConcept)
			oldBinaryVariables++;			
		else if (fcc instanceof LeftConcreteConcept)
			oldBinaryVariables += 3;
		else if (fcc instanceof RightConcreteConcept)
			oldBinaryVariables += 3;
		else if (fcc instanceof TriangularConcreteConcept)
			oldBinaryVariables += 4;
		else if (fcc instanceof TrapezoidalConcreteConcept)
			oldBinaryVariables += 5;
		else if (fcc instanceof LinearConcreteConcept)
		{
			old01Variables++;
			oldBinaryVariables++;
		}
	}


	private void ruleConcrete(Assertion ass)
	{
		rulesApplied[RULE_CONCRETE]++;
		FuzzyConcreteConcept fcc = (FuzzyConcreteConcept) ass.getConcept();
		computeVariablesOldCalculus(fcc);
		CreatedIndividual ind = (CreatedIndividual) ass.getIndividual();
		fcc.solveAssertion(ind, ass.getLowerLimit(), this);
	}
 

	private void ruleComplementedConcrete(Assertion ass)
	{
		rulesApplied[RULE_NOT_CONCRETE]++;
		FuzzyConcreteConcept fcc = (FuzzyConcreteConcept) ass.getConcept();
		computeVariablesOldCalculus(fcc);
		CreatedIndividual ind = (CreatedIndividual) ass.getIndividual();
		fcc.solveComplementAssertion(ind, ass.getLowerLimit(), this);
	}


	private void ruleFuzzyNumber(Assertion ass)
	{
		rulesApplied[RULE_FUZZY_NUMBER]++;
		ruleConcrete(ass);
	}


	private void ruleComplementedFuzzyNumber(Assertion ass)
	{
		rulesApplied[RULE_NOT_FUZZY_NUMBER]++;
		ruleComplementedConcrete(ass);
	}


	private void ruleModified(Assertion ass)
	{
		ModifiedConcept mod = ((ModifiedConcept) ass.getConcept());
		if (mod instanceof TriangularlyModifiedConcept)
			old01Variables += 2;
		else // LinearlyModifiedConcept
		{
			old01Variables++;
			oldBinaryVariables++;
		}
		
		rulesApplied[RULE_MODIFIED]++;
		mod.solveAssertion(ass.getIndividual(), ass.getLowerLimit(), this);
	}


	private void ruleComplementedModified(Assertion ass)
	{
		ModifiedConcept mod = ((ModifiedConcept) ass.getConcept());
		if (mod instanceof TriangularlyModifiedConcept)
		{
			old01Variables++;
			oldBinaryVariables++;
		}
		else // LinearlyModifiedConcept
		{
			old01Variables += 2;
			oldBinaryVariables += 2;
		}

		rulesApplied[RULE_NOT_MODIFIED]++;
		mod.solveComplementAssertion(ass.getIndividual(), ass.getLowerLimit(), this);
	}


	private void ruleBottom(Assertion ass) throws FuzzyOntologyException
	{
		rulesApplied[RULE_BOTTOM]++;
		Variable xAss = milp.getVariable(ass);
		milp.addNewConstraint(new Expression(new Term(1,xAss)), Inequation.EQ, 0);
	}


	private void ruleTop(Assertion ass)
	{
		rulesApplied[RULE_TOP]++;
		milp.addNewConstraint(ass, 1);
	}


	private void ruleSelf(Assertion ass) throws InconsistentOntologyException
	{
		rulesApplied[RULE_SELF]++;
		Individual a = ass.getIndividual();
		String role = ass.getConcept().getRole(); 
		Relation r = a.addRelation(role, a, ass.getLowerLimit(), this);
		solveRoleInclusionAxioms(a, r);
	}


	private void ruleComplementedSelf(Assertion ass)
	{
		rulesApplied[RULE_NOT_SELF]++;
		Individual a = ass.getIndividual();
		a.addNotSelfRestriction(ass.getConcept().getRole(), this);
	}


	private void ruleUpperApproximation(Assertion ass)
	{
		Individual a = ass.getIndividual();
		Concept con = ass.getConcept();
		addAssertion(new Assertion(a, Concept.some(con.getRole(), con.c1), ass.getLowerLimit()));
	}


	private void ruleLowerApproximation(Assertion ass)
	{
		Individual a = ass.getIndividual();
		Concept con = ass.getConcept();
		addAssertion(new Assertion(a, Concept.all(con.getRole(), con.c1), ass.getLowerLimit()));
	}


	private void ruleTightUpperApproximation(Assertion ass)
	{
		Individual a = ass.getIndividual();
		Concept con = ass.getConcept();
		addAssertion(new Assertion(a, Concept.all(con.getRole(), Concept.some(con.getRole(), con.c1)), ass.getLowerLimit()));
	}


	private void ruleTightLowerApproximation(Assertion ass)
	{
		Individual a = ass.getIndividual();
		Concept con = ass.getConcept();
		addAssertion(new Assertion(a, Concept.all(con.getRole(), Concept.all(con.getRole(), con.c1)), ass.getLowerLimit()));
	}


	private void ruleLooseUpperApproximation(Assertion ass)
	{
		Individual a = ass.getIndividual();
		Concept con = ass.getConcept();
		addAssertion(new Assertion(a, Concept.some(con.getRole(), Concept.some(con.getRole(), con.c1)), ass.getLowerLimit()));
	}


	private void ruleLooseLowerApproximation(Assertion ass)
	{
		Individual a = ass.getIndividual();
		Concept con = ass.getConcept();
		addAssertion(new Assertion(a, Concept.some(con.getRole(), Concept.all(con.getRole(), con.c1)), ass.getLowerLimit()));
	}


	private void ruleGoedelAnd(Assertion ass)
	{
		rulesApplied[RULE_G_AND]++;
		ZadehSolver.solveAnd(ass, this);
	}


	private void ruleGoedelOr(Assertion ass)
	{
		rulesApplied[RULE_G_OR]++;
		ZadehSolver.solveOr(ass, this);
	}


	private void ruleLukasiewiczAnd(Assertion ass)
	{
		rulesApplied[RULE_L_AND]++;
		LukasiewiczSolver.solveAnd(ass, this);
	}


	private void ruleLukasiewiczOr(Assertion ass)
	{
		rulesApplied[RULE_L_OR]++;
		LukasiewiczSolver.solveOr(ass, this);
	}


	private void ruleGoedelImplication(Assertion ass)
	{
		old01Variables += 2;
		oldBinaryVariables++;

		rulesApplied[RULE_G_IMPLIES]++;
		Individual ind = ass.getIndividual();
		Concept goedelImpl = ass.getConcept();
		Variable xIsC = milp.getVariable(ind, goedelImpl);

		Concept c1 = goedelImpl.concepts.get(0);
		Variable xIsC1 = milp.getVariable(ind, c1);
		Concept notC1 = Concept.complement(c1);
		Variable xIsNotC1 = milp.getVariable(ind, notC1);

		Concept c2 = goedelImpl.concepts.get(1);
		Variable xIsC2 = milp.getVariable(ind, c2);

		addAssertion(ind, notC1, Degree.getDegree(xIsNotC1));
		ruleComplemented(ind, notC1);
		addAssertion(ind, c2, Degree.getDegree(xIsC2));

		ZadehSolver.gImpliesEquation(xIsC, xIsC1, xIsC2, milp);
	}


	private void ruleZadehImplication(Assertion ass)
	{
		old01Variables += 2;
		oldBinaryVariables++;

		rulesApplied[RULE_Z_IMPLIES]++;
		Individual ind = ass.getIndividual();
		Concept zImpl = ass.getConcept();
		Variable xIsC = milp.getVariable(ind, zImpl);

		Concept c1 = zImpl.concepts.get(0);
		Variable xIsC1 = milp.getVariable(ind, c1);
		Concept notC1 = Concept.complement(c1);
		Variable xIsNotC1 = milp.getVariable(ind, notC1);

		Concept c2 = zImpl.concepts.get(1);
		Variable xIsC2 = milp.getVariable(ind, c2);

		addAssertion(ind, notC1, Degree.getDegree(xIsNotC1));
		ruleComplemented(ind, notC1);
		addAssertion(ind, c2, Degree.getDegree(xIsC2));

		ZadehSolver.zImpliesEquation(xIsC, xIsC1, xIsC2, milp);
	}


	private void ruleComplementedGoedelImplication(Assertion ass)
	{
		old01Variables += 2;
		oldBinaryVariables++;

		rulesApplied[RULE_NOT_G_IMPLIES]++;
		Individual ind = ass.getIndividual();
		Concept goedelImpl = ass.getConcept();
		Variable xIsC = milp.getVariable(ind, Concept.complement(goedelImpl));

		Concept c1 = goedelImpl.concepts.get(0);
		Variable xIsC1 = milp.getVariable(ind, c1);

		Concept c2 = goedelImpl.concepts.get(1);
		Variable xIsC2 = milp.getVariable(ind, c2);
		Concept notC2 = Concept.complement(c2);
		Variable xIsNotC2 = milp.getVariable(ind, notC2);

		addAssertion(ind, c1, Degree.getDegree(xIsC1));
		addAssertion(ind, notC2, Degree.getDegree(xIsNotC2));

		ZadehSolver.gImpliesEquation(xIsC, xIsC1, xIsC2, milp);

		ruleComplemented(ind, goedelImpl);
	}


	private void ruleComplementedZadehImplication(Assertion ass)
	{
		old01Variables += 2;
		oldBinaryVariables++;

		rulesApplied[RULE_NOT_Z_IMPLIES]++;
		Individual ind = ass.getIndividual();
		Concept zImpl = ass.getConcept();
		Variable xIsC = milp.getVariable(ind, Concept.complement(zImpl));

		Concept c1 = zImpl.concepts.get(0);
		Variable xIsC1 = milp.getVariable(ind, c1);

		Concept c2 = zImpl.concepts.get(1);
		Variable xIsC2 = milp.getVariable(ind, c2);
		Concept notC2 = Concept.complement(c2);
		Variable xIsNotC2 = milp.getVariable(ind, notC2);

		addAssertion(ind, c1, Degree.getDegree(xIsC1));
		addAssertion(ind, notC2, Degree.getDegree(xIsNotC2));

		ZadehSolver.zImpliesEquation(xIsC, xIsC1, xIsC2, milp);

		ruleComplemented(ind, zImpl);
	}


	private void rulePositiveThreshold(Assertion ass)
	{
		oldBinaryVariables++;
		
		rulesApplied[RULE_THRESHOLD]++;
		Individual i = ass.getIndividual();
		Concept tc = ass.getConcept();
		Variable xAinTc = milp.getVariable(i, tc);
		Concept c = tc.c1;
		Variable xAinC = milp.getVariable(i, c);
		double x = tc.getWeight();

		// a : C >= x_{C}
		addAssertion(i, c, Degree.getDegree(xAinC));

		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// Rules independent of the x
		ruleThresholdCommon(xAinC, xAinTc, y);

		// x_{v:C} < y + x
		milp.addNewConstraint(new Expression(-x+ConfigReader.EPSILON, new Term(-1, y), new Term(1, xAinC)), Inequation.LE);

		// x_{v:[\geq x] \; C} + (1-y) \geq x
		milp.addNewConstraint(new Expression(1-x, new Term(1, xAinTc), new Term(-1, y)), Inequation.GE);
	}


	private void ruleThresholdCommon(Variable xAinC, Variable xAinTc, Variable y)
	{
		// x_{v:[\geq x] \; C} \leq x_{v:C} + (1-y) 
		milp.addNewConstraint(new Expression(-1, new Term(1, xAinTc), new Term(-1, xAinC) , new Term(1, y)), Inequation.LE);

		// x_{v:[\leq x] \; C} + (1-y) \geq x_{v:C} 
		milp.addNewConstraint(new Expression(1, new Term(1, xAinTc), new Term(-1, xAinC) , new Term(-1, y)), Inequation.GE);

		// x_{v:[\geq x] \; C} \leq y
		milp.addNewConstraint(new Expression(new Term(1, xAinTc), new Term(-1, y)), Inequation.LE);
	}


	private void ruleComplementedPositiveThreshold(Assertion ass)
	{
		old01Variables += 2;
		oldBinaryVariables++;
		
		rulesApplied[RULE_NOT_THRESHOLD]++;
		ruleComplementedComplexAssertion(ass);
	}


	private void ruleNegativeThreshold(Assertion ass)
	{
		old01Variables++;

		rulesApplied[RULE_THRESHOLD]++;
		Individual i = ass.getIndividual();
		Concept tc = ass.getConcept();
		Variable xAinTc = milp.getVariable(i, tc);
		Concept c = tc.c1;
		Variable xAinC = milp.getVariable(i, c);
		double x = tc.getWeight();

		// a : C >= x_{C}
		assertions.add(new Assertion(i, c, Degree.getDegree(xAinC)));

		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// Rules independent of the x
		ruleThresholdCommon(xAinC, xAinTc, y);

		// x_{v:C} + 2y > x
		milp.addNewConstraint(new Expression(-x-ConfigReader.EPSILON, new Term(2, y), new Term(1, xAinC)), Inequation.GE);

		// x_{v:[\leq x] \; C} \leq x + (1-y)
		milp.addNewConstraint(new Expression(-1-x, new Term(1, xAinTc), new Term(1, y)), Inequation.LE);
	}


	private void ruleComplementedNegativeThreshold(Assertion ass)
	{
		old01Variables += 2;
		oldBinaryVariables++;

		rulesApplied[RULE_NOT_THRESHOLD]++;
		ruleComplementedComplexAssertion(ass);
	}


	private void ruleExtendedPositiveThreshold(Assertion ass)
	{
		old01Variables++;

		rulesApplied[RULE_THRESHOLD]++;
		Individual i = ass.getIndividual();
		Concept tc = ass.getConcept();
		Variable xAinTc = milp.getVariable(i, tc);
		Concept c = tc.c1;
		Variable xAinC = milp.getVariable(i, c);
		Variable x = tc.getWeightVar();

		// a : C >= x_{C}
		assertions.add(new Assertion(i, c, Degree.getDegree(xAinC)));

		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// Rules independent of the x
		ruleThresholdCommon(xAinC, xAinTc, y);

		// x_{v:C} < y + x
		milp.addNewConstraint(new Expression(ConfigReader.EPSILON, new Term(-1, x), new Term(-1, y), new Term(1, xAinC)), Inequation.LE);

		// x_{v:[\geq x] \; C} + (1-y) \geq x
		milp.addNewConstraint(new Expression(1, new Term(-1, x), new Term(1, xAinTc), new Term(-1, y)), Inequation.GE);
	}


	private void ruleComplementedExtendedPositiveThreshold(Assertion ass)
	{
		old01Variables += 2;
		oldBinaryVariables++;

		rulesApplied[RULE_NOT_THRESHOLD]++;
		ruleComplementedComplexAssertion(ass);
	}


	private void ruleExtendedNegativeThreshold(Assertion ass)
	{
		oldBinaryVariables++;

		rulesApplied[RULE_THRESHOLD]++;
		Individual i = ass.getIndividual();
		Concept tc = ass.getConcept();
		Variable xAinTc = milp.getVariable(i, tc);
		Concept c = tc.c1;
		Variable xAinC = milp.getVariable(i, c);
		Variable x = tc.getWeightVar();

		// a : C >= x_{C}
		assertions.add(new Assertion(i, c, Degree.getDegree(xAinC)));

		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// Rules independent of the x
		ruleThresholdCommon(xAinC, xAinTc, y);

		// x_{v:C} + 2y > x
		milp.addNewConstraint(new Expression(-ConfigReader.EPSILON, new Term(-1, x), new Term(2, y), new Term(1, xAinC)), Inequation.GE);

		// x_{v:[\leq x] \; C} \leq x + (1-y)
		milp.addNewConstraint(new Expression(-1, new Term(-1, x), new Term(1, xAinTc), new Term(1, y)), Inequation.LE);
	}


	private void ruleComplementedExtendedNegativeThreshold(Assertion ass)
	{
		old01Variables += 2;
		oldBinaryVariables++;

		rulesApplied[RULE_NOT_THRESHOLD]++;
		ruleComplementedComplexAssertion(ass);
	}


	private void ruleWeightedConcept(Assertion ass)
	{
		rulesApplied[RULE_WEIGHTED]++;
		Individual i = ass.getIndividual();
		Concept wc = ass.getConcept();
		Variable xAinWc = milp.getVariable(i, wc);
		Concept c = wc.c1;
		Variable xAinC = milp.getVariable(i, c);
		double w = wc.getWeight();

		// a : C >= x_{C}
		addAssertion(new Assertion(i, c, Degree.getDegree(xAinC)));

		// x_{WC} = w x_{C}
		milp.addNewConstraint(new Expression(new Term(1, xAinWc), new Term(-w, xAinC)), Inequation.EQ);
	}


	private void ruleComplementedWeighted(Assertion ass)
	{
		rulesApplied[RULE_NOT_WEIGHTED]++;
		Individual i = ass.getIndividual();
		Concept wc = ass.getConcept();
		Variable xAinWc = milp.getVariable(i, Concept.complement(wc));
		Concept notC = Concept.complement(wc.c1);
		Variable xAinC = milp.getVariable(i, wc.c1);
		Variable xAinNotC = milp.getVariable(i, notC);
		double w = wc.getWeight();

		// a : not C >= x_{not C}
		addAssertion(new Assertion(i, notC, Degree.getDegree(xAinNotC)));

		// x_{WC} = w x_{C}
		milp.addNewConstraint(new Expression(new Term(1, xAinWc), new Term(-w, xAinC)), Inequation.EQ);

		ruleComplemented(i, wc);
	}


	void ruleComplementedComplexAssertion(Assertion ass)
	{
		Individual i = ass.getIndividual();
		Concept c = Concept.complement(ass.getConcept());
		Variable x = milp.getVariable(i, c);
		ruleComplemented(i, c);

		// a : C >= x_{C}
		addAssertion(new Assertion(i, c, Degree.getDegree(x)));
	}


	void ruleComplemented(Individual i, Concept c)
	{
		Variable x = milp.getVariable(i, c);
		Concept c2 = Concept.complement(c);
		Variable x2 = milp.getVariable(i, c2);

		milp.addNewConstraint(new Expression(1, new Term(-1, x), new Term(-1, x2)), Inequation.EQ);
	}


	private void ruleWeightedSum(Assertion ass)
	{
		int n = ((WeightedSumConcept) ass.getConcept()).concepts.size();
		old01Variables += n;

		rulesApplied[RULE_W_SUM]++;
		((WeightedSumConcept) ass.getConcept()).solveAssertion(ass.getIndividual(), this);
	}


	private void ruleComplementedWeightedSum(Assertion ass)
	{
		int n = ((WeightedSumConcept) ass.getConcept()).concepts.size();
		old01Variables += n;

		rulesApplied[RULE_NOT_W_SUM]++;
		((WeightedSumConcept) ass.getConcept()).solveComplementedAssertion(ass.getIndividual(), this);
	}


	private void ruleWeightedSumZero(Assertion ass)
	{
		int n = ((WeightedSumZeroConcept) ass.getConcept()).concepts.size();
		old01Variables += n;

		rulesApplied[RULE_W_SUM_ZERO]++;
		((WeightedSumZeroConcept) ass.getConcept()).solveAssertion(ass.getIndividual(), this);
	}


	private void ruleComplementedWeightedSumZero(Assertion ass)
	{
		rulesApplied[RULE_NOT_W_SUM_ZERO]++;
		((WeightedSumZeroConcept) ass.getConcept()).solveComplementedAssertion(ass.getIndividual(), this);
	}


	private void ruleWeightedMin(Assertion ass)
	{
		rulesApplied[RULE_W_MIN]++;
		((WeightedMinConcept) ass.getConcept()).solveAssertion(ass.getIndividual(), this);
	}


	private void ruleComplementedWeightedMin(Assertion ass)
	{
		rulesApplied[RULE_NOT_W_MIN]++;
		((WeightedMinConcept) ass.getConcept()).solveComplementedAssertion(ass.getIndividual(), this);
	}


	private void ruleWeightedMax(Assertion ass)
	{
		rulesApplied[RULE_W_MAX]++;
		((WeightedMaxConcept) ass.getConcept()).solveAssertion(ass.getIndividual(), this);
	}


	private void ruleComplementedWeightedMax(Assertion ass)
	{
		rulesApplied[RULE_NOT_W_MAX]++;
		((WeightedMaxConcept) ass.getConcept()).solveComplementedAssertion(ass.getIndividual(), this);
	}


	private void ruleOwa(Assertion ass)
	{
		int n = ((OwaConcept) ass.getConcept()).concepts.size();
		old01Variables += 3*n;
		oldBinaryVariables += n;
		
		rulesApplied[RULE_OWA]++;
		((OwaConcept) ass.getConcept()).solveAssertion(ass.getIndividual(), this);
	}


	private void ruleComplementedOwa(Assertion ass)
	{
		int n = ((OwaConcept) ass.getConcept()).concepts.size();
		old01Variables += 3*n;
		oldBinaryVariables += n;

		rulesApplied[RULE_NOT_OWA]++;
		((OwaConcept) ass.getConcept()).solveComplementedAssertion(ass.getIndividual(), this);
	}


	private void ruleQuantifiedOwa(Assertion ass)
	{
		rulesApplied[RULE_OWA]++;
		((QowaConcept) ass.getConcept()).solveComplementedAssertion(ass.getIndividual(), this);
	}


	private void ruleComplementedQuantifiedOwa(Assertion ass)
	{
		rulesApplied[RULE_NOT_OWA]++;
		ruleComplementedComplexAssertion(ass);
	}


	private void ruleChoquet(Assertion ass)
	{
		rulesApplied[RULE_CHOQUET_INTEGRAL]++;
		((ChoquetIntegral) ass.getConcept()).solveAssertion(ass.getIndividual(), this);
	}


	private void ruleComplementedChoquet(Assertion ass)
	{
		rulesApplied[RULE_NOT_CHOQUET_INTEGRAL]++;
		((ChoquetIntegral) ass.getConcept()).solveComplementedAssertion(ass.getIndividual(), this);
	}


	private void ruleSugeno(Assertion ass)
	{
		rulesApplied[RULE_SUGENO_INTEGRAL]++;
		((SugenoIntegral) ass.getConcept()).solveAssertion(ass.getIndividual(), this);
	}


	private void ruleComplementedSugeno(Assertion ass)
	{
		rulesApplied[RULE_NOT_SUGENO_INTEGRAL]++;
		((SugenoIntegral) ass.getConcept()).solveComplementedAssertion(ass.getIndividual(), this);
	}


	private void ruleQuasiSugeno(Assertion ass)
	{
		rulesApplied[RULE_QUASI_SUGENO_INTEGRAL]++;
		((QsugenoIntegral) ass.getConcept()).solveAssertion(ass.getIndividual(), this);
	}


	private void ruleComplementedQuasiSugeno(Assertion ass)
	{
		rulesApplied[RULE_NOT_QUASI_SUGENO_INTEGRAL]++;
		((QsugenoIntegral) ass.getConcept()).solveComplementedAssertion(ass.getIndividual(), this);
	}


	private void ruleComplementedAtMostDatatypeRestriction(CreatedIndividual b, Assertion ass) throws FuzzyOntologyException, InconsistentOntologyException
	{
		rulesApplied[RULE_NOT_DATATYPE]++;
		DatatypeReasoner.applyNotAtMostValueRule(b, ass, this);
	}


	private void ruleComplementedAtLeastDatatypeRestriction(CreatedIndividual b, Assertion ass) throws FuzzyOntologyException, InconsistentOntologyException
	{
		rulesApplied[RULE_NOT_DATATYPE]++;
		DatatypeReasoner.applyNotAtLeastValueRule(b, ass, this);
	}


	private void ruleComplementedExactDatatypeRestriction(CreatedIndividual b, Assertion ass) throws FuzzyOntologyException, InconsistentOntologyException
	{
		rulesApplied[RULE_NOT_DATATYPE]++;
		DatatypeReasoner.applyNotExactValueRule(b, ass, this);
	}

	
	private void ruleSigmaCount(Assertion ass)
	{
		rulesApplied[RULE_SIGMA_COUNT]++;
		Variable xSigma = milp.getNewVariable(Variable.FREE_VARIABLE);	
		SigmaConcept sigma = (SigmaConcept) ass.getConcept();
		Individual i1 = ass.getIndividual();
		Collection<Individual> inds = sigma.getIndividuals();
		if ( (inds == null) || inds.isEmpty() )
			inds = getNamedIndividuals();
		Concept c = sigma.c1;
		String r = sigma.getRole();
		milp.addCardinalityList(new SigmaCount(xSigma, i1, inds, r, c));

		Variable xAss = milp.getVariable(ass);
		FuzzyConcreteConcept d = sigma.getFuzzyConcept();
		// xAss = d ( xSgigma)
		d.addEquation(xSigma, xAss, this);
	}


	private void ruleComplementedSigmaCount(Assertion ass)
	{
		rulesApplied[RULE_NOT_SIGMA_COUNT]++;
		ruleComplementedComplexAssertion(ass);	
	}


	/**
	 * Defines a concept to be crisp.
	 * @param c A concept.
	 */
	public void setCrispConcept(Concept c)
	{
		milp.addCrispConcept(c.toString());
	}


	/**
	 * Defines a role to be crisp.
	 * @param roleName A role.
	 */
	public void setCrispRole(String roleName)
	{
		milp.addCrispRole(roleName);
	}

	
	/**
	 * Sets dynamic blocking unless the current blocking is pairwise blocking.
	 */
	void setDynamicBlocking()
	{
		blockingDynamic = true;
	}


	/**
	 * Gets the version of the fuzzyDL reasoner.
	 * @return Version of the fuzzyDL reasoner.
	 */
	public double getVersion()
	{
		return VERSION;
	}


	/**
	 * Gets if a role is crisp.
	 * @param roleName Name of the concept.
	 * @return true if the semantics is classical logic or if the role is crisp, false otherwise.
	 */
	private boolean isCrispRole(String roleName)
	{
		return (semantics == FuzzyLogic.CLASSICAL) || milp.isCrispRole(roleName); 
	}


	/**
	 * Gets if a concept is crisp.
	 * @param conceptName Name of the concept.
	 * @return true if the semantics is classical logic or if the concept is crisp, false otherwise.
	 */
	private boolean isCrispConcept(String conceptName)
	{
		return (semantics == FuzzyLogic.CLASSICAL) || milp.isCrispConcept(conceptName);  
	}


	/**
	 * Gets if a concept is atomic and crisp.
	 * @param c Name A the concept.
	 * @return true if the concept is crisp (or the semantics classical logic) and atomic, false otherwise.
	 */
	private boolean isAtomicCrispConcept(Concept c)
	{
		return isCrispConcept(c.toString()) && c.isAtomic();
	}


	/**
	 * It optimizes an expression.
	 * @param objective Expression to be optimized.
	 * @return An optimal solution of the expression.
	 * @throws InconsistentOntologyException 
	 */
	Solution optimize(Expression e) throws FuzzyOntologyException, InconsistentOntologyException
	{
		if (semantics == FuzzyLogic.CLASSICAL)
			milp.setBinaryVariables();

		// N2 rule
		ruleN2();

		// N3 rule
		ruleN3();
		
		// Correctness rule
//		ruleCorrectness();

		// Sigma-count pending tasks
		milp.solveCardinalityList();
		
		Solution sol = milp.optimize(e);
		showStatistics();
		return sol;
	}


	private void showStatistics()
	{
		Util.println("\nProcessed TBox: ");
		Util.println("  A = B: " + tSyn.size());
		Util.println("  A = C: " + tDef.size());
		Util.println("  A isA X: " + tInc.size());
		Util.println("  C isA X (not absorbed): " + tG.size());
		Util.println("  Domain restrictions: " + getNumberOfDomainRestrictions() );
		Util.println("  Range restrictions: " + getNumberOfRangeRestrictions() );
//		Util.println("  Absorption time (s): " + tBoxTime);

		Util.println("\nTableau: ");
		Util.println("  Individuals: " + individuals.size());
		Util.println("  Concept assertions: " + numAssertions);
		Util.println("  Role assertions: " + numRelations);
		Util.println("  Maximal forest depth: " + maxDepth);	

		Util.println("\nReasoning rules: ");
		for (int i=0; i<rulesApplied.length; i++)
			if (rulesApplied[i] != 0)
				Util.println("  Rule " + RULE_NAMES[i] + ": " + rulesApplied[i]);

		Util.println("\nOld calculus: ");
		Util.println("  {0,1} variables (old calculus): " + oldBinaryVariables);	
		Util.println("  [0,1] variables (old calculus): " + old01Variables);	

		Util.println("\nAnswer:\n");
	}

	
	private int getNumberOfDomainRestrictions() 
	{
		int count = 0;
		for (Set<Concept> set : domainRestrictions.values())
			count += set.size();
		return count;
	}

	
	private int getNumberOfRangeRestrictions() 
	{
		int count = 0;
		for (Set<Concept> set : rangeRestrictions.values())
			count += set.size();
		return count;
	}


	// For some and all concepts, add x_{v:C} = 1 - x_{v:not C}
	void addNegatedEquations(Individual i, Concept c)
	{
		int type = c.getType();
		if ( (type == Concept.SOME) || (type == Concept.ALL) || (type == Concept.HAS_VALUE) || (type == Concept.NOT_HAS_VALUE) || (type == Concept.BOTTOM) || (type == Concept.TOP) )
			ruleComplemented(i, c);
	}


	// Computes if the type is one of the concretes (concrete, fuzzy number, or their complements)
	boolean isConcreteType(int type)
	{
		return (type == Concept.CONCRETE) || (type == Concept.CONCRETE_COMPLEMENT) || (type == Concept.FUZZY_NUMBER) || (type == Concept.FUZZY_NUMBER_COMPLEMENT) ;
	}


	// Checks if a concept c is only composed of crisp concepts or not
	boolean hasOnlyCrispSubconcepts(Concept c)
	{
		for (Concept ci : c.concepts)
		{
			if (isAtomicCrispConcept(ci) == false)
				return false;
		}
		return true;
	}


	// Gets a number to encode a concept name 
	private int getNumberFromConcept(String conceptName)
	{
		Integer number = numberOfConcepts.get(conceptName);
		if (number == null)
		{
			int value = numberOfConcepts.size();
			numberOfConcepts.put(conceptName, value);
			return value;
		}
		else
			return number;
	}


	// Gets the concept name encoded by a number 
	String getConceptFromNumber(int n)
	{
		for (String name : numberOfConcepts.keySet())
			if (numberOfConcepts.get(name) == n)
				return name;

		return null;
	}


	// Marks assertion as processed
	private void markProcessAssertion(Assertion ass)
	{
		Util.println(" Add assertion to processedAssertions : " +  milp.getNumberForAssertion(ass));
		processedAssertions.add(milp.getNumberForAssertion(ass));
	}


	// Checks if an assertion has already been processed
	private boolean isAssertionProcessed(Assertion ass)
	{
		return processedAssertions.contains(milp.getNumberForAssertion(ass));
	}


	private Digraph getDigraph(Hashtable<String,Integer> atC)
	{	
		Digraph g = new Digraph(atC.size());
		return g;
	}


	private void representTBoxWithGCIs() throws InconsistentOntologyException
	{		
		for(String atomicConcept : tSyn.keySet())
		{
			Concept a = getConcept(atomicConcept);
			for (String b : tSyn.get(atomicConcept))
				tG.add(new GeneralConceptInclusion(getConcept(b), a, Degree.ONE, GeneralConceptInclusion.LUKASIEWICZ));
		}
		
		for(String atomicConcept : axiomsAisaB.keySet())
		{
			Concept a = this.getConcept(atomicConcept);
			for (PrimitiveConceptDefinition pcd : axiomsAisaB.get(atomicConcept))
				tG.add(new GeneralConceptInclusion(pcd.getDefinition(), a, Degree.getDegree(pcd.getDegree()), pcd.getType()));
		}

		for(String atomicConcept : axiomsAequivC.keySet())
		{
			Concept a = getConcept(atomicConcept);
			for (Concept c : axiomsAequivC.get(atomicConcept))
			{
				tG.add(new GeneralConceptInclusion(a, c, Degree.ONE, GeneralConceptInclusion.LUKASIEWICZ));
				tG.add(new GeneralConceptInclusion(c, a, Degree.ONE, GeneralConceptInclusion.LUKASIEWICZ));
			}
		}

		for(String atomicConcept : axiomsAisaC.keySet())
		{
			Concept a = this.getConcept(atomicConcept);
			for (PrimitiveConceptDefinition pcd : axiomsAisaC.get(atomicConcept))
				tG.add(new GeneralConceptInclusion(pcd.getDefinition(), a, Degree.getDegree(pcd.getDegree()), pcd.getType()));
		}

		for(ConceptEquivalence ce : axiomsCequivD)
		{
			Concept a = ce.getC1();
			Concept b = ce.getC2();
			defineEquivalentConcepts(a, b);
		}

		for(HashSet<GeneralConceptInclusion> gcis : axiomsCisaA.values())
			tG.addAll(gcis);

		for(HashSet<GeneralConceptInclusion> gcis : axiomsCisaD.values())
			tG.addAll(gcis);

		// tG contains disjointConcepts
		for(String a : tDis.keySet())
			for (String c : tDis.get(a))
				tG.add(new GeneralConceptInclusion(Concept.CONCEPT_BOTTOM, Concept.gAnd(getConcept(a), getConcept(c)), Degree.ONE, GeneralConceptInclusion.LUKASIEWICZ));


		for(Individual ind : individuals.values())
			for(GeneralConceptInclusion gci : tG)
				solveGCI(ind, gci);

		solveDomainAndRangeAxioms();		
	}


	private void printTBox()
	{
		Util.println("\n*************** TBox ************************");

		Util.println("tInc:");
		for (HashSet<PrimitiveConceptDefinition> hs : tInc.values())
			for (PrimitiveConceptDefinition pcd : hs)
				Util.println("\t" + pcd);

		Util.println("\ntDef:");

		for (String s : tDef.keySet())
			Util.println("\t" + s + " = " + tDef.get(s));

		Util.println("\ntSyn:");
		for (String s : tSyn.keySet())
			for (String syn : tSyn.get(s))
				if (s.compareTo(syn) >= 0)
					Util.println("\t" + s + " = " + syn);

		Util.println("\ntDomainRestriction:");
		for(String role : domainRestrictions.keySet())
			for (Concept c : domainRestrictions.get(role))
				Util.println( "(\tdomain " + role + " " + c + ")" );

		Util.println("\ntRangeRestriction:");
		for(String role : rangeRestrictions.keySet())
			for (Concept c : rangeRestrictions.get(role))
				Util.println( "(\trange " + role + " " + c + ")" );

		Util.println("\ntDisj:");
		for(String atomicConcept : tDis.keySet())
		{
			Util.print("(disjoint " + atomicConcept);
			for (String disjC : tDis.get(atomicConcept))
				Util.print(" " + disjC);
			Util.println(")");
		}

		Util.println("\ntG:");
		for (GeneralConceptInclusion gci : tG)
			Util.println("\t" + gci);
	}


	/**
	 * Get the individuals.
	 * @return individuals.
	 */
	public Hashtable<String, Individual> getIndividuals() {
		return individuals;
	}

	
	public Collection<Individual> getNamedIndividuals() {
		Collection<Individual> c = new ArrayList<Individual> ();
		for (Individual i : individuals.values())
			if (i.isBlockable() == false)
				c.add(i); //i.clone());
		return c;
	}
	

    public void writeObjectToFile(String filepath) 
    {	 
        try 
        {
        	localSemantics = semantics;
            FileOutputStream fileOut = new FileOutputStream(filepath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(this);
            objectOut.close();
        } 
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    
    public static KnowledgeBase readObjectFromFile(String filepath) 
    {	 
        try 
        {
            FileInputStream fileIn = new FileInputStream(filepath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            KnowledgeBase kb = (KnowledgeBase) objectIn.readObject();
            objectIn.close();
            KnowledgeBase.semantics = kb.localSemantics;
            return kb;
        } 
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

	
/*
	private void ruleCorrectness() throws InconsistentOntologyException
	{
		// Case pairwise blocking
		if ( (blockingType == DOUBLE_BLOCKING) || (blockingType == ANYWHERE_DOUBLE_BLOCKING ) )
			ruleCorrectnessDouble();
		// Case subset and set blocking
		else if (blockingType != NO_BLOCKING)
			ruleCorrectnessSimple();
	}
	

	private void ruleCorrectnessSimple() throws InconsistentOntologyException
	{
		// for each directly blocked node v
		for (Individual i : individuals.values())
		{
			if (i instanceof CreatedIndividual)
			{
				CreatedIndividual v = (CreatedIndividual) i;
				if (v.isDirectlyBlocked(this))
				{
					// that is blocked by node w
					String wName = v.blockingAncestor;
					Individual w = getIndividual(wName);

					// for each C \in L(v)
					for (Concept c : v.listOfConcepts)
					{
						// add the constraint x_{v:C} = x_{w:C}
						Variable vIsC = milp.getVariable(i, c);
						Variable wIsC = milp.getVariable(w, c);
						milp.addNewConstraint(new Expression(new Term(1,vIsC), new Term(-1,wIsC)), Inequation.EQ);
						Util.println("Addded constraint: " + vIsC + " = " + wIsC);
					}						
				}
			}
		}
	}


	private void ruleCorrectnessDouble() throws InconsistentOntologyException
	{
		// for each quadruple (v,v�,w,w�) involved in pairwise directly blocked (L(v) = L(w) and L(v�) = L(w�))
		for (Individual i : individuals.values())
		{
			if (i instanceof CreatedIndividual)
			{
				CreatedIndividual v = (CreatedIndividual) i;
				if (v.isDirectlyBlocked(this))
				{
					// that is blocked by node vPrime, w, wPrime
					String wName = v.blockingAncestor;
					String vPrimeName = v.blockingAncestorY; 
					String wPrimeName = v.blockingAncestorYprime;

					Individual w = getIndividual(wName);
					Individual vPrime = getIndividual(vPrimeName);
					Individual wPrime = getIndividual(wPrimeName);
	
					// for each C \in L(v)
					for (Concept c : v.listOfConcepts)
					{
						// add the constraint x_{v:C} = x_{w:C}
						Variable vIsC = milp.getVariable(i, c);
						Variable wIsC = milp.getVariable(w, c);
						milp.addNewConstraint(new Expression(new Term(1,vIsC), new Term(-1,wIsC)), Inequation.EQ);
						Util.println("Addded constraint: " + vIsC + " = " + wIsC);
					}
					
					// for each D \in L(v')
					for (Concept d : vPrime.listOfConcepts)
					{
						// add the constraint x_{v�:D} = x_{w�:D}
						Variable vPrimeIsC = milp.getVariable(vPrime, d);
						Variable wPrimeIsC = milp.getVariable(wPrime, d);
						milp.addNewConstraint(new Expression(new Term(1,vPrimeIsC), new Term(-1,wPrimeIsC)), Inequation.EQ);
						Util.println("Addded constraint: " + vPrimeIsC + " = " + wPrimeIsC);
					}		
				}
			}
		}
	}
*/

	
	/**
	 * Checks if the knowledge base has already been classified.
	 * @return true if the knowledge base is classified; false otherwise.
	 */
	boolean isClassified()
	{
		return false;
	}


	void classify()
	{

	}


	ClassificationNode getClassificationNode(String name)
	{
		return null;
	}

	// Retrieves the value subFlags(a, b)
	double getSubFlags(ClassificationNode a, ClassificationNode b)
	{
		return 0;
	}

}
