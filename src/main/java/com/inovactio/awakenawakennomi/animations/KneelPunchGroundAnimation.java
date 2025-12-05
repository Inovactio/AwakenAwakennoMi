package com.inovactio.awakenawakennomi.animations;

import com.inovactio.awakenawakennomi.api.animations.Animation;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.HandSide;
import xyz.pixelatedw.mineminenomi.api.animations.AnimationId;

public class KneelPunchGroundAnimation extends Animation<LivingEntity, BipedModel<?>> {

    public KneelPunchGroundAnimation(AnimationId<KneelPunchGroundAnimation> animId) {
        super(animId);
        this.setAnimationAngles(this::angles);
    }

    public void angles(LivingEntity player, BipedModel model, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        model.rightArm.xRot = GetRotValue(-8.0F);
        model.rightArm.yRot = GetRotValue(1.0F);
        model.leftArm.xRot = GetRotValue(10.0F);
        model.body.xRot = GetRotValue(-20.0F);
        model.head.xRot = GetRotValue(-20.0F);
        model.leftLeg.xRot = GetRotValue(-90.0F);
        setPosition(model.rightArm, 0.0F, -9.0F, 0);
        setPosition(model.leftArm, 0.0F, -12.0F, 0);
        setPosition(model.body, 0.0F, -8.0F, 0);
        setPosition(model.head, 0.0F, -8.0F, 0);
        setPosition(model.rightLeg, 0.0F, 0.0F, -2);
        setPosition(model.leftLeg, 0.0F, -10.0F, 2);
    }


}
