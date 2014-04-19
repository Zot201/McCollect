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
import static com.google.common.collect.Collections2.filter;
import static com.google.common.collect.Collections2.transform;
import static zotmc.collect.FluentPredicate.alwaysTrue;
import static zotmc.collect.Matrixs.horizontalMirror;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.RecipeSorter.Category;
import zotmc.collect.FluentPredicate;
import zotmc.collect.Matrix;
import zotmc.collect.recipe.RecipeFinder.SearchManager.Filtered;
import zotmc.collect.recipe.RecipeFinder.SearchManager.SizeUndetermined;
import zotmc.collect.recipe.RecipeFinder.SearchManager.SizedSizeUndetermined;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

public class RecipeFinder {
	
	private static final SearchManager _default = new SearchManager();
	
	
	public static SizeUndetermined forResult(Item item) {
		return _default.forResult(item);
	}
	public static SizeUndetermined forResult(Item item, Function<IRecipe, RecipeView> factory) {
		return _default.forResult(item, factory);
	}
	
	public static SizeUndetermined forResult(Block block) {
		return _default.forResult(block);
	}
	public static SizeUndetermined forResult(Block block, Function<IRecipe, RecipeView> factory) {
		return _default.forResult(block, factory);
	}

	public static SizedSizeUndetermined forResult(ItemStack item) {
		return _default.forResult(item);
	}
	public static SizedSizeUndetermined forResult(ItemStack item, Function<IRecipe, RecipeView> factory) {
		return _default.forResult(item, factory);
	}
	
	public static Filtered forResult(Predicate<ItemStack> filter) {
		return _default.forResult(filter);
	}
	public static Filtered forResult(Predicate<ItemStack> filter, Function<IRecipe, RecipeView> factory) {
		return _default.forResult(filter, factory);
	}
	
	
	
	private static FluentPredicate<RecipeView> IS_APPLICABLE =
			new FluentPredicate<RecipeView>() {
		@Override public boolean apply(RecipeView input) {
			return input.isApplicable();
		}
	};
	private static Function<RecipeView, ItemStack> GET_OUTPUT =
			new Function<RecipeView, ItemStack>() {
		@Override public ItemStack apply(RecipeView input) {
			return input.getOutput();
		}
	};
	private static final Function<RecipeView, IRecipe> GET_RECIPE =
			new Function<RecipeView, IRecipe>() {
		@Override public IRecipe apply(RecipeView input) {
			return input.getRecipe();
		}
	};
	private static final Function<RecipeView, RecipeView> GET_DEFENSIVE_VIEW =
			new Function<RecipeView, RecipeView>() {
		@Override public RecipeView apply(RecipeView input) {
			return input.getDefensiveView();
		}
	};
	
	private static Predicate<RecipeView> resultFilter(final ItemStack item) {
		checkNotNull(item);
		return Predicates.compose(new Predicate<ItemStack>() {
			@Override public boolean apply(ItemStack input) {
				return input != null
						&& input.getItem() == item.getItem()
						&& input.getItemDamage() == item.getItemDamage()
						&& Objects.equal(item.stackTagCompound, input.stackTagCompound);
			}
		}, GET_OUTPUT);
	}
	
	
	
	public static class SearchManager {
		
		private final Function<IRecipe, RecipeView> defaultFactory;
		public SearchManager() {
			this(BasicRecipeView.STRICT_MATCHING);
		}
		public SearchManager(Function<IRecipe, RecipeView> defaultFactory) {
			this.defaultFactory = defaultFactory;
		}
		
		
		public SizeUndetermined forResult(Item item) {
			return forResult(item, defaultFactory);
		}
		public SizeUndetermined forResult(Item item, Function<IRecipe, RecipeView> factory) {
			return forResultSizeUndetermined(new ItemStack(item), factory);
		}
		public SizeUndetermined forResult(Block block) {
			return forResult(block, defaultFactory);
		}
		public SizeUndetermined forResult(Block block, Function<IRecipe, RecipeView> factory) {
			return forResultSizeUndetermined(new ItemStack(block), factory);
		}
		SizeUndetermined forResultSizeUndetermined(ItemStack item, Function<IRecipe, RecipeView> factory) {
			return new SizeUndetermined(resultFilter(item), factory);
		}

