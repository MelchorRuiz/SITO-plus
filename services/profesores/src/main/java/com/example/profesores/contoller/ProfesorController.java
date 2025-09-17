/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.profesores.contoller;

import com.example.profesores.modelo.Alumno;
import com.example.profesores.modelo.Profesor;
import com.example.profesores.modelo.ProfesorDTO;
import com.example.profesores.server.AlumnoService;
import com.example.profesores.server.ProfesorService;
import jakarta.websocket.server.PathParam;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author LENOVO
 */
@RestController
public class ProfesorController {
    
    @Autowired
    ProfesorService server;
    
    @Autowired
    AlumnoService serverA;
    
    @PostMapping(value = "profesores" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean guardarProfesor(@RequestBody Profesor profesor){
        return server.agregarProfesor(profesor);
    }
    
    @PostMapping(value = "alumnos" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean guardarAlumno(@RequestBody Alumno alumno){
        return serverA.agregarAlumno(alumno);
    }
    
    @PutMapping(value = "alumnos" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean calificar(@RequestBody Alumno alumno){
        return serverA.calificarAlumno(alumno);
    }
    
    @GetMapping(value = "alumnos/{idAlumno}")
    public Alumno calificar(@PathVariable("idAlumno") int idAluno){
        return serverA.buscarAlumno(idAluno);
    }
    @GetMapping(value = "profesores",produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Profesor> mostrarProfesor(){
        return server.buscarProfesores();
    }
    
    @PutMapping(value = "profesores/calificar" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean calificarAlumno(@RequestBody Alumno alumno){
        return server.calificarAlumno(alumno);
    }
    
    @PutMapping(value = "profesores" ,consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean actualizaContraseña(@RequestBody ProfesorDTO pd){
        return server.actualizarCorreo(pd);
    }
    @GetMapping("/index")
    public String index(){
        return "index";
    }
    
}
