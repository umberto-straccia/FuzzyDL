package fuzzydl.milp;

import edu.uci.ics.jung.graph.*;
import fuzzydl.*;
import fuzzydl.util.*;
//import gurobi.*;
import com.gurobi.gurobi.*;
import java.io.*;
import java.util.*;



/**
 * MILP problem manager, storing the problem and calling an external solver.
 * @author Fernando Bobillo
 */
public class MILPHelper implements Serializable
{

	private static final long serialVersionUID = 8931300466670323934L;

	
	public static boolean PARTITION = false;

	
	/**
	 * Indicates whether we want to show the membership degrees to linguistic labels or not.
	 */
	public static boolean PRINT_LABELS = true;


	/**
	 * Indicates whether we want to show the value of the variables or not.
	 */
	public static boolean PRINT_VARIABLES = true;


	private List<SigmaCount> cardinalities;
	private List<Inequation> constraints;
	private HashSet<String> crispConcepts;
	private HashSet<String> crispRoles;
	private boolean nominalVariables;
	private Hashtable<String, Integer> numberOfVariables;
	public ShowVariablesHelper showVars;
	private HashSet<String> stringFeatures;
	private Hashtable<Integer, String> stringValues;
	private List<Variable> variables;


	public MILPHelper()
	{
		cardinalities = new ArrayList<SigmaCount>();
		constraints = new ArrayList<Inequation>();
		crispConcepts = new HashSet<String>();
		crispRoles = new HashSet<String>();
		numberOfVariables = new Hashtable<String, Integer>();
		showVars = new ShowVariablesHelper();
		stringFeatures = new HashSet<String>();
		stringValues = new Hashtable<Integer, String>();
		variables = new ArrayList<Variable>();
	}


	/**
	 * Gets a copy of the object.
	 * @return A copy of the object.
	 */
	@Override
	public MILPHelper clone()
	{
		MILPHelper milp = new MILPHelper();
		milp.cardinalities = new ArrayList<SigmaCount>(cardinalities);
		milp.constraints = new ArrayList<Inequation>(constraints);
		milp.crispConcepts = new HashSet<String>(crispConcepts);
		milp.crispRoles = new HashSet<String>(crispRoles);
		milp.nominalVariables = nominalVariables;
		milp.numberOfVariables = new Hashtable<String, Integer>(numberOfVariables);
		milp.showVars = showVars.clone();
		milp.stringFeatures = new HashSet<String>(stringFeatures);
		milp.stringValues = new Hashtable<Integer, String>(stringValues);
		milp.variables = new ArrayList<Variable>(variables);
		return milp;		
	}


	/**
	 * It optimizes an expression.
	 * @param objective Expression to be optimized.
	 * @return An optimal solution of the expression.
	 */
	public Solution optimize(Expression objective)
	{
		return solveGurobi(objective);
	}


	/**
	 * Shows the membership degrees to some linguistic labels.
	 * @param fName Name of the feature.
	 * @param indName Name of the individual.
	 * @param value Value of the feature for the given individual.
	 */
	public void printInstanceOfLabels(String fName, String indName, double value)
	{
		String name = fName + "(" + indName + ")";
		ArrayList<FuzzyConcreteConcept> labels = showVars.getLabels(name);
		for (FuzzyConcreteConcept f : labels)
			System.out.println(name + " is " + f.getName() + " = " + f.getMembershipDegree(value));
	}


	/**
	 * Shows the membership degrees to some linguistic labels.
	 * @param fName Name of the form feature(individual).
	 * @param value Value of the feature for the given individual.
	 */
	private void printInstanceOfLabels(String name, double value)
	{
		ArrayList<FuzzyConcreteConcept> labels = showVars.getLabels(name);
		for (FuzzyConcreteConcept f : labels)
			System.out.println(name + " is " + f.getName() + " = " + f.getMembershipDegree(value));
	}


	/**
	 * Gets a new variable with the indicated type. 
	 * @param type Type of the new variable.
	 * @return A new variable with the indicated bound. 
	 */
	public Variable getNewVariable(char type)
	{
		Variable newVar;
		String varName;
		do {
			newVar = Variable.getNewVariable(type);
			varName = newVar.toString();
		} while (numberOfVariables.containsKey(varName));

		variables.add(newVar);
		numberOfVariables.put(varName, variables.size());
		return newVar;
	}


	/**
	 * Gets a variable with the indicated name and bound. 
	 * @param varName Name of the variable.
	 * @param type Type of the variable.
	 * @return A new variable with the indicated type. 
	 */
	// Only used by DatatypeReasoner
	public Variable getVariable(String varName, char type)
	{
		Variable newVar = getVariable(varName);
		newVar.setType(type);
		return newVar;
	}


	/**
	 * Gets a variable with the given name, creating a new one of type UP_BOUND_BY_ONE if it does not exist.
	 * @param name Name of the variable.
	 * @return A variable with the given name.
	 */
	public Variable getVariable(String name)
	{
		if (numberOfVariables.containsKey(name))
		{		
			for(Variable var : variables)
				if (var.toString().equals(name))
					return var;
		}

		Variable var = new Variable(name, Variable.UP_VARIABLE);
		variables.add(var);
		numberOfVariables.put(var.toString(), variables.size());
		return var;
	}

	
	/**
	 * Cheks if there is a variable with the given name.
	 * @param name Name of the variable.
	 * @return true if the variable exists; false otherwise
	 */
	public boolean hasVariable(String name)
	{
		return numberOfVariables.containsKey(name);
	}


