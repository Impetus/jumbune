package org.jumbune.profiling.hprof;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jumbune.profiling.hprof.BinaryHprof;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BinaryHprofTest {

	@Rule
	public final TemporaryFolder testFolder = new TemporaryFolder();

	@Test
	public void testReadMagicEndsWithComma() throws IOException {
		final String COMMATESTSTRING = "JAVA PROFILE Hello,";
		File testFile = testFolder.newFile("testProf.txt");
		FileOutputStream fios = new FileOutputStream(testFile);
		fios.write(COMMATESTSTRING.getBytes());
		fios.close();
		DataInputStream dis = new DataInputStream(new FileInputStream(testFile));
		String result = BinaryHprof.readMagic(dis);
		Assert.assertTrue(COMMATESTSTRING.substring(0, COMMATESTSTRING.lastIndexOf(",")).equals(result));
	}

	@Test
	public void testReadMagicEndswithBackslash() throws IOException {
		final String SLASHSSTRIG = new StringBuilder().append("JAVA PROFILE HELLO").append("\0").toString();
		File testFile = testFolder.newFile("testProf.txt");
		FileOutputStream fios = new FileOutputStream(testFile);
		fios.write(SLASHSSTRIG.getBytes());
		fios.close();
		DataInputStream dis = new DataInputStream(new FileInputStream(testFile));
		String result = BinaryHprof.readMagic(dis);
		Assert.assertTrue(SLASHSSTRIG.substring(0, SLASHSSTRIG.lastIndexOf("\0")).equals(result));

	}

	@Test
	public void testInvalidReadMagic() throws IOException {
		final String STRINGCONSTANT = "gfgsfgsggdsg";
		File testFile = testFolder.newFile("testProf.txt");
		FileOutputStream fios = new FileOutputStream(testFile);
		fios.write(STRINGCONSTANT.getBytes());
		fios.close();
		DataInputStream dis = new DataInputStream(new FileInputStream(testFile));
		String result = BinaryHprof.readMagic(dis);
		Assert.assertNull(result);
	}

	@Test
	public void testInvalidStringBeginReadMagic() throws IOException {
		final String STRINGCONSTANT = "HELLO,";
		File testFile = testFolder.newFile("testProf.txt");
		FileOutputStream fios = new FileOutputStream(testFile);
		fios.write(STRINGCONSTANT.getBytes());
		fios.close();
		DataInputStream dis = new DataInputStream(new FileInputStream(testFile));
		String result = BinaryHprof.readMagic(dis);
		Assert.assertNull(result);
	}
}
