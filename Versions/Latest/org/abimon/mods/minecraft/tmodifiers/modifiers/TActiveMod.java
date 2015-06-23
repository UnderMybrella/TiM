package org.abimon.mods.minecraft.tmodifiers.modifiers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import tconstruct.library.tools.ToolCore;

public interface TActiveMod {
	public boolean midStreamModify (ToolCore tool, NBTTagCompound tags, ItemStack stack, int x, int y, int z, EntityLivingBase entity);
	public void updateTool (ToolCore tool, NBTTagCompound tags, ItemStack stack, World world, Entity entity);
	public int attackDamage (int modDamage, int currentDamage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity);
	public int postDamage (int modDamage, int currentDamage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity);
	
	public int beheadingLevel(LivingDropsEvent event);
}
