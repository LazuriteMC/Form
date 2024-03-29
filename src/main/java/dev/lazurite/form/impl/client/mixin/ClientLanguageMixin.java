package dev.lazurite.form.impl.client.mixin;

import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import org.spongepowered.include.com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;

/**
 * The original call to {@link ClientLanguage#ClientLanguage(Map, boolean)} wraps the
 * map to an {@link ImmutableMap}. Here, we prevent that from happening by passing it normally.
 */
@Mixin(ClientLanguage.class)
public class ClientLanguageMixin {
    @Inject(
            method = "loadFrom",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private static void loadFrom(ResourceManager resourceManager, List<String> list, boolean bl, CallbackInfoReturnable<ClientLanguage> cir, Map<String, String> map) {
        cir.setReturnValue(ClientLanguageAccess.newClientLanguage(map, bl));
    }
}