	/**
	 * Cheks if there is a variable for a concept assertion.
	 * @param ass An assertion.
	 * @return true if the variable exists; false otherwise
	 */
	public boolean hasVariable(Assertion ass)
	{
		return hasVariable(ass.getNameWithoutDegree());
	}




	/**
	 * Gets a variable taking the value of an individual i belonging to the nominal concept {i}.
	 * @param i1 An individual.
	 * @return A variable taking the value of the assertion i : {i}.
	 */
	public Variable getNominalVariable(String i1)
	{
		return getNominalVariable(i1, i1);
	}

	
	/**
	 * Exists a variable taking the value of an individual i belonging to the nominal concept {i.
	 * @param i An individual.
	 * @return true if there is a variable taking the value of the assertion i: {i}; false otherwise
	 */
	public boolean existsNominalVariable(String i)
	{
		String varName = i + ":" + "{ " + i + " }";
		return numberOfVariables.contains(varName);
	}


	/**
	 * Gets a variable taking the value of an individual i1 belonging to the nominal concept {i2}.
	 * @param i1 An individual that is subject of the assertion.
	 * @param i2 An individual representing the nominal concept.
	 * @return A variable taking the value of the assertion i1: {i2}.
	 */
	public Variable getNominalVariable(String i1, String i2)
	{
		String varName = i1 + ":" + "{ " + i2 + " }";
		Variable v = getVariable(varName);
		v.setType(Variable.BINARY_VARIABLE);
		return v;
	}

	
	/**
	 * Checks if a variable is a nominal variable.
	 * @param i A variable name.
	 * @return True if it is a nominal variable, false otherwise.
	 */
	private boolean isNominalVariable(String i)
	{
		String[] s = i.split(":\\{ ");
		if (s.length != 2)
			return false;
		return s[1].compareTo(s[0] + " }") == 0;
		//return i.matches("(.*):\\{ (.*) \\}");
	}


	/**
	 * Checks if a collection of terms has a nominal variable.
	 * @param terms A collection of terms.
	 * @return True if there is a nominal variable, false otherwise.
	 */
	private boolean hasNominalVariable(Collection<Term> terms)
	{
		for (Term term : terms)
		{
			Variable var =  term.getVar();
			if (isNominalVariable(var.toString()))
				return true;
		}
		return false;
	}
	
	
	/**
	 * Gets a variable taking the value of an individual i1 not belonging to the nominal concept {i2}.
	 * @param i1 An individual that is subject of the assertion.
	 * @param i2 An individual representing the nominal concept.
	 * @return A variable taking the value of the assertion i1: not {i2}.
	 */
	public Variable getNegatedNominalVariable(String i1, String i2)
	{
		String varName = i1 + ": not " + "{ " + i2 + " }";
		boolean flag = numberOfVariables.contains(varName);
		Variable v = getVariable(varName);
		
		// First time the variable is created, x_{a:{o} } = 1 - x_{a: not {o} }
		if (flag == false)
		{
			v.setType(Variable.BINARY_VARIABLE);
			Variable notV = getNominalVariable(i1, i2);
			addNewConstraint(new Expression(1, new Term(-1, v), new Term(-1,notV) ), Inequation.EQ);
		}
		return v;
	}


	/**
	 * Gets a variable taking the value of a concept assertion, creating a new one of type UP_BOUND_BY_ONE if it does not exist.
	 * @param ass A fuzzy concept assertion.
	 * @return A variable taking the value of the assertion.
	 */
	public Variable getVariable(Assertion ass)
	{
		return getVariable(ass.getIndividual(), ass.getConcept());
	}


	/**
	 * Gets a variable taking the value of a role assertion, creating a new one of type UP_BOUND_BY_ONE if it does not exist.
	 * @param rel A fuzzy role assertion.
	 * @return A variable taking the value of the assertion.
	 */
	public Variable getVariable(Relation rel)
	{
		Individual a = rel.getSubjectIndividual();
		Individual b = rel.getObjectIndividual();
		String role = rel.getRoleName();
		return getVariable(a, b, role);
	}


	/**
	 * Gets a variable taking the value of a universal restriction, creating a new one of type UP_BOUND_BY_ONE if it does not exist.
	 * @param ind Subject individual of the restrictions.
	 * @param restric A fuzzy universal restriction.
	 * @return A variable taking the value of the assertion.
	 */
	public Variable getVariable(Individual ind, Restriction restric)
	{
		Variable var = getVariable(ind + ":" + restric.getNameWithoutDegree());
		if (showVars.showIndividuals(ind.toString()))
			showVars.addVariable(var, var.toString());
		return var;
	}


