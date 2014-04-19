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

import static com.google.common.base.Predicates.equalTo;
import static zotmc.collect.Conversions.entryToKey;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import zotmc.collect.Conversions;
import zotmc.collect.StandardImpls;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterators;
import com.google.common.collect.Sets;

public abstract class IterativeMap<K, V> implements Map<K, V> {
	
	protected abstract Collection<Entry<K, V>> backing();
	
	
	protected Predicate<Entry<K, V>> keyEqualTo(K key) {
		return Predicates.compose(equalTo(key), Conversions.<K, V>entryToKey());
	}
	protected Predicate<Entry<K, V>> valueEqualTo(V value) {
		return Predicates.compose(equalTo(value), Conversions.<K, V>entryToValue());
	}

	
	@Override public void clear() {
		backing().clear();
	}
	@Override public boolean containsKey(Object key) {
		return keySet().contains(key);
	}
	
	@SuppressWarnings("unchecked")
	@Override public boolean containsValue(Object value) {
		return Iterators.any(
				entrySet().iterator(),
				valueEqualTo((V) value));
	}

	protected Set<Entry<K, V>> entrySet;
	@Override public Set<Entry<K, V>> entrySet() {
		return entrySet != null ? entrySet : (entrySet = new Set<Entry<K, V>>() {
			
			@Override public boolean add(Entry<K, V> e) {
				throw new UnsupportedOperationException();
			}
			@Override public boolean addAll(Collection<? extends Entry<K, V>> c) {
				throw new UnsupportedOperationException();
			}
			
			@Override public void clear() {
				backing().clear();
			}
			@Override public boolean isEmpty() {
				return backing().isEmpty();
			}
			
			@SuppressWarnings("unchecked")
			protected Entry<K, V> cast(Object obj) {
				return (Entry<K, V>) obj;
			}
			
			@Override public Iterator<Entry<K, V>> iterator() {
				final Set<K> passed = Sets.newHashSet();
				final Predicate<Entry<K, V>> keyNotInPassed = new Predicate<Entry<K, V>>() {
					@Override public boolean apply(Entry<K, V> input) {
						return !passed.contains(input.getKey());
					}
				};
				
				return new DelegateIterator<Entry<K, V>>() {
					{
						delegatee = createDelegatee();
					}
					@Override protected Iterator<Entry<K, V>> createDelegatee() {
						return Iterators.filter(backing().iterator(), keyNotInPassed);
					}
					@Override protected Iterator<Entry<K, V>> delegatee() {
						return delegatee;
					}
					@Override protected Entry<K, V> passNext() {
						Entry<K, V> next = super.passNext();
						passed.add(next.getKey());
						return next;
					}
					@Override public void remove() {
						IterativeMap.this.remove(last());
						delegatee = createDelegatee();
					}
				};
			}
			
			@Override public boolean contains(Object o) {
				Entry<K, V> entry = cast(o);
				try {
					for (Entry<K, V> b : backing())
						if (b.getKey().equals(entry.getKey()))
							return b.getValue().equals(entry.getValue());
				} catch (NullPointerException ignored) {
				} catch (ClassCastException ignored) { }
				
				return false;
			}
			@Override public boolean containsAll(Collection<?> c) {
				return Sets.newHashSet(this).containsAll(c);
			}
			
			@Override public boolean remove(Object o) {
				if (contains(o)) {
					IterativeMap.this.remove(cast(o).getKey());
					return true;
				}
				return false;
			}
			@Override public boolean removeAll(Collection<?> c) {
				return StandardImpls.CollectionImpl.removeAll(this, c);
			}
			@Override public boolean retainAll(Collection<?> c) {
				return StandardImpls.CollectionImpl.retainAll(this, c);
			}
			
			@Override public int size() {
				return Iterators.size(iterator());
			}
			
			@Override public Object[] toArray() {
				return StandardImpls.CollectionImpl.toArray(this);
			}
			@Override public <T> T[] toArray(T[] a) {
				return StandardImpls.CollectionImpl.toArray(this, a);
			}
			
			@Override public int hashCode() {
				return StandardImpls.SetImpl.hashCode(this);
			}
			@Override public boolean equals(Object obj) {
				return Sets.newHashSet(this).equals(obj);
			}
			@Override public String toString() {
				return StandardImpls.SetImpl.toString(this);
			}
		});
	}

	protected Set<K> keySet;
	@Override public Set<K> keySet() {
		final Set<Entry<K, V>> backing = entrySet();
		
		return keySet != null ? keySet : (keySet = new TransformedSet<Entry<K, V>, K>() {
			@Override protected Set<Entry<K, V>> backing() {
				return backing;
			}
			@Override protected Function<Entry<K, V>, K> transformation() {
				return entryToKey();
			}
			@Override public boolean add(K e) {
				throw new UnsupportedOperationException();
			}
			@Override public boolean contains(Object o) {
				return Iterators.any(
						IterativeMap.this.backing().iterator(),
						keyEqualTo(cast(o)));
			}
			@Override public boolean remove(Object o) {
				return Iterators.removeIf(
						IterativeMap.this.backing().iterator(),
						keyEqualTo(cast(o)));
			}
		});
	}
	
	protected Collection<V> values;
	@Override public Collection<V> values() {
		return values != null ? values : (values = Collections2.transform(entrySet(), Conversions.<K, V>entryToValue()));
	}
	

	@SuppressWarnings("unchecked")
	@Override public V get(Object key) {
		try {
			return Iterators.find(
					IterativeMap.this.backing().iterator(),
					keyEqualTo((K) key)
			).getValue();
		} catch (NoSuchElementException ignored) { }
		
		return null;
	}

	@Override public boolean isEmpty() {
		return backing().isEmpty();
	}

	@Override public void putAll(Map<? extends K, ? extends V> m) {
		StandardImpls.MapImpl.putAll(this, m);
	}

	@Override public V remove(Object key) {
		V ret = null;
		Iterator<Entry<K, V>> ite = backing().iterator();
		while (ite.hasNext()) {
			Entry<K, V> entry = ite.next();
			if (Objects.equal(entry.getKey(), key)) {
				if (ret != null)
					ret = entry.getValue();
				ite.remove();
			}
		}
		return ret;
	}

	@Override public int size() {
		return entrySet().size();
	}
	
	@Override public int hashCode() {
		return StandardImpls.MapImpl.hashCode(this);
	}
	@Override public boolean equals(Object obj) {
		return StandardImpls.MapImpl.equals(this, obj);
	}
	@Override public String toString() {
		return StandardImpls.MapImpl.toString(this);
	}

}
