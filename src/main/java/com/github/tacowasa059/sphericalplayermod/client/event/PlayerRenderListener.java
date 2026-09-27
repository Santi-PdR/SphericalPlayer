package com.github.tacowasa059.sphericalplayermod.client.event;

import com.github.tacowasa059.sphericalplayermod.common.Interface.ICustomPlayerData;
import com.github.tacowasa059.sphericalplayermod.common.Interface.IPlayerRendererAccessor;
import com.github.tacowasa059.sphericalplayermod.SphericalPlayerMod;
import com.github.tacowasa059.sphericalplayermod.client.config.ClientConfig;
import com.github.tacowasa059.sphericalplayermod.client.utils.SphereRender;
import com.github.tacowasa059.sphericalplayermod.common.utils.QuaternionUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Quaternionf;

@Mod.EventBusSubscriber(modid = SphericalPlayerMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PlayerRenderListener {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void render(RenderLivingEvent.Pre<Player, PlayerModel<Player>> event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player player1) {
            AbstractClientPlayer player = (AbstractClientPlayer) entity;
            ICustomPlayerData playerData =(ICustomPlayerData)player1;

            boolean flag = playerData.sphericalPlayerMod$getFlag();
            if(!flag) return;
            if (entity.isInvisible()){
                event.setCanceled(true);
                return;
            }
            float size = playerData.sphericalPlayerMod$getSize();

            float partialTicks = event.getPartialTick();

            Quaternionf quaternion = playerData.sphericalPlayerMod$getInterpolatedQuaternion(partialTicks);

            ResourceLocation texture = player.getSkinTextureLocation();
            PoseStack poseStack  = event.getPoseStack();

            MultiBufferSource buffer = event.getMultiBufferSource();
            int packedLight = event.getPackedLight();
            poseStack.pushPose();
            poseStack.translate(0,size/2.0,0);
            if(player.getVehicle()==null) poseStack.mulPose(quaternion);
            else poseStack.mulPose(QuaternionUtils.getQuaternionFromEntity(entity));

            int overlay = OverlayTexture.NO_OVERLAY;
            if(player1.hurtTime > 0 || player1.deathTime > 0)overlay = OverlayTexture.pack(OverlayTexture.u(event.getPartialTick()),
                    OverlayTexture.v(player1.hurtTime > 0 || player1.deathTime > 0));

            SphereRender.drawTexturedSphere(poseStack, buffer, texture, (float) (size / 2.0), ClientConfig.SEGMENTS.get(), 0, 0, packedLight,true, overlay);

            poseStack.popPose();

            EntityRenderer<?> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);

            if (player instanceof RemotePlayer && renderer instanceof IPlayerRendererAccessor playerRendererAccessor) {
                if(playerRendererAccessor.sphericalPlayerMod$shouldShowName(player))
                    playerRendererAccessor.sphericalPlayerMod$callRenderName(player, player.getDisplayName(), poseStack, buffer, packedLight);
            }
            event.setCanceled(true);
        }
    }

}
