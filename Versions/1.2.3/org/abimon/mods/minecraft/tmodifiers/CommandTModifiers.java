package org.abimon.mods.minecraft.tmodifiers;

import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.modifier.ItemModifier;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandTModifiers extends CommandBase {

	@Override
	public String getCommandName() {
		return "tmodifiers";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "tmodifiers <print|reload>";
	}

	@Override
	public void processCommand(ICommandSender sender, String[] params) 
	{
		if(params.length == 0)
			sender.addChatMessage(new ChatComponentText("No parameter specified"));
		else
			if(params[0].equalsIgnoreCase("print"))
				for(ItemModifier mod : ModifyBuilder.instance.itemModifiers)
					sender.addChatMessage(new ChatComponentText(mod + ""));
			else if(params[0].equalsIgnoreCase("reload"))
			{
				TModifiers.reloadModifiers();
				sender.addChatMessage(new ChatComponentText("Reloaded TConstruct Modifiers"));
			}
			else
				sender.addChatMessage(new ChatComponentText("Unrecognised parameter"));
	}

}
