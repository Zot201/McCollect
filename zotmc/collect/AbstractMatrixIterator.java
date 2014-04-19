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

import java.util.NoSuchElementException;

abstract class AbstractMatrixIterator<E> implements BiListIterator<E> {
	
	protected abstract Matrix<E> matrix();
	
	int cursor = 0, last = -1;
	
	@Override public boolean hasPrevious() {
		return cursor > 0;
	}
	@Override public boolean hasNext() {
		return cursor < matrix().size();
	}
	
	@Override public int previousRowIndex() {
		return cursor > 0 ? (cursor - 1) / matrix().width() : -1;
	}
	@Override public int previousColumnIndex() {
		return cursor > 0 ? (cursor - 1) % matrix().width() : matrix().width() - 1;
	}
	@Override public int nextRowIndex() {
		return cursor / matrix().width();
	}
	@Override public int nextColumnIndex() {
		return cursor % matrix().width();
	}
	
	@Override public E previous() {
		if (!hasPrevious())
			throw new NoSuchElementException();
		last = --cursor;
		return matrix().get(nextRowIndex(), nextColumnIndex());
	}
	@Override public E next() {
		if (!hasNext())
			throw new NoSuchElementException();
		last = cursor++;
		return matrix().get(previousRowIndex(), previousColumnIndex());
	}

	@Override public void add(E e) {
		throw new UnsupportedOperationException();
	}
	@Override public void set(E e) {
		matrix().set(last / matrix().width(), last % matrix().width(), e);
	}
	@Override public void remove() {
		throw new UnsupportedOperationException();
	}
	
	
}
