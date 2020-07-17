package me.relavis.avatarcreatures.util.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.util.UUID;

public class EntityData {
    int mountID;
    EntityType entityType;
    String entityName;
    UUID entityUUID;
    boolean entityAlive;

    public EntityData(UUID entityUUID) {
        this.entityUUID = entityUUID;

    }

    public int getMountID() {
        return mountID;
    }

    public void setMountID(int mountID) {
        this.mountID = mountID;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

    public String getEntityDisplayName() {
        return entityName;
    }

    public void setEntityDisplayName(String entityName, boolean rename) {
        this.entityName = entityName;
        if (rename) {
            Bukkit.getEntity(entityUUID).setCustomName(entityName);
        }
    }

    public UUID getEntityUUID() {
        return entityUUID;
    }

    public void setEntityUUID(UUID entityUUID) {
        this.entityUUID = entityUUID;
    }

    public boolean getEntityAlive() {
        return entityAlive;
    }

    public void setEntityAlive(boolean entityAlive) {
        this.entityAlive = entityAlive;
        Bukkit.broadcastMessage("Setting alive to " + entityAlive + " in EntityData.java");
    }
}
