package org.openecomp.sdc.be.model;

import org.junit.Test;
import org.openecomp.sdc.be.datatypes.elements.CapabilityTypeDataDefinition;

import java.util.Map;


public class CapabilityTypeDefinitionTest {

	private CapabilityTypeDefinition createTestSubject() {
		return new CapabilityTypeDefinition();
	}

	@Test
	public void testCtor() throws Exception {
		new CapabilityTypeDefinition(new CapabilityTypeDataDefinition());
	}
	
	@Test
	public void testGetDerivedFrom() throws Exception {
		CapabilityTypeDefinition testSubject;
		String result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getDerivedFrom();
	}

	
	@Test
	public void testSetDerivedFrom() throws Exception {
		CapabilityTypeDefinition testSubject;
		String derivedFrom = "";

		// default test
		testSubject = createTestSubject();
		testSubject.setDerivedFrom(derivedFrom);
	}

	
	@Test
	public void testGetProperties() throws Exception {
		CapabilityTypeDefinition testSubject;
		Map<String, PropertyDefinition> result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getProperties();
	}

	
	@Test
	public void testSetProperties() throws Exception {
		CapabilityTypeDefinition testSubject;
		Map<String, PropertyDefinition> properties = null;

		// default test
		testSubject = createTestSubject();
		testSubject.setProperties(properties);
	}

	
	@Test
	public void testToString() throws Exception {
		CapabilityTypeDefinition testSubject;
		String result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.toString();
	}
}