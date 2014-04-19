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

import zotmc.collect.BiList.Cell;

import com.google.common.base.Objects;

public abstract class AbstractBiListCell<E> implements Cell<E> {
	
	@Override public int hashCode() {
		return Objects.hashCode(getRowIndex(), getColumnIndex(), getElement());
	}
	
	@Override public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof Cell) {
			Cell<?> o = (Cell<?>) obj;
			return o.getRowIndex() == getRowIndex() && o.getColumnIndex() == getColumnIndex()
					&& Objects.equal(getElement(), o.getElement());
		}
		return false;
	}
	
	@Override public String toString() {
		return "(" + getRowIndex() + "," + getColumnIndex() + ")=" + getElement();
	}

}
