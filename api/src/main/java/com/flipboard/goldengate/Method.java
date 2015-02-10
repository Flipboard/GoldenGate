package com.flipboard.goldengate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a method within a @Bridge annotated interface with this annotation to specify a different name for the
 * javascript method then the name of the method on the java side.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Method {
    String value();
}
