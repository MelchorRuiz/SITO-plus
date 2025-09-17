/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.profesores.server;

import com.example.profesores.modelo.Alumno;
import com.example.profesores.repository.AlumnoInteferface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service
public class AlumnoServiceImp implements AlumnoService{
    
    @Autowired
    AlumnoInteferface alumnoI;

    @Override
    public Alumno buscarAlumno(int idAlumno) {
        return alumnoI.buscarAlumno(idAlumno);
    }

    @Override
    public boolean agregarAlumno(Alumno alumno) {
        if (alumnoI.buscarAlumno(alumno.getIdAlumno())==null) {
            alumnoI.agregarAlumno(alumno);
            return true;
        }
        return false;
    }

    @Override
    public boolean calificarAlumno(Alumno alumno) {
        if (alumnoI.buscarAlumno(alumno.getIdAlumno())!=null) {
            alumnoI.calificarAlumno(alumno);
            return true;
        }else{
            
            return false;
        }
        
    }
    
}