		public SizedSizeUndetermined forResult(ItemStack item) {
			return forResult(item, defaultFactory);
		}
		public SizedSizeUndetermined forResult(ItemStack item, Function<IRecipe, RecipeView> factory) {
			return new SizedSizeUndetermined(resultFilter(item), factory, item.stackSize);
		}

		public Filtered forResult(Predicate<ItemStack> filter) {
			return forResult(filter, defaultFactory);
		}
		public Filtered forResult(Predicate<ItemStack> filter, Function<IRecipe, RecipeView> factory) {
			return new Filtered(Predicates.compose(filter, GET_OUTPUT), factory);
		}
		

		@SuppressWarnings("unchecked")
		public List<IRecipe> entireRecipeList() {
			return CraftingManager.getInstance().getRecipeList();
		}
		
		
		// (Important!!) Filtered collection does not support iterator().remove()
		public class Filtered implements Iterable<RecipeView> {
			
			private final Collection<RecipeView> unfiltered, targets;
			protected final Predicate<RecipeView> filter;
			protected final Function<IRecipe, RecipeView> factory;
			
			private Filtered(Predicate<RecipeView> filter,
					Function<IRecipe, RecipeView> factory) {
				this(filter(transform(entireRecipeList(), factory), IS_APPLICABLE),
						filter,
						factory);
			}
			private Filtered(Collection<RecipeView> unfiltered,
					Function<IRecipe, RecipeView> factory) {
				this(unfiltered,
						alwaysTrue().<RecipeView>cast(),
						factory);
			}
			private Filtered(Collection<RecipeView> unfiltered, Predicate<RecipeView> filter,
					Function<IRecipe, RecipeView> factory) {
				this.unfiltered = unfiltered;
				targets = filter(unfiltered, filter);
				this.filter = filter;
				this.factory = factory;
			}
			
			public List<IRecipe> getRecipes() {
				return ImmutableList.copyOf(transform(targets, GET_RECIPE));
			}
			
			@Override public Iterator<RecipeView> iterator() {
				return Iterators.transform(targets.iterator(), GET_DEFENSIVE_VIEW);
			}
			
	
			public Filtered using(Item item) {
				return using(RecipeElement.equalTo(item));
			}
			public Filtered using(Block block) {
				return using(RecipeElement.equalTo(block));
			}
			public Filtered using(String ore) {
				return using(RecipeElement.equalTo(ore));
			}
			public Filtered using(ItemStack item) {
				return using(RecipeElement.equalTo(item));
			}
			public Filtered using(final Predicate<RecipeElement> filter) {
				return new Filtered(
						FluentPredicate.from(this.filter).and(new Predicate<RecipeView>() {
							@Override public boolean apply(RecipeView input) {
								return Iterables.any(input.getInput(), filter);
							}
						}), factory);
			}
	
			public Filtered usingExactly(Multiset<RecipeElement> shapelessInput) {
				return usingExactly(true, shapelessInput);
			}
			public Filtered usingExactly(
					final boolean shapelessOnly, final Multiset<RecipeElement> shapelessInput) {
				
				checkNotNull(shapelessInput);
				return new Filtered(new FluentPredicate<RecipeView>() {
					@Override public boolean apply(RecipeView r) {
						try {
							return shapelessInput.equals(shapelessOnly ?
									r.getShapelessInput() : HashMultiset.create(r.getInput()));
						} catch (IllegalArgumentException ignored) { }
						
						return false;
					}
				}.and(filter), factory);
			}
			
