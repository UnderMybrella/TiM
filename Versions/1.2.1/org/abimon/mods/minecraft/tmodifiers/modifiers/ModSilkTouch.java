package org.abimon.mods.minecraft.tmodifiers.modifiers;

import java.util.Iterator;
import java.util.Map;

import org.abimon.mods.minecraft.tmodifiers.TModifiers;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class ModSilkTouch extends ModTBoolean {

	boolean withLava = false;
	boolean withFortune = false;
	
	int speedPenalty;
	int attackPenalty;

	public ModSilkTouch(ItemStack[] items, int effect, String tag, String color, String tip) {
		super(items, effect, tag, color, tip);
		speedPenalty = -300;
		attackPenalty = -3;
	}

	public ModSilkTouch(ItemStack[] items, int effect, String tag, String color, String tip, boolean withLava) {
		super(items, effect, tag, color, tip);
		this.withLava = withLava;
		speedPenalty = -300;
		attackPenalty = -3;
	}

	public ModSilkTouch(ItemStack[] items, int effect, String tag, String color, String tip, boolean withLava, boolean withFortune) {
		super(items, effect, tag, color, tip);
		this.withLava = withLava;
		this.withFortune = withFortune;
		speedPenalty = -300;
		attackPenalty = -3;
	}
	
	public ModSilkTouch(ItemStack[] items, int effect, String tag, String color, String tip, boolean withLava, boolean withFortune, int speedPenalty, int attackPenalty) {
		super(items, effect, tag, color, tip);
		this.withLava = withLava;
		this.withFortune = withFortune;
		this.speedPenalty = speedPenalty;
		this.attackPenalty = attackPenalty;
	}

	public ModSilkTouch(ItemStack[] items, String tag, String color, String tip) {
		super(items, tag, color, tip);
		speedPenalty = -300;
		attackPenalty = -3;
	}

	public ModSilkTouch(ItemStack[] items, String tag, String color, String tip, boolean withLava) {
		super(items, tag, color, tip);
		this.withLava = withLava;
		speedPenalty = -300;
		attackPenalty = -3;
	}

	public ModSilkTouch(ItemStack[] items, String tag, String color, String tip, boolean withLava, boolean withFortune) {
		super(items, tag, color, tip);
		this.withLava = withLava;
		this.withFortune = withFortune;
		speedPenalty = -300;
		attackPenalty = -3;
	}
	
	public ModSilkTouch(ItemStack[] items, String tag, String color, String tip, boolean withLava, boolean withFortune, int speedPenalty, int attackPenalty) {
		super(items, tag, color, tip);
		this.withLava = withLava;
		this.withFortune = withFortune;
		this.speedPenalty = speedPenalty;
		this.attackPenalty = attackPenalty;
	}

	/**
	 * NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");

			if (TModifiers.hasProperty(tags, ModProperty.SILK_TOUCH) && !withSilktouch)
				return false;

			if (TModifiers.hasProperty(tags, ModProperty.AUTOSMELT) && !withAutosmelt)
				return false;
	 */

	@Override
	protected boolean canModify (ItemStack tool, ItemStack[] input)
	{
		if(super.canModify(tool, input)){

			NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
			if (TModifiers.hasProperty(tags, ModProperty.FORTUNE) && !withFortune)
				return false;

			if (TModifiers.hasProperty(tags, ModProperty.AUTOSMELT) && !withLava)
				return false;
			
			return true;
		}
		return false;
	}
	
	@Override
    public void modify (ItemStack[] input, ItemStack tool)
    {
        NBTTagCompound tags = tool.getTagCompound().getCompoundTag("InfiTool");
        tags.setBoolean(key, true);
        addEnchantment(tool, Enchantment.silkTouch, 1);

        int modifiers = tags.getInteger("Modifiers");
        modifiers -= 1;
        tags.setInteger("Modifiers", modifiers);

        int attack = tags.getInteger("Attack");
        attack += attackPenalty;
        if (attack < 0)
            attack = 0;
        tags.setInteger("Attack", attack);

        int miningSpeed = tags.getInteger("MiningSpeed");
        miningSpeed += speedPenalty;
        if (miningSpeed < 0)
            miningSpeed = 0;
        tags.setInteger("MiningSpeed", miningSpeed);

        if (tags.hasKey("MiningSpeed2"))
        {
            int miningSpeed2 = tags.getInteger("MiningSpeed2");
            miningSpeed2 += speedPenalty;
            if (miningSpeed2 < 0)
                miningSpeed2 = 0;
            tags.setInteger("MiningSpeed2", miningSpeed2);
        }

        addToolTip(tool, color + tooltipName, color + key);
    }

    public void addEnchantment (ItemStack tool, Enchantment enchant, int level) //TODO: Move this to ItemModifier
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

}
