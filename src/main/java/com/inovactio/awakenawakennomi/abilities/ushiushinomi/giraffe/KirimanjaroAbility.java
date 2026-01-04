package com.inovactio.awakenawakennomi.abilities.ushiushinomi.giraffe;

import com.inovactio.awakenawakennomi.entities.projectiles.ushi.giraffe.AwakenBiganProjectile;
import com.inovactio.awakenawakennomi.entities.projectiles.ushi.giraffe.BiganProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.*;
import xyz.pixelatedw.mineminenomi.api.abilities.components.*;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceType;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.api.morph.MorphInfo;
import xyz.pixelatedw.mineminenomi.data.entity.ability.AbilityDataCapability;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;
import xyz.pixelatedw.mineminenomi.init.ModMorphs;
import xyz.pixelatedw.mineminenomi.wypi.WyRegistry;

public class KirimanjaroAbility extends Ability {
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "reworked_bigan", new Pair[]{ImmutablePair.of("Hits using the hardened giraffe nose.", (Object)null)});

    protected float cooldown = BASED_COOLDOWN;
    protected float damage = BASED_DAMAGE;
    private static final int BASED_COOLDOWN = 150;
    private static final int AWAKEN_COOLDOWN = 300;
    private static final int BASED_DAMAGE = 30;
    private static final int AWAKEN_DAMAGE = 100;
    public static final AbilityCore<KirimanjaroAbility> INSTANCE;
    protected AltModeComponent<UshiGiraffeHelper.Point> altModeComponent;
    protected ProjectileComponent projectileComponent = new ProjectileComponent(this, this::createProjectile);

    public KirimanjaroAbility(AbilityCore<KirimanjaroAbility> core) {
        super(core);
        this.isNew = true;
        altModeComponent = new AltModeComponent<>(this, UshiGiraffeHelper.Point.class, UshiGiraffeHelper.Point.NO_POINT, true).addChangeModeEvent(this::altModeChangeEvent);
        RequireMorphComponent requireMorphComponent = new RequireMorphComponent(this, (MorphInfo) ModMorphs.GIRAFFE_HEAVY.get(), new MorphInfo[]{(MorphInfo)ModMorphs.GIRAFFE_WALK.get(), (MorphInfo)com.inovactio.awakenawakennomi.init.ModMorphs.AWAKEN_GIRAFFE.get()});
        this.addComponents(new AbilityComponent[]{requireMorphComponent, altModeComponent, projectileComponent});
        this.addUseEvent(this::useEvent);
    }

    protected void useEvent(LivingEntity entity, IAbility ability) {
        this.projectileComponent.shoot(entity, 3, 0);
        this.cooldownComponent.startCooldown(entity, cooldown);
    }

    private void altModeChangeEvent(LivingEntity entity, IAbility ability, UshiGiraffeHelper.Point point) {
        switch (point) {
            case AWAKEN_HEAVY_POINT:
                this.damage = AWAKEN_DAMAGE;
                this.cooldown = AWAKEN_COOLDOWN;
                break;
            case WALK_POINT:
            case HEAVY_POINT:
            case NO_POINT:
            default:
                this.damage = BASED_DAMAGE;
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

    private BiganProjectile createProjectile(LivingEntity entity) {
        IAbilityData props = AbilityDataCapability.get(entity);
        if (UshiGiraffeHelper.hasAwakenHeavyPointActive(props)) {
            return new AwakenBiganProjectile(entity.level, entity);
        } else {
            return new BiganProjectile(entity.level, entity);
        }
    }

    static {
        INSTANCE = new AbilityCore.Builder<>("Kirimanjaro", AbilityCategory.DEVIL_FRUITS, KirimanjaroAbility::new)
                .addDescriptionLine(DESCRIPTION)
                .addDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, RequireMorphComponent.getTooltip()})
                .setSourceType(new SourceType[]{SourceType.FIST})
                .setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/ushi/giraffe/kirimanjaro.png"))
                .build();
    }
}
