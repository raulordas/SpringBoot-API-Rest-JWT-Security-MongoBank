package com.raul.mongobank.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection="cuentas")
public class Cuenta {

	@Id
	private ObjectId id;
	
	private String nombre;
	
	private double saldo;
	
	@CreatedDate
	private Date createdAt;
	
	private String usuarioId;
	
	@Transient
	List<Transaccion> transacciones;
	
	public Cuenta() {}

	public Cuenta(ObjectId id, String nombre, double saldo, Date createdAt) {
		this.id = id;
		this.nombre = nombre;
		this.saldo = saldo;
		this.createdAt = createdAt;
	}

	public String getId() {
		return id.toHexString();
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getSaldo() {
		return saldo;
	}

	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	@JsonInclude(value=Include.NON_NULL)
	public List<Transaccion> getTransacciones() {
		return transacciones;
	}

	public void setTransacciones(List<Transaccion> transacciones) {
		this.transacciones = transacciones;
	}

	public String getUsuarioId() {
		return usuarioId;
	}

	public void setUsuarioId(String usuarioId) {
		this.usuarioId = usuarioId;
	}

	public synchronized void retirarCantidad(double cantidad, String descripcion) {
		Transaccion provisional = new Transaccion(null, descripcion, new Date(), saldo, cantidad, saldo-cantidad, getId());
		
		if (this.saldo - cantidad > 0) {
			saldo-= cantidad;
			generarTransaccion(provisional);
			
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
		}
	}
	
	public synchronized void agregarSaldo(double cantidad, String descripcion) {
		Transaccion provisional = new Transaccion(null, descripcion, new Date(), saldo, cantidad, saldo+cantidad, getId());
		saldo+=cantidad;
		generarTransaccion(provisional);
	}

	private void generarTransaccion(Transaccion transaccion) {
		if (transacciones == null) {
			transacciones = new ArrayList<Transaccion>();
		}
		
		transacciones.add(transaccion);
		
	}

	@Override
	public String toString() {
		return "Cuenta [id=" + id + ", nombre=" + nombre + ", saldo=" + saldo + ", createdAt=" + createdAt
				+ ", usuarioId=" + usuarioId + ", transacciones=" + transacciones + "]";
	}
}
