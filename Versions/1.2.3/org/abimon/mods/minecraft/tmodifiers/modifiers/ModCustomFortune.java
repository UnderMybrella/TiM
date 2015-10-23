package org.abimon.mods.minecraft.tmodifiers.modifiers;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.abimon.mods.minecraft.tmodifiers.TModifiers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import tconstruct.library.tools.ToolCore;
import tconstruct.modifiers.tools.ItemModTypeFilter;
import tconstruct.modifiers.tools.ModLapis;

public class ModCustomFortune extends ItemModTypeFilter implements TActiveMod{

	public String tooltipName = "\u00a79Lapis"; //Lapis
	public String modifierTip = "\u00a79Luck"; //Luck
	public int max = 450;

	public boolean withSilktouch = false;
	public boolean withAutosmelt = true;

	public int[] luckValues;

	public ModCustomFortune(String key, ItemStack[] items, int[] values) {
		super(-1, key, items, values);
		TModifiers.keyToProperty.put(key, ModProperty.FORTUNE);
		luckValues = new int[]{100, 300, 450};
	}
	
	public ModCustomFortune(String key, int effect, ItemStack[] items, int[] values) {
		super(effect, key, items, values);
		TModifiers.keyToProperty.put(key, ModProperty.FORTUNE);
		luckValues = new int[]{100, 300, 450};
	}
	
	public ModCustomFortune(String key, int effect, ItemStack[] items, int[] values, String tooltipName) {
		super(effect, key, items, values);
		this.tooltipName = tooltipName;
		TModifiers.keyToProperty.put(key, ModProperty.FORTUNE);
		luckValues = new int[]{100, 300, 450};
	}
	
	public ModCustomFortune(String key, int effect, ItemStack[] items, int[] values, String tooltipName, String modifierTip) {
		super(effect, key, items, values);
		this.tooltipName = tooltipName;
		this.modifierTip = modifierTip;
		TModifiers.keyToProperty.put(key, ModProperty.FORTUNE);
		luckValues = new int[]{100, 300, 450};
	}
	
	public ModCustomFortune(String key, int effect, ItemStack[] items, int[] values, String tooltipName, String modifierTip, int max) {
		super(effect, key, items, values);
		this.tooltipName = tooltipName;
		this.modifierTip = modifierTip;
		this.max = max;
		TModifiers.keyToProperty.put(key, ModProperty.FORTUNE);
		luckValues = new int[]{100, 300, 450};
	}
	
	public ModCustomFortune(String key, int effect, ItemStack[] items, int[] values, String tooltipName, String modifierTip, int max, boolean withSilktouch) {
		super(effect, key, items, values);
		this.tooltipName = tooltipName;
		this.modifierTip = modifierTip;
		this.max = max;
		this.withSilktouch = withSilktouch;
		TModifiers.keyToProperty.put(key, ModProperty.FORTUNE);
		luckValues = new int[]{100, 300, 450};
	}
	
	public ModCustomFortune(String key, int effect, ItemStack[] items, int[] values, String tooltipName, String modifierTip, int max, boolean withSilktouch, boolean withAutosmelt) {
		super(effect, key, items, values);
		this.tooltipName = tooltipName;
		this.modifierTip = modifierTip;
		this.max = max;
		this.withSilktouch = withSilktouch;
		this.withAutosmelt = withAutosmelt;
		TModifiers.keyToProperty.put(key, ModProperty.FORTUNE);
		luckValues = new int[]{100, 300, 450};
	}
	
