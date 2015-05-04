/*package org.jumbune.common.utils;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.jumbune.common.beans.Enable;
import org.jumbune.common.config.JobConfig;
import org.jumbune.common.utils.ValidateInput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
public class ValidateInputTest {

	ValidateInput fixture;
	JobConfig config;
	
	@Before
	public void setUp() throws Exception {
	
	fixture = new ValidateInput();
	config = Mockito.mock(JobConfig.class);
	}

	@Test
	public void testValidateJson() {
		Enable enable = Enable.TRUE;
		when(config.getEnableDataValidation()).thenReturn(enable);
		assertNotNull(fixture.validateJson(config));
		
			
	}

	
}*/
