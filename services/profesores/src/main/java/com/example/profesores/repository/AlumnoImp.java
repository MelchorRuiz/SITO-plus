/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.profesores.repository;

import com.example.profesores.modelo.Alumno;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *
 * @author LENOVO
 */
@Repository
public class AlumnoImp implements AlumnoInteferface {

    @Autowired
    AlumnoJPA alumnoJPA;

    @Override
    public Alumno buscarAlumno(int idAlumno) {
        return alumnoJPA.findById(idAlumno).orElse(null);
    }

    @Override
    public boolean agregarAlumno(Alumno alumno) {
        alumnoJPA.save(alumno);
        return true;
        
    }

    @Override
    public boolean calificarAlumno(Alumno alumno) {
        alumnoJPA.actualizarCalificacion(alumno.getIdAlumno(), alumno.getCalificacion());
        return true;
    }

}
