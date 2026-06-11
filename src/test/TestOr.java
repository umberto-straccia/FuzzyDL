package test;
import junit.framework.TestCase;


public class TestOr extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/or1.txt");
		assertEquals("TestOr", 0.0, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/or2.txt");
		assertEquals("TestOr", 1.0, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/or3.txt");
		assertEquals("TestOr", 0.0, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/or4.txt");
		assertEquals("TestOr", 1.0, p.solve());
	}

}
