package com.inovactio.awakenawakennomi.renderers.layers;

import com.inovactio.awakenawakennomi.api.abilities.AwakenZoanAbility;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import xyz.pixelatedw.mineminenomi.abilities.gomu.GomuHelper;
import xyz.pixelatedw.mineminenomi.api.abilities.IAbility;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;
import xyz.pixelatedw.mineminenomi.models.abilities.GomuSmokeModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AwakenZoanSmokeLayer <T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
    private static final ResourceLocation SMOKE_0 = new ResourceLocation("awakenawakennomi", "textures/models/zoanmorph/awaken/smoke_0.png");
    private static final ResourceLocation SMOKE_1 = new ResourceLocation("awakenawakennomi", "textures/models/zoanmorph/awaken/smoke_1.png");
    private static final ResourceLocation SMOKE_2 = new ResourceLocation("awakenawakennomi", "textures/models/zoanmorph/awaken/smoke_2.png");
    private static final ResourceLocation SMOKE_3 = new ResourceLocation("awakenawakennomi", "textures/models/zoanmorph/awaken/smoke_3.png");
    private static final ResourceLocation[] SMOKE_ANIM;
    private static final float SCALE = 1.3F;
    private GomuSmokeModel model = new GomuSmokeModel();

    public AwakenZoanSmokeLayer(IEntityRenderer renderer) {
        super(renderer);
    }

    public void render(MatrixStack matrixStack, IRenderTypeBuffer buffer, int packedLight, T entity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {

        IAbilityData abilityData = AbilityDataCapability.get(entity);
        if (hasAwakenedZoanAbility(abilityData)) {
            float speed = 1000.0F;
            float anim = (float) Util.getMillis() % speed / (speed / (float)SMOKE_ANIM.length);
            matrixStack.pushPose();
            matrixStack.scale(SCALE, SCALE, SCALE);
            IVertexBuilder ivb = buffer.getBuffer(RenderType.entityTranslucent(SMOKE_ANIM[(int)Math.floor((double)anim)]));
            this.model.setupAnim(entity, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            this.model.renderToBuffer(matrixStack, ivb, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            matrixStack.popPose();
        }
    }

    private boolean hasAwakenedZoanAbility(IAbilityData abilityData) {
        if (abilityData == null) return false;
        Set<IAbility> abilities =
                abilityData.getEquippedAbilities(a -> {
                    return a instanceof AwakenZoanAbility && ((AwakenZoanAbility) a).isContinuous();
                });
        return !abilities.isEmpty();
    }

    static {
        SMOKE_ANIM = new ResourceLocation[]{SMOKE_0, SMOKE_1, SMOKE_2, SMOKE_3};
    }
}