package test;
import junit.framework.TestCase;


public class TestThresholdConcept extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/thresholdConcept1.txt");
		assertEquals("TestThresholdConcept", 0.2, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/thresholdConcept2.txt");
		assertEquals("TestThresholdConcept", 0.4, p.solve());
	}
	
	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/thresholdConcept3.txt");
		assertEquals("TestThresholdConcept", 0.3, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/thresholdConcept4.txt");
		assertEquals("TestThresholdConcept", 0.3, p.solve());
	}
}
