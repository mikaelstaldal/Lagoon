/*
 * Copyright (c) 2001, Mikael Ståldal
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

import org.xml.sax.*;


public class ContentHandlerSnooper implements ContentHandler
{
	private static final boolean DEBUG = true;

    private ContentHandler ch;

    public ContentHandlerSnooper(ContentHandler ch)
    {
        this.ch = ch;
		if (DEBUG) System.out.println("New ContentHandlerSnooper");
    }


    // ContentHandler implementation

    public void setDocumentLocator(Locator locator)
    {
        // ch.setDocumentLocator(locator);
    }

    public void startDocument()
        throws SAXException
    {
		if (DEBUG) System.out.println("startDocument");
        ch.startDocument();
    }

    public void endDocument()
        throws SAXException
    {
		if (DEBUG) System.out.println("endDocument");
        ch.endDocument();
    }

    public void startElement(String namespaceURI, String localName,
                             String qname, Attributes atts)
        throws SAXException
    {
		if (DEBUG) System.out.println("startElement("+namespaceURI+
            ','+localName+','+qname+')');
			
		ch.startElement(namespaceURI, localName, qname, atts);

    }

    public void endElement(String namespaceURI, String localName,
                           String qname)
        throws SAXException
    {
		if (DEBUG) System.out.println("endElement("+namespaceURI+','+
            localName+','+qname+')');
			
		ch.endElement(namespaceURI, localName, qname);

    }

    public void startPrefixMapping(String prefix, String uri)
        throws SAXException
    {
		if (DEBUG) System.out.println("startPrefixMapping("+
			((prefix.length() == 0) ? "<default>" : prefix) +','+uri+')');
			
		ch.startPrefixMapping(prefix, uri);
    }

    public void endPrefixMapping(String prefix)
        throws SAXException
    {
		if (DEBUG) System.out.println("endPrefixMapping("+
			((prefix.length() == 0) ? "<default>" : prefix)+')');
			
		ch.endPrefixMapping(prefix);
    }

    public void characters(char[] chars, int start, int length)
        throws SAXException
    {
        ch.characters(chars, start, length);
    }

    public void ignorableWhitespace(char[] chars, int start, int length)
        throws SAXException
    {
        ch.ignorableWhitespace(chars, start, length);
    }

    public void processingInstruction(String target, String data)
        throws SAXException
    {
		if (DEBUG) System.out.println("processingInstruction("+target+','+
			data+')');

        ch.processingInstruction(target, data);
    }

    public void skippedEntity(String name)
        throws SAXException
    {
		if (DEBUG) System.out.println("skippedEntity("+name+')');

        ch.skippedEntity(name);
    }

}

