package com.inovactio.awakenawakennomi.mixins.mineminenomi.abilities;

import com.inovactio.awakenawakennomi.init.ModMorphs;
import com.inovactio.awakenawakennomi.util.MorphRequirementRewritterHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.pixelatedw.mineminenomi.abilities.mogu.MoguraBananaAbility;
import xyz.pixelatedw.mineminenomi.abilities.mogu.MoguraTonpoAbility;
import xyz.pixelatedw.mineminenomi.abilities.ushigiraffe.BiganAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.Ability;
import xyz.pixelatedw.mineminenomi.api.abilities.components.AbilityComponent;

@Mixin(Ability.class)
public class AbilityMixin {

    @Inject(method = "addComponents",
            at = @At("HEAD"),
            remap = false
    )
    private void onAddComponents(AbilityComponent[] components, CallbackInfo ci) {
        MorphRequirementRewritterHelper.replaceRequireMorphIfInstance(
                this,
                components,
                MoguraTonpoAbility.class,
                ModMorphs.AWAKEN_MOGU.get(),
                xyz.pixelatedw.mineminenomi.init.ModMorphs.MOGU_HEAVY.get()
        );
        MorphRequirementRewritterHelper.replaceRequireMorphIfInstance(
                this,
                components,
                MoguraBananaAbility.class,
                ModMorphs.AWAKEN_MOGU.get(),
                xyz.pixelatedw.mineminenomi.init.ModMorphs.MOGU_HEAVY.get()
        );
        MorphRequirementRewritterHelper.replaceRequireMorphIfInstance(
                this,
                components,
                BiganAbility.class,
                ModMorphs.AWAKEN_GIRAFFE.get(),
                xyz.pixelatedw.mineminenomi.init.ModMorphs.GIRAFFE_WALK.get(),
                xyz.pixelatedw.mineminenomi.init.ModMorphs.GIRAFFE_HEAVY.get()
        );
    }
}
