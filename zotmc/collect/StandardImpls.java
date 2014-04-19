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

import static com.google.common.base.Preconditions.checkNotNull;
import static zotmc.collect.Conversions.castObject;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Table;
import com.google.common.primitives.Ints;

/**
 * Standard implementations for collections. 
 * 
 * @author zot
 */
public class StandardImpls {
	
	public static class CollectionImpl {

		/**
		 * Dependencies: {@link Collection#add(E)}
		 */
		public static <E> boolean addAll(Collection<E> collection, Collection<? extends E> c) {
			return Iterators.addAll(collection, c.iterator());
		}

		/**
		 * Dependencies: {@link Iterable#iterator()}
		 */
		public static boolean retainAll(Collection<?> collection, Collection<?> c) {
			return Iterators.retainAll(collection.iterator(), c);
		}

		/**
		 * Dependencies: {@link Collection#contains(Object)}
		 */
		public static boolean containsAll(Collection<?> collection, Collection<?> c) {
			for (Object e : c)
				if (!collection.contains(e))
					return false;
			return true;
		}

		/**
		 * Dependencies: {@link Iterable#iterator()}
		 */
		public static Object[] toArray(Collection<?> collection) {
			return Iterators.toArray(collection.iterator(), Object.class);
		}

		/**
		 * Dependencies: {@link Iterable#iterator()}
		 */
		public static <E, T> T[] toArray(Collection<E> collection, T[] a) {
			int size = collection.size();
			if (a.length < size)
				a = ObjectArrays.newArray(a, size);
			
			int i = 0;
			for (Object element : collection)
				a[i++] = castObject(element);
			
			if (a.length > size)
				a[size] = null; //marking end of a collection
			
			return a;
		}

		/**
		 * Dependencies: {@link Iterable#iterator()}
		 */
		public static String toString(Collection<?> collection) {
			return Iterators.toString(collection.iterator());
		}
		
		/**
		 * Dependencies: {@link Iterable#iterator()}
		 */
		public static boolean remove(Collection<?> collection, Object o) {
	        Iterator<?> it = collection.iterator();
	        if (o==null) {
	            while (it.hasNext()) {
	                if (it.next()==null) {
	                    it.remove();
	                    return true;
	                }
	            }
	        } else {
	            while (it.hasNext()) {
	                if (o.equals(it.next())) {
	                    it.remove();
	                    return true;
	                }
	            }
	        }
	        return false;
		}

		/**
		 * Dependencies: {@link Iterable#iterator()}
		 */
		public static boolean removeAll(Collection<?> collection, Collection<?> c) {
			return Iterators.removeAll(collection.iterator(), c);
		}
	}
	
	
	
	public static class MapImpl {

		/**
		 * Dependencies: {@link Map#entrySet()}.hashCode()
		 */
		public static int hashCode(Map<?, ?> map) {
			return map.entrySet().hashCode();
		}

		/**
		 * Dependencies: {@link Map#entrySet()}.equals(Object)
		 */
		public static boolean equals(Map<?, ?> map, Object obj) {
			if (obj == map)
				return true;
			if (obj instanceof Map)
				return map.entrySet().equals(((Map<?, ?>) obj).entrySet());
			return false;
		}

		/**
		 * Dependencies: {@link Map#entrySet()}.iterator()
		 */
		public static <K, V> String toString(Map<K, V> map) {
			StringBuilder sb = new StringBuilder(
					(int) Math.min(map.size() * 8L, Ints.MAX_POWER_OF_TWO)).append('{');
			Joiner.on(", ").useForNull("null").withKeyValueSeparator("=").appendTo(sb, map);
			return sb.append('}').toString();
		}

		/**
		 * Dependencies: {@link Map#put(K, V)}
		 */
		public static <K, V> void putAll(Map<K, V> map, Map<? extends K, ? extends V> m) {
			for (Map.Entry<? extends K, ? extends V> entry : m.entrySet())
				map.put(entry.getKey(), entry.getValue());
		}
		
	}
	
	
	
	public static class MultimapImpl {

		/**
		 * Dependencies: {@link Multimap#put(K, V)}
		 */
		public static <K, V> boolean putAll(Multimap<K, V> multimap, Multimap<? extends K, ? extends V> m) {
			boolean changed = false;
			for (Map.Entry<? extends K, ? extends V> entry : m.entries())
				changed |= multimap.put(entry.getKey(), entry.getValue());
			return changed;
		}

		/**
		 * Dependencies: {@link Multimap#asMap()}.hashCode()
		 */
		public static int hashCode(Multimap<?, ?> multimap) {
			return multimap.asMap().hashCode();
		}

