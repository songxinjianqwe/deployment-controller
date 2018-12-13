package com.jasper.deploymentcontroller;

import com.jasper.deploymentcontroller.controller.MyDeploymentController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {
    @Autowired
    private MyDeploymentController controller;

    @Test
    public void testController() {
        controller.run();
    }
}
