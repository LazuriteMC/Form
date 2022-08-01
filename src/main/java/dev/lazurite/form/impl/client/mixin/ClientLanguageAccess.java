package dev.lazurite.form.impl.client.mixin;

import net.minecraft.client.resources.language.ClientLanguage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Map;

@Mixin(ClientLanguage.class)
public interface ClientLanguageAccess {
    @Accessor Map<String, String> getStorage();

    static @Invoker("<init>") ClientLanguage newClientLanguage(Map<String, String> map, boolean bl) {
        throw new AssertionError();
    }
}
