package org.Custom.Transformations.formats.diba;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

public class DIBACSV2GENECSVTest {
    private DIBACSV2GENECSV converter;
    private String csvPathArqui;
    private String csvPathArque;

    @Before
    public void setUp() {
        csvPathArqui = getClass().getClassLoader().getResource("diba/Arquitectura.csv").getPath();
        csvPathArque = getClass().getClassLoader().getResource("diba/Arqueologia.csv").getPath();
        converter = new DIBACSV2GENECSV();
    }

    @Test
    public void testArqueologia() throws IOException {
        File tmpGene = Files.createTempFile("diba_arqueologia", ".csv").toFile();
        converter.convert(csvPathArque, tmpGene.getAbsolutePath());
        FileInputStream fis = new FileInputStream(tmpGene);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

    @Test
    public void testArquitectura() throws IOException {
        File tmpGene = Files.createTempFile("diba_arquitectura", ".csv").toFile();
        converter.convert(csvPathArqui, tmpGene.getAbsolutePath());
        FileInputStream fis = new FileInputStream(tmpGene);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

}