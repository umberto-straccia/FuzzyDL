package fuzzydl;

import java.io.*;

/**
 * Functional concrete feature with a range string, integer or real.
 * @author Fernando Bobillo
 */
public class ConcreteFeature implements Serializable
{

	private static final long serialVersionUID = -439593776684158804L;
	public static final int STRING = 0;
	public static final int INTEGER = 1;
	public static final int REAL = 2;
	public static final int BOOLEAN = 3;


	// Range
	private int type;

	// Name
	private String name;

	// Lower bound for the range
	private Object k1;

	// Upper bound for the range
	private Object k2;


	/**
	 * Constructor for strings.
	 * @param name Name of the feature.
	 */
	public ConcreteFeature (String name)
	{
		k1 = k2 = null;
		type = STRING;
		this.name = name;
	}


	/**
	 * Constructor for strings and booleans.
	 * @param name Name of the feature.
	 * @param isBoolean true if the range is boolean; false if the range is string.
	 */
	public ConcreteFeature (String name, boolean isBoolean)
	{
		this(name);
		if (isBoolean)
			type = BOOLEAN;
	}


	/**
	 * Constructor for integers.
	 * @param name Name of the feature.
	 * @param k1 Lower bound for the range.
	 * @param k2 Upper bound for the range.
	 */
	public ConcreteFeature (String name, Integer k1, Integer k2)
	{
		this.k1 = k1;
		this.k2 = k2;
		this.type = INTEGER;
		this.name = name;
	}


	/**
	 * Constructor for reals.
	 * @param name Name of the feature.
	 * @param k1 Lower bound for the range.
	 * @param k2 Upper bound for the range.
	 */
	public ConcreteFeature (String name, Double k1, Double k2)
	{
		this.k1 = k1;
		this.k2 = k2;
		this.type = REAL;
		this.name = name;
	}


	/**
	 * Gets the type of the feature.
	 * @return Type of the feature.
	 */
	public int getType()
	{
		return type;
	}


	/**
	 * Sets the type of the feature.
	 * @param newType Type of the feature.
	 */
	public void setType(int newType)
	{
		type = newType;
	}


	/**
	 * Gets the lower bound for the range.
	 * @return Lower bound for the range.
	 */
	public Object getK1()
	{
		return k1;
	}


	/**
	 * Gets the upper bound for the range.
	 * @return Upper bound for the range.
	 */
	public Object getK2()
	{
		return k2;
	}


	/**
	 * Sets the range of the feature.
	 * @param k1 Lower bound for the range.
	 * @param k2 Upper bound for the range.
	 */
	public void setRange(Object k1, Object k2)
	{
		this.k1 = k1;
		this.k2 = k2;	
	}


	/**
	 * Gets the name.
	 * @return Name.
	 */
	public String getName()
	{
		return name;
	}

}
