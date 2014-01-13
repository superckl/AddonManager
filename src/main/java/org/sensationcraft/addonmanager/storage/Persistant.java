package org.sensationcraft.addonmanager.storage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotate a field with this if it should persist through reloads.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Persistant {

	String key();
	Class<?> instantiationType();
	boolean reloadOnly() default false;

}
