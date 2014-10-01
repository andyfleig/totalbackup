package test;

import main.Controller;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

public class BackupTest {
	
	static File dir;
	static File dir1;
	static File dir2;
	static File dir3;
	static File testFile1;
	static File testFile2;
	static File testFile3;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dir = new File("/home/andy/dir");
		dir.mkdir();
		dir1 = new File("/home/andy/dir/dir1");
		dir1.mkdir();
		dir2 = new File("/home/andy/dir/dir2");
		dir2.mkdir();
		dir3 = new File("/home/andy/dir/dir3");
		dir3.mkdir();
		
		testFile1 = new File("/home/andy/dir/dir1/testFile1");
		//testFile1.mkdirs();
		testFile1.createNewFile();
		testFile2 = new File("/home/andy/dir/dir1/testFile2");
		//testFile2.mkdirs();
		testFile2.createNewFile();
		testFile3 = new File("/home/andy/dir/dir2/testFile3");
		//testFile3.mkdirs();
		testFile3.createNewFile();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		/*
		testFile1.delete();
		testFile2.delete();
		testFile3.delete();
		dir1.delete();
		dir2.delete();
		dir3.delete();
		*/
	}

	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRunBackup() {
		Controller c = new Controller();
		c.startController();

		//c.getMainframe()
		File f = new File ("/dir");
		assertEquals(f.list().length, 4);
		
		c = null;
	}
}
