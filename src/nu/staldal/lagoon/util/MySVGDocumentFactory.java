/*
 * Copyright (c) 2002, Mikael St�ldal
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the author nor the names of its contributors
 * may be used to endorse or promote products derived from this software
 * without specific prior written permission.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE REGENTS OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY
 * OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * Note: This is known as "the modified BSD license". It's an approved
 * Open Source and Free Software license, see
 * http://www.opensource.org/licenses/
 * and
 * http://www.gnu.org/philosophy/license-list.html
 */

package nu.staldal.lagoon.util;

import java.net.URL;

import javax.xml.parsers.*;
import org.xml.sax.*;

import org.apache.batik.dom.svg.*;
import org.apache.batik.dom.util.*;


/**
 * Create SVGDocument instances from SAX2 events.
 */
public class MySVGDocumentFactory extends SAXDocumentFactory
{
	private URL url;
	
	private static String xmlReaderClassName;

	static {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			XMLReader parser = spf.newSAXParser().getXMLReader();
			xmlReaderClassName = parser.getClass().getName();
		}
		catch (ParserConfigurationException e)
		{
			throw new Error(e.getMessage());	
		}
		catch (SAXException e)
		{
			throw new Error(e.getMessage());	
		}
		
		org.apache.batik.util.XMLResourceDescriptor.setXMLParserClassName(
			 xmlReaderClassName);
	}

	
    /**
     * Creates a new MySVGDocumentFactory, ready to receive SAX2 events.
	 *
	 * @param url     URL to use for resolving relative xlinks
     */
    public MySVGDocumentFactory(URL url)
	{
        super(ExtensibleSVGDOMImplementation.getDOMImplementation(), 
			xmlReaderClassName);
		
		document = implementation.createDocument(
			SVGDOMImplementation.SVG_NAMESPACE_URI, "svg", null);
			
		this.url = url;
    }

	
    /**
	 * Get the created document.
     */
    public SVGOMDocument getDocument() 
	{
		SVGOMDocument doc = (SVGOMDocument)document;
		
        doc.setURLObject(url);
		
        return doc;
    }
	
}