			public Filtered usingExactly(Matrix<RecipeElement> shapedInput) {
				return usingExactly(true, shapedInput);
			}
			public Filtered usingExactly(
					final boolean isMirrored, final Matrix<RecipeElement> shapedInput) {
				
				checkNotNull(shapedInput);
				return new Filtered(new FluentPredicate<RecipeView>() {
					@Override public boolean apply(RecipeView r) {
						try {
							if (isMirrored && r.isMirrored()) {
								Matrix<RecipeElement> matrix = r.getShapedInput();
								
								return shapedInput.equals(matrix)
										|| horizontalMirror(shapedInput).equals(matrix);
							}
							
							else if (!isMirrored && !r.isMirrored())
								return shapedInput.equals(r.getShapedInput());
							
							else
								return false;
							
						} catch (IllegalArgumentException ignored) { }
						
						return false;
					}
				}.and(filter), factory);
			}
			
			
			public Filtered resize(int size) { //keeping old size
				List<IRecipe> toRemove = Lists.newArrayList();
				List<IRecipe> toAdd = Lists.newArrayList();
				
				List<RecipeView> converted = Lists.newArrayList();
	
				for (RecipeView r : targets) {
					ItemStack output = r.getOutput().copy();
					output.stackSize = size;
					
					if (r.setOutput(output)) {
						toAdd.add(r.getRecipe());
						toRemove.add(r.getOldRecipe());
					}
	
					if (r.hasChange())
						converted.add(r);
				}
				
				entireRecipeList().removeAll(toRemove);
				entireRecipeList().addAll(toAdd);
				
				return new Filtered(converted, factory);
			}
			
			public Filtered adapt(Item item) { //keeping old size
				List<IRecipe> toRemove = Lists.newArrayList();
				List<IRecipe> toAdd = Lists.newArrayList();
				
				Set<RecipeView> converted = Sets.newHashSet();
	
				for (RecipeView r : targets) {
					ItemStack output = new ItemStack(item, r.getOutput().stackSize);
					
					if (r.setOutput(output)) {
						toAdd.add(r.getRecipe());
						toRemove.add(r.getOldRecipe());
					}
	
					if (r.hasChange())
						converted.add(r);
				}
	
				entireRecipeList().removeAll(toRemove);
				entireRecipeList().addAll(toAdd);
				
				return new Filtered(converted, factory);
			}
			public Filtered adapt(Block block) { //keeping old size
				return adapt(Item.getItemFromBlock(block));
			}
			
			public SizedAdapted adapt(ItemStack item) { //keeping old size
				List<IRecipe> toRemove = Lists.newArrayList();
				List<IRecipe> toAdd = Lists.newArrayList();
				
				List<RecipeView> converted = Lists.newArrayList();
	
				for (RecipeView r : targets) {
					ItemStack output = item.copy();
					output.stackSize = r.getOutput().stackSize;
					
					if (r.setOutput(output)) {
						toAdd.add(r.getRecipe());
						toRemove.add(r.getOldRecipe());
					}
	
					if (r.hasChange())
						converted.add(r);
				}
				
				entireRecipeList().removeAll(toRemove);
				entireRecipeList().addAll(toAdd);
				
				return new SizedAdapted(converted, factory, item.stackSize);
			}
			
			
			
			public Filtered replace(Predicate<RecipeElement> filter, Item item) {
				return replace(filter, new RecipeElement(item));
			}
			public Filtered replace(Predicate<RecipeElement> filter, Block block) {
				return replace(filter, new RecipeElement(block));
			}
			public Filtered replace(Predicate<RecipeElement> filter, String ore) {
				return replace(filter, new RecipeElement(ore));
			}
			public Filtered replace(Predicate<RecipeElement> filter, ItemStack item) {
				return replace(filter, new RecipeElement(item));
			}
			
			public Filtered replace(Predicate<RecipeElement> filter, RecipeElement element) {
				List<IRecipe> toRemove = Lists.newArrayList();
				List<IRecipe> toAdd = Lists.newArrayList();
				
				List<RecipeView> converted = Lists.newArrayList();
				
				for (RecipeView r : targets) {
					if (r.replaceInput(filter, element)) {
						toAdd.add(r.getRecipe());
						toRemove.add(r.getOldRecipe());
					}
					
					if (r.hasChange())
						converted.add(r);
				}
				
				entireRecipeList().removeAll(toRemove);
				entireRecipeList().addAll(toAdd);
				
				return new Filtered(converted, factory);
			}
			
			
			