	/**
	 * Gets a variable taking the value of a concept assertion, creating a new one of type UP_BOUND_BY_ONE if it does not exist.
	 * @param ind An individual.
	 * @param c A fuzzy concept.
	 * @return A variable taking the value of the assertion.
	 */
	public Variable getVariable(Individual ind, Concept c)
	{
		if (c.getType() == Concept.HAS_VALUE)
		{
			String r = c.getRole();
			String b = (String) c.getValue();
			return getVariable(ind.toString(), b, r, Variable.UP_VARIABLE);
		}
		else
			return getVariable(ind, c.toString());
	}


	/**
	 * Gets a variable taking the value of a concept assertion, creating a new one of type UP_BOUND_BY_ONE if it does not exist.
	 * @param ind An individual.
	 * @param conceptName A fuzzy concept name.
	 * @return A variable taking the value of the assertion.
	 */
	public Variable getVariable(Individual ind, String conceptName)
	{
		Variable var = getVariable(ind + ":" + conceptName);
		if (crispConcepts.contains(conceptName))
			var.setBinaryVariable();
		if (showVars.showIndividuals(ind.toString()))
			showVars.addVariable(var, var.toString());
		if (showVars.showConcepts(conceptName))
			showVars.addVariable(var, var.toString());
		return var;
	}


	/**
	 * Gets a variable taking the value of a role assertion, creating a new one of type UP_BOUND_BY_ONE if it does not exist.
	 * @param a Object individual.
	 * @param b Subject individual.
	 * @param role Role.
	 * @return A variable taking the value of the assertion.
	 */
	public Variable getVariable(Individual a, Individual b, String role)
	{
		return getVariable(a, b, role, Variable.UP_VARIABLE);
	}


	/**
	 * Checks if a variable taking the value of a role assertion exists, creating a new one of type UP_BOUND_BY_ONE if it does not exist.
	 * @param a Object individual.
	 * @param b Subject individual.
	 * @param role Role.
	 * @return true if the value exists; false otherwise.
	 */
	public boolean existsVariable(Individual a, Individual b, String role)
	{
		String varName = "(" + a + "," + b + "):" + role;
		return numberOfVariables.containsKey(varName);	
	}


	/**
	 * Gets a variable taking the value of a role assertion, creating a new one of type UP_BOUND_BY_ONE if it does not exist.
	 * @param a Object individual.
	 * @param b Subject individual.
	 * @param role Role.
	 * @param type Type of the variable.
	 * @return A variable taking the value of the assertion.
	 */
	public Variable getVariable(Individual a, Individual b, String role, char type)
	{
		return getVariable(a.toString(), b.toString(), role, type);
	}


	private Variable getVariable(String a, String b, String role, char type)
	{
		String varName = "(" + a + "," + b + "):" + role;
		Variable var = getVariable(varName);
		if (crispRoles.contains(role))
			var.setBinaryVariable();
		if (showVars.showAbstractRoleFillers(role, a))
			showVars.addVariable(var, varName);
		if (showVars.showConcreteFillers(role, a))
			showVars.addVariable(var, varName);
		var.setType(type);
		return var;
	}



	/**
	 * Gets a variable taking the value of a concrete individual.
	 * @param ind A concrete individual.
	 * @return A variable taking the value of the individual.
	 */
	public Variable getVariable(CreatedIndividual ind)
	{
		return getVariable(ind, Variable.FREE_VARIABLE);
	}



	/**
	 * Gets a variable taking the value of a concrete individual.
	 * @param ind A concrete individual.
	 * @param bound char to the variable.
	 * @return A variable taking the value of the individual.
	 */
	public Variable getVariable(CreatedIndividual ind, char bound)
	{
		String parentName;
		if (ind.getParent() == null)
			parentName = "unknownParent";
		else 
			parentName = ind.getParent().toString();

		String featureName = ind.getRoleName();
		if (featureName == null)
			featureName = "unknownFeature";

		String name = featureName + "(" + parentName + ")";
		if (name.equals("unknownFeature(unknownParent)"))
			name = ind.toString();

		Variable Xc;
		if (numberOfVariables.containsKey(name))
			Xc = getVariable(name);
		else
		{
			Xc = getVariable(name);
			if (showVars.showConcreteFillers(featureName, parentName))
				showVars.addVariable(Xc, name);
			Xc.setType(bound);
		}

		return Xc;
	}


	/**
	 * Adds a new inequality of the form:  expr constraintType 0.
	 * @param expr An expression in the left side of the inequality.
	 * @param constraintType Type of the constraint (EQ, GR, LE).
	 */
	public void addNewConstraint(Expression expr, char constraintType)
	{
		constraints.add(new Inequation(expr, constraintType));
	}


	/**
	 * Adds a new inequality of the form: x &gt;= n.
	 * @param x A variable.
	 * @param n A real number.
	 */
	public void addNewConstraint(Variable x, double n)
	{
		addNewConstraint(new Expression(new Term(1,x)), Inequation.GE, new DegreeNumeric(n) );
	}


	/**
	 * Given a fuzzy assertion a:C &gt;= L and a number n, adds an inequality of the form: xAss &gt;= n.
	 * @param ass A fuzzy assertion.
	 * @param n A real number.
	 */
	public void addNewConstraint(Assertion ass, double n)
	{
		addNewConstraint(getVariable(ass), n);
	}


