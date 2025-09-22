/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.idgs703.serviciosEscolares.util;

import com.google.gson.Gson;
import jakarta.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Utility class for handling authentication and token validation
 * @author Sandro
 */
public class AuthenticationUtil {
    
    private static final String AUTH_SERVICE_URL = "http://authentication/validate-token";
    
    /**
     * Validates a bearer token by calling the authentication service and returns detailed information
     * @param authHeader The Authorization header containing the bearer token
     * @return TokenValidationResult containing validation status and user information
     */
    public static TokenValidationResult validateTokenWithDetails(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return new TokenValidationResult();
        }
        
        String token = authHeader.substring(7); // Remove "Bearer " prefix
        
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(AUTH_SERVICE_URL))
                    .header("Authorization", "Bearer " + token)
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();
            
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                // Parse JSON response
                Gson gson = new Gson();
                AuthResponse authResponse = gson.fromJson(response.body(), AuthResponse.class);
                
                if (authResponse != null && authResponse.isValid() && authResponse.getData() != null) {
                    return new TokenValidationResult(
                        true,
                        authResponse.getData().getRole(),
                        authResponse.getData().getSub(),
                        authResponse.getData().getExp()
                    );
                }
            }
            
            return new TokenValidationResult();
        } catch (IOException | InterruptedException e) {
            // Log the error in a real application
            System.err.println("Error validating token: " + e.getMessage());
            return new TokenValidationResult();
        }
    }
    
    /**
     * Validates a bearer token by calling the authentication service
     * @param authHeader The Authorization header containing the bearer token
     * @return true if the token is valid, false otherwise
     */
    public static boolean validateToken(String authHeader) {
        return validateTokenWithDetails(authHeader).isValid();
    }
    
    /**
     * Creates an unauthorized response with a standard error message
     * @return Response with 401 status and error message
     */
    public static Response createUnauthorizedResponse() {
        String errorResponse = """
            {"error":"Token de autorización inválido o expirado"}
            """;
        return Response.status(Response.Status.UNAUTHORIZED).entity(errorResponse).build();
    }
    
    /**
     * Validates the authorization header and returns an error response if invalid
     * @param authHeader The Authorization header to validate
     * @return null if token is valid, or an unauthorized Response if invalid
     */
    public static Response validateTokenOrReturnError(String authHeader) {
        if (!validateToken(authHeader)) {
            return createUnauthorizedResponse();
        }
        return null;
    }
    
    /**
     * Validates token and checks if user has required role
     * @param authHeader The Authorization header to validate
     * @param requiredRole The role required to access the resource
     * @return null if authorized, or an error Response if unauthorized/forbidden
     */
    public static Response validateTokenAndRoleOrReturnError(String authHeader, String requiredRole) {
        TokenValidationResult result = validateTokenWithDetails(authHeader);
        
        if (!result.isValid()) {
            return createUnauthorizedResponse();
        }
        
        if (!result.hasRole(requiredRole)) {
            return createForbiddenResponse();
        }
        
        return null;
    }
    
    /**
     * Validates token and checks if user has any of the required roles
     * @param authHeader The Authorization header to validate
     * @param allowedRoles The roles allowed to access the resource
     * @return null if authorized, or an error Response if unauthorized/forbidden
     */
    public static Response validateTokenAndAnyRoleOrReturnError(String authHeader, String... allowedRoles) {
        TokenValidationResult result = validateTokenWithDetails(authHeader);
        
        if (!result.isValid()) {
            return createUnauthorizedResponse();
        }
        
        if (!result.hasAnyRole(allowedRoles)) {
            return createForbiddenResponse();
        }
        
        return null;
    }
    
    /**
     * Creates a forbidden response for insufficient privileges
     * @return Response with 403 status and error message
     */
    public static Response createForbiddenResponse() {
        String errorResponse = """
            {"error":"No tiene permisos suficientes para realizar esta acción"}
            """;
        return Response.status(Response.Status.FORBIDDEN).entity(errorResponse).build();
    }

    /**
     * Gets the subject (user ID) from the token as an integer
     * @param authHeader The Authorization header containing the bearer token
     * @return subject como int, o lanza excepción si no es válido
     */
    public static int getSubjectAsInt(String authHeader) throws Exception {
        TokenValidationResult result = validateTokenWithDetails(authHeader);
        if (!result.isValid() || result.getSubject() == null) {
            throw new Exception("Token inválido o sin subject");
        }
        return Integer.parseInt(result.getSubject());
    }

    /**
     * Gets the subject (user ID) from the token as a string
     * @param token The JWT token
     * @return subject como String, o lanza excepción si no es válido
     */
    public static String getSubjectAsString(String authHeader) throws Exception {
        TokenValidationResult result = validateTokenWithDetails(authHeader);
        if (!result.isValid() || result.getSubject() == null) {
            throw new Exception("Token inválido o sin subject");
        }
        return result.getSubject();   
    }
}