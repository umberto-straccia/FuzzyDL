package test;
import junit.framework.TestCase;


public class TestSat extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/sat1.txt");
		assertEquals("TestSat", 0.0, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/sat2.txt");
		assertEquals("TestSat", 1.0, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/sat3.txt");
		assertEquals("TestSat", 0.5, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/sat4.txt");
		assertEquals("TestSat", 1.0, p.solve());
	}

	public void testQuery5() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/sat5.txt");
		assertEquals("TestSat", 1.0, p.solve());
	}

	public void testQuery6() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/sat6.txt");
		assertEquals("TestSat", -1.0, p.solve());
	}

	public void testQuery7() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/sat7.txt");
		assertEquals("TestSat", 0.5, p.solve());
	}

}