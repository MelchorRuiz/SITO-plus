/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.idgs703.serviciosEscolares.model;

/**
 *
 * @author Sandro
 */
public class Grupo {
    private int id_grupo;
    private String nombre_grupo;
    private int estatus;
    private Carrera carrera;

    public Grupo() {
    }

    public Grupo(int id_grupo, String nombre_grupo, int estatus, Carrera carrera) {
        this.id_grupo = id_grupo;
        this.nombre_grupo = nombre_grupo;
        this.estatus = estatus;
        this.carrera = carrera;
    }

    public int getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(int id_grupo) {
        this.id_grupo = id_grupo;
    }

    public String getNombre_grupo() {
        return nombre_grupo;
    }

    public void setNombre_grupo(String nombre_grupo) {
        this.nombre_grupo = nombre_grupo;
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

    @Override
    public String toString() {
        return "Grupo{" + "id_grupo=" + id_grupo + ", nombre_grupo=" + nombre_grupo + ", estatus=" + estatus + ", carrera=" + carrera + '}';
    }

    
    
}
