package nu.staldal.xtree;

import java.io.*;

import org.xml.sax.*;

import nu.staldal.lagoon.util.*;

import junit.framework.*;

public class TestXTree extends TestCase
{
    public TestXTree(String name)
    {
        super(name);
    }
	
    
    public void testXTree() throws Exception
	{
		Element el = TreeBuilder.parseXML(
			new InputSource(getClass().getResourceAsStream("xtree.xml")), false);

		OutputStream fos = new FileOutputStream("xtree.ser");
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(el);
		fos.close();

		el = null;
		InputStream fis = new FileInputStream("xtree.ser");
		ObjectInputStream ois = new ObjectInputStream(fis);
		el = (Element)ois.readObject();
		fis.close();

		/*fos = new FileOutputStream("xtree.out");
		TreeBuilder.serialize(el, fos);
		fos.close(); */                
	}
    
    
    public void testXmlSpace() throws Exception
    {
		Element el = TreeBuilder.parseXML(
			new InputSource(getClass().getResourceAsStream("xmlspace.xml")), false);
        
        Element el2 = el.getFirstChildElement();
        Element el3 = el2.getFirstChildElement();
        Element el4 = el3.getFirstChildElement();
        Element el5 = el4.getFirstChildElement();
        
        assertTrue(!el.getPreserveSpace());
        assertTrue(el2.getPreserveSpace());
        assertTrue(el3.getPreserveSpace());
        assertTrue(!el4.getPreserveSpace());        
        assertTrue(!el5.getPreserveSpace());        
    }
    

	public void tearDown()
	{
		new File("xtree.ser").delete();	
		/*new File("xtree.out").delete();*/	
	}
}

