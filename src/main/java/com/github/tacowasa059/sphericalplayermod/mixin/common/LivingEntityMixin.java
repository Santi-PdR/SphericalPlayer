package com.github.tacowasa059.sphericalplayermod.mixin.common;

import com.github.tacowasa059.sphericalplayermod.common.Interface.ICustomPlayerData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

/**
 * ジャンプ・移動・ノックバック
 */
@Mixin(value = LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V", ordinal = 2),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true)
    private void injectCustomMotion(Vec3 p_21280_, CallbackInfo ci, double d0, AttributeInstance gravity, boolean flag, FluidState fluidstate, BlockPos blockpos, float f2, float f3, Vec3 vec35, double d2) {
        sphericalPlayerMod$setDeltaMovement(ci, vec35);
    }

    @Inject(method = "travel", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;setDeltaMovement(DDD)V", ordinal = 3),
            locals = LocalCapture.CAPTURE_FAILSOFT,
            cancellable = true)
    private void injectCustomMotion2(Vec3 p_21280_, CallbackInfo ci, double d0, AttributeInstance gravity, boolean flag, FluidState fluidstate, BlockPos blockpos, float f2, float f3, Vec3 vec35, double d2) {
        sphericalPlayerMod$setDeltaMovement(ci, vec35);
    }

    @Unique
    private void sphericalPlayerMod$setDeltaMovement(CallbackInfo ci, Vec3 vec35) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(entity instanceof Player){
            ICustomPlayerData playerData =(ICustomPlayerData) entity;
            if(!playerData.sphericalPlayerMod$getFlag()) return;

            double d2 = vec35.y;
            if (entity.hasEffect(MobEffects.LEVITATION)) {
                MobEffectInstance mobEffectInstance = entity.getEffect(MobEffects.LEVITATION);
                if(mobEffectInstance != null){
                    d2 += (0.05D * (double)(mobEffectInstance.getAmplifier() + 1) - vec35.y) * 0.2D;
                }

            } else if (!entity.isNoGravity()) {
                AttributeInstance attributeInstance = entity.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get());
                if(attributeInstance != null) d2 -= attributeInstance.getValue();
            }

//            System.out.println(entity.level.isClientSide+" "+vec35);

            float f = 0.95F;
            double vec_x = vec35.x * (double)f;
            double vec_z = vec35.z * (double)f;
            double sqr =vec_x*vec_x+vec_z*vec_z;

            if(sqr>=0.8*0.8){

                vec_x *= 0.8/Math.sqrt(sqr);
                vec_z *= 0.8/Math.sqrt(sqr);
            }

            Vec3 newVector = new Vec3(vec_x, d2 * 0.9800000190734863, vec_z);

            float weight = 1f;

            entity.setDeltaMovement(newVector.multiply(weight, 1, weight)
                    .add(entity.getDeltaMovement().multiply(1-weight,0,1-weight)));
            ci.cancel();
        }
    }

    @Inject(
            method = "knockback",
            at = @At(
                    value = "HEAD"
            ),
            cancellable = true
    )
    public void modifyKnockback(double p_233627_1_, double p_233627_2_, double p_233627_4_, CallbackInfo ci) {
        LivingEntity entity =(LivingEntity)(Object)this;
        if(!(entity instanceof Player))return;
        ICustomPlayerData playerData =(ICustomPlayerData)entity;
        if(!playerData.sphericalPlayerMod$getFlag())return;

        LivingKnockBackEvent event = ForgeHooks.onLivingKnockBack(entity, (float) p_233627_1_, p_233627_2_, p_233627_4_);
        if (!event.isCanceled()) {
            p_233627_1_ = event.getStrength();
            p_233627_2_ = event.getRatioX();
            p_233627_4_ = event.getRatioZ();
            p_233627_1_ = (float)(p_233627_1_ * (1.0f - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)));
            if (!(p_233627_1_ <= 0.0F)) {
                entity.hasImpulse = true;
                Vec3 vector3d = entity.getDeltaMovement();
                Vec3 vector3d1 = (new Vec3(p_233627_2_, 0.0, p_233627_4_)).normalize().scale(p_233627_1_);

                entity.setDeltaMovement(vector3d.x / 2.0 - vector3d1.x,
                        vector3d.y / 2.0 + p_233627_1_/2.0 + 0.1D, vector3d.z / 2.0 - vector3d1.z);
            }
            ci.cancel();
        }
    }

    @Inject(method = "getFrictionInfluencedSpeed", at = @At("HEAD"), cancellable = true)
    public void modifyFrictionInfluencedSpeed(float p_213335_1_, CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(!(entity instanceof Player))return;
        ICustomPlayerData playerData =(ICustomPlayerData) entity;
        if(!playerData.sphericalPlayerMod$getFlag())return;

        if (!entity.onGround()) {
            // 空中でも地上と同じ移動速度を適用entity.isOnGround()?entity.getAIMoveSpeed() * (0.21600002F / (p_213335_1_ * p_213335_1_ * p_213335_1_)):
            cir.setReturnValue(entity.getSpeed() * (0.21600002F / (p_213335_1_ * p_213335_1_ * p_213335_1_)));
        }
    }

    @Inject(method="getJumpPower", at=@At("HEAD"), cancellable = true)
    protected void getJumpPower(CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if(!(entity instanceof Player))return;
        ICustomPlayerData playerData =(ICustomPlayerData) entity;
        if(!playerData.sphericalPlayerMod$getFlag())return;

        float f = 0.42F * this.sphericalPlayerMod$getJumpFactor();

        f *= 1.75;

        cir.setReturnValue(f);
        cir.cancel();
    }
    @Unique
    protected float sphericalPlayerMod$getJumpFactor() {
        LivingEntity entity = (LivingEntity) (Object) this;
        float f = entity.level().getBlockState(entity.blockPosition()).getBlock().getJumpFactor();
        BlockPos pos = this.getOnPos(0.500001F);
        float f1 = entity.level().getBlockState(pos).getBlock().getJumpFactor();
        return (double)f == 1.0 ? f1 : f;
    }
}