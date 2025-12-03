package com.inovactio.awakenawakennomi.renderers;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import com.inovactio.awakenawakennomi.models.MyModelProperties;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class InvisibleModelWrapper implements IBakedModel {
    private final IBakedModel original;

    public InvisibleModelWrapper(IBakedModel original) {
        this.original = original;
    }

    // Version dépréciée → inventaire ou rendu sans IModelData
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state,
                                    @Nullable Direction side,
                                    Random rand) {
        // Toujours déléguer → évite les items plats
        return original.getQuads(state, side, rand);
    }

    // Version Forge avec IModelData → logique d’invisibilité
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state,
                                    @Nullable Direction side,
                                    Random rand,
                                    IModelData extraData) {
        if (extraData != null && extraData.hasProperty(MyModelProperties.POS)) {
            BlockPos pos = extraData.getData(MyModelProperties.POS);
            if (pos != null) {
                Minecraft mc = Minecraft.getInstance();
                if (mc != null) {
                    World world = mc.level;
                    if (world != null) {
                        if (InvisibleBlockManager.isInvisible(world, pos)) {
                            return Collections.emptyList(); // bloc invisible en monde
                        }
                    } else if (mc.player != null) {
                        if (InvisibleBlockManager.isInvisible(pos, mc.player.getUUID())) {
                            return Collections.emptyList(); // invisibilité basée sur l'UUID client
                        }
                    }
                }
            }
        }
        return original.getQuads(state, side, rand, extraData);
    }

    // Délégation des autres méthodes
    @Override public boolean useAmbientOcclusion() { return original.useAmbientOcclusion(); }
    @Override public boolean isGui3d() { return original.isGui3d(); }
    @Override public boolean usesBlockLight() { return original.usesBlockLight(); }
    @Override public boolean isCustomRenderer() { return original.isCustomRenderer(); }
    @Override public TextureAtlasSprite getParticleIcon() { return original.getParticleIcon(); }
    @Override public ItemOverrideList getOverrides() { return original.getOverrides(); }
}
