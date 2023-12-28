package com.rigo.theadventuremod;

import com.rigo.theadventuremod.networking.ModPackets;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TheAdventureMod implements ModInitializer {
	public static final String MOD_ID = "theadventuremod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
	{
		ModPackets.registerC2SPackets();

	}
}