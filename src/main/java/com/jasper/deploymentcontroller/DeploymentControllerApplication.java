package com.jasper.deploymentcontroller;

import com.jasper.deploymentcontroller.controller.MyDeploymentController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DeploymentControllerApplication implements CommandLineRunner {
    @Autowired
    private MyDeploymentController controller;

    public static void main(String[] args) {
        SpringApplication.run(DeploymentControllerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        controller.run();
    }
}
