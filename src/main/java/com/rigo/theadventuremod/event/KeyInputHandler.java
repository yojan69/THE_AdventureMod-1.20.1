package com.rigo.theadventuremod.event;

import com.rigo.theadventuremod.combat.CombatLogic;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler
{
    public static String KEY_CATEGORY_THEADVENTURE = "key.category.theadventuremod.theadventuremod";
    public static String KICK_ATTACK_KEY = "key.theadventuremod.kick_key";
    public static String PUNCH_ATTACK_KEY = "key.theadventuremod.punch_key";

    public static KeyBinding kickKey;
    public static KeyBinding punchKey;



    public static void registerKeyInputs(CombatLogic combatLogicInstance){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (kickKey.wasPressed()){
                combatLogicInstance.PerformKick();
            }
            else if (punchKey.wasPressed()){
                combatLogicInstance.PerformPunch();
            }
        });
    }

    public static void register(CombatLogic combatLogicInstance){
        kickKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                KICK_ATTACK_KEY,
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_2,
                KEY_CATEGORY_THEADVENTURE
        ));
        punchKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                PUNCH_ATTACK_KEY,
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_1,
                KEY_CATEGORY_THEADVENTURE
        ));

        registerKeyInputs(combatLogicInstance);
    }
}
