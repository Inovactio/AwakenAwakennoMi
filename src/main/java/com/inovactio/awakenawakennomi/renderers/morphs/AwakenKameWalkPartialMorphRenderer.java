package com.inovactio.awakenawakennomi.renderers.morphs;

import com.inovactio.awakenawakennomi.AwakenAwakenNoMiMod;
import com.inovactio.awakenawakennomi.renderers.layers.AwakenZoanSmokeLayer;
import com.inovactio.awakenawakennomi.util.CartAddonHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.api.morph.MorphModel;
import xyz.pixelatedw.mineminenomi.renderers.layers.AuraLayer;
import xyz.pixelatedw.mineminenomi.renderers.layers.MinkFeaturesLayer;
import xyz.pixelatedw.mineminenomi.renderers.layers.morphs.KameWalkLayer;
import xyz.pixelatedw.mineminenomi.renderers.morphs.ZoanMorphRenderer;

@OnlyIn(Dist.CLIENT)
public class AwakenKameWalkPartialMorphRenderer<T extends AbstractClientPlayerEntity, M extends MorphModel> extends ZoanMorphRenderer<T, M> {
    public AwakenKameWalkPartialMorphRenderer(EntityRendererManager renderManager, MorphInfo info, boolean hasSmallHands) {
        super(renderManager, info, hasSmallHands);
        this.removeLayer(HeldItemLayer.class);
        this.addLayer(new KameWalkLayer<>(this));;
        this.addLayer(new HeldItemLayer<>(this));
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
        this.addLayer(new MinkFeaturesLayer<>(this));
        this.addLayer(new CapeLayer(this));
        this.addLayer(new ElytraLayer<>(this));
        this.addLayer(new AwakenZoanSmokeLayer<>(this));
        if(AwakenAwakenNoMiMod.hasCartAddonInstalled())
        {
            CartAddonHelper.AddCartCyborgLayer(this);
            CartAddonHelper.AddCartFishmanLayer(this);
            CartAddonHelper.AddCartOniLayer(this);
            CartAddonHelper.AddCartCuriosLayer(this);
        }
    }

    protected void scale(AbstractClientPlayerEntity entitylivingbase, MatrixStack matrixStack, float partialTickTime) {
        matrixStack.scale(1.25F, 1.25F, 1.25F);
    }

    public void render(AbstractClientPlayerEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    public ResourceLocation getTextureLocation(AbstractClientPlayerEntity entity) {
        return entity.getSkinTextureLocation();
    }

    public static class Factory<T extends PlayerEntity> implements IRenderFactory<T> {
        private final MorphInfo info;
        private final boolean hasSmallHands;

        public Factory(MorphInfo info, boolean hasSmallHands) {
            this.info = info;
            this.hasSmallHands = hasSmallHands;
        }

        public EntityRenderer<? super T> createRenderFor(EntityRendererManager manager) {
            return new AwakenKameWalkPartialMorphRenderer(manager, this.info, this.hasSmallHands);
        }
    }
}

