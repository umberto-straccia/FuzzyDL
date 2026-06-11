package test;
import junit.framework.TestCase;


public class TestWeightedConcept extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/weightedConcept1.txt");
		assertEquals("TestWeightedConcept", 0.286, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/weightedConcept2.txt");
		assertEquals("TestWeightedConcept", 0.429, p.solve());
	}

}
