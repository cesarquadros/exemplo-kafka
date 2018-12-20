package com.example.demo.model;

import java.util.UUID;

@org.springframework.data.redis.core.RedisHash("TokenRedis")
public class TokenRedis {
	
	public enum Perfil{
		ADM, USER
	}
	
	private UUID token;
	private Perfil perfil;
	public UUID getToken() {
		return token;
	}
	public void setToken(UUID token) {
		this.token = token;
	}
	public Perfil getPerfil() {
		return perfil;
	}
	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	} 
	
}
