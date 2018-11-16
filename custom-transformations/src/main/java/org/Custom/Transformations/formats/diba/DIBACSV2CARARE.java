package org.Custom.Transformations.formats.diba;

import eu.carare.carareschema.CarareWrap;
import org.Custom.Transformations.core.Convertible;
import org.Custom.Transformations.formats.gene.GENECSV;
import org.Custom.Transformations.formats.gene.GENECSV2CARARE;


public class DIBACSV2CARARE extends Convertible<DIBACSV, CarareWrap> {

    @Override
    public CarareWrap convert(DIBACSV src) {
        DIBACSV2GENECSV diba2geneConverter = new DIBACSV2GENECSV();
        GENECSV genecsv = diba2geneConverter.convert(src);
        GENECSV2CARARE gene2carareConverter = new GENECSV2CARARE();
        gene2carareConverter.getParams().put("isArchitecture", this.getParams().getOrDefault("isArchitecture", "true"));
        return gene2carareConverter.convert(genecsv);
    }
}