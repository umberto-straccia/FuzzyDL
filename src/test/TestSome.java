package test;
import junit.framework.TestCase;


public class TestSome extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/some1.txt");
		assertEquals("TestSome", 0.8, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/some2.txt");
		assertEquals("TestSome", 0.8, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/some3.txt");
		assertEquals("TestSome", 0.6, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/some4.txt");
		assertEquals("TestSome", 0.8, p.solve());
	}

}