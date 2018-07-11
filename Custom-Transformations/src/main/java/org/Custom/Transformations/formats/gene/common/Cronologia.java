package org.Custom.Transformations.formats.gene.common;

public class Cronologia {
    private Integer any_inici;
    private Integer any_fi;
    private String descripcio;
    private String codi;

    public Integer getAny_inici() {
        return any_inici;
    }

    public void setAny_inici(Integer any_inici) {
        this.any_inici = any_inici;
    }

    public Integer getAny_fi() {
        return any_fi;
    }

    public void setAny_fi(Integer any_fi) {
        this.any_fi = any_fi;
    }

    public String getDescripcio() {
        return descripcio;
    }

    public void setDescripcio(String descripcio) {
        this.descripcio = descripcio;
    }

    public String getCodi() {
        return codi;
    }

    public void setCodi(String codi) {
        this.codi = codi;
    }

    public Cronologia(String codi, Integer any_inici, Integer any_fi, String descripcio){
        this.setCodi(codi);
        this.setAny_inici(any_inici);
        this.setAny_fi(any_fi);
        this.setDescripcio(descripcio);
    }

}
