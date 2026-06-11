package fuzzydl;

import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.util.*;
import java.util.*;

/**
 * Sugeno integral concept.
 * @author Fernando Bobillo
 */
public class SugenoIntegral extends Concept
{
	private static final long serialVersionUID = -2957780995611192563L;
	
	protected ArrayList<Double> weights;
	protected ArrayList<Concept> concepts;


	public SugenoIntegral(ArrayList<Double> weights, ArrayList<Concept> concepts) throws FuzzyOntologyException
	{
		super(SUGENO_INTEGRAL);
		initSugenoIntegral(weights, concepts);

	}


	protected void initSugenoIntegral(ArrayList<Double> weights, ArrayList<Concept> concepts) throws FuzzyOntologyException
	{
		this.concepts = concepts;
		if (weights != null)
		{
			this.weights = weights;
			int n = this.weights.size(); 
			if (n != concepts.size())
				Util.error("Error: The number of weights and the number of concepts should be the same");
			double max = Collections.max(this.weights);
			if (max != 1.0)
			{
				Util.println("In (quasi-)Sugeno integral, the greatest weight should be 1: normalizing weights");
				for(int i = 0; i < n; i++)
					this.weights.set(i, this.weights.get(i) / max);
			}
			setName(toString());
		}
		else
			this.weights = new ArrayList<Double> ();
	}

	SugenoIntegral(int type) throws FuzzyOntologyException
	{
		super(type);
	}


	@Override
	public String toString()
	{
		String name = "(sugeno (";
		if (weights != null)
		{
			name += weights.get(0);
			for(int i=1; i<weights.size(); i++)
				name += " " + weights.get(i).toString();
		}
		name += ") (";
		name += concepts.get(0);
		for(int i=1; i<concepts.size(); i++)
			name += " " + concepts.get(i).toString();
		name += ") )";

		if(type == SUGENO_INTEGRAL)
			return name;
		else
			return "(not " + name + " )";
	}


	/**
	 * Gets the complement of the concept.
	 * @return Complement of the concept.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public Concept complement() throws FuzzyOntologyException
	{
		Concept aux = new SugenoIntegral(weights, concepts);
		if(type == SUGENO_INTEGRAL)
			aux.setType(NOT_SUGENO_INTEGRAL);
		return aux;
	}


	/**
	 * Solves an assertion of the form (individual, concept) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param kb A fuzzy KB.
	 */
	public void solveAssertion(Individual ind, KnowledgeBase kb)
	{
		// New n variables x_i
		int n = concepts.size();
		Variable x[] = new Variable[n];
		for(int i=0; i<n; i++)
		{
			Concept ci = concepts.get(i);
			x[i] = kb.milp.getVariable(ind, ci);
			kb.addAssertion(ind, ci, Degree.getDegree(x[i]) );
		}

		// y1 > y2 > ... > yn
		Variable z[][] = new Variable[n][n];
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
				z[i][j] = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);		
		Variable y[] = kb.milp.getOrderedPermutation(x, z);

		Variable ow[] = new Variable[n];
		for(int i=0; i<n; i++)
			ow[i] = kb.milp.getNewVariable(Variable.UP_VARIABLE);

		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
			{
				// ow_j \geq (1 - z_{ij}) w_i
				kb.milp.addNewConstraint(new Expression(
					-weights.get(i), new Term(weights.get(i), z[i][j]), new Term(1, ow[j])
				), Inequation.GE);
			
				// ow_j \leq z_{ij} + w_i
				kb.milp.addNewConstraint(new Expression(
						-weights.get(i), new Term(-1, z[i][j]), new Term(1, ow[j])
					), Inequation.LE);
			}

		Variable a[] = new Variable[n];
		for(int i=0; i<n; i++)
			a[i] = kb.milp.getNewVariable(Variable.UP_VARIABLE);

		// a_1 = ow_1
		kb.milp.addNewConstraint(new Expression(new Term(1, a[0]), new Term(-1, ow[0])), Inequation.EQ);

		// a_i = ow_i \oplus a_{i-1}
		for(int i=1; i<n; i++)
		{
			Vector<Variable> vx = new Vector<Variable>();
			vx.add(ow[i]);
			vx.add(a[i-1]);			
			LukasiewiczSolver.orEquation(vx, a[i], kb.milp);
		}

		// New n variables c_i
		Variable c[] = new Variable[n];
		for(int i=0; i<n; i++)
		{
			c[i] = kb.milp.getNewVariable(Variable.UP_VARIABLE);

			// c_i = y_i \otimes a_i
			andEquation(c[i], y[i], a[i], kb);
		}

