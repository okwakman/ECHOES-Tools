package org.Custom.Transformations.formats.diba;

import cat.gencat.Estil;
import org.Custom.Transformations.formats.gene.arquitectura.EstilType;
import org.Custom.Transformations.formats.gene.common.Cronologia;
import org.Custom.Transformations.formats.gene.common.Municipi;
import org.Custom.Transformations.formats.gene.common.MunicipiType;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DIBACSV2GENECSV {
    private HashMap<String, String> generalColumnsMapping;
    private HashMap<String, String> archeologyColumnsMapping;
    private HashMap<String, String> archeologyStaticColumnsMapping;
    private HashMap<String, String> architectureStaticColumnsMapping;
    private HashMap<String, String> architectureColumnsMapping;
    private String[] fields = {
            // Comuns
            "codi", "tipus_registre", "agregat", "cod_comarca", "cod_sstt", "data_fi", "descripció", "nom",
            // Arqueologia
            "num_jaciment", "nom_actual", "nom_altres", "cod-mcpi",
            "coor_utm_long", "coor_utm_lat", "context_descripció", "tip_jac", "codi_crono_inici",
            "cod_crono_fi", "data_inici", "tip_noticia", "data",
            "notes", "tip_conservació", "consDescripció", "tip_prot_legal", "class_prot_legal",
            "num_reg_bcin_cpcc", "num_reg_estatal", "tip-regim",
            // Arquitectura
            "cod_arq", "nom_edifici", "altres_noms", "adreça", "cod_mcpi",
            "utm_x", "utm_y", "cod_arq_utilitzacio", "original_actual", "cod_epoca_inicial",
            "cod_epoca_final", "data_inicial", "cod_estil", "cognoms", "funcio", "any_inici",
            "any_fi", "notícies_històriques", "cod_estat_global", "classificacio", "entorn",
            "Numero_bcin", "Numero_bic", "ct_cultura_bcil", "cod_regim"
    };

    public DIBACSV2GENECSV() {
        fillGeneralColumnsMapping();
        fillArcheologyStaticColumnsMapping();
        fillArcheologyColumnsMapping();
        fillArchitectureStaticColumnsMapping();
        fillArchitectureColumnsMapping();
    }

    private Iterable<CSVRecord> getRecords(String fileNameIn) throws IOException {
        Reader in = new FileReader(fileNameIn);
        return CSVFormat.DEFAULT.withHeader().parse(in);
    }

    private CSVPrinter getCSVPrinter(String fileNameOut) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(fileNameOut));
        return new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(fields));
    }

    public void convert(String fileNameIn, String fileNameOut) throws IOException {
        Iterable<CSVRecord> records = getRecords(fileNameIn);
        CSVPrinter csvPrinter = getCSVPrinter(fileNameOut);
        for (CSVRecord record : records) {
            if (isArchitecture(record)) {
                csvPrinter.printRecord(processArchitectureColumns(record));
            } else {
                csvPrinter.printRecord(processArcheologyColumns(record));
            }
        }
        csvPrinter.flush();
    }

    private HashMap<String, String> getCalculatedArchitectureColumns(CSVRecord csvRecord) {
        HashMap<String, String> columns = new HashMap<>();
        columns.put("cod_arq", csvRecord.get("Poblacio") + ":" + csvRecord.get("NumFitxa"));
        if (csvRecord.isSet("Poblacio")) {
            Municipi municipi = getMunicipi(csvRecord.get("Poblacio"));
            if (municipi != null) {
                columns.put("cod_mcpi", municipi.getId());
                columns.put("cod_comarca", municipi.getId_comarca().toString());
            }
        }
        if (csvRecord.isSet("Any")) {
            int[] anys = getAnys(csvRecord.get("Any"));
            if (anys != null) {
                if (anys.length > 0) {
                    columns.put("data_inicial", String.valueOf(anys[0]));
                }
                if (anys.length > 1) {
                    columns.put("data_fi", String.valueOf(anys[1]));
                }
            }
        }
        if (csvRecord.isSet("Segle")) {
            Cronologia[] cronologies = getCronologiaFromSegle(csvRecord.get("Segle"), true);
            if (cronologies[0] != null) {
                columns.put("cod_epoca_inicial", cronologies[0].getCodi());
                if (!columns.containsKey("data_inicial")) {
                    columns.put("data_inicial", cronologies[0].getAny_inici().toString());
                }
            }
            if (cronologies[1] != null) {
                columns.put("cod_epoca_final", cronologies[1].getCodi());
                if (!columns.containsKey("data_fi")) {
                    columns.put("data_fi", cronologies[1].getAny_fi().toString());
                }
            }
        }
        if (csvRecord.isSet("EstatConservacio")) {
            int codiConservacio = getCodiConservacio(csvRecord.get("EstatConservacio"), true);
            if (codiConservacio != -1) {
                columns.put("cod_estat_global", String.valueOf(codiConservacio));
            }
        }
        if (csvRecord.isSet("Titularitat")) {
            String codiRegim = getRegimCodi(csvRecord.get("Titularitat"), true);
            if (codiRegim != null) {
                columns.put("cod_regim", codiRegim);
            }
        }
        if (csvRecord.isSet("Autor")) {
            String autor = csvRecord.get("Autor").trim();
            if (!autor.equals(".") && !autor.equals("-")){
                columns.put("nom", autor);
            }
        }
        if (csvRecord.isSet("Estil")) {
            String estil = csvRecord.get("Estil").trim();
            String[] estils = estil.split(",|\\-|/");
            List<String> estilsList = new ArrayList<>();
            EstilType estilType = new EstilType();
            for (String estil_sep : estils){
                String estil_sep_trim = estil_sep.trim();
                if (estilType.containsValue(estil_sep_trim)){
                    for (Map.Entry<Integer, String> estilEntry : estilType.entrySet()){
                        if (estilEntry.getValue().equals(estil_sep_trim)){
                            estilsList.add(estilEntry.getKey().toString());
                        }
                    }
                }
            }
            if (estilsList.size() > 0){
                columns.put("cod_estil", String.join("//", estilsList));
            }
        }
        return columns;
    }

    private HashMap<String, String> getCalculatedArcheologyColumns(CSVRecord csvRecord) {
        HashMap<String, String> columns = new HashMap<>();
        columns.put("num_jaciment", csvRecord.get("Poblacio") + ":" + csvRecord.get("NumFitxa"));
        if (csvRecord.isSet("Poblacio")) {
            Municipi municipi = getMunicipi(csvRecord.get("Poblacio"));
            if (municipi != null) {
                columns.put("cod-mcpi", municipi.getId());
                columns.put("cod_comarca", municipi.getId_comarca().toString());
            }
        }
        if (csvRecord.isSet("Any")) {
            int[] anys = getAnys(csvRecord.get("Any"));
            if (anys != null) {
                if (anys.length > 0) {
                    columns.put("data_inici", String.valueOf(anys[0]));
                }
                if (anys.length > 1) {
                    columns.put("data_fi", String.valueOf(anys[1]));
                }
            }
        }
        if (csvRecord.isSet("Segle")) {
            Cronologia[] cronologies = getCronologiaFromSegle(csvRecord.get("Segle"), false);
            if (cronologies[0] != null) {
                columns.put("codi_crono_inici", cronologies[0].getCodi());
                if (!columns.containsKey("data_inici")) {
                    columns.put("data_inici", cronologies[0].getAny_inici().toString());
                }
            }
            if (cronologies[1] != null) {
                columns.put("cod_crono_fi", cronologies[1].getCodi());
                if (!columns.containsKey("data_fi")) {
                    columns.put("data_fi", cronologies[1].getAny_fi().toString());
                }
            }
        }
        if (csvRecord.isSet("EstatConservacio")) {
            int codiConservacio = getCodiConservacio(csvRecord.get("EstatConservacio"), false);
            if (codiConservacio != -1) {
                columns.put("tip_conservació", String.valueOf(codiConservacio));
            }
        }
        if (csvRecord.isSet("Titularitat")) {
            String codiRegim = getRegimCodi(csvRecord.get("Titularitat"), false);
            if (codiRegim != null) {
                columns.put("tip-regim", codiRegim);
            }
        }
        if (csvRecord.isSet("Codi")){
            String tip_jac = CodiToTipusJaciment(csvRecord.get("Codi"));
            if (tip_jac != null){
                columns.put("tip_jac", tip_jac);
            }
        }
        return columns;
    }

    private Iterable<String> processArchitectureColumns(CSVRecord csvRecord) {
        HashMap<String, String> calcColumns = getCalculatedArchitectureColumns(csvRecord);
        List<String> record = new ArrayList<>();
        for (String field : fields) {
            String recordToAdd = "";
            if (generalColumnsMapping.containsKey(field)) {
                recordToAdd = csvRecord.get(generalColumnsMapping.get(field));
            } else if (architectureStaticColumnsMapping.containsKey(field)) {
                recordToAdd = architectureStaticColumnsMapping.get(field);
            } else if (architectureColumnsMapping.containsKey(field)) {
                recordToAdd = csvRecord.get(architectureColumnsMapping.get(field));
            } else {
                switch (field) {
                    case "cod_arq":
                    case "nom":
                    case "cod_comarca":
                    case "data_fi":
                    case "cod_mcpi":
                    case "cod_epoca_inicial":
                    case "cod_epoca_final":
                    case "data_inicial":
                    case "cod_estat_global":
                    case "cod_regim":
                        if (calcColumns.containsKey(field)) {
                            recordToAdd = calcColumns.get(field);
                        }
                }
            }
            record.add(recordToAdd);
        }
        return record;
    }

    private Iterable<String> processArcheologyColumns(CSVRecord csvRecord) {
        HashMap<String, String> calcColumns = getCalculatedArcheologyColumns(csvRecord);
        List<String> record = new ArrayList<>();
        for (String field : fields) {
            String recordToAdd = "";
            if (generalColumnsMapping.containsKey(field)) {
                recordToAdd = csvRecord.get(generalColumnsMapping.get(field));
            } else if (archeologyStaticColumnsMapping.containsKey(field)) {
                recordToAdd = archeologyStaticColumnsMapping.get(field);
            } else if (archeologyColumnsMapping.containsKey(field)) {
                recordToAdd = csvRecord.get(archeologyColumnsMapping.get(field));
            } else {
                switch (field) {
                    case "num_jaciment":
                    case "cod_comarca":
                    case "data_fi":
                    case "cod-mcpi":
                    case "codi_crono_inici":
                    case "cod_crono_fi":
                    case "data_inici":
                    case "tip_conservació":
                    case "tip-regim":
                    case "tip_jac":
                        if (calcColumns.containsKey(field)) {
                            recordToAdd = calcColumns.get(field);
                        }
                }
            }
            record.add(recordToAdd);
        }
        return record;
    }

    private void fillGeneralColumnsMapping() {
        generalColumnsMapping = new HashMap<>();
        generalColumnsMapping.put("codi", "Inventari");
        generalColumnsMapping.put("descripció", "Descripcio");
    }

    private void fillArcheologyColumnsMapping() {
        archeologyColumnsMapping = new HashMap<>();
        archeologyColumnsMapping.put("nom_actual", "Denom");
        archeologyColumnsMapping.put("coor_utm_long", "X");
        archeologyColumnsMapping.put("coor_utm_lat", "Y");
        archeologyColumnsMapping.put("context_descripció", "Emplacament");
        archeologyColumnsMapping.put("notes", "Historia");
        archeologyColumnsMapping.put("consDescripció", "NotesConservacio");
    }

    private void fillArcheologyStaticColumnsMapping() {
        archeologyStaticColumnsMapping = new HashMap<>();
        archeologyStaticColumnsMapping.put("tipus_registre", "arqueològic");
    }

    private void fillArchitectureStaticColumnsMapping() {
        architectureStaticColumnsMapping = new HashMap<>();
        architectureStaticColumnsMapping.put("tipus_registre", "arquitectònic");
    }

    private void fillArchitectureColumnsMapping() {
        architectureColumnsMapping = new HashMap<>();
        architectureColumnsMapping.put("nom_edifici", "Denom");
        architectureColumnsMapping.put("adreça", "Ubicacio");
        architectureColumnsMapping.put("utm_x", "X");
        architectureColumnsMapping.put("utm_y", "Y");
        architectureColumnsMapping.put("original_actual", "UsActual");
        architectureColumnsMapping.put("noticies_històriques", "Historia");
    }

    private boolean isArchitecture(CSVRecord record) {
        return !record.get("Codi").equals("1.4");
    }

    private Municipi getMunicipi(String poblacio) {
        for (Municipi municipi : new MunicipiType().values()) {
            if (normalizedEquals(poblacio, municipi.getNom())) {
                return municipi;
            }
        }
        return null;
    }

    private Cronologia[] getCronologiaFromSegle(String segles, boolean isArchitecture) {
        Cronologia[] cronologiaIniciFi = new Cronologia[2];
        HashMap<String, Cronologia> cronologies;
        if (isArchitecture) {
            cronologies = new org.Custom.Transformations.formats.gene.arquitectura.CronologiaType();
        } else {
            cronologies = new org.Custom.Transformations.formats.gene.arqueologia.CronologiaType();
        }
        String segleInici = "";
        String segleFi = "";
        String[] seglesSep = segles.split("-");
        if (seglesSep.length > 0) {
            segleInici = seglesSep[0].trim().toLowerCase();
        }
        if (seglesSep.length > 1) {
            segleFi = seglesSep[1].trim().toLowerCase();
        }
        for (Map.Entry<String, Cronologia> cronologia : cronologies.entrySet()) {
            if (normalizedEquals(cronologia.getValue().getDescripcio(), segleInici)) {
                cronologiaIniciFi[0] = cronologia.getValue();
                break;
            }
        }
        for (Map.Entry<String, Cronologia> cronologia : cronologies.entrySet()) {
            if (normalizedEquals(cronologia.getValue().getDescripcio(), segleFi)) {
                cronologiaIniciFi[1] = cronologia.getValue();
                break;
            }
        }
        return cronologiaIniciFi;
    }

    private int[] getAnys(String anys) {
        Pattern pattern = Pattern.compile("(\\d+)\\s*-?\\s*(\\d*)");
        Matcher matcher = pattern.matcher(anys);
        if (matcher.find()) {
            if (matcher.groupCount() == 2) {
                return new int[]{Integer.valueOf(matcher.group(1))};
            } else if (matcher.groupCount() == 3) {
                return new int[]{Integer.valueOf(matcher.group(1)), Integer.valueOf(matcher.group(2))};
            }
        }
        return null;
    }

    private int getCodiConservacio(String estatConservacio, boolean isArchitecture) {
        HashMap<Integer, String> conservacions;
        if (isArchitecture) {
            if (normalizedEquals(estatConservacio, "regular")){
                estatConservacio = "Mitjà";
            }
            conservacions = new org.Custom.Transformations.formats.gene.arquitectura.ConservacioType();
        } else {
            conservacions = new org.Custom.Transformations.formats.gene.arqueologia.ConservacioType();
        }
        for (Map.Entry<Integer, String> conservacio : conservacions.entrySet()) {
            if (normalizedEquals(conservacio.getValue(), estatConservacio)) {
                return conservacio.getKey();
            }
        }
        return -1;
    }

    private boolean normalizedEquals(String str1, String str2) {
        return StringUtils.stripAccents(str1.toLowerCase()).equals(StringUtils.stripAccents(str2.toLowerCase()));
    }

    private String getRegimCodi(String regimStr, boolean isArchitecture) {
        HashMap<String, String> regims;
        if (isArchitecture) {
            regims = new org.Custom.Transformations.formats.gene.arquitectura.RegimType();
        } else {
            regims = new org.Custom.Transformations.formats.gene.arqueologia.RegimType();
        }
        for (Map.Entry<String, String> regim : regims.entrySet()) {
            if (normalizedEquals(regim.getValue(), regimStr)) {
                return regim.getKey();
            }
        }
        return null;
    }

    private String CodiToTipusJaciment(String codi){
        switch (codi){
            // Patrimoni immoble - Jaciment arqueològic
            case "1.4":
                // Jaciment arqueològic
                return "2";
            // Patrimoni immoble - Element arquitectònic
            case "1.3":
            // Patrimoni moble - Element urbà
            case "2.1":
            // Patrimoni moble - Objecte
            case "2.2":
                // Element
                return "6";
        }
        return null;
    }
}
