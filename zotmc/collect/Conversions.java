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

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;

/**
 * Conversions provide a live view of the input object where possible. 
 * 
 * @author zot
 */
public class Conversions {

	public static <K, V> Function<Entry<K, V>, K> entryToKey() {
		return castRaw(ENTRY_TO_KEY);
	}
	public static <K, V> Function<Entry<K, V>, K> entryToKey(K k, V v) {
		return castRaw(ENTRY_TO_KEY);
	}
	private static Function<Entry<?, ?>, Object> ENTRY_TO_KEY = new Function<Entry<?, ?>, Object>() {
		@Override public Object apply(Entry<?, ?> input) {
			return input.getKey();
		}
	};

	public static <K, V> Function<Entry<K, V>, V> entryToValue() {
		return castRaw(ENTRY_TO_VALUE);
	}
	public static <K, V> Function<Entry<K, V>, V> entryToValue(K k, V v) {
		return castRaw(ENTRY_TO_VALUE);
	}
	private static Function<Entry<?, ?>, Object> ENTRY_TO_VALUE = new Function<Entry<?, ?>, Object>() {
		@Override public Object apply(Entry<?, ?> input) {
			return input.getValue();
		}
	};
	

	
	private static class KeyAlteredEntry<F, K> extends AbstractMapEntry<K, Object> {
		final Entry<F, ?> input;
		final Function<F, K> function;
		public KeyAlteredEntry(Entry<F, ?> input, Function<F, K> function) {
			this.input = input;
			this.function = function;
		}
		@Override public K getKey() {
			return function.apply(input.getKey());
		}
		@Override public Object getValue() {
			return input.getValue();
		}
	}
	
	public static <F, K> Function<Entry<F, ?>, Entry<K, ?>> entryAlterKey(final Function<F, K> function) {
		return new Function<Map.Entry<F, ?>, Map.Entry<K, ?>>() {
			@Override public Entry<K, ?> apply(final Entry<F, ?> input) {
				return new KeyAlteredEntry<F, K>(input, function);
			}
		};
	}
	public static <F, K, V> Function<Entry<F, V>, Entry<K, V>> entryAlterKey(Function<F, K> function, V v) {
		return castRaw(entryAlterKey(function));
	}
	
	public static <F, K> Commuter<Entry<F, ?>, Entry<K, ?>> entryAlterkey(final Commuter<F, K> function) {
		return new Commuter<Map.Entry<F, ?>, Map.Entry<K, ?>>() {
			// live view
			@Override public Entry<K, ?> apply(final Entry<F, ?> input) {
				return new KeyAlteredEntry<F, K>(input, function);
			}
			// fix view
			@Override public Entry<F, ?> disapply(Entry<K, ?> output) throws ReverseOperationException {
				return ImmutableMapEntry.of(function.disapply(output.getKey()), output.getValue());
			}
		};
	}
	public static <F, T, V> Commuter<Entry<F, V>, Entry<T, V>> entryAlterKey(Commuter<F, T> function, V v) {
		return castRaw(entryAlterkey(function));
	}
	
	
	
	
	
	public static <E> Commuter<List<E>, List<E>> listReverse() {
		return castRaw(LIST_REVERSE);
	}
	private static final Commuter<List<?>, List<?>> LIST_REVERSE = new Commuter<List<?>, List<?>>() {
		@Override public List<?> apply(List<?> input) {
			return Lists.reverse(input);
		}
		@Override public List<?> disapply(List<?> output) {
			return Lists.reverse(output);
		}
	};
	
	
	
	public static <E> Function<?, E> constant(E value) {
		return Functions.constant(value);
	}
	public static <F, E> Function<F, E> constant(E value, Class<F> f) {
		return castRaw(constant(value));
	}
	
	
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <F, T> Function<F, T> castRaw(Function function) {
		return function;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Predicate<T> castRaw(Predicate predicate) {
		return predicate;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <F, T> Commuter<F, T> castRaw(Commuter biFunction) {
		return biFunction;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> Comparator<T> castRaw(Comparator comparator) {
		return comparator;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <E> Set<E> castRaw(Set set) {
		return set;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <E> List<E> castRaw(List list) {
		return list;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <E> List<E> castRaw(List list, Class<E> e) {
		return list;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <E> Iterable<E> castRaw(Iterable iterable) {
		return iterable;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K, V> Map<K, V> castRaw(Map map) {
		return map;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K, V> Map<K, V> castRaw(Map map, Class<K> k, Class<V> v) {
		return map;
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K, V> Entry<K, V> castRaw(Entry entry) {
		return entry;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T castObject(Object object) {
		return (T) object;
	}
	
}
