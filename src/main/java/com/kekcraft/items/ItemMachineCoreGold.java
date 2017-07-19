package com.kekcraft.items;

import com.kekcraft.RecipeHandler;

import net.minecraftforge.oredict.ShapedOreRecipe;

public class ItemMachineCoreGold extends KekCraftItem {
	public ItemMachineCoreGold() {
		super("MachineCoreGold");
		RecipeHandler.RECIPES
				.add(new ShapedOreRecipe(this, " A ", "ABA", " A ", 'A', "ingotSteel", 'B', "circuitGold"));
	}
}