package com.github.tacowasa059.sphericalplayermod.client.keybinds;

import com.github.tacowasa059.sphericalplayermod.SphericalPlayerMod;
import com.github.tacowasa059.sphericalplayermod.client.gui.ConfigScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;


@Mod.EventBusSubscriber(modid = SphericalPlayerMod.MOD_ID,bus= Mod.EventBusSubscriber.Bus.FORGE,value = Dist.CLIENT)
public class ModKeyBindings {

    // キーバインディングの定義
    public static final KeyMapping OPEN_GUI = new KeyMapping(
            "key."+ SphericalPlayerMod.MOD_ID +".opengui",
            GLFW.GLFW_KEY_F10,
            "key.categories."+SphericalPlayerMod.MOD_ID
    );

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onKeyInput(InputEvent event) {
        if (OPEN_GUI.isDown()) {
            Minecraft.getInstance().setScreen(new ConfigScreen(Minecraft.getInstance().screen));
        }
    }
}
