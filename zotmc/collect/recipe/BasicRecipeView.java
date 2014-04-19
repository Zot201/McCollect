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

import static com.google.common.collect.Multisets.unmodifiableMultiset;
import static java.util.Collections.unmodifiableCollection;
import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;
import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;
import static net.minecraftforge.oredict.RecipeSorter.Category.UNKNOWN;
import static zotmc.collect.Conversions.castRaw;
import static zotmc.collect.Matrixs.horizontalMirror;
import static zotmc.collect.Matrixs.unmodifiableMatrix;
import static zotmc.collect.forge.McCollectInit.MODID;
import static zotmc.collect.recipe.RecipeElement.TO_ITEM_STACK;
import static zotmc.collect.recipe.RecipeElement.fromItemStack;
import static zotmc.collect.recipe.Reflections.Fields.HEIGHT;
import static zotmc.collect.recipe.Reflections.Fields.INPUT;
import static zotmc.collect.recipe.Reflections.Fields.MIRRORED;
import static zotmc.collect.recipe.Reflections.Fields.OUTPUT_S;
import static zotmc.collect.recipe.Reflections.Fields.OUTPUT_SL;
import static zotmc.collect.recipe.Reflections.Fields.RECIPE_OUTPUT_S;
import static zotmc.collect.recipe.Reflections.Fields.RECIPE_OUTPUT_SL;
import static zotmc.collect.recipe.Reflections.Fields.WIDTH;
import static zotmc.collect.recipe.StackInfos.asInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import zotmc.collect.Conversions;
import zotmc.collect.Matrix;
import zotmc.collect.delegate.DelegateList;
import zotmc.collect.recipe.RecipeElement.AbstractMatrix;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

public class BasicRecipeView implements RecipeView {
	
	public static final Function<IRecipe, RecipeView> STRICT_MATCHING = new Function<IRecipe, RecipeView>() {
		@Override public RecipeView apply(IRecipe input) {
			return new BasicRecipeView(input, true);
		}
	};
	public static final Function<IRecipe, RecipeView> LOOSE_MATCHING = new Function<IRecipe, RecipeView>() {
		@Override public RecipeView apply(IRecipe input) {
			return new BasicRecipeView(input, false);
		}
	};
	
	

	protected final boolean strictMatching;
	protected IRecipe oldRecipe = null, recipe;
	protected boolean hasChange;
	
	public BasicRecipeView(IRecipe recipe, boolean strictMatching) {
		this.recipe = recipe;
		this.strictMatching = strictMatching;
	}
	
	protected boolean compare(Class<? extends IRecipe> child, Class<? extends IRecipe> parent) {
		return strictMatching ? child == parent : parent.isAssignableFrom(child);
	}
	
	private static final List<Class<? extends IRecipe>> DISCRIMINATOR =
			ImmutableList.of(ShapedRecipes.class, ShapelessRecipes.class,
					ShapedOreRecipe.class, ShapelessOreRecipe.class);
	
	@Override public boolean isApplicable() {
		for (Class<? extends IRecipe> d : DISCRIMINATOR)
			if (compare(recipe.getClass(), d))
				return true;
		
		return false;
	}
	
	@Override public boolean hasChange() {
		return hasChange;
	}
	
	@Override public IRecipe getOldRecipe() {
		return oldRecipe != null ? oldRecipe : recipe;
	}

	@Override public IRecipe getRecipe() {
		return recipe;
	}
	
	
	
	@Override public Category getCategory() {
		if (recipe instanceof ShapedRecipes || recipe instanceof ShapedOreRecipe)
			return SHAPED;
		
		if (recipe instanceof ShapelessRecipes || recipe instanceof ShapelessOreRecipe)
			return SHAPELESS;
		
		return UNKNOWN;
	}
	
	
	
	protected void eraseState() {
		hasChange = false;
		oldRecipe = null;
	}
	
	
	
