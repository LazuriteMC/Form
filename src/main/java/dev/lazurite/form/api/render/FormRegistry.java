package dev.lazurite.form.api.render;

import dev.lazurite.form.api.Templated;
import dev.lazurite.form.impl.client.render.TemplatedItemRenderer;

import java.util.HashMap;
import java.util.Map;

public final class FormRegistry {

    private static final Map<Templated.Item, TemplatedItemRenderer<?>> itemRenderers = new HashMap<>();

    public static void register(Templated.Item item) {
        itemRenderers.put(item, new TemplatedItemRenderer<>());
    }

    public static TemplatedItemRenderer<?> getItemRenderer(Templated.Item item) {
        return itemRenderers.get(item);
    }

    private FormRegistry() {
    }
}
