package com.flipboard.goldengate;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class BridgeCallback extends BridgeParameter {

    public final TypeMirror genericType;

    public BridgeCallback(VariableElement e) {
        super(e);
        this.genericType = ((DeclaredType) type).getTypeArguments().get(0);
    }

}
