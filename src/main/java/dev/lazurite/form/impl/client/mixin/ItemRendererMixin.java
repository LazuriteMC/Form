package dev.lazurite.form.impl.client.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.lazurite.form.api.Templated;
import dev.lazurite.form.api.render.FormRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import software.bernie.geckolib.mixins.fabric.MixinItemRenderer;

/**
 * Uses priority 900 so it will apply before {@link MixinItemRenderer}.
 */
@Mixin(value = ItemRenderer.class, priority = 900)
public class ItemRendererMixin {

    @Inject(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/BlockEntityWithoutLevelRenderer;renderByItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"
            )
    )
    public void render$renderByItem(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo ci) {
        if (itemStack.getItem() instanceof Templated.Item item) {
            FormRegistry.getItemRenderer(item).getGeoModel().using(itemStack);
        }
    }

    @ModifyArgs(
            method = "renderGuiItem(Lnet/minecraft/world/item/ItemStack;IILnet/minecraft/client/resources/model/BakedModel;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"
            )
    )
    public void renderGuiItem$render(Args args) {
        if (((ItemStack) args.get(0)).getItem() instanceof Templated.Item) {
            args.set(1, ItemTransforms.TransformType.GUI);
        }
    }

}
