package com.github.tacowasa059.sphericalplayermod.client.event;

import com.github.tacowasa059.sphericalplayermod.common.Interface.ICustomPlayerData;
import com.github.tacowasa059.sphericalplayermod.SphericalPlayerMod;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SphericalPlayerMod.MOD_ID, bus= Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class SneakEventListener {
    @SubscribeEvent
    public static void onInputUpdate(MovementInputUpdateEvent event) {
        Player player = event.getEntity();
        ICustomPlayerData playerData = (ICustomPlayerData) player;
        if (player.onGround()&&!player.isPassenger() && playerData.sphericalPlayerMod$getFlag()) {
            event.getInput().shiftKeyDown=false;
        }
    }
}
