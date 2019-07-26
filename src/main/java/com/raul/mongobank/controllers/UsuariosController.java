package com.raul.mongobank.controllers;

import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.raul.mongobank.entities.Usuario;
import com.raul.mongobank.services.ServiceUsuarios;

@RestController
@RequestMapping(path="/usuarios")
public class UsuariosController {

	@Autowired
	private ServiceUsuarios serviceUsuarios;
	
	@Secured("ROLE_ADMIN")
	@PostMapping
	public ResponseEntity<Usuario> insertUsuario(@RequestBody @Valid Usuario usuario) {
		return new ResponseEntity<Usuario>(serviceUsuarios.saveUsuario(usuario), HttpStatus.CREATED);
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping
	public ResponseEntity<List<Usuario>> findAllUsuarios() {
		return new ResponseEntity<List<Usuario>>(serviceUsuarios.findAllUsuarios(), HttpStatus.OK);
	}
}
