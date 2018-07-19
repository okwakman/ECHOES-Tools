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

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.*;

public class GENERDF2EDMTest {

    private File xml;
    private File tmp;

    private EDM gene;

    @Test
    public void generateSource() throws IOException, JAXBException, DatatypeConfigurationException {
        cat.gencat.RDF rdf = new cat.gencat.RDF();

        IdentificacioType id = new IdentificacioType();
        id.setAbout("ID:About:1");
        id.setCodiInventari(this.stringToLiteralType("Codi Inventari 1"));
        id.setTipusPatrimoni(PatrimoniTipus.ARQUITECTÒNIC);
        rdf.getIdentificacioType().add(id);

        PropertyType propertyType = new PropertyType();
        propertyType.setAbout("ID:GENE:About:1");
        propertyType.setNom(this.stringToLiteralType("Nom Gene 1"));
        propertyType.setAltresNoms(this.stringToLiteralType("Altres Noms Gene 1"));
        propertyType.setTipusPatrimoni(PatrimoniTipus.ARQUITECTÒNIC);
        propertyType.setCodiIntern(this.stringToLiteralType("Codi Intern Gene 1"));
        propertyType.setProveidor(this.stringToResourceType("https://www.gencat.cat"));
        propertyType.setSubClassOf(this.aboutTypeToResourceType(id));
        rdf.getPropertyType().add(propertyType);

        IdentificacioType id2 = new IdentificacioType();
        id2.setAbout("ID:About:2");
        id2.setCodiIntern(this.stringToLiteralType("Codi Intern 2"));
        id2.setCodiInventari(this.stringToLiteralType("Codi Inventari 2"));
        id2.setNom(this.stringToLiteralType("Nom 2"));
        id2.setAltresNoms(this.stringToLiteralType("Altres Noms 2"));
        id2.setTipusPatrimoni(PatrimoniTipus.ARQUITECTÒNIC);
        id2.setPartOf(this.aboutTypeToResourceType(id));
        rdf.getIdentificacioType().add(id2);

        LocalitzacioType loc = new LocalitzacioType();
        loc.setAbout("LOC:About:1");
        loc.setAdreca(this.stringToLiteralType("Adreça 1"));
        loc.setAgregat(this.stringToLiteralType("Agregat 1"));
        loc.setLocDescripcio(this.stringToLiteralType("Descripció 1"));
        loc.setServeiTerritorial(ServeiTerritorialType.BARCELONA);
        loc.setX((float)1.00);
        loc.setY((float)2.00);
        TerritoriType territori = new TerritoriType();
        territori.setAbout("Territori 1");
        territori.setMunicipi(MunicipiType.BARCELONA);
        territori.getComarca().add(ComarcaType.BARCELONÈS);
        territori.getComarca().add(ComarcaType.ALT_EMPORDÀ);
        rdf.getTerritoriType().add(territori);
        loc.getTerritori().add(this.aboutTypeToResourceType(territori));
        //loc.setIdentificador(this.aboutTypeToResourceType(id));
        loc.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getLocalitzacioType().add(loc);

        ConservacioType conservacio = new ConservacioType();
        conservacio.setAbout("CON:About:1");
        conservacio.setIdentificador(this.propertyTypeToResourceType(propertyType));
        //conservacio.setIdentificador(this.aboutTypeToResourceType(id));
        conservacio.setConservacioComentari(this.stringToLiteralType("Comentari 1"));
        SequenceNoticiaConservacioType.ConservacioEstat estat = new SequenceNoticiaConservacioType.ConservacioEstat();
        estat.setConservacioEstatArquitectonic(ConservacioEstatArquitectonicType.RUÏNA);
        conservacio.setConservacioEstat(estat);
        rdf.getConservacioType().add(conservacio);

        DatacioType datacio = new DatacioType();
        datacio.setAbout("DAT:About:1");
        datacio.setAnyInici(500);
        datacio.setAnyFi(1000);
        SequenceDatacioType.CronologiaFinal cronologiaFinal = new SequenceDatacioType.CronologiaFinal();
        cronologiaFinal.setCronologiaFinal(EstilEpocaType.HISTORICISTA);
        SequenceDatacioType.CronologiaInicial cronologiaInicial = new SequenceDatacioType.CronologiaInicial();
        cronologiaInicial.setCronologiaInicialArquitectonic(CronologiaArquitectonicType.FERRO_IBÈRIC);
        datacio.setCronologiaFinal(cronologiaFinal);
        datacio.setCronologiaInicial(cronologiaInicial);
        //datacio.setIdentificador(this.aboutTypeToResourceType(id));
        datacio.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getDatacioType().add(datacio);

        AutorType autor = new AutorType();
        autor.setAbout("AUT:About:1");
        autor.setAnyInici(1500);
        autor.setAnyFi(2000);
        autor.setNoms(this.stringToLiteralType("Nom 1"));
        autor.getCognoms().add(this.stringToLiteralType("Cognom 1"));
        autor.setProfessio(this.stringToLiteralType("Professió 1"));
        //autor.setIdentificador(this.aboutTypeToResourceType(id));
        autor.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getAutorType().add(autor);

        DescripcioType desc = new DescripcioType();
        desc.setAbout("DSC:About:1");
        desc.setDescDescripcio(this.stringToLiteralType("Descripció 1"));
        desc.setIdentificador(this.aboutTypeToResourceType(id));
        //desc.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getDescripcioType().add(desc);

        EstilType estil = new EstilType();
        estil.setAbout("EST:About:1");
        SequenceEstilType.TipusEstil tipusEstil = new SequenceEstilType.TipusEstil();
        tipusEstil.setTipusEstilText(this.stringToLiteralType("Tipus Estil 1"));
        estil.setTipusEstil(tipusEstil);
        //estil.setIdentificador(this.aboutTypeToResourceType(id));
        estil.setIdentificador(this.propertyTypeToResourceType(propertyType));
        rdf.getEstilType().add(estil);

        InformacioFitxaType informacioFitxa = new InformacioFitxaType();
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

        NoticiaHistoricaType noticia = new NoticiaHistoricaType();
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

        PropietariType propietariType = new PropietariType();
        propietariType.setAbout("PRO:About:1");
        SequencePropietariType.TipusRegim tipusRegim = new SequencePropietariType.TipusRegim();
        tipusRegim.setTipusRegimArquitectonic(PropietariArquitectonicType.COOPERATIVA);
        propietariType.setTipusRegim(tipusRegim);
        //propietariType.setIdentificador(this.propertyTypeToResourceType(propertyType));
        propietariType.setIdentificador(this.aboutTypeToResourceType(id));
        rdf.getPropietariType().add(propietariType);

        ProteccioType proteccioType = new ProteccioType();
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

        UsType usType = new UsType();
        usType.setAbout("US:About:1");
        SequenceUsType.OriginalActual originalActual = new SequenceUsType.OriginalActual();
        originalActual.setTipusUtilitzacio(UtilitzacioType.ACADEMICISME);
        usType.setOriginalActual(originalActual);
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

        gene = FactoryEDM.createFactory(new GENERDF2EDM((cat.gencat.RDF) jxb.getObject(), properties()));
        assertNotNull(gene);
    }

