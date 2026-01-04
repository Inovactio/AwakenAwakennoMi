package com.inovactio.awakenawakennomi.mixins;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class BlockShouldRenderMixin {

    @Inject(method = "shouldRenderFace(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/IBlockReader;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;)Z", at = @At("HEAD"), cancellable = true)
    private static void onShouldRenderFace(BlockState state, IBlockReader reader, BlockPos pos, Direction side, CallbackInfoReturnable<Boolean> cir) {
        BlockPos neighbor = pos.relative(side);
        boolean invisible = false;

        if (reader instanceof World) {
            invisible = InvisibleBlockManager.isInvisible((World) reader, neighbor);
        } else {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.player != null) {
                invisible = InvisibleBlockManager.isInvisible(neighbor, mc.player.getUUID());
            }
        }

        if (invisible) {
            // Si la position adjacente est invisible, forcer le rendu de la face
            cir.setReturnValue(true);
        }
    }
}

