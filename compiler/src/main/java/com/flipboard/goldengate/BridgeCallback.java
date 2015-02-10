package com.flipboard.goldengate;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

public class BridgeCallback {

    public final String name;
    public final TypeMirror type;
    public final TypeMirror genericType;

    public BridgeCallback(VariableElement e) {
        this.name = e.getSimpleName().toString();
        this.type = e.asType();
        this.genericType = ((DeclaredType) type).getTypeArguments().get(0);
    }

}
