package dev.lazurite.form.impl.client;

import dev.lazurite.form.impl.common.Form;
import dev.lazurite.form.api.loader.TemplateLoader;
import dev.lazurite.form.impl.common.template.model.Template;
import dev.lazurite.form.impl.common.template.util.TemplateResourceLoader;
import dev.lazurite.toolbox.api.event.ClientEvents;
import dev.lazurite.toolbox.api.network.ClientNetworking;
import dev.lazurite.toolbox.api.network.PacketRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.packs.PackType;
import software.bernie.geckolib.GeckoLib;

import java.util.concurrent.CompletableFuture;

public class FormClient implements ClientModInitializer {
    public static final TemplateResourceLoader templateLoader = new TemplateResourceLoader();

    @Override
    public void onInitializeClient() {
        GeckoLib.initialize();
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(templateLoader);

        PacketRegistry.registerClientbound(Form.TEMPLATE, this::onTemplateReceived);
        ClientEvents.Lifecycle.DISCONNECT.register(this::onDisconnect);
        ClientEvents.Lifecycle.POST_LOGIN.register(this::onPostLogin);
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

    protected void onDisconnect(Minecraft minecraft, ClientLevel level) {
        TemplateLoader.clearRemoteTemplates();
    }

    protected void onPostLogin(Minecraft minecraft, ClientLevel level, LocalPlayer player) {
        TemplateLoader.getTemplates().forEach(template -> ClientNetworking.send(Form.TEMPLATE, template::serialize));
    }
}
