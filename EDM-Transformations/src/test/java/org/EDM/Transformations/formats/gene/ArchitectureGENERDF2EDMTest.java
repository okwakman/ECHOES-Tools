package org.EDM.Transformations.formats.gene;

import eu.europeana.corelib.definitions.jibx.RDF;
import org.EDM.Transformations.formats.EDM;
import org.EDM.Transformations.formats.FactoryEDM;
import org.EDM.Transformations.formats.xslt.XSLTTransformations;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.csuc.deserialize.JaxbUnmarshal;
import org.csuc.deserialize.JibxUnMarshall;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

public class ArchitectureGENERDF2EDMTest {

    private static Logger logger = LogManager.getLogger(ArchitectureGENERDF2EDMTest.class);
    private EDM gene_arquitectura;
    private File tmp_arquitectura;

    @Before
    public void setUp() throws Exception {
        File xml_arquitectura = new File(getClass().getClassLoader().getResource("gene/gene_arquitectura.xml").getFile());
        tmp_arquitectura = Files.createTempFile("generdf_edm_arquitectura", ".xml").toFile();
        assertTrue(xml_arquitectura.exists());

        JaxbUnmarshal jxb = new JaxbUnmarshal(xml_arquitectura, new Class[] { cat.gencat.RDF.class });
        assertNotNull(jxb.getObject());
        assertTrue(jxb.isValidating());

        gene_arquitectura = FactoryEDM.createFactory(new GENERDF2EDM((cat.gencat.RDF) jxb.getObject(), properties()));
        assertNotNull(xml_arquitectura);

        tmp_arquitectura.deleteOnExit();
    }

    @Test
    public void transformation_arquitectura() {
        XSLTTransformations transformations = null;
        try{
            transformations = gene_arquitectura.transformation(null);
            assertNull(transformations);
        }catch(Exception e){
            logger.error(e.getMessage());
        }
    }


    @Test
    public void transformation1_arquitectura() {
        XSLTTransformations transformations = null;
        try{
            transformations = gene_arquitectura.transformation(null, null, null);
            assertNull(transformations);
        }catch(Exception e){
            logger.error(e.getMessage());
        }
    }


    @Test
    public void creation_arquitectura() {
        gene_arquitectura.creation();
    }


    @Test
    public void creation1_arquitectura() {
        StringWriter writer = new StringWriter();
        gene_arquitectura.creation(UTF_8, true, writer);
        assertTrue(!writer.toString().isEmpty());
    }


    @Test
    public void creation2_arquitectura() throws Exception {
        FileOutputStream outs = new FileOutputStream(tmp_arquitectura);
        gene_arquitectura.creation(UTF_8, true, outs);

        int b  = new FileInputStream(tmp_arquitectura).read();
        assertNotEquals(-1, b);
    }


    @Test
    public void validateSchema_arquitectura() {
        StringWriter writer = new StringWriter();
        gene_arquitectura.creation(UTF_8, true, writer);

        Reader reader = new StringReader(writer.toString());
        JibxUnMarshall jibx = gene_arquitectura.validateSchema(reader, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema1_arquitectura() {
        StringWriter writer = new StringWriter();
        gene_arquitectura.creation(UTF_8, true, writer);

        Reader reader = new StringReader(writer.toString());
        JibxUnMarshall jibx = gene_arquitectura.validateSchema(reader, "name", RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema2_arquitectura() throws Exception {
        FileOutputStream outs = new FileOutputStream(tmp_arquitectura);
        gene_arquitectura.creation(UTF_8, true, outs);

        JibxUnMarshall jibx = gene_arquitectura.validateSchema(new FileInputStream(tmp_arquitectura), UTF_8, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema3_arquitectura() throws Exception {
        FileOutputStream outs = new FileOutputStream(tmp_arquitectura);
        gene_arquitectura.creation(UTF_8, true, outs);

        JibxUnMarshall jibx = gene_arquitectura.validateSchema(new FileInputStream(tmp_arquitectura), "name", UTF_8, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }

    private Map<String, String> properties() {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put("edmType", "TEXT");
        properties.put("provider", "GENE");
        properties.put("dataProvider", "GENE");
        properties.put("language", "ca_ES");
        properties.put("rights", "Generalitat de Catalunya");

        return properties;
    }
}