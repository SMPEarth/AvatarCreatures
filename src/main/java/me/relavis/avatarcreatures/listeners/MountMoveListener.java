package me.relavis.avatarcreatures.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.relavis.avatarcreatures.AvatarCreatures;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class MountMoveListener implements Listener {

    public static void onMountEntityMove(PacketEvent e) {
        if (e.getPacketType() == PacketType.Play.Client.STEER_VEHICLE && e.getPlayer().getVehicle() != null) {

            PacketContainer packet = e.getPacket();
            Player player = e.getPlayer();
            Entity entity = player.getVehicle();

            Boolean jump = packet.getBooleans().read(0); // Jump packet.
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



            // Jumping function
            if (jump && isOnGround(entity) && !AvatarCreatures.disableJump) {
                Jump(entity, player, playerDirection);
            }

            if (forward > 0.0F) { // Forwards
                if (sideways > 0.0F) { // Move forwards and to the left
                    playerLocation.setYaw(playerEyeYaw + 45.0F);
                }

                if (sideways < 0.0F) { // Move forwards and to the right
                    playerLocation.setYaw(playerEyeYaw - 45.0F);
                }

                MoveEntity(entity, player, AvatarCreatures.movementSpeed, playerDirection, playerEyeYaw, playerEyePitch);
                entity.setRotation(playerEyeYaw, playerEyePitch);
            }

            if (forward < 0.0F) { // Backwards
                if (sideways > 0.0F) { // Move backwards and to the left
                    playerLocation.setYaw(playerLocation.getYaw() - 45.0F);
                }

                if (sideways < 0.0F) { // Move backwards and to the right
                    playerLocation.setYaw(playerLocation.getYaw() + 45.0F);
                }

                MoveEntity(entity, player, AvatarCreatures.movementSpeed * -1.0D, playerDirection, playerEyeYaw, playerEyePitch);
                entity.setRotation(playerEyeYaw, playerEyePitch);
            }

            if (sideways > 0.0F) { // Strafe left
                playerLocation.setYaw(playerLocation.getYaw() - 90.0F);

                playerDirection = playerLocation.getDirection();
                MoveEntity(entity, player, AvatarCreatures.movementSpeed, playerDirection, playerEyeYaw, playerEyePitch);
                entity.setRotation(playerEyeYaw, playerEyePitch);
            }

            if (sideways < 0.0F) { // Strafe Right
                playerLocation.setYaw(playerLocation.getYaw() + 90.0F);

                playerDirection = playerLocation.getDirection();
                MoveEntity(entity, player, AvatarCreatures.movementSpeed, playerDirection, playerEyeYaw, playerEyePitch);
                entity.setRotation(playerEyeYaw, playerEyePitch);
            }

        }
    }

    public static boolean isOnGround(Entity entity) {
        double entityHeight = entity.getLocation().getY();
        double entityRemainder = entityHeight % (1.0/16.0);
        //double entityYVelocity = entity.getVelocity().getY();

        return entityRemainder == 0; //&& entityYVelocity == 0;
    }

    public static void MoveEntity(Entity entity, Player p, Double speed, Vector vector, Float yaw, Float pitch) {
            entity.setVelocity(vector.normalize().multiply(speed));
    }
    public static void autoJump(Entity entity) {
        entity.setVelocity(entity.getVelocity().add(entity.getLocation().getDirection())); // moving forward
        entity.setVelocity(entity.getVelocity().add(new Vector(0, 0.5, 0)));
    }

    public static void Jump(Entity entity, Player p, Vector vector) {
        // TODO Make jump more realistic
        entity.setVelocity(entity.getVelocity().add(entity.getVelocity())); // moving forward
        entity.setVelocity(entity.getVelocity().setY(1));

    }
}
