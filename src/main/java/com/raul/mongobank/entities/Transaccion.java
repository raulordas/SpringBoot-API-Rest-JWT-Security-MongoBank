package com.raul.mongobank.entities;

import java.util.Date;

import javax.validation.constraints.NotNull;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="transacciones")
public class Transaccion {
	
	@Id
	private ObjectId id;
	
	@NotNull
	private String descripcion;
	
	@CreatedDate
	private Date createdAt;
	
	private double saldoAnterior;
	
	private double cantidad;
	
	private double saldoFinal;
	
	private String idCuenta;
	
	public Transaccion() {}

	public Transaccion(ObjectId id, @NotNull String descripcion, Date createdAt, double saldoAnterior, double cantidad,
			double saldoFinal, String idCuenta) {
		this.id = id;
		this.descripcion = descripcion;
		this.createdAt = createdAt;
		this.saldoAnterior = saldoAnterior;
		this.cantidad = cantidad;
		this.saldoFinal = saldoFinal;
		this.idCuenta = idCuenta;
	}

	public String getId() {
		return id.toHexString();
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public double getSaldoAnterior() {
		return saldoAnterior;
	}

	public void setSaldoAnterior(double saldoAnterior) {
		this.saldoAnterior = saldoAnterior;
	}

	public double getCantidad() {
		return cantidad;
	}

	public void setCantidad(double cantidad) {
		this.cantidad = cantidad;
	}

	public double getSaldoFinal() {
		return saldoFinal;
	}

	public void setSaldoFinal(double saldoFinal) {
		this.saldoFinal = saldoFinal;
	}

	public String getIdCuenta() {
		return idCuenta;
	}

	public void setIdCuenta(String idCuenta) {
		this.idCuenta = idCuenta;
	}
}
