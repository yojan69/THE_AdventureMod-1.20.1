package com.rigo.theadventuremod.networking;

import com.rigo.theadventuremod.TheAdventureMod;
import com.rigo.theadventuremod.networking.packet.DamageEntityC2SPacket;
import com.rigo.theadventuremod.networking.packet.DisablePlayerGravityAndNoClipC2SPacket;
import com.rigo.theadventuremod.networking.packet.EnablePlayerGravityAndNoClipC2SPacket;
import com.rigo.theadventuremod.networking.packet.MovePlayerTowardsEntityC2SPacket;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

public class ModPackets
{
    public static final Identifier DISABLE_PLAYER_GRAVITY_AND_NO_CLIP
            = new Identifier(TheAdventureMod.MOD_ID, "disable_player_gravity_and_no_clip");
    public static final Identifier ENABLE_PLAYER_GRAVITY_AND_NO_CLIP
            = new Identifier(TheAdventureMod.MOD_ID, "enable_player_gravity_and_no_clip");

    public static final Identifier SERVER_ATTACK_LOGIC
            = new Identifier(TheAdventureMod.MOD_ID, "server_attack_logic");

    public static final Identifier DAMAGE_ENTITY
            = new Identifier(TheAdventureMod.MOD_ID, "damage_entity");


    public static void registerC2SPackets(){
        ServerPlayNetworking.registerGlobalReceiver(DISABLE_PLAYER_GRAVITY_AND_NO_CLIP, DisablePlayerGravityAndNoClipC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(ENABLE_PLAYER_GRAVITY_AND_NO_CLIP, EnablePlayerGravityAndNoClipC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(DAMAGE_ENTITY, DamageEntityC2SPacket::receive);
        ServerPlayNetworking.registerGlobalReceiver(SERVER_ATTACK_LOGIC, MovePlayerTowardsEntityC2SPacket::receive);
    }

    public static void registerS2CPackets(){

    }
}
