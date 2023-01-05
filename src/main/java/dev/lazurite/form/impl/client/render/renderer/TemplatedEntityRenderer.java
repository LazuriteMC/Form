package dev.lazurite.form.impl.client.render.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.form.api.Templated;
import dev.lazurite.form.impl.client.render.model.TemplatedModel;
import dev.lazurite.form.api.loader.TemplateLoader;
import dev.lazurite.form.impl.common.mixin.EntityAccess;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public abstract class TemplatedEntityRenderer<T extends LivingEntity & Templated & GeoAnimatable> extends GeoEntityRenderer<T> {
    public TemplatedEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TemplatedModel<>());
    }

    @Override
    public void render(T entity, float entityYaw, float tickDelta, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        TemplateLoader.getTemplateById(entity.getTemplate()).ifPresent(template -> {
            this.shadowRadius = ((EntityAccess) entity).getDimensions().width * ((EntityAccess) entity).getDimensions().height * 2;
            super.render(entity, 0, tickDelta, stack, bufferIn, packedLightIn);
        });
    }
}
