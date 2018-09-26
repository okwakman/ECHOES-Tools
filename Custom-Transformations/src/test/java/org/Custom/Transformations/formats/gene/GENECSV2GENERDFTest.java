package org.Custom.Transformations.formats.gene;
import cat.gencat.RDF;
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

public class GENECSV2GENERDFTest {
    GENECSV2GENERDF gene_arqui_converter;
    GENECSV2GENERDF gene_arque_converter;
    GENECSV genecsv_arqui;
    GENECSV genecsv_arque;

    @Before
    public void setUp() throws Exception {
        genecsv_arqui = new GENECSV();
        genecsv_arqui.load(Paths.get(getClass().getClassLoader().getResource("gene/Extraccio_bens_Arquitectonic_29-06-2017.csv").toURI()));
        genecsv_arque = new GENECSV();
        genecsv_arque.load(Paths.get(getClass().getClassLoader().getResource("gene/Extraccio_bens_Arqueologics_29-06-2017.csv").toURI()));
    }

    @Test
    public void testArquitectura() throws IOException, JAXBException {
        File tmp = Files.createTempFile("gene_rdf", ".xml").toFile();
        gene_arqui_converter = new GENECSV2GENERDF();
        gene_arqui_converter.getParams().put("isArchitecture", "true");
        RDF gene_arqui_rdf = gene_arqui_converter.convert(genecsv_arqui);
        JaxbMarshal jaxb = new JaxbMarshal(gene_arqui_rdf, RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[] {RDF.class});
        tmp.deleteOnExit();
    }

    @Test
    public void testArqueologia() throws IOException, JAXBException {
        File tmp = Files.createTempFile("gene_rdf", ".xml").toFile();
        gene_arque_converter = new GENECSV2GENERDF();
        gene_arque_converter.getParams().put("isArchitecture", "false");
        JaxbMarshal jaxb = new JaxbMarshal(gene_arque_converter.convert(genecsv_arque), RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[] {RDF.class});
        tmp.deleteOnExit();
    }


}