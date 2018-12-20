package org.Custom.Transformations.formats.diba2gene;

import eu.carare.carareschema.CarareWrap;
import org.Custom.Transformations.formats.diba.DIBACSV;
import org.Custom.Transformations.formats.diba.DIBACSV2CARARE;
import org.csuc.deserialize.JaxbUnmarshal;
import org.csuc.serialize.JaxbMarshal;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class DIBACSV2CARARETest {
    private DIBACSV2CARARE diba2carare_converter;
    private DIBACSV dibacsv;

    @Before
    public void setUp() {
        diba2carare_converter = new DIBACSV2CARARE();
    }

    @Test
    public void testArquitectura() throws IOException, JAXBException {
        File tmp = Files.createTempFile("diba_arquitectura", ".xml").toFile();
        dibacsv = new DIBACSV();
        try {
            dibacsv.load(Paths.get(getClass().getClassLoader().getResource("diba/Arquitectura.csv").toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        diba2carare_converter.getParams().put("isArchitecture", "true");
        CarareWrap carare = diba2carare_converter.convert(dibacsv);
        JaxbMarshal jaxb = new JaxbMarshal(carare, CarareWrap.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[]{CarareWrap.class});
        //tmp.deleteOnExit();
    }

    @Test
    public void testArqueologia() throws IOException, JAXBException {
        File tmp = Files.createTempFile("diba_arqueologia", ".xml").toFile();
        dibacsv = new DIBACSV();
        try {
            dibacsv.load(Paths.get(getClass().getClassLoader().getResource("diba/Arqueologia.csv").toURI()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        diba2carare_converter.getParams().put("isArchitecture", "false");
        CarareWrap carare = diba2carare_converter.convert(dibacsv);
        JaxbMarshal jaxb = new JaxbMarshal(carare, CarareWrap.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[]{CarareWrap.class});
        //tmp.deleteOnExit();
    }
}