/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.idgs703.serviciosEscolares.REST;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import org.utl.idgs703.serviciosEscolares.model.Alumno;
import org.utl.idgs703.serviciosEscolares.control.ControllerAlumno;

/**
 *
 * @author Sandro
 */
@Path("alumno")
public class RESTAlumno {
    @Path("getAllAlumnos")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getAllAlumnos() {
        String out = null;
        List<Alumno> alumnos = null;
        ControllerAlumno ca = new ControllerAlumno();
        
        try {
            alumnos = ca.getAll();
            out = new Gson().toJson(alumnos);
        } catch (SQLException e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("addAlumno")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response addAlumno(String jsonAlumno) {
        String out = null;
        Gson gson = new Gson();
        Alumno alumno = gson.fromJson(jsonAlumno, Alumno.class);
        ControllerAlumno ca = new ControllerAlumno();
        
        try {
            alumno = ca.add(alumno);
            out = gson.toJson(alumno);
        } catch (SQLException e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("updateAlumno")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    public Response updateAlumno(String jsonAlumno) {
        String out = null;
        Gson gson = new Gson();
        Alumno alumno = gson.fromJson(jsonAlumno, Alumno.class);
        ControllerAlumno ca = new ControllerAlumno();
        
        try {
            alumno = ca.update(alumno);
            out = gson.toJson(alumno);
        } catch (SQLException e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("deleteAlumno/{matricula}")
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    public Response deleteAlumno(@PathParam("matricula") int matricula) {
        String out = null;
        ControllerAlumno ca = new ControllerAlumno();
        
        try {
            ca.delete(matricula);
            out = "{\"success\":\"Alumno eliminado correctamente\"}";
        } catch (SQLException e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("assignToGroup/{matricula}/{grupoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    public Response assignToGroup(@PathParam("matricula") int matricula, @PathParam("grupoId") int grupoId) {
        String out = null;
        ControllerAlumno ca = new ControllerAlumno();
        
        try {
            ca.assignToGroup(matricula, grupoId);
            out = "{\"success\":\"Alumno asignado al grupo\"}";
        } catch (SQLException e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("removeFromGroup/{matricula}")
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    public Response removeFromGroup(@PathParam("matricula") int matricula) {
        String out = null;
        ControllerAlumno ca = new ControllerAlumno();
        
        try {
            ca.removeFromGroup(matricula);
            out = "{\"success\":\"Alumno removido del grupo\"}";
        } catch (SQLException e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
}