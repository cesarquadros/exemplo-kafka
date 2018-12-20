package com.example.demo.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.kafka.KafkaProduces;
import com.example.demo.model.Pessoa;
import com.example.demo.model.TokenRedis;
import com.example.demo.model.TokenRedis.Perfil;
import com.example.demo.repository.PessoaRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired
	private ObjectMapper mapper;
	
	@Autowired
	private PessoaRepository repository;
	
	@RequestMapping(value = "api/", method = RequestMethod.POST)
	public Pessoa testeKafka(@RequestBody Pessoa pessoa) throws JsonProcessingException {
		
//		k.enviar(pessoa);
		
		this.repository.save(pessoa);
		
		System.out.println(versao);
		
		return pessoa;
	}
	
	@RequestMapping(value = "api/{id}", method = RequestMethod.GET)
	public ResponseEntity<String> get(@PathVariable(value = "id") String id) throws JsonProcessingException{
		
		String tokenExistente = redisTemplate.opsForValue().get(id);
		
		if(ObjectUtils.isEmpty(tokenExistente)) {
			
			Optional<Pessoa> p = this.repository.findById(id);
			
			if(ObjectUtils.isEmpty(p)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Acesso não autorizado");
			}
			
			TokenRedis token = new TokenRedis();
			
			token.setPerfil(Perfil.ADM);
			token.setToken(UUID.randomUUID());
			
			redisTemplate.opsForValue().set(id, mapper.writeValueAsString(token));
			
			RedisOperations<String, String> redisOperation = redisTemplate.opsForValue().getOperations();

			redisOperation.expire(id, (long) 10.0, TimeUnit.SECONDS);
			
			tokenExistente = redisTemplate.opsForValue().get(id);
			
			return ResponseEntity.ok("Nao existia token, gerado tokem e gravado no redis: " + tokenExistente);
		}
		
		return ResponseEntity.ok("Token existente no REDIS: " + tokenExistente);
	}
	
	@RequestMapping(value = "api/", method = RequestMethod.GET)
	public List<Pessoa> testeKafka() throws JsonProcessingException {
		
		return this.repository.findAll();

	}
}
