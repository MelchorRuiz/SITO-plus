/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.profesores.server;

import com.example.profesores.modelo.Alumno;
import com.example.profesores.modelo.Profesor;
import com.example.profesores.modelo.ProfesorDTO;
import com.example.profesores.repository.ProfesoresInterface;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author LENOVO
 */
@Service//validaciones
public class ProfesorServiceImp implements ProfesorService{
    
    @Autowired
    ProfesoresInterface profesorI;

    @Override
    public boolean agregarProfesor(Profesor profesor) {
        if (profesorI.buscarProfesor(profesor.getNumeroEmpleado())==null) {
            profesorI.crearProfesor(profesor);
            return true;
        }
        return false;
    }

    @Override
    public boolean calificarAlumno(Alumno alumno) {
        return profesorI.calificarAlumno(alumno);
    }

    @Override
    public boolean actualizarCorreo(ProfesorDTO profesorDTO) {
        return profesorI.actualizarCorreo(profesorDTO);
    }

    @Override
    public List<Profesor> buscarProfesores() {
        return profesorI.buscarProfesores();
    }
    
}
