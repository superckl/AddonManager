package common.good.addonmanager.storage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotating your Addon class with this will cause AddonManager to look at all provided classes for persistant fields.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExtendPersistance {

	Class<?>[] classes();
}