    private LiteralType stringToLiteralType(String text){
        LiteralType literalType = new LiteralType();
        literalType.setValue(text);
        return literalType;
    }

    private ResourceType aboutTypeToResourceType(IdentificadorType aboutType){
        ResourceType resourceType = new ResourceType();
        resourceType.setResource(aboutType.getAbout());
        return resourceType;
    }

    private ResourceType stringToResourceType(String text){
        ResourceType resourceType = new ResourceType();
        resourceType.setResource(text);
        return resourceType;
    }

    private ResourceType propertyTypeToResourceType(PropertyType propertyType){
        ResourceType resourceType = new ResourceType();
        resourceType.setResource(propertyType.getAbout());
        return resourceType;
    }

    private ResourceType aboutTypeToResourceType(AboutType aboutType){
        ResourceType resourceType = new ResourceType();
        resourceType.setResource(aboutType.getAbout());
        return resourceType;
    }

    //@Before
    public void setUp() throws Exception {
        xml = new File(getClass().getClassLoader().getResource("gene/gene.xml").getFile());
        tmp = Files.createTempFile("generdf_edm", ".xml").toFile();
        assertTrue(xml.exists());

        JaxbUnmarshal jxb = new JaxbUnmarshal(xml, new Class[] { cat.gencat.RDF.class });
        assertNotNull(jxb.getObject());
        assertTrue(jxb.isValidating());

        gene = FactoryEDM.createFactory(new GENERDF2EDM((cat.gencat.RDF) jxb.getObject(), properties()));
        assertNotNull(gene);

        //tmp.deleteOnExit();
    }

    @Test
    public void transformation() throws Exception {
        XSLTTransformations transformations = null;
        try{
            transformations = gene.transformation(null);
            assertNull(transformations);
        }catch(Exception e){}
    }


    @Test
    public void transformation1() throws Exception {
        XSLTTransformations transformations = null;
        try{
            transformations = gene.transformation(null, null, null);
            assertNull(transformations);
        }catch(Exception e){}
    }


    @Test
    public void creation() throws Exception {
        gene.creation();
    }


    @Test
    public void creation1() throws Exception {
        StringWriter writer = new StringWriter();
        gene.creation(UTF_8, true, writer);
        assertTrue(!writer.toString().isEmpty());
    }


    @Test
    public void creation2() throws Exception {
        FileOutputStream outs = new FileOutputStream(tmp);
        gene.creation(UTF_8, true, outs);

        int b  = new FileInputStream(tmp).read();
        assertNotEquals(-1, b);
    }


    @Test
    public void validateSchema() throws Exception {
        StringWriter writer = new StringWriter();
        gene.creation(UTF_8, true, writer);

        Reader reader = new StringReader(writer.toString());
        JibxUnMarshall jibx = gene.validateSchema(reader, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema1() throws Exception {
        StringWriter writer = new StringWriter();
        gene.creation(UTF_8, true, writer);

        Reader reader = new StringReader(writer.toString());
        JibxUnMarshall jibx = gene.validateSchema(reader, "name", RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema2() throws Exception {
        FileOutputStream outs = new FileOutputStream(tmp);
        gene.creation(UTF_8, true, outs);

        JibxUnMarshall jibx = gene.validateSchema(new FileInputStream(tmp), UTF_8, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }


    @Test
    public void validateSchema3() throws Exception {
        FileOutputStream outs = new FileOutputStream(tmp);
        gene.creation(UTF_8, true, outs);

        JibxUnMarshall jibx = gene.validateSchema(new FileInputStream(tmp), "name", UTF_8, RDF.class);

        assertNotNull(jibx);
        assertNotNull(jibx.getElement());
        assertNull(jibx.getError());
    }

    @Test
    public void modify() throws Exception {

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