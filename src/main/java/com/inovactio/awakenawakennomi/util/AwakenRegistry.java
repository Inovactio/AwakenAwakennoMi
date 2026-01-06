package com.inovactio.awakenawakennomi.util;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.pixelatedw.mineminenomi.api.ModRegistries;
import xyz.pixelatedw.mineminenomi.api.charactercreator.FactionId;
import xyz.pixelatedw.mineminenomi.api.charactercreator.RaceId;
import xyz.pixelatedw.mineminenomi.api.charactercreator.StyleId;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

import java.util.HashMap;
import java.util.function.Supplier;

public class AwakenRegistry {
    public static final DeferredRegister<Attribute> ATTRIBUTES;
    private static final HashMap<String, String> langMap = new HashMap<>();

    public static HashMap<String, String> getLangMap() {
        return langMap;
    }

    public static String registerName(String key, String localizedName) {
        getLangMap().put(key, localizedName);
        return key;
    }

    public static RegistryObject<Attribute> registerAttribute(String localizedName, Supplier<Attribute> attr) {
        String resourceName = WyHelper.getResourceName(localizedName);
        getLangMap().put("attribute.name.generic.awakenawakennomi." + resourceName, localizedName);
        RegistryObject<Attribute> reg = ATTRIBUTES.register("generic." + resourceName, attr);
        return reg;
    }

    static {
        ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, "awakenwakennomi");
    }

}
