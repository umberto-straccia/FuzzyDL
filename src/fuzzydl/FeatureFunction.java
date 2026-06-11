package fuzzydl;

import fuzzydl.milp.*;

import java.io.*;
import java.util.*;

/**
 * Function involving several features.
 * @author Fernando Bobillo
 */
public class FeatureFunction implements Serializable 
{

	static final long serialVersionUID = 2978896500377751016L;


	/**
	 * Feature.
	 */
	public static final int ATOMIC = 0;


	/**
	 * Number
	 */
	public static final int NUMBER = 1;


	/**
	 * Sum function.
	 */
	public static final int SUM = 2;


	/**
	 * Substraction function.
	 */
	public static final int SUBSTRACTION = 3;


	/**
	 * Product of a number and a feature.
	 */
	public static final int PRODUCT = 5;


	// Type of the function
	private int type;


	// Features
	private ArrayList<FeatureFunction> f;


	// Atomic feature
	private String feature;


	// Number
	private double n;


	/**
	 * Constructor for the function (feature)
	 * @param feature Feature.
	 */
	public FeatureFunction(String feature)
	{
		try {
			Double n = Double.parseDouble(feature);
			type = NUMBER;
			this.n = n;
		} catch (NumberFormatException e) {
			type = ATOMIC;
			this.feature = feature;
		}
	}


	/**
	 * Constructor for the function (n * feature)
	 * @param feature Feature.
	 * @param n Constant.
	 */
	public FeatureFunction(double n, FeatureFunction feature)
	{
		type = PRODUCT;
		f = new ArrayList<FeatureFunction>();
		f.add(feature);
		this.n = n;
	}


	/**
	 * Constructor for the function (n)
	 * @param n Constant.
	 */
	public FeatureFunction(double n)
	{
		type = NUMBER;
		this.n = n;
	}


	/**
	 * Constructor for the function (feature1 - feature2)
	 * @param feature1 Minuhend feature.
	 * @param feature2 Subtracted feature.
	 */
	public FeatureFunction(FeatureFunction feature1, FeatureFunction feature2)
	{
		type = SUBSTRACTION;
		f = new ArrayList<FeatureFunction>(2);
		f.add(feature1);
		f.add(feature2);
	}


	/**
	 * Constructor for the function (feature1 + feature2 + ... + featureN)
	 * @param featureList List of features to be added.
	 */
	public FeatureFunction(ArrayList<FeatureFunction> featureList)
	{
		type = SUM;
		f = featureList;
	}


	/**
	 * Gets the type of the function.
	 * @return Type of the function.
	 */
	public int getType()
	{
		return type;
	}


	/**
	 * Gets the parameter n of the function.
	 * @return Parameter n of the function.
	 */
	public double getNumber()
	{
		return n;
	}


	/**
	 * Gets an array of features that take part in the function.
	 * @return Features that take part in the function..
	 */
	public HashSet<String> getFeatures ()
	{
		HashSet<String> a = new HashSet<String>();

		switch (type)
		{
			case ATOMIC:
				a.add(feature);
				return a;

			case PRODUCT:
				return f.get(0).getFeatures();
			
			case SUBSTRACTION:
				a = f.get(0).getFeatures();
				a.addAll(f.get(1).getFeatures());
				return a;

			case SUM:
				a = f.get(0).getFeatures();
				for (int i=1; i<f.size(); i++)
					a.addAll(f.get(i).getFeatures());
				return a;
		}

		return a;
	}


	/**
	 * Returns a Expression representing the function.
	 * @param a Subject individual of the features.
	 * @param milp Instance of the MILPHelper class.
	 * @return An expression representing the function.
	 */
	public Expression toExpression(Individual a, MILPHelper milp)
	{
		switch (type)
		{
			case ATOMIC:
				// Get the filler "b" for feature(a)
				ArrayList<Relation> relSet = a.roleRelations.get(feature);
				CreatedIndividual b = (CreatedIndividual) relSet.get(0).getObjectIndividual();

				// Get the variable xB
				Variable xB = milp.getVariable(b);
				return new Expression(new Term(1, xB));

			case NUMBER:
				return new Expression(n);

			case PRODUCT:
				Expression ex = f.get(0).toExpression(a, milp);
				return Expression.multiplyConstant(ex, n);
			
			case SUBSTRACTION:
				Expression ex1 = f.get(0).toExpression(a, milp);
				Expression ex2 = f.get(1).toExpression(a, milp);
				return Expression.subtractExpressions(ex1, ex2);

			case SUM:
				ex1 = f.get(0).toExpression(a, milp);
				for (int i=1; i<f.size(); i++)
				{
					ex2 = f.get(i).toExpression(a, milp);
					ex1 = Expression.addExpressions(ex1, ex2);
				}
				return ex1;
		}
		return null;
	}


	/**
	 * Gets the name of the object.
	 * @return Name of the object.
	 */
	@Override
	public String toString()
	{
		switch (type)
		{
			case ATOMIC:
				return feature;

			case NUMBER:
				return "" + n;

			case PRODUCT:
				return "(" + n + " * " + f.get(0) + ")";
			
			case SUBSTRACTION:
				return "(" + f.get(0) + " - " + f.get(1) + ")";

			case SUM:
				String aux = "(" + f.get(0);
				for (int i=1; i<f.size(); i++)
					aux = aux + " + " + f.get(i);
				return aux + ")";
		}
		return "";
	}

}
