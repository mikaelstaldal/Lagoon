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
import java.net.*;

/**
 * A FileStorage which transfers files to a remote site using FTP.
 *
 * <strong>Note:</strong> This class will transmit password in clear text over
 * the network.
 */
public class FTPFileStorage extends RemoteFileStorage
{
	private static final boolean DEBUG = false;

    private Socket control;
    private Socket data = null;
    private InputStream controlIn;
    private OutputStream controlOut;
    private String respString;
    private String lastPath = "";
    private int lastPathLen = 0;
    private String filename;
    private String currentPathname;


	private void sendLine(String str)
		throws IOException
	{
		controlOut.write(str.getBytes("US-ASCII"));
		controlOut.write('\r');
		controlOut.write('\n');
		controlOut.flush();
	}

	private String recvLine()
		throws EOFException, IOException
	{
		StringBuffer sb = new StringBuffer();

		while (true)
		{
			int i = controlIn.read();
			if (i < 0) throw new EOFException("Unexpected EOF when reading socket");
			byte b = (byte)i;
			if (b == '\n') break;
			if (b != '\r') sb.append((char)b);
		}

		return sb.toString();
	}

	private int recvResponse()
		throws EOFException, IOException
	{
		respString = recvLine();
		String code = respString.substring(0,3);
		if (respString.charAt(3) == '-') // multiline response
		{
			String endMark = code + ' ';
			while (true)
			{
				respString = recvLine();
				if (respString.startsWith(endMark))
					break;
			}
		}
		return Integer.parseInt(code);
	}

	private boolean chdir(String dir)
		throws FTPException, IOException
	{
		if (DEBUG) System.out.println("CWD " + dir);
		sendLine("CWD " + dir);
		int resp = recvResponse();
		switch (resp)
		{
			case 250:
				return true;

			case 550:
				return false;

			case 421:
				throw new FTPException("FTP server not avaliable (421)");

			default:
				throw new FTPException("Unexpected response from FTP server: " + respString);
		}
	}

	private boolean cdup()
		throws FTPException, IOException
	{
		if (DEBUG) System.out.println("CDUP");
		sendLine("CDUP");
		int resp = recvResponse();
		switch (resp)
		{
			case 200:
			case 250:
				return true;

			case 550:
				return false;

			case 421:
				throw new FTPException("FTP server not avaliable (421)");

			default:
				throw new FTPException("Unexpected response from FTP server: " + respString);
		}
	}

	private boolean mkdir(String dir)
		throws FTPException, IOException
	{
		if (DEBUG) System.out.println("MKD " + dir);
		sendLine("MKD " + dir);
		int resp = recvResponse();
		switch (resp)
		{
			case 257:
				return true;

			case 550:
				return false;

			case 421:
				throw new FTPException("FTP server not avaliable (421)");

			default:
				throw new FTPException("Unexpected response from FTP server: " + respString);
		}
	}

    /**
     * Default constructor.
     */
    public FTPFileStorage()
    {
        control = null;
    }

    public boolean needPassword()
    {
        return true;
    }


