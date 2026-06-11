package test;
import junit.framework.TestCase;


public class TestSubsumption extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/subsumption1.txt");
		assertEquals("TestSubsumption", 0.5, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/subsumption2.txt");
		assertEquals("TestSubsumption", 0.0, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/subsumption3.txt");
		assertEquals("TestSubsumption", 0.3, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/subsumption4.txt");
		assertEquals("TestSubsumption", 1.0, p.solve());
	}

}