	/**
	 * Add an inequality of the form: x &gt;= D.
	 * @param x A fuzzy assertion.
	 * @param D A degree.
	 */
	public void addNewConstraint(Variable x, Degree D)
	{
		addNewConstraint(new Expression(new Term(1,x)), Inequation.GE, D);
	}


	/**
	 * Adds a new inequality encoded in a fuzzy assertion.
	 * @param ass A fuzzy assertion.
	 */
	public void addNewConstraint(Assertion ass)
	{
		Variable xAss = getVariable(ass);
		String assName = xAss.toString();
		Degree deg = ass.getLowerLimit();
		if (deg instanceof DegreeVariable)
		{
			String degName = ((DegreeVariable) deg).getVariable().toString();
			if (degName.compareTo(assName) == 0)
				return;
		}
		addNewConstraint(xAss, deg);
	}


	/**
	 * Adds a new inequality of the form: expr constraintType degree.
	 * @param expr An expression in the left side of the inequality.
	 * @param constraintType Type of the constraint (EQ, GR, LE).
	 * @param degree A degree in the right side of the inequality.
	 */
	public void addNewConstraint(Expression expr, char constraintType, Degree degree)
	{
		constraints.add(degree.createInequalityWithDegreeRHS(expr, constraintType));
	}


	/**
	 * Adds a new inequality of the form: expr constraintType n.
	 * @param expr An expression in the left side of the inequality.
	 * @param constraintType Type of the constraint (EQ, GR, LE).
	 * @param n A real number expression in the right side of the inequality.
	 */
	public void addNewConstraint(Expression expr, char constraintType, double n)
	{
		addNewConstraint(expr, constraintType, Degree.getDegree(n));
	}


	/**
	 * Add an equality of the form: var1 = var2.
	 * @param var1 A variable.
	 * @param var2 Another variable.
	 */
	public void addEquality(Variable var1, Variable var2)
	{
		addNewConstraint(new Expression(new Term(1,var1), new Term(-1, var2)), Inequation.EQ);
	}


	/**
	 * Adds a string feature.
	 * @param role A string feature.
	 */
	public void addStringFeature(String role)
	{
		stringFeatures.add(role);
	}


	/**
	 * Relates the value of a string feature with an integer value.
	 * @param value Value of a string feature.
	 * @param intValue Corresponding integer value.
	 */
	public void addStringValue(String value, int intValue)
	{
		stringValues.put(intValue, value);
	}


	/**
	 * Replaces the name of the variables including an individual name with the name of another individual name.
	 * @param oldName Old individual name.
	 * @param newName New individual name.
	 * @param oldIsCreatedIndividual Indicates whether the old individual is a created individual or not.
	 */
	public void changeVariableNames(String oldName, String newName, boolean oldIsCreatedIndividual)
	{
		String[] oldValues = new String[3];
		oldValues[0] = oldName + ",";
		oldValues[1] = "," + oldName;
		oldValues[2] = oldName + ":";

		String[] newValues = new String[3];
		newValues[0] = newName + ",";
		newValues[1] = "," + newName;
		newValues[2] = newName + ":";

		List<Variable> toProcess = new ArrayList<Variable> (variables);
		for (Variable v1 : toProcess)
		{
			String name = v1.toString();
			for (int i=0; i<3; i++)
			{
				if (name.contains(oldValues[i]))
				{
					String name2 = name.replaceFirst(oldValues[i], newValues[i]);
					Variable v2 = getVariable(name2);
					if (checkIfReplacementIsNeeded(v1, oldValues[i], v2, newValues[i]) )
					{
						if (oldIsCreatedIndividual == true)
							addEquality(v1, v2);
						else
						{
							// a:{b} => x_{a:C}) \geq  x_{b:C}
							Variable aIsb = getNominalVariable(newName, oldName);					
							addNewConstraint(new Expression(1, new Term(-1,aIsb), new Term(1,v1), new Term(-1,v2)), Inequation.GE);
						}						
					}
				}
			}
		}
	}


	private boolean checkIfReplacementIsNeeded(Variable v1, String s1, Variable v2, String s2)
	{
		String name1 = v1.toString();
		int begin1 = name1.indexOf(s1);
		String name2 = v2.toString();
		int begin2 = name2.indexOf(s2);

		// They are not similar because the parts before s1 and s2 have different lengths.
		if (begin1 != begin2)
			return false;

		// If the parts before and after s1/s2 coincide, they are similar.
		if (name1.substring(0, begin1).equals(name2.substring(0, begin2)) && 
			name1.substring(begin1 + s1.length(), name1.length()).equals(name2.substring(begin2 + s2.length(), name2.length()))
		)
		{
			return true;
		}
		else
			return false;
	}


	public Variable[] getOrderedPermutation(Variable[] x)
	{
		int n = x.length;
		Variable z[][] = new Variable[n][n];
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
				z[i][j] = getNewVariable(Variable.BINARY_VARIABLE);
		return getOrderedPermutation(x, z);
	}


