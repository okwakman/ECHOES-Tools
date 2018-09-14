package org.EDM.Transformations.formats.gene;

import cat.gencat.*;
import eu.europeana.corelib.definitions.jibx.RDF;
import org.EDM.Transformations.formats.EDM;
import org.EDM.Transformations.formats.FactoryEDM;
import org.EDM.Transformations.formats.xslt.XSLTTransformations;
import org.csuc.deserialize.JaxbUnmarshal;
import org.csuc.deserialize.JibxUnMarshall;
import org.csuc.serialize.JaxbMarshal;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

public class GENERDF2EDMTest {

    private static Logger logger = LogManager.getLogger(GENERDF2EDMTest.class);
    private File tmp;
    private EDM gene_arqueologia;
    private EDM gene_arquitectura;
    private EDM gene_merge;
    private File tmp_arquitectura;
    private File tmp_arqueologia;
    private File tmp_merge;

    @Test
    public void generateSource() throws IOException, JAXBException, DatatypeConfigurationException {
        cat.gencat.RDF rdf = new cat.gencat.RDF();

        Identificacio id = new Identificacio();
        id.setAbout("ID:About:1");
        id.setCodiIntern(this.stringToLiteralType("Codi Intern 1"));
        id.setNom(this.stringToLiteralType("Nom 1"));
        id.setCodiInventari(this.stringToLiteralType("Codi Inventari 1"));
        id.setTipusPatrimoni(PatrimoniTipus.ARQUITECTÒNIC);
        rdf.getIdentificacioType().add(id);

        Property propertyType = new Property();
        propertyType.setAbout("ID:GENE:About:1");
        propertyType.setNom(this.stringToLiteralType("Nom Gene 1"));
        propertyType.setAltresNoms(this.stringToLiteralType("Altres Noms Gene 1"));
        propertyType.setTipusPatrimoni(PatrimoniTipus.ARQUITECTÒNIC);
        propertyType.setCodiIntern(this.stringToLiteralType("Codi Intern Gene 1"));
        propertyType.setProveidor(this.stringToResourceType("https://www.gencat.cat"));
        propertyType.setSubClassOf(this.aboutTypeToResourceType(id));
        rdf.getPropertyType().add(propertyType);

        Identificacio id2 = new Identificacio();
        id2.setAbout("ID:About:2");
        id2.setCodiIntern(this.stringToLiteralType("Codi Intern 2"));
        id2.setCodiInventari(this.stringToLiteralType("Codi Inventari 2"));
        id2.setNom(this.stringToLiteralType("Nom 2"));
        id2.setAltresNoms(this.stringToLiteralType("Altres Noms 2"));
        id2.setTipusPatrimoni(PatrimoniTipus.ARQUITECTÒNIC);
        id2.setPartOf(this.aboutTypeToResourceType(id));
        rdf.getIdentificacioType().add(id2);

        Localitzacio loc = new Localitzacio();
        loc.setAbout("LOC:About:1");
        loc.setAdreca(this.stringToLiteralType("Adreça 1"));
        loc.setAgregat(this.stringToLiteralType("Agregat 1"));
        loc.setLocDescripcio(this.stringToLiteralType("Descripció 1"));
        loc.setServeiTerritorial(ServeiTerritorialType.BARCELONA);
        loc.setX((float)1.00);
        loc.setY((float)2.00);
        Territori territori = new Territori();
        territori.setAbout("Territori 1");
        territori.setMunicipi(MunicipiType.BARCELONA);
        territori.getComarca().add(ComarcaType.BARCELONÈS);
        territori.getComarca().add(ComarcaType.ALT_EMPORDÀ);
        rdf.getTerritoriType().add(territori);
        loc.getTerritori().add(this.aboutTypeToResourceType(territori));
        //loc.setIdentificador(this.aboutTypeToResourceType(id));
        loc.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getLocalitzacioType().add(loc);

        Conservacio conservacio = new Conservacio();
        conservacio.setAbout("CON:About:1");
        conservacio.setIdentificador(this.propertyTypeToResourceType(propertyType));
        //conservacio.setIdentificador(this.aboutTypeToResourceType(id));
        conservacio.setConservacioEstatArquitectonic(ConservacioEstatArquitectonicType.RUÏNA);
        rdf.getConservacioType().add(conservacio);

        Datacio datacio = new Datacio();
        datacio.setAbout("DAT:About:1");
        /*datacio.getAnyInici().add(500);
        datacio.getAnyFi().add(1000);*/
        datacio.getCronologiaInicial().add(CronologiaArquitectonicType.FERRO_IBÈRIC);
        datacio.getCronologiaFinal().add(EstilEpocaType.HISTORICISTA);
        //datacio.setIdentificador(this.aboutTypeToResourceType(id));
        datacio.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getDatacioType().add(datacio);

        Autor autor = new Autor();
        autor.setAbout("AUT:About:1");
        autor.setAnyInici(1500);
        autor.setAnyFi(2000);
        autor.setNoms(this.stringToLiteralType("Nom 1"));
        autor.getCognoms().add(this.stringToLiteralType("Cognom 1"));
        autor.setProfessio(this.stringToLiteralType("Professió 1"));
        //autor.setIdentificador(this.aboutTypeToResourceType(id));
        autor.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getAutorType().add(autor);

        Descripcio desc = new Descripcio();
        desc.setAbout("DSC:About:1");
        desc.setDescDescripcio(this.stringToLiteralType("Descripció 1"));
        desc.setIdentificador(this.aboutTypeToResourceType(id));
        //desc.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getDescripcioType().add(desc);

        Estil estil = new Estil();
        estil.setAbout("EST:About:1");
        estil.setTipusEstilText(this.stringToLiteralType("Tipus Estil 1"));
        //estil.setIdentificador(this.aboutTypeToResourceType(id));
        estil.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getEstilType().add(estil);

        InformacioFitxa informacioFitxa = new InformacioFitxa();
        informacioFitxa.setAbout("IFT:About:1");
        GregorianCalendar gcal = new GregorianCalendar();
        XMLGregorianCalendar dataCreacioFitxaXMLGregorian = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
        dataCreacioFitxaXMLGregorian.setDay(11);
        dataCreacioFitxaXMLGregorian.setMonth(8);
        dataCreacioFitxaXMLGregorian.setYear(1990);
        informacioFitxa.setDataCreacioFitxa(dataCreacioFitxaXMLGregorian);
        GregorianCalendar gcal2 = new GregorianCalendar();
        XMLGregorianCalendar dataModificacioFitxaXMLGregorian = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal2);
        dataModificacioFitxaXMLGregorian.setDay(12);
        dataModificacioFitxaXMLGregorian.setMonth(9);
        dataModificacioFitxaXMLGregorian.setYear(1995);
        informacioFitxa.setDataModificacioFitxa(dataModificacioFitxaXMLGregorian);
        informacioFitxa.setAutorFitxa(this.stringToLiteralType("Autor 1"));
        //informacioFitxa.setIdentificador(this.aboutTypeToResourceType(id));
        informacioFitxa.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getInformacioFitxaType().add(informacioFitxa);

