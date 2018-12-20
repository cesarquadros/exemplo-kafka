package com.example.demo.model;

public class Sessao {
	
	private Pessoa pessoa;
	private Usuario usuario;
	private TokenRedis tokenRedis;
	
	public Pessoa getPessoa() {
		return pessoa;
	}
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	public Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	public TokenRedis getTokenRedis() {
		return tokenRedis;
	}
	public void setTokenRedis(TokenRedis tokenRedis) {
		this.tokenRedis = tokenRedis;
	}
	
	
}
