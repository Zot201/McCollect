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

import static zotmc.collect.Conversions.castRaw;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import zotmc.collect.delegate.CommutedSet;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

public class Matrixs { //possessive noun
	
	public static <E> UnmodifiableMatrix<E> unmodifiableMatrix(Matrix<E> matrix) {
		if (matrix instanceof UnmodifiableMatrix)
			return (UnmodifiableMatrix<E>) matrix;
		
		return new UnmodifiableMatrix<E>(matrix);
	}
	
	public static class UnmodifiableMatrix<E> implements Matrix<E> {
		private final Matrix<E> backing;
		public UnmodifiableMatrix(Matrix<E> backing) {
			this.backing = backing;
		}
		
		@Override public int size() {
			return backing.size();
		}
		@Override public boolean isEmpty() {
			return backing.isEmpty();
		}
		@Override public boolean contains(Object o) {
			return backing.contains(o);
		}
		@Override public Object[] toArray() {
			return backing.toArray();
		}
		@Override public <T> T[] toArray(T[] a) {
			return backing.toArray(a);
		}
		@Override public boolean containsAll(Collection<?> c) {
			return backing.containsAll(c);
		}
		@Override public int height() {
			return backing.height();
		}
		@Override public int width() {
			return backing.width();
		}
		@Override public E get(int rowIndex, int columnIndex) {
			return backing.get(rowIndex, columnIndex);
		}
		@Override public int hashCode() {
			return backing.hashCode();
		}
		@Override public boolean equals(Object obj) {
			return backing.equals(obj);
		}
		@Override public String toString() {
			return backing.toString();
		}
		
		
		@Override public Set<Cell<E>> cellSet() {
			return Collections.unmodifiableSet(backing.cellSet());
		}
		
		@Override public Iterator<E> iterator() {
			return Iterators.unmodifiableIterator(backing.iterator());
		}
		@Override public BiListIterator<E> biListIterator() {
			final BiListIterator<E> delegatee = backing.biListIterator();
			
			return new BiListIterator<E>() {
				@Override public boolean hasNext() {
					return delegatee.hasNext();
				}
				@Override public E next() {
					return delegatee.next();
				}
				@Override public boolean hasPrevious() {
					return delegatee.hasPrevious();
				}
				@Override public E previous() {
					return delegatee.previous();
				}
				@Override public int nextRowIndex() {
					return delegatee.nextRowIndex();
				}
				@Override public int previousRowIndex() {
					return delegatee.previousRowIndex();
				}
				@Override public int nextColumnIndex() {
					return delegatee.nextColumnIndex();
				}
				@Override public int previousColumnIndex() {
					return delegatee.previousColumnIndex();
				}

				@Override public void remove() {
					throw new UnsupportedOperationException();
				}
				@Override public void set(E e) {
					throw new UnsupportedOperationException();
				}
				@Override public void add(E e) {
					throw new UnsupportedOperationException();
				}
			};
		}
		
		private static final Function<List<?>, List<?>> WRAP_LIST = new Function<List<?>, List<?>>() {
			@Override public List<?> apply(List<?> input) {
				return Collections.unmodifiableList(input);
			}
		};
		protected Function<List<E>, List<E>> wrapList() {
			return castRaw(WRAP_LIST);
		}
		@Override public List<List<E>> rowList() {
			return Collections.unmodifiableList(Lists.transform(
					backing.rowList(), wrapList()));
		}
		
		
		@Deprecated @Override public E set(int rowIndex, int columnIndex, E e) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean add(E e) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean addAll(Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public void clear() {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public E remove(int rowIndex, int columnIndex) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean remove(Object o) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	
	
	public static <E> Matrix<E> horizontalMirror(Matrix<E> matrix) {
		if (matrix instanceof HorizontalMirror)
			return ((HorizontalMirror<E>) matrix).backing;
		
		return new HorizontalMirror<E>(matrix);
	}
	
	private static class HorizontalMirror<E> implements Matrix<E> {
		private final Matrix<E> backing;
		public HorizontalMirror(Matrix<E> backing) {
			this.backing = backing;
		}
		private int reverse(int columnIndex) {
			return width() - 1 - columnIndex;
		}
		
		
		@Override public E get(int rowIndex, int columnIndex) {
			return backing.get(rowIndex, reverse(columnIndex));
		}
		@Override public E set(int rowIndex, int columnIndex, E e) {
			return backing.set(rowIndex, reverse(columnIndex), e);
		}
		@Override public E remove(int rowIndex, int columnIndex) {
			return backing.remove(rowIndex, reverse(columnIndex));
		}

		private Set<Cell<E>> cellSet;
		@Override public Set<Cell<E>> cellSet() {
			return cellSet != null ? cellSet : (cellSet = CommutedSet.of(
					backing.cellSet(),
					new Commuter<Cell<E>, Cell<E>>() {
						@Override public Cell<E> apply(final Cell<E> input) {
							return new AbstractBiListCell<E>() {
								@Override public int getRowIndex() {
									return input.getRowIndex();
								}
								@Override public int getColumnIndex() {
									return reverse(input.getColumnIndex());
								}
								@Override public E getElement() {
									return input.getElement();
								}
							};
						}
						@Override public Cell<E> disapply(Cell<E> output) {
							return apply(output);
						}
					}));
		}
		
		
		@Override public boolean remove(Object o) {
			return StandardImpls.MatrixImpl.remove(this, o);
		}
		@Override public boolean removeAll(Collection<?> c) {
			return StandardImpls.MatrixImpl.removeAll(this, c);
		}
		@Override public boolean retainAll(Collection<?> c) {
			return StandardImpls.MatrixImpl.retainAll(this, c);
		}
		@Override public int hashCode() {
			return StandardImpls.MatrixImpl.hashCode(this);
		}
		@Override public boolean equals(Object obj) {
			return StandardImpls.MatrixImpl.equals(this, obj);
		}
		@Override public String toString() {
			return StandardImpls.MatrixImpl.toString(this);
		}
		@Override public Object[] toArray() {
			return StandardImpls.MatrixImpl.toArray(this);
		}
		@Override public <T> T[] toArray(T[] a) {
			return StandardImpls.MatrixImpl.toArray(this, a);
		}
		@Override public BiListIterator<E> biListIterator() {
			return StandardImpls.MatrixImpl.biListIterator(this);
		}
		@Override public Iterator<E> iterator() {
			return biListIterator();
		}
		
		
		@Override public boolean add(E e) {
			throw new UnsupportedOperationException();
		}
		@Override public boolean addAll(Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}
		
		
		
		private List<List<E>> rowList;
		@Override public List<List<E>> rowList() {
			return rowList != null ? rowList : (rowList = Lists.transform(
					backing.rowList(), Conversions.<E>listReverse()));
		}
		
		@Override public int size() {
			return backing.size();
		}
		@Override public boolean isEmpty() {
			return backing.isEmpty();
		}
		@Override public boolean contains(Object o) {
			return backing.contains(o);
		}
		@Override public boolean containsAll(Collection<?> c) {
			return backing.containsAll(c);
		}
		@Override public int height() {
			return backing.height();
		}
		@Override public int width() {
			return backing.width();
		}
		@Override public void clear() {
			backing.clear();
		}
		
		
	}

}
