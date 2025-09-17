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
public class ControllerGrupo {
    
    public Grupo update(Grupo g) throws SQLException {
        String sql = "{CALL actualizarGrupo(?,?,?)};";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();

        CallableStatement csmt = conn.prepareCall(sql);
        csmt.setString(1, g.getNombre_grupo());
        csmt.setInt(2, g.getCarrera().getId_carrera());
        csmt.setInt(3, g.getId_grupo());

        csmt.executeUpdate();

        csmt.close();
        connMySQL.close();

        return g;
    }
    
    public Grupo add(Grupo g) throws SQLException {
        String sql = "CALL agregarGrupo( ?,?,?);";
        int var_grupoId;

        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();

        CallableStatement csmt = conn.prepareCall(sql);
        csmt.setString(1, g.getNombre_grupo());
        csmt.setInt(2, g.getCarrera().getId_carrera());

        csmt.registerOutParameter(3, Types.INTEGER);

        csmt.executeUpdate();

        var_grupoId = csmt.getInt(3);

        g.setId_grupo(var_grupoId);
        csmt.close();
        connMySQL.close();

        return g;
    }
    
    public void delete(int id) throws SQLException {
        String sql = "{CALL eliminarGrupo(?)};";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();

        CallableStatement csmt = conn.prepareCall(sql);
        csmt.setInt(1, id);

        csmt.executeUpdate();

        csmt.close();
        connMySQL.close();
       }
    
    private Carrera locateCarrera(int idCarrera) throws SQLException{
        ControllerCarrera controllerstate = new ControllerCarrera();
        List<Carrera> carreras = controllerstate.getAll();
        for (Carrera c : carreras) {
            if (c.getId_carrera() == idCarrera) {
                return c;
            }
        }
        return null;   
    }
    
    private Grupo fill (ResultSet rs) throws SQLException{
        Grupo grupo = new Grupo();
        grupo.setId_grupo(rs.getInt("id_grupo"));
        grupo.setNombre_grupo(rs.getString("nombre_grupo"));
        grupo.setEstatus(rs.getInt("estatus"));
        grupo.setCarrera(locateCarrera(rs.getInt("carrera_id")));
        return grupo;
    }
    
    public List<Grupo> getAll() throws SQLException{
        List<Grupo> grupos = new ArrayList<Grupo>();
        String sql = "SELECT * FROM grupo WHERE estatus = 1;";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        Connection conn = connMySQL.open();
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        ResultSet rs = pstmt.executeQuery();
        
        while(rs.next()){
            grupos.add(fill(rs));
        }
        rs.close();
        pstmt.close();
        connMySQL.close();
        return grupos;
    }
}
