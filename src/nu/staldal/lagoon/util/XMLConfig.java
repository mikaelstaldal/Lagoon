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

/**
 * Represent a configuration that is read from an XML file.
 */
public abstract class XMLConfig implements ContentHandler, ErrorHandler,
                                                           EntityResolver
{
    // Work attributes
    private boolean firstEntity;
    private boolean foundDTD;
    private boolean validate;

	/**
	 * Get the Public ID of the DTD.
	 */
    protected abstract String getPublicId();


	/**
	 * Get the DTD to be used to validate the XML file.
	 */
    protected abstract java.io.InputStream getDTD();


    // partial SAX ContentHandler implementation

    public void setDocumentLocator(Locator locator)
    {
        // nothing to do
    }

    public final void startDocument()
        throws SAXException
    {
        // nothing to do
    }

    public final void endDocument()
        throws SAXException
    {
        if (!foundDTD && validate)
            throw new SAXException("The DTD was not properly specified");
    }


    // SAX ErrorHandler implementation

	public final void warning(SAXParseException e)
	{
        // ignore warning
	}

	public final void error(SAXParseException e)
        throws SAXException
	{
        throw e;
	}

	public final void fatalError(SAXParseException e)
        throws SAXException
	{
        throw e;
	}


    // SAX EntityResolver implementation

    public InputSource resolveEntity(String publicId, String SystemId)
        throws SAXException
    {
        if (firstEntity && publicId != null &&
            publicId.equals(getPublicId()))
        {
            firstEntity = false;
            foundDTD = true;
            java.io.InputStream theDTD = getDTD();
            if (theDTD == null)
                throw new SAXException("DTD not found");
            return new InputSource(theDTD);
        }
        else
        {
            firstEntity = false;
            return null;
        }
    }

    /**
     * Parse the XML file.
     *
     * @param input  where to read the XML data from
     */
    protected void parseXML(java.io.InputStream input, boolean validate)
        throws java.io.IOException, SAXException
    {
		this.validate = validate;

        XMLReader parser =
			new org.apache.xerces.parsers.SAXParser();

	    parser.setFeature("http://xml.org/sax/features/validation",
            validate);
        parser.setFeature("http://xml.org/sax/features/namespaces",
            true);
        parser.setFeature("http://xml.org/sax/features/namespace-prefixes",
            false);

        parser.setErrorHandler(this);
        parser.setContentHandler(this);
        parser.setEntityResolver(this);

        firstEntity = true;
        foundDTD = false;
        parser.parse(new InputSource(input));
    }

}
