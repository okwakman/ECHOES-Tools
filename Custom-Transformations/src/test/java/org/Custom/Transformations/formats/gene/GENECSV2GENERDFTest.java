package org.Custom.Transformations.formats.gene;
import org.csuc.deserialize.JaxbUnmarshal;
import org.csuc.deserialize.JibxUnMarshall;
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

public class GENECSV2GENERDFTest {
    GENECSV2GENERDF gene_arqui;
    GENECSV2GENERDF gene_arqui_real;
    GENECSV2GENERDF gene_arqui_real_fix;
    GENECSV2GENERDF gene_arqui_real_fix_100;
    GENECSV2GENERDF gene_arque;
    GENECSV2GENERDF gene_arque_real_fix;
    GENECSV2GENERDF gene_arque_real_fix_100;

    @Before
    public void setUp() throws Exception {
        String csvPathArqui = getClass().getClassLoader().getResource("gene/Arquitectura.csv").getPath();
        gene_arqui = new GENECSV2GENERDF("GENE", csvPathArqui, true);
        gene_arqui.generateRDF();
        String csvPathArquiReal = getClass().getClassLoader().getResource("gene/Extraccio_bens_Arquitectonic_29-06-2017.csv").getPath();
        gene_arqui_real = new GENECSV2GENERDF("GENE", csvPathArquiReal, true);
        gene_arqui_real.generateRDF();
        String csvPathArquiRealFix = getClass().getClassLoader().getResource("gene/Extraccio_bens_Arquitectonic_29-06-2017_fix.csv").getPath();
        gene_arqui_real_fix = new GENECSV2GENERDF("GENE", csvPathArquiRealFix, true);
        gene_arqui_real_fix.generateRDF();
        String csvPathArquiRealFix100 = getClass().getClassLoader().getResource("gene/Extraccio_bens_Arquitectonic_29-06-2017_fix_100items.csv").getPath();
        gene_arqui_real_fix_100 = new GENECSV2GENERDF("GENE", csvPathArquiRealFix100, true);
        gene_arqui_real_fix_100.generateRDF();
        String csvPathArque = getClass().getClassLoader().getResource("gene/Arqueologia.csv").getPath();
        gene_arque = new GENECSV2GENERDF("GENE", csvPathArque, false);
        gene_arque.generateRDF();
        String csvPathArqueFix = getClass().getClassLoader().getResource("gene/Extraccio_bens_Arqueologics_29-06-2017_fix.csv").getPath();
        gene_arque_real_fix = new GENECSV2GENERDF("GENE", csvPathArqueFix, false);
        gene_arque_real_fix.generateRDF();
        String csvPathArqueFix100 = getClass().getClassLoader().getResource("gene/Extraccio_bens_Arqueologics_29-06-2017_fix_100items.csv").getPath();
        gene_arque_real_fix_100 = new GENECSV2GENERDF("GENE", csvPathArqueFix100, false);
        gene_arque_real_fix_100.generateRDF();
    }

    @Test
    public void testGetRDF() throws IOException, JAXBException {
        File tmp = Files.createTempFile("gene_rdf", ".xml").toFile();
        JaxbMarshal jaxb = new JaxbMarshal(gene_arqui, RDF.class);
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
        File tmpArque = Files.createTempFile("gene_rdf", ".xml").toFile();
        JaxbMarshal jaxbArque = new JaxbMarshal(gene_arque, RDF.class);
        FileOutputStream fileOutputStreamArque = new FileOutputStream(tmpArque);
        jaxbArque.marshaller(fileOutputStreamArque);
        FileInputStream fisArque = new FileInputStream(tmpArque);
        JaxbUnmarshal jaxbun2 = new JaxbUnmarshal(fisArque, new Class[] {RDF.class});
        fisArque = new FileInputStream(tmpArque);
        int oneByteArque;
        while ((oneByteArque = fisArque.read()) != -1) {
            System.out.write(oneByteArque);
        }
        System.out.flush();

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
    public void testArquitecturaFix() throws IOException, JAXBException {
        File tmp = Files.createTempFile("gene_rdf", ".xml").toFile();
        JaxbMarshal jaxb = new JaxbMarshal(gene_arqui_real_fix, RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[] {GENECSV2GENERDF.class});
        fis = new FileInputStream(tmp);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

    @Test
    public void testArquitecturaFix100() throws IOException, JAXBException {
        File tmp = Files.createTempFile("gene_rdf", ".xml").toFile();
        JaxbMarshal jaxb = new JaxbMarshal(gene_arqui_real_fix_100, RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[] {GENECSV2GENERDF.class});
        fis = new FileInputStream(tmp);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }

    @Test
    public void testArqueologiaFix() throws IOException, JAXBException {
        File tmpArque = Files.createTempFile("gene_rdf", ".xml").toFile();
        JaxbMarshal jaxbArque = new JaxbMarshal(gene_arque_real_fix, RDF.class);
        FileOutputStream fileOutputStreamArque = new FileOutputStream(tmpArque);
        jaxbArque.marshaller(fileOutputStreamArque);
        FileInputStream fisArque = new FileInputStream(tmpArque);
        JaxbUnmarshal jaxbun2 = new JaxbUnmarshal(fisArque, new Class[] {RDF.class});
        fisArque = new FileInputStream(tmpArque);
        int oneByteArque;
        while ((oneByteArque = fisArque.read()) != -1) {
            System.out.write(oneByteArque);
        }
        System.out.flush();
    }

    @Test
    public void testArqueologiaFix100() throws IOException, JAXBException {
        File tmpArque = Files.createTempFile("gene_rdf", ".xml").toFile();
        JaxbMarshal jaxbArque = new JaxbMarshal(gene_arque_real_fix_100, RDF.class);
        FileOutputStream fileOutputStreamArque = new FileOutputStream(tmpArque);
        jaxbArque.marshaller(fileOutputStreamArque);
        FileInputStream fisArque = new FileInputStream(tmpArque);
        JaxbUnmarshal jaxbun2 = new JaxbUnmarshal(fisArque, new Class[] {RDF.class});
        fisArque = new FileInputStream(tmpArque);
        int oneByteArque;
        while ((oneByteArque = fisArque.read()) != -1) {
            System.out.write(oneByteArque);
        }
        System.out.flush();
    }

}