package com.mycompany.recursos.humanos.service.controller;

import com.mycompany.recursos.humanos.service.model.Profesor;
import com.mycompany.recursos.humanos.service.service.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.mycompany.recursos.humanos.service.security.AuthValidator;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/profesores")
public class ProfesorController {

    @Autowired
    private ProfesorService service;
    @Autowired
    private AuthValidator authValidator;

    @PostMapping
    public ResponseEntity<?> crearProfesor(@RequestBody Profesor p, HttpServletRequest request){
        authValidator.validate(request, List.of("human-resources"));
        try {
            Profesor creado = service.crearProfesor(p, request);
            return ResponseEntity.ok(creado);
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping
    public List<Profesor> obtenerTodos(HttpServletRequest request){
        authValidator.validate(request, List.of("human-resources", "school-services"));
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Profesor obtenerPorId(@PathVariable Long id, HttpServletRequest request){
        authValidator.validate(request, List.of("human-resources", "school-services"));
        return service.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Profesor actualizarProfesor(@PathVariable Long id, @RequestBody Profesor p, HttpServletRequest request){
        authValidator.validate(request, List.of("human-resources"));
        service.actualizarProfesor(id, p);
        return service.obtenerPorId(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarProfesor(@PathVariable Long id, HttpServletRequest request) {
        authValidator.validate(request, List.of("human-resources"));
        try {
            service.eliminarProfesor(id, request);
            return ResponseEntity.ok(Map.of("mensaje", "Profesor eliminado"));
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(Map.of("error", ex.getMessage()));
        }
    }
}
