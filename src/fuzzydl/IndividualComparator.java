package fuzzydl;

import java.io.*;
import java.util.*;

/**
 * Compares created individuals.
 * @author Fernando Bobillo
 */
public class IndividualComparator implements Comparator<CreatedIndividual>, Serializable
{
	private static final long serialVersionUID = 1458678352659989951L;

	@Override
	public int compare(CreatedIndividual ind1, CreatedIndividual ind2) 
	{  
		return ind1.getIntegerID() - ind2.getIntegerID();
/*
		int ID1 = ind1.getIntegerID();
		int ID2 = ind2.getIntegerID();
		if (ID1 < ID2)
			return -1;
		else if (ID1 == ID2)
			return 0;
		else
			return 1;
*/
	}
	
}
