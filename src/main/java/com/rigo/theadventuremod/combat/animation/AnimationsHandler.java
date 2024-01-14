package com.rigo.theadventuremod.combat.animation;

import com.rigo.theadventuremod.interfaces.IAnimatedPlayer;
import com.rigo.theadventuremod.networking.ModPackets;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.api.layered.modifier.MirrorModifier;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Random;

public class AnimationsHandler
{
    public static void PlayRandomKickAnimation(PlayerEntity player){
        Identifier randomIdentifier = GetRandomIdentifierFromMap(AnimationRegistry.kickAnimations);

        PlayAnimation(player, randomIdentifier, false);
    }

    public static void PlayRandomPunchAnimation(PlayerEntity player){
        Identifier randomIdentifier = GetRandomIdentifierFromMap(AnimationRegistry.punchAnimations);

        PlayAnimation(player, randomIdentifier, false);
    }

    public static void PlayAnimation(PlayerEntity player, Identifier animationIdentifier, boolean isOtherClient){
        KeyframeAnimation animation = AnimationRegistry.animations.get(animationIdentifier);

        var animator = ((IAnimatedPlayer) player).theadventuremod_getModAnimation();
        animator.addModifierLast(new MirrorModifier(true));

        if (animation != null){
            animator.setAnimation(new KeyframeAnimationPlayer(animation));
        }

        if (!isOtherClient){
            PlayAnimationForOtherClients(animationIdentifier);
        }
    }

    private static void PlayAnimationForOtherClients(Identifier animationIdentifier){
        PacketByteBuf infoForOtherClients = PacketByteBufs.create();
        infoForOtherClients.writeIdentifier(animationIdentifier);

        ClientPlayNetworking.send(ModPackets.WRITE_AND_SEND_ANIMATION_INFO_FOR_OTHER_CLIENTS, infoForOtherClients);
    }

    private static <K, I> K GetRandomIdentifierFromMap(Map<K, I> map){
        if (map.isEmpty()) {
            return null;
        }

        int randomIndex = new Random().nextInt(map.size());
        return map.keySet().stream().skip(randomIndex).findFirst().orElse(null);
    }
}