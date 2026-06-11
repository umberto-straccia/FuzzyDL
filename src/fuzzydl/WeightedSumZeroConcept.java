package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;

import java.util.*;

/**
 * Weighted sum zero concept.
 * @author Fernando Bobillo
 */
public class WeightedSumZeroConcept extends Concept
{
	private static final long serialVersionUID = -3348153253058217014L;
	
	protected ArrayList<Double> weights;
	protected ArrayList<Concept> concepts;


	public WeightedSumZeroConcept(ArrayList<Double> weights, ArrayList<Concept> concepts) throws FuzzyOntologyException
	{
		super(Concept.W_SUM_ZERO);

		if (weights.size() != concepts.size())
			Util.error("Error: The number of weights and the number of concepts should be the same");

		double sum = 0;
		for (double d : weights)
			sum += d;
		if(sum > 1)
			Util.error("Error: The sum of the weights of the weighted sum concept cannot be greater than 1.0.");

		this.concepts = concepts;
		this.weights = weights;
		setName(toString());
	}


	@Override
	public String toString()
	{
		String s =  "(w-sum-zero ";
		int n = concepts.size();
		for(int i=0; i<n; i++)
		{
			s += "(" + concepts.get(i);
			s += " " + weights.get(i) + ") ";
		}
		s += ")";
		if(type == Concept.NOT_W_SUM_ZERO)
			return "(not " + s + ")";
		else // if(type == Concept.W_SUM_ZERO)
			return s;
	}


	/**
	 * Gets the complement of the concept.
	 * @return Complement of the concept.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public Concept complement() throws FuzzyOntologyException
	{
		Concept aux = new WeightedSumZeroConcept(weights, concepts);
		if(type == Concept.W_SUM_ZERO)
			aux.setType(NOT_W_SUM_ZERO);
		return aux;
	}


	/**
	 * Solves an assertion of the form (individual, concept) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param kb A fuzzy KB.
	 */
	public void solveAssertion(Individual ind, KnowledgeBase kb)
	{
		Variable xAinWS = kb.milp.getVariable(ind, this);
		int n = concepts.size();
		Term[] terms = new Term[n+1];
		Vector<Variable> vx = new Vector<Variable> ();
		Variable y = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable z = kb.milp.getNewVariable(Variable.UP_VARIABLE);
		
		for(int i=0; i<n; i++)
		{
			Concept ci = concepts.get(i);
			Variable xi = kb.milp.getVariable(ind, ci);
			kb.addAssertion(ind, ci, Degree.getDegree(xi));
			kb.milp.addNewConstraint(new Expression(new Term(1, z), new Term(-1, xi) ), Inequation.LE);

			vx.add(xi);
			terms[i] = new Term(weights.get(i), xi);
		}
		terms[n] = new Term(-1, xAinWS);

		// z = min { x_{v:C_i} }   for all i
		ZadehSolver.andEquation(vx, z, kb.milp);

		// y = not_G  z
		ZadehSolver.gNotEquation(y, z, kb.milp);

		// xAinWS \leq  1-y
		kb.milp.addNewConstraint(new Expression(-1, new Term(1, y), new Term(1, xAinWS) ), Inequation.LE);

		// xAinWS \geq  w_1 x_{v:C_1} + \dots + w_n x_{v:C_n} - y
		Expression exp1 =(new Expression(terms));
		exp1.addTerm(new Term(-1, y));
		kb.milp.addNewConstraint(exp1, Inequation.LE);
	
		// xAinWS \leq  w_1 x_{v:C_1} + \dots + w_n x_{v:C_n} + y
		Expression exp2 =(new Expression(terms));
		exp2.addTerm(new Term(1, y));
		kb.milp.addNewConstraint(exp2, Inequation.GE);

		kb.ruleComplemented(ind, this);
	}


	/**
	 * Solves an assertion of the form (individual, not concept) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param kb A fuzzy KB.
	 */
	public void solveComplementedAssertion(Individual ind, KnowledgeBase kb)
	{
		Variable xAinNotWS = kb.milp.getVariable(ind, this);
		int n = concepts.size();
		Term[] terms = new Term[n+1];
		Vector<Variable> vx = new Vector<Variable> ();
		Variable y = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
		Variable z = kb.milp.getNewVariable(Variable.UP_VARIABLE);

		for(int i=0; i<n; i++)
		{
			Concept ci = concepts.get(i);
			Concept notCi = Concept.complement(ci);
			Variable xi = kb.milp.getVariable(ind, ci);
			Variable xNoti = kb.milp.getVariable(ind, notCi);
			kb.addAssertion(ind, notCi, Degree.getDegree(xNoti));

			vx.add(xi);
			terms[i] = new Term(- weights.get(i), xi);
		}
		terms[n] = new Term(-1, xAinNotWS);
		
		// z = min { x_{v:C_i} }   for all i
		ZadehSolver.andEquation(vx, z, kb.milp);
	
		// y = not_G  z
		ZadehSolver.gNotEquation(y, z, kb.milp);

		// xAinNotWS \geq  y
		kb.milp.addNewConstraint(new Expression(new Term(-1, y), new Term(1, xAinNotWS) ), Inequation.GE);

		// xAinNotWS \geq  1- (w_1 x_{v:C_1} + \dots + w_n x_{v:C_n}) - y
		Expression exp1 =(new Expression(1, terms));
		exp1.addTerm(new Term(-1, y));
		kb.milp.addNewConstraint(exp1, Inequation.LE);

		// xAinNotWS \leq  1 - (w_1 x_{v:C_1} + \dots + w_n x_{v:C_n}) + y
		Expression exp2 =(new Expression(1, terms));
		exp2.addTerm(new Term(1, y));
		kb.milp.addNewConstraint(exp2, Inequation.GE);

		kb.ruleComplemented(ind, this);
	}


	@Override
	public HashSet<Concept> computeAtomicConcepts()
	{
		HashSet<Concept> conceptList = new HashSet<Concept>();
		for (Concept c : concepts)
			conceptList.addAll(c.computeAtomicConcepts());
		return conceptList;
	}


	@Override
	public HashSet<String> getRoles()
	{
		HashSet<String> roleList = new HashSet<String>();
		for (Concept c : concepts)
			roleList.addAll(c.getRoles());
		return roleList;
	}


	@Override
	public Concept replace(Concept a, Concept c) throws FuzzyOntologyException
	{
		ArrayList<Concept> replacedConcepts = new ArrayList<Concept> ();
		for (Concept ci : concepts)
			replacedConcepts.add(ci.replace(a, c));

		Concept aux = new WeightedSumZeroConcept(weights, replacedConcepts);
		if(type == Concept.W_SUM_ZERO)
			aux.setType(NOT_W_SUM_ZERO);
		return aux;
	}
}


