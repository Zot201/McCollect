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

public abstract class DelegateCollection<E> extends DelegateObject implements Collection<E> {
	
	protected abstract Collection<E> delegatee();

	
	@Override public Iterator<E> iterator() {
		return delegatee().iterator();
	}
	protected class AnonymousIterator extends DelegateIterator<E> {
		public AnonymousIterator() { }
		@Override protected Iterator<E> createDelegatee() {
			return DelegateCollection.this.delegatee().iterator();
		}
	}
	
	
	@SuppressWarnings("unchecked") protected E cast(Object element) {
		return (E) element;
	}

	
	
	@Override public boolean add(E e) {
		return delegatee().add(e);
	}
	@Override public boolean addAll(Collection<? extends E> c) {
		return delegatee().addAll(c);
	}
	
	

	@Override public void clear() {
		delegatee().clear();
	}
	@Override public boolean remove(Object o) {
		return delegatee().remove(o);
	}
	@Override public boolean removeAll(Collection<?> c) {
		return delegatee().removeAll(c);
	}
	@Override public boolean retainAll(Collection<?> c) {
		return delegatee().retainAll(c);
	}
	
	

	@Override public boolean contains(Object o) {
		return delegatee().contains(o);
	}
	@Override public boolean containsAll(Collection<?> c) {
		return delegatee().containsAll(c);
	}
	@Override public boolean isEmpty() {
		return delegatee().isEmpty();
	}
	@Override public int size() {
		return delegatee().size();
	}
	
	

	@Override public Object[] toArray() {
		return delegatee().toArray();
	}
	@Override public <T> T[] toArray(T[] a) {
		return delegatee().toArray(a);
	}
	
	
}
