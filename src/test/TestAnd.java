package test;
import junit.framework.TestCase;


public class TestAnd extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/and1.txt");
		assertEquals("TestAnd", 0.7, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/and2.txt");
		assertEquals("TestAnd", 0.1, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/and3.txt");
		assertEquals("TestAnd", 0.7, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/and4.txt");
		assertEquals("TestAnd", 0.4, p.solve());
	}
}
