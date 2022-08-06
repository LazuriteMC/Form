package dev.lazurite.form.impl.common.mixin;

import dev.lazurite.form.api.Templated;
import dev.lazurite.form.impl.common.Form;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "getDescriptionId(Lnet/minecraft/world/item/ItemStack;)Ljava/lang/String;", at = @At("RETURN"), cancellable = true)
    public void getDescriptionId_RETURN(ItemStack itemStack, CallbackInfoReturnable<String> info) {
        if (itemStack.getItem() instanceof Templated.Item) {
            info.setReturnValue("template." + Form.MODID + "." + Templated.get(itemStack).getTemplate());
        }
    }
}
