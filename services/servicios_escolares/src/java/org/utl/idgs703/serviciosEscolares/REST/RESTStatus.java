package org.utl.idgs703.serviciosEscolares.REST;

import com.google.gson.Gson;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("status")
public class RESTStatus {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStatus() {
        String out = null;
        try {
            Map<String, String> status = new HashMap<>();
            status.put("status", "ok");
            out = new Gson().toJson(status);
        } catch (Exception e) {
            out = "{\"error\":\"Error al obtener el estado del servicio\"}";
        }
        return Response.status(Response.Status.OK).entity(out).build();
    }
}
