package com.github.dayzminecraft.dayzminecraft.common;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.WorldType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import com.github.dayzminecraft.dayzminecraft.DayZ;
import com.github.dayzminecraft.dayzminecraft.common.blocks.Blocks;
import com.github.dayzminecraft.dayzminecraft.common.effects.Effect;
import com.github.dayzminecraft.dayzminecraft.common.entities.EntityBullet;
import com.github.dayzminecraft.dayzminecraft.common.entities.EntityCrawler;
import com.github.dayzminecraft.dayzminecraft.common.entities.EntityZombieDayZ;
import com.github.dayzminecraft.dayzminecraft.common.items.Items;
import com.github.dayzminecraft.dayzminecraft.common.misc.ChatHandler;
import com.github.dayzminecraft.dayzminecraft.common.misc.Config;
import com.github.dayzminecraft.dayzminecraft.common.misc.LootManager;
import com.github.dayzminecraft.dayzminecraft.common.thirst.Thirst;
import com.github.dayzminecraft.dayzminecraft.common.world.WorldTypes;
import com.github.dayzminecraft.dayzminecraft.common.world.biomes.Biomes;
import com.github.dayzminecraft.dayzminecraft.common.world.generation.StructureHandler;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {
  public void preload(FMLPreInitializationEvent event) {
    ChatHandler.log = Logger.getLogger(DayZ.meta.modId);
    MinecraftForge.EVENT_BUS.register(new CommonEvents());
    MinecraftForge.TERRAIN_GEN_BUS.register(new CommonEventsTerrain());
    Config.init(event);
    ChatHandler.logInfo("Config loaded.");
  }

  public void load(FMLInitializationEvent event) {
    GameRegistry.registerPlayerTracker(new CommonPlayerHandler());
    TickRegistry.registerTickHandler(new CommonTickHandler(), Side.SERVER);

    Blocks.loadBlocks();
    Items.loadItems();
    Biomes.loadBiomes();
    Biomes.addVillages();
    WorldTypes.loadWorldTypes();
    Effect.loadEffects();
    StructureHandler.addDefaultStructures();
    Effect.register();

    EntityRegistry.registerGlobalEntityID(EntityZombieDayZ.class, "Zombie", EntityRegistry.findGlobalUniqueEntityId(), 1, 2);
    EntityRegistry.registerGlobalEntityID(EntityCrawler.class, "Crawler", EntityRegistry.findGlobalUniqueEntityId(), 1, 2);

    EntityRegistry.registerModEntity(EntityBullet.class, "Bullet", 1, DayZ.INSTANCE, 250, 5, true);

    EntityRegistry.addSpawn(EntityZombieDayZ.class, 200, 1, 4, EnumCreatureType.creature, Biomes.biomeForest, Biomes.biomePlains, Biomes.biomeRiver, Biomes.biomeSnowMountains, Biomes.biomeSnowPlains);
    EntityRegistry.addSpawn(EntityCrawler.class, 100, 1, 4, EnumCreatureType.creature, Biomes.biomeForest, Biomes.biomePlains, Biomes.biomeRiver, Biomes.biomeSnowMountains, Biomes.biomeSnowPlains);

    if (Config.canSpawnZombiesInDefaultWorld) {
      EntityRegistry.addSpawn(EntityZombieDayZ.class, 200, 1, 4, EnumCreatureType.creature, WorldType.base12Biomes);
      EntityRegistry.addSpawn(EntityCrawler.class, 100, 1, 4, EnumCreatureType.creature, WorldType.base12Biomes);
    }
  }

  public void postload(FMLPostInitializationEvent event) {
    LootManager.init();

    if (Loader.isModLoaded("ThirstMod")) {
      ChatHandler.logException(Level.SEVERE, "Thirst Mod is not compatible with DayZ, DayZ has it's own thirst system. Remove the Thirst Mod to fix this error.");
    }

    if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
      Logger.getLogger("Minecraft").info("Day Z " + DayZ.meta.version + " Loaded.");

      Logger.getLogger("Minecraft").info("Make sure your server.properties has one of the lines to create a DayZ world.");
      Logger.getLogger("Minecraft").info("level-type=DAYZBASE - To create the original DayZ world.");
      Logger.getLogger("Minecraft").info("level-type=DAYZSNOW - To create snowy DayZ world.");
    }
  }

  @ForgeSubscribe @SuppressWarnings("unused")
  public void serverStarting(FMLServerStartingEvent event) {
    DayZ.INSTANCE.thirst = new Thirst();
  }
}