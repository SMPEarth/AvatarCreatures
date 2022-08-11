package me.relavis.avatarcreatures.events;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.relavis.avatarcreatures.util.ConfigHandler;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class EntityMove implements Listener {
    static final ConfigHandler config = ConfigHandler.getInstance();

    public static void onMountEntitySteer(PacketEvent e) {
        if (e.getPacketType() == PacketType.Play.Client.STEER_VEHICLE && e.getPlayer().getVehicle() instanceof Ravager) {
            double movementSpeed = config.getAppaMovementSpeed();
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

    public static void moveEntity(Player player, Entity entity, double speed, Vector vector) {
        entity.setVelocity(vector.normalize().multiply(speed));
    }
}
