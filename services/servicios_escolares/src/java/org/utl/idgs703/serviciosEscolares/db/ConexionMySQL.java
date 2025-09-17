/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.idgs703.serviciosEscolares.db;

/**
 *
 * @author Sandro
 */

import java.sql.Connection;
import java.sql.DriverManager;

public class ConexionMySQL {
    Connection conn;
    
    public Connection open(){
        String user = "root";
        String password = "root";
        
        String url = "jdbc:mysql://localhost:3306/servicios_escolares?useSSL=false&useUnicode=true&characterEncoding=utf-8";
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            conn = DriverManager.getConnection(url,user,password);
            return conn;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void close(){
        try {
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("Excepcion controlada");
        }
    }
}
