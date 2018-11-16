package org.Custom.Transformations.formats.gene;

import eu.carare.carareschema.CarareWrap;
import org.Custom.Transformations.core.Convertible;
import org.Custom.Transformations.formats.carare.CARARE2CARARERDF;

import java.io.File;

public class GENECSV2CARARERDF extends Convertible<GENECSV,File> {
    @Override
    public File convert(GENECSV src) {
        GENECSV2CARARE gene2carareConverter = new GENECSV2CARARE();
        gene2carareConverter.getParams().put("isArchitecture", this.getParams().getOrDefault("isArchitecture", "true"));
        CarareWrap carare = gene2carareConverter.convert(src);
        CARARE2CARARERDF carare2cararerdf = new CARARE2CARARERDF();
        return carare2cararerdf.convert(carare);
    }
}
