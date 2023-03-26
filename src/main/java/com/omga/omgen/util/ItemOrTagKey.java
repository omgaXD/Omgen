package com.omga.omgen.util;

import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

/** Holds either an item (Block, Fluid) or its tagkey.
 * @param <T> Anything that can have a TagKey
 */
public class ItemOrTagKey<T extends IForgeRegistryEntry<T>> {
    @Nullable
    public T item;
    @Nullable
    public TagKey<T> tagKey;

    public ItemOrTagKey(T item) {
        this.item = item;
        this.tagKey = null;
    }
    public ItemOrTagKey(TagKey<T> tagKey) {
        this.item = null;
        this.tagKey = tagKey;
    }
    public boolean holdsItem() {
        return item != null;
    }
    public boolean holdsTagKey() {
        return tagKey != null;
    }
    public static <T extends IForgeRegistryEntry<T>> ItemOrTagKey byItem(T item) {
        return new ItemOrTagKey<T>(item);
    }
    public static <T extends IForgeRegistryEntry<T>> ItemOrTagKey byTagKey(TagKey<T> item) {
        return new ItemOrTagKey<T>(item);
    }

    @Override
    public String toString() {
        if (this.holdsItem()) {
            return this.item.getRegistryName().toString();
        } else if (this.holdsTagKey()) {
            return "#" + this.tagKey.location().toString();
        }
        return "itemortagkey skill issue occurred";
    }
}
