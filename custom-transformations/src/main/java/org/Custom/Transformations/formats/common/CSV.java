package org.Custom.Transformations.formats.common;

import org.Custom.Transformations.core.Inputable;
import org.Custom.Transformations.core.Outputable;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.nio.file.Files;

import java.nio.file.Path;
import java.util.*;

public class CSV implements Inputable, Outputable {
    private Map<String, String> headers = new HashMap<>();
    private List<Iterable<String>> recordsStr = new ArrayList<>();

    @Override
    public void load(String value) {
        load(new StringReader(value));
    }

    @Override
    public void load(Path path){
        try {
            load(new FileReader(path.toFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    protected void load(Reader reader){
        try {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader().withSkipHeaderRecord());
            List<CSVRecord> recordsCSV = csvParser.getRecords();
            for (CSVRecord record : recordsCSV){
                Map<String, String> valuesMap = record.toMap();
                List<String> valuesList = new ArrayList<>();
                for (String header : getCSVHeaders()){
                    valuesList.add(valuesMap.get(header));
                }
                this.recordsStr.add(valuesList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void save(Path path) throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(path);
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(getCSVHeaders()));
        for (Iterable<String> record : recordsStr){
            csvPrinter.printRecord(record);
        }
        csvPrinter.flush();
    }

    @Override
    public String getString() {
        StringWriter writer = new StringWriter();
        CSVPrinter csvPrinter;
        try {
            csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(getCSVHeaders()));
            for (Iterable<String> record : recordsStr){
                csvPrinter.printRecord(record);
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    private String[] getCSVHeaders(){
        Set<String> csvHeaders = new HashSet<>();
        for (Map.Entry<String, String> entry : headers.entrySet()){
            if (!entry.getValue().isEmpty()){
                csvHeaders.add(entry.getValue());
            } else {
                csvHeaders.add(entry.getKey());
            }
        }
        return csvHeaders.toArray(new String[0]);
    }

    protected void setHeader(Map headers){
        this.headers = headers;
    }

    protected void setHeader(String[] headers){
        Arrays.stream(headers).forEach(h -> this.headers.put(h, h));
    }

    public void addRecord(Iterable<String> record){
        this.recordsStr.add(record);
    }

    public List<org.Custom.Transformations.formats.common.CSVRecord> getRecords(){
        List<org.Custom.Transformations.formats.common.CSVRecord> records = new ArrayList<>();
        try {
            CSVParser csvParser = CSVParser.parse(getString(), CSVFormat.DEFAULT.withHeader(getCSVHeaders()).withSkipHeaderRecord());
            csvParser.getRecords().forEach(r -> records.add(new org.Custom.Transformations.formats.common.CSVRecord(headers, r)));
            return records;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
