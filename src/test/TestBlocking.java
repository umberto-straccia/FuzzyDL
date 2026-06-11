package test;
import junit.framework.TestCase;


public class TestBlocking extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/blockingLoop1.txt");
		assertEquals("TestBlocking", 0.8, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/blockingLoop2.txt");
		assertEquals("TestBlocking", 0.8, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/blockingLoop3.txt");
		assertEquals("TestBlocking", 0.8, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/blockingLoop4.txt");
		assertEquals("TestBlocking", 1.0, p.solve());
	}

	public void testQuery5() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/blockingLoop5.txt");
		assertEquals("TestBlocking", 0.0, p.solve());
	}

	public void testQuery6() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/blockingLoop6.txt");
		assertEquals("TestBlocking", 0.0, p.solve());
	}

	public void testQuery7() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/blockingDynamic.txt");
		assertEquals("TestBlocking", -1.0, p.solve());
	}	

	public void testQuery8() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/blockingPairwise.txt");
		assertEquals("TestBlocking", 0.0, p.solve());
	}

	public void testQuery9() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/blockingSimple.txt");
		assertEquals("TestBlocking", 0.8, p.solve());
	}

}
