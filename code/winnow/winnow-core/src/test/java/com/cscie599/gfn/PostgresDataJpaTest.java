package com.cscie599.gfn;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.*;

/**
 * @author PulkitBhanot
 */
@Documented
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@AutoConfigureEmbeddedDatabase
@AutoConfigureTestDatabase
@TestPropertySource(properties = "flyway.schemas=gfn")
public @interface PostgresDataJpaTest {
}
