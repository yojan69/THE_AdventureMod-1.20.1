package com.rigo.theadventuremod;

import com.rigo.theadventuremod.combat.animation.AnimationRegistry;
import com.rigo.theadventuremod.input.KeyInputHandler;
import com.rigo.theadventuremod.combat.CombatSystem;
import com.rigo.theadventuremod.networking.ModPackets;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;


public class TheAdventureModClient implements ClientModInitializer
{
    public static CombatSystem COMBAT_SYSTEM_INSTANCE;

    @Override
    public void onInitializeClient() {
        COMBAT_SYSTEM_INSTANCE = new CombatSystem();
        KeyInputHandler.register(COMBAT_SYSTEM_INSTANCE);
        ClientTickEvents.END_CLIENT_TICK.register(COMBAT_SYSTEM_INSTANCE);

        ModPackets.registerS2CPackets();

        ClientLifecycleEvents.CLIENT_STARTED.register((client) -> {
            AnimationRegistry.Load(MinecraftClient.getInstance().getResourceManager(),
                    AnimationRegistry.punchAnimationsFolder,
                    AnimationRegistry.punchAnimations);

            AnimationRegistry.Load(MinecraftClient.getInstance().getResourceManager(),
                    AnimationRegistry.kickAnimationsFolder,
                    AnimationRegistry.kickAnimations);
        });
    }
}
