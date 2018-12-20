package com.example.demo.kafka;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.example.demo.model.Pessoa;
import com.example.demo.repository.PessoaRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KafkaProduces {
	
	private static final Logger LOG = LoggerFactory.getLogger(KafkaProduces.class);
	
	@Autowired
	private PessoaRepository repository;
	
	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired
	private ObjectMapper mapper;
	
	public void enviar(Pessoa pessoa) throws JsonProcessingException {
		this.kafkaTemplate.send("topico.teste", mapper.writeValueAsString(pessoa));
	}
	
//	@KafkaListener(topics = "topico.teste")
	public void ouvirFila(@Payload String mensagem) throws JsonParseException, JsonMappingException, IOException {
		LOG.info(mensagem);
		
		Pessoa pessoa = mapper.readValue(mensagem, Pessoa.class);
		
		this.repository.save(pessoa);
		
	}
}
