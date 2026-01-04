package com.inovactio.awakenawakennomi.mixins;

import com.inovactio.awakenawakennomi.api.common.InvisibleBlockManager;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(TileEntityRendererDispatcher.class)
public class TileEntityRendererDispatcherMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private <E extends TileEntity> void onRender(E tile, float partialTicks,
                                                 MatrixStack matrixStack,
                                                 IRenderTypeBuffer buffer,
                                                 CallbackInfo ci) {
        BlockPos pos = tile.getBlockPos();

        boolean invisible = false;
        World world = tile.getLevel(); // peut être null côté client dans certains contextes

        if (world != null) {
            invisible = InvisibleBlockManager.isInvisible(world, pos);
        } else {
            Minecraft mc = Minecraft.getInstance();
            if (mc != null && mc.player != null) {
                UUID playerUuid = mc.player.getUUID();
                invisible = InvisibleBlockManager.isInvisible(pos, playerUuid);
            }
        }

        if (invisible) {
            ci.cancel(); // annule le rendu des tile entities invisibles
        }
    }
}