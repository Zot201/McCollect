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

import java.util.Map.Entry;

public class ImmutableMapEntry<K, V> extends AbstractMapEntry<K, V> {
	
	public static <K, V> ImmutableMapEntry<K, V> of(K key, V value) {
		return new ImmutableMapEntry<K, V>(key, value);
	}
	public static <K, V> ImmutableMapEntry<K, V> copyOf(Entry<K, V> entry) {
		return entry instanceof ImmutableMapEntry ?
				(ImmutableMapEntry<K, V>) entry : of(entry.getKey(), entry.getValue());
	}
	
	
	private final K key;
	private final V value;
	private ImmutableMapEntry(K key, V value) {
		this.key = key;
		this.value = value;
	}
	@Override public K getKey() {
		return key;
	}
	@Override public V getValue() {
		return value;
	}

}
