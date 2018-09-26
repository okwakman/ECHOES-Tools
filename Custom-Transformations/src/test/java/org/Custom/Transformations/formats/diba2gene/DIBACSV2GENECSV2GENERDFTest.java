package org.Custom.Transformations.formats.diba2gene;

import org.Custom.Transformations.formats.diba.DIBACSV;
import org.Custom.Transformations.formats.diba.DIBACSV2GENECSV;
import org.Custom.Transformations.formats.gene.GENECSV;
import org.Custom.Transformations.formats.gene.GENECSV2GENERDF;
import org.csuc.deserialize.JaxbUnmarshal;
import org.csuc.serialize.JaxbMarshal;
import org.junit.Before;
import org.junit.Test;
import cat.gencat.RDF;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DIBACSV2GENECSV2GENERDFTest {
    private DIBACSV2GENECSV diba2gene_converter;
    private GENECSV2GENERDF gene2rdf_converter;
    private DIBACSV dibacsv;
    private GENECSV genecsv;

    @Before
    public void setUp() throws Exception {
        diba2gene_converter = new DIBACSV2GENECSV();
        gene2rdf_converter = new GENECSV2GENERDF();
    }

    @Test
    public void testArquitectura() throws IOException, JAXBException {
        // DIBACSV -> GENECSV
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
        dibacsv = new DIBACSV();
        try {
            dibacsv.load(Paths.get(getClass().getClassLoader().getResource("diba/Arquitectura.csv").toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        genecsv = diba2gene_converter.convert(dibacsv);
        // GENECSV -> GENERDF
        File tmp = Files.createTempFile("diba_gene_rdf", ".xml").toFile();
        RDF generdf = gene2rdf_converter.convert(genecsv);
        JaxbMarshal jaxb = new JaxbMarshal(gene2rdf_converter.convert(genecsv), RDF.class);
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
        File tmpGene = Files.createTempFile("diba", ".csv").toFile();
        dibacsv = new DIBACSV();
        try {
            dibacsv.load(Paths.get(getClass().getClassLoader().getResource("diba/Arqueologia.csv").toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        genecsv = diba2gene_converter.convert(dibacsv);
        // GENECSV -> GENERDF
        File tmp = Files.createTempFile("diba_gene_rdf", ".xml").toFile();
        JaxbMarshal jaxb = new JaxbMarshal(gene2rdf_converter.convert(genecsv), RDF.class);
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