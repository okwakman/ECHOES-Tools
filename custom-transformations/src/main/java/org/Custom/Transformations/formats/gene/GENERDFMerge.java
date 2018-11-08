package org.Custom.Transformations.formats.gene;

import cat.gencat.*;
import org.Custom.Transformations.core.Convertible;
import org.Custom.Transformations.formats.diba.DIBACSVGENECSVDedupInfo;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GENERDFMerge extends Convertible<Pair<RDF, RDF>,RDF> {
    private HashMap<String, List<String>> archeologyDedupInfo;
    private HashMap<String, List<String>> architectureDedupInfo;

    private void linkToOriginalID(Property source_id, List<String> dups, RDF destination) {
        for (String dup : dups) {
            Optional<Property> dest_id_opt = getIdentificacio(destination, dup);
            if (dest_id_opt.isPresent()) {
                Property propDuplicat = dest_id_opt.get();
                propDuplicat.setSubClassOf(source_id.getSubClassOf());
            }
        }
    }

    private Optional<Identificacio> getIdentificacioOfProperty(RDF rdf, Property prop) {
        return rdf.getIdentificacioType().stream().filter(identificacio -> identificacio.getAbout().equals(prop.getSubClassOf().getResource())).findFirst();
    }

    private Optional<Property> getIdentificacio(RDF rdf, String id) {
        return rdf.getPropertyType().stream().filter(identificacio -> identificacio.getAbout().equals(id)).findFirst();
    }

    private Optional<Localitzacio> getLocalitzacio(RDF rdf, Property id) {
        return rdf.getLocalitzacioType().stream().filter(l -> l.getIdentificador().getResource().equals(id.getAbout())).findFirst();
    }

    private List<Tipologia> getTipologies(RDF rdf, Property id) {
        return rdf.getTipologiaType().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Us> getUsos(RDF rdf, Property id) {
        return rdf.getUsType().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Datacio> getDatacions(RDF rdf, Property id) {
        return rdf.getDatacioType().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Estil> getEstils(RDF rdf, Property id) {
        return rdf.getEstilType().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Autor> getAutors(RDF rdf, Property id) {
        return rdf.getAutorType().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Descripcio> getDescripcio(RDF rdf, Property id) {
        return rdf.getDescripcioType().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<NoticiaHistorica> getNoticiesHistoriques(RDF rdf, Property id) {
        return rdf.getNoticiaHistoricaType().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Conservacio> getConservacio(RDF rdf, Property id) {
        return rdf.getConservacioType().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Proteccio> getProteccio(RDF rdf, Property id) {
        return rdf.getProteccioType().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Propietari> getPropietaris(RDF rdf, Property id) {
        return rdf.getPropietariType().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<InformacioFitxa> getInformacioFitxes(RDF rdf, Property id) {
        return rdf.getInformacioFitxaType().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    /*private ResourceType stringToResourceType(String text) {
        ResourceType resourceType = new ResourceType();
        resourceType.setResource(text);
        return resourceType;
    }*/

    @Override
    public RDF convert(Pair<RDF, RDF> src) {
        boolean isArchitecture = Boolean.parseBoolean(this.getParams().getOrDefault("isArchitecture", "true"));
        String csvDibaPath = this.getParams().get("csvDibaPath");
        String csvGenePath = this.getParams().get("csvGenePath");
        if (isArchitecture){
            try {
                architectureDedupInfo = new DIBACSVGENECSVDedupInfo(csvDibaPath, csvGenePath, true).getIdentifierDedup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                archeologyDedupInfo = new DIBACSVGENECSVDedupInfo(csvDibaPath, csvGenePath, false).getIdentifierDedup();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        RDF source =  src.getKey();
        RDF destination = src.getValue();
        List<Identificacio> idsToAdd = new ArrayList<>();
        for (Property id : source.getPropertyType()) {
            HashMap<String, List<String>> dedupInfo;
            if (id.getTipusPatrimoni() == PatrimoniTipus.ARQUEOLÒGIC) {
                dedupInfo = this.archeologyDedupInfo;
            } else {
                dedupInfo = this.architectureDedupInfo;
            }
            if (dedupInfo.containsKey(id.getAbout())) {
                linkToOriginalID(id, dedupInfo.get(id.getAbout()), destination);
                Optional<Identificacio> origId = getIdentificacioOfProperty(source, id);
                origId.ifPresent(idsToAdd::add);
            }
            destination.getPropertyType().add(id);
            if (getLocalitzacio(source, id).isPresent()) {
                Localitzacio loc = getLocalitzacio(source, id).get();
                destination.getLocalitzacioType().add(getLocalitzacio(source, id).get());
                loc.getTerritori().forEach((territori) -> {
                    Optional<Territori> terr = destination.getTerritoriType().stream().filter(t -> t.getAbout().equals(territori.getResource())).findFirst();
                    if (!terr.isPresent()) {
                        destination.getTerritoriType().add(terr.get());
                    }
                });
            }
            destination.getTipologiaType().addAll(getTipologies(source, id));
            destination.getUsType().addAll(getUsos(source, id));
            destination.getDatacioType().addAll(getDatacions(source, id));
            destination.getEstilType().addAll(getEstils(source, id));
            destination.getAutorType().addAll(getAutors(source, id));
            destination.getConservacioType().addAll(getConservacio(source, id));
            destination.getProteccioType().addAll(getProteccio(source, id));
            destination.getPropietariType().addAll(getPropietaris(source, id));
            destination.getNoticiaHistoricaType().addAll(getNoticiesHistoriques(source, id));
            destination.getInformacioFitxaType().addAll(getInformacioFitxes(source, id));
            destination.getDescripcioType().addAll(getDescripcio(source, id));

        }
        destination.getIdentificacioType().addAll(idsToAdd);
        return destination;
    }
}
