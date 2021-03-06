package org.csuc.service.quality;


/**
 * @author amartinez
 */
public class QualityRequest {

    private String dataset;
    private String format;
    private String user;


    public QualityRequest() {
    }

    public String getDataset() {
        return dataset;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
