/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package org.utl.idgs703.serviciosEscolares.view;

import java.sql.SQLException;
import java.util.List;
import org.utl.idgs703.serviciosEscolares.control.ControllerCarrera;
import org.utl.idgs703.serviciosEscolares.control.ControllerGrupo;
import org.utl.idgs703.serviciosEscolares.control.ControllerAlumno;
import org.utl.idgs703.serviciosEscolares.model.Carrera;
import org.utl.idgs703.serviciosEscolares.model.Grupo;
import org.utl.idgs703.serviciosEscolares.model.Alumno;
/**
 *
 * @author Sandro
 */
public class main {

    /**
     * @param args the command line arguments
     */
    //public static void main(String[] args) throws SQLException {
        
        //mostrarCarreras();
        //mostrarGrupos();
        //mostrarAlumnos();
        //addGrupo();
        //actualizarGrupo();
        //addCarrera();
        //actualizarCarrera();
        //addAlumno();
        //actualizarAlumno();
        
    //}
    
    public static void mostrarCarreras() throws SQLException{
        ControllerCarrera carrera = new ControllerCarrera();
        List<Carrera> carreras = carrera.getAll();
        for(Carrera c : carreras)
            System.out.println(c);
    }
    
    public static void mostrarGrupos() throws SQLException{
        ControllerGrupo grupo = new ControllerGrupo();
        List<Grupo> grupos = grupo.getAll();
        for(Grupo g : grupos)
            System.out.println(g);
    }
    
    public static void mostrarAlumnos() throws SQLException{
        ControllerAlumno alumno = new ControllerAlumno();
        List<Alumno> alumnos = alumno.getAll();
        for(Alumno a : alumnos)
            System.out.println(a);
    }
    
    private static void addGrupo() throws SQLException
    {
        ControllerGrupo ctrlgrupo = new ControllerGrupo();
        Grupo grupo = new Grupo(0, "EVN403", 0, new Carrera(2, "", 0));
        System.out.println(ctrlgrupo.add(grupo));
    }
    
    private static void actualizarGrupo() throws SQLException
    {
        ControllerGrupo ctrlgrupo = new ControllerGrupo();
        Grupo grupo = new Grupo(4, "EVN770", 0,  new Carrera(2, "", 0));
        System.out.println(ctrlgrupo.update(grupo));
    }
    
    private static void addCarrera() throws SQLException
    {
        ControllerCarrera ctrlcarrera = new ControllerCarrera();
        Carrera carrera = new Carrera(0, "CARRERA SUPER NUEVA", 1);
        System.out.println(ctrlcarrera.add(carrera));
    }
    
    private static void actualizarCarrera() throws SQLException
    {
        ControllerCarrera ctrlcarrera = new ControllerCarrera();
        Carrera carrera = new Carrera(14, "JESUS", 1);
        System.out.println(ctrlcarrera.update(carrera));
    }
    
    private static void addAlumno() throws SQLException
    {
        ControllerAlumno ctrlalumno = new ControllerAlumno();
        Alumno alumno = new Alumno(23007788, "Giovanni Rodriguez", 23007788, "23007788", 1, new Carrera(7, "", 1), new Grupo(1, "", 1, new Carrera(7, "", 1)));
        System.out.println(ctrlalumno.add(alumno));
    }
    
    private static void actualizarAlumno() throws SQLException
    {
        ControllerAlumno ctrlalumno = new ControllerAlumno();
        Alumno alumno = new Alumno(23007788, "Giovanni Lloras", 23007788, "lloron", 1, new Carrera(8, "", 1), new Grupo(1, "", 1, new Carrera(7, "", 1)));
        System.out.println(ctrlalumno.update(alumno));
    }
}
