package com.inovactio.awakenawakennomi.abilities.sukesukenomi;

import com.inovactio.awakenawakennomi.api.abilities.BlockUseAbility;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.tuple.ImmutablePair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.components.SkinOverlayComponent;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public class InvisibleTouchAbility extends BlockUseAbility implements IAwakenable {

    private static final ITextComponent[] DESCRIPTION =
            AbilityHelper.registerDescriptionText("awakenawakennomi", "invisible_touch",
                    ImmutablePair.of("Turns a block invisible after hitting it.", null));

    public static final AbilityCore<InvisibleTouchAbility> INSTANCE;

    private static final AbilityOverlay OVERLAY =
            new AbilityOverlay.Builder()
                    .setOverlayPart(AbilityOverlay.OverlayPart.LIMB)
                    .setColor(SukeHelper.SUKE_COLOR)
                    .build();

    private final SkinOverlayComponent skinOverlayComponent;

    public InvisibleTouchAbility(AbilityCore<InvisibleTouchAbility> core) {
        super(core);
        this.skinOverlayComponent = new SkinOverlayComponent(this, OVERLAY);
        super.continuousComponent
                .addStartEvent(100, this::applyOverlay)
                .addEndEvent(100, this::removeOverlay);
        super.addComponents(this.skinOverlayComponent);
    }

    private void applyOverlay(LivingEntity entity, IAbility ability) {
        this.skinOverlayComponent.show(entity, OVERLAY);
    }

    private void removeOverlay(LivingEntity entity, IAbility ability) {
        this.skinOverlayComponent.hideAll(entity);
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.SUKE_SUKE_NO_MI);
    }

    @Override
    public Predicate<LivingEntity> canActivate() {
        return entity -> this.continuousComponent.isContinuous() && entity.getMainHandItem().isEmpty();
    }

    @Override
    public int getUseLimit() {
        return 0;
    }

    @Override
    public boolean onBlockUsed(LivingEntity entity, BlockPos pos, World world) {
        SukeHelper.toggleBlockInvisibility(pos, world, entity.getUUID());
        spawnParticlesAndSound(world, pos);
        return true;
    }

    private void spawnParticlesAndSound(World world, BlockPos pos) {
        if (world instanceof ServerWorld) {
            ((ServerWorld) world).sendParticles(
                    ParticleTypes.AMBIENT_ENTITY_EFFECT,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    20,
                    0.3, 0.3, 0.3,
                    0.0
            );
        }

        world.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public float getPunchCooldown() {
        return 0.0F;
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.SUKE_SUKE_NO_MI);
    }

    @Override
    public boolean GetAllowBlockActivation() {
        return false;
    }

    static {
        INSTANCE = new AbilityCore.Builder<InvisibleTouchAbility>("Invisible Touch", AbilityCategory.DEVIL_FRUITS, InvisibleTouchAbility::new)
                .setUnlockCheck(InvisibleTouchAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{
                        AbilityDescriptionLine.NEW_LINE,
                        ContinuousComponent.getTooltip()
                })
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/suke/invisible_touch.png"))
                .build();
    }
}