    public void open(String url, LagoonProcessor processor, String password)
        throws MalformedURLException, UnknownHostException,
        FTPException, IOException, AuthenticationException
    {
		if (!url.startsWith("ftp://"))
			throw new MalformedURLException(url);

		int userPos = 5;
		int hostPos = url.indexOf('@', userPos+1);
		if (hostPos < 0)
			throw new MalformedURLException(url);
		int portPos = url.indexOf(':', hostPos+1);
		int pathPos = url.indexOf('/', ((portPos<0) ? hostPos : portPos)+1);

		String username;
		String host;
		int port;
		String path;

		try
		{
			username = url.substring(userPos+1,hostPos);
			host = url.substring(hostPos+1,(portPos<0) ? pathPos : portPos);
			port = (portPos<0) ? 21 : Integer.parseInt(url.substring(portPos+1,pathPos));
			path = url.substring(pathPos+1);
		}
		catch (NumberFormatException e)
		{
			throw new MalformedURLException(url);
		}

		if (port < 0 || port > 65535 ||
			username.length() < 1 ||
			host.length() < 1 ||
			(path.length() > 0 && path.charAt(path.length()-1) != '/'))
			throw new MalformedURLException(url);

		control = new Socket(host, port);
		controlIn = control.getInputStream();
		controlOut = control.getOutputStream();

		int resp;

		// Receive greeting message
		greeting: while (true)
		{
			resp = recvResponse();
			switch (resp)
			{
				case 120:
					continue greeting;

				case 220:
					break greeting;

				case 421:
					throw new FTPException("FTP server not avaliable (421)");

				default:
					throw new FTPException("Unexpected response from FTP server: " + respString);
			}
		}

		sendLine("USER " + username);
		resp = recvResponse();
		switch (resp)
		{
			case 230:
				break;

			case 331:
				sendLine("PASS " + password);
				resp = recvResponse();
				switch (resp)
				{
					case 230:
						break;

					case 530:
                        throw new AuthenticationException();

					case 421:
						throw new FTPException("FTP server not avaliable (421)");

					default:
						throw new FTPException("Unexpected response from FTP server: " + respString);
				}

				break;

			case 530:
				throw new FTPException("Invalid username");

			case 421:
				throw new FTPException("FTP server not avaliable (421)");

			default:
				throw new FTPException("Unexpected response from FTP server: " + respString);
		}

		// change directory
		int pos, oldPos = 0;
        while (true)
        {
            pos = path.indexOf('/', oldPos);
            if (pos < 0) break;
            String comp = path.substring(oldPos, pos);
            if (!chdir(comp))
				throw new FTPException("Path not found: " + path);
            oldPos = pos + 1;
        }

        filename = null;

        openDateFile(processor);
    }


    /**
     * Close the file system and release any resources it holds.
     *
     * After this method has been invoked, no other method may be invoked.
     */
    public void close()
    	throws IOException
    {
        closeDateFile();

		sendLine("QUIT");
		int resp = recvResponse();
		control.close();
        control = null;
    }

    /**
     * Create a new file, or overwrite an existing file.
     * Use close() on the returned OutputStream when finished
     * writing to the file, and then commitFile().
     *
     * @param pathname  path to the file
     *
     * @return an OutputStream to write to
     */
    public OutputStream createFile(String pathname)
        throws IOException
    {
        currentPathname = pathname;
		int resp;
		String path;
		int pos = pathname.lastIndexOf('/');
	    path = pathname.substring(0, pos+1);
	    filename = pathname.substring(pos+1);

		if (!path.equals(lastPath))
		{
			// change directory
			for (int i = 0; i < lastPathLen; i++)
				if (!cdup())
					throw new FTPException("Unable to change to parent directory");

			lastPathLen = 0;
			int oldPos = 1;
			boolean mkd = false;
			while (true)
			{
				pos = path.indexOf('/', oldPos);
				if (pos < 0) break;
				lastPathLen++;
				String comp = path.substring(oldPos, pos);
				if (mkd)
				{
					if (!mkdir(comp))
						throw new FTPException("Unable to create directory: " + comp);
					if (!chdir(comp))
						throw new FTPException("Unable to change into newly created directory: " + comp);
				}
				else
				{
					if (!chdir(comp))
					{
						if (!mkdir(comp))
							throw new FTPException("Unable to create directory: " + comp);
						if (!chdir(comp))
							throw new FTPException("Unable to change into newly created directory: " + comp);
						mkd = true;
					}
				}
				oldPos = pos + 1;
			}
		}
		lastPath = path;

		sendLine("TYPE I");
		resp = recvResponse();
		switch (resp)
		{
			case 200:
				break;

			case 421:
				throw new FTPException("FTP server not avaliable (421)");

			default:
				throw new FTPException("Unexpected response from FTP server: " + respString);
		}

		sendLine("PASV");
		resp = recvResponse();
		switch (resp)
		{
			case 227:
				break;

			case 421:
				throw new FTPException("FTP server not avaliable (421)");

			default:
				throw new FTPException("Unexpected response from FTP server: " + respString);
		}

		InetAddress addr;
		int port;
		String s = respString.replace(',', '.');
		int i = 3;

		while (i < s.length() && !Character.isDigit(s.charAt(i))) i++;
		if (i == s.length()) throw new FTPException("invalid response to PASV command");
		int c1 = s.indexOf('.',i);
		if (c1 < 0) throw new FTPException("invalid response to PASV command");
		int c2 = s.indexOf('.',c1+2);
		if (c2 < 0) throw new FTPException("invalid response to PASV command");
		int c3 = s.indexOf('.',c2+1);
		if (c3 < 0) throw new FTPException("invalid response to PASV command");
		int c4 = s.indexOf('.',c3+1);
		if (c4 < 0) throw new FTPException("invalid response to PASV command");
		int c5 = s.indexOf('.',c4+1);
		if (c5 < 0) throw new FTPException("invalid response to PASV command");
		try {
			addr = InetAddress.getByName(s.substring(i,c4));

			i = c5+1;
			while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
			int portA = Integer.parseInt(s.substring(c4+1,c5));
			int portB = Integer.parseInt(s.substring(c5+1,i));
			port = (portA<<8) + portB;
		}
		catch (UnknownHostException e)
		{
			throw new FTPException("invalid response to PASV command");
		}
		catch (NumberFormatException e)
		{
			throw new FTPException("invalid response to PASV command");
		}

		sendLine("STOR " + filename);

		data = new Socket(addr, port);
		return data.getOutputStream();
    }

