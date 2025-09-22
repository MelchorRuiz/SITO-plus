package com.mycompany.recursos.humanos.service.security;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.List;

@Component
public class AuthValidator {
    private final String AUTH_URL = "http://authentication/validate-token";
    private final RestTemplate restTemplate = new RestTemplate();

    public void validate(HttpServletRequest request, List<String> allowedRoles) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token faltante");
        }
        String token = authHeader.substring(7);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        Map<String, Object> body = Map.of();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity(AUTH_URL, entity, Map.class);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error al validar el token");
        }
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        Map bodyResponse = response.getBody();
        if (bodyResponse == null || !(Boolean) bodyResponse.get("valid")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        Map data = (Map) bodyResponse.get("data");
        String userRole = (String) data.get("role");
        if (allowedRoles == null || !allowedRoles.contains(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado");
        }
    }

    public String validateAndGetUserId(HttpServletRequest request, List<String> allowedRoles) {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token faltante");
        }
        String token = authHeader.substring(7);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        Map<String, Object> body = Map.of();
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response;
        try {
            response = restTemplate.postForEntity(AUTH_URL, entity, Map.class);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Error al validar el token");
        }
        if (response.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        Map bodyResponse = response.getBody();
        if (bodyResponse == null || !(Boolean) bodyResponse.get("valid")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
        }
        Map data = (Map) bodyResponse.get("data");
        String userRole = (String) data.get("role");
        if (allowedRoles == null || !allowedRoles.contains(userRole)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Rol no autorizado");
        }
        return (String) data.get("sub");
    }
}
