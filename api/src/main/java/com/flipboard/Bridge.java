package com.flipboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate an interface with this annotation. The methods in the annotated interface serve as a communication bridge
 * to javascript running in a web view. An implementation of the javascript bridge will be generated from the interface
 * which was annotated with this annotation. The implementation will have the same name as your interface type but with
 * 'Bridge' appended to the end of the name.
 *
 * Methods within the interface can also be annotated with the @Method and @Property annotations for greater control
 * over what happens on the javascript side of things.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface Bridge { }
