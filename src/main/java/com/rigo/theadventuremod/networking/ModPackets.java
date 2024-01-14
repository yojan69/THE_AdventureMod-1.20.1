package com.rigo.theadventuremod.networking;

import com.rigo.theadventuremod.TheAdventureMod;
import com.rigo.theadventuremod.networking.packet.C2S.CombatLogic;
import com.rigo.theadventuremod.networking.packet.C2S.StartPlayerAttackLogic;
import com.rigo.theadventuremod.networking.packet.C2S.StopPlayerAttackLogic;
import com.rigo.theadventuremod.networking.packet.C2S.WriteAndSendAnimationInfoForOtherClients;
import com.rigo.theadventuremod.networking.packet.S2C.PlayAnimationForOtherClients;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets
{
    public static final Identifier PLAY_ANIMATION_FOR_ALL_CLIENTS
            = new Identifier(TheAdventureMod.MOD_ID, "play_animation_for_all_clients");
    public static final Identifier WRITE_AND_SEND_ANIMATION_INFO_FOR_OTHER_CLIENTS
            = new Identifier(TheAdventureMod.MOD_ID, "write_and_send_animation_info");

    public static final Identifier START_PLAYER_ATTACK_LOGIC
            = new Identifier(TheAdventureMod.MOD_ID, "start_player_attack_logic");
    public static final Identifier STOP_PLAYER_ATTACK_LOGIC
            = new Identifier(TheAdventureMod.MOD_ID, "stop_player_attack_logic");

    public static final Identifier COMBAT_LOGIC
            = new Identifier(TheAdventureMod.MOD_ID, "combat_logic");


    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(WRITE_AND_SEND_ANIMATION_INFO_FOR_OTHER_CLIENTS, WriteAndSendAnimationInfoForOtherClients::receive);
        ServerPlayNetworking.registerGlobalReceiver(START_PLAYER_ATTACK_LOGIC, StartPlayerAttackLogic::receive);
        ServerPlayNetworking.registerGlobalReceiver(STOP_PLAYER_ATTACK_LOGIC, StopPlayerAttackLogic::receive);
        ServerPlayNetworking.registerGlobalReceiver(COMBAT_LOGIC, CombatLogic::receive);
    }

    public static void registerS2CPackets(){
        ClientPlayNetworking.registerGlobalReceiver(PLAY_ANIMATION_FOR_ALL_CLIENTS, PlayAnimationForOtherClients::receive);
    }
}
