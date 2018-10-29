package net.iessochoa.joseantoniolopez.p5ejemplodb.model;

/**
 * POJO de la tabla Alumnos
 */

public class Alumno {


    private String id;
    private String nombre;
    private String grupo;
    private String fotoUri;

    public Alumno(String nombre, String grupo, String fotoUri) {
        this.nombre = nombre;
        this.grupo = grupo;
        this.fotoUri = fotoUri;
    }
    public Alumno(String id, String nombre, String grupo, String fotoUri) {
        this.id=id;
        this.nombre = nombre;
        this.grupo = grupo;
        this.fotoUri = fotoUri;
    }

    public String getId() {
        return id;
    }
    public String getNombre() {
        return nombre;
    }

    public String getGrupo() {
        return grupo;
    }

    public String getFotoUri() {
        return fotoUri;
    }
}
