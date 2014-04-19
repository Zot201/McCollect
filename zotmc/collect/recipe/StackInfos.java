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
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import zotmc.collect.Commuter;

import com.google.common.base.Objects;

public class StackInfos {
	
	public static StackInfo asInfo(Item item) {
		return asInfo(new ItemStack(checkNotNull(item)));
	}
	public static StackInfo asInfo(Block block) {
		return asInfo(new ItemStack(checkNotNull(block)));
	}
	public static StackInfo asInfo(final ItemStack item) {
		if (item == null)
			return nullItem();
		
		return new AbstractStackInfo() {
			@Override public boolean isNullItem() {
				return false;
			}
			@Override public Item item() {
				return item.getItem();
			}
			@Override public int size() {
				return item.stackSize;
			}
			@Override public int metadata() {
				return item.getItemDamage();
			}
			@Override public NBTTagCompound tag() {
				return item.stackTagCompound;
			}
			@Override public boolean equalTag(NBTTagCompound other) {
				return Objects.equal(item.stackTagCompound, other);
			}
			@Override public ItemStack asItemStack() {
				return item;
			}
		};
	}
	
	public static StackInfo unsized(ItemStack item) {
		return unsized(asInfo(item));
	}
	
	public static StackInfo unsized(final StackInfo info) {
		if (info.isNullItem())
			throw new IllegalArgumentException();
		
		return info instanceof UnsizedStackInfo ? info : new UnsizedStackInfo(info);
	}
	private static class UnsizedStackInfo extends AbstractStackInfo {
		private final StackInfo info;
		public UnsizedStackInfo(StackInfo info) {
			this.info = info;
		}
		@Override public boolean isNullItem() {
			return false;
		}
		@Override public Item item() {
			return info.item();
		}
		@Override public int size() {
			return 1;
		}
		@Override public int metadata() {
			return info.metadata();
		}
		@Override public NBTTagCompound tag() {
			return info.tag();
		}
	}
	
	
	
	
	public static StackInfo nullItem() {
		return NULL_ITEM;
	}
	private static final StackInfo NULL_ITEM = new AbstractStackInfo() {
		@Override public boolean isNullItem() {
			return true;
		}
		@Override public boolean equalTag(NBTTagCompound other) {
			return false;
		}
		@Override public int size() {
			return 0;
		}
		@Override public ItemStack asItemStack() {
			return null;
		}

		@Override public Item item() {
			throw new UnsupportedOperationException();
		}
		@Override public int metadata() {
			throw new UnsupportedOperationException();
		}
		@Override public NBTTagCompound tag() {
			throw new UnsupportedOperationException();
		}
		
		@Override public int hashCode() {
			return 0;
		}
		@Override public boolean equals(Object obj) {
			return obj == this || obj instanceof StackInfo && ((StackInfo) obj).isNullItem();
		}
	};
	
	
	public static Commuter<ItemStack, StackInfo> asInfo() {
		return AS_INFO;
	}
	private static final Commuter<ItemStack, StackInfo> AS_INFO =
			new Commuter<ItemStack, StackInfo>() {
		@Override public StackInfo apply(ItemStack input) {
			return asInfo(input);
		}
		@Override public ItemStack disapply(StackInfo output) {
			return output.asItemStack();
		}
	};
	
}
