package org.abimon.mods.minecraft.tmodifiers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.*;
import java.text.DecimalFormat;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import scala.actors.threadpool.Arrays;
import tconstruct.library.armor.ArmorPart;
import tconstruct.library.crafting.ModifyBuilder;
import tconstruct.library.modifier.ItemModifier;
import tconstruct.modifiers.armor.TravelModDoubleJump;
import tconstruct.modifiers.tools.ModExtraModifier;
import tconstruct.modifiers.tools.ModLapis;
import tconstruct.modifiers.tools.ModRedstone;
import net.minecraft.block.Block;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartedEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(name = TModifiers.NAME, modid = TModifiers.MODID, version = TModifiers.VERSION, dependencies = "required-after:TConstruct")
public class TModifiers
{
	public static final String MODID = "um_tmodifiers";
	public static final String NAME = "Tinkers' Modifiers";
	public static final String VERSION = "1.2";

	private static File configFile;
	private static File jsonFile;

	private static File modifierDirectory;

	public static Item updateChecker = new ItemUpdate().setTextureName("bread").setUnlocalizedName("updateChecker");

	private static final LinkedList<ItemModifier> addedModifiers = new LinkedList<ItemModifier>();

	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{
		//System.out.println(new ItemStack[]{new ItemStack(Items.cake), new ItemStack(Items.carrot), new ItemStack(Items.carrot_on_a_stick), new ItemStack(Items.cauldron)}.getClass().isArray());
		//System.out.println(Items.apple.equals(Items.bed));
		//System.out.println(int.class.isPrimitive());
		//System.out.println(reflectEquals(Items.cake.delegate, Items.carrot.delegate, true));
		//System.out.println("Equals: " + reflectEquals(450, 450, true));
		//FMLCommonHandler.instance().exitJava(0, false);
		configFile = new File(event.getSuggestedConfigurationFile().getAbsolutePath().replace(MODID, NAME));
		if(!configFile.exists())
			try {
				configFile.createNewFile();
				PrintStream out = new PrintStream(new FileOutputStream(configFile));
				writeDefaultConfig(out);
				out.close();
			} catch (IOException e) {
				Logger.getLogger(NAME).log(Level.WARNING, "Could not create config file - " + e);
			}

		jsonFile = new File(event.getSuggestedConfigurationFile().getAbsolutePath().replace(MODID, NAME).replace(".cfg", ".json"));
		if(!jsonFile.exists())
			try {
				jsonFile.createNewFile();
				PrintStream out = new PrintStream(new FileOutputStream(jsonFile));
				writeDefaultJSON(out);
				out.close();
			} catch (IOException e) {
				Logger.getLogger(NAME).log(Level.WARNING, "Could not create json file - " + e);
			}

		modifierDirectory = new File(event.getSuggestedConfigurationFile().getAbsolutePath().replace(MODID + ".cfg", "Modifiers"));
		if(modifierDirectory.exists())
			for(File f : modifierDirectory.listFiles())
				f.delete();
		else
			modifierDirectory.mkdir();

		GameRegistry.registerItem(updateChecker, "updateChecker");
		LanguageRegistry.addName(updateChecker, "Update Checker");
		
		CraftingManager.getInstance().addRecipe(new ItemStack(updateChecker), "RRR", "RBR", "RRR", 'R', Items.redstone, 'B', Items.bread);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		new Thread(){
			public void run(){
				LinkedList<ItemModifier> mods = new LinkedList<ItemModifier>();
				mods.addAll(ModifyBuilder.instance.itemModifiers);
				excreteModifiers(mods);
			}
		}.start();
	}

	public static void excreteModifiers(LinkedList<ItemModifier> modifiers){
		LinkedList<Class<? extends ItemModifier>> classes = new LinkedList<Class<? extends ItemModifier>>();

		for(ItemModifier mod : modifiers){
			if(!classes.contains(mod.getClass()))
			{	
				try{
					File file = new File(modifierDirectory.getAbsolutePath() + File.separator + mod.getClass().getPackage().toString().replace("package ", "") + ".txt");
					if(!file.exists())
						file.createNewFile();
					System.out.println(file.getAbsolutePath());
					PrintStream out = new PrintStream(new FileOutputStream(file, true));
					out.println("********************************");
					out.println("Class: " + mod.getClass().getName());
					out.println("Interfaces: ");
					for(Class intern : mod.getClass().getInterfaces())
						out.println("\t" + intern.getName());
					out.println("Constructors: ");
					for(Constructor constructor : mod.getClass().getDeclaredConstructors())
						out.println("\t" + attemptToDiscernConstructor(constructor));
					out.println("********************************");
					out.close();
					classes.add(mod.getClass());
				}
				catch(Throwable th){}
			}
		}
	}

	public static final HashMap<Class, Object> DV = new HashMap<Class, Object>();
	public static final HashMap<Class, Object> TV = new HashMap<Class, Object>(); //OBJECT IS AN ARRAY!!!!!

