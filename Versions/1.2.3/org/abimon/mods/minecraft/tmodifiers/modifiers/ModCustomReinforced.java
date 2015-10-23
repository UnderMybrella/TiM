package org.abimon.mods.minecraft.tmodifiers.modifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ModCustomReinforced extends ModTInteger {

	public ModCustomReinforced(ItemStack[] items, int effect, String dataKey,
			int increase, String c, String tip, int max, int maxApp) {
		super(items, effect, dataKey, increase, c, tip, max, maxApp);
	}

	public ModCustomReinforced(ItemStack[] items, int effect, String dataKey,
			int increase, int increase2, String c, String tip, int max, int maxApp) {
		super(items, effect, dataKey, increase, increase2, c, tip, max, maxApp);
	}

	public ModCustomReinforced(ItemStack[] items, String dataKey,
			int increase, String c, String tip, int max, int maxApp) {
		super(items, dataKey, increase, c, tip, max, maxApp);
	}

	public ModCustomReinforced(ItemStack[] items, String dataKey,
			int increase, int increase2, String c, String tip, int max, int maxApp) {
		super(items, dataKey, increase, increase2, c, tip, max, maxApp);
	}

	@Override
	/** Handle the modifiers yourself, lazy bugger */
	public void modify (ItemStack[] input, ItemStack tool)
	{
		super.modify(input, tool);

		if(getValue(tool) % max == 0)
		{
			NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
			int reinforced = tags.getInteger("Unbreaking");
			reinforced += 1;
			tags.setInteger("Unbreaking", reinforced);
			super.decreaseModifiers(tool, 1);
		}
	}

}
