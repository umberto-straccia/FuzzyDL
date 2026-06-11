package test;
import junit.framework.TestCase;


public class TestDatatype extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/datatype1.txt");
		assertEquals("TestDatatype", 1.0, p.solve());
	}
	
	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/datatype2.txt");
		assertEquals("TestDatatype", 1.0, p.solve());
	}

	public void testQuery3() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/datatype3.txt");
		assertEquals("TestDatatype", 1.0, p.solve());
	}

	public void testQuery4() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/datatype4.txt");
		assertEquals("TestDatatype", 0.0, p.solve());
	}

	public void testQuery5() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/datatype5.txt");
		assertEquals("TestDatatype", 1.0, p.solve());
	}

	public void testQuery6() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/datatype6.txt");
		assertEquals("TestDatatype", 0.0, p.solve());
	}

}