	static{

		DV.put(long.class, 0L);
		DV.put(int.class, 0);
		DV.put(short.class, (short) 0);
		DV.put(byte.class, (byte) 0);

		DV.put(double.class, 0.0D);
		DV.put(float.class, 0.0F);

		DV.put(boolean.class, false);

		DV.put(String.class, "Default");
		DV.put(ItemStack.class, new ItemStack(Items.apple));

		DV.put(long[].class, new long[4]);
		DV.put(int[].class, new int[4]);
		DV.put(short[].class, new short[4]);
		DV.put(byte[].class, new byte[4]);

		DV.put(double[].class, new double[4]);
		DV.put(float[].class, new float[4]);

		DV.put(boolean[].class, new boolean[4]);

		DV.put(String[].class, new String[]{"Default", "Default", "Default", "Default"});
		DV.put(ItemStack[].class, new ItemStack[]{new ItemStack(Items.apple), new ItemStack(Items.apple), new ItemStack(Items.apple), new ItemStack(Items.apple)});

		DV.put(EnumSet.class, null);
		
		DV.put(ModRedstone.class, new ModRedstone(0, new ItemStack[]{new ItemStack(Items.redstone)}, new int[]{1}));

		TV.put(long.class, new long[]{1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L});
		TV.put(int.class, new int[]{9, 10, 11, 12, 13, 14, 15, 16});
		TV.put(short.class, new short[]{17, 18, 19, 20, 21, 22, 23, 24});
		TV.put(byte.class, new byte[]{25, 26, 27, 28, 29, 30, 31, 32});

		TV.put(double.class, new double[]{33, 34, 35, 36, 37, 38, 39, 40});
		TV.put(float.class, new float[]{41, 42, 43, 44, 45, 46, 47, 48});

		TV.put(boolean.class, new boolean[]{true, true});

		TV.put(String.class, new String[]{"A", "B", "C", "D", "E", "F", "G", "H"});
		TV.put(ItemStack.class, new ItemStack[]{new ItemStack(Items.apple), new ItemStack(Items.arrow), new ItemStack(Items.baked_potato), new ItemStack(Items.bed), new ItemStack(Items.beef), new ItemStack(Items.blaze_powder), new ItemStack(Items.blaze_rod), new ItemStack(Items.boat)});

		TV.put(long[].class, new long[][]{{101, 102, 103, 104, 105, 106, 107, 108}, {109, 110, 111, 112, 113, 114, 115, 116}, {117, 118, 119, 120, 121, 122, 123, 124}, {125, 126, 127, 128, 129, 130, 131, 132}});
		TV.put(int[].class, new int[][]{{133, 134, 135, 136, 137, 138, 139, 140}, {141, 142, 143, 144, 145, 146, 147, 148}, {149, 150, 151, 152, 153, 154, 155, 156}, {157, 158, 159, 160, 161, 162, 163, 164, 165, 166}});
		TV.put(short[].class, new short[][]{{167, 168, 169, 170, 171, 172, 173, 174}, {175, 176, 177, 178, 179, 180, 181, 182}, {183, 184, 185, 186, 187, 188, 189, 190}});
		TV.put(byte[].class, new byte[][]{{(byte) 191, (byte) 192, (byte) 193, (byte) 194, (byte) 195, (byte) 196, (byte) 197, (byte) 198}, {(byte) 199, (byte) 200, (byte) 201, (byte) 202, (byte) 203, (byte) 204, (byte) 205, (byte) 206}});

		TV.put(double[].class, new double[][]{{50, 51, 52, 53, 54, 55, 56, 57}, {58, 59, 60, 61, 62, 63, 64, 65}});
		TV.put(float[].class, new float[][]{	{67, 68, 69, 70, 71, 72, 73, 74}, {75, 76, 77, 78, 79, 80, 81, 82}});

		TV.put(boolean[].class, new boolean[][]{{true, false, true, false}, {false, true, false, true}});

		TV.put(String[].class, new String[][]{{"Pasta", "Pizza", "Liqueur", "Cola"}, {"Juice", "Apple", "Potato", "Cable"}});
		TV.put(ItemStack[].class, new ItemStack[][]{{new ItemStack(Items.cake), new ItemStack(Items.carrot), new ItemStack(Items.carrot_on_a_stick), new ItemStack(Items.cauldron)}, {new ItemStack(Items.chest_minecart), new ItemStack(Items.chicken), new ItemStack(Items.chainmail_boots), new ItemStack(Items.chainmail_chestplate)}});

		TV.put(EnumSet.class, new EnumSet[]{EnumSet.of(ArmorPart.Head), EnumSet.of(ArmorPart.Chest), EnumSet.of(ArmorPart.Legs), EnumSet.of(ArmorPart.Feet)});
		
		TV.put(ModRedstone.class, new ModRedstone[]{new ModRedstone(0, new ItemStack[]{new ItemStack(Items.sugar)}, new int[]{1}), new ModRedstone(0, new ItemStack[]{new ItemStack(Items.brick)}, new int[]{-1})});
	}

