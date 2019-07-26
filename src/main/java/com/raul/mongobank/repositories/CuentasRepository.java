package com.raul.mongobank.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.raul.mongobank.entities.Cuenta;
import java.lang.String;
import java.util.List;

@Repository
public interface CuentasRepository extends MongoRepository<Cuenta, Integer> {
	
	List<Cuenta> findById(String id);
	
	List<Cuenta> findByUsuarioId(String usuarioid);
}
