package test;
import junit.framework.TestCase;


public class TestInconsistency extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/inconsistent1.txt");
		assertEquals("TestInconsistency", -1.0, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/inconsistent2.txt");
		assertEquals("TestInconsistency", -1.0, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/inconsistent3.txt");
		assertEquals("TestInconsistency", -1.0, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/inconsistent4.txt");
		assertEquals("TestInconsistency", -1.0, p.solve());
	}

	public void testQuery5() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/inconsistent5.txt");
		assertEquals("TestInconsistency", -1.0, p.solve());
	}

	public void testQuery6() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/inconsistent6.txt");
		assertEquals("TestInconsistency", -1.0, p.solve());
	}

}
