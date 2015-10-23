package org.abimon.mods.minecraft.tmodifiers.modifiers;

import org.abimon.mods.minecraft.tmodifiers.TModifiers;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.IModifyable;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.ModBoolean;
import tconstruct.util.config.PHConstruct;

public class ModCustomFlux extends ModTBoolean {
	
	public int maxEnergy = 80000;
	public int currEnergy = 0;
	public int extractionRate = 80;
	public int receiveRate = 200;
	
	public ModCustomFlux(ItemStack[] items, String tag, String color,
			String tip) {
		this(items, -1, tag, color, tip);
	}
	
	public ModCustomFlux(ItemStack[] items, int effect, String tag, String color,
			String tip) {
		this(items, effect, tag, color, tip, 80000);
	}
	
	public ModCustomFlux(ItemStack[] items, String tag, String color,
			String tip, int maxEnergy) {
		this(items, -1, tag, color, tip, maxEnergy, 0);
	}
	
	public ModCustomFlux(ItemStack[] items, int effect, String tag, String color,
			String tip, int maxEnergy) {
		this(items, effect, tag, color, tip, maxEnergy, 0);
	}
	
	public ModCustomFlux(ItemStack[] items, String tag, String color,
			String tip, int maxEnergy, int currEnergy) {
		this(items, -1, tag, color, tip, maxEnergy, currEnergy, 80);
	}
	
	public ModCustomFlux(ItemStack[] items, int effect, String tag, String color,
			String tip, int maxEnergy, int currEnergy) {
		this(items, effect, tag, color, tip, maxEnergy, currEnergy, 80);
	}
	
	public ModCustomFlux(ItemStack[] items, String tag, String color,
			String tip, int maxEnergy, int currEnergy, int extractionRate) {
		this(items, -1, tag, color, tip, maxEnergy, currEnergy, extractionRate, 200);
	}
	
	public ModCustomFlux(ItemStack[] items, int effect, String tag, String color,
			String tip, int maxEnergy, int currEnergy, int extractionRate) {
		this(items, effect, tag, color, tip, maxEnergy, currEnergy, extractionRate, 200);
	}
	
	public ModCustomFlux(ItemStack[] items, String tag, String color,
			String tip, int maxEnergy, int currEnergy, int extractionRate, int receiveRate) {
		this(items, -1, tag, color, tip, maxEnergy, currEnergy, extractionRate, receiveRate);
	}
	
	public ModCustomFlux(ItemStack[] items, int effect, String tag, String color,
			String tip, int maxEnergy, int currEnergy, int extractionRate, int receiveRate) {
		super(items, effect, tag, color, tip);
        this.maxEnergy = maxEnergy;
        this.currEnergy = currEnergy;
        this.extractionRate = extractionRate;
        this.receiveRate = receiveRate;
        
        TModifiers.keyToProperty.put(tip, ModProperty.FLUX);
	}

    @Override
    public boolean matches (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

        // not on ammo weapons, since they don't have durability technically
        String[] traits = ((IModifyable) tool.getItem()).getTraits();
        for(String trait : traits)
            if("ammo".equals(trait))
                return false;

        if(!super.matches(input, tool))
        	return false;

        // battery too big for our tool?
        if(PHConstruct.balancedFluxModifier && tags.getInteger("TotalDurability") < maxEnergy/1000) // durability needs to be at least 1/1000th of the charge
            return false;

        // check if we already have a flux modifier
        if (tags.getBoolean(key))
        {
            // only allow if it's an upgrade
            // remark: we use the ToolCores function here instead of accessing the tag directly, to achieve backwards compatibility with tools without tags.
            int a = maxEnergy;
            int b = ((ToolCore) tool.getItem()).getMaxEnergyStored(tool);
            return a > b;
        }
        // otherwise check if we have enough modfiers
        else if (tags.getInteger("Modifiers") < 1)
            return false;

        // all requirements satisfied!
        return true;
    }

	@Override
	public void modify(ItemStack[] recipe, ItemStack tool) {
		super.modify(recipe, tool);
		NBTTagCompound tags = tool.getTagCompound();

        // update modifiers (only applies if it's not an upgrade)
        if (!tags.hasKey(key))
        {
            int modifiers = tags.getCompoundTag("InfiTool").getInteger("Modifiers");
            modifiers -= 1;
            tags.getCompoundTag("InfiTool").setInteger("Modifiers", modifiers);
        }

        tags.getCompoundTag("InfiTool").setBoolean(key, true);

        // set the charge values
        int charge = currEnergy;

        // add already present charge in the tool
        if (tags.hasKey("Energy"))
            charge += tags.getInteger("Energy");
        int maxCharge = maxEnergy;

        int maxExtract = extractionRate;
        int maxReceive = receiveRate;

        // make sure we don't overcharge
        charge = Math.min(charge, maxCharge);

        tags.setInteger("Energy", charge);
        tags.setInteger("EnergyMax", maxCharge);
        tags.setInteger("EnergyExtractionRate", maxExtract);
        tags.setInteger("EnergyReceiveRate", maxReceive);

        tags.setInteger(key, 1);
        ToolCore toolcore = (ToolCore) tool.getItem();
        //tool.setItemDamage(1 + (toolcore.getMaxEnergyStored(tool) - charge) * (tool.getMaxDamage() - 1) / toolcore.getMaxEnergyStored(tool));
	}

}
