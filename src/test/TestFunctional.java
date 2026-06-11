package test;
import junit.framework.TestCase;


public class TestFunctional extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/functional1.txt");
		assertEquals("TestFunctional", 0.6, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/functional2.txt");
		assertEquals("TestFunctional", 1.0, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/functional3.txt");
		assertEquals("TestFunctional", -1.0, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/functional4.txt");
		assertEquals("TestFunctional", 1.0, p.solve());
	}

}
