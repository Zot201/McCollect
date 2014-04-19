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
import java.util.Iterator;

import zotmc.collect.StandardImpls;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

public abstract class ConcatCollection<E> implements Collection<E> {
	
	// mcp does not compile if the same 'of' is used here
	public static <E> ConcatCollection<E> af(final Collection<E>... backing) {
		return af(ImmutableList.copyOf(backing));
	}
	public static <E> ConcatCollection<E> af(final Iterable<? extends Collection<E>> backing) {
		return new ConcatCollection<E>() {
			@Override protected Iterable<? extends Collection<E>> backing() {
				return backing;
			}
		};
	}
	
	
	
	protected abstract Iterable<? extends Collection<E>> backing();
	
	protected Iterable<E> concatenated() {
		return Iterables.concat(backing());
	}
	
	@Override public Iterator<E> iterator() {
		return concatenated().iterator();
	}
	protected class AnonymousIterator extends DelegateIterator<E> {
		public AnonymousIterator() { }
		@Override protected Iterator<E> createDelegatee() {
			return concatenated().iterator();
		}
	}
	
	@SuppressWarnings("unchecked") protected E cast(Object element) {
		return (E) element;
	}

	
	@Override public boolean add(E e) {
		throw new UnsupportedOperationException();
	}
	@Override public boolean addAll(Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	
	@Override public void clear() {
		for (Collection<?> c : backing())
			c.clear();
	}
	@Override public boolean remove(Object o) {
		for (Collection<?> c : backing())
			if (c.remove(o))
				return true;
		return false;
	}
	@Override public boolean removeAll(Collection<?> c) {
		return StandardImpls.CollectionImpl.removeAll(this, c);
	}
	@Override public boolean retainAll(Collection<?> c) {
		return StandardImpls.CollectionImpl.retainAll(this, c);
	}
	

	@Override public boolean contains(Object o) {
		for (Collection<?> c : backing())
			if (c.contains(o))
				return true;
		return false;
	}
	@Override public boolean containsAll(Collection<?> c) {
		return StandardImpls.CollectionImpl.containsAll(this, c);
	}
	@Override public boolean isEmpty() {
		for (Collection<?> c : backing())
			if (!c.isEmpty())
				return false;
		return true;
	}
	@Override public int size() {
		int size = 0;
		for (Collection<?> c : backing())
			size += c.size();
		return size;
	}
	
	
	@Override public Object[] toArray() {
		return StandardImpls.CollectionImpl.toArray(this);
	}
	@Override public <T> T[] toArray(T[] a) {
		return StandardImpls.CollectionImpl.toArray(this, a);
	}
	
	@Override public String toString() {
		return StandardImpls.CollectionImpl.toString(this);
	}

}
