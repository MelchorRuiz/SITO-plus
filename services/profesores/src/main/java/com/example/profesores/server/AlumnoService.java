/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.profesores.server;

import com.example.profesores.modelo.Alumno;

/**
 *
 * @author LENOVO
 */
public interface AlumnoService{
    Alumno buscarAlumno(int idAlumno);
    boolean agregarAlumno (Alumno alumno);
    boolean calificarAlumno(Alumno alumno);
    
}
