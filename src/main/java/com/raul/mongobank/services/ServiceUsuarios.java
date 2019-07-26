package com.raul.mongobank.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.raul.mongobank.entities.Usuario;
import com.raul.mongobank.repositories.UsuariosRepository;

@Service
public class ServiceUsuarios {
	
	@Autowired
	private UsuariosRepository usuariosRepository;
	
	
	public Usuario saveUsuario(Usuario usuario) {
		return usuariosRepository.insert(usuario);
	}
	
	public List<Usuario> findAllUsuarios() {
		return usuariosRepository.findAll();
	}
	
	public List<Usuario> findUserByUsername(String username) {
		return usuariosRepository.findByUsername(username);
	}
	
	public List<Usuario> findUserById(String id) {
		return usuariosRepository.findById(id);
	}
}