    /**
     * Finishing writing to a file and commits it.
     * Must be invoked when finished writing to the OutputStream
     * createFile has returned.
     *
     * @see #createFile
     */
	public void commitFile()
		throws IOException
	{
        if (currentPathname == null)
            throw new IllegalStateException("No file to commit");

        if (data != null)
		{
			data.close();
			data = null;
		}

        theLoop: while (true)
        {
            int resp = recvResponse();
            switch (resp)
            {
                case 125:
                case 150:
                    //commitFile();
                    //break;
                    continue theLoop;

                case 226:
                case 250:
                    break;

                case 425:
                case 426:
                case 451:
                case 551:
                case 552:
                    throw new FTPException("Error in file transfer (" + resp + ")");

                case 421:
                    throw new FTPException("FTP server not avaliable (421)");

                default:
                    throw new FTPException("Unexpected response from FTP server: " + respString);
            }
            break;
        }

        fileModified(currentPathname);

		filename = null;
        currentPathname = null;
	}


    /**
     * Discards a new file and delete it.
     *
     * @see #createFile
     */
    public void discardFile()
        throws IOException
    {
        if (currentPathname == null)
            throw new IllegalStateException("No file to discard");

		String fn = filename;
		try {
			commitFile();
		}
		catch (FTPException e)
		{
			// ignore exception
		}

		sendLine("DELE " + fn);
		int resp = recvResponse();
		switch (resp)
		{
			case 250:
			case 550:
				break;

			case 450:
				throw new FTPException("Unable to delete file: " + respString);

			case 421:
				throw new FTPException("FTP server not avaliable (421)");

			default:
				throw new FTPException("Unexpected response from FTP server: " + respString);
		}

		filename = null;
        currentPathname = null;
	}


    /**
     * Deletes a file.
     * Does not signal any error if the file doesn't exist.
	 *
     * @param pathname  path to the file
     */
    public void deleteFile(String pathname)
        throws java.io.IOException
    {
        currentPathname = pathname;
		String path;
		String fn;
		int pos = pathname.lastIndexOf('/');
		path = pathname.substring(0, pos+1);
		fn = pathname.substring(pos+1);

		if (!path.equals(lastPath))
		{
			// change directory
			for (int i = 0; i < lastPathLen; i++)
				if (!cdup())
					throw new FTPException("Unable to change to parent directory");

			lastPathLen = 0;
			int oldPos = 1;
			while (true)
			{
				pos = path.indexOf('/', oldPos);
				if (pos < 0) break;
				lastPathLen++;
				String comp = path.substring(oldPos, pos);
				if (!chdir(comp))
				{
					return; // file doesn't exist
				}
				oldPos = pos + 1;
			}
		}
		lastPath = path;

		sendLine("DELE " + fn);
		int resp = recvResponse();
		switch (resp)
		{
			case 250:
			case 550:
				break;

			case 450:
				throw new FTPException("Unable to delete file: " + respString);

			case 421:
				throw new FTPException("FTP server not avaliable (421)");

			default:
				throw new FTPException("Unexpected response from FTP server: " + respString);
		}
	}

}
