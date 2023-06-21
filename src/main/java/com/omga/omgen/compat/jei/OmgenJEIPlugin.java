package com.omga.omgen.compat.jei;

import com.omga.omgen.Omgen;
import com.omga.omgen.resources.OmgenReloadListener;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IAdvancedRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Panda;

@JeiPlugin
public class OmgenJEIPlugin implements IModPlugin {
    OmgenCategory omgencategory;

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Omgen.MODID, "jei_plugin");
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(omgencategory.getRecipeType(), OmgenReloadListener.entries.values().asList());
    }


    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        omgencategory =  new OmgenCategory(registration.getJeiHelpers().getGuiHelper());
        registration.addRecipeCategories(omgencategory);
    }

}
