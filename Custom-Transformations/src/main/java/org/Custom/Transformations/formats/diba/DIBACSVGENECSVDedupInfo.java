package org.Custom.Transformations.formats.diba;

import org.Custom.Transformations.formats.gene.GENECSV2GENERDF;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DIBACSVGENECSVDedupInfo {
    private Iterable<CSVRecord> dibaRecords;
    private List<CSVRecord> listGeneRecords;
    private boolean isGeneArchitecture;

    public DIBACSVGENECSVDedupInfo(String dibaFileName, String geneFileName, boolean isGeneArchitecture) throws IOException {
        dibaRecords = CSVFormat.DEFAULT.withHeader().parse(new FileReader(dibaFileName));
        Iterable<CSVRecord> geneRecords = CSVFormat.DEFAULT.withHeader().parse(new FileReader(geneFileName));
        listGeneRecords = new ArrayList<>();
        geneRecords.forEach(listGeneRecords::add);
        this.isGeneArchitecture = isGeneArchitecture;
    }

    public HashMap<String, List<String>> getIdentifierDedup() {
        HashMap<String, List<String>> identifiersMap = new HashMap<>();
        for (CSVRecord dibaRecord : dibaRecords) {
            for (CSVRecord geneRecord : listGeneRecords) {
                if (isRegistryEquals(dibaRecord, geneRecord)) {
                    String geneIdentifier = getGeneIdentifier(geneRecord);
                    String dibaIdentifier = getDibaIdentifier(dibaRecord);
                    if (!identifiersMap.containsKey(dibaIdentifier)) {
                        identifiersMap.put(dibaIdentifier, new ArrayList<>());
                    }
                    identifiersMap.get(dibaIdentifier).add(geneIdentifier);
                }
            }
        }
        return identifiersMap;
    }

    private String getDibaIdentifier(CSVRecord record) {
        return GENECSV2GENERDF.getIdentificadorIdentifier(isArchitecture(record), record.get("Poblacio") + ":" + record.get("NumFitxa"));
    }

    private String getGeneIdentifier(CSVRecord record) {
        String id;
        if (isGeneArchitecture) {
            id = record.get("cod_arq");
        } else {
            id = record.get("num_jaciment");
        }
        return GENECSV2GENERDF.getIdentificadorIdentifier(isGeneArchitecture, id);
    }

    private boolean isRegistryEquals(CSVRecord dibaRecord, CSVRecord geneRecord) {
        if (!dibaRecord.isSet("DescrProtec") && !dibaRecord.isSet("Inventari")) {
            return false;
        }
        String proteccioDiba = null;
        if (dibaRecord.isSet("DescrProtec")) {
            proteccioDiba = dibaRecord.get("DescrProtec");
        }
        String proteccioGene = null;
        if (geneRecord.isSet("Proteccions")) {
            proteccioGene = geneRecord.get("Proteccions");
        }
        if (isGeneArchitecture && dibaRecord.isSet("Inventari")) {
            int dibaIPA = getIPADiba(dibaRecord.get("Inventari"));
            int geneIPA = Integer.parseInt(geneRecord.get("cod_arq"));
            if (dibaIPA == geneIPA) {
                return true;
            }
        }
        if (proteccioDiba == null || proteccioGene == null) {
            return false;
        }

        int dibaBCIL = getBCIL(proteccioDiba);
        int geneBCIL = getBCIL(proteccioGene);
        if (dibaBCIL != -1 && dibaBCIL == geneBCIL) {
            return true;
        }
        int dibaBCIN = getBCIN(proteccioDiba);
        return dibaBCIN != -1 && getBCIN(geneRecord.get("Proteccions")) == dibaBCIN;
    }

    private int getIPADiba(String text) {
        Pattern pattern = Pattern.compile("(?i)(?:IPAC?[-\\s_]*(?:CA)?[-\\s_]*(?:n\\s*º)?[-:\\s_]*0*(\\d+))|(?:(?:n\\s*º)?[-:\\s_]*0*(\\d+)[-\\s_]*IPAC?[-\\s_]*(?:CA)?)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find() && matcher.group(1) != null) {
            return Integer.valueOf(matcher.group(1));
        }
        return -1;
    }

    private int getBCIL(String text) {
        Pattern pattern = Pattern.compile("(?i)0*(\\d+)[\\s-_]*[il|]");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return Integer.valueOf(matcher.group(1));
        }
        return -1;
    }

    private int getBCIN(String text) {
        Pattern pattern = Pattern.compile("(?i)0*(?:(\\d+)[\\s-_]*(?:MH|CPCC|CCPC))|(?:(?:MH|CPCC|CCPC)[\\s-_]*0*(\\d+))");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            if (matcher.group(1) != null) {
                return Integer.valueOf(matcher.group(1));
            }
        }
        return -1;
    }

    private boolean isArchitecture(CSVRecord record) {
        return !record.get("Codi").equals("1.4");
    }

}
