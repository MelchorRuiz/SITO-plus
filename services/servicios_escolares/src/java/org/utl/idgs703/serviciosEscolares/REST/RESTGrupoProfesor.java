package org.utl.idgs703.serviciosEscolares.REST;

import com.google.gson.Gson;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.utl.idgs703.serviciosEscolares.model.GrupoProfesor;
import org.utl.idgs703.serviciosEscolares.control.ControllerGrupoProfesor;
import org.utl.idgs703.serviciosEscolares.util.AuthenticationUtil;

@Path("grupoprofesor")
public class RESTGrupoProfesor {

    @Path("addGrupoProfesor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @POST
    public Response addGrupoProfesor(@HeaderParam("Authorization") String authHeader, String jsonGrupoProfesor) {
        Response authResponse = AuthenticationUtil.validateTokenAndRoleOrReturnError(authHeader, "school-services");
        if (authResponse != null) {
            return authResponse;
        }

        String out;
        Gson gson = new Gson();
        GrupoProfesor gp = gson.fromJson(jsonGrupoProfesor, GrupoProfesor.class);
        ControllerGrupoProfesor cgp = new ControllerGrupoProfesor();
        try {
            gp = cgp.add(gp);
            out = gson.toJson(gp);
        } catch (Exception e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    @Path("deleteGrupoProfesor/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @DELETE
    public Response deleteGrupoProfesor(@HeaderParam("Authorization") String authHeader, @PathParam("id") int id) {
        Response authResponse = AuthenticationUtil.validateTokenAndRoleOrReturnError(authHeader, "school-services");
        if (authResponse != null) {
            return authResponse;
        }

        String out;
        ControllerGrupoProfesor cgp = new ControllerGrupoProfesor();
        try {
            cgp.delete(id);
            out = "{\"message\":\"Registro eliminado exitosamente\"}";
        } catch (Exception e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    @Path("getProfesoresByGrupoId/{grupoId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getProfesoresByGrupoId(@HeaderParam("Authorization") String authHeader, @PathParam("grupoId") int grupoId) {
        Response authResponse = AuthenticationUtil.validateTokenAndRoleOrReturnError(authHeader, "school-services");
        if (authResponse != null) {
            return authResponse;
        }

        String out;
        ControllerGrupoProfesor cgp = new ControllerGrupoProfesor();
        try {
            out = new Gson().toJson(cgp.getProfesoresByGrupoId(grupoId));
        } catch (Exception e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }

    @Path("getGruposByProfesor")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public Response getGruposByProfesor(@HeaderParam("Authorization") String authHeader) {
        Response authResponse = AuthenticationUtil.validateTokenAndRoleOrReturnError(authHeader, "teacher");
        if (authResponse != null) {
            return authResponse;
        }

        String out;
        try {
            String profesorId = AuthenticationUtil.getSubjectAsString(authHeader);

            ControllerGrupoProfesor cgp = new ControllerGrupoProfesor();
            out = new Gson().toJson(cgp.getGruposByProfesorId(profesorId));
        } catch (Exception e) {
            out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
}