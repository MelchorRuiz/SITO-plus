/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.example.profesores.repository;

import com.example.profesores.modelo.Profesor;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author LENOVO
 */
public interface ProfesoresJpa extends JpaRepository<Profesor, Integer> {

    @Modifying
    @Transactional
    @Query("UPDATE Profesor p SET p.correo = ?2, p.contrasena = ?3 WHERE p.numeroEmpleado = ?1")
    int actualizarCorreoYContrasena(int idProfesor, String correo, String contrasena);
}
