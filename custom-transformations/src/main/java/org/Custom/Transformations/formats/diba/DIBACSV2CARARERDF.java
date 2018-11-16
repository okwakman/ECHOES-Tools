package org.Custom.Transformations.formats.diba;

import eu.carare.carareschema.CarareWrap;
import org.Custom.Transformations.core.Convertible;
import org.Custom.Transformations.formats.carare.CARARE2CARARERDF;
import org.Custom.Transformations.formats.gene.GENECSV;
import org.Custom.Transformations.formats.gene.GENECSV2CARARE;

import java.io.File;

public class DIBACSV2CARARERDF extends Convertible<DIBACSV,File> {
    @Override
    public File convert(DIBACSV src) {
        DIBACSV2GENECSV diba2geneConverter = new DIBACSV2GENECSV();
        GENECSV genecsv = diba2geneConverter.convert(src);
        GENECSV2CARARE gene2carareConverter = new GENECSV2CARARE();
        gene2carareConverter.getParams().put("isArchitecture", this.getParams().getOrDefault("isArchitecture", "true"));
        CarareWrap carare = gene2carareConverter.convert(genecsv);
        CARARE2CARARERDF carare2cararerdf = new CARARE2CARARERDF();
        return carare2cararerdf.convert(carare);
    }
}
