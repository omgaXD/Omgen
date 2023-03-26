package com.omga.omgen.mixin;

import com.omga.omgen.logic.Generation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LiquidBlock.class)
public class LiquidBlockMixin {


    @Inject(at = @At("HEAD"), method = "shouldSpreadLiquid", cancellable = true)
    private void onLiquidSpread(Level level, BlockPos bp, BlockState bs, CallbackInfoReturnable<Boolean> ci) {
        var newBS = Generation.generateAt(level, bp);
        if (newBS != null) {
            level.setBlockAndUpdate(bp, newBS);
            // fizz (that's how vanilla does it)
            level.levelEvent(1501, bp, 0);
            ci.setReturnValue(false);
        }
    }

}
