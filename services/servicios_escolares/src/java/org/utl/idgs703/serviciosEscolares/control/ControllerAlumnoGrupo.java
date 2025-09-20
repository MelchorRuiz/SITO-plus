package org.utl.idgs703.serviciosEscolares.control;

import org.utl.idgs703.serviciosEscolares.model.AlumnoGrupo;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.utl.idgs703.serviciosEscolares.db.ConexionMySQL;

public class ControllerAlumnoGrupo {

	public AlumnoGrupo add(AlumnoGrupo ag) throws SQLException {
		String sql = "INSERT INTO alumno_grupo (matricula, alumno_nombre, grupo_id, estatus) VALUES (?, ?, ?, ?);";
		ConexionMySQL connMySQL = new ConexionMySQL();
		Connection conn = connMySQL.open();
		PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		pstmt.setInt(1, ag.getMatricula());
		pstmt.setString(2, ag.getAlumnoNombre());
		pstmt.setInt(3, ag.getGrupoId());
		pstmt.setInt(4, 1);
		pstmt.executeUpdate();
		ResultSet rs = pstmt.getGeneratedKeys();
		if (rs.next()) {
			ag.setId(rs.getInt(1));
		}
		rs.close();
		pstmt.close();
		connMySQL.close();
		return ag;
	}

	public void delete(int id) throws SQLException {
		String sql = "UPDATE alumno_grupo SET estatus=0 WHERE id=?;";
		ConexionMySQL connMySQL = new ConexionMySQL();
		Connection conn = connMySQL.open();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, id);
		pstmt.executeUpdate();
		pstmt.close();
		connMySQL.close();
	}

    public List<AlumnoGrupo> getGruposByMatricula(int matricula) throws SQLException {
		List<AlumnoGrupo> lista = new ArrayList<>();
		String sql = "SELECT ag.id, ag.matricula, ag.alumno_nombre, ag.grupo_id, g.nombre_grupo, ag.estatus FROM alumno_grupo ag JOIN grupo g ON ag.grupo_id = g.id_grupo WHERE ag.matricula = ? AND ag.estatus = 1;";
		ConexionMySQL connMySQL = new ConexionMySQL();
		Connection conn = connMySQL.open();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, matricula);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			AlumnoGrupo ag = new AlumnoGrupo(
				rs.getInt("id"),
				rs.getInt("matricula"),
				rs.getString("alumno_nombre"),
				rs.getInt("grupo_id"),
				rs.getString("nombre_grupo"),
				rs.getInt("estatus")
			);
			lista.add(ag);
		}
		rs.close();
		pstmt.close();
		connMySQL.close();
		return lista;
	}

	public int getNumeroAlumnosByGrupoId(int grupoId) throws SQLException {
		String sql = "SELECT COUNT(*) AS total FROM alumno_grupo WHERE grupo_id = ? AND estatus = 1;";
		ConexionMySQL connMySQL = new ConexionMySQL();
		Connection conn = connMySQL.open();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, grupoId);
		ResultSet rs = pstmt.executeQuery();
		int total = 0;
		if (rs.next()) {
			total = rs.getInt("total");
		}
		rs.close();
		pstmt.close();
		connMySQL.close();
		return total;
	}

    public List<AlumnoGrupo> getAlumnosByGrupoId(int grupoId) throws SQLException {
		List<AlumnoGrupo> lista = new ArrayList<>();
		String sql = "SELECT ag.id, ag.matricula, ag.alumno_nombre, ag.grupo_id, g.nombre_grupo, ag.estatus FROM alumno_grupo ag JOIN grupo g ON ag.grupo_id = g.id_grupo WHERE ag.grupo_id = ? AND ag.estatus = 1;";
		ConexionMySQL connMySQL = new ConexionMySQL();
		Connection conn = connMySQL.open();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		pstmt.setInt(1, grupoId);
		ResultSet rs = pstmt.executeQuery();
		while (rs.next()) {
			AlumnoGrupo ag = new AlumnoGrupo(
				rs.getInt("id"),
				rs.getInt("matricula"),
				rs.getString("alumno_nombre"),
				rs.getInt("grupo_id"),
				rs.getString("nombre_grupo"),
				rs.getInt("estatus")
			);
			lista.add(ag);
		}
		rs.close();
		pstmt.close();
		connMySQL.close();
		return lista;
	}
}
