package org.abimon.mods.minecraft.tmodifiers;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class CommandReloadRecipes extends CommandBase {

	@Override
	public String getCommandName() {
		return "umReloadTModifiers";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return null;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] p_71515_2_) 
	{
		TModifiers.reloadModifiers();
		sender.addChatMessage(new ChatComponentText("Reloaded TConstruct Modifiers"));
	}

}
