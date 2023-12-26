package com.rigo.theadventuremod.combat;

import com.rigo.theadventuremod.TheAdventureMod;
import com.rigo.theadventuremod.networking.ModPackets;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationRegistry;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class CombatLogic implements ClientTickEvents.EndTick
{
    //////// Raycast and result
    static MinecraftClient client = MinecraftClient.getInstance();
    static EntityHitResult entityHit;
    static Vec3d entityPos;
    static Entity entity;

    /// Attacks

    private static int damage;
    private static KeyframeAnimation animation;

    //////// Move towards entity
    static int timeToReachEntity = 15;
    static Vec3d playerStartingPos;
    static Vec3d lerpingPlayerPos;

    static double distanceToEntity;
    static double elapsedTime = 0;
    static double percentageComplete = 0;

    private static boolean canStart = false;
    private static boolean moveTowardsEntity = false;

    private static void AssignVariables(){
        if (client.player != null){

            playerStartingPos = client.player.getPos();

            HitResult hit = client.crosshairTarget;

            if(hit != null && hit.getType() == HitResult.Type.ENTITY)
            {
                entityHit = (EntityHitResult) hit;

                entity = entityHit.getEntity();
                entityPos = entityHit.getEntity().getPos();

                distanceToEntity = playerStartingPos.distanceTo(entityPos);

                canStart = true;
            }
            else{
                canStart = false;
            }
        }
    }

    private static void StartAttack(){
        PerformLogic();
    }

    private static void PerformLogic()
    {
        canStart = false;
        AnimationsHandler.PlayAnimation(client.player, animation);

        ClientPlayNetworking.send(ModPackets.DISABLE_PLAYER_GRAVITY_AND_NO_CLIP, PacketByteBufs.create());

        moveTowardsEntity = true;
    }

    /// MOVE PLAYER TOWARDS ENTITY

    @Override
    public void onEndTick(MinecraftClient client)
    {
        if (moveTowardsEntity)
        {
            if (client.player.getPos().distanceTo(entityPos) > 1.25 && percentageComplete != .8)
            {
                    elapsedTime++;
                    percentageComplete = elapsedTime / timeToReachEntity;
                    lerpingPlayerPos = playerStartingPos.lerp(entityPos, percentageComplete);

                    client.player.setPos(lerpingPlayerPos.x, lerpingPlayerPos.y, lerpingPlayerPos.z);
            }
            else
            {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeDouble(lerpingPlayerPos.x);
                    buf.writeDouble(lerpingPlayerPos.y);
                    buf.writeDouble(lerpingPlayerPos.z);
                    buf.writeDouble(distanceToEntity);
                    buf.writeInt(damage);
                    buf.writeUuid(entity.getUuid());

                    ClientPlayNetworking.send(ModPackets.SERVER_ATTACK_LOGIC, buf);
                    FinishAttack();
            }
        }
    }

    private static void FinishAttack(){
        moveTowardsEntity = false;

        ClientPlayNetworking.send(ModPackets.ENABLE_PLAYER_GRAVITY_AND_NO_CLIP, PacketByteBufs.create());

        // RESTART

        elapsedTime = 0;
        percentageComplete = 0;
        playerStartingPos = null;
        entity = null;
        entityPos = null;
        entityHit = null;
    }

    public static void PerformKick(){
        AssignVariables();

        if(canStart){
            damage = 2;
            if (distanceToEntity < 1.5){
                animation = PlayerAnimationRegistry.getAnimation
                        (new Identifier(TheAdventureMod.MOD_ID, "theadventuremod_kick"));
            }
            else{
                animation = PlayerAnimationRegistry.getAnimation
                        (new Identifier(TheAdventureMod.MOD_ID, "theadventuremod_kick_with_jump"));
            }

            StartAttack();
        }
    }

    public static void PerformPunch(){
        AssignVariables();

        if(canStart){
            damage = 2;
            animation = PlayerAnimationRegistry.getAnimation
                    (new Identifier(TheAdventureMod.MOD_ID, "punch"));
            StartAttack();
        }
    }
}
