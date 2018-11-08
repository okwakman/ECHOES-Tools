package org.Custom.Transformations.formats.diba;

import cat.gencat.*;
import org.Custom.Transformations.core.Convertible;
import org.Custom.Transformations.formats.gene.GENECSV;
import org.Custom.Transformations.formats.gene.common.Comarca;
import org.Custom.Transformations.formats.gene.common.Municipi;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DIBACSV2GENECSV extends Convertible<DIBACSV, GENECSV> {
    private Map<String, Municipi> municipis;
    private Map<Integer, Comarca> comarques;

    private HashMap<String, String> generalColumnsMapping;
    private HashMap<String, String> archeologyColumnsMapping;
    private HashMap<String, String> archeologyStaticColumnsMapping;
    private HashMap<String, String> architectureStaticColumnsMapping;
    private HashMap<String, String> architectureColumnsMapping;

    public DIBACSV2GENECSV() {
        municipis = new org.Custom.Transformations.formats.gene.common.MunicipiType();
        comarques = new org.Custom.Transformations.formats.gene.common.ComarcaType();
        fillGeneralColumnsMapping();
        fillArcheologyStaticColumnsMapping();
        fillArcheologyColumnsMapping();
        fillArchitectureStaticColumnsMapping();
        fillArchitectureColumnsMapping();
    }

    private static String getCronologiaValue(Object cronologia) {
        if (cronologia instanceof CronologiaArquitectonicType) {
            return ((CronologiaArquitectonicType) cronologia).value();
        } else if (cronologia instanceof CronologiaArqueologicType) {
            return ((CronologiaArqueologicType) cronologia).value();
        } else if (cronologia instanceof EstilEpocaType) {
            return ((EstilEpocaType) cronologia).value();
        }
        return null;
    }

    private HashMap<String, String> getCalculatedArchitectureColumns(CSVRecord csvRecord) {
        HashMap<String, String> columns = new HashMap<>();
        columns.put("cod_arq", csvRecord.get("Poblacio") + ":" + csvRecord.get("NumFitxa"));
        if (csvRecord.isSet("Poblacio")) {
            MunicipiType municipi = getMunicipi(csvRecord.get("Poblacio"));
            if (municipi != null) {
                String comarca = getComarcaFromMunicipi(municipi.value());
                columns.put("Municipi_Comarca", municipi.value() + " (" + comarca + ")");
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
            Datacio datacio = getCronologiaFromSegle(csvRecord.get("Segle"), true);
            if (!datacio.getCronologiaInicial().isEmpty()){
                String epoca = getCronologiaValue(datacio.getCronologiaInicial().get(0));
                if (!datacio.getCronologiaFinal().isEmpty()){
                    epoca += " - " + getCronologiaValue(datacio.getCronologiaFinal().get(0));
                }
                columns.put("Epoques", epoca);
            }
        }
        if (csvRecord.isSet("EstatConservacio")) {
            String codiConservacio = getCodiConservacio(csvRecord.get("EstatConservacio"), true);
            if (codiConservacio != null) {
                columns.put("cod_estat_global", codiConservacio);
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
            if (!autor.equals(".") && !autor.equals("-")) {
                columns.put("nom", autor);
            }
        }
        if (csvRecord.isSet("Estil")) {
            String estil = csvRecord.get("Estil").trim();
            String[] estils = estil.split(",|\\-|/");
            List<String> estilsList = new ArrayList<>();
            List<String> estilsEnum = new ArrayList<>(Arrays.asList(getNames(EstilArquitectonicType.class)));
            estilsEnum.addAll(new ArrayList<>(Arrays.asList(getNames(EstilEpocaType.class))));
            for (String estil_sep : estils) {
                String estil_sep_trim = estil_sep.trim();
                if (estilsEnum.contains(estil_sep_trim)) {
                    for (String estilValue : estilsEnum) {
                        if (estilValue.equals(estil_sep_trim)) {
                            estilsList.add(estilValue);
                        }
                    }
                }
            }
            if (estilsList.size() > 0) {
                columns.put("cod_estil", String.join("//", estilsList));
            }
        }
        if (csvRecord.isSet("DataReg")) {
            columns.put("d_alta", DibaDataToGene(csvRecord.get("DataReg")));
        }
        if (csvRecord.isSet("DataMod")) {
            columns.put("d_mod", DibaDataToGene(csvRecord.get("DataMod")));
        }
        return columns;
    }

    private HashMap<String, String> getCalculatedArcheologyColumns(CSVRecord csvRecord) {
        HashMap<String, String> columns = new HashMap<>();
        columns.put("num_jaciment", csvRecord.get("Poblacio") + ":" + csvRecord.get("NumFitxa"));
        if (csvRecord.isSet("Poblacio")) {
            MunicipiType municipi = getMunicipi(csvRecord.get("Poblacio"));
            if (municipi != null) {
                String comarca = getComarcaFromMunicipi(municipi.value());
                columns.put("Municipi_Comarca", municipi.value() + " (" + comarca + ")");
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
            Datacio datacio = getCronologiaFromSegle(csvRecord.get("Segle"), false);
            if (!datacio.getCronologiaInicial().isEmpty()){
                String epoca = getCronologiaValue(datacio.getCronologiaInicial().get(0));
                if (!datacio.getCronologiaFinal().isEmpty()){
                    epoca += " - " + getCronologiaValue(datacio.getCronologiaFinal().get(0));
                }
                columns.put("Epoques", epoca);
            }
        }

        if (csvRecord.isSet("EstatConservacio")) {
            String codiConservacio = getCodiConservacio(csvRecord.get("EstatConservacio"), false);
            if (codiConservacio != null) {
                columns.put("tip_conservació", codiConservacio);
            }
        }
        if (csvRecord.isSet("Titularitat")) {
            String codiRegim = getRegimCodi(csvRecord.get("Titularitat"), false);
            if (codiRegim != null) {
                columns.put("tip-regim", codiRegim);
            }
        }
        if (csvRecord.isSet("Codi")) {
            String tip_jac = CodiToTipusJaciment(csvRecord.get("Codi"));
            if (tip_jac != null) {
                columns.put("tip_jac", tip_jac);
            }
        }
        if (csvRecord.isSet("DataReg")) {
            columns.put("d_alta", DibaDataToGene(csvRecord.get("DataReg")));
        }
        if (csvRecord.isSet("DataMod")) {
            columns.put("d_mod", DibaDataToGene(csvRecord.get("DataMod")));
        }
        return columns;
    }

    private Iterable<String> processArchitectureColumns(CSVRecord csvRecord) {
        HashMap<String, String> calcColumns = getCalculatedArchitectureColumns(csvRecord);
        List<String> record = new ArrayList<>();
        for (String field : GENECSV.fields) {
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
                    case "Municipi_Comarca":
                    case "Epoques":
                    case "data_inicial":
                    case "data_fi":
                    case "cod_estat_global":
                    case "cod_regim":
                    case "d_alta":
                    case "d_mod":
                        if (calcColumns.containsKey(field)) {
                            recordToAdd = calcColumns.get(field);
                        }
                }
            }
            record.add(recordToAdd);
        }
        return record;
    }

    private String DibaDataToGene(String dataOrigen){
        if (dataOrigen.isEmpty()) return "";
        DateTimeFormatter formatOrigen = DateTimeFormatter.ofPattern("MM/dd/yy H:mm:ss");
        DateTimeFormatter formatDesti = DateTimeFormatter.ofPattern("dd/MM/yyyy[ H:mm]");
        LocalDate date = LocalDate.parse(dataOrigen, formatOrigen);
        return date.format(formatDesti);
    }

    private Iterable<String> processArcheologyColumns(CSVRecord csvRecord) {
        HashMap<String, String> calcColumns = getCalculatedArcheologyColumns(csvRecord);
        List<String> record = new ArrayList<>();
        for (String field : GENECSV.fields) {
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
                    case "Municipi_Comarca":
                    case "data_fi":
                    case "data_inici":
                    case "tip_conservació":
                    case "Cronologies":
                    case "tip-regim":
                    case "tip_jac":
                    case "d_alta":
                    case "d_mod":
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
        generalColumnsMapping.put("descripcio", "Descripcio");
        generalColumnsMapping.put("nom_actual", "Denom");
        generalColumnsMapping.put("usr_alta", "AutorFitxa");
        generalColumnsMapping.put("Proteccions", "DescrProtec");
    }

    private void fillArcheologyColumnsMapping() {
        archeologyColumnsMapping = new HashMap<>();
        archeologyColumnsMapping.put("coor_utm_long", "X");
        archeologyColumnsMapping.put("coor_utm_lat", "Y");
        archeologyColumnsMapping.put("context_desc", "Emplacament");
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
        architectureColumnsMapping.put("adreça", "Ubicacio");
        architectureColumnsMapping.put("utm_x", "X");
        architectureColumnsMapping.put("utm_y", "Y");
        architectureColumnsMapping.put("original_actual", "UsActual");
        architectureColumnsMapping.put("noticies_històriques", "Historia");
    }

    private boolean isArchitecture(CSVRecord record) {
        return !record.get("Codi").equals("1.4");
    }

    private MunicipiType getMunicipi(String poblacio) {
        for (MunicipiType municipi : MunicipiType.values()) {
            if (normalizedEquals(poblacio, municipi.value())) {
                return municipi;
            }
        }
        return null;
    }

    private Datacio getCronologiaFromSegle(String segles, boolean isArchitecture) {
        Datacio datacio = new Datacio();
        List<String> cronologies;
        List<String> cronologiesEpoca;
        if (isArchitecture) {
            cronologies = new ArrayList<>(Arrays.asList(getValues(CronologiaArquitectonicType.class)));
        } else {
            cronologies = new ArrayList<>(Arrays.asList(getValues(CronologiaArqueologicType.class)));
        }
        cronologiesEpoca = new ArrayList<>(Arrays.asList(getValues(EstilEpocaType.class)));
        String segleInici = "";
        String segleFi = "";
        String[] seglesSep = segles.split("-");
        if (seglesSep.length > 0) {
            segleInici = seglesSep[0].trim().toLowerCase();
        }
        if (seglesSep.length > 1) {
            segleFi = seglesSep[1].trim().toLowerCase();
        }
        for (String cronologia : cronologies) {
            if (normalizedEquals(cronologia, segleInici)) {
                if (isArchitecture) {
                    datacio.getCronologiaInicial().add(CronologiaArquitectonicType.fromValue(cronologia));
                } else {
                    datacio.getCronologiaInicial().add(CronologiaArqueologicType.fromValue(cronologia));
                }
            }
            if (normalizedEquals(cronologia, segleFi)) {
                if (isArchitecture) {
                    datacio.getCronologiaFinal().add(CronologiaArquitectonicType.fromValue(cronologia));
                } else {
                    datacio.getCronologiaFinal().add(CronologiaArqueologicType.fromValue(cronologia));
                }
            }
        }
        for (String cronologia : cronologiesEpoca) {
            if (normalizedEquals(cronologia, segleInici)) {
                datacio.getCronologiaInicial().add(EstilEpocaType.fromValue(cronologia));
            }
            if (normalizedEquals(cronologia, segleFi)) {
                datacio.getCronologiaFinal().add(EstilEpocaType.fromValue(cronologia));
            }
        }
        return datacio;
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

    private String getCodiConservacio(String estatConservacio, boolean isArchitecture) {
        String[] conservacions;
        if (isArchitecture) {
            if (normalizedEquals(estatConservacio, "regular")) {
                estatConservacio = "Mitjà";
            }
            conservacions = getNames(ConservacioEstatArquitectonicType.class);
        } else {
            conservacions = getNames(ConservacioEstatArqueologicType.class);
        }
        for (String conservacio : conservacions) {
            if (normalizedEquals(conservacio, estatConservacio)) {
                return conservacio;
            }
        }
        return null;
    }

    private boolean normalizedEquals(String str1, String str2) {
        return StringUtils.stripAccents(str1.toLowerCase()).equals(StringUtils.stripAccents(str2.toLowerCase()));
    }

    private String getRegimCodi(String regimStr, boolean isArchitecture) {
        String[] regims;
        if (isArchitecture) {
            regims = getNames(PropietariArquitectonicType.class);
        } else {
            regims = getNames(PropietariArqueologicType.class);
        }
        for (String regim : regims) {
            if (normalizedEquals(regim, regimStr)) {
                return regim;
            }
        }
        return null;
    }

    private static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }

    public static String[] getValues(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(DIBACSV2GENECSV::getValueOfEnum).toArray(String[]::new);
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

    private String getComarcaFromMunicipi(String municipi){
        for (Map.Entry<String, org.Custom.Transformations.formats.gene.common.Municipi> municipiType : municipis.entrySet()) {
            if (normalizedEquals(municipiType.getValue().getNom(), municipi)) {
                return comarques.get(municipiType.getValue().getId_comarca()).getNom();
            }
        }
        return null;
    }
    private String CodiToTipusJaciment(String codi) {
        switch (codi) {
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

    @Override
    public GENECSV convert(DIBACSV src) {
        GENECSV genecsv = new GENECSV();
        src.getRecords().forEach(r -> {
            if (isArchitecture(r)){
                genecsv.addRecord(processArchitectureColumns(r));
            } else {
                genecsv.addRecord(processArcheologyColumns(r));
            }
        } );
        return genecsv;
    }
}
