package com.inovactio.awakenawakennomi.models.entity;

import com.google.common.collect.ImmutableList;
import com.inovactio.awakenawakennomi.entities.mobs.ability.kuku.CakeGolemEntity;
import net.minecraft.client.renderer.entity.model.SegmentedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.util.math.MathHelper;

public class CakeGolemModel<T extends CakeGolemEntity> extends SegmentedModel<T> {
    private final ModelRenderer head;
    private final ModelRenderer body;
    private final ModelRenderer leftArm;
    private final ModelRenderer rightArm;
    private final ModelRenderer leftLeg;
    private final ModelRenderer rightLeg;

    public CakeGolemModel() {
        this.head = (new ModelRenderer(this)).setTexSize(128, 128);
        this.head.setPos(0.0F, -7.0F, -2.0F);
        this.head.texOffs(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, 0.0F);
        this.head.texOffs(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, 0.0F);

        this.body = (new ModelRenderer(this)).setTexSize(128, 128);
        this.body.setPos(0.0F, -7.0F, 0.0F);
        this.body.texOffs(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, 0.0F);
        this.body.texOffs(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, 0.5F);

        this.leftArm = (new ModelRenderer(this)).setTexSize(128, 128);
        this.leftArm.setPos(0.0F, -7.0F, 0.0F);
        this.leftArm.texOffs(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, 0.0F);

        this.rightArm = (new ModelRenderer(this)).setTexSize(128, 128);
        this.rightArm.setPos(0.0F, -7.0F, 0.0F);
        this.rightArm.texOffs(60, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, 0.0F);

        this.leftLeg = (new ModelRenderer(this, 0, 22)).setTexSize(128, 128);
        this.leftLeg.setPos(-4.0F, 11.0F, 0.0F);
        this.leftLeg.texOffs(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, 0.0F);

        this.rightLeg = (new ModelRenderer(this, 0, 22)).setTexSize(128, 128);
        this.rightLeg.mirror = true;
        this.rightLeg.texOffs(60, 0).setPos(5.0F, 11.0F, 0.0F);
        this.rightLeg.addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, 0.0F);
    }

    @Override
    public Iterable<ModelRenderer> parts() {
        return ImmutableList.of(this.head, this.body, this.leftLeg, this.rightLeg, this.leftArm, this.rightArm);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.head.yRot = netHeadYaw * ((float) Math.PI / 180F);
        this.head.xRot = headPitch * ((float) Math.PI / 180F);

        this.leftLeg.xRot = -1.5F * MathHelper.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
        this.rightLeg.xRot = 1.5F * MathHelper.triangleWave(limbSwing, 13.0F) * limbSwingAmount;

        this.leftLeg.yRot = 0.0F;
        this.rightLeg.yRot = 0.0F;
    }

    @Override
    public void prepareMobModel(T entity, float limbSwing, float limbSwingAmount, float partialTick) {
        int i = entity.getAttackAnimationTick();
        if (i > 0) {
            this.leftArm.xRot = -2.0F + 1.5F * MathHelper.triangleWave((float) i - partialTick, 10.0F);
            this.rightArm.xRot = -2.0F + 1.5F * MathHelper.triangleWave((float) i - partialTick, 10.0F);
        }
    }

}
