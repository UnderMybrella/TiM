package org.abimon.mods.minecraft.tmodifiers.modifiers;

import java.util.ArrayList;
import java.util.Iterator;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.library.tools.ToolCore;

public class ModChangeModifiers extends ItemModifier {

	int modifiers = 1;
	int uses = 0;
	
	public ModChangeModifiers(ItemStack[] recipe, String dataKey, int modifiers, int uses) {
		this(recipe, dataKey, -1, modifiers, uses);
	}
	
	public ModChangeModifiers(ItemStack[] recipe, String dataKey, int effect, int modifiers, int uses) {
		super(recipe, effect, dataKey);
		this.modifiers = modifiers;
		this.uses = uses;
	}
	
	 /** Checks to see if the inputs match the stored items
     * Note: Works like ShapelessRecipes
     * 
     * @param recipe The ItemStacks to compare against
     * @param input Item to modify, used for restrictions
     * @return Whether the recipe matches the input
     */
    public boolean matches (ItemStack[] recipe, ItemStack input)
    {
    	if(input.getTagCompound().getCompoundTag(getTagName(input)).getInteger(key) >= uses)
    		return false;
        ArrayList list = new ArrayList(this.stacks);

        for (int iter = 0; iter < recipe.length; ++iter)
        {
            ItemStack craftingStack = recipe[iter];

            if (craftingStack != null)
            {
                boolean canCraft = false;
                Iterator iterate = list.iterator();

                while (iterate.hasNext())
                {
                    ItemStack removeStack = (ItemStack) iterate.next();

                    if (craftingStack.getItem() == removeStack.getItem() && (removeStack.getItemDamage() == Short.MAX_VALUE || craftingStack.getItemDamage() == removeStack.getItemDamage()))
                    {
                        canCraft = true;
                        list.remove(removeStack);
                        break;
                    }
                }

                if (!canCraft)
                {
                    return false;
                }
            }
        }

        return list.isEmpty();
    }
    
    public void addMatchingEffect (ItemStack input){
    	if(effectIndex == -1)
    		return;
    	else
    		super.addMatchingEffect(input);
    }

	@Override
	public void modify(ItemStack[] recipe, ItemStack input) 
	{
		if(!input.hasTagCompound())
			input.setTagCompound(new NBTTagCompound());
		input.getTagCompound().getCompoundTag(getTagName(input)).setInteger("Modifiers", input.getTagCompound().getCompoundTag(getTagName(input)).getInteger("Modifiers") + modifiers);
		input.getTagCompound().getCompoundTag(getTagName(input)).setInteger(key, input.getTagCompound().getCompoundTag(getTagName(input)).getInteger(key) + 1);
	}

}
