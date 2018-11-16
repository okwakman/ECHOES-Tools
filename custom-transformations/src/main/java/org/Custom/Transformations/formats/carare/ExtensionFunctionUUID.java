package org.Custom.Transformations.formats.carare;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.StringValue;

import java.util.UUID;

public class ExtensionFunctionUUID extends ExtensionFunctionDefinition {

    String uuid;
    public ExtensionFunctionUUID(){
        uuid = UUID.randomUUID().toString();
    }

    @Override
    public StructuredQName getFunctionQName() {
        return new StructuredQName("util", "http://java.net/util", "uuid");
    }

    @Override
    public SequenceType[] getArgumentTypes() {
        return new SequenceType[] { SequenceType.SINGLE_STRING, SequenceType.SINGLE_STRING };
    }

    @Override
    public SequenceType getResultType(SequenceType[] sequenceTypes) {
        return SequenceType.SINGLE_STRING;
    }

    @Override
    public ExtensionFunctionCall makeCallExpression() {
        return new ExtensionFunctionCall() {
            @Override
            public Sequence call(XPathContext xPathContext, Sequence[] sequences) throws XPathException {
                String localName = sequences[0].toString().replace("\"", "");
                String id = uuid + ":" + sequences[1].toString().replace("\"", "");
                return new StringValue(localName + ":" + id);
            }
        };
    }


}
