package com.flipboard.goldengate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a method within a @Bridge annotated interface with this annotation to specify that this is not a method but
 * a property. This limits the method to be a simple parameter method. By default the name of this parameter is chosen
 * to be the name of the javascript property but a different name can be specified as a parameter to this annotation.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface Property {
    String value() default "";
}
