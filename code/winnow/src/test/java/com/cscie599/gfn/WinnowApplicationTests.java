package com.cscie599.gfn;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureEmbeddedDatabase
class WinnowApplicationTests {

	@Test
	void contextLoads() {
	}

}
