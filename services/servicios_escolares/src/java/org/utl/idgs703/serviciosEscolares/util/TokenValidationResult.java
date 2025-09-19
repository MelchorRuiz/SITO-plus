/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.idgs703.serviciosEscolares.util;

/**
 * Result of token validation containing validity status and user information
 * @author Sandro
 */
public class TokenValidationResult {
    private boolean valid;
    private String role;
    private String subject;
    private long expiration;
    
    public TokenValidationResult() {
        this.valid = false;
    }
    
    public TokenValidationResult(boolean valid, String role, String subject, long expiration) {
        this.valid = valid;
        this.role = role;
        this.subject = subject;
        this.expiration = expiration;
    }
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    public long getExpiration() {
        return expiration;
    }
    
    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }
    
    /**
     * Check if the user has a specific role
     * @param requiredRole The role to check
     * @return true if user has the required role
     */
    public boolean hasRole(String requiredRole) {
        return valid && role != null && role.equals(requiredRole);
    }
    
    /**
     * Check if the user has any of the specified roles
     * @param allowedRoles Array of allowed roles
     * @return true if user has any of the allowed roles
     */
    public boolean hasAnyRole(String... allowedRoles) {
        if (!valid || role == null) {
            return false;
        }
        
        for (String allowedRole : allowedRoles) {
            if (role.equals(allowedRole)) {
                return true;
            }
        }
        return false;
    }
}