	@Override public IRecipe getPrioritized() {
		if (recipe instanceof ShapedRecipes)
			return new PrioritizedShaped(
					getOutput(), getShapedInput());
		
		if (recipe instanceof ShapelessRecipes)
			return new PrioritizedShapeless(
					getOutput(), getShapelessInput());
		
		if (recipe instanceof ShapedOreRecipe)
			return new PrioritizedShapedOre(
					getOutput(), getShapedInput(), isMirrored());
		
		if (recipe instanceof ShapelessOreRecipe)
			return new PrioritizedShapelessOre(
					getOutput(), getShapelessInput());
		
		throw new IllegalArgumentException();
	}
	
	
	@Override public Class<? extends IRecipe> getRecipeClass() {
		return recipe.getClass();
	}
	
	@Override public int getRecipeSize() {
		return recipe.getRecipeSize();
	}
	

	
	
	private static class PrioritizedShaped extends ShapedRecipes {
		private PrioritizedShaped(ItemStack out, Matrix<RecipeElement> in) {
			super(in.width(), in.height(),
					FluentIterable.from(Iterables.concat(in.rowList()))
						.transform(TO_ITEM_STACK).toArray(ItemStack.class), out);
		}
	}
	
	private static class PrioritizedShapeless extends ShapelessRecipes {
		private PrioritizedShapeless(ItemStack out, Multiset<RecipeElement> in) {
			super(out, Lists.newArrayList(Iterables.transform(in, TO_ITEM_STACK)));
		}
	}
	
	private static class PrioritizedShapedOre extends ShapedOreRecipe {
		private PrioritizedShapedOre(ItemStack out, Matrix<RecipeElement> in, boolean isMirrored) {
			super(out, isMirrored, ' ', Items.apple);
			
			WIDTH.set(this, in.width());
			HEIGHT.set(this, in.height());
			INPUT.set(this,
					FluentIterable.from(Iterables.concat(in.rowList()))
						.transform(TO_ORE_RECIPE_INPUT).toArray(Object.class));
		}
	}
	
	private static class PrioritizedShapelessOre extends ShapelessOreRecipe {
		private PrioritizedShapelessOre(ItemStack out, Multiset<RecipeElement> in) {
			super(out);
			
			castRaw(getInput()).addAll(Collections2.transform(in, TO_ORE_RECIPE_INPUT));
		}
	}
	
	static {
		String beforeShaped = "before:minecraft:shaped";
		
		RecipeSorter.register(
				MODID + ":prioritizedShaped", PrioritizedShaped.class, SHAPED, beforeShaped);
		RecipeSorter.register(
				MODID + ":prioritizedShapeless", PrioritizedShapeless.class, SHAPELESS, beforeShaped);
		RecipeSorter.register(
				MODID + ":prioritizedShapeOre", PrioritizedShapedOre.class, SHAPED, beforeShaped);
		RecipeSorter.register(
				MODID + ":prioritizedShapelessOre", PrioritizedShapelessOre.class, SHAPELESS, beforeShaped);
	}
	
	
	
	
	@Override public ItemStack getOutput() {
		return recipe.getRecipeOutput();
	}
	
	@Override public boolean setOutput(ItemStack output) {
		eraseState();
		
		if (recipe instanceof ShapedRecipes) {
			hasChange = true;
			RECIPE_OUTPUT_S.set(recipe, output);
			return false;
		}
		if (recipe instanceof ShapelessRecipes) {
			hasChange = true;
			RECIPE_OUTPUT_SL.set(recipe, output);
			return false;
		}
		if (recipe instanceof ShapedOreRecipe) {
			hasChange = true;
			OUTPUT_S.set(recipe, output);
			return false;
		}
		if (recipe instanceof ShapelessOreRecipe) {
			hasChange = true;
			OUTPUT_SL.set(recipe, output);
			return false;
		}
		
		throw new IllegalArgumentException();
	}
	
	
	
	@Override public Collection<RecipeElement> getInput() {
		return getInputView();
	}
	@Override public Multiset<RecipeElement> getShapelessInput() {
		if (getCategory() != SHAPELESS)
			throw new IllegalArgumentException();
		
		return getInputView().copyToMultiset();
	}
	@Override public Matrix<RecipeElement> getShapedInput() {
		return getInputView().asMatrix();
	}
	@Override public boolean isMirrored() {
		return getMirrored(recipe);
	}
	
	
	
