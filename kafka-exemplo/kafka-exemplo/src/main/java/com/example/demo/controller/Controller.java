package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.kafka.KafkaProduces;
import com.example.demo.model.Pessoa;
import com.example.demo.repository.PessoaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;

import io.swagger.annotations.Api;

@RestController
@Api(value = "Agendamento", tags = "Agendamento")
public class Controller {
	
	@Autowired
	private KafkaProduces k;
	
	@Value("${info.app.java.target}")
	private String versao;
	
	@Value("${info.build.version}")
	private String versao3;
	
	
	@Autowired
	private PessoaRepository repository;
	
	@RequestMapping(value = "api/", method = RequestMethod.POST)
	public Pessoa testeKafka(@RequestBody Pessoa pessoa) throws JsonProcessingException {
		
		k.enviar(pessoa);
		
		System.out.println(versao);
		
		return pessoa;
	}
	
	@RequestMapping(value = "api/{teste}", method = RequestMethod.GET)
	public List<Pessoa> get(@PathVariable(value = "teste") String teste){
		
		return this.repository.findAll();
	}
	
	@RequestMapping(value = "api/", method = RequestMethod.GET)
	public void testeKafka() throws JsonProcessingException {
		
		System.out.println(versao);

		System.out.println(versao3);

	}
}
