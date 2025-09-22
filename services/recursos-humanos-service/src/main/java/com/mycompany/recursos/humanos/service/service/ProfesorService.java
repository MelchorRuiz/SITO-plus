package com.mycompany.recursos.humanos.service.service;

import com.mycompany.recursos.humanos.service.model.Profesor;
import com.mycompany.recursos.humanos.service.repository.ProfesorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;

@Service
public class ProfesorService {

    @Autowired
    private ProfesorRepository repository;

    // Crear profesor con número de empleado consecutivo y cuenta en autenticación
    public Profesor crearProfesor(Profesor p, HttpServletRequest request) {
        long total = repository.count();
        String numeroEmpleado = String.format("P%06d", total + 1);
        p.setNumeroEmpleado(numeroEmpleado);
        // Crear cuenta en el servicio de autenticación
        HttpHeaders headers = new HttpHeaders();
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        headers.set("Authorization", token);
        Map<String, Object> body = Map.of(
            "username", numeroEmpleado,
            "password", numeroEmpleado,
            "role", "teacher"
        );
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity("http://authentication/register", entity, Map.class);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear la cuenta del profesor: " + ex.getMessage());
        }
        if (response.getStatusCode().is2xxSuccessful()) {
            return repository.save(p);
        } else {
            throw new RuntimeException("No se pudo crear la cuenta del profesor");
        }
    }

    // Listar todos
    public List<Profesor> obtenerTodos() {
        return repository.findAll();
    }

    // Obtener por ID
    public Profesor obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
    }

    public Profesor obtenerPorNumeroEmpleado(String numeroEmpleado) {
        return repository.findByNumeroEmpleado(numeroEmpleado)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
    }

    // Actualizar profesor
    public void actualizarProfesor(Long id, Profesor p) {
        Profesor existente = obtenerPorId(id);
        existente.setNombre(p.getNombre());
        existente.setApellido(p.getApellido());
        existente.setSalario(p.getSalario());
        existente.setPuesto(p.getPuesto());
        repository.save(existente);
    }

    // Eliminar profesor y su cuenta en autenticación
    public void eliminarProfesor(Long id, HttpServletRequest request) {
        Profesor profesor = obtenerPorId(id);
        String numeroEmpleado = profesor.getNumeroEmpleado();

        // Eliminar cuenta en el servicio de autenticación
        HttpHeaders headers = new HttpHeaders();
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        headers.set("Authorization", token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            restTemplate.exchange(
                "http://authentication/delete-user/" + numeroEmpleado,
                HttpMethod.DELETE,
                entity,
                Void.class
            );
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo eliminar la cuenta del profesor: " + ex.getMessage());
        }

        // Eliminar profesor de la base de datos
        repository.deleteById(id);
    }
}
