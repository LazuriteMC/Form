package dev.lazurite.form.impl.client.mixin.resource;

import dev.lazurite.form.impl.client.resource.TemplateTextureManager;
import dev.lazurite.form.impl.common.Form;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * This mixin intercepts any resource that has a namespace starting with {@link Form#MODID} and
 * uses the {@link TemplateTextureManager} instead.
 */
@Mixin(TextureManager.class)
public abstract class TextureManagerMixin {
    @Shadow @Final private ResourceManager resourceManager;

    @Redirect(
            method = "loadTexture",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/renderer/texture/TextureManager;resourceManager:Lnet/minecraft/server/packs/resources/ResourceManager;"
            )
    )
    public ResourceManager loadTexture_FIELD(TextureManager textureManager, ResourceLocation resourceLocation) {
        if (resourceLocation.getNamespace().equals(Form.MODID)) {
            return new TemplateTextureManager(resourceManager);
        }

        return resourceManager;
    }
}
