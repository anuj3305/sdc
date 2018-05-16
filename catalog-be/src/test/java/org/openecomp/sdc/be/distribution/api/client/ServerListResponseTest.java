package org.openecomp.sdc.be.distribution.api.client;

import java.util.List;

import org.junit.Test;

public class ServerListResponseTest {

	private ServerListResponse createTestSubject() {
		return new ServerListResponse();
	}

	@Test
	public void testGetUebServerList() throws Exception {
		ServerListResponse testSubject;
		List<String> result;

		// default test
		testSubject = createTestSubject();
		result = testSubject.getUebServerList();
	}

	@Test
	public void testSetUebServerList() throws Exception {
		ServerListResponse testSubject;
		List<String> uebServerList = null;

		// default test
		testSubject = createTestSubject();
		testSubject.setUebServerList(uebServerList);
	}
}