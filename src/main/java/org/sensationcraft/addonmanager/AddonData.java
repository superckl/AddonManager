package org.sensationcraft.addonmanager;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stores information about an Addon. Annotate your main class with this.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AddonData
{
	String name();
	String version() default "";
	String[] authors() default {};
}