	/**
	 * Gets an ordered permutation of the variables.
	 * @param x A vector of input variables.
	 * @param z A matrix of intermediate variables.
	 * @return A permutation of the input variables such that y[0] &gt;= y[1] &gt;= ... &gt;= y[n-1] 
	 */
	public Variable[] getOrderedPermutation(Variable[] x, Variable [][] z)
	{
		int n = x.length;

		// New n [0,1] variables yi
		Variable y[] = new Variable[n];
		for(int i=0; i<n; i++)
			y[i] = getNewVariable(Variable.UP_VARIABLE);

		// y1 &gt;= y2 &gt;= ... &gt;= yn
		for(int i=0; i<n-1; i++)
			addNewConstraint(new Expression(new Term(1,y[i]), new Term(-1,y[i+1])), Inequation.GE);

		// for each i,j : yi - kz_{ij} <= xj
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
				addNewConstraint(new Expression(new Term(1, x[j]), new Term(-1, y[i]), new Term(1, z[i][j])), Inequation.GE);

		// for each i,j : xj <= yi + kz_{ij}
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
				addNewConstraint(new Expression(new Term(1, x[j]), new Term(-1, y[i]), new Term(-1, z[i][j])), Inequation.LE);

		// for each i : \sum_{j} z_{ij} = n - 1
		for(int i=0; i<n; i++)
		{
			Expression exp = new Expression(1-n);
			for(int j=0; j<n; j++)
				exp.addTerm(new Term(1, z[i][j]));
			addNewConstraint(exp, Inequation.EQ);
		}

		// for each j : \sum_{i} z_{ij} = n - 1
		for(int j=0; j<n; j++)
		{
			Expression exp = new Expression(1-n);
			for(int i=0; i<n; i++)
				exp.addTerm(new Term(1, z[i][j]));
			addNewConstraint(exp, Inequation.EQ);
		}

		return y;
	}


	private int BFS(UndirectedSparseGraph<Integer, Integer> graph, Hashtable<Integer, Integer> solution)
	{
		// Number of nodes
		int n = graph.getVertexCount();

		// solution is a mapping: variable -> partition
		// Initial partition value is 0
		for (int i = 0; i < n; i++)
			solution.put(i, 0);

		// Number of partition
		int p = 1;

		// Iterate over not processed nodes
		Queue<Integer> queue = new LinkedList<Integer>();
		int i = -1;
		do
		{
			i++;

			// Skip node if processed
			if (solution.get(i) != 0)
				continue;

			queue = new LinkedList<Integer>();
			queue.add(i);
			solution.put(i, p);
			computePartition(queue, solution, p, graph);

			// Next partition
			p++;

		} while (i < n - 1);

		return p-1;
	}


	private void computePartition(Queue<Integer> queue, Hashtable<Integer, Integer> solution, int p, UndirectedSparseGraph<Integer, Integer> graph)
	{
		while (queue.isEmpty() == false)
		{
			Integer current = queue.remove();
			Collection<Integer> neighbors = graph.getNeighbors(current);
			if (neighbors != null)
				for (int j : neighbors)
					if (solution.get(j) == 0)
					{
						solution.put(j, p);
						queue.add(j);
					}
		}
	}
	
	
	public void setNominalVariables(boolean value)
	{
		nominalVariables = value;
	}


	private void removeNominalVariables()
	{		
		for (Iterator<Inequation> it = constraints.iterator(); it.hasNext(); ) 
		{
			Inequation constraint = it.next();	
			Collection<Term> terms = constraint.getTerms();
			if (hasNominalVariable(terms))
			{
				it.remove();
			}
		}
		
		for (Iterator<Variable> it = variables.iterator(); it.hasNext(); ) 
		{
			Variable var = it.next();
			if (isNominalVariable(var.toString()))
				it.remove();	
		}
	}


