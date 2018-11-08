package org.Custom.Transformations.formats.gene;

import org.Custom.Transformations.formats.common.CSV;

public class GENECSV extends CSV {
    public static final String[] fields = {
        // Comuns
        "codi", "tipus_registre", "nom_actual", "agregat", "Municipi_Comarca", "Proteccions", "cod_sstt", "data_fi", "descripcio", "nom", "usr_alta", "d_alta", "d_mod",
        // Arqueologia
        "num_jaciment", "nom_altres",
        "coor_utm_long", "coor_utm_lat", "context_desc", "tip_jac", "Cronologies", "data_inici", "tip_noticia", "data",
        "notes", "tip_conservació", "consDescripció", "tip_prot_legal", "class_prot_legal",
        "tip-regim",
        // Arquitectura
        "cod_arq", "altres_noms", "adreça",
        "utm_x", "utm_y", "cod_arq_utilitzacio", "original_actual", "Epoques", "data_inicial", "cod_estil", "cognoms", "funcio", "any_inici",
        "any_fi", "notícies_històriques", "cod_estat_global", "classificacio", "entorn",
        "cod_regim"
    };

    public GENECSV() {
        this.setHeader(fields);
    }
}
