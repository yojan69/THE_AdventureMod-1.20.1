package com.rigo.theadventuremod.event.interfaces;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;

public interface IAnimatedPlayer
{
    ModifierLayer<IAnimation> theadventuremod_getModAnimation();
}
