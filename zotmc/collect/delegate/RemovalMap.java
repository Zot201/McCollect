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

public abstract class RemovalMap<K, V> extends DelegateMap<K, V> {

	@Override public V put(K key, V value) {
		throw new UnsupportedOperationException();
	}
	@Override public void putAll(Map<? extends K, ? extends V> m) {
		throw new UnsupportedOperationException();
	}
	
	
	protected Set<K> keySet;
	@Override public Set<K> keySet() {
		return keySet != null ? keySet : (keySet = new KeySet());
	}
	
	protected Set<Entry<K, V>> entrySet;
	@Override public Set<Entry<K, V>> entrySet() {
		return entrySet != null ? entrySet : (entrySet = new EntrySet());
	}
	
	protected Collection<V> values;
	@Override public Collection<V> values() {
		return values != null ? values : (values = new Values());
	}
	
}
