package com.inovactio.awakenawakennomi.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.nio.file.Paths;

@Mod.EventBusSubscriber(
        modid = "awakenawakennomi",
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class CommonConfig {
    public static final Path CONFIG_PATH = Paths.get("config", "awakenawakennomi-common.toml");
    public static final ForgeConfigSpec SPEC;
    public static final CommonConfig INSTANCE;
    public CommonConfig(ForgeConfigSpec.Builder builder)
    {
        builder.push("Unblock");
        UnlockConfig.UNLOCK_WITH_DORIKI.createValue(builder);
        UnlockConfig.UNLOCK_DORIKI_THRESHOLD.createValue(builder);
    }

    static {
        Pair<CommonConfig, ForgeConfigSpec> pair = (new ForgeConfigSpec.Builder()).configure(CommonConfig::new);
        SPEC = (ForgeConfigSpec)pair.getRight();
        INSTANCE = (CommonConfig)pair.getLeft();
    }
}