	public static String attemptToDiscernConstructor(Constructor con){
		try{
			LinkedList<Object> defaultParams = new LinkedList<Object>();
			for(Class clazz : con.getParameterTypes())
				defaultParams.add(DV.get(clazz));
			ItemModifier defaultMod = (ItemModifier) con.newInstance(defaultParams.toArray());
			String[] paramNames = new String[con.getParameterTypes().length];

			Class[] params = con.getParameterTypes();
			for(int i = 0; i < paramNames.length; i++)
			{
				Class clazz = params[i];
				Object tests = TV.get(clazz);
				if(tests == null)
				{
					paramNames[i] = "notSupportedPleaseReport";
					continue;
				}
				for(int j = 0; j < Array.getLength(tests); j++)
				{
					LinkedList<Object> parameters = clone(defaultParams);
					parameters.set(i, Array.get(tests, j));
					ItemModifier newMod = (ItemModifier) con.newInstance(parameters.toArray());
					String differing = differingField(newMod, defaultMod);
					//System.out.println("\n" + parameters + "\n" + defaultParams + "\n" + differing);
					paramNames[i] = differing;
				}	
			}

			String construct = con.toString().split("\\(")[0] + "(";
			if(paramNames.length > 0){
				for(int i = 0; i < paramNames.length; i++)
					construct += params[i].getCanonicalName() + " " + (paramNames[i] == null ? "arg" + i : paramNames[i]) + ", ";
				construct = construct.substring(0, construct.length() - 2);
			}
			return construct + ")";
		}
		catch(Throwable th){
			th.printStackTrace();
		}
		return "";
	}

	public static final String[] IGNORE = new String[]{"rnd", "rng", "random", "uid", "uuid", "unique", "rand", "delegate"};

	public static boolean deepEquals(Object one, Object two){
		if(one instanceof List)
			one = ((List) one).toArray();
		if(two instanceof List)
			two = ((List) two).toArray();
		if(one.getClass().isArray() && two.getClass().isArray());
		else
			return false;

		int arrlength = Array.getLength(one);
		Object[] outputOne = new Object[arrlength];
		for(int i = 0; i < arrlength; ++i)
			outputOne[i] = Array.get(one, i);

		arrlength = Array.getLength(two);
		Object[] outputTwo = new Object[arrlength];
		for(int i = 0; i < arrlength; ++i)
			outputTwo[i] = Array.get(two, i);

		if(outputOne.length != outputTwo.length)
			return false;

		for(int i = 0; i < arrlength; i++)
			if(!reflectEquals(outputOne[i], outputTwo[i], true))
				return false;

		return true;
	}

	public static boolean equalFloat(float one, float two){
		if(one < two - 0.01 && one > two + 0.01)
			return true;
		return false;
	}

	public static boolean equalFloat(Float one, Float two){
		if(one < two - 0.01 && one > two + 0.01)
			return true;
		return false;
	}

	public static boolean isWrapperType(Class<?> clazz) {
		return clazz.equals(Boolean.class) || 
				clazz.equals(Integer.class) ||
				clazz.equals(Character.class) ||
				clazz.equals(Byte.class) ||
				clazz.equals(Short.class) ||
				clazz.equals(Double.class) ||
				clazz.equals(Long.class) ||
				clazz.equals(Float.class);
	}

	public static boolean reflectEquals(Object one, Object two, boolean ignoreMissing){

		//System.out.println("\nOne: " + one + "\nTwo: " + two + "\nEquals: " + one == two);
		if(one == null && two == null)
			return true;
		else if(one == null)
			return false;
		else if(two == null)
			return false;

		if(one == two)
			return true;
		if(one.getClass() == float.class && two.getClass() == float.class && equalFloat((Float) one, (Float) two))
			return true;
		if(isWrapperType(one.getClass()) && isWrapperType(two.getClass()))
			return one.equals(two);
		if(deepEquals(one, two))
			return true;
		else if((one.getClass().isArray() || one instanceof List) || (two.getClass().isArray() || two instanceof List))
			return false;
		if(one == two)
			return true;
		if(one instanceof String)
			return one.equals(two);

		LinkedList<Field> fieldsOne = new LinkedList<Field>();
		LinkedList<Field> fieldsTwo = new LinkedList<Field>();

		for(Field f : getAllDeclaredFields(one.getClass()))
			fieldsOne.add(f);

		for(Field f : getAllDeclaredFields(two.getClass()))
			fieldsTwo.add(f);

		for(int i = 0; i < fieldsOne.size(); i++)
			if(fieldsTwo.contains(fieldsOne.get(i)))
			{
				Field field = fieldsOne.get(i);
				if(Modifier.isStatic(field.getModifiers()) || (Modifier.isFinal(field.getModifiers()) && Modifier.isPrivate(field.getModifiers())))
					continue;
				field.setAccessible(true);
				boolean ignore = false;
				for(String s : IGNORE)
					if(field.getName().toLowerCase().contains(s))
					{
						ignore = true;
						break;
					}
				if(ignore)
					continue;

				try {
					if(!reflectEquals(field.get(one), field.get(two), ignoreMissing))
						return false;
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} 
				catch(StackOverflowError e){
					continue;
				}

			}

		return true;
	}

	public static Field[] getAllDeclaredFields(Class clazz){
		LinkedList<Field> fields = new LinkedList<Field>();
		for(Field field : clazz.getDeclaredFields())
			fields.add(field);
		if(clazz.getSuperclass() != null)
			for(Field field : getAllDeclaredFields(clazz.getSuperclass()))
				fields.add(field);

		return fields.toArray(new Field[0]);
	}

