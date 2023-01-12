package dev.lazurite.form.impl.common.item;

import dev.lazurite.form.api.Templated;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

/**
 * Stores all relevant template information available via {@link Templated}
 * @see Templated
 */
public class TemplatedItemWrapper implements Templated {
    private final CompoundTag tag;

    public TemplatedItemWrapper(ItemStack stack) {
        this.tag = stack.getOrCreateTagElement("templated");
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
        if (obj instanceof final TemplatedItemWrapper template) {
            return getTemplate().equals(template.getTemplate());
        }

        return false;
    }
}
