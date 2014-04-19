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

package zotmc.collect;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

abstract class AbstractMapSetAsMap<K, V> implements Map<K, V> {
	
	protected abstract MapSet<K, V> mapSet();
	

	@SuppressWarnings("unchecked")
	protected K castKey(Object key) {
		return (K) key;
	}
	@SuppressWarnings("unchecked")
	protected V castValue(Object value) {
		return (V) value;
	}

	@Override public void clear() {
		mapSet().clear();
	}
	@Override public boolean containsKey(Object key) {
		try {
			return mapSet().containsKey(castKey(key));
		} catch (NullPointerException ignored) {
		} catch (ClassCastException ignored) { }
		
		return false;
	}
	@Override public boolean containsValue(Object value) {
		try {
			return mapSet().containsValue(castValue(value));
		} catch (NullPointerException ignored) {
		} catch (ClassCastException ignored) { }
		
		return false;
	}
	@Override public Set<Entry<K, V>> entrySet() {
		return mapSet();
	}
	@Override public V get(Object key) {
		try {
			return mapSet().getValue(castKey(key));
		} catch (NullPointerException ignored) {
		} catch (ClassCastException ignored) { }
		
		return null;
	}
	@Override public boolean isEmpty() {
		return mapSet().isEmpty();
	}
	@Override public Set<K> keySet() {
		return mapSet().keys();
	}
	@Override public V put(K key, V value) {
		return mapSet().put(key, value);
	}
	@Override public void putAll(Map<? extends K, ? extends V> m) {
		mapSet().putAll(m.entrySet());
	}
	@Override public V remove(Object key) {
		try {
			return mapSet().removeKey(castKey(key));
		} catch (NullPointerException ignored) {
		} catch (ClassCastException ignored) { }
		
		return null;
	}
	@Override public int size() {
		return mapSet().size();
	}
	@Override public Collection<V> values() {
		return mapSet().values();
	}
	@Override public int hashCode() {
		return StandardImpls.MapImpl.hashCode(this);
	}
	@Override public boolean equals(Object obj) {
		return StandardImpls.MapImpl.equals(this, obj);
	}
	@Override public String toString() {
		return StandardImpls.MapImpl.toString(this);
	}

}
