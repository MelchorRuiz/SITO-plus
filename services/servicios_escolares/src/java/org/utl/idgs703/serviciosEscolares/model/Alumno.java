/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.idgs703.serviciosEscolares.model;


/**
 *
 * @author Sandro
 */
public class Alumno {
    private int matricula;
    private String nombre;
    private int usuario;
    private String contrasenia;
    private int estatus;
    private Carrera carrera;
    private Grupo grupo;

    public Alumno() {
    }

    public Alumno(int matricula, String nombre, int usuario, String contrasenia, int estatus, Carrera carrera, Grupo grupo) {
        this.matricula = matricula;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasenia = contrasenia;
        this.estatus = estatus;
        this.carrera = carrera;
        this.grupo = grupo;
    }

    public int getMatricula() {
        return matricula;
    }

    public void setMatricula(int matricula) {
        this.matricula = matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getUsuario() {
        return usuario;
    }

    public void setUsuario(int usuario) {
        this.usuario = usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    public Carrera getCarrera() {
        return carrera;
    }

    public void setCarrera(Carrera carrera) {
        this.carrera = carrera;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    @Override
    public String toString() {
        return "Alumno{" + "matricula=" + matricula + ", nombre=" + nombre + ", usuario=" + usuario + ", contrasenia=" + contrasenia + ", estatus=" + estatus + ", carrera=" + carrera + ", grupo=" + grupo + '}';
    }

    
    
}
