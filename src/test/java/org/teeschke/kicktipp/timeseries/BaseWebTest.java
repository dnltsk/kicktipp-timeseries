package org.teeschke.kicktipp.timeseries;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;

@SpringApplicationConfiguration(classes = {ApplicationConfiguration.class})
@WebIntegrationTest("server.port=0")
public abstract class BaseWebTest extends AbstractTestNGSpringContextTests {

	@Value("${local.server.port}")
	protected int port;

	protected String getRootUrl() {
		return "http://localhost:" + port;
	}

}
