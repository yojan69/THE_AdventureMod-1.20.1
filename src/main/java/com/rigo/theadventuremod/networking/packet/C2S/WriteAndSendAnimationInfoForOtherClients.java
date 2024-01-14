package com.rigo.theadventuremod.networking.packet.C2S;

import com.rigo.theadventuremod.networking.ModPackets;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;


public class WriteAndSendAnimationInfoForOtherClients
{
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender){
        PacketByteBuf infoForOtherClients = PacketByteBufs.create();

        infoForOtherClients.writeUuid(player.getUuid());
        infoForOtherClients.writeIdentifier(buf.readIdentifier());

        /// Look for all the players in a radius of 150 (not sure if its 150 blocks or smth else)
        for (ServerPlayerEntity p : PlayerLookup.around((ServerWorld) player.getWorld(), player.getPos(), 150)){

            /// Don't send the s2c packet to the "original" client bc that would make it replay the animation
            if (p != player){
                ServerPlayNetworking.send(p, ModPackets.PLAY_ANIMATION_FOR_ALL_CLIENTS, infoForOtherClients);
            }
        }
    }
}
