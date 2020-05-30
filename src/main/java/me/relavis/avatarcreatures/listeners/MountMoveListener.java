package me.relavis.avatarcreatures.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import me.relavis.avatarcreatures.AvatarCreatures;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PacketPlayOutEntityHeadRotation;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Ravager;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.lang.reflect.InvocationTargetException;

public class MountMoveListener implements Listener {
    AvatarCreatures avatarCreatures = new AvatarCreatures();
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

    public static void MoveEntity(Player player, Entity entity, double speed, Vector vector) {
        //Original method:
        //entity.setVelocity(vector.normalize().multiply(speed));

        Location playerEye = player.getEyeLocation();
        float playerEyeYaw = playerEye.getYaw();
        float playerEyePitch = playerEye.getPitch();

        double xAdd = vector.getX();
        double yAdd = vector.getY();
        double zAdd = vector.getZ();
        ((CraftEntity) entity).getHandle().setMot(xAdd, yAdd, zAdd);
        entity.setRotation(playerEyeYaw, playerEyePitch);
        UpdateClientView(entity, player, playerEyeYaw);
    }

    public static void UpdateClientView(Entity entity, Player player, float playerEyeYaw) {
        PacketContainer packet = new PacketContainer(
                PacketType.Play.Server.ENTITY_HEAD_ROTATION);
        int entityID = entity.getEntityId();
        packet.getIntegers()
                .write(0, entityID);
        packet.getFloat()
                .write(1, playerEyeYaw);
        try {
            ProtocolLibrary.getProtocolManager()
                    .sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
