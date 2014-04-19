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

import java.util.ListIterator;

public abstract class DelegateListIterator<E> extends DelegateObject implements ListIterator<E> {
	
	protected abstract ListIterator<E> createDelegatee();
	
	protected ListIterator<E> delegatee;
	@Override protected ListIterator<E> delegatee() {
		return delegatee != null ? delegatee : (delegatee = createDelegatee());
	}
	

	
	@Override public boolean hasPrevious() {
		return delegatee().hasPrevious();
	}
	@Override public boolean hasNext() {
		return delegatee().hasNext();
	}
	private boolean hasLast;
	protected boolean hasLast() {
		return hasLast;
	}
	protected E setLast(E last) {
		hasLast = true;
		return this.last = last;
	}
	

	@Override public int previousIndex() {
		return delegatee().previousIndex();
	}
	@Override public int nextIndex() {
		return delegatee().nextIndex();
	}
	
	
	protected E passPrevious() {
		return delegatee().previous();
	}
	protected E passNext() {
		return delegatee().next();
	}
	
	
	@Override public E previous() {
		return setLast(passPrevious());
	}
	@Override public E next() {
		return setLast(passNext());
	}
	private E last;
	protected E last() {
		if (!hasLast())
			throw new IllegalStateException();
		
		hasLast = false;
		return last;
	}
	

	@Override public void add(E e) {
		delegatee().add(e);
	}
	@Override public void set(E e) {
		delegatee().set(e);
	}
	@Override public void remove() {
		delegatee().remove();
	}

}
