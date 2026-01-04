package com.inovactio.awakenawakennomi.renderers.morphs;

import com.inovactio.awakenawakennomi.renderers.layers.AwakenZoanSmokeLayer;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.api.morph.MorphModel;
import xyz.pixelatedw.mineminenomi.renderers.morphs.ZoanMorphRenderer;

public class AwakenMoguHeavyPointMorphRenderer<T extends AbstractClientPlayerEntity, M extends MorphModel> extends ZoanMorphRenderer<T, M> {
    public AwakenMoguHeavyPointMorphRenderer(EntityRendererManager renderManager, MorphInfo info, boolean hasSmallHands) {
        super(renderManager, info, hasSmallHands);
        this.removeLayer(HeldItemLayer.class);
        this.addLayer(new AwakenZoanSmokeLayer<>(this));
    }

    public void render(AbstractClientPlayerEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    public static class Factory<T extends PlayerEntity> implements IRenderFactory<T> {
        private final MorphInfo info;
        private final boolean hasSmallHands;

        public Factory(MorphInfo info, boolean hasSmallHands) {
            this.info = info;
            this.hasSmallHands = hasSmallHands;
        }

        public EntityRenderer<? super T> createRenderFor(EntityRendererManager manager) {
            return new AwakenMoguHeavyPointMorphRenderer(manager, this.info, this.hasSmallHands);
        }
    }
}
