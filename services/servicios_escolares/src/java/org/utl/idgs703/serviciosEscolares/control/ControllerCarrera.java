/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.idgs703.serviciosEscolares.control;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import org.utl.idgs703.serviciosEscolares.db.ConexionMySQL;
import org.utl.idgs703.serviciosEscolares.model.Carrera;
import org.utl.idgs703.serviciosEscolares.model.Grupo;

/**
 *
 * @author Sandro
 */
public class ControllerCarrera {
    
    public Carrera update(Carrera c) throws SQLException {
        String sql = "{CALL actualizarCarrera(?,?)};";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();

        CallableStatement csmt = conn.prepareCall(sql);
        csmt.setString(1, c.getNombre_carrera());
        csmt.setInt(2, c.getId_carrera());

        csmt.executeUpdate();

        csmt.close();
        connMySQL.close();

        return c;
    }
    
    public Carrera add(Carrera c) throws SQLException {
        String sql = "CALL agregarCarrera( ?,?);";
        int var_carreraId;

        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();

        CallableStatement csmt = conn.prepareCall(sql);
        csmt.setString(1, c.getNombre_carrera());

        csmt.registerOutParameter(2, Types.INTEGER);

        csmt.executeUpdate();

        var_carreraId = csmt.getInt(2);

        c.setId_carrera(var_carreraId);
        csmt.close();
        connMySQL.close();

        return c;
    }
    
    private Carrera fill (ResultSet rs) throws SQLException{
        Carrera carrera = new Carrera();
        carrera.setId_carrera(rs.getInt("id_carrera"));
        carrera.setNombre_carrera(rs.getString("nombre_carrera"));
        carrera.setEstatus(rs.getInt("estatus"));
        return carrera;
    }
    
    public List<Carrera> getAll() throws SQLException{
        List<Carrera> carreras = new ArrayList<Carrera>();
        String sql = "SELECT * FROM carrera WHERE estatus = 1;";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        Connection conn = connMySQL.open();
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        ResultSet rs = pstmt.executeQuery();
        
        while(rs.next()){
            carreras.add(fill(rs));
        }
        rs.close();
        pstmt.close();
        connMySQL.close();
        return carreras;
    }
}
