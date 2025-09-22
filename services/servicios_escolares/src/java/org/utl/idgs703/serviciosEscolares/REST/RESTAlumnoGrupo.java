package org.utl.idgs703.serviciosEscolares.REST;

import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
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
import org.utl.idgs703.serviciosEscolares.model.AlumnoGrupo;
import org.utl.idgs703.serviciosEscolares.control.ControllerAlumnoGrupo;
import org.utl.idgs703.serviciosEscolares.util.AuthenticationUtil;

@Path("alumnogrupo")
public class RESTAlumnoGrupo {

	@Path("addAlumnoGrupo")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@POST
	public Response addAlumnoGrupo(@HeaderParam("Authorization") String authHeader, String jsonAlumnoGrupo) {
		Response authResponse = AuthenticationUtil.validateTokenAndRoleOrReturnError(authHeader, "school-services");
		if (authResponse != null) {
			return authResponse;
		}
		String out = null;
		Gson gson = new Gson();
		AlumnoGrupo ag = gson.fromJson(jsonAlumnoGrupo, AlumnoGrupo.class);
		ControllerAlumnoGrupo cag = new ControllerAlumnoGrupo();
		try {
			ag = cag.add(ag);
			out = gson.toJson(ag);
		} catch (SQLException e) {
			out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
		}
		return Response.status(Response.Status.OK).entity(out).build();
	}

	@Path("deleteAlumnoGrupo/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	@DELETE
	public Response deleteAlumnoGrupo(@HeaderParam("Authorization") String authHeader, @PathParam("id") int id) {
		Response authResponse = AuthenticationUtil.validateTokenAndRoleOrReturnError(authHeader, "school-services");
		if (authResponse != null) {
			return authResponse;
		}
		String out = null;
		ControllerAlumnoGrupo cag = new ControllerAlumnoGrupo();
		try {
			cag.delete(id);
			out = "{\"success\":\"AlumnoGrupo eliminado correctamente\"}";
		} catch (SQLException e) {
			out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
		}
		return Response.status(Response.Status.OK).entity(out).build();
	}

	@Path("getGruposByMatricula")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getGruposByMatricula(@HeaderParam("Authorization") String authHeader) {
		Response authResponse = AuthenticationUtil.validateTokenAndRoleOrReturnError(authHeader, "student");
		if (authResponse != null) {
			return authResponse;
		}
		String out = null;
		List<AlumnoGrupo> lista = null;
		ControllerAlumnoGrupo cag = new ControllerAlumnoGrupo();
		try {
			int matricula = AuthenticationUtil.getSubjectAsInt(authHeader);
			lista = cag.getGruposByMatricula(matricula);
			out = new Gson().toJson(lista);
		} catch (Exception e) {
			out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
		}
		return Response.status(Response.Status.OK).entity(out).build();
	}

	@Path("getAlumnosByGrupoId/{grupoId}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getAlumnosByGrupoId(@HeaderParam("Authorization") String authHeader, @PathParam("grupoId") int grupoId) {
		Response authResponse = AuthenticationUtil.validateTokenAndAnyRoleOrReturnError(authHeader, "school-services", "teacher");
		if (authResponse != null) {
			return authResponse;
		}
		String out = null;
		List<AlumnoGrupo> lista = null;
		ControllerAlumnoGrupo cag = new ControllerAlumnoGrupo();
		try {
			lista = cag.getAlumnosByGrupoId(grupoId);
			out = new Gson().toJson(lista);
		} catch (SQLException e) {
			out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
		}
		return Response.status(Response.Status.OK).entity(out).build();
	}

	@Path("getNumeroAlumnosByGrupoId/{grupoId}")
	@Produces(MediaType.APPLICATION_JSON)
	@GET
	public Response getNumeroAlumnosByGrupoId(@HeaderParam("Authorization") String authHeader, @PathParam("grupoId") int grupoId) {
		Response authResponse = AuthenticationUtil.validateTokenAndRoleOrReturnError(authHeader, "school-services");
		if (authResponse != null) {
			return authResponse;
		}
		String out = null;
		ControllerAlumnoGrupo cag = new ControllerAlumnoGrupo();
		try {
			int total = cag.getNumeroAlumnosByGrupoId(grupoId);
			out = "{\"total\":" + total + "}";
		} catch (SQLException e) {
			out = "{\"error\":\"Ocurrio un error. Intente más tarde\"}";
		}
		return Response.status(Response.Status.OK).entity(out).build();
	}
}
