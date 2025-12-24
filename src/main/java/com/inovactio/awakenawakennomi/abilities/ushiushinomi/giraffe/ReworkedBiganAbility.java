package com.inovactio.awakenawakennomi.abilities.ushiushinomi.giraffe;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.abilities.gomu.GomuHelper;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.*;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceHakiNature;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceType;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.init.ModDamageSource;
import xyz.pixelatedw.mineminenomi.init.ModMorphs;
import xyz.pixelatedw.mineminenomi.wypi.WyRegistry;

import java.util.function.Predicate;

public class ReworkedBiganAbility extends PunchAbility2 {
    private static final TranslationTextComponent BIGAN_NAME = new TranslationTextComponent(WyRegistry.registerName("ability.awakenawakenomi.bigan", "Bigan"));
    private static final TranslationTextComponent AWAKEN_BIGAN_NAME = new TranslationTextComponent(WyRegistry.registerName("ability.awakenawakennomi.awaken_bigan", "Awaken Bigan"));
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "reworked_bigan", new Pair[]{ImmutablePair.of("Hits using the hardened giraffe nose.", (Object)null)});
    private static final AbilityDescriptionLine.IDescriptionLine BIGAN_DESC;
    private static final AbilityDescriptionLine.IDescriptionLine AWAKEN_BIGAN_DESC;

    protected float cooldown = BASED_COOLDOWN;
    protected float damage = BASED_DAMAGE;
    private static final int BASED_COOLDOWN = 100;
    private static final int AWAKEN_COOLDOWN = 200;
    private static final int BASED_DAMAGE = 25;
    private static final int AWAKEN_DAMAGE = 80;
    public static final AbilityCore<ReworkedBiganAbility> INSTANCE;
    protected AltModeComponent<UshiGiraffeHelper.Point> altModeComponent;

    public ReworkedBiganAbility(AbilityCore<ReworkedBiganAbility> core) {
        super(core);
        this.isNew = true;
        altModeComponent = new AltModeComponent<>(this, UshiGiraffeHelper.Point.class, UshiGiraffeHelper.Point.NO_POINT, true).addChangeModeEvent(this::altModeChangeEvent);
        RequireMorphComponent requireMorphComponent = new RequireMorphComponent(this, (MorphInfo) ModMorphs.GIRAFFE_HEAVY.get(), new MorphInfo[]{(MorphInfo)ModMorphs.GIRAFFE_WALK.get(), (MorphInfo)com.inovactio.awakenawakennomi.init.ModMorphs.AWAKEN_USHI.get()});
        this.addComponents(new AbilityComponent[]{requireMorphComponent, altModeComponent});
    }

    private void altModeChangeEvent(LivingEntity entity, IAbility ability, UshiGiraffeHelper.Point point) {
        switch (point) {
            case AWAKEN_HEAVY_POINT:
                this.setDisplayName(AWAKEN_BIGAN_NAME);
                this.damage = AWAKEN_DAMAGE;
                this.cooldown = AWAKEN_COOLDOWN;
                break;
            case WALK_POINT:
            case HEAVY_POINT:
            case NO_POINT:
            default:
                this.cooldown = BASED_COOLDOWN;
                this.setDisplayName(BIGAN_NAME);
                this.cooldown = BASED_COOLDOWN;
        }

    }

    public void switchNoPoint(LivingEntity entity) {
        this.altModeComponent.setMode(entity, UshiGiraffeHelper.Point.NO_POINT);
    }

    public void switchWalkPoint(LivingEntity entity) {
        this.altModeComponent.setMode(entity, UshiGiraffeHelper.Point.WALK_POINT);
    }

    public void switchHeavyPoint(LivingEntity entity) {
        this.altModeComponent.setMode(entity, UshiGiraffeHelper.Point.HEAVY_POINT);
    }

    public void switchAwakenHeavyPoint(LivingEntity entity) {
        this.altModeComponent.setMode(entity, UshiGiraffeHelper.Point.AWAKEN_HEAVY_POINT);
    }

    public float getPunchDamage() {
        return damage;
    }

    public float getPunchCooldown() {
        return cooldown;
    }

    public boolean onHitEffect(LivingEntity entity, LivingEntity target, ModDamageSource source) {
        return true;
    }

    public Predicate<LivingEntity> canActivate() {
        return (entity) -> this.continuousComponent.isContinuous();
    }

    public int getUseLimit() {
        return 1;
    }

    static {
        BIGAN_DESC = AbilityDescriptionLine.IDescriptionLine.of(AbilityHelper.mentionText(BIGAN_NAME));
        AWAKEN_BIGAN_DESC = AbilityDescriptionLine.IDescriptionLine.of(AbilityHelper.mentionText(AWAKEN_BIGAN_NAME));
        INSTANCE = new AbilityCore.Builder<>("Reworked Bigan", AbilityCategory.DEVIL_FRUITS, ReworkedBiganAbility::new)
                .addDescriptionLine(DESCRIPTION)
                .addDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, RequireMorphComponent.getTooltip()})
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, BIGAN_DESC, AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(BASED_COOLDOWN), ContinuousComponent.getTooltip()})
                .addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, AWAKEN_BIGAN_DESC, AbilityDescriptionLine.NEW_LINE, CooldownComponent.getTooltip(AWAKEN_COOLDOWN), ContinuousComponent.getTooltip()})
                .setSourceType(new SourceType[]{SourceType.FIST})
                .build();
    }
}
