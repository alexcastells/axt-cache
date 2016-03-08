package dev.axt.cache;

import java.io.Serializable;

/**
 * Cache entry
 *
 * @author alextremp
 * @param <T> cache entry data type to be stored
 */
public class ModelCacheEntry<T> implements Serializable {

	private final ModelCacheKey key;
	private final T data;

	public ModelCacheEntry(ModelCacheKey key, T data) {
		this.key = key;
		this.data = data;
	}

	/**
	 *
	 * @return get the key that identifies this data entry
	 */
	public ModelCacheKey getKey() {
		return key;
	}

	/**
	 *
	 * @return get the data stored in this entry
	 */
	public T getData() {
		return data;
	}

}
