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
import static net.minecraft.item.Item.itemRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import zotmc.collect.FluentPredicate;

import com.google.common.base.Function;
import com.google.common.base.Splitter;

public abstract class Content<T> {
	
	@Deprecated public static <T> Content<T> unset() {
		return absent();
	}
	
	public static <T> Content<T> absent() {
		return new Content<T>() {
			T reference;
			@Override public boolean exists() {
				return reference != null;
			}
			@Override public T get() {
				if (!exists())
					throw new IllegalStateException();
				return reference;
			}
			@Override public T orNull() {
				return reference;
			}
			@Override public void set(T reference) {
				if (exists())
					throw new IllegalStateException();
				this.reference = checkNotNull(reference);
			}
		};
	}
	public static <T> Content<T> of(T reference) {
		Content<T> content = absent();
		content.set(reference);
		return content;
	}
	
	
	
	@Deprecated public static <T extends Item> ItemContent<T> unsetItem() {
		return absentItem();
	}
	
	public static <T extends Item> ItemContent<T> absentItem() {
		return new ItemContent<T>() {
			Content<T> delegatee = absent();
			@Override public boolean exists() {
				return delegatee.exists();
			}
			@Override public T get() {
				return delegatee.get();
			}
			@Override public T orNull() {
				return delegatee.orNull();
			}
			@Override public void set(T reference) {
				delegatee.set(reference);
			}
		};
		
	}
	
	public static ItemContent<Item> of(String modid, String name) {
		return new ItemFetcher(modid + ":" + name);
	}
	

	private static final Splitter SP = Splitter.on(':').limit(2);
	public static FluentPredicate<ItemStack> inMod(final String modid) {
		checkNotNull(modid);
		return new FluentPredicate<ItemStack>() {
			@Override public boolean apply(ItemStack input) {
				return input != null && modid.equals(SP.splitToList(itemRegistry.getNameForObject(input.getItem())).get(0));
			}
		};
	}
	

	
	protected Content() { }
	
	public abstract boolean exists();
	
	public abstract T get();
	
	public abstract T orNull();
	
	public abstract void set(T reference);
	
	
	public static abstract class ItemContent<T extends Item> extends Content<T> {
		private ItemContent() { }
		
		public Content<ItemStack> meta(final int metadata) {
			return new Content<ItemStack>() {
				@Override public boolean exists() {
					return ItemContent.this.exists();
				}
				@Override public ItemStack get() {
					return new ItemStack(ItemContent.this.get(), 1, metadata);
				}
				@Override public ItemStack orNull() {
					Item item = ItemContent.this.orNull();
					return item != null ? new ItemStack(item, 1, metadata) : null;
				}
				@Override public void set(ItemStack reference) {
					throw new UnsupportedOperationException();
				}
			};
		}
	}

	private static class ItemFetcher extends ItemContent<Item> {
		private Item reference;
		private final String name;
		ItemFetcher(String name) {
			this.name = name;
		}
		protected Item fetchItem() {
			return (Item) itemRegistry.getObject(name);
		}
		@Override public boolean exists() {
			return reference != null || (reference = fetchItem()) != null;
		}
		@Override public Item get() {
			if (!exists())
				throw new ItemNotFoundException();
			return reference;
		}
		@Override public Item orNull() {
			return exists() ? reference : null;
		}
		@Override public void set(Item reference) {
			throw new UnsupportedOperationException();
		}
	}
	
	public static class ItemNotFoundException extends IllegalArgumentException {
		public ItemNotFoundException() {
			super();
		}
		public ItemNotFoundException(String message, Throwable cause) {
			super(message, cause);
		}
		public ItemNotFoundException(String s) {
			super(s);
		}
		public ItemNotFoundException(Throwable cause) {
			super(cause);
		}
	}
	
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static <T> Function<Content<T>, T> unwrap() {
		return (Function) UNWRAP;
	}
	private static final Function<Content<?>, Object> UNWRAP = new Function<Content<?>, Object>() {
		@Override public Object apply(Content<?> input) {
			return input.get();
		}
	};
	
}