	private UndirectedSparseGraph<Integer, Integer> getGraph()
	{
		UndirectedSparseGraph<Integer, Integer> g = new UndirectedSparseGraph<Integer, Integer> ();

		// Create nodes
		int n = variables.size();
		for(int i=0; i<n; i++)
			g.addVertex(i);

		// Create edges
		int edge = 0;
		for(Inequation constraint : constraints)
		{
			Collection<Term> terms = constraint.getTerms();
			if (terms.size() > 1)
			{
				Iterator<Term> it = terms.iterator();
				Term first = it.next();
				int firstVar = variables.indexOf(first.getVar());
				while (it.hasNext())
				{
					Term other = it.next();
					int otherVar = variables.indexOf(other.getVar());
					// Edge between first and other
					g.addEdge(edge++, firstVar, otherVar);
				}
			}
		}
		return g;
	}

	
/*
	private int getPartitionIndex(Hashtable<Integer, Integer> solution, int indexOfVariable)
	{
		return solution.get(indexOfVariable) - 1;
	}
*/

	
	private Solution solveGurobiUsingPartitions(Expression objective)
	{
		List<Variable> objectives = new ArrayList<Variable> ();

		// Partition time
		long iniTime = System.currentTimeMillis();

		// Graph
		Hashtable<Integer, Integer> solution = new Hashtable<Integer, Integer> ();
		int numPartitions = BFS(getGraph(), solution);

		// Mapping partition -> number of objective variables in partition
		int[] numVariablesInPartition = new int[numPartitions];
		for(int i=0; i<numPartitions; i++)
			numVariablesInPartition[i] = 0;

		// Compute objective coefficients
		for(Term term : objective.getTerms())
		{	
			Variable v = term.getVar();
			objectives.add(v);
			int index = variables.indexOf(v);
			int numPartition = solution.get(index) - 1;
			numVariablesInPartition[numPartition]++;
		}

		// Compute two or more partitions
		int twoOrMore = 0;
		int count = 0;
		for(int i=0; i<numPartitions; i++)
			if (numVariablesInPartition[i] > 1)
			{
				twoOrMore++;
				count += numVariablesInPartition[i];
			}

		long endTime = System.currentTimeMillis();
		double totalTime = ((endTime - iniTime)); // / 1000;
		System.out.println("Partition time: " + totalTime + " ms");
		
		if (twoOrMore == 0)
		{
			MILPHelper.PARTITION = false;
			return solveGurobi(objective);
		}

		// Specific algorithm starts here
		
		try
		{
			System.out.println("*** There are " + twoOrMore + " partitions with " + count + " dependent objective variables ");

			// PROBLEMS with 1 or less

			GRBEnv env = new GRBEnv(); // new GRBEnv("gurobi.log");
			if (ConfigReader.DEBUG_PRINT == false)
				env.set(GRB.IntParam.OutputFlag, 0);
			env.set(GRB.DoubleParam.IntFeasTol, 1e-6);
	
			GRBModel model = new GRBModel(env);

			// Create variables
			int size = variables.size();
			GRBVar[] vars = new GRBVar[size];

			for (int i=0; i<size; i++)
			{
				int numPartition = solution.get(i) - 1;
				if (numVariablesInPartition[numPartition] > 1)
					continue; // Next variable
				Variable v = variables.get(i);
				char type = v.getType();
				Util.println("Variable " + v.getLowerBound() + " " + v.getUpperBound()  + " " +  0  + " " + type + " " + v);
				vars[i] = model.addVar(v.getLowerBound(), v.getUpperBound(), 0, type, "" + numberOfVariables.get(v.toString()));
			}

			// Integrate new variables
			model.update();

			// Add constraints
			for(Inequation constraint : constraints)
			{
				Util.print("Constraint "); 
				GRBLinExpr expr = new GRBLinExpr();
				
				for(Term term : constraint.getTerms())
				{
					int index = variables.indexOf(term.getVar());
					int numPartition = solution.get(index) - 1;
					if (numVariablesInPartition[numPartition] > 1)
						break; // Exit for term loop

					GRBVar v = vars[index];
					double c = term.getCoeff();
					expr.addTerm(c, v);
					Util.print(c + " " + term.getVar().toString() + " "); 
				}
				if (expr.size() > 0)
				{
					model.addConstr(expr, constraint.getType(), constraint.getConstant(), null);
					Util.println(constraint.getStringType() + " " + constraint.getConstant());					
					}
			}

			// Integrate new constraints
			model.update();

			// Optimize model
			model.optimize();

			// Output
			Util.println("\nModel:\n");

			// Return solution
			if (model.get(GRB.IntAttr.SolCount) == 0)
				return new Solution(Solution.INCONSISTENT_KB);
			
			// One for each partition with two or more variables, plus one for the rest (all partitions with 0 and 1)
			Solution sol = new Solution(1);

			// PROBLEMS with 2 or more
			for (Variable objVar : objectives)
			{
				env = new GRBEnv(); // new GRBEnv("gurobi.log");
				if (ConfigReader.DEBUG_PRINT == false)
					env.set(GRB.IntParam.OutputFlag, 0);
				env.set(GRB.DoubleParam.IntFeasTol, 1e-6);
		
				model = new GRBModel(env);

				double[] objectiveValue = new double[1];
				int index = variables.indexOf(objVar);
				int problem = solution.get(index) - 1;
				objectiveValue[0] = 1;

				vars = new GRBVar[size];

				// Create variables
				for (int i=0; i<size; i++)
				{
					int numPartition = solution.get(i) - 1;
					if (numPartition != problem)
						continue;

					Variable v = variables.get(i);
					char type = v.getType();
					double ov = (i == variables.indexOf(objVar)) ? 1 : 0;
					Util.println("Variable " + v.getLowerBound() + " " + v.getUpperBound()  + " " +  ov  + " " + type + " " + v);
					vars[i] = model.addVar(v.getLowerBound(), v.getUpperBound(), ov, type, "" + numberOfVariables.get(v.toString()));
				}

				// Integrate new variables
				model.update();

				// Add constraints
				for(Inequation constraint : constraints)
				{
					Util.print("Constraint "); 
					GRBLinExpr expr = new GRBLinExpr();
					
					for(Term term : constraint.getTerms())
					{
						index = variables.indexOf(term.getVar());
						int numPartition = solution.get(index) - 1;
						if (numPartition != problem)
							break; // Exit for term loop
						
						GRBVar v = vars[index];
						double c = term.getCoeff();
						expr.addTerm(c, v);
						Util.print(c + " " + term.getVar().toString() + " "); 
					}
					if (expr.size() > 0)
					{
						model.addConstr(expr, constraint.getType(), constraint.getConstant(), null);
						Util.println(constraint.getStringType() + " " + constraint.getConstant());					
					}
				}

				// Integrate new constraints
				model.update();

				// Optimize model
				model.optimize();

				// Output
				Util.println("\nModel:\n");

				// Return solution
				if (model.get(GRB.IntAttr.SolCount) == 0)
					return new Solution(Solution.INCONSISTENT_KB);
				else
				{
					double result = Util.round(Math.abs(model.get(GRB.DoubleAttr.ObjVal)));
					String varName = objVar.toString();
					sol.addShowedVariable(varName, result);
				}
			}
						
			return sol;
		}
		catch (GRBException e) 
		{
			Util.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
			return null;
		}
	}
	
	

