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

import java.util.Map;

import zotmc.collect.Commuter.ReverseOperationException;
import zotmc.collect.delegate.DelegateMap;
import zotmc.collect.delegate.MapBackingSet;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

//TODO: stub
public class FluentMap<K, V> extends DelegateMap<K, V> {
	
	public static final <K, V> FluentMap<K, V> from(Map<K, V> map) {
		return new FluentMap<K, V>(map);
	}
	
	
	private FluentMap(Map<K, V> delegatee) {
		this.delegatee = delegatee;
	}
	protected final Map<K, V> delegatee;
	@Override protected Map<K, V> delegatee() {
		return delegatee;
	}
	
	
	public FluentMap<K, V> filterKeys(Predicate<? super K> keyPredicate) {
		return from(Maps.filterKeys(delegatee(), keyPredicate));
	}
	public FluentMap<K, V> filterValues(Predicate<? super V> valuePredicate) {
		return from(Maps.filterValues(delegatee(), valuePredicate));
	}
	public <W> FluentMap<K, W> transformValues(Function<? super V, W> function) {
		return from(Maps.transformValues(delegatee(), function));
	}
	public <W> FluentMap<K, W> commuteValues(final Commuter<V, W> function) {
		final Map<K, W> delegatee = Maps.transformValues(delegatee(), function);
		
		return from(new DelegateMap<K, W>() {
			@Override protected Map<K, W> delegatee() {
				return delegatee;
			}
			@Override public W put(K key, W value) {
				try {
					return function.apply(FluentMap.this.delegatee().put(key, function.disapply(value)));
				} catch (ReverseOperationException e) {
					throw new IllegalArgumentException(e);
				}
			}
		});
	}
	

}
