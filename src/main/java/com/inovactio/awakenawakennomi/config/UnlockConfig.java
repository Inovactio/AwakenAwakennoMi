package com.inovactio.awakenawakennomi.config;

import xyz.pixelatedw.mineminenomi.config.options.BooleanOption;
import xyz.pixelatedw.mineminenomi.config.options.IntegerOption;

public class UnlockConfig {
    public static final BooleanOption UNLOCK_WITH_DORIKI = new BooleanOption(true, "Unlock with Doriki", "Allows players to unlock awakened abilities based on their Doriki level.\nDefault: true");
    public static final IntegerOption UNLOCK_DORIKI_THRESHOLD = new IntegerOption(-1,-1,100000, "Doriki Threshold", "The Doriki level required to unlock awakened abilities.\n-1 for Max Doriki limit.\nDefault: -1");
}
