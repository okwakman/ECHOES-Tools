package org.EDM.Transformations.formats.gene;

import cat.gencat.*;
import cat.gencat.ResourceType;
import eu.europeana.corelib.definitions.jibx.*;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.jibx.ResourceOrLiteralType;
import net.sf.saxon.functions.IriToUri;
import org.EDM.Transformations.formats.EDM;
import org.EDM.Transformations.formats.xslt.XSLTTransformations;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.io.IoBuilder;
import org.csuc.deserialize.JibxUnMarshall;
import org.csuc.serialize.JibxMarshall;
import org.osgeo.proj4j.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class GENERDF2EDM extends RDF implements EDM {
    private static Logger logger = LogManager.getLogger(GENERDF2EDM.class);
    private cat.gencat.RDF GENERDF;
    private Map<String, String> properties;
    private Map<String, Identificacio> ids;
    private Map<String, Property> props;

    public GENERDF2EDM(cat.gencat.RDF type, Map<String, String> properties) {
        this.GENERDF = type;
        this.properties = properties;
        this.ids = new HashMap<>();
        this.props = new HashMap<>();
        this.cacheIdentificador();
        // TODO: Use Merged Info instead of the particular if available
        edmProvidedCHO();
        edmAgent();
        edmPlace();
        skosConcept();
        edmTimeSpan();
    }

    private void cacheIdentificador(){
        GENERDF.getIdentificacioType().forEach((Identificacio id) -> ids.put(id.getAbout(), id));
        GENERDF.getPropertyType().forEach((Property prop) -> props.put(prop.getAbout(), prop));
    }

    private void edmTimeSpan() {
        try {
            // Datacio
            GENERDF.getDatacioType().forEach((Datacio datacio) -> {
                Choice choice = new Choice();
                TimeSpanType timeSpan = new TimeSpanType();
                // Datacio[rdf:about]
                timeSpan.setAbout(datacio.getAbout());
                /* Datacio -> anyInici
                   Datacio -> anyFi
                   Datacio -> cronologiaInicial
                   Datacio -> cronologiaFinal
                */
                Begin begin = new Begin();
                End end = new End();
                PrefLabel prefLabel = new PrefLabel();
                if (datacio.getAnyInici() != null && !datacio.getAnyInici().isEmpty()){
                    prefLabel.setString(yearMinFourDigits(datacio.getAnyInici().get(0)));
                    begin.setString(yearMinFourDigits(datacio.getAnyInici().get(0)));
                }
                PatrimoniTipus tipusPatrimoni = getTipusPatrimoni(datacio);
                switch (tipusPatrimoni){
                    /*case ARQUITECTÒNIC:
                        if (datacio.getAnyInici() == null && datacio.getCronologiaInicialArquitectonic() != null){
                            prefLabel.setString(datacio.getCronologiaInicialArquitectonic().value());
                            begin.setString(datacio.getCronologiaInicialGeneral().value());
                        }
                        if (datacio.getAnyFi() == null && datacio.getCronologiaFinalArquitectonic() != null){
                            if (prefLabel.getString().isEmpty()) {
                                prefLabel.setString(datacio.getCronologiaFinalArquitectonic().value());
                            } else {
                                prefLabel.setString(prefLabel.getString() + " - " + datacio.getCronologiaFinalArquitectonic().value());
                            }
                            end.setString(datacio.getCronologiaFinalArquitectonic().value());
                        }
                        break;
                    case ARQUEOLÒGIC:
                        if (datacio.getAnyInici() == null && datacio.getCronologiaInicialArqueologic() != null){
                            prefLabel.setString(datacio.getCronologiaInicialArqueologic().value());
                            begin.setString(datacio.getCronologiaInicialArqueologic().value());
                        }
                        if (datacio.getAnyFi() == null && datacio.getCronologiaFinalArquitectonic() != null){
                            if (prefLabel.getString().isEmpty()) {
                                prefLabel.setString(datacio.getCronologiaFinalArqueologic().value());
                            } else {
                                prefLabel.setString(prefLabel.getString() + " - " + datacio.getCronologiaFinalArquitectonic().value());
                            }
                            end.setString(datacio.getCronologiaFinalArqueologic().value());
                        }
                        break;
                    */
                    case ARQUITECTÒNIC:
                    case ARQUEOLÒGIC:
                        String cronologiaInicial = null;
                        String cronologiaFinal = null;
                        if (!datacio.getCronologiaInicial().isEmpty()){
                            cronologiaInicial = getCronologiaValue(datacio.getCronologiaInicial().get(0));
                        }
                        if (!datacio.getCronologiaFinal().isEmpty()){
                            cronologiaFinal = getCronologiaValue(datacio.getCronologiaFinal().get(0));
                        }

                        if (datacio.getAnyInici().isEmpty() && cronologiaInicial != null){
                            prefLabel.setString(cronologiaInicial);
                            begin.setString(cronologiaInicial);
                        }
                        if (datacio.getAnyFi().isEmpty() && cronologiaFinal != null){
                            if (prefLabel.getString().isEmpty()) {
                                prefLabel.setString(cronologiaFinal);
                            } else {
                                prefLabel.setString(prefLabel.getString() + " - " + cronologiaFinal);
                            }
                            end.setString(cronologiaFinal);
                        }
                        break;

                }
                /*if (datacio.getAnyInici() == null && datacio.getCronologiaInicialGeneral() != null){
                    prefLabel.setString(datacio.getCronologiaInicialGeneral().value());
                    begin.setString(datacio.getCronologiaInicialGeneral().value());
                }
                if (datacio.getAnyFi() == null && datacio.getCronologiaFinalGeneral() != null){
                    if (prefLabel.getString().isEmpty()) {
                        prefLabel.setString("??-??-??");
                    }
                    prefLabel.setString(datacio.getCronologiaFinalGeneral().value());
                    begin.setString(datacio.getCronologiaFinalGeneral().value());
                } else if (datacio.getAnyFi() != null) {
                    if (prefLabel.getString().isEmpty()) {
                        prefLabel.setString("??-??-??");
                    }
                    prefLabel.setString(prefLabel.getString() + " - ");
                    prefLabel.setString(prefLabel.getString() + yearMinFourDigits(datacio.getAnyFi().get(0)) + "-01-01");
                }*/
                if (prefLabel.getString() != null && !prefLabel.getString().isEmpty()){
                    timeSpan.getPrefLabelList().add(prefLabel);
                }
                if (begin.getString() != null && !begin.getString().isEmpty()){
                    timeSpan.setBegin(begin);
                }
                if (end.getString() != null && !end.getString().isEmpty()){
                    timeSpan.setEnd(end);
                }
                choice.setTimeSpan(timeSpan);
                this.getChoiceList().add(choice);
            });
        } catch (Exception exception) {
            logger.error(String.format("[%s] error generate edmTimeSpan \n", exception));
        }
    }

    private static String getCronologiaValue(Object cronologia){
        if (cronologia instanceof CronologiaArquitectonicType){
            return ((CronologiaArquitectonicType)cronologia).value();
        } else if (cronologia instanceof CronologiaArqueologicType){
            return ((CronologiaArqueologicType)cronologia).value();
        } else if (cronologia instanceof EstilEpocaType){
            return ((EstilEpocaType)cronologia).value();
        }
        return null;
    }

    private void skosConcept() {
        try {
            // Us
            GENERDF.getUsType().forEach((Us us) -> {
                Choice choice = new Choice();
                Concept concept = new Concept();
                // "Us" + Us -> originalActual
                String usText = null;
                if (us.getOriginalActualText() != null){
                    usText = us.getOriginalActualText().getValue();
                } else if (us.getTipusOriginalActual() != null){
                    usText = us.getTipusOriginalActual().value();
                } else if (us.getTipusUtilitzacio() != null){
                    usText = us.getTipusUtilitzacio().value();
                }
                if (usText != null){
                    concept.setAbout("Us:" + IriToUri.iriToUri(StringUtils.deleteWhitespace(usText)));
                    // Us -> originalActual
                    PrefLabel prefLabel = new PrefLabel();
                    prefLabel.setString(usText);
                    Concept.Choice cChoice = new Concept.Choice();
                    cChoice.setPrefLabel(prefLabel);
                    concept.getChoiceList().add(cChoice);
                    // Relation with Identificacio
                    cChoice = new Concept.Choice();
                    Related related = new Related();
                    related.setResource(us.getIdentificador().getResource());
                    cChoice.setRelated(related);
                    concept.getChoiceList().add(cChoice);

                    choice.setConcept(concept);
                    this.getChoiceList().add(choice);
                }
            });
        } catch (Exception exception) {
            logger.error(String.format("[%s] error generate skosConcept \n", exception));
        }
    }

    private ProjCoordinate UTMToWGS84(Float x, Float y){
        CoordinateTransformFactory ctFactory = new CoordinateTransformFactory();
        CRSFactory csFactory = new CRSFactory();
        CoordinateReferenceSystem utm31 = csFactory.createFromName("EPSG:32631");
        CoordinateReferenceSystem wgs = csFactory.createFromName("EPSG:4326");
        CoordinateTransform trans = ctFactory.createTransform(utm31, wgs);
        ProjCoordinate p1 = new ProjCoordinate();
        ProjCoordinate p2 = new ProjCoordinate();
        p1.x = x;
        p1.y = y;
        trans.transform(p1, p2);
        return p2;
    }

    private void edmPlace() {
        try {
            // Localitzacio
            GENERDF.getLocalitzacioType().forEach((Localitzacio localitzacio) -> {
                Choice choice = new Choice();
                PlaceType place = new PlaceType();
                // Localitzacio[rdf:about]
                place.setAbout(localitzacio.getAbout());
                /*
                    Localitzacio -> X
                    Localitzacio -> Y
                */
                if (localitzacio.getX() != null && localitzacio.getY() != null) {
                    ProjCoordinate convertedCoords = UTMToWGS84(localitzacio.getX(), localitzacio.getY());
                    _Long _long = new _Long();
                    _long.setLong((float)convertedCoords.x);
                    place.setLong(_long);
                    Lat lat = new Lat();
                    lat.setLat((float)convertedCoords.y);
                    place.setLat(lat);
                }
                String prefLabelStr = "";
                // Localitzacio -> adreca
                if (localitzacio.getAdreca() != null) {
                    prefLabelStr += localitzacio.getAdreca().getValue() + ", ";
                }
                if (localitzacio.getTerritori() != null) {
                    // TODO: Use more than 1 Territori
                    ResourceType locTerritori = localitzacio.getTerritori().get(0);
                    Optional<Territori> territoriOpt = GENERDF.getTerritoriType().stream().filter(t -> t.getAbout().equals((locTerritori).getResource())).findFirst();
                    Territori territori = territoriOpt.get();
                    // Localitzacio -> territori -> comarca
                    prefLabelStr += territori.getMunicipi().value() + ", " + territori.getComarca().get(0).value();
                } else {
                    prefLabelStr = prefLabelStr.substring(0, prefLabelStr.length() - 2);
                }
                // Localitzacio -> agregat
                if (localitzacio.getAgregat() != null) {
                    prefLabelStr += " (" + localitzacio.getAgregat().getValue() + ")";
                }
                if (!prefLabelStr.isEmpty()) {
                    PrefLabel prefLabel = new PrefLabel();
                    prefLabel.setString(prefLabelStr);
                    place.getPrefLabelList().add(prefLabel);
                }
                // Localitzacio -> locDescripcio
                if (localitzacio.getLocDescripcio() != null) {
                    Note note = new Note();
                    note.setString(localitzacio.getLocDescripcio().getValue());
                    place.getNoteList().add(note);
                }
                choice.setPlace(place);
                this.getChoiceList().add(choice);
            });
        } catch (Exception exception) {
            logger.error(String.format("[%s] error generate edmPlace \n", exception));
        }
    }

    private String yearMinFourDigits(int year){
        boolean isNegative = year < 0;
        String year_converted = String.format("%04d", year);
        if (isNegative){
            year_converted = '-' + year_converted;
        }
        return year_converted;
    }

    private void edmAgent() {
        try {
            // Autor
            GENERDF.getAutorType().forEach((Autor autor) -> {
                Choice choice = new Choice();
                AgentType agent = new AgentType();
                // Autor[rdf:about]
                agent.setAbout(autor.getAbout());
                // Autor -> noms
                if (autor.getNoms() != null){
                    PrefLabel prefLabel = new PrefLabel();
                    prefLabel.setString(autor.getNoms().getValue());
                    agent.getPrefLabelList().add(prefLabel);
                }
                // Autor -> cognoms
                if (autor.getCognoms() != null){
                    autor.getCognoms().forEach((cat.gencat.LiteralType cognom) -> {
                        AltLabel altLabel = new AltLabel();
                        altLabel.setString(cognom.getValue());
                        agent.getAltLabelList().add(altLabel);
                    });
                }
                if (autor.getAnyInici() != null || autor.getAnyFi() != null){
                    Choice choiceDatacio = new Choice();
                    TimeSpanType timeSpan = new TimeSpanType();
                    // Autor[rdf:about]
                    timeSpan.setAbout(autor.getAbout() + ":Datacio");
                    // Autor -> anyInici
                    if (autor.getAnyInici() != null) {
                        PrefLabel prefLabel = new PrefLabel();
                        String anyInici = yearMinFourDigits(autor.getAnyInici());
                        prefLabel.setString(anyInici + "-01-01");
                        timeSpan.getPrefLabelList().add(prefLabel);
                        Begin begin = new Begin();
                        begin.setString(prefLabel.getString());
                        timeSpan.setBegin(begin);
                    }
                    // Autor -> anyFi
                    if (autor.getAnyFi() != null) {
                        End end = new End();
                        end.setString(yearMinFourDigits(autor.getAnyFi()) + "-01-01");
                        timeSpan.setEnd(end);
                    }
                    choiceDatacio.setTimeSpan(timeSpan);
                    this.getChoiceList().add(choiceDatacio);

                    Date date = new Date();
                    ResourceOrLiteralType.Resource resource = new ResourceOrLiteralType.Resource();
                    resource.setResource(timeSpan.getAbout());
                    date.setResource(resource);
                    date.setString("");
                }
                // Autor -> professio
                if (autor.getProfessio() != null){
                    ProfessionOrOccupation professio = new ProfessionOrOccupation();
                    professio.setString(autor.getProfessio().getValue());
                    agent.getProfessionOrOccupationList().add(professio);
                }
                choice.setAgent(agent);
                this.getChoiceList().add(choice);
            });
        } catch (Exception exception) {
            logger.error(String.format("[%s] error generate edmAgent \n", exception));
        }
    }

    private PatrimoniTipus getTipusPatrimoni(IdentificadorType idr){
        if (ids.containsKey(idr.getIdentificador().getResource())){
            return ids.get(idr.getIdentificador().getResource()).getTipusPatrimoni();
        } else if (props.containsKey(idr.getIdentificador().getResource())){
            return props.get(idr.getIdentificador().getResource()).getTipusPatrimoni();
        }
        return null;
    }
    private void edmProvidedCHO() {
        try {
            // Identificacio
            GENERDF.getPropertyType().forEach((Property identificacio) -> {
                Choice choice = new Choice();
                ProvidedCHOType provided = new ProvidedCHOType();
                // Identificacio[rdf:about]
                provided.setAbout(identificacio.getAbout());
                // Descripcio -> descDescripcio
                GENERDF.getDescripcioType().stream().filter(d -> d.getIdentificador().getResource().equals(identificacio.getAbout())).forEach((Descripcio descripcio) -> {
                    eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cDesc = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                    Description desc = new Description();
                    desc.setString(descripcio.getDescDescripcio().getValue());
                    cDesc.setDescription(desc);
                    provided.getChoiceList().add(cDesc);
                });
                // Identificacio -> codiIntern
                eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cCodiIntern = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                Identifier codiIntern = new Identifier();
                codiIntern.setString(identificacio.getCodiIntern().getValue());
                cCodiIntern.setIdentifier(codiIntern);
                provided.getChoiceList().add(cCodiIntern);
                // Identificacio -> codiInventari
                if (identificacio.getCodiInventari() != null) {
                    eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cCodiInventari = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                    Identifier codiInventari = new Identifier();
                    codiInventari.setString(identificacio.getCodiInventari().getValue());
                    cCodiInventari.setIdentifier(codiInventari);
                    provided.getChoiceList().add(cCodiInventari);
                }
                // Identificacio -> type
                eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cProveidor = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                Publisher publisher = new Publisher();
                ResourceOrLiteralType.Resource publisherRsc = new ResourceOrLiteralType.Resource();
                publisherRsc.setResource(identificacio.getProveidor().getResource());
                publisher.setResource(publisherRsc);
                publisher.setString("");
                cProveidor.setPublisher(publisher);
                provided.getChoiceList().add(cProveidor);
                // propietari -> tipusRegim
                GENERDF.getPropietariType().stream().filter(p -> p.getIdentificador().getResource().equals(identificacio.getAbout())).forEach((Propietari propietari) -> {
                    eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cPropietari = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                    Rights rights = new Rights();
                    PatrimoniTipus tipusPatrimoni = getTipusPatrimoni(propietari);
                    switch (tipusPatrimoni){
                        case ARQUITECTÒNIC:
                            if (propietari.getTipusRegimArquitectonic() != null){
                                rights.setString(propietari.getTipusRegimArquitectonic().value());
                            }
                            break;
                        case ARQUEOLÒGIC:
                            if (propietari.getTipusRegimArqueologic() != null){
                                rights.setString(propietari.getTipusRegimArqueologic().value());
                            }
                    }
                    if (!rights.getString().isEmpty()){
                        cPropietari.setRights(rights);
                        provided.getChoiceList().add(cPropietari);
                    }
                });
                // Identificacio -> nom
                eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cNom = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                Title title = new Title();
                title.setString(identificacio.getNom().getValue());
                cNom.setTitle(title);
                provided.getChoiceList().add(cNom);
                // Tipologia -> tipologia
                GENERDF.getTipologiaType().stream().filter(t -> t.getIdentificador().getResource().equals(identificacio.getAbout())).forEach((Tipologia tipologia) -> {
                    eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cTipologia = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                    Type1 type = new Type1();
                    type.setString(tipologia.toString());
                    cTipologia.setType(type);
                    provided.getChoiceList().add(cTipologia);
                });
                // Identificacio -> tipusPatrimoni
                if (identificacio.getTipusPatrimoni() != null) {
                    HasType hasType = new HasType();
                    hasType.setString(identificacio.getTipusPatrimoni().value());
                    provided.getHasTypeList().add(hasType);
                }
                // Identificacio -> altresNoms
                if (identificacio.getAltresNoms() != null) {
                    eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cAltresNoms = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                    Alternative alternative = new Alternative();
                    alternative.setString(identificacio.getAltresNoms().getValue());
                    cAltresNoms.setAlternative(alternative);
                    provided.getChoiceList().add(cAltresNoms);
                }
                // Parameter("edmType")
                Optional.ofNullable(properties).map((Map<String, String> m) -> m.get("edmType")).ifPresent((String present) -> {
                    if (Objects.nonNull(EdmType.convert(present))) {
                        Type2 t = new Type2();
                        t.setType(EdmType.convert(present));
                        provided.setType(t);
                    }
                });
                long numNoticiesHistoriques = GENERDF.getNoticiaHistoricaType().stream().filter(t -> t.getIdentificador().getResource().equals(identificacio.getAbout())).count();
                //System.out.print("NUM NOTICIES: " + numNoticiesHistoriques);
                if (numNoticiesHistoriques > 0){
                    Choice aggChoice = new Choice();
                    Aggregation agg = new Aggregation();
                    // Identificacio[rdf:about] + ":Aggregation"
                    agg.setAbout(identificacio.getAbout() + ":Aggregation");
                    // Identificacio[rdf:about]
                    AggregatedCHO aggregatedCHO = new AggregatedCHO();
                    aggregatedCHO.setResource(identificacio.getAbout());
                    agg.setAggregatedCHO(aggregatedCHO);
                    // Parameter("dataProvider")
                    Optional.ofNullable(properties).map((Map<String, String> m) -> m.get("dataProvider")).ifPresent((String data) -> {
                        DataProvider dataProvider = new DataProvider();
                        dataProvider.setString(data);
                        agg.setDataProvider(dataProvider);
                    });
                    // Parameter("provider")
                    Optional.ofNullable(properties).map((Map<String, String> m) -> m.get("provider")).ifPresent((String data) -> {
                        Provider provider = new Provider();
                        provider.setString(data);
                        agg.setProvider(provider);
                    });
                    // Parameter("rights")
                    Optional.ofNullable(properties).map((Map<String, String> m) -> m.get("rights")).ifPresent((String data) -> {
                        Rights1 rights = new Rights1();
                        rights.setResource(IriToUri.iriToUri(data).toString());
                        agg.setRights(rights);
                    });
                    // URL in ECHOES environment
                    IsShownAt isShownAt = new IsShownAt();
                    isShownAt.setResource("https://echoes.pre.csuc.cat/providers/details_improved/?subject=" + identificacio.getAbout());
                    agg.setIsShownAt(isShownAt);
                    aggChoice.setAggregation(agg);
                    this.getChoiceList().add(aggChoice);
                }
                // dcterms:temporal -> datacio
                GENERDF.getDatacioType().stream().filter(d -> d.getIdentificador().getResource().equals(identificacio.getAbout())).forEach((Datacio datacio) -> {
                    eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cTemporal = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                    Temporal temporal = new Temporal();
                    ResourceOrLiteralType.Resource resource = new ResourceOrLiteralType.Resource();
                    resource.setResource(datacio.getAbout());
                    temporal.setResource(resource);
                    temporal.setString("");
                    cTemporal.setTemporal(temporal);
                    provided.getChoiceList().add(cTemporal);
                });
                // dcterms:spatial -> localitzacio
                GENERDF.getLocalitzacioType().stream().filter(l -> l.getIdentificador().getResource().equals(identificacio.getAbout())).forEach((Localitzacio localitzacio) -> {
                    eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cSpatial = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                    Spatial spatial = new Spatial();
                    ResourceOrLiteralType.Resource resource = new ResourceOrLiteralType.Resource();
                    resource.setResource(localitzacio.getAbout());
                    spatial.setResource(resource);
                    spatial.setString("");
                    cSpatial.setSpatial(spatial);
                    provided.getChoiceList().add(cSpatial);
                });
                // dc:creator -> autor
                GENERDF.getAutorType().stream().filter(a -> a.getIdentificador().getResource().equals(identificacio.getAbout())).forEach((Autor autor) -> {
                    eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cCreator = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                    Creator creator = new Creator();
                    ResourceOrLiteralType.Resource resource = new ResourceOrLiteralType.Resource();
                    resource.setResource(autor.getAbout());
                    creator.setResource(resource);
                    creator.setString("");
                    cCreator.setCreator(creator);
                    provided.getChoiceList().add(cCreator);
                });
                choice.setProvidedCHO(provided);
                this.getChoiceList().add(choice);
            });
            // noticiaHistorica
            GENERDF.getNoticiaHistoricaType().forEach((NoticiaHistorica noticiaHistorica) -> {
                Choice choice = new Choice();
                ProvidedCHOType provided = new ProvidedCHOType();
                // noticiaHistorica[rdf:about]
                provided.setAbout(noticiaHistorica.getAbout());
                // noticiaHistorica -> dataNoticiaHistorica
                if (noticiaHistorica.getDataNoticiaHistorica() != null) {
                    eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cData = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                    Date date = new Date();
                    date.setString(noticiaHistorica.getDataNoticiaHistorica().toString());
                    cData.setDate(date);
                    provided.getChoiceList().add(cData);
                }
                // noticiaHistorica -> comentariNoticiaHistorica
                if (noticiaHistorica.getComentariNoticiaHistorica() != null) {
                    eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cComentari = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                    Description description = new Description();
                    description.setString(noticiaHistorica.getComentariNoticiaHistorica().getValue());
                    cComentari.setDescription(description);
                    provided.getChoiceList().add(cComentari);
                }
                // noticiaHistorica -> nomNoticiaHistorica
                if (noticiaHistorica.getNomNoticiaHistorica() != null) {
                    eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cNom = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                    Title title = new Title();
                    title.setString(noticiaHistorica.getNomNoticiaHistorica().getValue());
                    cNom.setTitle(title);
                    provided.getChoiceList().add(cNom);
                }
                // noticiaHistorica -> tipusNoticiaHistorica
                if (noticiaHistorica.getTipusNoticiaHistorica() != null) {
                    eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice cTipus = new eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice();
                    Type1 type = new Type1();
                    type.setString(noticiaHistorica.getTipusNoticiaHistorica().getValue());
                    cTipus.setType(type);
                    provided.getChoiceList().add(cTipus);
                }
                // HasType -> "HistoricalNews"
                HasType hasType = new HasType();
                hasType.setString("HistoricalNews");
                provided.getHasTypeList().add(hasType);
                // Relation to Ore:Aggregation
                IsRelatedTo isRelated = new IsRelatedTo();
                ResourceOrLiteralType.Resource resourceIsRelated = new ResourceOrLiteralType.Resource();
                resourceIsRelated.setResource(noticiaHistorica.getIdentificador().getResource() + ":Aggregation");
                isRelated.setResource(resourceIsRelated);
                isRelated.setString("");
                provided.getIsRelatedToList().add(isRelated);

                // edm:type = TEXT
                Type2 t = new Type2();
                t.setType(EdmType.convert("TEXT"));
                provided.setType(t);
                choice.setProvidedCHO(provided);
                this.getChoiceList().add(choice);
            });
        } catch (Exception exception) {
            logger.error(String.format("[%s] error generate edmProvidedCHO \n", exception));
        }
    }

    @Override
    public XSLTTransformations transformation(OutputStream out, Map<String, String> xsltProperties) throws Exception {
        throw new IllegalArgumentException("transformation is not valid for GENERDF2EDM!");
    }

    @Override
    public XSLTTransformations transformation(String xslt, OutputStream out, Map<String, String> xsltProperties) throws Exception {
        throw new IllegalArgumentException("transformation is not valid for GENERDF2EDM!");
    }

    @Override
    public XSLTTransformations transformation(String xslt) throws Exception {
        throw new IllegalArgumentException("transformation is not valid for GENERDF2EDM!");
    }

    @Override
    public void creation() {
        if (!Objects.equals(this, new RDF()))
            JibxMarshall.marshall(this, StandardCharsets.UTF_8.toString(),
                    false, IoBuilder.forLogger(GENERDF2EDM.class).setLevel(Level.INFO).buildOutputStream(), RDF.class, -1);
    }

    @Override
    public void creation(Charset encoding, boolean alone, OutputStream outs) {
        if (!Objects.equals(this, new RDF()))
            JibxMarshall.marshall(this, encoding.toString(), alone, outs, RDF.class, -1);
    }

    @Override
    public void creation(Charset encoding, boolean alone, Writer writer) {
        if (!Objects.equals(this, new RDF()))
            JibxMarshall.marshall(this, encoding.toString(), alone, writer, RDF.class, -1);
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
