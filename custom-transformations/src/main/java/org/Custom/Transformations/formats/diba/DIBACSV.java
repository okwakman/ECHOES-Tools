package org.Custom.Transformations.formats.diba;

import org.Custom.Transformations.formats.common.CSV;

public class DIBACSV extends CSV {
    public static final String[] fields = {
            "NumFitxa", "Codi", "Poblacio", "Ambit", "Denom", "Ubicacio", "Titularitat", "Titularitat_per", "Tipologia", "UsActual", "Descripcio", "Observacions", "EstatConservacio", "NotesConservacio", "Autor", "Estil", "Any", "Segle", "Emplacament", "X", "Y", "UTM", "Alcada", "Acces", "Negatiu", "Altres", "Historia", "Bibliografia", "Proteccio", "DescrProtec", "Inventari", "Vincle", "AutorFitxa", "DataReg", "DataMod", "Mapa", "Fotografia", "Fotografia 2", "Fotografia 3"
    };

    public DIBACSV() {
        this.setHeader(fields);
    }
}
