package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KafkaExemploApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaExemploApplication.class, args);
		String path = System.getProperty("user.dir");
		String lista[] = path.split("/");
		
		String versao = lista[lista.length-1];
		
		System.out.println(versao);
	}
}
