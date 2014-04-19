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

package zotmc.collect.recipe;

import static com.google.common.base.Preconditions.checkNotNull;
import static zotmc.collect.Conversions.constant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import zotmc.collect.BiListIterator;
import zotmc.collect.FallbackingMap;
import zotmc.collect.FluentPredicate;
import zotmc.collect.Matrix;
import zotmc.collect.StandardImpls.MatrixImpl;
import zotmc.collect.delegate.DelegateMultiset;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.primitives.Chars;

public class RecipeElement {
	
	/** The metadata wildcard value for recipe items */
	public static int W = OreDictionary.WILDCARD_VALUE, WILDCARD_VALUE = W;
	

	public static FluentPredicate<RecipeElement> equalTo(final Item item) {
		checkNotNull(item);
		return new FluentPredicate<RecipeElement>() {
			@Override public boolean apply(RecipeElement input) {
				return input.item == item && input.metadata == 0;
			}
		};
	}
	public static FluentPredicate<RecipeElement> equalTo(Block block) {
		final Item item = asItem(block);
		return new FluentPredicate<RecipeElement>() {
			@Override public boolean apply(RecipeElement input) {
				return input.item == item && input.metadata == 0;
			}
		};
	}
	public static FluentPredicate<RecipeElement> equalTo(String ore) {
		final ArrayList<ItemStack> list = OreDicts.getOres(ore);
		return new FluentPredicate<RecipeElement>() {
			@Override public boolean apply(RecipeElement input) {
				return input.isOre() && input.ore == list;
			}
		};
	}
	public static FluentPredicate<RecipeElement> equalTo(final ItemStack item) {
		checkNotNull(item);
		return new FluentPredicate<RecipeElement>() {
			@Override public boolean apply(RecipeElement input) {
				return input.item == item.getItem() && input.metadata == item.getItemDamage();
			}
		};
	}

	public static Vague vague(Block block) {
		return vague(asItem(block));
	}
	public static Vague vague(Item item) {
		return new Vague(item);
	}
	
	public static class Vague implements Predicate<RecipeElement>, Iterable<RecipeElement> {
		private final Item item;
		private Vague(Item item) {
			this.item = item;
		}
		@Override public Iterator<RecipeElement> iterator() {
			return Iterators.forArray(new RecipeElement(item), new RecipeElement(item, WILDCARD_VALUE));
		}
		@Override public boolean apply(RecipeElement input) {
			return input.item == item && (input.metadata == WILDCARD_VALUE || input.metadata == 0);
		}
		
		public Iterable<ItemStack> items() {
			return new Iterable<ItemStack>() {
				@Override public Iterator<ItemStack> iterator() {
					return Iterators.forArray(new ItemStack(item), new ItemStack(item, 1, WILDCARD_VALUE));
				}
			};
		}
		
	}
	
	

	public final Item item;
	public final int metadata;
	final ArrayList<ItemStack> ore;
	public RecipeElement(Item item) {
		this.item = item;
		this.metadata = 0;
		this.ore = null;
	}
	public RecipeElement(Block block) {
		this.item = asItem(block);
		this.metadata = 0;
		this.ore = null;
	}
	public RecipeElement(String ore) {
		this.item = null;
		this.metadata = 0;
		this.ore = OreDicts.getOres(ore);
	}
	RecipeElement(ArrayList<ItemStack> ore) {
		this.item = null;
		this.metadata = 0;
		this.ore = ore;
	}
	public RecipeElement(ItemStack item) {
		if (item != null) {
			this.item = item.getItem();
			this.metadata = item.getItemDamage();
			this.ore = null;
		}
		else {
			this.item = null;
			this.metadata = 0;
			this.ore = null;
		}
	}
	public RecipeElement(Item item, int metadata) {
		this.item = item;
		this.metadata = metadata;
		this.ore = null;
	}
	
	public static final RecipeElement NULL_ELEMENT = new RecipeElement((ItemStack) null);
	
