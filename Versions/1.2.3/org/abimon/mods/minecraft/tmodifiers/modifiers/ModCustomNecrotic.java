package org.abimon.mods.minecraft.tmodifiers.modifiers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.weaponry.IAmmo;
import tconstruct.modifiers.tools.ModInteger;

public class ModCustomNecrotic extends ModTInteger implements TActiveMod {

	public ModCustomNecrotic(ItemStack[] items, int effect, String dataKey,
			int increase1, String c, String tip) {
		super(items, effect, dataKey, increase1, c, tip, 1, 999);
	}
	
	public ModCustomNecrotic(ItemStack[] items, int effect, String dataKey,
			int increase1, int increase2, String c, String tip) {
		super(items, effect, dataKey, increase1, increase2, c, tip, 1, 999);
	}
	
	public ModCustomNecrotic(ItemStack[] items, int effect, String dataKey,
			int increase1, String c, String tip, int max, int maxApp) {
		super(items, effect, dataKey, increase1, c, tip, max, maxApp);
	}
	
	public ModCustomNecrotic(ItemStack[] items, int effect, String dataKey,
			int increase1, int increase2, String c, String tip, int max, int maxApp) {
		super(items, effect, dataKey, increase1, increase2, c, tip, max, maxApp);
	}

	@Override
	public boolean midStreamModify(ToolCore tool, NBTTagCompound tags,
			ItemStack stack, int x, int y, int z, EntityLivingBase entity) {
		return false;
	}
	
	public void modify (ItemStack[] input, ItemStack tool){
		super.modifyDecrease(input, tool);
	}
	
	public void updateTool (ToolCore tool, NBTTagCompound tags, ItemStack stack, World world, Entity entity)
    {

    }
	
	public int attackDamage (int modDamage, int currentDamage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
	{
		return 0;
	}

	@Override
	public int postDamage(int modDamage, int currentDamage, ToolCore tool,
			NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack,
			EntityLivingBase player, Entity entity) {
		int level = tags.getInteger(key) / max;
		player.heal(level);
		return 0;
	}

	@Override
	public int beheadingLevel(LivingDropsEvent event) {
		// TODO Auto-generated method stub
		return 0;
	}

}
