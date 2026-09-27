package com.github.tacowasa059.sphericalplayermod.client.gui;

import com.github.tacowasa059.sphericalplayermod.client.config.ClientConfig;
import com.github.tacowasa059.sphericalplayermod.client.utils.SphereRender;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.AxisAngle4f;
import org.joml.Math;
import org.joml.Quaternionf;

import javax.annotation.Nonnull;

@OnlyIn(Dist.CLIENT)
public class ConfigScreen extends Screen {

    private final Screen parentScreen;

    private float rotationY = 0.0f;

    public ConfigScreen(Screen parentScreen) {
        super(Component.literal("Spherical Player Mod"));
        this.parentScreen = parentScreen;
    }

    @Override
    protected void init() {
        super.init();

        // カスタムスライダーを追加
        this.addRenderableWidget(new SegmentsSlider(this.width / 4, this.height /4, this.width / 2, 20, 8, 32, ClientConfig.SEGMENTS.get()));

        // Doneボタンを追加
        Button.OnPress press = (button) -> {
            if(this.minecraft!=null) this.minecraft.setScreen(this.parentScreen); // 親画面に戻る
        };
        this.addRenderableWidget(Button.builder(Component.literal("Done"), press).pos(this.width / 4, this.height * 7 / 8-5)
                .size(this.width / 2, 20).build());
    }

    @Override
    public void onClose() {
        // 設定が閉じられるときに保存を行う
        ClientConfig.CLIENT_CONFIG.save();
        super.onClose();
    }
    @Override
    public void tick() {
        // Y軸回りの視点の回転角度を一定速度で更新
        // Y軸回転の速度
        float rotationSpeedY = 1.0f;
        rotationY += rotationSpeedY;

        // 必要に応じて角度を制限（360度を超えないように）
        if (rotationY > 360.0f) rotationY -= 360.0f;
    }

    @Override
    public void render(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        PoseStack poseStack = guiGraphics.pose();
        drawBackground(guiGraphics);
        guiGraphics.drawCenteredString(this.font, this.getTitle(), this.width / 2, this.height / 16, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        float radius = Math.min(this.width / 5, this.height / 4);
        drawSphereEdges(poseStack, radius);
        drawTexturedSphere(poseStack, radius);
    }

    private void drawBackground(GuiGraphics guiGraphics) {
        // ぼかし効果を適用
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // 透明なオーバーレイを描画することでぼかしを表現
        guiGraphics.fill(RenderType.gui(), 0, 0, this.width, this.height, 0xB0000000); // 黒の透明度を少し高くしてぼかし効果を再現
        RenderSystem.disableBlend();
    }

    private void drawSphereEdges(PoseStack poseStack, float radius) {
        // OpenGLの設定を行う
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();

        // 球体を描画する位置を調整

        poseStack.pushPose();
        poseStack.translate(this.width/4f, this.height/2f + 20 , radius+ 0.1f);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotationY));
        poseStack.mulPose(Axis.XP.rotationDegrees(20));


        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.LINES);

        SphereRender.drawWireframeSphere(poseStack, radius, vertexConsumer);

        // OpenGLの設定を元に戻す
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private void drawTexturedSphere(PoseStack poseStack, float radius) {
        LocalPlayer player = Minecraft.getInstance().player;
        ResourceLocation texture;
        if (player != null) {
            texture = player.getSkinTextureLocation();
        } else {
            texture = new ResourceLocation("textures/entity/player/wide/steve.png");
        }

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();

        // テクスチャ付き球体を描画する位置を調整
        poseStack.pushPose();
        poseStack.translate(this.width * 3 / 4f, this.height / 2f + 20, 0);

        poseStack.mulPose(new Quaternionf(new AxisAngle4f((float) Math.PI, 0, 1, 0)));
        poseStack.mulPose(new Quaternionf(new AxisAngle4f((float) Math.PI, 1, 0, 0)));
        poseStack.mulPose(new Quaternionf(new AxisAngle4f((float) (rotationY * Math.PI/180), 0, 1, 0)));
        poseStack.mulPose(new Quaternionf(new AxisAngle4f((float) (20 * Math.PI/180), 1, 0, 0)));

        // テクスチャ付きの球体を描画
        SphereRender.drawTexturedSphere(poseStack, buffer, texture, radius, ClientConfig.SEGMENTS.get(), 0, 0, 0xF000F0,false, OverlayTexture.NO_OVERLAY);

        buffer.endBatch();
        poseStack.popPose();
    }
}
