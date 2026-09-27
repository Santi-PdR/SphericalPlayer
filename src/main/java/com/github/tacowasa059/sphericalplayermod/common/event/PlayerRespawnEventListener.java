package com.github.tacowasa059.sphericalplayermod.common.event;

import com.github.tacowasa059.sphericalplayermod.common.Interface.ICustomPlayerData;
import com.github.tacowasa059.sphericalplayermod.SphericalPlayerMod;
import com.github.tacowasa059.sphericalplayermod.common.network.ModNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;


@Mod.EventBusSubscriber(modid = SphericalPlayerMod.MOD_ID,bus= Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerRespawnEventListener {

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        Player originalPlayer = event.getOriginal();
        Player newPlayer = event.getEntity();

        ICustomPlayerData original_playerData = (ICustomPlayerData)originalPlayer;
        ICustomPlayerData new_playerData = (ICustomPlayerData)newPlayer;

        new_playerData.sphericalPlayerMod$setFlagAndSizeAndRestitution(
                original_playerData.sphericalPlayerMod$getFlag(),original_playerData.sphericalPlayerMod$getSize(),
                original_playerData.sphericalPlayerMod$getRestitutionCoefficient());
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer) {
            ICustomPlayerData data = (ICustomPlayerData) event.getEntity();
            ModNetworking.sendInitialData(
                    (ServerPlayer) event.getEntity(),
                    data.sphericalPlayerMod$getSize(),
                    data.sphericalPlayerMod$getFlag(),
                    data.sphericalPlayerMod$getRestitutionCoefficient(),
                    data.sphericalPlayerMod$getQuaternion(),
                    data.sphericalPlayerMod$getCurrentPosition()
            );
        }
    }

}
