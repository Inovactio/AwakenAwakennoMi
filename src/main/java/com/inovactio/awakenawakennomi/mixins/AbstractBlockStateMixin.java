package com.inovactio.awakenawakennomi.mixins;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

    @Inject(method = "isSolidRender", at = @At("HEAD"), cancellable = true)
    private void onIsSolidRender(IBlockReader world, BlockPos pos,
                                 CallbackInfoReturnable<Boolean> cir) {
        if (InvisibleBlockManager.isInvisible(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canOcclude", at = @At("HEAD"), cancellable = true)
    private void onCanOcclude(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "getOcclusionShape", at = @At("HEAD"), cancellable = true)
    private void onGetOcclusionShape(IBlockReader world, BlockPos pos,
                                     CallbackInfoReturnable<VoxelShape> cir) {
        if (InvisibleBlockManager.isInvisible(pos)) {
            cir.setReturnValue(VoxelShapes.empty());
        }
    }

    @Inject(method = "isFaceSturdy", at = @At("HEAD"), cancellable = true)
    private void onIsFaceSturdy(IBlockReader world, BlockPos pos, Direction side,
                                CallbackInfoReturnable<Boolean> cir) {
        if (InvisibleBlockManager.isInvisible(pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "skipRendering", at = @At("HEAD"), cancellable = true)
    private void onSkipRendering(BlockState neighbor, Direction side,
                                 CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
