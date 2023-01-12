package dev.lazurite.form.api;

import dev.lazurite.form.api.loader.TemplateLoader;
import dev.lazurite.form.impl.common.item.TemplatedItemWrapper;
import dev.lazurite.form.impl.common.template.model.Template;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface Templated {

    interface Item extends Templated {
        // This is what we're doing for now until I figure out a better way.
        // The following methods should never be called.
        @Override
        default String getTemplate() {
            return "";
        }

        @Override
        default void setTemplate(String template) {

        }
    }

    static Optional<Template> find(String template) {
        return TemplateLoader.getTemplateById(template);
    }

    static Templated get(ItemStack stack) {
        return new TemplatedItemWrapper(stack);
    }

    default void copyFrom(Templated templated) {
        this.setTemplate(templated.getTemplate());
    }

    String getTemplate();
    void setTemplate(String template);

}
