package com.inovactio.awakenawakennomi.entities.morph;

import com.google.common.collect.ImmutableMap;
import com.inovactio.awakenawakennomi.abilities.ushiushinomi.giraffe.AwakenGiraffeHeavyPointAbility;
import com.inovactio.awakenawakennomi.renderers.morphs.AwakenGiraffeHeavyPointMorphRenderer;
import com.inovactio.awakenawakennomi.renderers.morphs.AwakenKameWalkPartialMorphRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xyz.pixelatedw.mineminenomi.abilities.ushigiraffe.GiraffeHeavyPointAbility;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.api.morph.MorphModel;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.items.AkumaNoMiItem;
import xyz.pixelatedw.mineminenomi.models.morphs.GiraffeHeavyModel;

import java.util.Map;

public class AwakenGiraffeHeavyMorphInfo extends MorphInfo {
    private static final EntitySize STANDING_SIZE = EntitySize.scalable(1.3F, 4.8F);
    private static final EntitySize CROUCHING_SIZE = EntitySize.scalable(1.3F, 4.7F);

    @OnlyIn(Dist.CLIENT)
    public MorphModel getModel() {
        return new GiraffeHeavyModel();
    }

    @OnlyIn(Dist.CLIENT)
    public void preRenderCallback(LivingEntity entity, MatrixStack matrixStack, float partialTickTime) {
        float scale = 1.8F;
        matrixStack.scale(scale, scale, scale);
    }

    @OnlyIn(Dist.CLIENT)
    public IRenderFactory getRendererFactory(LivingEntity entity) {
        boolean isSlim = false;
        if (entity instanceof AbstractClientPlayerEntity) {
            isSlim = ((AbstractClientPlayerEntity)entity).getModelName().equals("slim");
        }

        return new AwakenGiraffeHeavyPointMorphRenderer.Factory<>(this, isSlim);
    }

    public AkumaNoMiItem getDevilFruit() {
        return ModAbilities.USHI_USHI_NO_MI_GIRAFFE;
    }

    public String getForm() {
        return "awaken_giraffe_heavy";
    }

    public String getDisplayName() {
        return AwakenGiraffeHeavyPointAbility.INSTANCE.getUnlocalizedName();
    }

    public double getEyeHeight() {
        return 4.9;
    }

    public float getShadowSize() {
        return 0.9F;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean hasCulling() {
        return true;
    }

    public Map<Pose, EntitySize> getSizes() {
        return ImmutableMap.<Pose, EntitySize>builder()
                .put(Pose.STANDING, STANDING_SIZE)
                .put(Pose.CROUCHING, CROUCHING_SIZE)
                .build();
    }
}
