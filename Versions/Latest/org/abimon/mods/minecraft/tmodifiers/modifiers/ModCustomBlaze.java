package org.abimon.mods.minecraft.tmodifiers.modifiers;

import tconstruct.library.tools.ToolCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public class ModCustomBlaze extends ModTInteger implements TActiveMod{
	public ModCustomBlaze(ItemStack[] items, int effect, String dataKey,
			int increase1, String c, String tip) {
		super(items, effect, dataKey, increase1, c, tip, 24, 999);
	}
	
	public ModCustomBlaze(ItemStack[] items, int effect, String dataKey,
			int increase1, int increase2, String c, String tip) {
		super(items, effect, dataKey, increase1, increase2, c, tip, 24, 999);
	}
	
	public ModCustomBlaze(ItemStack[] items, int effect, String dataKey,
			int increase1, String c, String tip, int max, int maxApp) {
		super(items, effect, dataKey, increase1, c, tip, max, maxApp);
	}
	
	public ModCustomBlaze(ItemStack[] items, int effect, String dataKey,
			int increase1, int increase2, String c, String tip, int max, int maxApp) {
		super(items, effect, dataKey, increase1, increase2, c, tip, max, maxApp);
	}

	@Override
	public boolean midStreamModify(ToolCore tool, NBTTagCompound tags,
			ItemStack stack, int x, int y, int z, EntityLivingBase entity) {
		return false;
	}

	@Override
	public void updateTool(ToolCore tool, NBTTagCompound tags, ItemStack stack,
			World world, Entity entity) {
		
	}
	
	public void modify (ItemStack[] input, ItemStack tool){
		super.modifyDecrease(input, tool);
	}

	@Override
	public int attackDamage(int modDamage, int currentDamage, ToolCore tool,
			NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack,
			EntityLivingBase player, Entity entity) {
		return 0;
	}

	@Override
	public int postDamage(int modDamage, int currentDamage, ToolCore tool,
			NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack,
			EntityLivingBase player, Entity entity) {
		int level = toolTags.getInteger(key) / max;
		entity.setFire(level * 5);
		return 0;
	}

	@Override
	public int beheadingLevel(LivingDropsEvent event) {
		// TODO Auto-generated method stub
		return 0;
	}
}
