package com.raul.mongobank.controllers;

import java.util.Date;
import java.util.List;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.raul.mongobank.entities.Cuenta;
import com.raul.mongobank.entities.Transaccion;
import com.raul.mongobank.entities.Usuario;
import com.raul.mongobank.services.ServiceCuentas;
import com.raul.mongobank.services.ServiceTransacciones;
import com.raul.mongobank.services.ServiceUsuarios;

@RestController
public class CuentasController {
	private static Logger LOGGER = LoggerFactory.getLogger(CuentasController.class);

	@Autowired
	private ServiceUsuarios serviceUsuarios;

	@Autowired
	private ServiceCuentas serviceCuentas;

	@Autowired
	private ServiceTransacciones serviceTransacciones;
	
	@GetMapping(path="/login")
	public void login() {
		
	}

	@Secured("ROLE_ADMIN")
	@PostMapping(path = "/usuarios/{id_usuario}/cuentas")
	public ResponseEntity<Cuenta> insertCuenta(@PathVariable String id_usuario, @RequestBody @Valid Cuenta cuenta) {
		List<Usuario> user = serviceUsuarios.findUserById(id_usuario);
		
		if (user == null || user.size() == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		// Asignamos a la cuenta facilitada el identificador del usuario autenticado
		cuenta.setUsuarioId(user.get(0).getId());
		cuenta.setCreatedAt(new Date());

		return new ResponseEntity<Cuenta>(serviceCuentas.insertCuenta(cuenta), HttpStatus.OK);
	}

	@Secured("ROLE_USER")
	@PutMapping(path = "/usuarios/{id_usuario}/cuentas/{id_cuenta}")
	public ResponseEntity<Cuenta> updateSaldoCuenta(@PathVariable(name = "id_usuario") String id_usuario,
			@PathVariable(name = "id_cuenta") String id_cuenta,
			@RequestParam(name = "ingresar", required = false, defaultValue = "0") double ingreso,
			@RequestParam(name = "descIngreso", required = false, defaultValue = "Ingreso") String descIngreso,
			@RequestParam(name = "descGasto", required = false, defaultValue = "Gasto") String descGasto,
			@RequestParam(name = "retirar", required = false, defaultValue = "0") double gasto) {

		String username = getAuth();
		LOGGER.info(username);

		// Buscamos el usuario autenticado con el Token en la BD
		List<Usuario> userAuth = serviceUsuarios.findUserByUsername(username);

		if (userAuth == null || userAuth.size() == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		LOGGER.info(userAuth.get(0).toString());

		// Si el usuario autenticado no es el mismo que el del path variable, está
		// intentando acceder a otro usuario
		if (!userAuth.get(0).getId().equals(id_usuario)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		// Buscamos la cuenta y verificamos que este asociada al usuario autenticado
		List<Cuenta> cuentas = serviceCuentas.findCuentaById(id_cuenta);

		if (cuentas == null || cuentas.size() == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		if (!cuentas.get(0).getUsuarioId().equals(userAuth.get(0).getId())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		LOGGER.info(cuentas.get(0).toString());

		// Valoramos el ingreso o la retirada o ambos

		if (ingreso > 0) {
			cuentas.get(0).agregarSaldo(ingreso, descIngreso);
		}

		if (gasto > 0) {
			cuentas.get(0).retirarCantidad(gasto, descGasto);
		}

		// Generamos las transacciones asociadas si las hubiera;
		for (Transaccion trans : cuentas.get(0).getTransacciones()) {
			Transaccion tResult = serviceTransacciones.insertTransaccion(trans);

			if (tResult == null) {
				throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
			}
		}

		// Actualizamos la cuenta
		Cuenta cuentaResult = serviceCuentas.updateCuenta(cuentas.get(0));

		if (cuentaResult == null) {
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE);
		}

		return new ResponseEntity<Cuenta>(cuentaResult, HttpStatus.OK);
	}

	@Secured("ROLE_ADMIN")
	@GetMapping(path = "/usuarios/cuentas")
	public ResponseEntity<List<Cuenta>> findAllCuentas() {
		return new ResponseEntity<List<Cuenta>>(serviceCuentas.findAllCuentas(), HttpStatus.OK);
	}

	@Secured({"ROLE_USER, ROLE_ADMIN"})
	@GetMapping(path = "/usuarios/{id_usuario}/cuentas/")
	public ResponseEntity<List<Cuenta>> findAllCuentasFromUsuario(@PathVariable String id_usuario) {
		String username = getAuth();

		List<Usuario> userAuth = serviceUsuarios.findUserById(username);

		if (userAuth == null || userAuth.size() == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		if (!userAuth.get(0).getId().equals(id_usuario)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}

		List<Cuenta> cuentas = serviceCuentas.findCuentaByUserId(id_usuario);

		if (cuentas == null || cuentas.size() == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		return new ResponseEntity<List<Cuenta>>(cuentas, HttpStatus.OK);

	}
	
	@Secured({"ROLE_USER, ROLE_ADMIN"})
	@GetMapping(path = "/usuarios/{id_usuario}/cuentas/{id_cuenta}/transacciones")
	public ResponseEntity<List<Transaccion>> findAllTransaccionesFromCuenta(@PathVariable(name="id_usuario") String id_usuario,
			@PathVariable(name="id_cuenta") String id_cuenta) {
		
		String username = getAuth();

		List<Usuario> userAuth = serviceUsuarios.findUserByUsername(username);

		if (userAuth == null || userAuth.size() == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}

		if (!userAuth.get(0).getId().equals(id_usuario)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		
		List<Cuenta> cuentas = serviceCuentas.findCuentaById(id_cuenta);
		
		if (cuentas == null || cuentas.size() == 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		
		for (Cuenta c : cuentas) {
			if (!c.getUsuarioId().equals(id_usuario)) {
				throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
			}
		}
		
		List<Transaccion> transacciones = serviceTransacciones.findTransaccionesByCuentaId(cuentas.get(0).getId());
		
		return new ResponseEntity<List<Transaccion>>(transacciones, HttpStatus.OK);
	}

	@Async
	public String getAuth() {
		return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
	}
}
