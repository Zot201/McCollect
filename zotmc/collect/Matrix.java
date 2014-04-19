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
import java.util.List;

//TODO: incomplete interface
/**
 * BiList with fixed width and height. Note that the {@code size()} should always return the same result
 * as {@code height() * width()}.
 * 
 * @author zot
 */
public interface Matrix<E> extends BiList<E> {
	
	public int height();
	
	public int width();
	
	public List<List<E>> rowList();
	
	
	/**
	 * @throws IndexOutOfBoundsException if either height or width is exceeded.
	 */
	@Override public E get(int rowIndex, int columnIndex);
	
	
	
	@Deprecated @Override public boolean add(E e);
	
	@Deprecated @Override public boolean addAll(Collection<? extends E> c);
	
	@Deprecated @Override public void clear();
	
	@Deprecated @Override public E remove(int rowIndex, int columnIndex);
	
	@Deprecated @Override public boolean remove(Object o);
	
	@Deprecated @Override public boolean removeAll(Collection<?> c);
	
	@Deprecated @Override public boolean retainAll(Collection<?> c);
	
}
