
package org.Custom.Transformations.formats.gene;

import cat.gencat.*;
import net.sf.saxon.functions.IriToUri;
import org.Custom.Transformations.formats.gene.arquitectura.UtilitzacioType;
import org.Custom.Transformations.formats.gene.arquitectura.EstilType;
import org.Custom.Transformations.formats.gene.arqueologia.TipusJacimentType;
import org.Custom.Transformations.formats.gene.common.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.w3._1999._02._22_rdf_syntax_ns_.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class GENECSV2GENERDF extends RDF {
    private static final String SEPARATOR = " // ";
    private String fileName;
    private boolean isArchitecture;
    private String identificacio = "";
    private org.Custom.Transformations.formats.gene.arquitectura.CronologiaType arquiCronologiaType;
    private org.Custom.Transformations.formats.gene.arquitectura.ConservacioType arquiConservacioType;
    private org.Custom.Transformations.formats.gene.arquitectura.RegimType arquiRegimType;
    private org.Custom.Transformations.formats.gene.arqueologia.CronologiaType arqueCronologiaType;
    private org.Custom.Transformations.formats.gene.arqueologia.ConservacioType arqueConservacioType;
    private org.Custom.Transformations.formats.gene.arqueologia.RegimType arqueRegimType;
    private MunicipiType municipis;
    private ComarcaType comarques;
    private ServeiTerritorialType ssts;
    private String provider;
    private HashMap<String, Territori> territoris;

    public GENECSV2GENERDF(String provider, String fileName, boolean isArchitecture) {
        this.provider = provider;
        this.fileName = fileName;
        this.arquiCronologiaType = new org.Custom.Transformations.formats.gene.arquitectura.CronologiaType();
        this.arquiConservacioType = new org.Custom.Transformations.formats.gene.arquitectura.ConservacioType();
        this.arquiRegimType = new org.Custom.Transformations.formats.gene.arquitectura.RegimType();
        this.arqueCronologiaType = new org.Custom.Transformations.formats.gene.arqueologia.CronologiaType();
        this.arqueConservacioType = new org.Custom.Transformations.formats.gene.arqueologia.ConservacioType();
        this.arqueRegimType = new org.Custom.Transformations.formats.gene.arqueologia.RegimType();
        this.municipis = new MunicipiType();
        this.comarques = new ComarcaType();
        this.territoris = new HashMap<>();
        this.ssts = new ServeiTerritorialType();
        this.isArchitecture = isArchitecture;
    }

    public GENECSV2GENERDF(String provider, String fileName, boolean isArchitecture, HashMap<Integer, List<String>> arquiDups, HashMap<Integer, List<String>> arqueDups) {
        this.provider = provider;
        this.fileName = fileName;
        this.arquiCronologiaType = new org.Custom.Transformations.formats.gene.arquitectura.CronologiaType();
        this.arquiConservacioType = new org.Custom.Transformations.formats.gene.arquitectura.ConservacioType();
        this.arquiRegimType = new org.Custom.Transformations.formats.gene.arquitectura.RegimType();
        this.arqueCronologiaType = new org.Custom.Transformations.formats.gene.arqueologia.CronologiaType();
        this.arqueConservacioType = new org.Custom.Transformations.formats.gene.arqueologia.ConservacioType();
        this.arqueRegimType = new org.Custom.Transformations.formats.gene.arqueologia.RegimType();
        this.municipis = new MunicipiType();
        this.comarques = new ComarcaType();
        this.territoris = new HashMap<>();
        this.ssts = new ServeiTerritorialType();
        this.isArchitecture = isArchitecture;
    }

    public void generateRDF() throws IOException {
        Reader in = new FileReader(this.fileName);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader().parse(in);
        for (CSVRecord record : records) {
            if (isArchitecture) {
                this.getIdentificacio().add(getArquitecturaIdentificacio(record));
                this.getLocalitzacio().add(getArquitecturaLocalitzacio(record));
                this.getTipologia().addAll(getArquitecturaTipologies(record));
                this.getUs().addAll(getArquitecturaUsos(record));
                this.getDatacio().add(getArquitecturaDatacio(record));
                this.getEstil().addAll(getArquitecturaEstils(record));
                this.getAutor().add(getArquitecturaAutor(record));
                this.getDescripcio().add(getArquitecturaDescripcio(record));
                this.getNoticiaHistorica().add(getArquitecturaNoticiaHistorica(record));
                this.getConservacio().add(getArquitecturaConservacio(record));
                this.getProteccio().add(getArquitecturaProteccio(record));
                this.getPropietari().add(getArquitecturaPropietari(record));
            } else {
                this.getIdentificacio().add(getArqueologiaIdentificacio(record));
                this.getLocalitzacio().add(getArqueologiaLocalitzacio(record));
                this.getTipologia().addAll(getArqueologiaTipologies(record));
                this.getDatacio().add(getArqueologiaDatacio(record));
                this.getDescripcio().add(getArqueologiaDescripcio(record));
                this.getNoticiaHistorica().add(getArqueologiaNoticiaHistorica(record));
                this.getConservacio().add(getArqueologiaConservacio(record));
                this.getProteccio().add(getArqueologiaProteccio(record));
                this.getPropietari().add(getArqueologiaPropietari(record));
            }
        }
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
    private Identificacio getArqueologiaIdentificacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"num_jaciment"})){
            return null;
        }
        Identificacio id = new Identificacio();
        this.identificacio = getIdentificadorIdentifier(false, this.strOrNull(record, "num_jaciment"));
        id.setAbout(identificacio);
        {
            id.setTipusPatrimoni(TipusPatrimoniTipus.ARQUEOLÒGIC);
            id.setCodiIntern(this.strOrNull(record, "num_jaciment"));
            if (this.strOrNull(record, "codi") != null){
                id.setCodiInventari(this.strOrNull(record, "codi"));
            }
            id.setNom(this.strOrNull(record, "nom_actual"));
            id.setAltresNoms(this.strOrNull(record, "nom_altres"));
            id.getProveidor().add(this.provider);
        }
        return id;
    }

    private Localitzacio getArqueologiaLocalitzacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"cod-mcpi", "cod_comarca", "agregat", "cod_sstt", "coor_utm_long", "coor_utm_lat", "context_descripció"})){
            return null;
        }
        Localitzacio loc = new Localitzacio();
        loc.setAbout(this.getIdentifier("Localitzacio"));
        {
            String[] cod_mcpis = this.getMultivaluedField(record, "cod-mcpi");
            if (cod_mcpis != null){
                for (String cod_mcpi : cod_mcpis){
                    Municipi municipi = municipis.get(cod_mcpi);
                    String resource = getTerritoriResource(comarques.get(municipi.getId_comarca()).getNom(), municipi.getNom());
                    Localitzacio.Territori locTerritori = new Localitzacio.Territori();
                    locTerritori.setResource(resource);
                    loc.getTerritori().add(locTerritori);
                }
            }
            if (this.strOrNull(record, "agregat") != null){
                loc.setAgregat(new MancomunitatType().get(this.strOrNull(record, "agregat")));
            }
            if (this.strOrNull(record, "cod_sstt") != null){
                loc.setServeiTerritorial(ssts.get(this.strOrNull(record, "cod_sstt")));
            }
            if (this.strOrNull(record, "coor_utm_long") != null){
                loc.setX(Float.valueOf(this.strOrNull(record, "coor_utm_long").replace(",", ".")));
            }
            if (this.strOrNull(record, "coor_utm_lat") != null){
                loc.setY(Float.valueOf(this.strOrNull(record, "coor_utm_lat").replace(",", ".")));
            }
            loc.setLocDescripcio(this.strOrNull(record, "context_descripció"));
            loc.setIdentificador(getIdentificador());
        }
        return loc;
    }

    private List<Tipologia> getArqueologiaTipologies(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"tip_jac"})){
            return new ArrayList<>();
        }
        List<Tipologia> tipologies = new ArrayList<>();
        String[] tipus_jaciments = this.getMultivaluedField(record, "tip_jac");
        if (tipus_jaciments != null) {
            for (String tip_jac : tipus_jaciments) {
                Tipologia tip = new Tipologia();
                tip.setAbout(this.getIdentifier("Tipologia"));
                tip.setIdentificador(getIdentificador());
                {
                    tip.setTipologia(new TipusJacimentType().get(tip_jac));
                }
                tipologies.add(tip);
            }
        }
        return tipologies;
    }

    private Datacio getArqueologiaDatacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"codi_crono_inici", "cod_crono_fi", "data_inici", "data_fi"})){
            return null;
        }
        Datacio dat = new Datacio();
        dat.setAbout(this.getIdentifier("Datacio"));
        dat.setIdentificador(getIdentificador());
        {
            Cronologia inici = arqueCronologiaType.get(this.strOrNull(record, "codi_crono_inici"));
            Cronologia fi = arqueCronologiaType.get(this.strOrNull(record, "cod_crono_fi"));
            if (inici != null){
                dat.setCronologiaInicial(inici.getDescripcio());
                if (inici.getAny_inici() != null){
                    dat.setAnyInici(inici.getAny_inici());
                }
            } else if (this.strOrNull(record, "data_inici") != null){
                dat.setAnyInici(Integer.valueOf(this.strOrNull(record, "data_inici")));
            }
            if (fi != null){
                dat.setCronologiaFinal(fi.getDescripcio());
                if (fi.getAny_fi() != null){
                    dat.setAnyFi(fi.getAny_fi());
                }
            } else if (this.strOrNull(record, "data_fi") != null){
                dat.setAnyFi(Integer.valueOf(this.strOrNull(record, "data_fi")));
            }
        }
        return dat;
    }

    private Descripcio getArqueologiaDescripcio(CSVRecord record){
        if (!hasSomeValue(record, new String[] {"descripció"})){
            return null;
        }
        Descripcio desc = new Descripcio();
        desc.setAbout(this.getIdentifier("Descripcio"));
        desc.setIdentificador(getIdentificador());
        desc.setDescDescripcio(this.strOrNull(record, "descripció"));
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
            noticia.setTipusNoticiaHistorica(this.strOrNull(record, "tip_noticia"));
            if (this.strOrNull(record, "data") != null){
                try {
                    noticia.setDataNoticiaHistorica(getDate(this.strOrNull(record, "data")));
                } catch (ParseException | DatatypeConfigurationException e) {
                    e.printStackTrace();
                }
            }
            noticia.setNomNoticiaHistorica(this.strOrNull(record, "nom"));
            noticia.setComentariNoticiaHistorica(this.strOrNull(record, "notes"));
        }
        return noticia;
    }

    private Conservacio getArqueologiaConservacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"tip_conservacio", "consDescripció"})){
            return null;
        }
        Conservacio con = new Conservacio();
        con.setAbout(this.getIdentifier("Conservacio"));
        con.setIdentificador(getIdentificador());
        {
            String conservacio = this.strOrNull(record, "tip_conservació");
            if (conservacio != null){
                con.setConservacioEstat(arqueConservacioType.get(Integer.valueOf(conservacio)));
            }
            con.setConservacioComentari(this.strOrNull(record, "consDescripció"));
        }
        return con;
    }

    private Proteccio getArqueologiaProteccio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"class_prot_legal", "tip_prot_legal", "num_reg_bcin_cpcc", "num_reg_estatal"})){
            return null;
        }
        Proteccio proteccio = new Proteccio();
        proteccio.setAbout(this.getIdentifier("Proteccio"));
        proteccio.setIdentificador(getIdentificador());
        {
            String[] classificacions = this.getMultivaluedField(record, "class_prot_legal");
            if (classificacions != null) {
                for (String classificacio : classificacions) {
                    proteccio.getClassificacio().add(new org.Custom.Transformations.formats.gene.arqueologia.ProteccioClassificacioType().get(Integer.valueOf(classificacio)));
                }
            }
            proteccio.setProteccio(this.strOrNull(record, "tip_prot_legal"));
            proteccio.setBCIN(this.strOrNull(record, "num_reg_bcin_cpcc"));
            proteccio.setBIC(this.strOrNull(record, "num_reg_estatal"));
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
                propietari.setTipusRegim(arqueRegimType.get(tipusRegim));
            }
        }
        return propietari;
    }

    // Arquitectura
    private Identificacio getArquitecturaIdentificacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"cod_arq", "nom_edifici", "altres_noms"})){
            return null;
        }
        Identificacio id = new Identificacio();
        this.identificacio = getIdentificadorIdentifier(true, this.strOrNull(record, "cod_arq"));
        id.setAbout(identificacio);
        {
            id.setTipusPatrimoni(TipusPatrimoniTipus.ARQUITECTÒNIC);
            id.setCodiIntern(this.strOrNull(record, "cod_arq"));
            if (this.strOrNull(record, "codi") != null){
                id.setCodiInventari(this.strOrNull(record, "codi"));
            }
            id.setNom(this.strOrNull(record, "nom_edifici"));
            id.setAltresNoms(this.strOrNull(record, "altres_noms"));
            id.getProveidor().add(this.provider);
        }
        return id;
    }

    private Localitzacio getArquitecturaLocalitzacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"cod_mcpi", "cod_comarca", "agregat", "cod_sstt", "utm_x", "utm_y"})){
            return null;
        }
        Localitzacio loc = new Localitzacio();
        loc.setAbout(this.getIdentifier("Localitzacio"));
        {
            loc.setAdreca(this.strOrNull(record, "adreça"));
            String[] cod_mcpis = this.getMultivaluedField(record, "cod_mcpi");
            if (cod_mcpis != null){
                for (String cod_mcpi : cod_mcpis){
                    Municipi municipi = municipis.get(cod_mcpi);
                    String resource = getTerritoriResource(comarques.get(municipi.getId_comarca()).getNom(), municipi.getNom());
                    Localitzacio.Territori locTerritori = new Localitzacio.Territori();
                    locTerritori.setResource(resource);
                    loc.getTerritori().add(locTerritori);
                }
            }
            if (this.strOrNull(record, "agregat") != null){
                loc.setAgregat(new MancomunitatType().get(this.strOrNull(record, "agregat")));
            }
            if (this.strOrNull(record, "cod_sstt") != null){
                loc.setServeiTerritorial(ssts.get(this.strOrNull(record, "cod_sstt")));
            }
            if (this.strOrNull(record, "utm_x") != null){
                loc.setX(Float.valueOf(this.strOrNull(record, "utm_x").replace(",", ".")));
            }
            if (this.strOrNull(record, "utm_y") != null){
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
                    tip.setTipologia(new UtilitzacioType().get(Integer.valueOf(tip_us)));
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
                    us.setOriginalActual(tip_us);
                }
                usos.add(us);
            }
        }
        return usos;
    }

    private Datacio getArquitecturaDatacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"cod_epoca_inicial", "cod_epoca_final", "data_inicial", "data_fi"})){
            return null;
        }
        Datacio dat = new Datacio();
        dat.setAbout(this.getIdentifier("Datacio"));
        dat.setIdentificador(getIdentificador());
        {
            Cronologia inici = arquiCronologiaType.get(this.strOrNull(record, "cod_epoca_inicial"));
            Cronologia fi = arquiCronologiaType.get(this.strOrNull(record, "cod_epoca_final"));
            if (inici != null){
                dat.setCronologiaInicial(inici.getDescripcio());
                if (inici.getAny_inici() != null){
                    dat.setAnyInici(inici.getAny_inici());
                }
            } else if (this.strOrNull(record, "data_inicial") != null){
                dat.setAnyInici(Integer.valueOf(this.strOrNull(record, "data_inicial")));
            }
            if (fi != null){
                dat.setCronologiaFinal(fi.getDescripcio());
                if (fi.getAny_fi() != null){
                    dat.setAnyFi(fi.getAny_fi());
                }
            } else if (this.strOrNull(record, "data_fi") != null){
                dat.setAnyFi(Integer.valueOf(this.strOrNull(record, "data_fi")));
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
                    estilDesc.setTipusEstil(new EstilType().get(Integer.valueOf(estil)));
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
                    autDesc.getCognoms().add(cognom);
                }
            }
            autDesc.setNoms(this.strOrNull(record, "nom"));
            autDesc.setProfessio(this.strOrNull(record, "funcio"));
            if (this.strOrNull(record, "any_inici") != null){
                autDesc.setAnyInici(Integer.valueOf(this.strOrNull(record, "any_inici")));
            }

            if (this.strOrNull(record, "any_fi") != null){
                autDesc.setAnyFi(Integer.valueOf(this.strOrNull(record, "any_fi")));
            }

        }
        return autDesc;
    }

    private Descripcio getArquitecturaDescripcio(CSVRecord record){
        if (!hasSomeValue(record, new String[] {"descripció"})){
            return null;
        }
        Descripcio desc = new Descripcio();
        desc.setAbout(this.getIdentifier("Descripcio"));
        desc.setIdentificador(getIdentificador());
        desc.setDescDescripcio(this.strOrNull(record, "descripció"));
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
            noticia.setComentariNoticiaHistorica(this.strOrNull(record, "noticies_històriques"));
        }
        return noticia;
    }

    private Conservacio getArquitecturaConservacio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"cod_estat_global"})){
            return null;
        }
        Conservacio con = new Conservacio();
        con.setAbout(this.getIdentifier("Conservacio"));
        con.setIdentificador(getIdentificador());
        {
            String conservacio = this.strOrNull(record, "cod_estat_global");
            if (conservacio != null){
                con.setConservacioEstat(arquiConservacioType.get(Integer.valueOf(conservacio)));
            }
        }
        return con;
    }

    private Proteccio getArquitecturaProteccio(CSVRecord record) {
        if (!hasSomeValue(record, new String[] {"classificacio", "entorn", "Numero_bcin", "Numero_bic", "ct_culturabcil"})){
            return null;
        }
        Proteccio proteccio = new Proteccio();
        proteccio.setAbout(this.getIdentifier("Proteccio"));
        proteccio.setIdentificador(getIdentificador());
        {
            String[] classificacions = this.getMultivaluedField(record, "classificacio");
            if (classificacions != null) {
                for (String classificacio : classificacions) {
                    proteccio.getClassificacio().add(new org.Custom.Transformations.formats.gene.arquitectura.ProteccioClassificacioType().get(Integer.valueOf(classificacio)));
                }
            }
            try {
                proteccio.setEntornProteccio(Boolean.valueOf(Integer.valueOf(this.strOrNull(record, "entorn")) != 0));
            } catch (Exception ex) {

            }

            proteccio.setBCIN(this.strOrNull(record, "Numero_bcin"));
            proteccio.setBIC(this.strOrNull(record, "Numero_bic"));
            proteccio.setPCC(this.strOrNull(record, "ct_cultura_bcil"));
        }
        return proteccio;
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
                propietari.setTipusRegim(arquiRegimType.get(tipusRegim));
            }
        }
        return propietari;
    }

    private boolean hasSomeValue(CSVRecord record, String[] values){
        for (String value : values){
            if (this.strOrNull(record, value) != null){
                return true;
            }
        }
        return false;
    }

    private Identificador getIdentificador(){
        Identificador id = new Identificador();
        id.setResource(identificacio);
        return id;
    }

    private XMLGregorianCalendar getDate(String dateStr) throws ParseException, DatatypeConfigurationException {
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = format.parse(dateStr);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
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
            return fieldValue.split(Pattern.quote(SEPARATOR));
        }
        return null;
    }

    private String getTerritoriResource(String comarca, String municipi){
        String key = IriToUri.iriToUri(String.format("Territori:%s:%s", StringUtils.deleteWhitespace(municipi), StringUtils.deleteWhitespace(comarca))).toString();
        if (territoris.containsKey(key)){
            return territoris.get(key).getAbout();
        }
        Territori territori = new Territori();
        territori.setComarca(comarca);
        territori.setMunicipi(municipi);
        territori.setAbout(key);
        this.getTerritori().add(territori);
        this.territoris.put(key, territori);
        return territori.getAbout();
    }
}
