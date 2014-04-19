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
import java.util.Set;

import zotmc.collect.StandardImpls;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

public abstract class TransformedSet<F, E> implements Set<E> {
	
	protected abstract Set<F> backing();
	
	protected abstract Function<F, E> transformation();
	
	
	
	@SuppressWarnings("unchecked") protected E cast(Object element) {
		return (E) element;
	}
	
	
	@Override public Iterator<E> iterator() {
		return Iterators.transform(backing().iterator(), transformation());
	}
	protected abstract class AnonymousIterator extends TransformedIterator<F, E> {
		public AnonymousIterator() { }
		@Override protected Function<F, E> transformation() {
			return TransformedSet.this.transformation();
		}
		@Override protected Iterator<F> createBacking() {
			return TransformedSet.this.backing().iterator();
		}
	}
	
	
	@Override public boolean addAll(Collection<? extends E> c) {
		return StandardImpls.CollectionImpl.addAll(this, c);
	}
	

	@Override public void clear() {
		backing().clear();
	}
	@Override public boolean removeAll(Collection<?> c) {
		return StandardImpls.SetImpl.removeAll(this, c);
	}
	@Override public boolean retainAll(Collection<?> c) {
		return StandardImpls.SetImpl.retainAll(this, c);
	}
	
	
	@Override public boolean isEmpty() {
		return backing().isEmpty();
	}
	@Override public int size() {
		return backing().size();
	}
	@Override public boolean containsAll(Collection<?> c) {
		return StandardImpls.CollectionImpl.containsAll(this, c);
	}
	

	@Override public Object[] toArray() {
		return StandardImpls.CollectionImpl.toArray(this);
	}
	@Override public <T> T[] toArray(T[] a) {
		return StandardImpls.CollectionImpl.toArray(this, a);
	}
	
	
	@Override public int hashCode() {
		return StandardImpls.SetImpl.hashCode(this);
	}
	@Override public boolean equals(Object o) {
		return StandardImpls.SetImpl.equals(this, o);
	}
	@Override public String toString() {
		return StandardImpls.CollectionImpl.toString(this);
	}
	
}
