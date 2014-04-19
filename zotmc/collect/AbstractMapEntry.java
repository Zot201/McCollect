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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Objects.firstNonNull;

import java.util.Map.Entry;

public abstract class AbstractMapEntry<K, V> implements Entry<K, V> {
	
	@Override public V setValue(V value) {
		throw new UnsupportedOperationException();
	}
	
	@Override public int hashCode() {
		return firstNonNull(getKey(), 0).hashCode() ^ firstNonNull(getValue(), 0).hashCode();
	}
	
	@Override public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof Entry) {
			Entry<?, ?> o = (Entry<?, ?>) obj;
			return equal(getKey(), o.getKey()) && equal(getValue(), o.getValue());
		}
		return false;
	}
	
	@Override public String toString() {
		return getKey() + "=" + getValue();
	}
	
}
