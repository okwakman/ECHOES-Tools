package org.Custom.Transformations.formats.gene.arqueologia;

import java.util.HashMap;

public class ProteccioClassificacioType extends HashMap<Integer, String> {
    public ProteccioClassificacioType()
    {
        this.put(1, "Incoat BCIN");
        this.put(2, "Declarat BCIN");
        this.put(4, "BCIL");
        this.put(5, "Declarat EPA");
        this.put(6, "Plans d'ordenació");
        this.put(7, "Altres");
        this.put(8, "Patrimoni Mundial");
        this.put(18, "Pendent comprovació");
    }
}
