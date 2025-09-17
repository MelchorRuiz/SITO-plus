/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.profesores.repository;

import com.example.profesores.modelo.Alumno;
import com.example.profesores.modelo.Profesor;
import com.example.profesores.modelo.ProfesorDTO;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author LENOVO
 */
@Repository
public class ProfesoresImp implements  ProfesoresInterface{
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ProfesoresJpa profesoresJpa;
    
    @Override
    public boolean crearProfesor(Profesor profesor) {
        profesoresJpa.save(profesor);
        return true;
    }

    @Override
    public Profesor buscarProfesor(int numeroEmpleado) {
        return profesoresJpa.findById(numeroEmpleado).orElse(null);
    
    }

    @Override
    public boolean calificarAlumno(Alumno alumno) {
        String url = "http://localhost:8083/alumnos"; // Cambia por la URL real

        // No necesitas HttpHeaders ni HttpEntity explícitos aquí
        ResponseEntity<Boolean> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                new HttpEntity<>(alumno),  // RestTemplate infiere que es JSON
                Boolean.class
        );

        return response.getBody();
    
    }

    @Override
    public boolean actualizarCorreo(ProfesorDTO profesorDTO) {
        profesoresJpa.actualizarCorreoYContrasena(profesorDTO.getNumeroEmpleado(), profesorDTO.getCorreo(), profesorDTO.getContrasena());
        return true;
    }
    
   @Override
    public List<Profesor> buscarProfesores() {
        return profesoresJpa.findAll();
    }
    
}
