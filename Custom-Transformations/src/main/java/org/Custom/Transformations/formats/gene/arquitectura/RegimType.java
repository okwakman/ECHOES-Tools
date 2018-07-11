package org.Custom.Transformations.formats.gene.arquitectura;

import java.util.HashMap;

public class RegimType extends HashMap<String, String>{
    public RegimType() {
        this.put("1", "Privada");
        this.put("2", "Pública");
        this.put("3", "Eclesiàstica");
        this.put("4", "Cooperativa");
        this.put("5", "Pendent");
    }
}
