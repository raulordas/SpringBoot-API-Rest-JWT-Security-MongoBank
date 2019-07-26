package com.raul.mongobank.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.raul.mongobank.entities.Transaccion;
import com.raul.mongobank.repositories.TransaccionesRepository;

@Service
public class ServiceTransacciones {
	
	@Autowired
	private TransaccionesRepository transaccionesRepository;
	
	public Transaccion insertTransaccion(Transaccion transaccion) {
		return transaccionesRepository.insert(transaccion);
	}
	
	public List<Transaccion> findTransaccionesByCuentaId(String cuentaId) {
		return transaccionesRepository.findByIdCuenta(cuentaId);
	}

}
