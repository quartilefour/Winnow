package com.cscie599.gfn;
import static org.assertj.core.api.Assertions.assertThat;

import com.cscie599.gfn.controller.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmokeTest extends BaseTest{

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
    public void testContextLoads() throws Exception {
        assertThat(geneController).isNotNull();
        assertThat(userController).isNotNull();
        assertThat(jobLauncherController).isNotNull();
        assertThat(meshtermController).isNotNull();
        assertThat(statusController).isNotNull();
    }
}


