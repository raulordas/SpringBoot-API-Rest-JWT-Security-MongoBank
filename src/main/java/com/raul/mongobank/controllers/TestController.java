package com.raul.mongobank.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	private static Logger logger = LoggerFactory.getLogger(TestController.class);
	
	@Secured("ROLE_USER")
	@GetMapping(path="/hola")
	public void HelloWorld() {
		logger.info("BIENVENIDO");
	}
}