		/**
		 * Dependencies: {@link Multimap#asMap()}.equals(Object)
		 */
		public static boolean equals(Multimap<?, ?> multimap, Object obj) {
			if (obj == multimap)
				return true;
			
			if (obj instanceof Multimap) {
				Multimap<?, ?> o = (Multimap<?, ?>) obj;
				return multimap.asMap().equals(o.asMap());
			}
			
			return false;
		}

		/**
		 * Dependencies: {@link Multimap#asMap()}.toString()
		 */
		public static String toString(Multimap<?, ?> multimap) {
			return multimap.asMap().toString();
		}
		
	}
	
	
	
	public static class MultisetImpl extends CollectionImpl {

		private static <E> Multiset<E> cast(Iterable<E> c) {
			return (Multiset<E>) c;
		}

		/**
		 * Dependencies: {@link Multiset#add(E, int)}
		 */
		public static <E> boolean add(Multiset<E> multiset, E element) {
			multiset.add(element, 1);
			return true;
		}

		/**
		 * Dependencies: {@link Multiset#add(E, int)}, {@link Collection#add(E)}
		 */
		public static <E> boolean addAll(Multiset<E> multiset, Collection<? extends E> c) {
			if (c.isEmpty())
				return false;
			
			if (c instanceof Multiset)
				for (Multiset.Entry<? extends E> entry : cast(c).entrySet())
					multiset.add(entry.getElement(), entry.getCount());
			else
				Iterators.addAll(multiset, c.iterator());
			
			return true;
		}

		/**
		 * Dependencies: {@link Multiset#remove(E, int)}
		 */
		public static <E> boolean remove(Multiset<E> multiset, Object element) {
			return multiset.remove(element, 1) > 0;
		}

		/**
		 * Dependencies: {@link Multiset#elementSet()}.removeAll(Collection)
		 */
		public static boolean removeAll(Multiset<?> multiset, Collection<?> c) {
			return multiset.elementSet().removeAll(c instanceof Multiset ? cast(c).elementSet() : c);
		}

		/**
		 * Dependencies: {@link Multiset#elementSet()}.retainAll(Collection)
		 */
		public static boolean retainAll(Multiset<?> multiset, Collection<?> c) {
			return multiset.elementSet().retainAll(c instanceof Multiset ? cast(c).elementSet() : c);
		}

		/**
		 * Dependencies: {@link Multiset#count(Object)}
		 */
		public static boolean contains(Multiset<?> multiset, Object element) {
			return multiset.count(element) > 0;
		}

		/**
		 * Dependencies: {@link Multiset#entrySet()}.hashCode()
		 */
		public static int hashCode(Multiset<?> multiset) {
			return multiset.entrySet().hashCode();
		}

		/**
		 * Dependencies: {@link Collection#size()}, {@link Multiset#elementSet()}.size(),
		 * {@link Multiset#count(Object)}
		 */
		public static boolean equals(Multiset<?> multiset, Object obj) {
			if (obj == multiset)
				return true;
			
			if (obj instanceof Multiset) {
				Multiset<?> m = (Multiset<?>) obj;
				
				if (m.size() != multiset.size()
						|| m.elementSet().size() != multiset.elementSet().size())
					return false;
				
				for (Multiset.Entry<?> entry : m.entrySet())
					if (entry.getCount() != multiset.count(entry.getElement()))
						return false;
				
				return true;
			}
			
			return false;
		}

		/**
		 * Dependencies: {@link Multiset#entrySet()}.toString()
		 */
		public static String toString(Multiset<?> multiset) {
			return multiset.entrySet().toString();
		}
		
	}
	
	
	
	public static class SetMultimapImpl extends MultimapImpl {

		/**
		 * Dependencies: {@link SetMultimap#removeAll(Object)}, {@link Multimap#putAll(K, Iterable)}
		 */
		public static <K, V> Set<V> replaceValues(SetMultimap<K, V> multimap, K key, Iterable<? extends V> values) {
			checkNotNull(values);
			Set<V> ret = multimap.removeAll(values);
			multimap.putAll(key, values);
			return ret;
		}
		
	}
	
	
	
	public static class SetImpl extends CollectionImpl {

		/**
		 * Dependencies: {@link Collection#size()}, {@link Iterable#iterator()}, {@link Collection#remove()}
		 */
		public static boolean removeAll(Set<?> set, Collection<?> c) {
			if (c instanceof Multiset)
				c = ((Multiset<?>) c).elementSet();

			boolean changed = false;
			
			if (c instanceof Set && c.size() > set.size()) {
				Iterator<?> ite = set.iterator();
				while (ite.hasNext())
					if (c.contains(ite.next())) {
						changed = true;
						ite.remove();
					}
				return changed;
			}
			
			for (Object obj : c)
				changed |= set.remove(obj);
			return changed;
		}

