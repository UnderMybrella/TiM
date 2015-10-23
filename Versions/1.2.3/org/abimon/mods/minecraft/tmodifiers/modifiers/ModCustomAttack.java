package org.abimon.mods.minecraft.tmodifiers.modifiers;

import org.abimon.mods.minecraft.tmodifiers.TModifiers;

import tconstruct.library.tools.ToolCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;

public class ModCustomAttack extends ModTInteger implements TActiveMod{

	public String[] ENTITY_LIST;
	public boolean whitelist = true;

	public int max;

	public ModCustomAttack(ItemStack[] items, int effect, String dataKey,
			int increase, String c, String tip, String[] ENTITY_LIST, int max, int maxApplication) {
		this(items, effect, dataKey, increase, c, tip, ENTITY_LIST, max, true, maxApplication);
	}

	public ModCustomAttack(ItemStack[] items, int effect, String dataKey,
			int increase, int increase2, String c, String tip, String[] ENTITY_LIST, int max, int maxApplication) {
		this(items, effect, dataKey, increase, increase2, c, tip, ENTITY_LIST, max, true, maxApplication);
	}

	public ModCustomAttack(ItemStack[] items, int effect, String dataKey,
			int increase, String c, String tip, String[] ENTITY_LIST, int max, boolean whitelist, int maxApplication) {
		this(items, effect, dataKey, increase, increase, c, tip, ENTITY_LIST, max, whitelist, maxApplication);
	}

	public ModCustomAttack(ItemStack[] items, int effect, String dataKey,
			int increase, int increase2, String c, String tip, String[] ENTITY_LIST, int max, boolean whitelist, int maxApplication) {
		super(items, effect, dataKey, increase, increase2, c, tip, max, 999);
		this.ENTITY_LIST = ENTITY_LIST;
		this.max = max;
		this.whitelist = whitelist;
	}

	public ModCustomAttack(ItemStack[] items, String dataKey,
			int increase, String c, String tip, String[] ENTITY_LIST, int max, int maxApplication) {
		this(items, dataKey, increase, c, tip, ENTITY_LIST, max, true, maxApplication);
	}

	public ModCustomAttack(ItemStack[] items, String dataKey,
			int increase, int increase2, String c, String tip, String[] ENTITY_LIST, int max, int maxApplication) {
		this(items, dataKey, increase, increase2, c, tip, ENTITY_LIST, max, true, maxApplication);
	}

	public ModCustomAttack(ItemStack[] items, String dataKey,
			int increase, String c, String tip, String[] ENTITY_LIST, int max, boolean whitelist, int maxApplication) {
		this(items, dataKey, increase, increase, c, tip, ENTITY_LIST, max, whitelist, maxApplication);
	}

	public ModCustomAttack(ItemStack[] items, String dataKey,
			int increase, int increase2, String c, String tip, String[] ENTITY_LIST, int max, boolean whitelist, int maxApplication) {
		this(items, -1, dataKey, increase, increase2, c, tip, ENTITY_LIST, max, whitelist, maxApplication);
	}

	public ModCustomAttack(ItemStack[] items, int effect, String dataKey,
			int increase, String c, String tip, String[] ENTITY_LIST, int max) {
		this(items, effect, dataKey, increase, c, tip, ENTITY_LIST, max, true);
	}

	public ModCustomAttack(ItemStack[] items, int effect, String dataKey,
			int increase, int increase2, String c, String tip, String[] ENTITY_LIST, int max) {
		this(items, effect, dataKey, increase, increase2, c, tip, ENTITY_LIST, max, true);
	}

	public ModCustomAttack(ItemStack[] items, int effect, String dataKey,
			int increase, String c, String tip, String[] ENTITY_LIST, int max, boolean whitelist) {
		this(items, effect, dataKey, increase, increase, c, tip, ENTITY_LIST, max, whitelist);
	}

	public ModCustomAttack(ItemStack[] items, int effect, String dataKey,
			int increase, int increase2, String c, String tip, String[] ENTITY_LIST, int max, boolean whitelist) {
		super(items, effect, dataKey, increase, increase2, c, tip, max, 999);
		this.ENTITY_LIST = ENTITY_LIST;
		this.max = max;
		this.whitelist = whitelist;
	}

	public ModCustomAttack(ItemStack[] items, String dataKey,
			int increase, String c, String tip, String[] ENTITY_LIST, int max) {
		this(items, dataKey, increase, c, tip, ENTITY_LIST, max, true);
	}

	public ModCustomAttack(ItemStack[] items, String dataKey,
			int increase, int increase2, String c, String tip, String[] ENTITY_LIST, int max) {
		this(items, dataKey, increase, increase2, c, tip, ENTITY_LIST, max, true);
	}

