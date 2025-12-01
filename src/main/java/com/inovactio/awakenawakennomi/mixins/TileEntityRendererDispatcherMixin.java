package com.inovactio.awakenawakennomi.mixins;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TileEntityRendererDispatcher.class)
public class TileEntityRendererDispatcherMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends TileEntity> void onRender(E tile, float partialTicks,
                                                 MatrixStack matrixStack,
                                                 IRenderTypeBuffer buffer,
                                                 CallbackInfo ci) {
        BlockPos pos = tile.getBlockPos();
        if (InvisibleBlockManager.isInvisible(pos)) {
            ci.cancel(); // cancel rendering for invisible tile entities
        }
    }
}
