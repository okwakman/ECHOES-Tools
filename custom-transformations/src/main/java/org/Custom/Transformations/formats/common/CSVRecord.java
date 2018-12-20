package org.Custom.Transformations.formats.common;

import java.util.Map;

public class CSVRecord {
    org.apache.commons.csv.CSVRecord csvRecord;
    Map<String, String> headers;
    CSVRecord(Map<String, String> headers, org.apache.commons.csv.CSVRecord csvRecord) {
        this.headers = headers;
        this.csvRecord = csvRecord;
    }

    public boolean isSet(String name){
        boolean parentIsSet;
        if (this.headers.containsKey(name) && !this.headers.get(name).isEmpty()){
            parentIsSet = csvRecord.isSet(this.headers.get(name));
        } else {
            parentIsSet = csvRecord.isSet(name);
        }
        return parentIsSet && !get(name).equals("NULL");
    }

    public String get(String name) {
        if (!this.headers.get(name).isEmpty()){
            return csvRecord.get(this.headers.get(name));
        }
        return csvRecord.get(name);
    }
}
