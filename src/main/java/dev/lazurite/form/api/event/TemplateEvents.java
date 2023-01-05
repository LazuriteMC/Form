package dev.lazurite.form.api.event;

import dev.lazurite.form.impl.common.template.model.Template;
import dev.lazurite.toolbox.api.event.Event;
import net.minecraft.world.entity.Entity;

public class TemplateEvents {
    public static final Event<EntityTemplateChanged> ENTITY_TEMPLATE_CHANGED = Event.create();
    public static final Event<TemplateLoaded> TEMPLATE_LOADED = Event.create();

    public interface EntityTemplateChanged {
        void onEntityTemplateChanged(Entity entity);
    }

    public interface TemplateLoaded {
        void onTemplateLoaded(Template template);
    }
}
