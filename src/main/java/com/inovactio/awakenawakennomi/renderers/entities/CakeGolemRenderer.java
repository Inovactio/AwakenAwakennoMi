package com.inovactio.awakenawakennomi.renderers.entities;

import com.inovactio.awakenawakennomi.entities.mobs.ability.kuku.CakeGolemEntity;
import com.inovactio.awakenawakennomi.models.entity.CakeGolemModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class CakeGolemRenderer extends MobRenderer<CakeGolemEntity, CakeGolemModel<CakeGolemEntity>> {
    private final ResourceLocation texture;
    public CakeGolemRenderer(EntityRendererManager manager, ResourceLocation texture) {
        super(manager, new CakeGolemModel<>(), 0.7F);
        this.texture = texture;
    }

    @Override
    public ResourceLocation getTextureLocation(CakeGolemEntity entity) {
        return this.texture;
    }

    @Override
    protected void setupRotations(CakeGolemEntity entity, MatrixStack stack, float x, float y, float z) {
        super.setupRotations(entity, stack, x, y, z);
        if (!((double)entity.animationSpeed < 0.01D)) {
            float f = 13.0F;
            float f1 = entity.animationPosition - entity.animationSpeed * (1.0F - z) + 6.0F;
            float f2 = (Math.abs(f1 % 13.0F - 6.5F) - 3.25F) / 3.25F;
            stack.mulPose(Vector3f.ZP.rotationDegrees(6.5F * f2));
        }
    }

    @Override
    protected void scale(CakeGolemEntity entity, MatrixStack stack, float partialTickTime) {
        float s = MathHelper.clamp(entity.getScaleSize(), 0.1F, 20.0F);
        stack.scale(s, s, s);
        super.scale(entity, stack, partialTickTime);
    }

    public static class Factory implements IRenderFactory<CakeGolemEntity> {
        private ResourceLocation texture;

        public Factory(ResourceLocation texture) {
            this.texture = texture;
        }

        public EntityRenderer createRenderFor(EntityRendererManager manager) {
            return new CakeGolemRenderer(manager, this.texture);
        }
    }
}
