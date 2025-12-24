package com.inovactio.awakenawakennomi.animations.kame;

import com.inovactio.awakenawakennomi.api.animations.Animation;
import com.inovactio.awakenawakennomi.api.animations.IHandAnimation;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.vector.Vector3f;
import xyz.pixelatedw.mineminenomi.api.animations.AnimationId;

public class SpinAnimation extends Animation<LivingEntity, BipedModel<?>> {
    protected float speed = 80.0F;

    public SpinAnimation(AnimationId<? extends SpinAnimation> animId) {
        super(animId);
        this.setAnimationSetup(this::setup);
        this.setAnimationAngles(this::angles);
        this.setAnimationHeldItem(this::heldItem);
    }

    public void setup(LivingEntity player, MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, float rotationYaw, float partialTicks) {
        float rot = (float)this.getTime() * this.speed + partialTicks;
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(rot));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(90));
        float yOffset = -player.getBbHeight() * 0.1F;
        float zOffset = -player.getBbHeight() * 0.7F; // Essayez différentes valeurs entre 0.5 et 0.9
        matrixStack.translate(0.0D, yOffset, zOffset);
    }

    public void angles(LivingEntity player, BipedModel<?> model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        model.head.visible = false;
        model.rightArm.visible = false;
        model.leftArm.visible = false;
        model.rightLeg.visible = false;
        model.leftLeg.visible = false;
    }

    public void heldItem(LivingEntity entity, ItemStack stack, ItemCameraTransforms.TransformType transformType, HandSide handSide, MatrixStack matrixStack, IRenderTypeBuffer renderBuffer, int packedLight)
    {
        matrixStack.clear();
    }
}
