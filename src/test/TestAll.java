package test;
import junit.framework.TestCase;


public class TestAll extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/all1.txt");
		assertEquals("TestAll", 1.0, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/all2.txt");
		assertEquals("TestAll", 0.7, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/all3.txt");
		assertEquals("TestAll", 1.0, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/all4.txt");
		assertEquals("TestAll", 0.8, p.solve());
	}

}
