package dev.lazurite.form.impl.client.render;

import dev.lazurite.form.api.Templated;
import net.minecraft.world.item.Item;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class TemplatedItemRenderer<T extends Item & GeoAnimatable & Templated.Item> extends GeoItemRenderer<T> {

    public TemplatedItemRenderer() {
        super(new TemplatedModel<>());
    }

    @Override
    public TemplatedModel<T> getGeoModel() {
        return (TemplatedModel<T>) this.model;
    }
}
