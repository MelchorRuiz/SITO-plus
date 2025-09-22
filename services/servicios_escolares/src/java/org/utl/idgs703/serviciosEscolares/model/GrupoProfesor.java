package org.utl.idgs703.serviciosEscolares.model;

public class GrupoProfesor {
    private int id;
    private String profesorId;
    private String profesorNombre;
    private int grupoId;
    private int estatus;

    // Constructors
    public GrupoProfesor() {}

    public GrupoProfesor(int id, String profesorId, String profesorNombre, int grupoId, int estatus) {
        this.id = id;
        this.profesorId = profesorId;
        this.profesorNombre = profesorNombre;
        this.grupoId = grupoId;
        this.estatus = estatus;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProfesorId() {
        return profesorId;
    }

    public void setProfesorId(String profesorId) {
        this.profesorId = profesorId;
    }

    public String getProfesorNombre() {
        return profesorNombre;
    }

    public void setProfesorNombre(String profesorNombre) {
        this.profesorNombre = profesorNombre;
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
        return "GrupoProfesor{" +
                "id=" + id +
                ", profesorId=" + profesorId +
                ", profesorNombre='" + profesorNombre + '\'' +
                ", grupoId=" + grupoId +
                ", estatus=" + estatus +
                '}';
    }
}