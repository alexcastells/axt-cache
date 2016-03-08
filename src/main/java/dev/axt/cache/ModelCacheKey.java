package dev.axt.cache;

import java.io.Serializable;

/**
 * Cache entry key that should have a nice implementation of equals and hashcode
 * methods.
 *
 * @author alextremp
 * @param <T>
 */
public interface ModelCacheKey<T> extends Serializable {

}
