package com.flipboard;

import javax.lang.model.element.Element;

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
}