	/**
	 * Solves a MILP problem using Gurobi.
	 * @return An optimal solution to the problem.
	 */
	private Solution solveGurobi(Expression objective)
	{
		if (! nominalVariables)
			removeNominalVariables();

		if (PARTITION)
			return solveGurobiUsingPartitions(objective);

		try 
		{
			Util.println("\nRunning MILP solver: Gurobi\n");
			int numBinaryVariables = 0;
			int numFreeVariables = 0;
			int numIntegerVariables = 0;
			int numUpVariables = 0;
			Solution sol = null;

			int size = variables.size();
			double[] objectiveValue = new double[size];
			if (objective != null)
			{
				// Compute objective coefficients
				for(Term term : objective.getTerms())
				{
					int index = variables.indexOf(term.getVar());
					objectiveValue[index] = term.getCoeff();
				}
			}

			GRBEnv env = new GRBEnv(); // new GRBEnv("gurobi.log");
			if (ConfigReader.DEBUG_PRINT == false)
				env.set(GRB.IntParam.OutputFlag, 0);
			env.set(GRB.DoubleParam.IntFeasTol, 1e-6);

			GRBModel model = new GRBModel(env);
			size = variables.size();
			GRBVar[] vars = new GRBVar[size];
			boolean[] showVariable = new boolean[size];

			// Create variables
			Collection<Variable> myVars = showVars.getVariables();
			for (int i=0; i<size; i++)
			{
				Variable v = variables.get(i);
				char type = v.getType();
				double ov = objectiveValue[i];
				Util.println("Variable " + v.getLowerBound() + " " + v.getUpperBound()  + " " +  ov  + " " + type + " " + v);

				vars[i] = model.addVar(v.getLowerBound(), v.getUpperBound(), ov, type, "" + numberOfVariables.get(v.toString()));
				if (myVars.contains(v))
					showVariable[i] = true;

				if (ConfigReader.DEBUG_PRINT)
				{
					switch (type)
					{
						case Variable.BINARY_VARIABLE:
							numBinaryVariables++;
							break;
	
						case Variable.FREE_VARIABLE:
							 numFreeVariables++;
							 break;
	
						case Variable.INTEGER_VARIABLE:
							numIntegerVariables++;
							 break;
	
						case Variable.UP_VARIABLE:
							numUpVariables++;
							 break;
					}
				}
			}

			// Integrate new variables
			model.update();

			// Add constraints
			for(Inequation constraint : constraints)
			{
				Util.print("Constraint "); 
				GRBLinExpr expr = new GRBLinExpr();
				for(Term term : constraint.getTerms())
				{
					int index = variables.indexOf(term.getVar());
					GRBVar v = vars[index];
					double c = term.getCoeff();
					expr.addTerm(c, v);
					Util.print(c + " " + term.getVar().toString() + " "); 
				}
				model.addConstr(expr, constraint.getType(), constraint.getConstant(), null);
				Util.println(constraint.getStringType() + " " + constraint.getConstant());				
			}			

			// Integrate new constraints
			model.update();

			// Optimize model
			model.optimize();

			// Output
			Util.println("\nModel:\n");

			// Return solution
			if (model.get(GRB.IntAttr.SolCount) == 0)
				sol = new Solution(Solution.INCONSISTENT_KB);
			else
			{
				double result = Util.round(Math.abs(model.get(GRB.DoubleAttr.ObjVal)));
				sol = new Solution(result);

				for (int i=0; i<size; i++)
					if (ConfigReader.DEBUG_PRINT || showVariable[i])
					{
						String name = vars[i].get(GRB.StringAttr.VarName);
						double value = Util.round(vars[i].get(GRB.DoubleAttr.X));
						String varName = getNameForInteger(Integer.parseInt(name));
						if (showVariable[i])
							sol.addShowedVariable(varName, value);
						
						//if (PRINT_VARIABLES)
							System.out.println(varName + " = " + value);
						if (PRINT_LABELS)
							printInstanceOfLabels(name, value);
					}
			}

			Util.println("\n**************************************************");
			Util.println("Statistics:");
			Util.println("**************************************************\n");

			Util.println("MILP problem: ");

			// Show number of variables
			Util.println("  [0,1] variables: " + numUpVariables);
			Util.println("  {0,1} variables: " + numBinaryVariables);
			Util.println("  Free variables: " + numFreeVariables);
			Util.println("  Integer variables: " + numIntegerVariables);
			Util.println("  Total variables: " + variables.size());

			// Show number of constraints
			Util.println(" Constraints: " + constraints.size());

			return sol;

		} 
		catch (GRBException e) 
		{
			Util.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
			return null;
		}
	}


