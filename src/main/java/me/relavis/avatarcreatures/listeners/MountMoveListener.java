package me.relavis.avatarcreatures.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.relavis.avatarcreatures.util.ConfigHandler;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

public class MountMoveListener implements Listener {

    static ConfigHandler config = ConfigHandler.getInstance();


    public static void onMountEntitySteer(PacketEvent e) {
        if (e.getPacketType() == PacketType.Play.Client.STEER_VEHICLE && e.getPlayer().getVehicle() instanceof Ravager) {
            double movementSpeed = Double.parseDouble(config.getAppaConfig("movementSpeed"));
            PacketContainer packet = e.getPacket();
            Player player = e.getPlayer();
            Entity entity = player.getVehicle();

            Float sideways = packet.getFloat().read(0); // Left and right packet. Left is positive.
            Float forward = packet.getFloat().read(1); // Forwards/backwards packet. Forwards is positive.

            // Player location, direction, and eye location. Yaw is horizontal, pitch is vertical.
            Location playerLocation = player.getLocation();
            Vector playerDirection = playerLocation.getDirection();
            Location playerEye = player.getEyeLocation();
            float playerEyeYaw = playerEye.getYaw();
            float playerEyePitch = playerEye.getPitch();

            // Entity location, set entity's body and head rotation to match player's
            entity.setRotation(playerEyeYaw, playerEyePitch); //Set body and head rotation of entity to match mounted player
            entity.setFallDistance(0);

            if (forward > 0.0F) { // Forwards
                if (sideways > 0.0F) { // Move forwards and to the left
                    playerLocation.setYaw(playerEyeYaw + 45.0F);
                }

                if (sideways < 0.0F) { // Move forwards and to the right
                    playerLocation.setYaw(playerEyeYaw - 45.0F);
                }

                moveEntity(player, entity, movementSpeed, playerDirection);
            }

            if (forward < 0.0F) { // Backwards
                if (sideways > 0.0F) { // Move backwards and to the left
                    playerLocation.setYaw(playerLocation.getYaw() - 45.0F);
                } // TODO fix moving backwards

                if (sideways < 0.0F) { // Move backwards and to the right
                    playerLocation.setYaw(playerLocation.getYaw() + 45.0F);
                }

                moveEntity(player, entity, movementSpeed * -1.0D, playerDirection);
            }

            if (sideways > 0.0F) { // Strafe left
                playerLocation.setYaw(playerLocation.getYaw() - 90.0F);

                playerDirection = playerLocation.getDirection();
                moveEntity(player, entity, movementSpeed, playerDirection);
            }

            if (sideways < 0.0F) { // Strafe Right
                playerLocation.setYaw(playerLocation.getYaw() + 90.0F);

                playerDirection = playerLocation.getDirection();
                moveEntity(player, entity, movementSpeed, playerDirection);
            }

        }
    }

    public static void onMountEntityMove(PacketEvent e) {
        if (e.getPacketType() == PacketType.Play.Server.ENTITY_HEAD_ROTATION && e.getPlayer().getVehicle() instanceof Ravager) {
            PacketContainer packet = e.getPacket();
            Player player = e.getPlayer();
            Location playerEye = player.getEyeLocation();

            packet.getFloat()
                    .write(0, playerEye.getYaw())
                    .write(1, playerEye.getPitch());

            e.setPacket(packet);
        }
    }

    public static void moveEntity(Player player, Entity entity, double speed, Vector vector) {
        //Original method:
        //entity.setVelocity(vector.normalize().multiply(speed));

        Location playerEye = player.getEyeLocation();
        float playerEyeYaw = playerEye.getYaw();
        float playerEyePitch = playerEye.getPitch();

        double xAdd = vector.getX();
        double yAdd = vector.getY();
        double zAdd = vector.getZ();
        ((CraftEntity) entity).getHandle().setMot(xAdd, yAdd, zAdd);
        updateClientView(entity, player, playerEyeYaw, playerEyePitch);
    }

    public static void updateClientView(Entity entity, Player player, float playerEyeYaw, float playerEyePitch) {
        PacketContainer enforceRotation1 = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        enforceRotation1.getIntegers().writeSafely(0, entity.getEntityId());
        enforceRotation1.getBytes().writeSafely(0, (byte) (playerEyeYaw * 256F / 360F));

        PacketContainer enforceRotation2 = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_LOOK);
        enforceRotation2.getIntegers().writeSafely(0, entity.getEntityId());
        enforceRotation2.getBytes().writeSafely(0, (byte) (playerEyeYaw * 256F / 360F));
        enforceRotation2.getBytes().writeSafely(1, (byte) (playerEyePitch * 256F / 360F));
        try {
            ProtocolLibrary.getProtocolManager()
                    .sendServerPacket(player, enforceRotation1);
            ProtocolLibrary.getProtocolManager()
                    .sendServerPacket(player, enforceRotation2);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
