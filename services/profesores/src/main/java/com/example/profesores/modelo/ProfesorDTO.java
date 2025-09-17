/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.profesores.modelo;

/**
 *
 * @author LENOVO
 */
public class ProfesorDTO {
    private int numeroEmpleado;
    private String contrasena;
    private String correo;

    public ProfesorDTO() {
    }

    public ProfesorDTO(int numeroEmpleado, String contrasena, String correo) {
        this.numeroEmpleado = numeroEmpleado;
        this.contrasena = contrasena;
        this.correo = correo;
    }

    public int getNumeroEmpleado() {
        return numeroEmpleado;
    }

    public void setNumeroEmpleado(int numeroEmpleado) {
        this.numeroEmpleado = numeroEmpleado;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
    
    
}
