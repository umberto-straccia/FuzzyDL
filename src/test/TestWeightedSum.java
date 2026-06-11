package test;
import junit.framework.TestCase;


public class TestWeightedSum extends TestCase 
{

	public void testQuery() throws Exception 
	{
 		ParserInterface p = new ParserInterface("examples/TestSuite/weightedSum.txt");
		assertEquals("TestWeightedSum", 0.8, p.solve());
	}
}
