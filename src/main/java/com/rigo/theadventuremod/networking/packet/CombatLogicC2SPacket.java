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

import java.util.UUID;


public class CombatLogicC2SPacket
{
    public static void receive(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler,
                               PacketByteBuf buf, PacketSender responseSender){
        /// Buf packet reading order matters

        ServerWorld world = (ServerWorld) player.getWorld();

        /* Getting the entity from the server world (server world means is the world, no idea why is there
           a server and a client world */

        UUID entityUuid = buf.readUuid();
        Entity entity = world.getEntity(entityUuid);

        /* Get the player pos to teleport them there bc the movement made every tick on the CombatLogic method is only
           rendered to the clients but in the server that didn't happen, what this means is that things like the player's
           hitbox is just left behind, so a teleport actually puts the player and all his things in the correct place*/

        Vec3d lastPlayerPos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        player.teleport(lastPlayerPos.x, lastPlayerPos.y, lastPlayerPos.z);

        int damage = buf.readInt();
        boolean attackWithoutKnockback = buf.readBoolean();

        if (entity != null){
            /*Distance to entity quite farther away bc packet takes a bit to be sent, so the player might've moved
            a lil bit*/
            if (player.getPos().distanceTo(entity.getPos()) < 2.5) {
                entity.damage(entity.getDamageSources().playerAttack(player), damage);

                if (attackWithoutKnockback){
                    player.sendMessage(Text.literal("attacked without knockback"));
                    entity.setVelocity(0,0,0);
                }
            }
        }
    }
}
