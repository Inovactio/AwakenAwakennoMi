package com.inovactio.awakenawakennomi.api.animations;

import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import xyz.pixelatedw.mineminenomi.api.animations.AnimationId;

public class Animation<E extends LivingEntity, M extends EntityModel> extends xyz.pixelatedw.mineminenomi.api.animations.Animation<E,M>{
    public Animation(AnimationId animId) {
        super(animId);
    }

    public float GetRotValue(float value)
    {
        return(float)Math.toRadians((double)-value);
    }

    public void setPosition(ModelRenderer model,
                            float x, float y, float z) {
        model.x -= x;
        model.y -= y;
        model.z += z;
    }

}
