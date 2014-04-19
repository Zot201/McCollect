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

public class ImmutableBiListCell<E> extends AbstractBiListCell<E> {
	
	public static <E> ImmutableBiListCell<E> of(int rowIndex, int columnIndex, E element) {
		return new ImmutableBiListCell<E>(rowIndex, columnIndex, element);
	}
	
	
	private final int rowIndex, columnIndex;
	private final E element;
	private ImmutableBiListCell(int rowIndex, int columnIndex, E element) {
		this.rowIndex = rowIndex;
		this.columnIndex = columnIndex;
		this.element = element;
	}

	@Override public int getRowIndex() {
		return rowIndex;
	}
	@Override public int getColumnIndex() {
		return columnIndex;
	}
	@Override public E getElement() {
		return element;
	}

}
