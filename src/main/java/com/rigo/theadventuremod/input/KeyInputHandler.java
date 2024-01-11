package com.rigo.theadventuremod.input;

import com.rigo.theadventuremod.combat.CombatLogic;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyInputHandler
{
    public static String KEY_CATEGORY_THEADVENTURE = "key.category.theadventuremod.theadventuremod";
    public static String ATTACK_KEY = "key.theadventuremod.attack_key";

    public static KeyBinding attackKey;

    public static void registerKeyInputs(CombatLogic combatLogicInstance){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (attackKey.isPressed()){
                combatLogicInstance.PerformAttack();
            }
        });

    }
    public static void register(CombatLogic combatLogicInstance){
        attackKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                ATTACK_KEY,
                InputUtil.Type.MOUSE,
                GLFW.GLFW_MOUSE_BUTTON_2,
                KEY_CATEGORY_THEADVENTURE
        ));

        registerKeyInputs(combatLogicInstance);
    }
}
