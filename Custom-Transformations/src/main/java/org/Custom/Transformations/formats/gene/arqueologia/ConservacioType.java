package org.Custom.Transformations.formats.gene.arqueologia;

import java.util.HashMap;

public class ConservacioType extends HashMap<Integer, String> {
    public ConservacioType(){
        this.put(1, "Excel·lent");
        this.put(2, "Bo");
        this.put(3, "Regular");
        this.put(4, "Dolent");
        this.put(5, "Destruït");
        this.put(6, "Parcialment destruït");
        this.put(7, "Desconegut");
    }
}
