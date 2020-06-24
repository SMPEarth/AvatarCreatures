package me.relavis.avatarcreatures.util;

import org.bukkit.entity.EntityType;

import java.util.UUID;

public class PlayerData {
    ConfigHandler config = ConfigHandler.getInstance();
    UUID playerUUID;

    int appaID;
    String appaName;
    UUID appaUUID;
    boolean appaAlive;

    public PlayerData(UUID playerUUID) {
        this.playerUUID = playerUUID;

    }

    public int getEntityID(EntityType entityType) {
        switch(entityType) {
            case RAVAGER:
                return appaID;
            default:
                return 0;
        }
    }

    public String getEntityDisplayName(EntityType entityType) {
        switch(entityType) {
            case RAVAGER:
                return appaName;
            default:
                return null;
        }
    }

    public UUID getEntityUUID(EntityType entityType) {
        switch(entityType) {
            case RAVAGER:
                return appaUUID;
            default:
                return null;
        }
    }

    public boolean getEntityAlive(EntityType entityType) {
        switch(entityType) {
            case RAVAGER:
                return appaAlive;
            default:
                return false;
        }
    }

    public void setEntityID(EntityType entityType, int entityID) {
        switch(entityType) {
            case RAVAGER:
                appaID = entityID;
        }
    }

    public void setEntityDisplayName(EntityType entityType, String entityName) {
        switch(entityType) {
            case RAVAGER:
                appaName = entityName;
        }
    }

    public void setEntityUUID(EntityType entityType, UUID entityUUID) {
        switch(entityType) {
            case RAVAGER:
                appaUUID = entityUUID;
        }
    }

    public void setEntityAlive(EntityType entityType, boolean entityAlive) {
        switch(entityType) {
            case RAVAGER:
                appaAlive = entityAlive;
        }
    }
}
