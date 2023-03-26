package com.omga.omgen.util;

import com.google.gson.*;
import com.mojang.logging.LogUtils;
import com.omga.omgen.logic.GenerationCondition;
import net.minecraft.core.Registry;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import javax.json.JsonArrayBuilder;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.BiConsumer;

public class RandomCollection<E> {
    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;

    public double getTotal() {
        return total;
    }

    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public void forEach(BiConsumer<Double, E> consumer) {
        map.forEach(consumer);
    }
    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public E next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }

    public static class Serializer implements JsonDeserializer<RandomCollection<BlockState>>, JsonSerializer<RandomCollection<BlockState>> {

        @Override
        public RandomCollection<BlockState> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            var arr = json.getAsJsonArray();
            RandomCollection<BlockState> blockRandomCollection = new RandomCollection<>();
            arr.forEach(element -> {
                double weight = element.getAsJsonArray().get(0).getAsDouble();
                String string = element.getAsJsonArray().get(1).getAsString();
                ItemOrTagKey<Block> itemOrTag = GenerationCondition.Serializer.fromString(string, ForgeRegistries.BLOCKS);
                List<Block> blocks = new ArrayList<>();
                if (itemOrTag.holdsItem()) {
                    blocks.add(itemOrTag.item);
                } else {
                    try {
                        Registry.BLOCK.getTag(itemOrTag.tagKey).get().forEach(block -> {
                            blocks.add(block.value());
                        });
                    } catch (NoSuchElementException e) {
                        LogUtils.getLogger().debug(itemOrTag.tagKey.location().toString() + "doesnt exist!!");
                        Registry.BLOCK.getTagNames().forEach(t -> {
                            LogUtils.getLogger().debug(t.location().toString());
                        });
                        e.printStackTrace();
                    }
                }
                blocks.forEach(block -> {
                    blockRandomCollection.add(weight / blocks.size(), block.defaultBlockState());
                });
            });

            return blockRandomCollection;
        }

        @Override
        public JsonElement serialize(RandomCollection<BlockState> src, Type typeOfSrc, JsonSerializationContext context) {
            JsonArray gens = new JsonArray();
            src.map.forEach((d, bs) -> {
                var temp = new JsonArray();
                temp.add(d);
                temp.add(bs.getBlock().getRegistryName().toString());
                gens.add(temp);
            });
            return gens;
        }
    }
}