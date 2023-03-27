package com.omga.omgen.mixin;

import com.omga.omgen.logic.Generation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {
    @Inject(at = @At("HEAD"), method = "onPlace", cancellable = true)
    private void onPlace(BlockState idk1, Level level, BlockPos bp, BlockState idk2, boolean idk3, CallbackInfo ci) {
        injection(level, bp, ci);
    }
    @Inject(at = @At("HEAD"), method = "neighborChanged", cancellable = true)
    private void neighborChanged(BlockState idk1, Level level, BlockPos bp, Block idk2, BlockPos idk3, boolean idk4, CallbackInfo ci) {
        injection(level, bp, ci);
    }

    private void injection(Level level, BlockPos bp, CallbackInfo ci) {
        var newBS = Generation.generateAt(level, bp);
        if (newBS != null) {
            level.setBlockAndUpdate(bp, newBS);
            // fizz (that's how vanilla does it)
            level.levelEvent(1501, bp, 0);
            ci.cancel();
        }
    }
}
