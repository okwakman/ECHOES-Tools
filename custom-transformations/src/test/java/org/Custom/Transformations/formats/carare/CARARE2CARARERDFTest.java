package org.Custom.Transformations.formats.carare;

import eu.carare.carareschema.CarareWrap;
import org.csuc.deserialize.JaxbUnmarshal;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class CARARE2CARARERDFTest {
    private CARARE2CARARERDF converter;
    private CarareWrap geneCarareArqui;
    private CarareWrap geneCarareArque;

    @Before
    public void setUp() {
        File geneArquiCarareFile = new File(getClass().getClassLoader().getResource("carare/Extraccio_bens_Arqueologics_29-06-2017_100.xml").getFile());
        JaxbUnmarshal carareArqui_unmarshall = new JaxbUnmarshal(geneArquiCarareFile, new Class[]{CarareWrap.class});
        geneCarareArqui = (CarareWrap) carareArqui_unmarshall.getObject();
        File geneArqueCarareFile = new File(getClass().getClassLoader().getResource("carare/Extraccio_bens_Arquitectonic_29-06-2017_100.xml").getFile());
        JaxbUnmarshal carareArque_unmarshall = new JaxbUnmarshal(geneArqueCarareFile, new Class[]{CarareWrap.class});
        geneCarareArque = (CarareWrap) carareArque_unmarshall.getObject();
        converter = new CARARE2CARARERDF();
    }

    @Test
    public void testArqueologia() {
        converter.convert(geneCarareArque);
    }

    @Test
    public void testArquitectura() {
        converter.convert(geneCarareArqui);
    }
}
