package com.inovactio.awakenawakennomi.util;

import com.inovactio.awakenawakennomi.init.ModI18n;
import net.minecraft.util.text.StringTextComponent;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityDescriptionLine;
import xyz.pixelatedw.mineminenomi.api.abilities.AbilityStat;
import xyz.pixelatedw.mineminenomi.api.abilities.components.ProjectileComponent;
import xyz.pixelatedw.mineminenomi.entities.projectiles.AbilityProjectileEntity;
import xyz.pixelatedw.mineminenomi.init.ModAbilityKeys;

public class ToolTipHelper {
    public static AbilityDescriptionLine.IDescriptionLine[] getExplosionTooltips(float power, float size, float damage) {
        AbilityDescriptionLine.IDescriptionLine[] list = new AbilityDescriptionLine.IDescriptionLine[4];
        list[0] = (entity, ability) -> new StringTextComponent("§a" + ModI18n.ABILITY_DESCRIPTION_STAT_NAME_EXPLOSION.getString() + "§r");
        list[1] = getPowerFromExplosionTooltip(power);
        list[2] = getSizeFromExplosionTooltip(size);
        list[3] = getDamageFromExplosionTooltip(damage);
        return list;
    }

    private static AbilityDescriptionLine.IDescriptionLine getPowerFromExplosionTooltip(float power) {
        return (entity, ability) -> {
            if (power > 0) {
                AbilityStat.Builder statBuilder = (new AbilityStat.Builder(ModI18n.ABILITY_DESCRIPTION_STAT_NAME_POWER, power));
                return statBuilder.build().getStatDescription(2);
            } else {
                return null;
            }
        };
    }

    private static AbilityDescriptionLine.IDescriptionLine getSizeFromExplosionTooltip(float size) {
        return (entity, ability) -> {
            if (size > 0) {
                AbilityStat.Builder statBuilder = (new AbilityStat.Builder(ModI18n.ABILITY_DESCRIPTION_STAT_NAME_SIZE, size));
                return statBuilder.build().getStatDescription(2);
            } else {
                return null;
            }
        };
    }

    private static AbilityDescriptionLine.IDescriptionLine getDamageFromExplosionTooltip(float damage) {
        return (entity, ability) -> {
            if (damage > 0.0F) {
                AbilityStat.Builder statBuilder = (new AbilityStat.Builder(xyz.pixelatedw.mineminenomi.init.ModI18n.ABILITY_DESCRIPTION_STAT_NAME_DAMAGE, damage));
                return statBuilder.build().getStatDescription(2);
            } else {
                return null;
            }
        };
    }
}
