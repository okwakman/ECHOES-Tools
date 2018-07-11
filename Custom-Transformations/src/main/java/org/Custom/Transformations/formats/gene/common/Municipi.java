package org.Custom.Transformations.formats.gene.common;

public class Municipi {
    private String id;
    private String nom;
    private Integer id_comarca;

    public Municipi(String id, String nom, Integer id_comarca){
        this.id = id;
        this.nom = nom;
        this.id_comarca = id_comarca;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Integer getId_comarca() {
        return id_comarca;
    }

    public void setId_comarca(Integer id_comarca) {
        this.id_comarca = id_comarca;
    }
}
