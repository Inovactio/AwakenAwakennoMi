package com.inovactio.awakenawakennomi.entities.morph;

import com.google.common.collect.ImmutableMap;
import com.inovactio.awakenawakennomi.abilities.kamekamenomi.AwakenKameWalkPointAbility;
import com.inovactio.awakenawakennomi.renderers.morphs.AwakenHumanRenderer;
import com.inovactio.awakenawakennomi.renderers.morphs.AwakenKameWalkPartialMorphRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.api.morph.MorphModel;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.items.AkumaNoMiItem;
import xyz.pixelatedw.mineminenomi.models.morphs.KameWalkPartialModel;
import xyz.pixelatedw.mineminenomi.renderers.morphs.KameWalkPartialMorphRenderer;

import javax.annotation.Nullable;
import java.util.Map;

public class AwakenKameWalkMorphInfo extends MorphInfo {

    private static final EntitySize STANDING_SIZE = EntitySize.scalable(0.75F, 2.25F);
    private static final EntitySize CROUCHING_SIZE = EntitySize.scalable(0.75F, 1.875F);


    @OnlyIn(Dist.CLIENT)
    public IRenderFactory getRendererFactory(LivingEntity entity) {
        boolean isSlim = false;
        if (entity instanceof AbstractClientPlayerEntity) {
            isSlim = ((AbstractClientPlayerEntity)entity).getModelName().equals("slim");
        }

        return new AwakenKameWalkPartialMorphRenderer.Factory(this, isSlim);
    }

    @OnlyIn(Dist.CLIENT)
    public MorphModel getModel() {
        return new KameWalkPartialModel();
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderFirstPersonHand() {
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderFirstPersonLeg() {
        return false;
    }

    public boolean isPartial() {
        return true;
    }

    public String getForm() {
        return "awaken_kame_walk";
    }

    public double getEyeHeight() {
        return 2.025F;
    }

    public float getShadowSize() {
        return 0.625F;
    }

    @OnlyIn(Dist.CLIENT)
    public double getCameraZoom(LivingEntity entity) {
        return (double)5F;
    }

    @Nullable
    public AkumaNoMiItem getDevilFruit() {
        return ModAbilities.KAME_KAME_NO_MI;
    }

    public String getDisplayName() {
        return AwakenKameWalkPointAbility.INSTANCE.getUnlocalizedName();
    }

    @OnlyIn(Dist.CLIENT)
    public double getCameraHeight(LivingEntity entity) {
        boolean isFirstPerson = Minecraft.getInstance().options.getCameraType() == PointOfView.FIRST_PERSON;
        boolean shouldSit = entity.isPassenger() && entity.getVehicle() != null && entity.getVehicle().shouldRiderSit();
        return isFirstPerson && shouldSit ? (double)0.5F : (double)0.0F;
    }

    public Map<Pose, EntitySize> getSizes() {
        return ImmutableMap.<Pose, EntitySize>builder()
                .put(Pose.STANDING, STANDING_SIZE)
                .put(Pose.CROUCHING, CROUCHING_SIZE)
                .build();
    }
}
