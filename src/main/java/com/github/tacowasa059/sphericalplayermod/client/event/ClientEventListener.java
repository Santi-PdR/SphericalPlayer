package com.github.tacowasa059.sphericalplayermod.client.event;

import com.github.tacowasa059.sphericalplayermod.SphericalPlayerMod;
import com.github.tacowasa059.sphericalplayermod.client.gui.ConfigScreen;
import com.github.tacowasa059.sphericalplayermod.client.keybinds.ModKeyBindings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(modid = SphericalPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventListener {
    @SuppressWarnings("removal")
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

        //　コンフィグからGUIを開くための設定

        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ConfigScreen(screen)));
    }

    @SubscribeEvent
    public static void onRegisterKey(RegisterKeyMappingsEvent event){
        event.register(ModKeyBindings.OPEN_GUI);
    }
}
