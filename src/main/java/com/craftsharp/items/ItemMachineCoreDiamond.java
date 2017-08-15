package com.craftsharp.items;

import com.craftsharp.RecipeHandler;

import net.minecraftforge.oredict.ShapedOreRecipe;

public class ItemMachineCoreDiamond extends KekCraftItem {
	public ItemMachineCoreDiamond() {
		super("MachineCoreDiamond");
		RecipeHandler.RECIPES
				.add(new ShapedOreRecipe(this, " A ", "ABA", " A ", 'A', "ingotSteel", 'B', "circuitDiamond"));
	}
}