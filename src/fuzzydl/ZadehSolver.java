package fuzzydl;

import java.util.*;

import fuzzydl.exception.InconsistentOntologyException;
import fuzzydl.milp.*;
import fuzzydl.util.*;

/**
 * Solver for Zadeh fuzzy logic semantics.
 * @author Fernando Bobillo
 */
public class ZadehSolver
{

	/**
	 * Solves a conjunction fuzzy assertion with respect to a reference fuzzy KB.
	 * @param ass A conjunction fuzzy assertion.
	 * @param kb A reference fuzzy KB.
	 */
	public static void solveAnd(Assertion ass, KnowledgeBase kb)
	{
		Concept c = ass.getConcept();
		Individual ind = ass.getIndividual();
		Variable xAss = kb.milp.getVariable(ass);
		Vector<Variable> v = new Vector<Variable>();
		for (Concept ci : c.concepts)
		{
			Variable var = kb.milp.getVariable(ind, ci);
			kb.addAssertion(ind, ci, Degree.getDegree(var) );
			v.add(var);
		}
		andEquation(v, xAss, kb.milp);
	}


	// Compute z = x1 AND x2 AND ... AND xN
	static void andEquation(Vector<Variable> x, Variable z, MILPHelper milp)
	{
		andEquation(x, new Term(1, z), milp);
	}


	private static void andEquation(Vector<Variable> x, Term t, MILPHelper milp)
	{
		int N = x.size();
		int M = Util.log2(N);

		// z \leq x_i
		for (Variable xi : x)
			milp.addNewConstraint(new Expression(t, new Term(-1,xi)), Inequation.LE);

		// y \in {0,1}
		Variable[] y = new Variable[M];
		for (int j=0; j<M; j++)
			y[j] = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// x_{i} \leq z + \sum_{j=1}^{m} e_{ij}
		int i = 0;
		for (Variable xi : x)
		{
			int dividendo = i;
			Expression exp = new Expression(t, new Term(-1, xi));
			for (int j=0; j<M; j++)
			{
				if ((dividendo % 2) == 0)
					exp.addTerm(new Term(1, y[j]));
				else
				{
					exp.addTerm(new Term(-1, y[j]));
					exp.incrementConstant();
				}
				dividendo /= 2;
			}
			i++;
			milp.addNewConstraint(exp, Inequation.GE);
		}


		// \sum_{j=1}^{m} 2^{j-1} y_{j} \leq n-1
		Expression exp2 = new Expression(1-N);
		double k = 1;
		for (int j=0; j<M; j++)
		{
			exp2.addTerm(new Term(k, y[j]));
			k *= 2;
		}
		milp.addNewConstraint(exp2, Inequation.LE);
	}


	// Compute z = x1 AND x2
	static void andEquation(Variable z, Variable x1, double x2, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);
		
		// x_1 \leq x_2
		milp.addNewConstraint(new Expression(new Term(1,z), new Term(-1,x1)), Inequation.LE);
		
		// x_1 \leq x_3
		milp.addNewConstraint(new Expression(new Term(1,z)), Inequation.LE, x2);

		// x_2 \leq x_1 + y
		milp.addNewConstraint(new Expression(new Term(1,x1), new Term(-1,z), new Term(-1,y)), Inequation.LE);

