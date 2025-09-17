/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.profesores.repository;

import com.example.profesores.modelo.Alumno;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

/**
 *
 * @author LENOVO
 */
public interface AlumnoJPA extends JpaRepositoryImplementation<Alumno, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Alumno a SET a.calificacion = ?2 WHERE a.idAlumno = ?1")
    int actualizarCalificacion(int idAlumno, double calificacion);
    
}
