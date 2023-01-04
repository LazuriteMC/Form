package dev.lazurite.form.impl.client.render.model;

import dev.lazurite.form.api.Templated;
import dev.lazurite.form.impl.common.Form;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

/**
 * This class dynamically provides a {@link Templated} model
 * based on which template it is assigned. It also caches the {@link GeoModel} object.
 */
public class TemplatedModel<T extends Templated & GeoAnimatable> extends GeoModel<T> {
    private BakedGeoModel cachedModel;

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
    public BakedGeoModel getBakedModel(ResourceLocation resourceLocation) {
        final var model = super.getBakedModel(resourceLocation);

        if (model != null) {
            cachedModel = model;
        }

        return cachedModel;
    }
}
