package org.Custom.Transformations.formats.gene;

import org.Custom.Transformations.formats.common.CSV;

import java.util.HashMap;
import java.util.Map;

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
        Map<String, String> fields = new HashMap<>();
        fields.put("codi", "CODI");
        fields.put("tipus_registre", "");
        fields.put("nom_actual", "nom_actual");
        fields.put("agregat", "");
        // TODO: Utilitzar els separats: "Nom_Municipi" i "Nom_Comarca"
        fields.put("Municipi_Comarca", "MUNICIPIS_COMARQUES");
        fields.put("Proteccions", "");
        fields.put("cod_sstt", "");
        fields.put("data_fi", "ANY_FI");
        fields.put("descripcio", "descripcio");
        fields.put("nom", "NOM_AUTOR");
        fields.put("usr_alta", "");
        fields.put("d_alta", "DATA_ALTA");
        fields.put("d_mod", "DATA_ACTUALITZ");
        fields.put("num_jaciment", "CODI");
        fields.put("nom_altres", "nom_altres");
        fields.put("coor_utm_long", "coor_utm_long");
        fields.put("coor_utm_lat", "coor_utm_lat");
        fields.put("Materials", "MATERIAL");
        fields.put("context_desc", "context_desc");
        fields.put("tip_jac", "SITUACIO");
        fields.put("CRONO_INICI", "CRONO_INICI");
        fields.put("CRONO_FI", "CRONO_FI");
        fields.put("Cronologies", "");
        fields.put("data_inici", "ANY_INI");
        // TODO: No tenim Notícies Històriques
        fields.put("tip_noticia", "");
        fields.put("data", "");
        fields.put("notes", "");
        fields.put("tip_conservació", "conservacio");
        fields.put("consDescripció", "");
        fields.put("Numero_bcin", "NUM_BCIN");
        fields.put("Numero_bic", "NUM_BIC");
        fields.put("tip_prot_legal", "");
        fields.put("class_prot_legal", "");
        fields.put("tip-regim", "CATEGORIAS_PROT");
        fields.put("cod_arq", "CODI");
        fields.put("altres_noms", "altres_noms");
        fields.put("adreça", "adreca");
        fields.put("utm_x", "utm_x");
        fields.put("utm_y", "utm_y");
        fields.put("cod_arq_utilitzacio", "");
        fields.put("original_actual", "US_ESPECIFIC");
        fields.put("Epoques", "");
        fields.put("data_inicial", "ANY_INI");
        fields.put("cod_estil", "ESTILS");
        fields.put("cognoms", "COGNOM_AUTOR");
        fields.put("funcio", "FUNCIO_AUTOR");
        fields.put("any_inici", "ANY_INI");
        fields.put("any_fi", "ANY_FI");
        // TODO: No tenim Notícies Històriques
        fields.put("noticies_històriques", "");
        fields.put("cod_estat_global", "conservacio");
        fields.put("conservacio", "conservacio");
        fields.put("classificacio", "");
        fields.put("entorn", "");
        fields.put("cod_regim", "CATEGORIA_PROT");
        fields.put("num_reg_estatal", "NUM_BIC");
        fields.put("ct_cultura_bcil", "NUM_BCIN");
        this.setHeader(fields);

    }
}
