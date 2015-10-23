package org.abimon.mods.minecraft.tmodifiers.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.modifiers.tools.ModInteger;

public class ModTInteger extends ModInteger{

	public String color;
	public String tooltipName;

	public int initialIncrease = 1;
	public int secondaryIncrease = 1;
	public int max = 1;

	public int maxApplication;

	public int getValue(ItemStack tool){
		return tool.getTagCompound().getCompoundTag("InfiTool").getInteger(key);
	}

	public ModTInteger(ItemStack[] items, int effect, String dataKey,
			int increase, String c, String tip, int max, int maxApp) {
		super(items, effect, dataKey, increase, c, tip);
		this.color = c;
		tooltipName = tip;

		this.initialIncrease = secondaryIncrease = increase;
		this.max = max;
		this.maxApplication = maxApp;
	}

	public ModTInteger(ItemStack[] items, int effect, String dataKey,
			int increase, int increase2, String c, String tip, int max, int maxApp) {
		super(items, effect, dataKey, increase, increase2, c, tip);
		this.color = c;
		tooltipName = tip;
		this.initialIncrease = increase;
		this.secondaryIncrease = increase2;
		this.max = max;
		this.maxApplication = maxApp;
	}

	public ModTInteger(ItemStack[] items, String dataKey,
			int increase, String c, String tip, int max, int maxApp) {
		super(items, -1, dataKey, increase, c, tip);
		this.color = c;
		tooltipName = tip;

		this.initialIncrease = secondaryIncrease = increase;
		this.max = max;
		this.maxApplication = maxApp;
	}

	public ModTInteger(ItemStack[] items, String dataKey,
			int increase, int increase2, String c, String tip, int max, int maxApp) {
		super(items, -1, dataKey, increase, increase2, c, tip);
		this.color = c;
		tooltipName = tip;
		this.initialIncrease = increase;
		this.secondaryIncrease = increase2;
		this.max = max;
		this.maxApplication = maxApp;
	}

	public boolean matches (ItemStack[] recipe, ItemStack input){
		boolean matches = super.matches(recipe, input);
		return matches;
	}

	public void addMatchingEffect (ItemStack input){
		if(effectIndex == -1)
			return;
		else
			super.addMatchingEffect(input);
	}
	@Override
	public boolean canModify (ItemStack input, ItemStack[] recipe)
	{
		NBTTagCompound tags = input.getTagCompound().getCompoundTag(getTagName(input));
		if(tags.hasKey(key) && tags.getInteger(key + "-Applied") >= this.maxApplication)
			return false;
		int value = tags.getInteger(key);
		int[] modCounts = new int[this.stacks.size()];
		for(ItemStack item : recipe){
			ItemStack itemCopy = null;
			if(item != null){
				itemCopy = item.copy();
				itemCopy.stackSize = 1;
			}
			for(int i = 0; i < modCounts.length; i++)
				if(ItemStack.areItemStacksEqual(itemCopy, (ItemStack) stacks.get(i)))
					modCounts[i]++;
		}
		int mod = modCounts[0];
		for(int i : modCounts)
			if(i != mod)
				return false;

		if(mod <= 0)
			return false;

		int adding = mod * secondaryIncrease; //2
		if(!tags.hasKey(key))
		{
			adding = mod * initialIncrease;
			if(adding > max && (adding % max != 0))
				return false;
			if(adding < max)
				return tags.getInteger("Modifiers") > 1;
				int difference = (adding / max);
				if(max <= 1)
					difference = adding;
				return tags.getInteger("Modifiers") > difference;
		}
		if((adding + (value % max)) > (((value / max) + (value % max != 0 ? 1 : 0)) * max) && (adding + (value % max)) % max != 0)
			return false;
		if(value % max != 0)
			return true;
		int difference = (adding + (value % max)) - (value / max);
		if(max <= 1)
			difference = adding;
		return tags.getInteger("Modifiers") >= difference;
	}

	@Override
	/** Handle the modifiers yourself, lazy bugger */
	public void modify (ItemStack[] input, ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		if (tags.hasKey(key))
		{
			int increase = tags.getInteger(key);
			increase += secondaryIncrease;
			tags.setInteger(key, increase);
			updateModTag(tool, tags.getInteger(key + "-VAR"), color + tooltipName + " (" + tags.getInteger(key) + "/" + (((tags.getInteger(key) / max) + (tags.getInteger(key) % max != 0 ? 1 : 0)) * max) + ")", color + key);
		}
		else
		{
			tags.setInteger(key, initialIncrease);
			int var = addToolTip(tool, color + tooltipName, color + key);
			tags.setInteger(key + "-VAR", var);
		}
		updateModTag(tool, tags.getInteger(key + "-VAR"), color + tooltipName + " (" + tags.getInteger(key) + "/" + (((tags.getInteger(key) / max) + (tags.getInteger(key) % max != 0 ? 1 : 0)) * max) + ")", color + key);
	}

	void updateModTag (ItemStack tool, int var, String modName, String tiop)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		String tip = "ModifierTip" + var;
		String tiptip= "Tooltip" + var;
		tags.setString(tip, modName);
		tags.setString(tiptip, tiop + ((tags.getInteger(key) / max)> 0 ? " " + Integer.toHexString(tags.getInteger(key) / max) : ""));
	}

	public void modifyDecrease(ItemStack[] input, ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

		int[] modCounts = new int[this.stacks.size()];
		for(ItemStack item : input)
			for(int i = 0; i < modCounts.length; i++){
				if(item == null)
					break;
				ItemStack copy = item.copy();
				copy.stackSize = 1;
				if(ItemStack.areItemStacksEqual(copy, (ItemStack) stacks.get(i)))
					modCounts[i]++;
			}
		int mod = modCounts[0];
		if(mod == 0)
			return;

		int adding = mod * secondaryIncrease; //2
		int value = tags.getInteger(key);
		
		if (tags.hasKey(key))
		{
			int increase = value + adding;
			if(value % max == 0)
				if(adding % max > 0)
					decreaseModifiers(tool, (adding / max) + 1);
				else
					decreaseModifiers(tool, adding / max);
			tags.setInteger(key, value + adding);
		}
		else
		{
			tags.setInteger(key, initialIncrease);
			if(max > 1)
				if(adding < max)
					decreaseModifiers(tool, 1);
				else
					decreaseModifiers(tool, (adding - (adding / max)) / max);
			else
				decreaseModifiers(tool, adding);
			int var = addToolTip(tool, color + tooltipName, color + key);
			tags.setInteger(key + "-VAR", var);
		}
		updateModTag(tool, tags.getInteger(key + "-VAR"), color + tooltipName + " (" + tags.getInteger(key) + "/" + (((tags.getInteger(key) / max) + (tags.getInteger(key) % max != 0 ? 1 : 0)) * max) + ")", color + key);
	}

	public ItemStack decreaseModifiers(ItemStack tool, int modifier){
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		int modifiers = tags.getInteger("Modifiers");
		modifiers -= modifier;
		tags.setInteger("Modifiers", modifiers);
		tags.setInteger(key + "-Applied", tags.getInteger(key + "-Applied") + 1);
		return tool;
	}

}
