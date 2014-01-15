package org.sensationcraft.addonmanager.addon.dependencies;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @deprecated Tell them to make an Addon version! Plugins and Addons don't play well together.
 */
@Deprecated
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PluginDepends {

}
