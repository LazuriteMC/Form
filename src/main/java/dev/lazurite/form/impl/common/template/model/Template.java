package dev.lazurite.form.impl.common.template.model;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;

import java.util.Arrays;

/**
 * Represents a "template" for an entity containing a geo model, animation, texture, and any additional metadata
 * the user wishes to provide for their mod. Origin distance is a number between 0 and 2 that signifies the number
 * of jumps between servers + clients the given template has made (e.g. a template loaded on the local client would
 * have a value of 0, whereas a template loaded on another client, passed off to the server, and then passed off to
 * your local client would have a value of 2).
 * @param metadata
 * @param geo
 * @param animation
 * @param texture
 * @param originDistance
 */
public record Template(JsonObject metadata, JsonObject geo, JsonObject animation, byte[] texture, int originDistance) {
    public Template {
        if (metadata == null) {
            throw new RuntimeException("Missing or corrupted metadata file");
        } else if (geo == null) {
            throw new RuntimeException("Missing or corrupted geo model file in " + metadata.get("id").getAsString());
        } else if (animation == null) {
            throw new RuntimeException("Missing or corrupted animation file in " + metadata.get("id").getAsString());
        } else if (texture == null) {
            throw new RuntimeException("Missing or corrupted texture file in " + metadata.get("id").getAsString());
        }

    }

    public void serialize(FriendlyByteBuf buf) {
        buf.writeUtf(metadata.toString());
        buf.writeUtf(geo.toString());
        buf.writeUtf(animation.toString());
        buf.writeByteArray(texture);
        buf.writeInt(originDistance + 1);
    }

    public static Template deserialize(FriendlyByteBuf buf) {
        return new Template(
                JsonParser.parseString(buf.readUtf(32767)).getAsJsonObject(),
                JsonParser.parseString(buf.readUtf(32767)).getAsJsonObject(),
                JsonParser.parseString(buf.readUtf(32767)).getAsJsonObject(),
                buf.readByteArray(),
                buf.readInt());
    }

    public String getId() {
        return this.metadata.get("id").getAsString();
    }

    public String getName() {
        return this.metadata.get("name").getAsString();
    }

    public String getAuthor() {
        return this.metadata.get("author").getAsString();
    }

    public String getModId() {
        return this.metadata.get("modid").getAsString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Template template) {
            return template.metadata.equals(metadata) &&
                    template.animation.equals(animation) &&
                    template.geo.equals(geo) &&
                    Arrays.equals(template.texture, texture);
        }

        return false;
    }
}
