package org.abimon.mods.minecraft.tmodifiers.modifiers;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.modifiers.tools.ModBoolean;

public class ModLore extends ModTBoolean {
	public ModLore(ItemStack[] items, String tag, String color,
			String tip) {
		super(items, -1, tag, color, tip);
		this.color = color;
        tooltipName = tip;
	}
	
	public ModLore(ItemStack[] items, int effect, String tag, String color,
			String tip) {
		super(items, effect, tag, color, tip);		
		this.color = color;
        tooltipName = tip;
	}

}