			public Filtered reformulate(Multiset<RecipeElement> inputs) {
				List<IRecipe> toRemove = Lists.newArrayList();
				List<IRecipe> toAdd = Lists.newArrayList();
				
				List<RecipeView> converted = Lists.newArrayList();
				
				for (RecipeView r : targets) {
					if (r.setInput(inputs)) {
						toAdd.add(r.getRecipe());
						toRemove.add(r.getOldRecipe());
					}
	
					if (r.hasChange())
						converted.add(r);
				}
	
				entireRecipeList().removeAll(toRemove);
				entireRecipeList().addAll(toAdd);
				
				return new Filtered(converted, factory);
			}
			
			public Filtered reformulate(Matrix<RecipeElement> inputs) {
				return reformulate(inputs, true);
			}
			
			public Filtered reformulate(Matrix<RecipeElement> inputs, boolean mirrored) {
				List<IRecipe> toRemove = Lists.newArrayList();
				List<IRecipe> toAdd = Lists.newArrayList();
				
				List<RecipeView> converted = Lists.newArrayList();
	
				for (RecipeView r : targets) {
					if (r.setInput(inputs, mirrored)) {
						toAdd.add(r.getRecipe());
						toRemove.add(r.getOldRecipe());
					}
	
					if (r.hasChange())
						converted.add(r);
				}
	
				entireRecipeList().removeAll(toRemove);
				entireRecipeList().addAll(toAdd);
				
				return new Filtered(converted, factory);
			}
			
	
			
			public Filtered removeDuplicates() {
				Set<RecipeView> uniqueRecipes = Sets.newHashSet(targets);
				List<IRecipe> toAdd = FluentIterable.from(uniqueRecipes)
						.transform(GET_RECIPE).toList();
	
				entireRecipeList().removeAll(getRecipes());
				entireRecipeList().addAll(toAdd);
				
				return new Filtered(uniqueRecipes, factory);
			}
			
			public boolean clearRecipes() {
				boolean ret = !targets.isEmpty();
				if (ret)
					entireRecipeList().removeAll(getRecipes());
				return ret;
			}
			
			public void prioritizeRecipes() {
				List<IRecipe> toRemove = Lists.newArrayList();
				List<IRecipe> toAdd = Lists.newArrayList();
				
				for (RecipeView r : targets) {
					toAdd.add(r.getPrioritized());
					toRemove.add(r.getOldRecipe());
				}
	
				entireRecipeList().removeAll(toRemove);
				entireRecipeList().addAll(0, toAdd);
			}
			
		}
		
		
		public class SizedAdapted extends Filtered {
			private final int size;
			private SizedAdapted(List<RecipeView> unfiltered,
					Function<IRecipe, RecipeView> factory, int size) {
				super(unfiltered, factory);
				this.size = size;
			}
			public Filtered regardingSize() {
				return resize(size);
			}
		}
		
	
		public class Undetermined extends Filtered {
			private Undetermined(Predicate<RecipeView> filter,
					Function<IRecipe, RecipeView> factory) {
				super(filter, factory);
			}
			public Filtered ofShapedInput() {
				return new Filtered(new FluentPredicate<RecipeView>() {
					@Override public boolean apply(RecipeView input) {
						return input.getCategory() == Category.SHAPED;
					}
				}.and(filter), factory);
			}
			public Filtered ofShapelessInput() {
				return new Filtered(new FluentPredicate<RecipeView>() {
					@Override public boolean apply(RecipeView input) {
						return input.getCategory() == Category.SHAPELESS;
					}
				}.and(filter), factory);
			}
		}
		
		
		public class SizeUndetermined extends Undetermined {
			private SizeUndetermined(Predicate<RecipeView> filter,
					Function<IRecipe, RecipeView> factory) {
				super(filter, factory);
			}
			public Undetermined inSize(final int size) {
				return new Undetermined(new FluentPredicate<RecipeView>() {
					@Override public boolean apply(RecipeView input) {
						try {
							return input.getOutput().stackSize == size;
						} catch (NullPointerException ignored) { }
						return false;
					}
				}.and(filter), factory);
			}
		}
		
		
		public class SizedSizeUndetermined extends SizeUndetermined {
			private final int size;
			private SizedSizeUndetermined(Predicate<RecipeView> filter,
					Function<IRecipe, RecipeView> factory, int size) {
				super(filter, factory);
				this.size = size;
			}
			public Undetermined regardingSize() {
				return inSize(size);
			}
		}
		
	}
	
	
}
