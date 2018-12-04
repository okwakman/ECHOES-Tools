package org.EDM.Transformations.formats.utils;

import eu.carare.carareschema.CarareWrap;
import isbn._1_931666_22_9.Ead;
import nl.memorix_maior.api.rest._3.Memorix;
import nl.mindbus.a2a.A2AType;
import org.openarchives.oai._2_0.oai_dc.OaiDcType;

/**
 * @author amartinez
 */
public enum SchemaType {

    A2A(A2AType.class),
    DC(OaiDcType.class),
    MEMORIX(Memorix.class),
    EAD(Ead.class),
    CARARE(CarareWrap .class);

    private Class<?> Class;

    SchemaType(Class<?> type){
        this.Class = type;
    }

    public Class<?> getType(){
        return Class;
    }
}
