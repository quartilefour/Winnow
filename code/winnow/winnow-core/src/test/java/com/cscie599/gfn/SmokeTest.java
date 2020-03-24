package com.cscie599.gfn;
import static org.assertj.core.api.Assertions.assertThat;

import com.cscie599.gfn.controller.*;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureEmbeddedDatabase
public class SmokeTest {

    @Autowired
    private GeneController geneController;

    @Autowired
    private UserController userController;

    @Autowired
    private JobLauncherController jobLauncherController;

    @Autowired
    private MeshtermController meshtermController;

    @Autowired
    private StatusController statusController;

    @Test
    public void testContexLoads() throws Exception {
        assertThat(geneController).isNotNull();
        assertThat(userController).isNotNull();
        assertThat(jobLauncherController).isNotNull();
        assertThat(meshtermController).isNotNull();
        assertThat(statusController).isNotNull();
    }
}


