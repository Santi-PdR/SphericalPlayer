package com.github.tacowasa059.sphericalplayermod;

import com.github.tacowasa059.sphericalplayermod.client.config.ClientConfig;
import com.github.tacowasa059.sphericalplayermod.common.network.ModNetworking;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(SphericalPlayerMod.MOD_ID)
public class SphericalPlayerMod {
    public static final String MOD_ID="sphericalplayermod";
    @SuppressWarnings("removal")
    public SphericalPlayerMod() {
        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        ModNetworking.register();
    }
}
