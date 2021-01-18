package me.relavis.avatarcreatures.util.data;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class EntityData {

    @Getter
    @Setter
    int mountID;
    @Getter
    @Setter
    EntityType entityType;
    @Getter
    @Setter
    String entityName;
    @Getter
    @Setter
    UUID entityUUID;
    @Getter
    @Setter
    boolean entityAlive;

    public EntityData(UUID entityUUID) {
        this.entityUUID = entityUUID;
    }

    public void setEntityDisplayName(String entityName, boolean rename) {
        this.entityName = entityName;
        if (rename) {
            Bukkit.getEntity(entityUUID).setCustomName(entityName);
        }
    }

}