	@Override public int hashCode() {
		return Objects.hashCode(item, metadata, ore);
	}
	@Override public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj instanceof RecipeElement) {
			RecipeElement o = (RecipeElement) obj;
			return o.item == item && o.metadata == metadata && o.ore == ore;
		}
		return false;
	}
	
	private static final Splitter X = Splitter.on('x').limit(2);
	@Override public String toString() {
		if (isOre())
			return ore();
		if (item != null)
			return X.splitToList(TO_ITEM_STACK.apply(this).toString()).get(1);
		return "empty";
	}
	public String ore() {
		return OreDicts.name(ore);
	}
	
	public boolean isOre() {
		return ore != null;
	}
	static final Predicate<RecipeElement> IS_ORE = new Predicate<RecipeElement>() {
		@Override public boolean apply(RecipeElement input) {
			return input.isOre();
		}
	};
	
	
	static Item asItem(Block block) {
		return Item.getItemFromBlock(block);
	}
	public static Function<ItemStack, RecipeElement> fromItemStack() {
		return FROM_ITEM_STACK;
	}
	public static Function<RecipeElement, ItemStack> toItemStack() {
		return TO_ITEM_STACK;
	}
	private static final Function<ItemStack, RecipeElement> FROM_ITEM_STACK = new Function<ItemStack, RecipeElement>() {
		@Override public RecipeElement apply(ItemStack input) {
			return new RecipeElement(input);
		}
	};
	static final Function<RecipeElement, ItemStack> TO_ITEM_STACK = new Function<RecipeElement, ItemStack>() {
		@Override public ItemStack apply(RecipeElement input) {
			if (input.isOre())
				throw new IllegalArgumentException();
			return input.item != null ? new ItemStack(input.item, 1, input.metadata) : null;
		}
	};
	
	
	
	static abstract class AbstractMatrix implements Matrix<RecipeElement> {
		
		@Override public boolean isEmpty() {
			return size() == 0;
		}
		
		private Set<Cell<RecipeElement>> cellSet;
		@Override public Set<Cell<RecipeElement>> cellSet() {
			return cellSet != null ? cellSet : (cellSet = MatrixImpl.cellSet(this));
		}
		
		@Override public Iterator<RecipeElement> iterator() {
			return biListIterator();
		}
		@Override public BiListIterator<RecipeElement> biListIterator() {
			return MatrixImpl.biListIterator(this);
		}
		
		
		
		@Override public int hashCode() {
			return MatrixImpl.hashCode(this);
		}
		@Override public boolean equals(Object obj) {
			return MatrixImpl.equals(this, obj);
		}
		@Override public String toString() {
			return MatrixImpl.toString(this);
		}

		@Override public boolean contains(Object e) {
			return Iterators.contains(iterator(), e);
		}
		
		@Override public Object[] toArray() {
			return MatrixImpl.toArray(this);
		}
		@Override public <T> T[] toArray(T[] a) {
			return MatrixImpl.toArray(this, a);
		}
		@Override public boolean containsAll(Collection<?> c) {
			return MatrixImpl.containsAll(this, c);
		}
		
		@Deprecated @Override public boolean add(RecipeElement e) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean addAll(Collection<? extends RecipeElement> c) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public void clear() {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean remove(Object e) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public RecipeElement remove(int rowIndex, int columnIndex) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		
		@Deprecated @Override public RecipeElement set(int rowIndex, int columnIndex, RecipeElement e) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	
	public static class ElementMatrix extends AbstractMatrix {
		
		private final int h, w;
		private final char[][] shape;
		private final Map<Character, RecipeElement> items =
				FallbackingMap.create(constant(NULL_ELEMENT, Character.class));
		private ElementMatrix(int h, int w, char[][] shape) {
			this.h = h;
			this.w = w;
			this.shape = shape;
		}
		@Override public int height() {
			return h;
		}
		@Override public int width() {
			return w;
		}
		@Override public int size() {
			return w * h;
		}
		
		@Override public RecipeElement get(int rowIndex, int columnIndex) {
			return items.get(shape[rowIndex][columnIndex]);
		}
		
		private Function<char[], List<RecipeElement>> transformRow;
		protected Function<char[], List<RecipeElement>> transformRow() {
			return transformRow != null ? transformRow : (transformRow =
					new Function<char[], List<RecipeElement>>() {
				@Override public List<RecipeElement> apply(char[] input) {
					return Lists.transform(Chars.asList(input), Functions.forMap(items));
				}
			});
		}
		
		private List<List<RecipeElement>> rowList;
		@Override public List<List<RecipeElement>> rowList() {
			return rowList != null ? rowList : (rowList = Lists.transform(Arrays.asList(shape), transformRow()));
		}
		
		
		
		public ElementMatrix let(char symbol, Item item) {
			items.put(symbol, new RecipeElement(item));
			return this;
		}
		public ElementMatrix let(char symbol, Block block) {
			items.put(symbol, new RecipeElement(block));
			return this;
		}
		public ElementMatrix let(char symbol, String ore) {
			items.put(symbol, new RecipeElement(ore));
			return this;
		}
		public ElementMatrix let(char symbol, ItemStack item) {
			items.put(symbol, new RecipeElement(item));
			return this;
		}
	}
	
	public static ElementMatrix inShape(String... shape) {
		int h = shape.length, w = 0;
		for (String s : shape)
			w = Math.max(w, s.length());
		
		char[][] charArray = new char[h][w];
		for (int j = 0; j < h; j++) {
			int i = 0;
			int length = shape[j].length();
			for (; i < length; i++)
				charArray[j][i] = shape[j].charAt(i);
			for (; i < w; i++)
				charArray[j][i] = ' ';
		}
		
		return new ElementMatrix(h, w, charArray);
	}
	
	
	public static class ElementMultiSet extends DelegateMultiset<RecipeElement> {
		private ElementMultiSet() { }
		private final Multiset<RecipeElement> delegatee = HashMultiset.create();
		@Override protected Multiset<RecipeElement> delegatee() {
			return delegatee;
		}

		public ElementMultiSet pad(Item... items) {
			for (Item item : items)
				delegatee().add(new RecipeElement(item));
			return this;
		}
		public ElementMultiSet pad(Block... blocks) {
			for (Block block : blocks)
				delegatee().add(new RecipeElement(block));
			return this;
		}
		public ElementMultiSet pad(String... ores) {
			for (String ore : ores)
				delegatee().add(new RecipeElement(ore));
			return this;
		}
		public ElementMultiSet pad(ItemStack... items) {
			for (ItemStack item : items)
				delegatee().add(new RecipeElement(item));
			return this;
		}
		
		public ElementMultiSet pad(Item item, int amount) {
			delegatee().add(new RecipeElement(item), amount);
			return this;
		}
		public ElementMultiSet pad(Block block, int amount) {
			delegatee().add(new RecipeElement(block), amount);
			return this;
		}
		public ElementMultiSet pad(String ore, int amount) {
			delegatee().add(new RecipeElement(ore), amount);
			return this;
		}
		public ElementMultiSet pad(ItemStack item, int amount) {
			delegatee().add(new RecipeElement(item), amount);
			return this;
		}
	}
	

	public static ElementMultiSet inProportion(Item... items) {
		return new ElementMultiSet().pad(items);
	}
	public static ElementMultiSet inProportion(Block... blocks) {
		return new ElementMultiSet().pad(blocks);
	}
	public static ElementMultiSet inProportion(String... ores) {
		return new ElementMultiSet().pad(ores);
	}
	public static ElementMultiSet inProportion(ItemStack... items) {
		return new ElementMultiSet().pad(items);
	}
	
	public static ElementMultiSet inProportion(Item item, int amount) {
		return new ElementMultiSet().pad(item, amount);
	}
	public static ElementMultiSet inProportion(Block block, int amount) {
		return new ElementMultiSet().pad(block, amount);
	}
	public static ElementMultiSet inProportion(String ore, int amount) {
		return new ElementMultiSet().pad(ore, amount);
	}
	public static ElementMultiSet inProportion(ItemStack item, int amount) {
		return new ElementMultiSet().pad(item, amount);
	}
	

}
