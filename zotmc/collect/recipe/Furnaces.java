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

import static net.minecraft.item.crafting.FurnaceRecipes.smelting;
import static zotmc.collect.Conversions.castRaw;
import static zotmc.collect.Conversions.entryAlterKey;
import static zotmc.collect.recipe.Reflections.EXPERIENCE_LIST;
import static zotmc.collect.recipe.Reflections.FUEL_HANDLERS;
import static zotmc.collect.recipe.StackInfos.asInfo;
import static zotmc.collect.recipe.StackInfos.unsized;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntityFurnace;
import zotmc.collect.FluentMap;
import zotmc.collect.delegate.IterativeMap;
import zotmc.collect.delegate.MapBackingSet;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import cpw.mods.fml.common.IFuelHandler;

public class Furnaces {
	
	public static RecipeMap recipes() {
		return RECIPES;
	}
	
	public static ExperienceMap experience() {
		return EXPERIENCE;
	}
	
	public static FuelFunction fuels() {
		return FUELS;
	}
	
	
	
	public static abstract class FurnaceMap<V> extends MapBackingSet<StackInfo, V> {
		
		private FurnaceMap(Map<ItemStack, V> raw) {
			this.raw = raw;
			
			final Collection<Entry<StackInfo, V>> backing = Collections2.transform(
					raw.entrySet(),
					entryAlterKey(keyTransformation, (V) null));
			
			this.backing = new IterativeMap<StackInfo, V>() {
				@Override protected Collection<Entry<StackInfo, V>> backing() {
					return backing;
				}
				@Override public V put(StackInfo key, V value) {
					V ret = remove(key);
					raw().put(key.asItemStack(), value);
					return ret;
				}
			};
		}
		
		private final Map<ItemStack, V> raw;
		protected final Map<ItemStack, V> raw() {
			return raw;
		}
		private final Map<StackInfo, V> backing;
		protected Map<StackInfo, V> backing() {
			return backing;
		}

		private final Function<ItemStack, StackInfo> keyTransformation = new Function<ItemStack, StackInfo>() {
			@Override public StackInfo apply(final ItemStack input) {
				if (input == null)
					return StackInfos.nullItem();
				
				return new AbstractStackInfo() {
					@Override public Item item() {
						return input.getItem();
					}
					@Override public int size() {
						return 1;
					}
					@Override public int metadata() {
						return input.getItemDamage();
					}
					@Override public NBTTagCompound tag() {
						return null;
					}
					@Override public boolean isNullItem() {
						return false;
					}
				};
			}
		};
		
	}
	
	
	
	private static final RecipeMap RECIPES = new RecipeMap();
	
	public static class RecipeMap extends FurnaceMap<StackInfo> {

		/**
		 * Called this if you want to know the exact result instead of the mapping.
		 */
		public StackInfo getResultant(StackInfo item) {
			return getResultant(item.asItemStack());
		}
		/**
		 * Called this if you want to know the exact result instead of the mapping.
		 */
		public StackInfo getResultant(ItemStack item) {
			return asInfo(smelting().getSmeltingResult(item));
		}
		
		
		private RecipeMap() {
			super(FluentMap.from(
					castRaw(smelting().getSmeltingList(), ItemStack.class, ItemStack.class))
						.commuteValues(asInfo()));
		}
		
		
		public StackInfo put(ItemStack key, Item value) {
			return put(asInfo(key), asInfo(value));
		}
		public StackInfo put(ItemStack key, Block value) {
			return put(asInfo(key), asInfo(value));
		}
		public StackInfo put(ItemStack key, ItemStack value) {
			return put(asInfo(key), asInfo(value));
		}
		
		public boolean remove(ItemStack key, Item value) {
			return remove(asInfo(key), asInfo(value));
		}
		public boolean remove(ItemStack key, Block value) {
			return remove(asInfo(key), asInfo(value));
		}
		public boolean remove(ItemStack key, ItemStack value) {
			return remove(asInfo(key), asInfo(value));
		}
		
		public StackInfo getValue(ItemStack key) {
			return getValue(asInfo(key));
		}
		public StackInfo removeKey(ItemStack key) {
			return removeKey(asInfo(key));
		}

	}
	

	
	private static ExperienceMap EXPERIENCE = new ExperienceMap();
	
	public static class ExperienceMap extends FurnaceMap<Float> {

		/**
		 * Called this if you want to know the exact result instead of the mapping.
		 */
		public Float getResultant(StackInfo item) {
			return getResultant(item.asItemStack());
		}
		/**
		 * Called this if you want to know the exact result instead of the mapping.
		 */
		public Float getResultant(ItemStack item) {
			return smelting().func_151398_b(item);
		}
		
		
		private ExperienceMap() {
			super(EXPERIENCE_LIST);
		}
		
		public Float put(ItemStack key, Float value) {
			return put(asInfo(key), value);
		}
		public boolean remove(ItemStack key, Float value) {
			return remove(asInfo(key), value);
		}
		public Float removeKey(ItemStack key) {
			return removeKey(asInfo(key));
		}

		public Float getValue(ItemStack key) {
			return getValue(asInfo(key));
		}
		
	}
	
	
	
	
	
	private static final FuelFunction FUELS = new FuelFunction();
	
	public static class FuelFunction implements Function<ItemStack, Integer> {
		
		private FuelFunction() { }

		public void unset(ItemStack item) {
			set(item, 0);
		}
		public void set(ItemStack item, Integer value) {
			final StackInfo info = unsized(item);
			
			set(item, new Predicate<ItemStack>() {
				@Override public boolean apply(ItemStack input) {
					return info.equals(unsized(input));
				}
			}, value);
		}
		public void set(ItemStack probe, Predicate<ItemStack> inputPredicate, Integer output) {
			Builder<IFuelHandler> b = ImmutableList.builder();
			Iterator<IFuelHandler> ite = FUEL_HANDLERS.iterator();
			while (ite.hasNext()) {
				IFuelHandler handler = ite.next();
				try {
					if (handler.getBurnTime(probe) <= output)
						continue;
				}
				catch (NullPointerException ignored) { }
				
				b.add(handler);
				ite.remove();
			}
			FUEL_HANDLERS.add(new FilteredHandler(inputPredicate, output, b.build()));
		}
		
		
		
		@Override public Integer apply(ItemStack item) {
			return TileEntityFurnace.getItemBurnTime(item);
		}
		
		private static int getFuelValue(List<IFuelHandler> list, ItemStack item) {
			int ret = 0;
			for (IFuelHandler handler : list)
				try {
					ret = Math.max(ret, handler.getBurnTime(item));
				} catch (NullPointerException ignored) { }
			return ret;
		}
		
		
		private static class FilteredHandler implements IFuelHandler {
			final Predicate<ItemStack> filter;
			final int value;
			final List<IFuelHandler> handlers;
			FilteredHandler(Predicate<ItemStack> filter, int value, List<IFuelHandler> handlers) {
				this.filter = filter;
				this.value = value;
				this.handlers = handlers;
			}
			@Override public int getBurnTime(ItemStack fuel) {
				return filter.apply(fuel) ? value : getFuelValue(handlers, fuel);
			}
			@Override public String toString() {
				return "{" + filter + "?" + value + ":" + handlers + "}";
			}
		}
		
	}
	
	

}
