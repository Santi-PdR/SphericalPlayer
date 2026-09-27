package com.github.tacowasa059.sphericalplayermod.common.Interface;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public interface IPlayerRendererAccessor {
    void sphericalPlayerMod$callRenderName(AbstractClientPlayer player, Component name,
                                           PoseStack matrixStack, MultiBufferSource buffer, int packedLight);
    boolean sphericalPlayerMod$shouldShowName(Player player);
}

