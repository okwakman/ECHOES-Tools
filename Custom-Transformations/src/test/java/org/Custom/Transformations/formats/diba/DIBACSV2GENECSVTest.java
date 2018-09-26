package org.Custom.Transformations.formats.diba;
import org.Custom.Transformations.formats.gene.GENECSV;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DIBACSV2GENECSVTest {
    private DIBACSV2GENECSV converter;
    private DIBACSV dibacsvArqui;
    private DIBACSV dibacsvArque;

    @Before
    public void setUp() throws URISyntaxException {
        dibacsvArqui = new DIBACSV();
        dibacsvArqui.load(Paths.get(getClass().getClassLoader().getResource("diba/Arquitectura.csv").toURI()));
        dibacsvArque = new DIBACSV();
        dibacsvArque.load(Paths.get(getClass().getClassLoader().getResource("diba/Arqueologia.csv").toURI()));
        converter = new DIBACSV2GENECSV();
    }

    @Test
    public void testArqueologia() throws IOException {
        File tmpGene = Files.createTempFile("diba_arqueologia", ".csv").toFile();
        GENECSV genecsv = converter.convert(dibacsvArque);
        genecsv.save(tmpGene.toPath());
        tmpGene.deleteOnExit();
    }

    @Test
    public void testArquitectura() throws IOException {
        File tmpGene = Files.createTempFile("diba_arquitectura", ".csv").toFile();
        GENECSV genecsv = converter.convert(dibacsvArqui);
        genecsv.save(tmpGene.toPath());
        tmpGene.deleteOnExit();
    }

}