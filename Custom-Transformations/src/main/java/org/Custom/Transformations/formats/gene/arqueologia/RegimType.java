package org.Custom.Transformations.formats.gene.arqueologia;

import java.util.HashMap;

public class RegimType extends HashMap<String, String>{
    public RegimType() {
        this.put("1.", "Pública");
        this.put("1.1.", "Pública Nacional");
        this.put("1.2.", "Pública Diputació");
        this.put("1.3.", "Pública Municipal");
        this.put("1.4.", "Pública Consell Comarcal");
        this.put("1.5.", "Pública Estatal");
        this.put("2.", "Eclesiàstica");
        this.put("3.", "Privada");
        this.put("3.1.", "Privada Institució");
        this.put("3.2.", "Privada Particular");
    }
}
