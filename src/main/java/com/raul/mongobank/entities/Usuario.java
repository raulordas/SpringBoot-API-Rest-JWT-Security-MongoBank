package com.raul.mongobank.entities;

import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Document(collection="usuarios")
public class Usuario {
	
	@Id
	private ObjectId id;
	
	@NotNull
	private String nombre;
	
	@NotNull
	private String apellidos;
	
	@NotNull
	@Email
	@Indexed(unique=true)
	private String email;
	
	@NotNull
	@Indexed(unique=true)
	private String username;
	
	@NotNull
	@BsonIgnore
	private String password;
	
	List<Rol> roles;
	
	@JsonInclude(value=Include.NON_NULL)
	List<Cuenta> cuentas;
	
	public Usuario() {}

	public Usuario(ObjectId id, @NotNull String nombre, @NotNull String apellidos, @NotNull @Email String email,
			@NotNull String username, String password, List<Rol> roles, List<Cuenta> cuentas) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.apellidos = apellidos;
		this.email = email;
		this.username = username;
		this.password = password;
		this.roles = roles;
		this.cuentas = cuentas;
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

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public List<Rol> getRoles() {
		return roles;
	}

	public void setRoles(List<Rol> roles) {
		this.roles = roles;
	}

	public List<Cuenta> getCuentas() {
		return cuentas;
	}

	public void setCuentas(List<Cuenta> cuentas) {
		this.cuentas = cuentas;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = new BCryptPasswordEncoder().encode(password);
	}

	@Override
	public String toString() {
		return "Usuario [id=" + id + ", nombre=" + nombre + ", apellidos=" + apellidos + ", email=" + email
				+ ", username=" + username + ", password=" + password + ", roles=" + roles + ", cuentas=" + cuentas
				+ "]";
	}
}
