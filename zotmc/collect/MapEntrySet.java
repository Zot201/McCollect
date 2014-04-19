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
import java.util.Map.Entry;
import java.util.Set;

public interface MapEntrySet<K, V> extends Set<Entry<K, V>> {
	
	public boolean add(K key, V value);

	public boolean remove(K key, V value);
	
	public boolean containsKey(K key);

	public boolean containsValue(V value);

	public Collection<K> keys();

	public Collection<V> values();
	
	public void putAll(Collection<? extends Entry<? extends K, ? extends V>> c);
	
}
