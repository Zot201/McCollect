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

import zotmc.collect.ImmutableMapEntry;
import zotmc.collect.StandardImpls;
import zotmc.collect.MapSet;

import com.google.common.base.Objects;

public abstract class MapBackingSet<K, V> extends DelegateSet<Entry<K, V>> implements MapSet<K, V> {
	
	protected abstract Map<K, V> backing();
	
	
	@Override protected Set<Entry<K, V>> delegatee() {
		return backing().entrySet();
	}
	@Override public Map<K, V> asMap() {
		return backing();
	}
	
	@Override public boolean add(K key, V value) {
		return !Objects.equal(backing().put(key, value), value);
	}
	@Override public boolean add(Entry<K, V> e) {
		return add(e.getKey(), e.getValue());
	}
	@Override public V put(K key, V value) {
		return backing().put(key, value);
	}
	@Override public void putAll(Collection<? extends Entry<? extends K, ? extends V>> c) {
		StandardImpls.MapSetImpl.putAll(this, c);
	}
	@Override public boolean addAll(Collection<? extends Entry<K, V>> c) {
		return StandardImpls.SetImpl.addAll(this, c);
	}
	
	@Override public boolean remove(K key, V value) {
		return remove(ImmutableMapEntry.of(key, value));
	}
	
	@Override public V getValue(K key) {
		return backing().get(key);
	}
	@Override public V removeKey(K key) {
		return backing().remove(key);
	}

	@Override public boolean containsKey(K key) {
		return backing().containsKey(key);
	}
	@Override public boolean containsValue(V value) {
		return backing().containsValue(value);
	}
	@Override public Set<K> keys() {
		return backing().keySet();
	}
	@Override public Collection<V> values() {
		return backing().values();
	}

}
