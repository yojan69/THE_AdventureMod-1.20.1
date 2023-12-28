package com.rigo.theadventuremod.combat;

import com.rigo.theadventuremod.interfaces.IAnimatedPlayer;
import dev.kosmx.playerAnim.api.layered.KeyframeAnimationPlayer;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import net.minecraft.entity.player.PlayerEntity;

public class AnimationsHandler
{
    public static void PlayAnimation(PlayerEntity player, KeyframeAnimation animation){
        var animationContainer = ((IAnimatedPlayer) player).theadventuremod_getModAnimation();
        animationContainer.setAnimation(new KeyframeAnimationPlayer(animation));
    }
}