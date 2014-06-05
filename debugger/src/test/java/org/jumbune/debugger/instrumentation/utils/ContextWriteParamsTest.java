package org.jumbune.debugger.instrumentation.utils;

import org.jumbune.debugger.instrumentation.utils.ContextWriteParams;
import org.junit.*;


import static org.junit.Assert.*;

public class ContextWriteParamsTest {
	private ContextWriteParams fixtureInstance; 

	public ContextWriteParams getFixtureInstance() throws Exception {
		return fixtureInstance;
	}

	@Test
	public void testGetInstance_1() throws Exception {

		ContextWriteParams result = ContextWriteParams.getInstance();

		assertNotNull(result);
	}

	@Test
	public void testGetTempValueVariableIndex_fixtureInstance_1()
			throws Exception {
		ContextWriteParams fixture = getFixtureInstance();

		int result = fixture.getTempValueVariableIndex();

		assertNotNull(result);
	}

	@Test
	public void testSetTempKeyVariableIndex_fixtureInstance_1()
			throws Exception {
		ContextWriteParams fixture = getFixtureInstance();
		int tempKeyIndex = 0;

		fixture.setTempKeyVariableIndex(tempKeyIndex);

	}

	@Test
	public void testSetTempKeyVariableIndex_fixtureInstance_2()
			throws Exception {
		ContextWriteParams fixture = getFixtureInstance();
		int tempKeyIndex = 1;

		fixture.setTempKeyVariableIndex(tempKeyIndex);

	}

	@Test
	public void testSetTempKeyVariableIndex_fixtureInstance_3()
			throws Exception {
		ContextWriteParams fixture = getFixtureInstance();
		int tempKeyIndex = 7;

		fixture.setTempKeyVariableIndex(tempKeyIndex);

	}

	@Test
	public void testSetTempValueVariableIndex_fixtureInstance_1()
			throws Exception {
		ContextWriteParams fixture = getFixtureInstance();
		int tempValueIndex = 0;

		fixture.setTempValueVariableIndex(tempValueIndex);

	}

	@Test
	public void testSetTempValueVariableIndex_fixtureInstance_2()
			throws Exception {
		ContextWriteParams fixture = getFixtureInstance();
		int tempValueIndex = 1;

		fixture.setTempValueVariableIndex(tempValueIndex);

	}

	@Test
	public void testSetTempValueVariableIndex_fixtureInstance_3()
			throws Exception {
		ContextWriteParams fixture = getFixtureInstance();
		int tempValueIndex = 7;

		fixture.setTempValueVariableIndex(tempValueIndex);

	}

	@Before
	public void setUp() throws Exception {
		fixtureInstance = ContextWriteParams
				.getInstance();
	}

	@After
	public void tearDown() throws Exception {
	}
}