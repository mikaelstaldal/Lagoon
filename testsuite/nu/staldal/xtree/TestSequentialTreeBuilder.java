package nu.staldal.xtree;

import java.io.*;

import org.xml.sax.*;

// import junit.framework.*;

public class TestSequentialTreeBuilder // extends TestCase
{
    public TestSequentialTreeBuilder(String name)
    {
        // super(name);
    }

    public void testSequentialTreeBuilder() throws Exception
	{
		System.out.println("----- BEGIN");
		Element root = SequentialTreeBuilder.parseXML(
			new InputSource(getClass().getResourceAsStream("xtree.xml")), false,
			new ElementHandler() {
				public void processElement(Element el) throws SAXException
				{
					System.out.println("Element  localName=" 
						+ el.getLocalName()	
						+ "  ns=" + el.getNamespaceURI());
					
					System.out.println("Root element  localName=" 
						+ ((Element)el.getParent()).getLocalName()	
						+ "  ns=" + ((Element)el.getParent()).getNamespaceURI());			
				}
			});
		System.out.println("----- END");
		System.out.println("Root element  localName=" + root.getLocalName() 
			+ "  ns=" + root.getNamespaceURI());
		
	}

	
	public static void main(String[] args) throws Exception
	{
		(new TestSequentialTreeBuilder("foo")).testSequentialTreeBuilder();
	}
}

