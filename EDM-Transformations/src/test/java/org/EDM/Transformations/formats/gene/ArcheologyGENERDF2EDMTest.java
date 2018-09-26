package org.EDM.Transformations.formats.gene;

import eu.europeana.corelib.definitions.jibx.RDF;
import org.EDM.Transformations.formats.EDM;
import org.EDM.Transformations.formats.FactoryEDM;
import org.EDM.Transformations.formats.xslt.XSLTTransformations;
import org.csuc.deserialize.JaxbUnmarshal;
import org.csuc.deserialize.JibxUnMarshall;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

public class ArcheologyGENERDF2EDMTest {

    private static Logger logger = LogManager.getLogger(ArcheologyGENERDF2EDMTest.class);
    private EDM gene_arqueologia;
    private File tmp_arqueologia;

    @Before
    public void setUp() throws Exception {

        File xml_arqueologia = new File(getClass().getClassLoader().getResource("gene/gene_arqueologia.xml").getFile());
        tmp_arqueologia = Files.createTempFile("generdf_edm_arqueologia", ".xml").toFile();
        assertTrue(xml_arqueologia.exists());

        JaxbUnmarshal jxb = new JaxbUnmarshal(xml_arqueologia, new Class[] { cat.gencat.RDF.class });
        assertNotNull(jxb.getObject());
        assertTrue(jxb.isValidating());

        gene_arqueologia = FactoryEDM.createFactory(new GENERDF2EDM((cat.gencat.RDF) jxb.getObject(), properties()));
        assertNotNull(xml_arqueologia);

        tmp_arqueologia.deleteOnExit();
    }

    @Test
    public void transformation_arqueologia(){
        XSLTTransformations transformations = null;
        try{
            transformations = gene_arqueologia.transformation(null);
            assertNull(transformations);
        }catch(Exception e){
            logger.error(e.getMessage());
        }
    }


    @Test
    public void transformation1_arqueologia(){
        XSLTTransformations transformations = null;
        try{
            transformations = gene_arqueologia.transformation(null, null, null);
            assertNull(transformations);
        }catch(Exception e){
            logger.error(e.getMessage());
        }
    }


    @Test
    public void creation_arqueologia(){
        gene_arqueologia.creation();
    }


    @Test
    public void creation1_arqueologia() {
        StringWriter writer = new StringWriter();
        gene_arqueologia.creation(UTF_8, true, writer);
        assertTrue(!writer.toString().isEmpty());
    }


    @Test
    public void creation2_arqueologia() throws Exception {
        FileOutputStream outs = new FileOutputStream(tmp_arqueologia);
        gene_arqueologia.creation(UTF_8, true, outs);

        int b  = new FileInputStream(tmp_arqueologia).read();
        assertNotEquals(-1, b);
    }


    @Test
    public void validateSchema_arqueologia() {
        StringWriter writer = new StringWriter();
        gene_arqueologia.creation(UTF_8, true, writer);

        Reader reader = new StringReader(writer.toString());
        JibxUnMarshall jibx = gene_arqueologia.validateSchema(reader, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema1_arqueologia() throws Exception {
        StringWriter writer = new StringWriter();
        gene_arqueologia.creation(UTF_8, true, writer);

        Reader reader = new StringReader(writer.toString());
        JibxUnMarshall jibx = gene_arqueologia.validateSchema(reader, "name", RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema2_arqueologia() throws Exception {
        FileOutputStream outs = new FileOutputStream(tmp_arqueologia);
        gene_arqueologia.creation(UTF_8, true, outs);

        JibxUnMarshall jibx = gene_arqueologia.validateSchema(new FileInputStream(tmp_arqueologia), UTF_8, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema3_arqueologia() throws Exception {
        FileOutputStream outs = new FileOutputStream(tmp_arqueologia);
        gene_arqueologia.creation(UTF_8, true, outs);

        JibxUnMarshall jibx = gene_arqueologia.validateSchema(new FileInputStream(tmp_arqueologia), "name", UTF_8, RDF.class);

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