	public static String differingField(Object one, Object two){
		if(one == null && two == null)
			return "Both Null";
		else if(one == null)
			return "One is Null";
		else if(two == null)
			return "Two is Null";

		if(deepEquals(one, two))
			return "Arrays!";
		else if((one.getClass().isArray() || one instanceof List) || (two.getClass().isArray() || two instanceof List))
			return "Array Mismatch";
		if(one == two)
			return "Direct Equals";
		if(one instanceof String)
			if(one.equals(two))
				return "String Equals";
			else
				return "String disequals";

		LinkedList<Field> fieldsOne = new LinkedList<Field>();
		LinkedList<Field> fieldsTwo = new LinkedList<Field>();

		for(Field f : getAllDeclaredFields(one.getClass()))
			fieldsOne.add(f);

		for(Field f : getAllDeclaredFields(two.getClass()))
			fieldsTwo.add(f);

		for(int i = 0; i < fieldsOne.size(); i++)
			if(fieldsTwo.contains(fieldsOne.get(i)))
			{
				Field field = fieldsOne.get(i);
				if(Modifier.isStatic(field.getModifiers()) || (Modifier.isFinal(field.getModifiers()) && Modifier.isPrivate(field.getModifiers())))
					continue;
				field.setAccessible(true);
				boolean ignore = false;
				for(String s : IGNORE)
					if(field.getName().toLowerCase().contains(s))
					{
						ignore = true;
						break;
					}
				if(ignore)
					continue;

				try {
					if(!reflectEquals(field.get(one), field.get(two), true))
					{
						//System.out.println("Good Morning " + field.get(one) + ", " + field.get(two));
						return field.getName();
					}
					//					else
					//						System.out.println(field.get(one) + ", " + field.get(two));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} 
				catch(StackOverflowError e){
					continue;
				}

			}

		//System.out.println("\n" + fieldsOne + "\n" + fieldsTwo);

		return "noPurpose";
	}

	public static LinkedList<Object> clone(LinkedList<Object> list){
		LinkedList<Object> newList = new LinkedList<Object>();
		newList.addAll(list);
		return newList;
	}

	public static void writeDefaultConfig(PrintStream out){
		out.println("#This is a comment");
		out.println("//So Is This");
		out.println("#Just remember: 2 spaces for separating syntax, and 1 for arrays");
		out.println("#Syntax:");
		out.println("#add|subtract  classname  constructorParam1  constructorParam2  constructorParamArrayElement0 element1 element2  local::translated.string  itemstackName::itemstackDamage");
		out.println("#Clear syntax (Whitelist): clear ModToolRepair tconstruct.modifiers.tools.ModDurability");
		out.println("#Clear syntax (Blacklist): clear - ModToolRepair tconstruct.modifiers.tools.ModDurability");
		out.flush();
	}

	public static void writeDefaultJSON(PrintStream out){
		out.println("{");
		out.println("\t\"comments\":\"Comments are really just any unused tag\",");
		out.println("\t\"clear\":[],");
		out.println("\t\"clearComment\":\"That was the clear tag. That's used to clear any existing modifiers with that class. Using an asterisk (*) will clear all modifiers.\",");
		out.println("\t\"itemComment\":\"The other thing to note is that items are defined using a dictionary ({}). There, define 'item' using a string and 'damage' using either an int or a string for a wildcard. Stack size is ignored\",");
		out.println("\t\"addModifiers\":["
				+ "\n\t\t{"
				+ "\n\t\t\t\"class\":\"tconstruct.modifiers.tools.ModRedstone\","
				+ "\n\t\t\t\"arg0\":0,"
				+ "\n\t\t\t\"arg1\":["
				+ "\n\t\t\t\t{"
				+ "\n\t\t\t\t\t\"item\":\"minecraft:sugar\""
				+ "\n\t\t\t\t}"
				+ "\n\t\t\t],"
				+ "\n\t\t\t\"arg2\":["
				+ "\n\t\t\t\t1"
				+ "\n\t\t\t]"
				+ "\n\t\t},"
				+ "\n\t\t{"
				+ "\n\t\t\t\"class\":\"tconstruct.modifiers.tools.ModRedstone\","
				+ "\n\t\t\t\"arg0\":1,"
				+ "\n\t\t\t\"arg1\":["
				+ "\n\t\t\t\t{"
				+ "\n\t\t\t\t\t\"item\":\"minecraft:cake\""
				+ "\n\t\t\t\t}"
				+ "\n\t\t\t],"
				+ "\n\t\t\t\"arg2\":["
				+ "\n\t\t\t\t50"
				+ "\n\t\t\t]"
				+ "\n\t\t},"
				+ "\n\t\t\"In an addModifiers array, you're able to add a comment as just a string\","
				+ "\n\t\t\"With the modifier we just added, we use an incrementing argument number, which corresponds to the position of the value, alternatively you can just use an array called params. These values can be obtained from the Modifers json file generated on startup\""
				+ "\n\t],");
		out.println("\t\"addModifierComment\":\"That was the Add Modifiers tag. The Add Modifiers array stores all modifiers to add to the internal registry.\",");
		out.println("\t\"subModifiers\":["
				+ "\n\t\t{"
				+ "\n\t\t\t\"class\":\"tconstruct.modifiers.tools.ModRedstone\","
				+ "\n\t\t\t\"arg0\":1,"
				+ "\n\t\t\t\"arg1\":["
				+ "\n\t\t\t\t{"
				+ "\n\t\t\t\t\t\"item\":\"minecraft:cake\""
				+ "\n\t\t\t\t}"
				+ "\n\t\t\t],"
				+ "\n\t\t\t\"arg2\":["
				+ "\n\t\t\t\t50"
				+ "\n\t\t\t]"
				+ "\n\t\t},"
				+ "\n\t\t\"In a subModifiers array, you're able to add a comment as just a string\","
				+ "\n\t\t\"With the modifier we just added, we use an incrementing argument number, which corresponds to the position of the value, alternatively you can just use an array called params. These values can be obtained from the Modifers json file generated on startup\""
				+ "\n\t],");
		out.println("\t\"subModifierComment\":\"That was the Sub Modifiers tag. The Sub Modifiers array stores all modifiers to subtract from the internal registry.\"");
		out.println("}");
		out.flush();
	}

	@EventHandler
	public void serverStart(FMLServerStartedEvent event)
	{
		((ServerCommandManager) MinecraftServer.getServer().getCommandManager()).registerCommand(new CommandReloadRecipes());
		((ServerCommandManager) MinecraftServer.getServer().getCommandManager()).registerCommand(new CommandPrintRecipes());

		if(!configFile.exists())
			try {
				configFile.createNewFile();
				PrintStream out = new PrintStream(new FileOutputStream(configFile));
				writeDefaultConfig(out);
				out.close();
			} catch (IOException e) {
				Logger.getLogger(NAME).log(Level.WARNING, "Could not create config file - " + e);
			}

		if(!jsonFile.exists())
			try {
				jsonFile.createNewFile();
				PrintStream out = new PrintStream(new FileOutputStream(jsonFile));
				writeDefaultJSON(out);
				out.close();
			} catch (IOException e) {
				Logger.getLogger(NAME).log(Level.WARNING, "Could not create json file - " + e);
			}

		reloadModifiers();

		for(ItemModifier mod : ModifyBuilder.instance.itemModifiers)
			System.out.println(mod.getClass());

		ItemUpdate.checkForUpdate();
	}


	public static void reloadModifiers()
	{		
		if(!configFile.exists())
			try {
				configFile.createNewFile();
				PrintStream out = new PrintStream(new FileOutputStream(configFile));
				writeDefaultConfig(out);
				out.close();
			} catch (IOException e) {
				Logger.getLogger(NAME).log(Level.WARNING, "Could not create config file - " + e);
			}

		if(!jsonFile.exists())
			try {
				jsonFile.createNewFile();
				PrintStream out = new PrintStream(new FileOutputStream(jsonFile));
				writeDefaultJSON(out);
				out.close();
			} catch (IOException e) {
				Logger.getLogger(NAME).log(Level.WARNING, "Could not create json file - " + e);
			}

		for(ItemModifier mod : addedModifiers)
			if(ModifyBuilder.instance.itemModifiers.remove(mod));
			else
				ModifyBuilder.instance.itemModifiers.add(mod);

		addedModifiers.clear();

		try 
		{
			FileInputStream in = new FileInputStream(configFile);
			byte[] data = new byte[in.available()];
			in.read(data);
			in.close();

			String s = new String(data);
			for(String line : s.split("\n"))
				parseLine(line);
		} catch (Throwable th){}

		try 
		{
			FileInputStream in = new FileInputStream(jsonFile);
			byte[] data = new byte[in.available()];
			in.read(data);
			in.close();

			String s = new String(data);
			parseJSON(new JsonParser().parse(s).getAsJsonObject());
		} catch (Throwable th){
			th.printStackTrace();
		}
	}
	
	public static boolean instanceOf(Class one, Class two){
		if(one == two)
			return true;
		else if(one.getSuperclass() != null)
			return instanceOf(one.getSuperclass(), two);
		return false;
	}

	public static ItemModifier parse(JsonObject obj){
		try{
			Class clazz = Class.forName(obj.get("class").getAsString());
			for(Constructor con : clazz.getDeclaredConstructors())
			{
				try{
					con.setAccessible(true);
					boolean allThere = true;
					for(int i = 0; i < con.getParameterTypes().length; i++)
						if(!obj.has("arg" + i))
							allThere = false;
					if(obj.has("params") && !allThere)
						allThere = con.getParameterTypes().length <= obj.get("params").getAsJsonArray().size();
					if(allThere){
						LinkedList<Object> objs = new LinkedList<Object>();
						for(int i = 0; i < con.getParameterTypes().length; i++){
							Class c = con.getParameterTypes()[i];
							JsonElement argElem = obj.has("params") ? obj.get("params").getAsJsonArray().get(i) : obj.get("arg" + i);
							if(c == int.class)
								objs.add(argElem.getAsInt());
							if(c == byte.class)
								objs.add(argElem.getAsByte());
							if(c == long.class)
								objs.add(argElem.getAsLong());
							if(c == float.class)
								objs.add(argElem.getAsFloat());
							if(c == double.class)
								objs.add(argElem.getAsDouble());
							if(c == boolean.class)
								objs.add(argElem.getAsBoolean());
							if(c == String.class)
							{
								String s = argElem.getAsString();
								String[] parts = s.split("::");
								if(parts[0].contains("local"))
									s = parts[0].replace("local", "") + StatCollector.translateToLocal(parts[1]);
								objs.add(s);
							}
							if(c == ItemStack.class){
								String itemName = argElem.getAsJsonObject().get("item").getAsString();
								int damage = obj.has("damage") ? argElem.getAsJsonObject().get("damage").getAsInt() : 0;
								ItemStack item = new ItemStack((Item.itemRegistry.containsKey(itemName) ? (Item) Item.itemRegistry.getObject(itemName) : (Block.blockRegistry.containsKey(itemName) ? Item.getItemFromBlock((Block) Block.blockRegistry.getObject(itemName)) : Items.apple)), 1, damage);
								objs.add(item);
							}

							if(c == EnumSet.class){

								try{
									JsonObject set = argElem.getAsJsonObject();
									String className = set.get("class").getAsString();
									Class<? extends Enum> enumClass = (Class<? extends Enum>) Class.forName(className);
									LinkedList<Enum> enums = new LinkedList<Enum>();
									for(JsonElement e  : set.get("elements").getAsJsonArray())
										for(Enum en : enumClass.getEnumConstants())
											if(en.name().equalsIgnoreCase(e.getAsString()))
												enums.add(en);
									objs.add(EnumSet.copyOf(enums));
								}
								catch(Throwable th){}
							}
							
							if(instanceOf(c, ItemModifier.class))
								objs.add(parse(argElem.getAsJsonObject()));

							if(c.isArray()){
								LinkedList<Object> tmp = new LinkedList<Object>();
								for(JsonElement elem : argElem.getAsJsonArray())
								{
									if(c == int[].class)
										tmp.add(elem.getAsInt());
									if(c == byte[].class)
										tmp.add(elem.getAsByte());
									if(c == long[].class)
										tmp.add(elem.getAsLong());
									if(c == float[].class)
										tmp.add(elem.getAsFloat());
									if(c == double[].class)
										tmp.add(elem.getAsDouble());
									if(c == boolean[].class)
										tmp.add(elem.getAsBoolean());
									if(c == String[].class)
									{
										String s = elem.getAsString();
										String[] parts = s.split("::");
										if(parts[0].contains("local"))
											s = parts[0].replace("local", "") + StatCollector.translateToLocal(parts[1]);
										tmp.add(s);
									}
									if(c == ItemStack[].class){
										String itemName = elem.getAsJsonObject().get("item").getAsString();
										int damage = elem.getAsJsonObject().has("damage") ? elem.getAsJsonObject().get("damage").getAsInt() : 0;
										ItemStack item = new ItemStack((Item.itemRegistry.containsKey(itemName) ? (Item) Item.itemRegistry.getObject(itemName) : (Block.blockRegistry.containsKey(itemName) ? Item.getItemFromBlock((Block) Block.blockRegistry.getObject(itemName)) : Items.apple)), 1, damage);
										tmp.add(item);
									}
								}
								if(c == String[].class)
									objs.add(tmp.toArray(new String[0]));
								if(c == int[].class){
									int[] array = new int[tmp.size()];
									for(int j = 0; j < array.length; j++)
										array[j] = (Integer) tmp.get(j);
									objs.add(array);
								}
								if(c == byte[].class){
									byte[] array = new byte[tmp.size()];
									for(byte j = 0; j < array.length; j++)
										array[j] = (Byte) tmp.get(j);
									objs.add(array);
								}
								if(c == long[].class){
									long[] array = new long[tmp.size()];
									for(int j = 0; j < array.length; j++)
										array[j] = (Long) tmp.get(j);
									objs.add(array);
								}
								if(c == float[].class){
									float[] array = new float[tmp.size()];
									for(int j = 0; j < array.length; j++)
										array[j] = (Float) tmp.get(j);
									objs.add(array);
								}
								if(c == double[].class){
									double[] array = new double[tmp.size()];
									for(int j = 0; j < array.length; j++)
										array[j] = (Double) tmp.get(j);
									objs.add(array);
								}
								if(c == boolean[].class){
									boolean[] array = new boolean[tmp.size()];
									for(int j = 0; j < array.length; j++)
										array[j] = (Boolean) tmp.get(j);
									objs.add(array);
								}
								if(c == ItemStack[].class)
									objs.add(tmp.toArray(new ItemStack[0]));
							}
						}

						ItemModifier mod = (ItemModifier) con.newInstance(objs.toArray());
						return mod;
					}
				}
				catch(Throwable the){
					the.printStackTrace();
				}
			}
		}
		catch(Throwable th){}
		return null;
	}

	public static void parseJSON(JsonObject json){
		try{
			for(JsonElement element : (JsonArray) json.get("clear")){
				String s = element.getAsString();
				LinkedList<ItemModifier> modifiers = new LinkedList<ItemModifier>();
				modifiers.addAll(ModifyBuilder.instance.itemModifiers);
				for(ItemModifier mod : modifiers)
					if(mod.getClass().getSimpleName().equalsIgnoreCase(s) || mod.getClass().getName().equalsIgnoreCase(s) || s.equals("*"))
					{
						ModifyBuilder.instance.itemModifiers.remove(mod);
						addedModifiers.add(mod);
					}
			}

			for(JsonElement element : (JsonArray) json.get("addModifiers")){
				try{
					if(!element.isJsonObject())
						continue;
					JsonObject obj = element.getAsJsonObject();
					ItemModifier mod = parse(obj);
					if(mod == null)
						continue;
					ModifyBuilder.registerModifier(mod);
					addedModifiers.add(mod);
					System.out.println(mod + " is registered!");
				}
				catch(Throwable th){
					th.printStackTrace();
				}
			}
			for(JsonElement element : (JsonArray) json.get("subModifiers")){
				try{
					if(!element.isJsonObject())
						continue;
					JsonObject obj = element.getAsJsonObject();
					ItemModifier mod = parse(obj);
					if(mod == null)
						continue;
					//								LinkedList<ItemModifier> modifiers = new LinkedList<ItemModifier>();
					//								modifiers.addAll(ModifyBuilder.instance.itemModifiers);
					for(int i = 0; i < ModifyBuilder.instance.itemModifiers.size(); i++)
						if(reflectEquals(ModifyBuilder.instance.itemModifiers.get(i), mod, true))
						{
							ModifyBuilder.instance.itemModifiers.remove(i);
							addedModifiers.add(mod);
							System.out.println(mod + " has been removed!");
							break;
						}
				}
				catch(Throwable th){
					th.printStackTrace();
				}
			}
		}
		catch(Throwable th){
			th.printStackTrace();
		}
	}

	public static void parseLine(String line) {
		try
		{
			if(line.trim().startsWith("clear"))
			{
				LinkedList<String> strings = new LinkedList<String>();
				boolean inverted = false;
				for(String s : line.split("\\s+"))
					if(s.equalsIgnoreCase("clear"))
						continue;
					else if(!s.matches("\\s+"))
					{
						if(s.startsWith("-"))
						{
							inverted = true;
							strings.add(s.substring(1));
						}
						else
							strings.add(s);
					}

				LinkedList<ItemModifier> modifiers = new LinkedList<ItemModifier>();
				modifiers.addAll(ModifyBuilder.instance.itemModifiers);
				if(!inverted)
					for(ItemModifier mod : modifiers)
						if(strings.isEmpty())
						{
							addedModifiers.add(mod);
							ModifyBuilder.instance.itemModifiers.remove(mod);
						}
						else if(strings.contains(mod.getClass().getName()) || strings.contains(mod.getClass().getSimpleName()))
						{
							addedModifiers.add(mod);
							ModifyBuilder.instance.itemModifiers.remove(mod);
						}
						else;
				else
					for(ItemModifier mod : modifiers)
					{
						if(strings.contains(mod.getClass().getName()) || strings.contains(mod.getClass().getSimpleName()));
						else
						{
							addedModifiers.add(mod);
							ModifyBuilder.instance.itemModifiers.remove(mod);
						}
					}

				return;
			}
			String[] params = line.split("\\s{2,}");
			boolean add = params[0].equals("add");
			if(!(params[0].startsWith("#") || params[0].startsWith("//")))
			{
				String modifierClass = params[1];
				Class modClass = Class.forName(modifierClass);
				Constructor constructor = null;
				for(Constructor con : modClass.getDeclaredConstructors())
					if(con.getParameterTypes().length == params.length - 2)
					{
						constructor = con;
						constructor.setAccessible(true);
						break;
					}

				if(constructor == null)
					return;

				LinkedList<Object> objs = new LinkedList<Object>();
				for(int i = 0; i < constructor.getParameterTypes().length; i++)
				{
					Class c = constructor.getParameterTypes()[i];
					if(c == String.class)
					{
						String s = params[i + 2];
						String[] parts = s.split("::");
						if(parts[0].contains("local"))
							s = parts[0].replace("local", "") + StatCollector.translateToLocal(parts[1]);
						objs.add(s);
					}
					if(c == int.class)
						objs.add(Integer.parseInt(params[i + 2]));
					if(c == byte.class)
						objs.add(Byte.parseByte(params[i + 2]));
					if(c == long.class)
						objs.add(Long.parseLong(params[i + 2]));
					if(c == float.class)
						objs.add(Float.parseFloat(params[i + 2]));
					if(c == double.class)
						objs.add(Double.parseDouble(params[i + 2]));
					if(c == boolean.class)
						objs.add(Boolean.parseBoolean(params[i + 2]));
					if(c == ItemStack.class)
					{
						ItemStack item = null;
						if(Item.itemRegistry.containsKey(params[i + 2].split("::")[0]))
							item = (new ItemStack((Item) Item.itemRegistry.getObject(params[i + 2].split("::")[0]), 1, params[i + 2].split("::").length == 1 ? 0 : Integer.parseInt(params[i + 2].split("::")[1])));
						else 
							item = (new ItemStack((Block) Block.blockRegistry.getObject(params[i + 2].split("::")[0]), 1, params[i + 2].split("::").length == 1 ? 0 : Integer.parseInt(params[i + 2].split("::")[1])));

						if(item != null)
							objs.add(item);
					}					
					if(c == ItemStack[].class)
					{
						LinkedList<ItemStack> items = new LinkedList<ItemStack>();
						for(String s : params[i + 2].split("\\s+"))
						{
							ItemStack item = null;
							if(Item.itemRegistry.containsKey(s.split("::")[0]))
								item = (new ItemStack((Item) Item.itemRegistry.getObject(s.split("::")[0]), 1, s.split("::").length == 1 ? 0 : Integer.parseInt(s.split("::")[1])));
							else 
								item = (new ItemStack((Block) Block.blockRegistry.getObject(s.split("::")[0]), 1, s.split("::").length == 1 ? 0 : Integer.parseInt(s.split("::")[1])));
							if(item != null)
								items.add(item);
							System.out.println(item);
						}
						objs.add(items.toArray(new ItemStack[0]));
					}

					if(c == String[].class)
					{
						LinkedList<String> strings = new LinkedList<String>();
						for(String s : params[i + 2].split("\\s+"))
						{
							String[] parts = s.split("::");
							if(parts[0].contains("local"))
								s = parts[0].replace("local", "") + StatCollector.translateToLocal(parts[1]);
							strings.add(s);
						}
						objs.add(strings.toArray(new String[0]));
					}
					if(c == int[].class)
					{
						LinkedList<Integer> ints = new LinkedList<Integer>();
						for(String s : params[i + 2].split("\\s+"))
							ints.add(Integer.parseInt(s));
						int[] ret = new int[ints.size()];
						for(int j = 0; j < ret.length; j++)
							ret[j] = ints.get(j);
						objs.add(ret);
					}
					if(c == byte[].class)
					{
						LinkedList<Byte> bytes = new LinkedList<Byte>();
						for(String s : params[i + 2].split("\\s+"))
							bytes.add(Byte.parseByte(s));
						byte[] ret = new byte[bytes.size()];
						for(int j = 0; j < ret.length; j++)
							ret[j] = bytes.get(j);
						objs.add(ret);
					}
					if(c == long[].class)
					{
						LinkedList<Long> longs = new LinkedList<Long>();
						for(String s : params[i + 2].split("\\s+"))
							longs.add(Long.parseLong(s));
						long[] ret = new long[longs.size()];
						for(int j = 0; j < ret.length; j++)
							ret[j] = longs.get(j);
						objs.add(ret);
					}
					if(c == float[].class)
					{
						LinkedList<Float> ints = new LinkedList<Float>();
						for(String s : params[i + 2].split("\\s+"))
							ints.add(Float.parseFloat(s));
						float[] ret = new float[ints.size()];
						for(int j = 0; j < ret.length; j++)
							ret[j] = ints.get(j);
						objs.add(ret);
					}
					if(c == double[].class)
					{
						LinkedList<Double> ints = new LinkedList<Double>();
						for(String s : params[i + 2].split("\\s+"))
							ints.add(Double.parseDouble(s));
						double[] ret = new double[ints.size()];
						for(int j = 0; j < ret.length; j++)
							ret[j] = ints.get(j);
						objs.add(ret);
					}
					if(c == boolean[].class)
					{
						LinkedList<Boolean> ints = new LinkedList<Boolean>();
						for(String s : params[i + 2].split("\\s+"))
							ints.add(Boolean.parseBoolean(s));
						boolean[] ret = new boolean[ints.size()];
						for(int j = 0; j < ret.length; j++)
							ret[j] = ints.get(j);
						objs.add(ret);
					}
					if(c == EnumSet.class)
					{
						LinkedList<Enum> objs2 = new LinkedList<Enum>();
						for(String s : params[i + 2].split("\\s+"))
						{
							String[] parts = s.split("::");
							Class<? extends Enum> individual = (Class<? extends Enum>) Class.forName(parts[0]);
							for(Enum e : individual.getEnumConstants())
								if(e.name().equals(parts[1]))
								{	
									objs2.add(e);
									break;
								}
						}
						objs.add(EnumSet.copyOf(objs2));
					}
				}

				Object o = objs.size() > 0 ? constructor.newInstance(objs.toArray()) : constructor.newInstance();
				if(o instanceof ItemModifier)
				{
					addedModifiers.add((ItemModifier) o);
					if(add)
						ModifyBuilder.registerModifier((ItemModifier) o);
					else
					{
						ItemModifier removMod = (ItemModifier) o;
						ModifyBuilder.instance.itemModifiers.remove(removMod);
					}
				}
				Thread.sleep(10);
			}
		}
		catch(Throwable th){
			th.printStackTrace();
		}

	}

	public static File getConfigFile()
	{
		return configFile;
	}

}
