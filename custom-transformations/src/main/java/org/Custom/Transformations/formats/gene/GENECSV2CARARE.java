
package org.Custom.Transformations.formats.gene;

import eu.carare.carareschema.*;
import org.Custom.Transformations.core.Convertible;
import org.Custom.Transformations.formats.gene.common.*;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GENECSV2CARARE extends Convertible<GENECSV, CarareWrap> {
    private static final String SEPARATOR = " // ";
    private static final String CRONOLOGIA_SEPARATOR = " - ";
    private static final Pattern MUNICIPI_COMARCA_PATTERN = Pattern.compile("(.*)\\s+\\((.*)\\).*");
    private static final Pattern BCIN_PATTERN = Pattern.compile(".*BCIN \\((.*)\\).*");
    private static final Pattern BCIL_PATTERN = Pattern.compile(".*BCIL \\((.*)\\).*");
    private boolean isArchitecture;
    private String language;
    private String countryName;
    private String repositoryLocationName;
    private String institutionName;
    private String spatialReferenceSystem;
    private String europeanaRights;
    private String rights;

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

    private XMLGregorianCalendar getDate(String dateStr) throws DatatypeConfigurationException {
        //DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy[ H:mm]");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/y[ H:mm]");
        ZoneId zoneId = ZoneId.of("Europe/Madrid");
        GregorianCalendar cal;
        /*try {
            LocalDateTime formatDateTime = LocalDateTime.parse(dateStr, format);
            cal = GregorianCalendar.from(formatDateTime.atZone(zoneId));
        } catch (Exception ex){
            LocalDate formatDate = LocalDate.parse(dateStr, format);
            cal = GregorianCalendar.from(formatDate.atStartOfDay(zoneId));
        }*/
        try {
            LocalDateTime formatDateTime = LocalDateTime.parse(dateStr, format);
            cal = GregorianCalendar.from(formatDateTime.atZone(zoneId));
        } catch (Exception ex){
            LocalDate formatDate = LocalDate.parse(dateStr, format);
            cal = GregorianCalendar.from(formatDate.atStartOfDay(zoneId));
        }
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
            return fieldValue.trim().split(Pattern.quote(SEPARATOR));
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
        return Arrays.stream(e.getEnumConstants()).map(GENECSV2CARARE::getValueOfEnum).toArray(String[]::new);
    }

    private boolean hasSomeValue(CSVRecord record, String[] values){
        for (String value : values){
            if (this.strOrNull(record, value) != null){
                return true;
            }
        }
        return false;
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

    private Carare.HeritageAssetIdentification.Conditions getConditionsArqueologia(CSVRecord record){
        if (strOrNull(record,"conservacio") != null){
            String conservacio = strOrNull(record,"conservacio");
            Carare.HeritageAssetIdentification.Conditions conditions = new Carare.HeritageAssetIdentification.Conditions();
            Carare.HeritageAssetIdentification.Conditions.Condition condition = new Carare.HeritageAssetIdentification.Conditions.Condition();
            condition.setLang(language);
            ConservacioEstatArquitectonicType conEstat = getEnumValue(ConservacioEstatArquitectonicType.class, conservacio);
            if (conEstat != null){
                conservacio = conEstat.value();
                condition.setValue(conservacio);
                conditions.setCondition(condition);
                return conditions;
            }
        }
        return null;
    }

    private Carare.Activity getActivity(CSVRecord record){
        Carare.Activity activity = new Carare.Activity();
        Actors actors = new Actors();
        Actors.Name name = new Actors.Name();
        if (strOrNull(record,"nom") != null){
            name.setLang(language);
            name.setValue(strOrNull(record,"nom"));
        }
        String[] cognoms = this.getMultivaluedField(record, "cognoms");
        if (cognoms != null){
            name.setValue(name.getValue() + " " + String.join(" ", cognoms));
        }
        if (name.getValue() != null){
            actors.getName().add(name);
        }

        if (strOrNull(record,"funcio") != null){
            Actors.Roles roles = new Actors.Roles();
            roles.setLang(language);
            roles.setValue(strOrNull(record,"funcio"));
            actors.getRoles().add(roles);
        }
        Temporal temporal = new Temporal();
        Temporal.TimeSpan timeSpan = new Temporal.TimeSpan();
        if (strOrNull(record,"any_inici") != null){
            try {
                timeSpan.getStartDate().add(getDate("01/01/" + strOrNull(record,"any_inici")).toString());
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }
        if (strOrNull(record,"any_fi") != null){
            try {
                timeSpan.getEndDate().add(getDate("01/01/" + strOrNull(record,"any_fi")).toString());
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }
        if (!timeSpan.getStartDate().isEmpty() || timeSpan.getEndDate().isEmpty()){
            temporal.getTimeSpan().add(timeSpan);
            activity.getTemporal().add(temporal);
        }

        if (!actors.getRoles().isEmpty() || !actors.getName().isEmpty()){
            activity.getActors().add(actors);
        }

        if (activity.getActors().isEmpty()){
            return null;
        }
        return activity;
    }

    private Carare.HeritageAssetIdentification.Designations getDesignation(String lang, String gradeStr, String protectionTypeStr, String dateFromStr){
        Carare.HeritageAssetIdentification.Designations designation = new Carare.HeritageAssetIdentification.Designations();
        Carare.HeritageAssetIdentification.Designations.ProtectionType protectionType = new Carare.HeritageAssetIdentification.Designations.ProtectionType();
        Carare.HeritageAssetIdentification.Designations.Grade grade = new Carare.HeritageAssetIdentification.Designations.Grade();
        grade.setLang(lang);
        grade.setValue(gradeStr);
        designation.setGrade(grade);
        protectionType.setLang(lang);
        protectionType.setValue(protectionTypeStr);
        designation.setProtectionType(protectionType);
        if (dateFromStr != null){
            try {
                designation.setDateFrom(getDate(dateFromStr).toString());
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }
        return designation;
    }

    private List<Carare.HeritageAssetIdentification.Designations> getArqueologiaDesignations(CSVRecord record){
        if (!hasSomeValue(record, new String[] {"class_prot_legal", "tip_prot_legal", "Proteccions", "num_reg_estatal"})){
            return new ArrayList<>();
        }
        List<Carare.HeritageAssetIdentification.Designations> designations = new ArrayList<>();
        String[] classificacions = this.getMultivaluedField(record, "class_prot_legal");
        if (classificacions != null){
            for (String classificacio : classificacions) {
                try {
                    ClassificacioArqueologicType classArque = ClassificacioArqueologicType.fromValue(classificacio);
                    designations.add(getDesignation(language, "Protecció Legal", classArque.value(), null));
                } catch (Exception ex){

                }
            }
        }
        String BCIN = this.getBCIN(this.getMultivaluedField(record, "Proteccions"));
        String BCIL = this.getBCIL(this.getMultivaluedField(record, "Proteccions"));
        String BIC = strOrNull(record, "num_reg_estatal");
        String Proteccio = strOrNull(record, "tip_prot_legal");
        if (BCIN != null){
            designations.add(getDesignation(language, "BCIN", BCIN, null));
        }
        if (BCIL != null){
            designations.add(getDesignation(language, "BCIL", BCIL, null));
        }
        if (BIC != null){
            designations.add(getDesignation(language, "BIC", BIC, null));
        }
        if (Proteccio != null){
            designations.add(getDesignation(language, "Protecció Legal", Proteccio, null));
        }
        return designations;
    }

    private List<Carare.HeritageAssetIdentification.Designations> getArquitecturaDesignations(CSVRecord record){
        if (!hasSomeValue(record, new String[] {"classificacio", "Numero_bcin", "Numero_bic", "ct_cultura_bcil", "Proteccions"})){
            return new ArrayList<>();
        }
        List<Carare.HeritageAssetIdentification.Designations> designations = new ArrayList<>();
        String[] classificacions = this.getMultivaluedField(record, "classificacio");
        if (classificacions != null){
            for (String classificacio : classificacions) {
                try {
                    ClassificacioArquitectonicType classArque = ClassificacioArquitectonicType.fromValue(classificacio);
                    designations.add(getDesignation(language, "Protecció Legal", classArque.value(), null));
                } catch (Exception ex){

                }
            }
        }
        String BCIN1 = this.getBCIN(this.getMultivaluedField(record, "Proteccions"));
        String BCIN2 = this.strOrNull(record, "Numero_bcin");
        String BIC = strOrNull(record, "Numero_bic");
        String PCC1 = this.getBCIL(this.getMultivaluedField(record, "Proteccions"));
        String PCC2 = strOrNull(record, "ct_cultura_bcil");
        if (BCIN1 != null){
            designations.add(getDesignation(language, "BCIN", BCIN1, null));
        }
        if (BCIN2 != null){
            designations.add(getDesignation(language, "BCIN", BCIN2, null));
        }
        if (BIC != null){
            designations.add(getDesignation(language, "BIC", BIC, null));
        }
        if (PCC1 != null){
            designations.add(getDesignation(language, "PCC", PCC1, null));
        }
        if (PCC2 != null){
            designations.add(getDesignation(language, "PCC", PCC2, null));
        }
        return designations;
    }

    private List<Carare.HeritageAssetIdentification.Designations> getDesignations(CSVRecord record){
        if (this.isArchitecture){
            return getArquitecturaDesignations(record);
        } else {
            return getArqueologiaDesignations(record);
        }
    }

    private HashSet<String> getMunicipiComarques(String municipi, List<String> comarques){
        List<String> municipi_comarques = new ArrayList<>();
        String municipi_raw = StringEscapeUtils.unescapeXml(municipi);
        municipi_comarques.add(getEnumValue(MunicipiType.class, municipi_raw).value());
        if (comarques != null) {
            for (String comarca : comarques) {
                String comarca_raw = StringEscapeUtils.unescapeXml(comarca);
                municipi_comarques.add(getEnumValue(ComarcaType.class, comarca_raw).value());
            }
        }
        return new HashSet<>(municipi_comarques);
    }

    private Spatial getArqueologiaSpatial(CSVRecord record){
        if (!hasSomeValue(record, new String[] {"Municipi_Comarca", "agregat", "coor_utm_long", "coor_utm_lat"})){
            return null;
        }
        Spatial spatial = new Spatial();
        Spatial.LocationSet locationSet = new Spatial.LocationSet();
        Spatial.LocationSet.NamedLocation namedLocation = new Spatial.LocationSet.NamedLocation();
        namedLocation.setLang(language);

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
        if (!mcpis.isEmpty()){
            HashSet<String> municipi_comarques = getMunicipiComarques(mcpis.get(0), comarques);
            namedLocation.setValue(String.join(", ", municipi_comarques));
            locationSet.getNamedLocation().add(namedLocation);
        }
        if (strOrNull(record,"agregat") != null){
            Spatial.LocationSet.GeopoliticalArea geopoliticalArea = new Spatial.LocationSet.GeopoliticalArea();
            geopoliticalArea.setLang(language);
            geopoliticalArea.setValue(strOrNull(record,"agregat"));
            geopoliticalArea.setType("agregat");
            locationSet.setGeopoliticalArea(geopoliticalArea);
        }
        if (strOrNull(record,"Cadastre") != null){
            Spatial.LocationSet.CadastralReference cadastralReference = new Spatial.LocationSet.CadastralReference();
            cadastralReference.setValue(strOrNull(record,"Cadastre"));
            locationSet.setCadastralReference(cadastralReference);
        }
        if (hasSomeValue(record, new String[] {"coor_utm_long", "coor_utm_lat"})){
            Spatial.Geometry geometry = new Spatial.Geometry();
            geometry.setSpatialReferenceSystem(spatialReferenceSystem);
            if (strOrNull(record,"Tipus de representació espacial") != null){
                spatial.setRepresentations(strOrNull(record,"Tipus de representació espacial"));
            }
            if (strOrNull(record,"coor_utm_long") != null && strOrNull(record,"coor_utm_lat") != null){
                Spatial.Geometry.Quickpoint quickPoint = new Spatial.Geometry.Quickpoint();
                quickPoint.setX(new BigDecimal(strOrNull(record,"coor_utm_long").replace(",", ".")));
                quickPoint.setY(new BigDecimal(strOrNull(record,"coor_utm_lat").replace(",", ".")));
                geometry.setQuickpoint(quickPoint);
            }
            spatial.setGeometry(geometry);
        }
        spatial.setLocationSet(locationSet);
        return spatial;
    }

    private Spatial getArquitecturaSpatial(CSVRecord record){
        if (!hasSomeValue(record, new String[] {"Municipi_Comarca", "agregat", "utm_x", "utm_y"})){
            return null;
        }
        Spatial spatial = new Spatial();
        Spatial.LocationSet locationSet = new Spatial.LocationSet();
        Spatial.LocationSet.NamedLocation namedLocation = new Spatial.LocationSet.NamedLocation();
        namedLocation.setLang(language);

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
        if (!mcpis.isEmpty()){
            HashSet<String> municipi_comarques = getMunicipiComarques(mcpis.get(0), comarques);
            namedLocation.setValue(String.join(", ", municipi_comarques));
            locationSet.getNamedLocation().add(namedLocation);
        }
        if (strOrNull(record,"agregat") != null){
            Spatial.LocationSet.GeopoliticalArea geopoliticalArea = new Spatial.LocationSet.GeopoliticalArea();
            geopoliticalArea.setLang(language);
            geopoliticalArea.setValue(strOrNull(record,"agregat"));
            geopoliticalArea.setType("agregat");
            locationSet.setGeopoliticalArea(geopoliticalArea);
        }
        if (strOrNull(record,"Cadastre") != null){
            Spatial.LocationSet.CadastralReference cadastralReference = new Spatial.LocationSet.CadastralReference();
            cadastralReference.setValue(strOrNull(record,"Cadastre"));
            locationSet.setCadastralReference(cadastralReference);
        }
        if (hasSomeValue(record, new String[] {"Sistema de referència geoespacial", "Tipus de representació espacial", "utm_x", "utm_y"})){
            Spatial.Geometry geometry = new Spatial.Geometry();
            geometry.setSpatialReferenceSystem(spatialReferenceSystem);
            if (strOrNull(record,"Tipus de representació espacial") != null){
                spatial.setRepresentations(strOrNull(record,"Tipus de representació espacial"));
            }
            if (strOrNull(record,"utm_x") != null && strOrNull(record,"utm_y") != null){
                Spatial.Geometry.Quickpoint quickPoint = new Spatial.Geometry.Quickpoint();
                quickPoint.setX(new BigDecimal(strOrNull(record,"utm_x").replace(",", ".")));
                quickPoint.setY(new BigDecimal(strOrNull(record,"utm_y").replace(",", ".")));
                geometry.setQuickpoint(quickPoint);
            }
            spatial.setGeometry(geometry);
        }
        spatial.setLocationSet(locationSet);
        return spatial;
    }

    private Spatial getSpatial(CSVRecord record){
        if (this.isArchitecture){
            return getArquitecturaSpatial(record);
        } else {
            return getArqueologiaSpatial(record);
        }
    }

    private String getAltresNoms(CSVRecord record){
        if (this.isArchitecture){
            return strOrNull(record, "altres_noms");
        } else {
            return strOrNull(record, "nom_altres");
        }
    }

    private Carare.HeritageAssetIdentification getHeritageAssetIdentification(CSVRecord record){
        Carare.HeritageAssetIdentification heritageAssetIdentification = new Carare.HeritageAssetIdentification();
        if (!this.isArchitecture){
            Carare.HeritageAssetIdentification.Conditions conditions = getConditionsArqueologia(record);
            if (conditions != null){
                heritageAssetIdentification.getConditions().add(getConditionsArqueologia(record));
            }
        }
        heritageAssetIdentification.getDesignations().addAll(getDesignations(record));
        heritageAssetIdentification.getSpatial().add(getSpatial(record));
        heritageAssetIdentification.setRecordInformation(getRecordInformation(record));
        Carare.HeritageAssetIdentification.RepositoryLocation repositoryLocation = new Carare.HeritageAssetIdentification.RepositoryLocation();
        repositoryLocation.setLang(language);
        repositoryLocation.setValue(repositoryLocationName);
        heritageAssetIdentification.getRepositoryLocation().add(repositoryLocation);
        Carare.HeritageAssetIdentification.Characters characters = getCharacters(record);
        if (characters != null){
            heritageAssetIdentification.setCharacters(characters);
        }
        Appellation appellation = new Appellation();
        Appellation.Name appellationName = new Appellation.Name();
        appellation.setId(getId(record));
        appellationName.setValue(strOrNull(record,"nom_actual"));
        appellationName.setLang(language);
        appellationName.setPreferred(true);
        appellation.getName().add(appellationName);
        if (getAltresNoms(record) != null){
            Appellation.Name appellationAltres = new Appellation.Name();
            appellationAltres.setValue(getAltresNoms(record));
            appellationAltres.setLang(language);
            appellationAltres.setPreferred(false);
            appellation.getName().add(appellationAltres);
        }
        heritageAssetIdentification.getAppellation().add(appellation);
        if (strOrNull(record,"descripcio") != null){
            Description description = new Description();
            description.setLang(language);
            description.setValue(strOrNull(record,"descripcio"));
            description.setType("General");
            description.setPreferred(true);
            heritageAssetIdentification.getDescription().add(description);
        }
        String noticiesHistoriques = null;
        if (this.isArchitecture){
            if (strOrNull(record,"NotíciesHistòriques") != null){
                noticiesHistoriques = strOrNull(record, "noticies_històriques");
            }
        } else {
            noticiesHistoriques = strOrNull(record, "notes");
        }
        if (noticiesHistoriques != null){
            Description descriptionNot = new Description();
            descriptionNot.setLang(language);
            descriptionNot.setValue(strOrNull(record,"descripcio"));
            descriptionNot.setType("Historic News");
            descriptionNot.setValue(noticiesHistoriques);
            heritageAssetIdentification.getDescription().add(descriptionNot);
        }

        return heritageAssetIdentification;
    }

    private Carare.HeritageAssetIdentification.Characters getCharacters(CSVRecord record){
        Carare.HeritageAssetIdentification.Characters characters = new Carare.HeritageAssetIdentification.Characters();
        String[] materialsStr = getMultivaluedField(record, "Materials");
        if (materialsStr != null){
            for (String material : materialsStr){
                Carare.HeritageAssetIdentification.Characters.Materials materials = new Carare.HeritageAssetIdentification.Characters.Materials();
                materials.setLang(language);
                materials.setValue(material);
                characters.getMaterials().add(materials);
            }
        }
        String[] inscripcionsStr = getMultivaluedField(record, "Incripcions");
        if (inscripcionsStr != null){
            for (String incripcio : inscripcionsStr){
                Carare.HeritageAssetIdentification.Characters.Inscriptions incripcions = new Carare.HeritageAssetIdentification.Characters.Inscriptions();
                incripcions.setLang(language);
                incripcions.setValue(incripcio);
                characters.getInscriptions().add(incripcions);
            }
        }
        if (!this.isArchitecture){
            String[] tip_jacs = getMultivaluedField(record, "tip_jac");
            if (tip_jacs != null){
                for (String tip_jac : tip_jacs){
                    TipologiaArqueologicType tip_jac_arqueologic = getEnumValue(TipologiaArqueologicType.class, tip_jac);
                    if (tip_jac_arqueologic != null){
                        tip_jac = tip_jac_arqueologic.value();
                    }
                    Carare.HeritageAssetIdentification.Characters.HeritageAssetType tipologia = new Carare.HeritageAssetIdentification.Characters.HeritageAssetType();
                    tipologia.setLang(language);
                    tipologia.setValue(tip_jac);
                    tipologia.setNamespace("http://cultura.gencat.cat/tipologies");
                    characters.getHeritageAssetType().add(tipologia);
                }
            }
        }

        if (this.isArchitecture){
            String[] usos = getMultivaluedField(record, "original_actual");
            if (usos != null){
                for (String us : usos){
                    String usFinal = null;
                    UtilitzacioType tipusUtilitzacio = getEnumValue(UtilitzacioType.class, us);
                    if (tipusUtilitzacio != null){
                        usFinal = tipusUtilitzacio.value();
                    } else {
                        OriginalActualType tipusOriginalActual = getEnumValue(OriginalActualType.class, us);
                        if (tipusOriginalActual != null) {
                            usFinal = tipusOriginalActual.value();
                        }
                    }
                    if (usFinal == null){
                        usFinal = us;
                    }
                    Carare.HeritageAssetIdentification.Characters.HeritageAssetType original = new Carare.HeritageAssetIdentification.Characters.HeritageAssetType();
                    original.setLang(language);
                    original.setValue(usFinal);
                    original.setNamespace("http://cultura.gencat.cat/usos");
                    characters.getHeritageAssetType().add(original);
                }
            }
        }
        if (this.isArchitecture){
            String[] estils = getMultivaluedField(record, "cod_estil");
            if (estils != null){
                for (String estil : estils){
                    String estilFinal = null;
                    EstilArquitectonicType estilArquitectonic = getEnumValue(EstilArquitectonicType.class, estil);
                    if (estilArquitectonic != null){
                        estilFinal = estilArquitectonic.value();
                    } else {
                        EstilEpocaType estilEpoca = getEnumValue(EstilEpocaType.class, estil);
                        if (estilEpoca != null){
                            estilFinal = estilEpoca.value();
                        }
                    }
                    if (estilFinal == null){
                        estilFinal = estil;
                    }
                    Carare.HeritageAssetIdentification.Characters.HeritageAssetType estilHeritage = new Carare.HeritageAssetIdentification.Characters.HeritageAssetType();
                    estilHeritage.setLang(language);
                    estilHeritage.setValue(estilFinal);
                    estilHeritage.setNamespace("http://cultura.gencat.cat/estil");
                    characters.getHeritageAssetType().add(estilHeritage);
                }
            }
        }
        Temporal temporal = null;
        if (this.isArchitecture){
            temporal = getArquitecturaTemporal(record);
        } else {
            temporal = getArqueologiaTemporal(record);
        }
        if (temporal != null){
            characters.getTemporal().add(temporal);
        }

        if (!characters.getTemporal().isEmpty() || !characters.getHeritageAssetType().isEmpty() || !characters.getInscriptions().isEmpty() || !characters.getMaterials().isEmpty()){
            return characters;
        }
        return null;
    }

    private Temporal getArqueologiaTemporal(CSVRecord record){
        if (!hasSomeValue(record, new String[] {"Cronologies", "data_inici", "data_fi"})){
            return null;
        }
        String[] anys_inici = this.getMultivaluedField(record, "data_inici");
        String[] anys_fi = this.getMultivaluedField(record, "data_fi");
        String[] cronologies = this.getMultivaluedField(record, "Cronologies");
        Temporal temporal = new Temporal();

        if (anys_inici != null || anys_fi != null){
            Temporal.TimeSpan timeSpan = new Temporal.TimeSpan();
            if (anys_inici != null){
                for (String any_inici : anys_inici){
                    try {
                        timeSpan.getStartDate().add(getDate("01/01/" + any_inici).toString());
                    } catch (DatatypeConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (anys_fi != null) {
                for (String any_fi : anys_fi) {
                    try {
                        timeSpan.getEndDate().add(getDate("01/01/" + any_fi).toString());
                    } catch (DatatypeConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            }
            temporal.getTimeSpan().add(timeSpan);
        }


        if (cronologies != null){
            for (String cronologia : cronologies){
                Temporal.PeriodName periodName = new Temporal.PeriodName();
                String[] cronologies_sep = cronologia.trim().split(Pattern.quote(CRONOLOGIA_SEPARATOR));
                String cronologia_inicial = StringEscapeUtils.unescapeXml(cronologies_sep[0]);
                String cronologia_final = "";

                String cronologiaInicial = null;
                String cronologiaFinal = null;
                String cronologiaTemporalFinal = null;

                if (cronologies_sep.length == 2){
                    cronologia_final = StringEscapeUtils.unescapeXml(cronologies_sep[1]);
                }
                try {
                    cronologiaInicial = getEnumValue(CronologiaArqueologicType.class, cronologia_inicial).value();
                    cronologiaFinal = cronologiaInicial;
                } catch (Exception ignored){
                    try {
                        cronologiaInicial = getEnumValue(EstilEpocaType.class, cronologia_inicial).value();
                        cronologiaFinal = cronologiaInicial;
                    } catch (Exception ignored2){
                    }
                }
                if (cronologiaInicial == null){
                    continue;
                }

                try {
                    cronologiaTemporalFinal = cronologiaFinal;
                    cronologiaFinal = getEnumValue(CronologiaArqueologicType.class, cronologia_final).value();
                } catch (Exception ignored){
                    try {
                        cronologiaFinal = getEnumValue(EstilEpocaType.class, cronologia_final).value();
                    } catch (Exception ignored2){
                        cronologiaFinal = cronologiaTemporalFinal;
                    }
                }

                if (cronologiaFinal == null){
                    cronologiaFinal = cronologiaInicial;
                }

                periodName.setLang(language);
                if (cronologiaFinal.equals(cronologiaInicial)){
                    periodName.setValue(cronologiaInicial);
                } else {
                    periodName.setValue(cronologiaInicial + " - " + cronologiaFinal);
                }

                temporal.getPeriodName().add(periodName);
            }
        }

        if (!temporal.getTimeSpan().isEmpty() || !temporal.getPeriodName().isEmpty()){
            return temporal;
        }
        return null;
    }

    private Temporal getArquitecturaTemporal(CSVRecord record){
        if (!hasSomeValue(record, new String[] {"Epoques", "data_inicial", "data_fi"})){
            return null;
        }
        String[] anys_inici = this.getMultivaluedField(record, "data_inicial");
        String[] anys_fi = this.getMultivaluedField(record, "data_fi");
        String[] cronologies = this.getMultivaluedField(record, "Epoques");
        Temporal temporal = new Temporal();

        if (anys_inici != null || anys_fi != null){
            Temporal.TimeSpan timeSpan = new Temporal.TimeSpan();
            if (anys_inici != null){
                for (String any_inici : anys_inici){
                    try {
                        timeSpan.getStartDate().add(getDate("01/01/" + any_inici).toString());
                    } catch (DatatypeConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (anys_fi != null) {
                for (String any_fi : anys_fi) {
                    try {
                        timeSpan.getEndDate().add(getDate("01/01/" + any_fi).toString());
                    } catch (DatatypeConfigurationException e) {
                        e.printStackTrace();
                    }
                }
            }
            temporal.getTimeSpan().add(timeSpan);
        }



        if (cronologies != null){
            for (String cronologia : cronologies){
                Temporal.PeriodName periodName = new Temporal.PeriodName();
                String[] cronologies_sep = cronologia.trim().split(Pattern.quote(CRONOLOGIA_SEPARATOR));
                String cronologia_inicial = StringEscapeUtils.unescapeXml(cronologies_sep[0]);
                String cronologia_final = "";

                String cronologiaInicial = null;
                String cronologiaFinal = null;
                String cronologiaTemporalFinal = null;

                if (cronologies_sep.length == 2){
                    cronologia_final = StringEscapeUtils.unescapeXml(cronologies_sep[1]);
                }
                try {
                    cronologiaInicial = getEnumValue(CronologiaArquitectonicType.class, cronologia_inicial).value();
                    cronologiaFinal = cronologiaInicial;
                } catch (Exception ignored){
                    try {
                        cronologiaInicial = getEnumValue(EstilEpocaType.class, cronologia_inicial).value();
                        cronologiaFinal = cronologiaInicial;
                    } catch (Exception ignored2){
                    }
                }
                if (cronologiaInicial == null){
                    continue;
                }

                try {
                    cronologiaTemporalFinal = cronologiaFinal;
                    cronologiaFinal = getEnumValue(CronologiaArquitectonicType.class, cronologia_final).value();
                } catch (Exception ignored){
                    try {
                        cronologiaFinal = getEnumValue(EstilEpocaType.class, cronologia_final).value();
                    } catch (Exception ignored2){
                        cronologiaFinal = cronologiaTemporalFinal;
                    }
                }

                if (cronologiaFinal == null){
                    cronologiaFinal = cronologiaInicial;
                }

                periodName.setLang(language);
                if (cronologiaFinal.equals(cronologiaInicial)){
                    periodName.setValue(cronologiaInicial);
                } else {
                    periodName.setValue(cronologiaInicial + " - " + cronologiaFinal);
                }
                temporal.getPeriodName().add(periodName);
            }
        }

        if (!temporal.getTimeSpan().isEmpty() || !temporal.getPeriodName().isEmpty()){
            return temporal;
        }
        return null;

    }

    private String getId(CSVRecord record){
        if (this.isArchitecture){
            return this.strOrNull(record, "cod_arq");
        } else {
            return this.strOrNull(record, "num_jaciment");
        }
    }

    private Carare.HeritageAssetIdentification.RecordInformation getRecordInformation(CSVRecord record){
        Carare.HeritageAssetIdentification.RecordInformation recordInformation = new Carare.HeritageAssetIdentification.RecordInformation();
        recordInformation.setId(getId(record));
        RecordInformation.Country country = new RecordInformation.Country();
        country.setLang(language);
        country.setValue(countryName);
        recordInformation.setCountry(country);
        RecordInformation.Source source = new RecordInformation.Source();
        source.setLang(language);
        source.setValue(institutionName);
        recordInformation.setSource(source);
        RecordInformation.Creation creation = new RecordInformation.Creation();
        RecordInformation.Update update = new RecordInformation.Update();
        try {
            if (hasSomeValue(record, new String[] {"d_alta", "usr_alta"})){
                if (strOrNull(record, "d_alta") != null){
                    creation.setDate(getDate(strOrNull(record, "d_alta")));
                }
                if (strOrNull(record, "usr_alta") != null && !strOrNull(record, "usr_alta").trim().isEmpty()){
                    Contacts contacts = new Contacts();
                    Contacts.Name name = new Contacts.Name();
                    name.setValue(strOrNull(record, "usr_alta"));
                    name.setLang(language);
                    contacts.getName().add(name);
                    creation.getContacts().add(contacts);
                }
                recordInformation.setCreation(creation);
            }
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        if (strOrNull(record, "d_mod") != null){
            try {
                update.setDate(getDate(strOrNull(record, "d_mod")));
                recordInformation.setUpdate(update);
            } catch (DatatypeConfigurationException e) {
                e.printStackTrace();
            }
        }


        return recordInformation;
    }

    private Carare.CollectionInformation getCollectionInformation(CSVRecord record) {
        Carare.CollectionInformation collectionInformation = new Carare.CollectionInformation();
        Carare.CollectionInformation.Keywords keywords = new Carare.CollectionInformation.Keywords();
        Rights rights = new Rights();
        Rights.CopyrightCreditLine copyrightCreditLine = new Rights.CopyrightCreditLine();
        copyrightCreditLine.setValue(this.rights);
        copyrightCreditLine.setLang(language);
        rights.setCopyrightCreditLine(copyrightCreditLine);
        rights.setEuropeanaRights(this.europeanaRights);
        collectionInformation.setRights(rights);
        keywords.setLang(language);
        if (this.isArchitecture){
            keywords.setValue("Arquitectura");
        } else {
            keywords.setValue("Arqueologia");
        }
        collectionInformation.getKeywords().add(keywords);
        return collectionInformation;
    }

    @Override
    public CarareWrap convert(GENECSV src) {
        //this.provider = this.getParams().getOrDefault("provider", "GENE");
        this.language = this.getParams().getOrDefault("language", "ca_ES");
        this.isArchitecture = Boolean.parseBoolean(this.getParams().getOrDefault("isArchitecture", "true"));
        this.institutionName = this.getParams().getOrDefault("institutionName", "Generalitat de Catalunya");
        this.repositoryLocationName = this.getParams().getOrDefault("repositoryLocationName", "Generalitat de Catalunya");
        this.spatialReferenceSystem = this.getParams().getOrDefault("spatialReferenceSystem", "EPSG:32631");
        this.countryName = this.getParams().getOrDefault("countryName", "Espanya");
        this.rights = this.getParams().getOrDefault("rights", "La ©Generalitat de Catalunya permet la reutilització dels continguts i de les dades sempre que se citi la font i la data d'actualització, que no es desnaturalitzi la informació i que no es contradigui amb una llicència específica.");
        this.europeanaRights = this.getParams().getOrDefault("europeanaRights", "Copyright Not Evaluated (CNE)");
        CarareWrap carareWrap = new CarareWrap();
        List<Carare> carareList = carareWrap.getCarare();
        for (CSVRecord record : src.getRecords()) {
            Carare carare = new Carare();
            carare.setId(getId(record));
            carare.setHeritageAssetIdentification(getHeritageAssetIdentification(record));
            carare.getCollectionInformation().add(getCollectionInformation(record));
            if (this.isArchitecture){
                Carare.Activity activity = getActivity(record);
                if (activity != null){
                    carare.getActivity().add(getActivity(record));
                }
            }
            carareList.add(carare);
        }
        return carareWrap;
    }
}
