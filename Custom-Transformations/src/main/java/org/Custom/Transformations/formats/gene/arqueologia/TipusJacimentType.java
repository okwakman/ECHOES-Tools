package org.Custom.Transformations.formats.gene.arqueologia;

import java.util.HashMap;

public class TipusJacimentType extends HashMap<String, String>{
    public TipusJacimentType() {
        this.put("1", "Prefitxa");
        this.put("2", "Jaciment arqueològic");
        this.put("3", "Resultat negatiu total");
        this.put("4", "Jaciment paleontològic");
        this.put("5", "Jaciment subaquàtic");
        this.put("6", "Element");
        this.put("9", "Pendent revisió");
        this.put("10", "Guerra Civil");
        this.put("12", "Resultat negatiu en superfície");
        this.put("13", "Resultat negatiu parcial");
        this.put("14", "Notícia històrica");
    }
}
