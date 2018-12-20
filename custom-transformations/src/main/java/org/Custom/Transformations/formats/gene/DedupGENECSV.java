package org.Custom.Transformations.formats.gene;

import org.apache.commons.csv.*;

import java.io.*;
import java.util.*;

public class DedupGENECSV extends GENECSV {
    protected void load(Reader reader){
        Map<String, Map<String, Set<String>>> uniqueInfo = new HashMap<>();
        try {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader().withSkipHeaderRecord());
            List<CSVRecord> recordsCSV = csvParser.getRecords();

            for (CSVRecord record : recordsCSV){
                String code = record.get("CODI");
                if (!uniqueInfo.containsKey(code)){
                    Map<String, Set<String>> valuesMap = new HashMap<>();
                    valuesMap.put(code, new HashSet<>(Arrays.asList(code)));
                    uniqueInfo.put(code, valuesMap);
                }
                for (Map.Entry<String, String> entry : record.toMap().entrySet()){
                    String value = "";
                    if (!entry.getValue().equals("NULL") || entry.getKey().equals("CRONO_INICI") || entry.getKey().equals("CRONO_FI")) {
                        value = entry.getValue().trim();
                    }
                    if (!value.isEmpty()){
                        if (!uniqueInfo.get(code).containsKey(entry.getKey())) {
                            uniqueInfo.get(code).put(entry.getKey(), new HashSet<>(Arrays.asList(value.trim())));
                        } else {
                            uniqueInfo.get(code).get(entry.getKey()).add(value.trim());
                        }
                    }
                }
            }
            StringWriter writer = new StringWriter();
            CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(csvParser.getHeaderMap().keySet().toArray(new String[1])));
            for (String code : uniqueInfo.keySet()){
                for (String field : csvParser.getHeaderMap().keySet()){
                    Set<String> values = uniqueInfo.get(code).get(field);
                    if (values == null){
                        csvPrinter.print("");
                    } else {
                        csvPrinter.print(String.join(GENECSV2CARARE.SEPARATOR, values));
                    }
                }
                csvPrinter.println();
            }
            csvPrinter.flush();
            writer.close();
            super.load(new StringReader(writer.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
