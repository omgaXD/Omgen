package com.omga.omgen.logic;

import com.google.gson.*;
import com.omga.omgen.util.ItemOrTagKey;
import com.omga.omgen.util.RandomCollection;
import com.omga.omgen.util.StaticHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.json.JsonArrayBuilder;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class GenerationCondition {
    private Context context;
    private Predicate<FluidState> initiatingFluid;
    private BiPredicate<LevelAccessor, BlockPos> condition;

    public Predicate<FluidState> getInitiatingFluid() {
        return initiatingFluid;
    }

    public BiPredicate<LevelAccessor, BlockPos> getCondition() {
        return condition;
    }
    @SuppressWarnings("ConstantConditions")
    public GenerationCondition(@Nonnull Context context) {
        // Set this for debugging ease purposes
        this.context = context;

        if (context.initiatingFluid.holdsItem()) {
            initiatingFluid = (fs) -> fs.is(context.initiatingFluid.item);
        } else if (context.initiatingFluid.holdsTagKey()) {
            initiatingFluid = (fs) -> fs.is(context.initiatingFluid.tagKey);
        } else {
            // this cna't be null because initiating fluid is only really important thing tbh.
            throw new NullPointerException("Skill issue occurred");
        }

        // starting a massive predicate that'll do the big check for logic.
        condition = (l, bp) -> true;

        // add the other fluid to the predicate.
        if (context.theOtherFluid != null) {
            if (context.pos == null) throw new NullPointerException("Skill issue occurred #2");

            if (context.pos == Context.PositionOfTheOtherFluid.Replace) {
                // if I want the generation to happen when liquid #1 drops onto liquid #2 from above.
                if (context.theOtherFluid.holdsItem()) {
                    condition = condition.and((l, bp) -> l.getFluidState(bp).is(context.theOtherFluid.item));
                } else if (context.theOtherFluid.holdsTagKey()) {
                    condition = condition.and((l, bp) -> l.getFluidState(bp).is(context.theOtherFluid.tagKey));
                }
            } else if (context.pos == Context.PositionOfTheOtherFluid.Neighbour) {
                // if I want the generation to happen when the other liquid is right next to the new block of my current.
                if (context.theOtherFluid.holdsItem()) {
                    condition = condition.and((l, bp) -> Generation.getFluidStatesAroundButNotAbove(l, bp).stream().anyMatch(fs -> fs.is(context.theOtherFluid.item)));
                } else if (context.theOtherFluid.holdsTagKey()) {
                    condition = condition.and((l, bp) -> Generation.getFluidStatesAroundButNotAbove(l, bp).stream().anyMatch(fs -> fs.is(context.theOtherFluid.tagKey)));
                }
            } else if (context.pos == Context.PositionOfTheOtherFluid.Doesntmatter) {
                if (context.theOtherFluid.holdsItem()) {
                    condition = condition.and((l, bp) -> Generation.getFluidStatesAroundButNotAbove(l, bp).stream().anyMatch(fs -> fs.is(context.theOtherFluid.item)) || l.getFluidState(bp).is(context.theOtherFluid.item));
                } else if (context.theOtherFluid.holdsTagKey()) {
                    condition = condition.and((l, bp) -> Generation.getFluidStatesAroundButNotAbove(l, bp).stream().anyMatch(fs -> fs.is(context.theOtherFluid.tagKey)) || l.getFluidState(bp).is(context.theOtherFluid.tagKey));
                }
            }
        }

        // add block above.
        if (context.blockAbove != null) {
            if (context.blockAbove.holdsItem()) {
                condition = condition.and((l, bp) -> l.getBlockState(bp.above()).is(context.blockAbove.item));
            } else if (context.blockAbove.holdsTagKey()) {
                condition = condition.and((l, bp) -> l.getBlockState(bp.above()).is(context.blockAbove.tagKey));
            }
        }

        // block below
        if (context.blockBelow != null) {
            if (context.blockBelow.holdsItem()) {
                condition = condition.and((l, bp) -> l.getBlockState(bp.below()).is(context.blockBelow.item));
            } else if (context.blockBelow.holdsTagKey()) {
                condition = condition.and((l, bp) -> l.getBlockState(bp.below()).is(context.blockBelow.tagKey));
            }
        }
        // blocks around (4 blocks at most)
        if (context.neighbourBlocksAround != null) {
            // size check, if too big, skill issue.
            if (context.neighbourBlocksAround.length > 4) {
                throw new IllegalArgumentException("Array length skill issue occurred #1");
            }
            condition = condition.and((l, bp) -> checkForAllMatches(l, bp, context.neighbourBlocksAround));
        }


    }
    private static boolean checkForAllMatches(LevelAccessor l, BlockPos bp, ItemOrTagKey<Block>[] around) {
        List<BlockState> realAround = Generation.getBlockStatesAround(l, bp);
        return StaticHelper.isListAInListB(
                Arrays.stream(around).toList(),
                realAround,
                (ItemOrTagKey<Block> iotk, BlockState bs) -> {
                    if (iotk.holdsItem()) {
                        return bs.is(iotk.item);
                    } else if (iotk.holdsTagKey()) {
                        return bs.is(iotk.tagKey);
                    } else return false;
                }
        );
    }

    public Context getContext() {
        return context;
    }

    public static class Context {

        @Nullable
        public ItemOrTagKey<Block> blockBelow;
        @Nullable
        public ItemOrTagKey<Block> blockAbove;

        /**
         *  max size: 4
         */
        @Nullable
        public ItemOrTagKey<Block>[] neighbourBlocksAround;


        @Nonnull
        public ItemOrTagKey<Fluid> initiatingFluid;

        @Nullable
        public ItemOrTagKey<Fluid> theOtherFluid;
        @Nullable
        public PositionOfTheOtherFluid pos;

        public Context(@Nullable ItemOrTagKey<Block> blockBelow, @Nullable ItemOrTagKey<Block> blockAbove, @Nullable ItemOrTagKey<Block>[] neighbourBlocksAround, @Nonnull ItemOrTagKey<Fluid> initiatingFluid, @Nullable ItemOrTagKey<Fluid> theOtherFluid, @Nullable PositionOfTheOtherFluid pos) {
            this.blockBelow = blockBelow;
            this.blockAbove = blockAbove;
            this.neighbourBlocksAround = neighbourBlocksAround;
            this.initiatingFluid = initiatingFluid;
            this.theOtherFluid = theOtherFluid;
            this.pos = pos;
        }

        public enum PositionOfTheOtherFluid {
            /**When fluid is just nearby*/
            Neighbour,
            /**When fluid is being replaced (e.g. I come from above and try to replace other fluid below me)*/
            Replace,
            /**Both. Both are good.*/
            Doesntmatter
        }

    }
    public static class Serializer implements JsonDeserializer<GenerationEntry>, JsonSerializer<GenerationEntry> {

        public static Serializer getInstance() {
            if (_instance == null) {
                _instance = new GenerationCondition.Serializer();
            }
            return _instance;
        }
        private static Serializer _instance = null;

        @Override
        public GenerationEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext c) throws JsonParseException {

            JsonObject jsonobject = GsonHelper.convertToJsonObject(json, "omgen entry");


            // init everything for ease of reading.
            ItemOrTagKey<Block> blockBelow = null;
            ItemOrTagKey<Block> blockAbove = null;
            ItemOrTagKey<Block>[] neighbourBlocksAround = null;
            ItemOrTagKey<Fluid> initiatingFluid = null;
            ItemOrTagKey<Fluid> theOtherFluid = null;
            GenerationCondition.Context.PositionOfTheOtherFluid pos = null;

            // now deserialize it all.
            if (jsonobject.has("below"))
                blockBelow = fromString(GsonHelper.getAsString(jsonobject, "below"),
                        ForgeRegistries.BLOCKS);
            if (jsonobject.has("above"))
                blockAbove = fromString(GsonHelper.getAsString(jsonobject, "above"),
                        ForgeRegistries.BLOCKS);
            if (jsonobject.has("around")) {
                List<ItemOrTagKey<Block>> list = new ArrayList<>();
                GsonHelper.getAsJsonArray(jsonobject, "around").forEach(element -> {
                    list.add(fromString(element.getAsString(), ForgeRegistries.BLOCKS));
                });
                neighbourBlocksAround = list.toArray(new ItemOrTagKey[0]);

            }
            if (jsonobject.has("primary"))
                initiatingFluid = fromString(GsonHelper.getAsString(jsonobject, "primary"),
                        ForgeRegistries.FLUIDS);
            if (jsonobject.has("secondary"))
                theOtherFluid = fromString(GsonHelper.getAsString(jsonobject, "secondary"),
                        ForgeRegistries.FLUIDS);
            if (jsonobject.has("secondary_pos"))
                pos = Enum.valueOf(
                        GenerationCondition.Context.PositionOfTheOtherFluid.class,
                        GsonHelper.getAsString(jsonobject, "secondary_pos"));


            GenerationCondition.Context resultContext = new GenerationCondition.Context(
                    blockBelow,
                    blockAbove,
                    neighbourBlocksAround,
                    initiatingFluid,
                    theOtherFluid,
                    pos
            );

            // next up, serialize the weighted drops
            RandomCollection<BlockState> blockRandomCollection = new RandomCollection.Serializer().deserialize(jsonobject.getAsJsonArray("gens"), typeOfT, c);

            return new GenerationEntry(
                    new GenerationCondition(resultContext),
                    blockRandomCollection
            );
        }

        public static <T extends IForgeRegistryEntry<T>> ItemOrTagKey fromString(String string, IForgeRegistry<T> reg) {
            if (string == null) return null;
            if (string.isBlank()) {
                throw new IllegalArgumentException("Blank string");
            }
            if (string.startsWith("#")) {
                return new ItemOrTagKey<T>(reg.tags().createTagKey(new ResourceLocation(string.substring(1))));
            } else {
                return new ItemOrTagKey<T>(reg.getValue(new ResourceLocation(string)));
            }
        }

        @Override
        public JsonElement serialize(GenerationEntry src, Type typeOfSrc, JsonSerializationContext c) {
            JsonObject object = new JsonObject();

            // first add context-related
            var context = src.condition.getContext();
            object.addProperty("below", context.blockBelow.toString());
            object.addProperty("above", context.blockAbove.toString());

            JsonArray around = new JsonArray();
            for (ItemOrTagKey<Block> block :
                    context.neighbourBlocksAround) {
                around.add(block.toString());
            }
            object.add("around", around);

            object.addProperty("primary", context.initiatingFluid.toString());
            object.addProperty("secondary", context.theOtherFluid.toString());
            object.addProperty("secondary_pos", context.pos.name());

            // now random collection
            object.add("gens", new RandomCollection.Serializer().serialize(src.pool, typeOfSrc, c));
            return object;
        }
    }

}
