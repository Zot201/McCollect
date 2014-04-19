/*
 * Copyright (c) 2014, Zothf, All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package zotmc.collect.delegate;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class DelegateMap<K, V> extends DelegateObject implements Map<K, V> {
	
	protected abstract Map<K, V> delegatee();
	
	
	@Override public Set<Entry<K, V>> entrySet() {
		return delegatee().entrySet();
	}
	protected class EntrySet extends RemovalSet<Entry<K, V>> {
		public EntrySet() { }
		@Override protected Set<Entry<K, V>> delegatee() {
			return DelegateMap.this.delegatee().entrySet();
		}
	}

	@Override public Set<K> keySet() {
		return delegatee().keySet();
	}
	protected class KeySet extends RemovalSet<K> {
		public KeySet() { }
		@Override protected Set<K> delegatee() {
			return DelegateMap.this.delegatee().keySet();
		}
	}
	
	
	

	@Override public Collection<V> values() {
		return delegatee().values();
	}
	protected class Values extends RemovalCollection<V> {
		public Values() { }
		@Override protected Collection<V> delegatee() {
			return DelegateMap.this.delegatee().values();
		}
	}
	
	
	
	
	@SuppressWarnings("unchecked") protected K castKey(Object key) {
		return (K) key;
	}
	
	

	
	@Override public V put(K key, V value) {
		return delegatee().put(key, value);
	}
	@Override public void putAll(Map<? extends K, ? extends V> m) {
		delegatee().putAll(m);
	}
	
	
	

	@Override public void clear() {
		delegatee().clear();
	}
	@Override public V remove(Object key) {
		return delegatee().remove(key);
	}
	
	

	@Override public V get(Object key) {
		return delegatee().get(key);
	}
	

	@Override public boolean containsKey(Object key) {
		return delegatee().containsKey(key);
	}
	@Override public boolean containsValue(Object value) {
		return delegatee().containsKey(value);
	}
	@Override public boolean isEmpty() {
		return delegatee().isEmpty();
	}
	@Override public int size() {
		return delegatee().size();
	}

}