	protected InputView getInputView() {
		if (recipe instanceof ShapedRecipes)
			return new InputView(recipe, Lists.transform(
					Arrays.asList(((ShapedRecipes) recipe).recipeItems), fromItemStack()));
		
		if (recipe instanceof ShapelessRecipes)
			return new InputView(recipe, Lists.transform(
					castRaw(((ShapelessRecipes) recipe).recipeItems, ItemStack.class), fromItemStack()));
		
		if (recipe instanceof ShapedOreRecipe)
			return new InputView(recipe, Lists.transform(
					Arrays.asList(((ShapedOreRecipe) recipe).getInput()), FROM_ORE_RECIPE_INPUT));
		
		if (recipe instanceof ShapelessOreRecipe)
			return new InputView(recipe, Lists.transform(
					castRaw(((ShapelessOreRecipe) recipe).getInput()), FROM_ORE_RECIPE_INPUT));
		
		throw new IllegalArgumentException();
	}
	
	
	
	protected static <T> Function<T, T> replaceFunction(final Predicate<T> criteria, final T replacement) {
		return new Function<T, T>() {
			@Override public T apply(T input) {
				return criteria.apply(input) ? replacement : input;
			}
		};
	}
	
	@Override public boolean replaceInput(Predicate<RecipeElement> filter, RecipeElement element) {
		eraseState();
		
		List<RecipeElement> inputs = getInputView();
		
		if (Iterables.any(inputs, filter)) {
			hasChange = true;
			
			IRecipe temp = recipe;
			recipe = explicit(
					recipe,
					Lists.transform(inputs, replaceFunction(filter, element)),
					recipe.getRecipeOutput());
			oldRecipe = temp;
			
			return true;
		}
		
		return false;
	}
	
	@Override public boolean setInput(Multiset<RecipeElement> inputs) {
		eraseState();
		hasChange = true;
		
		IRecipe temp = recipe;
		recipe = explicitShapeless(inputs, recipe.getRecipeOutput());
		oldRecipe = temp;
		
		return true;
	}
	@Override public boolean setInput(Matrix<RecipeElement> inputs, boolean mirrored) {
		eraseState();
		hasChange = true;
		
		IRecipe temp = recipe;
		recipe = explicitShaped(
				mirrored, inputs.width(), inputs.height(),
				ImmutableList.copyOf(Iterables.concat(inputs.rowList())),
				recipe.getRecipeOutput());
		oldRecipe = temp;
		
		return true;
	}
	
	@Override public int hashCode() {
		Hasher h = Hashing.goodFastHash(32).newHasher()
				.putInt(asInfo(getOutput()).hashCode());
		
		Category cat = getCategory();
		
		if (cat == SHAPED) {
			h.putBoolean(false).putBoolean(true);
			
			Matrix<RecipeElement> input = getShapedInput();
			if (isMirrored())
				h.putInt(input.hashCode() ^ horizontalMirror(input).hashCode());
			else
				h.putInt(input.hashCode());
		}
		
		else if (cat == SHAPELESS)
			h.putBoolean(true).putBoolean(false)
				.putInt(getShapelessInput().hashCode());
		
		else
			h.putBoolean(false).putBoolean(false);
		
		return h.hash().asInt();
	}
	
	@Override public boolean equals(Object obj) {
		if (obj == this)
			return true;
		
		if (obj instanceof BasicRecipeView) {
			BasicRecipeView r = (BasicRecipeView) obj;

			if (asInfo(getOutput()).equals(asInfo(r.getOutput()))) {
				
				Category cat = getCategory();
				
				if (cat == SHAPED && r.getCategory() == SHAPED) {
					
					boolean isMirrored = isMirrored();
					
					if (isMirrored && r.isMirrored()) {
						Matrix<RecipeElement>
						input = getShapedInput(),
						rInput = r.getShapedInput();
						
						return input.equals(rInput)
								|| horizontalMirror(input).equals(rInput);
					}
					else if (!isMirrored && !r.isMirrored())
						return getShapedInput().equals(r.getShapedInput());
					else
						return false;
				}
				
				else if (cat == SHAPELESS && r.getCategory() == SHAPELESS)
					return getShapelessInput().equals(r.getShapelessInput());
				
				else
					return r.getCategory() == cat;
			}
		}
		
		return false;
	}
	
