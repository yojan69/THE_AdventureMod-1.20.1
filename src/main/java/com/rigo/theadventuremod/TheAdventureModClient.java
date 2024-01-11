package com.rigo.theadventuremod;

import com.rigo.theadventuremod.input.KeyInputHandler;
import com.rigo.theadventuremod.combat.CombatLogic;
import com.rigo.theadventuremod.networking.ModPackets;
import de.maxhenkel.configbuilder.ConfigBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import java.nio.file.Paths;

public class TheAdventureModClient implements ClientModInitializer
{
    public static CombatLogic COMBAT_LOGIC_INSTANCE;


    @Override
    public void onInitializeClient()
    {
        COMBAT_LOGIC_INSTANCE = new CombatLogic();
        KeyInputHandler.register(COMBAT_LOGIC_INSTANCE);
        ModPackets.registerS2CPackets();
        ClientTickEvents.END_CLIENT_TICK.register(COMBAT_LOGIC_INSTANCE);
    }
}
