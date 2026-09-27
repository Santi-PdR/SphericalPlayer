package com.github.tacowasa059.sphericalplayermod.mixin.common;

import com.github.tacowasa059.sphericalplayermod.common.Interface.ICustomPlayerData;
import com.github.tacowasa059.sphericalplayermod.common.utils.QuaternionUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * server side quaternion calculation
 */
@Mixin(Entity.class)
public abstract class EntityMixin {
    @Unique
    private Vec3 sphericalPlayerMod$previousPosition;

    @Inject(method="tick", at=@At("HEAD"))
    public void tick(CallbackInfo ci){
        Entity entity = (Entity) (Object) this;
        if (entity instanceof Player) {
            sphericalPlayerMod$updateQuaternion((Player) entity);
        }
    }

    @Unique
    private void sphericalPlayerMod$updateQuaternion(Player player) {
        Level world = player.level();

        // サーバー側でのみ実行
        if (!world.isClientSide) {
            if(player.getVehicle()!=null) {
                ICustomPlayerData playerData = (ICustomPlayerData) player;
                playerData.sphericalPlayerMod$setQuaternion(QuaternionUtils.getQuaternionFromEntity(player));
                return;
            }

            Vec3 currentPosition = player.position();
            ICustomPlayerData playerData = (ICustomPlayerData) player;

            Quaternionf rot_Quaternion = QuaternionUtils.getUpdatedQuaternion(currentPosition, sphericalPlayerMod$previousPosition, playerData);
            sphericalPlayerMod$previousPosition = currentPosition;
            playerData.sphericalPlayerMod$setCurrentPosition(player.position());

            if (rot_Quaternion == null) return;
            playerData.sphericalPlayerMod$setQuaternion(rot_Quaternion);
        }
    }
}