	/**
	 * Defines a concept to be crisp.
	 * @param conceptName A concept name.
	 */
	public void addCrispConcept(String conceptName)
	{
		crispConcepts.add(conceptName);
	}


	/**
	 * Defines a role to be crisp.
	 * @param roleName A role name.
	 */
	public void addCrispRole(String roleName)
	{
		crispRoles.add(roleName);
	}


	/**
	 * Checks if a concept is crisp or not.
	 * @param conceptName A concept name.
	 * @return true if the concept is crisp; false otherwise.
	 */	
	public boolean isCrispConcept(String conceptName)
	{
		return crispConcepts.contains(conceptName);
	}


	/**
	 * Checks if a role is crisp or not.
	 * @param roleName A role name.
	 * @return true if the role is crisp; false otherwise.
	 */	
	public boolean isCrispRole(String roleName)
	{
		return crispRoles.contains(roleName);
	}


	/**
	 * Transforms every [0,1]-variable into a {0,1} variable.
	 */
	public void setBinaryVariables()
	{
		// Umberto
		// set all variables binary, except 
		// - those that hold the value of a datatype filler
		// - free variables in constraints
		for (Variable v : variables) 
			//if (!v.getDatatypeFillerType()) v.setBinaryVariable();
			if (!v.getDatatypeFillerType() && !(v.getType() == Variable.FREE_VARIABLE) && !(v.getType() == Variable.INTEGER_VARIABLE))
				v.setBinaryVariable();
	}


	// Gets the name of the i-th variable
	private String getNameForInteger(Integer i)
	{
		for (String name : numberOfVariables.keySet())
		{
			Integer i2 = numberOfVariables.get(name);
			if (i.intValue() == i2.intValue())
				return name;
		}
		return null;
	}


	/**
	 * Gets an integer codification of an assertion.
	 * @param ass An assertion.
	 * @return An integer codification of ass.
	 */
	public int getNumberForAssertion(Assertion ass)
	{
		return numberOfVariables.get(getVariable(ass).toString());
	}


	/**
	 * Add a contradiction to make the fuzzy KB unsatisfiable
	 */
	public void addContradiction( )
	{
		constraints = new ArrayList<Inequation>();
		addNewConstraint(new Expression(1), Inequation.EQ);
	}
	
	
	/**
	 * SigmaCount(r,C,O,d)^I(w) = d^I(
	 * 
	 * @param xSigma Free variable taking the value  \sigma_{i2 \in O} r(i1, i2) \otimes C(i2)
	 * @param i1 Name of an individual, subject of the relation.
	 * @param O Set of individuals candidates to be the object of the relation.
	 * @param r Role.
	 * @param C Concept.
	 */
	public void addCardinalityList(SigmaCount sc)
	{
		cardinalities.add(sc);
	}


	/** Solve the list of sigma-count pending tasks */
	public void solveCardinalityList()
	{
		for (SigmaCount sc : cardinalities)
			solveCardinality(sc);
	}


	private void solveCardinality(SigmaCount sc)
	{
		solveCardinality(sc.getVariable(), sc.getIndividual(), sc.getIndividuals(), sc.getRole(), sc.getConcept());
	}


	/**
	 * SigmaCount(r,C,O,d)^I(w) = d^I(
	 * 
	 * @param xSigma Free variable taking the value  \sigma_{i2 \in O} r(i1, i2) \otimes C(i2)
	 * @param i1 Name of an individual, subject of the relation.
	 * @param O Set of individuals candidates to be the object of the relation.
	 * @param r Role.
	 * @param C Concept.
	 */
	private void solveCardinality(Variable xSigma, Individual i1, Collection<Individual> O, String r, Concept C)
	{
		// kb is needed to get the semantics and to use the method addSigmaCountEquation
		List<Variable> xwInCi = new ArrayList<Variable>();
		for (Individual i2 : O)
		{
			// Only for known r-fillers, the relation must already exist!
			if (existsVariable(i1, i2, r))
			{
				Variable xAss = getVariable(i1, i2, r);
				Variable xwInC = getVariable(i2, C);
				Variable xAnd = getNewVariable(Variable.UP_VARIABLE);
				xwInCi.add(xAnd); 

				// xAnd = xwInC \otimes xAss		
				if (KnowledgeBase.semantics == FuzzyLogic.LUKASIEWICZ)
					LukasiewiczSolver.andEquation(xAnd, xwInC, xAss, this);
				else
					ZadehSolver.andEquation(xAnd, xwInC, xAss, this);
			}
		}

		// xSigma = cardinality(xwInCi)
		addSigmaCountEquation(xSigma, xwInCi);
	}
	
	
	// xSigma = sigma-count(xwInCi)
	private void addSigmaCountEquation(Variable xSigma, List<Variable> xwInCi)
	{
		int n = xwInCi.size();
		if (n > 0)
		{
			Term[] terms = new Term[n];
			for (int i=0; i<n; i++)
				terms[i] = new Term(1, xwInCi.get(i));
			addNewConstraint(new Expression(terms), Inequation.EQ, Degree.getDegree(xSigma) );
		}
	}


}

