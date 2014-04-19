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
import java.util.Set;

//TODO: incomplete interface
/**
 * Biaxial List.
 * 
 * @author zot
 */
public interface BiList<E> extends Collection<E> {
	
	public E get(int rowIndex, int columnIndex);
	
	public E set(int rowIndex, int columnIndex, E e);
	
	public E remove(int rowIndex, int columnIndex);
	
	public Set<Cell<E>> cellSet();
	
	public BiListIterator<E> biListIterator();
	
	
	public interface Cell<E> {
		
		public int getRowIndex();
		
		public int getColumnIndex();
		
		public E getElement();
		
	}
	
}
