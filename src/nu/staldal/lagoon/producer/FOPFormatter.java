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

package nu.staldal.lagoon.producer;

import java.io.*;

import org.xml.sax.*;
import org.apache.fop.apps.*;
import org.apache.fop.messaging.MessageHandler;

import nu.staldal.lagoon.core.*;

/**
 * Uses Apache FOP version 0.17.
 */
public class FOPFormatter extends Format
{
    public void init()
    {
        // nothing to do
    }

    public void start(OutputStream out, Target target)
        throws IOException, SAXException
    {
		MessageHandler.setOutputMethod(MessageHandler.NONE);

		Driver driver = new Driver();
		driver.setRenderer("org.apache.fop.render.pdf.PDFRenderer",
						   Version.getVersion());
		driver.addElementMapping("org.apache.fop.fo.StandardElementMapping");
		driver.addElementMapping("org.apache.fop.svg.SVGElementMapping");
        driver.addPropertyList("org.apache.fop.fo.StandardPropertyListMapping");
        driver.addPropertyList("org.apache.fop.svg.SVGPropertyListMapping");
		driver.setOutputStream(out);

		getNext().start(driver.getContentHandler(), target);

		try {
			driver.format();
    		driver.render();
		}
		catch (FOPException e)
		{
			throw new SAXException(e);
		}
	}

    public boolean hasBeenUpdated(long when)
        throws LagoonException, IOException
    {
        return getNext().hasBeenUpdated(when);
    }

}
