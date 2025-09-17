/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.idgs703.serviciosEscolares.REST;

import com.google.gson.Gson;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import org.utl.idgs703.serviciosEscolares.model.Carrera;
import org.utl.idgs703.serviciosEscolares.control.ControllerCarrera;

/**
 *
 * @author Sandro
 */
@Path("carrera")
public class RESTCarrera {
    @Path("getAllCarreras")
    @Produces(MediaType. APPLICATION_JSON)
    @GET
    public Response getAllCarreras() {
        String out = null;
        List<Carrera> carreras = null;
        ControllerCarrera cc = new ControllerCarrera();
        
        try {
            carreras = cc.getAll();
            // Se utiliza la interfaz Gson para convertir la estructura en un array de JSON's
            out = new Gson().toJson(carreras);
        } catch (Exception e) {
            out="""
                {"error":"Ocurrio un error. Intente más tarde"}
                """;
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
}
