package test;
import junit.framework.TestCase;


public class TestImpliesRole extends TestCase 
{

	public void testQuery1() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/impliesRole1.txt");
		assertEquals("TestImpliesRole", 0.6, p.solve());
	}

	public void testQuery2() throws Exception
	{
		ParserInterface p = new ParserInterface("examples/TestSuite/impliesRole2.txt");
		assertEquals("TestImpliesRole", 0.7, p.solve());
	}

}
