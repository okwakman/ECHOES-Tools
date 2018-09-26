package org.Custom.Transformations.formats.gene;

import cat.gencat.RDF;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.csuc.deserialize.JaxbUnmarshal;
import org.csuc.serialize.JaxbMarshal;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;

public class GENERDFMergeTest {
    RDF gene;
    RDF diba;

    @Before
    public void setUp() throws FileNotFoundException {
        FileInputStream fisGene = new FileInputStream(new File(getClass().getClassLoader().getResource("gene/Extraccio_bens_Arquitectonic_29-06-2017.xml").getPath()));
        JaxbUnmarshal jaxGene = new JaxbUnmarshal(fisGene, new Class[]{RDF.class});
        gene = (RDF) jaxGene.getObject();
        FileInputStream fisDiba = new FileInputStream(new File(getClass().getClassLoader().getResource("diba/DIBA_arquitectonic.xml").getPath()));
        JaxbUnmarshal jaxDiba = new JaxbUnmarshal(fisDiba, new Class[]{RDF.class});
        diba = (RDF) jaxDiba.getObject();
    }

    @Test
    public void testMerge() throws IOException, JAXBException {
        String csvDiba = getClass().getClassLoader().getResource("diba/DIBA.csv").getPath();
        String csvGeneArqui = getClass().getClassLoader().getResource("gene/Extraccio_bens_Arquitectonic_29-06-2017.csv").getPath();
        File tmp = Files.createTempFile("gene_rdf_merge", ".xml").toFile();

        GENERDFMerge merge = new GENERDFMerge();
        merge.getParams().put("csvDibaPath", csvDiba);
        merge.getParams().put("csvGenePath", csvGeneArqui);
        Pair<RDF, RDF> dibaGeneRdf = new ImmutablePair<>(diba, gene);

        RDF generdfmerge = merge.convert(dibaGeneRdf);

        JaxbMarshal jaxb = new JaxbMarshal(generdfmerge, RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[]{RDF.class});;
        tmp.deleteOnExit();
    }
}
