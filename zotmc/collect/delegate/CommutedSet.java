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

import java.util.Set;

import zotmc.collect.Commuter;
import zotmc.collect.Commuter.ReverseOperationException;

public abstract class CommutedSet<F, E> extends TransformedSet<F, E> {
	
	public static <F, E> CommutedSet<F, E> of(
			final Set<F> backing, final Commuter<F, E> transformation) {
		return new CommutedSet<F, E>() {
			@Override protected Set<F> backing() {
				return backing;
			}
			@Override protected Commuter<F, E> transformation() {
				return transformation;
			}
		};
	}
	

	
	protected abstract Commuter<F, E> transformation();
	
	/**
	 * @throws IllegalArgumentException if a {@link ReverseOperationException} is thrown
	 * by the transformation
	 */
	@Override public boolean add(E e) {
		try {
			return backing().add(transformation().disapply(e));
		} catch (ReverseOperationException roe) {
			throw new IllegalArgumentException(roe);
		}
	}
	
	@Override public boolean remove(Object o) {
		try {
			return backing().remove(transformation().disapply(cast(o)));
		} catch (NullPointerException ignored) {
		} catch (ClassCastException ignored) {
		} catch (ReverseOperationException ignored) { }
		
		return false;
	}
	
	@Override public boolean contains(Object o) {
		try {
			return backing().contains(transformation().disapply(cast(o)));
		} catch (NullPointerException ignored) {
		} catch (ClassCastException ignored) {
		} catch (ReverseOperationException ignored) { }
		
		return false;
	}
	
}
