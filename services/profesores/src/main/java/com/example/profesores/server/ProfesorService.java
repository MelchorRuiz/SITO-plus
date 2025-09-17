/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.profesores.server;

import com.example.profesores.modelo.Alumno;
import com.example.profesores.modelo.Profesor;
import com.example.profesores.modelo.ProfesorDTO;

/**
 *
 * @author LENOVO
 */
public interface ProfesorService {
    boolean agregarProfesor(Profesor profesor);
    boolean calificarAlumno(Alumno alumno);
    boolean actualizarCorreo(ProfesorDTO profesorDTO);
}
