package com.rigo.theadventuremod.networking.packet.S2C;

import com.rigo.theadventuremod.combat.animation.AnimationsHandler;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.UUID;


public class PlayAnimationForOtherClients
{
    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender) {
        UUID playerToAnimateUuid = buf.readUuid();
        Identifier animationIdentifier = buf.readIdentifier();

        if (client.world != null){

            /// Get the desired client's player by searching him on the other client's world
            PlayerEntity playerToAnimate = client.world.getPlayerByUuid(playerToAnimateUuid);

            /*
            Check if the desired client's player isn't null bc I believe the other client's world could somehow not find the desired client's
            player
            */
            if (playerToAnimate != null){
                AnimationsHandler.PlayAnimation(playerToAnimate, animationIdentifier, true);
            }
        }
    }
}
