package com.inovactio.awakenawakennomi.entities.morph;

import com.google.common.collect.ImmutableMap;
import com.inovactio.awakenawakennomi.abilities.mogumogunomi.AwakenMoguHeavyPointAbility;
import com.inovactio.awakenawakennomi.renderers.morphs.AwakenGiraffeHeavyPointMorphRenderer;
import com.inovactio.awakenawakennomi.renderers.morphs.AwakenMoguHeavyPointMorphRenderer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xyz.pixelatedw.mineminenomi.abilities.mogu.MoguHeavyPointAbility;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.api.morph.MorphModel;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.items.AkumaNoMiItem;
import xyz.pixelatedw.mineminenomi.models.morphs.MoguMoleModel;

import java.util.Map;

public class AwakenMoguHeavyMorphInfo extends MorphInfo {
    private static final EntitySize STANDING_SIZE = EntitySize.scalable(0.5F, 1.4F);
    private static final EntitySize CROUCHING_SIZE = EntitySize.scalable(0.5F, 1.3F);

    @OnlyIn(Dist.CLIENT)
    public MorphModel getModel() {
        return new MoguMoleModel();
    }

    @OnlyIn(Dist.CLIENT)
    public void preRenderCallback(LivingEntity entity, MatrixStack matrixStack, float partialTickTime) {
        float scale = 0.8F;
        matrixStack.scale(scale, scale, scale);
    }

    @OnlyIn(Dist.CLIENT)
    public IRenderFactory getRendererFactory(LivingEntity entity) {
        boolean isSlim = false;
        if (entity instanceof AbstractClientPlayerEntity) {
            isSlim = ((AbstractClientPlayerEntity)entity).getModelName().equals("slim");
        }
        return new AwakenMoguHeavyPointMorphRenderer.Factory<>(this, isSlim);
    }

    public AkumaNoMiItem getDevilFruit() {
        return ModAbilities.MOGU_MOGU_NO_MI;
    }

    public String getForm() {
        return "awaken_mogu_heavy";
    }

    public String getDisplayName() {
        return AwakenMoguHeavyPointAbility.INSTANCE.getUnlocalizedName();
    }

    public double getEyeHeight() {
        return 1.4;
    }

    public float getShadowSize() {
        return 0.4F;
    }

    public Map<Pose, EntitySize> getSizes() {
        return ImmutableMap.<Pose, EntitySize>builder()
                .put(Pose.STANDING, STANDING_SIZE)
                .put(Pose.CROUCHING, CROUCHING_SIZE)
                .build();
    }
}
