package com.raul.mongobank.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.raul.mongobank.entities.Cuenta;
import com.raul.mongobank.repositories.CuentasRepository;

@Service
public class ServiceCuentas {
	
	@Autowired
	private CuentasRepository cuentasRepository;
	
	public Cuenta insertCuenta(Cuenta cuenta) {
		return cuentasRepository.insert(cuenta);
	}
	
	public Cuenta updateCuenta(Cuenta cuenta) {
		return cuentasRepository.save(cuenta);
	}
	
	public List<Cuenta> findCuentaById(String id) {
		return cuentasRepository.findById(id);
	}
	
	public List<Cuenta> findAllCuentas() {
		return cuentasRepository.findAll();
	}
	
	public List<Cuenta> findCuentaByUserId(String userId) {
		return cuentasRepository.findByUsuarioId(userId);
	}
}
