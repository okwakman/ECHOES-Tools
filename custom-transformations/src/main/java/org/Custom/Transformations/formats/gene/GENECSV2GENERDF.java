
package org.Custom.Transformations.formats.gene;

import cat.gencat.*;
import net.sf.saxon.functions.IriToUri;
import org.Custom.Transformations.core.Convertible;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.csv.CSVRecord;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GENECSV2GENERDF extends Convertible<GENECSV, RDF> {
    private static final String SEPARATOR = " // ";
    private static final String CRONOLOGIA_SEPARATOR = " - ";
    private static final Pattern MUNICIPI_COMARCA_PATTERN = Pattern.compile("(.*)\\s+\\((.*)\\).*");
    private static final Pattern BCIN_PATTERN = Pattern.compile(".*BCIN \\((.*)\\).*");
    private static final Pattern BCIL_PATTERN = Pattern.compile(".*BCIL \\((.*)\\).*");
    private List<Territori> territoriTypes;
    private boolean isArchitecture;
    private String identificacio = "";
    private String provider;
    private HashMap<String, Territori> territoris;

    public GENECSV2GENERDF() {
        this.territoris = new HashMap<>();
        this.territoriTypes = new ArrayList<>();
    }

    private Identificacio getIdentificacio(CSVRecord record) {
        Identificacio id = new Identificacio();
        id.setAbout(this.getIdentifier("Identificador"));
        return id;
    }

    private InformacioFitxa getInformacioFitxa(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"d_alta", "d_mod", "usr_alta"})){
            return null;
        }
        InformacioFitxa infoFitxa = new InformacioFitxa();
        infoFitxa.setAbout(this.getIdentifier("InformacioFitxa"));
        if (this.strOrNull(record, "d_alta") != null){
            try {
                infoFitxa.setDataCreacioFitxa(getDate(this.strOrNull(record, "d_alta")));
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }
        if (this.strOrNull(record, "d_mod") != null){
            try {
                infoFitxa.setDataModificacioFitxa(getDate(this.strOrNull(record, "d_mod")));
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }
        if (this.literalOrNull(record, "usr_alta") != null){
            infoFitxa.setAutorFitxa(this.literalOrNull(record, "usr_alta"));
        }
        infoFitxa.setIdentificador(getIdentificador());
        return infoFitxa;
    }

    private String getIdentifier(String entity){
        return String.format("%s:%s", entity, UUID.randomUUID().toString());
    }

    public static String getIdentificadorIdentifier(boolean isArchitecture, String id){
        String tipus;
        if (isArchitecture){
            tipus = "Arquitectura";
        } else {
            tipus = "Arqueologia";
        }
        return IriToUri.iriToUri(String.format("Identificador:%s:%s", tipus, StringUtils.deleteWhitespace(id))).toString();
    }

    // Arqueologia
    private Property getArqueologiaPropietat(CSVRecord record, Identificacio idPare) {
        if (!hasSomeValue(record, new String[] {"num_jaciment"})){
            return null;
        }
        Property id = new Property();
        id.setSubClassOf(stringToResourceType(idPare.getAbout()));
        this.identificacio = getIdentificadorIdentifier(false, this.strOrNull(record, "num_jaciment"));
        id.setAbout(identificacio);
        {
            id.setTipusPatrimoni(PatrimoniTipus.ARQUEOLÒGIC);
            id.setCodiIntern(this.literalOrNull(record, "num_jaciment"));
            if (this.literalOrNull(record, "codi") != null){
                id.setCodiInventari(this.literalOrNull(record, "codi"));
            }
            id.setNom(this.literalOrNull(record, "nom_actual"));
            id.setAltresNoms(this.literalOrNull(record, "nom_altres"));
            id.setProveidor(this.stringToResourceType(this.provider));
        }
        return id;
    }

    private Localitzacio getArqueologiaLocalitzacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"Municipi_Comarca", "agregat", "cod_sstt", "coor_utm_long", "coor_utm_lat", "context_desc"})){
            return null;
        }
        Localitzacio loc = new Localitzacio();
        loc.setAbout(this.getIdentifier("Localitzacio"));
        {
            List<String> mcpis = new ArrayList<>();
            List<String> comarques = new ArrayList<>();
            String[] mcpis_comarques = this.getMultivaluedField(record, "Municipi_Comarca");

            if (mcpis_comarques != null){
                for (String mcpi_comarca : mcpis_comarques){
                    Matcher matcher = MUNICIPI_COMARCA_PATTERN.matcher(mcpi_comarca);
                    if (matcher.matches()){
                        mcpis.add(matcher.group(1));
                        comarques.add(matcher.group(2));
                    }
                }
            }

            for (int i = 0; i < mcpis.size(); i++){
                List<String> comarcaAsList = new ArrayList<>();
                comarcaAsList.add(comarques.get(i));
                ResourceType t = getTerritoriResource(mcpis.get(i), comarcaAsList);
                if (t != null){
                    loc.getTerritori().add(getTerritoriResource(mcpis.get(i), comarcaAsList));
                }
            }

            if (this.literalOrNull(record, "agregat") != null){
                loc.setAgregat(this.literalOrNull(record, "agregat"));
            }
            if (this.literalOrNull(record, "cod_sstt") != null){
                String cod_sstt = StringEscapeUtils.unescapeXml(this.strOrNull(record, "cod_sstt"));
                loc.setServeiTerritorial(getEnumValue(ServeiTerritorialType.class, cod_sstt));
            }
            if (this.literalOrNull(record, "coor_utm_long") != null){
                loc.setX(Float.valueOf(this.strOrNull(record, "coor_utm_long").replace(",", ".")));
            }
            if (this.literalOrNull(record, "coor_utm_lat") != null){
                loc.setY(Float.valueOf(this.strOrNull(record, "coor_utm_lat").replace(",", ".")));
            }
            loc.setLocDescripcio(this.literalOrNull(record, "context_desc"));
            loc.setIdentificador(getIdentificador());
        }
        return loc;
    }

    private List<Tipologia> getArqueologiaTipologies(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"tip_jac"})){
            return new ArrayList<>();
        }

        List<Tipologia> tipologies = new ArrayList<>();
        Tipologia tipi = new Tipologia();
        tipi.setTipologiaArqueologic(TipologiaArqueologicType.ELEMENT);
        tipi.setAbout(this.getIdentifier("Tipologia"));
        tipi.setIdentificador(getIdentificador());
        tipologies.add(tipi);

        String[] tipus_us = this.getMultivaluedField(record, "tip_jac");
        if (tipus_us != null) {
            for (String tip_us : tipus_us) {
                Tipologia tip = new Tipologia();
                tip.setAbout(this.getIdentifier("Tipologia"));
                tip.setIdentificador(getIdentificador());
                tip.setTipologiaArqueologic(getEnumValue(TipologiaArqueologicType.class, tip_us));
                if (tip.getTipologiaArqueologic() != null){
                    tipologies.add(tip);
                }
            }
        }
        return tipologies;
    }

    private Datacio getArqueologiaDatacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"Cronologies", "data_inici", "data_fi"})){
            return null;
        }
        Datacio dat = new Datacio();
        dat.setAbout(this.getIdentifier("Datacio"));
        dat.setIdentificador(getIdentificador());
        {
            String[] anys_inici = this.getMultivaluedField(record, "data_inici");
            String[] anys_fi = this.getMultivaluedField(record, "data_fi");
            String[] cronologies = this.getMultivaluedField(record, "Cronologies");

            if (anys_inici != null){
                for (String any_inici : anys_inici){
                    dat.getAnyInici().add(Integer.valueOf(any_inici));
                }
            }

            if (anys_fi != null) {
                for (String any_fi : anys_fi) {
                    dat.getAnyFi().add(Integer.valueOf(any_fi));
                }
            }

            if (cronologies != null){
                for (String cronologia : cronologies){
                    String[] cronologies_sep = cronologia.trim().split(Pattern.quote(CRONOLOGIA_SEPARATOR));
                    String cronologia_inicial = StringEscapeUtils.unescapeXml(cronologies_sep[0]);
                    String cronologia_final = "";

                    Object cronologiaInicial = null;
                    Object cronologiaFinal = null;
                    Object cronologiaTemporalFinal = null;

                    if (cronologies_sep.length == 2){
                        cronologia_final = StringEscapeUtils.unescapeXml(cronologies_sep[1]);
                    }
                    try {
                        cronologiaInicial = getEnumValue(CronologiaArqueologicType.class, cronologia_inicial);
                        cronologiaFinal = cronologiaInicial;
                    } catch (Exception ignored){
                        try {
                            cronologiaInicial = getEnumValue(EstilEpocaType.class, cronologia_inicial);
                            cronologiaFinal = cronologiaInicial;
                        } catch (Exception ignored2){
                        }
                    }
                    if (cronologiaInicial == null){
                        continue;
                    }

                    try {
                        cronologiaTemporalFinal = cronologiaFinal;
                        cronologiaFinal = getEnumValue(CronologiaArqueologicType.class, cronologia_final);
                    } catch (Exception ignored){
                        try {
                            cronologiaFinal = getEnumValue(EstilEpocaType.class, cronologia_final);
                        } catch (Exception ignored2){
                            cronologiaFinal = cronologiaTemporalFinal;
                        }
                    }

                    if (cronologiaFinal == null){
                        cronologiaFinal = cronologiaInicial;
                    }
                    dat.getCronologiaInicial().add(cronologiaInicial);
                    dat.getCronologiaFinal().add(cronologiaFinal);
                }
            }
        }
        return dat;
    }

    private Descripcio getArqueologiaDescripcio(CSVRecord record){
        if (!hasSomeValue(record, new String[] {"descripcio"})){
            return null;
        }
        Descripcio desc = new Descripcio();
        desc.setAbout(this.getIdentifier("Descripcio"));
        desc.setIdentificador(getIdentificador());
        desc.setDescDescripcio(this.literalOrNull(record, "descripcio"));
        return desc;
    }

    private NoticiaHistorica getArqueologiaNoticiaHistorica(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"tip_noticia", "data", "nom", "notes"})){
            return null;
        }
        NoticiaHistorica noticia = new NoticiaHistorica();
        noticia.setAbout(this.getIdentifier("NoticiesHistoriques"));
        noticia.setIdentificador(getIdentificador());
        {
            noticia.setTipusNoticiaHistorica(this.literalOrNull(record, "tip_noticia"));
            if (this.strOrNull(record, "data") != null){
                try {
                    noticia.setDataNoticiaHistorica(getDate(this.strOrNull(record, "data")));
                } catch (DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
            }
            noticia.setNomNoticiaHistorica(this.literalOrNull(record, "nom"));
            noticia.setComentariNoticiaHistorica(this.literalOrNull(record, "notes"));
        }
        return noticia;
    }

    private Conservacio getArqueologiaConservacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"tip_conservació", "consDescripció"})){
            return null;
        }
        Conservacio con = new Conservacio();
        con.setAbout(this.getIdentifier("Conservacio"));
        con.setIdentificador(getIdentificador());
        {
            String conservacio = this.strOrNull(record, "tip_conservació");
            if (conservacio != null) {
                con.setConservacioEstatArqueologic(getEnumValue(ConservacioEstatArqueologicType.class, conservacio));
            }
            con.setConservacioComentari(this.literalOrNull(record, "consDescripció"));
        }
        return con;
    }

    private String getBCIN(String[] proteccions){
        if (proteccions == null) return null;
        for (String proteccio : proteccions){
            Matcher matcher = BCIN_PATTERN.matcher(proteccio);
            if (matcher.matches()){
                return matcher.group(1);
            }
        }
        return null;
    }

    private Proteccio getArqueologiaProteccio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"class_prot_legal", "tip_prot_legal", "Proteccions", "num_reg_estatal"})){
            return null;
        }
        Proteccio proteccio = new Proteccio();
        proteccio.setAbout(this.getIdentifier("Proteccio"));
        proteccio.setClassificacioArqueologic(ClassificacioArqueologicType.DECLARAT_BCIN);
        proteccio.setIdentificador(getIdentificador());
        {
            String[] classificacions = this.getMultivaluedField(record, "class_prot_legal");
            if (classificacions != null) {
                for (String classificacio : classificacions) {
                    ClassificacioArqueologicType classArque = ClassificacioArqueologicType.fromValue(classificacio);
                    proteccio.setClassificacioArqueologic(classArque);
                }
            }
            proteccio.setBCIN(this.stringToLiteralType(this.getBCIN(this.getMultivaluedField(record, "Proteccions"))));
            proteccio.setPCC(this.stringToLiteralType(this.getBCIL(this.getMultivaluedField(record, "Proteccions"))));
            proteccio.setBIC(this.literalOrNull(record, "num_reg_estatal"));
            proteccio.setProteccio(this.literalOrNull(record, "tip_prot_legal"));
        }
        return proteccio;
    }

    private Propietari getArqueologiaPropietari(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"tip-regim"})){
            return null;
        }
        Propietari propietari = new Propietari();
        propietari.setAbout(this.getIdentifier("Propietari"));
        propietari.setIdentificador(getIdentificador());
        {
            String tipusRegim = this.strOrNull(record, "tip-regim");
            if (tipusRegim != null){
                propietari.setTipusRegimArqueologic(getEnumValue(PropietariArqueologicType.class, tipusRegim));
            }
        }
        return propietari;
    }

    // Arquitectura
    private Property getArquitecturaPropietat(CSVRecord record, Identificacio idPare) {
        if (!hasSomeValue(record, new String[] {"cod_arq", "nom_actual", "altres_noms"})){
            return null;
        }
        Property id = new Property();
        id.setSubClassOf(stringToResourceType(idPare.getAbout()));
        this.identificacio = getIdentificadorIdentifier(true, this.strOrNull(record, "cod_arq"));
        id.setAbout(identificacio);
        {
            id.setTipusPatrimoni(PatrimoniTipus.ARQUITECTÒNIC);
            id.setCodiIntern(this.literalOrNull(record, "cod_arq"));
            if (this.literalOrNull(record, "codi") != null){
                id.setCodiInventari(this.literalOrNull(record, "codi"));
            }
            id.setNom(this.literalOrNull(record, "nom_actual"));
            id.setAltresNoms(this.literalOrNull(record, "altres_noms"));
            id.setProveidor(this.stringToResourceType(this.provider));
        }
        return id;
    }

    private Localitzacio getArquitecturaLocalitzacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"Municipi_Comarca", "agregat", "cod_sstt", "utm_x", "utm_y"})){
            return null;
        }
        Localitzacio loc = new Localitzacio();
        loc.setAbout(this.getIdentifier("Localitzacio"));
        {
            List<String> mcpis = new ArrayList<>();
            List<String> comarques = new ArrayList<>();
            String[] mcpis_comarques = this.getMultivaluedField(record, "Municipi_Comarca");

            if (mcpis_comarques != null){
                for (String mcpi_comarca : mcpis_comarques){
                    Matcher matcher = MUNICIPI_COMARCA_PATTERN.matcher(mcpi_comarca);
                    if (matcher.matches()){
                        mcpis.add(matcher.group(1));
                        comarques.add(matcher.group(2));
                    }
                }
            }

            for (int i = 0; i < mcpis.size(); i++){
                List<String> comarcaAsList = new ArrayList<>();
                comarcaAsList.add(comarques.get(i));
                ResourceType t = getTerritoriResource(mcpis.get(i), comarcaAsList);
                if (t != null){
                    loc.getTerritori().add(t);
                }
            }

            if (this.literalOrNull(record, "agregat") != null){
                loc.setAgregat(this.literalOrNull(record, "agregat"));
            }
            if (this.literalOrNull(record, "cod_sstt") != null){
                String cod_sstt = StringEscapeUtils.unescapeXml(this.strOrNull(record, "cod_sstt"));
                loc.setServeiTerritorial(getEnumValue(ServeiTerritorialType.class, cod_sstt));
            }
            if (this.literalOrNull(record, "utm_x") != null){
                loc.setX(Float.valueOf(this.strOrNull(record, "utm_x").replace(",", ".")));
            }
            if (this.literalOrNull(record, "utm_y") != null){
                loc.setY(Float.valueOf(this.strOrNull(record, "utm_y").replace(",", ".")));
            }
            loc.setIdentificador(getIdentificador());
        }
        return loc;
    }

    private List<Tipologia> getArquitecturaTipologies(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"cod_arq_utilitzacio"})){
            return new ArrayList<>();
        }
        List<Tipologia> tipologies = new ArrayList<>();

        String[] tipus_us = this.getMultivaluedField(record, "cod_arq_utilitzacio");
        if (tipus_us != null) {
            for (String tip_us : tipus_us) {
                Tipologia tip = new Tipologia();
                tip.setAbout(this.getIdentifier("Tipologia"));
                tip.setIdentificador(getIdentificador());
                {
                    Tipologia tipo = new Tipologia();
                    tipo.setTipologiaArquitectonic(getEnumValue(TipologiaArquitectonicType.class, tip_us));
                }
                tipologies.add(tip);
            }
        }
        return tipologies;
    }

    private List<Us> getArquitecturaUsos(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"original_actual"})){
            return new ArrayList<>();
        }
        List<Us> usos = new ArrayList<>();
        String[] tipus_us = this.getMultivaluedField(record, "original_actual");
        if (tipus_us != null) {
            for (String tip_us : tipus_us) {
                Us us = new Us();
                us.setAbout(this.getIdentifier("Us"));
                us.setIdentificador(getIdentificador());
                {
                    UtilitzacioType tipusUtilitzacio = getEnumValue(UtilitzacioType.class, tip_us);
                    if (tipusUtilitzacio != null){
                        us.setTipusUtilitzacio(tipusUtilitzacio);
                        usos.add(us);
                        continue;
                    }
                    OriginalActualType tipusOriginalActual = getEnumValue(OriginalActualType.class, tip_us);
                    if (tipusOriginalActual != null){
                        us.setTipusOriginalActual(tipusOriginalActual);
                        usos.add(us);
                        continue;
                    }
                    us.setOriginalActualText(stringToLiteralType(tip_us));
                    usos.add(us);
                }
            }
        }
        return usos;
    }

    private Datacio getArquitecturaDatacio(CSVRecord record) {
            if (!hasSomeValue(record, new String[] {"Epoques", "data_inicial", "data_fi"})){
                return null;
            }
            Datacio dat = new Datacio();
            dat.setAbout(this.getIdentifier("Datacio"));
            dat.setIdentificador(getIdentificador());
            {
                String[] anys_inici = this.getMultivaluedField(record, "data_inicial");
                String[] anys_fi = this.getMultivaluedField(record, "data_fi");
                String[] cronologies = this.getMultivaluedField(record, "Epoques");

                if (anys_inici != null){
                    for (String any_inici : anys_inici){
                        dat.getAnyInici().add(Integer.valueOf(any_inici));
                    }
                }

                if (anys_fi != null) {
                    for (String any_fi : anys_fi) {
                        dat.getAnyFi().add(Integer.valueOf(any_fi));
                    }
                }


                if (cronologies != null){
                    for (String cronologia : cronologies){
                        String[] cronologies_sep = cronologia.trim().split(Pattern.quote(CRONOLOGIA_SEPARATOR));
                        String cronologia_inicial = StringEscapeUtils.unescapeXml(cronologies_sep[0]);
                        String cronologia_final = "";

                        Object cronologiaInicial = null;
                        Object cronologiaFinal = null;
                        Object cronologiaTemporalFinal = null;

                        if (cronologies_sep.length == 2){
                            cronologia_final = StringEscapeUtils.unescapeXml(cronologies_sep[1]);
                        }
                        try {
                            cronologiaInicial = getEnumValue(CronologiaArquitectonicType.class, cronologia_inicial);
                            cronologiaFinal = cronologiaInicial;
                        } catch (Exception ignored){
                            try {
                                cronologiaInicial = getEnumValue(EstilEpocaType.class, cronologia_inicial);
                                cronologiaFinal = cronologiaInicial;
                            } catch (Exception ignored2){
                            }
                        }
                        if (cronologiaInicial == null){
                            continue;
                        }

                        try {
                            cronologiaTemporalFinal = cronologiaFinal;
                            cronologiaFinal = getEnumValue(CronologiaArquitectonicType.class, cronologia_final);
                        } catch (Exception ignored){
                            try {
                                cronologiaFinal = getEnumValue(EstilEpocaType.class, cronologia_final);
                            } catch (Exception ignored2){
                                cronologiaFinal = cronologiaTemporalFinal;
                            }
                        }

                        if (cronologiaFinal == null){
                            cronologiaFinal = cronologiaInicial;
                        }

                        dat.getCronologiaInicial().add(cronologiaInicial);
                        dat.getCronologiaFinal().add(cronologiaFinal);
                    }
                }
            }
            return dat;
        }

    private List<Estil> getArquitecturaEstils(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"cod_estil"})){
            return new ArrayList<>();
        }
        List<Estil> estilsDesc = new ArrayList<>();
        String[] estils = this.getMultivaluedField(record, "cod_estil");
        if (estils != null) {
            for (String estil : estils) {
                Estil estilDesc = new Estil();
                estilDesc.setIdentificador(getIdentificador());
                estilDesc.setAbout(this.getIdentifier("Estil"));
                {
                    try {
                        estilDesc.setTipusEstilArquitectonic(getEnumValue(EstilArquitectonicType.class, estil));
                    } catch (Exception ignored){
                        try {
                            estilDesc.setTipusEstilEpoca(getEnumValue(EstilEpocaType.class, estil));
                        } catch (Exception ignored2){
                            estilDesc.setTipusEstilText(stringToLiteralType(estil));
                        }
                    }
                }
                estilsDesc.add(estilDesc);
            }
        }
        return estilsDesc;
    }

    private Autor getArquitecturaAutor(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"nom", "cognoms", "funcio", "any_inici", "any_fi"})){
            return null;
        }
        Autor autDesc = new Autor();
        autDesc.setAbout(this.getIdentifier("Autor"));
        autDesc.setIdentificador(getIdentificador());
        {
            String[] cognoms = this.getMultivaluedField(record, "cognoms");
            if (cognoms != null){
                for (String cognom : cognoms){
                    autDesc.getCognoms().add(this.stringToLiteralType(cognom));
                }
            }
            autDesc.setNoms(this.literalOrNull(record, "nom"));
            autDesc.setProfessio(this.literalOrNull(record, "funcio"));
            if (this.literalOrNull(record, "any_inici") != null){
                autDesc.setAnyInici(Integer.valueOf(this.strOrNull(record, "any_inici")));
            }

            if (this.literalOrNull(record, "any_fi") != null){
                autDesc.setAnyFi(Integer.valueOf(this.strOrNull(record, "any_fi")));
            }

        }
        return autDesc;
    }

    private Descripcio getArquitecturaDescripcio(CSVRecord record){
        if (!hasSomeValue(record, new String[] {"descripcio"})){
            return null;
        }
        Descripcio desc = new Descripcio();
        desc.setAbout(this.getIdentifier("Descripcio"));
        desc.setIdentificador(getIdentificador());
        desc.setDescDescripcio(this.literalOrNull(record, "descripcio"));
        return desc;
    }

    private NoticiaHistorica getArquitecturaNoticiaHistorica(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"noticies_històriques"})){
            return null;
        }
        NoticiaHistorica noticia = new NoticiaHistorica();
        noticia.setAbout(this.getIdentifier("NoticiesHistoriques"));
        noticia.setIdentificador(getIdentificador());
        {
            noticia.setComentariNoticiaHistorica(this.literalOrNull(record, "noticies_històriques"));
        }
        return noticia;
    }

    private Conservacio getArquitecturaConservacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"conservacio"})){
            return null;
        }
        Conservacio con = new Conservacio();
        con.setAbout(this.getIdentifier("Conservacio"));
        con.setIdentificador(getIdentificador());
        {
            String conservacio = this.strOrNull(record, "conservacio");
            if (conservacio != null) {
                con.setConservacioEstatArquitectonic(getEnumValue(ConservacioEstatArquitectonicType.class, conservacio));
                if (con.getConservacioEstatArquitectonic() != null) {
                    con.setConservacioComentari(this.stringToLiteralType(conservacio));
                }
            }
        }
        return con;
    }

    private Proteccio getArquitecturaProteccio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"classificacio", "entorn", "Numero_bcin", "Numero_bic", "ct_cultura_bcil", "Proteccions"})){
            return null;
        }
        Proteccio proteccio = new Proteccio();
        proteccio.setAbout(this.getIdentifier("Proteccio"));
        proteccio.setIdentificador(getIdentificador());
        {
            String[] classificacions = this.getMultivaluedField(record, "classificacio");
            if (classificacions != null) {
                for (String classificacio : classificacions) {
                    try {
                        ClassificacioArquitectonicType classArqui = ClassificacioArquitectonicType.fromValue(classificacio);
                        proteccio.setClassificacioArquitectonic(classArqui);
                    } catch (Exception ex){

                    }
                }
            }
            try {
                proteccio.setEntornProteccio(Integer.valueOf(this.strOrNull(record, "entorn")) != 0);
            } catch (Exception ex) {

            }
            proteccio.setBCIN(this.stringToLiteralType(this.getBCIN(this.getMultivaluedField(record, "Proteccions"))));
            if (this.literalOrNull(record, "Numero_bcin") != null){
                proteccio.setBCIN(this.literalOrNull(record, "Numero_bcin"));
            }
            proteccio.setBIC(this.literalOrNull(record, "Numero_bic"));
            proteccio.setPCC(this.stringToLiteralType(this.getBCIL(this.getMultivaluedField(record, "Proteccions"))));
            if (this.literalOrNull(record, "ct_cultura_bcil") != null){
                proteccio.setPCC(this.literalOrNull(record, "ct_cultura_bcil"));
            }
        }
        return proteccio;
    }

    private String getBCIL(String[] proteccions) {
        if (proteccions == null) return null;
        for (String proteccio : proteccions){
            Matcher matcher = BCIL_PATTERN.matcher(proteccio);
            if (matcher.matches()){
                return matcher.group(1);
            }
        }
        return null;
    }

    private Propietari getArquitecturaPropietari(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"cod_regim"})){
            return null;
        }
        Propietari propietari = new Propietari();
        propietari.setAbout(this.getIdentifier("Propietari"));
        propietari.setIdentificador(getIdentificador());
        {
            String tipusRegim = this.strOrNull(record, "cod_regim");
            if (tipusRegim != null){
                propietari.setTipusRegimArquitectonic(getEnumValue(PropietariArquitectonicType.class, tipusRegim));
            }
        }
        return propietari;
    }

    private ResourceType stringToResourceType(String text){
        ResourceType resourceType = new ResourceType();
        resourceType.setResource(text);
        return resourceType;
    }

    private boolean hasSomeValue(CSVRecord record, String[] values){
        for (String value : values){
            if (this.literalOrNull(record, value) != null){
                return true;
            }
        }
        return false;
    }

    private ResourceType getIdentificador(){
        return stringToResourceType(identificacio);
    }

    private XMLGregorianCalendar getDate(String dateStr) throws DatatypeConfigurationException {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy[ H:mm]");
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        GregorianCalendar cal;
        try {
            LocalDateTime formatDateTime = LocalDateTime.parse(dateStr, format);
            cal = GregorianCalendar.from(formatDateTime.atZone(zoneId));
        } catch (Exception ex){
            LocalDate formatDate = LocalDate.parse(dateStr, format);
            cal = GregorianCalendar.from(formatDate.atStartOfDay(zoneId));
        }
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
    }

    private LiteralType stringToLiteralType(String text){
        if (text == null) return null;
        LiteralType literalType = new LiteralType();
        literalType.setValue(text);
        return literalType;
    }

    private LiteralType literalOrNull(CSVRecord record, String field) {
        String str = this.strOrNull(record, field);
        if (str != null){
            return stringToLiteralType(str);
        }
        return null;
    }

    private String strOrNull(CSVRecord record, String field) {
        if (record.isSet(field) && !record.get(field).isEmpty()){
            return StringEscapeUtils.escapeXml11(record.get(field));
        }
        return null;
    }

    private String[] getMultivaluedField(CSVRecord record, String field){
        String fieldValue = strOrNull(record, field);
        if (fieldValue != null){
            return fieldValue.trim().split(Pattern.quote(SEPARATOR));
        }
        return null;
    }

    private ResourceType getTerritoriResource(String municipi, List<String> comarques){
        String municipi_raw = StringEscapeUtils.unescapeXml(municipi);
        String comarquesText = "";
        if (comarques != null){
            comarquesText = ":" + StringUtils.deleteWhitespace(String.join(":", comarques));
        }
        String key = IriToUri.iriToUri(String.format("Territori:%s%s", StringUtils.deleteWhitespace(municipi), comarquesText)).toString();
        if (territoris.containsKey(key)){
            return stringToResourceType(territoris.get(key).getAbout());
        }
        Territori territori = new Territori();
        if (comarques != null) {
            for (String comarca : comarques) {
                String comarca_raw = StringEscapeUtils.unescapeXml(comarca);
                territori.getComarca().add(getEnumValue(ComarcaType.class, comarca_raw));
            }
        }
        territori.setMunicipi(getEnumValue(MunicipiType.class, municipi_raw));
        if (!territori.getComarca().isEmpty() && territori.getMunicipi() != null){
            territori.setAbout(key);
            this.territoriTypes.add(territori);
            this.territoris.put(key, territori);
            return stringToResourceType(territori.getAbout());
        }
        return null;
    }

    public static String getValueOfEnum(Enum<?> e){
        String value = null;
        try {
            Method method = e.getClass().getMethod("value");
            value = (String) method.invoke(e);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e1) {
            e1.printStackTrace();
        }
        return value;
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    public static String[] getValues(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(GENECSV2GENERDF::getValueOfEnum).toArray(String[]::new);
    }

    private boolean normalizedEquals(String str1, String str2) {
        return StringUtils.stripAccents(str1.toLowerCase()).equals(StringUtils.stripAccents(str2.toLowerCase()));
    }

    private <T extends Enum<T>> T getEnumValue (Class<T> e, String value){
        String[] values = getValues(e);
        String[] names = getNames(e);
        for (int i = 0; i < values.length; i++){
            if (normalizedEquals(values[i], value)){
                return Enum.valueOf(e, names[i]);
            }
        }
        return null;
    }

    @Override
    public RDF convert(GENECSV src) {
        this.provider = this.getParams().getOrDefault("provider", "GENE");
        this.isArchitecture = Boolean.parseBoolean(this.getParams().getOrDefault("isArchitecture", "true"));
        RDF generdf = new RDF();
        for (CSVRecord record : src.getRecords()) {
            Identificacio id = getIdentificacio(record);
            generdf.getIdentificacioType().add(id);
            if (isArchitecture) {
                generdf.getPropertyType().add(getArquitecturaPropietat(record, id));
                generdf.getLocalitzacioType().add(getArquitecturaLocalitzacio(record));
                generdf.getTipologiaType().addAll(getArquitecturaTipologies(record));
                generdf.getUsType().addAll(getArquitecturaUsos(record));
                generdf.getDatacioType().add(getArquitecturaDatacio(record));
                generdf.getEstilType().addAll(getArquitecturaEstils(record));
                generdf.getAutorType().add(getArquitecturaAutor(record));
                generdf.getDescripcioType().add(getArquitecturaDescripcio(record));
                generdf.getNoticiaHistoricaType().add(getArquitecturaNoticiaHistorica(record));
                generdf.getConservacioType().add(getArquitecturaConservacio(record));
                generdf.getProteccioType().add(getArquitecturaProteccio(record));
                generdf.getPropietariType().add(getArquitecturaPropietari(record));
            } else {
                generdf.getPropertyType().add(getArqueologiaPropietat(record, id));
                generdf.getLocalitzacioType().add(getArqueologiaLocalitzacio(record));
                generdf.getTipologiaType().addAll(getArqueologiaTipologies(record));
                generdf.getDatacioType().add(getArqueologiaDatacio(record));
                generdf.getDescripcioType().add(getArqueologiaDescripcio(record));
                generdf.getNoticiaHistoricaType().add(getArqueologiaNoticiaHistorica(record));
                generdf.getConservacioType().add(getArqueologiaConservacio(record));
                generdf.getProteccioType().add(getArqueologiaProteccio(record));
                generdf.getPropietariType().add(getArqueologiaPropietari(record));
            }
            generdf.getTerritoriType().addAll(this.territoriTypes);
            generdf.getInformacioFitxaType().add(getInformacioFitxa(record));
        }
        return generdf;
    }
}
