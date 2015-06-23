package org.abimon.mods.minecraft.tmodifiers.modifiers;

import org.abimon.mods.minecraft.tmodifiers.TModifiers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.ModRedstone;

public class ModCustomHaste extends ModRedstone {

	public ModCustomHaste(int effect, ItemStack[] items, int[] values, String tooltip) {
		super(effect, items, values);
		tooltipName = tooltip;
	}
	
	public ModCustomHaste(int effect, ItemStack[] items, int[] values, String tooltip, int max) {
		super(effect, items, values);
		tooltipName = tooltip;
		this.max = max;
	}
	
	public void addMatchingEffect (ItemStack input){
		if(effectIndex == -1)
			return;
		else
			super.addMatchingEffect(input);
	}
	
	@Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        int[] keyPair;
        int increase = matchingAmount(input);
        int current = 0;
        if (tags.hasKey(key))
        {
            keyPair = tags.getIntArray(key);
            if (keyPair[0] % max == 0)
            {
                keyPair[0] += increase;
                keyPair[1] += max;
                tags.setIntArray(key, keyPair);

                int modifiers = tags.getInteger("Modifiers");
                modifiers -= 1;
                tags.setInteger("Modifiers", modifiers);
            }
            else
            {
                keyPair[0] += increase;
                tags.setIntArray(key, keyPair);
            }
            current = keyPair[0];
            updateModTag(tool, keyPair);
        }
        else
        {
            int modifiers = tags.getInteger("Modifiers");
            modifiers -= 1;
            tags.setInteger("Modifiers", modifiers);
            String modName = tooltipName + "(" + increase + "/" + max + ")";
            int tooltipIndex = addToolTip(tool, tooltipName, modName);
            keyPair = new int[] { increase, max, tooltipIndex };
            current = keyPair[0];
            tags.setIntArray(key, keyPair);
        }

        int miningSpeed = tags.getInteger("MiningSpeed");
        int boost = 8 + ((current - 1) / max * 2);
        Item temp = tool.getItem();
        if (temp instanceof ToolCore)
        {
            ToolCore toolcore = (ToolCore) temp;
            if (toolcore.durabilityTypeHandle() == 2)
                boost += 2;
            if (toolcore.durabilityTypeAccessory() == 2)
                boost += 2;
            if (toolcore.durabilityTypeExtra() == 2)
                boost += 2;
        }
        miningSpeed += (increase * boost);
        tags.setInteger("MiningSpeed", miningSpeed);

        String[] type = { "MiningSpeed2", "MiningSpeedHandle", "MiningSpeedExtra" };

        for (int i = 0; i < 3; i++)
        {
            if (tags.hasKey(type[i]))
            {
                int speed = tags.getInteger(type[i]);
                speed += (increase * boost);
                tags.setInteger(type[i], speed);
            }
        }
    }
	
    void updateModTag (ItemStack tool, int[] keys)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        String tip = "ModifierTip" + keys[2];
        String modName = tooltipName + "(" + keys[0] + "/" + keys[1] + ")";
        tags.setString(tip, modName);
    }

}
