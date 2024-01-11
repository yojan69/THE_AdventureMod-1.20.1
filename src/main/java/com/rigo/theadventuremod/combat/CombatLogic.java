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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class CombatLogic implements ClientTickEvents.EndTick
{
    /// Customizable variables
    int timeToReachEntity = 10;
    int attackCooldown = 12;

    /* The next variables are not meant to be modified */

    /// Raycast and result variables
    MinecraftClient client = MinecraftClient.getInstance();
    EntityHitResult entityHit;
    Vec3d entityPos;
    Entity entity;

    /// Attack variables
    private int damage;
    private KeyframeAnimation animation;
    private int attackedEnemyCount = 0;
    private boolean attackedSameEnemyLimitReached;

    /// Move towards entity variables
    double elapsedTime = 0;
    double percentageComplete = 0;
    Vec3d playerStartingPos;
    Vec3d lerpingPlayerPos;
    private boolean startMovingEveryTick = false;

    /// Cooldown
    boolean canAttack = true;
    boolean startCooldown;
    int attackCooldownTimer;

    /// Animations
    Identifier kick = new Identifier(TheAdventureMod.MOD_ID, "theadventuremod_kick");
    Identifier longKick = new Identifier(TheAdventureMod.MOD_ID, "theadventuremod_kick_with_jump");
    Identifier punch = new Identifier(TheAdventureMod.MOD_ID, "theadventuremod_punch");

    public void PerformAttack(){
        if (canAttack){
            canAttack = false;
            int damage = 2;
            AssignVariablesAndStartAttack(damage);

            client.player.sendMessage(Text.literal(String.valueOf(attackedEnemyCount)));
        }
    }

    private void AssignVariablesAndStartAttack(int damage){
        if (client.player != null){

            playerStartingPos = client.player.getPos();

            HitResult hit = client.crosshairTarget;


            if(hit != null && hit.getType() == HitResult.Type.ENTITY)
            {
                /// Assign entity variables

                entityHit = (EntityHitResult) hit;

                if (entity == entityHit.getEntity()){
                    attackedEnemyCount++;

                    if (attackedEnemyCount >= 3){
                        client.player.sendMessage(Text.literal("turned 0"));
                        client.player.sendMessage(Text.literal("UH?"));
                        attackedEnemyCount = 0;
                        attackedSameEnemyLimitReached = true;
                    }
                }
                else {
                    entity = entityHit.getEntity();
                    attackedEnemyCount++;

                    if (attackedEnemyCount != 1){
                        attackedEnemyCount = 1;
                    }
                }

                entityPos = entityHit.getEntity().getPos();

                /// Assign Damage

                this.damage = damage;

                /// Start attack according

                if (playerStartingPos.distanceTo(entityPos) < 2.25){
                    StartShortRangeAttack();
                }
                else{
                    StartLongRangeAttack();
                }
            }
            else{
                MissedAttack();
            }
        }
    }


    /// No real reason for the SOMETHINGAttack methods but just to make the code look cleaner or smth idk lol

    private void MissedAttack(){
        animation = PlayerAnimationRegistry.getAnimation(kick);
        PerformMissLogic();
    }

    private void StartShortRangeAttack(){
        animation = PlayerAnimationRegistry.getAnimation(kick);

        PerformLogic(false);
    }

    private void StartLongRangeAttack(){
        animation = PlayerAnimationRegistry.getAnimation(longKick);

        PerformLogic(true);
    }

    private void PerformLogic(boolean isLongRange)
    {
        AnimationsHandler.PlayAnimation(client.player, animation);

        if (isLongRange){
            ClientPlayNetworking.send(ModPackets.START_PLAYER_ATTACK_LOGIC, PacketByteBufs.create());

            startMovingEveryTick = true;
        }
        else{
            PacketByteBuf buf = PacketByteBufs.create();
            WriteAndSendBufPacket(buf, playerStartingPos, client.player, entity, damage, attackedSameEnemyLimitReached);
            ClientPlayNetworking.send(ModPackets.COMBAT_LOGIC, buf);
            FinishAttack();
        }
    }

    private void PerformMissLogic(){
        AnimationsHandler.PlayAnimation(client.player, animation);

        if (client.player != null) {
            client.player.setVelocity(0,client.player.getVelocity().y,0);
        }

        FinishAttack();
    }

    /// Move player towards entity every tick

    @Override
    public void onEndTick(MinecraftClient client)
    {
        if (client.player != null)
        {
            if (startMovingEveryTick){
                /* Hard coded a bit the percentageComplete condition bc values over .8 of the lerp are so small
                that go unnoticed really */
                if (client.player.getPos().distanceTo(entityPos) > 1.5 && percentageComplete != .8)
                {
                    elapsedTime++;
                    percentageComplete = elapsedTime / timeToReachEntity;
                    lerpingPlayerPos = playerStartingPos.lerp(entityPos, percentageComplete);

                    /// Move the player
                    client.player.setPos(lerpingPlayerPos.x, lerpingPlayerPos.y, lerpingPlayerPos.z);
                }
                else
                {
                    /// Send buf packet for server to do its thing
                    PacketByteBuf buf = PacketByteBufs.create();
                    WriteAndSendBufPacket(buf, lerpingPlayerPos, client.player, entity, damage, attackedSameEnemyLimitReached);

                    ClientPlayNetworking.send(ModPackets.COMBAT_LOGIC, buf);
                    FinishAttack();
                }
            }

            if (startCooldown){
                StartCooldownTimer();
            }
        }
    }

    private void WriteAndSendBufPacket(PacketByteBuf buf, Vec3d pos, PlayerEntity player, Entity entity, int damage, boolean attackWithoutKnockback){
        buf.writeUuid(entity.getUuid());

        if (pos != null){
            buf.writeDouble(pos.x);
            buf.writeDouble(pos.y);
            buf.writeDouble(pos.z);
        }
        else {
            buf.writeDouble(player.getPos().x);
            buf.writeDouble(player.getPos().y);
            buf.writeDouble(player.getPos().z);
        }

        buf.writeInt(damage);
        buf.writeBoolean(attackWithoutKnockback);
    }

    private void FinishAttack(){
        startMovingEveryTick = false;

        ClientPlayNetworking.send(ModPackets.STOP_PLAYER_ATTACK_LOGIC, PacketByteBufs.create());

        // RESTART
        startCooldown = true;
        percentageComplete = 0;
        elapsedTime = 0;
        playerStartingPos = null;
        entityPos = null;
        entityHit = null;

        if (attackedSameEnemyLimitReached){
            attackedSameEnemyLimitReached = false;
        }
    }

    private void StartCooldownTimer(){
        attackCooldownTimer++;

        if (attackCooldownTimer >= attackCooldown){
            RestartCooldown();
        }
    }

    private void RestartCooldown(){
        startCooldown = false;
        attackCooldownTimer = 0;

        canAttack = true;
    }
}