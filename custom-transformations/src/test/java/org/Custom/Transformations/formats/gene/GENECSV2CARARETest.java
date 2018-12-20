package org.Custom.Transformations.formats.gene;
import eu.carare.carareschema.CarareWrap;
import org.csuc.deserialize.JaxbUnmarshal;
import org.csuc.serialize.JaxbMarshal;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GENECSV2CARARETest {
    GENECSV2CARARE gene_arqui_converter;
    GENECSV2CARARE gene_arque_converter;
    GENECSV genecsv_arqui;
    GENECSV genecsv_arque;

    @Before
    public void setUp() throws Exception {
        genecsv_arqui = new DedupGENECSV();
        genecsv_arqui.load(Paths.get(getClass().getClassLoader().getResource("gene/Arquitectura.csv").toURI()));
        genecsv_arque = new DedupGENECSV();
        genecsv_arque.load(Paths.get(getClass().getClassLoader().getResource("gene/Arqueologia.csv").toURI()));
    }

    @Test
    public void testArquitectura() throws IOException, JAXBException {
        File tmp = Files.createTempFile("gene_carare_arquitectura", ".xml").toFile();
        gene_arqui_converter = new GENECSV2CARARE();
        gene_arqui_converter.getParams().put("isArchitecture", "true");
        CarareWrap gene_arqui_rdf = gene_arqui_converter.convert(genecsv_arqui);
        JaxbMarshal jaxb = new JaxbMarshal(gene_arqui_rdf, CarareWrap.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[] {CarareWrap.class});
        tmp.deleteOnExit();
    }

    @Test
    public void testArqueologia() throws IOException, JAXBException {
        File tmp = Files.createTempFile("gene_carare_arqueologia", ".xml").toFile();
        gene_arque_converter = new GENECSV2CARARE();
        gene_arque_converter.getParams().put("isArchitecture", "false");
        JaxbMarshal jaxb = new JaxbMarshal(gene_arque_converter.convert(genecsv_arque), CarareWrap.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[] {CarareWrap.class});
        tmp.deleteOnExit();
    }


}