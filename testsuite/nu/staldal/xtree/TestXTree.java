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

		fos = new FileOutputStream("xtree.out");
		TreeBuilder.toOutputStream(el, fos);
		fos.close();
	}

}

