
import java.io.*;
import java.util.*;

import fuzzydl.*;
import fuzzydl.exception.*;
import fuzzydl.milp.*;
import fuzzydl.parser.*;


public class Test
{

	
	public static void main(String[] args) throws FuzzyOntologyException, InconsistentOntologyException, IOException, ParseException
	{
		String inputFile = "kb.txt";  // args[0]
		
		double iniTime = (new Date()).getTime();

		// Load options for the reasoner, using file "CONFIG"
		ConfigReader.loadParameters("CONFIG", new String[0]);

		// The three latter lines can be replaced by the following one
		KnowledgeBase kb = Parser.getKB(inputFile);

		// After having created KB and queries, start logical inference
		kb.solveKB();
		
		Concept C = kb.getConcept("C");
    	AllInstancesQuery q = new AllInstancesQuery(C);
    	Solution result = q.solve(kb);

		double endTime = (new Date()).getTime();
		double totalTime = ((endTime - iniTime)); // / 1000;
		System.out.println("Time: " + totalTime + " ms");

		// Print the result
        if ( (q instanceof AllInstancesQuery) && kb.getIndividuals().values().isEmpty() )
            System.out.println(q.toString() + " There are no individuals in the fuzzy KB");
        else
        {
			if (result.isConsistentKB())
			{
		    	for (int i=0; i<q.getIndividuals().size(); i++)
					System.out.println(q.getIndividuals().get(i) + " : " + q.getDegrees().get(i));
		    	System.out.println(/*q.toString() +*/ result.getSolution());
			}
			else
				System.out.println("KB is inconsistent");
        }
	}

}





