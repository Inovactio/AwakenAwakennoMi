// java
package com.inovactio.awakenawakennomi.init;

import com.inovactio.awakenawakennomi.animations.KneelPunchGroundAnimation;
import net.minecraft.util.ResourceLocation;
import xyz.pixelatedw.mineminenomi.api.animations.Animation;
import xyz.pixelatedw.mineminenomi.api.animations.AnimationId;

public class ModAnimations {
    //public static final AnimationId<KneelPunchGroundAnimation> KNEEL_PUNCH_GROUND = register("kneel_punch_ground");

    private static <A extends Animation<?, ?>> AnimationId<A> register(String name) {
        return new AnimationId<>(new ResourceLocation("awakenawakennomi", name));
    }

    public static void clientInit() {
        // Enregistre l'implémentation de l'animation côté client
        //AnimationId.register(new KneelPunchGroundAnimation(KNEEL_PUNCH_GROUND));
    }
}
