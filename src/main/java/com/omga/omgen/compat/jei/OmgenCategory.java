package com.omga.omgen.compat.jei;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.logging.LogUtils;
import com.omga.omgen.Omgen;
import com.omga.omgen.logic.GenerationCondition;
import com.omga.omgen.logic.GenerationEntry;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotTooltipCallback;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.apache.commons.codec.language.bm.Lang;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("removal")
public class OmgenCategory implements IRecipeCategory<GenerationEntry> {
    public static final int width = 116;
    public static final int height = 54;
    private final IDrawable background;
    private final IDrawable icon;

    private final Component localizedName;

    private static final Logger LOGGER = LogUtils.getLogger();
    public OmgenCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation("jei", "textures/gui/gui_vanilla.png");;
        this.background = guiHelper.createDrawable(location, 0, 60, width, height);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Blocks.COBBLESTONE));
        this.localizedName = Component.translatable("gui.omgen.category.omgen");

    }

    public ResourceLocation getUid() {
        return new ResourceLocation(Omgen.MODID, Omgen.MODID);
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
    @Override
    public RecipeType<GenerationEntry> getRecipeType() {
        return new RecipeType<>(getUid(), GenerationEntry.class);
    }

    private static int f(int x) {
        return x * 18 + 1;
    }
    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GenerationEntry recipe, IFocusGroup focuses) {
        GenerationCondition.Context context = recipe.condition.getContext();

        /*
        var temp = builder.addInvisibleIngredients(RecipeIngredientRole.INPUT);
        if (context.initiatingFluid.holdsItem()) {
            temp.addFluidStack(context.initiatingFluid.item, 1000);
        } else if (context.initiatingFluid.holdsTagKey()) {
            Registry.FLUID.getTag(context.initiatingFluid.tagKey).get().forEach(f -> {
                temp.addFluidStack(f.value(), 1000);
            });
        }

        if (context.theOtherFluid != null) {
            if (context.theOtherFluid.holdsItem()) {
                temp.addFluidStack(context.theOtherFluid.item, 1000);
            } else if (context.theOtherFluid.holdsTagKey()) {
                Registry.FLUID.getTag(context.theOtherFluid.tagKey).get().forEach(f -> {
                    temp.addFluidStack(f.value(), 1000);
                });
            }
        }

        if (context.blockBelow != null) {
            if (context.blockBelow.holdsItem()) {
                temp.addItemStack(new ItemStack(context.blockBelow.item.asItem()));
            } else if (context.blockBelow.holdsTagKey()) {
                Registry.BLOCK.getTag(context.blockBelow.tagKey).get().forEach(b -> {
                    temp.addItemStack(new ItemStack(b.value().asItem()));
                });
            }
        }*/

        // prepare positions
        int     fluid1X = 0, fluid1Y = 1,
                fluid2X = fluid1X + 2, fluid2Y = fluid1Y,
                interactionX = fluid1X + 1, interactionY = fluid1Y,
                resultX = 95, resultY = 1;
        ;
        boolean primaryAbove = false;
        if (recipe.condition.getContext().pos == null) {

        } else switch (recipe.condition.getContext().pos) {
            case Doesntmatter:
            case Neighbour:
                break;

            case Replace:
                fluid1X += 1;
                fluid1Y -= 1;
                primaryAbove = true;
                break;
        }
        IRecipeSlotBuilder primarySlot = builder.addSlot(RecipeIngredientRole.CATALYST, f(fluid1X), f(fluid1Y));
        if (context.initiatingFluid.holdsItem()) {
            primarySlot.addFluidStack(context.initiatingFluid.item, 1000);
        } else if (context.initiatingFluid.holdsTagKey()) {
            Registry.FLUID.getTag(context.initiatingFluid.tagKey).get().forEach(f -> {
                primarySlot.addFluidStack(f.value(), 1000);
            });
        }
        IRecipeSlotBuilder secondarySlot = builder.addSlot(RecipeIngredientRole.CATALYST, f(fluid2X), f(fluid2Y));
        if (context.theOtherFluid != null) {
            if (context.theOtherFluid.holdsItem()) {
                secondarySlot.addFluidStack(context.theOtherFluid.item, 1000);
            } else if (context.theOtherFluid.holdsTagKey()) {
                Registry.FLUID.getTag(context.theOtherFluid.tagKey).get().forEach(f -> {
                    secondarySlot.addFluidStack(f.value(), 1000);
                });
            }
        }
        IRecipeSlotBuilder belowSlot = builder.addSlot(RecipeIngredientRole.CATALYST, f(interactionX), f(interactionY + 1));
        if (context.blockBelow != null) {
            if (context.blockBelow.holdsItem()) {
                belowSlot.addItemStack(new ItemStack(context.blockBelow.item.asItem()));
            } else if (context.blockBelow.holdsTagKey()) {
                Registry.BLOCK.getTag(context.blockBelow.tagKey).get().forEach(b -> {
                    belowSlot.addItemStack(new ItemStack(b.value().asItem()));
                });
            }
        }
        belowSlot.addTooltipCallback(new IRecipeSlotTooltipCallback() {
            @Override
            public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
                tooltip.add(Component.translatable("gui.omgen.text.below"));
            }
        });
        IRecipeSlotBuilder aboveSlot;
        if (primaryAbove) {
            aboveSlot = primarySlot;
        } else {
            aboveSlot = builder.addSlot(RecipeIngredientRole.CATALYST, f(interactionX), f(interactionY - 1));
        }
        if (context.blockAbove != null) {
            if (context.blockAbove.holdsItem()) {
                aboveSlot.addItemStack(new ItemStack(context.blockAbove.item.asItem()));
            } else if (context.blockAbove.holdsTagKey()) {
                Registry.BLOCK.getTag(context.blockAbove.tagKey).get().forEach(b -> {
                    aboveSlot.addItemStack(new ItemStack(b.value().asItem()));
                });
            }
        }
        aboveSlot.addTooltipCallback(new IRecipeSlotTooltipCallback() {
            @Override
            public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
                tooltip.add(Component.translatable("gui.omgen.text.above"));
            }
        });
        Map<Item, Integer> blocksCount = new HashMap<>();
        IRecipeSlotBuilder interactionSlot = builder.addSlot(RecipeIngredientRole.CATALYST, f(interactionX), f(interactionY));
        if (context.neighbourBlocksAround != null) {
            Arrays.stream(context.neighbourBlocksAround).iterator().forEachRemaining(b -> {
                if (b.holdsItem()) {
                    interactionSlot.addItemStack(new ItemStack(b.item.asItem()));
                    if (blocksCount.getOrDefault(b.item.asItem(), 0) == 0) {
                        blocksCount.put(b.item.asItem(), 1);
                    } else {
                        blocksCount.put(b.item.asItem(), blocksCount.get(b.item.asItem()) + 1);
                    }
                } else if (b.holdsTagKey()) {
                    Registry.BLOCK.getTag(b.tagKey).get().forEach(b2 -> {
                        interactionSlot.addItemStack(new ItemStack(b2.value().asItem()));
                        if (blocksCount.getOrDefault(b2.value().asItem(), 0) == 0) {
                            blocksCount.put(b2.value().asItem(), 1);
                        } else {
                            blocksCount.put(b2.value().asItem(), blocksCount.get(b2.value().asItem()) + 1);
                        }
                    });
                }
            });
        }
        interactionSlot.addTooltipCallback(new IRecipeSlotTooltipCallback() {
            @Override
            public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
                if (recipeSlotView.getDisplayedIngredient().get().getItemStack().isPresent())
                    tooltip.add(Component.translatable("gui.omgen.text.side").append(": " + blocksCount.getOrDefault(recipeSlotView
                        .getDisplayedIngredient()
                        .get().getItemStack().get().getItem(), 0)));
            }
        });
        IRecipeSlotBuilder resultSlot = builder.addSlot(RecipeIngredientRole.OUTPUT, resultX, f(resultY)).setSlotName("result");
        AtomicReference<Double> latest = new AtomicReference<>((double) 0);
        recipe.pool.forEach((d, bs) -> {
            var stack = new ItemStack(bs.getBlock().asItem());
            stack.setHoverName(newHoverName(stack, d - latest.get(), recipe.pool.getTotal()));
            resultSlot.addItemStack(stack);
            latest.updateAndGet(v -> (double) (d));
        });


        // add fluids in interaction slot if they make sense to be here.
        if (context.initiatingFluid.holdsItem()) {
            interactionSlot.addFluidStack(context.initiatingFluid.item, 1000);
        } else if (context.initiatingFluid.holdsTagKey()) {
            Registry.FLUID.getTag(context.initiatingFluid.tagKey).get().forEach(f -> {
                interactionSlot.addFluidStack(f.value(), 1000);
            });
        }
        if (primaryAbove) {
            if (context.theOtherFluid.holdsItem()) {
                interactionSlot.addFluidStack(context.theOtherFluid.item, 1000);
            } else if (context.theOtherFluid.holdsTagKey()) {
                Registry.FLUID.getTag(context.theOtherFluid.tagKey).get().forEach(f -> {
                    interactionSlot.addFluidStack(f.value(), 1000);
                });
            }
        }
    }
    public static String beautifyDouble(double d) {
        String result = String.format("%.2f", d).replaceAll("0*$", "").replace(',', '.').replaceAll("\\.$", "");;
        if (Math.abs(d - Double.parseDouble(result)) >= 0.005) {
            result = "~" + result;
        }
        return result;
    }
    public static Component newHoverName(ItemStack stack, double d, double total) {
        return stack.getHoverName().copy().append(" | ").append(Component.translatable("gui.omgen.text.weight").append(": " + beautifyDouble(d * 100 / total)).append("%"));
    }
    @Override
    public void draw(GenerationEntry recipe, IRecipeSlotsView recipeSlotsView, PoseStack stack, double mouseX, double mouseY) {
        Minecraft.getInstance().font.draw(stack, new TranslatableComponent("gui.omgen.text.min_height").append(String.valueOf(recipe.condition.getContext().minHeight)), 9, 55, DyeColor.WHITE.getTextColor());
        Minecraft.getInstance().font.draw(stack, new TranslatableComponent("gui.omgen.text.max_height").append(String.valueOf(recipe.condition.getContext().maxHeight)), 9, 58, DyeColor.WHITE.getTextColor());
    }

}
