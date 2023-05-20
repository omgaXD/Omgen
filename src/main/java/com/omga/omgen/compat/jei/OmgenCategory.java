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
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;
import org.apache.commons.codec.language.bm.Lang;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("removal")
public class OmgenCategory implements IRecipeCategory<GenerationEntry> {
    public static final int width = 116;
    public static final int height = 91;
    private final IDrawable background;
    private final IDrawable icon;

    private final Component localizedName;

    private static final Logger LOGGER = LogUtils.getLogger();
    public OmgenCategory(IGuiHelper guiHelper) {
        ResourceLocation location = new ResourceLocation(Omgen.MODID, "textures/gui/omgen.png");
        this.background = guiHelper.createDrawable(location, 0, 0, width, height);
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

    /**
     * Returns a screen coordinate value for the slot coordinate.
     * @param x The SLOT coordinate
     * @return The SCREEN coordinate
     */
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
                resultX = 95, resultY = 1,
                sideY = 55;
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
        IRecipeSlotBuilder interactionSlot = builder.addSlot(RecipeIngredientRole.CATALYST, f(interactionX), f(interactionY));
        if (context.neighbourBlocksAround != null) {
            // add a new slot for side blocks per each next unique value
            ArrayList<IRecipeSlotBuilder> sideSlots = new ArrayList<>();
            AtomicInteger x = new AtomicInteger();
            Arrays.stream(context.neighbourBlocksAround).iterator().forEachRemaining(b -> {
                if (b.holdsItem()) {
                    sideSlots.add(builder.addSlot(RecipeIngredientRole.CATALYST, 44 + f(x.get()), sideY).addItemStack(new ItemStack(b.item.asItem())));
                    x.getAndIncrement();
                } else if (b.holdsTagKey()) {
                    var slot = builder.addSlot(RecipeIngredientRole.CATALYST, 44 + f(x.get()), sideY);
                    Registry.BLOCK.getTag(b.tagKey).get().forEach(b2 -> {
                        slot.addItemStack(new ItemStack(b2.value().asItem()));
                        x.getAndIncrement();
                    });
                    sideSlots.add(slot);
                }
            });
            /*
            sideSlots.forEach(s -> s.addTooltipCallback(new IRecipeSlotTooltipCallback() {
                @Override
                public void onTooltip(IRecipeSlotView recipeSlotView, List<Component> tooltip) {
                    if (recipeSlotView.getDisplayedIngredient().get().getItemStack().isPresent())
                        tooltip.add(Component.translatable("gui.omgen.text.side").append(": " + blocksCount.getOrDefault(recipeSlotView
                                .getDisplayedIngredient()
                                .get().getItemStack().get().getItem(), 0)));
                }
            }));*/
        }

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
        MutableComponent acceptableHeightText = new TranslatableComponent("gui.omgen.text.acceptable_height");

        Integer maxHeight = recipe.condition.getContext().maxHeight;
        Integer minHeight = recipe.condition.getContext().minHeight;

        if (maxHeight == null && minHeight == null) {
            acceptableHeightText.append(new TranslatableComponent("gui.omgen.text.any"));
        } else {
            if (minHeight != null) {
                acceptableHeightText.append(minHeight + " ≤ ");
            }
            acceptableHeightText.append("Y");
            if (maxHeight != null) {
                acceptableHeightText.append(" ≤ " + maxHeight);
            }
        }
        Minecraft.getInstance().font.draw(stack, acceptableHeightText , 2, 79, DyeColor.WHITE.getTextColor());
    }

}