	public ModCustomAttack(ItemStack[] items, String dataKey,
			int increase, String c, String tip, String[] ENTITY_LIST, int max, boolean whitelist) {
		this(items, dataKey, increase, increase, c, tip, ENTITY_LIST, max, whitelist);
	}

	public ModCustomAttack(ItemStack[] items, String dataKey,
			int increase, int increase2, String c, String tip, String[] ENTITY_LIST, int max, boolean whitelist) {
		this(items, -1, dataKey, increase, increase2, c, tip, ENTITY_LIST, max, whitelist);
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

	@Override
	public void modify (ItemStack[] input, ItemStack tool){
		super.modifyDecrease(input, tool);
		
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

		int num = tags.getInteger(key);
		int base = num / (max / 2);
		int bonus = 1 + base;// + random.nextInt(base + 1);
		
		if(tags.hasKey(key + "-AttackVar")){
			int var = tags.getInteger(key + "-AttackVar");
			String tiptip= "Tooltip" + var;
			tags.setString(tiptip, "Additional Attack for " + key + ": " + bonus + " - " + (bonus + base + 1));
		}
		else
		{
			int var = addToolTip(tool, color + key + "-CustomAttackBonus", color + key + "-CustomAttackBonus");
			String tiptip= "Tooltip" + var;
			tags.setString(tiptip, "Additional Attack for " + key + ": " + bonus + " - " + (bonus + base + 1));
			tags.removeTag("ModifierTip" + var);
			tags.setInteger(key + "-AttackVar", var);
		}

	}

	//	@Override
	//	public void modify (ItemStack[] input, ItemStack tool)
	//	{
	//		IModifyable toolItem = (IModifyable) tool.getItem();
	//		NBTTagCompound tags = tool.getTagCompound().getCompoundTag(toolItem.getBaseTagName());
	//		if (tags.hasKey(key))
	//		{
	//			int[] keyPair = tags.getIntArray(key);
	//			int increase = matchingAmount(input);
	//
	//			int leftToBoost = threshold - (keyPair[0] % threshold);
	//			if (increase >= leftToBoost)
	//			{
	//				int attack = tags.getInteger("Attack");
	//				attack += 1;
	//				tags.setInteger("Attack", attack);
	//			}
	//
	//			if (keyPair[0] % max == 0)
	//			{
	//				keyPair[0] += increase;
	//				keyPair[1] += max;
	//				tags.setIntArray(key, keyPair);
	//
	//				int modifiers = tags.getInteger("Modifiers");
	//				modifiers -= 1;
	//				tags.setInteger("Modifiers", modifiers);
	//			}
	//			else
	//			{
	//				keyPair[0] += increase;
	//				tags.setIntArray(key, keyPair);
	//			}
	//			updateModTag(tool, keyPair);
	//
	//		}
	//		else
	//		{
	//			int modifiers = tags.getInteger("Modifiers");
	//			modifiers -= 1;
	//			tags.setInteger("Modifiers", modifiers);
	//			int increase = matchingAmount(input);
	//			String modName = "\u00a7f" + guiType + " (" + increase + "/" + max + ")";
	//			int tooltipIndex = addToolTip(tool, tooltipName, modName);
	//			int[] keyPair = new int[] { increase, max, tooltipIndex };
	//			tags.setIntArray(key, keyPair);
	//
	//			int attack = tags.getInteger("Attack");
	//			attack += 1;
	//			tags.setInteger("Attack", attack);
	//		}
	//	}

	@Override
	public int attackDamage (int modDamage, int currentDamage, ToolCore tool, NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack, EntityLivingBase player, Entity entity)
	{
		int bonus = 0;
		for(String str : ENTITY_LIST){
			boolean entityEquals = TModifiers.classExtends(entity.getClass(), str);
			if(whitelist == entityEquals)
			{	
				int num = toolTags.getInteger(key);
				int base = num / (max / 2);
				bonus += 1 + base + random.nextInt(base + 1);
			}
			else{
				try{
					EnumCreatureAttribute eca = EnumCreatureAttribute.valueOf(str);
					if(entity instanceof EntityLivingBase && (((EntityLivingBase) entity).getCreatureAttribute()) == eca)
					{
						int num = toolTags.getInteger(key);
						int base = num / (max / 2);
						bonus += 1 + base + random.nextInt(base + 1);
					}
				}
				catch(Throwable th){}
			}
		}
		return bonus;
	}

	@Override
	public int postDamage(int modDamage, int currentDamage, ToolCore tool,
			NBTTagCompound tags, NBTTagCompound toolTags, ItemStack stack,
			EntityLivingBase player, Entity entity) {
		return 0;
	}

	@Override
	public int beheadingLevel(LivingDropsEvent event) {
		// TODO Auto-generated method stub
		return 0;
	}	

}
