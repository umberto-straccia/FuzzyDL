package fuzzydl;

import java.util.*;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;


/**
 * Datatype restrictions reasoner.
 * @author Fernando Bobillo
 */
public class DatatypeReasoner
{

	/**
	 * Gets the concrete feature with the name fName if previously defined; otherwise it produces an error.
	 * @param fName Name of the feature.
	 * @param kb A reference KnowledgeBase.
	 * @return A concrete feature.
	 */
	private static ConcreteFeature getFeature(String fName, KnowledgeBase kb) throws FuzzyOntologyException
	{
		ConcreteFeature t = (ConcreteFeature) kb.concreteFeatures.get(fName);
		if (t == null)
			Util.error("Error: Concrete feature " + fName + " is not defined");
		return t;
	}


	/**
	 * Gets the bounds of a concrete feature.
	 * @param t A concrete feature.
	 * @return An array double[2] with the bounds {k1,k2} of t.
	 */
	private static double[] getBounds(ConcreteFeature t) throws FuzzyOntologyException
	{
		double[] k = new double[2];
		if (t.getType() == ConcreteFeature.BOOLEAN)
			return null;
		if (t.getType() == ConcreteFeature.INTEGER)
		{
			k[0] = new Double(((Integer) t.getK1()).intValue());
			k[1] = new Double(((Integer) t.getK2()).intValue());
		}
		else // if (t.getType() == ConcreteFeature.REAL); string is not possible at this point
		{
			k[0] = (Double) t.getK1();
			k[1] = (Double) t.getK2();
		}
		return k;
	}


	// Gets the values of b, xF, xB
	private static Object[] getCreatedIndividualAndVariables(Individual ind, String role, ConcreteFeature t, double k[], KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		Object[] returnValue = new Object[3];
		CreatedIndividual b;
		Variable xF;
		String fName = t.getName();
		boolean newVariable = false;
		if(ind.roleRelations.containsKey(role))
		{
			ArrayList<Relation> relSet = ind.roleRelations.get(role);
			b = (CreatedIndividual) relSet.get(0).getObjectIndividual();
			xF = kb.milp.getVariable(ind, b, fName, Variable.BINARY_VARIABLE);
		}
		else
		{
			newVariable = true;
			b = kb.getNewConcreteIndividual(ind, fName);
			xF = kb.milp.getVariable(ind, b, fName, Variable.BINARY_VARIABLE);
			// (a,b):F >= x_{(a,b):F}
			ind.addRelation(role, b, Degree.getDegree(xF), kb);
		}

		Variable xB = getXb(b, t, kb.milp);
		if (newVariable && (k != null) )
			kb.restrictRange(xB, xF, k[0], k[1]);

		returnValue[0] = b;
		returnValue[1] = xB;
		returnValue[2] = xF;
		return returnValue;
	}
	
	
	private static void ruleNotTriangularFuzzyNumber(CreatedIndividual b, KnowledgeBase kb, String fName, Variable xB, Variable xF, Variable xIsC, TriangularFuzzyNumber n, double k[], char type) throws InconsistentOntologyException
	{
		// TriangularFuzzyNumber version
		CreatedIndividual bPrime = b.getRepresentative(RepresentativeIndividual.GREATER_EQUAL, fName, n, kb);
		Variable xBprime = kb.milp.getVariable(bPrime, Variable.FREE_VARIABLE);

		// Add equations for determining F(b') 
		Variable xBprimeIsF = kb.milp.getVariable(bPrime, n);
		n.solveAssertion(bPrime, Degree.getDegree(xBprimeIsF), kb);
		
		Variable xIsF = kb.milp.getVariable(bPrime, (TriangularFuzzyNumber) n);
		
		writeNotFuzzyNumberEquation(xB, xBprime, xBprimeIsF, xF, xIsC, xIsF, k, type, kb.milp);
	}
	

