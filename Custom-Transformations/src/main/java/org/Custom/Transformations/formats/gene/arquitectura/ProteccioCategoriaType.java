package org.Custom.Transformations.formats.gene.arquitectura;

import java.util.HashMap;

public class ProteccioCategoriaType extends HashMap<Integer, String> {
    public ProteccioCategoriaType()
    {
        this.put(0, "Pendent");
        this.put(1, "Incoat-BCIN");
        this.put(2, "BCIN");
        this.put(3, "BCIL");
        this.put(6, "Altres");
        this.put(8, "Entorn BCIN");
        this.put(9, "Pendent comprovació");
        this.put(10, "Plans d'ordenació");
    }
}
