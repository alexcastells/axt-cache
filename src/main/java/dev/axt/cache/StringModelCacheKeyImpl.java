package dev.axt.cache;

/**
 * String based cache key
 *
 * @author alextremp
 */
public class StringModelCacheKeyImpl implements ModelCacheKey<String> {

	private String key;

	public StringModelCacheKeyImpl() {
	}

	public StringModelCacheKeyImpl(String key) {
		this.key = key;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 17 * hash + (this.key != null ? this.key.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final StringModelCacheKeyImpl other = (StringModelCacheKeyImpl) obj;
		return !((this.key == null) ? (other.key != null) : !this.key.equals(other.key));
	}

	@Override
	public String toString() {
		return "StringModelCacheKeyImpl[" + "key=" + key + ']';
	}

}
