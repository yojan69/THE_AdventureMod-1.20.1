package com.rigo.theadventuremod.combat;

import com.rigo.theadventuremod.combat.animation.AnimationsHandler;
import com.rigo.theadventuremod.networking.ModPackets;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class CombatSystem implements ClientTickEvents.EndTick
{
    /// Customizable variables
    double maximumShortRangeDistance = 2;

    int timeToReachEntity = 10;

    int attackCooldown = 12;

    int maxCombo = 3;

    /* The next variables are not meant to be directly modified */

    /// Raycast and result variables

    MinecraftClient client = MinecraftClient.getInstance();
    EntityHitResult entityHit;
    Vec3d entityPos;
    Entity entity;

    /// Attack variables

    private int damage;
    private int comboCount;
    private boolean comboLimitReached;

    /// Move towards entity variables

    Vec3d playerStartingPos;
    Vec3d lerpingPlayerPos;
    double elapsedTime;
    double percentageComplete;
    private boolean startMovingEveryTick = false;

    /// Cooldown

    boolean canAttack = true;
    boolean startCooldown;
    int attackCooldownTimer;

    public void PerformAttack(){
        if (canAttack){
            canAttack = false;
            int damage = 2;

            AssignVariablesAndStartAttack(damage);
        }
    }

    private void AssignVariablesAndStartAttack(int damage){
        if (client.player != null){

            playerStartingPos = client.player.getPos();

            /// Make a raycast to the player's crosshair position
            HitResult hit = client.crosshairTarget;

            if(hit != null && hit.getType() == HitResult.Type.ENTITY)
            {
                /// Assign entity variables

                entityHit = (EntityHitResult) hit;

                if (entity == entityHit.getEntity()){
                    CountCombo();
                }
                else {
                    entity = entityHit.getEntity();
                    StartCombo();
                }

                entityPos = entityHit.getEntity().getPos();

                /// Assign Damage
                this.damage = damage;

                /// Start attack according to the distance

                if (playerStartingPos.distanceTo(entityPos) < maximumShortRangeDistance){
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
        PerformMissLogic();
    }

    private void StartShortRangeAttack(){
        PerformLogic(false);
    }

    private void StartLongRangeAttack(){
        PerformLogic(true);
    }

    private void PerformLogic(boolean isLongRange)
    {
        if (isLongRange){
            AnimationsHandler.PlayRandomKickAnimation(client.player);

            ClientPlayNetworking.send(ModPackets.START_PLAYER_ATTACK_LOGIC, PacketByteBufs.create());

            startMovingEveryTick = true;
        }
        else{
            AnimationsHandler.PlayRandomPunchAnimation(client.player);

            PacketByteBuf buf = WriteBufPacket(playerStartingPos, client.player, entity, damage, comboLimitReached);
            ClientPlayNetworking.send(ModPackets.COMBAT_LOGIC, buf);
            FinishAttack();
        }
    }

    private void PerformMissLogic(){
        AnimationsHandler.PlayRandomPunchAnimation(client.player);

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
                if (client.player.getPos().distanceTo(entityPos) > 1.5)
                {
                    elapsedTime++;
                    percentageComplete = elapsedTime / timeToReachEntity;

                    /// Lerp the player starting position with the entity position by small increments
                    lerpingPlayerPos = playerStartingPos.lerp(entityPos, percentageComplete);

                    /// Move the player
                    client.player.setPos(lerpingPlayerPos.x, lerpingPlayerPos.y, lerpingPlayerPos.z);
                }
                else
                {
                    /// Send buf packet for server to do some logic like damaging entity

                    PacketByteBuf buf = WriteBufPacket(lerpingPlayerPos, client.player, entity, damage, comboLimitReached);
                    ClientPlayNetworking.send(ModPackets.COMBAT_LOGIC, buf);
                    FinishAttack();
                }
            }

            if (startCooldown){
                StartCooldown();
            }
        }
    }

    private PacketByteBuf WriteBufPacket(Vec3d pos, PlayerEntity player, Entity entity, int damage, boolean attackWithoutKnockback){
        PacketByteBuf buf = PacketByteBufs.create();

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

        return buf;
    }

    private void FinishAttack(){
        startMovingEveryTick = false;

        ClientPlayNetworking.send(ModPackets.STOP_PLAYER_ATTACK_LOGIC, PacketByteBufs.create());

        /// Restart Everything
        startCooldown = true;
        percentageComplete = 0;
        elapsedTime = 0;
        playerStartingPos = null;
        entityPos = null;
        entityHit = null;

        if (comboLimitReached){
            RestartCombo();
        }
    }

    private void StartCooldown(){
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

    private void StartCombo(){
        comboCount++;

        if (comboCount != 1){
            comboCount = 1;
        }
    }

    private void CountCombo(){
        comboCount++;

        if (comboCount >= maxCombo){
            comboCount = 0;
            comboLimitReached = true;
        }
    }

    private void RestartCombo(){
        comboLimitReached = false;
    }
}