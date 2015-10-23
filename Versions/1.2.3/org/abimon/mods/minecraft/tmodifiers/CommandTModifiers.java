package org.abimon.mods.minecraft.tmodifiers;

import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.modifier.ItemModifier;

import java.util.List;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import scala.actors.threadpool.Arrays;

public class CommandTModifiers extends CommandBase {

	@Override
	public String getCommandName() {
		return "tmodifiers";
	}

	public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] strings)
	{
		if(strings.length == 1)
			return Arrays.asList(new String[]{"print", "reload"});
		if(strings.length == 2)
			if(strings[1].toLowerCase().startsWith("p"))
				return Arrays.asList(new String[]{"print"});
			else if(strings[1].toLowerCase().startsWith("r"))
				return Arrays.asList(new String[]{"reload"});
		return null;
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
