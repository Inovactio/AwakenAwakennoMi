package com.inovactio.awakenawakennomi.abilities.mogumogunomi;

import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.init.ModEffects;
import com.inovactio.awakenawakennomi.init.ModMorphs;
import net.MrMagicalCart.cartaddon.abilities.maguextra.NewDaiFunkaAbility;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.*;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import xyz.pixelatedw.mineminenomi.wypi.WyRegistry;

public class MoguDigAbility extends Ability implements IAwakenable {
    private static final TranslationTextComponent MOGU_DIG_NAME = new TranslationTextComponent(WyRegistry.registerName("ability.mineminenomi.mogu_dig.normal_mode", "Normal Mode"));
    private static final TranslationTextComponent MOGU_DIG_BREAK_NAME = new TranslationTextComponent(WyRegistry.registerName("ability.mineminenomi.mogu_dig.break_mode", "Break Mode"));
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "mogu_dig", new Pair[]{ImmutablePair.of("Lets the user swim trough blocks. Swimming is activated by running into a ground block.", (Object)null)});
    private static final AbilityDescriptionLine.IDescriptionLine MOGU_DIG_DESC;
    private static final AbilityDescriptionLine.IDescriptionLine MOGU_DIG_BREAK_DESC;
    private static final int COOLDOWN = 20;
    public static final AbilityCore<MoguDigAbility> INSTANCE;
    private final ContinuousComponent continuousComponent = (new ContinuousComponent(this, true)).addTickEvent(this::tickContinuityEvent).addEndEvent(this::endContinuityEvent);
    protected RequireMorphComponent requireMorphComponent = new RequireMorphComponent(this, (MorphInfo) ModMorphs.AWAKEN_MOGU.get(), xyz.pixelatedw.mineminenomi.init.ModMorphs.MOGU_HEAVY.get());
    protected AltModeComponent altModeComponent;

    public MoguDigAbility(AbilityCore<MoguDigAbility> core) {
        super(core);
        this.isNew = true;
        this.altModeComponent = (new AltModeComponent<>(this, MoguDigAbility.Mode.class, Mode.NORMAL)).addChangeModeEvent(this::onAltModeChange);
        this.addComponents(new AbilityComponent[]{this.continuousComponent, requireMorphComponent, altModeComponent});
        this.addCanUseCheck(AbilityHelper::canUseMomentumAbilities);
        this.addUseEvent(this::useEvent);
    }


    private void useEvent(LivingEntity entity, IAbility ability) {
        this.continuousComponent.triggerContinuity(entity);
    }

    private void tickContinuityEvent(LivingEntity entity, IAbility ability) {
        if (!MoguHelper.isDigging(entity)) {
            if(altModeComponent.getCurrentMode() == Mode.BREAKING) {
                entity.addEffect(new EffectInstance((Effect) ModEffects.GROUND_DIG_BREAK.get(), Integer.MAX_VALUE, 0, false, false, true));
            } else
            {
                entity.addEffect(new EffectInstance((Effect) ModEffects.GROUND_DIG.get(), Integer.MAX_VALUE, 0, false, false, true));
            }
        }
    }

    private void endContinuityEvent(LivingEntity entity, IAbility ability) {
        entity.removeEffect((Effect) ModEffects.GROUND_DIG.get());
        entity.removeEffect((Effect) ModEffects.GROUND_DIG_BREAK.get());
        this.cooldownComponent.startCooldown(entity, COOLDOWN);
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit()
                && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.MOGU_MOGU_NO_MI);
    }


    private void onAltModeChange(LivingEntity livingEntity, IAbility ability, Enum anEnum) {
        Mode mode = (Mode)anEnum;
        if (this.continuousComponent.isContinuous()) {
            if (mode == Mode.NORMAL) {
                this.setDisplayName(MOGU_DIG_NAME);
            } else if (mode == Mode.BREAKING) {
                this.setDisplayName(MOGU_DIG_BREAK_NAME);
            }
        }
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return canUnlock(user);
    }

    static {
        MOGU_DIG_DESC = AbilityDescriptionLine.IDescriptionLine.of(AbilityHelper.mentionText(MOGU_DIG_NAME));
        MOGU_DIG_BREAK_DESC = AbilityDescriptionLine.IDescriptionLine.of(AbilityHelper.mentionText(MOGU_DIG_BREAK_NAME));
        INSTANCE = new AbilityCore.Builder<>("Mogu Dig", AbilityCategory.DEVIL_FRUITS, MoguDigAbility::new)
                .setUnlockCheck(MoguDigAbility::canUnlock)
                .addDescriptionLine(DESCRIPTION)
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, MOGU_DIG_BREAK_DESC, AbilityDescriptionLine.NEW_LINE, ContinuousComponent.getTooltip(), CooldownComponent.getTooltip(COOLDOWN)})
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, MOGU_DIG_DESC, AbilityDescriptionLine.NEW_LINE, ContinuousComponent.getTooltip(), CooldownComponent.getTooltip(COOLDOWN)})
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/mogu/mogu_dig.png"))
                .build();
    }

    public static enum Mode {
        NORMAL,
        BREAKING;
    }
}
