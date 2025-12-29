package com.inovactio.awakenawakennomi.abilities.ushiushinomi.giraffe;

import xyz.pixelatedw.mineminenomi.abilities.gomu.GearFifthAbility;
import xyz.pixelatedw.mineminenomi.abilities.gomu.GearFourthAbility;
import xyz.pixelatedw.mineminenomi.abilities.gomu.GearThirdAbility;
import xyz.pixelatedw.mineminenomi.abilities.ushigiraffe.GiraffeHeavyPointAbility;
import xyz.pixelatedw.mineminenomi.abilities.ushigiraffe.GiraffeWalkPointAbility;
import xyz.pixelatedw.mineminenomi.api.abilities.Ability;
import xyz.pixelatedw.mineminenomi.data.entity.ability.IAbilityData;

public class UshiGiraffeHelper {


    public static boolean hasWalkPointActive(IAbilityData props) {
        Ability ability = (Ability)props.getEquippedAbility(GiraffeWalkPointAbility.INSTANCE);
        return ability != null && ability.isContinuous();
    }

    public static boolean hasHeavyPointActive(IAbilityData props) {
        Ability ability = (Ability)props.getEquippedAbility(GiraffeHeavyPointAbility.INSTANCE);
        return ability != null && ability.isContinuous();
    }

    public static boolean hasAwakenHeavyPointActive(IAbilityData props) {
        Ability ability = (Ability)props.getEquippedAbility(AwakenGiraffeHeavyPointAbility.INSTANCE);
        return ability != null && ability.isContinuous();
    }

    public static enum Point {
        NO_POINT,
        WALK_POINT,
        HEAVY_POINT,
        AWAKEN_HEAVY_POINT,
    }
}
