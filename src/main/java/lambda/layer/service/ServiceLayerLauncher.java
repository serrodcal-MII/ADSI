package lambda.layer.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;

@SpringBootApplication
public class ServiceLayerLauncher extends SpringBootServletInitializer implements Runnable {

	public void run() {
		SpringApplication.run(ServiceLayerLauncher.class);
	}

}