# TiM
Tinker's Modifiers

Sometimes, as a modpack author, or even as a user, you just don't agree with the defaults. This is why we're here.

Tinkers' Modifiers allows you to change what modifiers are used by Tinkers' Construct (TiC), and allows you to omit them completely, modify existing modifiers, or add new ones!

 

How to use it

Tinkers' Modifiers is setup in such a way that you can configure the modifiers at any point in time and then simply reload them with a command. This step is also done when the server is started, or a world is loading in single player. The mod generates a few files:


Tinkers' Modifiers.cfg - The original syntax file. Obsolete, and shouldn't be used. Kept purely for backwards compatibility.
Tinkers' Modifers.json - The current configuration file. Using JSON, you're able to configure the modifiers used in the game.
Modifiers - This directory contains all detected Modifier files for Tinkers' Construct. It contains the class name, and also the constructor used to initialise those modifiers.
Syntax

First off, having a decent knowledge of how to use JSON is pretty much required. After that, though, things are a bit clearer, especially with the documentation built in.

clear - The clear tag is simply an array of strings. Each string should be a class name, which will clear all modifiers using that class, or an asterisk to clear all modifiers
addModifiers - This is an array of dictionaries (Or maps, or whatever you want to call them). Each map requires the field 'class', which is a string indicating the class to use for the modifier. Then, starting at arg0, increase the number after arg for every parameter. In addition, you can just make an array called 'params'
subModifiers - This is almost identical to addModifiers, except it removes the modifier rather than adding it.
Parameters

Parameters are handled fairly simply, with most of them being just straight values and handled internally. So integers and other number types are just the number, booleans are just their values, and a similar thing with arrays, being handled between [ and ], EnumSets work similarly in this regard.

ItemStacks, however, are handled like a dictionary/map, with two values - item and damage. Damage is an optional field, and defaults to 0. A negative value indicates a wildcard value.

 

Assistance and Issues

If you have any problems, feel free to send me a message or leave a comment. If you have an actual bug with the mod, then leave it on the GitHub page please.

 

ModPacks

Go wild with it, please. The original purpose of this was for modpacks, so feel free to include it. Just drop me a link/whatever in a PM or in the comments.
