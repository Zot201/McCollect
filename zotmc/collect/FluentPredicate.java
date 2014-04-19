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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

//TODO: stub
public abstract class FluentPredicate<T> implements Predicate<T> {
	
	public static <T> FluentPredicate<T> from(final Predicate<T> predicate) {
		if (predicate instanceof FluentPredicate)
			return (FluentPredicate<T>) predicate;
		
		return new FluentPredicate<T>() {
			@Override public boolean apply(T input) {
				return predicate.apply(input);
			}
			@Override protected Predicate<T> unwrap() {
				return predicate;
			}
			@Override public int hashCode() {
				return predicate.hashCode();
			}
			@Override public String toString() {
				return predicate.toString();
			}
		};
	}
	
	protected Predicate<T> unwrap() {
		return this;
	}

	public FluentPredicate<T> and(Predicate<? super T> other) {
		return from(Predicates.and(unwrap(), other));
	}
	public FluentPredicate<T> or(Predicate<? super T> other) {
		return from(Predicates.or(unwrap(), other));
	}
	public static <T> FluentPredicate<T> in(Collection<? extends T> target) {
		return from(Predicates.in(target));
	}
	
	
	

	public static <T> WildcardPredicate<T> notNull() {
		return NOT_NULL.cast();
	}
	private static final WildcardPredicate<?> NOT_NULL = WildcardPredicate.of(Predicates.notNull());

	public static <T> WildcardPredicate<T> alwaysTrue() {
		return ALWAYS_TRUE.cast();
	}
	private static final WildcardPredicate<?> ALWAYS_TRUE = WildcardPredicate.of(Predicates.alwaysTrue());
	
	public static abstract class WildcardPredicate<T> implements Predicate<T> {

		private static <T> WildcardPredicate<T> of(final Predicate<T> predicate) {
			return new WildcardPredicate<T>() {
				@Override public boolean apply(T input) {
					return predicate.apply(input);
				}
				@Override protected Predicate<T> unwrap() {
					return predicate;
				}
				@Override public int hashCode() {
					return predicate.hashCode();
				}
				@Override public String toString() {
					return predicate.toString();
				}
			};
		}
		
		protected Predicate<T> unwrap() {
			return this;
		}
		
		@SuppressWarnings("hiding")
		public <T> FluentPredicate<T> and(Predicate<? super T> other) {
			return from(Predicates.<T>and(cast().unwrap(), other));
		}
		@SuppressWarnings("hiding")
		public <T> FluentPredicate<T> or(Predicate<? super T> other) {
			return from(Predicates.<T>or(cast().unwrap(), other));
		}
		@SuppressWarnings({ "hiding", "unchecked" })
		public <T> WildcardPredicate<T> cast() {
			return (WildcardPredicate<T>) this;
		}
		
	}

}
