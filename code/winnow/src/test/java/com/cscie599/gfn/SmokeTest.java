package com.cscie599.gfn;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import com.cscie599.gfn.controller.GeneController;
import com.cscie599.gfn.controller.UserController;
import com.cscie599.gfn.controller.JobLauncherController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SmokeTest {

    @Autowired
    private GeneController geneController;

    @Autowired
    private UserController userController;

    @Autowired
    private JobLauncherController jobLauncherController;

    @Test
    public void contexLoads() throws Exception {
        assertThat(geneController).isNotNull();
        assertThat(userController).isNotNull();
        assertThat(jobLauncherController).isNotNull();
    }
}