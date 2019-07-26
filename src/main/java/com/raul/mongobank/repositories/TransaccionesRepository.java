package com.raul.mongobank.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.raul.mongobank.entities.Transaccion;
import java.lang.String;
import java.util.List;

@Repository
public interface TransaccionesRepository extends MongoRepository<Transaccion, Integer> {
	List<Transaccion> findByIdCuenta(String idcuenta);
}
