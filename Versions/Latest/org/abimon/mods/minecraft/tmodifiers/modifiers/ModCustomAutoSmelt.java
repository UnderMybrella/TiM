package org.abimon.mods.minecraft.tmodifiers.modifiers;

import org.abimon.mods.minecraft.tmodifiers.TModifiers;

import tconstruct.library.tools.HarvestTool;
import tconstruct.library.tools.ToolCore;
import tconstruct.util.config.PHConstruct;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public class ModCustomAutoSmelt extends ModTBoolean implements TActiveMod{

	boolean withSilktouch = false;
	boolean withFortune = true;

	public ModCustomAutoSmelt(ItemStack[] items, String tag, String color,
			String tip) {
		super(items, -1, tag, color, tip);
		TModifiers.keyToProperty.put(key, ModProperty.AUTOSMELT);
	}

	public ModCustomAutoSmelt(ItemStack[] items, int effect, String tag,
			String color, String tip) {
		super(items, effect, tag, color, tip);
		TModifiers.keyToProperty.put(key, ModProperty.AUTOSMELT);
	}

	public ModCustomAutoSmelt(ItemStack[] items, int effect, String tag,
			String color, String tip, boolean withSilktouch) {
		super(items, effect, tag, color, tip);
		TModifiers.keyToProperty.put(key, ModProperty.AUTOSMELT);
		this.withSilktouch = withSilktouch;
	}

	public ModCustomAutoSmelt(ItemStack[] items, int effect, String tag,
			String color, String tip, boolean withSilktouch, boolean withFortune) {
		super(items, effect, tag, color, tip);
		TModifiers.keyToProperty.put(key, ModProperty.AUTOSMELT);
		this.withSilktouch = withSilktouch;
		this.withFortune = withFortune;
	}

	@Override
	protected boolean canModify (ItemStack tool, ItemStack[] input)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		if(TModifiers.hasProperty(tags, ModProperty.SILK_TOUCH) && !withSilktouch)
			return false;
		if(TModifiers.hasProperty(tags, ModProperty.FORTUNE) && !withFortune)
			return false;
		return tags.getInteger("Modifiers") > 0 && !tags.getBoolean(key); //Will fail if the modifier is false or the tag doesn't exist
	}

	@Override
	public boolean midStreamModify(ToolCore tool, NBTTagCompound tags,
			ItemStack stack, int x, int y, int z, EntityLivingBase entity) {
		
		if(!tags.hasKey(key))
			return false;
		World world = entity.worldObj;
		Block block = world.getBlock(x, y, z);
		if (block == null)
			return false;

		int blockMeta = world.getBlockMetadata(x, y, z);

		if(block.getMaterial().isToolNotRequired()) {
			// only if effective tool
			if(tool instanceof HarvestTool) {
				if (!((HarvestTool) tool).isEffective(block, blockMeta))
					return false;
			}
			else
				return false;
		}
		else if(!ForgeHooks.canToolHarvestBlock(block, blockMeta, stack))
			return false;

		if (block.quantityDropped(blockMeta, 0, random) > 0)
		{
			int itemMeta = block.damageDropped(blockMeta);
			int amount = block.quantityDropped(random);
			Item item = block.getItemDropped(blockMeta, random, EnchantmentHelper.getFortuneModifier(entity));

			if(entity instanceof EntityPlayer)
				if(EnchantmentHelper.getSilkTouchModifier(entity))
					if(block.canSilkHarvest(world, (EntityPlayer) entity, x, y, z, blockMeta))
						item = Item.getItemFromBlock(block);
			// apparently some things that don't drop blocks (like glass panes without silktouch) return null.
			if (item == null)
				return false;

			ItemStack result = FurnaceRecipes.smelting().getSmeltingResult(new ItemStack(item, amount, itemMeta));
			if (result != null)
			{
				world.setBlockToAir(x, y, z);
				if (entity instanceof EntityPlayer && !((EntityPlayer) entity).capabilities.isCreativeMode)
					tool.onBlockDestroyed(stack, world, block, x, y, z, entity);
				if (!world.isRemote)
				{
					ItemStack spawnme = new ItemStack(result.getItem(), amount * result.stackSize, result.getItemDamage());
					if (result.hasTagCompound())
						spawnme.setTagCompound(result.getTagCompound());
					if (!(result.getItem() instanceof ItemBlock) && PHConstruct.lavaFortuneInteraction)
					{
						int loot = EnchantmentHelper.getEnchantmentLevel(Enchantment.fortune.effectId, stack);
						if (loot > 0)
						{
							spawnme.stackSize *= (random.nextInt(loot + 1) + 1);
						}
					}
					EntityItem entityitem = new EntityItem(world, x + 0.5, y + 0.5, z + 0.5, spawnme);

					entityitem.delayBeforeCanPickup = 10;
					world.spawnEntityInWorld(entityitem);
					world.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (blockMeta << 12));

					int i = spawnme.stackSize;
					float f = FurnaceRecipes.smelting().func_151398_b(spawnme);
					int j;

					if (f == 0.0F)
					{
						i = 0;
					}
					else if (f < 1.0F)
					{
						j = MathHelper.floor_float((float) i * f);

						if (j < MathHelper.ceiling_float_int((float) i * f) && (float) Math.random() < (float) i * f - (float) j)
						{
							++j;
						}

						i = j;
					}

					while (i > 0)
					{
						j = EntityXPOrb.getXPSplit(i);
						i -= j;
						entity.worldObj.spawnEntityInWorld(new EntityXPOrb(world, x, y + 0.5, z, j));
					}
				}
				for (int i = 0; i < 5; i++)
				{
					float f = (float) x + random.nextFloat();
					float f1 = (float) y + random.nextFloat();
					float f2 = (float) z + random.nextFloat();
					float f3 = 0.52F;
					float f4 = random.nextFloat() * 0.6F - 0.3F;
					world.spawnParticle("smoke", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", f - f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);

					world.spawnParticle("smoke", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", f + f3, f1, f2 + f4, 0.0D, 0.0D, 0.0D);

					world.spawnParticle("smoke", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", f + f4, f1, f2 - f3, 0.0D, 0.0D, 0.0D);

					world.spawnParticle("smoke", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
					world.spawnParticle("flame", f + f4, f1, f2 + f3, 0.0D, 0.0D, 0.0D);
				}
				return true;
			}
		}
		return false;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int beheadingLevel(LivingDropsEvent event) {
		// TODO Auto-generated method stub
		return 0;
	}


}
