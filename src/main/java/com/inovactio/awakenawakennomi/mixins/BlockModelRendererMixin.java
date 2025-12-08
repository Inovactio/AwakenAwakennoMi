package com.inovactio.awakenawakennomi.mixins;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import com.inovactio.awakenawakennomi.renderers.RenderPosHolder;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockModelRenderer.class)
public class BlockModelRendererMixin {

    @Inject(method = "renderModel(Lnet/minecraft/world/IBlockDisplayReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRenderModelHead(IBlockDisplayReader worldIn, IBakedModel modelIn, BlockState stateIn,
                               BlockPos posIn, MatrixStack matrixIn, IVertexBuilder buffer,
                               boolean checkSides, Random randomIn, long rand,
                               int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData,
                               CallbackInfoReturnable<Boolean> cir) {
        if (posIn != null) RenderPosHolder.set(posIn);

        boolean invisible = false;

        if (worldIn instanceof ServerWorld) {
            invisible = InvisibleBlockManager.isInvisible((World) worldIn, posIn);
        } else {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.player != null) {
                invisible = InvisibleBlockManager.isInvisible(posIn, mc.player.getUUID());
            }
        }

        if (invisible) {
            // Annule le rendu du modèle pour ce bloc (les faces adjacentes sont forcées ailleurs)
            cir.setReturnValue(false);
            // nettoyer la position immédiatement pour éviter fuite
            RenderPosHolder.clear();
        }
    }

    @Inject(method = "renderModel(Lnet/minecraft/world/IBlockDisplayReader;Lnet/minecraft/client/renderer/model/IBakedModel;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;Lcom/mojang/blaze3d/matrix/MatrixStack;Lcom/mojang/blaze3d/vertex/IVertexBuilder;ZLjava/util/Random;JILnet/minecraftforge/client/model/data/IModelData;)Z", at = @At("RETURN"), remap = false)
    private void onRenderModelReturn(IBlockDisplayReader worldIn, IBakedModel modelIn, BlockState stateIn,
                               BlockPos posIn, MatrixStack matrixIn, IVertexBuilder buffer,
                               boolean checkSides, Random randomIn, long rand,
                               int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData,
                               CallbackInfoReturnable<Boolean> cir) {
        RenderPosHolder.clear();
    }
}