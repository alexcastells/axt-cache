package dev.axt.cache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic Cache with data recovery callback built on ehcache.
 *
 * @author alextremp
 * @param <T> Type of the data to store
 * @param <K> Type of the key that identifies the data entries to store
 */
public abstract class AbstractModelCache<T, K extends ModelCacheKey> {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractModelCache.class);

	private static final long DEFAULT_timeToLiveSeconds = 60 * 60 * 8;

	private static final int DEFAULT_maxElementsInMemory = 1000;

	private static final String CACHE_PREFIX = "CACHE::";

	private final String cacheName;

	/**
	 *
	 * @param name Cache name, id in ehcache
	 */
	public AbstractModelCache(String name) {
		this(name, null, null);
	}

	/**
	 *
	 * @param name Cache name, id in ehcache
	 * @param configTimeToLive Time to live in seconds of stored data, 8h by
	 * default
	 * @param configMaxElements Max entries to mantain stored, 1000 by default
	 */
	public AbstractModelCache(String name, Long configTimeToLive, Integer configMaxElements) {
		if (name == null || name.isEmpty()) {
			throw new IllegalArgumentException("Cache name can't be empty");
		}
		cacheName = CACHE_PREFIX + name;
		Cache cache = getCache();
		if (cache == null) {
			addCache(configTimeToLive, configMaxElements);
		}
	}

	public final String getCacheName() {
		return cacheName;
	}

	private Cache getCache() {
		return CacheManager.getInstance().getCache(getCacheName());
	}

	private synchronized void addCache(Long configTimeToLive, Integer configMaxElements) {
		Cache cache = getCache();
		if (cache == null) {
			LOG.info("INIT " + getCacheName());
			long ttl = configTimeToLive != null && configTimeToLive > 0 ? configTimeToLive : DEFAULT_timeToLiveSeconds;

			int meim = configMaxElements != null && configMaxElements > 0 ? configMaxElements : DEFAULT_maxElementsInMemory;

			LOG.info(String.format("CONFIG [%s]: [timeToLiveSeconds=%s] [maxElementsInMemory=%s]", getCacheName(), ttl, meim));
			cache = new Cache(getCacheName(), meim, false, false, ttl, ttl / 2);
			CacheManager.getInstance().addCache(cache);
		}
	}

	public final synchronized void reset() {
		getCache().removeAll();
	}

	/**
	 * Get a cache entry from it's key, or calls the recovery method if data is
	 * not found.
	 *
	 * @param key
	 * @return
	 */
	public final T getEntry(K key) {
		if (key == null) {
			throw new IllegalArgumentException("Key cannot be null");
		}
		Cache cache = getCache();
		Element element = cache.get(key);
		ModelCacheEntry<T> entry;
		if (element != null) {
			entry = (ModelCacheEntry<T>) element.getObjectValue();
			if (LOG.isDebugEnabled()) {
				LOG.debug(">>> RECOVERED " + key);
			}
		} else {
			LOG.info(">>> RECOVERING " + key);
			T data = load(key);
			entry = new ModelCacheEntry<T>(key, data);
			element = new Element(key, entry);
			if (cache.putIfAbsent(element) == null) {
				LOG.info(String.format(">>> CACHED data [%s]=>[%s]", key, data));
			} else {
				LOG.info(String.format(">>> was already CACHED data [%s]=>[%s]", key, data));
			}
		}
		return entry.getData();
	}

	/**
	 * Data recovery method that will be called if a key has'nt a related entry
	 * stored in the cache.
	 *
	 * @param key Clave que identifica la entrada de datos a recuperar.
	 * @return datos a cachear.
	 */
	protected abstract T load(K key);

}
