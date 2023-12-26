package com.rigo.theadventuremod.networking.packet;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.UUID;

public class DamageEntityC2SPacket
{
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender)
    {
        Entity entity = null;
        UUID entityUuid = buf.readUuid();

        ServerWorld world = (ServerWorld) player.getWorld();

        if (entityUuid != null)
        {
            entity = world.getEntity(entityUuid);

            if (entity != null){
                entity.damage(entity.getDamageSources().playerAttack(player), 10);
            }
        }
    }
}