		// if bi = 0, then ci >= x_{ind:SI}
		Degree degree = Degree.getDegree(kb.milp.getVariable(ind, this));
		Variable b[] = new Variable[n];
		for(int i=0; i<n; i++)
		{
			b[i] = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
			kb.milp.addNewConstraint(new Expression(new Term(1,b[i]), new Term(1,c[i])), Inequation.GE, degree);
		}
		
		// \sum bi = n-1
		Expression exp = new Expression();
		for(int i=0; i<n; i++)
			exp.addTerm(new Term(1, b[i]));
		kb.milp.addNewConstraint(exp, Inequation.EQ, n-1);	
	}


	/**
	 * Solves an assertion of the form (individual, not concept) with respect to a fuzzy KB.
	 * @param ind An individual.
	 * @param kb A fuzzy KB.
	 */
	public void solveComplementedAssertion(Individual ind, KnowledgeBase kb)
	{
		// New n variables x_i
		int n = concepts.size();
		Variable x[] = new Variable[n];
		for(int i=0; i<n; i++)
		{
			Concept ci = concepts.get(i);
			Concept notCi = Concept.complement(ci);
			x[i] = kb.milp.getVariable(ind, ci);
			Variable xNoti = kb.milp.getVariable(ind, notCi);
			kb.addAssertion(ind, notCi, Degree.getDegree(xNoti));
		}

		// y1 > y2 > ... > yn
		Variable z[][] = new Variable[n][n];
		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
				z[i][j] = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);		
		Variable y[] = kb.milp.getOrderedPermutation(x, z);


		Variable ow[] = new Variable[n];
		for(int i=0; i<n; i++)
			ow[i] = kb.milp.getNewVariable(Variable.UP_VARIABLE);

		for(int i=0; i<n; i++)
			for(int j=0; j<n; j++)
			{
				// ow_j \geq (1 - z_{ij}) w_i
				kb.milp.addNewConstraint(new Expression(
					-weights.get(i), new Term(weights.get(i), z[i][j]), new Term(1, ow[j])
				), Inequation.GE);
			
				// ow_j \leq z_{ij} + w_i
				kb.milp.addNewConstraint(new Expression(
						-weights.get(i), new Term(-1, z[i][j]), new Term(1, ow[j])
					), Inequation.LE);
			}

		Variable a[] = new Variable[n];
		for(int i=0; i<n; i++)
			a[i] = kb.milp.getNewVariable(Variable.UP_VARIABLE);

		// a_1 = ow_1
		kb.milp.addNewConstraint(new Expression(new Term(1, a[0]), new Term(-1, ow[0])), Inequation.EQ);

		// a_i = ow_i \oplus a_{i-1}
		for(int i=1; i<n; i++)
		{
			Vector<Variable> vx = new Vector<Variable>();
			vx.add(ow[i]);
			vx.add(a[i-1]);			
			LukasiewiczSolver.orEquation(vx, a[i], kb.milp);
		}

		// New n variables c_i
		Variable c[] = new Variable[n];
		for(int i=0; i<n; i++)
		{
			c[i] = kb.milp.getNewVariable(Variable.UP_VARIABLE);

			// c_i = y_i \otimes a_i
			andEquation(c[i], y[i], a[i], kb);
		}

		// if bi = 0, then ci >= x_{ind:SI}
		Degree degree = Degree.getDegree(kb.milp.getVariable(ind, this));
		Variable b[] = new Variable[n];
		for(int i=0; i<n; i++)
		{
			b[i] = kb.milp.getNewVariable(Variable.BINARY_VARIABLE);
			kb.milp.addNewConstraint(new Expression(new Term(1,b[i]), new Term(1,c[i])), Inequation.GE, degree);
		}
		
		// \sum bi = n-1
		Expression exp = new Expression();
		for(int i=0; i<n; i++)
			exp.addTerm(new Term(1, b[i]));
		kb.milp.addNewConstraint(exp, Inequation.EQ, n-1);

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


	// x1 = x2 AND x3
	protected void andEquation(Variable x1, Variable x2, Variable x3, KnowledgeBase kb)
	{
		ZadehSolver.andEquation(x1, x2, x3, kb.milp);  	
	}


	@Override
	public Concept replace(Concept a, Concept c) throws FuzzyOntologyException
	{
		ArrayList<Concept> replacedConcepts = new ArrayList<Concept> ();
		for (Concept ci : concepts)
			replacedConcepts.add(ci.replace(a, c));
		 
		Concept aux = new SugenoIntegral(weights, replacedConcepts);
		if(type == SUGENO_INTEGRAL)
			aux.setType(NOT_SUGENO_INTEGRAL);
		return aux;
	}

}
