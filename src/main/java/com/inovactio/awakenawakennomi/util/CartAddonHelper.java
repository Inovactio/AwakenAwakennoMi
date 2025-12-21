package com.inovactio.awakenawakennomi.util;

import net.MrMagicalCart.cartaddon.renderers.layers.*;
import top.theillusivec4.curios.client.render.CuriosLayer;
import xyz.pixelatedw.mineminenomi.renderers.morphs.ZoanMorphRenderer;

public class CartAddonHelper {

    public static void AddCartAllLayer(ZoanMorphRenderer renderer)
    {
        AddCartOniLayer(renderer);
        AddCartLunarianLayer(renderer);
        AddCartSkyFolkLayer(renderer);
        AddCartFishmanLayer(renderer);
        AddCartCyborgLayer(renderer);
        AddCartSantoryuLayer(renderer);
        AddCartTontattaLayer(renderer);
        AddCartCuriosLayer(renderer);
    }

    public static void AddCartOniLayer(ZoanMorphRenderer renderer)
    {
        renderer.addLayer(new OniFeaturesLayer(renderer));
    }

    public static void AddCartLunarianLayer(ZoanMorphRenderer renderer)
    {
        renderer.addLayer(new LunarianFeaturesLayer(renderer));
    }

    public static void AddCartSkyFolkLayer(ZoanMorphRenderer renderer)
    {
        renderer.addLayer(new SkyFolkFeaturesLayer(renderer));
    }

    public static void AddCartFishmanLayer(ZoanMorphRenderer renderer)
    {
        renderer.addLayer(new FishmanFeaturesLayer(renderer));
    }

    public static void AddCartCyborgLayer(ZoanMorphRenderer renderer)
    {
        renderer.addLayer(new CyborgFeaturesLayer(renderer));
    }

    public static void AddCartSantoryuLayer(ZoanMorphRenderer renderer)
    {
        renderer.addLayer(new SantoryuFeatureLayer(renderer));
    }

    public static void AddCartTontattaLayer(ZoanMorphRenderer renderer)
    {
        renderer.addLayer(new TontattaFeaturesLayer(renderer));
    }

    public static void AddCartCuriosLayer(ZoanMorphRenderer renderer)
    {
        renderer.addLayer(new CuriosLayer(renderer));
    }
}
