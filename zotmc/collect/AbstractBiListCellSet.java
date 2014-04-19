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

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import zotmc.collect.BiList.Cell;

import com.google.common.base.Objects;

abstract class AbstractBiListCellSet<E> implements Set<Cell<E>> {
	
	protected abstract BiList<E> biList();
	
	
	
	protected Cell<?> cast(Object cell) {
		return (Cell<?>) cell;
	}
	
	@Override public int size() {
		return biList().size();
	}
	@Override public boolean isEmpty() {
		return biList().isEmpty();
	}

	@Override public boolean contains(Object e) {
		try {
			Cell<?> cell = cast(e);
			return Objects.equal(
					biList().get(cell.getRowIndex(), cell.getColumnIndex()), cell.getElement());
		} catch (NullPointerException ignored) {
		} catch (ClassCastException ignored) { }
		return false;
	}
	
	@Override public Iterator<Cell<E>> iterator() {
		final BiListIterator<E> backing = biList().biListIterator();
		
		return new Iterator<Cell<E>>() {
			@Override public boolean hasNext() {
				return backing.hasNext();
			}
			@Override public Cell<E> next() {
				return ImmutableBiListCell.of(
						backing.nextRowIndex(), backing.nextColumnIndex(), backing.next());
			}
			@Override public void remove() {
				backing.remove();
			}
		};
	}

	@Override public Object[] toArray() {
		return StandardImpls.SetImpl.toArray(this);
	}
	@Override public <T> T[] toArray(T[] a) {
		return StandardImpls.SetImpl.toArray(this, a);
	}
	
	
	
	@Override public boolean add(Cell<E> e) {
		throw new UnsupportedOperationException();
	}
	@Override public boolean remove(Object e) {
		try {
			Cell<?> cell = cast(e);
			int r = cell.getRowIndex(), c = cell.getColumnIndex();
			return Objects.equal(biList().get(r, c), cell.getElement())
					&& biList().remove(r, c) != null;
		} catch (ClassCastException ignored) { }
		return false;
	}

	@Override public boolean containsAll(Collection<?> c) {
		return StandardImpls.SetImpl.containsAll(this, c);
	}
	@Override public boolean addAll(Collection<? extends Cell<E>> c) {
		return StandardImpls.SetImpl.addAll(this, c);
	}
	@Override public boolean retainAll(Collection<?> c) {
		return StandardImpls.SetImpl.retainAll(this, c);
	}
	@Override public boolean removeAll(Collection<?> c) {
		return StandardImpls.SetImpl.removeAll(this, c);
	}
	@Override public void clear() {
		biList().clear();
	}
	
	
	@Override public int hashCode() {
		return StandardImpls.SetImpl.hashCode(this);
	}
	@Override public boolean equals(Object obj) {
		return StandardImpls.SetImpl.equals(this, obj);
	}
	@Override public String toString() {
		return StandardImpls.SetImpl.toString(this);
	}
	
}
