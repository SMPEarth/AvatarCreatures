package me.relavis.avatarcreatures.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.relavis.avatarcreatures.AvatarCreatures;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

public class MountMoveListener implements Listener {

    public static void onMountEntitySteer(PacketEvent e) {
        if (e.getPacketType() == PacketType.Play.Client.STEER_VEHICLE && e.getPlayer().getVehicle() instanceof Ravager) {

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

            if (forward > 0.0F) { // Forwards
                if (sideways > 0.0F) { // Move forwards and to the left
                    playerLocation.setYaw(playerEyeYaw + 45.0F);
                }

                if (sideways < 0.0F) { // Move forwards and to the right
                    playerLocation.setYaw(playerEyeYaw - 45.0F);
                }

                MoveEntity(player, entity, AvatarCreatures.movementSpeed, playerDirection);
            }

            if (forward < 0.0F) { // Backwards
                if (sideways > 0.0F) { // Move backwards and to the left
                    playerLocation.setYaw(playerLocation.getYaw() - 45.0F);
                }

                if (sideways < 0.0F) { // Move backwards and to the right
                    playerLocation.setYaw(playerLocation.getYaw() + 45.0F);
                }

                MoveEntity(player, entity, AvatarCreatures.movementSpeed * -1.0D, playerDirection);
            }

            if (sideways > 0.0F) { // Strafe left
                playerLocation.setYaw(playerLocation.getYaw() - 90.0F);

                playerDirection = playerLocation.getDirection();
                MoveEntity(player, entity, AvatarCreatures.movementSpeed, playerDirection);
            }

            if (sideways < 0.0F) { // Strafe Right
                playerLocation.setYaw(playerLocation.getYaw() + 90.0F);

                playerDirection = playerLocation.getDirection();
                MoveEntity(player, entity, AvatarCreatures.movementSpeed, playerDirection);
            }

        }
    }

    public static void onMountEntityMove(PacketEvent e) {
        if (e.getPacketType() == PacketType.Play.Client.VEHICLE_MOVE && e.getPlayer().getVehicle() instanceof Ravager) {
            PacketContainer packet = e.getPacket();
            Player player = e.getPlayer();
            Location playerEye = player.getEyeLocation();

            packet.getFloat()
                    .write(0, playerEye.getYaw())
                    .write(1, playerEye.getPitch());

            e.setPacket(packet);
/*
            int entityId = e.getPlayer().getVehicle().getEntityId();
            PacketPlayOutEntityVelocity packet2 = new PacketPlayOutEntityVelocity(entityId, new Vec3D(0, 0.5, 0) );
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet2);
  */
        }
    }

    public static void MoveEntity(Player player, Entity entity, double speed, Vector vector) {
        //entity.setVelocity(vector.normalize().multiply(speed));

        //Bukkit.broadcastMessage(vector.toString());
        Location playerEye = player.getEyeLocation();
        float playerEyeYaw = playerEye.getYaw();
        float playerEyePitch = playerEye.getPitch();
        double x = entity.getLocation().getX();
        double y = entity.getLocation().getY();
        double z = entity.getLocation().getZ();

        double xAdd = vector.getX();
        double yAdd = vector.getY();
        double zAdd = vector.getZ();
        //(EntityInsentient) ((CraftEntity) entity).setRotation(playerEyeYaw, playerEyePitch);
        ((CraftEntity) entity).getHandle().setMot(xAdd, yAdd, zAdd);
        entity.setRotation(playerEyeYaw, playerEyePitch);
    }
}
