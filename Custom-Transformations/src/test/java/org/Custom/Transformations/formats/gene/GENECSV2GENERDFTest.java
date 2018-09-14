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

public class GENECSV2GENERDFTest {
    GENECSV2GENERDF gene_arqui_real;
    GENECSV2GENERDF gene_arque_real;

    @Before
    public void setUp() throws Exception {
        String csvPathArquiReal = getClass().getClassLoader().getResource("gene/Extraccio_bens_Arquitectonic_29-06-2017.csv").getPath();
        gene_arqui_real = new GENECSV2GENERDF("GENE", csvPathArquiReal, true);
        gene_arqui_real.generateRDF();
        String csvPathArqueReal = getClass().getClassLoader().getResource("gene/Extraccio_bens_Arqueologics_29-06-2017.csv").getPath();
        gene_arque_real = new GENECSV2GENERDF("GENE", csvPathArqueReal, false);
        gene_arque_real.generateRDF();
    }

    @Test
    public void testArquitectura() throws IOException, JAXBException {
        File tmp = Files.createTempFile("gene_rdf", ".xml").toFile();
        JaxbMarshal jaxb = new JaxbMarshal(gene_arqui_real, RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[] {RDF.class});
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
        File tmp = Files.createTempFile("gene_rdf", ".xml").toFile();
        JaxbMarshal jaxb = new JaxbMarshal(gene_arque_real, RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[] {RDF.class});
        fis = new FileInputStream(tmp);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }


}