		// x_3 \leq x_1 + (1-y)
		milp.addNewConstraint(new Expression(-1+x2, new Term(-1,z), new Term(1,y)), Inequation.LE);
	}


	// Compute z = (1 - x1) AND x2
	static void andNegatedEquation(Variable z, Variable x1, double x2, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);
		
		// x_1 \leq (1 - x_2)
		milp.addNewConstraint(new Expression(-1, new Term(1,z), new Term(1,x1)), Inequation.LE);
		
		// x_1 \leq x_3
		milp.addNewConstraint(new Expression(new Term(1,z)), Inequation.LE, x2);

		// (1 - x_2) \leq x_1 + y
		milp.addNewConstraint(new Expression(1, new Term(-1,x1), new Term(-1,z), new Term(-1,y)), Inequation.LE);

		// x_3 \leq x_1 + (1-y)
		milp.addNewConstraint(new Expression(-1+x2, new Term(-1,z), new Term(1,y)), Inequation.LE);
	}


	// Compute z = x1 AND x2
	public static void andEquation(Variable z, Variable x1, Variable x2, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);
		
		// x_1 \leq x_2
		milp.addNewConstraint(new Expression(new Term(1,z), new Term(-1,x1)), Inequation.LE);
		
		// x_1 \leq x_3
		milp.addNewConstraint(new Expression(new Term(1,z), new Term(-1,x2)), Inequation.LE);

		// x_2 \leq x_1 + y
		milp.addNewConstraint(new Expression(new Term(1,x1), new Term(-1,z), new Term(-1,y)), Inequation.LE);

		// x_3 \leq x_1 + (1-y)
		milp.addNewConstraint(new Expression(-1, new Term(1,x2), new Term(-1,z), new Term(1,y)), Inequation.LE);
	}


	// Compute z <= x1 AND x2
	static void andLeqEquation(Variable z, Variable x1, Variable x2, MILPHelper milp)
	{
		milp.addNewConstraint(new Expression(new Term(-1,x1), new Term(1,z)), Inequation.LE);
		milp.addNewConstraint(new Expression(new Term(-1,x2), new Term(1,z)), Inequation.LE);
	}


	// Compute z >= x1 AND x2
	static void andGeqEquation(Variable z, Variable x1, Variable x2, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// If y = 0, z >= x1
		milp.addNewConstraint(new Expression(new Term(1,y), new Term(1,z), new Term(-1,x1)), Inequation.GE);

		// If y = 1, z >= x2
		milp.addNewConstraint(new Expression(1, new Term(-1,y), new Term(1,z), new Term(-1,x2)), Inequation.GE);
	}


	// Compute z >= x1 AND x2
	static void andGeqEquation(Variable z, Variable x1, double x2, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// If y = 0, z >= x1
		milp.addNewConstraint(new Expression(new Term(1,y), new Term(1,z), new Term(-1,x1)), Inequation.GE);

		// If y = 1, z >= x2
		milp.addNewConstraint(new Expression(1, new Term(-1,y), new Term(1,z) ), Inequation.GE, x2);
	}


	// Compute x1 AND x2 <= 0
	// Used to reason with disjoint concepts
	static void andEquation(Variable x1, Variable x2, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);
		milp.addNewConstraint(new Expression(new Term(-1,y), new Term(1,x1)), Inequation.LE);
		milp.addNewConstraint(new Expression(1,new Term(-1,y), new Term(-1,x2)), Inequation.GE);
	}


	/**
	 * Solves a disjunction fuzzy assertion with respect to a reference fuzzy KB.
	 * @param ass A disjunction fuzzy assertion.
	 * @param kb A reference fuzzy KB.
	 */
	public static void solveOr(Assertion ass, KnowledgeBase kb)
	{
		Concept c = ass.getConcept();
		Individual ind = ass.getIndividual();
		Variable xAss = kb.milp.getVariable(ass);
		kb.old01Variables += 2 * c.concepts.size() - 1;
		kb.oldBinaryVariables += 1 * c.concepts.size() - 1;
		
		Vector<Variable> v = new Vector<Variable>();
		for (Concept ci : c.concepts)
		{
			Variable var = kb.milp.getVariable(ind, ci);
			kb.addAssertion(ind, ci, Degree.getDegree(var) );
			v.add(var);
		}
		orEquation(v, xAss, kb.milp);
	}


	/**
	 * Solves a existential restriction fuzzy assertion with respect to a reference fuzzy KB.
	 * @param ass A existential restriction fuzzy assertion.
	 * @param kb A reference fuzzy KB.
	 * @throws InconsistentOntologyException Inconsistent ontology.
	 */
	public static void solveSome(Assertion ass, KnowledgeBase kb) throws InconsistentOntologyException
	{
		Individual a = ass.getIndividual();
		String role = ass.getConcept().getRole();
		Concept c = ass.getConcept().c1;
		kb.rulesApplied[KnowledgeBase.RULE_G_SOME]++;

		// Concept simplification
		int type = c.getType();

		Individual b;
		if(kb.funcRoles.contains(role) && a.roleRelations.containsKey(role))
		{
			ArrayList<Relation> relSet = a.roleRelations.get(role);
			b = relSet.get(0).getObjectIndividual();
		}
		else
		{
			if (kb.isConcreteType(type))
				b = kb.getNewConcreteIndividual(a, role);
			else
				b = kb.getNewIndividual(a, role);
		}

		Variable rVar = kb.milp.getVariable(a, b, role);
		Variable cVar = kb.milp.getVariable(b, c);

		// b:C >= x_{b:C}
		kb.addAssertion(b, c, new DegreeVariable(cVar));

		// (a,b):R >= x_{(a:b):R}
		Relation r = a.addRelation(role, b, new DegreeVariable(rVar), kb);

		// xAss <= x_{b:C} \otimes x_{(a:b):R}
		Variable xAss = kb.milp.getVariable(ass);
		andLeqEquation(xAss, cVar, rVar, kb.milp);

		kb.solveRoleInclusionAxioms(a, r);

		// For every inverse role
		Set<String> listInverseRoles = kb.invRoles.get(ass.getConcept().getRole() );
		if (listInverseRoles != null)
		{
			for(String invRole : listInverseRoles)
			{
				b.addRelation(invRole, ass.getIndividual(), new DegreeVariable(rVar), kb); // (b,a):inv(R) >= l
				kb.solveRoleInclusionAxioms(b, r);
			}
		}
	}


	/**
	 * Solves a universal restriction fuzzy assertion with respect to a reference fuzzy KB.
	 * @param rel A relation.
	 * @param restric A universal restriction.
	 * @param kb A reference fuzzy KB.
	 */
	public static void solveAll(Relation rel, Restriction restric, KnowledgeBase kb)
	{
		if(! rel.getDegree().isNumeric() || ! restric.getDegree().isNumeric())
			kb.old01Variables += 1;

		Individual b = rel.getObjectIndividual();
		Variable xBinC;

		// Has value restriction
		if (restric instanceof HasValueRestriction)
		{
			HasValueRestriction hvr = (HasValueRestriction) restric;
			xBinC = kb.milp.getNegatedNominalVariable(b.toString(), hvr.getIndividual());
			kb.rulesApplied[KnowledgeBase.RULE_NOT_HAS_VALUE]++;
		}
		else
		{
			Concept c = restric.getConcept();
			xBinC = kb.milp.getVariable(b, c);
			kb.addAssertion(b, c, new DegreeVariable(xBinC));
			kb.rulesApplied[KnowledgeBase.RULE_G_ALL]++;
		}

		if(kb.transRoles.contains(restric.getRoleName()) && !kb.checkTransRoleApplied(rel, restric))
		{
			Concept forAll;
			if (restric instanceof HasValueRestriction)
			{
				HasValueRestriction hvr = (HasValueRestriction) restric;
				forAll = Concept.notHasValue(restric.getRoleName(), hvr.getIndividual());
			}
			else 
				forAll = Concept.all(restric.getRoleName(), restric.getConcept());

			Variable xForAllB = kb.milp.getVariable(b, forAll);
			Degree d = Degree.getDegree(xForAllB);
			kb.addAssertion(b, forAll, d);

			// xForAll  \leq  xRel \Rightarrow xForAllB 
			Individual a = rel.getSubjectIndividual();
			Variable xForAll = kb.milp.getVariable(a, restric);		
			Variable xRel = kb.milp.getVariable(rel);
			kdImpliesLeqEquation(xForAll, xRel, xForAllB, kb.milp);			
		}

		if(kb.rolesWithTransChildren.containsKey(restric.getRoleName()) && !kb.checkTransRoleApplied(rel, restric))
		{
			ArrayList<String> transChildren = kb.rolesWithTransChildren.get(restric.getRoleName());
			for(String tc : transChildren)
			{
				Concept all;
				if (restric instanceof HasValueRestriction)
				{
					HasValueRestriction hvr = (HasValueRestriction) restric;
					all = Concept.notHasValue(tc, hvr.getIndividual());
				}
				else
					all = Concept.all(tc, restric.getConcept());

				Variable xForAllB = kb.milp.getVariable(b,all);
				Degree d = Degree.getDegree(xForAllB);
				kb.addAssertion(b, all, d);

				// xForAll  \leq  xRel \Rightarrow xForAllB 
				Individual a = rel.getSubjectIndividual();
				Variable xForAll = kb.milp.getVariable(a, restric);		
				Variable xRel = kb.milp.getVariable(rel);
				kdImpliesLeqEquation(xForAll, xRel, xForAllB, kb.milp);		
			}
		}

		// xForAll  \leq xRel  \Rightarrow  xBinC
		Variable xRel = kb.milp.getVariable(rel);
		Variable xForAll = kb.milp.getVariable(rel.getSubjectIndividual(), restric);
		kdImpliesLeqEquation(xForAll, xRel, xBinC, kb.milp);
	}


	// Compute z <= x1 KD-implies x2
	static void kdImpliesLeqEquation(Variable z, Variable x1, Variable x2, MILPHelper milp)
	{
		/* If y=0: x2  \geq  z
		 * If y=1: 1 - x1 \geq  z
		 */
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);
		milp.addNewConstraint(new Expression(new Term(1,x2), new Term(1,y), new Term(-1,z)), Inequation.GE);
		milp.addNewConstraint(new Expression(2, new Term(-1,y), new Term(-1,z), new Term(-1,x1)), Inequation.GE);
	}


	// Compute z = x1 G-implies x2
	static void gImpliesEquation(Variable z, Variable x1, Variable x2, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// 2y + x1 \geq x2 + \epsilon
		milp.addNewConstraint(new Expression(new Term(2,y), new Term(1,x1), new Term(-1,x2)), Inequation.GE, ConfigReader.EPSILON);

		// y + x2 \geq z
		milp.addNewConstraint(new Expression(new Term(1,y), new Term(1,x2), new Term(-1,z)), Inequation.GE);

		// x2 \leq z + y
		milp.addNewConstraint(new Expression(new Term(1,x2), new Term(-1,z), new Term(-1,y)), Inequation.LE);

		// z \geq y
		milp.addNewConstraint(new Expression(new Term(1,z), new Term(-1,y)), Inequation.GE);

		// x1 \leq x2 + (1 - y)
		milp.addNewConstraint(new Expression(-1, new Term(1,x1), new Term(-1,x2), new Term(1,y)), Inequation.LE);
	}


	// Compute z = x1 Z-implies x2
	static void zImpliesEquation(Variable z, Variable x1, Variable x2, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// 2y + x1 \geq x2 + \epsilon
		milp.addNewConstraint(new Expression(new Term(2,y), new Term(1,x1), new Term(-1,x2)), Inequation.GE, ConfigReader.EPSILON);

		// z = y
		milp.addNewConstraint(new Expression(new Term(1,z), new Term(-1,y)), Inequation.EQ);

		// x1 \leq x2 + (1 - y)
		milp.addNewConstraint(new Expression(-1, new Term(1,x1), new Term(-1,x2), new Term(1,y)), Inequation.LE);
	}


	// Compute z = x1 Z-implies x2
	static void zImpliesEquation(double z, Variable x1, Variable x2, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// 2y + x1 \geq x2 + \epsilon
		milp.addNewConstraint(new Expression(new Term(2,y), new Term(1,x1), new Term(-1,x2)), Inequation.GE, ConfigReader.EPSILON);

		// z = y
		milp.addNewConstraint(new Expression(z, new Term(-1,y)), Inequation.EQ);

		// x1 \leq x2 + (1 - y)
		milp.addNewConstraint(new Expression(-1, new Term(1,x1), new Term(-1,x2), new Term(1,y)), Inequation.LE);
	}


	// Compute y = NOT z
	static void gNotEquation(Variable y, Variable z, MILPHelper milp)
	{
		if (y.getLowerBound() != Variable.BINARY_VARIABLE)
			y.setType(Variable.BINARY_VARIABLE);

		// y \leq 1 - z
		milp.addNewConstraint(new Expression(-1, new Term(1,z), new Term(1,y)), Inequation.LE);
		
		// z + y \geq \epsilon
		milp.addNewConstraint(new Expression(-ConfigReader.EPSILON, new Term(1,z), new Term(1,y)), Inequation.GE);
	}


	// Compute z = x1 OR x2
	static void orEquation(Variable z, Variable x1, double x2, MILPHelper milp)
	{	
		// z  \geq x1
		milp.addNewConstraint(new Expression(new Term(1,z), new Term(-1,x1)), Inequation.GE);
		
		// z  \geq x2
		milp.addNewConstraint(new Expression(new Term(1,z)), Inequation.GE, x2);
		
		// x1 + y \geq z
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);
		milp.addNewConstraint(new Expression(new Term(1,x1), new Term(1,y), new Term(-1,z)), Inequation.GE);

		// x_2 + (1-y) \geq z
		milp.addNewConstraint(new Expression(1+x2, new Term(-1,y), new Term(-1,z)), Inequation.GE);
	}


	// Compute z = (1 - x1) OR x2
	static void orNegatedEquation(Variable z, Variable x1, double x2, MILPHelper milp)
	{	
		// z  \geq (1 - x1) 
		milp.addNewConstraint(new Expression(1, new Term(1,z), new Term(1,x1)), Inequation.GE);
		
		// z  \geq x2
		milp.addNewConstraint(new Expression(new Term(1,z)), Inequation.GE, x2);

		// (1 - x1) + y \geq z
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);
		milp.addNewConstraint(new Expression(1, new Term(-1,x1), new Term(1,y), new Term(-1,z)), Inequation.GE);

		// x_2 + (1-y) \geq z
		milp.addNewConstraint(new Expression(1+x2, new Term(-1,y), new Term(-1,z)), Inequation.GE);
	}

	// Compute z = x1 OR x2 OR ... OR xN
	public static void orEquation(Vector<Variable> x, Variable z, MILPHelper milp)
	{
		int N = x.size();
		int M = Util.log2(N);

		// z \geq x_i
		for (Variable xi : x)
			milp.addNewConstraint(new Expression(new Term(1,z), new Term(-1,xi)), Inequation.GE);

		// y \in {0,1}
		Variable[] y = new Variable[M];
		for (int j=0; j<M; j++)
			y[j] = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// x_{i} + \sum_{j=1}^{m} e_{ij} \geq z
		int i = 0;
		for (Variable xi : x)
		{
			int dividendo = i;
			Expression exp = new Expression(new Term(-1, z), new Term(1, xi));
			for (int j=0; j<M; j++)
			{
				if ((dividendo % 2) == 0)
					exp.addTerm(new Term(1, y[j]));
				else
				{
					exp.addTerm(new Term(-1, y[j]));
					exp.incrementConstant();
				}
				dividendo /= 2;
			}
			i++;
			milp.addNewConstraint(exp, Inequation.GE);
		}

		// \sum_{j=1}^{m} 2^{j-1} y_{j} \leq n-1
		Expression exp2 = new Expression(1-N);
		double k = 1;
		for (int j=0; j<M; j++)
		{
			exp2.addTerm(new Term(k, y[j]));
			k *= 2;
		}
		milp.addNewConstraint(exp2, Inequation.LE);
	}

	
	/**
	 * Gets the value n1 and n2, according to Goedel t-norm
	 * @param n1 A degree of truth in [0,1].
	 * @param n2 A degree of truth in [0,1].
	 * @return A degree of truth in [0,1] computed as min{n1,n2}.
	 */
	static double and(double n1, double n2)
	{
		if (n2 < n1)
			return n2;
		else
			return n1;		
	}


	// Compute z <= x1 Z-implies x2, where x1 is binary
	static void zImpliesLeqEquation(Variable z, Variable x1, Variable x2, MILPHelper milp)
	{
		milp.addNewConstraint(new Expression(1, new Term(-1,x1), new Term(1,x2), new Term(-1,z)), Inequation.GE);
	}

}
