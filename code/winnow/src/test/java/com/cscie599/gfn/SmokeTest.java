package com.cscie599.gfn;
import static org.assertj.core.api.Assertions.assertThat;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import com.cscie599.gfn.controller.GeneController;
import com.cscie599.gfn.controller.UserController;
import com.cscie599.gfn.controller.JobLauncherController;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

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

    @Test
    public void testContexLoads() throws Exception {
        assertThat(geneController).isNotNull();
        assertThat(userController).isNotNull();
        assertThat(jobLauncherController).isNotNull();
    }
}


