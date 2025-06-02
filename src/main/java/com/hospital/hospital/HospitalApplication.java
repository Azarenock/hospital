package com.hospital.hospital;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HospitalApplication {

	private static final Logger logger = LoggerFactory.getLogger(HospitalApplication.class);

	public static void main(String[] args) {

		logger.info("приложение запущенно");
		SpringApplication.run(HospitalApplication.class, args);
	}

}