	@Override public String toString() {
		Category cat = getCategory();
		
		if (cat == SHAPED)
			return String.format("%s = %s %s %s", getOutput(),
					cat,
					isMirrored() ? "mirrored" : "unmirrored",
					getShapedInput());
		
		else if (cat == SHAPELESS)
			return String.format("%s = %s %s", getOutput(),
					cat,
					getShapelessInput());
		
		return String.format("%s = %s", getOutput(), cat);
	}
	
	
	
	
	
	private static final Function<Object, RecipeElement> FROM_ORE_RECIPE_INPUT =
			new Function<Object, RecipeElement>() {
		@SuppressWarnings("unchecked")
		@Override public RecipeElement apply(Object input) {
			if (input == null || input instanceof ItemStack)
				return new RecipeElement((ItemStack) input);
			if (input instanceof ArrayList)
				return new RecipeElement((ArrayList<ItemStack>) input);
			throw new IllegalArgumentException();
		}
	};
	private static final Function<RecipeElement, Object> TO_ORE_RECIPE_INPUT =
			new Function<RecipeElement, Object>() {
		@Override public Object apply(RecipeElement input) {
			if (input.isOre())
				return input.ore;
			if (input.item != null)
				return new ItemStack(input.item, 1, input.metadata);
			return null;
		}
	};
	
	
	
	
	private static boolean getMirrored(IRecipe recipe) {
		if (recipe instanceof ShapedRecipes)
			return true;
		if (recipe instanceof ShapedOreRecipe)
			return MIRRORED.get(recipe);
		throw new IllegalArgumentException();
	}
	
	private static int getWidth(IRecipe recipe) {
		if (recipe instanceof ShapedRecipes)
			return ((ShapedRecipes) recipe).recipeWidth;
		if (recipe instanceof ShapedOreRecipe)
			return WIDTH.get(recipe);
		throw new IllegalArgumentException();
	}
	
	private static int getHeight(IRecipe recipe) {
		if (recipe instanceof ShapedRecipes)
			return ((ShapedRecipes) recipe).recipeHeight;
		if (recipe instanceof ShapedOreRecipe)
			return HEIGHT.get(recipe);
		throw new IllegalArgumentException();
	}
	
	
	
	private static IRecipe explicit(IRecipe original, Collection<RecipeElement> inputs, ItemStack result) {
		if (original instanceof ShapedRecipes) {
			ShapedRecipes r = (ShapedRecipes) original;
			return explicitShaped(true, r.recipeWidth, r.recipeHeight, inputs, result);
		}
		
		if (original instanceof ShapelessRecipes)
			return explicitShapeless(inputs, result);
		
		if (original instanceof ShapedOreRecipe) {
			boolean mirrored = MIRRORED.get(original);
			int w = WIDTH.get(original);
			int h = HEIGHT.get(original);
			return explicitShaped(mirrored, w, h, inputs, result);
		}
		
		if (original instanceof ShapelessOreRecipe)
			return explicitShapeless(inputs, result);
		
		throw new IllegalArgumentException();
	}
	
	private static IRecipe explicitShaped(
			boolean mirrored, int w, int h, Collection<RecipeElement> inputs, ItemStack result) {
		if (!mirrored || Iterables.any(inputs, RecipeElement.IS_ORE)) {
			ShapedOreRecipe r = new ShapedOreRecipe(result, mirrored, ' ', Items.apple);
			WIDTH.set(r, w);
			HEIGHT.set(r, h);
			INPUT.set(r, Collections2.transform(inputs, TO_ORE_RECIPE_INPUT).toArray(new Object[w * h]));
			return r;
		}
		
		return new ShapedRecipes(w, h,
				Collections2.transform(inputs, TO_ITEM_STACK).toArray(new ItemStack[w * h]), result);
	}
	
	private static IRecipe explicitShapeless(Collection<RecipeElement> inputs, ItemStack result) {
		if (Iterables.any(inputs, RecipeElement.IS_ORE)) {
			ShapelessOreRecipe r = new ShapelessOreRecipe(result);
			castRaw(r.getInput()).addAll(Collections2.transform(inputs, TO_ORE_RECIPE_INPUT));
			return r;
		}
		
		return new ShapelessRecipes(result, ImmutableList.copyOf(Collections2.transform(inputs, TO_ITEM_STACK)));
	}

	
	
	
	public static class InputView extends DelegateList<RecipeElement> {
		private final IRecipe recipe;
		private final List<RecipeElement> delegatee;
		private InputView(IRecipe recipe, List<RecipeElement> delegatee) {
			this.recipe = recipe;
			this.delegatee = Collections.unmodifiableList(delegatee);
		}
		@Override protected List<RecipeElement> delegatee() {
			return delegatee;
		}
		
