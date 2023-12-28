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
    /// Raycast and result variables
    MinecraftClient client = MinecraftClient.getInstance();
    EntityHitResult entityHit;
    Vec3d entityPos;
    Entity entity;

    /// Attack variables
    private int damage;
    private KeyframeAnimation animation;

    private enum AttackTypes{
        kick, punch
    }

    /// Move towards entity variables
    int timeToReachEntity = 15;
    double elapsedTime = 0;
    double percentageComplete = 0;
    Vec3d playerStartingPos;
    Vec3d lerpingPlayerPos;
    private boolean canStartMovingEveryTick = false;

    private void AssignVariablesAndStartAttack(int damage, AttackTypes attackType){
        if (client.player != null){
            playerStartingPos = client.player.getPos();

            HitResult hit = client.crosshairTarget;

            this.damage = damage;

            if(hit != null && hit.getType() == HitResult.Type.ENTITY)
            {
                entityHit = (EntityHitResult) hit;

                entity = entityHit.getEntity();
                entityPos = entityHit.getEntity().getPos();
                client.player.sendMessage(Text.literal(String.valueOf(playerStartingPos.distanceTo(entityPos))));

                /// Assign Animation

                if (playerStartingPos.distanceTo(entityPos) < 1.25){
                    animation = PlayerAnimationRegistry.getAnimation
                            (new Identifier(TheAdventureMod.MOD_ID, "theadventuremod_"+attackType));
                }
                else{
                    animation = PlayerAnimationRegistry.getAnimation
                            (new Identifier(TheAdventureMod.MOD_ID, "theadventuremod_"+attackType+"_with_jump"));
                }

                StartAttack();
            }
        }
    }

    /// No reason for this method but just so it looks cooler or smth idk lol
    private void StartAttack(){
        PerformLogic();
    }

    private void PerformLogic()
    {
        AnimationsHandler.PlayAnimation(client.player, animation);

        ClientPlayNetworking.send(ModPackets.DISABLE_PLAYER_GRAVITY_AND_NO_CLIP, PacketByteBufs.create());

        canStartMovingEveryTick = true;
    }

    /// Move player towards entity every tick

    @Override
    public void onEndTick(MinecraftClient client)
    {
        if (canStartMovingEveryTick && client.player != null)
        {
            /// Hard coded a bit the percentageComplete condition bc values over .8 of the lerp are so small that go unnoticed really
            if (client.player.getPos().distanceTo(entityPos) > 1.25 && percentageComplete != .8)
            {
                /// Move the player
                    elapsedTime++;
                    percentageComplete = elapsedTime / timeToReachEntity;
                    lerpingPlayerPos = playerStartingPos.lerp(entityPos, percentageComplete);

                    client.player.setPos(lerpingPlayerPos.x, lerpingPlayerPos.y, lerpingPlayerPos.z);
            }
            else
            {
                /// Send buf packet for server to do it's thing
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeUuid(entity.getUuid());
                    if (lerpingPlayerPos != null){
                        buf.writeDouble(lerpingPlayerPos.x);
                        buf.writeDouble(lerpingPlayerPos.y);
                        buf.writeDouble(lerpingPlayerPos.z);
                    }
                    else {
                        buf.writeDouble(client.player.getPos().x);
                        buf.writeDouble(client.player.getPos().y);
                        buf.writeDouble(client.player.getPos().z);
                    }
                    buf.writeInt(damage);

                    ClientPlayNetworking.send(ModPackets.COMBAT_LOGIC, buf);
                    FinishAttack();
            }
        }
    }

    private void FinishAttack(){
        canStartMovingEveryTick = false;

        ClientPlayNetworking.send(ModPackets.ENABLE_PLAYER_GRAVITY_AND_NO_CLIP, PacketByteBufs.create());

        // RESTART

        percentageComplete = 0;
        elapsedTime = 0;
        playerStartingPos = null;
        entityPos = null;
        entityHit = null;
    }

    public void PerformKick(){
        int damage = 2;
        AssignVariablesAndStartAttack(damage, AttackTypes.kick);
    }
    public void PerformPunch(){
        int damage = 2;
        AssignVariablesAndStartAttack(damage, AttackTypes.punch);
    }
}