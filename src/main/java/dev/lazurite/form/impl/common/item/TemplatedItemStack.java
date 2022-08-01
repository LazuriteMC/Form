package dev.lazurite.form.impl.common.item;

import dev.lazurite.form.api.Templated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Stores all relevant template information available via {@link Templated}
 * @see Templated
 */
public class TemplatedItemStack implements Templated {
    private final ItemStack stack;
    private final CompoundTag tag;

    public TemplatedItemStack(ItemStack stack) {
        this.stack = stack;
        this.tag = this.stack.getOrCreateTagElement("templated");
    }

    @Override
    public void setTemplate(String template) {
        tag.putString("template", template);
    }

    @Override
    public String getTemplate() {
        return tag.getString("template");
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof final TemplatedItemStack template) {
            return getTemplate().equals(template.getTemplate());
        }

        return false;
    }
}
