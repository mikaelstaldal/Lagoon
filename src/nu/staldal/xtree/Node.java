/*
 * Copyright (c) 2001, Mikael St�ldal
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

package nu.staldal.xtree;

import org.xml.sax.*;

public abstract class Node implements java.io.Serializable
{
    String systemId;
    int line;
    int column;
    NodeWithChildren parent = null;

    void setParent(NodeWithChildren n)
    {
        parent = n;
    }

    public NodeWithChildren getParent()
    {
        return parent;
    }

	public abstract void toSAX(ContentHandler sax)
		throws SAXException;


    /**
     * Return the system identifier for this node.
     *
     * <p>The return value is the system identifier of the document
     * entity or of the external parsed entity.</p>
     *
     * @return A string containing the system identifier, or null
     *         if none is available.
     */
    public String getSystemId()
    {
    	return systemId;
	}


    /**
     * Return the line number where this node ends.
     *
     * <p>The return value is an approximation of the line number
     * in the document entity or external parsed entity.</p>
     *
	 * <p>The first line in the document is line 1.</p>
     *
     * @return The line number, or -1 if none is available.
     * @see #getColumnNumber
     */
    public int getLineNumber()
    {
		return line;
	}


    /**
     * Return the column number where this node ends.
     *
     * <p>The return value is an approximation of the column number
     * in the document entity or external parsed entity.</p>
     *
	 * <p>The first column in each line is column 1.</p>
     *
     * @return The column number, or -1 if none is available.
     * @see #getLineNumber
     */
    public int getColumnNumber()
    {
		return column;
	}

}
