package com.flipboard.goldengate;

import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;

public class BridgeParameter {

    public final String name;
    public final TypeMirror type;

    public BridgeParameter(VariableElement param) {
        this.name = param.getSimpleName().toString();
        this.type = param.asType();
    }

}
