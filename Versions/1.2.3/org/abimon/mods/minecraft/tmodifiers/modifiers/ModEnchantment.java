package org.abimon.mods.minecraft.tmodifiers.modifiers;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import tconstruct.library.tools.ToolCore;

public class ModEnchantment extends ModTInteger {

	int enchantmentID = 0;
	int level = 1;
	int maxLevel = 1;

	String[] validTypes = new String[0];

	public ModEnchantment(ItemStack[] items, int effect, String dataKey, int increase, int increase2, String c,
			String tip, int max, int maxApp, int enchID, int level, int maxLevel) {
		super(items, effect, dataKey, increase, increase2, c, tip, max, maxApp);
		this.enchantmentID = enchID;

		this.level = level;
		this.maxLevel = maxLevel;
	}


	public void modify (ItemStack[] input, ItemStack tool)
	{
		int currentLevel = EnchantmentHelper.getEnchantmentLevel(enchantmentID, tool);
		if(currentLevel + level <= maxLevel){
			Map enchantments = EnchantmentHelper.getEnchantments(tool);
			enchantments.put(enchantmentID, currentLevel + level);
			EnchantmentHelper.setEnchantments(enchantments, tool);
			super.modifyDecrease(input, tool);
		}
	}

	public boolean validType (ToolCore tool)
	{
		List list = Arrays.asList(tool.getTraits());

		for(String type : validTypes)
			if(list.contains(type))
				return true;
		return validTypes.length == 0;
	}

}
