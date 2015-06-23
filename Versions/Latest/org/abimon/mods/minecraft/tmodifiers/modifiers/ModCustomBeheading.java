package org.abimon.mods.minecraft.tmodifiers.modifiers;

import org.abimon.mods.minecraft.tmodifiers.TModifiers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import tconstruct.library.tools.AbilityHelper;
import tconstruct.library.tools.ToolCore;
import tconstruct.library.weaponry.IAmmo;
import tconstruct.modifiers.tools.ModInteger;

public class ModCustomBeheading extends ModTInteger implements TActiveMod {

	public ModCustomBeheading(ItemStack[] items, int effect, String dataKey,
			int increase1, String c, String tip, int max) {
		super(items, effect, dataKey, increase1, c, tip, max, 999);
		TModifiers.keyToProperty.put(dataKey, ModProperty.BEHEADING);
	}
	
	public ModCustomBeheading(ItemStack[] items, int effect, String dataKey,
			int increase1, int increase2, String c, String tip) {
		super(items, effect, dataKey, increase1, increase2, c, tip, 1, 999);
		TModifiers.keyToProperty.put(dataKey, ModProperty.BEHEADING);
	}
	
	public ModCustomBeheading(ItemStack[] items, int effect, String dataKey,
			int increase1, String c, String tip, int max, int maxApp) {
		super(items, effect, dataKey, increase1, c, tip, max, maxApp);
		TModifiers.keyToProperty.put(dataKey, ModProperty.BEHEADING);
	}
	
	public ModCustomBeheading(ItemStack[] items, int effect, String dataKey,
			int increase1, int increase2, String c, String tip, int max, int maxApp) {
		super(items, effect, dataKey, increase1, increase2, c, tip, max, maxApp);
		TModifiers.keyToProperty.put(dataKey, ModProperty.BEHEADING);
	}

	@Override
	public boolean midStreamModify(ToolCore tool, NBTTagCompound tags,
			ItemStack stack, int x, int y, int z, EntityLivingBase entity) {
		return false;
	}
	
	public void modify (ItemStack[] input, ItemStack tool)
	{
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
		return 0;
	}

	@Override
	public int beheadingLevel(LivingDropsEvent event) {
		EntityPlayer player = (EntityPlayer) event.source.getEntity();
		ItemStack stack = player.getCurrentEquippedItem();
		NBTTagCompound nbt = stack.getTagCompound().getCompoundTag("InfiTool");
		
		System.out.println("Beheading: " + nbt.getInteger(key) / max);
		
		return nbt.getInteger(key) / max;
	}

}
