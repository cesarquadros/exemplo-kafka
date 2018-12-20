package com.example.demo.controller;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.kafka.KafkaProduces;
import com.example.demo.model.Pessoa;
import com.example.demo.model.Sessao;
import com.example.demo.model.TokenRedis;
import com.example.demo.model.TokenRedis.Perfil;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PessoaRepository;
import com.example.demo.repository.UsuarioRepository;
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
	private PessoaRepository pessoaRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@RequestMapping(value = "api/", method = RequestMethod.POST)
	public Pessoa testeKafka(@RequestBody Pessoa pessoa) throws JsonProcessingException {

//		k.enviar(pessoa);

		this.pessoaRepository.save(pessoa);

		System.out.println(versao);

		return pessoa;
	}

	@RequestMapping(value = "api/autenticar", method = RequestMethod.POST)
	public ResponseEntity<Sessao> autenticar(@RequestBody Usuario usuario) throws JsonProcessingException {

		Usuario usuarioMongo = this.usuarioRepository.findByUsuarioAndSenha(usuario.getUsuario(), usuario.getSenha());
		
		if(ObjectUtils.isEmpty(usuarioMongo)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		
		Pessoa pessoa = this.pessoaRepository.findById(usuarioMongo.getIdUsuario()).get();
		String token = UUID.randomUUID().toString();
		
		Sessao sessao = new Sessao();
		
		sessao.setPessoa(pessoa);
		sessao.setUsuario(usuarioMongo);
		
		HttpHeaders header = new HttpHeaders();
		
		header.add("token", token);
		
		redisTemplate.opsForValue().set(token, mapper.writeValueAsString(sessao));
		
		RedisOperations<String, String> redisOperation = redisTemplate.opsForValue().getOperations();

		redisOperation.expire(token, (long) 30.0, TimeUnit.SECONDS);
				
		return new ResponseEntity<>(sessao, header, HttpStatus.OK);
	}
	
	@RequestMapping(value = "api/usuario", method = RequestMethod.POST)
	public ResponseEntity<Usuario> salvarUsuario(@RequestBody Usuario usuario) throws JsonProcessingException {
		
		return ResponseEntity.ok(this.usuarioRepository.save(usuario));
	}
	
	@RequestMapping(value = "api/pessoa", method = RequestMethod.POST)
	public ResponseEntity<Pessoa> salvarPessoa(@RequestBody Pessoa pessoa) throws JsonProcessingException {
		
		return ResponseEntity.ok(this.pessoaRepository.save(pessoa));
	}

	@RequestMapping(value = "api/{token}", method = RequestMethod.GET)
	public ResponseEntity<String> get(@PathVariable(value = "token") String token) throws JsonProcessingException {

		String tokenExistente = redisTemplate.opsForValue().get(token);

		if (ObjectUtils.isEmpty(tokenExistente)) {

			TokenRedis tokenRedis = new TokenRedis();

			tokenRedis.setPerfil(Perfil.ADM);
			tokenRedis.setToken(UUID.randomUUID());

			redisTemplate.opsForValue().set(token, mapper.writeValueAsString(token));

			RedisOperations<String, String> redisOperation = redisTemplate.opsForValue().getOperations();

			redisOperation.expire(token, (long) 10.0, TimeUnit.SECONDS);

			tokenExistente = redisTemplate.opsForValue().get(token);

			return ResponseEntity.ok("Nao existia token, gerado tokem e gravado no redis: " + tokenExistente);
		}

		return ResponseEntity.ok("Token existente no REDIS: " + tokenExistente);
	}

	@RequestMapping(value = "api/pessoas", method = RequestMethod.GET)
	public ResponseEntity<Object> testeKafka(@RequestHeader("token")String token) throws JsonProcessingException {
		
		String tokenExistente = redisTemplate.opsForValue().get(token);
		
		if(ObjectUtils.isEmpty(tokenExistente)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Por favor efetue o login");
		}

		return ResponseEntity.ok(this.pessoaRepository.findAll());
	}
}