	public ModCustomFortune(String key, int effect, ItemStack[] items, int[] values, String tooltipName, String modifierTip, int max, boolean withSilktouch, boolean withAutosmelt, int[] luckValues) {
		super(effect, key, items, values);
		this.tooltipName = tooltipName;
		this.modifierTip = modifierTip;
		this.max = max;
		this.withSilktouch = withSilktouch;
		this.withAutosmelt = withAutosmelt;
		this.luckValues = luckValues;
		TModifiers.keyToProperty.put(key, ModProperty.FORTUNE);
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

			if (TModifiers.hasProperty(tags, ModProperty.SILK_TOUCH) && !withSilktouch)
				return false;

			if (TModifiers.hasProperty(tags, ModProperty.AUTOSMELT) && !withAutosmelt)
				return false;

			if (!tags.hasKey(key))
				return tags.getInteger("Modifiers") > 0 && matchingAmount(input) <= max;

				int keyPair[] = tags.getIntArray(key);
				if (keyPair[0] + matchingAmount(input) <= max)
					return true;
		}
		return false;
	}

	@Override
	public void modify (ItemStack[] input, ItemStack tool)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		if (!tags.hasKey(key))
		{
			tags.setBoolean(key, true);

			String modName = tooltipName + " (0/" + max + ")";
			int tooltipIndex = addToolTip(tool, modifierTip, modName);
			int[] keyPair = new int[] { 0, tooltipIndex };
			tags.setIntArray(key, keyPair);

			int modifiers = tags.getInteger("Modifiers");
			modifiers -= 1;
			tags.setInteger("Modifiers", modifiers);
		}

		int increase = matchingAmount(input);
		int keyPair[] = tags.getIntArray(key);
		keyPair[0] += increase;
		tags.setIntArray(key, keyPair);
		ToolCore toolcore = (ToolCore) tool.getItem();
		String[] types = toolcore.getTraits();
		boolean weapon = false;
		boolean harvest = false;
		for (String s : types)
		{
			if (s.equals("harvest"))
				harvest = true;
			else if (s.equals("weapon"))
				weapon = true;
		}
		if (weapon)
			for(int i = luckValues.length - 1; i >= 0; i--)
				if(keyPair[0] >= luckValues[i])
				{
					addEnchantment(tool, Enchantment.looting, i + 1);
					break;
				}
		if (harvest)
		{
			for(int i = luckValues.length - 1; i >= 0; i--)
			{
				if(keyPair[0] >= luckValues[i])
				{
					addEnchantment(tool, Enchantment.fortune, i + 1);
					break;
				}
			}
		}

		updateModTag(tool, keyPair);
	}

	public boolean midStreamModify (ToolCore toolItem, NBTTagCompound nbtTags, ItemStack tool, int x, int y, int z, EntityLivingBase entity)
	{
		NBTTagCompound tags = nbtTags;
		if (!tags.hasKey(key))
			return false;

		int keyPair[] = tags.getIntArray(key);
		if (keyPair[0] == max)
			return false;

		if (random.nextInt(50) == 0)
		{
			keyPair[0] += 1;
			tags.setIntArray(key, keyPair);
			updateModTag(tool, keyPair);
		}

		List list = Arrays.asList(toolItem.getTraits());
		if (list.contains("weapon"))
			for(int i = luckValues.length - 1; i >= 0; i--)
				if(keyPair[0] >= luckValues[i])
				{
					addEnchantment(tool, Enchantment.looting, i + 1);
					break;
				}

		if (list.contains("harvest"))
			for(int i = luckValues.length - 1; i >= 0; i--)
				if(keyPair[0] >= luckValues[i])
				{
					addEnchantment(tool, Enchantment.fortune, i + 1);
					break;
				}
		return false;
	}

	public void addEnchantment (ItemStack tool, Enchantment enchant, int level)
	{
		NBTTagList tags = new NBTTagList();
		Map enchantMap = EnchantmentHelper.getEnchantments(tool);
		Iterator iterator = enchantMap.keySet().iterator();
		int index;
		int lvl;
		boolean hasEnchant = false;
		while (iterator.hasNext())
		{
			NBTTagCompound enchantTag = new NBTTagCompound();
			index = ((Integer) iterator.next()).intValue();
			lvl = (Integer) enchantMap.get(index);
			if (index == enchant.effectId)
			{
				hasEnchant = true;
				enchantTag.setShort("id", (short) index);
				enchantTag.setShort("lvl", (short) ((byte) level));
				tags.appendTag(enchantTag);
			}
			else
			{
				enchantTag.setShort("id", (short) index);
				enchantTag.setShort("lvl", (short) ((byte) lvl));
				tags.appendTag(enchantTag);
			}
		}
		if (!hasEnchant)
		{
			NBTTagCompound enchantTag = new NBTTagCompound();
			enchantTag.setShort("id", (short) enchant.effectId);
			enchantTag.setShort("lvl", (short) ((byte) level));
			tags.appendTag(enchantTag);
		}
		tool.stackTagCompound.setTag("ench", tags);
	}

	public void updateModTag (ItemStack tool, int[] keys)
	{
		NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
		String tip = "ModifierTip" + keys[1];
		String modName = tooltipName + " (" + keys[0] + "/" + max + ")";
		tags.setString(tip, modName);
	}

	public boolean validType (ToolCore tool)
	{
		List list = Arrays.asList(tool.getTraits());
		return !list.contains("ammo") && (list.contains("weapon") || list.contains("harvest"));
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
