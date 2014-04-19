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

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cpw.mods.fml.common.registry.GameRegistry;

/**
 * A recipe builder designed to form chains only. The caller of a method may or may not receive the change
 * from the call. Always use the returned object to process further changes.
 * 
 * @author zot
 */
public class RecipeBuilder {

	protected final ItemStack result;
	private RecipeBuilder(ItemStack result) {
		this.result = result;
	}

	public static RecipeBuilder resulting(Item result){
		return new RecipeBuilder(new ItemStack(result));
	}
	public static RecipeBuilder resulting(Block result){
		return new RecipeBuilder(new ItemStack(result));
	}
	/**
	 * Warning: Stack size is neglected. Please call {@code regardingSize()} to apply back the stack size.
	 */
	public static SizedRecipeBuilder resulting(ItemStack result){
		ItemStack r = result.copy();
		r.stackSize = 1;
		return new SizedRecipeBuilder(r, result.stackSize);
	}
	
	public RecipeBuilder inSize(int size) {
		result.stackSize = size;
		return this;
	}
	
	public static class SizedRecipeBuilder extends RecipeBuilder {
		private final int size;
		public SizedRecipeBuilder(ItemStack result, int size) {
			super(result);
			this.size = size;
		}
		public RecipeBuilder regardingSize() {
			result.stackSize = size;
			return new RecipeBuilder(result);
		}
	}
	
	// the same as the usual recipe construction, but with space padding at the end to the strings.
	public ShapedRecipeBuilder inShape(String... shape) {
		int w = 0, h = shape.length;
		for (String s : shape)
			w = Math.max(w, s.length());
		
		String[] symbols = new String[h];
		for (int i = 0; i < h; i++)
			symbols[i] = Strings.padEnd(shape[i], w, ' ');
		
		return new ShapedRecipeBuilder(symbols, w);
	}

	public ShapelessRecipeBuilder uses(Item... items) {
		return new ShapelessRecipeBuilder().pad(items);
	}
	public ShapelessRecipeBuilder uses(Block... blocks) {
		return new ShapelessRecipeBuilder().pad(blocks);
	}
	public ShapelessRecipeBuilder uses(ItemStack... items) {
		return new ShapelessRecipeBuilder().pad(items);
	}
	public ShapelessOreRecipeBuilder uses(String... ores) {
		return new ShapelessOreRecipeBuilder(Lists.newArrayList()).pad(ores);
	}
	
	public ShapelessRecipeBuilder uses(Item item, int amount) {
		return new ShapelessRecipeBuilder().pad(item, amount);
	}
	public ShapelessRecipeBuilder uses(Block block, int amount) {
		return new ShapelessRecipeBuilder().pad(block, amount);
	}
	public ShapelessRecipeBuilder uses(ItemStack item, int amount) {
		return new ShapelessRecipeBuilder().pad(item, amount);
	}
	public ShapelessOreRecipeBuilder uses(String ore, int amount) {
		return new ShapelessOreRecipeBuilder(Lists.newArrayList()).pad(ore, amount);
	}
	
	
	
	protected abstract class AbstractBuilder {
		public abstract IRecipe build();
		public void addCraftingRecipe() {
			GameRegistry.addRecipe(build());
		}
	}
	
	public class ShapedRecipeBuilder extends AbstractBuilder {
		private final String[] symbols;
		private final int w;
		private final Map<Character, ItemStack> items = Maps.newHashMap();
		
		private ShapedRecipeBuilder(String[] symbols, int w) {
			this.symbols = symbols;
			this.w = w;
		}
		public ShapedRecipeBuilder let(char symbol, Item item) {
			items.put(symbol, new ItemStack(item));
			return this;
		}
		public ShapedRecipeBuilder let(char symbol, Block block) {
			items.put(symbol, new ItemStack(block));
			return this;
		}
		public ShapedRecipeBuilder let(char symbol, ItemStack item) {
			items.put(symbol, checkNotNull(item));
			return this;
		}
		public ShapedOreRecipeBuilder let(char symbol, String ore) {
			return new ShapedOreRecipeBuilder(symbols, w, Maps.<Character, Object>newHashMap(items))
					.let(symbol, ore);
		}
		@Override public ShapedRecipes build() {
			int h = symbols.length;
			
			ItemStack[] inputs = new ItemStack[w * h];
			int i = 0;
			for (String s : symbols)
				for (char c : s.toCharArray())
					inputs[i++] = items.get(c); // Null would be appropriate here to represent blank
			
			return new ShapedRecipes(w, h, inputs, result);
		}
	}

	public class ShapedOreRecipeBuilder extends AbstractBuilder {
		private final String[] symbols;
		private final int w;
		private final Map<Character, Object> items;
		
