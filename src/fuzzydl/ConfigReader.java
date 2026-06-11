package fuzzydl;

import java.io.*;
import java.util.*;
import fuzzydl.util.*;

/**
 * Reads some configuration arguments for the reasoner.
 * @author Fernando Bobillo
 */
public class ConfigReader 
{

	/**
	 * Anywhere pairwise blocking applied.
	 * false disables anywhere double blocking; true enables anywher edouble blocking.
	 */
	public static boolean ANYWHERE_DOUBLE_BLOCKING = true;


	/**
	 * Anywhere simple blocking applied.
	 * false disables anywhere simple blocking; true enables anywhere simple blocking.
	 */
	public static boolean ANYWHERE_SIMPLE_BLOCKING = true;


	/**
	 * Debugging mode.
	 */
	public static boolean DEBUG_PRINT = false;


	/**
	 * Precision of the reasoner.
	 */
	public static double EPSILON = 0.001;


	/**
	 * Maximum number of new individuals that will be created.
	 */
	public static int MAX_INDIVIDUALS = -1;


	/**
	 * Number of digits of precision.
	 */
	public static int NUMBER_DIGITS = 2;


	/**
	 * Level of the optimizations applied.
	 * 0 disables optimizations; a positive value enables optimizations.
	 */
	public static int OPTIMIZATIONS = 1;


	/**
	 * Rule acyclic TBox optimization applied.
	 */
	public static boolean RULE_ACYCLIC_TBOXES = true;


	/**
	 * Show the version of the reasoner.
	 */
	public static boolean SHOW_VERSION = false;


	/**
	 * Load parameters for the reasoner.
	 * The input is a configuration file or an array of Strings.
	 * @param configFile Configuration file.
	 * @param args Array or parameters.
	 */
	public static void loadParameters(String configFile, String[] args)
	{
		try
		{
			Properties param = new Properties();
			FileInputStream inParam = new FileInputStream(configFile);
			param.load(inParam);
			inParam.close();
	
			if (args.length > 1)
				for(int i=0; i<args.length; i=i+2)
					param.setProperty(args[i], args[i+1]);

			DEBUG_PRINT = (Boolean.valueOf(param.getProperty("debugPrint"))).booleanValue();
			EPSILON = Double.parseDouble(param.getProperty("epsilon"));
			MAX_INDIVIDUALS = Integer.parseInt(param.getProperty("maxIndividuals"));
			NUMBER_DIGITS = (int) Math.round(Math.abs(Math.log10(EPSILON) - 1));
//			OPTIMIZATIONS = Integer.parseInt(param.getProperty("optimization"));
			SHOW_VERSION = (Boolean.valueOf(param.getProperty("showVersion"))).booleanValue();
			Util.println("Debugging mode = " + DEBUG_PRINT);

			if ((Boolean.valueOf(param.getProperty("author"))).booleanValue())
				System.out.println("Authors of fuzzyDL: Fernando Bobillo and Umberto Straccia");
		}
		catch (java.io.FileNotFoundException e)
		{
			System.out.println("Error: File " + configFile + " not found.");
		}
		catch (java.io.IOException e)
		{
			System.out.println("Error: " + e + ".");
		}
	}


}
