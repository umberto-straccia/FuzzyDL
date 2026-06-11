package test;
import junit.framework.TestCase;


public class TestRoughSets extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/roughSets1.txt");
		assertEquals("TestRoughSets", 0.6, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/roughSets2.txt");
		assertEquals("TestRoughSets", 1.0, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/roughSets3.txt");
		assertEquals("TestRoughSets", 1.0, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/roughSets4.txt");
		assertEquals("TestRoughSets", 1.0, p.solve());
	}

	public void testQuery5() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/roughSets5.txt");
		assertEquals("TestRoughSets", 1.0, p.solve());
	}

}
