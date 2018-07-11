package org.Custom.Transformations.formats.gene.arquitectura;

import java.util.HashMap;

public class ConservacioType extends HashMap<Integer, String> {
    public ConservacioType(){
        this.put(1, "Bo");
        this.put(2, "Mitjà");
        this.put(3, "Dolent");
        this.put(4, "Ruïna");
        this.put(5, "Si");
        this.put(6, "No");
        this.put(7, "Desaparegut");
    }
}
