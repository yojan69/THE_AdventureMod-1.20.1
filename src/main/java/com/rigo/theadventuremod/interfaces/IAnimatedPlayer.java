package com.rigo.theadventuremod.interfaces;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;

/**
 * Used to get a Kosmx's playerAnimator ModifierLayer
 */

public interface IAnimatedPlayer
{
    ModifierLayer<IAnimation> theadventuremod_getModAnimation();
}
