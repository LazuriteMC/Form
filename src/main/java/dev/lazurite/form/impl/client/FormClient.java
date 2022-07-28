package dev.lazurite.form.impl.client;

import dev.lazurite.form.impl.common.Form;
import dev.lazurite.form.impl.common.template.TemplateLoader;
import dev.lazurite.form.impl.common.template.model.Template;
import dev.lazurite.form.impl.common.template.util.TemplateResourceLoader;
import dev.lazurite.toolbox.api.network.PacketRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import software.bernie.geckolib3.GeckoLib;

import java.util.concurrent.CompletableFuture;

public class FormClient implements ClientModInitializer {
    public static final TemplateResourceLoader templateLoader = new TemplateResourceLoader();

    @Override
    public void onInitializeClient() {
        GeckoLib.initialize();
        PacketRegistry.registerClientbound(Form.TEMPLATE, this::onTemplateReceived);
    }

    protected void onTemplateReceived(PacketRegistry.ClientboundContext ctx) {
        final var client = Minecraft.getInstance();
        final var buf = ctx.byteBuf();
        final var template = Template.deserialize(buf);

        client.execute(() -> {
            TemplateLoader.load(template);
            CompletableFuture.runAsync(() -> FormClient.templateLoader.load(template));
        });
    }
}