	private static void ruleTriangularFuzzyNumber(CreatedIndividual b, KnowledgeBase kb, String fName, Variable xB, Variable xF, Variable xIsC, TriangularFuzzyNumber n, char type) throws InconsistentOntologyException
	{
		// TriangularFuzzyNumber version
		CreatedIndividual bPrime = b.getRepresentative(RepresentativeIndividual.GREATER_EQUAL, fName, n, kb);
		Variable xBprime = kb.milp.getVariable(bPrime, Variable.FREE_VARIABLE);

		// F(c') >= x_{c':F}
		Variable xBprimeIsF = kb.milp.getVariable(bPrime, n);
		n.solveAssertion(bPrime, Degree.getDegree(xBprimeIsF), kb);

		// x:{v:C} \leq x_{b' : F}
		kb.milp.addNewConstraint(new Expression(new Term(1,xIsC), new Term(-1,xBprimeIsF)), Inequation.LE);

		// x:{v:C} \leq 1 - y // Umberto : not needed
		//Variable y = kb.milp.getNewVariable(Variable.BINARY_VARIABLE); 
		//kb.milp.addNewConstraint(new Expression(- 1, new Term(1,xIsC), new Term(1,y)), Inequation.LE);

		writeFuzzyNumberEquation(xF, xB, xBprime, type, kb.milp);
	}


	private static void ruleFeatureFunction(Individual ind, ConcreteFeature t, FeatureFunction fun, KnowledgeBase kb, Variable xB, Variable xIsC, Variable xF, double k[], char type) throws FuzzyOntologyException, InconsistentOntologyException
	{
		// Gets fillers bi from every feature fi
		HashSet<String> array = fun.getFeatures();
		
		Variable xFi;
		boolean newVariable = false;
		for (String feature : array)
		{
			ConcreteFeature ti = getFeature(feature, kb);
			double ki[] = getBounds(ti);
			
			CreatedIndividual bi;
			if(ind.roleRelations.containsKey(feature))
			{
				ArrayList<Relation> relSet = ind.roleRelations.get(feature);
				bi = (CreatedIndividual) relSet.get(0).getObjectIndividual();
				xFi = kb.milp.getVariable(ind, bi, feature, Variable.BINARY_VARIABLE);
			}
			else
			{
				newVariable = true;
				bi = kb.getNewConcreteIndividual(ind, feature);
				xFi = kb.milp.getVariable(ind, bi, feature, Variable.BINARY_VARIABLE);
				// (a,bi):Fi >= x_{(a,bi):Fi}
				ind.addRelation(feature, bi, Degree.getDegree(xFi), kb);
			}
	
			Variable xBi;
			if (t.getType() == ConcreteFeature.INTEGER)
				xBi = kb.milp.getVariable(bi, Variable.INTEGER_VARIABLE);
			else
				xBi = kb.milp.getVariable(bi, Variable.FREE_VARIABLE);

			if (newVariable && (ki != null) )
				kb.restrictRange(xBi, xFi, ki[0], ki[1]);

			// xIsC <= xFi
			kb.milp.addNewConstraint(new Expression(new Term(1,xIsC), new Term(-1,xFi)), Inequation.LE);

			// xF \in {0,1}
			xFi.setBinaryVariable();
			
			// xB is a datatype filler
			xBi.setDatatypeFillerVariable();
		}

		writeFeatureEquation(ind, fun, xB, xIsC, xF, k, type, kb.milp);
	}


