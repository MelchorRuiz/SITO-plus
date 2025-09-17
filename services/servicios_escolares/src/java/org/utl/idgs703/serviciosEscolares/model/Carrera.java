/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.idgs703.serviciosEscolares.model;

/**
 *
 * @author Sandro
 */
public class Carrera {
    private int id_carrera;
    private String nombre_carrera;
    private int estatus;

    public Carrera() {
    }

    public Carrera(int id_carrera, String nombre_carrera, int estatus) {
        this.id_carrera = id_carrera;
        this.nombre_carrera = nombre_carrera;
        this.estatus = estatus;
    }

    public int getId_carrera() {
        return id_carrera;
    }

    public void setId_carrera(int id_carrera) {
        this.id_carrera = id_carrera;
    }

    public String getNombre_carrera() {
        return nombre_carrera;
    }

    public void setNombre_carrera(String nombre_carrera) {
        this.nombre_carrera = nombre_carrera;
    }

    public int getEstatus() {
        return estatus;
    }

    public void setEstatus(int estatus) {
        this.estatus = estatus;
    }

    @Override
    public String toString() {
        return "Carrera{" + "id_carrera=" + id_carrera + ", nombre_carrera=" + nombre_carrera + ", estatus=" + estatus + '}';
    }

    
}
