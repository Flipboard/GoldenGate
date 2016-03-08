package com.flipboard.goldengate;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by emilsjolander on 2/2/15.
 */
public class Util {
    public static boolean isCallback(Element e) {
        return Processor.instance.typeUtils.isSubtype(
                Processor.instance.typeUtils.erasure(e.asType()),
                Processor.instance.typeUtils.erasure(Processor.instance.elementUtils.getTypeElement(Callback.class.getCanonicalName()).asType())
        );
    }

    /**
     * Returns the {@link TypeMirror} for a given {@link Class}.
     *
     * Adapter from https://github.com/typetools/checker-framework/
     */
    public static TypeMirror typeFromClass(Types types, Elements elements, Class<?> clazz) {
        if (clazz == void.class) {
            return types.getNoType(TypeKind.VOID);
        } else if (clazz.isPrimitive()) {
            String primitiveName = clazz.getName().toUpperCase();
            TypeKind primitiveKind = TypeKind.valueOf(primitiveName);
            return types.getPrimitiveType(primitiveKind);
        } else if (clazz.isArray()) {
            TypeMirror componentType = typeFromClass(types, elements, clazz.getComponentType());
            return types.getArrayType(componentType);
        } else {
            TypeElement element = elements.getTypeElement(clazz.getCanonicalName());
            if (element == null) {
                throw new IllegalArgumentException("Unrecognized class: " + clazz);
            }
            return element.asType();
        }
    }
}
