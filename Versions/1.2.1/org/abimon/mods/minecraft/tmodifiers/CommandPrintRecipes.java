package org.abimon.mods.minecraft.tmodifiers;

import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.modifier.ItemModifier;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandPrintRecipes extends CommandBase {

	@Override
	public String getCommandName() {
		return "umPrintModifiers";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] p_71515_2_) 
	{
		for(ItemModifier mod : ModifyBuilder.instance.itemModifiers)
			sender.addChatMessage(new ChatComponentText(mod + ""));
	}

}
