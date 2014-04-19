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

import static zotmc.collect.recipe.Reflections.ORE_STACKS;

import java.util.ArrayList;
import java.util.Map.Entry;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDicts {
	
	public static void register(String name, Item item) {
		OreDictionary.registerOre(name, item);
	}
	public static void register(String name, ItemStack item) {
		OreDictionary.registerOre(name, item);
	}
	
	public static ArrayList<ItemStack> getOres(String name) {
		return OreDictionary.getOres(name);
	}
	
	static String name(ArrayList<ItemStack> ore) {
		for (Entry<Integer, ArrayList<ItemStack>> entry : ORE_STACKS.entrySet())
			if (entry.getValue() == ore)
				return OreDictionary.getOreName(entry.getKey());
		return null;
	}

}
