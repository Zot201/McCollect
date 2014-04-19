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
import java.util.Map.Entry;
import java.util.Set;

import zotmc.collect.MapSet;

public abstract class DelegateMapSet<K, V> extends DelegateSet<Entry<K, V>> implements MapSet<K, V> {
	
	@Override protected abstract MapSet<K, V> delegatee();
	
	

	@Override public Collection<V> values() {
		return delegatee().values();
	}
	protected class Values extends RemovalCollection<V> {
		public Values() { }
		@Override protected Collection<V> delegatee() {
			return DelegateMapSet.this.delegatee().values();
		}
	}
	

	@Override public Map<K, V> asMap() {
		return delegatee().asMap();
	}
	protected class AsMap extends RemovalMap<K, V> {
		public AsMap() { }
		@Override protected Map<K, V> delegatee() {
			return DelegateMapSet.this.delegatee().asMap();
		}
	}
	

	@Override public Set<K> keys() {
		return delegatee().keys();
	}
	protected class Keys extends RemovalSet<K> {
		public Keys() { }
		@Override protected Set<K> delegatee() {
			return DelegateMapSet.this.delegatee().keys();
		}
	}
	
	
	@Override public boolean add(K key, V value) {
		return delegatee().add(key, value);
	}
	@Override public boolean remove(K key, V value) {
		return delegatee().remove(key, value);
	}
	@Override public boolean containsKey(K key) {
		return delegatee().containsKey(key);
	}
	@Override public boolean containsValue(V value) {
		return delegatee().containsValue(value);
	}
	@Override public void putAll(Collection<? extends Entry<? extends K, ? extends V>> c) {
		delegatee().putAll(c);
	}
	@Override public V put(K key, V value) {
		return delegatee().put(key, value);
	}
	@Override public V getValue(K key) {
		return delegatee().getValue(key);
	}
	@Override public V removeKey(K key) {
		return delegatee().removeKey(key);
	}


}
