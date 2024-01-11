package com.rigo.theadventuremod.networking;

import com.rigo.theadventuremod.TheAdventureMod;
import com.rigo.theadventuremod.networking.packet.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver;

public class ModPackets
{
    public static final Identifier START_PLAYER_ATTACK_LOGIC
            = new Identifier(TheAdventureMod.MOD_ID, "start_player_attack_logic");
    public static final Identifier STOP_PLAYER_ATTACK_LOGIC
            = new Identifier(TheAdventureMod.MOD_ID, "stop_player_attack_logic");

    public static final Identifier COMBAT_LOGIC
            = new Identifier(TheAdventureMod.MOD_ID, "combat_logic");


    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(COMBAT_LOGIC, CombatLogicC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(START_PLAYER_ATTACK_LOGIC, StartPlayerAttackLogic::receive);
        ServerPlayNetworking.registerGlobalReceiver(STOP_PLAYER_ATTACK_LOGIC, StopPlayerAttackLogic::receive);
    }

    public static void registerS2CPackets(){

    }
}
