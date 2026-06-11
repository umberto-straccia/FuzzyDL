package test;
import junit.framework.TestCase;


public class TestFuzzyNumber extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/fuzzyNumber1.txt");
		assertEquals("TestFuzzyNumber", 7.5, p.solve());
	}
	
	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/fuzzyNumber2.txt");
		assertEquals("TestFuzzyNumber", -1.833, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/fuzzyNumber3.txt");
		assertEquals("TestFuzzyNumber", 2.5, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/fuzzyNumber4.txt");
		assertEquals("TestFuzzyNumber", 11.0, p.solve());
	}

	public void testQuery5() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/fuzzyNumber5.txt");
		assertEquals("TestFuzzyNumber", 2.0, p.solve());
	}

}
