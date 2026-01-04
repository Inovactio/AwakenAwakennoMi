package com.inovactio.awakenawakennomi.renderers.morphs;

import com.inovactio.awakenawakennomi.AwakenAwakenNoMiMod;
import com.inovactio.awakenawakennomi.renderers.layers.AwakenZoanSmokeLayer;
import com.inovactio.awakenawakennomi.util.CartAddonHelper;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.api.morph.MorphModel;
import xyz.pixelatedw.mineminenomi.init.ModRenderTypes;
import xyz.pixelatedw.mineminenomi.models.morphs.NoMorphModel;
import xyz.pixelatedw.mineminenomi.renderers.layers.MinkFeaturesLayer;
import xyz.pixelatedw.mineminenomi.renderers.layers.abilities.GomuDawnWhipLayer;
import xyz.pixelatedw.mineminenomi.renderers.layers.abilities.GomuSmokeLayer;
import xyz.pixelatedw.mineminenomi.renderers.morphs.MegaRenderer;
import xyz.pixelatedw.mineminenomi.renderers.morphs.ZoanMorphRenderer;

public class AwakenHumanRenderer<T extends AbstractClientPlayerEntity, M extends MorphModel> extends ZoanMorphRenderer<T, M> {
    public AwakenHumanRenderer(EntityRendererManager rendererManager, MorphInfo info, boolean hasSmallHands) {
        super(rendererManager, info, hasSmallHands);
        this.model = new NoMorphModel<>(hasSmallHands);
        this.addLayer(new BipedArmorLayer<>(this, new BipedModel<>(0.5F), new BipedModel<>(1.0F)));
        this.addLayer(new MinkFeaturesLayer<>(this));
        this.addLayer(new GomuSmokeLayer<>(this));
        this.addLayer(new GomuDawnWhipLayer<>(this));
        this.addLayer(new AwakenZoanSmokeLayer<>(this));
        if(AwakenAwakenNoMiMod.hasCartAddonInstalled())
        {
            CartAddonHelper.AddCartAllLayer(this);
        }
    }

    public void render(AbstractClientPlayerEntity entity, float entityYaw, float partialTicks, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, matrixStack, buffer, packedLight);
    }

    protected void renderModel(AbstractClientPlayerEntity entity, MatrixStack matrixStack, int packedLight, IRenderTypeBuffer buffer, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        (this.model).prepareMobModel(entity, limbSwing, limbSwingAmount, partialTicks);
        (this.model).setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        boolean shouldSit = entity.isPassenger() && entity.getVehicle() != null && entity.getVehicle().shouldRiderSit();
        if (shouldSit) {
            matrixStack.translate((double)0.0F, (double)-2.5F, (double)0.0F);
        }

        boolean flag = this.isBodyVisible(entity);
        boolean flag1 = !flag && !entity.isInvisibleTo(Minecraft.getInstance().player);
        RenderType renderType = ModRenderTypes.getZoanRenderType(this.getTextureLocation(entity));
        if (renderType != null && flag) {
            IVertexBuilder ivertexbuilder = buffer.getBuffer(renderType);
            int i = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));
            (this.model).renderToBuffer(matrixStack, ivertexbuilder, packedLight, i, 1.0F, 1.0F, 1.0F, flag1 ? 0.15F : 1.0F);
        }

    }

    protected void scale(AbstractClientPlayerEntity entitylivingbase, MatrixStack matrixStack, float partialTickTime) {
        matrixStack.scale(1.25F, 1.25F, 1.25F);
    }

    public ResourceLocation getTextureLocation(AbstractClientPlayerEntity entity) {
        return entity.getSkinTextureLocation();
    }

    public static class Factory<T extends PlayerEntity> implements IRenderFactory<T> {
        private MorphInfo info;
        private boolean hasSmallHands;

        public Factory(MorphInfo info, boolean hasSmallHands) {
            this.info = info;
            this.hasSmallHands = hasSmallHands;
        }

        public EntityRenderer<? super T> createRenderFor(EntityRendererManager manager) {
            AwakenHumanRenderer renderer = new AwakenHumanRenderer(manager, this.info, this.hasSmallHands);
            return renderer;
        }
    }
}