        NoticiaHistorica noticia = new NoticiaHistorica();
        noticia.setAbout("NOT:About:1");
        noticia.setComentariNoticiaHistorica(this.stringToLiteralType("Comentari 1"));
        noticia.setNomNoticiaHistorica(this.stringToLiteralType("Nom Noticia 1"));
        GregorianCalendar gcal3 = new GregorianCalendar();
        XMLGregorianCalendar dataNoticiaHistorica = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal3);
        dataNoticiaHistorica.setDay(11);
        dataNoticiaHistorica.setMonth(8);
        dataNoticiaHistorica.setYear(2001);
        noticia.setDataNoticiaHistorica(dataNoticiaHistorica);
        noticia.setTipusNoticiaHistorica(this.stringToLiteralType("Tipus Noticia 1"));
        //noticia.setIdentificador(this.aboutTypeToResourceType(id));
        noticia.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getNoticiaHistoricaType().add(noticia);

        Propietari propietariType = new Propietari();
        propietariType.setAbout("PRO:About:1");
        propietariType.setTipusRegimArquitectonic(PropietariArquitectonicType.COOPERATIVA);
        //propietariType.setIdentificador(this.propertyTypeToResourceType(propertyType));
        propietariType.setIdentificador(this.aboutTypeToResourceType(id));
        rdf.getPropietariType().add(propietariType);

        Proteccio proteccioType = new Proteccio();
        proteccioType.setAbout("PROT:About:1");
        proteccioType.setAmbit(AmbitProteccioType.FÍSICA);
        proteccioType.setBCIN(this.stringToLiteralType("BCIN"));
        proteccioType.setBIC(this.stringToLiteralType("BIC"));
        proteccioType.setCategoria(CategoriaType.ALTRES);
        proteccioType.setEntornProteccio(true);
        proteccioType.setPCC(this.stringToLiteralType("PCC"));
        proteccioType.setProteccio(this.stringToLiteralType("Proteccio"));
        //proteccioType.setIdentificador(this.aboutTypeToResourceType(id));
        proteccioType.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getProteccioType().add(proteccioType);

        Us usType = new Us();
        usType.setAbout("US:About:1");
        usType.setTipusUtilitzacio(UtilitzacioType.ACADEMICISME);
        //usType.setIdentificador(this.aboutTypeToResourceType(id));
        usType.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getUsType().add(usType);

        JaxbMarshal marshall = new JaxbMarshal(rdf, cat.gencat.RDF.class);
        tmp = Files.createTempFile("source_generdf", ".xml").toFile();
        marshall.marshaller(new FileOutputStream(tmp));

        assertTrue(tmp.exists());

        JaxbUnmarshal jxb = new JaxbUnmarshal(tmp, new Class[] { cat.gencat.RDF.class });
        assertNotNull(jxb.getObject());
        assertTrue(jxb.isValidating());

        gene_arquitectura = FactoryEDM.createFactory(new GENERDF2EDM((cat.gencat.RDF) jxb.getObject(), properties()));
        assertNotNull(gene_arquitectura);
    }

    private LiteralType stringToLiteralType(String text){
        LiteralType literalType = new LiteralType();
        literalType.setValue(text);
        return literalType;
    }

    /*private ResourceType aboutTypeToResourceType(IdentificadorType aboutType){
        ResourceType resourceType = new ResourceType();
        resourceType.setResource(aboutType.getAbout());
        return resourceType;
    }*/

    private ResourceType stringToResourceType(String text){
        ResourceType resourceType = new ResourceType();
        resourceType.setResource(text);
        return resourceType;
    }

    private ResourceType propertyTypeToResourceType(Property propertyType){
        ResourceType resourceType = new ResourceType();
        resourceType.setResource(propertyType.getAbout());
        return resourceType;
    }

    private ResourceType aboutTypeToResourceType(AboutType aboutType){
        ResourceType resourceType = new ResourceType();
        resourceType.setResource(aboutType.getAbout());
        return resourceType;
    }

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

        File xml_arqueologia = new File(getClass().getClassLoader().getResource("gene/gene_arqueologia.xml").getFile());
        tmp_arqueologia = Files.createTempFile("generdf_edm_arqueologia", ".xml").toFile();
        assertTrue(xml_arqueologia.exists());

        jxb = new JaxbUnmarshal(xml_arqueologia, new Class[] { cat.gencat.RDF.class });
        assertNotNull(jxb.getObject());
        assertTrue(jxb.isValidating());

        gene_arqueologia = FactoryEDM.createFactory(new GENERDF2EDM((cat.gencat.RDF) jxb.getObject(), properties()));
        assertNotNull(xml_arqueologia);

        File xml_merge = new File(getClass().getClassLoader().getResource("gene/gene_diba_merge_rdf.xml").getFile());
        tmp_merge = Files.createTempFile("generdf_merge_edm", ".xml").toFile();
        assertTrue(xml_merge.exists());

        jxb = new JaxbUnmarshal(xml_merge, new Class[] { cat.gencat.RDF.class });
        assertNotNull(jxb.getObject());
        assertTrue(jxb.isValidating());

        gene_merge = FactoryEDM.createFactory(new GENERDF2EDM((cat.gencat.RDF) jxb.getObject(), properties()));
        assertNotNull(xml_merge);
        //tmp.deleteOnExit();
    }

    @Test
    public void transformation_merge() throws Exception {
        XSLTTransformations transformations = null;
        try{
            transformations = gene_merge.transformation(null);
            assertNull(transformations);
        }catch(Exception e){}
    }


    @Test
    public void transformation1_merge() throws Exception {
        XSLTTransformations transformations = null;
        try{
            transformations = gene_merge.transformation(null, null, null);
            assertNull(transformations);
        }catch(Exception e){}
    }


    @Test
    public void creation_merge() throws Exception {
        gene_merge.creation();
    }


    @Test
    public void creation1_merge() throws Exception {
        StringWriter writer = new StringWriter();
        gene_merge.creation(UTF_8, true, writer);
        assertTrue(!writer.toString().isEmpty());
    }


    @Test
    public void creation2_merge() throws Exception {
        FileOutputStream outs = new FileOutputStream(tmp_merge);
        gene_merge.creation(UTF_8, true, outs);

        int b  = new FileInputStream(tmp_merge).read();
        assertNotEquals(-1, b);
    }


    @Test
    public void validateSchema_merge() throws Exception {
        StringWriter writer = new StringWriter();
        gene_merge.creation(UTF_8, true, writer);

        Reader reader = new StringReader(writer.toString());
        JibxUnMarshall jibx = gene_merge.validateSchema(reader, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema1_merge() throws Exception {
        StringWriter writer = new StringWriter();
        gene_merge.creation(UTF_8, true, writer);

        Reader reader = new StringReader(writer.toString());
        JibxUnMarshall jibx = gene_merge.validateSchema(reader, "name", RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema2_merge() throws Exception {
        FileOutputStream outs = new FileOutputStream(tmp_merge);
        gene_merge.creation(UTF_8, true, outs);

        JibxUnMarshall jibx = gene_merge.validateSchema(new FileInputStream(tmp), UTF_8, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema3_merge() throws Exception {
        FileOutputStream outs = new FileOutputStream(tmp_merge);
        gene_merge.creation(UTF_8, true, outs);

        JibxUnMarshall jibx = gene_merge.validateSchema(new FileInputStream(tmp_merge), "name", UTF_8, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }

    @Test
    public void transformation_arquitectura() throws Exception {
        XSLTTransformations transformations = null;
        try{
            transformations = gene_arquitectura.transformation(null);
            assertNull(transformations);
        }catch(Exception e){}
    }


    @Test
    public void transformation1_arquitectura() throws Exception {
        XSLTTransformations transformations = null;
        try{
            transformations = gene_arquitectura.transformation(null, null, null);
            assertNull(transformations);
        }catch(Exception e){}
    }


    @Test
    public void creation_arquitectura() throws Exception {
        gene_arquitectura.creation();
    }


    @Test
    public void creation1_arquitectura() throws Exception {
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
    public void validateSchema_arquitectura() throws Exception {
        StringWriter writer = new StringWriter();
        gene_arquitectura.creation(UTF_8, true, writer);

        Reader reader = new StringReader(writer.toString());
        JibxUnMarshall jibx = gene_arquitectura.validateSchema(reader, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema1_arquitectura() throws Exception {
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

    @Test
    public void transformation_arqueologia() throws Exception {
        XSLTTransformations transformations = null;
        try{
            transformations = gene_arqueologia.transformation(null);
            assertNull(transformations);
        }catch(Exception e){}
    }


    @Test
    public void transformation1_arqueologia() throws Exception {
        XSLTTransformations transformations = null;
        try{
            transformations = gene_arqueologia.transformation(null, null, null);
            assertNull(transformations);
        }catch(Exception e){}
    }


    @Test
    public void creation_arqueologia() throws Exception {
        gene_arqueologia.creation();
    }


    @Test
    public void creation1_arqueologia() throws Exception {
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
    public void validateSchema_arqueologia() throws Exception {
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