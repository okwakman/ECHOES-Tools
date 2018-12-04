package org.EDM.Transformations.formats.carare;

import eu.carare.carareschema.CarareWrap;
import eu.europeana.corelib.definitions.jibx.RDF;
import org.EDM.Transformations.formats.EDM;
import org.EDM.Transformations.formats.FactoryEDM;
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
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

public class CARARE2EDMTest {

    private static Logger logger = LogManager.getLogger(CARARE2EDMTest.class);

    private File xml;
    private File xslt;
    private File tmp;

    private EDM carare;

    @Before
    public void setUp() throws Exception {
        xml = new File(getClass().getClassLoader().getResource("carare/CARARE_JAEN_2018_05_24.xml").getFile());
        xslt = new File(getClass().getClassLoader().getResource("carare/carare-2.0.6_to_edm.xsl").getFile());
        tmp = Files.createTempFile("carare_edm", ".xml").toFile();

        assertTrue(xml.exists());
        assertTrue(xslt.exists());

        JaxbUnmarshal jxb = new JaxbUnmarshal(xml, new Class[]{CarareWrap.class});
        assertNotNull(jxb.getObject());
        assertTrue(jxb.isValidating());

        carare = FactoryEDM.createFactory(new CARARE2EDM(UUID.randomUUID().toString(), (CarareWrap) jxb.getObject(), properties()));
        assertNotNull(carare);

        //tmp.deleteOnExit();
    }

    @Test
    public void transformation() throws Exception {
        carare.transformation(xslt.getPath());
    }

    @Test
    public void transformation1() throws Exception {
        carare.transformation(
                xslt.getPath(),
                new FileOutputStream(tmp), properties());
    }

    @Test
    public void validateSchema() throws Exception {
        carare.transformation(xslt.getPath(), new FileOutputStream(tmp), properties());

        JibxUnMarshall jibx = carare.validateSchema(new FileReader(tmp), RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }

    @Test
    public void validateSchema1() throws Exception {
        carare.transformation(xslt.getPath(), new FileOutputStream(tmp), properties());

        JibxUnMarshall jibx = carare.validateSchema(new FileReader(tmp), "name", RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }

    @Test
    public void validateSchema2() throws Exception {
        carare.transformation(xslt.getPath(), new FileOutputStream(tmp), properties());

        JibxUnMarshall jibx = carare.validateSchema(new FileInputStream(tmp), UTF_8, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }

    @Test
    public void validateSchema3() throws Exception {
        carare.transformation(xslt.getPath(), new FileOutputStream(tmp), properties());

                JibxUnMarshall jibx = carare.validateSchema(new FileInputStream(tmp), "name", UTF_8, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }

    private Map<String, String> properties() {
        Map<String, String> properties = new HashMap<>();
        properties.put("rights", "1");
        properties.put("project_acronym", "ECHOES");
        return properties;
    }
}