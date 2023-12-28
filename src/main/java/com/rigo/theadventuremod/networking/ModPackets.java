package com.rigo.theadventuremod.networking;

import com.rigo.theadventuremod.TheAdventureMod;
import com.rigo.theadventuremod.networking.packet.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import static net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.registerGlobalReceiver;

public class ModPackets
{
    public static final Identifier DISABLE_PLAYER_GRAVITY_AND_NO_CLIP
            = new Identifier(TheAdventureMod.MOD_ID, "disable_player_gravity_and_no_clip");
    public static final Identifier ENABLE_PLAYER_GRAVITY_AND_NO_CLIP
            = new Identifier(TheAdventureMod.MOD_ID, "enable_player_gravity_and_no_clip");

    public static final Identifier COMBAT_LOGIC
            = new Identifier(TheAdventureMod.MOD_ID, "combat_logic");


    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(COMBAT_LOGIC, CombatLogicC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(DISABLE_PLAYER_GRAVITY_AND_NO_CLIP, DisablePlayerGravityAndNoClipC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ENABLE_PLAYER_GRAVITY_AND_NO_CLIP, EnablePlayerGravityAndNoClipC2SPacket::receive);
    }

    public static void registerS2CPackets(){

    }
}
