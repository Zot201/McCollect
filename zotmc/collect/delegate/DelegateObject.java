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

public abstract class DelegateObject {
	
	protected abstract Object delegatee();

	
	@Override public int hashCode() {
		return delegatee().hashCode();
	}
	
	@Override public boolean equals(Object obj) {
		return delegatee().equals(obj);
	}
	
	@Override public String toString() {
		return delegatee().toString();
	}

}
