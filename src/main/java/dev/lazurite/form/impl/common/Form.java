package dev.lazurite.form.impl.common;

import dev.lazurite.form.api.loader.TemplateLoader;
import dev.lazurite.form.impl.common.template.model.Template;
import dev.lazurite.toolbox.api.event.ServerEvents;
import dev.lazurite.toolbox.api.network.PacketRegistry;
import dev.lazurite.toolbox.api.network.ServerNetworking;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class Form implements ModInitializer {
    public static final String MODID = "form";
    public static final Logger LOGGER = LogManager.getLogger("Form");

    public static final ResourceLocation TEMPLATE = new ResourceLocation(MODID, "template");

    @Override
    public void onInitialize() {
        TemplateLoader.initialize();
        PacketRegistry.registerServerbound(TEMPLATE, this::onTemplateReceived);

        ServerEvents.Lifecycle.JOIN.register(this::onJoin);
    }

    protected void onJoin(ServerPlayer player) {
        TemplateLoader.getTemplates().forEach(template -> ServerNetworking.send(player, TEMPLATE, template::serialize));
    }

    protected void onTemplateReceived(PacketRegistry.ServerboundContext ctx) {
        final var player = ctx.player();
        final var buf = ctx.byteBuf();
        final var template = Template.deserialize(buf);

        Optional.ofNullable(player.getServer()).ifPresent(server -> {
            server.execute(() -> {
                PlayerLookup.all(server).forEach(p -> {
                    if (!p.equals(player)) {
                        ServerNetworking.send(p, TEMPLATE, template::serialize);
                    }
                });

                TemplateLoader.load(template);
            });
        });
    }
}
