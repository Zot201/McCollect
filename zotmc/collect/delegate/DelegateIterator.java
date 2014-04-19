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

import java.util.Iterator;

public abstract class DelegateIterator<E> extends DelegateObject implements Iterator<E> {

	protected abstract Iterator<E> createDelegatee();
	
	protected Iterator<E> delegatee;
	protected Iterator<E> delegatee() {
		return delegatee != null ? delegatee : (delegatee = createDelegatee());
	}
	
	

	private boolean hasLast;
	protected boolean hasLast() {
		return hasLast;
	}
	private E last;
	protected E last() {
		if (!hasLast())
			throw new IllegalStateException();
		
		hasLast = false;
		return last;
	}
	protected E setLast(E last) {
		hasLast = true;
		return this.last = last;
	}
	@Override public E next() {
		return setLast(passNext());
	}
	
	
	
	@Override public boolean hasNext() {
		return delegatee().hasNext();
	}
	
	protected E passNext() {
		return delegatee().next();
	}
	
	
	
	@Override public void remove() {
		delegatee().remove();
	}
	
}
