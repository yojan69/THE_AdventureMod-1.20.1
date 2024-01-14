package com.rigo.theadventuremod.combat.animation;

import com.rigo.theadventuremod.TheAdventureMod;
import dev.kosmx.playerAnim.core.data.KeyframeAnimation;
import dev.kosmx.playerAnim.core.data.gson.AnimationSerializing;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rigo.theadventuremod.TheAdventureMod.LOGGER;

/**
 * Used to get and put the animations into a map for easier access
 */

public class AnimationRegistry {

    public static final String kickAnimationsFolder = "animations/kick_animations";
    public static final String punchAnimationsFolder = "animations/punch_animations";

    /*
    Punch and kick animations will be put in their according map, as well as "animations" map to make
    some things simpler
    */

    public static Map<Identifier, KeyframeAnimation> animations = new HashMap<>();
    public static Map<Identifier, KeyframeAnimation> kickAnimations = new HashMap<>();
    public static Map<Identifier, KeyframeAnimation> punchAnimations = new HashMap<>();

    public static void Load(ResourceManager resourceManager,
                            String animationsFolderToSearch,
                            Map<Identifier, KeyframeAnimation> mapToGiveAnimations){

        /*
         Get identifier and animation from every file ending with .json in kick or punch animations folder inside:
         resources/assets/theadventuremod/animations
        */

        for (var entry : resourceManager.findResources(animationsFolderToSearch,
                fileName -> fileName.getPath().endsWith(".json")).entrySet()) {

            var identifier = entry.getKey();
            var resource = entry.getValue();
            try {
                List<KeyframeAnimation> readAnimations = AnimationSerializing.
                        deserializeAnimation(resource.getInputStream());
                KeyframeAnimation animation = readAnimations.get(0);

                /*
                Convert identifier to string to be able to clean the identifier up (remove unnecessary path),
                and "convert" the string back to identifier, might not be the best way to do it but tbh I'm new at this
                */

                var id = identifier
                        .toString()
                        .replace( animationsFolderToSearch + "/", "");
                id = id.substring(0, id.lastIndexOf('.'));

                /// Add the identifier and animation to the animations map and to the desired map

                AnimationRegistry.animations.put(Identifier.tryParse(id), animation);
                mapToGiveAnimations.put(Identifier.tryParse(id), animation);

            } catch (Exception e) {
                LOGGER.error("Failed to load animation " + identifier.toString());
                e.printStackTrace();
            }
        }
    }
}
