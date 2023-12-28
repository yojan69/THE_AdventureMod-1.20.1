package com.rigo.theadventuremod;

import com.rigo.theadventuremod.event.KeyInputHandler;
import com.rigo.theadventuremod.combat.CombatLogic;
import com.rigo.theadventuremod.networking.ModPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class TheAdventureModClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        CombatLogic combatLogicInstance = new CombatLogic();

        KeyInputHandler.register(combatLogicInstance);
        ModPackets.registerS2CPackets();
        ClientTickEvents.END_CLIENT_TICK.register(combatLogicInstance);
    }
}
