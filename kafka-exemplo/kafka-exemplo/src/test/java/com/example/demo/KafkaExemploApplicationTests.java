package com.example.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class KafkaExemploApplicationTests {

	@Value("${info.build.version}")
	private String versao3;
	@Test
	public void contextloads() {
		System.out.println(versao3);
	}

}
