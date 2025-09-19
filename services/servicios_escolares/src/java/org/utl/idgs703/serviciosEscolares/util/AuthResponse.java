/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.idgs703.serviciosEscolares.util;

/**
 * Model for the authentication service response
 * @author Sandro
 */
public class AuthResponse {
    private boolean valid;
    private AuthData data;
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }
    
    public AuthData getData() {
        return data;
    }
    
    public void setData(AuthData data) {
        this.data = data;
    }
    
    public static class AuthData {
        private String sub;
        private String role;
        private long exp;
        
        public String getSub() {
            return sub;
        }
        
        public void setSub(String sub) {
            this.sub = sub;
        }
        
        public String getRole() {
            return role;
        }
        
        public void setRole(String role) {
            this.role = role;
        }
        
        public long getExp() {
            return exp;
        }
        
        public void setExp(long exp) {
            this.exp = exp;
        }
    }
}