package dev.lazurite.form.impl.client.resource;

import dev.lazurite.form.impl.common.template.TemplateLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TemplateTextureManager implements ResourceManager {
    private final ResourceManager original;

    public TemplateTextureManager(ResourceManager original) {
        this.original = original;
    }

    @Override
    public Optional<Resource> getResource(ResourceLocation resourceLocation) {
        if (resourceLocation.getPath().endsWith("_n.png") || resourceLocation.getPath().endsWith("_s.png")) {
            return getOriginal().getResource(resourceLocation);
        }

        final var optionalTemplate = TemplateLoader.getTemplate(resourceLocation.getPath());

        if (optionalTemplate.isPresent()) {
            final var template = optionalTemplate.get();
            return Optional.of(new Resource(template.getId(), () -> new ByteArrayInputStream(template.getTexture())));
        } else {
            return original.getResource(resourceLocation);
        }
    }

    // We don't use anything below here...

    @Override
    public Set<String> getNamespaces() {
        return new HashSet<>();
    }

    @Override
    public List<Resource> getResourceStack(ResourceLocation resourceLocation) {
        return new ArrayList<>();
    }

    @Override
    public Map<ResourceLocation, Resource> listResources(String resourceType, Predicate<ResourceLocation> pathPredicate) {
        return new HashMap<>();
    }

    @Override
    public Map<ResourceLocation, List<Resource>> listResourceStacks(String string, Predicate<ResourceLocation> predicate) {
        return new HashMap<>();
    }

    @Override
    public Stream<PackResources> listPacks() {
        return new ArrayList<PackResources>().stream();
    }

    public ResourceManager getOriginal() {
        return this.original;
    }
}