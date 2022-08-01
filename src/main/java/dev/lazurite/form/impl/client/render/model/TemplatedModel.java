package dev.lazurite.form.impl.client.render.model;

import dev.lazurite.form.api.Templated;
import dev.lazurite.form.impl.common.Form;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoModel;
import software.bernie.geckolib3.model.AnimatedGeoModel;

/**
 * This class dynamically provides a {@link Templated} model
 * based on which template it is assigned. It also caches the {@link GeoModel} object.
 */
public class TemplatedModel<T extends Templated & IAnimatable> extends AnimatedGeoModel<T> {
    private GeoModel cachedModel;

    @Override
    public ResourceLocation getModelResource(T remoteControllableEntity) {
        return new ResourceLocation(Form.MODID, remoteControllableEntity.getTemplate());
    }

    @Override
    public ResourceLocation getTextureResource(T remoteControllableEntity) {
        return new ResourceLocation(Form.MODID, remoteControllableEntity.getTemplate());
    }

    @Override
    public ResourceLocation getAnimationResource(T remoteControllableEntity) {
        return new ResourceLocation(Form.MODID, remoteControllableEntity.getTemplate());
    }

    @Override
    public GeoModel getModel(ResourceLocation resourceLocation) {
        final var model = super.getModel(resourceLocation);

        if (model != null) {
            cachedModel = model;
        }

        return cachedModel;
    }
}
