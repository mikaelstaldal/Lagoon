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

package nu.staldal.lagoon.filestorage;

import nu.staldal.lagoon.core.*;

import java.io.*;
import java.util.Hashtable;

/**
 * A FileStorage which transfers files to a remote site using SSH.
 */
public abstract class RemoteFileStorage implements FileStorage
{
    private static final boolean DEBUG = false;
    
    private LagoonProcessor processor;
    private Hashtable lastModTable;

    protected void openDateFile(LagoonProcessor processor)
        throws IOException
    {
        if (DEBUG) System.out.println("RemoteFileStorage.init()");
        
        this.processor = processor;
		try
		{
			lastModTable =
                (Hashtable)processor.getObjectFromRepository(
                    "nu.staldal.lagoon.filestorage.RemoteFileStorage");
            if (lastModTable == null) lastModTable = new Hashtable();
		}
		catch (ClassCastException e)
		{
			throw new IOException("Date file is corrupt");
		}
    }

    protected void closeDateFile()
        throws IOException
    {
		processor.putObjectIntoRepository(
            "nu.staldal.lagoon.filestorage.RemoteFileStorage", lastModTable);
    }

    protected void fileModified(String pathname)
    {
        lastModTable.put(pathname, new Long(System.currentTimeMillis()));
    }

    /**
     * Check if a file exists and when it was last modified.
     *
     * @param pathname  path to the file
     *
     * @return  the time when the file was last modified,
     * or -1 if that information is not avaliable.
     */
    public long fileLastModified(String pathname)
        // throws java.io.IOException
    {
		Long l = (Long)lastModTable.get(pathname);
		if (l == null)
			return -1;
		else
			return l.longValue();
    }
}
