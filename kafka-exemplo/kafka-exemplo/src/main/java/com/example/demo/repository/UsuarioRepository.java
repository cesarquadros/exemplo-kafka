package com.example.demo.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {
	
	Usuario findByUsuario(String usuario);
}
