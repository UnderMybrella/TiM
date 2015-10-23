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

public class ModCustomRepair extends ModTInteger implements TActiveMod {

	public ModCustomRepair(ItemStack[] items, int effect, String dataKey,
			int increase1, String c, String tip) {
		super(items, effect, dataKey, increase1, c, tip, 1, 999);
	}
	
	public ModCustomRepair(ItemStack[] items, int effect, String dataKey,
			int increase1, int increase2, String c, String tip) {
		super(items, effect, dataKey, increase1, increase2, c, tip, 1, 999);
	}
	
	public ModCustomRepair(ItemStack[] items, int effect, String dataKey,
			int increase1, String c, String tip, int max, int maxApp) {
		super(items, effect, dataKey, increase1, c, tip, max, maxApp);
	}
	
	public ModCustomRepair(ItemStack[] items, int effect, String dataKey,
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
        if (!world.isRemote && entity instanceof EntityLivingBase && !((EntityLivingBase) entity).isSwingInProgress && stack.getTagCompound() != null)
        {
            if (tags.hasKey(key))
            {
                int chance = tags.getInteger(key) / max;
                int check = world.canBlockSeeTheSky((int) entity.posX, (int) entity.posY, (int) entity.posZ) ? 350 : 1150;
                // REGROWING AMMO :OOoo
                if(tool instanceof IAmmo && random.nextInt(check*3) < chance) // ammo regenerates at a much slower rate
                {
                    IAmmo ammothing = (IAmmo)tool;
                    if(ammothing.getAmmoCount(stack) > 0) // must have ammo
                        ammothing.addAmmo(1, stack);
                }
                // selfrepairing tool. LAAAAAME
                else if (random.nextInt(check) < chance)
                {
                    AbilityHelper.healTool(stack, 1, (EntityLivingBase) entity, true);
                }
            }
        }
    }
	
	public int attackDamage (int modDamage, int currentDamage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
	{
		return 0;
	}

	@Override
	public int postDamage(int modDamage, int currentDamage, ToolCore tool,
			NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack,
			EntityLivingBase player, Entity entity) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int beheadingLevel(LivingDropsEvent event) {
		// TODO Auto-generated method stub
		return 0;
	}

}
