package me.relavis.ravagersaddle.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.relavis.ravagersaddle.RavagerSaddle;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class MountMoveListener implements Listener {

    public static void onMountEntityMove(PacketEvent e) {
        if (e.getPacketType() == PacketType.Play.Client.STEER_VEHICLE) {

            PacketContainer packet = e.getPacket();
            Player player = e.getPlayer();
            Entity entity = player.getVehicle();
            if (entity == null)
                return;

            Boolean jump = packet.getBooleans().read(0); // Jump packet.
            Float sideways = packet.getFloat().read(0); // Left and right packet. Left is positive.
            Float forward = packet.getFloat().read(1); // Forwards/backwards packet. Forwards is positive.

            // Player location, direction, and eye location. Yaw is horizontal, pitch is vertical.
            Location playerLocation = player.getLocation();
            Vector playerDirection = playerLocation.getDirection();
            Location playerEye = player.getEyeLocation();
            Float playerEyeYaw = playerEye.getYaw();
            Float playerEyePitch = playerEye.getPitch();

            // Entity location, set entity's body and head rotation to match player's
            Location entityLocation = entity.getLocation();
            entity.setRotation(playerEyeYaw, playerEyePitch); //Set body and head rotation of entity to match mounted player

            entity.setFallDistance(0);

            // Jumping function
            if (jump && isOnGround(entity) && !RavagerSaddle.disablejump) {
                Jump(entity, player, playerDirection);
            }

            if (forward > 0.0F) { // Forwards
                if (sideways > 0.0F) { // Move forwards and to the left
                    playerLocation.setYaw(playerEyeYaw + 45.0F);
                }

                if (sideways < 0.0F) { // Move forwards and to the right
                    playerLocation.setYaw(playerEyeYaw - 45.0F);
                }

                MoveEntity(entity, player, RavagerSaddle.mspeed, playerDirection, playerEyeYaw, playerEyePitch);
            }

            if (forward < 0.0F) { // Backwards
                if (sideways > 0.0F) { // Move backwards and to the left
                    playerLocation.setYaw(playerLocation.getYaw() - 45.0F);
                }

                if (sideways < 0.0F) { // Move backwards and to the right
                    playerLocation.setYaw(playerLocation.getYaw() + 45.0F);
                }

                MoveEntity(entity, player, RavagerSaddle.mspeed * -1.0D, playerDirection, playerEyeYaw, playerEyePitch);
            }

            if (sideways > 0.0F) { // Strafe left
                playerLocation.setYaw(playerLocation.getYaw() - 90.0F);

                playerDirection = playerLocation.getDirection();
                MoveEntity(entity, player, RavagerSaddle.mspeed, playerDirection, playerEyeYaw, playerEyePitch);
            }

            if (sideways < 0.0F) { // Strafe Right
                playerLocation.setYaw(playerLocation.getYaw() + 90.0F);

                playerDirection = playerLocation.getDirection();
                MoveEntity(entity, player, RavagerSaddle.mspeed, playerDirection, playerEyeYaw, playerEyePitch);
            }

        }
    }

    public static boolean isOnGround(Entity entity) {
        double entityHeight = entity.getLocation().getY();
        double entityRemainder = entityHeight % (1.0/16.0);
        double entityYVelocity = entity.getVelocity().getY();

        return entityRemainder == 0; //&& entityYVelocity == 0;
    }

    public static void MoveEntity(Entity entity, Player p, Double speed, Vector vector, Float yaw, Float pitch) {
        entity.setVelocity(vector.normalize().multiply(speed));
        entity.setRotation(yaw, pitch);
        entity.getLocation().setYaw(yaw);
        entity.getLocation().setPitch(pitch);

    }

    public static void Jump(Entity entity, Player p, Vector vector) {
        // TODO Make sure jump always works
        Bukkit.broadcastMessage("Jump");
        entity.setVelocity(vector.normalize().setY(RavagerSaddle.mjumpspd));
        entity.setVelocity(vector.normalize().setX(0));
        entity.setVelocity(vector.normalize().setZ(0));
    }
}
