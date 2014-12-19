package test;

import main.BackupTask;
import main.Controller;
import main.NormalBackup;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.sun.corba.se.spi.orbutil.fsm.Input;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class BackupTest {

	static String testFolder = "/home/andy/TotalBackup/test";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNormalBackup1() {
		BackupTask task = new BackupTask("test");
		task.addSourcePath(testFolder + "/source");
		task.setDestinationPath(testFolder + "/dest");

		Controller c = new Controller();
		c.startController();
		c.startBackup(task);

		// Kopierte Dateien prüfen:
		File log = new File(testFolder + "/dest/test.log");
		log.delete();

		File dest = new File(testFolder + "/dest");
		String[] filesInDest = dest.list();
		if (filesInDest.length != 1) {
			System.out.println("FEHLER!");
		}
		File testFile = new File(testFolder + "/dest/" + filesInDest[0] + "/source/testFile");

		File original = new File(testFolder + "/source/testFile");
		String originalHash;
		String backupHash;
		try {
			originalHash = calcMD5(original);
		} catch (Exception e) {
			return;
		}
		try {
			backupHash = calcMD5(testFile);
		} catch (Exception e) {
			return;
		}
		
		System.out.println(originalHash);
		System.out.println(backupHash);
		
		assertTrue(originalHash.equals(backupHash));

		// Backup löschen:
		testFile.delete();
		(new File(testFolder + "/dest/" + filesInDest[0] + "/source")).delete();
		(new File(testFolder + "/dest/" + filesInDest[0])).delete();
	}

	private String calcMD5(File f) throws Exception {
		MessageDigest md;

		md = MessageDigest.getInstance("MD5");

		FileInputStream fis;

		fis = new FileInputStream(f.getAbsoluteFile());

		byte[] dataBytes = new byte[1024];

		int nread = 0;
		while ((nread = fis.read(dataBytes)) != -1) {
			md.update(dataBytes, 0, nread);
		}
		;
		byte[] mdbytes = md.digest();

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mdbytes.length; i++) {
			sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
		}
		return sb.toString();
	}

}
