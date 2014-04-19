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
import java.util.Set;

import zotmc.collect.StandardImpls;

import com.google.common.collect.ImmutableList;

public abstract class DisjointUnion<E> extends ConcatCollection<E> implements Set<E> {
	
	public static <E> DisjointUnion<E> of(final Set<E>... backing) {
		return of(ImmutableList.copyOf(backing));
	}
	public static <E> DisjointUnion<E> of(final Iterable<? extends Set<E>> backing) {
		return new DisjointUnion<E>() {
			@Override protected Iterable<? extends Set<E>> backing() {
				return backing;
			}
		};
	}
	

	protected abstract Iterable<? extends Set<E>> backing();

	@SuppressWarnings("unchecked") protected E cast(Object element) {
		return (E) element;
	}

	@Override public boolean removeAll(Collection<?> c) {
		// use standard set implementation, expecting delegatee().size() is small i.e. size and remove are fast
		return StandardImpls.SetImpl.removeAll(this, c);
	}
	
	@Override public int hashCode() {
		int h = 0;
		for (Set<?> set : backing())
			h += set.hashCode();
		return h;
	}
	@Override public boolean equals(Object obj) {
		return StandardImpls.SetImpl.equals(this, obj);
	}
	
}
