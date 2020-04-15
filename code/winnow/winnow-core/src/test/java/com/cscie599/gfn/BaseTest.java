package com.cscie599.gfn;

import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author PulkitBhanot
 */
@RunWith(SpringRunner.class)
@ActiveProfiles("integrationtest")
@PostgresDataJpaTest
public abstract class BaseTest {
}
