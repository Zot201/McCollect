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
import java.util.List;
import java.util.ListIterator;

public abstract class DelegateList<E> extends DelegateCollection<E> implements List<E> {

	@Override protected abstract List<E> delegatee();
	
	
	
	
	@Override public ListIterator<E> listIterator() {
		return delegatee().listIterator();
	}
	@Override public ListIterator<E> listIterator(int index) {
		return delegatee().listIterator(index);
	}
	protected class AnonymousListIterator extends DelegateListIterator<E> {
		public AnonymousListIterator() {
			delegatee = createDelegatee();
		}
		@Override protected ListIterator<E> createDelegatee() {
			return DelegateList.this.delegatee().listIterator();
		}
		
		public AnonymousListIterator(int index) {
			delegatee = createDelegatee(index);
		}
		protected ListIterator<E> createDelegatee(int index) {
			return DelegateList.this.delegatee().listIterator(index);
		}
		
		@Override protected ListIterator<E> delegatee() {
			return delegatee;
		}
	}
	
	
	
	@Override public void add(int index, E e) {
		delegatee().add(index, e);
	}
	@Override public boolean addAll(int index, Collection<? extends E> c) {
		return delegatee().addAll(index, c);
	}
	@Override public E get(int index) {
		return delegatee().get(index);
	}
	@Override public int indexOf(Object e) {
		return delegatee().indexOf(e);
	}
	@Override public int lastIndexOf(Object e) {
		return delegatee().lastIndexOf(e);
	}
	@Override public E remove(int index) {
		return delegatee().remove(index);
	}
	@Override public E set(int index, E e) {
		return delegatee().set(index, e);
	}
	@Override public List<E> subList(int from, int to) {
		return delegatee().subList(from, to);
	}

}
