package org.Custom.Transformations.formats.gene;

import cat.gencat.*;
import org.w3._1999._02._22_rdf_syntax_ns_.RDF;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GENERDFMerge {
    private RDF source;
    private RDF destination;
    private HashMap<String, List<String>> archeologyDedupInfo;
    private HashMap<String, List<String>> architectureDedupInfo;

    public GENERDFMerge(RDF source, RDF destination, HashMap<String, List<String>> archeologyDedupInfo, HashMap<String, List<String>> architectureDedupInfo) {
        this.source = source;
        this.destination = destination;
        this.archeologyDedupInfo = archeologyDedupInfo;
        this.architectureDedupInfo = architectureDedupInfo;
    }

    public RDF merge() {
        for (Identificacio id : source.getIdentificacio()) {
            HashMap<String, List<String>> dedupInfo;
            if (id.getTipusPatrimoni() == TipusPatrimoniTipus.ARQUEOLÒGIC) {
                dedupInfo = this.archeologyDedupInfo;
            } else {
                dedupInfo = this.architectureDedupInfo;
            }
            if (dedupInfo.containsKey(id.getAbout())) {
                mergeFields(id, dedupInfo.get(id.getAbout()));
            } else {
                destination.getIdentificacio().add(id);
                if (getLocalitzacio(source, id).isPresent()) {
                    Localitzacio loc = getLocalitzacio(source, id).get();
                    destination.getLocalitzacio().add(getLocalitzacio(source, id).get());
                    loc.getTerritori().forEach((territori) -> {
                        Optional<Territori> terr = destination.getTerritori().stream().filter(t -> t.getAbout().equals(territori.getResource())).findFirst();
                        if (!terr.isPresent()) {
                            destination.getTerritori().add(terr.get());
                        }
                    });
                }
                destination.getTipologia().addAll(getTipologies(source, id));
                destination.getUs().addAll(getUsos(source, id));
                destination.getDatacio().addAll(getDatacions(source, id));
                destination.getEstil().addAll(getEstils(source, id));
                destination.getAutor().addAll(getAutors(source, id));
                destination.getConservacio().addAll(getConservacio(source, id));
                destination.getProteccio().addAll(getProteccio(source, id));
                destination.getPropietari().addAll(getPropietaris(source, id));
            }
        }
        return destination;
    }

    private void mergeFields(Identificacio source_id, List<String> dups) {
        for (String dup : dups) {
            Optional<Identificacio> dest_id_opt = getIdentificacio(destination, dup);
            if (dest_id_opt.isPresent()) {
                Identificacio dest_id = dest_id_opt.get();
                List<String> proveidorsToAdd = new ArrayList<>();
                for (String proveidor : source_id.getProveidor()){
                    if (!dest_id.getProveidor().contains(proveidor)){
                        proveidorsToAdd.add(proveidor);
                    }
                }
                dest_id.getProveidor().addAll(proveidorsToAdd);
                mergeIdentificacio(source_id, dest_id);
                mergeLocalitzacio(source_id, dest_id);
                mergeTipologia(source_id, dest_id);
                mergeUs(source_id, dest_id);
                mergeEstil(source_id, dest_id);
                mergeAutor(source_id, dest_id);
                mergePropietari(source_id, dest_id);
                mergeDatacio(source_id, dest_id);
                mergeDescripcio(source_id, dest_id);
                mergeNoticiesHistoriques(source_id, dest_id);
                mergeConservacions(source_id, dest_id);
                mergeProteccions(source_id, dest_id);
            }
        }
    }

    private Optional<Identificacio> getIdentificacio(RDF rdf, String id) {
        return rdf.getIdentificacio().stream().filter(identificacio -> identificacio.getAbout().equals(id)).findFirst();
    }

    private Optional<Localitzacio> getLocalitzacio(RDF rdf, Identificacio id) {
        return rdf.getLocalitzacio().stream().filter(l -> l.getIdentificador().getResource().equals(id.getAbout())).findFirst();
    }

    private List<Tipologia> getTipologies(RDF rdf, Identificacio id) {
        return rdf.getTipologia().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Us> getUsos(RDF rdf, Identificacio id) {
        return rdf.getUs().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Datacio> getDatacions(RDF rdf, Identificacio id) {
        return rdf.getDatacio().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Estil> getEstils(RDF rdf, Identificacio id) {
        return rdf.getEstil().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Autor> getAutors(RDF rdf, Identificacio id) {
        return rdf.getAutor().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Descripcio> getDescripcio(RDF rdf, Identificacio id) {
        return rdf.getDescripcio().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<NoticiaHistorica> getNoticiesHistoriques(RDF rdf, Identificacio id) {
        return rdf.getNoticiaHistorica().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Conservacio> getConservacio(RDF rdf, Identificacio id) {
        return rdf.getConservacio().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Proteccio> getProteccio(RDF rdf, Identificacio id) {
        return rdf.getProteccio().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private List<Propietari> getPropietaris(RDF rdf, Identificacio id) {
        return rdf.getPropietari().stream().filter(t -> t.getIdentificador().getResource().equals(id.getAbout())).collect(Collectors.toList());
    }

    private void mergeIdentificacio(Identificacio source_id, Identificacio dest_id) {
        if (dest_id.getAltresNoms() != null && dest_id.getAltresNoms().isEmpty()) {
            if (source_id.getAltresNoms() != null && !source_id.getAltresNoms().isEmpty()) {
                dest_id.setAltresNoms(source_id.getAltresNoms());
            } else if (source_id.getNom() != null && !source_id.getNom().isEmpty() && (source_id.getNom() == null || !source_id.getNom().equals(dest_id.getNom()))) {
                dest_id.setAltresNoms(source_id.getNom());
            }
        }
    }

    private void mergeLocalitzacio(Identificacio source_id, Identificacio dest_id) {
        Optional<Localitzacio> localitzacio_src_opt = getLocalitzacio(source, source_id);
        Optional<Localitzacio> localitzacio_dst_opt = getLocalitzacio(destination, dest_id);
        if (localitzacio_src_opt.isPresent()) {
            Localitzacio localizacio_src = localitzacio_src_opt.get();
            if (localitzacio_dst_opt.isPresent()) {
                Localitzacio localitzacio_dst = localitzacio_dst_opt.get();
                if (localizacio_src.getAdreca() != null && !localizacio_src.getAdreca().isEmpty() && localitzacio_dst.getAdreca() != null && localitzacio_dst.getAdreca().isEmpty()) {
                    localitzacio_dst.setAdreca(localizacio_src.getAdreca());
                }
                if (localizacio_src.getServeiTerritorial() != null && !localizacio_src.getServeiTerritorial().isEmpty() && localitzacio_dst.getServeiTerritorial() != null && localitzacio_dst.getServeiTerritorial().isEmpty()) {
                    localitzacio_dst.setServeiTerritorial(localizacio_src.getServeiTerritorial());
                }
                if (localizacio_src.getX() != null && localizacio_src.getX() != null && localitzacio_dst.getX() == null) {
                    localitzacio_dst.setX(localizacio_src.getX());
                }
                if (localizacio_src.getY() != null && localitzacio_dst.getY() == null) {
                    localitzacio_dst.setY(localizacio_src.getY());
                }
                if (localizacio_src.getLocDescripcio() != null && !localizacio_src.getLocDescripcio().isEmpty() && localitzacio_dst.getLocDescripcio() != null && localitzacio_dst.getLocDescripcio().isEmpty()) {
                    localitzacio_dst.setLocDescripcio(localizacio_src.getLocDescripcio());
                }
            } else {
                localizacio_src.setIdentificador(IdentificacioToIdentificador(dest_id));
                destination.getLocalitzacio().add(localizacio_src);
                localizacio_src.getTerritori().forEach((territori) -> {
                    Optional<Territori> terr = destination.getTerritori().stream().filter(t -> t.getAbout().equals(territori.getResource())).findFirst();
                    if (terr.isPresent()) {
                        if (!destination.getTerritori().contains(terr.get())) {
                            destination.getTerritori().add(terr.get());
                        }
                    }
                });
            }
        }
    }

    private void mergeTipologia(Identificacio source_id, Identificacio dest_id) {
        List<Tipologia> tipologies_dst = getTipologies(destination, dest_id);
        if (tipologies_dst.size() == 0) {
            List<Tipologia> tipologies_src = getTipologies(source, source_id);
            for (Tipologia tip : tipologies_src){
                tip.setIdentificador(IdentificacioToIdentificador(dest_id));
            }
            destination.getTipologia().addAll(tipologies_src);
        }
    }

    private void mergeUs(Identificacio source_id, Identificacio dest_id) {
        List<Us> usos_dst = getUsos(destination, dest_id);
        if (usos_dst.size() == 0) {
            List<Us> usos_src = getUsos(source, source_id);
            for (Us us : usos_src){
                us.setIdentificador(IdentificacioToIdentificador(dest_id));
            }
            destination.getUs().addAll(usos_src);
        }
    }

    private void mergeEstil(Identificacio source_id, Identificacio dest_id) {
        List<Estil> estils_dst = getEstils(destination, dest_id);
        if (estils_dst.size() == 0) {
            List<Estil> estils_src = getEstils(source, source_id);
            for (Estil estil : estils_src){
                estil.setIdentificador(IdentificacioToIdentificador(dest_id));
            }
            destination.getEstil().addAll(estils_src);
        }
    }

    private void mergeAutor(Identificacio source_id, Identificacio dest_id) {
        List<Autor> autors_dst = getAutors(destination, dest_id);
        if (autors_dst.size() == 0) {
            List<Autor> autors_src = getAutors(source, source_id);
            for (Autor autor : autors_src){
                autor.setIdentificador(IdentificacioToIdentificador(dest_id));
            }
            destination.getAutor().addAll(autors_src);
        }
    }

    private void mergePropietari(Identificacio source_id, Identificacio dest_id) {
        List<Propietari> propietari_dst = getPropietaris(destination, dest_id);
        if (propietari_dst.size() == 0) {
            List<Propietari> propietaris_src = getPropietaris(source, source_id);
            for (Propietari propietari : propietaris_src){
                propietari.setIdentificador(IdentificacioToIdentificador(dest_id));
            }
            destination.getPropietari().addAll(propietaris_src);
        }
    }

    private void mergeDatacio(Identificacio source_id, Identificacio dest_id) {
        List<Datacio> datacions_dst = getDatacions(destination, dest_id);
        List<Datacio> datacions_src = getDatacions(source, source_id);
        if (datacions_dst.size() == 0) {
            for (Datacio datacio : datacions_src){
                datacio.setIdentificador(IdentificacioToIdentificador(dest_id));
            }
            destination.getDatacio().addAll(datacions_src);
        } else if (datacions_src.size() > 0){
            Datacio datacio_src = datacions_src.get(0);
            Datacio datacio_dst = datacions_dst.get(0);
            if (datacio_dst.getCronologiaInicial() != null && datacio_dst.getCronologiaInicial().equals(datacio_src.getCronologiaInicial()) ||
                    datacio_dst.getCronologiaFinal() != null && datacio_dst.getCronologiaFinal().equals(datacio_src.getCronologiaFinal()) ||
                    datacio_dst.getAnyInici() != null && datacio_dst.getAnyInici() == datacio_src.getAnyInici() ||
                    datacio_dst.getAnyFi() != null && datacio_dst.getAnyFi() == datacio_src.getAnyFi()
                    ) {
                if (datacio_dst.getCronologiaInicial() == null && datacio_src.getCronologiaInicial() != null) {
                    datacio_dst.setCronologiaInicial(datacio_src.getCronologiaInicial());
                }
                if (datacio_dst.getCronologiaFinal() == null && datacio_src.getCronologiaFinal() != null) {
                    datacio_dst.setCronologiaFinal(datacio_src.getCronologiaFinal());
                }
                if (datacio_dst.getAnyInici() == null && datacio_src.getAnyInici() != null) {
                    datacio_dst.setAnyInici(datacio_src.getAnyInici());
                }
                if (datacio_dst.getAnyFi() == null && datacio_src.getAnyFi() != null) {
                    datacio_dst.setAnyFi(datacio_src.getAnyFi());
                }
            }
        }
    }

    private void mergeDescripcio(Identificacio source_id, Identificacio dest_id) {
        List<Descripcio> descripcio_dst = getDescripcio(destination, dest_id);
        if (descripcio_dst.size() == 0) {
            List<Descripcio> descripcio_src = getDescripcio(source, source_id);
            for (Descripcio descripcio : descripcio_src){
                descripcio.setIdentificador(IdentificacioToIdentificador(dest_id));
            }
            destination.getDescripcio().addAll(descripcio_src);
        }
    }

    private void mergeNoticiesHistoriques(Identificacio source_id, Identificacio dest_id) {
        List<NoticiaHistorica> noticies_dst = getNoticiesHistoriques(destination, dest_id);
        List<NoticiaHistorica> noticies_src = getNoticiesHistoriques(source, source_id);
        if (noticies_dst.size() == 0) {
            for (NoticiaHistorica noticiaHistorica : noticies_src){
                noticiaHistorica.setIdentificador(IdentificacioToIdentificador(dest_id));
            }
            destination.getNoticiaHistorica().addAll(noticies_src);
        } else if (noticies_src.size() > 0){
            NoticiaHistorica noticia_dst = noticies_dst.get(0);
            NoticiaHistorica noticia_src = noticies_src.get(0);
            if (noticia_dst.getDataNoticiaHistorica() != null && noticia_src.getDataNoticiaHistorica().equals(noticia_dst.getDataNoticiaHistorica())) {
                if (noticia_dst.getComentariNoticiaHistorica() == null && noticia_src.getComentariNoticiaHistorica() != null) {
                    noticia_dst.setComentariNoticiaHistorica(noticia_src.getComentariNoticiaHistorica());
                }
                if (noticia_dst.getNomNoticiaHistorica() == null && noticia_src.getNomNoticiaHistorica() != null) {
                    noticia_dst.setNomNoticiaHistorica(noticia_src.getNomNoticiaHistorica());
                }
                if (noticia_dst.getTipusNoticiaHistorica() == null && noticia_src.getTipusNoticiaHistorica() != null) {
                    noticia_dst.setTipusNoticiaHistorica(noticia_src.getTipusNoticiaHistorica());
                }
            }
        }
    }

    private void mergeConservacions(Identificacio source_id, Identificacio dest_id) {
        List<Conservacio> conservacions_dst = getConservacio(destination, dest_id);
        List<Conservacio> conservacions_src = getConservacio(source, source_id);
        if (conservacions_dst.size() == 0) {
            for (Conservacio conservacio : conservacions_src){
                conservacio.setIdentificador(IdentificacioToIdentificador(dest_id));
            }
            destination.getConservacio().addAll(conservacions_src);
        } else if (conservacions_src.size() > 0){
            Conservacio conservacio_dst = conservacions_dst.get(0);
            Conservacio conservacio_src = conservacions_src.get(0);
            if (conservacio_dst.getConservacioGlobal() != null && conservacio_dst.getConservacioGlobal().equals(conservacio_src.getConservacioGlobal()) || conservacio_dst.getConservacioGlobal() != null && conservacio_dst.getConservacioEstat().equals(conservacio_src.getConservacioEstat())) {
                if (conservacio_dst.getConservacioGlobal() == null && conservacio_src.getConservacioGlobal() != null) {
                    conservacio_dst.setConservacioGlobal(conservacio_src.getConservacioGlobal());
                }
                if (conservacio_dst.getConservacioEstat() == null && conservacio_src.getConservacioEstat() != null) {
                    conservacio_dst.setConservacioEstat(conservacio_src.getConservacioEstat());
                }
                if (conservacio_dst.getConservacioComentari() == null && conservacio_src.getConservacioComentari() != null) {
                    conservacio_dst.setConservacioComentari(conservacio_src.getConservacioComentari());
                }
            }
        }
    }

    private void mergeProteccions(Identificacio source_id, Identificacio dest_id) {
        List<Proteccio> proteccions_dst = getProteccio(destination, dest_id);
        List<Proteccio> proteccions_src = getProteccio(source, source_id);
        if (proteccions_dst.size() == 0) {
            for (Proteccio proteccio : proteccions_src){
                proteccio.setIdentificador(IdentificacioToIdentificador(dest_id));
            }
            destination.getProteccio().addAll(proteccions_src);
        } else if (proteccions_src.size() > 0){
            Proteccio proteccio_dst = proteccions_dst.get(0);
            Proteccio proteccio_src = proteccions_src.get(0);
            if (proteccio_dst.getClassificacio().equals(proteccio_src.getClassificacio()) ||
                    proteccio_dst.getBCIN().equals(proteccio_src.getBCIN()) ||
                    proteccio_dst.getBIC().equals(proteccio_src.getBIC()) ||
                    proteccio_dst.getPCC().equals(proteccio_src.getPCC())
                    ) {
                if (proteccio_dst.getClassificacio() == null && proteccio_src.getClassificacio() != null) {
                    proteccio_dst.setCategoria(proteccio_src.getCategoria());
                }
                if (proteccio_dst.getPCC() == null && proteccio_src.getPCC() != null) {
                    proteccio_dst.setPCC(proteccio_src.getPCC());
                }
                if (proteccio_dst.getBCIN() == null && proteccio_src.getBCIN() != null) {
                    proteccio_dst.setBCIN(proteccio_src.getBCIN());
                }
                if (proteccio_dst.getBIC() == null && proteccio_src.getBIC() != null) {
                    proteccio_dst.setBIC(proteccio_src.getBCIN());
                }
                if (proteccio_dst.getProteccio() == null && proteccio_src.getProteccio() != null) {
                    proteccio_dst.setProteccio(proteccio_src.getProteccio());
                }
                if (proteccio_dst.getCategoria() == null && proteccio_src.getCategoria() != null) {
                    proteccio_dst.setCategoria(proteccio_src.getCategoria());
                }
            }

        }
    }

    private Identificador IdentificacioToIdentificador(Identificacio identificacio){
        Identificador identificador = new Identificador();
        identificador.setResource(identificacio.getAbout());
        return identificador;
    }
}