		public Multiset<RecipeElement> copyToMultiset() {
			return ImmutableMultiset.copyOf(delegatee());
		}
		
		private InputMatrix asMatrix;
		public InputMatrix asMatrix() {
			if (asMatrix != null)
				return asMatrix;
			
			int h = getHeight(recipe), w = getWidth(recipe);
			if (w * h > size())
				throw new IllegalArgumentException();
			return asMatrix = new InputMatrix(h, w);
		}
		
		public class InputMatrix extends AbstractMatrix {
			private final int h, w;
			private InputMatrix(int h, int w) {
				this.h = h;
				this.w = w;
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
				return delegatee().get(rowIndex * width() + columnIndex);
			}
			private List<List<RecipeElement>> rowList;
			@Override public List<List<RecipeElement>> rowList() {
				return rowList != null ? rowList : (rowList = Lists.partition(delegatee(), width()));
			}
			
			private InputMatrix mirror;
			public InputMatrix mirror(){
				return mirror != null ? mirror : (mirror = new InputMatrix(h, w) {
					
					@Override public RecipeElement get(int rowIndex, int columnIndex) {
						return InputMatrix.this.get(width() - rowIndex, columnIndex);
					}
					private List<List<RecipeElement>> rowList;
					@Override public List<List<RecipeElement>> rowList() {
						return rowList != null ? rowList : (rowList = Lists.transform(
								InputMatrix.this.rowList(), Conversions.<RecipeElement>listReverse()));
					}
					@Override public InputMatrix mirror() {
						return InputMatrix.this;
					}
					
				});
			}
			
		}
	}
	
	
	
	@Override public RecipeView getDefensiveView() {
		return new DefensiveView();
	}
	
	private class DefensiveView implements RecipeView {
		
		private DefensiveView() { }
		
		@Override public RecipeView getDefensiveView() {
			return this;
		}
		
		@Override public boolean isApplicable() {
			return BasicRecipeView.this.isApplicable();
		}
		
		@Override public ItemStack getOutput() {
			return ItemStack.copyItemStack(BasicRecipeView.this.getOutput());
		}
		@Override public Category getCategory() {
			return BasicRecipeView.this.getCategory();
		}
		@Override public Collection<RecipeElement> getInput() {
			return unmodifiableCollection(BasicRecipeView.this.getInput());
		}
		@Override public Multiset<RecipeElement> getShapelessInput() {
			return unmodifiableMultiset(BasicRecipeView.this.getShapelessInput());
		}
		@Override public Matrix<RecipeElement> getShapedInput() {
			return unmodifiableMatrix(BasicRecipeView.this.getShapedInput());
		}
		
		@Override public Class<? extends IRecipe> getRecipeClass() {
			return BasicRecipeView.this.getRecipeClass();
		}
		@Override public int getRecipeSize() {
			return BasicRecipeView.this.getRecipeSize();
		}
		@Override public boolean isMirrored() {
			return BasicRecipeView.this.isMirrored();
		}
		@Override public int hashCode() {
			return BasicRecipeView.this.hashCode();
		}
		@Override public boolean equals(Object obj) {
			return BasicRecipeView.this.equals(obj);
		}
		@Override public String toString() {
			return BasicRecipeView.this.toString();
		}
		
		@Deprecated @Override public boolean hasChange() {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public IRecipe getRecipe() {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public IRecipe getOldRecipe() {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public IRecipe getPrioritized() {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean setOutput(ItemStack output) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean replaceInput(
				Predicate<RecipeElement> filter, RecipeElement element) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean setInput(
				Multiset<RecipeElement> inputs) {
			throw new UnsupportedOperationException();
		}
		@Deprecated @Override public boolean setInput(
				Matrix<RecipeElement> inputs, boolean mirrored) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	
}
