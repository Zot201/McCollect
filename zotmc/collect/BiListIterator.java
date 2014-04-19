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

import java.util.Iterator;

//TODO: incomplete interface
public interface BiListIterator<E> extends Iterator<E> {

	public boolean hasPrevious();

	public E previous();

	public int nextRowIndex();

	public int previousRowIndex();

	public int nextColumnIndex();

	public int previousColumnIndex();

	public void set(E e);

	public void add(E e);
	
}