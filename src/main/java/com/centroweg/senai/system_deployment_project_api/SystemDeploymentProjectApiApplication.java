package com.centroweg.senai.system_deployment_project_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SystemDeploymentProjectApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SystemDeploymentProjectApiApplication.class, args);
	}

}
