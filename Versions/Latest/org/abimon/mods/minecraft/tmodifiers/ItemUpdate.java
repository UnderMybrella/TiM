package org.abimon.mods.minecraft.tmodifiers;

import java.awt.Desktop;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Scanner;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ItemUpdate extends Item 
{
	public static boolean hasUpdate = false;
	public static String version = "";
	
	@SideOnly(Side.CLIENT)
    public void addInformation(ItemStack p_77624_1_, EntityPlayer p_77624_2_, List list, boolean p_77624_4_) {
		if(hasUpdate){
			list.add("Update available!");
			list.add("The latest version is " + version + " and you have version " + TModifiers.VERSION);
			list.add("Shift-Right click to open the Curse page in your default browser.");
		}
		else
			list.add("No updates available");
	}
	
    public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer player, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_)
    {
    	if(player.isSneaking() && hasUpdate && player.worldObj.isRemote)
    	{
    		try{
    			Desktop.getDesktop().browse(new URL("http://goo.gl/LNfZ75").toURI());
    		}
    		catch(Throwable th){}
    		return true;
    	}
    	new Thread(){
    		public void run(){
    			checkForUpdate();
    		}
    	}.start();
        return false;
    }
    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_)
    {
    	new Thread(){
    		public void run(){
    			checkForUpdate();
    		}
    	}.start();
        return p_77659_1_;
    }

	@SuppressWarnings("resource")
	public static void checkForUpdate(){
		try{
			URL url = new URL("https://raw.githubusercontent.com/UnderMybrella/TiM/master/version.txt");
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.connect();
			version = new Scanner(con.getInputStream()).nextLine();
			if(!TModifiers.VERSION.equals(version))
				hasUpdate = true;
		}
		catch(Throwable th){}
	}
	
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack par1ItemStack, int pass){
    	return hasUpdate;
    }
}
