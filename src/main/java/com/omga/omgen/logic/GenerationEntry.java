package com.omga.omgen.logic;

import com.omga.omgen.util.WeightedRandomCollection;
import net.minecraft.world.level.block.state.BlockState;

public class GenerationEntry {
    public GenerationCondition condition;
    // blockstate-weight
    public WeightedRandomCollection<BlockState> pool;

    private int priority;

    public GenerationEntry(GenerationCondition condition, WeightedRandomCollection<BlockState> pool, int priority) {
        this.condition = condition;
        this.pool = pool;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
