package org.abimon.mods.minecraft.tmodifiers;

import org.abimon.mods.minecraft.tmodifiers.modifiers.TActiveMod;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import tconstruct.library.ActiveToolMod;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.library.tools.ToolCore;

public class TModifiersToolMod extends ActiveToolMod {
	@Override
	public boolean beforeBlockBreak (ToolCore tool, ItemStack stack, int x, int y, int z, EntityLivingBase entity)
	{
		try{
			NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");

			if (entity instanceof EntityPlayer && ((EntityPlayer) entity).capabilities.isCreativeMode)
				return false;
			boolean rValue = false;
			for(ItemModifier mod : ModifyBuilder.instance.itemModifiers)
				if(mod instanceof TActiveMod && tags.hasKey(mod.key))
					if(((TActiveMod) mod).midStreamModify(tool, tags, stack, x, y, z, entity))
						rValue = true;

			return rValue;
		}
		catch(Throwable th){}
		return false;
	}

	public void updateTool (ToolCore tool, ItemStack stack, World world, Entity entity)
	{
		try{
			NBTTagCompound tags = stack.getTagCompound().getCompoundTag("InfiTool");

			for(ItemModifier mod : ModifyBuilder.instance.itemModifiers)
				if(mod instanceof TActiveMod && tags.hasKey(mod.key))
					((TActiveMod) mod).updateTool(tool, tags, stack, world, entity);
		}
		catch(Throwable th){}
	}

	/* Attacking */

	public int baseAttackDamage (int earlyModDamage, int damage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
	{
		try{
		}
		catch(Throwable th){}
		return earlyModDamage;
	}

	public int attackDamage (int modDamage, int currentDamage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
	{
		try{
			for(ItemModifier mod : ModifyBuilder.instance.itemModifiers)
				if(mod instanceof TActiveMod && toolTags.hasKey(mod.key))
					modDamage += ((TActiveMod) mod).attackDamage(modDamage, currentDamage, tool, tags, toolTags, stack, player, entity);

			for(ItemModifier mod : ModifyBuilder.instance.itemModifiers)
				if(mod instanceof TActiveMod && toolTags.hasKey(mod.key))
					((TActiveMod) mod).postDamage(modDamage, currentDamage, tool, tags, toolTags, stack, player, entity);
		}
		catch(Throwable th){}
		return modDamage;
	}

	// Calculated after sprinting and enchant bonuses
	public float knockback (float modKnockback, float currentKnockback, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
	{
		return modKnockback;
	}

}
