package fuzzydl;

import java.util.*;

import fuzzydl.exception.*;
import fuzzydl.milp.*;

/**
 * Solver for Lukasiewicz fuzzy logic semantics.
 * @author Fernando Bobillo
 */
public class LukasiewiczSolver 
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
		kb.old01Variables += 2 * c.concepts.size() - 1;
		kb.oldBinaryVariables += 1 * c.concepts.size() - 1;
		
		Vector<Variable> v = new Vector<Variable>();
		for (Concept ci : c.concepts)
		{
			Variable var = kb.milp.getVariable(ind, ci);
			kb.addAssertion(ind, ci, Degree.getDegree(var) );
			v.add(var);
		}
		andEquation(v, xAss, kb.milp);
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
		kb.rulesApplied[KnowledgeBase.RULE_L_SOME]++;

		// Concept simplification
		int type = c.getType();
		kb.old01Variables += 2;
		kb.oldBinaryVariables += 1;

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
				Variable var = kb.milp.getVariable(b, ass.getIndividual(), invRole);
				b.addRelation(invRole, ass.getIndividual(), new DegreeVariable(var), kb); // (b,a):inv(R) >= l
				kb.milp.addNewConstraint(new Expression(new Term(1, rVar), new Term(-1, var)), Inequation.EQ);
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

		Individual a = rel.getSubjectIndividual();
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
			kb.rulesApplied[KnowledgeBase.RULE_L_ALL]++;
		}

		Variable xRel = kb.milp.getVariable(rel);
		Variable xForAll = kb.milp.getVariable(a, restric);

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
			kb.addAssertion(b, forAll, Degree.getDegree(xForAllB) );

			// xForAllB >= xForAll \otimes xRel
			andGeqEquation(xForAllB, xForAll, xRel, kb.milp);
		}

		if(kb.rolesWithTransChildren.containsKey(restric.getRoleName()) && !kb.checkTransRoleApplied(rel, restric))
		{
			ArrayList<String> transChildren = kb.rolesWithTransChildren.get(restric.getRoleName());
			for(String tc: transChildren)
			{
				double n = kb.getInclusionDegree(tc, restric.getRoleName());
				Concept all;
				if (restric instanceof HasValueRestriction)
				{
					HasValueRestriction hvr = (HasValueRestriction) restric;
					all = Concept.notHasValue(tc, hvr.getIndividual());
				}
				else
					all = Concept.all(tc, restric.getConcept());
				if (n != 1)
				{
					kb.old01Variables += 1;
					Variable xBallC = kb.milp.getVariable(b, all);
					kb.addAssertion(b, Concept.all(tc, all), Degree.getDegree(xBallC) );
					kb.milp.addNewConstraint(new Expression(2 - n, new Term(1,xBallC), new Term(-1,xRel), new Term(-1,xForAll)), Inequation.GE);
				}
				else
					 kb.addAssertion(b, all, Degree.getDegree(xForAll));
			 }
		}

		// xBinC >= xForAll \otimes xRel
		andGeqEquation(xBinC, xRel, xForAll, kb.milp);
	}


	// Compute z = x1 AND x2 AND ... AND xN
	private static void andEquation(Vector<Variable> x, Variable z, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);
		int N = x.size();

		Expression exp = new Expression(x);
		exp.addTerm(new Term(-1, z));
		exp.setConstant(1-N);

		// \sum_{i=1}^{n} x_i - (n-1) \leq z,
		milp.addNewConstraint(exp, Inequation.LE);
		
		// y \leq 1-z,
		milp.addNewConstraint(new Expression(1, new Term(-1,z), new Term(-1,y)), Inequation.GE);

		// \sum_{i=1}^{n} x_i - (n-1)  \geq z - (n-1) y,
		Expression exp2 = new Expression(exp);
		exp2.addTerm(new Term(N-1, y));
		milp.addNewConstraint(exp2, Inequation.GE);
	}


	// Compute z = x1 AND x2
	static void andEquation(Variable z, Variable x1, double x2, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// x1 + x2 - 1 \leq z,
		milp.addNewConstraint(new Expression(1-x2, new Term(-1,x1), new Term(1,z)), Inequation.GE);

		// x1 + x2 - 1 \geq z - y,
		milp.addNewConstraint(new Expression(1-x2, new Term(-1,x1), new Term(1,z), new Term(-1,y)), Inequation.LE);

		// z \leq 1 - y,
		milp.addNewConstraint(new Expression(-1, new Term(1,z), new Term(1,y)), Inequation.LE);
	}


	// Compute z = x1 AND x2
	public static void andEquation(Variable z, Variable x1, Variable x2, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);

		// x1 + x2 - 1 \leq z,
		milp.addNewConstraint(new Expression(1, new Term(-1,x1), new Term(-1,x2), new Term(1,z)), Inequation.GE);

		// x1 + x2 - 1 \geq z - y,
		milp.addNewConstraint(new Expression(1, new Term(-1,x1), new Term(-1,x2), new Term(1,z), new Term(-1,y)), Inequation.LE);

		// z \leq 1 - y,
		milp.addNewConstraint(new Expression(-1, new Term(1,z), new Term(1,y)), Inequation.LE);
	}


	// Compute z <= x1 AND x2
	private static void andLeqEquation(Variable z, Variable x1, Variable x2, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);
		milp.addNewConstraint(new Expression(1, new Term(-1,z), new Term(-1,y)), Inequation.GE);
		milp.addNewConstraint(new Expression(-1, new Term(1,x1), new Term(1,x2), new Term(-1,z), new Term(1,y)), Inequation.GE);
	}


	// Compute z >= x1 AND x2
	private static void andGeqEquation(Variable z, Variable x1, Variable x2, MILPHelper milp)
	{
		milp.addNewConstraint(new Expression(-1, new Term(-1,z), new Term(1,x1), new Term(1,x2)), Inequation.LE);
	}


	// Compute z >= x1 AND x2
	static void andGeqEquation(Variable z, Variable x1, double x2, MILPHelper milp)
	{
		milp.addNewConstraint(new Expression(-1+x2, new Term(-1,z), new Term(1,x1) ), Inequation.LE);
	}


	// Compute z = x1 OR x2 OR ... OR xN
	static void orEquation(Vector<Variable> x, Variable z, MILPHelper milp)
	{
		Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);
		int N = x.size();

		// \sum_{i=1}^{n} x_i \geq z,
		Expression exp = new Expression(x);
		exp.addTerm(new Term(-1, z));
		milp.addNewConstraint(exp, Inequation.GE);

		// y \leq z,
		milp.addNewConstraint(new Expression(new Term(1,y), new Term(-1,z)), Inequation.LE);

		// \sum_{i=1}^{n} x_i \leq z + (n-1) y,
		Expression exp2 = new Expression(exp);
		exp2.addTerm(new Term(1-N, y));
		milp.addNewConstraint(exp2, Inequation.LE);
	}

	
	/**
	 * Gets the value n1 and n2, according to Lukasiewicz t-norm
	 * @param n1 A degree of truth in [0,1].
	 * @param n2 A degree of truth in [0,1].
	 * @return A degree of truth in [0,1] computed as max{n1+n2-1, 0}.
	 */
	static double and(double n1, double n2)
	{
		double n = n1 + n2 - 1;
		if (n >= 0)
			return n;
		else
			return 0;		
	}

}
