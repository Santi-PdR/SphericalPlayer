package com.github.tacowasa059.sphericalplayermod.common.network;

import com.github.tacowasa059.sphericalplayermod.SphericalPlayerMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.joml.Quaternionf;

import java.util.Optional;

public class ModNetworking {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(SphericalPlayerMod.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(packetId++, S2CPlayerDataPacket.class, S2CPlayerDataPacket::encode,
                S2CPlayerDataPacket::decode, S2CPlayerDataPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void sendInitialData(ServerPlayer player,
                                       float size, boolean flag, float restitution,
                                       Quaternionf quaternion, Vec3 currentPos) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new S2CPlayerDataPacket(size, flag, restitution, quaternion, currentPos));
    }

}
