package me.relavis.avatarcreatures.util.data;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData {
    private final HashMap<UUID, EntityData> entityData = new HashMap<>();
    UUID playerUUID;

    public PlayerData(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public HashMap<UUID, EntityData> getEntityData() {
        return entityData;
    }

    public boolean ownsEntity(UUID entityUUID) {
        return entityData.containsKey(entityUUID);
    }

    public String getEntityDisplayName(UUID entityUUID) {
        return entityData.get(entityUUID).getEntityDisplayName();
    }

    @Deprecated
    public String getEntityDisplayName(EntityType entityType) {
        for (Map.Entry<UUID, EntityData> pair : entityData.entrySet()) {
            if (entityData.get(pair.getKey()).getEntityType() == entityType) {
                return entityData.get(pair.getKey()).getEntityDisplayName();
            }
        }
        return null;
    }

    @Deprecated
    public UUID getEntityUUID(EntityType entityType) {
        for (Map.Entry<UUID, EntityData> pair : entityData.entrySet()) {
            if (entityData.get(pair.getKey()).getEntityType() == entityType) {
                return pair.getKey();
            }
        }
        return null;
    }

    public boolean getEntityAlive(UUID entityUUID) {
        return entityData.get(entityUUID).getEntityAlive();
    }

    @Deprecated
    public boolean getEntityAlive(EntityType entityType) {
        for (Map.Entry<UUID, EntityData> pair : entityData.entrySet()) {
            if (entityData.get(pair.getKey()).getEntityType() == entityType) {
                return entityData.get(pair.getKey()).getEntityAlive();
            }
        }
        return false;
    }

    public boolean getEntityExists(UUID entityUUID) {
        return entityData.containsKey(entityUUID);
    }

    @Deprecated
    public boolean getEntityExists(EntityType entityType) {
        for (Map.Entry<UUID, EntityData> pair : entityData.entrySet()) {
            Bukkit.getLogger().info(pair.toString());
            if (entityData.get(pair.getKey()).getEntityType() == entityType) {
                return true;
            }
        }
        return false;
    }

    public void setEntityDisplayName(UUID entityUUID, String entityName, boolean rename) {
        entityData.get(entityUUID).setEntityDisplayName(entityName, rename);
    }

    @Deprecated
    public void setEntityDisplayName(EntityType entityType, String entityName, boolean rename) {
        for (Map.Entry<UUID, EntityData> pair : entityData.entrySet()) {
            if (entityData.get(pair.getKey()).getEntityType() == entityType) {
                entityData.get(pair.getKey()).setEntityDisplayName(entityName, rename);
            }
        }
    }

    public void setEntityUUID(UUID entityUUID, UUID newEntityUUID) {
        EntityData data = entityData.get(entityUUID);
        entityData.remove(entityUUID);

        data.setEntityUUID(newEntityUUID);
        entityData.put(newEntityUUID, data);
    }

    @Deprecated
    public void setEntityUUID(EntityType entityType, UUID newEntityUUID) {
        for (Map.Entry<UUID, EntityData> pair : entityData.entrySet()) {
            if (entityData.get(pair.getKey()).getEntityType() == entityType) {
                EntityData data = entityData.get(pair.getKey());
                entityData.remove(pair.getKey());

                data.setEntityUUID(newEntityUUID);
                entityData.put(newEntityUUID, data);
            }
        }
    }

    public EntityType getEntityType(UUID entityUUID) {
        return entityData.get(entityUUID).getEntityType();
    }

    public void setEntityAlive(UUID entityUUID, boolean entityAlive) {
        entityData.get(entityUUID).setEntityAlive(entityAlive);
        Bukkit.getLogger().info("Setting alive to " + entityAlive + " in PlayerData.java");
    }

    public int getMountID(UUID entityUUID) {
        return entityData.get(entityUUID).getMountID();
    }

    public void setMountID(UUID entityUUID, int mountID) {
        entityData.get(entityUUID).setMountID(mountID);
    }

    public void deleteEntity(UUID entityUUID) {
        entityData.remove(entityUUID);
    }

    public void initializeEntity(int mountID, EntityType entityType, String entityName, UUID entityUUID, boolean entityAlive) {
        EntityData data = new EntityData(entityUUID);
        data.setMountID(mountID);
        data.setEntityType(entityType);
        data.setEntityDisplayName(entityName, false);
        data.setEntityUUID(entityUUID);
        data.setEntityAlive(entityAlive);
        data.setEntityAlive(entityAlive);
        entityData.put(entityUUID, data);

        Bukkit.getLogger().info(String.valueOf(mountID));
        Bukkit.getLogger().info(String.valueOf(entityType));
        Bukkit.getLogger().info(String.valueOf(entityName));
        Bukkit.getLogger().info(String.valueOf(entityUUID));
        Bukkit.getLogger().info(String.valueOf(entityAlive));
    }
}
