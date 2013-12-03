package com.github.dayzminecraft.dayzminecraft.common.thirst;

import java.util.HashMap;

import net.minecraft.entity.player.EntityPlayer;

import com.github.dayzminecraft.dayzminecraft.common.misc.ChatHandler;
import com.github.dayzminecraft.dayzminecraft.common.misc.DamageType;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.util.StatCollector;

public class Thirst {
  public HashMap<String, Integer> thirstMap = new HashMap<String, Integer>();

  /** Performs the thirst changes for all players in the thirst map */
  public void handleThirst() {
    for (String username : thirstMap.keySet()) {
      handleThirst(username);
    }
  }

  /**
   * Performs the thirst changes for the specified player
   *
   * @param username player to handle
   */
  private void handleThirst(String username) {
    int playerThirst = thirstMap.get(username);
    EntityPlayer player = FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().getPlayerForUsername(username);

    if (player == null || player.isDead || player.capabilities.isCreativeMode) {
      return;
    } else if (playerThirst == 20000) {
      ChatHandler.chatWarning(player, StatCollector.translateToLocal("thirst.thirdwarning"));
      playerThirst++;
    } else if (playerThirst == 18000) {
      ChatHandler.chatWarning(player, StatCollector.translateToLocal("thirst.secondwarning"));
      playerThirst++;
    } else if (playerThirst == 16000) {
      ChatHandler.chatWarning(player, StatCollector.translateToLocal("thirst.firstwarning"));
      playerThirst++;
    } else if (playerThirst >= 24000 && FMLCommonHandler.instance().getMinecraftServerInstance().getTickCounter() % 40 == 0) {
      player.attackEntityFrom(DamageType.thirstDeath, 1);
    } else if (player.isSprinting() || player.isAirBorne) {
      playerThirst = playerThirst + 2;
    } else {
      playerThirst++;
    }
    ChatHandler.logDebug(String.valueOf(playerThirst));
    thirstMap.put(username, playerThirst);
  }

  public int getPlayerThirst(EntityPlayer player) {
    return thirstMap.get(player.username);
  }

  public int getPlayerThirst(String username) {
    return thirstMap.get(username);
  }

  /**
   * Subtracts the thirst for the specified player Used on drinking
   *
   * @param player
   * @param amount
   */
  public void subtractThirst(EntityPlayer player, int amount) {
    int playerThirst = thirstMap.get(player.username);
    playerThirst = playerThirst - amount;
    if (playerThirst < 0) {
      playerThirst = 0;
    }
    thirstMap.put(player.username, playerThirst);
    if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
      ChatHandler.chatConfirmation(player, StatCollector.translateToLocal("thirst.replentish"));
    }
  }
}
