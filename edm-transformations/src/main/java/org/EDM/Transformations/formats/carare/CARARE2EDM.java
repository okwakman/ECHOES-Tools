package org.EDM.Transformations.formats.carare;

import eu.carare.carareschema.CarareWrap;
import eu.europeana.corelib.definitions.jibx.RDF;
import org.EDM.Transformations.formats.EDM;
import org.EDM.Transformations.formats.xslt.XSLTTransformations;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.io.IoBuilder;
import org.csuc.deserialize.JibxUnMarshall;
import org.csuc.util.FormatType;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.util.JAXBSource;
import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author dfernandez
 *
 */
public class CARARE2EDM implements EDM {

    private static Logger logger = LogManager.getLogger(org.EDM.Transformations.formats.carare.CARARE2EDM.class);

    private CarareWrap type;
    private String identifier;

    private Map<String, String> properties;

    /**
     *
     * @param identifier
     * @param type
     * @param properties
     */
    public CARARE2EDM(String identifier, CarareWrap type, Map<String, String> properties) {
        this.identifier = identifier;
        this.type = type;
        this.properties = properties;
    }

    @Override
    public void transformation(OutputStream out, Map<String, String> xsltProperties) throws Exception {
        InputStream xsl = getClass().getClassLoader().getResourceAsStream("carare/carare-2.0.6_to_edm.xsl");

        JAXBSource source = new JAXBSource( JAXBContext.newInstance(CarareWrap.class), type );

        new XSLTTransformations(xsl, out, xsltProperties).transformationsFromSource(source);
    }

    @Override
    public void transformation(String xslt, OutputStream out, Map<String, String> xsltProperties) throws Exception {
        JAXBSource source = new JAXBSource( JAXBContext.newInstance(CarareWrap.class), type );

        new XSLTTransformations(xslt, out, xsltProperties)
                .transformationsFromSource(source);
    }

    @Override
    public void transformation(String xslt) throws Exception {
        JAXBSource source = new JAXBSource( JAXBContext.newInstance(CarareWrap.class), type );

        new XSLTTransformations(xslt, IoBuilder.forLogger(org.EDM.Transformations.formats.carare.CARARE2EDM.class).setLevel(Level.INFO).buildOutputStream(), properties)
                .transformationsFromSource(source);
    }

    @Override
    public void creation() {
        throw new IllegalArgumentException("creation is not valid for EAD2EDM!");
    }

    @Override
    public void creation(FormatType formatType) {
        throw new IllegalArgumentException("creation is not valid for EAD2EDM!");

    }

    @Override
    public void creation(Charset encoding, boolean alone, OutputStream outs) {
        throw new IllegalArgumentException("creation is not valid for EAD2EDM!");

    }

    @Override
    public void creation(Charset encoding, boolean alone, OutputStream outs, FormatType formatType) {
        throw new IllegalArgumentException("creation is not valid for EAD2EDM!");
    }


    @Override
    public void creation(Charset encoding, boolean alone, Writer writer) {
        throw new IllegalArgumentException("creation is not valid for EAD2EDM!");
    }

    @Override
    public JibxUnMarshall validateSchema(InputStream ins, Charset enc, Class<?> classType) {
        return new JibxUnMarshall(ins, enc, classType);
    }

    @Override
    public JibxUnMarshall validateSchema(InputStream ins, String name, Charset enc, Class<?> classType) {
        return new JibxUnMarshall(ins, name, enc, classType);
    }

    @Override
    public JibxUnMarshall validateSchema(Reader rdr, Class<?> classType) {
        return new JibxUnMarshall(rdr, classType);
    }

    @Override
    public JibxUnMarshall validateSchema(Reader rdr, String name, Class<?> classType) {
        return new JibxUnMarshall(rdr, name, classType);
    }

    @Override
    public void modify(RDF rdf) {

    }

}
