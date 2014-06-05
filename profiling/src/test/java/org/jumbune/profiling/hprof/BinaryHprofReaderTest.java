package org.jumbune.profiling.hprof;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.util.Map;

import org.jumbune.profiling.hprof.BinaryHprofReader;
import org.jumbune.profiling.hprof.CPUSamplesBean;
import org.jumbune.profiling.hprof.HeapAllocSitesBean;
import org.jumbune.profiling.hprof.HprofData;
import org.junit.Assert;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;



public class BinaryHprofReaderTest {

	public final TemporaryFolder testFolder1 = new TemporaryFolder();
	
	//@Test
	public void testBinaryHprofReader() throws Exception{
		final String TESTSTRING = "Jumbune Test";
		File testFile = testFolder1.newFile("testProf1.txt");
		FileOutputStream fios = new FileOutputStream(testFile);
		fios.write(TESTSTRING.getBytes());
		fios.close();
		BinaryHprofReader result = new BinaryHprofReader(new FileInputStream(testFile));
    	Assert.assertNull(result);
		//assertEquals(true, result.getStrict());
	}
	
	@Test
	public void testGetCPUSamples_1()
	throws Exception {
	BinaryHprofReader fixture = new BinaryHprofReader(new PipedInputStream());
	fixture.setStrict(true);
	CPUSamplesBean result = fixture.getCPUSamples();
	assertNotNull(result);
	assertEquals(0, result.getTotalSamplesCount());
	assertEquals("Total Samples Count:0\n---CPU Samples---\n", result.toString());
}
	@Test
	public void testGetHeapBean_1()
		throws Exception {
		BinaryHprofReader fixture = new BinaryHprofReader(new PipedInputStream());
		fixture.setStrict(true);

		HeapAllocSitesBean result = fixture.getHeapBean();

		assertNotNull(result);
		assertEquals(0, result.getNoOfSites());
		assertEquals(0.0f, result.getCutOffRatio(), 1.0f);
		assertEquals(0, result.getTotalLiveBytes());
		assertEquals(0, result.getTotalLiveInstances());
		assertEquals(null, result.getTotalByteAllocated());
		assertEquals(null, result.getTotalInstancesAllocated());
		assertEquals("Cut Off Ratio:0.0\ntotal live bytes:0\ntotal live instances:0\ntotal bytes allocated:null\ntotal instances allocated:null\nNo. of sites to follow:0\n\n---Sites to follow---\n\n", result.toString());
	}
	@Test
	public void testGetIdToStackTrace_1()
		throws Exception {
		BinaryHprofReader fixture = new BinaryHprofReader(new PipedInputStream());
		fixture.setStrict(true);

		Map<Integer, HprofData.StackTrace> result = fixture.getIdToStackTrace();

		assertNotNull(result);
		assertEquals(0, result.size());
	}
	@Test
	public void testGetStackFramesMap_1()
		throws Exception {
		BinaryHprofReader fixture = new BinaryHprofReader(new PipedInputStream());
		fixture.setStrict(true);

		Map<Integer, StackTraceElement> result = fixture.getStackFramesMap();

		assertNotNull(result);
		assertEquals(0, result.size());
	}
	@Test
	public void testGetStrict_1()
		throws Exception {
		BinaryHprofReader fixture = new BinaryHprofReader(new PipedInputStream());
		fixture.setStrict(true);

		boolean result = fixture.getStrict();
		assertEquals(true, result);
	}
	@Test
	public void testGetStrict_2()
		throws Exception {
		BinaryHprofReader fixture = new BinaryHprofReader(new PipedInputStream());
		fixture.setStrict(false);

		boolean result = fixture.getStrict();
		assertEquals(false, result);
	}
	@Test(expected = org.jumbune.profiling.hprof.MalformedHprofException.class)
	public void testRead_1()
		throws Exception {
		BinaryHprofReader fixture = new BinaryHprofReader(new PipedInputStream());
		fixture.setStrict(true);

		fixture.read();
	}
	@Test
	public void testSetStrict_1()
		throws Exception {
		BinaryHprofReader fixture = new BinaryHprofReader(new PipedInputStream());
		fixture.setStrict(true);
		boolean strict = true;

		fixture.setStrict(strict);
	}
}