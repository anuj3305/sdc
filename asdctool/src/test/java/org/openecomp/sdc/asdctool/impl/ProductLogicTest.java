package org.openecomp.sdc.asdctool.impl;

import static org.junit.Assert.assertFalse;
import org.junit.Test;

public class ProductLogicTest {

	private ProductLogic createTestSubject() {
		return new ProductLogic();
	}

	@Test
	public void testDeleteAllProducts() throws Exception {
		ProductLogic testSubject;
		String titanFile = "";
		String beHost = "";
		String bePort = "";
		String adminUser = "";
		boolean result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.deleteAllProducts(titanFile, beHost, bePort, adminUser);
		assertFalse(result);
	}
}
