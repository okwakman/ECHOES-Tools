package org.Custom.Transformations.formats.diba2gene;

import org.Custom.Transformations.formats.diba.DIBACSV2GENECSV;
import org.Custom.Transformations.formats.gene.GENECSV2GENERDF;
import org.csuc.deserialize.JaxbUnmarshal;
import org.csuc.serialize.JaxbMarshal;
import org.junit.Before;
import org.junit.Test;
import org.w3._1999._02._22_rdf_syntax_ns_.RDF;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

public class DIBACSV2GENECSV2GENERDFTest {
    GENECSV2GENERDF csvConverter;
    DIBACSV2GENECSV rdfConverter;

    @Before
    public void setUp() throws Exception {
        rdfConverter = new DIBACSV2GENECSV();
    }

    @Test
    public void testArquitectura() throws IOException, JAXBException {
        // DIBACSV -> GENECSV
        String csvPathArqui = getClass().getClassLoader().getResource("diba/Arquitectura.csv").getPath();
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
        rdfConverter.convert(csvPathArqui, tmpGene.getAbsolutePath());
        // GENECSV -> GENERDF
        csvConverter = new GENECSV2GENERDF("DIBA", tmpGene.getAbsolutePath(), true);
        csvConverter.generateRDF();
        File tmp = Files.createTempFile("diba_gene_rdf", ".xml").toFile();
        JaxbMarshal jaxb = new JaxbMarshal(csvConverter, RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[]{RDF.class});
        fis = new FileInputStream(tmp);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

    @Test
    public void testArqueologia() throws IOException, JAXBException {
        // DIBACSV -> GENECSV
        String csvPathArqui = getClass().getClassLoader().getResource("diba/Arqueologia.csv").getPath();
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
        rdfConverter.convert(csvPathArqui, tmpGene.getAbsolutePath());
        // GENECSV -> GENERDF
        csvConverter = new GENECSV2GENERDF("DIBA", tmpGene.getAbsolutePath(), false);
        csvConverter.generateRDF();
        File tmp = Files.createTempFile("diba_gene_rdf", ".xml").toFile();
        JaxbMarshal jaxb = new JaxbMarshal(csvConverter, RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[]{RDF.class});
        fis = new FileInputStream(tmp);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

    @Test
    public void testArqueologiaTot() throws IOException, JAXBException {
        // DIBACSV -> GENECSV
        String csvPathArqui = getClass().getClassLoader().getResource("diba/Arqueologia_tot.csv").getPath();
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
        rdfConverter.convert(csvPathArqui, tmpGene.getAbsolutePath());
        // GENECSV -> GENERDF
        csvConverter = new GENECSV2GENERDF("DIBA", tmpGene.getAbsolutePath(), false);
        csvConverter.generateRDF();
        File tmp = Files.createTempFile("diba_gene_rdf", ".xml").toFile();
        JaxbMarshal jaxb = new JaxbMarshal(csvConverter, RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[]{RDF.class});
        fis = new FileInputStream(tmp);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

    @Test
    public void testArqueologia100() throws IOException, JAXBException {
        // DIBACSV -> GENECSV
        String csvPathArqui = getClass().getClassLoader().getResource("diba/Arqueologia_100.csv").getPath();
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
        rdfConverter.convert(csvPathArqui, tmpGene.getAbsolutePath());
        // GENECSV -> GENERDF
        csvConverter = new GENECSV2GENERDF("DIBA", tmpGene.getAbsolutePath(), false);
        csvConverter.generateRDF();
        File tmp = Files.createTempFile("diba_gene_rdf", ".xml").toFile();
        JaxbMarshal jaxb = new JaxbMarshal(csvConverter, RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[]{RDF.class});
        fis = new FileInputStream(tmp);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

    @Test
    public void testArquitectura100() throws IOException, JAXBException {
        // DIBACSV -> GENECSV
        String csvPathArqui = getClass().getClassLoader().getResource("diba/Arquitectura_100.csv").getPath();
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
        rdfConverter.convert(csvPathArqui, tmpGene.getAbsolutePath());
        // GENECSV -> GENERDF
        csvConverter = new GENECSV2GENERDF("DIBA", tmpGene.getAbsolutePath(), true);
        csvConverter.generateRDF();
        File tmp = Files.createTempFile("diba_gene_rdf", ".xml").toFile();
        JaxbMarshal jaxb = new JaxbMarshal(csvConverter, RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[]{RDF.class});
        fis = new FileInputStream(tmp);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

    @Test
    public void testTot() throws IOException, JAXBException {
        // DIBACSV -> GENECSV
        String csvPathArqui = getClass().getClassLoader().getResource("diba/DIBA.csv").getPath();
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
        rdfConverter.convert(csvPathArqui, tmpGene.getAbsolutePath());
        // GENECSV -> GENERDF
        csvConverter = new GENECSV2GENERDF("DIBA", tmpGene.getAbsolutePath(), true);
        csvConverter.generateRDF();
        File tmp = Files.createTempFile("diba_gene_rdf", ".xml").toFile();
        JaxbMarshal jaxb = new JaxbMarshal(csvConverter, RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[]{RDF.class});
        fis = new FileInputStream(tmp);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }
}