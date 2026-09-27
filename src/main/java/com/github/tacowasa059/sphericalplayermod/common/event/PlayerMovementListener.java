package com.github.tacowasa059.sphericalplayermod.common.event;

import com.github.tacowasa059.sphericalplayermod.common.Interface.ICustomPlayerData;
import com.github.tacowasa059.sphericalplayermod.SphericalPlayerMod;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SphericalPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerMovementListener {
    // only in server side
    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof Player player) {
            ICustomPlayerData playerData = (ICustomPlayerData) player;
            if(!playerData.sphericalPlayerMod$getFlag())return;

            if(!player.level().isClientSide) bouncePlayer(player, event.getDistance(),playerData.sphericalPlayerMod$getRestitutionCoefficient());
            event.setDamageMultiplier(0);
        }
    }

    @SubscribeEvent
    public static void onPlayerFlyableFall(PlayerFlyableFallEvent event){
        Player player = event.getEntity();
        ICustomPlayerData playerData = (ICustomPlayerData) player;
        if(!playerData.sphericalPlayerMod$getFlag())return;

        if(!player.level().isClientSide) bouncePlayer(player, event.getDistance(),playerData.sphericalPlayerMod$getRestitutionCoefficient());
    }

    private static void bouncePlayer(Player player,float fallDistance, float restitutionCoefficient) {
        if(Math.abs(fallDistance)<1f) return;
        // 反発速度を計算
        double bounceVelocityY = Math.sqrt(2 * fallDistance * 0.08) * restitutionCoefficient;

        double restoredMotionX = player.getX() - player.xOld;
        double restoredMotionZ = player.getZ() - player.zOld;

        player.hasImpulse = true;
        player.setDeltaMovement(restoredMotionX, bounceVelocityY, restoredMotionZ);

        if(player instanceof ServerPlayer serverPlayer){
            serverPlayer.connection.send(new ClientboundSetEntityMotionPacket(
                    player
            ));
        }

    }
}
