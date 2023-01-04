package dev.lazurite.form.impl.common.template.util;

import com.google.gson.JsonObject;
import dev.lazurite.form.impl.client.mixin.ClientLanguageAccess;
import dev.lazurite.form.impl.common.Form;
import dev.lazurite.form.api.loader.TemplateLoader;
import dev.lazurite.form.impl.common.template.model.Template;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.resources.language.ClientLanguage;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import software.bernie.geckolib.GeckoLibException;
import software.bernie.geckolib.cache.GeckoLibCache;
import software.bernie.geckolib.loading.json.FormatVersion;
import software.bernie.geckolib.loading.json.raw.Model;
import software.bernie.geckolib.loading.object.BakedAnimations;
import software.bernie.geckolib.loading.object.BakedModelFactory;
import software.bernie.geckolib.loading.object.GeometryTree;
import software.bernie.geckolib.util.JsonUtil;

import java.util.Map;

public class TemplateResourceLoader implements SimpleSynchronousResourceReloadListener {
    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(Form.MODID, "templates");
    }

    /**
     * For loading non-synchronously.
     * @param template the template to load
     */
    public void load(Template template) {
        final var identifier = new ResourceLocation(Form.MODID, template.getId());

        loadGeo(identifier, template.geo());
        loadAnimation(identifier, template.animation());
        loadLang(template.getId(), template.getName());
        Minecraft.getInstance().getTextureManager().register(identifier, new SimpleTexture(identifier));
    }

    /**
     * Loads the goe model from json into geckolib.
     * @param resourceLocation identifies the model
     * @param geoJson the geo model json string
     */
    private void loadGeo(ResourceLocation resourceLocation, JsonObject geoJson) {
        final var model = JsonUtil.GEO_GSON.fromJson(
                GsonHelper.fromJson(JsonUtil.GEO_GSON, geoJson.toString(), JsonObject.class),
                Model.class
        );

        if (model.formatVersion() != FormatVersion.V_1_12_0) {
            throw new GeckoLibException(resourceLocation, "Unsupported geometry json version. Supported versions: 1.12.0");
        }

        final var bakedModel = BakedModelFactory.getForNamespace(resourceLocation.getNamespace()).constructGeoModel(GeometryTree.fromModel(model));
        GeckoLibCache.getBakedModels().put(resourceLocation, bakedModel);
    }

    /**
     * Loads the animation from json into geckolib.
     * @param resourceLocation identifies the animation
     * @param animationJson the animation json string
     */
    private void loadAnimation(ResourceLocation resourceLocation, JsonObject animationJson) {
        final var bakedAnimation = JsonUtil.GEO_GSON.fromJson(GsonHelper.getAsJsonObject(
                GsonHelper.fromJson(JsonUtil.GEO_GSON, animationJson.toString(), JsonObject.class), "animations"),
                BakedAnimations.class
        );

        GeckoLibCache.getBakedAnimations().put(resourceLocation, bakedAnimation);
    }

    /**
     * Loads the name of the template into translation storage.
     * @param id the id of the template
     * @param name the actual name
     */
    private void loadLang(String id, String name) {
        if (Language.getInstance() instanceof ClientLanguage) {
            ((ClientLanguageAccess) Language.getInstance()).getStorage().put("template." + Form.MODID + "." + id, name);
        }
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        TemplateLoader.getTemplates().forEach(this::load);
    }
}
