package nu.staldal.lagoon.filestorage;

import nu.staldal.lagoon.core.*;

import java.io.*;

import junit.framework.*;

public class TestLocalFileStorage extends TestCase
{
    public TestLocalFileStorage(String name)
    {
        super(name);
    }

    private FileStorage fs;


    protected void setUp() throws Exception
    {
        fs = new LocalFileStorage();
        fs.open("localFileStorageTest", null, null); // ***
    }

    protected void tearDown() throws Exception
    {
        fs.close();
    }

    public void testLocalFileStorage() throws Exception
    {

       OutputHandler oh = fs.createFile("/create/this/file");
       byte[] ba = {45,46,47};
       oh.getOutputStream().write(ba);
       oh.commit();
       assert((new File("localFileStorageTest/create/this/file")).isFile());
       assert(fs.fileLastModified("/create/this/file") > 0);

       OutputHandler oh2 = fs.createFile("/discard/this/file");
       oh2.getOutputStream().write(ba);
       oh2.discard();
       assert(!(new File("localFileStorageTest/discard/this/file")).exists());

       fs.deleteFile("/create/this/file");
       assert(!(new File("localFileStorageTest/create/this/file")).exists());
       assert(fs.fileLastModified("/create/this/file") <= 0);
    }
}
