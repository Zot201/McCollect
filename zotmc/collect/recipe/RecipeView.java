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

import java.util.Collection;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.RecipeSorter.Category;
import zotmc.collect.Matrix;

import com.google.common.base.Predicate;
import com.google.common.collect.Multiset;

public interface RecipeView {
	
	public boolean isApplicable();
	
	public boolean hasChange();
	
	public IRecipe getRecipe();
	
	public IRecipe getOldRecipe();
	
	public IRecipe getPrioritized();
	
	
	
	public Class<? extends IRecipe> getRecipeClass();
	
	public int getRecipeSize();
	
	public RecipeView getDefensiveView();
	
	

	public ItemStack getOutput();
	

	public Category getCategory();
	
	public Collection<RecipeElement> getInput();
	
	public Multiset<RecipeElement> getShapelessInput();
	
	public Matrix<RecipeElement> getShapedInput();
	
	public boolean isMirrored();
	
	
	
	public boolean setOutput(ItemStack output);
	
	
	public boolean replaceInput(Predicate<RecipeElement> filter, RecipeElement element);
	
	public boolean setInput(Multiset<RecipeElement> inputs);
	
	public boolean setInput(Matrix<RecipeElement> inputs, boolean mirrored);
	
	
	@Override public int hashCode();
	
	@Override public boolean equals(Object obj);
	
	@Override public String toString();
	
}
