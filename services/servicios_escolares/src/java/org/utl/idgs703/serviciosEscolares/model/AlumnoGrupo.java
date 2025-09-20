package org.utl.idgs703.serviciosEscolares.model;

public class AlumnoGrupo {
	private int id;
	private int matricula;
	private String alumnoNombre;
	private int grupoId;
	private String nombreGrupo;
	private int estatus;

	public AlumnoGrupo() {
	}

	public AlumnoGrupo(int id, int matricula, String alumnoNombre, int grupoId, String nombreGrupo, int estatus) {
		this.id = id;
		this.matricula = matricula;
		this.alumnoNombre = alumnoNombre;
		this.grupoId = grupoId;
		this.nombreGrupo = nombreGrupo;
		this.estatus = estatus;
	}
	public String getNombreGrupo() {
		return nombreGrupo;
	}

	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getMatricula() {
		return matricula;
	}

	public void setMatricula(int matricula) {
		this.matricula = matricula;
	}

	public String getAlumnoNombre() {
		return alumnoNombre;
	}

	public void setAlumnoNombre(String alumnoNombre) {
		this.alumnoNombre = alumnoNombre;
	}

	public int getGrupoId() {
		return grupoId;
	}

	public void setGrupoId(int grupoId) {
		this.grupoId = grupoId;
	}

	public int getEstatus() {
		return estatus;
	}

	public void setEstatus(int estatus) {
		this.estatus = estatus;
	}

	@Override
	public String toString() {
		return "AlumnoGrupo{" + "id=" + id + ", matricula=" + matricula + ", alumnoNombre='" + alumnoNombre + '\'' + ", grupoId=" + grupoId + ", nombreGrupo='" + nombreGrupo + '\'' + ", estatus=" + estatus + '}';
	}
}
