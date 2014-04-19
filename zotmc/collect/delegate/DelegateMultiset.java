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

import java.util.Set;

import com.google.common.collect.Multiset;

public abstract class DelegateMultiset<E> extends DelegateCollection<E> implements Multiset<E> {
	
	@Override protected abstract Multiset<E> delegatee();
	

	@Override public Set<E> elementSet() {
		return delegatee().elementSet();
	}
	protected class ElementSet extends RemovalSet<E> {
		public ElementSet() { }
		@Override protected Set<E> delegatee() {
			return DelegateMultiset.this.delegatee().elementSet();
		}
	}
	
	
	

	@Override public Set<Entry<E>> entrySet() {
		return delegatee().entrySet();
	}
	protected class EntrySet extends RemovalSet<Entry<E>> {
		public EntrySet() { }
		@Override protected Set<Entry<E>> delegatee() {
			return DelegateMultiset.this.delegatee().entrySet();
		}
	}
	
	
	
	
	
	
	@Override public int add(E element, int occurrences) {
		return delegatee().add(element, occurrences);
	}
	@Override public int remove(Object element, int occurrences) {
		return delegatee().remove(element, occurrences);
	}
	@Override public int setCount(E element, int count) {
		return delegatee().setCount(element, count);
	}
	
	
	
	@Override public boolean setCount(E element, int oldCount, int newCount) {
		return delegatee().setCount(element, oldCount, newCount);
	}
	
	
	
	
	
	
	@Override public int count(Object element) {
		return delegatee().count(element);
	}

}
