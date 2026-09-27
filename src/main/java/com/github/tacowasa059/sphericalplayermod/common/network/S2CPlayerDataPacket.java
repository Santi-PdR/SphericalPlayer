package com.github.tacowasa059.sphericalplayermod.common.network;

import com.github.tacowasa059.sphericalplayermod.common.Interface.ICustomPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import org.joml.Quaternionf;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

public class S2CPlayerDataPacket {
    private final float size;
    private final boolean flag;
    private final float restitution;
    private final Quaternionf quaternion;
    private final Vec3 currentPosition;

    public S2CPlayerDataPacket(float size, boolean flag, float restitution, @Nonnull Quaternionf quaternion, @Nonnull Vec3 currentPosition) {
        this.size = size;
        this.flag = flag;
        this.restitution = restitution;
        this.quaternion = quaternion;
        this.currentPosition = currentPosition;
    }

    public static void encode(S2CPlayerDataPacket packet, FriendlyByteBuf buf) {
        buf.writeFloat(packet.size);
        buf.writeBoolean(packet.flag);
        buf.writeFloat(packet.restitution);
        buf.writeFloat(packet.quaternion.x);
        buf.writeFloat(packet.quaternion.y);
        buf.writeFloat(packet.quaternion.z);
        buf.writeFloat(packet.quaternion.w);
        buf.writeDouble(packet.currentPosition.x);
        buf.writeDouble(packet.currentPosition.y);
        buf.writeDouble(packet.currentPosition.z);
    }

    public static S2CPlayerDataPacket decode(FriendlyByteBuf buf) {
        float size = buf.readFloat();
        boolean flag = buf.readBoolean();
        float restitution = buf.readFloat();
        float x = buf.readFloat();
        float y = buf.readFloat();
        float z = buf.readFloat();
        float w = buf.readFloat();
        Quaternionf quaternion = new Quaternionf(x, y, z, w);
        Vec3 pos = new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble());
        return new S2CPlayerDataPacket(size, flag, restitution, quaternion, pos);
    }

    public static void handle(S2CPlayerDataPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> applyToClientPlayer(packet));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void applyToClientPlayer(S2CPlayerDataPacket packet) {
        Player player = Minecraft.getInstance().player;
        if (player instanceof ICustomPlayerData data) {
            data.sphericalPlayerMod$setSize(packet.size);
            data.sphericalPlayerMod$setFlag(packet.flag);
            data.sphericalPlayerMod$setRestitutionCoefficient(packet.restitution);

            data.sphericalPlayerMod$setQuaternion(packet.quaternion);
            data.sphericalPlayerMod$setCurrentPosition(packet.currentPosition);

            player.setPos(player.position());
        }
    }
}