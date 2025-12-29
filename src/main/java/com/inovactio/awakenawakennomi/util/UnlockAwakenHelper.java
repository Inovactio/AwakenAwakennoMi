package com.inovactio.awakenawakennomi.util;

import com.inovactio.awakenawakennomi.config.UnlockConfig;
import net.minecraft.entity.player.PlayerEntity;
import xyz.pixelatedw.mineminenomi.config.GeneralConfig;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.DevilFruitCapability;
import xyz.pixelatedw.mineminenomi.data.entity.devilfruit.IDevilFruit;
import xyz.pixelatedw.mineminenomi.data.entity.entitystats.EntityStatsCapability;
import xyz.pixelatedw.mineminenomi.data.entity.entitystats.IEntityStats;
import xyz.pixelatedw.mineminenomi.events.abilities.AbilityValidationEvents;
import xyz.pixelatedw.mineminenomi.packets.server.SSyncDevilFruitPacket;
import xyz.pixelatedw.mineminenomi.wypi.WyNetwork;

public class UnlockAwakenHelper {
    public static void checkForAwakenUnlocks(PlayerEntity player) {
        if (checkIfAwakenedUnlocked(player)){
            unlockAwakenedFruit(player);
        }
    }

    private static boolean checkIfAwakenedUnlocked(PlayerEntity player) {
        IDevilFruit props = DevilFruitCapability.get(player);
        if(props.hasAwakenedFruit()) return false;
        if(!UnlockConfig.UNLOCK_WITH_DORIKI.get()) return false;
        IEntityStats stats = EntityStatsCapability.get(player);
        if(UnlockConfig.UNLOCK_DORIKI_THRESHOLD.get() < 0) {
            return stats.getDoriki() >= GeneralConfig.DORIKI_LIMIT.get();
        }
        return stats.getDoriki() >= UnlockConfig.UNLOCK_DORIKI_THRESHOLD.get();
    }

    public static void unlockAwakenedFruit(PlayerEntity player) {
        IDevilFruit props = DevilFruitCapability.get(player);
        props.setAwakenedFruit(true);
        AbilityValidationEvents.checkForPossibleFruitAbilities(player);
        WyNetwork.sendTo(new SSyncDevilFruitPacket(player.getId(), props), player);
    }
}
