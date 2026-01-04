// java
package com.inovactio.awakenawakennomi.init;

import com.inovactio.awakenawakennomi.animations.kame.SpinAnimation;
import net.minecraft.util.ResourceLocation;
import xyz.pixelatedw.mineminenomi.api.animations.Animation;
import xyz.pixelatedw.mineminenomi.api.animations.AnimationId;

public class ModAnimations {
    //public static final AnimationId<KneelPunchGroundAnimation> KNEEL_PUNCH_GROUND = register("kneel_punch_ground");
    public static final AnimationId<SpinAnimation> SPIN = register("spin");

    private static <A extends Animation<?, ?>> AnimationId<A> register(String name) {
        return new AnimationId<>(new ResourceLocation("awakenawakennomi", name));
    }

    public static void clientInit() {
        //AnimationId.register(new KneelPunchGroundAnimation(KNEEL_PUNCH_GROUND));
        AnimationId.register(new SpinAnimation(SPIN));
    }
}