	/* 
	 * <ul>
	 *  <li>type GE: xB >= xBprime - (k2 - k1) y.</li>
	 *  <li>type LE: xB <= xBprime + (k2 - k1) y.</li>
	 *  <li>type EQ: xB >= xBprime - (k2 - k1) y, xB <= xBprime + (k2 - k1) y.</li>
	 * </ul>
	 */
	//private static void writeFuzzyNumberEquation(Variable y, Variable xB, Variable xBprime, char type, MILPHelper milp)
	private static void writeFuzzyNumberEquation(Variable xF, Variable xB, Variable xBprime, char type, MILPHelper milp)
	{
		switch (type)
		{
			case Inequation.EQ:

				writeFuzzyNumberEquation(xF, xB, xBprime, Inequation.GE, milp);
				writeFuzzyNumberEquation(xF, xB, xBprime, Inequation.LE, milp);
				break;
			
			case Inequation.GE:

				milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL2, new Term(1,xB), new Term(-1,xBprime), new Term(-KnowledgeBase.MAXVAL2,xF)), Inequation.GE);
				milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(1, xBprime)), Inequation.GE); // xBprime \geq -maxVal
				milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(-1, xBprime)), Inequation.GE); // xBprime \leq maxVal
				break;
				
			case Inequation.LE:

				milp.addNewConstraint(new Expression(-KnowledgeBase.MAXVAL2, new Term(1,xB), new Term(-1,xBprime), new Term(KnowledgeBase.MAXVAL2,xF)), Inequation.LE);
				milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(1, xBprime)), Inequation.GE); // xBprime \geq -maxVal
				milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(-1, xBprime)), Inequation.GE); // xBprime \leq maxVal
				break;
		}
	}


	/* 
	 * <ul>
	 *  <li>type GE: xB >= f(t1,...,tn) - (k2 - k1) (1 - xC).</li>
	 *  <li>type LE: xB <= f(t1,...,tn) + (k2 - k1) (1 - xC).</li>
	 *  <li>type EQ: xB >= f(t1,...,tn) - (k2 - k1) (1 - xC), xB <= f(t1,...,tn) + (k2 - k1) (1 - xC).</li>
	 * </ul>
	 */
	private static void writeFeatureEquation(Individual ind, FeatureFunction fun, Variable xB, Variable xIsC, Variable xF, double k[], char type, MILPHelper milp)
	{
		DegreeExpression deg = new DegreeExpression(fun.toExpression(ind, milp));
		switch (type)
		{
			case Inequation.EQ:

				writeFeatureEquation(ind, fun, xB, xIsC,  xF, k, Inequation.GE, milp);
				writeFeatureEquation(ind, fun, xB, xIsC, xF, k, Inequation.LE, milp);
				break;

			case Inequation.GE:

				milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL2, new Term(1,xB), new Term(-KnowledgeBase.MAXVAL2,xF)), Inequation.GE, deg);				
				milp.addNewConstraint(new Expression(-KnowledgeBase.MAXVAL), Inequation.LE, deg); // x \geq -maxVal
				milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL), Inequation.GE, deg); // x \leq maxVal
				break;
				
			case Inequation.LE:

				milp.addNewConstraint(new Expression(-KnowledgeBase.MAXVAL2, new Term(1,xB), new Term(KnowledgeBase.MAXVAL2,xF)), Inequation.LE, deg);				
				milp.addNewConstraint(new Expression(-KnowledgeBase.MAXVAL), Inequation.LE, deg); // x \geq -maxVal
				milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL), Inequation.GE, deg); // x \leq maxVal
				break;
		}
	}


	/* 
	 * <ul>
	 *  <li>type GE: xB >= n - (k2 - k1) (1 - xC).</li>
	 *  <li>type LE: xB <= n + (k2 - k1) (1 - xC).</li>
	 *  <li>type EQ: xB >= n - (k2 - k1) (1 - xC), xB <= n + (k2 - k1) (1 - xC).</li>
	 * </ul>
	 * UMBERTO
	 *  <ul>
	 *  <li>type GE: xB >= k1 (1 - xC) + n xC </li>
	 *  <li>type LE: xB <= k2 (1 - xC) + n xC </li>
	 *  <li>type EQ: xB >= xB <= k2 (1 - xC) + n xC, type LE: xB <= k2 (1 - xC) + n xC</li>
	 * </ul> 
	 *		     
	 *   1. fix case type EQ
	 *   2. fix case semantics is crisp
	 */
	private static void ruleSimpleRestriction(Object n, KnowledgeBase kb, Variable xB, Variable xIsC, Variable xF, double k[], char type)
	{
		switch (type)
		{
			case Inequation.EQ:

				ruleSimpleRestriction(n, kb, xB, xIsC, xF,k, Inequation.GE);
				ruleSimpleRestriction(n, kb, xB, xIsC, xF, k, Inequation.LE);
				break;
			
			case Inequation.GE:	

				if (n instanceof Double)
				{
					kb.milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(1,xB), new Term(-KnowledgeBase.MAXVAL,xF), new Term(- (Double) n,xF)), Inequation.GE);
				}
				else if (n instanceof Variable)
				{
					kb.milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL2, new Term(-1, (Variable) n), new Term(1,xB), new Term(-KnowledgeBase.MAXVAL2,xF)), Inequation.GE);
					kb.milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(1, (Variable) n)), Inequation.GE); // x \geq -maxVal
					kb.milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(-1, (Variable) n)), Inequation.GE); // x \leq maxVal
				}
				break;
				
			case Inequation.LE:

				if (n instanceof Double)
				{
					kb.milp.addNewConstraint(new Expression(-KnowledgeBase.MAXVAL, new Term(1,xB), new Term(KnowledgeBase.MAXVAL,xF), new Term(- (Double) n,xF)), Inequation.LE);
				}
				else if (n instanceof Variable)
				{
					kb.milp.addNewConstraint(new Expression(-KnowledgeBase.MAXVAL2, new Term(-1, (Variable) n), new Term(1,xB), new Term(KnowledgeBase.MAXVAL2,xF)), Inequation.LE);
					kb.milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(1, (Variable) n)), Inequation.GE); // x \geq -maxVal
					kb.milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(-1, (Variable) n)), Inequation.GE); // x \leq maxVal
				}
				break;
		}
	}	


	private static void applyRule(Assertion ass, KnowledgeBase kb, char type) throws FuzzyOntologyException, InconsistentOntologyException
	{
		Individual a = ass.getIndividual();
		Concept c = ass.getConcept();
		String fName = c.getRole();
		ConcreteFeature t = getFeature(fName, kb);

		double k[] = getBounds(t);
		Object[] returnValue = getCreatedIndividualAndVariables(a, ass.getConcept().getRole(), t, k, kb);
		CreatedIndividual b = (CreatedIndividual) returnValue[0];
		Variable xB = (Variable) returnValue[1];
		Variable xF = (Variable) returnValue[2];
		Variable xIsC = kb.milp.getVariable(ass);

		kb.oldBinaryVariables += 1;

		// xIsC <= xF
		kb.milp.addNewConstraint(new Expression(new Term(1,xIsC), new Term(-1,xF)), Inequation.LE);

		// xF \in {0,1}
		xF.setBinaryVariable();
		
		// xB is a datatype filler
		xB.setDatatypeFillerVariable();

		Object n = c.getValue();		
		if (n instanceof TriangularFuzzyNumber)
			ruleTriangularFuzzyNumber(b, kb, fName, xB, xF, xIsC, (TriangularFuzzyNumber) n, type);
		else
		{
			// xIsC \in {0,1}
			xIsC.setBinaryVariable();

			// xB \bowtie n 
			if (n instanceof FeatureFunction)
				ruleFeatureFunction(a, t, (FeatureFunction) n, kb, xB, xIsC, xF, k, type);	
			else if (t.getType() == ConcreteFeature.BOOLEAN)
			{
				xB.setBinaryVariable();
				int value;
				if (n == Boolean.TRUE)
					value = 1;
				else
					value = 0;
				kb.milp.addNewConstraint(new Expression(1+value, new Term(-1,xB), new Term(-1,xF)), Inequation.GE);
				kb.milp.addNewConstraint(new Expression(1-value, new Term(1,xB), new Term(-1,xF)), Inequation.GE);
			}
			else
				ruleSimpleRestriction(n, kb, xB, xIsC, xF, k, type);
		}
	}


	/**
	 * Reasons with a fuzzy at-least value assertion, with respect to a fuzzy KB.
	 * @param ass Fuzzy at-least value assertion.
	 * @param kb Fuzzy kb.
	 */
	static void applyAtLeastValueRule(Assertion ass, KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		applyRule(ass, kb, Inequation.GE);
	}


	/**
	 * Reasons with a fuzzy at-most value assertion, with respect to a fuzzy KB.
	 * @param ass Fuzzy at-most value assertion.
	 * @param kb Fuzzy kb.
	 */
	static void applyAtMostValueRule(Assertion ass, KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		applyRule(ass, kb, Inequation.LE);
	}


	/**
	 * Reasons with a fuzzy exact value assertion, with respect to a fuzzy KB.
	 * @param ass Fuzzy exact value assertion.
	 * @param kb Fuzzy kb.
	 */
	static void applyExactValueRule(Assertion ass, KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		applyRule(ass, kb, Inequation.EQ);
	}


	private static Variable getXb(CreatedIndividual b, ConcreteFeature t, MILPHelper milp)
	{
		if (t.getType() == ConcreteFeature.INTEGER)
			return milp.getVariable(b, Variable.INTEGER_VARIABLE);
		else
			return milp.getVariable(b, Variable.FREE_VARIABLE);
	}


	/* 
	 * <ul>
	 *  <li>type GE: xB <= (n - epsilon) + K (1 - xF) + K xC.</li>
	 *  <li>type LE: xB >= (n + epsilon) - K (1 - xF) - K xC.</li>
	 *  <li>type EQ: xB <= (n - epsilon) + K (1 - xF) + K xC + K y, xB >= (n + epsilon) - K (1 - xF) - K xC - (1 - y) K.</li>
	 *  
	 *  case n double
	 *  <li>type GE: xB <= (n - epsilon) (1- xC) +k2 xC  .</li>
	 *  <li>type LE: xB >= (n + epsilon) (1- xC) + k1 xC .</li>
	 *  <li>type EQ: xB <= (n - epsilon)(1-y) + k2 y, xB >= (n + epsilon) y - k1 y .</li>
	 *  
	 *  
	 * </ul>
	 */
	private static void ruleNotSimpleRestriction(Object n,  KnowledgeBase kb, Variable xB, Variable xF, Variable xIsC, double k[], char type, MILPHelper milp)
	{
		switch (type)
		{
			case Inequation.GE:

				if (n instanceof Double)
				{
					// Fernando NEW: xB  <=  (n - \epsilon)  +  (2 k_\infty + \epsilon) (1 - xF)  +  (2k_\infty + \epsilon) xIsC
					milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL2 + (Double) n, new Term(-1,xB), new Term(- KnowledgeBase.MAXVAL2 - ConfigReader.EPSILON,xF), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON,xIsC)), Inequation.GE);		
				}
				else if (n instanceof Variable)
				{
					// Fernando NEW: xB  <=  x  -  \epsilon xF  +  2k_\infty  (1 - xF)  +  (2k_\infty + \epsilon) xIsC
					milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL2, new Term(-1,xB), new Term(1,(Variable) n), new Term(-KnowledgeBase.MAXVAL2 - ConfigReader.EPSILON,xF), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON,xIsC)), Inequation.GE);
					kb.milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(1, (Variable) n)), Inequation.GE); // x \geq -maxVal
					kb.milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(-1, (Variable) n)), Inequation.GE); // x \leq maxVal
				}
				break;

			case Inequation.LE:

				if (n instanceof Double)
				{
					// Fernando NEW: xB  >=  (n + \epsilon)  -  (2 k_\infty + \epsilon) (1 - xF)  -  (2k_\infty + \epsilon) xIsC
					milp.addNewConstraint(new Expression(- KnowledgeBase.MAXVAL2 + (Double) n, new Term(-1,xB), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON,xF), new Term(- KnowledgeBase.MAXVAL2 - ConfigReader.EPSILON,xIsC)), Inequation.LE);		
				} 
				else if (n instanceof Variable)
				{
					// Fernando NEW: xB  >=  x  +  \epsilon xF  -  2k_\infty (1 - xF)  -  (2k_\infty + \epsilon) xIsC
					milp.addNewConstraint(new Expression(- KnowledgeBase.MAXVAL2, new Term(-1,xB), new Term(1,(Variable) n), new Term(-KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON,xF), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON,xIsC)), Inequation.LE);
					kb.milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(1, (Variable) n)), Inequation.GE); // x \geq -maxVal
					kb.milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(-1, (Variable) n)), Inequation.GE); // x \leq maxVal
				}
				break;

			case Inequation.EQ:
	
				if (n instanceof Double)
				{
					Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);
					// Fernando NEW: xB  <=  (n - \epsilon) y  +  k_\infty (1-y)  +  (2 k_\infty + \epsilon) (1 - xF) +   (2k_\infty + \epsilon) xIsC
					milp.addNewConstraint(new Expression(3 * KnowledgeBase.MAXVAL + ConfigReader.EPSILON, new Term((Double) n - ConfigReader.EPSILON - KnowledgeBase.MAXVAL, y), new Term(-1,xB), new Term(-KnowledgeBase.MAXVAL2 - ConfigReader.EPSILON,xF), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON,xIsC)), Inequation.GE);
					// Fernando NEW: xB  >=  (n + \epsilon) (1 - y)  -  k_\infty y  -  (2 k_\infty + \epsilon) (1 - xF) - (2k_\infty + \epsilon) xIsC
					milp.addNewConstraint(new Expression(- KnowledgeBase.MAXVAL2 + (Double) n, new Term(-1,xB), new Term(KnowledgeBase.MAXVAL2 - (Double) n, y), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON,xF), new Term(- KnowledgeBase.MAXVAL2 - ConfigReader.EPSILON,xIsC)), Inequation.LE);				
				} 
				else if (n instanceof Variable)
				{
					Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);
					// Fernando NEW: xB  <=  x  -  \epsilon xF  +  (2k_\infty + \epsilon) (1 - y)  +  2k_\infty (1 - xF)  + (2k_\infty + \epsilon) xIsC
					milp.addNewConstraint(new Expression(4 * KnowledgeBase.MAXVAL + ConfigReader.EPSILON, new Term(KnowledgeBase.MAXVAL + (Double) n + ConfigReader.EPSILON, y), new Term(-1,xB), new Term(1,(Variable) n), new Term(-KnowledgeBase.MAXVAL2 - ConfigReader.EPSILON,xF), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON,xIsC)), Inequation.GE);
					// Fernando NEW: xB  >=  x  +  \epsilon xF  -  (2k_\infty + \epsilon) y  -  2k_\infty (1 - xF)  -  (2k_\infty + \epsilon) xIsC
					milp.addNewConstraint(new Expression(- KnowledgeBase.MAXVAL2, new Term(-1,xB), new Term(1,(Variable) n), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON, y), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON,xF), new Term(- KnowledgeBase.MAXVAL2 - ConfigReader.EPSILON,xIsC)), Inequation.LE);				
					kb.milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(1, (Variable) n)), Inequation.GE); // x \geq -maxVal
					kb.milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(-1, (Variable) n)), Inequation.GE); // x \leq maxVal
				}
				break;
		}
	}


	/* 
	 * <ul>
	 *  <li>type GE: xB <= (f(t1,...,tn) - epsilon) + K (1 - xF) + K xC.</li>
	 *  <li>type LE: xB >= (f(t1,...,tn) + epsilon) - K (1 - xF) - K xC.</li>
	 *  <li>type EQ: xB <= (f(t1,...,tn) - epsilon) + K (1 - xF) + K xC + K y, xB >= (f(t1,...,tn) + epsilon) - K (1 - xF) - K xC - (1 - y) K.</li>
	 * </ul>
	 */
	private static void writeNotFeatureEquation(DegreeExpression deg, Variable xB, Variable xF, Variable xIsC, double k[], char type, MILPHelper milp)
	{
		milp.addNewConstraint(new Expression(-KnowledgeBase.MAXVAL), Inequation.LE, deg); // x \geq -maxVal
		milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL), Inequation.GE, deg); // x \leq maxVal
		switch (type)
		{
			case Inequation.GE:

				// Fernando NEW: xB  <=  f(b_1,\dots,b_n)  -  \epsilon xF  +  2k_\infty (1 - xF)  +  (2k_\infty + \epsilon) xIsC
				milp.addNewConstraint(new Expression(-KnowledgeBase.MAXVAL2, new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON, xF), new Term(- KnowledgeBase.MAXVAL2 - ConfigReader.EPSILON, xIsC), new Term(1, xB)), Inequation.LE, deg);			
				break;

			case Inequation.LE:

				// Fernando NEW: xB  >=  f(b_1,\dots,b_n)  +  \epsilon xF  -  2k_\infty (1 - xF)  -  (2k_\infty + \epsilon) xIsC			
				milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL2, new Term(-KnowledgeBase.MAXVAL2 - ConfigReader.EPSILON, xF), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON, xIsC), new Term(1, xB)), Inequation.GE, deg);			
				break;

			case Inequation.EQ:

				Variable y = milp.getNewVariable(Variable.BINARY_VARIABLE);		
				// Fernando NEW: xB  <=  f(b_1,\dots,b_n)  -  \epsilon xF  + (2k_\infty + \epsilon) (1 - y)  +  2k_\infty (1 - xF)  +  (2k_\infty + \epsilon) xIsC
				milp.addNewConstraint(new Expression(-4 * KnowledgeBase.MAXVAL - ConfigReader.EPSILON, new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON, xF), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON, y), new Term(- KnowledgeBase.MAXVAL2 - ConfigReader.EPSILON, xIsC), new Term(1, xB)), Inequation.LE, deg);			
				// Fernando NEW: xB  >=  f(b_1,\dots,b_n)  +  \epsilon xF  -  (2k_\infty + \epsilon) y  -  2k_\infty (1 - xF)  -  (2k_\infty + \epsilon) xIsC
				milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL2, new Term(-KnowledgeBase.MAXVAL2 - ConfigReader.EPSILON, xF), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON, y), new Term(KnowledgeBase.MAXVAL2 + ConfigReader.EPSILON, xIsC), new Term(1, xB)), Inequation.GE, deg);			
				break;
		}
	}


	/* 
	 * <ul>
	 *  <li>type GE: xB <= (xBprime - epsilon) + K (1 - xF) + K z1 + K z2.</li>
	 *  <li>type LE: xB >= (xBprime + epsilon) - K (1 - xF) - K z1 - K z2.</li>
	 *  <li>type EQ: xB <= (xBprime - epsilon) + K (1 - xF) + K z1 + K z2 + K y, xB >= (xBprime + epsilon) - K (1 - xF) - K z1 - K z2 - (1 - y) K.</li>
	 * </ul>
	 */
	private static void writeNotFuzzyNumberEquation(Variable xB, Variable xBprime, Variable xBprimeIsF, Variable xF, Variable xIsC, Variable xIsF, double k[], char type, MILPHelper milp)
	{
		Variable y1 = milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable y2 = milp.getNewVariable(Variable.BINARY_VARIABLE);

		switch (type)
		{
			case Inequation.GE:

				// compute y1 = (xB < xBprime)
				geqEquation(y1, xBprime, xB, milp);

				// compute y2 = (xIsC < xBprimeIsF)
				geqEquation(y2, xBprimeIsF, xIsC, milp);

				// xF + y1 + y2 \leq 2, same as min(xF, y1, y2) \leq 0
				milp.addNewConstraint(new Expression(-2,  new Term(1, xF), new Term(1, y1), new Term(1, y2)), Inequation.LE);
				
				break;

			case Inequation.LE:

				// compute y1 = (xB > xBprime)
				geqEquation(y1, xB, xBprime, milp);

				// compute y2 = (xIsC < xBprimeIsF)
				geqEquation(y2, xBprimeIsF, xIsC, milp);

				// xF + y1 + y2 \leq 2, same as min(xF, y1, y2) \leq 0
				milp.addNewConstraint(new Expression(-2,  new Term(1, xF), new Term(1, y1), new Term(1, y2)), Inequation.LE);
				
				break;

			case Inequation.EQ:

				Variable y3 = milp.getNewVariable(Variable.BINARY_VARIABLE);
				Variable y4 = milp.getNewVariable(Variable.BINARY_VARIABLE);
				
				// compute y1 = (xB < xBprime)
				geqEquation(y1, xBprime, xB, milp);

				// compute y2 = (xIsC < xBprimeIsF)
				geqEquation(y2, xBprimeIsF, xIsC, milp);
				
				// compute y3 = (xB > xBprime)
				geqEquation(y3, xB, xBprime, milp);
				
				// xF + y1 + y2 \leq 2 OR xF + y3 + y2 \leq 2, same as min(xF, y1, y2) \leq 0 OR min(xF, y3, y2)
				// That is:
				// xF + y1 + y2 \leq 2 + y4
				// xF + y3 + y2 \leq 3 - y4
				milp.addNewConstraint(new Expression(-2,  new Term(1, xF), new Term(1, y1), new Term(1, y2), new Term(-1, y4)), Inequation.LE);
				milp.addNewConstraint(new Expression(-3,  new Term(1, xF), new Term(1, y3), new Term(1, y2), new Term(1, y4)), Inequation.LE);
				break;
		}
	}


	// Compute y = (x1 > x2)
	private static void geqEquation(Variable y, Variable x1, Variable x2, MILPHelper milp)
	{
		Variable z = milp.getNewVariable(Variable.FREE_VARIABLE);
		milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(1, z)), Inequation.GE); // z \geq -maxVal
		milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL, new Term(-1, z)), Inequation.GE); // z \leq maxVal
		
		// z = x1-x2
		milp.addNewConstraint(new Expression(new Term(1, z), new Term(-1, x1), new Term(1, x2)), Inequation.GE); 
		milp.addNewConstraint(new Expression(new Term(1, z), new Term(-1, x1), new Term(1, x2)), Inequation.LE); 
		
		// z >= \epsilon y - k_\infty (1-y)
		milp.addNewConstraint(new Expression(KnowledgeBase.MAXVAL,  new Term(1, z),  new Term(-KnowledgeBase.MAXVAL - ConfigReader.EPSILON, y)), Inequation.GE);
		
		// z <= k_\infty y,
		milp.addNewConstraint(new Expression(new Term(1, z), new Term(-KnowledgeBase.MAXVAL, y)), Inequation.LE); 
	}


	private static void applyNotRule(CreatedIndividual b, Assertion ass, KnowledgeBase kb, char type) throws FuzzyOntologyException, InconsistentOntologyException
	{
		Individual a = ass.getIndividual();
		Concept notC = ass.getConcept();
		String fName = notC.getRole();
		
		ConcreteFeature t = getFeature(fName, kb);
		double k[] = getBounds(t);  		
		Object[] returnValue = getCreatedIndividualAndVariables(a, ass.getConcept().getRole(), t, k, kb);		
		Variable xF = (Variable) returnValue[2];
		Variable xIsNotC = kb.milp.getVariable(ass);

		kb.oldBinaryVariables += 1;
	
		// xF \in {0,1}
		xF.setBinaryVariable();

		Variable xB = getXb(b, t, kb.milp);
		
		// xIsC = 1 - xIsNotC 
		Concept c = Concept.complement(notC);
		Variable xIsC = kb.milp.getVariable(a, c);
		
		// xB is a datatype filler
		xB.setDatatypeFillerVariable();
		
		kb.milp.addNewConstraint(new Expression(1, new Term(-1,xIsC), new Term(-1,xIsNotC)), Inequation.EQ);

		Object n = c.getValue();
		if (n instanceof TriangularFuzzyNumber)
		{		
			if (type == Inequation.EQ)
				kb.oldBinaryVariables += 3;
			else
				kb.oldBinaryVariables += 4;
			ruleNotTriangularFuzzyNumber(b, kb, fName, xB, xF, xIsC, (TriangularFuzzyNumber) n, k, type);
		}
		else
		{
			if (type == Inequation.EQ)
				kb.oldBinaryVariables += 3;
			else
				kb.oldBinaryVariables += 2;

			// xIsNotC \in {0,1}
			xIsNotC.setBinaryVariable();
			
			// xIsC \in {0,1}
			xIsC.setBinaryVariable();

			if (n instanceof FeatureFunction)
			{					
				// If n is a FeatureFunction, check that there exist fillers
				FeatureFunction fun = (FeatureFunction) n;
				HashSet<String> array = fun.getFeatures();
				for (String feature : array)
					if (a.roleRelations.get(feature) == null)
					{
						Util.println("No fillers for feature " + feature);
						return ;
					}

				DegreeExpression deg = new DegreeExpression(fun.toExpression(a, kb.milp));
				writeNotFeatureEquation(deg, xB, xF, xIsC, k, type, kb.milp);
			}
			else if (t.getType() == ConcreteFeature.BOOLEAN)
			{
				int value;
				if (n == Boolean.TRUE)
					value = 0;
				else
					value = 1;
				kb.milp.addNewConstraint(new Expression(1 + value, new Term(-1,xB), new Term(-1,xF), new Term(1,xIsC)), Inequation.GE);
				kb.milp.addNewConstraint(new Expression(1 - value, new Term(1,xB), new Term(-1,xF), new Term(1,xIsC)), Inequation.GE);
			}
			else
				ruleNotSimpleRestriction(n, kb, xB, xF, xIsC, k, type, kb.milp);
		}
	}	


	/**
	 * Reasons with a fuzzy not at-least value assertion, with respect to a fuzzy KB.
	 * @param b Existing role filler for the assertion.
	 * @param ass Fuzzy not at-least value assertion.
	 * @param kb Fuzzy kb.
	 */
	static void applyNotAtLeastValueRule(CreatedIndividual b, Assertion ass, KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		applyNotRule(b, ass, kb, Inequation.GE);
	}


	/**
	 * Reasons with a fuzzy not at-most value assertion, with respect to a fuzzy KB.
	 * @param b Existing role filler for the assertion.
	 * @param ass Fuzzy not at-most value assertion.
	 * @param kb Fuzzy kb.
	 */
	static void applyNotAtMostValueRule(CreatedIndividual b, Assertion ass, KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		applyNotRule(b, ass, kb, Inequation.LE);
	}


	/**
	 * Reasons with a fuzzy not exact value assertion, with respect to a fuzzy KB.
	 * @param b Existing role filler for the assertion.
	 * @param ass Fuzzy not exact value assertion.
	 * @param kb Fuzzy kb.
	 */
	static void applyNotExactValueRule(CreatedIndividual b, Assertion ass, KnowledgeBase kb) throws FuzzyOntologyException, InconsistentOntologyException
	{
		applyNotRule(b, ass, kb, Inequation.EQ);
	}

}
