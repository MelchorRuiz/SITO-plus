/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.idgs703.serviciosEscolares.REST;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;
import org.utl.idgs703.serviciosEscolares.model.Grupo;
import org.utl.idgs703.serviciosEscolares.control.ControllerGrupo;
import org.utl.idgs703.serviciosEscolares.util.AuthenticationUtil;

/**
 *
 * @author Sandro
 */
@Path("grupo")
public class RESTGrupo {
    @Path("getAllGrupos")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getAllGrupos(@HeaderParam("Authorization") String authHeader) {
        // Validate bearer token using utility class
        Response authResponse = AuthenticationUtil.validateTokenAndRoleOrReturnError(authHeader, "school-services");
        if (authResponse != null) {
            return authResponse;
        }
        
        String out = null;
        List<Grupo> grupos = null;
        ControllerGrupo cg = new ControllerGrupo();
        
        try {
            grupos = cg.getAll();
            out = new Gson().toJson(grupos);
        } catch (SQLException e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("addGrupo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response addGrupo(@HeaderParam("Authorization") String authHeader, String jsonGrupo) {
        // Validate bearer token using utility class
        Response authResponse = AuthenticationUtil.validateTokenAndRoleOrReturnError(authHeader, "school-services");
        if (authResponse != null) {
            return authResponse;
        }
        
        String out = null;
        Gson gson = new Gson();
        Grupo grupo = gson.fromJson(jsonGrupo, Grupo.class);
        ControllerGrupo cg = new ControllerGrupo();
        
        try {
            grupo = cg.add(grupo);
            out = gson.toJson(grupo);
        } catch (SQLException e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("updateGrupo")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @PUT
    public Response updateGrupo(@HeaderParam("Authorization") String authHeader, String jsonGrupo) {
        // Validate bearer token using utility class
        Response authResponse = AuthenticationUtil.validateTokenAndRoleOrReturnError(authHeader, "school-services");
        if (authResponse != null) {
            return authResponse;
        }
        
        String out = null;
        Gson gson = new Gson();
        Grupo grupo = gson.fromJson(jsonGrupo, Grupo.class);
        ControllerGrupo cg = new ControllerGrupo();
        
        try {
            grupo = cg.update(grupo);
            out = gson.toJson(grupo);
        } catch (SQLException e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
    
    @Path("deleteGrupo/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    public Response deleteGrupo(@HeaderParam("Authorization") String authHeader, @PathParam("id") int id) {
        // Validate bearer token using utility class
        Response authResponse = AuthenticationUtil.validateTokenAndRoleOrReturnError(authHeader, "school-services");
        if (authResponse != null) {
            return authResponse;
        }
        
        String out = null;
        ControllerGrupo cg = new ControllerGrupo();
        
        try {
            cg.delete(id);
            out = "{\"success\":\"Grupo eliminado correctamente\"}";
        } catch (SQLException e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
}