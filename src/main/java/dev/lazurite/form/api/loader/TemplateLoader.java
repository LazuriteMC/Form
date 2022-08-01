package dev.lazurite.form.api.loader;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lazurite.form.api.Templated;
import dev.lazurite.form.impl.common.Form;
import dev.lazurite.form.impl.common.template.model.Template;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipFile;

/**
 * This is the main housing for all of the quadcopter templates loaded
 * into the game. It handles loading from template zip files and contains
 * a map of all {@link Template} objects which can be referenced at any
 * time throughout execution. As such, templates aren't stored anywhere
 * except here.
 */
public interface TemplateLoader {
    Map<ResourceLocation, Template> TEMPLATES = new ConcurrentHashMap<>();

    static ItemStack getItemStackFor(Template template, Item item) {
        final var env = FabricLoader.getInstance().getEnvironmentType();

        /*
         * Only add to inventory if the template originated on the server or your own client. Other
         * players' templates are not included in your inventory, but can be obtained from the player themself.
         */
        if (env == EnvType.CLIENT && template.originDistance() < 2) {
            final var stack = new ItemStack(item);
            Templated.get(stack).setTemplate(template.getId());
            return stack;
        }

        return null;
    }

    static void load(Template template) {
        final var id = template.getId();
        final var loaded = TEMPLATES.get(id);

        if (!TEMPLATES.containsKey(id) || (TEMPLATES.containsKey(id) && !loaded.equals(template))) {
            Form.LOGGER.info("Loading {} template...", template.getId());
            TEMPLATES.put(new ResourceLocation(template.getModId(), template.getId()), template);
        } else {
            Form.LOGGER.info("{} template already exists! Skipping...", template.getId());
        }
    }

    static void initialize(String modid) {
        FabricLoader.getInstance().getModContainer(modid).ifPresent(container -> {
            final var jar = container.getRootPaths().get(0).resolve("assets").resolve(modid).resolve("templates");

            try {
                initialize(new ArrayList<>(Files.walk(jar).filter(path -> path.getParent().equals(jar)).toList()));
            } catch(IOException e) {
                Form.LOGGER.error(e);
                throw new RuntimeException("Unable to load from internal jar: " + e);
            }
        });
    }

    static void initialize() {
        final var templates = FabricLoader.getInstance().getGameDir().normalize().resolve(Paths.get("templates"));

        try {
            if (!Files.exists(templates)) {
                Files.createDirectories(templates);
            }

            initialize(new ArrayList<>(Files.walk(templates).filter(path -> path.getParent() == templates).toList()));
        } catch (IOException e) {
            Form.LOGGER.error(e);
            throw new RuntimeException("Unable to load from templates/ directory: " + e);
        }
    }

    static void initialize(List<Path> paths) {
        try {
            Form.LOGGER.info("Reading templates...");

            for (Path path : paths) {
                JsonObject metadata = null;
                JsonObject geo = null;
                JsonObject animation = null;
                byte[] texture = null;

                if (path.getFileName().toString().contains(".zip")) {
                    final var id = FilenameUtils.removeExtension(path.getName(path.getNameCount() - 1).toString());
                    final var zip = new ZipFile(path.toFile());
                    final var entries = zip.entries();

                    while (entries.hasMoreElements()) {
                        final var entry = entries.nextElement();

                        if (!entry.isDirectory()) {
                            /* Settings */
                            if (entry.getName().contains(id + ".settings.json")) {
                                metadata = JsonParser.parseReader(new InputStreamReader(zip.getInputStream(entry))).getAsJsonObject();

                            /* Geo Model */
                            } else if (entry.getName().contains(id + ".geo.json")) {
                                geo = JsonParser.parseReader(new InputStreamReader(zip.getInputStream(entry))).getAsJsonObject();

                            /* Animation */
                            } else if (entry.getName().contains(id + ".animation.json")) {
                                animation = JsonParser.parseReader(new InputStreamReader(zip.getInputStream(entry))).getAsJsonObject();

                            /* Texture */
                            } else if (entry.getName().contains(id + ".png")) {
                                final var stream = zip.getInputStream(entry);
                                texture = new byte[stream.available()];
                                stream.read(texture);
                            }
                        }
                    }

                    try {
                        load(new Template(metadata, geo, animation, texture, 0));
                    } catch (NoSuchElementException e) {
                        Form.LOGGER.error("Error reading from directory: " + path, e);
                    }
                } else if (Files.isDirectory(path)) {
                    String id = path.getFileName().toString().replace("/", "");
                    List<Path> files = Files.walk(path).toList();

                    for (var file : files) {
                        /* Settings */
                        if (file.toString().contains(id + ".settings.json")) {
                            metadata = JsonParser.parseReader(new InputStreamReader(Files.newInputStream(file))).getAsJsonObject();

                        /* Geo Model */
                        } else if (file.toString().contains(id + ".geo.json")) {
                            geo = JsonParser.parseReader(new InputStreamReader(Files.newInputStream(file))).getAsJsonObject();

                        /* Animation */
                        } else if (file.toString().contains(id + ".animation.json")) {
                            animation = JsonParser.parseReader(new InputStreamReader(Files.newInputStream(file))).getAsJsonObject();

                        /* Texture */
                        } else if (file.toString().contains(id + ".png")) {
                            final var stream = Files.newInputStream(file);
                            texture = new byte[stream.available()];
                            stream.read(texture);
                        }
                    }

                    try {
                        load(new Template(metadata, geo, animation, texture, 0));
                    } catch (NoSuchElementException e) {
                        Form.LOGGER.error("Error reading from directory: " + path, e);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load templates.");
        } finally {
            Form.LOGGER.info("Done!");
        }
    }

    static Optional<Template> getTemplateById(String templateId) {
        return TEMPLATES.values().stream().filter(template -> template.getId().equals(templateId)).findFirst();
    }

    static List<Template> getTemplateByModId(String modid) {
        return TEMPLATES.keySet().stream()
                .filter(resourceLocation -> resourceLocation.getNamespace().equals(modid))
                .map(TEMPLATES::get).toList();
    }

    static List<Template> getTemplates() {
        return new ArrayList<>(TEMPLATES.values());
    }

    /**
     * Removes any templates that are from anywhere besides this instance of the game.
     */
    static void clearRemoteTemplates() {
        TEMPLATES.values().removeIf(template -> template.originDistance() > 0);
    }
}
