package org.utl.idgs703.serviciosEscolares.control;

import org.utl.idgs703.serviciosEscolares.db.ConexionMySQL;
import org.utl.idgs703.serviciosEscolares.model.GrupoProfesor;
import org.utl.idgs703.serviciosEscolares.model.Grupo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerGrupoProfesor {

    private Connection getConnection() throws SQLException {
        ConexionMySQL conexion = new ConexionMySQL();
        return conexion.open();
    }

    public GrupoProfesor add(GrupoProfesor grupoProfesor) throws SQLException {
        String query = "INSERT INTO grupo_profesor (profesor_id, profesor_nombre, grupo_id, estatus) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, grupoProfesor.getProfesorId());
            stmt.setString(2, grupoProfesor.getProfesorNombre());
            stmt.setInt(3, grupoProfesor.getGrupoId());
            stmt.setInt(4, 1);

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    grupoProfesor.setId(generatedKeys.getInt(1));
                }
            }
        }
        return grupoProfesor;
    }

    public void delete(int id) throws SQLException {
        String query = "UPDATE grupo_profesor SET estatus = 0 WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public List<GrupoProfesor> getProfesoresByGrupoId(int grupoId) throws SQLException {
        List<GrupoProfesor> grupoProfesores = new ArrayList<>();
        String query = "SELECT * FROM grupo_profesor WHERE grupo_id = ? AND estatus = 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, grupoId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    GrupoProfesor gp = new GrupoProfesor(
                            rs.getInt("id"),
                            rs.getString("profesor_id"),
                            rs.getString("profesor_nombre"),
                            rs.getInt("grupo_id"),
                            rs.getInt("estatus")
                    );
                    grupoProfesores.add(gp);
                }
            }
        }
        return grupoProfesores;
    }

    public List<Grupo> getGruposByProfesorId(String profesorId) throws SQLException {
        List<Grupo> grupos = new ArrayList<>();
        String query = "SELECT g.* FROM grupo_profesor gp " +
                       "JOIN grupo g ON gp.grupo_id = g.id_grupo " +
                       "WHERE gp.profesor_id = ? AND gp.estatus = 1 AND g.estatus = 1";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, profesorId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Grupo g = new Grupo();
                    g.setId_grupo(rs.getInt("id_grupo"));
                    g.setNombre_grupo(rs.getString("nombre_grupo"));
                    g.setEstatus(rs.getInt("estatus"));
                    grupos.add(g);
                }
            }
        }
        return grupos;
    }
}