package org.Custom.Transformations.formats.gene;

import org.Custom.Transformations.formats.diba.DIBACSVGENECSVDedupInfo;
import org.csuc.deserialize.JaxbUnmarshal;
import org.csuc.serialize.JaxbMarshal;
import org.junit.Before;
import org.junit.Test;
import org.w3._1999._02._22_rdf_syntax_ns_.RDF;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.nio.file.Files;

public class GENERDFMergeTest {
    RDF gene;
    RDF diba;

    @Before
    public void setUp() throws FileNotFoundException {
        FileInputStream fisGene = new FileInputStream(new File(getClass().getClassLoader().getResource("gene/Extraccio_bens_Arquitectonic_29-06-2017_fix.xml").getPath()));
        JaxbUnmarshal jaxGene = new JaxbUnmarshal(fisGene, new Class[]{RDF.class});
        gene = (RDF) jaxGene.getObject();
        FileInputStream fisDiba = new FileInputStream(new File(getClass().getClassLoader().getResource("diba/DIBA.xml").getPath()));
        JaxbUnmarshal jaxDiba = new JaxbUnmarshal(fisDiba, new Class[]{RDF.class});
        diba = (RDF) jaxDiba.getObject();
    }

    @Test
    public void testMerge() throws IOException, JAXBException {
        String csvDiba = getClass().getClassLoader().getResource("diba/DIBA.csv").getPath();
        String csvGeneArque = getClass().getClassLoader().getResource("gene/Extraccio_bens_Arqueologics_29-06-2017_fix.csv").getPath();
        String csvGeneArqui = getClass().getClassLoader().getResource("gene/Extraccio_bens_Arquitectonic_29-06-2017_fix.csv").getPath();
        DIBACSVGENECSVDedupInfo geneArqueDedup = new DIBACSVGENECSVDedupInfo(csvDiba, csvGeneArque, false);
        DIBACSVGENECSVDedupInfo geneArquiDedup = new DIBACSVGENECSVDedupInfo(csvDiba, csvGeneArqui, true);
        File tmp = Files.createTempFile("gene_rdf_merge", ".xml").toFile();
        GENERDFMerge merge = new GENERDFMerge(diba, gene, geneArqueDedup.getIdentifierDedup(), geneArquiDedup.getIdentifierDedup());
        JaxbMarshal jaxb = new JaxbMarshal(merge.merge(), RDF.class);
        FileOutputStream fileOutputStream = new FileOutputStream(tmp);
        jaxb.marshaller(fileOutputStream);
        FileInputStream fis = new FileInputStream(tmp);
        JaxbUnmarshal jaxbun = new JaxbUnmarshal(fis, new Class[]{RDF.class});
        fis = new FileInputStream(tmp);
        int oneByte;
        while ((oneByte = fis.read()) != -1) {
            System.out.write(oneByte);
        }
        System.out.flush();
        System.out.println();
    }
}
