package com.omga.omgen.logic;

import com.google.gson.*;
import com.omga.omgen.util.RandomCollection;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GenerationEntry {
    public GenerationCondition condition;
    // blockstate-weight
    public RandomCollection<BlockState> pool;

    public GenerationEntry(GenerationCondition condition, RandomCollection<BlockState> pool) {
        this.condition = condition;
        this.pool = pool;
    }
}
