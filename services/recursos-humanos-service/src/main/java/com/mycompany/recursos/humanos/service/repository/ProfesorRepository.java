package com.mycompany.recursos.humanos.service.repository;

import com.mycompany.recursos.humanos.service.model.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ProfesorRepository extends JpaRepository<Profesor, Long> {
    Optional<Profesor> findByNumeroEmpleado(String numeroEmpleado);
}
