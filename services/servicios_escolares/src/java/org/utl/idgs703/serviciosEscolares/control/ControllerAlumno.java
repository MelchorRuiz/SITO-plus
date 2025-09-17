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
import org.utl.idgs703.serviciosEscolares.model.Alumno;
import org.utl.idgs703.serviciosEscolares.model.Grupo;
import org.utl.idgs703.serviciosEscolares.model.Carrera;

/**
 *
 * @author Sandro
 */
public class ControllerAlumno {
    
    public Alumno update(Alumno a) throws SQLException {
        String sql = "{CALL actualizarAlumno(?,?,?,?,?,?)};";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();

        CallableStatement csmt = conn.prepareCall(sql);
        csmt.setInt(1, a.getMatricula());
        csmt.setString(2, a.getNombre());
        csmt.setInt(3, a.getUsuario());
        csmt.setString(4, a.getContrasenia());
        csmt.setInt(5, a.getCarrera().getId_carrera());
        if (a.getGrupo() != null) {
            csmt.setInt(6, a.getGrupo().getId_grupo());
        } else {
            csmt.setNull(6, Types.INTEGER);
        }

        csmt.executeUpdate();

        csmt.close();
        connMySQL.close();

        return a;
    }
    
    public Alumno add(Alumno a) throws SQLException {
        String sql = "CALL agregarAlumno( ?,?,?,?,?,?);";

        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();

        CallableStatement csmt = conn.prepareCall(sql);
        csmt.setInt(1, a.getMatricula());
        csmt.setString(2, a.getNombre());
        csmt.setInt(3, a.getUsuario());
        csmt.setString(4, a.getContrasenia());
        csmt.setInt(5, a.getCarrera().getId_carrera());
        if (a.getGrupo() != null) {
            csmt.setInt(6, a.getGrupo().getId_grupo());
        } else {
            csmt.setNull(6, Types.INTEGER);
        }

        csmt.executeUpdate();

        csmt.close();
        connMySQL.close();

        return a;
    }
    
    public void delete(int matricula) throws SQLException {
        String sql = "{CALL eliminarAlumno(?)};";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();

        CallableStatement csmt = conn.prepareCall(sql);
        csmt.setInt(1, matricula);

        csmt.executeUpdate();

        csmt.close();
        connMySQL.close();
    }
    
    public void assignToGroup(int matricula, int grupoId) throws SQLException {
        String sql = "{CALL asignarAlumnoGrupo(?,?)};";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();

        CallableStatement csmt = conn.prepareCall(sql);
        csmt.setInt(1, matricula);
        csmt.setInt(2, grupoId);

        csmt.executeUpdate();

        csmt.close();
        connMySQL.close();
    }
    
    public void removeFromGroup(int matricula) throws SQLException {
        String sql = "{CALL removerAlumnoGrupo(?)};";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        Connection conn = connMySQL.open();

        CallableStatement csmt = conn.prepareCall(sql);
        csmt.setInt(1, matricula);

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
    
    private Grupo locateGrupo(int idGrupo) throws SQLException{
        ControllerGrupo controllerGroup = new ControllerGrupo();
        List<Grupo> grupos = controllerGroup.getAll();
        for (Grupo g : grupos) {
            if (g.getId_grupo() == idGrupo) {
                return g;
            }
        }
        return null;   
    }
    
    private Alumno fill (ResultSet rs) throws SQLException{
        Alumno alumno = new Alumno();
        alumno.setMatricula(rs.getInt("matricula"));
        alumno.setNombre(rs.getString("nombre"));
        alumno.setUsuario(rs.getInt("nombre_usuario"));
        alumno.setContrasenia(rs.getString("contrasenia"));
        alumno.setEstatus(rs.getInt("estatus"));
        alumno.setCarrera(locateCarrera(rs.getInt("carrera_id")));
        alumno.setGrupo(locateGrupo(rs.getInt("grupo_id")));
        return alumno;
    }
    
    public List<Alumno> getAll() throws SQLException{
        List<Alumno> alumnos = new ArrayList<Alumno>();
        String sql = "SELECT * FROM alumno WHERE estatus = 1;";
        
        ConexionMySQL connMySQL = new ConexionMySQL();
        
        Connection conn = connMySQL.open();
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        
        ResultSet rs = pstmt.executeQuery();
        
        while(rs.next()){
            alumnos.add(fill(rs));
        }
        rs.close();
        pstmt.close();
        connMySQL.close();
        return alumnos;
    }
}