		/**
		 * Dependencies: {@link Iterable#iterator()}
		 */
		public static int hashCode(Set<?> set) {
			int h = 0;
			for (Object o : set)
				if (o != null)
					h += o.hashCode();
			return h;
		}

		/**
		 * Dependencies: {@link Collection#size()}, {@link Collection#containsAll(Collection)}
		 */
		public static boolean equals(Set<?> set, Object obj) {
			if (obj == set)
				return true;
			
			if (obj instanceof Set)
				try {
					Set<?> o = (Set<?>) obj;
					return o.size() == set.size() && set.containsAll(o);
				} catch (NullPointerException ignored) {
				} catch (ClassCastException ignored) { }
			
			return false;
		}
		
	}
	
	
	
	public static class TableImpl {

		/**
		 * Dependencies: {@link Table#put(R, C, V)}
		 */
		public static <R, C, V> void putAll(Table<R, C, V> table, Table<? extends R, ? extends C, ? extends V> t) {
			for (Table.Cell<? extends R, ? extends C, ? extends V> cell : t.cellSet())
				table.put(cell.getRowKey(), cell.getColumnKey(), cell.getValue());
		}
		
		/**
		 * Dependencies: {@link Table#cellSet()}.hashcode()
		 */
		public static int hashCode(Table<?, ? ,?> table) {
			return table.cellSet().hashCode();
		}

		/**
		 * Dependencies: {@link Table#cellSet()}.equals(Object)
		 */
		public static boolean equals(Table<?, ? ,?> table, Object obj) {
			if (obj == table)
				return true;
			if (obj instanceof Table) {
				Table<?, ?, ?> other = (Table<?, ?, ?>) obj;
				return table.cellSet().equals(other.cellSet());
			}
			return false;
		}

		/**
		 * Dependencies: {@link Table#rowMap()}.toString()
		 */
		public static String toString(Table<?, ? ,?> table) {
			return table.rowMap().toString();
		}
		
	}
	
	
	
	public static class MapSetImpl extends SetImpl {

		/**
		 * Dependencies: {@link MapSet#add(Map.Entry)}
		 */
		public static <K, V> boolean addAll(MapSet<K, V> mapSet,
				Collection<? extends Entry<K, V>> c) {
			boolean changed = false;
			for (Entry<K, V> entry : c)
				changed |= mapSet.add(entry);
			return changed;
		}
		
		/**
		 * Dependencies: {@link MapSet#put(K, V)}
		 */
		public static <K, V> void putAll(MapSet<K, V> mapSet,
				Collection<? extends Entry<? extends K, ? extends V>> c) {
			for (Entry<? extends K, ? extends V> entry : c)
				mapSet.put(entry.getKey(), entry.getValue());
		}
		
		public static <K, V> Map<K, V> asMap(final MapSet<K, V> mapSet) {
			return new AbstractMapSetAsMap<K, V>() {
				@Override protected MapSet<K, V> mapSet() {
					return mapSet;
				}
			};
		}
		
	}
	
	
	
	public static class BiListImpl extends CollectionImpl {

		/**
		 * Dependencies: {@link BiList#cellSet()}.hashCode()
		 */
		public static int hashCode(BiList<?> biList) {
			return biList.cellSet().hashCode();
		}

		/**
		 * Dependencies: {@link BiList#cellSet()}.equals(Object)
		 */
		public static boolean equals(BiList<?> biList, Object obj) {
			if (obj == biList)
				return true;
			if (obj instanceof BiList)
				return biList.cellSet().equals(((BiList<?>) obj).cellSet());
			return false;
		}
		
		
		public static <E> Set<BiList.Cell<E>> cellSet(final BiList<E> biList) {
			return new AbstractBiListCellSet<E>() {
				@Override protected BiList<E> biList() {
					return biList;
				}
			};
		}
		
	}
	
	
	
	public static class MatrixImpl extends BiListImpl {
		
		/**
		 * Dependencies: {@link BiList#rowList()}.equals(Object)
		 */
		public static boolean equals(Matrix<?> matrix, Object obj) {
			if (obj == matrix)
				return true;
			if (obj instanceof Matrix) {
				Matrix<?> o = (Matrix<?>) obj;
				return o.width() == matrix.width() && matrix.rowList().equals(o.rowList());
			}
			if (obj instanceof BiList)
				return matrix.cellSet().equals(((BiList<?>) obj).cellSet());
			return false;
		}
		
		public static String toString(Matrix<?> matrix) {
			return matrix.rowList().toString();
		}
		
		public static <E> BiListIterator<E> biListIterator(final Matrix<E> matrix) {
			return new AbstractMatrixIterator<E>() {
				@Override protected Matrix<E> matrix() {
					return matrix;
				}
			};
		}
		
	}

}
