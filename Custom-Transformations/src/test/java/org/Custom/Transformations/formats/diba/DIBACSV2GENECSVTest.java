package org.Custom.Transformations.formats.diba;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;

public class DIBACSV2GENECSVTest {
    private DIBACSV2GENECSV converter;
    private String csvPathArqui;
    private String csvPathArqui100;
    private String csvPathTot;
    private String csvPathArque;
    private String csvPathArque100;

    @Before
    public void setUp() {
        csvPathArqui = getClass().getClassLoader().getResource("diba/Arquitectura.csv").getPath();
        csvPathArqui100 = getClass().getClassLoader().getResource("diba/Arquitectura_100.csv").getPath();
        csvPathArque = getClass().getClassLoader().getResource("diba/Arqueologia.csv").getPath();
        csvPathArque100 = getClass().getClassLoader().getResource("diba/Arqueologia_100.csv").getPath();
        csvPathTot = getClass().getClassLoader().getResource("diba/DIBA.csv").getPath();
        converter = new DIBACSV2GENECSV();
    }

    @Test
    public void testArqueologia() throws IOException, JAXBException {
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
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
    public void testArqueologia100() throws IOException, JAXBException {
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
        converter.convert(csvPathArque100, tmpGene.getAbsolutePath());
        FileInputStream fis = new FileInputStream(tmpGene);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

    @Test
    public void testArquitectura() throws IOException, JAXBException {
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
        converter.convert(csvPathArqui, tmpGene.getAbsolutePath());
        FileInputStream fis = new FileInputStream(tmpGene);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

    @Test
    public void testArquitectura100() throws IOException, JAXBException {
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
        converter.convert(csvPathArqui100, tmpGene.getAbsolutePath());
        FileInputStream fis = new FileInputStream(tmpGene);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

    @Test
    public void testTot() throws IOException {
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
        converter.convert(csvPathTot, tmpGene.getAbsolutePath());
        FileInputStream fis = new FileInputStream(tmpGene);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

}