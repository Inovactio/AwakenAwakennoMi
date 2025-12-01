package com.inovactio.awakenawakennomi.mixins;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.BlockState;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(BlockModelRenderer.class)
public class BlockModelRendererMixin {

    @Inject(method = "renderModel", at = @At("HEAD"), cancellable = true, remap = false)
    private void onRenderModel(IBlockDisplayReader worldIn, IBakedModel modelIn, BlockState stateIn,
                               BlockPos posIn, MatrixStack matrixIn, IVertexBuilder buffer,
                               boolean checkSides, Random randomIn, long rand,
                               int combinedOverlayIn, net.minecraftforge.client.model.data.IModelData modelData,
                               CallbackInfoReturnable<Boolean> cir) {
        if (posIn != null && InvisibleBlockManager.isInvisible(posIn)) {
            cir.setReturnValue(false);
        }
    }
}



