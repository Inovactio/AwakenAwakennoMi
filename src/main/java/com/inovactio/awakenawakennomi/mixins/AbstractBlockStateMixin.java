package com.inovactio.awakenawakennomi.mixins;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import net.minecraft.block.AbstractBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public class AbstractBlockStateMixin {

    @Unique
    @SuppressWarnings("ConstantConditions")
    private static boolean mixin$isInvisibleAt(IBlockReader world, BlockPos pos) {
        // Si on a un World (serveur ou client), utiliser la vérification serveur persistante
        if (world instanceof World) {
            return InvisibleBlockManager.isInvisible((World) world, pos);
        }

        // Sinon, on est probablement côté client sans World fourni : utiliser le joueur local
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return false;
        return InvisibleBlockManager.isInvisible(pos, mc.player.getUUID());
    }

    @Inject(method = "isSolidRender", at = @At("HEAD"), cancellable = true)
    private void onIsSolidRender(IBlockReader world, BlockPos pos,
                                 CallbackInfoReturnable<Boolean> cir) {
        if (mixin$isInvisibleAt(world, pos)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getOcclusionShape", at = @At("HEAD"), cancellable = true)
    private void onGetOcclusionShape(IBlockReader world, BlockPos pos,
                                     CallbackInfoReturnable<VoxelShape> cir) {
        if (mixin$isInvisibleAt(world, pos)) {
            cir.setReturnValue(VoxelShapes.empty());
        }
    }

    @Inject(method = "isFaceSturdy(Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private void onIsFaceSturdy(IBlockReader world, BlockPos pos, Direction side,
                                CallbackInfoReturnable<Boolean> cir) {
        if (mixin$isInvisibleAt(world, pos)) {
            cir.setReturnValue(false);
        }
    }
}