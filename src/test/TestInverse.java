package test;
import junit.framework.TestCase;


public class TestInverse extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/inverseRole1.txt");
		assertEquals("TestInverse", 0.5, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/inverseRole2.txt");
		assertEquals("TestInverse", 0.8, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/inverseRole3.txt");
		assertEquals("TestInverse", -1.0, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/inverseRole4.txt");
		assertEquals("TestInverse", 0.7, p.solve());
	}

	public void testQuery5() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/inverseRole5.txt");
		assertEquals("TestInverse", 0.8, p.solve());
	}

	public void testQuery6() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/inverseRole6.txt");
		assertEquals("TestInverse", -1.0, p.solve());
	}

}
