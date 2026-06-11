package fuzzydl.util;

import fuzzydl.*;
import fuzzydl.exception.*;

import java.io.*;
import java.math.*;
import java.util.*;

/**
 * Common utilities.
 * @author Fernando Bobillo
 */
public class Util {


	/**
	 * Prints on screen a message and a new line only in debug mode
	 * @param message Message to be printed.
	 */
	public static void println(String message) //throws IOException
	{
		print(message + "\n");
	}


	/**
	 * Prints on screen a message only in debug mode
	 * @param message Message to be printed.
	 */
	public static void print(String message) //throws IOException
	{
		if (ConfigReader.DEBUG_PRINT) 
			System.out.print(message);
	}


	/**
	 * Prints an error message and finishes the execution of the program.
	 * @param message Message to display.
	 * @throws FuzzyOntologyException fuzzy ontology exception.
	 */
	public static void error(String message) throws FuzzyOntologyException
	{
	/*
		System.out.println(message);
		System.exit(-1);
	 */
		throw new FuzzyOntologyException(message);
	}


	/**
	 * Checks whether a double number has an integer value or not.
	 * @param d A double number.
	 * @return Whether d has an integer value or not.
	 */
	public static boolean hasIntegerValue(Double d)
	{
		Integer i = new Integer(d.intValue());
		return (new Double(i.intValue())).equals(d);
	}

	
	/**
	 * Rounds a double number to the number of decimals ConfigReader.NUMBER_DIGITS
	 * 
	 * @param x A double number.
	 * @return A rounded double number.
	 */
	public static double round(double x)
	{
		BigDecimal bigDecimal = new BigDecimal("" + x);
		return bigDecimal.setScale(ConfigReader.NUMBER_DIGITS, RoundingMode.HALF_UP).doubleValue();
	}


	/**
	 * Order a vector of concepts.
	 * @param v A vector of concepts.
	 */
	public static void order(ArrayList<Concept> v)
	{
		Collections.sort(v, new ConceptOrdered());
	}


	private static class ConceptOrdered implements Comparator<Concept>
	{
		public int compare(Concept c1, Concept c2) 
		{
			return c1.toString().compareTo(c2.toString());
		}
	}


	/**
	 * Computes the base 2 logarithm of a number. 
	 * @param n A real number.
	 * @return Smallest integer greater or equal than the base 2 logarithm of a number.
	 */
	public static int log2(double n)
	{
		return (int) Math.ceil(Math.log(n) / Math.log(2));
	}

}
