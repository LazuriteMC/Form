package dev.lazurite.form.impl.common.template;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.lazurite.form.impl.common.Form;
import dev.lazurite.form.impl.common.template.model.Template;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This is the main housing for all of the quadcopter templates loaded
 * into the game. It handles loading from template zip files and contains
 * a map of all {@link Template} objects which can be referenced at any
 * time throughout execution. As such, templates aren't stored anywhere
 * except here.
 */
public class TemplateLoader {
    private static final List<String> templateFolders = new ArrayList<>();
    private static Map<String, Template> templates;

    public static void load(Template template) {
        String id = template.getId();
        Template loaded = templates.get(id);

        if (!templates.containsKey(id) || (templates.containsKey(id) && !loaded.equals(template))) {
            Form.LOGGER.info("Loading {} template...", template.getId());
            EnvType env = FabricLoader.getInstance().getEnvironmentType();
            templates.put(template.getId(), template);

            /*
             * Only add to inventory if the template originated on the server or your own client. Other
             * players' templates are not included in your inventory, but can be obtained from the player themself.
             */
            if (env == EnvType.CLIENT && template.getOriginDistance() < 2) {
//                ItemStack stack = new ItemStack(Form.QUADCOPTER_ITEM);
//                Templated.get(stack).ifPresent(state -> state.setTemplate(template.getId()));
//                ItemGroupHandler.getInstance().register(stack);
            }
        } else {
            Form.LOGGER.info("{} template already exists! Skipping...", template.getId());
        }
    }

    public static void addTemplateFolder(String folderName) {
        templateFolders.add(folderName);
    }

    public static void initialize() {
        templates = new ConcurrentHashMap<>();

        try {
            Form.LOGGER.info("Reading templates...");

            // Set up directories
            final var quadz = FabricLoader.getInstance().getGameDir().normalize().resolve(Paths.get("quadz"));
            final var jar = FabricLoader.getInstance().getModContainer("quadz").get().getRootPath().resolve("assets").resolve("quadz").resolve("templates");

            // Make the quadz folder if it doesn't exist
            if (!Files.exists(quadz)) {
                Files.createDirectories(quadz);
            }

            // Collect all paths and iterate
            final var templatePaths = new ArrayList<Path>();
            templatePaths.addAll(Files.walk(jar).filter(path -> path.getParent().equals(jar)).toList());
            templatePaths.addAll(Files.walk(quadz).filter(path -> path.getParent() == quadz).toList());

            for (Path path : templatePaths) {
                Template.Settings settings = null;
                JsonObject geo = null;
                JsonObject animation = null;
                byte[] texture = null;

                if (path.getFileName().toString().contains(".zip")) {
                    final var id = FilenameUtils.removeExtension(path.getName(path.getNameCount() - 1).toString());
                    final var zip = new ZipFile(path.toFile());
                    final var entries = zip.entries();

                    while (entries.hasMoreElements()) {
                        ZipEntry entry = entries.nextElement();

                        if (!entry.isDirectory()) {
                            /* Settings */
                            if (entry.getName().contains(id + ".settings.json")) {
                                settings = new Gson().fromJson(new InputStreamReader(zip.getInputStream(entry)), Template.Settings.class);

                            /* Geo Model */
                            } else if (entry.getName().contains(id + ".geo.json")) {
                                geo = new JsonParser().parse(new InputStreamReader(zip.getInputStream(entry))).getAsJsonObject();

                            /* Animation */
                            } else if (entry.getName().contains(id + ".animation.json")) {
                                animation = new JsonParser().parse(new InputStreamReader(zip.getInputStream(entry))).getAsJsonObject();

                            /* Texture */
                            } else if (entry.getName().contains(id + ".png")) {
                                InputStream stream = zip.getInputStream(entry);
                                texture = new byte[stream.available()];
                                stream.read(texture);
                            }
                        }
                    }

                    try {
                        load(new Template(settings, geo, animation, texture, 0));
                    } catch (NoSuchElementException e) {
                        Form.LOGGER.error("Error reading from directory: " + path, e);
                    }
                } else if (Files.isDirectory(path)) {
                    String id = path.getFileName().toString().replace("/", "");
                    List<Path> files = Files.walk(path).collect(Collectors.toList());

                    for (Path file : files) {
                        /* Settings */
                        if (file.toString().contains(id + ".settings.json")) {
                            settings = new Gson().fromJson(new InputStreamReader(Files.newInputStream(file)), Template.Settings.class);

                        /* Geo Model */
                        } else if (file.toString().contains(id + ".geo.json")) {
                            geo = new JsonParser().parse(new InputStreamReader(Files.newInputStream(file))).getAsJsonObject();

                        /* Animation */
                        } else if (file.toString().contains(id + ".animation.json")) {
                            animation = new JsonParser().parse(new InputStreamReader(Files.newInputStream(file))).getAsJsonObject();

                        /* Texture */
                        } else if (file.toString().contains(id + ".png")) {
                            InputStream stream = Files.newInputStream(file);
                            texture = new byte[stream.available()];
                            stream.read(texture);
                        }
                    }

                    try {
                        load(new Template(settings, geo, animation, texture, 0));
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

    public static Optional<Template> getTemplate(String id) {
        return Optional.ofNullable(templates.get(id));
    }

    public static List<Template> getTemplates() {
        return new ArrayList<>(templates.values());
    }

    /**
     * Removes any templates that are from anywhere besides this instance of the game.
     */
    public static void clearRemoteTemplates() {
        templates.values().removeIf(template -> template.getOriginDistance() > 0);
    }
}
