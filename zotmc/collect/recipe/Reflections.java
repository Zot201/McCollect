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

import static cpw.mods.fml.common.ObfuscationReflectionHelper.remapFieldNames;
import static cpw.mods.fml.relauncher.ReflectionHelper.findField;
import static net.minecraft.item.crafting.FurnaceRecipes.smelting;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.IFuelHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.ReflectionHelper.UnableToAccessFieldException;

class Reflections {
	
	static final Map<ItemStack, Float> EXPERIENCE_LIST = ObfuscationReflectionHelper.getPrivateValue(
			FurnaceRecipes.class, smelting(), "experienceList", "field_77605_c");
	
	static final List<IFuelHandler> FUEL_HANDLERS =
			ReflectionHelper.getPrivateValue(GameRegistry.class, null, "fuelHandlers");
	
	static final Map<Integer, ArrayList<ItemStack>> ORE_STACKS =
			ReflectionHelper.getPrivateValue(OreDictionary.class, null, "oreStacks");

	
	
	enum Fields {
		TAG_LIST			(NBTTagList.class,			true, "tagList", "field_74747_a"),
		TAG_TYPE			(NBTTagList.class,			true, "tagType", "field_74746_b"),
		TAG_MAP				(NBTTagCompound.class,		true, "tagMap", "field_74784_a"),
		RECIPE_OUTPUT_S		(ShapedRecipes.class,		true, "recipeOutput", "field_77575_e"),
		RECIPE_OUTPUT_SL	(ShapelessRecipes.class,	true, "recipeOutput", "field_77580_a"),
		
		MIRRORED			(ShapedOreRecipe.class,		false, "mirrored"),
		WIDTH				(ShapedOreRecipe.class,		false, "width"),
		HEIGHT				(ShapedOreRecipe.class,		false, "height"),
		INPUT				(ShapedOreRecipe.class,		false, "input"),
		OUTPUT_S			(ShapedOreRecipe.class,		false, "output"),
		OUTPUT_SL			(ShapelessOreRecipe.class,	false, "output");
		

		private final Field field;
		private Fields(Class<?> clz, boolean isObfuscated, String... names) {
			if (isObfuscated)
				names = remapFieldNames(clz.getName(), names);
			
			field = findField(clz, names);
			field.setAccessible(true);
		}
		
		@SuppressWarnings("unchecked")
		public <V> V get(Object instance) {
			try {
				return (V) field.get(instance);
			} catch (Exception e) {
				throw new UnableToAccessFieldException(new String[0], e);
			}
		}
		public <V> void set(Object instance, V value) {
			try {
				field.set(instance, value);
			} catch (Exception e) {
				throw new UnableToAccessFieldException(new String[0], e);
			}
		}
		public <V> void copyValue(Object sourceInstance, Object targetInstance) {
			try {
				field.set(targetInstance, field.get(sourceInstance));
			} catch (Exception e) {
				throw new UnableToAccessFieldException(new String[0], e);
			}
		}
		
	}

}
