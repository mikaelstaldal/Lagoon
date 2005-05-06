/*
 * Copyright (c) 2005, Mikael Ståldal
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

package nu.staldal.xodus;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;

import org.xml.sax.*;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.DeclHandler;

import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;


/**
 * Serialize SAX2 events to its textual XML representation.
 */
public abstract class Serializer implements ContentHandler, LexicalHandler, 
                                            DTDHandler, DeclHandler
{
    protected final OutputConfig outputConfig; 
    protected final String systemId;
    protected final XMLCharacterEncoder out;
    
    private final boolean doClose;    
    
    
    /**
     * Factory method.
     */
    public static Serializer createSerializer(
            StreamResult result, Properties outputProperties)
        throws IllegalArgumentException, IOException, 
            UnsupportedEncodingException
    {
        OutputConfig outputConfig = 
            OutputConfig.createOutputConfig(outputProperties); 
        
        if (outputConfig.isHtml)
            throw new IllegalArgumentException("HTML output is not supported");
        else if (outputConfig.isXhtml)
            return new XMLSerializer(result, outputConfig);
        else if (outputConfig.isText)
            return new TextSerializer(result, outputConfig);
        else // XML
            return new XMLSerializer(result, outputConfig);
    }

    
    protected Serializer(StreamResult result, OutputConfig outputConfig)
        throws IllegalArgumentException, IOException, 
            UnsupportedEncodingException
    {
        this.outputConfig = outputConfig;
        
        if (!outputConfig.cdata_section_elements.isEmpty())
        {
            throw new IllegalArgumentException(
                "cdata_section_elements is not supported");
        }

        this.systemId = result.getSystemId();
        OutputStream os = result.getOutputStream();
        Writer w = result.getWriter();
        if (os != null)
        {
            out = new XMLCharacterEncoder(os, outputConfig.encoding);
            doClose = false;
        }
        else if (w != null)
        {
            out = new XMLCharacterEncoder(w);
            doClose = false;
        }
        else if (systemId != null)
        {
            URL url = new URL(systemId);
            URLConnection urlConn = url.openConnection();
            OutputStream _os = urlConn.getOutputStream();
            os = new BufferedOutputStream(_os); 
            out = new XMLCharacterEncoder(os, outputConfig.encoding);
            doClose = true;
        }
        else
        {
            throw new IllegalArgumentException("Empty StreamResult");     
        }
    }
    
    
    /**
     * Finish writing to output. Does <em>not</em> close output if
     * an {@link java.io.OutputStream} or {@link Writer} was provided.
     */
    protected void finishOutput()
        throws IOException
    {
        out.finish();
        if (doClose) out.close();        
    }
    

    /**
     * Write a newline.
     */
    protected void newline()
        throws IOException
    {
        out.write('\n');    
    }

}

