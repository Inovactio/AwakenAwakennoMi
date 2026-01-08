package com.inovactio.awakenawakennomi.renderers.entities;

import com.inovactio.awakenawakennomi.entities.mobs.ability.kuku.CakeGolemEntity;
import com.inovactio.awakenawakennomi.models.entity.CakeGolemModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.MrMagicalCart.cartaddon.entities.mobs.ability.MajinEntity;
import net.MrMagicalCart.cartaddon.renderers.entities.MajinRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class CakeGolemRenderer extends MobRenderer<CakeGolemEntity, CakeGolemModel<CakeGolemEntity>> {
    private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation("textures/entity/cake_golem.png");

    public CakeGolemRenderer(EntityRendererManager p_i46133_1_) {
        super(p_i46133_1_, new CakeGolemModel(), 0.7F);
    }

    public ResourceLocation getTextureLocation(CakeGolemEntity p_110775_1_) {
        return GOLEM_LOCATION;
    }

    protected void setupRotations(CakeGolemEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
        super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
        if (!((double)p_225621_1_.animationSpeed < 0.01D)) {
            float f = 13.0F;
            float f1 = p_225621_1_.animationPosition - p_225621_1_.animationSpeed * (1.0F - p_225621_5_) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            p_225621_2_.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
        }
    }

    public static class Factory implements IRenderFactory<CakeGolemEntity> {
        public EntityRenderer<? super CakeGolemEntity> createRenderFor(EntityRendererManager manager) {
            return new CakeGolemRenderer(manager);
        }
    }
}