		private ShapedOreRecipeBuilder(String[] symbols, int w, Map<Character, Object> items) {
			this.symbols = symbols;
			this.w = w;
			this.items = items;
		}
		public ShapedOreRecipeBuilder let(char symbol, Item item) {
			items.put(symbol, new ItemStack(item));
			return this;
		}
		public ShapedOreRecipeBuilder let(char symbol, Block block) {
			items.put(symbol, new ItemStack(block));
			return this;
		}
		public ShapedOreRecipeBuilder let(char symbol, ItemStack item) {
			items.put(symbol, checkNotNull(item));
			return this;
		}
		public ShapedOreRecipeBuilder let(char symbol, String ore) {
			items.put(symbol, checkNotNull(ore)); // put straightly the string here
			return this;
		}
		@Override public ShapedOreRecipe build() { // perhaps rewriting this with a reflection approach
			int h = symbols.length;
			
			Object[] unparsed = new Object[h + items.size() * 2];
			System.arraycopy(symbols, 0, unparsed, 0, h);
			int i = h;
			for (Entry<Character, Object> entry : items.entrySet()) {
				unparsed[i++] = entry.getKey();
				unparsed[i++] = entry.getValue();
			}
			
			return new ShapedOreRecipe(result, unparsed);
		}
	}

	public class ShapelessRecipeBuilder extends AbstractBuilder {
		private final List<ItemStack> inputs = Lists.newArrayList();
		private ShapelessRecipeBuilder() { }
		
		public ShapelessRecipeBuilder pad(Item... items) {
			for (Item item : items)
				inputs.add(new ItemStack(item));
			return this;
		}
		public ShapelessRecipeBuilder pad(Block... blocks) {
			for (Block block : blocks)
				inputs.add(new ItemStack(block));
			return this;
		}
		public ShapelessRecipeBuilder pad(ItemStack... items) {
			for (ItemStack item : items)
				inputs.add(checkNotNull(item));
			return this;
		}
		public ShapelessOreRecipeBuilder pad(String... ores) {
			return new ShapelessOreRecipeBuilder(Lists.<Object>newArrayList(inputs)).pad(ores);
		}
		
		public ShapelessRecipeBuilder pad(Item item, int amount) {
			ItemStack s = new ItemStack(item);
			for (int i = 0; i < amount; i++)
				inputs.add(s);
			return this;
		}
		public ShapelessRecipeBuilder pad(Block block, int amount) {
			ItemStack s = new ItemStack(block);
			for (int i = 0; i < amount; i++)
				inputs.add(s);
			return this;
			
		}
		public ShapelessRecipeBuilder pad(ItemStack item, int amount) {
			checkNotNull(item);
			for (int i = 0; i < amount; i++)
				inputs.add(item);
			return this;
		}
		public ShapelessOreRecipeBuilder pad(String ore, int amount) {
			return new ShapelessOreRecipeBuilder(Lists.<Object>newArrayList(inputs)).pad(ore, amount);
		}
		@Override public ShapelessRecipes build() {
			return new ShapelessRecipes(result, Lists.newArrayList(inputs));
		}
	}
	
	public class ShapelessOreRecipeBuilder extends AbstractBuilder {
		private final List<Object> inputs;
		private ShapelessOreRecipeBuilder(List<Object> inputs) {
			this.inputs = inputs;
		}

		public ShapelessOreRecipeBuilder pad(Item... items) {
			for (Item item : items)
				inputs.add(new ItemStack(item));
			return this;
		}
		public ShapelessOreRecipeBuilder pad(Block... blocks) {
			for (Block block : blocks)
				inputs.add(new ItemStack(block));
			return this;
		}
		public ShapelessOreRecipeBuilder pad(ItemStack... items) {
			for (ItemStack item : items)
				inputs.add(checkNotNull(item));
			return this;
		}
		public ShapelessOreRecipeBuilder pad(String... ores) {
			for (String ore : ores)
				inputs.add(checkNotNull(ore));
			return this;
		}
		
		public ShapelessOreRecipeBuilder pad(Item item, int amount) {
			ItemStack s = new ItemStack(item);
			for (int i = 0; i < amount; i++)
				inputs.add(s);
			return this;
		}
		public ShapelessOreRecipeBuilder pad(Block block, int amount) {
			ItemStack s = new ItemStack(block);
			for (int i = 0; i < amount; i++)
				inputs.add(s);
			return this;
			
		}
		public ShapelessOreRecipeBuilder pad(ItemStack item, int amount) {
			checkNotNull(item);
			for (int i = 0; i < amount; i++)
				inputs.add(item);
			return this;
		}
		public ShapelessOreRecipeBuilder pad(String ore, int amount) {
			checkNotNull(ore);
			for (int i = 0; i < amount; i++)
				inputs.add(ore);
			return this;
		}
		@Override public ShapelessOreRecipe build() {
			return new ShapelessOreRecipe(result, Lists.newArrayList(inputs));
		}
	}
	
	
}
