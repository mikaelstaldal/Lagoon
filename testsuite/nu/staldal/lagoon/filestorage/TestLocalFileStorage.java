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

       OutputStream os = fs.createFile("/create/this/file");
       byte[] ba = {45,46,47};
       os.write(ba);
       os.close();
       fs.commitFile();
       assert((new File("localFileStorageTest/create/this/file")).isFile());
       assert(fs.fileLastModified("/create/this/file") > 0);

       OutputStream os2 = fs.createFile("/discard/this/file");
       os2.write(ba);
       os2.close();
       fs.discardFile();
       assert(!(new File("localFileStorageTest/discard/this/file")).exists());

       fs.deleteFile("/create/this/file");
       assert(!(new File("localFileStorageTest/create/this/file")).exists());
       assert(fs.fileLastModified("/create/this/file") <= 0);
    }
}
