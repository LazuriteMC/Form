package dev.lazurite.form.impl.common.mixin;

import dev.lazurite.form.api.Templated;
import dev.lazurite.form.api.event.TemplateEvents;
import dev.lazurite.form.api.loader.TemplateLoader;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow private EntityDimensions dimensions;
    @Shadow public abstract void setBoundingBox(AABB aABB);
    @Shadow public abstract double getX();
    @Shadow public abstract double getY();
    @Shadow public abstract double getZ();
    @Shadow public abstract AABB getBoundingBox();

    @Inject(method = "refreshDimensions", at = @At("HEAD"), cancellable = true)
    public void refreshDimensions_HEAD(CallbackInfo info) {
        if (this instanceof Templated templated) {
            TemplateLoader.getTemplateById(templated.getTemplate()).ifPresent(template -> {
                final var dimensions1 = this.dimensions;
                this.dimensions = new EntityDimensions(template.metadata().get("width").getAsFloat(), template.metadata().get("height").getAsFloat(), true);

                if (dimensions.width < dimensions1.width) {
                    final var d = (double) dimensions.width / 2.0D;
                    setBoundingBox(new AABB(getX() - d, getY(), getZ() - d, getX() + d, getY() + dimensions.height, getZ() + d));
                } else {
                    final var box = this.getBoundingBox();
                    setBoundingBox(new AABB(box.minX, box.minY, box.minZ, box.minX + dimensions.width, box.minY + dimensions.height, box.minZ + dimensions.width));
                }

                TemplateEvents.ENTITY_TEMPLATE_CHANGED.invoke(this);
                info.cancel();
            });
        }
    }
}
