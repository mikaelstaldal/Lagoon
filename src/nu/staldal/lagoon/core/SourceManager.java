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

package nu.staldal.lagoon.core;

import java.io.*;

/**
 * Defines operations a Producer can do on the source file tree.
 *
 * Filename parameters are specified as an relative URL,
 * i.e. using '/' to separate directories. If a filename
 * parameter starts with '/', it's searched for relative to the
 * source root directory, otherwise it's searched for relative to
 * the main source file (or a FileNotFoundException is thrown if
 * there is no main source file).
 */
public interface SourceManager
{
    /**
     * Get the current target URL.
     * Will return a pseudo-absolute URL string.
     * Will return the target URL <em>before</em> wildcard expansion.
     *
     * This method doesn't conceptually belong in this interface,
     * but I found no better place for it.
     */
    public String getTargetPath();


    /**
     * Get an absolute File object representing the source root directory.
     */
    public File getRootDir();


    /**
     * Get an absolute URL object representing the source root directory.
     */
    public java.net.URL getRootDirURL();


    /**
     * Open the main source file (if any).
     *
     * @return an InputStream to the file.
     *
     * @throw FileNotFoundException
     * if the main source file cannot be found, or is not specified,
     * or is a directory.
     */
    public InputStream openSource()
        throws FileNotFoundException;

    /**
     * Get a File object representing the main source file.
     * Might be a file or a directory.
     *
     * @throw FileNotFoundException
     * if the main source file is not specified.
     */
    public File getSource()
        throws FileNotFoundException;

    /**
     * Get a pseudo-absolute URL string representing the main source file.
     * Might be a file or a directory.
     *
     * @throw FileNotFoundException
     * if the main source file is not specified.
     */
    public String getSourcePath()
        throws FileNotFoundException;

    /**
     * Get an absolute URL representing the main source file.
     * Might be a file or a directory.
     *
     * @throw FileNotFoundException
     * if the main source file is not specified.
     */
    public java.net.URL getSourceURL()
        throws FileNotFoundException;

    /**
     * Check if the main source file has been updated since the specified time.
     *
     * @param when  the time
     *
     * @return false if the main source is not specified.
     */
    public boolean sourceHasBeenUpdated(long when);


    /**
     * Open an auxiallary source file.
     * This might e.g. be used to read the stylesheet for an
     * XSLT transformation.
     *
     * @param filename  the name of the file to open
     *
     * @return an InputStream to the file.
     */
    public InputStream openFile(String filename)
        throws FileNotFoundException;

    /**
     * Get a File object representing the given file or directory.
     *
     * @param filename  the name of the file to get
     */
    public File getFile(String filename)
        throws FileNotFoundException;

    /**
     * Get an pseudo-absolute URL string representing the given file or
     * directory.
     *
     * @param name  the name of the file to get
     */
    public String getFilePath(String name)
        throws FileNotFoundException;

    /**
     * Get an absolute URL representing the given file or directory.
     *
     * @param name  the name of the file to get
     */
    public java.net.URL getFileURL(String name)
        throws FileNotFoundException;

    /**
     * Check if the specified file has been updated since the specified time.
     *
     * @param filename  name of the file to check.
     * @param when  the time
     */
    public boolean fileHasBeenUpdated(String filename, long when)
        throws FileNotFoundException;


    /**
     * Get an pseudo-absolute URL string representing the given file or
     * directory. If name is relative, it will be searched for realtive to
     * the base parameter (and not the main source file). The return value
     * can be used as argument to openFile(String), getFile(String) or
     * fileHasBeenUpdated(String,long).
     *
     * @param name  the name of the file to get
     * @param base  an pseudo-absolute URL string
     */
    public String getFilePathRelativeTo(String name, String base);
}
