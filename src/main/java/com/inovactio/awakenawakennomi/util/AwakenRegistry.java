package com.inovactio.awakenawakennomi.util;

import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.potion.Effect;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import xyz.pixelatedw.mineminenomi.wypi.WyHelper;

import java.util.HashMap;
import java.util.function.Supplier;

public class AwakenRegistry {
    public static final DeferredRegister<Attribute> ATTRIBUTES;
    public static final DeferredRegister<Effect> EFFECTS;
    private static final HashMap<String, String> langMap = new HashMap<>();

    public static HashMap<String, String> getLangMap() {
        return langMap;
    }

    public static String registerName(String key, String localizedName) {
        getLangMap().put(key, localizedName);
        return key;
    }

    public static <T extends Effect> RegistryObject<T> registerEffect(String localizedName, Supplier<T> effect) {
        String resourceName = WyHelper.getResourceName(localizedName);
        return registerEffect(localizedName, resourceName, effect);
    }

    public static <T extends Effect> RegistryObject<T> registerEffect(String localizedName, String resourceKey, Supplier<T> effect) {
        String resourceName = WyHelper.getResourceName(resourceKey);
        getLangMap().put("effect.awakenawakennomi." + resourceName, localizedName);
        RegistryObject<T> reg = EFFECTS.register(resourceName, effect);
        return reg;
    }

    public static RegistryObject<Attribute> registerAttribute(String localizedName, Supplier<Attribute> attr) {
        String resourceName = WyHelper.getResourceName(localizedName);
        getLangMap().put("attribute.name.generic.awakenawakennomi." + resourceName, localizedName);
        RegistryObject<Attribute> reg = ATTRIBUTES.register("generic." + resourceName, attr);
        return reg;
    }

    static {
        ATTRIBUTES = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, "awakenawakennomi");
        EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, "awakenawakennomi");
    }

}
