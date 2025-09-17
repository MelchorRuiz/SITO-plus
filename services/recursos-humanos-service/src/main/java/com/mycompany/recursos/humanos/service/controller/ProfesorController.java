package com.mycompany.recursos.humanos.service.controller;

import com.mycompany.recursos.humanos.service.model.Profesor;
import com.mycompany.recursos.humanos.service.service.ProfesorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/profesores")
public class ProfesorController {

    @Autowired
    private ProfesorService service;

    @PostMapping
    public Map<String,Object> crearProfesor(@RequestBody Profesor p){
        Profesor saved = service.crearProfesor(p);
        return Map.of("id", saved.getId(), "mensaje", "Profesor creado");
    }

    @GetMapping
    public List<Profesor> obtenerTodos(){
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    public Profesor obtenerPorId(@PathVariable Long id){
        return service.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public Map<String,String> actualizarProfesor(@PathVariable Long id, @RequestBody Profesor p){
        service.actualizarProfesor(id, p);
        return Map.of("mensaje","Profesor actualizado");
    }

    @PutMapping("/{id}/contrasena")
    public Map<String,String> cambiarContrasena(@PathVariable Long id, @RequestBody Map<String,String> payload){
        service.cambiarContrasena(id, payload.get("nueva"));
        return Map.of("mensaje","Contraseña actualizada");
    }

    @DeleteMapping("/{id}")
    public Map<String,String> eliminarProfesor(@PathVariable Long id){
        service.eliminarProfesor(id);
        return Map.of("mensaje","Profesor eliminado");
    }
}
