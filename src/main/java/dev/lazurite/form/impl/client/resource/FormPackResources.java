package dev.lazurite.form.impl.client.resource;

import dev.lazurite.form.impl.common.Form;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.resources.IoSupplier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FormPackResources implements PackResources {
    public Map<ResourceLocation, InputStream> streams;

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... strings) {
        return () -> this.streams.get(new ResourceLocation(strings[0], strings[1]));
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation resourceLocation) {
        return () -> this.streams.get(resourceLocation);
    }

    @Override
    public void listResources(PackType packType, String string, String string2, ResourceOutput resourceOutput) {

    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        var set = new HashSet<String>();
        set.add(Form.MODID);
        return set;
    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> metadataSectionSerializer) throws IOException {
        return null;
    }

    @Override
    public String packId() {
        return Form.MODID;
    }

    @Override
    public void close() {
        this.streams.forEach((resourceLocation, inputStream) -> {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
