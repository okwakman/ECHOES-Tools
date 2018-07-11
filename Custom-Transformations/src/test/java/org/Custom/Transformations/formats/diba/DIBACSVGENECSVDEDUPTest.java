package org.Custom.Transformations.formats.diba;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DIBACSVGENECSVDEDUPTest {
    private DIBACSVGENECSVDedupInfo geneArqueDedup;
    private DIBACSVGENECSVDedupInfo geneArquiDedup;

    @Before
    public void setUp() throws IOException {
        String csvDiba = getClass().getClassLoader().getResource("diba/DIBA.csv").getPath();
        String csvGeneArque = getClass().getClassLoader().getResource("gene/Extraccio_bens_Arqueologics_29-06-2017_fix.csv").getPath();
        String csvGeneArqui = getClass().getClassLoader().getResource("gene/Extraccio_bens_Arquitectonic_29-06-2017_fix.csv").getPath();
        geneArqueDedup = new DIBACSVGENECSVDedupInfo(csvDiba, csvGeneArque, false);
        geneArquiDedup = new DIBACSVGENECSVDedupInfo(csvDiba, csvGeneArqui, true);
    }

    @Test
    public void test() throws IOException {
        HashMap<String, List<String>> arqueDedup = geneArqueDedup.getIdentifierDedup();
        HashMap<String, List<String>> arquiDedup = geneArquiDedup.getIdentifierDedup();
        System.out.println("Arquitectura DEDUP Num.: " + arquiDedup.values().size());
        for (Map.Entry<String, List<String>> arquiKeys : arquiDedup.entrySet()){
            System.out.println("DIBA: " + arquiKeys.getKey());
            System.out.println("\tGENE: " + arquiKeys.getValue());
        }
        System.out.println("Arqueologia DEDUP Num.: " + arqueDedup.values().size());
        for (Map.Entry<String, List<String>> arqueKeys : arqueDedup.entrySet()){
            System.out.println("DIBA: " + arqueKeys.getKey());
            System.out.println("\tGENE: " + arqueKeys.getValue());
        }
    }
}
