package com.rigo.theadventuremod.networking.packet;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;


public class MovePlayerTowardsEntityC2SPacket
{
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender){

        Vec3d lastPlayerPos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());

        ServerWorld world = (ServerWorld) player.getWorld();
        Entity entity = world.getEntity(buf.readUuid());
        
        player.teleport(lastPlayerPos.x, lastPlayerPos.y, lastPlayerPos.z);

            if (lastPlayerPos.distanceTo(entity.getPos()) < 1.5) {
                entity.damage(entity.getDamageSources().playerAttack(player), buf.readInt());
            }
            else {
                player.sendMessage(Text.literal("ran away gadayum"));
            }
    }
}
