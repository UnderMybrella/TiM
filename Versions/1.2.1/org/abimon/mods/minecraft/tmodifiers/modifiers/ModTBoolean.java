package org.abimon.mods.minecraft.tmodifiers.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.modifiers.tools.ModBoolean;

public class ModTBoolean extends ModBoolean {
	public String color;
	public String tooltipName;

	public ModTBoolean(ItemStack[] items, String tag, String color,
			String tip) {
		super(items, -1, tag, color, tip);
		this.color = color;
		tooltipName = tip;
	}

	public ModTBoolean(ItemStack[] items, int effect, String tag, String color,
			String tip) {
		super(items, effect, tag, color, tip);		
		this.color = color;
		tooltipName = tip;
	}

	@Override
	protected boolean canModify (ItemStack tool, ItemStack[] input)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		return !tags.getBoolean(key);
	}

	public void addMatchingEffect (ItemStack input){
		if(effectIndex == -1)
			return;
		else
			super.addMatchingEffect(input);
	}

	@Override
	/** Handle the modifiers yourself, lazy bugger */
	public void modify (ItemStack[] input, ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		tags.setBoolean(key, true);
		addToolTip(tool, color + tooltipName, color + key);
	}

	public ItemStack decreaseModifiers(ItemStack tool, int modifier){
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		int modifiers = tags.getInteger("Modifiers");
		modifiers -= modifier;
		tags.setInteger("Modifiers", modifiers);
		return tool;
	}
}
