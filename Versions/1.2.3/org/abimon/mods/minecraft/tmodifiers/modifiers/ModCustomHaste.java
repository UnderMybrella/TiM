package org.abimon.mods.minecraft.tmodifiers.modifiers;

import java.util.Arrays;
import java.util.List;

import org.abimon.mods.minecraft.tmodifiers.TModifiers;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.ItemModTypeFilter;

public class ModCustomHaste extends ItemModTypeFilter{

	public String tooltipName;
    public int max = 50;
	
	public ModCustomHaste(int effect, ItemStack[] items, int[] values, String tooltip) {
		super(effect, tooltip, items, values);
		tooltipName = tooltip;
	}
	
	public ModCustomHaste(int effect, ItemStack[] items, int[] values, String tooltip, int max) {
		super(effect, tooltip, items, values);
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
    protected boolean canModify (ItemStack tool, ItemStack[] input)
    {
        if (tool.getItem() instanceof ToolCore)
        {
            ToolCore toolItem = (ToolCore) tool.getItem();
            if (!validType(toolItem))
                return false;

            if (matchingAmount(input) > max)
                return false;

            NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
            if (!tags.hasKey(key))
                return tags.getInteger("Modifiers") > 0 && matchingAmount(input) <= max;

            int keyPair[] = tags.getIntArray(key);

            if (keyPair[0] + matchingAmount(input) <= keyPair[1])
                return true;
            else if (keyPair[0] == keyPair[1])
                return tags.getInteger("Modifiers") > 0;
        }

        return false;
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
            String modName = "\u00a74" + tooltipName + "(" + increase + "/" + max + ")";
            int tooltipIndex = addToolTip(tool, tooltipName, modName);
            keyPair = new int[] { increase, max, tooltipIndex };
            current = keyPair[0];
            tags.setIntArray(key, keyPair);
        }

        int miningSpeed = tags.getInteger("MiningSpeed");
        int boost = 8 + ((current - 1) / 50 * 2);
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
        String modName = "\u00a74" + tooltipName + "(" + keys[0] + "/" + keys[1] + ")";
        tags.setString(tip, modName);
    }

    public boolean validType (IModifyable input)
    {
        return input.getModifyType().equals("Tool");
    }

    public boolean validType (ToolCore tool)
    {
        List list = Arrays.asList(tool.getTraits());

        // handled by the windup modifier
        if(list.contains("windup"))
            return false;
        return list.contains("harvest") || list.contains("utility");
    }

}
