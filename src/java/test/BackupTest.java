package test;

import main.BackupHelper;
import main.BackupTask;
import main.Controller;
import main.Source;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;

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
	public void testNormalBackupSingleFile() {
		BackupTask task = new BackupTask("test");
		task.addSourcePath(new Source(testFolder + "/source"));
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
			originalHash = BackupHelper.calcMD5(original);
		} catch (Exception e) {
			return;
		}
		try {
			backupHash =  BackupHelper.calcMD5(testFile);
		} catch (Exception e) {
			return;
		}
		
		assertTrue(originalHash.equals(backupHash));

		// Backup löschen:
		testFile.delete();
		(new File(testFolder + "/dest/" + filesInDest[0] + "/source")).delete();
		(new File(testFolder + "/dest/" + filesInDest[0])).delete();
	}

}
