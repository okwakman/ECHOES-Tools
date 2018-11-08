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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSV implements Inputable, Outputable {
    private String[] headers = new String[1];
    private List<CSVRecord> recordsCSV = new ArrayList<>();
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

    private void load(Reader reader){
        try {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader().withSkipHeaderRecord());
            this.recordsCSV = csvParser.getRecords();
            for (CSVRecord record : this.recordsCSV){
                Map<String, String> valuesMap = record.toMap();
                List<String> valuesList = new ArrayList<>();
                for (String header : headers){
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
        CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
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
            csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(headers));
            for (Iterable<String> record : recordsStr){
                csvPrinter.printRecord(record);
            }
            csvPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return writer.toString();
    }

    protected void setHeader(String[] headers){
        this.headers = headers;
    }

    public void addRecord(Iterable<String> record){
        this.recordsStr.add(record);
    }

    public List<CSVRecord> getRecords(){
        try {
            CSVParser csvParser = CSVParser.parse(getString(), CSVFormat.DEFAULT.withHeader(headers).withSkipHeaderRecord());
            return csvParser.getRecords();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*public List<CSVRecord> getOriginalRecords(){
        return this.recordsCSV;
    }*/
}
