package dev.lazurite.form.api;

import dev.lazurite.form.api.loader.TemplateLoader;
import dev.lazurite.form.impl.common.item.TemplatedItemStack;
import dev.lazurite.form.impl.common.template.model.Template;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface Templated {
    interface Item { }

    static Optional<Template> find(String template) {
        return TemplateLoader.getTemplateById(template);
    }

    static Templated get(ItemStack stack) {
        return new TemplatedItemStack(stack);
    }

    default void copyFrom(Templated templated) {
        this.setTemplate(templated.getTemplate());
    }

    String getTemplate();
    void setTemplate(String template);
}
