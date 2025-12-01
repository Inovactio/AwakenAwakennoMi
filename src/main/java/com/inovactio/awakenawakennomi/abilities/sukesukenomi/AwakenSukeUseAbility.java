package com.inovactio.awakenawakennomi.abilities.sukesukenomi;

import com.inovactio.awakenawakennomi.api.abilities.BlockUseAbility;
import com.inovactio.awakenawakennomi.api.abilities.IAwakenable;
import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import com.inovactio.awakenawakennomi.network.ModNetwork;
import com.inovactio.awakenawakennomi.network.ToggleInvisiblePacket;
import net.minecraft.entity.LivingEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCategory;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityCore;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityDescriptionLine;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ContinuousComponent;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceHakiNature;
import xyz.pixelatedw.mineminenomi.api.damagesource.SourceType;
import xyz.pixelatedw.mineminenomi.api.helpers.AbilityHelper;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.init.ModAbilities;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public class AwakenSukeUseAbility extends BlockUseAbility implements IAwakenable{
    private static final ITextComponent[] DESCRIPTION = AbilityHelper.registerDescriptionText("awakenawakennomi", "awaken_suke_punch", new Pair[]{ImmutablePair.of("Turns a bloc insible after hitting it.", (Object)null)});
    public static final AbilityCore<AwakenSukeUseAbility> INSTANCE;

    public AwakenSukeUseAbility(AbilityCore<AwakenSukeUseAbility> core) {
        super(core);
    }

    protected static boolean canUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit() && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.SUKE_SUKE_NO_MI);
    }

    public Predicate<LivingEntity> canActivate() {
        return (entity) -> this.continuousComponent.isContinuous() && entity.getMainHandItem().isEmpty();
    }

    public int getUseLimit() {
        return 0;
    }

    @Override
    public boolean onBlockPunched(LivingEntity entity, BlockPos pos, World world) {
        boolean invisible = !InvisibleBlockManager.isInvisible(world, pos);
        InvisibleBlockManager.setInvisible(world, pos, invisible);
        ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(),
                new ToggleInvisiblePacket(pos, invisible));

        ((ServerWorld) world).sendParticles(
                ParticleTypes.AMBIENT_ENTITY_EFFECT, // particules d’invisibilité vanilla
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, // centre du bloc
                20,        // nombre de particules
                0.3, 0.3, 0.3, // zone de dispersion
                0.0        // vitesse
        );

        // 👉 Son magique
        world.playSound(
                null, // null = tous les joueurs proches entendent
                pos,
                SoundEvents.ENDERMAN_TELEPORT, // choisis ton son ici
                SoundCategory.BLOCKS,
                1.0F, // volume
                1.0F  // pitch
        );
        return true;
    }

    public float getPunchCooldown() {
        return 0.0F;
    }

    @Override
    public boolean AwakenUnlock(LivingEntity user) {
        return DevilFruitCapability.get(user).hasAwakenedFruit() && DevilFruitCapability.get(user).hasDevilFruit(ModAbilities.SUKE_SUKE_NO_MI);
    }

    @Override
    public boolean GetAllowBlockActivation()
    {
        return false;
    }

    static {
        INSTANCE = (new AbilityCore.Builder("AwakenSukePunch", AbilityCategory.DEVIL_FRUITS, AwakenSukeUseAbility::new)).setUnlockCheck(AwakenSukeUseAbility::canUnlock).addDescriptionLine(DESCRIPTION).addAdvancedDescriptionLine(new AbilityDescriptionLine.IDescriptionLine[]{AbilityDescriptionLine.NEW_LINE, ContinuousComponent.getTooltip()}).setSourceHakiNature(SourceHakiNature.HARDENING).setSourceType(new SourceType[]{SourceType.FIST}).build();
        INSTANCE.setIcon(new ResourceLocation("awakenawakennomi", "textures/abilities/awaken_suke_punch.png"));
    }
}
