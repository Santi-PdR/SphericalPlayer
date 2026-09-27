package com.github.tacowasa059.sphericalplayermod.mixin.client;

import com.github.tacowasa059.sphericalplayermod.common.Interface.IPlayerRendererAccessor;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

/**
 * render name
 */
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin extends LivingEntityRenderer<Player, PlayerModel<Player>> implements IPlayerRendererAccessor {

    public PlayerRendererMixin(EntityRendererProvider.Context p_i50965_1_, PlayerModel<Player> p_i50965_2_, float p_i50965_3_) {
        super(p_i50965_1_, p_i50965_2_, p_i50965_3_);
    }

    @Shadow
    protected abstract void renderNameTag(AbstractClientPlayer player, Component name, PoseStack matrixStack,
                                          MultiBufferSource buffer, int packedLight);

    @Override
    public void sphericalPlayerMod$callRenderName(AbstractClientPlayer player, Component name, PoseStack matrixStack,
                                                  MultiBufferSource buffer, int packedLight) {
        this.renderNameTag(player, name, matrixStack, buffer, packedLight);
    }

    @Override
    public boolean sphericalPlayerMod$shouldShowName(Player player){
        return this.shouldShowName(player);
    }
}
