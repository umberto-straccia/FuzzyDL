package test;
import junit.framework.TestCase;


public class TestAggregation extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/aggregation1.txt");
		assertEquals("TestOwa", 0.6, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/aggregation2.txt");
		assertEquals("TestQ-Owa", 0.6, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/aggregation3.txt");
		assertEquals("TestSugeno", 0.4, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/aggregation4.txt");
		assertEquals("TestQ-Sugeno", 0.9, p.solve());
	}

	public void testQuery5() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/aggregation5.txt");
		assertEquals("TestChoquet", 0.5, p.solve());
	}

	public void testQuery6() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/aggregation6.txt");
		assertEquals("TestWeightedMin", 0.6, p.solve());
	}

	public void testQuery7() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/aggregation7.txt");
		assertEquals("TestWeightedMax", 0.6, p.solve());
	}
}
