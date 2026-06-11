package test;

import java.io.*;
import fuzzydl.*;
import fuzzydl.milp.Solution;
import fuzzydl.parser.*;


/**
 * Tests the reasoner.
 * @author Fernando Bobillo
 */
public class ParserInterface {

	Parser parser;
	
	public ParserInterface(String fileName) throws Exception
	{
		String args[] = new String[0]; 
		ConfigReader.loadParameters("CONFIG", args);
		parser = new Parser(new FileInputStream(fileName));
		Parser.reset();
	}

	public double solve()
	throws Exception
	{
		parser.Start();
		KnowledgeBase kb = parser.getKB();
		Query query = parser.getQueries().get(0);
		kb.solveKB();
		Solution sol = query.solve(kb);
		if (sol.isConsistentKB())
			return sol.getSolution();
		else
			return -1;
	}

}
