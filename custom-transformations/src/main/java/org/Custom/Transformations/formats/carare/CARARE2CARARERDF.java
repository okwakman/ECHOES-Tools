package org.Custom.Transformations.formats.carare;

import eu.carare.carareschema.CarareWrap;
import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;
import org.Custom.Transformations.core.Convertible;
import org.csuc.serialize.JaxbMarshal;

import javax.xml.bind.JAXBException;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CARARE2CARARERDF extends Convertible<CarareWrap, File> {

    private TransformerFactory fact = new net.sf.saxon.TransformerFactoryImpl();
    private Transformer transformer = null;

    @Override
    public File convert(CarareWrap src) {
        String xsl = getClass().getClassLoader().getResource("carare/CARARE2RDF.xsl").getFile();
        TransformerFactoryImpl tFactoryImpl = (TransformerFactoryImpl) fact;
        Configuration saxonConfig = tFactoryImpl.getConfiguration();

        saxonConfig.registerExtensionFunction(new ExtensionFunctionUUID());
        StreamSource xlsStreamSource = new StreamSource(Paths
                .get(xsl)
                .toAbsolutePath().toFile());
        try {
            transformer = tFactoryImpl.newTransformer(xlsStreamSource);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        }

        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, StandardCharsets.UTF_8.name());
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");

        JaxbMarshal jaxb = new JaxbMarshal(src, CarareWrap.class);
        File tmp = null;
        try {
            tmp = Files.createTempFile("carare", ".xml").toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(tmp);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            jaxb.marshaller(fileOutputStream);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        try {
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        File tmp_carare = null;
        try {
            tmp_carare = Files.createTempFile("carare_rdf", ".xml").toFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            transformer.transform(new StreamSource(tmp),
                    new StreamResult(tmp_carare));
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        tmp.deleteOnExit();
        return tmp_carare;
    }
}
