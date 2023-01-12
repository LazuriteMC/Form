package dev.lazurite.form.impl.client.render;

import dev.lazurite.form.api.Templated;
import dev.lazurite.form.impl.common.Form;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

/**
 * This class dynamically provides a {@link Templated} model
 * based on which template it is assigned. It also caches the {@link GeoModel} object.
 */
public class TemplatedModel<T extends GeoAnimatable & Templated> extends GeoModel<T> {
    private BakedGeoModel cachedModel;
    private Templated itemStack;

    @Override
    public ResourceLocation getModelResource(T templated) {
        if (itemStack != null) {
            return new ResourceLocation(Form.MODID, itemStack.getTemplate());
        }

        return new ResourceLocation(Form.MODID, templated.getTemplate());
    }

    @Override
    public ResourceLocation getTextureResource(T templated) {
        if (itemStack != null) {
            return new ResourceLocation(Form.MODID, itemStack.getTemplate());
        }

        return new ResourceLocation(Form.MODID, templated.getTemplate());
    }

    @Override
    public ResourceLocation getAnimationResource(T templated) {
        if (itemStack != null) {
            return new ResourceLocation(Form.MODID, itemStack.getTemplate());
        }

        return new ResourceLocation(Form.MODID, templated.getTemplate());
    }

    @Override
    public BakedGeoModel getBakedModel(ResourceLocation resourceLocation) {
        final var model = super.getBakedModel(resourceLocation);

        if (model != null) {
            cachedModel = model;
        }

        return cachedModel;
    }

    public void using(ItemStack itemStack) {
        if (itemStack.getItem() instanceof Templated.Item) {
            this.itemStack = Templated.get(itemStack);
        }
    }
}
